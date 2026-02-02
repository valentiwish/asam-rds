package com.robotCore.robot.dto;

import lombok.Data;

import java.util.List;

/**
 * @Des:
 * @author: zxl
 * @date: 2023/6/30
 **/
@Data
public class RobotBindAttrDTO {

    private Integer code;

    private List<String> vehicleIds;

    private String point;  //地图上的站点

    private String dmsIp; //光通信站IP

    private String dmsName; //光通信站名字
}
