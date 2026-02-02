package com.robotCore.scheduing.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.lang.String;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.beans.redis.RedisUtil;
import com.entity.EntityResult;
import com.entity.R;
import com.page.BasePage;
import com.page.FormatPage;
import com.page.JPAPage;
import com.robotCore.common.base.controller.BaseController;
import com.robotCore.common.base.vo.UserVO;
import com.robotCore.common.config.ControllerLog;
import com.robotCore.common.config.OpsLogType;
import com.robotCore.common.constant.Constant;
import com.robotCore.common.constant.PortConstant;
import com.robotCore.common.utils.DataConvertUtil;
import com.robotCore.common.utils.ObjectUtil;
import com.robotCore.robot.dto.RobotPathPlanDTO;
import com.robotCore.robot.entity.RobotBasicInfo;
import com.robotCore.robot.service.RobotBasicInfoService;
import com.robotCore.robot.service.RobotConnectService;
import com.robotCore.scheduing.common.config.CustomScheduledTaskRegistrar;
import com.robotCore.scheduing.common.enums.ControlStatusEnum;
import com.robotCore.scheduing.dto.*;
import com.robotCore.scheduing.entity.RobotPathPlanning;
import com.robotCore.scheduing.entity.RobotTask;
import com.robotCore.scheduing.entity.RobotWaybill;
import com.robotCore.scheduing.service.*;
import com.robotCore.scheduing.vo.RobotCommonResVO;
import com.robotCore.scheduing.vo.RobotInfoVO;
import com.robotCore.scheduing.vo.RobotPathVO;
import com.robotCore.scheduing.vo.RobotTaskVO;
import com.robotCore.task.tcpCilent.IClientRead1;
import com.robotCore.task.tcpCilent.TcpClientThread;
import com.utils.tools.CopyUtils;
import com.utils.tools.StringUtils;
import feign.Param;
import io.netty.channel.ChannelHandlerContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.One;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @Des: 机器人任务的控制器类
 * @author: zxl
 * @date: 2023/6/5
 **/
@Slf4j
@RestController
@Api(value = "RobotTaskController", description = "机器人任务的控制器类")
@RequestMapping(value = "task")
public class RobotTaskController extends BaseController {

    @Autowired
    private RobotTaskService robotTaskService;

    @Autowired
    private RobotBasicInfoService robotBasicInfoService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private CustomScheduledTaskRegistrar customScheduledTaskRegistrar;

    @Autowired
    private RobotInfoService robotInfoService;

    @Autowired
    private RobotWaybillService robotWaybillService;

    @Autowired
    private RobotTaskLogService robotTaskLogService;

    @Autowired
    private RobotConnectService robotConnectService;

    @Autowired
    RobotPathPlanningService robotPathPlanningService;


    @ApiOperation(value = "获取全部对象")
    @PostMapping(value = "/list")
    public Object list(JPAPage varBasePage, String data) {
        RobotTask robotTask = null;
        IPage<RobotTask> varPage = BasePage.getMybaitsPage(varBasePage);
        CopyUtils.convert(varBasePage, varPage, CopyUtils.methodType.Define);
        if (ObjectUtil.isNotEmpty(data)) {
            robotTask = JSON.parseObject(data, RobotTask.class);
        }
        EntityResult varEntityResult = new EntityResult();
        IPage<RobotTask> page = robotTaskService.findPageList(varPage, robotTask);
        varEntityResult.setData(FormatPage.getFormatMybaitsPage(page));
        return varEntityResult;
    }

    @ApiOperation(value = "获取任务信息和任务所需要的参数列表")
    @PostMapping(value = "/getTaskInfo")
    public Object getTaskInfo() {
        List<RobotTaskVO> list = robotTaskService.getTaskInfo();
        return R.ok(list);
    }

