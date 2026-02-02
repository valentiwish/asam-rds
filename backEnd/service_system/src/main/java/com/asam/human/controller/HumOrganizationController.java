package com.asam.human.controller;

import com.alibaba.fastjson.JSON;
import com.asam.common.base.controller.BaseController;
import com.asam.common.base.model.BasisNode;
import com.asam.common.constant.Constant;
import com.asam.common.logUtil.ControllerLog;
import com.asam.common.logUtil.OpsLogType;
import com.asam.common.util.ObjectUtil;
import com.asam.human.entity.HumOrganization;
import com.asam.human.entity.UserVO;
import com.asam.human.service.HumOrganizationService;
import com.asam.human.service.HumUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.entity.R;
import com.page.JPAPage;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/*import com.asam.common.util.R;*/

/**
 * @Description: 组织机构管理
 * @Author: fyy
 * @Create: 2022-04-19
 */
@Slf4j
@RestController
@RequestMapping("/organization")
public class HumOrganizationController extends BaseController {

    @Autowired
    private HumOrganizationService organizationService;
    @Autowired
    private HumUserService userService;

    @ApiOperation(value = "获取分页列表")
    @RequestMapping("/list")
    public Object list(JPAPage varBasePage, String name, String parentName, String code, String parentId) {
        List<String> parentIds=null;
        if(ObjectUtil.isNotEmpty(parentId)){
            parentIds=organizationService.getOrganizationIdsByPid(parentId);
        }
        IPage<HumOrganization> page = organizationService.findPageList(varBasePage, name,parentName,code,parentIds);
        return R.ok(page);
    }

    @ControllerLog(type = OpsLogType.ADDORUPDATE, value = "组织机构管理")
    @ApiOperation(value = "新增或更新对象")
    @RequestMapping("/save")
    public Object save(String data) {
        if (ObjectUtil.isNotEmpty(data)) {
            HumOrganization model = JSON.parseObject(data, HumOrganization.class);
            // 保存外链接
            if (ObjectUtil.isNotEmpty(model.getParentId())) {
                HumOrganization parentOrg = organizationService.getById(model.getParentId());
                if (parentOrg != null) {
                    model.setParentCode(parentOrg.getCode());
                    model.setParentName(parentOrg.getName());
                }
            } else {
                model.setParentCode(null);
                model.setParentName(null);
            }
            //引用实体更新
            String updateId=model.getId();
            if(updateId!=null) {
                organizationService.updateByParentId(model.getCode(), model.getName(),updateId);
                userService.updateByOrgId(model.getCode(), model.getName(),updateId);
            }
            model.setState(Constant.STATE_VALID);
            UserVO curUser=getCurUser();//当前创建人
            boolean b=organizationService.save(model,curUser.getId(),curUser.getUserName());
            return b?R.ok(): R.error();
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "根据Id获取对象")
    @RequestMapping("/findById")
    public Object findById(String id) {
        if (ObjectUtil.isNotEmpty(id)) {
            HumOrganization org=organizationService.getById(id);
            return R.ok(org);
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ControllerLog(type = OpsLogType.DELETE, value = "组织机构管理")
    @ApiOperation(value = "根据Id删除对象")
    @RequestMapping("/delete")
    public Object deleteById(String orgId) {
        if (ObjectUtil.isNotEmpty(orgId)) {
            //引用实体删除
            List<String> mIds = organizationService.getOrganizationIdsByPid(orgId);
            boolean b=true;
            if (mIds.size() > 0) {
                organizationService.updateStateById(Constant.STATE_INVALID, mIds);
            }
            return R.ok( b);
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "校验名称")
    @RequestMapping("/check")
    public Object checkCode(String code, String id){
        if (ObjectUtil.isNotEmpty(code)) {
            List<HumOrganization> list = organizationService.findList(id,code,null,null);
            boolean b=Objects.isNull(list) || 0 == list.size();
            return R.ok(b);
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "获取部门树")
    @RequestMapping("/getOrgTreeById")
    public Object getOrgTreeById(String orgId) {
        List<HumOrganization> humOrganizationList = null;
        if (ObjectUtil.isNotEmpty(orgId)) {
            List<String> ids = organizationService.getOrganizationIdsByPid(orgId);
            humOrganizationList = organizationService.findList(null,null,null,ids);
        } else {
            humOrganizationList = organizationService.findList(null,null,null,null);
        }
        List<BasisNode> nodes = new ArrayList<BasisNode>();
        // 构建树形机构各个节点
        for(HumOrganization organization : humOrganizationList) {
            BasisNode subnode = new BasisNode();
            subnode.setId(organization.getId());
            subnode.setCode(organization.getCode());
            subnode.setTitle(organization.getName());
            subnode.setName(organization.getName());
            subnode.setPid(organization.getParentId());
            nodes.add(subnode);
        }
        return R.ok(nodes);
    }

    @ApiOperation(value = "根据部门id查询部门信息", notes = "外部接口调用")
    @RequestMapping(value = "/getDeptById")
    public Object getDeptById(String id) {
        QueryWrapper<HumOrganization> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(HumOrganization::getId, id);
        return organizationService.getOne(wrapper);
    }

    @ApiOperation(value = "根据部门编码查询部门信息", notes = "外部接口调用")
    @RequestMapping(value = "/getDeptByCode")
    public Object getDeptByCode(String code) {
        QueryWrapper<HumOrganization> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(HumOrganization::getCode, code);
        return organizationService.getOne(wrapper);
    }

    @ApiOperation(value = "根据部门名称查询部门信息", notes = "外部接口调用")
    @RequestMapping(value = "/getDeptByName")
    public Object getDeptByName(String name) {
        QueryWrapper<HumOrganization> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(HumOrganization::getName, name);
        return organizationService.getOne(wrapper);
    }

    @ApiOperation(value = "获取部门树", notes = "外部接口调用")
    @RequestMapping("/getOrgTree")
    public Object getOrgTree() {
        return getOrgTreeById(null);
    }
}
