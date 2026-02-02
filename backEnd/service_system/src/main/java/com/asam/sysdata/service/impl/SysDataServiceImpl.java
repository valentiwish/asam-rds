package com.asam.sysdata.service.impl;

import com.asam.common.util.ObjectUtil;
import com.asam.sysdata.entity.SysData;
import com.asam.sysdata.mapper.SysDataDao;
import com.asam.sysdata.service.SysDataService;
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
public class SysDataServiceImpl extends ServiceImpl<SysDataDao, SysData> implements SysDataService {

    @Autowired
    private SysDataDao sysDataDao;

    @Override
    public IPage<SysData> findPageList(JPAPage varBasePage, String textName, Long dataTypeId) {
        QueryWrapper<SysData> wrapper = new QueryWrapper<>();
        wrapper.lambda().like(ObjectUtil.isNotEmpty(textName),SysData::getTextName, textName);
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(dataTypeId),SysData::getDataTypeId, dataTypeId);
        wrapper.lambda().eq(SysData::getState, STATE_VALID).orderByDesc(SysData::getId);
        IPage<SysData> varPage = BasePage.getMybaitsPage(varBasePage);
        //CopyUtils.convert(varBasePage, varPage, CopyUtils.methodType.Define);
        return page(varPage, wrapper);
    }

    @Override
    public List<SysData> findList(Long id, String textName, String dataTypeCode, Long dataTypeId) {
        QueryWrapper<SysData> wrapper = new QueryWrapper<>();
        wrapper.lambda().ne(ObjectUtil.isNotEmpty(id),SysData::getId, id);
        wrapper.lambda().like(ObjectUtil.isNotEmpty(textName),SysData::getTextName, textName);
        wrapper.lambda().like(ObjectUtil.isNotEmpty(dataTypeCode),SysData::getDataTypeCode, dataTypeCode);
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(dataTypeId),SysData::getDataTypeId, dataTypeId);
        wrapper.lambda().eq(SysData::getState, STATE_VALID).orderByDesc(SysData::getId);
        return list(wrapper);
    }

    @Override
    public boolean save(SysData model, Long userId,String userName) {
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
    public void updateByDataTypeId(String dataTypeCode, String dataTypeName, Long dataTypeId) {
        sysDataDao.updateByDataTypeId(dataTypeCode,dataTypeName,dataTypeId);
    }

    @Override
    public void deleteByDataTypeId(Long dataTypeId) {
        sysDataDao.deleteByDataTypeId(dataTypeId);
    }
}
