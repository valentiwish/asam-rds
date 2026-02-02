package com.robotCore.robot.dto;

import lombok.Data;

/**
 * @Des: 机器人电池返回的实体类
 * @author: zxl
 * @date: 2023/5/29
 **/
@Data
public class RobotBatteryResDTO {
    private float batteryLevel; //机器人电池电量, 范围 [0, 1]

    private float batteryTemp; //机器人电池温度, 单位 ℃

    private boolean charging; //电池是否正在充电

    private float voltage; //电压, 单位 V

    private float current; //电流, 单位 A

    private float maxChargeVoltage; //允许充电的最大电压( -1 = 该电池不支持此功能), 单位 V

    private float maxChargeCurrent; //允许充电的最大电流( -1 = 该电池不支持此功能), 单位 A

    private Boolean manualCharge; //仅指示机器人是否连接手动充电器，不保证电源是否接通，连接手动充电器时不能运动(仅 SRC-2000 支持)

    private Boolean autoCharge; //仅指示机器人是否连接自动充电桩，不保证电源是否接通(仅 SRC-2000 支持)

    private float batteryCycle; //电池循环次数(从电池 BMS 获取，不保证正确)

    private String batteryUserData; //电池用户自定义数据

    private Integer retCode; //API 错误码

    private String createOn; //API 上传时间戳

    private String errMsg; //错误信息
}
