package com.robotCore.robot.entityVo;

import lombok.Data;

/**
 * @Des: 机器人基本信息
 * @author: zxl
 * @date: 2023/4/20
 **/
@Data
public class RobotByAddVO {

    private  String currentIp;  //页面配置 IP

    private  String robotType;  //机器人分类

    private String id;   //机器人Id

    private String vehicleId;  //机器人名称

    private String robotNote;  //机器人备注

    private String model;  //机器人模型名

    private String version;  //Robokit版本号

    private String dspVersion;  //底层固件版本号

    private String currentMap;  //当前地图

    private String currentMapMd5;  //md5

    private  String groupName;	//机器人分组

    private String batteryLevel;  //电池电量

    private float confidence;  //置信度

}
