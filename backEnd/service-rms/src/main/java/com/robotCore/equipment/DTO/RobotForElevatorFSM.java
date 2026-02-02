package com.robotCore.equipment.DTO;

/**
 * @Des: 机器人相对电梯的状态机（机器人进入电梯区域后，状态机生效）
 * @author: leo
 * @date: 2025/8/1
 */
public class RobotForElevatorFSM {
    // 与电梯无绑定关系
    public static final int FREE = 0;

    // 等待电梯到达起始楼层：该状态下发送登记起始楼层指令
    public static final int WAIT_ELEVATOR_ARRIVE_START = 1;
    // 等待进电梯开门
    public static final int WAIT_IN_ELEVATOR_OPEN = 2;
    // 正在进入电梯
    public static final int ENTERING_ELEVATOR = 3;
    // 等待电梯运行：该状态下发送关门、登记目标楼层指令
    public static final int WAIT_ELEVATOR_RUNNING = 4;

    // 等待电梯到达目标楼层
    public static final int WAIT_ELEVATOR_ARRIVE_TARGET = 5;

    // 等待出电梯开门
    public static final int WAIT_OUT_ELEVATOR_OPEN = 6;
    // 正在出电梯门
    public static final int OUTING_ELEVATOR_DOOR = 7;
    // 已经离开电梯
    public static final int LEAVE_ELEVATOR = 8;
}
