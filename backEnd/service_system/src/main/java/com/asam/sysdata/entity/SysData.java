package com.asam.sysdata.entity;

import com.asam.common.base.model.BaseModelL;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/***
 * @Description: 数据字典--内容
 * @Author: fyy
 * @Create: 2022-04-19
 */
@SuppressWarnings("serial")
@Data
@Table(name = "sys_data")
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class SysData extends BaseModelL {

    //数据内容名称
    @Column(name = "text_name",length = 100)
    private String textName;

    //显示顺序
    @Column(name = "sequence",length = 2)
    private String sequence;

    //数据类型ID
    @Column(name = "data_type_id")
    private Long dataTypeId;

    //类型编码
    @Column(name = "data_type_code",length = 50)
    private String dataTypeCode;

    //数据类型名称
    @Column(name = "data_type_name",length =50)
    private String dataTypeName;
}
