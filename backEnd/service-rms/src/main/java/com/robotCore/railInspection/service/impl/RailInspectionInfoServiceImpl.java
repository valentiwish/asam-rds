package com.robotCore.railInspection.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.robotCore.common.utils.ObjectUtil;
import com.robotCore.railInspection.entity.RailInspectionInfo;
import com.robotCore.railInspection.entityVo.RailInspectionInfoVo;
import com.robotCore.railInspection.mapper.RailInspectionInfoDao;
import com.robotCore.railInspection.service.RailInspectionInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @Des:
 * @author: zxl
 * @date: 2023/11/6
 **/
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class RailInspectionInfoServiceImpl extends ServiceImpl<RailInspectionInfoDao, RailInspectionInfo> implements RailInspectionInfoService {
    @Override
    public IPage<RailInspectionInfoVo> findPageList(IPage<RailInspectionInfo> varPage, RailInspectionInfo railInspectionInfo) {
        QueryWrapper<RailInspectionInfo> wrapper = new QueryWrapper<>();
        if(ObjectUtil.isNotEmpty(railInspectionInfo)){
            wrapper.lambda().like(ObjectUtil.isNotEmpty(railInspectionInfo.getInspectionResult()),RailInspectionInfo::getInspectionResult, railInspectionInfo.getInspectionResult());
        }
        wrapper.lambda().orderByDesc(RailInspectionInfo::getCreateTime);
        List<RailInspectionInfo> list = list(wrapper);
//        IPage<RailInspectionInfo> railInspectionInfoIPage = this.baseMapper.selectPage(varPage, wrapper);
        IPage<RailInspectionInfoVo> page =new Page<>();
        page.setCurrent(varPage.getCurrent());
        page.setSize(varPage.getSize());
        page.setTotal(list.size());
        page.setRecords(dataInit(list));
        return page;
    }

    @Override
    public List<RailInspectionInfoVo> dataInit(List<RailInspectionInfo> list) {
        List<RailInspectionInfoVo> inspectionInfoVos = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            RailInspectionInfoVo vo = new RailInspectionInfoVo();
            RailInspectionInfo railInspectionInfo = list.get(i);
            vo.setId(railInspectionInfo.getId());
            vo.setSerialNumber(i + 1);
            vo.setInspectionTime(railInspectionInfo.getCreateTime().toString().substring(0,railInspectionInfo.getCreateTime().toString().length() - 2));
            vo.setTaskName("任务名称");
            vo.setTaskType("即时任务");
            vo.setInspectionObject("配电柜电流表");
            if (railInspectionInfo.getInspectionType() == null) {
                vo.setInspectionType("手势识别");
            } else {
                vo.setInspectionType(railInspectionInfo.getInspectionType());
            }
            vo.setResultType(0);
            vo.setInspectionResult(railInspectionInfo.getInspectionResult());
            vo.setPictureId(railInspectionInfo.getPictureId());
            inspectionInfoVos.add(vo);
        }
        return inspectionInfoVos;
    }
}
