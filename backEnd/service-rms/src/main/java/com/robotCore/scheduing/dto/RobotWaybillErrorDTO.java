package com.robotCore.scheduing.dto;

import lombok.Data;

/**
 * @Des: 机器人运单错误DTO
 * @author: zxl
 * @date: 2023/8/14
 **/
@Data
public class RobotWaybillErrorDTO {

    private String errorCode; //错误码

    private String errorTime; //错误时间

    private String errorDescription; //错误描述
}
