package com.example.demo.disruptor;

import com.example.demo.util.ThreadPoolFactory;
import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RingBufferWorkerPoolFactory {

    private static class SingletonHolder{
        static final RingBufferWorkerPoolFactory instance = new RingBufferWorkerPoolFactory();
    }

    public RingBufferWorkerPoolFactory() {
    }

    public static RingBufferWorkerPoolFactory getInstance(){
        return SingletonHolder.instance;
    }

    //生产者池
    private static Map<String, MessageProducer> producers = new ConcurrentHashMap<String, MessageProducer>();

    //消费者池
    private static Map<String, MessageConsumer> consumers = new ConcurrentHashMap<String, MessageConsumer>();

    private RingBuffer<DisruptorData> ringBuffer;

    private SequenceBarrier sequenceBarrier;

    private WorkerPool<DisruptorData> workerPool;

    public void startDisruptor(ProducerType type, int bufferSize, WaitStrategy waitStrategy,MessageConsumer[] messageConsumers){
        //1. 构建ringBuffer对象
        this.ringBuffer = RingBuffer.create(type,
                new EventFactory<DisruptorData>() {
                    public DisruptorData newInstance() {
                        return new DisruptorData();
                    }
                },
                bufferSize,
                waitStrategy);
        //2.设置序号栅栏
        this.sequenceBarrier = this.ringBuffer.newBarrier();
        //3.设置工作池
        this.workerPool = new WorkerPool<DisruptorData>(this.ringBuffer,
                this.sequenceBarrier,
                new EventExceptionHandler(), messageConsumers);
        //4 把所构建的消费者置入池中
        for(MessageConsumer mc : messageConsumers){
            this.consumers.put(mc.getConsumerId(), mc);
        }
        //5 添加我们的sequences
        this.ringBuffer.addGatingSequences(this.workerPool.getWorkerSequences());
        //6 启动我们的工作池
        this.workerPool.start(ThreadPoolFactory.getInstance().getThreadPool(Runtime.getRuntime().availableProcessors(),10000,Integer.MAX_VALUE));
}

    /**
     * 获取生产者
     * @param producerId
     * @return
     */
    public MessageProducer getMessageProducer(String producerId){
        MessageProducer messageProducer = this.producers.get(producerId);
        if(null == messageProducer) {
            messageProducer = new MessageProducer(producerId, this.ringBuffer);
            this.producers.put(producerId, messageProducer);
        }
        return messageProducer;
    }


    /**
     * 异常静态类
     * @author Alienware
     *
     */
    static class EventExceptionHandler implements ExceptionHandler<DisruptorData> {
        public void handleEventException(Throwable ex, long sequence, DisruptorData event) {
        }

        public void handleOnStartException(Throwable ex) {
        }

        public void handleOnShutdownException(Throwable ex) {
        }
    }
}
