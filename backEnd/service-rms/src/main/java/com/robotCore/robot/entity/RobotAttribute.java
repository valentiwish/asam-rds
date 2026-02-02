package com.robotCore.robot.entity;

import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.Table;
import com.robotCore.common.base.model.BaseModelU;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Des:
 * @author: zxl
 * @date: 2023/6/28
 **/
@Table(name = "robot_attribute") // 数据库中表名称
@Data
@AllArgsConstructor // 全参数构造函数
@NoArgsConstructor // 空参数的构造函数
@Accessors(chain = true) // 链表的访问
public class RobotAttribute extends BaseModelU {

    @Column(name = "vehicle_id")
    private String vehicleId;	//机器人名称

    //6--dmsParkPoint, 4--chargePoint
    @Column(name = "attribute_code")
    private Integer attributeCode;	//机器人属性Code

    @Column(name = "attribute_name")
    private String attributeName;	//机器人属性Name

    @Column(name = "point")
    private String point;  //绑定的点
}
