package com.robotCore.multiScheduling.vo;

import lombok.Data;

/***
 * @Description: 缓存：门信息
 * @Author: zhangqi
 * @Create: 2024/6/22
 */
@Data
public class DoorInfoVO {
    /**
     * 工作间
     */
    private String workShop;

    /**
     * 工作区域
     */
    private String workRegion;

    /**
     * 门名称
     */
    private String doorName;

    /**
     * 门编码
     */
    private String doorCode;

    /**
     * 门类型，0-手推门，1-自动卷帘门，2-电梯门
     */
    private Integer doorType;

    /**
     * 关联门
     */
    private String linkDoor;

    /**
     * 工位编码
     */
    private String stationCode;

    /**
     * 自动门的关联变量
     */
    private String doorParam;

    /**
     * 自动门的反馈变量
     */
    private String backParam;

    /**
     * 工房编码
     */
    private String plantCode;

    /**
     * 所属部门
     */
    private String deptId;

    /**
     * 转运位置编码，唯一标识
     */
    private String locationCode;

    /**
     * 门状态，0-非开非关，1-开，2-关，3-故障
     */
    private Integer doorStatus = 0;

    /**
     * Agv请求，0-无，1-开门请求，2-关门请求
     */
    private Integer agvRequest = 0;

    /**
     * 门启用状态，0-未启用(预留)，1-启用
     */
    private Integer doorEnable = 1;

    /**
     * 更新时间
     */
    private String updateAt;
}
