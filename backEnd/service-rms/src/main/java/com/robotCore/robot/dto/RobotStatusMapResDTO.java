package com.robotCore.robot.dto;

import lombok.Data;

/**
 * @Des: 机器人载入的地图以及储存的地图的响应
 * @author: zxl
 * @date: 2023/5/6
 **/
@Data
public class RobotStatusMapResDTO {

    private String currentMap; //当前地图

    private String[] maps; //所有储存在机器人上的地图名组成的数组

    private Integer retCode; //API错误码

    private String createOn; //API 上传时间戳

    private String errMsg; //错误信息
}
