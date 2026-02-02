package com.robotCore.scheduing.common.enums;

/**
 * @Des: 单行状态
 * @author: zxl
 * @date: 2023/6/14
 **/
public enum JackStatusEnum {
    /**
     * 上升中
     */
    NONE(0, "上升中"),
    /**
     * 上升到位
     */
    WAITING(1, "上升到位"),
    /**
     * 下降中
     */
    RUNNING(2, "下降中"),
    /**
     * 下降到位
     */
    SUSPENDED(3, "下降到位"),
    /**
     * 停止
     */
    COMPLETED(4, "停止"),
    /**
     * 执行失败
     */
    CANCELED(255, "执行失败");


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
    JackStatusEnum(Integer code, String name) {
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

    public static JackStatusEnum getByCode(Integer code){
        for (JackStatusEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
