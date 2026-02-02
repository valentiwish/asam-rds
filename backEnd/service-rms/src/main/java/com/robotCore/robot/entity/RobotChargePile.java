package com.robotCore.robot.entity;

import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.Table;
import com.robotCore.common.base.model.BaseModelU;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Des: 机器人智能充电桩实体类
 * @author: zxl
 * @date: 2023/7/21
 **/
@Table(name = "robot_charge_pile") // 数据库中表名称
@Data
@AllArgsConstructor // 全参数构造函数
@NoArgsConstructor // 空参数的构造函数
@Accessors(chain = true) // 链表的访问
public class RobotChargePile extends BaseModelU {

    @Column(name = "charge_pile_name")
    private String chargePileName;    //充电桩Name

    @Column(name = "charge_pile_ip")
    private String chargePileIp;    //充电桩的Ip

    @Column(name = "charge_pile_point")
    private String chargePilePoint;    //充电桩绑定的地图站点

    //通信类型
    @Column(name = "communication_type")
    private String communicationType;

    /**
     * ------------- Modbus协议相关参数 -------------
     */

    // Slave Id
    @Column(name = "slave_id")
    private int slaveId;

    // 手动伸出按钮偏移地址
    @Column(name = "manual_extend_offset")
    private int manualExtendOffset;

    // 手动缩回按钮偏移地址
    @Column(name = "manual_retract_offset")
    private int manualRetractOffset;

    // 强制启动按钮偏移地址
    @Column(name = "force_start_offset")
    private int forceStartOffset;

    // 启动按钮偏移地址
    @Column(name = "start_offset")
    private int startOffset;

    // 暂停按钮偏移地址
    @Column(name = "pause_offset")
    private int pauseOffset;

    // 停止按钮偏移地址
    @Column(name = "stop_offset")
    private int stopOffset;

    // 复位按钮偏移地址
    @Column(name = "reset_offset")
    private int resetOffset;

    // 状态寄存器偏移地址
    @Column(name = "status_register_offset")
    private int statusRegisterOffset;

    // ------------- Modbus协议相关参数 -------------

}
