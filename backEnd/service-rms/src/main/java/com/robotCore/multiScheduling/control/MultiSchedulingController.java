package com.robotCore.multiScheduling.control;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.entity.EntityResult;
import com.entity.R;
import com.page.BasePage;
import com.page.FormatPage;
import com.page.JPAPage;
import com.robotCore.common.base.controller.BaseController;
import com.robotCore.common.utils.ObjectUtil;
import com.robotCore.multiScheduling.dto.MissionCallbackUrlReqDTO;
import com.robotCore.multiScheduling.dto.RobotTaskRequestParam;
import com.robotCore.multiScheduling.dto.RobotWaybillStatusDTO;
import com.robotCore.multiScheduling.entity.RobotMultiWaybill;
import com.robotCore.multiScheduling.service.RobotMultiWaybillService;
import com.robotCore.multiScheduling.vo.RobotMultiWaybillVO;
import com.robotCore.scheduing.common.enums.RobotWaybillStatusEnum;
import com.utils.tools.CopyUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.List;

/**
 * @Des: 多品牌调度系统综合管控
 * @author: zxl
 * @date: 2025/6/5
 **/
@Slf4j
@RestController
@Api(value = "MultiSchedulingController", description = "多品牌调度系统综合处理控制器类")
@RequestMapping(value = "multiTask")
public class MultiSchedulingController extends BaseController {

    @Autowired
    private RobotMultiWaybillService robotMultiWaybillService;


    @ApiOperation(value = "运行下发给不同调度系统的机器人任务")
    @RequestMapping(value = "/runTask")
    public Object runDmsTask(@RequestBody RobotTaskRequestParam reqDto) {
        log.info("下发机器人任务：{}",JSON.toJSONString(reqDto));
        boolean flag = false;
        if (ObjectUtil.isNotEmpty(reqDto)) {
            //机器人类型 1--米松叉车，2--金陵,3--西安航天
            flag = robotMultiWaybillService.createRobotMultipleWaybill(reqDto);
        }
        return flag ? R.ok() :R.error("下发机器人任务失败！");
    }

