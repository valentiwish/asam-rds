package com.asam.human.service;

import com.asam.human.entity.HumPosition;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.page.JPAPage;

import java.util.List;


public interface HumPositionService extends IService<HumPosition> {

    IPage<HumPosition> findPageList(JPAPage varBasePage, String name);

    List<HumPosition> findList(Long id, String name);

    boolean save(HumPosition model, Long userId,String userName);
}
