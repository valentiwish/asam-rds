package com.robotCore.scheduing.dto;

import lombok.Data;

/**
 * @Des:
 * @author: zxl
 * @date: 2023/6/15
 **/
@Data
public class RobotJackDTO {

    private String jackEmc;

    private double jackHeight;

    private Integer jackState;

    private Integer retCode;

    private String createOn;

    private String errMsg;
}
