package com.robotCore.scheduing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Des:
 * @author: zxl
 * @date: 2023/9/28
 **/
@Data
@AllArgsConstructor
public class RobotPathDTO {

    private String startPoint;

    private String endPoint;
}
