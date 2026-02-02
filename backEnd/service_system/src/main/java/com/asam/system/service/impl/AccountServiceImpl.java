package com.asam.system.service.impl;

import com.asam.common.util.ObjectUtil;
import com.asam.system.entity.SysAccount;
import com.asam.system.mapper.AccountDao;
import com.asam.system.service.AccountService;
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
@Transactional(rollbackFor = Exception.class)
public class AccountServiceImpl extends ServiceImpl<AccountDao, SysAccount> implements AccountService {

    @Autowired
    private AccountDao accountDao;

    @Override
    public IPage<SysAccount> findPageList(JPAPage varBasePage, String userName,String loginName,String loginName1) {
        QueryWrapper<SysAccount> wrapper = new QueryWrapper<>();
        wrapper.lambda().like(ObjectUtil.isNotEmpty(userName),SysAccount::getUserName, userName);
        wrapper.lambda().like(ObjectUtil.isNotEmpty(loginName1),SysAccount::getLoginName, loginName1);
        wrapper.lambda().eq(SysAccount::getState, STATE_VALID).orderByDesc(SysAccount::getId);
        if(!loginName.equals(SAFETY_ADMIN)) {
            wrapper.lambda().notIn(SysAccount::getId, new ArrayList<>(Arrays.asList("1","2","3")));
        }
        IPage<SysAccount> varPage = BasePage.getMybaitsPage(varBasePage);
        return page(varPage, wrapper);
    }

    @Override
    public SysAccount findModel(Long userId) {
        QueryWrapper<SysAccount> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(userId),SysAccount::getUserId, userId);
        wrapper.lambda().eq(SysAccount::getState, STATE_VALID);
        return getOne(wrapper);
    }

    @Override
    public boolean save(SysAccount model, Long userId,String userName) {
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
    public List<SysAccount> findList(Collection<Long> ids,String loginName,String loginName1){
        QueryWrapper<SysAccount> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(SysAccount::getState,STATE_VALID);
        wrapper.lambda().in(ObjectUtil.isNotEmpty(ids), SysAccount::getId, ids);
        wrapper.lambda().like(ObjectUtil.isNotEmpty(loginName1),SysAccount::getLoginName, loginName1);
        if(!loginName.equals(SAFETY_ADMIN)) {
            wrapper.lambda().notIn(SysAccount::getId, new ArrayList<>(Arrays.asList("1","2","3")));
        }
        return list(wrapper);
    }
}
