package com.asam.system.service.impl;

import com.asam.common.base.model.MenuNode;
import com.asam.common.util.ObjectUtil;
import com.asam.system.entity.SysMoudle;
import com.asam.system.mapper.MoudleDao;
import com.asam.system.service.MoudleService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.asam.common.constant.Constant.STATE_VALID;


@Service
@Transactional(rollbackFor=Exception.class)
public class MoudleServiceImpl extends ServiceImpl<MoudleDao, SysMoudle> implements MoudleService {

    @Autowired
    private MoudleDao moudleDao;


    @Override
    public List<SysMoudle> findList(Long systemId, Long parentId,Integer isOperation,Integer enable,Collection<Long> parentIds) {
        QueryWrapper<SysMoudle> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(systemId),SysMoudle::getSystemId, systemId);
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(parentId),SysMoudle::getParentId, parentId);
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(isOperation),SysMoudle::getIsOperation, isOperation);
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(enable),SysMoudle::getEnable, enable);
        wrapper.lambda().in(ObjectUtil.isNotEmpty(parentIds),SysMoudle::getParentId, parentIds);
        wrapper.lambda().eq(SysMoudle::getState, STATE_VALID).orderByDesc(SysMoudle::getId);
        return list(wrapper);
    }

    //用于栏目菜单显示
    @Override
    public List<MenuNode> findMenuNodeBySysMoudleIds(List<Long> moudleIds,Long systemId){
        QueryWrapper<SysMoudle> wrapper = new QueryWrapper<>();
        wrapper.lambda().in(SysMoudle::getId, moudleIds);
        wrapper.lambda().eq(SysMoudle::getSystemId, systemId);
        wrapper.lambda().eq(SysMoudle::getState, STATE_VALID).eq(SysMoudle::getEnable, 1).ne(SysMoudle::getId, 1);
        wrapper.lambda().orderByAsc(SysMoudle::getSort, SysMoudle::getParentId);
        List<SysMoudle> list= list(wrapper);
        List<MenuNode> nodes = new ArrayList<>();
        for (SysMoudle sysSysMoudle :list){
            MenuNode node = new MenuNode(sysSysMoudle.getId().toString(), sysSysMoudle.getCode(),sysSysMoudle.getName(), sysSysMoudle.getParentId()!=null?sysSysMoudle.getParentId().toString():null,
                    sysSysMoudle.getActionUrl(), sysSysMoudle.getImgUrl(), sysSysMoudle.getIcon(), sysSysMoudle.getIsOperation(), sysSysMoudle.getIsDisplay());
            node.setJsonCss(sysSysMoudle.getJsonCss());
            nodes.add(node);
        }
        return nodes;
    }

    @Override
    public boolean save(SysMoudle model, Long userId, String userName) {
        Long id=model.getId();
        if(id==null){
            model.setCreateId(userId);
            model.setCreateUser(userName);
            model.setCreateTime(new Date());
        }else{
            model.setUpdateId(userId);
            model.setUpdateUser(userName);
            model.setUpdateTime(new Date());
            //查询该模块下是否有子模块，如果存在子模块，在禁用父模块时将子模块同时禁用,删除时同时删除
            List<Long> ids=getIdsByPid(id);
            ids.remove(0);//把自己剔除
            if(ids!=null&&ids.size()>0) {
                moudleDao.updateByIds(model.getState(), model.getEnable(), ids);
            }
        }
        return saveOrUpdate(model);
    }

    //获取当前模块id的所有子模块的id的list集合 包括自身的id
    @Override
    public List<Long> getIdsByPid(Long parentId) {
        List<Long> moudleIds=new ArrayList<>();
        moudleIds.add(parentId);
        List<SysMoudle> list=getSysMoudleListByPid(moudleIds);
        if(list!=null&&list.size()>0){
            for(SysMoudle o:list){
                if(!moudleIds.contains(o.getId())) {
                    moudleIds.add(o.getId());
                }
            }
        }
        return moudleIds;
    }

    //获取当前模块id的所有子模块的集合包括自身的id
    @Override
    public List<SysMoudle> getByPid(Long parentId) {
        List<Long> moudleIds=new ArrayList<>();
        moudleIds.add(parentId);
        List<SysMoudle> list=getSysMoudleListByPid(moudleIds);
        return list;
    }

    /**
     * 递归查询
     * @param ids
     * @return
     */
    private List<SysMoudle> getSysMoudleListByPid(List<Long> ids) {
        //根据父ID查询模块
        List<SysMoudle> departments = findList(null,null,null,null,ids);
        if (ObjectUtil.isNotEmpty(departments)) {
            //拿到当前所有模块ID
            List<Long> parentIds = departments.stream().map(item -> item.getId()).collect(Collectors.toList());
            //拼接子模块查询结果
            departments.addAll(getSysMoudleListByPid(parentIds));
            return departments;
        } else {
            //如果没有下级模块那么我们就返回空集合，结束递归。
            return new ArrayList<SysMoudle>();
        }
    }

    @Override
    public void deleteBySystemId(Long systemId) {
       moudleDao.deleteBySystemId(systemId);
    }
}
