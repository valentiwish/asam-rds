package com.robotCore.scheduing.dto;

import lombok.Data;

/**
 * @Des:
 * @author: zxl
 * @date: 2025/6/25
 **/
@Data
public class RobotLockPathAndPoint {

    private AlgorithmResResultDTO.Path lockedPath;

    private String lockedPoint;
}
