package org.matcher.me.engine;

import com.lmax.disruptor.EventHandler;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongAVLTreeSet;
import it.unimi.dsi.fastutil.longs.LongSortedSet;
import org.matcher.me.enums.OrderType;
import org.matcher.me.enums.TradeType;
import org.matcher.me.model.OrderEntity;
import org.matcher.me.model.TradedOrder;
import org.matcher.me.model.depth.DepthMap;

import java.util.Comparator;

/**
 * 高性能订单簿实现（买卖处理逻辑分离版本）
 * 关键优化：
 * 1. 分片锁减少竞争
 * 2. 高效双端队列
 * 3. 延迟更新价格列表
 */
public class OrderBook implements EventHandler<GlobalDisruptor.OrderEvent> {
    private final String symbol;

    // 存储结构：价格 -> 订单队列（使用FastUtil优化内存）
    private final Long2ObjectOpenHashMap<LinkedDeque<OrderEntity>> buyPrices =
            new Long2ObjectOpenHashMap<>(1024, 0.9f);
    private final Long2ObjectOpenHashMap<LinkedDeque<OrderEntity>> sellPrices =
            new Long2ObjectOpenHashMap<>(1024, 0.9f);

    // 价格排序集合（使用AVL树维护有序性）
    private final LongSortedSet sortedBuyPrices = new LongAVLTreeSet(Comparator.reverseOrder()); // 买方降序
    private final LongSortedSet sortedSellPrices = new LongAVLTreeSet(); // 卖方升序

    //深度
    private final DepthMap depthMap = new DepthMap();

    // 分片锁配置（128分片，提升并发度）
    private static final int PRICE_SHARDS = 128;
    private final Object[] buyLocks = new Object[PRICE_SHARDS];
    private final Object[] sellLocks = new Object[PRICE_SHARDS];

    public OrderBook(String symbol) {
        this.symbol = symbol;
        // 初始化分片锁对象
        for (int i = 0; i < PRICE_SHARDS; i++) {
            buyLocks[i] = new Object();
            sellLocks[i] = new Object();
        }
        GlobalDisruptor.registerSymbol(symbol, this);
    }

    @Override
    public void onEvent(GlobalDisruptor.OrderEvent event, long sequence, boolean endOfBatch) {
        OrderEntity order = event.getOrder();
        try {
            if (order.getType() == OrderType.BUY) {
                processBuyOrder(order);
            } else {
                processSellOrder(order);
            }
        } finally {
            OrderPool.returnObject(order);
        }
    }

    // region 买方订单处理
    private void processBuyOrder(OrderEntity buyOrder) {
        // 阶段1：与卖方最优价格订单撮合
        while (buyOrder.getRemaining() > 0 && !sortedSellPrices.isEmpty()) {
            // 最佳卖单价
            long bestSellPrice = sortedSellPrices.firstLong();

            // 限价单价格检查：买单价 >= 卖一价
            if (buyOrder.getTradeType() == TradeType.LIMIT && buyOrder.getPrice() < bestSellPrice) {
                break;
            }

            int shard = getSellShard(bestSellPrice);
            synchronized (sellLocks[shard]) {
                LinkedDeque<OrderEntity> sellQueue = sellPrices.get(bestSellPrice);
                if (sellQueue == null || sellQueue.isEmpty()) {
                    // 价格列表维护：延迟删除空队列对应的价格
                    sortedSellPrices.remove(bestSellPrice);
                    continue;
                }

                // 批量处理同一价格层的订单
                while (!sellQueue.isEmpty() && buyOrder.getRemaining() > 0) {
                    OrderEntity sellOrder = sellQueue.peekFirst();
                    long tradeAmount = Math.min(buyOrder.getRemaining(), sellOrder.getRemaining());
                    executeTrade(buyOrder, sellOrder, bestSellPrice, tradeAmount);

                    // 更新卖方订单状态
                    updateOrderState(sellOrder, tradeAmount, sellQueue, sellPrices, sortedSellPrices);
                    buyOrder.setRemaining(buyOrder.getRemaining() - tradeAmount);
                }
            }
        }

        // 阶段2：剩余量加入买方订单簿
        if (buyOrder.getRemaining() > 0 && buyOrder.getTradeType() == TradeType.LIMIT) {
            addToBuyBook(buyOrder);
        }
    }
    // endregion

