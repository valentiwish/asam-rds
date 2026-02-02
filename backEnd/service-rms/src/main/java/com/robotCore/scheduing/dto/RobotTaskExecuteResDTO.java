package com.robotCore.scheduing.dto;

import lombok.Data;

/**
 * @Des:
 * @author: zxl
 * @date: 2023/9/11
 **/
@Data
public class RobotTaskExecuteResDTO {

    private boolean isSuccess;

    private String instruction;

    private String robotIp;
}
