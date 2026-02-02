package com.asam.system.service.impl;

import com.asam.common.util.ObjectUtil;
import com.asam.system.entity.SysRoleMoudle;
import com.asam.system.mapper.RoleMoudleDao;
import com.asam.system.service.RoleMoudleService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;


@Service
@Transactional(rollbackFor=Exception.class)
public class RoleMoudleServiceImpl extends ServiceImpl<RoleMoudleDao, SysRoleMoudle> implements RoleMoudleService {

    @Autowired
    private RoleMoudleDao roleMoudleDao;

    @Override
    public List<SysRoleMoudle> findList(Long roleId, Long systemId,Collection<Long> roleIds) {
        QueryWrapper<SysRoleMoudle> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(roleId),SysRoleMoudle::getSysRoleId, roleId);
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(systemId),SysRoleMoudle::getSystemId, systemId);
        wrapper.lambda().in(ObjectUtil.isNotEmpty(roleIds),SysRoleMoudle::getSysRoleId, roleIds);
        wrapper.lambda().orderByDesc(SysRoleMoudle::getId);
        return list(wrapper);
    }

    @Override
    public boolean save(SysRoleMoudle model) {
        return saveOrUpdate(model);
    }

    @Override
    public void insertAll(List<SysRoleMoudle> models) {
        roleMoudleDao.insertAll(models);
    }

    @Override
    public void deleteByRoleId(Long roleId,Long systemId) {
        if (ObjectUtil.isNotEmpty(systemId)){
            roleMoudleDao.deleteByRoleIdAndSystemId(roleId,systemId);
        }else{
            roleMoudleDao.deleteByRoleId(roleId);
        }
    }
}
