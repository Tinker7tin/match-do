package com.example.demo.thread;


import com.example.demo.entity.OrderBook;

import java.util.Date;

/**
 * 撮合线程
 */
public class MatchThread extends Thread {
    private OrderBook orderBook;

    private long time = 0;
    private boolean matchFlag = true;

    public MatchThread(OrderBook orderBook
    ) {
        this.orderBook = orderBook;
        this.matchEngine = matchEngine;
    }

    /**
     * 监听撮合状态
     * @return
     */
    public boolean listenerStatus(){
        long t = new Date().getTime();
        return (t-time)<=3000?true:false;
    }

    /**
     * 停止撮合线程
     * @return
     */
    public void stopMatch(){
        this.matchFlag = false;
    }

    @Override
    public void run() {
        while (true){
            try {
                if (!this.matchFlag)
                    break;
                time = new Date().getTime();
                matchEngine.autoMatch(orderBook);
                Thread.sleep(5);
            }catch (Exception e){

            }
        }
    }
}
