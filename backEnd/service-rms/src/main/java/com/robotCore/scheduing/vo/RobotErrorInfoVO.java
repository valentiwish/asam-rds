package com.robotCore.scheduing.vo;

import lombok.Data;

/**
 * @Des:
 * @author: zxl
 * @date: 2023/8/16
 **/
@Data
public class RobotErrorInfoVO {

    private String errorCode;  //错误码

    private Integer errorFrequency;  //错误次数

    private String errorTime;  //错误时间

    private String errorDescription;  //错误描述
}
