package com.asam.system.controller;

import com.alibaba.fastjson.JSONArray;
import com.asam.common.base.controller.BaseController;
import com.asam.common.logUtil.ControllerLog;
import com.asam.common.logUtil.OpsLogType;
import com.asam.common.util.ObjectUtil;
import com.entity.R;
import com.asam.human.entity.HumUserOrg;
import com.asam.human.entity.UserVO;
import com.asam.human.service.UserOrgService;
import com.asam.system.entity.SysAccount;
import com.asam.system.entity.SysAccountRole;
import com.asam.system.service.AccountRoleService;
import com.asam.system.service.AccountService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.page.JPAPage;
import com.utils.tools.Md5;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.*;

/**
 * @Description: 账户管理
 * @Author: fyy
 * @Create: 2022-04-20
 */
@RestController
@Api(value = "AccountController", description = "账户管理")
@RequestMapping(value = "/account")
public class AccountController extends BaseController {

    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountRoleService accountRoleService;
    @Autowired
    private UserOrgService userOrgService;

    @ApiOperation(value = "获取分页列表")
    @RequestMapping(value = "/list")
    public Object list(JPAPage varBasePage,String loginName, String userName){
        IPage<SysAccount> page = accountService.findPageList(varBasePage, userName,getCurLoginName(),loginName);
        return R.ok(page);
    }

    @ControllerLog(type = OpsLogType.RESET, value = "账户管理")
    @ApiOperation(value = "重置密码")
    @RequestMapping(value = "/resetPwd")
    public Object resetPwd(Long accountId ) {
        if (Objects.nonNull(accountId)) {
            SysAccount sysAccount = accountService.getById(accountId);
            if (Objects.nonNull(sysAccount)) {
                sysAccount.setPassword(Md5.toMD5("111111"));
                UserVO curUser=getCurUser();//当前创建人
                return accountService.save(sysAccount, curUser.getId(), curUser.getUserName())?R.ok(): R.error();
            }else {
                return R.error("账户不存在！");
            }
        }else {
            return R.error("前端传递参数为空");
        }
    }

    @ControllerLog(type = OpsLogType.UPDATE, value = "账户管理")
    @ApiOperation(value = "修改密码")
    @RequestMapping(value = "/changePwd")
    public Object changePwd(String accountId, String oldPwd, String newPwd ) {
        Map<String, Object> map = new HashMap<String, Object>(1);
        if (StringUtils.isNotBlank(accountId)) {
            Long accountid = Long.parseLong(accountId);
            SysAccount oldSysAccount = accountService.getById(accountid);
            if (!oldSysAccount.getPassword().equalsIgnoreCase(Md5.toMD5(oldPwd))) {
                return R.error("原始密码不正确！");
            } else {
                oldSysAccount.setPassword(Md5.toMD5(newPwd));
                oldSysAccount.setUpdatePasswordTime(new Timestamp(System.currentTimeMillis()));
                UserVO user = getCurUser();
                return R.ok(accountService.save(oldSysAccount, user.getId(), user.getUserName()));
            }
        } else {
            return R.error("前台传递信息为空，请检查！");
        }
    }

