package com.robotCore.scheduing.common.enums;

/**
 * @Des: 机器人任务链状态
 * @author: zxl
 * @date: 2023/8/18
 **/
public enum RobotTaskListStatusEnum {
    /**
     * StatusNone
     */
    STATUS_NONE(0, "无任务"),
    /**
     * Waiting
     */
    WAITING(1, "等待"),
    /**
     * Running
     */
    RUNNING(2, "正在运行"),
    /**
     * Suspended
     */
    SUSPENDED(3, "暂停"),
    /**
     * Completed
     */
    COMPLETED(4, "完成"),
    /**
     * Failed
     */
    FAILED(5, "失败"),
    /**
     * Canceled
     */
    CANCELED(6, "取消"),
    /**
     * OverTime
     */
    OVERTIME(7, "超时");

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
    RobotTaskListStatusEnum(Integer code, String name) {
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

    public static RobotTaskListStatusEnum getByCode(Integer code){
        for (RobotTaskListStatusEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
