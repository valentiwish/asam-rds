package com.robotCore.robot.dto;

import lombok.Data;

/**
 * @Des:
 * @author: zxl
 * @date: 2024/6/18
 **/
@Data
public class RobotDmsEditorReqDto {

//    private String robotName;	//机器人名称

    private String areaName;	//光通信区域的名字

    private String areaCenterPoint;	//光通信区域的中心点

    private String areaInfo;	//光通信区域信息

    private String areaContainPoints;  //光通信区域包含所有站点信息

    private Integer areaType;	//区域类型,0--普通区域，1--充电区域，2--管控区域

    //区域占用状态
    private Integer occupiedStatus;

    //区域内机器人IP
    private String robotIp;
}
