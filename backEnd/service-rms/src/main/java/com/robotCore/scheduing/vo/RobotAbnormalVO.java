package com.robotCore.scheduing.vo;

import com.robotCore.scheduing.dto.RobotAbnormalDTO;
import lombok.Data;

/**
 * @Des: 机器人异常的VO
 * @author: zxl
 * @date: 2023/7/3
 **/
@Data
public class RobotAbnormalVO {

    //机器人接单状态异常
    private RobotAbnormalDTO orderDto;

    //机器人电量
    private RobotAbnormalDTO batteryLevelDto;

}
