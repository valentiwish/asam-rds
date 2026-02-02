package com.robotCore.scheduing.common.enums;

/**
 * @Des: 导航状态
 * @author: zxl
 * @date: 2023/6/14
 **/
public enum NavigationStatusEnum {
    /**
     * NONE
     */
    NONE(0, "没有导航"),
    /**
     * WAITING
     */
    WAITING(1, "目前不可能出现该状态"),
    /**
     * RUNNING
     */
    RUNNING(2, "导航正在运行"),
    /**
     * SUSPENDED
     */
    SUSPENDED(3, "导航暂停"),
    /**
     * COMPLETED
     */
    COMPLETED(4, "导航完成"),
    /**
     * FAILED
     */
    FAILED(5, "导航失败"),
    /**
     * CANCELED
     */
    CANCELED(6, "取消导航");


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
    NavigationStatusEnum(Integer code, String name) {
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

    public static NavigationStatusEnum getByCode(Integer code){
        for (NavigationStatusEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }

    public static NavigationStatusEnum getByName(String name){
        for (NavigationStatusEnum value : values()) {
            if (value.getName().equals(name)) {
                return value;
            }
        }
        return null;
    }
}
