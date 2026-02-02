package com.robotCore.scheduing.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.robotCore.common.utils.ObjectUtil;
import com.robotCore.scheduing.dto.RobotTaskRequestDTO;
import com.robotCore.scheduing.entity.RobotTaskLog;
import com.robotCore.scheduing.mapper.RobotTaskLogDao;
import com.robotCore.scheduing.service.RobotTaskLogService;
import com.robotCore.scheduing.vo.RobotTaskLogVO;
import com.robotCore.scheduing.vo.RobotWaybillResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * @Des:
 * @author: zxl
 * @date: 2024/4/10
 **/
@Slf4j
@Service
public class RobotTaskLogServiceImpl extends ServiceImpl<RobotTaskLogDao, RobotTaskLog> implements RobotTaskLogService {

    @Override
    public boolean save(RobotTaskRequestDTO reqDto, HttpServletRequest request, boolean isSuccess, String operateMessage) {
        RobotTaskLog taskLog = new RobotTaskLog();
        taskLog.setRequestIp(request.getRemoteAddr());
        taskLog.setMesWaybillId(reqDto.getMesWaybillId());
        //如果wmsWaybillId不为空，则是wms下发的转运任务
        if (reqDto.getWmsWaybillId() != null) {
            //WMS
            taskLog.setApplicationName("WMS");
            taskLog.setOperateBusiness("WMS下发机器人任务");
        } else {
            //MES
            taskLog.setApplicationName("MES");
            taskLog.setOperateBusiness("MES下发机器人任务");
        }
        taskLog.setOperateContent(JSON.toJSONString(reqDto));
        taskLog.setOperateSuccess(isSuccess);
        taskLog.setOperateMessage(operateMessage);
        taskLog.setCreateTime(new Timestamp(System.currentTimeMillis()));
        return save(taskLog);
    }

    @Override
    public boolean saveSyncMesWaybill(RobotWaybillResultVO reqDto, String mesWaybillId, String wmsWaybillId, String requestIp, boolean isSuccess, String operateMessage) {
        RobotTaskLog taskLog = new RobotTaskLog();
        taskLog.setApplicationName("AMR调度系统");
        taskLog.setRequestIp(requestIp);
        taskLog.setMesWaybillId(mesWaybillId);
        taskLog.setWmsWaybillId(wmsWaybillId);
        taskLog.setOperateBusiness("AMR同步运单状态至第三方系统");
        taskLog.setOperateContent(JSON.toJSONString(reqDto));
        taskLog.setOperateSuccess(isSuccess);
        taskLog.setOperateMessage(operateMessage);
        taskLog.setCreateTime(new Timestamp(System.currentTimeMillis()));
        return save(taskLog);
    }

    @Override
    public IPage<RobotTaskLog> findPageList(IPage<RobotTaskLog> varPage, RobotTaskLog robotTaskLog) {
        QueryWrapper<RobotTaskLog> wrapper = new QueryWrapper<>();
        if(ObjectUtil.isNotEmpty(robotTaskLog)){
            wrapper.lambda().like(ObjectUtil.isNotEmpty(robotTaskLog.getMesWaybillId()),RobotTaskLog::getMesWaybillId, robotTaskLog.getMesWaybillId());
            wrapper.lambda().like(ObjectUtil.isNotEmpty(robotTaskLog.getApplicationName()),RobotTaskLog::getApplicationName, robotTaskLog.getApplicationName());
            wrapper.lambda().like(ObjectUtil.isNotEmpty(robotTaskLog.getRequestIp()),RobotTaskLog::getRequestIp, robotTaskLog.getRequestIp());
            wrapper.lambda().eq(ObjectUtil.isNotEmpty(robotTaskLog.getOperateSuccess()),RobotTaskLog::getOperateSuccess, robotTaskLog.getOperateSuccess());
        }
        wrapper.lambda().orderByDesc(RobotTaskLog::getCreateTime);
        return page(varPage, wrapper);
    }

    @Override
    public List<RobotTaskLogVO> dataInit(List<RobotTaskLog> list) {
        List<RobotTaskLogVO> robotTaskLogVOS = new ArrayList<>();
        for (RobotTaskLog taskLog : list) {
            RobotTaskLogVO vo = new RobotTaskLogVO();
            vo.setId(taskLog.getId());
            vo.setMesWaybillId(taskLog.getMesWaybillId());
            vo.setApplicationName(taskLog.getApplicationName());
            vo.setRequestIp(taskLog.getRequestIp());
            vo.setOperateBusiness(taskLog.getOperateBusiness());
            vo.setOperateContent(taskLog.getOperateContent());
            vo.setOperateSuccess(taskLog.getOperateSuccess() ? "成功" : "失败");
            vo.setOperateTime(taskLog.getCreateTime().toString().substring(0, taskLog.getCreateTime().toString().length() - 2));
            vo.setOperateMessage(taskLog.getOperateMessage());
            robotTaskLogVOS.add(vo);
        }
        return robotTaskLogVOS;
    }

}
