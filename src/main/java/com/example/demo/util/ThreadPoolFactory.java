package com.example.demo.util;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolFactory {

    private static class SingletonHolder {
        static final ThreadPoolFactory instance = new ThreadPoolFactory();
    }

    public static ThreadPoolFactory getInstance(){
        return SingletonHolder.instance;
    }

    /**
     * 线程池工厂类
     * @param corePoolSize
     * @param maximumPoolSize
     * @param capacity
     * @return
     */
    public ThreadPoolExecutor getThreadPool(int corePoolSize,int maximumPoolSize,int capacity){
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(capacity),
                new RejectedExecutionHandler() {
                    @Override
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                        System.out.println("拒绝策略");
                    }
                });
        return executor;
    }
}
