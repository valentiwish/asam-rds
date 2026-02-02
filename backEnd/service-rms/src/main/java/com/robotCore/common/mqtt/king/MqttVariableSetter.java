package com.robotCore.common.mqtt.king;

import com.alibaba.fastjson.JSONObject;
import com.robotCore.common.mqtt.MqttGateway;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author likundong
 * @create 2024-06-12
 */

@Slf4j
@Data
public class MqttVariableSetter extends Thread  {
    private final ScheduledExecutorService executor;
    private MqttGateway mqttGateway;
    private volatile boolean successReceived = false;
    private  String setTopic;
    private  String variable;
    private  Long qid;
    private  Object value;
    private  String clientOutId;
    private AtomicInteger retryCount = new AtomicInteger(0);
    private final int maxRetries;
    private final int retryInterval;
    public MqttVariableSetter(MqttGateway mqttGateway, ScheduledExecutorService executor,
                              String setTopic, String variable, Object value, String clientOutId, int maxRetries, Long qid, int retryInterval) {
        this.mqttGateway = mqttGateway;
        this.executor = executor;
        this.setTopic = setTopic;
        this.variable = variable;
        this.value = value;
        this.clientOutId=clientOutId;
        this.maxRetries=maxRetries;
        this.qid=qid;
        this.retryInterval=retryInterval;
    }


    @Override
    public void run() {
        try {
            while (!(retryCount.get()+1 > maxRetries || successReceived)) {
                if(retryCount.get()>0){
                    long time=retryInterval;
                    Thread.sleep(time);
                }
                if(!successReceived){
                    mqttGateway.sendToMqtt(setTopic, formatValue(variable, value));
                    log.info("MQTT下发变量:{}, 值:{} ",variable ,value);
                    retryCount.incrementAndGet(); // 原子性递增
                }else break;
            }
            if((retryCount.get()+1 > maxRetries)&&!successReceived){
                log.info("MQTT下发变量:{}, 值:{},重试次数：{}，已经超过最大阈值：{} ,下发失败",variable ,value,retryCount.get()+1,maxRetries);
            }
            if (onStopCallback != null) {
                onStopCallback.onStop(this);
            }
        } catch (Exception e) {
            System.err.println("MQTT消息发送失败: " + e.getMessage());
        }
    }

    public void onSuccessReceived() {
        this.successReceived = true;
    }


    @FunctionalInterface
    public interface OnStopCallback {//回调逻辑
        void onStop(MqttVariableSetter setter);
    }

    private OnStopCallback onStopCallback;

    public void setOnStopCallback(OnStopCallback onStopCallback) {
        this.onStopCallback = onStopCallback;
    }

    String formatValue(String variable,Object value){
        Map<String,Object> map=new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss.SSS+0800");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        // 获取当前时间并格式化为UTC时间字符串
        Date now = new Date();
        String utcTime = sdf.format(now);
        map.put("Writer",clientOutId);
        map.put("WriteTime",utcTime);
        map.put("Qid",qid);
        map.put("Username","Admin");
        map.put("Password","e10adc3949ba59abbe56e057f20f883e");
        Map<String,Object> pnsmap1=new HashMap<>();
        pnsmap1.put("1","V");
        pnsmap1.put("2","T");
        pnsmap1.put("3","Q");
        map.put("PNs",pnsmap1);
        Map<String,Object> pvsmap1=new HashMap<>();
        pvsmap1.put("1",0);
        pvsmap1.put("2",utcTime);
        pvsmap1.put("3",192);
        map.put("PVs",pvsmap1);
        List<Map<String,Object>> maps=new ArrayList<>();
        Map<String,Object> Objs=new HashMap<>();
        Objs.put("N",variable);
        Objs.put("1",value);
        Objs.put("2",now.getTime());
        Objs.put("3",192);
        maps.add(Objs);
        map.put("Objs",maps);
        String jsvalue= JSONObject.toJSONString(map);
        return jsvalue;
    }
}
