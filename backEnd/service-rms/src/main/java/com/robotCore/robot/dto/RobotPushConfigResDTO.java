package com.robotCore.robot.dto;

import lombok.Data;

/**
 * @Des: 机器人推送配置返回的实体
 * @author: zxl
 * @date: 2023/4/27
 **/
@Data
public class RobotPushConfigResDTO {

    private Integer retCode; //API错误码

    private String createOn; //API 上传时间戳

    private String errMsg; //错误信息
}
