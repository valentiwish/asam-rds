package com.asam.human.entity;

import lombok.Data;

/**
 * @Description: 角色映射类
 * @Author: zhangqi
 * @Create: 2021/10/16 18:36
 */
@Data
public class UserRoleVo {
    /**
     * 角色描述,UI界面显示使用
     */
    private String name;

    /**
     * 角色级别   对应SysData内，sysData.sequence
     */
    private Integer roleLevel = 0;

    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 描述
     */
    private String description;
}
