package com.robotCore.scheduing.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.beans.redis.RedisUtil;
import com.entity.R;
import com.robotCore.common.base.controller.BaseController;
import com.robotCore.common.constant.Constant;
import com.robotCore.common.utils.ObjectUtil;
import com.robotCore.robot.dto.RobotPathPlanDTO;
import com.robotCore.scheduing.common.utils.BezierCurveLengthUtil;
import com.robotCore.scheduing.dto.*;
import com.robotCore.scheduing.entity.RobotPathPlanning;
import com.robotCore.robot.entity.RobotBasicInfo;
import com.robotCore.scheduing.service.RobotPathPlanningService;
import com.robotCore.scheduing.service.RobotTaskService;
import com.robotCore.scheduing.vo.RobotPathVO;
import com.robotCore.robot.service.RobotBasicInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Des: 机器人地图信息控制器
 * @author: zxl
 * @date: 2023/8/25
 **/
@Slf4j
@RestController
@Api(value = "RobotPathPlanningController", description = "机器人地图信息")
@RequestMapping(value = "pathPlanning")
public class RobotPathPlanningController extends BaseController {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RobotBasicInfoService robotBasicInfoService;

    @Autowired
    private RobotTaskService robotTaskService;

    @Autowired
    private RobotPathPlanningService robotPathPlanningService;

    @ApiOperation(value = "根据机器人名称获取机器人地图信息", notes = "根据机器人名称获取机器人地图信息")
    @PostMapping(value = "/info")
    public Object getRobotMapInfo(String vehicleId) {
        if (ObjectUtil.isNotEmpty(vehicleId)) {
            //获取redis中存储的机器人地图信息
            Map<String,String> downloadMapRes = (Map<String,String>)redisUtil.stringWithGet("robot_config_downloadmap_res");
            List<RobotBasicInfo> robots = robotBasicInfoService.findByName(vehicleId);
            if (robots.size() < 1) {
                return R.error("机器人名名称输入错误，请重新输入！");
            } else {
                RobotBasicInfo robot = robots.get(0);
                //获取该机器人对应的数据
                String mapText = downloadMapRes.get(robot.getCurrentMap());
                RobotMapDTO dto = JSON.parseObject(mapText, RobotMapDTO.class);
                List<MapAdvancedCurveDTO> advancedCurveList = dto.getAdvancedCurveList();
                List<RobotPathVO> pathVOList = new ArrayList<>();
                for (MapAdvancedCurveDTO mapAdvancedCurveDTO : advancedCurveList) {
                    //获取贝塞尔曲线四个点的坐标
                    MapAdvancedCurveDTO.PointPos startPos = mapAdvancedCurveDTO.getStartPos();
                    MapAdvancedCurveDTO.PointPos endPos = mapAdvancedCurveDTO.getEndPos();
                    MapAdvancedCurveDTO.Pos controlPos1 = mapAdvancedCurveDTO.getControlPos1();
                    MapAdvancedCurveDTO.Pos controlPos2 = mapAdvancedCurveDTO.getControlPos2();
                    //计算贝塞尔曲线的长度
                    float bezierCurveLength = BezierCurveLengthUtil.getBezierCurveLength(startPos.getPos().getX(), startPos.getPos().getY(),
                            controlPos1.getX(), controlPos1.getY(), controlPos2.getX(), controlPos2.getY(), endPos.getPos().getX(), endPos.getPos().getY());
                    String instanceName = mapAdvancedCurveDTO.getInstanceName();
                    //查找-在字符串中的位置
                    int index = instanceName.indexOf("-");
                    //获取起始站点名字
                    String startPoint = instanceName.substring(0, index);
                    //获取终点站点名字
                    String endPoint = instanceName.substring(index + 1);
                    RobotPathVO pathVO = new RobotPathVO();
                    pathVO.setStartPoint(startPoint);
                    pathVO.setEndPoint(endPoint);
                    pathVO.setCurveLength(bezierCurveLength);
                    pathVOList.add(pathVO);
                }
                return R.ok(pathVOList);
            }
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "测试", notes = "测试")
    @PostMapping(value = "/test")
    public Object startPathPlanningTask() throws Exception {
        List<RobotBasicInfo> robotBasicInfos1 = robotBasicInfoService.findByName("W600DS_7_1");
        RobotBasicInfo robot1 = robotBasicInfos1.get(0);
        List<RobotPathVO> pathList1 = JSONObject.parseArray(robot1.getPaths(), RobotPathVO.class);
        RobotPathPlanDTO.RobotPath robotPath1 = robotTaskService.encapsulateData("W600DS_7_1", robot1.getGroupName(),"LM6", "AP43");

        List<RobotBasicInfo> robotBasicInfos2 = robotBasicInfoService.findByName("test_robot");
        RobotBasicInfo robot2 = robotBasicInfos2.get(0);
        List<RobotPathVO> pathList2 = JSONObject.parseArray(robot2.getPaths(), RobotPathVO.class);
        RobotPathPlanDTO.RobotPath robotPath2 = robotTaskService.encapsulateData("test_robot", robot2.getGroupName(),"LM893", "LM83");


        List<RobotPathVO> pathList = new ArrayList<>();
        pathList.addAll(pathList1);
        pathList.addAll(pathList2);

        List<RobotPathPlanDTO.RobotPath> robotPathList = new ArrayList<>();
        robotPathList.add(robotPath1);
        robotPathList.add(robotPath2);

        AlgorithmRequestResDTO algorithmRequestResDTO = robotTaskService.startPathPlanningTask(pathList,robotPathList,null);
        return R.ok(algorithmRequestResDTO);
    }

    @ApiOperation(value = "获取路径算法执行结果", notes = "获取路径算法执行结果")
    @PostMapping(value = "/getPathPlanningTask")
    public Object getPathPlanningTask(String status, String taskId, String processingTime, String result) {
        if (ObjectUtil.isNotEmpty(status) && ObjectUtil.isNotEmpty(taskId) && ObjectUtil.isNotEmpty(processingTime) && ObjectUtil.isNotEmpty(result)) {
            RobotPathPlanning pathPlanning = new RobotPathPlanning();
            pathPlanning.setStatus(status);
            pathPlanning.setTaskId(taskId);
            pathPlanning.setProcessingTime(processingTime);
            pathPlanning.setResult(result);
            pathPlanning.setCreateTime(new Timestamp(new Date().getTime()));
            //设置状态为有效
            pathPlanning.setState(Constant.STATE_VALID);
//            List<AlgorithmResResultDTO> list = JSON.parseArray(result,AlgorithmResResultDTO.class);
            boolean flag = robotPathPlanningService.save(pathPlanning);
            return flag ? R.ok() : R.error("保存路径算法执行结果失败");
        } else {
            return R.error("获取路径算法执行结果为空");
        }
    }

}
