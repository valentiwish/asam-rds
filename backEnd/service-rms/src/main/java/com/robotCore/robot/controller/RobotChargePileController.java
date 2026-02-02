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
import com.robotCore.robot.entity.RobotChargePile;
import com.robotCore.robot.service.RobotChargePileService;
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
 * @Des: 机器人充电桩控制器类
 * @author: zxl
 * @date: 2024/3/18
 **/
@Slf4j
@RestController
@Api(value = "RobotChargePileController", description = "机器人充电桩控制器类")
@RequestMapping(value = "chargePile")
public class RobotChargePileController extends BaseController {

    @Autowired
    private RobotChargePileService robotChargePileService;

    @ApiOperation(value = "获取智能充电桩列表")
    @PostMapping(value = "/list")
    public Object list(JPAPage varBasePage, String data) {
        RobotChargePile robotChargePile =null;
        IPage<RobotChargePile> varPage = BasePage.getMybaitsPage(varBasePage);
        CopyUtils.convert(varBasePage, varPage, CopyUtils.methodType.Define);
        if(ObjectUtil.isNotEmpty(data)){
            robotChargePile = JSON.parseObject(data,RobotChargePile.class);
        }
        EntityResult varEntityResult = new EntityResult();
        IPage<RobotChargePile> page = robotChargePileService.findPageList(varPage, robotChargePile);
        varEntityResult.setData(FormatPage.getFormatMybaitsPage(page));
        return varEntityResult;
    }

    @ApiOperation(value = "新增智能充电桩", notes = "新增光通信站点")
    @PostMapping(value = "/save")
    public Object save(String data) {
        if (ObjectUtil.isNotEmpty(data)) {
            RobotChargePile model = JSON.parseObject(data,RobotChargePile.class);
            boolean flag = robotChargePileService.saveOrUpdate(model);
            return flag ? R.ok() : R.error("新增光通信站点失败");
        } else {
            return R.error("前端传递参数为空");
        }
    }

    //    @ControllerLog(type = OpsLogType.DELETE, value ="删除光通信站点")
    @ApiOperation(value = "根据Id删除对象")
    @RequestMapping(value = "/delete")
    public Object delete(String id) {
        if (ObjectUtil.isNotEmpty(id)) {
            boolean flag = robotChargePileService.removeById(id);
            return flag ? R.ok() : R.error();
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "根据Id获取对象")
    @RequestMapping(value = "/findById")
    public Object findById(String id){
        if (ObjectUtil.isNotEmpty(id)) {
            RobotChargePile model= robotChargePileService.getById(id);
            return R.ok(model);
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "校验光智能充电桩名称")
    @RequestMapping(value = "/checkChargePileName")
    public Object checkChargePileName(String chargePileName, String id){
        if (ObjectUtil.isNotEmpty(chargePileName)) {
            List<RobotChargePile> list = robotChargePileService.findListByAttribute(id,chargePileName,null,null);
            boolean b= Objects.isNull(list) || 0 == list.size();
            return R.ok(b);
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "校验智能充电桩IP")
    @RequestMapping(value = "/checkChargePileIp")
    public Object checkChargePileIp(String chargePileIp, String id){
        if (ObjectUtil.isNotEmpty(chargePileIp)) {
            List<RobotChargePile> list = robotChargePileService.findListByAttribute(id,null,chargePileIp,null);
            boolean b= Objects.isNull(list) || 0 == list.size();
            return R.ok(b);
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "校验智能充电桩点位")
    @RequestMapping(value = "/checkChargePilePoint")
    public Object checkChargePilePoint(String chargePilePoint, String id){
        if (ObjectUtil.isNotEmpty(chargePilePoint)) {
            List<RobotChargePile> list = robotChargePileService.findListByAttribute(id,null,null,chargePilePoint);
            boolean b= Objects.isNull(list) || 0 == list.size();
            return R.ok(b);
        } else {
            return R.error("前端传递参数为空");
        }
    }
}
