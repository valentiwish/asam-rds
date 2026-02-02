package com.asam.sysdata.service.impl;

import com.asam.common.util.ObjectUtil;
import com.asam.sysdata.entity.SysDataType;
import com.asam.sysdata.mapper.SysDataTypeDao;
import com.asam.sysdata.service.SysDataTypeService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.page.BasePage;
import com.page.JPAPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static com.asam.common.constant.Constant.STATE_VALID;


@Service
@Transactional
public class SysDataTypeServiceImpl extends ServiceImpl<SysDataTypeDao, SysDataType> implements SysDataTypeService {

    @Autowired
    private SysDataTypeDao sysDataTypeDao;

    @Override
    public IPage<SysDataType> findPageList(JPAPage varBasePage, String typeName, String typeCode) {
        QueryWrapper<SysDataType> wrapper = new QueryWrapper<>();
        wrapper.lambda().like(ObjectUtil.isNotEmpty(typeName),SysDataType::getTypeName, typeName);
        wrapper.lambda().like(ObjectUtil.isNotEmpty(typeCode),SysDataType::getTypeCode, typeCode);
        wrapper.lambda().eq(SysDataType::getState, STATE_VALID).orderByDesc(SysDataType::getId);
        IPage<SysDataType> varPage = BasePage.getMybaitsPage(varBasePage);
        return page(varPage, wrapper);
    }


    @Override
    public List<SysDataType> findList(Long id, String code) {
        QueryWrapper<SysDataType> wrapper = new QueryWrapper<>();
        wrapper.lambda().ne(ObjectUtil.isNotEmpty(id),SysDataType::getId, id);
        wrapper.lambda().like(ObjectUtil.isNotEmpty(code),SysDataType::getTypeCode, code);
        wrapper.lambda().eq(SysDataType::getState, STATE_VALID).orderByDesc(SysDataType::getId);
        return list(wrapper);
    }

    @Override
    public boolean save(SysDataType model, Long userId,String userName) {
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
