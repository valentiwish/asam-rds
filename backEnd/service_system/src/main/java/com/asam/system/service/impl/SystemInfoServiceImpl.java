package com.asam.system.service.impl;

import com.asam.common.util.ObjectUtil;
import com.asam.system.entity.SysSubsystem;
import com.asam.system.mapper.SystemInfoDao;
import com.asam.system.service.SystemInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.page.BasePage;
import com.page.JPAPage;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static com.asam.common.constant.Constant.STATE_VALID;


@Service
@Transactional
public class SystemInfoServiceImpl extends ServiceImpl<SystemInfoDao, SysSubsystem> implements SystemInfoService {
    
    @Autowired
    private SystemInfoDao systemInfoDao;
    
    @Override
    public IPage<SysSubsystem> findPageList(JPAPage varBasePage, String name, String appid) {
        QueryWrapper<SysSubsystem> wrapper = new QueryWrapper<>();
        wrapper.lambda().like(StringUtils.isNotBlank(name),SysSubsystem::getName, name);
        wrapper.lambda().like(StringUtils.isNotBlank(appid),SysSubsystem::getAppid, appid);
        wrapper.lambda().eq(SysSubsystem::getState, STATE_VALID).orderByDesc(SysSubsystem::getId);
        IPage<SysSubsystem> varPage = BasePage.getMybaitsPage(varBasePage);
        return page(varPage, wrapper);
    }

    @Override
    public List<SysSubsystem> findList(String appid, Integer enable, Collection<String> ids) {
        QueryWrapper<SysSubsystem> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(appid),SysSubsystem::getAppid, appid);
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(enable),SysSubsystem::getEnable, enable);
        wrapper.lambda().in(ObjectUtil.isNotEmpty(ids),SysSubsystem::getId, ids);
        wrapper.lambda().eq(SysSubsystem::getState, STATE_VALID).orderByAsc(SysSubsystem::getSort);
        return list(wrapper);
    }

    @Override
    public boolean save(SysSubsystem model, Long userId,String userName) {
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
    public SysSubsystem checkAppid(Long id, String appid) {
        QueryWrapper<SysSubsystem> wrapper = new QueryWrapper<>();
        wrapper.lambda().ne(ObjectUtil.isNotEmpty(id),SysSubsystem::getId, id);
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(appid),SysSubsystem::getAppid, appid);
        wrapper.lambda().eq(SysSubsystem::getState, STATE_VALID);
        return getOne(wrapper);
    }
}