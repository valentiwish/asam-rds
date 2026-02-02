package com.robotCore.robot.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.entity.EntityResult;
import com.entity.R;
import com.page.BasePage;
import com.page.FormatPage;
import com.page.JPAPage;
import com.robotCore.common.base.controller.BaseController;
import com.robotCore.common.utils.ObjectUtil;
import com.robotCore.robot.entity.RobotType;
import com.robotCore.robot.entityVo.RobotTypeVO;
import com.robotCore.robot.service.RobotTypeService;
import com.utils.tools.CopyUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

/**
 * @Des: 机器人类别控制器
 * @author: zxl
 * @date: 2023/10/31
 **/
@Slf4j
@RestController
@Api(value = "RobotTypeController", description = "机器人类别控制器")
@RequestMapping(value = "robotType")
public class RobotTypeController extends BaseController {

    @Autowired
    private RobotTypeService robotTypeService;

    @ApiOperation(value = "获取机器人类型列表")
    @PostMapping(value = "/list")
    public Object list(JPAPage varBasePage, String data) {
        RobotType robotType =null;
        IPage<RobotType> varPage = BasePage.getMybaitsPage(varBasePage);
        CopyUtils.convert(varBasePage, varPage, CopyUtils.methodType.Define);
        if(ObjectUtil.isNotEmpty(data)){
            robotType = JSON.parseObject(data,RobotType.class);
        }
        EntityResult varEntityResult = new EntityResult();
        IPage<RobotTypeVO> page = robotTypeService.findPageList(varPage, robotType);
        varEntityResult.setData(FormatPage.getFormatMybaitsPage(page));
        return varEntityResult;
    }

    @ApiOperation(value = "新增机器人类型", notes = "新增机器人类型")
    @PostMapping(value = "/save")
    public Object save(String data) {
        if (ObjectUtil.isNotEmpty(data)) {
            RobotType model = JSON.parseObject(data, RobotType.class);
            boolean flag = robotTypeService.saveOrUpdate(model);
            return flag ? R.ok() : R.error("新增点失败");
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "根据Id删除对象")
    @RequestMapping(value = "/delete")
    public Object delete(String id) {
        if (ObjectUtil.isNotEmpty(id)) {
            boolean flag = robotTypeService.removeById(id);
            return flag ? R.ok() : R.error();
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "根据Id获取对象")
    @RequestMapping(value = "/findById")
    public Object findById(String id){
        if (ObjectUtil.isNotEmpty(id)) {
            RobotType model= robotTypeService.getById(id);
            return R.ok(model);
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "校验机器人类型名称")
    @RequestMapping(value = "/checkTypeName")
    public Object checkDmsName(String typeName, String id){
        if (ObjectUtil.isNotEmpty(typeName)) {
            List<RobotType> list = robotTypeService.findListByTypeName(id,typeName);
            boolean b= Objects.isNull(list) || 0 == list.size();
            return R.ok(b);
        } else {
            return R.error("前端传递参数为空");
        }
    }
}
