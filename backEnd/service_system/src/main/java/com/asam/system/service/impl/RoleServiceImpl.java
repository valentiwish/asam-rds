package com.asam.system.service.impl;

import com.asam.common.util.ObjectUtil;
import com.asam.human.entity.UserRoleVo;
import com.asam.system.entity.SysRole;
import com.asam.system.mapper.RoleDao;
import com.asam.system.service.RoleService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.page.BasePage;
import com.page.JPAPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.asam.common.constant.Constant.SAFETY_ADMIN;
import static com.asam.common.constant.Constant.STATE_VALID;


@Service
@Transactional(rollbackFor=Exception.class)
public class RoleServiceImpl  extends ServiceImpl<RoleDao, SysRole> implements RoleService {

    @Autowired
    private RoleDao roleDao;

    @Override
    public IPage<SysRole> findPageList(JPAPage varBasePage,  String name,String loginName) {
        QueryWrapper<SysRole> wrapper = new QueryWrapper<>();
        wrapper.lambda().like(ObjectUtil.isNotEmpty(name),SysRole::getName, name);
        wrapper.lambda().eq(SysRole::getState, STATE_VALID).orderByDesc(SysRole::getId);
        if(!loginName.equals(SAFETY_ADMIN)) {
            wrapper.lambda().notIn(SysRole::getId, new ArrayList<>(Arrays.asList("1","2","3")));
        }
        IPage<SysRole> varPage = BasePage.getMybaitsPage(varBasePage);
        return page(varPage, wrapper);
    }

    @Override
    public List<SysRole> findList(Long id, String name,String roleCode,Collection<Long> ids,String loginName) {
        QueryWrapper<SysRole> wrapper = new QueryWrapper<>();
        wrapper.lambda().ne(ObjectUtil.isNotEmpty(id),SysRole::getId, id);
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(name),SysRole::getName, name);
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(roleCode),SysRole::getRoleCode,roleCode);
        wrapper.lambda().in(ObjectUtil.isNotEmpty(ids), SysRole::getId, ids);
        if(!loginName.equals(SAFETY_ADMIN)) {
            wrapper.lambda().notIn(SysRole::getId, new ArrayList<>(Arrays.asList("1","2","3")));
        }
        wrapper.lambda().eq(SysRole::getState, STATE_VALID).orderByDesc(SysRole::getId);
        return list(wrapper);
    }

    @Override
    public boolean save(SysRole model, Long userId, String userName) {
        if(model.getId()==null){
            model.setCreateId(userId);
            model.setCreateUser(userName);
            model.setCreateTime(new Date());
        }else{
            model.setUpdateId(userId);
            model.setUpdateUser(userName);
            model.setUpdateTime(new Date());
        }
        return saveOrUpdate(model);
    }

    @Override
    public List<UserRoleVo> findRoleByLoginName(String loginName) {
        return roleDao.findRoleByLoginName(loginName);
    }
}
