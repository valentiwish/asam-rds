package com.robotCore.equipment.utils;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Des: 对robotId进行字符串到字节的转换及映射（用于电梯指令构建）
 * @author: leo
 * @date: 2025/7/16
 */
public class RobotIdToByteManager {
    // 存储机器人ID到字节值的映射
    private final Map<String, Byte> robotIdByteMap = new ConcurrentHashMap<>();

    // 字节分配计数器（0-255循环）
    private final AtomicInteger robotIdByteAllocator = new AtomicInteger(0);

    // 最大活跃ID数（字节范围0-255）
    private static final int MAX_ACTIVE_IDS = 256;

    // 单例模式实现
    private static final RobotIdToByteManager INSTANCE = new RobotIdToByteManager();

    private RobotIdToByteManager() {}

    public static RobotIdToByteManager getInstance() {
        return INSTANCE;
    }

    /**
     * 获取机器人ID对应的唯一字节值
     *
     * @param robotId 机器人ID字符串
     * @return 对应的唯一字节值（范围 0x00 ~ 0xFF）
     */
    public byte getRobotByteId(String robotId) {
        // 检查是否已存在映射
        if (robotIdByteMap.containsKey(robotId)) {
            return robotIdByteMap.get(robotId);
        }

        // 分配新的唯一字节值
        return allocateNewByte(robotId);
    }

    /**
     * 根据字节值获取对应的机器人ID（不增加额外Map）
     *
     * @param robotByteId 字节值（范围 0x00 ~ 0xFF）
     * @return 对应的机器人ID字符串，如果找不到返回null
     */
    public String getRobotId(byte robotByteId) {
        // 遍历现有映射查找匹配的字节值
        for (Map.Entry<String, Byte> entry : robotIdByteMap.entrySet()) {
            if (entry.getValue() == robotByteId) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * 为新的机器人ID分配唯一字节值
     *
     * @param robotId 机器人ID字符串
     * @return 新分配的唯一字节值
     * @throws IllegalStateException 当超过256个活跃ID时抛出
     */
    private synchronized byte allocateNewByte(String robotId) {
        // 双重检查锁，防止并发重复创建
        if (robotIdByteMap.containsKey(robotId)) {
            return robotIdByteMap.get(robotId);
        }

        // 检查容量限制（0-255）
        if (robotIdByteMap.size() >= MAX_ACTIVE_IDS) {
            throw new IllegalStateException("无法分配新ID，已达最大容量256个活跃机器人");
        }

        // 获取新字节值（0-255循环）
        byte newByte = (byte) (robotIdByteAllocator.getAndIncrement() & 0xFF);

        // 确保字节值唯一
        while (robotIdByteMap.containsValue(newByte)) {
            newByte = (byte) (robotIdByteAllocator.getAndIncrement() & 0xFF);
        }

        // 创建新映射
        robotIdByteMap.put(robotId, newByte);
        return newByte;
    }

    /**
     * 显式释放不再使用的机器人ID
     *
     * @param robotId 要释放的机器人ID
     */
    public void releaseRobotId(String robotId) {
        robotIdByteMap.remove(robotId);
    }

    /**
     * 获取当前活跃的机器人ID数量
     */
    public int getActiveCount() {
        return robotIdByteMap.size();
    }
}
