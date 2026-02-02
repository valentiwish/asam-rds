package com.robotCore.multiScheduling.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.robotCore.multiScheduling.entity.ExtInterfaceLog;
import com.robotCore.multiScheduling.entity.ExtProtocolAddress;
import com.robotCore.multiScheduling.entity.ReturnObj;
import com.robotCore.multiScheduling.mapper.ExtInterfaceLogDao;
import com.robotCore.multiScheduling.service.ExtInterfaceLogService;
import com.utils.tools.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.robotCore.common.constant.Constant.STATE_INVALID;
import static com.robotCore.common.constant.Constant.STATE_VALID;

/**
 * @Description:
 * @Author: zhangqi
 * @Create: 2022/7/31 14:58
 */
@DS("datasync")
@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
public class ExtInterfaceLogServiceImpl extends ServiceImpl<ExtInterfaceLogDao, ExtInterfaceLog> implements ExtInterfaceLogService {

    @Autowired
    private ExtInterfaceLogDao extInterfaceLogDao;

    @Override
    public Boolean saveLog(ExtProtocolAddress protocolAddress) {
        ExtInterfaceLog extInterfaceLog = new ExtInterfaceLog();
        extInterfaceLog.setLogId(protocolAddress.getBizOpn());
        if (StringUtils.isNotBlank(protocolAddress.getContent())) {
            extInterfaceLog.setContent(protocolAddress.getContent());
        }
        extInterfaceLog.setFromSys(protocolAddress.getFromSys());
        extInterfaceLog.setToSys(protocolAddress.getToSys());
        extInterfaceLog.setCreateTime(new Date());
        ReturnObj returnObj = protocolAddress.getReturnObj();
        extInterfaceLog.setReturnObj(JSON.toJSONString(returnObj));
        extInterfaceLog.setStatus(returnObj.STATUS.equals("Y") || returnObj.STATUS.equals("200") ? STATE_VALID : STATE_INVALID);
        Boolean flag = this.save(extInterfaceLog);
        return flag;
    }

    /**
     * 保存外部HTTP接口日志
     *
     */
    @Override
    public void saveExtInterFaceLog(String logId, String fromSys, String toSys, String content, String returnObj, String returnMsg, Integer status) {
        ExtInterfaceLog extInterfaceLog = new ExtInterfaceLog();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddhhmmssSSS");
        String dateStr = formatter.format(new Date());
        extInterfaceLog.setLogId(logId + dateStr);
        extInterfaceLog.setCreateTime(new Date());
        extInterfaceLog.setFromSys(fromSys);
        extInterfaceLog.setToSys(toSys);
        extInterfaceLog.setContent(content);
        extInterfaceLog.setReturnMsg(returnMsg);
        extInterfaceLog.setReturnObj(returnObj);
        extInterfaceLog.setStatus(status);
        extInterfaceLogDao.insert(extInterfaceLog);
    }
}
