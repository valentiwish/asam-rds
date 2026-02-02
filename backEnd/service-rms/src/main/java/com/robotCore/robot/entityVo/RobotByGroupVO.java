package com.robotCore.robot.entityVo;

import lombok.Data;

/**
 * @Des: 显示同一个分组中机器人的信息
 * @author: zxl
 * @date: 2023/6/29
 **/
@Data
public class RobotByGroupVO {

    private  String currentIp;  //页面配置 IP

    private String id;   //机器人Id

    private String vehicleId;  //机器人名称

    private String currentMap;  //当前地图
}
