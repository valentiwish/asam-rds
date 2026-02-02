package com.robotCore.robot.entityVo;

import lombok.Data;

/**
 * @Des:
 * @author: zxl
 * @date: 2023/6/28
 **/
@Data
public class RobotAttributeVO {

    private String id;

    private  Integer attributeCode;	//机器人属性Code

    private  String attributeName;  //机器人属性Name

    private  String point;  //绑定的点
}
