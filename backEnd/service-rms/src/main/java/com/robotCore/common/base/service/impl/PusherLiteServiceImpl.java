package com.robotCore.common.base.service.impl;

import com.alibaba.fastjson.JSON;
import com.beans.feignconfg.RpcPostHystrix;
import com.robotCore.common.base.service.PusherLiteService;
import com.robotCore.common.base.vo.BusinessDataVO;
import com.robotCore.common.base.vo.PusherDataVO;
import com.robotCore.common.base.vo.RealTimeDataVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: pushLite数据推送接口实现
 * @Author: zhangqi
 * @Create: 2021/8/31 10:52
 */
@Service
@PropertySource(value = "application.yml")
public class PusherLiteServiceImpl implements PusherLiteService {
    private static final Logger logger = LoggerFactory.getLogger(PusherLiteServiceImpl.class);

    @Value("${asam.pusher.businessTopic}")
    private String businessTopic;

    @Value("${asam.pusher.dataTopic}")
    private String dataTopic;

    @Value("${asam.pusher.url}")
    private String url;

    @Value("${asam.pusher.api}")
    private String api;

    @Value("${asam.pusher.appId}")
    private String appId;

    @Value("${asam.pusher.appSecret}")
    private String appSecret;

    @Autowired
    private RpcPostHystrix rpcPostHystrix;

    @Override
    public String sendBusinessMessage(BusinessDataVO businessDataVO) {
        String body = "";
        body = sendMessage(businessTopic, JSON.toJSONString(businessDataVO));
        return body;
    }

    @Override
    public String sendRealTimeMessage(RealTimeDataVO realTimeDataVO) {
        String body = "";
        body = sendMessage(dataTopic, JSON.toJSONString(realTimeDataVO));
        return body;
    }

    /**
     * 推送设备实时信息给前端（按分组推送）
     */
    @Override
    public String sendRealTimeMessageByGroup(String pusherKey, RealTimeDataVO realTimeDataVO) {
        String body = "";
        body = sendMessageByGroup(dataTopic, pusherKey, realTimeDataVO);
        return body;
    }

    /**
     * @Description: 组装推送服务需要的数据包
     * @Author: Created by zhangqi on 2021/09/01
     */
    private String sendMessage(String pusherKey, String pusherData){
        String body = "";
        Map<String, String> params= new HashMap<>();
        params.put("pusherKey", pusherKey);
        params.put("pusherData", pusherData);
        params.put("needResult", "0");
        params.put("appid", appId);
        params.put("appsecret", appSecret);
        String sendDataStr = JSON.toJSONString(params);
        //由于数据推送过于频繁，此处禁止推送
//        logger.info("推送数据："+ sendDataStr);
        body = rpcPostHystrix.postRpcHttp(url, api, params, String.class);
        return body;
    }

    /**
     * @Description: 组装推送服务需要的数据包-分组推送（带通配符）
     * @param roomKey 推送消息分组
     * @param pusherKey 推送消息主键
     * @param pusherData 推送消息内容
     * @return String 字符串
     */
    private String sendMessageByGroup(String roomKey, String pusherKey, Object pusherData){
        String body = "";
        PusherDataVO pusherDataVO = new PusherDataVO();
        pusherDataVO.setAppid(appId);
        pusherDataVO.setAppsecret(appSecret);
        pusherDataVO.setNeedResult(0);
        pusherDataVO.setRoomKey(roomKey);
        pusherDataVO.setPusherKey(pusherKey);
        pusherDataVO.setPusherData(pusherData);
        String sendDataStr = JSON.toJSONString(pusherDataVO);

        logger.info("三维推送数据:{}", sendDataStr);
        body = rpcPostHystrix.postRpcHttp(url, api, pusherDataVO, String.class);
        return body;
    }

}
