package com.robotCore.multiScheduling.entity;

import java.util.Map;

/**
 * @Description: 3车间产线--数据上传返回值
 * @Author: Created by crystal on 2021/11/2.
 * @Modifier: Modify by crystal on 2021/11/2.
 */
public class ReturnObj {
    /**
     * 结果：Y-成功; N-失败
     */
    public String STATUS;

    /**
     * 异常信息
     */
    public String ERROR_MSG;

    /**
     * 数据
     */
    public Map<String, Object> data;
}
