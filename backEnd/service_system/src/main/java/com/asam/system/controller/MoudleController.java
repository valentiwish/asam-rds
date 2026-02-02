package com.asam.system.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.asam.common.base.controller.BaseController;
import com.asam.common.base.model.MenuNode;
import com.asam.common.constant.Constant;
import com.asam.common.logUtil.ControllerLog;
import com.asam.common.logUtil.OpsLogType;
import com.asam.common.util.ObjectUtil;
import com.entity.R;
import com.asam.human.entity.UserVO;
import com.asam.system.entity.SysAccount;
import com.asam.system.entity.SysMoudle;
import com.asam.system.entity.SysRoleMoudle;
import com.asam.system.entity.SysSubsystem;
import com.asam.system.service.AccountRoleService;
import com.asam.system.service.MoudleService;
import com.asam.system.service.RoleMoudleService;
import com.asam.system.service.SystemInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

import static com.asam.common.constant.Constant.*;

/**
 * @Description: 账户管理
 * @Author: fyy
 * @Create: 2022-04-20
 */
@RestController
@Api(value = "MoudleController", description = "模块管理")
@RequestMapping(value = "/moudle")
public class MoudleController extends BaseController {

    @Autowired
    private MoudleService moudleService;
    @Autowired
    private AccountRoleService accountRoleService;
    @Autowired
    private RoleMoudleService roleMoudleService;
    @Autowired
    private SystemInfoService systemInfoService;


