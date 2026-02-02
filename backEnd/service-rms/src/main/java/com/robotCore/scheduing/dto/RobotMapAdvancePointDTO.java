package com.robotCore.scheduing.dto;

import lombok.Data;

import java.util.List;

/**
 * @Des: 机器人地图高级点
 * @author: zxl
 * @date: 2023/6/28
 **/
@Data
public class RobotMapAdvancePointDTO {

    private String className;

    private String instanceName;

    private RobotMapPointDTO pos;

    private double dir;

    private List<RobotMapPropertyDTO> propertyDTOS;

    private boolean ignoreDir;
}
