package com.robotCore.railInspection.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.entity.EntityResult;
import com.entity.R;
import com.modbus4j.entity.ModbusTcpRead;
import com.modbus4j.entity.ModbusTcpWrite;
import com.modbus4j.util.ConstDataType;
import com.modbus4j.util.ConstFunCode;
import com.modbus4j.util.api.ModbusUtils;
import com.page.BasePage;
import com.page.FormatPage;
import com.page.JPAPage;
import com.robotCore.common.utils.DataConvertUtil;
import com.robotCore.railInspection.entity.RailInspectionMap;
import com.robotCore.railInspection.entityVo.RailInspectionInfoVo;
import com.robotCore.railInspection.enums.InspectionResultEnum;
import com.robotCore.camera.utils.LoginUtil;
import com.robotCore.camera.utils.PTZControlUtil;
import com.robotCore.common.base.controller.BaseController;
import com.robotCore.common.utils.ObjectUtil;
import com.robotCore.railInspection.entity.RailInspectionInfo;
import com.robotCore.railInspection.service.RailInspectionInfoService;
import com.robotCore.railInspection.service.RailInspectionMapService;
import com.robotCore.scheduing.vo.RailInspectionMapVO;
import com.robotCore.task.core.ProtocolConvert;
import com.utils.tools.CopyUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import socket.handler.client.MsgMode;
import socket.handler.client.TcpClientThread;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Des: 挂轨巡检机器人控制器类
 * @author: zxl
 * @date: 2023/11/6
 **/
@Slf4j
@RestController
@Api(value = "RailInspectionController", description = "挂轨巡检机器人控制器")
@RequestMapping(value = "railInspection")
public class RailInspectionController extends BaseController {

    @Autowired
    private RailInspectionInfoService railInspectionInfoService;

    @Autowired
    private RailInspectionMapService railInspectionMapService;

    public static final String railInspectionRobotIp = "192.168.13.72";

    @ApiOperation(value = "获取监测信息列表")
    @PostMapping(value = "/list")
    public Object list(JPAPage varBasePage, String data) {
        RailInspectionInfo railInspectionInfo =null;
        IPage<RailInspectionInfo> varPage = BasePage.getMybaitsPage(varBasePage);
        CopyUtils.convert(varBasePage, varPage, CopyUtils.methodType.Define);
        if(ObjectUtil.isNotEmpty(data)){
            railInspectionInfo = JSON.parseObject(data,RailInspectionInfo.class);
        }
        EntityResult varEntityResult = new EntityResult();
        IPage<RailInspectionInfoVo> page = railInspectionInfoService.findPageList(varPage, railInspectionInfo);
        varEntityResult.setData(FormatPage.getFormatMybaitsPage(page));
        return varEntityResult;
    }