    // region 卖方订单处理（对称逻辑）
    private void processSellOrder(OrderEntity sellOrder) {
        while (sellOrder.getRemaining() > 0 && !sortedBuyPrices.isEmpty()) {
            long bestBuyPrice = sortedBuyPrices.firstLong();

            if (sellOrder.getTradeType() == TradeType.LIMIT &&
                    sellOrder.getPrice() > bestBuyPrice) {
                break;
            }

            int shard = getBuyShard(bestBuyPrice);
            synchronized (buyLocks[shard]) {
                LinkedDeque<OrderEntity> buyQueue = buyPrices.get(bestBuyPrice);
                if (buyQueue == null || buyQueue.isEmpty()) {
                    sortedBuyPrices.remove(bestBuyPrice);
                    continue;
                }

                while (!buyQueue.isEmpty() && sellOrder.getRemaining() > 0) {
                    OrderEntity buyOrder = buyQueue.peekFirst();
                    long tradeAmount = Math.min(sellOrder.getRemaining(), buyOrder.getRemaining());
                    executeTrade(buyOrder, sellOrder, bestBuyPrice, tradeAmount);

                    updateOrderState(buyOrder, tradeAmount, buyQueue, buyPrices, sortedBuyPrices);
                    sellOrder.setRemaining(sellOrder.getRemaining() - tradeAmount);
                }
            }
        }

        if (sellOrder.getRemaining() > 0 && sellOrder.getTradeType() == TradeType.LIMIT) {
            addToSellBook(sellOrder);
        }
    }
    // endregion

    // region 订单簿操作辅助方法
    private void addToBuyBook(OrderEntity order) {
        long price = order.getPrice();
        int shard = getBuyShard(price);
        synchronized (buyLocks[shard]) {
            LinkedDeque<OrderEntity> queue = buyPrices.get(price);
            if (queue == null) {
                queue = new LinkedDeque<>();
                buyPrices.put(price, queue);
                sortedBuyPrices.add(price); // 同步块内维护价格列表
            }
            queue.offerLast(order);
        }
    }

    private void addToSellBook(OrderEntity order) {
        long price = order.getPrice();
        int shard = getSellShard(price);
        synchronized (sellLocks[shard]) {
            LinkedDeque<OrderEntity> queue = sellPrices.get(price);
            if (queue == null) {
                queue = new LinkedDeque<>();
                sellPrices.put(price, queue);
                sortedSellPrices.add(price);
            }
            queue.offerLast(order);
        }
    }
    // endregion

    // region 通用工具方法
    private void executeTrade(OrderEntity taker, OrderEntity maker, long price, long amount) {
        // 生成成交记录（实际应发送到结算队列）
        TradedOrder trade = new TradedOrder(
                taker.getOrderId(),
                maker.getOrderId(),
                price,
                amount
        );
        System.out.println("[TRADE] " + trade);
    }

    private void updateOrderState(OrderEntity order, long traded, LinkedDeque<OrderEntity> queue,
                                  Long2ObjectOpenHashMap<LinkedDeque<OrderEntity>> priceMap,
                                  LongSortedSet priceSet) {
        order.setRemaining(order.getRemaining() - traded);
        if (order.getRemaining() == 0) {
            queue.pollFirst();
            if (queue.isEmpty()) {
                priceMap.remove(order.getPrice());
                priceSet.remove(order.getPrice());
            }
        }
    }

    // 改进的分片计算：混合高位和低位减少碰撞
    private int getBuyShard(long price) {
        long mixed = price ^ (price >>> 32);
        return (int) (mixed & (PRICE_SHARDS - 1));
    }

    private int getSellShard(long price) {
        return getBuyShard(price); // 共享同一分片计算逻辑
    }
    // endregion

    // region 高性能双端队列实现
    /**
     * 自定义链表双端队列（写优化设计）
     * 特性：
     * - 头部删除 O(1)
     * - 尾部添加 O(1)
     * - 无容量限制
     */
    static class LinkedDeque<T> {
        private Node<T> head;
        private Node<T> tail;
        private int size;

        private static class Node<T> {
            T item;
            Node<T> prev;
            Node<T> next;

            Node(T item) {
                this.item = item;
            }
        }

        public boolean offerLast(T item) {
            Node<T> newNode = new Node<>(item);
            if (tail == null) {
                head = tail = newNode;
            } else {
                tail.next = newNode;
                newNode.prev = tail;
                tail = newNode;
            }
            size++;
            return true;
        }

        public T peekFirst() {
            return (head != null) ? head.item : null;
        }

        public T pollFirst() {
            if (head == null) return null;
            Node<T> oldHead = head;
            T item = oldHead.item;
            head = oldHead.next;
            if (head != null) {
                head.prev = null;
            } else {
                tail = null;
            }
            size--;
            return item;
        }

        public boolean isEmpty() {
            return head == null;
        }
    }
    // endregion
}