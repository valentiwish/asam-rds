package com.robotCore.common.base.vo;

import lombok.Data;

/**
 * @Description: 组织机构
 * @Author: zhangqi
 * @Create: 2021/6/15 15:02
 */
@Data
public class OrganizationVO {
    /***
     * 部门ID
     */
    private String id;
    /**
     * 部门编码
     */
    private String code;
    /**
     * 部门名称
     */
    private String name;

    /**
     * 部门简称
     */
    private String shortName;

    /**
     * 上级部门ID
     */
    private String parentId;

    /**
     * 上级部门编码
     */
    private String parentCode;

    /**
     * 上级部门名称
     */
    private String parentName;
}
