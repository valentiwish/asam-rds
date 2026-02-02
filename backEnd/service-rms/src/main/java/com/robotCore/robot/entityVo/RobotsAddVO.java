package com.robotCore.robot.entityVo;

import lombok.Data;

import java.util.List;

/**
 * @Des:
 * @author: zxl
 * @date: 2023/6/28
 **/
@Data
public class RobotsAddVO {

    private String groupName;

    private List<RobotByAddVO> data;
}
