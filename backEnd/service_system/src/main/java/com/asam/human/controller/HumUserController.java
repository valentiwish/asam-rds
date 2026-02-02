package com.asam.human.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.asam.common.base.controller.BaseController;
import com.asam.common.base.model.BasisNode;
import com.asam.common.constant.Constant;
import com.asam.common.logUtil.ControllerLog;
import com.asam.common.logUtil.OpsLogType;
import com.asam.common.util.ObjectUtil;
import com.asam.common.util.QrCodeUtil;
import com.asam.human.entity.HumOrganization;
import com.asam.human.entity.HumUser;
import com.asam.human.entity.HumUserOrg;
import com.asam.human.entity.UserVO;
import com.asam.human.service.HumOrganizationService;
import com.asam.human.service.HumUserService;
import com.asam.human.service.UserOrgService;
import com.asam.system.entity.SysAccount;
import com.asam.system.service.AccountRoleService;
import com.asam.system.service.AccountService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.entity.R;
import com.page.JPAPage;
import com.utils.tools.Md5;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

/**
 * @Description：员工管理
 * @Author: fyy
 * @Create: 2022-04-20
 */
@RestController
@RequestMapping("/human")
@Slf4j
public class HumUserController extends BaseController {

    @Autowired
    private HumUserService userService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private HumOrganizationService organizationService;
    @Autowired
    private AccountRoleService accountRoleService;
    @Autowired
    private UserOrgService userOrgService;

    @ApiOperation(value = "获取分页列表")
    @RequestMapping("/list")
    public Object list(JPAPage varBasePage, String name, String jobNumber, String sex, String orgId) {
        List<String> orgIds=null;
        if(ObjectUtil.isNotEmpty(orgId)){
            orgIds=organizationService.getOrganizationIdsByPid(orgId);
        }
        IPage<HumUser> page = userService.findPageList(varBasePage, name,jobNumber,sex,orgIds);
        return R.ok(page);
    }

