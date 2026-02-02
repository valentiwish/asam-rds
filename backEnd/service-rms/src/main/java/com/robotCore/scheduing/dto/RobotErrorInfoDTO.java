package com.robotCore.scheduing.dto;

import lombok.Data;

/**
 * @Des:
 * @author: zxl
 * @date: 2023/8/16
 **/
@Data
public class RobotErrorInfoDTO {

    private Integer times;  //错误次数

    private String desc;  //错误描述
}
