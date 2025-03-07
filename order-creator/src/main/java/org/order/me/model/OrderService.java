package org.order.me.model;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.order.me.model.entity.OrderEntity;
import org.order.me.model.enums.OrderStatus;
import org.order.me.model.enums.OrderType;
import org.order.me.model.enums.TradeType;
import org.order.me.model.proto.OrderProto;
import org.order.me.util.DistributedSeqIdGenerator;
import org.springframework.stereotype.Service;

import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderProducer orderProducer;
    DistributedSeqIdGenerator idGenerator = new DistributedSeqIdGenerator(1);

    /**
     * 自动创建订单
     */
    public void createOrder(Integer size) {
        Thread thread = new Thread(() -> {
            for (int i = 0; i < size; i++) {
                OrderEntity order = randomGen();
                orderProducer.sendMessage("order-topic", convert(order).toByteArray());
                log.info("订单发送成功：{}", order);
            }
        });
        thread.start();
    }

    public OrderProto.Order convert(OrderEntity order){
        return OrderProto.Order.newBuilder()
                .setOrderId(order.getOrderId())
                .setType(order.getType().getType())
                .setAmount(order.getAmount())
                .setTradeType(order.getTradeType().getType())
                .setPrice(order.getPrice())
                .setStatus(order.getStatus().getStatus())
                .setSymbol(order.getSymbol())
                .build();
    }

    public OrderEntity randomGen(){
        Random random = new Random();
        return new OrderEntity()
                .setOrderId(idGenerator.nextId())
                .setSymbol("BTC/USDT")
                .setType(OrderType.random())
                .setAmount(random.nextLong(1,1000))
                .setPrice(random.nextLong(1,1000))
                .setStatus(OrderStatus.UNFILLED)
                .setTradeType(TradeType.random());
    }


}