    @ApiOperation(value = "MES/WMS取消运单任务")
    @RequestMapping(value = "/mesOrWmsCancelTask")
    public Object mesCancelTask(String mesWaybillId, String wmsWaybillId, String id) throws Exception {
        log.info("MES/WMS取消运单任务,mesWaybillId:{},wmsWaybillId:{}",mesWaybillId,wmsWaybillId);
        if (ObjectUtil.isNotEmpty(mesWaybillId) || ObjectUtil.isNotEmpty(wmsWaybillId) || ObjectUtil.isNotEmpty(id)) {
            RobotMultiWaybill robotWaybill;
            if (ObjectUtil.isNotEmpty(mesWaybillId)) {
                robotWaybill = robotMultiWaybillService.getWaybillByMesWaybillId(mesWaybillId);
            } else if (ObjectUtil.isNotEmpty(wmsWaybillId)){
                robotWaybill = robotMultiWaybillService.getWaybillByWmsWaybillId(wmsWaybillId);
            } else {
                robotWaybill = robotMultiWaybillService.getById(id);
            }
            if (robotWaybill != null) {
                boolean flag = false;
                if (RobotWaybillStatusEnum.EXECUTING.getCode().equals(robotWaybill.getWaybillStatus())) {
                    //光通信運行
                    //取消任务
                    flag = robotMultiWaybillService.cancelTask(robotWaybill);
                } else if (RobotWaybillStatusEnum.CANCEL.getCode().equals(robotWaybill.getWaybillStatus()) || RobotWaybillStatusEnum.TERMINATE.getCode().equals(robotWaybill.getWaybillStatus())) {
                    robotMultiWaybillService.cancelTask(robotWaybill);
                    flag = true;
                } else if (RobotWaybillStatusEnum.WAIT.getCode().equals(robotWaybill.getWaybillStatus())) {
                    flag = true;
                }
                //更新运单状态为取消
                robotWaybill.setWaybillStatus(4);
                robotMultiWaybillService.saveOrUpdate(robotWaybill);
                //如果由MES下发的指令，则同步完成状态给MES系统;如果是WMS系统，则同步完成状态给WMS系统
                robotMultiWaybillService.completeTaskSyncToOtherSystem(robotWaybill.getMesWaybillId(), robotWaybill.getWmsWaybillId());
                return flag ? R.ok() : R.error("取消当前导航失败" );
            } else {
                return R.error("当前取消的运单不存在");
            }
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "获取机器人运单管理列表")
    @PostMapping(value = "/list")
    public Object list(JPAPage varBasePage, String data) {
        RobotMultiWaybill robotMultiWaybill = null;
        IPage<RobotMultiWaybill> varPage = BasePage.getMybaitsPage(varBasePage);
        CopyUtils.convert(varBasePage, varPage, CopyUtils.methodType.Define);
        if(ObjectUtil.isNotEmpty(data)){
            robotMultiWaybill = JSON.parseObject(data,RobotMultiWaybill.class);
        }
        EntityResult varEntityResult = new EntityResult();
        IPage<RobotMultiWaybill> page = robotMultiWaybillService.findPageList(varPage, robotMultiWaybill);
        IPage<RobotMultiWaybillVO> newPage = new Page<>();
        newPage.setCurrent(page.getCurrent());
        newPage.setSize(page.getSize());
        newPage.setTotal(page.getTotal());
        newPage.setRecords(robotMultiWaybillService.dataInit(page.getRecords()));
        varEntityResult.setData(FormatPage.getFormatMybaitsPage(newPage));
        return varEntityResult;
    }

    @ApiOperation(value = "更新运单状态")
    @RequestMapping(value = "/updateStatus")
    public Object updateStatus(@RequestBody RobotWaybillStatusDTO waybillStatusDTO) {
        if (ObjectUtil.isNotEmpty(waybillStatusDTO)) {

        }
        return R.ok();
    }

    @ApiOperation(value = "Mission更新运单状态")
    @RequestMapping(value = "/missionUpdateWaybillStatus")
    public Object missionUpdateWaybillStatus(@RequestBody MissionCallbackUrlReqDTO missionCallbackUrlReqDTO) throws Exception {
        log.info("Mission更新运单状态:{}",JSON.toJSONString(missionCallbackUrlReqDTO));
        if (ObjectUtil.isNotEmpty(missionCallbackUrlReqDTO)) {
            List<RobotMultiWaybill> missionWaybills = robotMultiWaybillService.getMissionWaybillByMissionWaybillId(missionCallbackUrlReqDTO.getId());
            if (missionWaybills.size() > 0) {
                RobotMultiWaybill robotMultiWaybill = missionWaybills.get(0);
                //运单状态 0--等待，1--正在执行，2--完成，3--终止，4--取消
                //      1-CREATE：创建
                //     * 2-ASSIGNED：已分配
                //     * 3-WAIT：等待执行
                //     * 4-RUNNING：执行中
                //     * 5-SUCCESS：执行成功
                //     * 6-FAULT：执行错误
                //     * 7-PAUSE：暂停
                //     * 8-SHUTDOWN：已停止
                //     * 9-BEING_PAUSE：暂停中
                //     * 10-BEING_RESUME：恢复中
                //     * 11-BEING_SHUTDOWN：停止中
                if ("CREATE".equals(missionCallbackUrlReqDTO.getStatus())) {
                    robotMultiWaybill.setWaybillStatus(1);
                } else if ("ASSIGNED".equals(missionCallbackUrlReqDTO.getStatus())) {
                    robotMultiWaybill.setWaybillStatus(1);
                } else if ("WAIT".equals(missionCallbackUrlReqDTO.getStatus())) {
                    robotMultiWaybill.setWaybillStatus(1);
                } else if ("RUNNING".equals(missionCallbackUrlReqDTO.getStatus())) {
                    robotMultiWaybill.setWaybillStatus(1);
                } else if("SUCCESS".equals(missionCallbackUrlReqDTO.getStatus())) {
                    robotMultiWaybill.setWaybillStatus(2);
                    robotMultiWaybill.setCompleteTime(new Timestamp(System.currentTimeMillis()));
                } else if ("FAULT".equals(missionCallbackUrlReqDTO.getStatus())) {
                    robotMultiWaybill.setWaybillStatus(3);
                    robotMultiWaybill.setCompleteTime(new Timestamp(System.currentTimeMillis()));
                } else if ("PAUSE".equals(missionCallbackUrlReqDTO.getStatus())) {
                    robotMultiWaybill.setWaybillStatus(4);
                    robotMultiWaybill.setCompleteTime(new Timestamp(System.currentTimeMillis()));
                } else if ("SHUTDOWN".equals(missionCallbackUrlReqDTO.getStatus())) {
                    robotMultiWaybill.setWaybillStatus(4);
                    robotMultiWaybill.setCompleteTime(new Timestamp(System.currentTimeMillis()));
                } else if ("BEING_PAUSE".equals(missionCallbackUrlReqDTO.getStatus())) {
                    robotMultiWaybill.setWaybillStatus(4);
                    robotMultiWaybill.setCompleteTime(new Timestamp(System.currentTimeMillis()));
                } else if ("BEING_RESUME".equals(missionCallbackUrlReqDTO.getStatus())) {
                    robotMultiWaybill.setWaybillStatus(4);
                    robotMultiWaybill.setCompleteTime(new Timestamp(System.currentTimeMillis()));
                } else if ("BEING_SHUTDOWN".equals(missionCallbackUrlReqDTO.getStatus())) {
                    robotMultiWaybill.setWaybillStatus(4);
                    robotMultiWaybill.setCompleteTime(new Timestamp(System.currentTimeMillis()));
                } else if ("STOP".equals(missionCallbackUrlReqDTO.getStatus())) {
                    robotMultiWaybill.setWaybillStatus(4);
                    robotMultiWaybill.setCompleteTime(new Timestamp(System.currentTimeMillis()));
                }
                if ("8".equals(missionCallbackUrlReqDTO.getDescription().substring(0,1))
                        && ObjectUtil.isNotEmpty(missionCallbackUrlReqDTO.getCurrentActionSequence()) && missionCallbackUrlReqDTO.getCurrentActionSequence() == 9) {
                    log.info("Mission更新取货完成运单状态description:{}",missionCallbackUrlReqDTO.getDescription());
                    robotMultiWaybill.setWaybillStatus(6);
                    robotMultiWaybill.setCompleteTime(new Timestamp(System.currentTimeMillis()));
                }
                robotMultiWaybill.setRobotName(missionCallbackUrlReqDTO.getAgvCode());
                robotMultiWaybill.setErrorCode(missionCallbackUrlReqDTO.getErrorCode());
                robotMultiWaybill.setErrorMessage(missionCallbackUrlReqDTO.getErrorMessage());
                robotMultiWaybill.setErrorTime(new Timestamp(System.currentTimeMillis()));
                robotMultiWaybillService.saveOrUpdate(robotMultiWaybill);
                //如果由MES下发的指令，则同步完成状态给MES系统;如果是WMS系统，则同步完成状态给WMS系统
                robotMultiWaybillService.completeTaskSyncToOtherSystem(robotMultiWaybill.getMesWaybillId(), robotMultiWaybill.getWmsWaybillId());
            }
        } else {
            return R.error("前端传递参数为空！");
        }
        return R.ok();
    }

}
