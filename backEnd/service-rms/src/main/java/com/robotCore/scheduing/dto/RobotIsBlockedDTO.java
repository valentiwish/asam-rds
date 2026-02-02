package com.robotCore.scheduing.dto;

import lombok.Data;

/**
 * @Des: 机器人是否被阻挡
 * @author: zxl
 * @date: 2023/9/20
 **/
@Data
public class RobotIsBlockedDTO {

    private String vehicleId;

    private String currentStation;

    private float x;

    private float y;

    private boolean isBlocked;
}
