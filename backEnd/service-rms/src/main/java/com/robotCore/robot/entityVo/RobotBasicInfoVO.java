package com.robotCore.robot.entityVo;

import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import lombok.Data;

/**
 * @Des: 机器人基本信息
 * @author: zxl
 * @date: 2023/4/20
 **/
@Data
public class RobotBasicInfoVO {

    private boolean isExistAbnormal; //机器人是否存在异常

    private  String currentIp;  //页面配置 IP

    private String id;   //机器人Id

    private String vehicleId;  //机器人名称

    private String robotNote;  //机器人备注

    private String model;  //机器人模型名

    private String version;  //Robokit版本号

    private String dspVersion;  //底层固件版本号

    private String currentMap;  //当前地图

    private String currentMapMd5;  //md5

    private  String groupName;	//机器人分组

    private float batteryLevel;  //电池电量

    private float confidence;  //置信度

    private boolean charging;  //充电

    private boolean online;  //在线

    private  Integer chargeOnly;	//Integer	机器人在电量下降到此值时，需要前往充电桩进行充电，并且在充电过程不可以接单。

    private  Integer chargeNeed;	//Integer	机器人在电量下降到此值时，需要前往充电桩进行充电，但是在充电过程中可以接单。

    private  Integer chargeOk;	//Integer	机器人在充电时接到订单，只有电量充到此值时，才可以离开充电桩去接单。

    private  Integer chargeFull;	//Integer	机器人在充电时，电量达到此值后可以离开充电点。

}
