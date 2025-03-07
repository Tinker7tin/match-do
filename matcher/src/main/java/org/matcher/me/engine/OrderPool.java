package org.matcher.me.engine;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.matcher.me.model.OrderEntity;

public class OrderPool {
    private static final GenericObjectPool<OrderEntity> pool =
            new GenericObjectPool<>(new OrderFactory());

    static {
        GenericObjectPoolConfig<OrderEntity> config = new GenericObjectPoolConfig<>();
        config.setMaxTotal(1_000_000);
        config.setMaxIdle(100_000);
        pool.setConfig(config);
    }

    public static OrderEntity borrowObject() throws Exception {
        return pool.borrowObject();
    }

    public static void returnObject(OrderEntity order) {
        pool.returnObject(order);
    }

    private static class OrderFactory extends BasePooledObjectFactory<OrderEntity> {
        @Override
        public OrderEntity create() {
            return new OrderEntity();
        }

        @Override
        public PooledObject<OrderEntity> wrap(OrderEntity obj) {
            return new DefaultPooledObject<>(obj);
        }

        @Override
        public void passivateObject(PooledObject<OrderEntity> p) {
            OrderEntity order = p.getObject();
            // 重置对象状态
            order.setRemaining(0);
            order.setStatus(null);
        }
    }
}
