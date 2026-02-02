package com.robotCore.task.tcpCilent;

import com.entity.EntityResult;
import com.robotCore.common.constant.ChargeConstant;
import com.robotCore.common.constant.Constant;
import com.robotCore.common.constant.PortConstant;
import com.robotCore.common.constant.ProtocolConstant;
import com.robotCore.multiScheduling.entity.RobotMultiWaybill;
import com.robotCore.multiScheduling.service.RobotMultiWaybillService;
import com.robotCore.robot.entity.RobotAttribute;
import com.robotCore.robot.entity.RobotBasicInfo;
import com.robotCore.robot.entity.RobotDms;
import com.robotCore.robot.service.*;
import com.robotCore.scheduing.common.config.CustomScheduledTaskRegistrar;
import com.robotCore.scheduing.common.enums.ControlStatusEnum;
import com.robotCore.scheduing.common.enums.RobotMapPointAttrEnum;
import com.robotCore.scheduing.common.enums.RobotStatusEnum;
import com.robotCore.scheduing.common.enums.RobotWaybillStatusEnum;
import com.robotCore.scheduing.entity.RobotWaybill;
import com.robotCore.scheduing.service.RobotWaybillService;
import com.robotCore.scheduing.vo.RobotInfoVO;
import com.robotCore.task.core.ProtocolConvert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * 
 * @Description: 静态键值对 Date: 2020-8-10 11:21:29
 */
@Slf4j
@Component
public final class ClientStartSocket implements ApplicationListener<ContextRefreshedEvent> {

	@Autowired
	private CustomScheduledTaskRegistrar customScheduledTaskRegistrar;

	@Autowired
	private RobotBasicInfoService robotBasicInfoService;

	@Autowired
	private RobotWaybillService robotWaybillService;

	@Autowired
	private RobotRealTimeInfoService robotRealTimeInfoService;

	@Autowired
	private RobotAttributeService robotAttributeService;

	@Autowired
	private RobotDmsService robotDmsService;

	@Autowired
	private RobotConnectService robotConnectService;

	@Autowired
	private RobotMultiWaybillService robotMultiWaybillService;

	@Value("${project.name}")
	private String projectName;

	@Value("${equipment.isIntelligentChargingStation}")
	private Boolean isIntelligentChargingStation;

