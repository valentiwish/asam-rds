package com.robotCore.scheduing.vo;

import com.robotCore.scheduing.dto.RobotWarningInfoDTO;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @Des:
 * @author: zxl
 * @date: 2023/4/19
 **/
@Data
public class RobotMonitorVO {
    /**
     * 机器人基本信息
     */
    private String groupName;  //机器人分组
    private String currentMap; //当前地图名
    private String currentMapMd5; //当前地图 MD5 值
    private float controllerTemp;//控制器温度, 单位 ℃
    private float controllerHumi;//控制器湿度, 单位 %
    private float controllerVoltage;//控制器电压, 单位 V
    private String dspVersion; // 固件版本号
    private String currentIp;  //ip
    private String model;  //机器人模型名
    private String robotNote;  //备注
    private String version;  //Robokit版本号

    /**
     * 当前导航路径上已经经过的站点, 为站点的数组,
     */
    private String endFinishedPath;

    /**
     * 编号
     */
    private String vehicleId;  //机器人名称（UUID/小车编号）

    /**
     * rbk信息
     */
    private  String disMap;
    private  String dosMap;
    private String DI; //DI
    private String DO; //DO
    private String angle; //机器人的 angle 坐标, 单位 rad
    private float batteryLevel; //机器人电池电量, 范围 [0, 1]
    private boolean blocked; //机器人是否被阻挡
    private boolean brake; //是否抱闸
    private boolean charging; //电池是否正在充电
    private float confidence;  //机器人置信度
    private String voltage; //电压, 单位 V
    private String current; //电流, 单位 A
    private String currentStation;//机器人当前站点的 id
    private boolean softEmc; //是否处于软急停状态
    private boolean emergency; //急停按钮处于激活状态
    private float steer; //单舵轮机器人当前的舵轮角度, 单位 rad
    private String odo; //累计行驶里程, 单位 m
    private String todayOdo; //今日累计行驶里程, 单位 m
    private String time; //本次运行时间(开机后到当前的时间), 单位 ms
    private String totalTime; //累计运行时间, 单位 ms
    private String vx; //机器人在机器人坐标系的 x 方向实际的速度, 单位 m/s
    private String vy; //机器人在机器人坐标系的 y 方向实际的速度, 单位 m/s
    private String w; //机器人在机器人坐标系的实际的角速度(即顺时针转为负, 逆时针转为正), 单位 rad/s
    private float x;  //机器人x坐标
    private float y; //机器人y坐标
    private String relocStatus; //0 = FAILED(定位失败),1 = SUCCESS(定位正确),2 = RELOCING(正在重定位),3=COMPLETED(定位完成)
    private String taskStatus;//导航状态
    private String jackStatus;//顶升顶降状态
    private String controlStatus;//控制权状态

    /**
     * 机器人控制权详情
     */
    private String controlDesc; //控制权所有者描述
    private String controlIp; //控制权所有者 ip
    private boolean controlLocked; //当前控制权是否被抢占
    private String controlNickName; //控制权所有者昵称信息
    private Integer controlPort; //控制权所有者端口号
    private float controlTimeT; //抢占控制权的时间戳(s)
    private Integer controlType; //控制权所有者类型

    /**
     * 机器人叉车详情
     */
    private float forkHeight; //货叉高度, 单位 m
    private boolean forkHeightInPlace;  //货叉高度是否到位, true = 到位, false = 未到位
    private boolean forkAutoFlag;  //叉车的控制模式
    private float forwardVal;  //货叉前移后退距离, 单位: m	是
    private boolean forwardInPlace; //货叉前移后退是否到位, true = 到位, false = 未到位	是
    private float forkPressureActual; //货叉称重数据，单位：kg

    /**
     * 机器人不可接单原因
     */
    private boolean isZaiXian;  //机器人是否在线（与机器人网络断联）
    private String orderStatus; //可接单状态
    private boolean isLowBatteryLevel; //是否低电量
    private boolean isOrderSuspended; //是否订单被暂停
    private boolean isReLoc; //是否确认定位

    /**
     * 报警信息
     */
    private List<RobotWarningInfoDTO> fatals;//报警码 Fatal的数组, 所有出现的 Fatal 报警都会出现在数据中
    private List<RobotWarningInfoDTO> errors; //报警码 Error的数组, 所有出现的 Error 报警都会出现在数据中
    private List<RobotWarningInfoDTO> warnings; //报警码 Warning的数组, 所有出现的 Warning 报警都会出现在数据中
    private List<RobotWarningInfoDTO> notices; //报警码 Warning的数组, 所有出现的 Warning 报警都会出现在数据中

    /**
     * 任务链状态
     */
    private String taskListName; //任务链名称
    private Integer taskListStatus; //任务链执行状态
}
