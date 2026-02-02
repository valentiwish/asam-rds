package com.asam.human.service;

import com.asam.human.entity.HumOrganization;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.page.JPAPage;

import java.util.Collection;
import java.util.List;

public interface HumOrganizationService extends IService<HumOrganization> {

    IPage<HumOrganization> findPageList(JPAPage varBasePage, String name, String parentName, String code, List<String> parentIds);

    List<HumOrganization> findList(String id,String code,String parentId,Collection<String> ids);

    boolean save(HumOrganization model, Long userId,String userName);

    void updateStateById(Integer state, Collection<String> orgIds);

    void updateByParentId(String parentCode, String parentName, String parentId);

    List<String> getOrganizationIdsByPid(String parentId);

    HumOrganization getOrganizationByZid(String id);

    List<String> getpid(List<String> idList,String pid,List<HumOrganization> organizationList);
}