    @ApiOperation(value = "获取模块树与列表页")
    @RequestMapping(value = "/getMoudleData")
    public Object getMoudleData(Long systemId) {
        if (ObjectUtil.isNotEmpty(systemId)) {
            List<SysMoudle> moudles = moudleService.findList(systemId, null, null, null, null);
            //给按钮元素组装字典
            Map<Long, List<String>> allowMap = new HashMap<>();//父级id,按钮元素的code集合
            for (SysMoudle sysMoudle : moudles) {
                if (sysMoudle.getIsOperation() == 1) {
                    Long moudleId = sysMoudle.getParentId();
                    String code = sysMoudle.getCode();
                    if (allowMap.containsKey(moudleId)) {
                        List<String> list1 = allowMap.get(moudleId);
                        if (!list1.contains(code)) {
                            list1.add(code);
                        }
                    } else {
                        List<String> list = new ArrayList<String>(1);
                        list.add(code);
                        allowMap.put(moudleId, list);
                    }
                }
            }
            //封装模块数据组列表
            List<Map<String, Object>> tree = new ArrayList<>();
            Map<String, Object> moudleTree = null;
            Iterator<SysMoudle> it = moudles.iterator();
            while (it.hasNext()) {
                SysMoudle moudle = it.next();
                if (moudle.getIsOperation() == 0 && moudle.getParentId() != null) {
                    moudleTree = new HashMap<>(3);
                    moudleTree.put("open", true);
                    moudleTree.put("id", moudle.getId());
                    moudleTree.put("name", moudle.getName());
                    moudleTree.put("title", moudle.getName());
                    moudleTree.put("pid", moudle.getParentId());
                    if (allowMap.containsKey(moudle.getId())) {
                        moudleTree.put("allow", allowMap.get(moudle.getId()));
                    }
                    tree.add(moudleTree);
                } else {
                    it.remove();
                }
            }
            Map<String, Object> map = new HashMap<String, Object>(3);
            map.put(STATUS_CODE, SUCCESS);
            map.put(RESULT_SET, moudles);
            map.put("moudleTree", tree);
            return R.ok(map);
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "根据Id获取对象")
    @RequestMapping(value = "/findById")
    public Object findById(Long id) {
        if (ObjectUtil.isNotEmpty(id)) {
            Map<String, Object> map = new HashMap<String, Object>(3);
            SysMoudle moudle = moudleService.getById(id);
            //所属操作按钮
            List<Map<String, Object>> operations = new ArrayList<Map<String, Object>>();
            //构造资源
            List<SysMoudle> moudles = moudleService.findList(null,moudle.getId(),null, null, null);
            if (moudles != null && moudles.size() > 0) {
                for (SysMoudle m : moudles) {
                    if (m.getIsOperation() == 1) {
                        Map<String, Object> oper = new HashMap<String, Object>(3);
                        oper.put("code", m.getCode());
                        //oper.put("url", m.getActionUrl());
                        operations.add(oper);
                    }
                }
            }
            //构造返回数据
            Map<String, Object> data = new HashMap<String, Object>(3);
            data.put("moudle", moudle);
            data.put("operations", operations);
            return R.ok(data);
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ControllerLog(type = OpsLogType.ADD, value = "模块管理")
    @ApiOperation(value = "新增对象")
    @RequestMapping(value = "/save")
    public Object save(String data, String parent, String menu) {
        SysMoudle moudle = JSON.parseObject(data, SysMoudle.class);
        if (Objects.nonNull(data)) {
            JSONArray arr = JSONArray.parseArray(parent);
            Object[] obj = arr.toArray();
            if (obj.length != 0) {
                moudle.setParentId(Long.parseLong(obj[obj.length - 1].toString()));
            }else{
                moudle.setParentId(new Long(1));
            }
            //保存外链接
            if(moudle.getParentId()!=null) {
                SysMoudle parentComp = moudleService.getById(moudle.getParentId());
                if (parentComp != null) {
                    moudle.setParentName(parentComp.getName());
                }
            }else{
                moudle.setParentName(null);
            }
            moudle.setIsOperation(0);
            moudle.setState(1);
            UserVO curUser = getCurUser();//获取当前登录人
            moudleService.save(moudle,curUser.getId(), curUser.getUserName());
            //保存模块对应的按钮
            if (menu != null && menu.length() > 0) {
                List<Map<String, String>> list = JSON.parseObject(menu, new TypeReference<List<Map<String, String>>>() {});
                for (Map<String, String> m : list) {
                    saveMenuSingle(moudle, m,curUser.getId(),curUser.getUserName());
                }
            }
            return R.ok();
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ControllerLog(type = OpsLogType.UPDATE, value = "模块管理")
    @ApiOperation(value = "更新对象")
    @RequestMapping(value = "/update")
    public Object update(String data, String id, String parent, String menu) {
        Long systemId = null;
        if (StringUtils.isNotBlank(data)) {
            SysMoudle moudle = JSON.parseObject(data, SysMoudle.class);
            systemId = moudle.getSystemId();
            JSONArray arr = JSONArray.parseArray(parent);
            Object[] obj = arr.toArray();
            if (obj.length != 0) {
                moudle.setParentId(Long.parseLong(obj[obj.length - 1].toString()));
            }else{
                moudle.setParentId(new Long(1));
            }
            //保存外链接
            if(moudle.getParentId()!=null) {
                SysMoudle parentComp = moudleService.getById(moudle.getParentId());
                if (parentComp != null) {
                    moudle.setParentName(parentComp.getName());
                }
            }else{
                moudle.setParentName(null);
            }
            moudle.setId(Long.parseLong(id));
            moudle.setState(1);
            moudle.setIsOperation(0);
            UserVO curUser = getCurUser();//获取当前登录人
            moudleService.save(moudle,curUser.getId(),curUser.getUserName());
            //引用实体更新子集
            List<SysMoudle> moudles = moudleService.getByPid(moudle.getId());
            Iterator<SysMoudle> ity = moudles.iterator();
            while(ity.hasNext()) {
                SysMoudle m = ity.next();
                if( m.getParentId().equals(moudle.getId())) {
                    m.setParentName(moudle.getName());
                }
                m.setSystemId(moudle.getSystemId());
                moudleService.save(m,curUser.getId(),curUser.getUserName());
                if(m.getIsOperation()==0||!m.getParentId().equals(moudle.getId())){//只剩紧随下一级的按钮
                    ity.remove();
                }
            }
            //剩余的 子集的按钮
            if (menu != null && menu.length() > 0) {
                List<Map<String, String>> list = JSON.parseObject(menu, new TypeReference<List<Map<String, String>>>() {});
                if(moudles != null && moudles.size() > 0){
                    //原来有按钮，首先，删除属于原来，不属于现在的按钮
                    Iterator<SysMoudle> it = moudles.iterator();
                    while(it.hasNext()) {
                        boolean f = false;
                        SysMoudle old = it.next();
                        old.setSystemId(systemId);
                        for (Map<String, String> now : list) {
                            if (old.getCode().equals(now.get("code"))) {
                                f = true;
                                old.setActionUrl(now.get("url"));
                                old.setEnable(moudle.getEnable());
                                moudleService.save(old,curUser.getId(),curUser.getUserName());
                            }
                        }
                        if (!f) {
                            it.remove();
                            old.setState(0);
                            moudleService.save(old,curUser.getId(),curUser.getUserName());
                        }
                    }
                    for (Map<String, String> now : list) {
                        boolean f = false;
                        for (SysMoudle old : moudles) {
                            if (old.getCode().equals(now.get("code"))) {
                                f = true;
                            }
                        }
                        if (!f) {
                            moudle.setSystemId(systemId);
                            saveMenuSingle(moudle, now,curUser.getId(),curUser.getUserName());
                        }
                    }
                }else{
                    //原来没有按钮，直接保存现在的按钮
                    moudle.setSystemId(systemId);
                    List<Map<String, String>> list1 = JSON.parseObject(menu, new TypeReference<List<Map<String, String>>>() {});
                    for (Map<String, String> m : list1) {
                        saveMenuSingle(moudle, m,curUser.getId(),curUser.getUserName());
                    }
                }
            }else{
                //逻辑删除原来的按钮操作
                if(moudles != null && moudles.size()>0){
                    for (SysMoudle m : moudles) {
                        if(m.getIsOperation()==1) {//说明是嵌套按钮
                            m.setState(0);
                            moudleService.save(m, curUser.getId(),curUser.getUserName());
                        }
                    }
                }
            }
            return R.ok();
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ControllerLog(type = OpsLogType.DELETE, value = "模块管理")
    @ApiOperation(value = "根据Id删除对象")
    @RequestMapping(value = "/delete")
    public Object delete(Long id) {
        if (ObjectUtil.isNotEmpty(id)) {
            SysMoudle model = moudleService.getById(id);
            model.setState(Constant.STATE_INVALID);
            UserVO curUser = getCurUser();//获取当前登录人
            boolean flag = moudleService.save(model,curUser.getId(), curUser.getUserName());
            return flag ? R.ok() : R.error();
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "获取模块树")
    @RequestMapping(value = "/getMoudleTree")
    public Object getMoudleTree(Long systemId) {
        List<Map<String, Object>> tree = new ArrayList<>();
        Map<String, Object> moudleTree = null;
        List<SysMoudle> moudles = moudleService.findList(systemId,null,null,null,null);
        for (SysMoudle moudle : moudles) {
            if(moudle.getParentId()!=null) {
                moudleTree = new HashMap<>(3);
                moudleTree.put("open", true);
                moudleTree.put("id", moudle.getId());
                moudleTree.put("name", moudle.getName());
                moudleTree.put("title", moudle.getName());
                if (moudle.getParentId() != null) {
                    moudleTree.put("pid", moudle.getParentId());
                } else {
                    moudleTree.put("pid", "");
                }
                tree.add(moudleTree);
            }
        }
        return R.ok(tree);
    }

    //获取模块权限
    @ApiOperation(value = "根据登录用户获取权限模块集合")
    @RequestMapping(value = "/getAuthMoudles")
    public Object getAuthMoudles(String appid) {
        //根据appid查找systemId
        Long systemId=null;
        List<SysSubsystem> systemInfoList = systemInfoService.findList(appid,1,null);//String appid, Integer enable, Collection<String> ids
        if(systemInfoList!=null&&systemInfoList.size()>0) {
            systemId = systemInfoList.get(0).getId();
        }
        //根据账户id找角色id
        SysAccount currentAccount =getCurAccount();
        List<Long> roleIds=accountRoleService.getRoleIdsByAccountId(currentAccount.getId());
        //通过角色id找模块
        List<Long> moudelIds = new ArrayList<>();
        if(roleIds!=null&&roleIds.size() > 0) {
            List<SysRoleMoudle> rmList = roleMoudleService.findList(null,systemId,roleIds);
            if (rmList != null && rmList.size() > 0) {
                int count2 = rmList.size();
                for (int j = 0; j < count2; j++) {
                    SysRoleMoudle rm = rmList.get(j);
                    Long moudleId=rm.getSysMoudleId();
                    if (moudleId != null) {
                        if(!moudelIds.contains(moudleId)) {
                            moudelIds.add(moudleId);
                        }
                    }
                }
            }
        }
        List<MenuNode> nodes = moudleService.findMenuNodeBySysMoudleIds(moudelIds,systemId);
        //查找所有操作按钮权限
        Map<Long, List<String>> operations = new HashMap<Long, List<String>>(1);
        Iterator<MenuNode> it = nodes.iterator();
        while(it.hasNext()) {
            MenuNode node = it.next();
            if (node.isOperation == 1) {//属于按钮菜单
                Long pid = Long.parseLong(node.getPid());
                if (operations.containsKey(pid)) {
                    List<String> operTmp = operations.get(pid);
                    operTmp.add(node.getCode());
                    operations.put(pid, operTmp);
                } else {
                    List<String> operTmp = new ArrayList<String>(1);
                    operTmp.add(node.getCode());
                    operations.put(pid, operTmp);
                }
                it.remove();
            }
        }
        for(MenuNode node : nodes) {
            Long id=Long.parseLong(node.getId());
            if (operations.containsKey(id)) {
                node.allow = operations.get(id);
            }
        }
        return R.ok(nodes);
    }

    @ApiOperation(value = "通过模块id获取所有的子模块ids")
    @RequestMapping(value = "/findzMoudleIdsById")
    public Object findzMoudleIdsById(Long id) {
        Map<String, Object> map = new HashMap<String, Object>(2);
        if (Objects.nonNull(id)) {
            List<Long> idslist= moudleService.getIdsByPid(id);
            return R.ok(idslist);
        }  else {
            return R.error("前端传递参数为空");
        }
    }

    /**
     * @Description:  新增模块对应的操作按钮小方法
     */
    private void saveMenuSingle(SysMoudle data, Map<String, String> now,Long userId,String userName){
        SysMoudle moudleMenu = new SysMoudle();
        moudleMenu.setCode(now.get("code"));
        moudleMenu.setName(now.get("name"));
        moudleMenu.setParentId(data.getId());
        moudleMenu.setSystemId(data.getSystemId());
        moudleMenu.setIsOperation(1);
        moudleMenu.setIsDisplay(0);
        moudleMenu.setEnable(data.getEnable());
        moudleMenu.setSort(1);
        moudleMenu.setState(1);
        moudleService.save(moudleMenu,userId,userName);
    }
}
