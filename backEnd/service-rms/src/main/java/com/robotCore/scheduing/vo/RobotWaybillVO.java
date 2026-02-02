package com.robotCore.scheduing.vo;

import lombok.Data;

/**
 * @Des: 机器人运单
 * @author: zxl
 * @date: 2023/8/14
 **/
@Data
public class RobotWaybillVO {

    /**
     * id
     */
    private String id;

    /**
     * 任务id
     */
    private String taskId;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 序号
     */
    private Integer serialNumber;

    /**
     * 优先级
     */
    private String priority;

    /**
     * 机器人
     */
    private String robotName;

    /**
     * 运单状态 0--等待，1--正在执行，2--完成，3--终止
     */
    private Integer waybillStatus;

    /**
     * 机器人分组
     */
    private String robotGroupName;

    /**
     * 通信方式
     */
    private String communicationType;

    /**
     * 创建时间戳
     */
    private String createTime;

    /**
     * 接单时间戳
     */
    private String orderTime;

    /**
     * 完成时间戳
     */
    private String completeTime;

    /**
     * 执行耗时
     */
    private String executionTime;

    /**
     * 错误信息
     */
    private String errorMessage;

}
