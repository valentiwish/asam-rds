package com.asam.system.controller;

import com.alibaba.fastjson.JSON;
import com.asam.common.base.controller.BaseController;
import com.asam.common.constant.Constant;
import com.asam.common.logUtil.ControllerLog;
import com.asam.common.logUtil.OpsLogType;
import com.asam.common.util.ObjectUtil;
import com.entity.R;
import com.asam.human.entity.UserVO;
import com.asam.system.entity.SysVariable;
import com.asam.system.service.SysVariableService;
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
 * @Description: 设置系统变量开关，用户是否检查白名单等项目
 * @Author: fyy
 * @Create: 2022-07-26
 */
@RestController
@Api(value = "SysVariableController", description = "系统变量管理")
@RequestMapping(value = "sysVariable")
public class SysVariableController extends BaseController {

    @Autowired
    private SysVariableService sysVariableService;

    @ApiOperation(value = "获取分页列表")
    @RequestMapping("/list")
    public Object list(JPAPage varBasePage, String data) {
        SysVariable sysIpFilter = JSON.parseObject(data, SysVariable.class);
        IPage<SysVariable> page = sysVariableService.findPageList(varBasePage, sysIpFilter);
        return R.ok(page);
    }

    @ControllerLog(type = OpsLogType.ADDORUPDATE, value = "职务管理")
    @ApiOperation(value = "新增或更新对象")
    @RequestMapping("/save")
    public Object save(String data) {
        if (ObjectUtil.isNotEmpty(data)) {
            SysVariable model = JSON.parseObject(data, SysVariable.class);
            model.setState(Constant.STATE_VALID);
            UserVO curUser=getCurUser();//当前创建人
            boolean flag = sysVariableService.save(model, curUser.getId(), curUser.getUserName());
            return flag ? R.ok() : R.error();
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ControllerLog(type = OpsLogType.DELETE, value = "职务管理")
    @ApiOperation(value = "根据Id删除对象")
    @RequestMapping(value = "/delete")
    public Object delete(Long id){
        if (ObjectUtil.isNotEmpty(id)) {
            SysVariable model = sysVariableService.getById(id);
            model.setState(Constant.STATE_INVALID);
            UserVO curUser=getCurUser();//当前创建人
            boolean flag = sysVariableService.save(model, curUser.getId(), curUser.getUserName());
            return flag ? R.ok() : R.error();
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "根据Id获取对象")
    @RequestMapping(value = "/findById")
    public Object findById(String id) {
        SysVariable model=sysVariableService.getById(id);
        return R.ok(model);
    }

    @ApiOperation(value = "根据编码查询类型", notes = "系统变量管理")
    @RequestMapping(value = "/findByCode")
    public Object findByCode(String varCode) {
        if (ObjectUtil.isNotEmpty(varCode)) {
            SysVariable sysVasriable =null;
             List<SysVariable> list = sysVariableService.findList(null, varCode);
            if(ObjectUtil.isNotEmpty(list)){
                sysVasriable =list.get(0);
            }
            return R.ok(sysVasriable);
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "编码校验", notes = "系统变量管理")
    @RequestMapping(value = "/checkCodeUnique")
    public Object checkCode(String id, String code) {
        if (ObjectUtil.isNotEmpty(code)) {
            List<SysVariable> list = sysVariableService.findList(id, code);
            boolean b = Objects.isNull(list) || 0 == list.size();
            return R.ok(b);
        } else {
            return R.error("前端传递参数为空");
        }
    }
}