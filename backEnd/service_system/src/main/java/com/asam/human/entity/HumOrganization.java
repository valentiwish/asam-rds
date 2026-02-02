package com.asam.human.entity;


import com.asam.common.base.model.BaseModelU;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;


/**
 * @Description: 组织机构表
 * @Author: fyy
 * @Create: 2022-04-19
 */

@SuppressWarnings("serial")
@Data
@Table(name = "hum_organization")
@AllArgsConstructor // 全参数构造函数
@NoArgsConstructor // 空参数的构造函数
@Accessors(chain = true) // 链表的访问
public class HumOrganization extends BaseModelU {

    /**
     * 部门编码
     */
    @Column(name = "code",length = 50)
    private String code;


    /**
     * 部门名称
     */
    @Column(name = "name",length = 100)
    private String name;

    /**
     * 部门简称
     */
    @Column(name = "short_name",length = 50)
    private String shortName;

    /**
     * 是否公司 1:是 0：不是
     */
    @Column(name = "is_company",length = 1)
    private Integer isCompany;

    /**
     * 上级部门id
     */
    @Column(name = "parent_id",length = 50)
    private String parentId;

    /**
     * 上级部门编码
     */
    @Column(name = "parent_code",length = 50)
    private String parentCode;

    /**
     * 上级部门名称
     */
    @Column(name = "parent_name",length = 50)
    private String parentName;

    /**
     * 职能描述
     */
    @Column(name = "responsibility")
    private String responsibility;

    /**
     * 电话
     */
    @Column(name = "telephone",length = 50)
    private String telephone;

    /**
     * 联系人
     */
    @Column(name = "linker",length = 50)
    private String linker;

    /**
     * 地址
     */
    @Column(name = "address")
    private String address;

}
