package com.robotCore.scheduing.dto;

import lombok.Data;

import java.util.List;

/**
 * @Des: 机器人地图
 * @author: zxl
 * @date: 2023/6/9
 **/
@Data
public class RobotMapDTO {

    private RobotMapHeaderDTO header;

    private List<RobotMapAdvancePointDTO> advancedPointList;

    private List<MapAdvancedCurveDTO> advancedCurveList;

}
