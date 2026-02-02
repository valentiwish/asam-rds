package com.robotCore.common.base.service;

import java.util.Map;

/**
 * @Description:
 * @Author: zhangqi
 * @Create: 2021/11/17 15:07
 */
public interface RestApiService {

    /**
     * 向金陵AGV调度系统发送卷帘门已打开到（可通过）位指令
     */
    Map doorHasOpen(String data);

    /**
     * 向金陵AGV调度系统请求地图信息
     */
    Map getWorkingMap();
}
