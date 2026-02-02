package com.asam.system.service;

import com.asam.common.base.model.MenuNode;
import com.asam.system.entity.SysMoudle;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Collection;
import java.util.List;


public interface MoudleService  extends IService<SysMoudle> {

    List<SysMoudle> findList(Long systemId, Long parentId,Integer isOperation,Integer enable,Collection<Long> parentIds);

    List<MenuNode> findMenuNodeBySysMoudleIds(List<Long> moudleIds, Long systemId);

    boolean save(SysMoudle model, Long userId, String userName);

    List<Long> getIdsByPid(Long parentId);

    List<SysMoudle> getByPid(Long parentId);

    void deleteBySystemId(Long systemId);
}
