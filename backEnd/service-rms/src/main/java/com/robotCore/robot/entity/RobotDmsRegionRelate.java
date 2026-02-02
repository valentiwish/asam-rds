package com.robotCore.robot.entity;

import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.Table;
import com.robotCore.common.base.model.BaseModelU;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Des: 机器人光通信编辑器类
 * @author: zxl
 * @date: 2024/6/18
 **/
@Table(name = "robot_dms_region_relate") // 数据库中表名称
@Data
@AllArgsConstructor // 全参数构造函数
@NoArgsConstructor // 空参数的构造函数
@Accessors(chain = true) // 链表的访问
public class RobotDmsRegionRelate extends BaseModelU {

    @Column(name = "current_region_id")
    private String currentRegionId;	//当前区域ID

    @Column(name = "current_region_name")
    private String currentRegionName;	//当前的区域名称

    @Column(name = "current_region_point")
    private String currentRegionPoint;	//当前光通信区域的中心点

    @Column(name = "bind_region_id")
    private String bindRegionId;	//绑定的区域ID

    @Column(name = "bind_region_name")
    private String bindRegionName;	//绑定的区域名称

    @Column(name = "bind_region_point")
    private String bindRegionPoint;	//当前光通信区域的中心点

}
