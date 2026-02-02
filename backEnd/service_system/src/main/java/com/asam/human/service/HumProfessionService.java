package com.asam.human.service;

import com.asam.human.entity.HumProfession;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.page.JPAPage;

import java.util.List;


public interface HumProfessionService extends IService<HumProfession> {

    IPage<HumProfession> findPageList(JPAPage varBasePage, String name);

    List<HumProfession> findList(Long id, String name);

    boolean save(HumProfession model, Long userId,String userName);
}
