package com.asam.human.service;

import com.asam.human.entity.HumUserOrg;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Collection;
import java.util.List;


public interface UserOrgService extends IService<HumUserOrg> {

    List<HumUserOrg> findList(Long userId, Collection<String> userIds) ;

    boolean save(HumUserOrg model);

    void insertAll(List<HumUserOrg> models);

    void deleteByUserId(Long userId);
}
