package com.asam.human.service.impl;

import com.asam.common.util.ObjectUtil;
import com.asam.human.entity.HumProfession;
import com.asam.human.mapper.HumProfessionDao;
import com.asam.human.service.HumProfessionService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.page.BasePage;
import com.page.JPAPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static com.asam.common.constant.Constant.STATE_VALID;


@Service
/*@DS("service_user")*/  //多数据元配置
public class HumProfessionServiceImpl extends ServiceImpl<HumProfessionDao, HumProfession> implements HumProfessionService {

    @Autowired
    private HumProfessionDao humProfessionDao;

    @Override
    public IPage<HumProfession> findPageList(JPAPage varBasePage, String name) {
        QueryWrapper<HumProfession> wrapper = new QueryWrapper<>();
        wrapper.lambda().like(ObjectUtil.isNotEmpty(name),HumProfession::getName, name);
        wrapper.lambda().eq(HumProfession::getState, STATE_VALID).orderByDesc(HumProfession::getCreateTime);
        IPage<HumProfession> varPage = BasePage.getMybaitsPage(varBasePage);
        return page(varPage, wrapper);
    }


    @Override
    public List<HumProfession> findList(Long id, String name) {
        QueryWrapper<HumProfession> wrapper = new QueryWrapper<>();
        wrapper.lambda().like(ObjectUtil.isNotEmpty(name), HumProfession::getName, name);
        wrapper.lambda().ne(ObjectUtil.isNotEmpty(id),HumProfession::getId, id);
        wrapper.lambda().eq(HumProfession::getState, STATE_VALID);
        return list(wrapper);
    }

    @Override
    public boolean save(HumProfession model, Long userId,String userName) {
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
