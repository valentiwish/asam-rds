package com.asam.system.entity;

import com.asam.common.base.model.BaseModelU;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Description: 设置系统变量开关，用户是否检查白名单等项目
 * @Author: fyy
 * @Create: 2022-07-26
 */
@SuppressWarnings("serial")
@Table(name = "sys_variable") // 数据库中表名称
@Data
@AllArgsConstructor // 全参数构造函数
@NoArgsConstructor // 空参数的构造函数
@Accessors(chain = true) // 链表的访问
public class SysVariable extends BaseModelU {

    /**
     * 变量编码
     */
    @Column
    private String varCode;

    /**
     * 变量名称
     */
    @Column
    private String varName;

    /**
     * 变量描述
     */
    @Column
    private String varSpec;

    /**
     * 数据类型
     */
    @Column
    private String dataType;

    /**
     * 值类型
     */
    @Column
    private String valueType;

    /**
     * 变量值
     */
    @Column
    private String varValue;

}