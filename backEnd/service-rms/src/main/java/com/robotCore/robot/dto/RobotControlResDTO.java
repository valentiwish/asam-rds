package com.robotCore.robot.dto;

import lombok.Data;

/**
 * @Des: 当前控制权所有者
 * @author: zxl
 * @date: 2023/5/6
 **/
@Data
public class RobotControlResDTO {

    private boolean locked; //当前控制权是否被抢占

    private String ip; //控制权所有者 ip

    private Integer port; //控制权所有者端口号

    private String nickName; //控制权所有者昵称信息

    private String desc; //控制权所有者描述

    private float timeT; //抢占控制权的时间戳(s)

    private Integer type; //控制权所有者类型

    private String retCode; //API 错误码

    private String createOn; //API 上传时间戳

    private String errMsg; //错误信息
}
