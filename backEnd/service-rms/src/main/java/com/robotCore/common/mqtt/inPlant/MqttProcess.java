package com.robotCore.common.mqtt.inPlant;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beans.redis.RedisUtil;
import com.robotCore.common.mqtt.MqttGateway;
import com.robotCore.common.mqtt.inPlant.dto.*;
import com.robotCore.common.utils.ObjectUtil;
import com.utils.tools.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Lu Fan
 * @description
 * @date 2025/3/18 14:35
 */
@Component
@Slf4j
public class MqttProcess {

    @Autowired
    private MqttGateway mqttGateway;

    @Value("${asam.mqtt.inPlant_topic_subscribe}")
    private String topic;

    @Autowired
    private RedisUtil redisUtil;

    public void subscribe(String topic, String payload) throws ParseException {

        //todo  对消息进行处理
        switch (topic){
            //处理359九分厂烘箱
            case "HX":
                solveTopicHXMsg(payload);
                break;
            case "topic2":
                break;
            case "HISTORYTOPIC":
                solveHistoryMsg(payload);
                break;
            case "ALARMTOPIC":
                solveAlarmMsg(payload);
                break;
            //359三台重载
            case "readdata_S_KIO_Project":
                solve359ThreeHeavyRobotMsg(payload);
                break;
        }
    }

    public void publish(String topic, SendMessage sendMessage) {
        log.info("主题{}，发布消息{}，开始",topic,sendMessage);
        mqttGateway.sendToMqtt(topic, JSONObject.toJSONString(sendMessage));
//        String sendMsgJson = JSON.toJSONString(sendMessage);
//        mqttGateway.sendToMqtt(topic,qos,sendMsgJson);
        log.info("主题{}，发布消息{}，成功",topic,sendMessage);
        log.info("主题{}，发布消息JSON{}，成功",topic,JSONObject.toJSONString(sendMessage));
//        //查询历史数据
//        HistoryDataRequest historyDataRequest = new HistoryDataRequest();
//        historyDataRequest.setTopic("HISTORYTOPIC");
//        historyDataRequest.setCount(1);
//        mqttGateway.sendToMqtt(topic,historyDataRequest);
    }

    public void queryHistory(String topic,SendMessage sendMessage) {
        log.info("主题{}，发布消息{}，开始",topic,sendMessage);
        //查询历史数据
        HistoryDataRequest historyDataRequest = new HistoryDataRequest();
        historyDataRequest.setTopic("HISTORYTOPIC");
        historyDataRequest.setCount(1);
        mqttGateway.sendToMqtt(topic,historyDataRequest);
        log.info("主题{}，发布消息{}，成功",topic,sendMessage);
    }

    public void subAlarm(String topic) {
        SubAlarmMessage subAlarmMessage = new SubAlarmMessage();
        subAlarmMessage.setTopic(topic);
        subAlarmMessage.setState(0);
        log.info("主题{}，发布消息{}，开始",topic,subAlarmMessage);
        mqttGateway.sendToMqtt("SupconScadaRealAlarm",subAlarmMessage);
        log.info("主题{}，发布消息{}，成功",topic,subAlarmMessage);
    }

    public void solveTopicHXMsg(String payload) {
        ReceiveMessage scadaMessage = JSON.parseObject(payload, ReceiveMessage.class);
        List<RTValue> list = scadaMessage.getRTValue();
        Map<String, Integer> map;
        if (redisUtil.stringWithGet("topic_HX_msg") == null) {
            map = new HashMap<>();
        } else {
            map = (Map<String, Integer>)redisUtil.stringWithGet("topic_HX_msg");
        }
        for (RTValue rtValue: list) {
            //todo 业务处理
            map.put(rtValue.getName(), rtValue.getValue());
        }
        redisUtil.stringWithSet("topic_HX_msg", map);
    }

    public void solveHistoryMsg(String payload) {
        HistoryDataResponse historyDataResponse = JSON.parseObject(payload, HistoryDataResponse.class);
        log.info("接收历史数据{}", historyDataResponse);
        //todo 业务处理
    }

    public void solveAlarmMsg(String payload) {
        RealAlarmData realAlarmData = JSON.parseObject(payload, RealAlarmData.class);
        log.info("接收报警数据{}", realAlarmData);
        //todo 业务处理
    }

    /**
     * 处理359三台重载喷涂项目自动门数据
     * @param payload
     */
    public void solve359ThreeHeavyRobotMsg(String payload) {
        //todo 业务处理
        if (ObjectUtil.isNotEmpty(payload)) {
            Map<String, Object> map = JsonUtils.toMap(payload);
            JSONArray objsjson = new JSONArray();

            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if ("Objs".equals(entry.getKey())) {
                    objsjson = JSONArray.parseArray(entry.getValue().toString());
                }
            }
            if (ObjectUtil.isNotEmpty(objsjson)) {
                for (int i = 0; i < objsjson.size(); i++) {
                    JSONObject object = JSONObject.parseObject(objsjson.getString(i));
                    if (object.containsKey("N") && object.containsKey("1")) {
//                        log.info("接收MQTT数据name{},value{}", object.getString("N"), object.getString("1"));
                        String varName = object.getString("N");
                        String varValue = object.getString("1");
                        if (varName.contains("DOOR")) {
                            redisUtil.stringWithSet(varName, varValue);
                        }
                    }
                }
            }
        }
    }
}
