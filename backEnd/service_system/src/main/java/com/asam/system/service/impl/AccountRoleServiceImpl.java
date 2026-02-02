package com.asam.system.service.impl;

import com.asam.common.util.ObjectUtil;
import com.asam.system.entity.SysAccountRole;
import com.asam.system.mapper.AccountRoleDao;
import com.asam.system.service.AccountRoleService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
@Transactional(rollbackFor = Exception.class)
public class AccountRoleServiceImpl extends ServiceImpl<AccountRoleDao, SysAccountRole> implements AccountRoleService {

    @Autowired
    private AccountRoleDao accountRoleDao;

    @Override
    public List<SysAccountRole> findList(Long accountId, Long roleId) {
        QueryWrapper<SysAccountRole> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(accountId),SysAccountRole::getSysAccountId, accountId);
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(roleId),SysAccountRole::getSysRoleId, roleId);
        wrapper.lambda().orderByDesc(SysAccountRole::getId);
        return list(wrapper);
    }

    @Override
    public List<Long> getRoleIdsByAccountId(Long accountId) {
        List<SysAccountRole> list = findList(accountId,null);
        List<Long> roleIdList =null;
        if (list != null && list.size() > 0) {
            roleIdList = new ArrayList<Long>();
            for (SysAccountRole ar : list) {
                if(!roleIdList.contains(ar.getSysRoleId())) {
                    roleIdList.add(ar.getSysRoleId());
                }
            }
        }
        return roleIdList;
    }

    @Override
    public List<Long> getAccountsByRoleId(Long roleId) {
        List<SysAccountRole> list = findList(null,roleId);
        List<Long> aIdList =null;
        if (list != null && list.size() > 0) {
            aIdList = new ArrayList<Long>();
            for (SysAccountRole ar : list) {
                if(!aIdList.contains(ar.getSysAccountId())) {
                    aIdList.add(ar.getSysAccountId());
                }
            }
        }
        return aIdList;
    }

    @Override
    public boolean save(SysAccountRole model) {
        return saveOrUpdate(model);
    }

    @Override
    public void insertAll(List<SysAccountRole> models) {
         accountRoleDao.insertAll(models);
    }

    @Override
    public void deleteByAccountId(Long accountId) {
         accountRoleDao.deleteByAId(accountId);
    }

    @Override
    public void deleteByRoleId(Long roleId) {
        accountRoleDao.deleteByRoleId(roleId);
    }
}
