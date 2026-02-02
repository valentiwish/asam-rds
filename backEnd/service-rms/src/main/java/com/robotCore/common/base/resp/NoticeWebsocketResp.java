package com.robotCore.common.base.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @Des:
 * @author: zxl
 * @date: 2023/5/24
 **/
@Data
@ApiModel("ws通知返回对象")
public class NoticeWebsocketResp<T> {

    @ApiModelProperty(value = "通知类型")
    private String id;

    @ApiModelProperty(value = "通知内容")
    private T text;

}
