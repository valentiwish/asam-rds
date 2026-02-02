package com.robotCore.common.base.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.beans.feignconfg.RpcPostHystrix;
import com.beans.redis.RedisUtil;
import com.robotCore.common.base.service.RestApiService;
import com.robotCore.common.constant.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @Description:
 * @Author: zhangqi
 * @Create: 2021/11/17 15:08
 */
@Service
@PropertySource(value = "application.yml")
@Slf4j
public class RestApiServiceImpl implements RestApiService {

    @Autowired
    private RpcPostHystrix rpcPostHystrix;

    @Value("${service.agv.url}")
    private String jlAGVApiUrl;

    @Override
    public Map doorHasOpen(String data) {
        Map<String, Object> returnMap = new HashMap<>();
        log.info("卷帘门已开到位接口数据："+ data);
        Map params = JSON.parseObject(data);
        String returnMsg = "";
        String content = data;
        String returnObj = "";
        Integer status = 0;
        try {
            String body = rpcPostHystrix.postRpcHttp(jlAGVApiUrl, "door/doorHasOpen", params, String.class);
            log.info("卷帘门已开到位接口返回数据：" + body);
            if (Objects.nonNull(body)) {
                returnObj = body;
                JSONObject jsonObject = JSON.parseObject(body);
                if (jsonObject.containsKey("meta")) {
                    String metaStr = jsonObject.getString("meta");
                    JSONObject metaObject = JSON.parseObject(metaStr);
                    int code = Integer.parseInt(metaObject.getString("code"));
                    if (Constant.SUCCESS == code) {
                        log.info("卷帘门已开到位接口调用成功");
                    } else {
                        log.error("卷帘门已开到位接口调用失败：" + jsonObject.getString("message"));
                        //错误信息反馈

                    }
                    returnMsg = jsonObject.getString("message");
                    status = code;
                }
            } else {
                log.error("卷帘门已开到位接口返回为空");
                returnMsg = "任务取消接口返回为空";
                status = Constant.STATE_INVALID;
            }
        }catch (Exception ex){
            log.error("卷帘门已开到位接口连接错误");
            returnMsg = ex.getMessage();
            status = Constant.STATE_INVALID;
        }
        returnMap.put("code", status);
        returnMap.put("msg", returnMsg);
        return returnMap;
    }

    @Override
    public Map getWorkingMap() {
        Map<String, Object> returnMap = new HashMap<>();
        log.info("获取地图信息接口请求数据："+ "");
        String returnMsg = "";
        String content = "";
        String returnObj = "";
        Integer status = 0;
        String dataStr = "";
        try {
            String body = rpcPostHystrix.postRpcHttp(jlAGVApiUrl, "map/getWorkingMap", "", String.class);
            log.info("获取地图信息接口返回数据：" + body);
            if (Objects.nonNull(body)) {
                returnObj = body;
                JSONObject jsonObject = JSON.parseObject(body);
                if (jsonObject.containsKey("meta") && jsonObject.containsKey("data")) {
                    String metaStr = jsonObject.getString("meta");
                    dataStr = jsonObject.getString("data");
                    JSONObject metaObject = JSON.parseObject(metaStr);
                    int code = Integer.parseInt(metaObject.getString("code"));
                    if (Constant.SUCCESS == code) {
                        log.info("获取地图信息接口调用成功");
                    } else {
                        log.error("获取地图信息接口调用失败：" + jsonObject.getString("message"));
                    }
                    returnMsg = jsonObject.getString("message");
                    status = code;
                }
            } else {
                log.error("获取地图信息接口返回为空");
                returnMsg = "获取地图信息接口返回为空";
                status = Constant.STATE_INVALID;
            }
        }catch (Exception ex){
            log.error("获取地图信息接口连接错误");
            returnMsg = ex.getMessage();
            status = Constant.STATE_INVALID;
        }
        returnMap.put("code", status);
        if(ObjectUtils.isNotEmpty(dataStr)) {
            returnMap.put("data", dataStr);
        }
        returnMap.put("msg", returnMsg);
        return returnMap;
    }
}