    @ApiOperation(value = "控制云台")
    @RequestMapping(value = "/control")
    public Object upControlPtz(int flag, String id){
        if (ObjectUtil.isNotEmpty(flag) && ObjectUtil.isNotEmpty(id)) {
            if (LoginUtil.m_hLoginHandle.longValue() == 0) {
                boolean login = LoginUtil.login("192.168.13.117", 37777, "admin", "admin123");
                if (!login) {
                    return R.error("登录失败！");
                }
            }
            RailInspectionInfo model = new RailInspectionInfo();
            model.setPictureId(id);
            if (InspectionResultEnum.getByCode(flag) == null) {
                model.setInspectionResult("无效手势");
            } else {
                model.setInspectionResult(Objects.requireNonNull(InspectionResultEnum.getByCode(flag)).getName());
            }
            long timestamp = System.currentTimeMillis();
            Timestamp ts = new Timestamp(timestamp);
            model.setCreateTime(ts);
            boolean flag1 = railInspectionInfoService.saveOrUpdate(model);
            if (!flag1) {
                return R.error("请求保存巡检记录失败");
            }
            //前进
            if(flag == 15 || flag == 8) {
                String convertString = "func0006{\"direction\":1}";
                String data = DataConvertUtil.convertStringToHex(convertString);
                EntityResult result = ProtocolConvert.sendHexMsg("仙工机器人", railInspectionRobotIp, 19206, data);
                assert result != null;
                return result.isSuccess() ? R.ok() : R.error("请求机器人运动指令失败！");
            }
            //后退
            if(flag == 0 || flag == 16) {
                String convertString = "func0006{\"direction\":-1}";
                String data = DataConvertUtil.convertStringToHex(convertString);
                EntityResult result = ProtocolConvert.sendHexMsg("仙工机器人", railInspectionRobotIp, 19206, data);
                assert result != null;
                return result.isSuccess() ? R.ok() : R.error("请求机器人运动指令失败！");
            }
            //停止
            if(flag == 7 || flag == 13) {
                String convertString = "func0006{\"direction\":0}";
                String data = DataConvertUtil.convertStringToHex(convertString);
                EntityResult result = ProtocolConvert.sendHexMsg("仙工机器人", railInspectionRobotIp, 19206, data);
                assert result != null;
                return result.isSuccess() ? R.ok() : R.error("请求机器人运动指令失败！");
            }
            return R.error("发送的手势指令失败！");
        } else {
            return R.error("前端传递参数为空");
        }
    }


