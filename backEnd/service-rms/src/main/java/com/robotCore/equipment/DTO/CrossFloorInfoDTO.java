package com.robotCore.equipment.DTO;

import lombok.Builder;
import lombok.Data;

/**
 * @Des: 跨楼层调度信息
 * @author: leo
 * @date: 2025/8/5
 */
@Data
@Builder
public class CrossFloorInfoDTO {

    // 起始楼层
    private int startFloor;

    // 起始楼层地图名称
    private String startFloorMapName;

    // 终止楼层
    private int endFloor;

    // 终止楼层地图名称
    private String endFloorMapName;

}