	private boolean flag = true;
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		//WIFI和光通信自动充电逻辑
		String autoChargeKey = "robot_auto_charge";
		//每五分钟执行一次
		String autoChargeCron = "0 0/30 * * * ?";
		//机器人开始连接的时候，开启定时任务，每5分钟监测一下机器人充电逻辑
		customScheduledTaskRegistrar.addTriggerTask(
				autoChargeKey,
				//()->log.info("key:{},开始执行定时任务---{}", instruction, cron),
				() -> {
					try {
						List<RobotBasicInfo> robotBasicInfos = robotBasicInfoService.findListAll();
						for (RobotBasicInfo robotBasicInfo : robotBasicInfos) {
							//获取机器人光通信站待命点属性
							List<RobotAttribute> robotBindAttr = robotAttributeService.getRobotBindAttr(robotBasicInfo.getVehicleId(), RobotMapPointAttrEnum.DMS_PARK_POINT.getCode());
							//robotBindAttr != null,则表明当前机器人支持光通信运行
							if (robotBindAttr != null) {
								//运行光通信自动充电
								robotBasicInfoService.autoDmsCharge(robotBasicInfo.getVehicleId(), robotBindAttr.get(0).getPoint());
							} else {
								//运行WIFI自动充电
								robotBasicInfoService.autoCharge(robotBasicInfo.getVehicleId());
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				},
				triggerContext -> new CronTrigger(autoChargeCron).nextExecutionTime(triggerContext));

		//执行机器人任务队列
		if(flag){
			String key = "robot_task_list";
			String cron = "*/3 * * * * ?";
			customScheduledTaskRegistrar.addTriggerTask(
					key,
					//()->log.info("key:{},开始执行定时任务---{}", instruction, cron),
					() -> {
						try {
							//获取待执行运单列表
							List<RobotWaybill> toBeExecutedWaybill = robotWaybillService.getToBeExecutedWaybill();
							if (!toBeExecutedWaybill.isEmpty()) {
                                // 8610test
                                if(toBeExecutedWaybill.size() == 1){
                                    log.info("有1个待执行的运单:{}",toBeExecutedWaybill.get(0));
                                }
                                if(toBeExecutedWaybill.size() > 1){
                                    log.info("有{}个待执行的运单:{}",toBeExecutedWaybill.size(),toBeExecutedWaybill);
                                }

								RobotWaybill robotWaybill = toBeExecutedWaybill.get(0);
								//如果任务中指定了机器人名称和分组，则按照指定的信息匹配
								List<RobotBasicInfo> effectiveRobots = robotBasicInfoService.getEffectiveRobots(robotWaybill.getRobotName(), robotWaybill.getRobotGroupName());
								for (RobotBasicInfo robot : effectiveRobots) {
									//如果机器人在线
									String currentIp = robot.getCurrentIp();
									//如果运单的执行方式是wifi运行
									if ("WIFI运行".equals(robotWaybill.getCommunicationType())) {
										InetAddress geek = InetAddress.getByName(currentIp);
										if (geek.isReachable(1000)) {
											Map<String, Thread> map = TcpClientThread.getIpThreadNetty();
											//开始机器人任务时，先确认定位
											if (!map.containsKey(currentIp + PortConstant.ROBOT_CONTROL_PORT)) {
												robotBasicInfoService.connectTcp(currentIp, PortConstant.ROBOT_CONTROL_PORT);
											}
											EntityResult result = TcpClientThread.sendHexMsg(currentIp, PortConstant.ROBOT_CONTROL_PORT, ProtocolConstant.CONFIRM_LOC);
											//设置标志位
											boolean currentFlag = result.isSuccess();
											//在推送机器人实时信息之前先配置推送端口，后续需要注意是否判断是否配置成功
											boolean pushFlag = robotRealTimeInfoService.pushMsgConfig(currentIp);
											currentFlag = pushFlag && currentFlag;
											if (!map.containsKey(currentIp + PortConstant.ROBOT_PUSH_PORT)) {
												//配置推送端口之后，开始推送
												robotRealTimeInfoService.getRealTimeStatusInfo(currentIp);
											}
											//获取机器人实时状态信息
											RobotInfoVO robotStatus = robotWaybillService.getRobotStatus(robot);
											//抢占机器人控制权
											if (robotStatus != null && (ControlStatusEnum.UN_PREEMPTED_CONTROL.getCode().equals(robotStatus.getControlState())
													|| ControlStatusEnum.LOSS_OF_CONTROL.getCode().equals(robotStatus.getControlState()))) {
												boolean acquireControl = robotBasicInfoService.acquireControl(robot);
												currentFlag = currentFlag && acquireControl;
											}

											//由于8610机器人只能手动充电，因此在充电时不能执行任务
											//如果机器人处于可接单和已抢占控制权状态,则执行机器人任务
											if (currentFlag && robotStatus != null && !robotStatus.isCharging() && RobotStatusEnum.ORDER_AVAILABLE.getCode().equals(robotStatus.getOrderState()) &&
													ControlStatusEnum.SEIZED_CONTROL.getCode().equals(robotStatus.getControlState())) {
												//关闭其它机器人端口
												for (RobotBasicInfo effectiveRobot : effectiveRobots) {
													if (!effectiveRobot.getCurrentIp().equals(robotStatus.getCurrentIp())) {
														String typeName = robotConnectService.getRobotType(effectiveRobot.getCurrentIp());
														ProtocolConvert.disConnect(typeName, effectiveRobot.getCurrentIp());
													}
												}
												//开始机器人连接
												if (!map.containsKey(currentIp + PortConstant.ROBOT_STATUS_PORT)) {
													robotBasicInfoService.connectTcp(currentIp, PortConstant.ROBOT_STATUS_PORT);
												}
												//连接机器人导航端口
												if (!map.containsKey(currentIp + PortConstant.ROBOT_NAVIGATION_PORT)) {
													//连接机器人导航端口
													robotBasicInfoService.connectTcp(currentIp, PortConstant.ROBOT_NAVIGATION_PORT);
												}
												//执行机器人wifi运单任务，设置当前运单选择的机器人名称和分组
												robotWaybill.setRobotName(robot.getVehicleId());
												robotWaybill.setRobotGroupName(robot.getGroupName());
												//设置机器人的接单时间
												robotWaybill.setOrderTime(new Timestamp(System.currentTimeMillis()));
												//设置运单的状态为正在执行
												robotWaybill.setWaybillStatus(RobotWaybillStatusEnum.EXECUTING.getCode());
												robotWaybillService.saveOrUpdate(robotWaybill);
												//如果由MES下发的指令，则同步完成状态给MES系统;如果是WMS系统，则同步完成状态给WMS系统
												robotWaybillService.completeTaskSyncToOtherSystem(robotWaybill.getMesWaybillId(), robotWaybill.getWmsWaybillId());
												//执行wifi机器人任务
												robotWaybillService.runRobotWifiTask(robotWaybill, robot.getVehicleId(), robot.getGroupName());
												break;
											}
										}
									}

									//如果通信方式为光通信
									if ("DMS运行".equals(robotWaybill.getCommunicationType())){
										//设置机器人的接单时间
										robotWaybill.setOrderTime(new Timestamp(System.currentTimeMillis()));
										//设置运单的状态为正在执行
										robotWaybill.setWaybillStatus(RobotWaybillStatusEnum.EXECUTING.getCode());
										//设置当前运单选择的机器人名称和分组
										robotWaybill.setRobotName(robot.getVehicleId());
										robotWaybill.setRobotGroupName(robot.getGroupName());
										robotWaybillService.saveOrUpdate(robotWaybill);
										//设置机器人的状态为非空闲
										RobotBasicInfo robotBasicInfo = robotBasicInfoService.findByName(robot.getVehicleId()).get(0);
										robotBasicInfo.setLeisure(Constant.STATE_INVALID);
										robotBasicInfoService.saveOrUpdate(robotBasicInfo);
										//同步运单执行状态到第三方系统
										robotWaybillService.completeTaskSyncToOtherSystem(robotWaybill.getMesWaybillId(), robotWaybill.getWmsWaybillId());

										//获取机器人相关的光通信站属性
										List<RobotAttribute> robotBindAttr = null;
										//如果是充电完成
										if (ChargeConstant.ROBOT_END_CHARGE.equals(robotWaybill.getTaskName())) {
											//获取机器人相关的光通信站属性
											robotBindAttr = robotAttributeService.getRobotBindDmsAttr(robot.getVehicleId(), 4);
											//如果智能充电桩
											if (isIntelligentChargingStation) {
												//充电完成，关闭充电桩插板开启指令的定时任务
												customScheduledTaskRegistrar.removeTriggerTask(robotWaybill.getRobotName() + ChargeConstant.ROBOT_START_CHARGE_KEY);
											}
											//设置机器人充电状态
											robot.setLeisure(Constant.STATE_INVALID);
											robot.setCharge(0);
											robotBasicInfoService.saveOrUpdate(robot);
										} else {
											robotBindAttr = robotAttributeService.getRobotBindDmsAttr(robot.getVehicleId(), 6);
											//如果机器人开始充电，设置机器人充电状态
											if (ChargeConstant.ROBOT_START_CHARGE.equals(robotWaybill.getTaskName())) {
												//设置机器人充电状态
												robot.setLeisure(Constant.STATE_INVALID);
												robot.setCharge(1);
												robotBasicInfoService.saveOrUpdate(robot);
											}
										}
										if (robotBindAttr.size() <= 0) {
											//设置运单执行状态
											robotWaybillService.setWaybillResult(robotWaybill,RobotWaybillStatusEnum.TERMINATE.getCode(),robot.getVehicleId() + "机器人没有绑定光通信站信息");
											//同步运单执行状态到第三方系统
											robotWaybillService.completeTaskSyncToOtherSystem(robotWaybill.getMesWaybillId(), robotWaybill.getWmsWaybillId());
										} else {
											//获取当前站点的光通信站信息
											RobotDms robotDms = robotDmsService.getDmsByDmsPoint(robotBindAttr.get(0).getPoint());
											if (robotDms == null) {
												//设置运单执行状态
												robotWaybillService.setWaybillResult(robotWaybill,RobotWaybillStatusEnum.TERMINATE.getCode(),robot.getVehicleId() + "机器人没有绑定光通信站信息");
												//同步运单执行状态到第三方系统
												robotWaybillService.completeTaskSyncToOtherSystem(robotWaybill.getMesWaybillId(), robotWaybill.getWmsWaybillId());
											} else {
												//判断当前光通信站是否在线
												InetAddress geek = InetAddress.getByName(robotDms.getDmsIp());
												if (geek.isReachable(1000)) {
													if ("359厂复合材料法兰件自动热压设备项目".equals(projectName) || "359厂外防热自动刮涂成型系统项目".equals(projectName)
													 || "西安航天自动化车间轻载辊筒".equals(projectName) || "41所点火药盒自动化生产单元(二期)".equals(projectName)) {
														//用于359厂复合材料法兰件自动热压设备项目
														robotWaybillService.runRobotDmsTaskByStep(robotWaybill, robotDms.getDmsIp());
													} else {
														//运行光通信运行的多机器人任务
														robotWaybillService.runMultiRobotsDmsTask(robotWaybill, robotDms.getDmsIp());
													}
													break;
												} else {
													//设置运单执行状态
													robotWaybillService.setWaybillResult(robotWaybill,RobotWaybillStatusEnum.TERMINATE.getCode(),robotDms.getDmsIp()+ "光通信站没有在线");
													//同步运单执行状态到第三方系统
													robotWaybillService.completeTaskSyncToOtherSystem(robotWaybill.getMesWaybillId(), robotWaybill.getWmsWaybillId());
												}
											}
										}
									}
								}
							}

							//执行多品牌调度系统综合管控处理
							if (true) {
								robotMultiWaybillService.runRobotMultipleWaybill();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					},
					triggerContext -> new CronTrigger(cron).nextExecutionTime(triggerContext));
		}
	}
}
