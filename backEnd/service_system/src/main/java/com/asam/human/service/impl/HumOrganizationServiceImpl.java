package com.asam.human.service.impl;

import com.asam.common.util.ObjectUtil;
import com.asam.human.entity.HumOrganization;
import com.asam.human.mapper.HumOrganizationDao;
import com.asam.human.service.HumOrganizationService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.page.BasePage;
import com.page.JPAPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.asam.common.constant.Constant.STATE_VALID;


@Service
@Transactional(rollbackFor=Exception.class)
public class HumOrganizationServiceImpl extends ServiceImpl<HumOrganizationDao, HumOrganization> implements HumOrganizationService {

    @Autowired
    private HumOrganizationDao humOrganizationDao;

    @Override
    public IPage<HumOrganization> findPageList(JPAPage varBasePage, String name, String parentName, String code, List<String> parentIds) {
        QueryWrapper<HumOrganization> wrapper = new QueryWrapper<>();
        wrapper.lambda().like(ObjectUtil.isNotEmpty(name),HumOrganization::getName, name);
        wrapper.lambda().like(ObjectUtil.isNotEmpty(parentName),HumOrganization::getParentName, parentName);
        wrapper.lambda().like(ObjectUtil.isNotEmpty(code),HumOrganization::getCode, code);
        wrapper.lambda().in(ObjectUtil.isNotEmpty(parentIds),HumOrganization::getParentId, parentIds);
        wrapper.lambda().eq(HumOrganization::getState, STATE_VALID).orderByDesc(HumOrganization::getCreateTime);
        IPage<HumOrganization> varPage = BasePage.getMybaitsPage(varBasePage);
        return page(varPage, wrapper);
    }


    @Override
    public List<HumOrganization> findList(String id,String code,String parentId,Collection<String> ids) {
        QueryWrapper<HumOrganization> wrapper = new QueryWrapper<>();
        wrapper.lambda().ne(ObjectUtil.isNotEmpty(id),HumOrganization::getId, id);
        wrapper.lambda().like(ObjectUtil.isNotEmpty(code),HumOrganization::getCode, code);
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(parentId),HumOrganization::getParentId, parentId);
        wrapper.lambda().in(ObjectUtil.isNotEmpty(ids),HumOrganization::getId, ids);
        wrapper.lambda().eq(HumOrganization::getState, STATE_VALID).orderByDesc(HumOrganization::getCreateTime);
        return list(wrapper);
    }

    @Override
    public boolean save(HumOrganization model, Long userId,String userName) {
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

    @Override
    public void updateStateById(Integer state, Collection<String> orgIds){
        humOrganizationDao.updateStateByIds(state,orgIds);
    }

    @Override
    public void updateByParentId(String parentCode, String parentName, String parentId){
        humOrganizationDao.updateByParentId(parentCode,parentName,parentId);
    }

    //获取当前部门id的所有子部门的id 包括自身的id集合
    @Override
    public List<String> getOrganizationIdsByPid(String parentId) {
        List<String> organizationIds=new ArrayList<>();
        organizationIds.add(parentId);
        List<String> list=getOrganizationListByPid(organizationIds);
        return list;
    }

    //获取当前部门id的顶层的所属公司id
    @Override
    public HumOrganization getOrganizationByZid(String id) {
        id=getOrganizationIdByZid(id);
        HumOrganization org = getById(id);
        if(org.getIsCompany()==1){
          return org;
        }else{
            return null;
        }
    }

    /**
     * 递归查询所有子部门
     * @param ids
     * @return
     */
    private List<String> getOrganizationListByPid(List<String> ids) {
        //用来存取调用自身递归时的参数
        List<String> temp= new ArrayList<String>();
        //查询数据库中对应id的实体类
        List<HumOrganization> sysEnterpriseOrgList = new ArrayList<HumOrganization>();
        //遍历传递过来的参数ids
        for (String id :ids) {
            sysEnterpriseOrgList=findList(null,null,id,null);//查询pid等于id的对象
            //遍历list获取符合条件的对象的id值，一份存到temp中用作递归的参数，并存到全局变量中用来获取所有符合条件的id
            for (HumOrganization s:sysEnterpriseOrgList) {
                temp.add(s.getId());
            }
        }
        if(temp.size()!=0&&temp!=null){
            for(String id :temp){
                if(!ids.contains(id)){
                    ids.add(id);
                }
            }
            ids.addAll(getOrganizationListByPid(temp));
        }
        return ids;
    }

    /**
     * 递归查询顶层部门
     * @param id
     * @return
     */
    private String getOrganizationIdByZid(String id) {
        HumOrganization org = getById(id);//查询数据库中对应id的实体类
        HumOrganization parentOrg = getById(org.getParentId());//查询id等于pid的父级对象
        if(parentOrg!=null){
            id=getOrganizationIdByZid(parentOrg.getId());
        }
        return id;
    }

    /**
     * 递归查询
     * 获取当前部门的所有上级部门 包含自身的
     */
    @Override
    public List<String> getpid(List<String> idList,String pid,List<HumOrganization> organizationList){
        if(ObjectUtil.isNotEmpty(pid)) {
            Optional<HumOrganization> op = organizationList.stream().filter(s -> s.getId().equals(pid)).findFirst();
            HumOrganization humOrganization=op.equals(Optional.empty())?null:op.get();
            if (humOrganization!=null&&ObjectUtil.isNotEmpty(humOrganization.getId())) {
                idList.add(humOrganization.getId());
                getpid(idList, humOrganization.getParentId(), organizationList);
            }
        }
        return  idList;
    }
}
