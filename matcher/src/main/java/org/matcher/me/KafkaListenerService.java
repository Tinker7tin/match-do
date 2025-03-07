package org.matcher.me;

import com.google.protobuf.InvalidProtocolBufferException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.matcher.me.engine.OrderGateway;
import org.matcher.me.enums.OrderStatus;
import org.matcher.me.enums.OrderType;
import org.matcher.me.enums.TradeType;
import org.matcher.me.model.OrderEntity;
import org.matcher.me.model.proto.OrderProto;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaListenerService {

    private final OrderGateway orderGateway;

    @KafkaListener(topics = "order-topic")
    public void listen(byte[] message) throws InvalidProtocolBufferException {
        OrderProto.Order order = OrderProto.Order.parseFrom(message);
        log.info("order-topic receive message:{}",order);
        OrderEntity orderEntity = OrderEntity.builder()
                .orderId(order.getOrderId())
                .type(OrderType.getEnum(order.getType()))
                .amount(order.getAmount())
                .remaining(order.getAmount())
                .symbol(order.getSymbol())
                .price(order.getPrice())
                .tradeType(TradeType.getEnum(order.getTradeType()))
                .status(OrderStatus.getStatus(order.getStatus()))
                .build();
        orderGateway.onOrderReceived(orderEntity);
    }
}
