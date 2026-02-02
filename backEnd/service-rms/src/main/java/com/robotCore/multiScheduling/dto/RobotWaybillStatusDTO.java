package com.robotCore.multiScheduling.dto;

import lombok.Data;

/**
 * @Des:
 * @author: zxl
 * @date: 2025/6/11
 **/
@Data
public class RobotWaybillStatusDTO {

    //任务ID
    private String taskId;

    //任务状态
    private String taskState;

    //agv编号
    private String agvId;

    //车辆当前位置
    private String station;

}
