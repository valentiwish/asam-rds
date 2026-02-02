package com.asam.common.base.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @Description: 基础树形结构实体类
 * @Author: mln
 * @Create: 2018-10-22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class BasisNode{

    private String id;//编号

    private String code;//编码

    private String name;//节点名称

    private String title;//节点标题

    private String url;// url地址信息

    private String pid;//父节点

    private boolean checked = false;//是否选中

    private  boolean open = false;// 非叶子节点是否默认展开

    private boolean disabled = false;//是否可以点击

    private Integer  unreadmes ;//备注，用于获取附带数据

    private List chatList ;
}