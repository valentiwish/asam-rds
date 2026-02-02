package com.asam.human.service;

import com.asam.human.entity.HumUser;
import com.asam.human.entity.UserVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.page.JPAPage;

import java.util.Collection;
import java.util.List;


public interface HumUserService extends IService<HumUser> {

    IPage<HumUser> findPageList(JPAPage varBasePage, String name, String jobNumber, String sex, List<String> orgIds);

    List<HumUser> findList(String id, String loginName, String jobNumber, String userPhone, String cardId, String email, String orgId, Collection<String> orgIds, Collection<String> ids);

    boolean save(HumUser model, Long userId,String userName);

    UserVO getUserByLoginName(String loginName);

    void updateByOrgId(String orgCode, String orgName, String orgId);
}
