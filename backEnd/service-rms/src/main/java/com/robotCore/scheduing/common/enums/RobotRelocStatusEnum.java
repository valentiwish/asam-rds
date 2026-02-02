package com.robotCore.scheduing.common.enums;

/**
 * @Des: 机器人重定位状态枚举
 * @author: zxl
 * @date: 2023/6/8
 **/
public enum RobotRelocStatusEnum {
    /**
     * 定位失败
     */
    FAILED(0, "定位失败"),
    /**
     * 未完成
     */
    SUCCESS(1, "定位正确"),
    /**
     * 正在重定位
     */
    RELOCING(2, "正在重定位"),
    /**
     * 定位完成
     */
    COMPLETED(3, "定位完成");


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
    RobotRelocStatusEnum(Integer code, String name) {
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

    public static RobotRelocStatusEnum getByCode(Integer code){
        for (RobotRelocStatusEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }

    public static RobotRelocStatusEnum getByName(String name){
        for (RobotRelocStatusEnum value : values()) {
            if (value.getName().equals(name)) {
                return value;
            }
        }
        return null;
    }
}
