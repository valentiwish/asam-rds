package com.robotCore.robot.entity;

import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.Table;
import com.robotCore.common.base.model.BaseModelU;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Des: 机器人分组的实体类
 * @author: zxl
 * @date: 2023/4/17
 **/
@Table(name = "robot_group") // 数据库中表名称
@Data
@AllArgsConstructor // 全参数构造函数
@NoArgsConstructor // 空参数的构造函数
@Accessors(chain = true) // 链表的访问
public class RobotGroup extends BaseModelU {

    @Column(name = "group_name", length = 10)
    private String groupName; //分组名称

    @Column(name = "group_des", length = 100)
    private String groupDes; //分组描述

    @Column(name = "pooling")
    private Integer pooling; //0--false,1--true
}
