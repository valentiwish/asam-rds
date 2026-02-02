package com.robotCore.robot.entityVo;

import lombok.Data;

/**
 * @Des:
 * @author: zxl
 * @date: 2023/6/1
 **/
@Data
public class RobotMapInfoVO {

    private String currentMap; //当前地图

    private String[] maps; //所有储存在机器人上的地图名组成的数组
}
