package com.robotCore.common.base.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.beans.redis.RedisUtil;
import com.robotCore.common.base.service.AsynTaskService;
import com.robotCore.common.base.service.PusherLiteService;
import com.robotCore.common.base.service.RestApiService;
import com.robotCore.common.mqtt.MqttGateway;
import com.robotCore.feign.IServiceMes;
import com.robotCore.multiScheduling.constant.InterfaceConstant;
import com.robotCore.multiScheduling.entity.ExtInterfaceLog;
import com.robotCore.multiScheduling.service.ExtInterfaceLogService;
import com.robotCore.multiScheduling.vo.DoorInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Description: 异步任务执行方法
 * @Author: zhangqi
 * @Create: 2023/4/7 8:30
 */
@Service
@Slf4j
public class AsynTaskServiceImpl implements AsynTaskService {

    @Autowired
    PusherLiteService pusherLiteService;

    @Autowired
    private IServiceMes iServiceMes;

    @Autowired
    private RedisUtil redisUtil;

    @Resource
    private MqttGateway mqttGateway;

    @Autowired
    private ExtInterfaceLogService extInterfaceLogService;

    @Autowired
    private RestApiService restApiService;

    @Value("${asam.mqtt.setTopic}")
    private String setSubTopic;

    @Value("${asam.mqtt.client.outId}")
    private String clientOutId;

    @Value("${asam.mqtt.client.inId}")
    private String clientInId;

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 异步任务：下发卷帘门开门指令
     */
    @Override
    @Async("asyncServiceExecutor")
    public void openDoorAsync(String doorId) {
        log.info("进入异步开门任务时间", new Date().toString());
        //根据卷帘门Id获取开门变量
        if (redisUtil.isHasKey("door@" + doorId)) {
            Object varInfoObj = redisUtil.stringWithGet("door@" + doorId);
            if(ObjectUtils.isNotEmpty(varInfoObj)) {
                DoorInfoVO doorInfoVo = JSON.parseObject(varInfoObj.toString(), DoorInfoVO.class);
                if(ObjectUtils.isNotEmpty(doorInfoVo.getDoorParam())) {
                    //更新缓存中卷帘门的请求状态
                    doorInfoVo.setAgvRequest(1);
                    redisUtil.stringWithSet("door@" + doorId, JSON.toJSONString(doorInfoVo, SerializerFeature.WriteMapNullValue));
                    String sendData = commandSendPackage(doorInfoVo.getDoorParam(), 1, 0);
                    ExtInterfaceLog extInterfaceLog = new ExtInterfaceLog();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddhhmmssSSS");
                    String dateStr = formatter.format(new Date());
                    extInterfaceLog.setLogId("开卷帘门_" + dateStr);
                    extInterfaceLog.setContent(sendData);
                    extInterfaceLog.setCreateTime(new Date());
                    extInterfaceLog.setFromSys(InterfaceConstant.APP_SYS_MOM);
                    extInterfaceLog.setToSys(InterfaceConstant.APP_SYS_MQTT_SCADA);
                    log.info("开卷帘门{}指令发送,数据：{}", doorId, sendData);
                    mqttGateway.sendToMqtt(setSubTopic, sendData);
                    extInterfaceLogService.save(extInterfaceLog);
                }else{
                    log.error("缓存中卷帘门{}的控制变量为空，开卷帘门指令发送失败", doorId);
                }
            }else{
                log.error("缓存中卷帘门{}的数据异常，开卷帘门指令发送失败", doorId);
            }
        }else{
            log.error("缓存中没有卷帘门{}的信息，开卷帘门指令发送失败", doorId);
        }
    }