    @ApiOperation(value = "火焰烟雾检测")
    @RequestMapping(value = "/fire")
    public Object fireSmokeDetect(int flag, String id){
        if (ObjectUtil.isNotEmpty(flag) && ObjectUtil.isNotEmpty(id)) {
            if (LoginUtil.m_hLoginHandle.longValue() == 0) {
                boolean login = LoginUtil.login("192.168.13.117", 37777, "admin", "admin123");
                if (!login) {
                    return R.error("登录失败！");
                }
            }
            RailInspectionInfo model = new RailInspectionInfo();
            model.setPictureId(id);
            if (InspectionResultEnum.getByCode(flag) == null) {
                model.setInspectionResult("未知场景");
            } else {
                model.setInspectionResult(Objects.requireNonNull(InspectionResultEnum.getByCode(flag)).getName());
            }
            long timestamp = System.currentTimeMillis();
            Timestamp ts = new Timestamp(timestamp);
            model.setCreateTime(ts);
            boolean flag1 = railInspectionInfoService.saveOrUpdate(model);
            return flag1 ? R.ok() : R.error("请求保存监测信息失败");
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "前端控制云台")
    @RequestMapping(value = "/PTZController")
    public Object controlPtz(int flag){
        if (ObjectUtil.isNotEmpty(flag)) {
            if (LoginUtil.m_hLoginHandle.longValue() == 0) {
                boolean login = LoginUtil.login("192.168.13.117", 37777, "admin", "admin123");
                if (!login) {
                    return R.error("登录失败！");
                }
            }
            //向上移动
            if (flag == 0) {
                boolean b = PTZControlUtil.upControlPtz(0, 0, 2);
                return b ? R.ok("向上移动") : R.error("没有登录，请先登录！");
            }

            //向下移动
            if (flag == 1) {
                boolean b = PTZControlUtil.downControlPtz(0, 0, 2);
                return b ? R.ok("向下移动") : R.error("没有登录，请先登录！");
            }

            //向左移动
            if (flag == 2) {
                boolean b = PTZControlUtil.leftControlPtz(0, 0, 2);
                return b ? R.ok("向左移动") : R.error("没有登录，请先登录！");
            }

            //向右移动
            if (flag == 3) {
                boolean b = PTZControlUtil.rightControlPtz(0, 0, 2);
                return b ? R.ok("向右移动") : R.error("没有登录，请先登录！");
            }

            //向左上移动
            if (flag == 4) {
                boolean b = PTZControlUtil.ptzControlLeftUpStart(0, 0, 2);
                return b ? R.ok("向左上移动") : R.error("没有登录，请先登录！");
            }

            //向左下移动
            if (flag == 5) {
                boolean b = PTZControlUtil.ptzControlLeftDownStart(0, 0, 2);
                return b ? R.ok("向左下移动") : R.error("没有登录，请先登录！");
            }

            //向右上移动
            if (flag == 6) {
                boolean b = PTZControlUtil.ptzControlRightUpStart(0, 0, 2);
                return b ? R.ok("向右上移动") : R.error("没有登录，请先登录！");
            }

            //向右下移动
            if (flag == 7) {
                boolean b = PTZControlUtil.ptzControlRightDownStart(0, 0, 2);
                return b ? R.ok("向右下移动") : R.error("没有登录，请先登录！");
            }

            //停止移动
            if (flag == 8) {
                boolean b = PTZControlUtil.stopUpControl(0);
                return b ? R.ok("停止移动") : R.error("没有登录，请先登录！");
            }

            return R.error("输入运动控制方向错误!");
        } else {
            return R.error("前端传递参数为空");
        }
    }

    /**
     * 控制云台--转至预置点
     */
    @ApiOperation(value = "控制云台--转至回零状态")
    @RequestMapping(value = "/toPresetPointControl")
    public Object toPresetPointControl(){
        boolean b = PTZControlUtil.presetPointControlPtz(1, 0, 1);
        return b ? R.ok() : R.error("没有登录，请先登录！");
    }

    @ApiOperation(value = "请求保存监测信息", notes = "算法识别到异常信息则上传")
    @PostMapping(value = "/requestSave")
    public Object requestSave(String pictureId, String inspectionResult, String inspectionType) {
        if (ObjectUtil.isNotEmpty(pictureId) && ObjectUtil.isNotEmpty(inspectionResult)) {
            RailInspectionInfo model = new RailInspectionInfo();
            model.setPictureId(pictureId);
            model.setInspectionResult(inspectionResult);
            model.setInspectionType(inspectionType);
            long timestamp = System.currentTimeMillis();
            Timestamp ts = new Timestamp(timestamp);
            model.setCreateTime(ts);
            boolean flag = railInspectionInfoService.saveOrUpdate(model);
            return flag ? R.ok() : R.error("请求保存监测信息失败");
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "根据Id删除对象")
    @RequestMapping(value = "/delete")
    public Object delete(String id) {
        if (ObjectUtil.isNotEmpty(id)) {
            boolean flag = railInspectionInfoService.removeById(id);
            return flag ? R.ok() : R.error();
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "机器人向前、向后、停止运动")
    @RequestMapping(value = "/navigate")
    public Object navigate(int direction) {
        if (ObjectUtil.isNotEmpty(direction)) {
            String convertString = "func0006{\"direction\":" + direction + "}";
            String data = DataConvertUtil.convertStringToHex(convertString);
            EntityResult result = ProtocolConvert.sendHexMsg("仙工机器人", railInspectionRobotIp, 19206, data);
            assert result != null;
            return result.isSuccess() ? R.ok() : R.error();
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "机器人路径导航")
    @RequestMapping(value = "/pathNavigation")
    public Object pathNavigation(String startPoint, String endPoint) {
        if (ObjectUtil.isNotEmpty(startPoint) && ObjectUtil.isNotEmpty(endPoint)) {
            List<RailInspectionMap> mapList = railInspectionMapService.findList();
            List<String> mapPointNames = new ArrayList<>();
            for (RailInspectionMap railInspectionMap : mapList) {
                mapPointNames.add(railInspectionMap.getPointName());
            }
            if (!mapPointNames.contains(startPoint) || !mapPointNames.contains(endPoint)) {
                return R.error("输入的地图名称在该地图中不存在！");
            }
            String convertString = "func0002{\"start\":" + "\"" + startPoint + "\",\"goal\":\"" + endPoint + "\"}";
            String data = DataConvertUtil.convertStringToHex(convertString);
            EntityResult result = ProtocolConvert.sendHexMsg("仙工机器人", railInspectionRobotIp, 19206, data);
            assert result != null;
            return result.isSuccess() ? R.ok() : R.error("请求机器人运动指令失败！");
        } else {
            return R.error("前端传递参数为空");
        }
    }


    @ApiOperation(value = "获取巡检地图")
    @RequestMapping(value = "/getMap")
    public Object getMap(String vehicleId) {
        if (ObjectUtil.isNotEmpty(vehicleId)) {
            List<RailInspectionMap> mapList = railInspectionMapService.findList();
            List<RailInspectionMapVO> list = new ArrayList<>();
            for (RailInspectionMap railInspectionMap : mapList) {
                RailInspectionMapVO vo = new RailInspectionMapVO();
                vo.setName(railInspectionMap.getPointName());
                vo.setValue(railInspectionMap.getPointLength());
                vo.setStatus(railInspectionMap.getInspectionStatus());
                list.add(vo);
            }
            return R.ok(list);
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @PostMapping("/tcpClientStart")
    public Object tcpClientStart(String ip, int port) {
        EntityResult start = TcpClientThread.start(ip, port);
        return R.ok(start);
    }

    @PostMapping("/tcpClientSendMsg")
    public Object tcpClientSendMsg(int port, String msg) {
        EntityResult result = TcpClientThread.sendMsg(port, MsgMode.MsgType.ASCIICode, msg);
        return R.ok(result);
    }


    /* ************* 测试通用modbus操作 *************
     * ---参数说明---
     * ip：设备IP地址，offset：寄存器偏移地址，operationType：操作类型（0：读取线圈，1：写入线圈，2：读取保持寄存器，3：写入保持寄存器，value：写入值（仅写入操作时有效）
     */
    @RequestMapping(value = "/testModbusOperation")
    public Object testModbusOperation(String ip, Integer slaveId, Integer offset, Integer operationType, @RequestParam(required = false) Integer value) {
        int functionCode;
        ModbusTcpRead modbusOperation;

        switch (operationType) {
            case 1: // 读取线圈
                functionCode = ConstFunCode.func01;
                modbusOperation = new ModbusTcpRead(slaveId, functionCode, offset, ConstDataType.BooleanType, ip, 502);
                break;
            case 2: // 写入线圈
                functionCode = ConstFunCode.func05;
                modbusOperation = new ModbusTcpWrite(slaveId, functionCode, offset, ConstDataType.BooleanType, ip, 502);
                ((ModbusTcpWrite) modbusOperation).setWriteValue(value == null ? "0" : (value == 0 ? "0" : "1"));
                break;
            case 3: // 读取保持寄存器
                functionCode = ConstFunCode.func03;
                modbusOperation = new ModbusTcpRead(slaveId, functionCode, offset, ConstDataType.TWO_BYTE_INT_SIGNED, ip, 502);
                break;
            case 4: // 写入保持寄存器
                functionCode = ConstFunCode.func06;
                modbusOperation = new ModbusTcpWrite(slaveId, functionCode, offset, ConstDataType.TWO_BYTE_INT_SIGNED, ip, 502);
                ((ModbusTcpWrite) modbusOperation).setWriteValue(value == null ? "0" : String.valueOf(value));
                break;
            default:
                throw new IllegalArgumentException("Unsupported operation type");
        }

        // 分别处理读和写操作
        if (modbusOperation instanceof ModbusTcpWrite) {
            // 调用写方法
            return ModbusUtils.writeTcpModbusByFunCode(((ModbusTcpWrite) modbusOperation));
        } else if (modbusOperation instanceof ModbusTcpRead) {
            // 调用读方法
            return ModbusUtils.readModbusByTcp((ModbusTcpRead) modbusOperation);
        }
        throw new IllegalStateException("异常值类型: " + modbusOperation.getClass().getName());
    }

}
