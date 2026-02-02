package com.robotCore.common.base.service;

import com.robotCore.common.base.vo.BusinessDataVO;
import com.robotCore.common.base.vo.RealTimeDataVO;

/**
 * @Description: pushLite数据推送接口
 * @Author: zhangqi
 * @Create: 2021/8/31 10:52
 */
public interface PusherLiteService {
    /**
     * 推送业务数据
     * @param businessDataVO 推送具体数据
     * @return String 字符串
     */
    String sendBusinessMessage(BusinessDataVO businessDataVO);

    /**
     * 推送实时数据
     * @param realTimeDataVO 推送具体数据
     * @return String 字符串
     */
    String sendRealTimeMessage(RealTimeDataVO realTimeDataVO);

    /**
     * 推送实时数据
     * @param pusherKey 推送消息主键
     * @param realTimeDataVO 推送具体数据
     * @return String 字符串
     */
    String sendRealTimeMessageByGroup(String pusherKey, RealTimeDataVO realTimeDataVO);

}
