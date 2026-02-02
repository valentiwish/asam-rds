package com.robotCore.robot.controller;

/**
 * @Des:
 * @author: zxl
 * @date: 2024/6/18
 **/

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.entity.EntityResult;
import com.entity.R;
import com.page.BasePage;
import com.page.FormatPage;
import com.page.JPAPage;
import com.robotCore.common.utils.ObjectUtil;
import com.robotCore.robot.dto.RobotDmsEditorReqDto;
import com.robotCore.robot.dto.RobotEditorIdsDTO;
import com.robotCore.robot.dto.RobotEditorIdsStatusDTO;
import com.robotCore.robot.entity.RobotChargePile;
import com.robotCore.robot.entity.RobotDmsEditor;
import com.robotCore.robot.entity.RobotDmsRegionRelate;
import com.robotCore.robot.service.RobotDmsEditorService;
import com.robotCore.robot.service.RobotDmsRegionRelateService;
import com.utils.tools.CopyUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@Slf4j
@RestController
@Api(value = "RobotDmsEditorController", description = "机器人光通信区域编辑控制器")
@RequestMapping(value = "dmsEditor")
public class RobotDmsEditorController {

    @Autowired
    private RobotDmsEditorService robotDmsEditorService;

    @Autowired
    private RobotDmsRegionRelateService robotDmsRegionRelateService;

