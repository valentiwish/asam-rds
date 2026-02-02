package com.robotCore.common.constant;

/**
 * @Des: 机器人不同类别API对应的端口信息
 * @author: zxl
 * @date: 2023/4/14
 **/
public class PortConstant {
    /**
     * 机器人状态API端口
     */
    public static final Integer ROBOT_STATUS_PORT = 19204;

    /**
     * 机器人控制API端口
     */
    public static final Integer ROBOT_CONTROL_PORT = 19205;

    /**
     * 机器人导航API端口
     */
    public static final Integer ROBOT_NAVIGATION_PORT = 19206;

    /**
     * 机器人配置API端口
     */
    public static final Integer ROBOT_CONFIGURATION_PORT = 19207;

    /**
     * 机器人推送API端口
     */
    public static final Integer ROBOT_PUSH_PORT = 19301;

    /**
     * 其他API端口
     */
    public static final Integer ROBOT_OTHER_PORT = 19210;

    /**
     * DMS端口
     */
    public static final Integer DMS_PORT = 8088;

    /**
     * 智能充电桩充电端口
     */
    public static final Integer CHARGE_PILE_START = 4002;

    /**
     * 梯控端口
     */
    public static final Integer ELEVATOR_CONTROL_PORT = 8080;



}
