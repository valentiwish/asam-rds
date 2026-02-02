package com.asam.human.service;

import com.asam.human.entity.HumPost;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.page.JPAPage;

import java.util.List;


public interface HumPostService extends IService<HumPost> {

    IPage<HumPost> findPageList(JPAPage varBasePage, String name);

    List<HumPost> findList(Long id, String name);

    boolean save(HumPost model, Long userId,String userName);

}
