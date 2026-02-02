package com.robotCore.common.base.vo;

import lombok.Data;

/**
 * @Description: 数据推送-业务数据
 * @Author: zhangqi
 * @Create: 2021/9/1 14:42
 */
@Data
public class BusinessDataVO {

    /***
     * 功能码
     */
    private String code;

    /***
     * 信息
     */
    private String msg;

    /***
     * 成功状态
     */
    private boolean success  = true;

    /***
     * 数据
     */
    private Object data;

    /***
     * 用户ID
     */
    private Long uid;

}
