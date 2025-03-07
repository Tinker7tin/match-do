package org.matcher.me.engine;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;
import lombok.Getter;
import lombok.Setter;
import org.matcher.me.model.OrderEntity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GlobalDisruptor {
    private static final int RING_SIZE = 1024 * 1024;
    private static final Disruptor<OrderEvent> disruptor;
    private static final RingBuffer<OrderEvent> ringBuffer;
    private static final Map<String, OrderBook> symbolToBook = new ConcurrentHashMap<>();

    static {
        disruptor = new Disruptor<>(
                OrderEvent::new,
                RING_SIZE,
                DaemonThreadFactory.INSTANCE,
                ProducerType.MULTI,
                new YieldingWaitStrategy()
        );

        disruptor.handleEventsWith((event, sequence, endOfBatch) -> {
            OrderBook book = symbolToBook.get(event.getOrder().getSymbol());
            if (book != null) {
                book.onEvent(event, sequence, endOfBatch);
            }
        });

        ringBuffer = disruptor.getRingBuffer();
        disruptor.start();
    }

    public static void registerSymbol(String symbol, OrderBook book) {
        symbolToBook.put(symbol, book);
    }

    public static void publish(OrderEntity order) {
        long sequence = ringBuffer.next();
        try {
            OrderEvent event = ringBuffer.get(sequence);
            event.setOrder(order);
        } finally {
            ringBuffer.publish(sequence);
        }
    }

    @Setter
    @Getter
    public static class OrderEvent {
        private OrderEntity order;
    }
}
