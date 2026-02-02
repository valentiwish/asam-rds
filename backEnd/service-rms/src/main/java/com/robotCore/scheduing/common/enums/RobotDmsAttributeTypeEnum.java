package com.robotCore.scheduing.common.enums;

/**
 * @Des: 导航状态
 * @author: zxl
 * @date: 2023/6/14
 **/
public enum RobotDmsAttributeTypeEnum {
    /**
     * 待命点
     */
    PARK_POINT(3, "待命点"),

    /**
     * 充电电
     */
    CHARGE_POINT(4, "充电点"),
    /**
     * DMS_POINT
     */
    DMS_POINT(5, "光通信站"),
    /**
     * DMS_PARK_POINT
     */
    DMS_PARK_POINT(6, "光通信待命点站");

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
    RobotDmsAttributeTypeEnum(Integer code, String name) {
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

    public static RobotDmsAttributeTypeEnum getByCode(Integer code){
        for (RobotDmsAttributeTypeEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }

    public static RobotDmsAttributeTypeEnum getByName(String name){
        for (RobotDmsAttributeTypeEnum value : values()) {
            if (value.getName().equals(name)) {
                return value;
            }
        }
        return null;
    }
}
