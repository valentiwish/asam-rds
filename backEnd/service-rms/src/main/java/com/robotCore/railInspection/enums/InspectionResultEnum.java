package com.robotCore.railInspection.enums;

/**
 * @Des:
 * @author: zxl
 * @date: 2023/11/6
 **/
public enum InspectionResultEnum {

//    /**
//     * 向上移动
//     */
//    UP(0, "向上移动"),
//    /**
//     * 向下移动
//     */
//    DOWN1(1, "向下移动"),
//    /**
//     * 向下移动
//     */
//    DOWN2(2, "向下移动"),
//    /**
//     * 向左移动
//     */
//    LEFT1(3, "向左移动"),
//    /**
//     * 向左移动
//     */
//    LEFT2(4, "向左移动"),
//    /**
//     * 向右移动
//     */
//    RIGHT(5, "向右移动"),
//    /**
//     * 停止移动
//     */
//    STOP(13, "停止移动"),
//    /**
//     * 没有手势
//     */
//    NULL(18, "没有手势"),

    /**
     * 前进
     */
    GO_ON1(15,"前进"),

    /**
     * 前进
     */
    GO_ON2(8,"前进"),

    /**
     * 后退
     */
    BACK1(0,"后退"),

    /**
     * 后退
     */
    BACK2(16,"后退"),

    /**
     * 停止
     */
    STOP1(7,"停止"),

    /**
     * 停止
     */
    STOP2(13,"停止");


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
    InspectionResultEnum(Integer code, String name) {
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

    public static InspectionResultEnum getByCode(Integer code){
        for (InspectionResultEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
