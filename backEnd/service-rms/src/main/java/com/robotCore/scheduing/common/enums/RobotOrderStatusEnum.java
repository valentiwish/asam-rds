package com.robotCore.scheduing.common.enums;

/**
 * @Des: 机器人接单状态枚举
 * @author: zxl
 * @date: 2023/6/2
 **/
public enum RobotOrderStatusEnum {
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
    NOT_ORDER_NOT_RESOURCE(3, "不接单不占资源");


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
     RobotOrderStatusEnum(Integer code, String name) {
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

    public static RobotOrderStatusEnum getByCode(Integer code){
        for (RobotOrderStatusEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
