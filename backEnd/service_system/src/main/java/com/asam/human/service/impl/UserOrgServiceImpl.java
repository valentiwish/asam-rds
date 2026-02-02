package com.asam.human.service.impl;

import com.asam.common.util.ObjectUtil;
import com.asam.human.entity.HumUserOrg;
import com.asam.human.mapper.HumUserOrgDao;
import com.asam.human.service.UserOrgService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;


@Service
@Transactional(rollbackFor=Exception.class)
public class UserOrgServiceImpl extends ServiceImpl<HumUserOrgDao, HumUserOrg> implements UserOrgService {

    @Autowired
    private HumUserOrgDao humUserOrgDao;

    @Override
    public List<HumUserOrg> findList(Long userId, Collection<String> userIds) {
        QueryWrapper<HumUserOrg> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(userId), HumUserOrg::getSysUserId, userId);
        wrapper.lambda().in(ObjectUtil.isNotEmpty(userIds), HumUserOrg::getSysUserId, userIds);
        wrapper.lambda().orderByDesc(HumUserOrg::getId);
        return list(wrapper);
    }

    @Override
    public boolean save(HumUserOrg model) {
        return saveOrUpdate(model);
    }

    @Override
    public void insertAll(List<HumUserOrg> models) {
        humUserOrgDao.insertAll(models);
    }

    @Override
    public void deleteByUserId(Long userId) {
        humUserOrgDao.deleteByUserId(userId);
    }
}
