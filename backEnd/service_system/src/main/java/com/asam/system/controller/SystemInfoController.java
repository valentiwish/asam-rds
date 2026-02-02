package com.asam.system.controller;

import com.alibaba.fastjson.JSON;
import com.asam.common.base.controller.BaseController;
import com.asam.common.constant.Constant;
import com.asam.common.logUtil.ControllerLog;
import com.asam.common.logUtil.OpsLogType;
import com.asam.common.util.ObjectUtil;
import com.entity.R;
import com.asam.human.entity.UserVO;
import com.asam.system.entity.SysSubsystem;
import com.asam.system.service.MoudleService;
import com.asam.system.service.SystemInfoService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.page.JPAPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

/**
 * @Description: 系统管理
 * @Author: fyy
 * @Create: 2022-04-20
 */
@RestController
@Api(value = "SystemInfoController", description = "系统基础信息")
@RequestMapping(value = "systemInfo")
public class SystemInfoController extends BaseController {

    @Autowired
    private SystemInfoService systemInfoService;
    @Autowired
    private MoudleService moudleService;

    @ApiOperation(value = "获取分页列表")
    @RequestMapping(value = "/list")
    public Object list(JPAPage varBasePage, String name, String appid){
        IPage<SysSubsystem> page = systemInfoService.findPageList(varBasePage, name,appid);
        return R.ok(page);
    }

    @ControllerLog(type = OpsLogType.ADDORUPDATE, value = "系统管理")
    @ApiOperation(value = "新增或更新对象")
    @RequestMapping(value = "/save")
    public Object save(String data){
        if (ObjectUtil.isNotEmpty(data)) {
            SysSubsystem model = JSON.parseObject(data, SysSubsystem.class);
            model.setState(Constant.STATE_VALID);
            model.setEnable(Constant.STATE_VALID);
            UserVO curUser=getCurUser();//当前创建人
            boolean b=systemInfoService.save(model,curUser.getId(),curUser.getUserName());
            return b?R.ok(): R.error();
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "根据Id获取对象")
    @RequestMapping(value = "/findById")
    public Object findById(Long id){
        if (ObjectUtil.isNotEmpty(id)) {
            SysSubsystem model = systemInfoService.getById(id);
            return R.ok(model);
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ControllerLog(type = OpsLogType.DELETE, value = "系统管理")
    @ApiOperation(value = "根据Id删除对象")
    @RequestMapping(value = "/delete")
    public Object delete(Long id) {
        if (ObjectUtil.isNotEmpty(id)) {
            SysSubsystem model = systemInfoService.getById(id);
            model.setState(Constant.STATE_INVALID);
            UserVO curUser = getCurUser();//获取当前登录人
            boolean flag = systemInfoService.save(model,curUser.getId(), curUser.getUserName());
            //引用实体删除
            if (flag) {
                moudleService.deleteBySystemId(id);
            }
            return flag ? R.ok() : R.error();
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "获取对象列表")
    @RequestMapping(value = "/allList")
    public Object allList() {
        List<SysSubsystem> list = systemInfoService.findList(null,1,null);
        return R.ok(list);
    }

    @ApiOperation(value = "校验编码唯一性：true-唯一，false-已存在")
    @RequestMapping(value = "/checkAppid")
    public Object checkAppid(String appid, Long id) {
        if (ObjectUtil.isNotEmpty(appid)) {
            SysSubsystem model = systemInfoService.checkAppid(id,appid);
            boolean b= Objects.isNull(model);
            return R.ok(b);
        } else {
            return R.error("前端传递参数为空");
        }
    }
}