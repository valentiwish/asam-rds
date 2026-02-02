package com.robotCore.equipment.DTO;

import lombok.*;

/**
 * @Des: 电梯运行状态
 * @author: leo
 * @date: 2025/7/16
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ElevatorStatusDTO {
    private String robotId;
    private int currentFloor;

    // 核心运行状态
    private boolean movingUp;        // D0: 上行状态
    private boolean movingDown;      // D1: 下行状态
    private boolean rearDoorOpen;    // D2: 后门开门状态（不存在前后门时为到达状态arrived）
    private boolean frontDoorOpen;   // D3: 前门开门状态

    // 楼层状态
    private boolean floorCalibrated; // D4: 1层校准
    private boolean floorDetect2;    // D5: 楼层检测2
    private boolean floorDetect1;    // D6: 楼层检测1

    // 辅助方法
    public boolean isStopped() {
        return !movingUp && !movingDown;
    }

    public boolean isMoving() {
        return movingUp || movingDown;
    }

    /**
     * <获取指定电梯门的开启状态>
     *
     * @param isFrontDoor 是否前门
     * @return 是否开启
     */
    public boolean isDoorOpen(boolean isFrontDoor) {
        // 若为true，则返回前门的开启状态；若为false，则返回后门的开启状态
        return isFrontDoor? frontDoorOpen : rearDoorOpen;
    }

    /**
     * <电梯门是否关闭>
     *
     * @return 是否关闭
     */
    public boolean isDoorClosed() {
        return !frontDoorOpen && !rearDoorOpen;
    }

}
