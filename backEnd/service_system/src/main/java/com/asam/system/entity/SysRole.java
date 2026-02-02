package com.asam.system.entity;

import com.asam.common.base.model.BaseModelL;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @Description: 角色实体类
 * @Author: fyy
 * @Create: 2022-04-19
 */
@SuppressWarnings("serial")
@Data
@Table(name = "sys_role")
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class SysRole extends BaseModelL {
    /**
     * 角色描述,UI界面显示使用
     */
    @Column(name = "name",length = 50)
    public String name;

    /**
     * 角色级别   对应SysData内，sysData.sequence
     */
    @Column(name = "role_level")
    public Integer roleLevel = 0;

    /**
     * 角色编码
     */
    @Column(name="role_code",length = 50)
    public String roleCode;

    /**
     * 描述
     */
    @Column(name = "description")
    public String description;
}