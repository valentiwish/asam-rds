package com.robotCore.multiScheduling.dto;

import lombok.Data;

/**
 * @Des: 米松创建作业的运单状态回调参数
 * @author: zxl
 * @date: 2025/9/11
 **/
@Data
public class MissionCallbackUrlReqDTO {

    //作业ID，可用该值查询作业
    private String id;

    //指定执行任务的agv编号
    private String agvCode;

    private String status;

    //是否取货完成
    private String progress;

    //运行步骤
    private String description;

    private Integer currentActionSequence;

    private String errorCode;

    private String errorMessage;

}
