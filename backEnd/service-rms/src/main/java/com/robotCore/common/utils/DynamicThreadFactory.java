package com.robotCore.common.utils;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Des: 动态线程工厂
 * @author: leo
 * @date: 2025/6/6
 */
public class DynamicThreadFactory implements ThreadFactory {
    // 静态内部类持有计数器
    private static class Counter {
        static final ConcurrentHashMap<String, AtomicInteger> counters = new ConcurrentHashMap<>();
    }

    private final String robotId;
    private final String operFlag;

    public DynamicThreadFactory(String robotId, String operFlag) {
        this.robotId = robotId;
        this.operFlag = operFlag;
    }
//        @Override
//        public Thread newThread(@NotNull Runnable r) {
//            // 动态生成包含机器人和序列号的线程名
//            return new Thread(r, operFlag + "DoorWorker-" + robotId + "-" + threadCounterForOpenDoor.getAndIncrement());
//        }
    public Thread newThread(@NotNull Runnable r) {
        // 每个robotId + operFlag组合独立计数
        String counterKey = robotId + "|" + operFlag;
        AtomicInteger counter = DynamicThreadFactory.Counter.counters.computeIfAbsent(counterKey, k -> new AtomicInteger(1));
        return new Thread(r, operFlag + "Worker-" + robotId + "-" + counter.getAndIncrement());
    }

}