    /**
     * 异步任务：下发卷帘门关门指令
     */
    @Override
    @Async("asyncServiceExecutor")
    public void closeDoorAsync(String doorId) {
        //根据卷帘门Id获取关门变量
        if (redisUtil.isHasKey("door@" + doorId)) {
            Object varInfoObj = redisUtil.stringWithGet("door@" + doorId);
            if(ObjectUtils.isNotEmpty(varInfoObj)) {
                DoorInfoVO doorInfoVo = JSON.parseObject(varInfoObj.toString(), DoorInfoVO.class);
                if (ObjectUtils.isNotEmpty(doorInfoVo.getDoorParam())) {
                    //更新缓存中卷帘门的请求状态
                    doorInfoVo.setAgvRequest(2);
                    redisUtil.stringWithSet("door@" + doorId, JSON.toJSONString(doorInfoVo, SerializerFeature.WriteMapNullValue));
                    String sendData = commandSendPackage(doorInfoVo.getDoorParam(), 2, 0);
                    ExtInterfaceLog extInterfaceLog = new ExtInterfaceLog();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddhhmmssSSS");
                    String dateStr = formatter.format(new Date());
                    extInterfaceLog.setLogId("开卷帘门_" + dateStr);
                    extInterfaceLog.setContent(sendData);
                    extInterfaceLog.setCreateTime(new Date());
                    extInterfaceLog.setFromSys(InterfaceConstant.APP_SYS_MOM);
                    extInterfaceLog.setToSys(InterfaceConstant.APP_SYS_MQTT_SCADA);
                    log.info("关卷帘门{}指令发送,数据：{}", doorId, sendData);
                    mqttGateway.sendToMqtt(setSubTopic, sendData);
                    extInterfaceLogService.save(extInterfaceLog);
                }else{
                    log.error("缓存中卷帘门{}的控制变量为空，关卷帘门指令发送失败", doorId);
                }
            }else{
                log.error("缓存中卷帘门{}的数据异常，关卷帘门指令发送失败", doorId);
            }
        }else{
            log.error("缓存中没有卷帘门{}的信息，关卷帘门指令发送失败", doorId);
        }
    }


    /**
     * IOServer下发控制指令封装，参数：变量名，设置值，下发次数
     * @param variable
     * @param value
     * @param sendNum
     * @return
     */
    public String commandSendPackage(String variable, Object value, Integer sendNum){
        Long qid= System.nanoTime()%1000000000;
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
        //下发命令按变量名，Qid保存，若下发不成功，可重新下发
        redisUtil.stringWithSet("varSet@" + qid, variable + "@" + value + "@" + sendNum, 600);
        return JSONObject.toJSONString(map);
    }

    /**
     * 异步任务: 给IOServer重复发送控制指令
     */
    @Override
    @Async("asyncServiceExecutor")
    public void reSendControlAsync(String variableName, Object value, Integer sendNum) {
        try{
            Thread.sleep(2000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        String sendData = commandSendPackage(variableName, value, sendNum);
        ExtInterfaceLog extInterfaceLog = new ExtInterfaceLog();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddhhmmssSSS");
        String dateStr = formatter.format(new Date());
        extInterfaceLog.setLogId("控制指令重发_" + dateStr);
        extInterfaceLog.setContent(sendData);
        extInterfaceLog.setCreateTime(new Date());
        extInterfaceLog.setFromSys(InterfaceConstant.APP_SYS_MOM);
        extInterfaceLog.setToSys(InterfaceConstant.APP_SYS_MQTT_SCADA);
        log.info("第{}次给KIOServer重发的下发控制指令数据：{}", sendNum + 1, sendData);
        mqttGateway.sendToMqtt(setSubTopic, sendData);
        extInterfaceLogService.save(extInterfaceLog);
    }

    /**
     * 异步任务：IOServer控制指令发送
     */
    @Override
//    @Async("asyncServiceExecutor")
    public void setControlAsync(String variable, Object value) {
        String sendData = commandSendPackage(variable, value, 0);
        ExtInterfaceLog extInterfaceLog = new ExtInterfaceLog();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddhhmmssSSS");
        String dateStr = formatter.format(new Date());
        extInterfaceLog.setLogId("控制指令发送_" + dateStr);
        extInterfaceLog.setContent(sendData);
        extInterfaceLog.setCreateTime(new Date());
        extInterfaceLog.setFromSys(InterfaceConstant.APP_SYS_MOM);
        extInterfaceLog.setToSys(InterfaceConstant.APP_SYS_MQTT_SCADA);
        log.info("给KIOServer下设请求指令：{}", sendData);
        mqttGateway.sendToMqtt(setSubTopic, sendData);
        extInterfaceLogService.save(extInterfaceLog);
    }

    /**
     * 异步任务: 业务系统更新自动卷帘门的状态
     */
    @Override
    @Async("asyncServiceExecutor")
    public void updateDoorStatus(String toJSONString) {
        iServiceMes.updateDoorStatus(toJSONString);
    }

}
