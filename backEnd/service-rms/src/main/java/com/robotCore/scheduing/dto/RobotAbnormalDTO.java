package com.robotCore.scheduing.dto;

import lombok.Data;

/**
 * @Des:
 * @author: zxl
 * @date: 2023/7/3
 **/
@Data
public class RobotAbnormalDTO {

    //是否异常
    public boolean isAbnormal;

    //描述信息
    public String description;

}
