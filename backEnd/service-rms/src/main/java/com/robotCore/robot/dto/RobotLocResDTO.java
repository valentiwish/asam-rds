package com.robotCore.robot.dto;

import lombok.Data;

/**
 * @Des: 机器人位置返回的实体类
 * @author: zxl
 * @date: 2023/5/29
 **/
@Data
public class RobotLocResDTO {
    private float x; //机器人的 x 坐标, 单位 m

    private float y; //机器人的 y 坐标, 单位 m

    private float angle; //机器人的 angle 坐标, 单位 rad

    private float confidence; //机器人的定位置信度, 范围 [0, 1]

    private String currentStation; //离机器人最近站点的 id

    private Integer retCode; //API 错误码

    private String createOn; //API 上传时间戳

    private String errMsg; //错误信息
}
