package com.robotCore.robot.entity;

import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.Table;
import com.robotCore.common.base.model.BaseModelU;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Des: 机器人设备
 * @author: zxl
 * @date: 2023/4/28
 **/
@Table(name = "robot_connect") // 数据库中表名称
@Data
@AllArgsConstructor // 全参数构造函数
@NoArgsConstructor // 空参数的构造函数
@Accessors(chain = true) // 链表的访问
public class RobotConnect extends BaseModelU {

    @Column(name = "current_ip")
    private  String currentIp;	//IP

    @Column(name = "current_map")
    private  String currentMap;	//当前地图名

    @Column(name = "vehicle_id")
    private  String vehicleId;	//机器人名称

    @Column(name = "robot_note")
    private  String robotNote;	//机器人备注

    @Column(name = "robot_type")
    private  String robotType;	//机器人类型

    @Column(name = "version")
    private  String version;  //版本

    @Column(name = "dsp_version")
    private  String dspVersion;  //dsp_version版本

    @Column(name = "current_map_md5")
    private  String currentMapMd5;  //current_map_md5

    @Column(name = "model")
    private  String model;  //model
}
