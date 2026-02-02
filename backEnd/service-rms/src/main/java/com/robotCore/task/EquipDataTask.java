package com.robotCore.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.beans.redis.RedisUtil;
import com.robotCore.common.base.service.AsynTaskService;
import com.robotCore.common.base.service.PusherLiteService;
import com.robotCore.common.base.service.RestApiService;
import com.robotCore.common.base.vo.RealTimeDataVO;
import com.robotCore.common.config.RequestQueue;
import com.robotCore.common.utils.ObjectUtil;
import com.robotCore.multiScheduling.entity.DataInfo;
import com.robotCore.multiScheduling.entity.PointVariable;
import com.robotCore.multiScheduling.vo.DoorInfoVO;
import com.utils.tools.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

/***
 * @Description: 处理Mqtt设备数据的任务
 * @Author: zhangqi
 * @Create: 2024/10/16
 */
@Component
@Slf4j
public class EquipDataTask extends Thread{

    @Autowired
    private RequestQueue requestQueue;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    PusherLiteService pusherLiteService;

    @Autowired
    private AsynTaskService asynTaskService;

    @Autowired
    private RestApiService restApiService;


    private volatile boolean running = true;

    private String regex = "[+-]?\\d+\\.\\d+";

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);
    // 存储条件与任务的映射（线程安全）
    private final ConcurrentHashMap<String, ScheduledFuture<?>> taskMap = new ConcurrentHashMap<>();

    @Override
    public void run(){
        log.info("线程启动，初始状态: " + running);
        while (running) {
            try {
                //从队列中取设备数据进行处理
                String recevieStr = requestQueue.getEquipQueue().take();
                if (ObjectUtil.isNotEmpty(recevieStr)) {
                    log.info("取队首数据，设备数据队列剩余：{}", requestQueue.getEquipQueue().size());
                    Map<String, Object> map = JsonUtils.toMap(recevieStr);
                    long startTime = System.currentTimeMillis();
                    JSONObject pvsjson = new JSONObject();
                    JSONArray objsjson = new JSONArray();

                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        if ("PVs".equals(entry.getKey())) {
                            pvsjson = JSONObject.parseObject(entry.getValue().toString());
                        }
                        if ("Objs".equals(entry.getKey())) {
                            objsjson = JSONArray.parseArray(entry.getValue().toString());
                        }
                    }
                    String time = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                    log.info("处理此批{}的objsjson为：{},一共有{}个", time,JSON.toJSONString(objsjson),objsjson.size());
                    if (ObjectUtil.isNotEmpty(pvsjson) && ObjectUtil.isNotEmpty(objsjson)) {
                        List<DataInfo> dataInfoList = new ArrayList<DataInfo>();
                        long startTime2 = System.currentTimeMillis();
                        for (int i = 0; i < objsjson.size(); i++) {
                            JSONObject object = JSONObject.parseObject(objsjson.getString(i));
                            if(i == objsjson.size()-1)   log.info("处理的object为：{},是{}批的第最后一个", JSON.toJSONString(object),time);
                            log.info("处理的object为：{},是{}批的第{}个", JSON.toJSONString(object),time,i+1);
                            if (object.containsKey("N")) {
                                String varName = object.getString("N");
                                DataInfo dataInfo = new DataInfo();
                                Boolean changeFlag = false;
                                Boolean hasVarFlag = redisUtil.isHasKey("var@" + varName);
                                log.info("{}批的变量长名{}是否存在于redis：{}", time,varName, hasVarFlag);
                                //查询接收到的变量长名是否存在于redis
                                if (hasVarFlag) {
                                    //查询变量的信息
                                    Object varInfoObj = redisUtil.stringWithGet("var@" + varName);
                                    log.info("{}批的量长名{}在redis中的变量：{}", time,varName,JSON.toJSONString(varInfoObj));
                                    if (ObjectUtils.isNotEmpty(varInfoObj)) {
                                        try {
                                            String jsonStr1 = varInfoObj.toString();
                                            log.info("准备解析 PointVariable，变量名: {}, JSON内容: {},是{}批的第{}个", varName, jsonStr1,time,i+1);
                                            PointVariable mypoint = JSON.parseObject(jsonStr1, PointVariable.class);
                                            log.info("成功解析 PointVariable，变量名: {}, 解析结果: {}，是{}批的第{}个", varName, JSON.toJSONString(mypoint),time,i+1);
                                            if (ObjectUtils.isNotEmpty(mypoint.getVariableName())) {
                                                dataInfo.setHostname(mypoint.getVariableName());
                                                mypoint.setSingal(1);
                                                if (!object.containsKey("1") && pvsjson.containsKey("1")) {
                                                    if (Integer.parseInt(pvsjson.get("3").toString()) > 0) {//变量质量戳>0
                                                        if (pvsjson.get("1") instanceof Boolean) {
                                                            boolean b = ((Boolean) pvsjson.get("1")).booleanValue();
                                                            if (b) {
                                                                changeFlag = judgeChangeValue(mypoint, "1");
                                                                mypoint.setValue("1");
                                                            } else {
                                                                changeFlag = judgeChangeValue(mypoint, "0");
                                                                mypoint.setValue("0");
                                                            }
                                                            mypoint.setDataType(1);
                                                            mypoint.setUpdateAt(pvsjson.get("2").toString());
                                                        } else {
                                                            changeFlag = judgeChangeValue(mypoint, pvsjson.get("1").toString());
                                                            //变量特殊处理，逻辑判断和控制指令下发
                                                            eqtDataHandle(mypoint.getVariableName(), pvsjson.get("1").toString());
                                                            mypoint.setValue(pvsjson.get("1").toString());
                                                            mypoint.setDataType(2);
                                                            mypoint.setUpdateAt(pvsjson.get("2").toString());
                                                            //参数值判断，处理（保留两位小数，固化压力、温度处理），推送
                                                            mypoint = equipDataHandle(mypoint);
                                                            if (redisUtil.isHasKey("varDyn@VariableNameList")) {
                                                                Object variableName = redisUtil.stringWithGet("varDyn@VariableNameList");
                                                                String jsonStr = (String) variableName;
                                                                List<String> list = JSON.parseArray(jsonStr, String.class);
                                                                if(list.contains(mypoint.getVariableName())){
                                                                    redisUtil.stringWithSet("var@" + mypoint.getVariableName(), JSON.toJSONString(mypoint, SerializerFeature.WriteMapNullValue));
                                                                }
                                                            }
                                                        }
                                                        //influxdb数据添加到集合
                                                        dataInfo.setSingal(mypoint.getSingal());
                                                        dataInfo.setValue(Double.valueOf(mypoint.getValue()));
                                                        dataInfo.setType(mypoint.getDataType());
                                                        dataInfo.setCtime(mypoint.getUpdateAt());
                                                        dataInfoList.add(dataInfo);
                                                        //判断是否超过正常范围，需要推送报警
                                                        isAlarm(mypoint);
                                                        //数据推送前端
                                                        equipDataPush(mypoint, changeFlag);
                                                    } else {
                                                        mypoint.setSingal(0);
                                                    }
                                                } else if (object.containsKey("1")) {
                                                    if (Integer.parseInt(object.get("3").toString()) > 0) {//变量质量戳>0
                                                        if (object.get("1") instanceof Boolean) {
                                                            boolean b = ((Boolean) object.get("1")).booleanValue();
                                                            if (b) {
                                                                changeFlag = judgeChangeValue(mypoint, "1");
                                                                mypoint.setValue("1");
                                                            } else {
                                                                changeFlag = judgeChangeValue(mypoint, "0");
                                                                mypoint.setValue("0");
                                                            }
                                                            mypoint.setDataType(1);
                                                            mypoint.setUpdateAt(object.get("2").toString());
                                                        } else {
                                                            //判断新上传的值与上次值对比，是否改变
                                                            changeFlag = judgeChangeValue(mypoint, object.get("1").toString());
                                                            //变量特殊处理，逻辑判断和控制指令下发
                                                            eqtDataHandle(mypoint.getVariableName(), object.get("1").toString());
                                                            mypoint.setValue(object.get("1").toString());
                                                            mypoint.setDataType(2);
                                                            mypoint.setUpdateAt(object.get("2").toString());
                                                            //参数值判断，处理（保留两位小数，固化压力、温度处理），推送
                                                            mypoint = equipDataHandle(mypoint);
                                                            if (redisUtil.isHasKey("varDyn@VariableNameList")) {
                                                                Object variableName = redisUtil.stringWithGet("varDyn@VariableNameList");
                                                                String jsonStr = (String) variableName;
                                                                List<String> list = JSON.parseArray(jsonStr, String.class);
                                                                if(list.contains(mypoint.getVariableName())){
                                                                    redisUtil.stringWithSet("var@" + mypoint.getVariableName(), JSON.toJSONString(mypoint, SerializerFeature.WriteMapNullValue));
                                                                }
                                                            }
                                                        }
                                                        //influxdb数据添加到集合
                                                        dataInfo.setSingal(mypoint.getSingal());
                                                        dataInfo.setValue(Double.valueOf(mypoint.getValue()));
                                                        dataInfo.setType(mypoint.getDataType());
                                                        dataInfo.setCtime(mypoint.getUpdateAt());
                                                        dataInfoList.add(dataInfo);
                                                        //判断是否超过正常范围，需要推送报警
                                                        isAlarm(mypoint);
                                                        //数据推送前端
                                                        equipDataPush(mypoint, changeFlag);
                                                    } else {
                                                        mypoint.setSingal(0);
                                                    }
                                                } else {
                                                    mypoint.setSingal(0);
                                                }
                                                //更新变量缓存信息，key:变量长名
                                                redisUtil.stringWithSet("var@" + mypoint.getVariableName(), JSON.toJSONString(mypoint, SerializerFeature.WriteMapNullValue));
                                            }
                                        } catch (Exception e) {
                                            log.error("解析 PointVariable 异常，变量名: {}, JSON内容: {}", varName, varInfoObj.toString(), e);
                                            log.error("异常类型: {}, 异常信息: {}", e.getClass().getName(), e.getMessage());
                                            log.error("完整异常堆栈: ", e);
                                            continue; // 继续处理下一个变量
                                        }

                                    }
                                }
                            }
                        }
                        long endTime2 = System.currentTimeMillis();
                        log.info("for循环逻辑判断时间：{}", endTime2 - startTime2);
                        long startTime1 = System.currentTimeMillis();
                        //将要存储的influxdb数据集合，放入influxdb存储队列。
                        requestQueue.getInfluxdbQueue().put(dataInfoList);
                        log.info("当前Influxdb数据存储队列存数：{}", requestQueue.getInfluxdbQueue().size());
                        long endTime1 = System.currentTimeMillis();
                        log.info("放入Influxdb队列时间：{}", endTime1 - startTime1);
                    }
                    long endTime = System.currentTimeMillis();
                    log.info("数据解析时间：{}", endTime - startTime);
                }else {
                    continue;
                }
            } catch (Exception e) {
                log.error("数据解析异常：{}", e.getMessage(), e);
//                running = false;
            }
        }
    }

    public void setRunning(boolean running){
        this.running = running;
    }

    /**
     * 判断设别参数值是否改变
     * @param mypoint
     * @param newValue
     * @return
     */
    public boolean judgeChangeValue(PointVariable mypoint, String newValue){
        Boolean changeFlag = false;
        if(ObjectUtils.isNotEmpty(mypoint.getValue())) {
            if (!newValue.equals(mypoint.getValue())) {
                changeFlag = true;
            }
        }else{
            changeFlag = true;
        }
        return changeFlag;
    }

    /**
     * 参数值判断，处理，推送
     * @param mypoint
     * @return
     */
    public PointVariable equipDataHandle(PointVariable mypoint){
        PointVariable point = new PointVariable();
        if(ObjectUtils.isNotEmpty(mypoint.getValue())) {
            if(mypoint.getDataType() == 2) {
                //参数值小数点后含2位以上小数的，只保留两位小数
                boolean containsDecimal = mypoint.getValue().matches(".*" + regex + ".*");
                if (containsDecimal) {
                    BigDecimal number = new BigDecimal(mypoint.getValue());
                    number = number.setScale(2, RoundingMode.DOWN);
                    mypoint.setValue(number.toString());
                }
            }

            if(mypoint.getVariableName().contains("HX01_7210_DGH") || mypoint.getVariableName().contains("HX01_7210_ZGH")) {
                //固化设备的温度和压力值处理
                String[] split = mypoint.getVariableName().split("_");
                if (split.length > 3) {
                    String s2 = split[2];
                    String s3 = split[3];
                    //固化的温度和压力数值处理，温度*0.1，压力*0.01
                    if (s2.contains("GH") && s3.contains("GH")) {
                        if (mypoint.getVariableName().endsWith("WD")) {
                            try {
                                BigDecimal number = new BigDecimal(mypoint.getValue());
                                if (number.compareTo(new BigDecimal(0)) > 0) {
                                    //计算结果保留两位小数，其他小数位舍弃
                                    number = number.multiply(new BigDecimal(0.1)).setScale(2, RoundingMode.DOWN);
                                }
                                mypoint.setValue(number.toString());
                            } catch (NumberFormatException e) {
                                log.error("newValue不是数字，newValue：" + mypoint.getValue());
                            }
                        } else if (mypoint.getVariableName().endsWith("YL")) {
                            try {
                                BigDecimal number = new BigDecimal(mypoint.getValue());
                                if (number.compareTo(new BigDecimal(0)) > 0) {
                                    //计算结果保留两位小数，其他小数位舍弃
                                    number = number.multiply(new BigDecimal(0.01)).setScale(2, RoundingMode.DOWN);
                                }
                                mypoint.setValue(number.toString());
                            } catch (NumberFormatException e) {
                                log.error("newValue不是数字，newValue：" + mypoint.getValue());
                            }
                        }
                    }
                }
            }else if(mypoint.getVariableName().contains("HX01_7210_PHS")){
                //喷涂设备的温度值处理
                String[] split = mypoint.getVariableName().split("_");
                if (split.length > 3) {
                    String s2 = split[2];
                    String s3 = split[3];
                    String s4 = split[4];
                    //喷涂的温度值处理，温度*0.1
                    if (s2.contains("PHS") && s3.contains("PHS") && s4.startsWith("WD")) {
                        try {
                            BigDecimal number = new BigDecimal(mypoint.getValue());
                            if (number.compareTo(new BigDecimal(0)) > 0) {
                                //计算结果保留1位小数，其他小数位舍弃
                                number = number.multiply(new BigDecimal(0.1)).setScale(1, RoundingMode.DOWN);
                            }
                            mypoint.setValue(number.toString());
                        } catch (NumberFormatException e) {
                            log.error("newValue不是数字，newValue：" + mypoint.getValue());
                        }
                    }
                }
            }else{
                //其他设备变量值处理
                // 获取最后一个下划线的位置
                int lastIndex = mypoint.getVariableName().lastIndexOf('_');
                // 提取最后一个下划线后的子串
                String result = mypoint.getVariableName().substring(lastIndex + 1);

                if(result.contains("KQWD") || result.contains("KQSD") || result.contains("WD")){
                    try {
                        BigDecimal number = new BigDecimal(mypoint.getValue());
                        number = number.multiply(new BigDecimal(0.1)).setScale(2, RoundingMode.DOWN);
                        mypoint.setValue(number.toString());
                    } catch (NumberFormatException e) {
                        log.error("newValue不是数字，newValue：" + mypoint.getValue());
                    }
                }
            }
        }
        point = mypoint;
//        log.info("point数据:{}", JSON.toJSONString(point));
        return point;
    }

    /**
     * 特殊设备变量（自动卷帘门、升降平台）判断、处理
     * @param variableName
     * @param newValue
     */
    public void eqtDataHandle(String variableName, String newValue) {

        switch (variableName){
            case "HX01_7210_ASAMPLC1_AS01_FK":
            case "HX01_7210_ASAMPLC1_AS02_FK":
            case "HX01_7210_ASAMPLC1_AS03_FK":
            case "HX01_7210_ASAMPLC2_AS04_FK":
            case "HX01_7210_ASAMPLC2_AS05_FK":
            case "HX01_7210_ASAMPLC3_AS06_FK":
            case "HX01_7210_ASAMPLC3_AS07_FK":
            case "HX01_7210_ASAMPLC4_AS08_FK":
            case "HX01_7210_ASAMPLC4_AS09_FK":
            case "HX01_7210_ASAMPLC4_AS10_FK":
            case "HX01_7210_ASAMPLC5_AN01_FK":
            case "HX01_7210_ASAMPLC5_AN02_FK":
            case "HX01_7210_ASAMPLC6_AN03_FK":
            case "HX01_7210_ASAMPLC6_AN04_FK":
            case "HX01_7210_ASAMPLC7_AN05_FK":
            case "HX01_7210_ASAMPLC7_AN06_FK":
            case "HX01_7210_ASAMPLC8_AN07_FK":
            case "HX01_7210_ASAMPLC8_AN08_FK":
            case "HX01_7210_ASAMPLC9_AN09_FK":
            case "HX01_7210_ASAMPLC9_AN10_FK":
            case "HX01_7210_ASAMPLC10_AN11_FK":
            case "HX01_7210_ASAMPLC10_AN12_FK":
            case "HX01_7210_ASAMPLC11_AN13_FK":
            case "HX01_7210_ASAMPLC11_AN14_FK":
            case "HX01_7210_ASAMPLC12_AN15_FK":
                //卷帘门已开、关到位
                if(!"0".equals(newValue)) {
                    long startTime = System.currentTimeMillis();
                    updateDoorStatus(variableName, newValue);
                    long endTime = System.currentTimeMillis();
//                    log.info("变量：{}，门状态处理：{}",  variableName, endTime-startTime);
                }
                break;
            case "HX01_7210_SJPT_sjpt001_Allowentry":
                //升降平台门开到位，允许进出
                break;
            case "HX01_7210_SJPT_sjpt001_door1sta":
                //升降平台一层门状态：1已开2已关3正开4正关5故障
                break;
            case "HX01_7210_SJPT_sjpt001_door2sta":
                //升降平台二层门状态：1已开2已关3正开4正关5故障
                break;
            case "HX01_7210_SJPT_sjpt001_state":
                //升降平台设备总状态：0不在楼层1正升2正降3在二层4在一层
                break;
            case "HX01_7210_SJPT_sjpt001_REMOTE":
                //升降平台远程就地状态，1-远程，0-就地
                break;
            case "HX01_7210_SJPT_sjpt001_GZ":
                //升降平台故障总状态，不为0
                break;
//            case "HX01_7210_YH3_YH0003_ASAM#30JRHFBB001CT1":
//            case  "HX01_7210_ZGH4_ZGH0004_GJWD":
//                //升降平台故障总状态，不为0
//                isTaskEnabled(variableName,newValue);
////                updateLiftCache(null, Integer.parseInt(newValue), null, null, null);
//                break;
            default:break;
        }
    }

    /**
     * 更新门状态，并判断是否发送命令给Agv调度系统
     * @param variableName
     * @param newVaule
     */
    private void updateDoorStatus(@NotNull String variableName, String newVaule){
//        log.info("卷帘门反馈变量{}，值：{}", variableName, newVaule);
        Boolean changeFlag = false;
        Integer doorStatus = Integer.parseInt(newVaule);
        String[] varNames =  variableName.split("_");
        if(varNames.length > 4){
            String doorId = varNames[3];
            if (ObjectUtils.isNotEmpty(doorId)) {
                if (redisUtil.isHasKey("door@" + doorId)) {
                    Object varInfoObj = redisUtil.stringWithGet("door@" + doorId);
                    if(ObjectUtils.isNotEmpty(varInfoObj)) {
                        DoorInfoVO doorInfoVo = JSON.parseObject(varInfoObj.toString(), DoorInfoVO.class);
                        //判断自动卷帘门的状态是否改变
                        if(doorInfoVo.getDoorStatus() != doorStatus){
                            changeFlag = true;
                        }
                        doorInfoVo.setDoorStatus(doorStatus);
                        //doorStatus门的到位状态：1-开到位，2-关到位，3-故障
                        if(doorStatus == 1){
                            //当前门存在AGV请求，开门请求置0
                            if(doorInfoVo.getAgvRequest() == 1) {
                                log.info("卷帘门{}开到位，允许通行", doorId);
                                doorInfoVo.setAgvRequest(0);
                                //给机器人调度系统发送运行通行卷帘门的指令
                                Map params = new HashMap<>();
                                params.put("doorId", doorId);
                                params.put("doorState", "hasOpen");
                                restApiService.doorHasOpen(JSON.toJSONString(params));
//                                asynTaskService.doorHasOpen(doorId);
                            }
                        }else if(doorStatus == 2){
                            //agvRequest当前门存在AGV请求，关门请求置0
                            if(doorInfoVo.getAgvRequest() == 2) {
                                log.info("卷帘门{}关到位", doorId);
                                doorInfoVo.setAgvRequest(0);
                            }
                        }else{
                            //自动卷帘门故障，进行故障提醒
                            log.error("卷帘门{}发生故障", doorId);
                            if(doorInfoVo.getAgvRequest() > 0) {
                                doorInfoVo.setAgvRequest(0);
                            }

                        }
                        doorInfoVo.setUpdateAt(sdf.format(new Date()));
                        //更新缓存
                        redisUtil.stringWithSet("door@" + doorId, JSON.toJSONString(doorInfoVo, SerializerFeature.WriteMapNullValue));
                        if(changeFlag) {
                            //向前端或三维推送门状态
                            RealTimeDataVO realTimeDataVO = new RealTimeDataVO();
                            realTimeDataVO.setData(doorInfoVo);
                            realTimeDataVO.setCode("doorStatus");
                            pusherLiteService.sendRealTimeMessageByGroup(doorInfoVo.getDoorCode(), realTimeDataVO);
                            //更新业务系统的门状态
//                            asynTaskService.updateDoorStatus(JSON.toJSONString(doorInfoVo));
                        }
                    }
                }
            }
        }
    }

    /**
     * 推送前端数据
     * @param mypoint
     * @param changeFlag
     */
    private void equipDataPush(PointVariable mypoint, Boolean changeFlag){

        if(ObjectUtils.isNotEmpty(mypoint.getIsPush()) && ObjectUtils.isNotEmpty(mypoint.getValue())) {
            //推送到前端：改变推送
//            if (mypoint.getIsPush() == 1 && changeFlag) {
            log.info("点变量：{}的isPush是{}",mypoint.getVariableName(),mypoint.getIsPush());
            if (mypoint.getIsPush() == 1) {
                //判断是否信号量，1或0转换成是或否
                if(mypoint.getDataType() == 1){
                    if("1".equals(mypoint.getValue())){
                        mypoint.setValue("是");
                    }else{
                        mypoint.setValue("否");
                    }
                }
                //将变量描述简写，赋值给变量描述，进行前端展示
                if(ObjectUtils.isNotEmpty(mypoint.getRemark())) {
                    mypoint.setVariableDesp(mypoint.getRemark());
                }
                RealTimeDataVO realTimeDataVO = new RealTimeDataVO();
                realTimeDataVO.setData(mypoint);
                realTimeDataVO.setCode("pointer");
                pusherLiteService.sendRealTimeMessageByGroup(mypoint.getVariableName(), realTimeDataVO);
            }
        }
    }

    private void isAlarm(PointVariable mypoint){
        if(ObjectUtils.isNotEmpty(mypoint)){
            if(ObjectUtils.isNotEmpty(mypoint.getValue())){
                double value = Double.parseDouble(mypoint.getValue());
                if(ObjectUtils.isNotEmpty(mypoint.getLowerLimit()) || ObjectUtils.isNotEmpty(mypoint.getUpperLimit())){
                    // 创建副本对象
                    PointVariable alarmPoint = new PointVariable();
                    // 复制所有属性
                    BeanUtils.copyProperties(mypoint, alarmPoint);

                    // 修改副本对象的属性
                    alarmPoint.setVariableName(alarmPoint.getVariableName()+"_BJ");
                    if(ObjectUtils.isNotEmpty(alarmPoint.getLowerLimit()) && ObjectUtils.isNotEmpty(alarmPoint.getUpperLimit())){
                        double lower = Double.parseDouble(alarmPoint.getLowerLimit());
                        double upper = Double.parseDouble(alarmPoint.getUpperLimit());
                        if(lower <= value && value <= upper){
                            alarmPoint.setValue("0");
                        }else if(!(lower <= value)){
                            alarmPoint.setValue("-1");
                        }else if(!(value <= upper)){
                            alarmPoint.setValue("1");
                        }
                    }else if(ObjectUtils.isNotEmpty(alarmPoint.getLowerLimit())){
                        double lower = Double.parseDouble(alarmPoint.getLowerLimit());
                        if(!(lower <= value)){
                            alarmPoint.setValue("-1");
                        }else {
                            alarmPoint.setValue("0");
                        }
                    }else if(ObjectUtils.isNotEmpty(alarmPoint.getUpperLimit())){
                        double upper = Double.parseDouble(alarmPoint.getUpperLimit());
                        if(!(value <= upper)){
                            alarmPoint.setValue("1");
                        }else {
                            alarmPoint.setValue("0");
                        }
                    }
                    if(alarmPoint.getVariableName().endsWith("_BJ")){
                        RealTimeDataVO realTimeDataVO = new RealTimeDataVO();
                        realTimeDataVO.setData(alarmPoint);
                        realTimeDataVO.setCode("pointer");
                        pusherLiteService.sendRealTimeMessageByGroup(alarmPoint.getVariableName(), realTimeDataVO);
                    }
                }
            }
        }
    }

}
