package com.robotCore.scheduing.common.enums;

/**
 * @Des: 机器人运单状态的枚举
 * @author: zxl
 * @date: 2023/8/15
 **/
public enum RobotWaybillStatusEnum {
    /**
     * 等待
     */
    WAIT(0, "等待"),
    /**
     * 正在执行
     */
    EXECUTING(1, "正在执行"),
    /**
     * 完成
     */
    COMPLETE(2, "完成"),
    /**
     * 终止
     */
    TERMINATE(3, "失败"),
    /**
     * 取消
     */
    CANCEL(4, "取消"),

    /**
     * 等待DO
     */
    WAIT_DO(5, "等待DO"),

    /**
     * 取货完成
     */
    PICK_UP_COMPLETED(6,"取货完成"),

    /**
     * 放货完成
     */
    PICK_DOWN_COMPLETED(7,"放货完成"),

    /**
     * 已下发
     */
    ISSUED(8,"已下发");


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
    RobotWaybillStatusEnum(Integer code, String name) {
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

    public static RobotWaybillStatusEnum getByCode(Integer code){
        for (RobotWaybillStatusEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
