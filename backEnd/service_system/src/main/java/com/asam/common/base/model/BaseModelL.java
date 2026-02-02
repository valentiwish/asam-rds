package com.asam.common.base.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.IsAutoIncrement;
import com.gitee.sunchenbin.mybatis.actable.annotation.IsKey;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description：基类，主键自增
 * @Author: fyy
 * @Create: 2022-04-19
 */

@Data
@Accessors(chain = true) // 链表的访问
public class BaseModelL implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @TableId(type = IdType.AUTO) //mybatis-plus主键注解
    @IsKey                         //actable主键注解
    @IsAutoIncrement             //自增
    @Column(name = "id")
    private Long id;


    /**
     * 创建人id
     */
    @Column(name = "create_id")
    private Long createId;

    /**
     * 修改人id
     */
    @Column(name = "update_id")
    private Long updateId;

    /**
     * 创建人
     */
    @Column(name = "create_user",length = 50)
    private String createUser;

    /**
     * 修改人
     */
    @Column(name = "update_user",length = 50)
    private String updateUser;

    /**
     * 创建时间
     */
    //@TableField(fill = FieldFill.INSERT)
    @Column(name = "create_time",type = MySqlTypeConstant.DATETIME)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 修改时间
     */
    @Column(name = "update_time",type = MySqlTypeConstant.DATETIME)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 状态   1:有效    0：无效
     */
    @Column(name = "state",length = 2)
    private Integer state;

    /**
     * 备注
     */
    @Column(name = "remark",length = 500)
    private String remark;

    /**
     * 密级
     */
    @Column(name = "secret_level_no",length = 2)
    private Integer secretLevelNo;
}