    @ControllerLog(type = OpsLogType.ADDORUPDATE, value = "天风任务")
    @ApiOperation(value = "新增或更新对象")
    @PostMapping(value = "/save")
    public Object save(String data) {
        if (ObjectUtil.isNotEmpty(data)) {
            RobotTask model = JSON.parseObject(data, RobotTask.class);
            if (model.getTaskContent() == null) {
                model.setTaskContent("");
            }
            model.setState(Constant.STATE_VALID);
            UserVO curUser = getCurUser();//当前创建人
            boolean b = robotTaskService.save(model, curUser.getId(), curUser.getUserName());
            return b ? R.ok() : R.error();
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "校验名称")
    @RequestMapping(value = "/check")
    public Object check(String name, String id) {
        if (ObjectUtil.isNotEmpty(name)) {
            List<RobotTask> list = robotTaskService.getCheckList(id, name);
            boolean b = Objects.isNull(list) || 0 == list.size();
            return R.ok(b);
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "根据Id获取对象")
    @RequestMapping(value = "/findById")
    public Object findById(String id) {
        if (ObjectUtil.isNotEmpty(id)) {
            RobotTask data = robotTaskService.getById(id);
            return R.ok(data);
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ControllerLog(type = OpsLogType.DELETE, value = "删除天风任务")
    @ApiOperation(value = "根据Id删除对象")
    @RequestMapping(value = "/delete")
    public Object delete(String id) {
        if (ObjectUtil.isNotEmpty(id)) {
            RobotTask model = robotTaskService.getById(id);
            model.setState(Constant.STATE_INVALID);
            boolean flag = robotTaskService.removeById(id);
            return flag ? R.ok() : R.error();
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "更新机器人循环执行状态")
    @PostMapping(value = "/updateLoopState")
    public Object updateLoopState(String id, Integer enabledState) {
        if (ObjectUtil.isNotEmpty(id) && ObjectUtil.isNotEmpty(enabledState)) {
            RobotTask model = robotTaskService.getById(id);
            model.setEnabledState(enabledState);
            boolean b = robotTaskService.saveOrUpdate(model);
            return b ? R.ok() : R.error();
        } else {
            return R.error("前端传递参数为空");
        }
    }

    /**
     * 运行WIFI任务
     *
     * @param taskId            任务id
     * @param taskParameterList WIFI运行的参数列表
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "运行WIFI任务")
    @RequestMapping(value = "/runTask")
    public Object runWifiTask(String taskId, String taskParameterList) throws Exception {
        if (ObjectUtil.isNotEmpty(taskId)) {
            //如果taskParameterList==null，则使用默认的任务参数；反之，则使用用户实际输入的任务参数去替换默认的任务参数
            if (ObjectUtil.isEmpty(taskParameterList)) {
                taskParameterList = robotTaskService.getTaskParameterList(taskId);
            }
            //获取任务编辑中的任务列表
            List<RobotRunDTO> taskList = robotWaybillService.getTaskList(taskId, taskParameterList);

            if (taskList == null || taskList.size() <= 2) {
                return R.error("请检查任务是否输入完整！");
            }
            //获取运行的任务指定的机器人名字
            String vehicle = taskList.get(0).getVehicle();
            //设置运行方式
            String communicationType = "WIFI运行";
            //如果没有指定机器人，则不需要校验机器人的状态
            if (vehicle == null || vehicle.length() == 0) {
                //创建任务运单
                RobotCommonResVO res = robotWaybillService.createWaybill(null, null, null, taskList, taskId, taskParameterList, communicationType);
                return res.isSuccess() ? R.ok() : R.error(res.getMessage());
            } else {
                List<RobotBasicInfo> list = robotBasicInfoService.findByName(vehicle);
                List<RobotInfoVO> robotInfoVOList = robotInfoService.dataInit(list);
                if (robotInfoVOList.size() > 0) {
                    RobotInfoVO robotInfoVO = robotInfoVOList.get(0);
                    //如果机器人处于可接单状态并且已经抢占控制权（可以运行WIFI任务）
                    if (Objects.equals(robotInfoVO.getOrderState(), Constant.ORDER_AVAILABLE_NUMBER)
                            && ControlStatusEnum.SEIZED_CONTROL.getCode().equals(robotInfoVO.getControlState())) {
                        List<TaskParameterDTO> taskParameterDTOS = JSONObject.parseArray(taskParameterList, TaskParameterDTO.class);
                        if (taskParameterDTOS == null) {
                            return R.error("输入的任务参数格式不正确!");
                        }
                        //创建任务运单
                        RobotCommonResVO res = robotWaybillService.createWaybill(null, null, null, taskList, taskId, taskParameterList, communicationType);
                        return res.isSuccess() ? R.ok() : R.error(res.getMessage());
                    } else {
                        return R.error("没有WIFI运行控制权，请抢占WIFI控制权！");
                    }
                } else {
                    return R.error("根据任务获取机器人名称失败!");
                }
            }
        } else {
            return R.error("前端传递参数为空！");
        }
    }

    /**
     * 运行WIFI任务
     *
     * @param reqDto WIFI运行的参数列表
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "外部接口--运行WIFI任务")
    @RequestMapping(value = "/executeWifiTask")
    public Object executeWifiTask(@RequestBody RobotTaskRequestDTO reqDto, HttpServletRequest request) throws Exception {
        log.info("外部系统调用运行机器人任务成功:{}", JSON.toJSON(reqDto));
        if (ObjectUtil.isNotEmpty(reqDto) && ObjectUtil.isNotEmpty(reqDto.getMesWaybillId()) &&
                ObjectUtil.isNotEmpty(reqDto.getTaskNumber()) && ObjectUtil.isNotEmpty(reqDto.getTaskParameter())) {
            //获取任务编辑中的任务列表
            RobotTask robotTask = robotTaskService.getTaskByNumber(reqDto.getTaskNumber());
            if (robotTask == null) {
                String errorMessage = "当前任务不存在，请重新输入！";
                robotTaskLogService.save(reqDto, request, false, errorMessage);
                return R.error(errorMessage);
            }
            List<TaskParameterDTO> taskParameterDTOList = robotTaskService.taskParameterDataConvert(reqDto.getTaskParameter());
            String taskParameterStr = JSON.toJSONString(taskParameterDTOList);
            List<RobotRunDTO> taskList = robotWaybillService.getTaskList(robotTask.getId(), taskParameterStr);
            if (taskList == null || taskList.size() <= 2) {
                String errorMessage = "请检查任务是否输入完整！";
                robotTaskLogService.save(reqDto, request, false, errorMessage);
                return R.error(errorMessage);
            }
            //获取运行的任务指定的机器人名字
            String vehicle = taskList.get(0).getVehicle();
            //设置运行方式
            String communicationType = "WIFI运行";
            //如果没有指定机器人，则不需要校验机器人的状态
            if (vehicle == null || vehicle.length() == 0) {
                //判断是否有机器人在线
                boolean flag = false;
                List<RobotBasicInfo> robots = robotBasicInfoService.findByName(null);
                for (RobotBasicInfo robot : robots) {
                    boolean online = robotConnectService.isOnline(robot.getCurrentIp());
                    flag = flag || online;
                }
                if (!flag) {
                    return R.error("没有机器人在线！");
                }
                //创建任务运单
                RobotCommonResVO res = robotWaybillService.createWaybill(reqDto.getMesWaybillId(), reqDto.getWmsWaybillId(), reqDto.getPalletCode(), taskList, robotTask.getId(), taskParameterStr, communicationType);
                if (res.isSuccess()) {
                    robotTaskLogService.save(reqDto, request, true, "成功");
                    return R.ok();
                } else {
                    robotTaskLogService.save(reqDto, request, false, res.getMessage());
                    return R.error(res.getMessage());
                }
            } else {
                List<RobotBasicInfo> list = robotBasicInfoService.findByName(vehicle);
                List<RobotInfoVO> robotInfoVOList = robotInfoService.dataInit(list);
                if (robotInfoVOList.size() > 0) {
                    List<TaskParameterDTO> taskParameterDTOS = JSONObject.parseArray(taskParameterStr, TaskParameterDTO.class);
                    if (taskParameterDTOS == null) {
                        String errorMessage = "输入的任务参数格式不正确！";
                        robotTaskLogService.save(reqDto, request, false, errorMessage);
                        return R.error(errorMessage);
                    }
                    //创建任务运单
                    RobotCommonResVO res = robotWaybillService.createWaybill(reqDto.getMesWaybillId(), reqDto.getWmsWaybillId(), reqDto.getPalletCode(), taskList, robotTask.getId(), taskParameterStr, communicationType);
                    if (res.isSuccess()) {
                        robotTaskLogService.save(reqDto, request, true, "成功");
                        return R.ok();
                    } else {
                        robotTaskLogService.save(reqDto, request, false, res.getMessage());
                        return R.error(res.getMessage());
                    }
                } else {
                    String errorMessage = "根据任务获取机器人名称失败！";
                    robotTaskLogService.save(reqDto, request, false, errorMessage);
                    return R.error(errorMessage);
                }
            }
        } else {
            String errorMessage = "请求输入参数为空！";
            robotTaskLogService.save(reqDto, request, false, errorMessage);
            return R.error(errorMessage);
        }
    }

    @ApiOperation(value = "运行光通信任务--内部接口")
    @RequestMapping(value = "/runDmsTaskByInner")
    public Object runDmsTaskByInner(String taskId, String taskParameterList) throws Exception {
        if (ObjectUtil.isNotEmpty(taskId)) {
            //如果taskParameterList==null，则使用默认的任务参数；反之，则使用用户实际输入的任务参数去替换默认的任务参数
            if (ObjectUtil.isEmpty(taskParameterList)) {
                taskParameterList = robotTaskService.getTaskParameterList(taskId);
            }
            //获取任务编辑中的任务列表
            List<List<RobotRunDTO>> runList = robotWaybillService.parsingTaskContent(taskId, taskParameterList);
            if (runList == null) {
                return R.error("请检查任务是否输入完整！");
            }
            List<RobotRunDTO> taskList = runList.get(0);
            //获取运行的任务指定的机器人名字
            String vehicle = taskList.get(0).getVehicle();
            //设置运行方式
            String communicationType = "DMS运行";
            //如果没有指定机器人，则不需要校验机器人的状态
            if (vehicle == null || vehicle.length() == 0) {
                //创建任务运单
                RobotCommonResVO res = robotWaybillService.createWaybill(null, null, null, taskList, taskId, taskParameterList, communicationType);
                return res.isSuccess() ? R.ok() : R.error(res.getMessage());
            } else {
                List<RobotBasicInfo> list = robotBasicInfoService.findByName(vehicle);
                List<RobotInfoVO> robotInfoVOList = robotInfoService.dataInit(list);
                if (robotInfoVOList.size() > 0) {
                    //如果机器人出去可接单状态并且无人抢占控制权（可以运行DMS任务）
                    List<TaskParameterDTO> taskParameterDTOS = JSONObject.parseArray(taskParameterList, TaskParameterDTO.class);
                    if (taskParameterDTOS == null) {
                        return R.error("输入的任务参数格式不正确!");
                    }
                    //创建任务运单
                    RobotCommonResVO res = robotWaybillService.createWaybill(null, null, null, taskList, taskId, taskParameterList, communicationType);
                    return res.isSuccess() ? R.ok() : R.error(res.getMessage());
                } else {
                    return R.error("根据任务获取机器人名称失败!");
                }
            }
        } else {
            return R.error("前端传递参数为空！");
        }
    }

    @ApiOperation(value = "运行光通信任务--外部接口")
    @RequestMapping(value = "/runDmsTask")
    public Object runDmsTask(@RequestBody RobotTaskRequestDTO reqDto) {
        if (ObjectUtil.isNotEmpty(reqDto.getTaskNumber())) {
            RobotTask robotTask = robotTaskService.getTaskByNumber(reqDto.getTaskNumber());
            String taskId = robotTask.getId();
            List<TaskParameterDTO> taskParameterDTOList = robotTaskService.taskParameterDataConvert(reqDto.getTaskParameter());
            String taskParameterList = JSON.toJSONString(taskParameterDTOList);
            //获取任务编辑中的任务列表
            List<List<RobotRunDTO>> runList = robotWaybillService.parsingTaskContent(taskId, taskParameterList);
            if (runList == null) {
                return R.error("请检查任务是否输入完整！");
            }
            List<RobotRunDTO> taskList = runList.get(0);

            //获取运行的任务指定的机器人名字
            String vehicle = taskList.get(0).getVehicle();
            //设置运行方式
            String communicationType = "DMS运行";
            //如果没有指定机器人，则不需要校验机器人的状态
            if (vehicle == null || vehicle.length() == 0) {
                //创建任务运单
                RobotCommonResVO res = robotWaybillService.createWaybill(reqDto.getMesWaybillId(), reqDto.getWmsWaybillId(), reqDto.getPalletCode(), taskList, taskId, taskParameterList, communicationType);
                return res.isSuccess() ? R.ok() : R.error(res.getMessage());
            } else {
                //如果机器人出去可接单状态并且无人抢占控制权（可以运行DMS任务）
                List<TaskParameterDTO> taskParameterDTOS = JSONObject.parseArray(taskParameterList, TaskParameterDTO.class);
                if (taskParameterDTOS == null) {
                    return R.error("输入的任务参数格式不正确!");
                }
                //创建任务运单
                RobotCommonResVO res = robotWaybillService.createWaybill(reqDto.getMesWaybillId(), reqDto.getWmsWaybillId(), reqDto.getPalletCode(), taskList, taskId, taskParameterList, communicationType);
                return res.isSuccess() ? R.ok() : R.error(res.getMessage());
            }
        } else {
            return R.error("前端传递参数为空！");
        }
    }

    //    @ControllerLog(value = "顶升机构上升")
    @ApiOperation(value = "顶升机构上升", notes = "顶升机构上升")
    @PostMapping(value = "/jackLoad")
    public Object jackLoad(String robotName) {
        List<RobotBasicInfo> robots = robotBasicInfoService.findByName(robotName);
        if (robots.size() > 0) {
            boolean flag = robotTaskService.jackLoad(robots);
            String data = (String) redisUtil.stringWithGet("robot_other_jack_load_res");
            RobotResDTO resp = JSON.parseObject(data, RobotResDTO.class);
            if (resp.getRetCode() == 0) {
                return flag ? R.ok() : R.error("顶升机构上升失败：" + robotName);
            } else {
                return R.error("顶升机构上升失败,retCode:" + resp.getRetCode() + ",errMsg:" + resp.getErrMsg());
            }
        }
        return R.error(robotName + "不存在");
    }

    //    @ControllerLog(value = "顶升机构下降")
    @ApiOperation(value = "顶升机构下降", notes = "顶升机构下降")
    @PostMapping(value = "/jackUnload")
    public Object jackUnload(String robotName) {
        List<RobotBasicInfo> robots = robotBasicInfoService.findByName(robotName);
        if (robots.size() > 0) {
            boolean flag = robotTaskService.jackUnload(robots);
            String data = (String) redisUtil.stringWithGet("robot_other_jack_unload_res");
            RobotResDTO resp = JSON.parseObject(data, RobotResDTO.class);
            if (resp.getRetCode() == 0) {
                return flag ? R.ok() : R.error("顶升机构下降失败：" + robotName);
            } else {
                return R.error("顶升机构下降失败,retCode:" + resp.getRetCode() + ",errMsg:" + resp.getErrMsg());
            }
        }
        return R.error(robotName + "不存在");
    }

    @ApiOperation(value = "保存任务编辑")
    @PostMapping(value = "/saveTaskEdit")
    public Object saveTaskEdit(String data, String id) {
        if (ObjectUtil.isNotEmpty(id) && ObjectUtil.isNotEmpty(data)) {
            RobotTask model = robotTaskService.getById(id);
            model.setTaskContent(data);
            model.setState(Constant.STATE_VALID);
            UserVO curUser = getCurUser();//当前创建人
            boolean flag = robotTaskService.save(model, curUser.getId(), curUser.getUserName());
            return flag ? R.ok() : R.error("保存任务失败");
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "保存任务配置参数")
    @PostMapping(value = "/saveTaskParameter")
    public Object saveTaskParameter(String data, String id) {
        if (ObjectUtil.isNotEmpty(id) && ObjectUtil.isNotEmpty(data)) {
            RobotTask model = robotTaskService.getById(id);
            model.setTaskParameter(data);
            model.setState(Constant.STATE_VALID);
            UserVO curUser = getCurUser();//当前创建人
            boolean flag = robotTaskService.save(model, curUser.getId(), curUser.getUserName());
            return flag ? R.ok() : R.error("保存配置参数失败");
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "获取任务配置参数")
    @PostMapping(value = "/getTaskParameter")
    public Object getTaskParameter(String id) {
        if (ObjectUtil.isNotEmpty(id)) {
            RobotTask model = robotTaskService.getById(id);
            String taskParameter;
            if (model.getTaskParameter() == null) {
                taskParameter = "[]";
            } else {
                taskParameter = model.getTaskParameter();
            }
            List<TaskParameterDTO> parameters = JSONObject.parseArray(taskParameter, TaskParameterDTO.class);
            return R.ok(parameters);
        } else {
            return R.error("前端传递参数为空");
        }
    }

    /**
     * 外部接口，用来获取任务列表和任务参数信息
     *
     * @return
     */
    @ApiOperation(value = "获取任务列表")
    @PostMapping(value = "/getTaskList")
    public Object getTaskList() {
        List<RobotTaskVO> list = robotTaskService.getTaskInfo();
        return R.ok(list);
    }

    @ApiOperation("测试动态创建定时任务")
    @PostMapping("/startJob")
    public Object createJob(String cron, String key) {
        if (StringUtils.isBlank(cron)) {
            //默认一秒执行一次
            cron = "*/1 * * * * ?";
        }
        String tempCron = cron;
        customScheduledTaskRegistrar.addTriggerTask(
                key, () -> log.info("key:{},开始执行定时任务---{}", key, tempCron),
                triggerContext -> new CronTrigger(tempCron).nextExecutionTime(triggerContext));
        return R.ok(tempCron);
    }

    @ApiOperation("测试动态删除定时任务")
    @PostMapping("removeJob")
    public R removeJob(String key) {
        customScheduledTaskRegistrar.removeTriggerTask(key);
        return R.ok();
    }

    @ApiOperation("测试指定路径导航")
    @PostMapping("/testAppointNav")//测试指定路径导航
    public R testAppointNav() throws InterruptedException {
        Map<String, Thread> map = TcpClientThread.getIpThreadNetty();
        String robotIp = "192.168.13.118";
        String navCommandString = "{\"move_task_list\":[{\"source_id\":\"AP43\",\"id\":\"AP45\",\"task_id\":\"12344325\"},{\"source_id\":\"AP45\",\"id\":\"AP47\",\"task_id\":\"12344326\"},{\"source_id\":\"AP47\",\"id\":\"AP45\",\"task_id\":\"12344327\"},{\"source_id\":\"AP45\",\"id\":\"AP43\",\"task_id\":\"12344328\"}]}";
        log.info(navCommandString);
        String navCommandHex = DataConvertUtil.convertStringToHex(navCommandString);
        // 报文长度：实际传输或处理时，数据是以字节为单位的。length()方法返回的是字符数（4位），而一个字节是八位，占两个字符，所以要除以 2
        int byteLength = navCommandHex.length() / 2;
        /*
         *  %04x 格式说明符含义：
         *  0：用零填充左侧空白。
         *  4：总宽度为4位。
         *  x：小写十六进制格式。
         */
        String navCommandLength = String.format("%04x", byteLength);
        /**
         *  <指定路径导航报文>
         *  报文 = 固定头部 + 可变数据部分
         *  头部包含协议版本、命令码、数据长度等元信息
         *  可变数据部分为 JSON 格式（需转十六进制）
         */
        String testInstruction = "5A0100010000" + navCommandLength + "0BFA000000000000" + navCommandHex;
        if (!map.containsKey(robotIp + PortConstant.ROBOT_NAVIGATION_PORT)) {
            //若该ip端口没有tcp连接，则创建tcp连接
            robotBasicInfoService.connectTcp(robotIp, PortConstant.ROBOT_NAVIGATION_PORT);
        }
        // 向该ip端口发送tcp指令
        EntityResult response = null;
        int retryCount = 0;
        do {
            // 发送TCP指令
            response = TcpClientThread.sendHexMsg(robotIp, PortConstant.ROBOT_NAVIGATION_PORT, testInstruction);

            // 200时或已达最大重试次数时退出循环
            if (response.isSuccess() || retryCount >= 3) {
                break;
            }

            // 等待0.5秒后重试
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            retryCount++;
        } while (!Thread.currentThread().isInterrupted());
        return response.isSuccess() ? R.ok() : R.error("指定导航错误");
    }


    @ApiOperation("测试动作脚本重构")
    @PostMapping("/testRefactorJson")
    public EntityResult testRefactorJson(@RequestBody String customizedActionsRawJson) {
        // 示例dragMap
        Map<String, String> dragMap = new HashMap<>();
        dragMap.put("jackUpPoint", "AP43");
        dragMap.put("diPoint", "AP50");
        dragMap.put("dmsPoint", "AP99");

        // 执行重构
        String customizedActionsNewJson = robotTaskService.refactorJson(customizedActionsRawJson, dragMap);
        log.info("重构后的自定义动作脚本: {}", customizedActionsNewJson);
//        RobotTaskListDTO robotTaskListDTO = robotTaskService.reconstructRobotTask("测试任务链重构任务", customizedActionsNewJson);
//        log.info("重构后的任务链DTO: {}", JSON.toJSONString(robotTaskListDTO));
        return new EntityResult().setData(customizedActionsNewJson);
    }
}
