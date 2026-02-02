package com.asam.system.service.impl;

import com.asam.common.util.ObjectUtil;
import com.asam.system.entity.SysVariable;
import com.asam.system.mapper.SysVariableDao;
import com.asam.system.service.SysVariableService;
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
@Transactional(rollbackFor = Exception.class)
public class SysVariableServiceImpl extends ServiceImpl<SysVariableDao, SysVariable> implements SysVariableService {

    @Autowired
    private SysVariableDao sysVariableDao;

    @Override
    public IPage<SysVariable> findPageList(JPAPage varBasePage, SysVariable sysVariable) {
        QueryWrapper<SysVariable> wrapper = new QueryWrapper<>();
        if(ObjectUtils.isNotEmpty(sysVariable)) {
            if(ObjectUtils.isNotEmpty(sysVariable.getVarCode())) {
                wrapper.lambda().like(SysVariable::getVarCode, sysVariable.getVarCode());
            }
            if(ObjectUtils.isNotEmpty(sysVariable.getVarName())) {
                wrapper.lambda().like(SysVariable::getVarName, sysVariable.getVarName());
            }
        }
        wrapper.lambda().eq(SysVariable::getState, STATE_VALID).orderByDesc(SysVariable::getCreateTime);
        IPage<SysVariable> varPage = BasePage.getMybaitsPage(varBasePage);
        return page(varPage, wrapper);
    }


    @Override
    public List<SysVariable> findList(String id, String varCode) {
        QueryWrapper<SysVariable> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(varCode),SysVariable::getVarCode,varCode);
        wrapper.lambda().ne(ObjectUtil.isNotEmpty(id),SysVariable::getId, id);
        wrapper.lambda().eq(SysVariable::getState, STATE_VALID);
        return list(wrapper);
    }

    @Override
    public boolean save(SysVariable model, Long userId,String userName) {
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