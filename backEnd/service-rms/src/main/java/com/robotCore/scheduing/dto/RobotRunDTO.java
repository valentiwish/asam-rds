package com.robotCore.scheduing.dto;

import lombok.Data;

/**
 * @Des:
 * @author: zxl
 * @date: 2023/6/14
 **/
@Data
public class RobotRunDTO {

    //机器人名称
    private String vehicle;

    //组名
    private String groupName;

    //站点
    private String station;

    //指令
    private String instruction;

    //DO编号
    private String waitDoId;

    //DO状态
    private String waitDoStatus;

    //DO等待时间
    private String waitDoTime;

    //叉货高度
    private String forkLiftHeight;

    //DI编号
    private String waitDiId;

    //DI状态
    private String waitDiStatus;

    //DI延时
    private String waitDiTime;

    //binTask叉货
    private String forkLoadOrUnload;

    //binTask 动作
    private String loadOrUnload;

    //binTask 库位
    private String locationLabel;

    //自定义动作
    private String customizedActions;

    //modbus块参数（leo add）
    private ModbusParam modbusParam = new ModbusParam();
    @Data
    public static class ModbusParam {
        /**
         * 从站IP
         */
        private String ip;

        /**
         * 从站端口
         */
        private Integer port;

        /**
         * 从站slaveId
         */
        private Integer slaveId;

        /**
         * 寄存器偏移地址
         */
        private Integer offset;

        /**
         * Modbus操作类型
         */
        private Integer operationType;

        /**
         * Modbus通用块为写入值，Modbus等待块为目标值
         */
        private Integer value;
    }
}
