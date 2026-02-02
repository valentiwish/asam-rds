package com.asam.system.service.impl;

import com.asam.common.util.ObjectUtil;
import com.asam.system.entity.SysIpFilter;
import com.asam.system.mapper.SysIpFilterDao;
import com.asam.system.service.SysIpFilterService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.page.BasePage;
import com.page.JPAPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static com.asam.common.constant.Constant.STATE_VALID;

@Service
@Transactional(rollbackFor=Exception.class)
public class SysIpFilterServiceImpl extends ServiceImpl<SysIpFilterDao, SysIpFilter> implements SysIpFilterService {

    @Autowired
    private SysIpFilterDao sysIpFilterDao;

    @Override
    public IPage<SysIpFilter> findPageList(JPAPage varBasePage, SysIpFilter sysIpFilter) {
        QueryWrapper<SysIpFilter> wrapper = new QueryWrapper<>();
        if(ObjectUtils.isNotEmpty(sysIpFilter)) {
            if(ObjectUtils.isNotEmpty(sysIpFilter.getIp())) {
                wrapper.lambda().like(SysIpFilter::getIp, sysIpFilter.getIp());
            }
            if(ObjectUtils.isNotEmpty(sysIpFilter.getStartTime()) || ObjectUtils.isNotEmpty(sysIpFilter.getEndTime())) {
                wrapper.lambda().between(SysIpFilter::getCreateTime, sysIpFilter.getStartTime(), sysIpFilter.getEndTime());
            }
        }
        wrapper.lambda().eq(SysIpFilter::getState, STATE_VALID).orderByDesc(SysIpFilter::getCreateTime);
        IPage<SysIpFilter> varPage = BasePage.getMybaitsPage(varBasePage);
        return page(varPage, wrapper);
    }


    @Override
    public List<SysIpFilter> findList(String id, String ip) {
        QueryWrapper<SysIpFilter> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(ip),SysIpFilter::getIp,ip);
        wrapper.lambda().ne(ObjectUtil.isNotEmpty(id),SysIpFilter::getId, id);
        wrapper.lambda().eq(SysIpFilter::getState, STATE_VALID);
        return list(wrapper);
    }

    @Override
    public boolean save(SysIpFilter model, Long userId,String userName) {
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
}