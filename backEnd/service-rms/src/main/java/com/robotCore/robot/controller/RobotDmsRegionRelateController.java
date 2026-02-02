package com.robotCore.robot.controller;

import com.alibaba.fastjson.JSONObject;
import com.entity.R;
import com.robotCore.common.base.controller.BaseController;
import com.robotCore.common.utils.ObjectUtil;
import com.robotCore.robot.dto.RobotDmsRegionRelateReqDto;
import com.robotCore.robot.entity.RobotDmsEditor;
import com.robotCore.robot.entity.RobotDmsRegionRelate;
import com.robotCore.robot.service.RobotDmsEditorService;
import com.robotCore.robot.service.RobotDmsRegionRelateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * @Des:
 * @author: zxl
 * @date: 2024/6/20
 **/

@Slf4j
@RestController
@Api(value = "RobotDmsRegionRelateController", description = "机器人光通信区域绑定")
@RequestMapping(value = "dmsRegionRelate")
public class RobotDmsRegionRelateController  extends BaseController {

    @Autowired
    private RobotDmsRegionRelateService robotDmsRegionRelateService;

    @Autowired
    private RobotDmsEditorService robotDmsEditorService;

    @ApiOperation(value = "获取光通信区域关系绑定列表")
    @RequestMapping(value = "/list")
    public Object list(String currentRegionId){
        List<RobotDmsRegionRelate> list = robotDmsRegionRelateService.getRelateRegionsByCurrentRegionId(currentRegionId);
        return R.ok(list);
    }

    @ApiOperation(value = "新增光通信区域关系绑定", notes = "新增光通信区域关系绑定")
    @PostMapping(value = "/save")
    public Object save(@RequestBody RobotDmsRegionRelateReqDto dto) throws IOException {
        if (ObjectUtil.isNotEmpty(dto)) {
            List<String> list = JSONObject.parseArray(dto.getBindRegionIds(), String.class);
            boolean flag = true;
            for (String s : list) {
                RobotDmsRegionRelate model = new RobotDmsRegionRelate();
                //获取当前区域信息
                RobotDmsEditor currentDmsEditor = robotDmsEditorService.getById(dto.getCurrentRegionId());
                model.setCurrentRegionId(dto.getCurrentRegionId());
                model.setBindRegionId(s);
                model.setCurrentRegionName(currentDmsEditor.getAreaName());
                model.setCurrentRegionPoint(currentDmsEditor.getAreaCenterPoint());
                //获取绑定的区域信息
                RobotDmsEditor bindDmsEditor = robotDmsEditorService.getById(s);
                model.setBindRegionName(bindDmsEditor.getAreaName());
                model.setBindRegionPoint(bindDmsEditor.getAreaCenterPoint());
                boolean flag1 = robotDmsRegionRelateService.saveOrUpdate(model);
                flag = flag && flag1;
            }
            return flag ? R.ok() : R.error("新增光通信区域关系绑定失败");
        } else {
            return R.error("前端传递参数为空");
        }
    }

    //    @ControllerLog(type = OpsLogType.DELETE, value ="删除光通信区域关系绑定")
    @ApiOperation(value = "删除光通信区域关系绑定")
    @RequestMapping(value = "/delete")
    public Object delete(String id) {
        if (ObjectUtil.isNotEmpty(id)) {
            boolean flag = robotDmsRegionRelateService.removeById(id);
            return flag ? R.ok() : R.error();
        } else {
            return R.error("前端传递参数为空");
        }
    }

}
