package com.asam.human.entity;

import com.asam.common.base.model.BaseModelL;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/***
 * @Description: 岗位实体
 * @Author: fyy
 * @Create: 2022-07-26
 */
@SuppressWarnings("serial")
@Table(name = "hum_post")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class HumPost extends BaseModelL {

    //名称
    @Column(name = "name", length = 50)
    private String name;

    //职责
    @Column(name = "duty", length = 50)
    private String duty;

    //任职条件
    @Column(name = "conditions")
    @TableField(insertStrategy = FieldStrategy.IGNORED)
    private String conditions;
}
