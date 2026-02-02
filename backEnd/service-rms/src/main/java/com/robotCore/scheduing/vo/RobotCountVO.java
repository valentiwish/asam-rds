package com.robotCore.scheduing.vo;


import lombok.Data;

/**
 * @Des: 机器人的总个数和在线机器人的数量
 * @author: zxl
 * @date: 2023/5/26
 **/
@Data
public class RobotCountVO {

    private Integer allRobots;

    private Integer onlineRobots;
}
