package com.robotCore.common.base.vo;

/**
 * @Description: 基础树形结构各节点对象(id为UUID)
 */
public class BaseNodeU {
    /**
     * 主键
     */
    public String id;

    /**
     * 节点名称
     */
    public String name;

    /**
     * 节点标题
     */
    public String title;

    /**
     * url地址信息
     */
    public String url;

    /**
     * 父节点
     */
    public String pid;

    /**
     * 是否选中
     */
    public boolean checked = false;

    /**
     * 是否右键菜单
     */
    public boolean contextmenu = false;

    /**
     * 非叶子节点是否默认展开
     */
    public boolean open = false;

    /**
     * 额外字段
     */
    public String extraData;

}
