package com.asam.system.entity;

import com.asam.common.base.model.BaseModelL;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;

/**
 * @Description 系统的基础数据维护
 * @Author: fyy
 * @Create: 2022-04-19
 */
@SuppressWarnings("serial")
@Data
@Table(name="sys_subsystem")
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class SysSubsystem extends BaseModelL {
    private static final long serialVersionUID = 100101L;

    /**
     * 系统名称
     */
    @Column(name="name",length = 50)
    public String name;

    /**
     * 应用编号
     */
    @Column(name="appid",length = 100)
    public String appid;

    /**
     * 密钥
     */
    @Column(name="appsecret",length = 100)
    public String appsecret;

    /**
     * 附件id
     */
    @Column(name="file_id",length = 100)
    public String fileId;

    /**
     * 跳转链接
     */
    @Column(name="system_url",length = 100)
    public String systemUrl;

    /**
     * 安全域名
     */
    @Column(name="domain",length = 50)
    public String domain;

    /**
     * 服务商
     */
    @Column(name="providername",length = 50)
    public String providername;

    /**
     * 是否可用 1可用 0不可用
     */
    @Column(name="enable",length = 1)
    public Integer enable;

    /**
     * 排序
     */
    @Column(name="sort")
    public Integer sort;
}