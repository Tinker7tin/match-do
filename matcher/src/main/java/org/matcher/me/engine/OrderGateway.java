package org.matcher.me.engine;

import org.matcher.me.model.OrderEntity;
import org.springframework.stereotype.Component;

@Component
public class OrderGateway {

    public void onOrderReceived(OrderEntity rawOrder) {
        try {
            // 从对象池中获取一个对象
            OrderEntity pooledOrder = OrderPool.borrowObject();
            copyOrderData(rawOrder, pooledOrder);
            // 将订单放入 disruptor 中分发
            GlobalDisruptor.publish(pooledOrder);
        } catch (Exception e) {
            // 处理异常
            e.printStackTrace();
        }
    }

    private void copyOrderData(OrderEntity src, OrderEntity dest) {
        // 拷贝字段
        dest.setOrderId(src.getOrderId());
        dest.setSymbol(src.getSymbol());
        dest.setPrice(src.getPrice());
        dest.setAmount(src.getAmount());
        dest.setRemaining(src.getRemaining());
        dest.setTradeType(src.getTradeType());
        dest.setStatus(src.getStatus());
        dest.setType(src.getType());
    }
}
