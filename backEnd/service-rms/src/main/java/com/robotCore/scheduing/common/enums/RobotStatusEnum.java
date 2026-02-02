package com.robotCore.scheduing.common.enums;

/**
 * @Des: 机器人状态枚举
 * @author: zxl
 * @date: 2023/6/2
 **/
public enum RobotStatusEnum {
    /**
     * 可接单
     */
    ORDER_AVAILABLE(1, "可接单"),
    /**
     * 不接单占资源
     */
    NOT_ORDER_BUT_RESOURCE(2, "不接单占资源"),
    /**
     * 不接单不占资源
     */
    NOT_ORDER_NOT_RESOURCE(3, "不接单不占资源"),
    /**
     * 抢占控制权
     */
    SEIZE_CONTROL(4, "抢占控制权"),
    /**
     * 释放控制权
     */
    RELEASE_CONTROL(5, "释放控制权"),
    /**
     * 继续
     */
    CONTINUE(6, "继续"),
    /**
     * 暂停
     */
    PAUSE(7, "暂停"),
    /**
     * 确认定位
     */
    CONFIRM_LOCATION(8, "确认定位");


    /**
     * 成员变量：编码
     */
    private Integer code;

    /**
     * 成员变量：名称
     */
    private String name;

    /**
     * 构造方法
     * @param code
     * @param name
     */
     RobotStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "code=" + this.code + ",name=" + this.name;
    }

    public static RobotStatusEnum getByCode(Integer code){
        for (RobotStatusEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
