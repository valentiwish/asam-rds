package com.robotCore.robot.dto;

import lombok.Data;

/**
 * @Des:
 * @author: zxl
 * @date: 2024/4/26
 **/
@Data
public class RobotForkResDTO {

    private float forkHeight; //货叉高度, 单位 m

    private boolean forkHeightInPlace;  //货叉高度是否到位, true = 到位, false = 未到位

    private boolean forkAutoFlag;  //叉车的控制模式

    private float forwardVal;  //货叉前移后退距离, 单位: m	是

    private boolean forwardInPlace; //货叉前移后退是否到位, true = 到位, false = 未到位	是

    private float forkPressureActual; //货叉称重数据，单位：kg

    private Integer retCode; //API 错误码

    private String createOn; //API 上传时间戳

    private String errMsg; //错误信息
}
