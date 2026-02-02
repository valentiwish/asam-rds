package com.robotCore.robot.dto;

import lombok.Data;


/**
 * @Des:
 * @author: zxl
 * @date: 2024/6/18
 **/
@Data
public class RobotDmsRegionRelateReqDto {

    private String currentRegionId;	//当前区域id

    private String bindRegionIds;	//被绑定的区域id列表

}
