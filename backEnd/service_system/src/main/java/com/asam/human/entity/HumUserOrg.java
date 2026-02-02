package com.asam.human.entity;

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
 * @Description: 数据权限  人员找部门数据信息
 * @Author: fyy
 * @Create: 2022-04-19
 */
@SuppressWarnings("serial")
@Data
@Table(name = "hum_user_org")
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class HumUserOrg implements Serializable {

    @TableId(type = IdType.AUTO)
    @IsKey
    @IsAutoIncrement             //自增
    @Column(name = "id")
    private Long id;

    /**
     * 人员ID
     */
    @Column(name = "sys_user_id")
    public Long sysUserId;

    /**
     * 部门ID
     */
    @Column(name = "sys_org_id",length = 50)
    public String sysOrgId;
}
