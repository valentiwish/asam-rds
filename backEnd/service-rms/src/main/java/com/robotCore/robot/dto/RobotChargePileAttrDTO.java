package com.robotCore.robot.dto;

import lombok.Data;

import java.util.List;

/**
 * @Des:
 * @author: zxl
 * @date: 2023/6/30
 **/
@Data
public class RobotChargePileAttrDTO {

    private Integer code;

    private List<String> vehicleIds;

    private String point;  //地图上的站点

    private String chargePileIp; //智能充电桩IP

    private String chargePileName; //智能充电桩名字
}
