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
import com.robotCore.robot.entity.RobotDms;
import com.robotCore.robot.service.RobotDmsService;
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
 * @Des: 机器人光通信控制器
 * @author: zxl
 * @date: 2023/6/29
 **/

@Slf4j
@RestController
@Api(value = "RobotDmsController", description = "机器人光通信控制器")
@RequestMapping(value = "dms")
public class RobotDmsController extends BaseController {

    @Autowired
    private RobotDmsService robotDmsService;

    @ApiOperation(value = "获取光通信站列表")
    @PostMapping(value = "/list")
    public Object list(JPAPage varBasePage, String data) {
        RobotDms robotDms =null;
        IPage<RobotDms> varPage = BasePage.getMybaitsPage(varBasePage);
        CopyUtils.convert(varBasePage, varPage, CopyUtils.methodType.Define);
        if(ObjectUtil.isNotEmpty(data)){
            robotDms = JSON.parseObject(data,RobotDms.class);
        }
        EntityResult varEntityResult = new EntityResult();
        IPage<RobotDms> page = robotDmsService.findPageList(varPage, robotDms);
        varEntityResult.setData(FormatPage.getFormatMybaitsPage(page));
        return varEntityResult;
    }

    @ApiOperation(value = "新增光通信站点", notes = "新增光通信站点")
    @PostMapping(value = "/save")
    public Object save(String data) {
        if (ObjectUtil.isNotEmpty(data)) {
            RobotDms model = JSON.parseObject(data,RobotDms.class);
            boolean flag = robotDmsService.saveOrUpdate(model);
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
            boolean flag = robotDmsService.removeById(id);
            return flag ? R.ok() : R.error();
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "根据Id获取对象")
    @RequestMapping(value = "/findById")
    public Object findById(String id){
        if (ObjectUtil.isNotEmpty(id)) {
            RobotDms model= robotDmsService.getById(id);
            return R.ok(model);
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "校验光通信站名称")
    @RequestMapping(value = "/checkDmsName")
    public Object checkDmsName(String dmsName, String id){
        if (ObjectUtil.isNotEmpty(dmsName)) {
            List<RobotDms> list = robotDmsService.findListByDmsName(id,dmsName);
            boolean b= Objects.isNull(list) || 0 == list.size();
            return R.ok(b);
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "校验光通信站IP")
    @RequestMapping(value = "/checkDmsIp")
    public Object checkDmsIp(String dmsIp, String id){
        if (ObjectUtil.isNotEmpty(dmsIp)) {
            List<RobotDms> list = robotDmsService.findListByDmsIp(id,dmsIp);
            boolean b= Objects.isNull(list) || 0 == list.size();
            return R.ok(b);
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "校验光通信站点位")
    @RequestMapping(value = "/checkDmsPoint")
    public Object checkDmsPoint(String dmsPoint, String id){
        if (ObjectUtil.isNotEmpty(dmsPoint)) {
            List<RobotDms> list = robotDmsService.findListByDmsPoint(id,dmsPoint);
            boolean b= Objects.isNull(list) || 0 == list.size();
            return R.ok(b);
        } else {
            return R.error("前端传递参数为空");
        }
    }

}
