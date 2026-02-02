package com.robotCore.scheduing.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.robotCore.scheduing.dto.RobotTaskRequestDTO;
import com.robotCore.scheduing.entity.RobotTaskLog;
import com.robotCore.scheduing.vo.RobotTaskLogVO;
import com.robotCore.scheduing.vo.RobotWaybillResultVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Des:
 * @author: zxl
 * @date: 2024/4/10
 **/
public interface RobotTaskLogService  extends IService<RobotTaskLog> {

    boolean save(RobotTaskRequestDTO reqDto, HttpServletRequest request, boolean isSuccess, String operateMessage);

    boolean saveSyncMesWaybill(RobotWaybillResultVO reqDto, String mesWaybillId, String wmsWaybillId, String requestIp, boolean isSuccess, String operateMessage);

    IPage<RobotTaskLog> findPageList(IPage<RobotTaskLog> varPage, RobotTaskLog robotTaskLog);

    List<RobotTaskLogVO> dataInit(List<RobotTaskLog> list);
}
