package com.asam.system.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.asam.common.base.controller.BaseController;
import com.asam.common.constant.Constant;
import com.asam.common.logUtil.ControllerLog;
import com.asam.common.logUtil.OpsLogType;
import com.asam.common.util.ObjectUtil;
import com.entity.R;
import com.asam.human.entity.UserVO;
import com.asam.system.entity.SysAccount;
import com.asam.system.entity.SysMoudle;
import com.asam.system.entity.SysRole;
import com.asam.system.entity.SysRoleMoudle;
import com.asam.system.service.AccountRoleService;
import com.asam.system.service.MoudleService;
import com.asam.system.service.RoleMoudleService;
import com.asam.system.service.RoleService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.page.JPAPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @Description: 角色管理
 * @Author: fyy
 * @Create: 2022-04-20
 */
@RestController
@Api(value = "RoleController", description = "角色管理")
@RequestMapping(value = "role")
public class RoleController extends BaseController {
    @Autowired
    private RoleService roleService;
    @Autowired
    private MoudleService moudleService;
    @Autowired
    private RoleMoudleService roleMoudleService;
    @Autowired
    private AccountRoleService accountRoleService;

    @ApiOperation(value = "获取分页列表")
    @RequestMapping(value = "/list")
    public Object list(JPAPage varBasePage, String name){
        String loginName=getCurLoginName();
        IPage<SysRole> page = roleService.findPageList(varBasePage, name,loginName);
        return R.ok(page);
    }

    @ControllerLog(type = OpsLogType.ADDORUPDATE, value = "角色管理")
    @ApiOperation(value = "新增或更新对象")
    @RequestMapping(value = "/save")
    public Object save(String data){
        if (ObjectUtil.isNotEmpty(data)) {
            SysRole model = JSON.parseObject(data, SysRole.class);
            model.setState(Constant.STATE_VALID);
            UserVO curUser=getCurUser();//当前创建人
            boolean b=roleService.save(model,curUser.getId(),curUser.getUserName());
            return b?R.ok(): R.error();
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "根据Id获取对象")
    @RequestMapping(value = "/findById")
    public Object findById(Long id){
        if (ObjectUtil.isNotEmpty(id)) {
            SysRole model=roleService.getById(id);
            return R.ok(model);
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "检验角色名称")
    @RequestMapping(value = "/checkRoleName")
    public Object checkRoleName(String rolename, Long id) {
        if (ObjectUtil.isNotEmpty(rolename)) {
            List<SysRole> list = roleService.findList(id, rolename,null,null,getCurLoginName());
            boolean b = Objects.isNull(list) || 0 == list.size();
            return R.ok(b);
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "检验角色编码")
    @RequestMapping(value = "/checkRoleCode")
    public Object checkRoleCode(String roleCode, Long id) {
        if (ObjectUtil.isNotEmpty(roleCode)) {
            List<SysRole> list = roleService.findList(id, null,roleCode,null,getCurLoginName());
            boolean b = Objects.isNull(list) || 0 == list.size();
            return R.ok(b);
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ControllerLog(type = OpsLogType.DELETE, value = "角色管理")
    @ApiOperation(value = "根据Id删除对象")
    @RequestMapping(value = "/delete")
    public Object delete(Long id) {
        if (ObjectUtil.isNotEmpty(id)) {
            SysRole model = roleService.getById(id);
            model.setState(Constant.STATE_INVALID);
            UserVO curUser = getCurUser();//获取当前登录人
            boolean b=roleService.save(model, curUser.getId(), curUser.getUserName());
            //删除角色模块关联表
            if(b) {
                roleMoudleService.deleteByRoleId(id,null);
            }
            return b? R.ok() : R.error();
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "获取对象列表")
    @RequestMapping(value = "/findAll")
    public Object findAll() {
        List<SysRole> list = roleService.findList(null,null,null,null,getCurLoginName());
        return R.ok(list);
    }

    @ApiOperation(value = "根据角色id和系统id查询角色和权限数据")
    @RequestMapping(value = "/findRoleMoudleById")
    public Object findRoleMoudleById(Long roleId,Long systemId) {
        SysRole role = roleService.getById(roleId);
        //把模块的所有子节点id选出来 存起来
        Set<Long> leafList = new HashSet<Long>();
        List<SysMoudle> mlist = moudleService.findList(systemId,null,null,1,null);
        Set<Long> parentList = new HashSet<Long>();
        if (mlist != null && mlist.size() > 0) {
            for (SysMoudle model : mlist) {
                parentList.add(model.getParentId());
            }
        }
        if (mlist != null && mlist.size() > 0) {
            for (SysMoudle model : mlist) {
                if (!parentList.contains(model.getId())) {
                    leafList.add(model.getId());
                }
            }
        }
        //根据角色找到当前模块 剔除父级模块
        List<SysRoleMoudle> sysRoleMoudles = roleMoudleService.findList(roleId,systemId,null);
        Set<Long> mouIds = new HashSet<Long>();
        if (sysRoleMoudles != null && sysRoleMoudles.size() > 0) {
            for (SysRoleMoudle rm : sysRoleMoudles) {
                Long moudleId=rm.getSysMoudleId();
                if (leafList.contains(moudleId)&&!mouIds.contains(moudleId)) {
                    mouIds.add(moudleId);
                }
            }
        }
        Map<String, Object> res = new HashMap<String, Object>(2);
        res.put("role", role);
        res.put("mouIds", mouIds);
        return R.ok(res);
    }

    @ControllerLog(type = OpsLogType.UPDATE, value = "角色权限管理")
    @ApiOperation(value = "给角色分配权限")
    @RequestMapping(value = "/assignAuthorityToRole")
    public Object assignAuthorityToRole(Long roleId, String moudleId,Long systemId) {
        boolean b=true;
        SysRole role = roleService.getById(roleId);
        List<SysRoleMoudle> rms = roleMoudleService.findList(roleId,systemId,null);
        if (rms != null && rms.size() > 0) {
            roleMoudleService.deleteByRoleId(roleId, systemId);
        }
        if(b){
            //获取选择的所有的模块信息，是一个以，隔开的字符串
            JSONArray mids = JSONArray.parseArray(moudleId);
            List<SysRoleMoudle> sysRoleMoudleList = new ArrayList<SysRoleMoudle>();
            if (mids.size() > 0) {
                int count = mids.size();
                for (int i = 0; i < count; i++) {
                    SysRoleMoudle rm = new SysRoleMoudle();
                    rm.setSysMoudleId(mids.getLong(i));
                    rm.setSysRoleId(role.getId());
                    rm.setSystemId(systemId);
                    sysRoleMoudleList.add(rm);
                }
                if (sysRoleMoudleList.size() > 0) {
                    roleMoudleService.insertAll(sysRoleMoudleList);
                }
            }else{
                b=false;
            }
        }
        return b? R.ok() : R.error();
    }

    @ApiOperation(value = "根据登录名查询角色信息", notes = "外部接口调用")
    @RequestMapping(value = "/findRolesByLoginName")
    public Object findRolesByLoginName(String loginName) {
        SysAccount currentAccount =getCurAccount();
        List<Long> roleIds=accountRoleService.getRoleIdsByAccountId(currentAccount.getId());
        List<SysRole> roleList= roleService.findList(null,null,null,roleIds,getCurLoginName());
        return R.ok(roleList);
    }
}
