package com.asam.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.gitee.sunchenbin.mybatis.actable.annotation.IsAutoIncrement;
import com.gitee.sunchenbin.mybatis.actable.annotation.IsKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @Description: 账户角色关联
 * @Author: fyy
 * @Create: 2022-04-19
 */
@SuppressWarnings("serial")
@Data
@Table(name = "sys_account_role")
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class SysAccountRole implements Serializable {

    @TableId(type = IdType.AUTO)
    @IsKey
    @IsAutoIncrement             //自增
    @Column(name = "id")
    private Long id;

    /**
     * 账号ID
     */
    @Column(name = "sys_account_id",length = 11)
    public Long sysAccountId;

    /**
     * 角色ID
     */
    @Column(name = "sys_role_id",length = 11)
    public Long sysRoleId;
}