    @ControllerLog(type = OpsLogType.SET, value = "账户管理")
    @ApiOperation(value = "设置用户状态")
    @RequestMapping(value = "/setUserStatus")
    public Object setUserStatus(Long id, boolean flag) {
        if (Objects.nonNull(id) && Objects.nonNull(flag)) {
            SysAccount sysAccount = accountService.getById(id);
            if (Objects.nonNull(sysAccount)) {
                sysAccount.setState(flag == true ? 1 : 2);
                UserVO curUser=getCurUser();//当前创建人
                return accountService.save(sysAccount, curUser.getId(), curUser.getUserName())?R.ok(): R.error();
            }
            return R.ok();
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "根据账户查询角色信息")
    @RequestMapping(value = "/findRolesByAccountId")
    public Object findRolesByAccountId(Long id) {
        if (ObjectUtil.isNotEmpty(id)) {
            List<Long> roleIdList=accountRoleService.getRoleIdsByAccountId(id);
            return R.ok(roleIdList);
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ControllerLog(type = OpsLogType.UPDATE, value = "账户角色管理")
    @ApiOperation(value = "根据账户修改角色信息")
    @RequestMapping(value = "/assignRolesByAccountId")
    public Object assignRolesByAccountId(Long accountId, String roleIds ) {
        if (Objects.nonNull(accountId) && StringUtils.isNotEmpty(roleIds)) {
            //先删除原来的
            accountRoleService.deleteByAccountId(accountId);
            //添加新的
            JSONArray rids = JSONArray.parseArray(roleIds);
            List<SysAccountRole> arList = new ArrayList<SysAccountRole>();
            if (rids != null && rids.size() > 0) {
                int count = rids.size();
                for (int i = 0; i < count; i++) {
                    SysAccountRole ar = new SysAccountRole();
                    ar.setSysAccountId(accountId);
                    ar.setSysRoleId(rids.getLong(i));
                    arList.add(ar);
                }
                if (arList != null && arList.size() > 0) {
                    accountRoleService.insertAll(arList);
                }
            }
            return R.ok();
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "当前登录人员信息")
    @RequestMapping(value = "/getUserInfo")
    public Object getUserInfo() {
        UserVO user=getCurUser();
        return R.ok(user);
    }

    @ApiOperation(value = "当前登录账户")
    @RequestMapping(value = "/getAccountInfo")
    public Object getAccountInfo() {
        SysAccount sysAccount =getCurAccount();
        return R.ok(sysAccount);
    }

    @ApiOperation(value = "当前登录人管理的部门信息")
    @RequestMapping(value = "/getCurOrgs")
    public Object getCurOrgIds(){
        UserVO user=getCurUser();
        List<Long> orgIds =null;
        List<HumUserOrg> srList= userOrgService.findList(user.getId(), null);
        if(srList!=null&&srList.size()>0) {
            orgIds=new ArrayList<>();
            for(HumUserOrg s:srList){
                if(!orgIds.contains(s.getId())){
                    orgIds.add(s.getId());
                }
            }
        }
        return R.ok(orgIds);
    }

  /*  @RequestMapping(value = "/createAccount")
    public Object createAccount(String userObj,String curUser ) {
        UserVO user = JSON.parseObject(userObj, UserVO.class);
        Map<String, Object> result = new HashMap<String, Object>(1);
        SysAccount sysAccount = new SysAccount();
        sysAccount.setUserName(user.getUserName());
        //用户的手机号码作为登录账号
        sysAccount.setUserId(user.getId());
        //Md5加密初始密码
        sysAccount.setPassword(Md5.toMD5(Md5.toMD5("123456")));
        sysAccount.setState(STATE_VALID);
        //启用账户信息
        //获取当前登录人
        UserVO curUserVO = JSON.parseObject(curUser, UserVO.class);
        accountService.saveAccount(sysAccount, curUserVO.getId(), curUserVO.getUserName());
        result.put(RESULT_SET, user);
        result.put(STATUS_CODE, SUCCESS);
        return result;
    }

    @RequestMapping(value = "/updateAccount")
    public Object updateAccount(String userObj,String curUser, Long userId) {
        UserVO user = JSON.parseObject(userObj, UserVO.class);
        Map<String, Object> result = new HashMap<String, Object>(1);
        SysAccount sysAccount = accountService.findByLoginName(user.getId());
        if(Objects.nonNull(sysAccount)){
            //用户的手机号码作为登录账号
            sysAccount.setUserId(user.getId());
            sysAccount.setUserName(user.getUserName());
            sysAccount.setState(STATE_VALID);
            //启用账户信息
            //获取当前登录人
            UserVO curUserVO = JSON.parseObject(curUser, UserVO.class);
            accountService.saveAccount(sysAccount, curUserVO.getId(), curUserVO.getUserName());
            result.put(RESULT_SET, user);
            result.put(STATUS_CODE, SUCCESS);
        }else{
            result.put(STATUS_CODE, ERROR);
        }
        return result;
    }*/
/*
    @RequestMapping(value = "/getUserIdsByRoleCode")
    public Object getUserIdsByRoleCode(String roleCode) {
        List<String> userIds = accountService.getUserIdsByRoleCode(roleCode);
        return userIds;
    }*/
/*
    @ApiOperation(value = "删除账户信息")
    @RequestMapping(value = "/deleteAccount")
    public Object deleteAccount(String userPhone ) {
        Map<String, Object> result = new HashMap<>(1);
        HumUser humUser =  humUserService.getUserByLoginName(userPhone);
        SysAccount sysAccount = accountService.findByLoginName(humUser.getId());
        if(Objects.nonNull(sysAccount)){
            sysAccount.setState(0);
            HumUser user = getCurUser();
            accountService.saveAccount(sysAccount,user.getId(), user.getUserName());
            result.put(STATUS_CODE, SUCCESS);
        }else{
            result.put(STATUS_CODE, ERROR);
        }
        return result;
    }*/

    @ControllerLog(type = OpsLogType.UPDATE, value = "账户管理")
    @ApiOperation(value = "给该角色分配账户信息")
    @RequestMapping(value = "/assignAccountToRole")
    public Object assignAccountToRole(Long roleId,String accountIds) {
        if (Objects.nonNull(roleId) && StringUtils.isNotEmpty(accountIds)) {
            //删除原有的账户信息
            accountRoleService.deleteByRoleId(roleId);
            // 获取所有角色中选中的账户集合
            JSONArray aids = JSONArray.parseArray(accountIds);
            List<SysAccountRole> arList = new ArrayList<SysAccountRole>();
            if (aids != null && aids.size() > 0) {
                int count = aids.size();
                for (int i = 0; i < count; i++) {
                    SysAccountRole ar = new SysAccountRole();
                    ar.setSysRoleId(roleId);
                    ar.setSysAccountId(aids.getLong(i));
                    arList.add(ar);
                }
                if (arList != null && arList.size() > 0) {
                    accountRoleService.insertAll(arList);
                }
            }
            return R.ok();
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "获取所有账户信息")
    @RequestMapping(value = "/findAccountsList")
    public Object findAccountsList() {
        List<SysAccount> list = accountService.findList(null,getCurLoginName(),null);
        return R.ok(list);
    }

    @ApiOperation(value = "根据角色id查找所有账户信息")
    @RequestMapping(value = "/getAccountsByRoleId")
    public Object getAccountsByRoleId(Long roleId) {
        List<Long> list= accountRoleService.getAccountsByRoleId(roleId);
        return R.ok(list);
    }

    @ApiOperation(value = "根据角色查询账户", notes = "外部接口调用")
    @RequestMapping(value = "/findAccountsByRoleId")
    public Object findAccountsByRoleId(@RequestBody Long roleId) {
        List<Long> idlist= accountRoleService.getAccountsByRoleId(roleId);
        List<SysAccount> accountList=accountService.findList(idlist,getCurLoginName(),null);
        return R.ok(accountList);
    }

    @ApiOperation(value = "获取当前登录人的密码修改时间")
    @RequestMapping(value = "/isPwdOverdue")
    private Object isPwdOverdue(){
        Map<String, Object> result = new HashMap<>(2);
        SysAccount sysAccount = getCurAccount();
        //密码已超过30天未修改
        long monthMills = 3600L * 24L * 30L * 1000L;
        long now = System.currentTimeMillis();
        if(ObjectUtils.isNotEmpty(sysAccount.getUpdatePasswordTime())) {
            if((now - sysAccount.getUpdatePasswordTime().getTime()) > monthMills) {
                result.put("overdue", true);
            }
        } else {
            if(ObjectUtils.isNotEmpty(sysAccount.getCreateTime())) {
                if((now - sysAccount.getCreateTime().getTime()) > monthMills) {
                    result.put("overdue", true);
                }
            }
        }
        return result;
    }
}
