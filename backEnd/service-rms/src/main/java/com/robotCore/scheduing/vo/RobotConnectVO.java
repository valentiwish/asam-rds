package com.robotCore.scheduing.vo;

import lombok.Data;

/**
 * @Des:
 * @author: zxl
 * @date: 2023/6/1
 **/
@Data
public class RobotConnectVO {

    private String id;  //机器人id

    private String typeName;  //机器人id

    private  String currentIp;	//IP

    private  String currentMap;	//当前地图名

    private  String vehicleId;	//机器人名称

    private  String robotNote;	//机器人备注

    private  String version;  //版本

    private  boolean online;  //是否在线
}
