package com.asam.human.service.impl;

import com.asam.common.util.ObjectUtil;
import com.asam.human.entity.HumPosition;
import com.asam.human.mapper.HumPositionDao;
import com.asam.human.service.HumPositionService;
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
public class HumPositionServiceImpl extends ServiceImpl<HumPositionDao, HumPosition> implements HumPositionService {

    @Autowired
    private HumPositionDao humPositionDao;

    @Override
    public IPage<HumPosition> findPageList(JPAPage varBasePage, String name) {
        QueryWrapper<HumPosition> wrapper = new QueryWrapper<>();
        wrapper.lambda().like(ObjectUtil.isNotEmpty(name),HumPosition::getName, name);
        wrapper.lambda().eq(HumPosition::getState, STATE_VALID).orderByDesc(HumPosition::getCreateTime);
        IPage<HumPosition> varPage = BasePage.getMybaitsPage(varBasePage);
        return page(varPage, wrapper);
    }


    @Override
    public List<HumPosition> findList(Long id,String name) {
        QueryWrapper<HumPosition> wrapper = new QueryWrapper<>();
        wrapper.lambda().like(ObjectUtil.isNotEmpty(name),HumPosition::getName, name);
        wrapper.lambda().ne(ObjectUtil.isNotEmpty(id),HumPosition::getId, id);
        wrapper.lambda().eq(HumPosition::getState, STATE_VALID);
        return list(wrapper);
    }

    @Override
    public boolean save(HumPosition model, Long userId,String userName) {
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
