package com.robotCore.multiScheduling.control;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.beans.redis.RedisUtil;
import com.robotCore.common.base.service.AsynTaskService;
import com.robotCore.common.base.service.PusherLiteService;
import com.robotCore.common.base.service.RestApiService;
import com.robotCore.multiScheduling.vo.DoorInfoVO;
import com.utils.tools.JsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Description: 金陵AGV转运任务
 * @Author: Created by dingps on 2022/11/30.
 */

@RestController
@Api(value = "JlAgvController")
@RequestMapping(value = "/agvTask")
@Slf4j
public class JlAgvController {

    @Autowired
    private RestApiService restApiService;

    @Autowired
    private AsynTaskService asynTaskService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    PusherLiteService pusherLiteService;

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @ApiOperation(value = "请求开卷帘门", notes = "转运任务")
    @PostMapping(value = "/openDoor")
    public Object openDoor(@RequestBody String data) {
        log.info("请求开卷帘门的接口数据：{}", data);
        Map<String,Object> map =new HashMap<>();
        Map<String,Object> metaMap =new HashMap<>();
        Map<String,String> dataResult =new HashMap<>();
        String returnObj = "";
        if(Objects.nonNull(data)) {
            Map<String, String> dataMap = JsonUtils.toMap(data);
            try {
                if(dataMap.containsKey("doorId") && ObjectUtils.isNotEmpty(dataMap.get("doorId"))) {
                    if (redisUtil.isHasKey("door@" + dataMap.get("doorId"))) {
                        Object varInfoObj = redisUtil.stringWithGet("door@" + dataMap.get("doorId"));
                        if (ObjectUtils.isNotEmpty(varInfoObj)) {
                            DoorInfoVO doorInfoVo = JSON.parseObject(varInfoObj.toString(), DoorInfoVO.class);
                            if (doorInfoVo.getDoorEnable() == 1) {
                                if(doorInfoVo.getDoorStatus() == 1){
                                    log.info("卷帘门{}已开", dataMap.get("doorId"));
                                    metaMap.put("code", 200);
                                    metaMap.put("success", true);
                                    metaMap.put("msg", "卷帘门"+ dataMap.get("doorId") +"已开");
                                    //更新缓存中卷帘门的请求状态
                                    updateDoorCache(dataMap.get("doorId"), 1);
                                    //查看门的状态
//                                    asynTaskService.doorHasOpen(dataMap.get("doorId"));
                                }else if(doorInfoVo.getDoorStatus() == 2){
                                    log.info("给IOServer发送打开卷帘门{}的指令", dataMap.get("doorId"));
                                    //给IOServer发指令，打开对应编号的卷帘门
                                    asynTaskService.openDoorAsync(dataMap.get("doorId"));
                                    metaMap.put("code", 200);
                                    metaMap.put("success", true);
                                    metaMap.put("msg", "success");
                                }else if(doorInfoVo.getDoorStatus() == 3){
                                    log.error("卷帘门{}故障，无法开门", dataMap.get("doorId"));
                                    metaMap.put("code", 500);
                                    metaMap.put("success", false);
                                    metaMap.put("msg", "卷帘门"+ dataMap.get("doorId") +"故障，无法开门");
                                }else {
                                    log.error("卷帘门{}状态异常", dataMap.get("doorId"));
                                    metaMap.put("code", 500);
                                    metaMap.put("success", false);
                                    metaMap.put("msg", "卷帘门"+ dataMap.get("doorId") +"状态异常");
                                }
                            }else{
                                log.error("卷帘门{}不可用", dataMap.get("doorId"));
                                metaMap.put("code", 500);
                                metaMap.put("success", false);
                                metaMap.put("msg", "卷帘门"+ dataMap.get("doorId") +"不可用，无法开门");
                            }
                        }
                    } else {
                        log.error("卷帘门{}不存在", dataMap.get("doorId"));
                        metaMap.put("code", 500);
                        metaMap.put("success", false);
                        metaMap.put("msg", "卷帘门"+ dataMap.get("doorId") +"不存在，请检查门信息是否正确！");
                    }
                } else {
                    metaMap.put("code", 201);
                    metaMap.put("success", false);
                    metaMap.put("msg", "请求参数异常");
                }
            } catch (Exception e) {
                e.printStackTrace();
                metaMap.put("code", 500);
                metaMap.put("success", false);
                metaMap.put("msg", e.getMessage());
            }
            dataResult.put("doorId", dataMap.get("doorId"));
        }else{
            metaMap.put("code", 201);
            metaMap.put("success", false);
            metaMap.put("msg", "请求参数为空");
        }
        metaMap.put("timestamp", new Date());
        map.put("meta",metaMap);
        map.put("data",dataResult);
        returnObj = JSON.toJSONString(map);
        log.info("请求开卷帘门的接口返回：{}", returnObj);
        return map;
    }

