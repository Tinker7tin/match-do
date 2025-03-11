package org.matcher.me.model.depth;


import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import lombok.Data;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;
@Data
public class DepthMap {
    private long lastestOrderId;
    // 买盘（降序排列）
    private final ConcurrentNavigableMap<Long, DepthLevel> bids = new ConcurrentSkipListMap<>(Comparator.reverseOrder());
    // 卖盘（升序排列）
    private final ConcurrentNavigableMap<Long, DepthLevel> asks = new ConcurrentSkipListMap<>();
    private final Long2ObjectMap<DepthLevel> bidPriceToLevel = new Long2ObjectOpenHashMap<>(2048);
    private final Long2ObjectMap<DepthLevel> askPriceToLevel = new Long2ObjectOpenHashMap<>(2048);

    @Data
    private static class DepthLevel {
        private volatile long totalQty; // 使用volatile保证可见性
        private final long price;

        DepthLevel(long price, long qty) {
            this.price = price;
            this.totalQty = qty;
        }
    }

    public void addOrder(boolean isAdd, long price, long qty) {
        if (isAdd) {
            updateDepth(bidPriceToLevel, bids, price, qty);
        } else {
            updateDepth(askPriceToLevel, asks, price, qty);
        }
    }

    private void updateDepth(Long2ObjectMap<DepthLevel> priceMap,
                             ConcurrentNavigableMap<Long, DepthLevel> depthMap,
                             long priceKey,
                             long deltaQty) {
        // 无锁化CAS更新（线程安全关键）
        priceMap.compute(priceKey, (k, existingLevel) -> {
            if (existingLevel != null) {
                existingLevel.totalQty += deltaQty;
                return existingLevel;
            } else {
                DepthLevel newLevel = new DepthLevel(priceKey, deltaQty);
                depthMap.put(priceKey, newLevel);
                return newLevel;
            }
        });
    }

    public void removeOrder(boolean isAdd, long price, long qty) {

        if (isAdd) {
            reduceDepth(bidPriceToLevel, bids, price, qty);
        } else {
            reduceDepth(askPriceToLevel, asks, price, qty);
        }
    }

    private void reduceDepth(Long2ObjectMap<DepthLevel> priceMap,
                             ConcurrentNavigableMap<Long, DepthLevel> depthMap,
                             long priceKey,
                             long deltaQty) {
        priceMap.computeIfPresent(priceKey, (k, level) -> {
            level.totalQty -= deltaQty;
            if (level.totalQty <= 0) { // 数量归零则移除
                depthMap.remove(priceKey);
                return null;
            }
            return level;
        });
    }

    public DepthSnapshot getDepthSnapshot(int maxLevels) {
        return new DepthSnapshot(
                getLevels(bids, maxLevels),
                getLevels(asks, maxLevels)
        );
    }

    private List<PriceLevel> getLevels(ConcurrentNavigableMap<Long, DepthLevel> source,
                                       int maxLevels) {
        return source.values().stream()
                .limit(maxLevels)
                .map(level -> new PriceLevel(
                        convertLongToPrice(level.getPrice()),
                        level.totalQty))
                .collect(Collectors.toList());
    }

    private double convertLongToPrice(long priceLong) {
        // 示例：假设价格以1e8为精度存储（如BTC/USDT 50000.0 -> 50000_00000000L）
        return priceLong;
//        return priceLong / 100_000_000.0;
    }
}
