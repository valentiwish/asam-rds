package com.robotCore.common.base.service;

import org.springframework.scheduling.annotation.Async;

/**
 * @Description: 异步任务接口
 * @Author: zhangqi
 * @Create: 2023/4/7 8:30
 */
public interface AsynTaskService {

    /**
     * 异步任务：下发卷帘门开门指令
     */
    void openDoorAsync(String doorId);

    /**
     * 异步任务：下发卷帘门关门指令
     */
    void closeDoorAsync(String doorId);

    /**
     * 异步任务: 业务系统更新自动卷帘门的状态
     */
    void updateDoorStatus(String toJSONString);

    /**
     * 异步任务：IOServer控制指令发送
     */
    void setControlAsync(String variable, Object value);

    /**
     * 异步任务: 给IOServer重复发送控制指令
     */
    void reSendControlAsync(String variableName, Object value, Integer sendNum);

}
