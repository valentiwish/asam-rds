package com.robotCore.common.base.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.IsAutoIncrement;
import com.gitee.sunchenbin.mybatis.actable.annotation.IsKey;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @Description 基类,自增整形为主键
 * @Author zhangqi
 * @Date 2021/12/17 8:29
 */
@Data
public class BaseModeAuto implements Serializable {
    /**
     * 主键 TableId中可以决定主键的类型,不写会采取默认值,默认值可以在yml中配置 AUTO: 数据库ID自增 INPUT: 用户输入ID
     * ID_WORKER: 全局唯一ID，Long类型的主键 ID_WORKER_STR: 字符串全局唯一ID UUID: 全局唯一ID，UUID类型的主键
     * NONE: 该类型为未设置主键类型
     */
    @TableId(type = IdType.AUTO) // mybatis-plus主键注解
    @IsKey // actable主键注解
    @IsAutoIncrement // 自增
	@Column // 对应数据库字段，不配置name会直接采用属性名作为字段名
    public Long id;

    /**
     * 创建人id
     */
    @Column(name = "create_id",length = 11)
    private Long createId;

    /**
     * 修改人id
     */
    @Column(name = "update_id",length = 11)
    private Long updateId;

    /**
     * 创建人
     */
    @Column(name = "create_user",length = 100)
    private String createUser;

    /**
     * 修改人
     */
    @Column(name = "update_user",length = 100)
    private String updateUser;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @Column(type = MySqlTypeConstant.DATETIME)
    private Timestamp createTime;

    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.UPDATE)
    @Column(type = MySqlTypeConstant.DATETIME)
    private Timestamp updateTime;

    /**
     * 备注
     */
    @Column(name = "remark",length = 500)
    private String remark;

    /**
     * 密级
     */
    @Column(name = "secret_level_no")
    private Integer secretLevelNo;
}
