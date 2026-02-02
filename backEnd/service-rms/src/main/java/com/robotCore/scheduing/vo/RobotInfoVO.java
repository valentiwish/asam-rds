package com.robotCore.scheduing.vo;

import com.robotCore.scheduing.dto.AlgorithmResResultDTO;
import lombok.Data;

/**
 * @Des: 调度系统机器人模块的Vo
 * @author: zxl
 * @date: 2023/6/8
 **/
@Data
public class RobotInfoVO {

    private String id;   //机器人Id

    private String robotType;   //机器人Id

    private String vehicleId;  //机器人名称

    private  String currentIp;  //页面配置 IP

    private String groupName;  //机器人组

    private Integer leisureState;  //机器人空闲状态 0--不空闲， 1--空闲

    private Integer orderState;  //机器人接单状态 0--不可接单，1--可接单

    private String executingWaybill;  //正在执行的运单

    private String waybillState;  //运单状态

    private String batteryLevel;  //电池电量

    private String confidence;  //置信度

    private Integer controlState;  //控制权

    private Integer locationState;  //定位状态

    private Integer navigationState;  //导航状态

    private String currentMap;  //当前地图

    private boolean charging; //电池是否正在充电

    private boolean online; //机器人是否在线

    private String lockPoint; //当前锁定站点集合

    private String lockPath; //锁定的路径

}
