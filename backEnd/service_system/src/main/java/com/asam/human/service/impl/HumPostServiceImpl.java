package com.asam.human.service.impl;

import com.asam.common.util.ObjectUtil;
import com.asam.human.entity.HumPost;
import com.asam.human.mapper.HumPostDao;
import com.asam.human.service.HumPostService;
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
public class HumPostServiceImpl extends ServiceImpl<HumPostDao, HumPost> implements HumPostService {

    @Autowired
    private HumPostDao humPostDao;

    @Override
    public IPage<HumPost> findPageList(JPAPage varBasePage, String name) {
        QueryWrapper<HumPost> wrapper = new QueryWrapper<>();
        wrapper.lambda().like(ObjectUtil.isNotEmpty(name),HumPost::getName, name);
        wrapper.lambda().eq(HumPost::getState, STATE_VALID).orderByDesc(HumPost::getCreateTime);
        IPage<HumPost> varPage = BasePage.getMybaitsPage(varBasePage);
        return page(varPage, wrapper);
    }


    @Override
    public List<HumPost> findList(Long id, String name) {
        QueryWrapper<HumPost> wrapper = new QueryWrapper<>();
        wrapper.lambda().like(ObjectUtil.isNotEmpty(name), HumPost::getName, name);
        wrapper.lambda().ne(ObjectUtil.isNotEmpty(id),HumPost::getId, id);
        wrapper.lambda().eq(HumPost::getState, STATE_VALID);
        return list(wrapper);
    }

    @Override
    public boolean save(HumPost model, Long userId,String userName) {
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
