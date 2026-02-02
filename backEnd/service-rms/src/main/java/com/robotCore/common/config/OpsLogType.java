package com.robotCore.common.config;

/**
 * @Description: 业务操作分类枚举
 * @Author: Created by crystal on 2020/4/29.
 * @Modifier: Modify by crystal on 2020/4/29.
 */
public enum OpsLogType {
    /**
     * 添加
     */
    ADD("01", "添加"),
    /**
     * 修改
     */
    UPDATE("02", "修改"),
    /**
     * 删除
     */
    DELETE("03", "删除"),
    /**
     * 查询
     */
    SELECT("04", "查询"),
    /**
     * 设置
     */
    SET("05", "设置"),
    /**
     * 重置
     */
    RESET("06", "重置"),
    /**
     * 停用
     */
    STOP("07", "停用"),
    /**
     * 上传
     */
    UPLOAD("08", "上传"),
    /**
     * 下载
     */
    DOWNLOAD("09", "下载"),
    /**
     * 下载
     */
    LOGIN("10", "登录"),
    /**
     * 下载
     */
    LOGOUT("11", "登出"),

    /**
     * 新增或更新
     */
    ADDORUPDATE("12","新增或更新");

    /**
     * 成员变量：编码
     */
    private String code;

    /**
     * 成员变量：名称
     */
    private String name;

    /**
     * 构造方法
     * @param code
     * @param name
     */
    private OpsLogType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
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
}
