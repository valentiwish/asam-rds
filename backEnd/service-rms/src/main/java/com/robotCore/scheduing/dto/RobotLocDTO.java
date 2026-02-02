package com.robotCore.scheduing.dto;

import lombok.Data;

/**
 * @Des: 机器人位置返回的实体类
 * @author: zxl
 * @date: 2023/5/29
 **/
@Data
public class RobotLocDTO {
    private float x; //机器人的 x 坐标, 单位 m

    private float y; //机器人的 y 坐标, 单位 m

    private float angle; //机器人的 angle 坐标, 单位 rad
}
