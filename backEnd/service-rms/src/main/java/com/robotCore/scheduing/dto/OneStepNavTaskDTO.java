package com.robotCore.scheduing.dto;

import lombok.Data;

/**
 * @Des: 指定路径导航任务——原子任务
 * @author: leo
 * @date: 2025/3/21
 */
@Data
public class OneStepNavTaskDTO {
    // 任务ID（全局唯一）
    private String taskId;
    // 起点
    private String startPoint;
    // 终点
    private String endPoint;
    // 起始点间的总长度（或虚拟总长度）
    private float totalCurveLength;
    // 是否已下发
    private boolean isDispatched;
    // 是否已完成
    private boolean isCompleted;


    /**
     * <构造一个步进任务对象>
     *
     * @param robotId          机器人唯一标识
     * @param startPoint       导航起始点标识
     * @param endPoint         导航终点标识
     * @param totalCurveLength 导航路径的总曲线长度
     * @param seq              任务序列号，用于区分同一机器人下的不同任务
     */
    public OneStepNavTaskDTO(String robotId,
                             String startPoint,
                             String endPoint,
                             float totalCurveLength,
                             String seq) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.totalCurveLength = totalCurveLength;
        this.taskId = String.format("%s_%s", robotId, seq);
    }
}
