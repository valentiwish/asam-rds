package com.robotCore.common.base.vo;

import lombok.Data;

import java.util.List;

@Data
public class UserVO {
    /**
     * 编号
     */
    private Long id;
    /***
     * 登录帐号
     */
    private String loginName;
    /**
     * 数据状态 0：无效；1：有效，即已经逻辑删除
     */
    private Integer state;
    /**
     * 姓名
     */
    private String userName;
    /**
     * 联系电话
     */
    private String userPhone;
    /**
     * 身份证号
     */
    private String cardId;
    /**
     * 部门id
     */
    private String orgId;
    /**
     * 部门编码
     */
    private String orgCode;
    /**
     * 部门名称
     */
    private String orgName;
    /**
     * 工号
     */
    private String jobNumber;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 所属班组ID
     */
    private String teamId;
    /**
     * 班组编码
     */
    private String teamCode;
    /**
     * 所属班组名称
     */
    private String teamName;
    /**
     * 是否班组长
     */
    private Integer monitorMark;
    /**
     * 人员唯一码
     */
    private String uniqueCode;
    /**
     * 人员角色
     */
    private List<UserRoleVo> userRoleVoList;
}
