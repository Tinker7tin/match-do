package com.example.demo.matchcore;

import com.example.demo.disruptor.DisruptorOrderConsumer;
import com.example.demo.disruptor.MessageConsumer;
import com.example.demo.disruptor.RingBufferWorkerPoolFactory;
import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;

@Component
public class Start {


    @PostConstruct
    public void startDisruptor(){
        MessageConsumer[] conusmers = new MessageConsumer[1000];

        for(int i =0; i < conusmers.length; i++) {
//            MessageConsumer messageConsumer = new DisruptorOrderConsumer();
//            conusmers[i] = messageConsumer;
        }
//
        RingBufferWorkerPoolFactory.getInstance().startDisruptor(ProducerType.MULTI,
                1024*1024*2,
//                new YieldingWaitStrategy(),
                new BusySpinWaitStrategy(),
                conusmers);

        System.out.println("启动disruptor服务");
    }

    @PostConstruct
    public void startRead(){

        System.out.println("启动读取账本");

    }
}
