package com.robotCore.scheduing.common.enums;

/**
 * @Des: 控制权状态的枚举
 * @author: zxl
 * @date: 2023/6/7
 **/
public enum RobotTaskPriorityEnum {
    /**
     * 低优先级
     */
    LOW_PRIORITY(1, "低优先级"),
    /**
     * 中优先级
     */
    MEDIUM_PRIORITY(2, "中优先级"),
    /**
     * 中优先级
     */
    HEIGHT_PRIORITY(3, "中优先级");

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
    RobotTaskPriorityEnum(Integer code, String name) {
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

    public static RobotTaskPriorityEnum getByCode(Integer code){
        for (RobotTaskPriorityEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }

    public static RobotTaskPriorityEnum getByName(String name){
        for (RobotTaskPriorityEnum value : values()) {
            if (value.getName().equals(name)) {
                return value;
            }
        }
        return null;
    }
}