    @ControllerLog(type = OpsLogType.ADDORUPDATE, value = "员工管理")
    @ApiOperation(value = "新增或更新对象")
    @RequestMapping("/save")
    public Object save(String data) {
        if (ObjectUtil.isNotEmpty(data)) {
            HumUser model = JSON.parseObject(data, HumUser.class);
            // 保存外链接
            if (ObjectUtil.isNotEmpty(model.getOrgId())) {
                HumOrganization organization = organizationService.getById(model.getOrgId());
                if (organization != null) {
                    model.setOrgCode(organization.getCode());
                    model.setOrgName(organization.getName());
                }
            } else {
                model.setOrgCode(null);
                model.setOrgName(null);
            }
            model.setState(Constant.STATE_VALID);
            UserVO curUser = getCurUser();//获取当前登录人
            boolean flag = userService.save(model, curUser.getId(), curUser.getUserName());
            //修改关联账户信息
            if (flag&&model.getIsAccount()!=null && model.getIsAccount() == 1) {//增加或者修改账户
                SysAccount account = accountService.findModel(model.getId());
                if (account == null) {
                    account = new SysAccount();
                    account.setPassword(Md5.toMD5("111111"));
                    account.setUserId(model.getId());
                    account.setLoginName(model.getJobNumber());
                    account.setUserName(model.getUserName());
                    account.setState(Constant.STATE_VALID);
                } else {
                    account.setLoginName(model.getJobNumber());
                    account.setUserName(model.getUserName());
                }
                flag = accountService.save(account, curUser.getId(), curUser.getUserName());
            } else {//删除账户
                SysAccount account = accountService.findModel(model.getId());
                if (account != null) {
                    flag = deleteAccount(account.getId(), curUser.getId(), curUser.getUserName());
                }
            }
            return flag ? R.ok() : R.error();
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ControllerLog(type = OpsLogType.DELETE, value = "员工管理")
    @ApiOperation(value = "根据Id删除对象")
    @RequestMapping(value = "/delete")
    public Object delete(Long id){
        if (ObjectUtil.isNotEmpty(id)) {
            HumUser model = userService.getById(id);
            model.setState(Constant.STATE_INVALID);
            UserVO curUser = getCurUser();//获取当前登录人
            boolean flag =  userService.save(model, curUser.getId(), curUser.getUserName());
            //删除账户
            if(flag){
                SysAccount account = accountService.findModel(model.getId());
                if (account != null) {
                    flag= deleteAccount(account.getId(),curUser.getId(), curUser.getUserName());
                }
            }
            return flag ? R.ok() : R.error();
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "根据Id获取对象")
    @RequestMapping(value = "/findById")
    public Object findById(String id) {
        HumUser model=userService.getById(id);
        return R.ok(model);
    }

    @ApiOperation(value = "校验对象")
    @RequestMapping(value = "/check")//各类参数的校验
    public Object check(String loginName,String jobNumber,String userPhone,String cardId,String email,String id){
        List<HumUser> list = userService.findList(id,loginName,jobNumber,userPhone,cardId,email,null,null,null);
        boolean b=Objects.isNull(list) || 0 == list.size();
        return R.ok(b);
    }

    @ApiOperation(value = "获取人员树")
    @RequestMapping(value = "/getTree")
    public  Object getTree(String orgId) {
        return getUserTree(orgId);
    }

    @ApiOperation(value = "获取人员树")
    @RequestMapping(value = "/getTreeByOrgId")
    public  Object getTreeByOrgId(String orgId) {
        return getUserTree(orgId);
    }

    @ApiOperation(value = "获取人员树")
    @RequestMapping(value = "/getUserTree")
    public  Object getUserTree(String orgId) {
        List<BasisNode> nodes = new ArrayList<>();
        List<String> orgIds=null;
        if (ObjectUtil.isNotEmpty(orgId)) {
            orgIds = organizationService.getOrganizationIdsByPid(orgId);
        }
        List<HumOrganization> organizationList = organizationService.findList(null,null,null,orgIds);
        if (organizationList != null&&organizationList.size()>0) {
            for (HumOrganization organization : organizationList) {
                BasisNode modnode = new BasisNode();
                modnode.setId(organization.getId());
                modnode.setTitle(organization.getName());
                modnode.setName(organization.getName());
                modnode.setPid(organization.getParentId());
                modnode.setUrl("org");
                nodes.add(modnode);
            }
        }
        List<HumUser> userlist = userService.findList(null,null,null,null,null,null,null,orgIds,null);
        if (userlist != null&&userlist.size()>0) {
            for (HumUser user : userlist) {
                BasisNode modnode = new BasisNode();
                modnode.setId(user.getId().toString());
                modnode.setTitle(user.getUserName());
                modnode.setName(user.getUserName());
                if (user.getOrgId() != null) {
                    modnode.setPid(user.getOrgId());
                }
                modnode.setUrl("user");
                nodes.add(modnode);
            }
        }
        return R.ok(nodes);
    }

    @ApiOperation(value = "获取人员树,把没有人员的部门")
    @RequestMapping(value = "/getUserTree1")
    public  Object getUserTree1(String orgId) {
        List<BasisNode> nodes = new ArrayList<>();
        List<String> orgIds=null;
        if (ObjectUtil.isNotEmpty(orgId)) {
            orgIds = organizationService.getOrganizationIdsByPid(orgId);
        }
        List<HumOrganization> organizationList = organizationService.findList(null,null,null,orgIds);
        //子父级部门关系字典
        Map<String,String> orgMap=new HashMap<>();
        if (organizationList != null&&organizationList.size()>0) {
            for (HumOrganization organization : organizationList) {
                orgMap.put(organization.getId(),organization.getParentId());
            }
        }
        //人员信息
        List<String> orgIdList=new ArrayList<>();//把人员所属部门id全部存起来
        List<HumUser> userlist = userService.findList(null,null,null,null,null,null,null,orgIds,null);
        if (userlist != null&&userlist.size()>0) {
            for (HumUser user : userlist) {
                BasisNode modnode = new BasisNode();
                modnode.setId(user.getId().toString());
                modnode.setTitle(user.getUserName());
                modnode.setName(user.getUserName());
                String userorgId=user.getOrgId();
                if (userorgId!= null) {
                    modnode.setPid(userorgId);
                    if(!orgIdList.contains(userorgId)){
                        orgIdList.add(userorgId);
                    }
                }
                modnode.setUrl("user");
                nodes.add(modnode);
            }
        }
        //获取人员中的部门的所有父级部门集合 包含自身
        List<String> neworgIdList=new ArrayList<>();//把人员所属部门id全部存起来
        if(orgIdList.size()>0){
            for(String orgIdStr:orgIdList){
                List<String> idList = new ArrayList<>();//初始化id集合
                idList.add(orgIdStr);
                List<String> ids=organizationService.getpid(idList,orgMap.get(orgIdStr),organizationList);
                for(String id:ids){
                    if(!neworgIdList.contains(id)){
                        neworgIdList.add(id);
                    }
                }
            }
        }
        //重新查找部门的节点树
        if (organizationList != null&&organizationList.size()>0) {
            for (HumOrganization organization : organizationList) {
              if(neworgIdList.contains(organization.getId())){
                  BasisNode modnode = new BasisNode();
                  modnode.setId(organization.getId());
                  modnode.setTitle(organization.getName());
                  modnode.setName(organization.getName());
                  modnode.setPid(organization.getParentId());
                  modnode.setUrl("org");
                  nodes.add(modnode);
              }
            }
        }
        return R.ok(nodes);
    }

    @ApiOperation(value = "查找对应人员的部门权限")
    @RequestMapping(value = "/findUserOrgById")
    public Object findUserOrgById(Long userId) {
        HumUser user = userService.getById(userId);
        //把模块的所有子节点id选出来 存起来
        List<HumUserOrg> roleOrgs = userOrgService.findList(userId,null);
        Set<String> mouIds = new HashSet<String>();
        if (roleOrgs != null && roleOrgs.size() > 0) {
            for (HumUserOrg rm : roleOrgs) {
                String orgId=rm.getSysOrgId();
                if (!mouIds.contains(orgId)) {
                    mouIds.add(orgId);
                }
            }
        }
        Map<String, Object> res = new HashMap<String, Object>(2);
        res.put("user", user);
        res.put("mouIds", mouIds);
        return R.ok(res);
    }

    @ControllerLog(type = OpsLogType.UPDATE, value = "角色部门管理")
    @ApiOperation(value = "分配权限保存部门")
    @RequestMapping(value = "/assignOrgToUser")
    public Object assignOrgToUser(Long userId, String orgId) {
        boolean b=true;
        HumUser user = userService.getById(userId);
        List<HumUserOrg> rms = userOrgService.findList(userId,null);
        if (rms != null && rms.size() > 0) {
            userOrgService.deleteByUserId(userId);
            rms=null;
        }
        if(b){
            //获取选择的所有的模块信息，是一个以，隔开的字符串
            JSONArray mids = JSONArray.parseArray(orgId);
            List<HumUserOrg> roleOrgList = new ArrayList<HumUserOrg>();
            if (mids.size() > 0) {
                int count = mids.size();
                for (int i = 0; i < count; i++) {
                    HumUserOrg rm = new HumUserOrg();
                    rm.setSysOrgId(mids.getString(i));
                    rm.setSysUserId(user.getId());
                    roleOrgList.add(rm);
                }
                if (roleOrgList.size() > 0) {
                    userOrgService.insertAll(roleOrgList);
                }
            }else{
                b=false;
            }
        }
        return b? R.ok() : R.error();
    }

    //删除账户小方法
    // @ControllerLog(type = OpsLogType.ADDORUPDATE, value = "账户管理")
    private boolean deleteAccount(Long accountId,Long userId,String userName) {
        boolean flag =true;
        if (Objects.nonNull(accountId)) {
            SysAccount account = accountService.getById(accountId);
            if (Objects.nonNull(account)) {
                account.setState(0);
                flag= accountService.save(account, userId,userName);
                //删除账户的角色关联表
                if(flag) {
                   // accountRoleService.deleteByAccountId(accountId);
                }
            }
        }
        return flag;
    }

    @ApiOperation(value = "根据登录名查询人员信息", notes = "外部接口调用")
    @RequestMapping(value = "/getUserVOByLoginName")
    public UserVO getUserVOByLoginName(String loginName){
        return getCurUser();
    }

    @ApiOperation(value = "根据班组查询人员信息", notes = "外部接口调用")
    @RequestMapping(value = "/findByTeamId2")
    public Object findByTeamId2(String teamId) {
        QueryWrapper<HumUser> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(HumUser::getTeamId, teamId);
        return userService.list(wrapper);
    }

    /**
     *
     * @Description: 根据班组ID获取人员列表
     * @author zhangqi
     * @date 2022-6-21
     * @return HumUser
     */
    @PostMapping(value = "/findByTeamId")
    public Object findByTeamId(String teamId,String userName) {
        QueryWrapper<HumUser> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(HumUser::getTeamId, teamId);
        if(ObjectUtils.isNotEmpty(userName)){
            wrapper.lambda().like(HumUser::getUserName, userName);
        }
        return R.ok(userService.list(wrapper));
    }

    @ApiOperation(value = "根据当前登录人查询所管辖的部门权限", notes = "外部接口调用")
    @RequestMapping(value = "/findOrgIdListByByLoginName")
    public Object findOrgIdListByByLoginName() {
        UserVO user=getCurUser();
        List<String> orgIdList=new ArrayList<>();
        //先查找当前的部门
        if(user!=null&&user.getOrgId()!=null){
            orgIdList.add(user.getOrgId());
        }
        //在查找管辖的数据权限下部门
        List<HumUserOrg> rms = userOrgService.findList(user.getId(),null);
        if(ObjectUtil.isNotEmpty(rms)){
            for (HumUserOrg uo:rms) {
                String orgId=uo.getSysOrgId();
                if(!orgIdList.contains(orgId)){
                    orgIdList.add(orgId);
                }
            }
        }
        return R.ok(orgIdList);
    }

    @ApiOperation(value = "打印二维码", notes = "生产准备")
    @RequestMapping(value = "/getQrCode")
    public void getQrCode (HttpServletResponse response, String code) {
        if (Objects.nonNull(code)) {
            try {
                InputStream input = QrCodeUtil.getQrCodeImage(code);
                OutputStream osOut = response.getOutputStream();
                byte[] buf = new byte[1024];
                int bytesRead;
                while ((bytesRead = input.read(buf)) > 0) {
                    osOut.write(buf, 0, bytesRead);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @ApiOperation(value = "自动生成编码")
    @RequestMapping(value = "/printUniqueCode")
    public Object printUniqueCode(String userId) {
        HumUser humUser = userService.getById(userId);
        if (ObjectUtils.isNotEmpty(humUser)) {
            if (ObjectUtils.isEmpty(humUser.getUniqueCode())) {
                humUser.setUniqueCode(UUID.randomUUID().toString());
                userService.saveOrUpdate(humUser);
            }
        }
        return R.ok(humUser);
    }

    @ApiOperation(value = "获取当前用户信息")
    @PostMapping(value = "/getUserInfo")
    public Object getUserInfo(){
        return R.ok(getCurUser());
    }

    @ApiOperation(value = "获取班组人员")
    @PostMapping("/listTeam")
    public Object listTeam(Integer currentPage, Integer pageSize, String userName, String orgId, int teamFlag, String teamCode) {
        // 需要在Config配置类中配置分页插件
        currentPage = ObjectUtil.isNotEmpty(currentPage) ? currentPage : 1;
        pageSize = ObjectUtil.isNotEmpty(pageSize) ? pageSize : 15;
        QueryWrapper<HumUser> wrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(userName)){
            wrapper.lambda().like(HumUser::getUserName, userName);
        }
        if (StringUtils.isNotBlank(orgId)){
            wrapper.lambda().eq(HumUser::getOrgId, orgId);
        }
        if (teamFlag == 1){
            if (StringUtils.isNotBlank(teamCode)){
                wrapper.lambda().eq(HumUser::getTeamCode, teamCode);
            }
        }else if(teamFlag == 0){
            wrapper.lambda().isNull(HumUser::getTeamCode).or().eq(HumUser::getTeamCode, "");
        }
        wrapper.lambda().eq(HumUser::getState, 1);
        wrapper.lambda().orderByDesc(HumUser::getCreateTime);
        IPage<HumUser> page = new Page<>();
        page.setCurrent(currentPage);
        page.setSize(pageSize);
        return R.ok(userService.page(page, wrapper));
    }
    /**
     *
     * @Description: 批量更新人员的所属班组信息
     * @author zhangqi
     * @date 2022-6-21
     * @return boolean
     */
    @ControllerLog(value = "批量更新用户班组")
    @PostMapping("/batchUpdateTeam")
    public Object batchUpdateTeam(String data, String teamId, String teamCode, String teamName) {
        if(ObjectUtil.isNotEmpty(data)) {
            List<HumUser> lists = JSON.parseArray(data, HumUser.class);
            for(HumUser user : lists){
                if(ObjectUtil.isNotEmpty(teamId)) {
                    user.setTeamId(teamId);
                    user.setTeamCode(teamCode);
                    user.setTeamName(teamName);
                    user.setMonitorMark(0);
                    userService.updateById(user);
                }
            }
            return R.ok();
        }else{
            return R.error();
        }
    }
}