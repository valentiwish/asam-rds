package com.robotCore.common.base.vo;

import lombok.Data;

/**
 * @Description: 数据推送-实时数据
 * @Author: zhangqi
 * @Create: 2021/9/1 14:42
 */
@Data
public class RealTimeDataVO {
    /***
     * 功能码
     */
    public String code;

    /***
     * 信息
     */
    public String msg;

    /***
     * 数据
     */
    public Object data;

    /***
     * 客户端ID（设备、终端等的识别码）
     */
    public String clientId;
}
