package com.robotCore.scheduing.common.enums;

/**
 * @Des: 控制权状态的枚举
 * @author: zxl
 * @date: 2023/6/7
 **/
public enum ControlStatusEnum {
    /**
     * 已抢占控制权
     */
    SEIZED_CONTROL(0, "已抢占控制权"),
    /**
     * 无人抢占控制权
     */
    UN_PREEMPTED_CONTROL(1, "无人抢占控制权"),
    /**
     * 控制权被抢占
     */
    LOSS_OF_CONTROL(2, "控制权被抢占");

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
    ControlStatusEnum(Integer code, String name) {
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

    public static ControlStatusEnum getByCode(Integer code){
        for (ControlStatusEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }

    public static ControlStatusEnum getByName(String name){
        for (ControlStatusEnum value : values()) {
            if (value.getName().equals(name)) {
                return value;
            }
        }
        return null;
    }
}
