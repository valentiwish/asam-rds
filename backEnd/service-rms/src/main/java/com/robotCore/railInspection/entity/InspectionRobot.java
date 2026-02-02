package com.robotCore.railInspection.entity;

import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.Table;
import com.robotCore.common.base.model.BaseModelU;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Des: 巡检机器人实体类
 * @author: zxl
 * @date: 2024/3/8
 **/

@Table(name = "inspection_robot") // 数据库中表名称
@Data
@AllArgsConstructor // 全参数构造函数
@NoArgsConstructor // 空参数的构造函数
@Accessors(chain = true) // 链表的访问
public class InspectionRobot extends BaseModelU {

    //机器人名称
    @Column(name = "robot_name")
    private String robotName;

    //机器人电量
    @Column(name = "battery")
    private  float battery;

}
