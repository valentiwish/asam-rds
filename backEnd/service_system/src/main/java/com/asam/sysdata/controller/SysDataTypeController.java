package com.asam.sysdata.controller;

import com.alibaba.fastjson.JSON;
import com.asam.common.base.controller.BaseController;
import com.asam.common.constant.Constant;
import com.asam.common.logUtil.ControllerLog;
import com.asam.common.logUtil.OpsLogType;
import com.asam.common.util.ObjectUtil;
import com.entity.R;
import com.asam.human.entity.UserVO;
import com.asam.sysdata.entity.SysDataType;
import com.asam.sysdata.service.SysDataService;
import com.asam.sysdata.service.SysDataTypeService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.page.JPAPage;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

/***
 * @Description: 数据字典--类型
 * @Author: fyy
 * @Create: 2022-04-20
 */
@RestController
@RequestMapping(value = "/sysDataType")
@Slf4j
public class SysDataTypeController extends BaseController {

    @Autowired
    SysDataTypeService sysDataTypeService;
    @Autowired
    SysDataService sysDataService;

    @ApiOperation(value = "获取分页列表")
    @RequestMapping(value = "/list")
    public Object list(JPAPage varBasePage,String typeName, String typeCode){
        IPage<SysDataType> page = sysDataTypeService.findPageList(varBasePage, typeName,typeCode);
        return R.ok(page);
    }

    @ControllerLog(type = OpsLogType.ADDORUPDATE, value = "数据字典类型")
    @ApiOperation(value = "新增或更新对象")
    @RequestMapping(value = "/save")
    public Object save(String data){
        if (ObjectUtil.isNotEmpty(data)) {
            SysDataType model = JSON.parseObject(data, SysDataType.class);
            //引用实体更新
            Long updateId=model.getId();
            if(updateId!=null) {
                sysDataService.updateByDataTypeId(model.getTypeCode(), model.getTypeName(),updateId);
            }
            model.setState(Constant.STATE_VALID);
            UserVO curUser=getCurUser();//当前创建人
            boolean b=sysDataTypeService.save(model,curUser.getId(),curUser.getUserName());
            return b?R.ok(): R.error();
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "根据Id获取对象")
    @RequestMapping(value = "/findById")
    public Object findById(Long id){
        if (ObjectUtil.isNotEmpty(id)) {
            SysDataType model=sysDataTypeService.getById(id);
            return R.ok(model);
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ControllerLog(type = OpsLogType.DELETE, value = "数据字典类型")
    @ApiOperation(value = "根据Id删除对象")
    @RequestMapping(value = "/delete")
    public Object delete(Long id) {
        if (ObjectUtil.isNotEmpty(id)) {
            SysDataType model = sysDataTypeService.getById(id);
            model.setState(Constant.STATE_INVALID);
            UserVO curUser = getCurUser();//获取当前登录人
            boolean flag = sysDataTypeService.save(model, curUser.getId(), curUser.getUserName());
            //引用实体删除
            if (flag) {
                sysDataService.deleteByDataTypeId(id);
            }
            return flag ? R.ok() : R.error();
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "校验名称")
    @RequestMapping(value = "/check")
    public Object checkCode(String code, Long id){
        if (ObjectUtil.isNotEmpty(code)) {
            List<SysDataType> list = sysDataTypeService.findList(id,code);
            boolean b=Objects.isNull(list) || 0 == list.size();
            return R.ok(b);
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "获取对象列表")
    @RequestMapping(value = "/findAll")
    public Object findAll(){
        List<SysDataType> list = sysDataTypeService.findList(null,null);
        return R.ok(list);
    }
}
