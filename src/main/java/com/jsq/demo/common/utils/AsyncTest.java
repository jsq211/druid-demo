package com.jsq.demo.common.utils;

import java.util.Random;
import java.util.concurrent.*;


public class AsyncTest {
    private static Random rand = new Random();
    private static long t = System.currentTimeMillis();
    static int getMoreData() {
        System.out.println("begin to start compute");
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("end to start compute. passed " + (System.currentTimeMillis() - t)/1000 + " seconds");
        return rand.nextInt(1000);
    }
    public static void main(String[] args) throws Exception {
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(AsyncTest::getMoreData);
        Future<Integer> f = future.whenComplete((v, e) -> {
            System.out.println(v);
            System.out.println(e);
        });
        System.out.println(f.get());
        CompletableFuture<String> futureA = CompletableFuture.supplyAsync(()->{
            System.out.println("启动A");
            try {
                Thread.sleep(1000);
                Test.pring();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "completableFutureA";
        });
        CompletableFuture<String> futureB = CompletableFuture.supplyAsync(()->{
            System.out.println("启动B");
            try {
                Thread.sleep(100);
                Test.pring();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "completableFutureB";
        });
        CompletableFuture<String> futureC = futureA.thenApplyAsync((c)->{
            System.out.println("启动C");
            Test.pring();
            return "completableFutureC";
        });
//        System.out.println("阻塞读取");
//        CompletableFuture result = CompletableFuture.allOf(futureA,futureB,futureC);
//        System.out.println("读取结果 ："+ futureA.get() + futureB.get()+futureC.get());
//        System.out.println(result);
        System.in.read();
    }
}