    @ApiOperation(value = "请求关卷帘门", notes = "转运任务")
    @PostMapping(value = "/closeDoor")
    public Object closeDoor(@RequestBody String data) {
        log.info("请求关卷帘门的接口数据：{}", data);
        Map<String,Object> map =new HashMap<>();
        Map<String,Object> metaMap =new HashMap<>();
        Map<String,String> dataResult =new HashMap<>();
        String returnObj = "";
        if(Objects.nonNull(data)) {
            Map<String, String> dataMap = JsonUtils.toMap(data);
            try {
                if(dataMap.containsKey("doorId") && ObjectUtils.isNotEmpty(dataMap.get("doorId"))) {
                    if (redisUtil.isHasKey("door@" + dataMap.get("doorId"))) {
                        Object varInfoObj = redisUtil.stringWithGet("door@" + dataMap.get("doorId"));
                        if (ObjectUtils.isNotEmpty(varInfoObj)) {
                            DoorInfoVO doorInfoVo = JSON.parseObject(varInfoObj.toString(), DoorInfoVO.class);
                            if(doorInfoVo.getDoorEnable() == 1) {
                                if(doorInfoVo.getDoorStatus() == 1){
                                    log.info("给IOServer发送关卷帘门{}的指令", dataMap.get("doorId"));
                                    //给IOServer发指令，关闭对应编号的卷帘门
                                    asynTaskService.closeDoorAsync(dataMap.get("doorId"));
                                    metaMap.put("code", 200);
                                    metaMap.put("success", true);
                                    metaMap.put("msg", "success");
                                }else if(doorInfoVo.getDoorStatus() == 2){
                                    log.info("卷帘门{}已关", dataMap.get("doorId"));
                                    metaMap.put("code", 200);
                                    metaMap.put("success", false);
                                    metaMap.put("msg", "卷帘门"+ dataMap.get("doorId") +"已关，无需重复请求");
                                }else if(doorInfoVo.getDoorStatus() == 3){
                                    log.error("卷帘门{}故障，无法关门", dataMap.get("doorId"));
                                    metaMap.put("code", 500);
                                    metaMap.put("success", false);
                                    metaMap.put("msg", "卷帘门"+ dataMap.get("doorId") +"故障，无法开门");
                                }else{
                                    log.error("卷帘门{}状态异常，无法关门", dataMap.get("doorId"));
                                    metaMap.put("code", 500);
                                    metaMap.put("success", false);
                                    metaMap.put("msg", "卷帘门"+ dataMap.get("doorId") +"状态异常，无法开门");
                                }
                            } else {
                                log.error("卷帘门{}不可用", dataMap.get("doorId"));
                                metaMap.put("code", 500);
                                metaMap.put("success", false);
                                metaMap.put("msg", "卷帘门"+ dataMap.get("doorId") +"不可用，无法开门");
                            }
                        }
                    } else {
                        log.error("卷帘门{}不存在", dataMap.get("doorId"));
                        metaMap.put("code", 500);
                        metaMap.put("success", false);
                        metaMap.put("msg", "卷帘门"+ dataMap.get("doorId") +"不存在，请检查门信息是否正确！");
                    }
                } else {
                    metaMap.put("code", 201);
                    metaMap.put("success", false);
                    metaMap.put("msg", "请求参数异常");
                }
            } catch (Exception e) {
                e.printStackTrace();
                metaMap.put("code", 500);
                metaMap.put("success", false);
                metaMap.put("msg", e.getMessage());
            }
            dataResult.put("doorId", dataMap.get("doorId"));
        }else{
            metaMap.put("code", 201);
            metaMap.put("success", false);
            metaMap.put("msg", "请求参数为空");
        }
        metaMap.put("timestamp", new Date());
        map.put("meta",metaMap);
        map.put("data",dataResult);
        returnObj = JSON.toJSONString(map);
        log.info("请求关卷帘门的接口返回：{}", returnObj);
        return map;
    }

    @ApiOperation(value = "查询卷帘门状态", notes = "转运任务")
    @PostMapping(value = "/queryDoorState")
    public Object queryDoorState(@RequestBody String data) {
        log.info("请求查询卷帘门状态的接口数据：{}", data);
        Map<String,Object> map =new HashMap<>();
        Map<String,Object> metaMap =new HashMap<>();
        Map<String,Integer> dataResult =new HashMap<>();
        if(Objects.nonNull(data)) {
            Map<String, String> dataMap = JsonUtils.toMap(data);
            if(dataMap.containsKey("doorId") && ObjectUtils.isNotEmpty(dataMap.get("doorId"))) {
                if (redisUtil.isHasKey("door@" + dataMap.get("doorId"))) {
                    Object varInfoObj = redisUtil.stringWithGet("door@" + dataMap.get("doorId"));
                    if (ObjectUtils.isNotEmpty(varInfoObj)) {
                        DoorInfoVO doorInfoVo = JSON.parseObject(varInfoObj.toString(), DoorInfoVO.class);
                        dataResult.put("doorState", doorInfoVo.getDoorStatus());
                        metaMap.put("code", 200);
                        metaMap.put("success", true);
                        metaMap.put("msg", "success");
                    } else {
                        log.error("门{}在数据库中没有数据", dataMap.get("doorId"));
                        metaMap.put("code", 500);
                        metaMap.put("success", false);
                        metaMap.put("msg", "门{}在数据库中没有数据");
                    }
                }
            } else {
                log.error("门{}不存在", dataMap.get("doorId"));
                metaMap.put("code", 500);
                metaMap.put("success", false);
                metaMap.put("msg", "门不存在");
            }
        } else {
            log.error("请求输入参数为空");
            metaMap.put("code", 500);
            metaMap.put("success", false);
            metaMap.put("msg", "请求输入参数为空");
        }
        metaMap.put("timestamp", new Date());
        map.put("meta",metaMap);
        map.put("data",dataResult);
        return map;
    }

    /**
     * 更新缓存中门的请求状态
     * @param doorId
     * @param agvRequest
     */
    private void updateDoorCache(String doorId, Integer agvRequest){
        if (redisUtil.isHasKey("door@" + doorId)) {
            Object varInfoObj = redisUtil.stringWithGet("door@" + doorId);
            if(ObjectUtils.isNotEmpty(varInfoObj)) {
                DoorInfoVO doorInfoVo = JSON.parseObject(varInfoObj.toString(), DoorInfoVO.class);
                doorInfoVo.setAgvRequest(agvRequest);
                redisUtil.stringWithSet("door@" + doorId, JSON.toJSONString(doorInfoVo, SerializerFeature.WriteMapNullValue));
            }
        }
    }

}
