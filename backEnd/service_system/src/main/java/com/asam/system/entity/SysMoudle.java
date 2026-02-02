package com.asam.system.entity;

import com.asam.common.base.model.BaseModelL;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * @Description: 模块管理
 * @Author: fyy
 * @Create: 2022-04-19
 */
@SuppressWarnings("serial")
@Data
@Table(name = "sys_moudle")
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class SysMoudle extends BaseModelL {

    /**
     * 菜单按钮code
     */
    @Column(name = "code", length = 50)
    public String code;

    /**
     * 名称
     */
    @Column(name = "name", length = 50)
    public String name;

    /**
     * 资源路径
     */
    @Column(name = "action_url", length = 100)
    public String actionUrl;

    /**
     * 模块背景图片地址
     */
    @Column(name = "img_url", length = 100)
    public String imgUrl;

    /**
     * 图标
     */
    @Column(name = "icon", length = 50)
    public String icon;

    /**
     * 父编号
     */
    @Column(name = "parent_id", length = 11)
    public Long parentId;

    @Column(name = "parent_name")
    public String parentName;

    /**
     * 排序标识
     */
    @Column(name = "sort", length = 11)
    public Integer sort;

    /**
     * 是否附带按钮:1是  0否   在内部嵌套的按钮资源（比如人员新增，在按钮元素中添加上一个，在添加一行人员新增的行数据）
     */
    @Column(name = "is_operation", length = 11)
    public Integer isOperation;

    /**
     * 状态--是否启用：0不启用，1启用
     */
    @Column(name = "enable", length = 11)
    public Integer enable;

    /**
     * 状态--是否显示在左侧菜单：0不显示，1显示
     */
    @Column(name = "is_display")
    public Integer isDisplay;

    /**
     * 描述
     */
    @Column(name = "description")
    public String description;

    /**
     * 系统基础数据id
     */
    @Column(name = "system_id")
    public Long systemId;

    /**
     * json描述，用户存放一些json格式的配置信息
     */
    @Column(name = "json_css", length = 800)
    public String jsonCss;
}