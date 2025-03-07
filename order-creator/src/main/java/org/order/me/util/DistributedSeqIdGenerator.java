package org.order.me.util;

import lombok.Data;

@Data
public class DistributedSeqIdGenerator {
    // 各部分的位数分配（可根据实际需求调整）
    private static final long TIMESTAMP_BITS = 41L;    // 时间戳位数
    private static final long WORKER_ID_BITS = 10L;    // 工作节点ID位数
    private static final long SEQUENCE_BITS = 12L;     // 序列号位数

    // 最大值计算
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);

    // 位移计算
    private static final long TIMESTAMP_SHIFT = WORKER_ID_BITS + SEQUENCE_BITS;
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;

    // 时间基准（2023-01-01 00:00:00 UTC）
    private static final long EPOCH = 1672531200000L;

    private final long workerId;
    private long lastTimestamp = -1L;
    private long sequence = 0L;

    /**
     * 构造函数
     * @param workerId 工作节点ID (0 ≤ workerId ≤ MAX_WORKER_ID)
     */
    public DistributedSeqIdGenerator(long workerId) {
        if (workerId < 0 || workerId > MAX_WORKER_ID) {
            throw new IllegalArgumentException(
                    String.format("Worker ID must be between 0 and %d", MAX_WORKER_ID));
        }
        this.workerId = workerId;
    }

    /**
     * 生成下一个ID（线程安全）
     */
    public synchronized long nextId() {
        long currentTimestamp = timeGen();

        // 处理时钟回拨
        if (currentTimestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate id");
        }

        // 同一毫秒内生成序列号
        if (lastTimestamp == currentTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            // 序列号用尽时等待到下一毫秒
            if (sequence == 0) {
                currentTimestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = currentTimestamp;

        // 组合各部分生成最终ID
        return ((currentTimestamp - EPOCH) << TIMESTAMP_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;
    }

    /**
     * 等待到下一毫秒
     */
    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 获取当前时间戳
     */
    private long timeGen() {
        return System.currentTimeMillis();
    }

}
