package com.asam.sysdata.controller;

import com.alibaba.fastjson.JSON;
import com.asam.common.base.controller.BaseController;
import com.asam.common.constant.Constant;
import com.asam.common.logUtil.ControllerLog;
import com.asam.common.logUtil.OpsLogType;
import com.asam.common.util.ObjectUtil;
import com.entity.R;
import com.asam.human.entity.UserVO;
import com.asam.sysdata.entity.SysData;
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
 * @Description: 数据字典--内容
 * @Author: fyy
 * @Create: 2022-04-20
 */
@RestController
@RequestMapping(value = "/sysData")
@Slf4j
public class SysDataController extends BaseController {

    @Autowired
    SysDataService sysDataService;
    @Autowired
    SysDataTypeService sysDataTypeService;

    @ApiOperation(value = "获取分页列表")
    @RequestMapping(value = "/list")
    public Object list(JPAPage varBasePage, String textName, Long dataTypeId){
        IPage<SysData> page = sysDataService.findPageList(varBasePage, textName,dataTypeId);
        return R.ok(page);
    }

    @ControllerLog(type = OpsLogType.ADDORUPDATE, value = "数据字典内容")
    @ApiOperation(value = "新增或更新对象")
    @RequestMapping(value = "/save")
    public Object save(String data){
        if (ObjectUtil.isNotEmpty(data)) {
            SysData model = JSON.parseObject(data, SysData.class);
            // 保存外链接
            if (ObjectUtil.isNotEmpty(model.getDataTypeId())) {
                SysDataType sysDataType = sysDataTypeService.getById(model.getDataTypeId());
                if (sysDataType != null) {
                    model.setDataTypeCode(sysDataType.getTypeCode());
                    model.setDataTypeName(sysDataType.getTypeName());
                }
            }else {
                model.setDataTypeCode(null);
                model.setDataTypeName(null);
            }
            model.setState(Constant.STATE_VALID);
            UserVO curUser=getCurUser();//当前创建人
            boolean b=sysDataService.save(model,curUser.getId(),curUser.getUserName());
            return b?R.ok(): R.error();
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "根据Id获取对象")
    @RequestMapping(value = "/findById")
    public Object findById(Long id){
        if (ObjectUtil.isNotEmpty(id)) {
            SysData model= sysDataService.getById(id);
            return R.ok(model);
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ControllerLog(type = OpsLogType.DELETE, value = "数据字典内容")
    @ApiOperation(value = "根据Id删除对象")
    @RequestMapping(value = "/delete")
    public Object delete(Long id) {
        if (ObjectUtil.isNotEmpty(id)) {
            SysData model = sysDataService.getById(id);
            model.setState(Constant.STATE_INVALID);
            UserVO curUser = getCurUser();//获取当前登录人
            boolean b=sysDataService.save(model, curUser.getId(), curUser.getUserName());
            return b? R.ok() : R.error();
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "校验名称")
    @RequestMapping(value = "/check")
    public Object check(String textName,  Long dataTypeId,Long id){
        if (ObjectUtil.isNotEmpty(textName)) {
            List<SysData> list = sysDataService.findList(id,textName,null,dataTypeId);
            boolean b=Objects.isNull(list) || 0 == list.size();
            return R.ok(b);
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "根据类型编码获得对象")
    @RequestMapping(value = "/findByTypeCode")
    public Object findByTypeCode(String dataTypeCode){
        if (ObjectUtil.isNotEmpty(dataTypeCode)) {
            List<SysData> list = sysDataService.findList(null,null,dataTypeCode,null);
            return R.ok(list);
        } else {
            return R.error("前端传递参数为空");
        }
    }
}
