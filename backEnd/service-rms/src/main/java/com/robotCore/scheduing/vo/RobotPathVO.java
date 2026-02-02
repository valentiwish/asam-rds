package com.robotCore.scheduing.vo;

import lombok.Data;

/**
 * @Des: 机器人路径规划，用于提供给路径规划算法使用
 * @author: zxl
 * @date: 2023/8/25
 **/
@Data
public class RobotPathVO {

    private String groupName;

    private String startPoint;

    private String endPoint;

    private double curveLength;
}
