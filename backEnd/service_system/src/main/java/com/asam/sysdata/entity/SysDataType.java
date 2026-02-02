package com.asam.sysdata.entity;

import com.asam.common.base.model.BaseModelL;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/***
 * @Description: 数据字典--类型
 * @Author: fyy
 * @Create: 2022-04-19
 */
@SuppressWarnings("serial")
@Data
@Table(name = "sys_data_type")
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class SysDataType extends BaseModelL {

    //类型编码
    @Column(name = "type_code")
    private String typeCode;

    //数据类型名称
    @Column(name = "type_name",length = 50)
    private String typeName;
}
