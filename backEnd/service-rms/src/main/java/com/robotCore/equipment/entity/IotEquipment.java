package com.robotCore.equipment.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.Table;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import com.robotCore.common.base.model.BaseModelU;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

/**
 * @Des:
 * @author: leo
 * @date: 2024/10/24
 **/
@EqualsAndHashCode(callSuper = true)
@Table(name = "iot_equipment") // 数据库中表名称
@Data
@AllArgsConstructor // 全参数构造函数
@NoArgsConstructor // 空参数的构造函数
@Accessors(chain = true) // 链表的访问
public class IotEquipment extends BaseModelU {


    // 设备类型
    @Column(name = "equipment_type")
    private String equipmentType;

    public static class EquipmentType {
        // 自动门
        public static final String AUTOMATIC_DOOR = "automaticDoor";
        // 电梯
        public static final String ELEVATOR = "elevator";

    }

    // 通信类型
    @Column(name = "communication_type")
    private String communicationType;

    public static class CommunicationType {
        // MQTT
        public static final String MQTT = "MQTT";
        // MODBUS
        public static final String MODBUS = "Modbus";
        // TCP
        public static final String TIBOSHI_TCP = "TiBOSHI TCP";
    }

    // 设备编号
    @Column(name = "equipment_code")
    private String equipmentCode;

    // 组编号
    @Column(name = "group_code")
    private String groupCode;

    // 设备名称
    @Column(name = "equipment_name")
    private String equipmentName;

    // 设备IP
    @Column(name = "equipment_ip")
    private String equipmentIp;

    // Slave ID(modbus)

    //设备端口
    @Column(name = "equipment_port")
    private Integer equipmentPort;

    //门内部是否可以装光通信站
    @Column(name = "equipment_is_install_dms")
    private String equipmentIsInstallDms;

    //Slave ID(modbus)
    @Column(name = "slave_id")
    private int slaveId;

    // 寄存器状态位偏移地址(modbus)
    @Column(name = "equip_state_offset")
    private int equipStateOffset;

    // 寄存器写入位偏移地址(modbus)
    @Column(name = "write_offset")
    private int writeOffset;

    // Modbus 读写类型
    public static class ModbusRegisterType {
        // 读取线圈
        public static final int READ_COIL = 1;
        // 写入线圈
        public static final int WRITE_COIL = 2;
        // 读取保持寄存器
        public static final int READ_HOLDING_REGISTER = 3;
        // 写入保持寄存器
        public static final int WRITE_HOLDING_REGISTER = 4;
        // 读取输入寄存器
        public static final int READ_INPUT_REGISTER = 5;
        // 读取设备状态
    }

    // 通用Modbus读写返回结果
    @TableField(exist = false)
    private ModbusRwResult modbusRwResult = new ModbusRwResult();

    @Data
    public static class ModbusRwResult {
        private boolean success;
        private int value;
    }

    //相邻站点
    @Column(name = "adjacent_site")
    private String adjacentSite;

    /**
     * 电梯对应的虚拟路径列表
     */
    @Column(name = "virtual_paths", type = MySqlTypeConstant.MEDIUMTEXT)
    private String virtualPaths;

    //设备所在区域
    @Column(name = "equipment_region")
    private String equipmentRegion;

    //设备状态
    @Column(name = "equipment_status")
    private int equipmentStatus;

    // 开门状态，不为null
    @Column(name = "door_opened_status", isNull = false)
    private int doorOpenedStatus;

    // 自动门开启状态
    public static class AutomaticDoorOpenedStatus {
        // 自动门打开
        public static final int AUTOMATIC_DOOR_OPEN = 1;
        // 自动门关闭
        public static final int AUTOMATIC_DOOR_CLOSE = 2;
        // 自动门状态未初始化
        public static final int AUTOMATIC_DOOR_STATUS_UNINITIATED = 0;
    }

    // 主题(MQTT)
    @Column(name = "topic")
    private String topic;

    //打开(MQTT)
    @Column(name = "open")
    private String open;

    //关闭(MQTT)
    @Column(name = "close")
    private String close;

    //打开到位(MQTT)
    @Column(name = "open_end")
    private String openEnd;

    //关闭到位(MQTT)
    @Column(name = "close_end")
    private String closeEnd;

}