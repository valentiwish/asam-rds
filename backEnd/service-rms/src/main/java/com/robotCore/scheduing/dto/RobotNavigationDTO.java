package com.robotCore.scheduing.dto;

import lombok.Data;

/**
 * @Des: 任务导航状态
 * @author: zxl
 * @date: 2023/6/15
 **/
@Data
public class RobotNavigationDTO {

    private Integer taskStatus;  //导航状态

    private Integer taskType;  //导航类型

    private Integer retCode; //API 错误码

    private String createOn; //API 上传时间戳

    private String errMsg; //错误信息
}
