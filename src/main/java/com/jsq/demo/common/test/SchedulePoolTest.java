package com.jsq.demo.common.test;


import com.jsq.demo.common.utils.Test;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SchedulePoolTest {
    private static ScheduledExecutorService  scheduledExecutorService = new ScheduledThreadPoolExecutor(2);

    public static void main(String[] args) {
        AtomicInteger atomicInteger = new AtomicInteger(0);
//        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
//            @Override
//            public void run() {
//                Test.pring();
//                System.out.println("当前线程："+Thread.currentThread() + atomicInteger.getAndIncrement());
//            }
//        },0, 1, TimeUnit.SECONDS);
//        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
//            @Override
//            public void run() {
//                Test.pring();
//                System.out.println("当前线程："+Thread.currentThread() + atomicInteger.getAndIncrement());
//            }
//        },0, 1, TimeUnit.SECONDS);
//
//        scheduledExecutorService.shutdown();
        ExecutorService executorService = new ThreadPoolExecutor(1,1,2L,TimeUnit.SECONDS,new LinkedBlockingQueue<>());
        Callable callable = new Callable() {
            @Override
            public Object call() throws Exception {
                Test.pring();
                System.out.println(Thread.currentThread() + " " +atomicInteger.getAndIncrement());
                Thread.sleep(2000);
                return null;
            }
        };
        Future future = executorService.submit(callable);
        try {
            future.get(2,TimeUnit.SECONDS);
        } catch (Exception e) {
            System.out.println(e);
            System.out.println(Thread.currentThread());
            Thread.currentThread().interrupt();
            System.out.println("结束");
        }
    }

}
