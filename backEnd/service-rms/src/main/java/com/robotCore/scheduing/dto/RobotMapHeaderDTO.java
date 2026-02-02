package com.robotCore.scheduing.dto;

import lombok.Data;

/**
 * @Des: 机器人地图头部信息
 * @author: zxl
 * @date: 2023/6/28
 **/
@Data
public class RobotMapHeaderDTO {

    private String mapType;

    private String mapName;

    private RobotMapPointDTO minPos;

    private RobotMapPointDTO maxPos;

    private float resolution;

    private String version;
}
