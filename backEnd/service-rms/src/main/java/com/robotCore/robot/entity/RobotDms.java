package com.robotCore.robot.entity;

import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.Table;
import com.robotCore.common.base.model.BaseModelU;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Des: 机器人光通信实体类
 * @author: zxl
 * @date: 2023/7/21
 **/
@Table(name = "robot_dms") // 数据库中表名称
@Data
@AllArgsConstructor // 全参数构造函数
@NoArgsConstructor // 空参数的构造函数
@Accessors(chain = true) // 链表的访问
public class RobotDms extends BaseModelU {

    @Column(name = "dms_name")
    private String dmsName;	//光通信站Name

    @Column(name = "dms_ip")
    private String dmsIp;	//光通信站的Ip

    @Column(name = "dms_point")
    private String dmsPoint;	//光通信站绑定的地图站点

    @Column(name = "dms_type")
    private String dmsType;	//光通信站类型

}
