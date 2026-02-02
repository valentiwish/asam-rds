package com.robotCore.common.base.vo;

import lombok.Data;

import java.util.List;

/**
 * @Description: 基础树形结构实体类
 * @Author: zhangqi
 * @Create: 2021/10/19 15:11
 */
@Data
public class BasisNode {
    private String id;//编号

    private String name;//节点名称

    private String title;//节点标题

    private String url;// url地址信息

    private String pid;//父节点

    private String pName; //父节点名称

    private boolean checked = false;//是否选中

    private  boolean open = false;// 非叶子节点是否默认展开

    private boolean disabled = false;//是否可以点击

    private Integer  unreadmes ;//备注，用于获取附带数据

    private List chatList ;
}
