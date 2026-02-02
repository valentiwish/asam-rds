package com.robotCore.multiScheduling.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.robotCore.multiScheduling.entity.ExtInterfaceLog;
import com.robotCore.multiScheduling.entity.ExtProtocolAddress;

/**
 * @Description:
 * @Author: zhangqi
 * @Create: 2022/7/31 14:58
 */
public interface ExtInterfaceLogService extends IService<ExtInterfaceLog> {

    Boolean saveLog(ExtProtocolAddress protocolAddress);

    void saveExtInterFaceLog(String logId, String fromSys, String toSys, String content, String returnObj, String returnMsg, Integer status);
}
