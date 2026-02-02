package com.robotCore.robot.entityVo;

import lombok.Data;

/**
 * @Des:
 * @author: zxl
 * @date: 2023/11/1
 **/
@Data
public class RobotTypeVO {

    /**
     * ID
     */
    private String id;

    /**
     * 序号
     */
    private Integer serialNumber;

    /**
     * 机器人类型名称
     */
    private String typeName;
}
