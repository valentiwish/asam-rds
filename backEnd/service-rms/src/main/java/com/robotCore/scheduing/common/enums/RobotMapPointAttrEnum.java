package com.robotCore.scheduing.common.enums;


/**
 * @Des: API错误码
 * @author: zxl
 * @date: 2023/6/8
 **/
public enum RobotMapPointAttrEnum {
//    /**
//     * delayFinishTime
//     */
//    DELAY_FINISH_TIME(0, "delayFinishTime"),
//    /**
//     * stopForBlockGroup
//     */
//    STOP_FOR_BLOCK_GROUP(1, "stopForBlockGroup"),
//    /**
//     * waitForDistribute
//     */
//    WAIT_FOR_DISTRIBUTE(2, "waitForDistribute"),
    /**
     * parkPoint
     */
    PARK_POINT(3, "parkPoint"),

    /**
     * chargePoint
     */
    CHARGE_POINT(4, "chargePoint"),

    /**
     * dmsPoint
     */
    DMS_POINT(5, "dmsPoint"),

    /**
     * dmsParkPoint
     */
    DMS_PARK_POINT(6, "dmsParkPoint");


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
    RobotMapPointAttrEnum(Integer code, String name) {
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

    public static RobotMapPointAttrEnum getByCode(Integer code){
        for (RobotMapPointAttrEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }

    public static RobotMapPointAttrEnum getByName(String name){
        for (RobotMapPointAttrEnum value : values()) {
            if (value.getName().equals(name)) {
                return value;
            }
        }
        return null;
    }
}