    @ApiOperation(value = "新增光通信区域", notes = "新增光通信区域")
    @PostMapping(value = "/save")
    public Object save(@RequestBody RobotDmsEditorReqDto dto) {
        if (ObjectUtil.isNotEmpty(dto)) {
            RobotDmsEditor model = new RobotDmsEditor();
            model.setAreaName(dto.getAreaName());
            model.setAreaInfo(dto.getAreaInfo());
            model.setAreaCenterPoint(dto.getAreaCenterPoint());
            model.setAreaContainPoints(dto.getAreaContainPoints());
            model.setAreaType(dto.getAreaType());
            //创建后设置为非占用状态
            model.setOccupiedStatus(0);
            boolean flag = robotDmsEditorService.saveOrUpdate(model);
            return flag ? R.ok() : R.error("新增光通信站点失败");
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "获取机器人光通信区域列表")
    @RequestMapping(value = "/list")
    public Object list(){
        List<RobotDmsEditor> list = robotDmsEditorService.findList();
        return R.ok(list);
    }

    @ApiOperation(value = "获取区域管控列表")
    @RequestMapping(value = "/areaList")
    public Object focusAreaList(JPAPage varBasePage, String data){
        RobotDmsEditor robotDmsEditor =null;
        IPage<RobotDmsEditor> varPage = BasePage.getMybaitsPage(varBasePage);
        CopyUtils.convert(varBasePage, varPage, CopyUtils.methodType.Define);
        if(ObjectUtil.isNotEmpty(data)){
            robotDmsEditor = JSON.parseObject(data,RobotDmsEditor.class);
        }
        EntityResult varEntityResult = new EntityResult();
        IPage<RobotDmsEditor> page = robotDmsEditorService.findPageList(varPage, robotDmsEditor);
        varEntityResult.setData(FormatPage.getFormatMybaitsPage(page));
        return varEntityResult;
    }

    //    @ControllerLog(type = OpsLogType.DELETE, value ="删除光通信区域")
    @ApiOperation(value = "删除光通信区域")
    @RequestMapping(value = "/delete")
    public Object delete(String id) {
        if (ObjectUtil.isNotEmpty(id)) {
            //删除光通信区域前，先删除绑定的相邻区域关系
            List<RobotDmsRegionRelate> relateRegions = robotDmsRegionRelateService.getRelateRegionsByCurrentRegionId(id);
            boolean deleteRegionsFlag = true;
            for (RobotDmsRegionRelate relateRegion : relateRegions) {
                boolean b = robotDmsRegionRelateService.removeById(relateRegion.getId());
                deleteRegionsFlag = deleteRegionsFlag && b;
            }
            if (!deleteRegionsFlag) {
                return R.error("删除当前区域与相邻区域绑定的关系失败");
            }
            boolean flag = robotDmsEditorService.removeById(id);
            return flag ? R.ok() : R.error();
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "查询管控区域是否被其它品牌机器人锁定", notes = "查询管控区域是否被其它品牌机器人锁定")
    @PostMapping(value = "/editorState")
    public Object editorState(@RequestBody RobotEditorIdsDTO dto) {
        log.info("请求查询管控区域是否被其它品牌机器人锁定的接口数据：{}", dto.toString());
        Map<String,Object> map =new HashMap<>();
        Map<String,Object> metaMap =new HashMap<>();
        Map<String,RobotEditorIdsStatusDTO> dataResult =new HashMap<>();
        if (ObjectUtil.isNotEmpty(dto)) {
            List<String> editorIds = dto.getEditorIds();
            //所有请求区域状态
            RobotEditorIdsStatusDTO editorIdsStatusDTO = new RobotEditorIdsStatusDTO();
            List<RobotEditorIdsStatusDTO.RobotEditorIdStatus> list = new ArrayList<>();
            //区域占用状态
            for (String editorId : editorIds) {
                List<RobotDmsEditor> listByAreaCenterPoints = robotDmsEditorService.findListByAreaCenterPoint(editorId);
                if (listByAreaCenterPoints.size() > 0) {
                    RobotDmsEditor robotDmsEditor = listByAreaCenterPoints.get(0);
                    RobotEditorIdsStatusDTO.RobotEditorIdStatus robotEditorIdStatus = new RobotEditorIdsStatusDTO.RobotEditorIdStatus();
                    robotEditorIdStatus.setEditorId(editorId);
                    robotEditorIdStatus.setIsOccupied(robotDmsEditor.getOccupiedStatus());
                    robotEditorIdStatus.setOccupiedRobotType(robotDmsEditor.getOccupiedRobotType());
                    list.add(robotEditorIdStatus);
                }
            }
            editorIdsStatusDTO.setEditorIdStatuses(list);
            dataResult.put("occupiedStatus", editorIdsStatusDTO);
            metaMap.put("code", 200);
            metaMap.put("success", true);
            metaMap.put("msg", "success");
        } else {
            metaMap.put("code", 201);
            metaMap.put("success", false);
            metaMap.put("msg", "请求参数为空");
        }
        metaMap.put("timestamp", new Date());
        map.put("meta",metaMap);
        map.put("data",dataResult);
        return map;
    }

    @ApiOperation(value = "释放管控区域的占用状态", notes = "释放管控区域的占用状态")
    @PostMapping(value = "/removeOccupied")
    public Object removeOccupied(@RequestBody RobotEditorIdsDTO dto) {
        log.info("请求释放管控区域的占用状态的接口数据：{}", dto.toString());
        Map<String,Object> map =new HashMap<>();
        Map<String,Object> metaMap =new HashMap<>();
        Map<String,Integer> dataResult =new HashMap<>();
        if (ObjectUtil.isNotEmpty(dto)) {
            List<String> editorIds = dto.getEditorIds();
            for (String editorId : editorIds) {
                List<RobotDmsEditor> listByAreaCenterPoints = robotDmsEditorService.findListByAreaCenterPoint(editorId);
                if (listByAreaCenterPoints.size() > 0) {
                    RobotDmsEditor robotDmsEditor = listByAreaCenterPoints.get(0);
                    //释放区域占用状态
                    robotDmsEditor.setOccupiedStatus(0);
                    robotDmsEditorService.saveOrUpdate(robotDmsEditor);
                }
            }
            metaMap.put("code", 200);
            metaMap.put("success", true);
            metaMap.put("msg", "success");
        } else {
            metaMap.put("code", 201);
            metaMap.put("success", false);
            metaMap.put("msg", "请求参数为空");
        }
        metaMap.put("timestamp", new Date());
        map.put("meta",metaMap);
        map.put("data",dataResult);
        return map;
    }

}
