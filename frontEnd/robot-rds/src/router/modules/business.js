// 实时检测
export default [
  {//运行监控
    "name": "/agv/operationMonitoring",
    "package": "views/agv/monitor/operationMonitoring"
  }, 
  {//机器人连接
    "name": "/home",
    "package": "views/home/index"
  }, 
   {//机器人类型
    "name": "/robotType",
    "package": "views/home/type/list"
  }, 
  {//调度编辑器
    "name": "/editor/robot",
    "package": "views/editor/index",
    // "meta": { keepAlive: true }
  }, 
  {//光通信
    "name": "/editor/dms",
    "package": "views/editor/dms/list",
    // "meta": { keepAlive: true }
  }, 
  {//热成像仪
    "name": "/editor/imager",
    "package": "views/editor/imager/list",
    // "meta": { keepAlive: true }
  }, 
  {//自动门
    "name": "/editor/iotEquipment/automaticDoor",
    "package": "views/editor/iotEquipment/list",
    // "meta": { keepAlive: true }
  },
   {//电梯
    "name": "/editor/iotEquipment/elevator",
    "package": "views/editor/iotEquipment/list",
    // "meta": { keepAlive: true }
  },

   {//智能充电桩
    "name": "/editor/chargePile",
    "package": "views/editor/chargePile/list",
    // "meta": { keepAlive: true }
  }, 
  {//任务编辑
    "name": "/agv/taskEdit",
    "params": "id",
    "package": "views/agv/taskEdit",
    // "meta": { keepAlive: true }
  }, 
  {//天风任务
    "name": "/agv/task",
    "package": "views/agv/task",
    // "meta": { keepAlive: true }
  }, 
  {//AGV机器人
    "name": "/agv/agvrobot",
    "package": "views/agv/robot/list",
    // "meta": { keepAlive: true }
  }, 
   {//运单管理
    "name": "/agv/waybillManage",
    "package": "views/agv/waybill/list",
    // "meta": { keepAlive: true }
  },
   {//任务日志管理
    "name": "/agv/taskLog",
    "package": "views/agv/taskLog/list",
    // "meta": { keepAlive: true }
  }, 
  {//天风任务
    "name": "/agv/task",
    "params": "taskId",
    "package": "views/agv/task",
    // "meta": { keepAlive: true }
  }, 
  {
    name: '/person/view',
    package:"views/human/person/view"
  },
  {//巡检-运行监控
    "name": "/patrol/monitor",
    "package": "views/patrol/monitor",
  }, 

  /* ================= 我的工作台 start =============== */
  {//消息列表
      "name": "/workbench/message",
      "package": "views/workbench/message/list",
      "meta": { keepAlive: true }
  }, 
  {//站内通知公告
      "name": "/workbench/notice",
      "package": "views/workbench/notice/list",
      "meta": { keepAlive: true }
  }, 
  {//新增站内通知公告
    "name": "/workbench/createNotice",
    "package": "views/workbench/notice/create"
  }, 
  {//站内通知公告查看
      "name": "/workbench/viewNotice",
      "params": "id",
      "package": "views/workbench/notice/view"
  }, 
  {//告警列表
      "name": "/workbench/warning",
      "package": "views/workbench/warning/list"
  },
  /* ================= 我的工作台 end =============== */

  /************系统设置 start *************/
  {
  "name": "/account/list", //账户管理
  "package": "views/system/account/list"
  }, 
  {
  "name": "/module/list", //模块管理
  "package": "views/system/moudle/list",
  "meta": { keepAlive: true }
  }, 
  {
  "name": "/role/listRole", //角色管理
  "package": "views/system/role/list"
  }, 
  {
  "name": "/role/roleToAuthority", //角色配置
  "params": "id",
  "package": "views/system/role/roleToAuthority"
  }, 
  {
  "name": "/role/viewRoleAuthority", //角色配置查看
  "params": "id",
  "package": "views/system/role/roleToAuthorityView"
  }, 
  {
  "name": "/operation/list", //菜单操作
  "package": "views/system/operation/list"
  },
  {//系统日志 - 操作日志 - 列表
  "name": "/sysLog/list", 
  "package": "views/system/sysLog/opeLog/list"
  },
  {//系统日志 - 操作日志 - 查看
    "name": "/sysLog/view", 
    "params": "id",
    "package": "views/system/sysLog/opeLog/cur"
  },
  {//系统日志 - 异常日志 - 列表
    "name": "/sysExcLog/list", 
    "package": "views/system/sysLog/excLog/list"
  },
  {//系统日志 - 异常日志 - 查看
    "name": "/sysExcLog/view", 
    "params": "id",
    "package": "views/system/sysLog/excLog/cur"
  },
  {
  "name": "/sysData/list", //数字字典内容
  "package": "views/system/sysData/list"
  },
  {
  "name": "/sysDataType/list", //数据字典类型
  "package": "views/system/sysDataType/list"
  }, 
  { //文件模板
  "name": "/fileTemplate/list",
  "package": "views/system/fileTemplate/list"
  },
  { //SSO认证配置
  "name": "/ssoService/list",
  "package": "views/system/sso/list"
  },
  { //系统配置
  "name": "/sysInfo/list",
  "package": "views/system/sysInfo/list"
  },
  { //系统配置
    "name": "/mobileVersion/list",
    "package": "views/system/mobileVersion/list"
  },
  { //数据管理
      "name": "/datamanage/list",
      "package": "views/system/datamanage/list"
  },
  /************系统设置 end *************/

/************待办列表 start *************/

{
  "name":"/workFlowTask/list",
  "package": "views/system/workFlowTask/list"
},
  
//办理任务
{
  "name":"/workFlowTask/create",
  "params": "id,type",
  "package": "views/system/workFlowTask/taskCur"
},
{
  "name":"/workFlowTask/update",
  "params": "id,type",
  "package": "views/system/workFlowTask/taskCur"
},
{
  "name":"/workFlowTask/view",
  "params": "id,type",
  "package": "views/system/workFlowTask/taskCur"
},
 /************待办列表 end *************/

  /************人事管理 start *************/
  {
  "name": "/organization/list",//组织机构管理
  "package": "views/human/organization/list"
  },
  { //人员管理
  "name": "/user/list",
  "package": "views/human/userInfo/list"
  },
  { //人员管理新增
  "name": "/user/create", 
  "package": "views/human/userInfo/cur"
  },
  { //人员管理查看
  "name": "/user/view",
  "params": "id,type",
  "package": "views/human/userInfo/cur"
  },
  { //人员管理修改
  "name": "/user/update", 
  "params": "id,type",
  "package": "views/human/userInfo/cur"
  },
  { //企业列表
  "name": "/company/list",
  "package": "views/human/company/list"
  },
  { //企业新增
  "name": "/company/create",
  "package": "views/human/company/cur"
  },
  { //企业修改
  "name": "/company/update",
  "params": "id,type",
  "package": "views/human/company/cur"
  },
  { //企业查看
  "name": "/company/view",
  "params": "id,type",
  "package": "views/human/company/cur"
  },
  { //职务列表
  "name": "/position/list",
  "package": "views/human/position/list"
  },
  { //岗位列表
  "name": "/post/list",
  "package": "views/human/post/list"
  },
  { //工种列表
  "name": "/profession/list",
  "package": "views/human/profession/list"
  },
  {//统计分析
  "name": "/human/statisticsSex",
  "package": "views/human/statistics/sex"
  }, 
  {
  "name": "/human/statisticsAge",
  "package": "views/human/statistics/age"
  }, 
  {
  "name": "/human/statisticsPost",
  "package": "views/human/statistics/post"
  }, 
  {
  "name": "/human/statisticsEducation",
  "package": "views/human/statistics/education"
  }, 
  {
  "name": "/human/statisticsTechnical",
  "package": "views/human/statistics/technical"
  },
  /*================= 人事管理 end ====================*/


  /*===================== 呼叫中心 start ==================*/
  { //事件回访列表
    "name": "/listVist/list",
    "package": "views/callcenter/revisit/list",
  },
  { //事件登记列表
    "name": "/registEvent/list",
    "package": "views/callcenter/registEvent/list",
  }, 
  { //新增事件登记
    "name": "/registEvent/create",
    "package": "views/callcenter/registEvent/cur"
  }, 
  { //修改事件登记
    "name": "/registEvent/update",
    "params": "id,type",
    "package": "views/callcenter/registEvent/cur"
  }, 
  { //事件登记查看
    "name": "/registEvent/view",
    "params": "id,type",
    "package": "views/callcenter/registEvent/cur"
  },
  { //事件确认列表
    "name": "/realCheckList/list",
    "package": "views/callcenter/realCheckList/list",
  },
  { //事件确认跳转列表
    "name": "/realCheckList/firstCheckDoTask",
    "package": "views/callcenter/realCheckList/cur",
    "params": "id",
  },
  { //事件确认跳转列表
    "name": "/realCheckList/firstCheckView",
    "package": "views/callcenter/realCheckList/cur",
    "params": "id,type",
  },
  { //事件回访确认
    "name": "/revisit/visit",
    "package": "views/callcenter/revisit/cur",
    "params": "id,type",
  },
  { //事件回访查看
    "name": "/revisit/view",
    "package": "views/callcenter/revisit/cur",
    "params": "id,type",
  },
  { //事件办结
    "name": "/revisit/endview",
    "package": "views/callcenter/revisit/cur",
    "params": "id,type",
  },
  { //保养计划列表
    "name": "/repairPlan/list",
    "package": "views/callcenter/repairPlan/list",
  },
  { //保养计划列表
    "name": "/repairPlan/create",
    "package": "views/callcenter/repairPlan/cur",
  },
  { //创建工单
    "name": "/createMenu/list",
    "package": "views/callcenter/createMenu/list",
  },
  { //工单汇报
    "name": "/workorderReport/list",
    "package": "views/callcenter/workorderReport/list",
  },
  { //事件类别
    "name": "/category/list",
    "package": "views/callcenter/category/list"
  }, 
  { //事件查询
    "name": "/supervise/historyList",
    "package": "views/callcenter/supervise/historyList"
  }, 
  { //督察督办
    "name": "/supervise/supervisionList",
    "package": "views/callcenter/supervise/supervisionList"
  },
  { //发布督办意见
    "name": "/supervise/supervisionOper",
    "params": "id",
    "package": "views/callcenter/supervise/supervisionOper"
  }, 
  { //事件归档
    "name": "/archive/archiveHandle",
    "package": "views/callcenter/archive/archiveHandle"
  },
  { //归档历史
    "name": "/archive/archiveHistory",
    "package": "views/callcenter/archive/archiveHistory"
  },
  { //事件台账
    "name": "/statistics/ledgerList",
    "package": "views/callcenter/statistics/ledgerList"
  },
  { //处理上报统计
    "name": "/statistics/eventCount",
    "package": "views/callcenter/statistics/eventCount"
  }, 
  {//事件类别统计
    "name": "/statistics/typeCount",
    "package": "views/callcenter/statistics/typeCount"
  },
  {//事件性质统计
    "name": "/statistics/natureCount",
    "package": "views/callcenter/statistics/natureCount"
  },
  /*===================== 呼叫中心 end ====================*/


  /*===================== start 巡检管理=====================*/
  { //参数设置 - 巡检规程 - 列表
    "name": "/inspType/list",
    "package": "views/insp/inspType/list",
  }, 
  { //参数设置 - 巡检规程 - 新增规程
    "name": "/inspType/create",
    "package": "views/insp/inspType/cur",
  }, 
  { //参数设置 - 巡检规程 - 修改规程
    "name": "/inspType/update",
    "params": "id,type",
    "package": "views/insp/inspType/cur",
  }, 
  { //参数设置 - 巡检规程 - 查看规程
    "name": "/inspType/view",
    "params": "id,type",
    "package": "views/insp/inspType/cur",
  },
  {//巡检组
    "name": "/inspTeams/list",
    "package": "views/insp/inspTeams/list",
  },
  {//巡检计划 - 创建
    "name": "/inspPlan/create",
    "package": "views/insp/inspPlan/cur",
  },
  {//巡检计划 - 修改
    "name": "/inspPlan/update",
    "params": "id,type",
    "package": "views/insp/inspPlan/cur",
  },
  {//巡检计划 - 查看
    "name": "/inspPlan/view",
    "params": "id,type",
    "package": "views/insp/inspPlan/cur",
  },
  {//巡检计划启停
    "name": "/inspPlan/list",
    "package": "views/insp/inspPlan/list",
  },
  {//巡检线路 - 列表启停
    "name": "/inspLine/list",
    "package": "views/insp/inspLine/list",
  },
  {//巡检线路 - 新增
    "name": "/inspLine/create",
    "package": "views/insp/inspLine/cur",
  },
  {//巡检线路 - 修改
    "name": "/inspLine/update",
    "params": "id,type",
    "package": "views/insp/inspLine/cur",
  },
  {//巡检线路 - 查看
    "name": "/inspLine/view",
    "params": "id,type",
    "package": "views/insp/inspLine/cur",
  },
  {//巡检记录--列表
    "name": "/inspLine/hisList",
    "package": "views/insp/inspLine/hisList",
  },
  {//巡检记录--查看
    "name": "/inspLine/hisView",
    "params": "id",
    "package": "views/insp/inspLine/hisView",
  },
  {//巡检工单--列表
    "name": "/inspLine/jobList",
    "package": "views/insp/inspLine/jobList",
  },
  {//巡检工单--查看
    "name": "/inspLine/jobView",
    "params": "id",
    "package": "views/insp/inspLine/jobView",
  },
  { //巡检管理 - 主动上报 - 上报工单
    "name": "/reportJob/list",
    "package": "views/insp/reportJob/list"
  },
  { //巡检管理 - 主动上报 - 工单查看
    "name": "/reportJob/view",
    "params": "id",
    "package": "views/insp/reportJob/view"
  },
  { //巡检管理 - 主动上报 - 上报对象
    "name": "/reportJob/objectList",
    "package": "views/insp/reportJob/objectList"
  },
  { //巡检任务 - 任务工单 - 任务 - 列表
    "name": "/inspTask/list",
    "package": "views/insp/inspTask/list",
  }, 
  { //巡检任务 - 任务工单 - 任务 - 新增任务
    "name": "/inspTask/create",
    "package": "views/insp/inspTask/cur",
  }, 
  { //巡检任务 - 任务工单 - 任务 - 新增任务
    "name": "/inspTaskTodo/list",
    "package": "views/insp/inspTaskTodo/list",
  }, 
  { //巡检任务 - 任务工单 - 任务 - 新增任务
    "name": "/inspTaskTodo/todo",
    "params": "id",
    "package": "views/insp/inspTaskTodo/cur",
  }, 
  { //巡检任务 - 任务工单 - 任务 - 查看任务
    "name": "/inspTask/view",
    "params": "id,pageName",
    "package": "views/insp/inspTask/view",
  },  
  { //巡检管理 - 统计分析 - 巡检任务台账 
    "name": "/statistics/inspAccounts",
    "package": "views/insp/statistics/inspAccounts",
  },
  { //巡检管理 - 统计分析 - 巡检工单台账 
    "name": "/statistics/jobAccounts",
    "package": "views/insp/statistics/jobAccounts",
  },
  { //巡检管理 - 统计分析 - 巡检工单台账-查看 
    "name": "/statistics/jobView",
    "params": "id,pageName",
    "package": "views/insp/statistics/jobView",
  },
  { //巡检管理 - 统计分析 - 组任务记录 
    "name": "/statistics/inspTeamTask",
    "package": "views/insp/statistics/inspTeamTask",
  },
  { //巡检管理 - 统计分析 - 个人任务报表 
    "name": "/statistics/inspUserJob",
    "package": "views/insp/statistics/inspUserJob",
  },

  /*===================== end 巡检管理=====================*/

  /*===================== start 项目管理=====================*/
  { //项目信息列表
    "name": "/projectInfo/list",
    "package": "views/project/projectInfo/list",
  }, 
  { //新增项目
    "name": "/projectInfo/create",
    "params": "type",
    "package": "views/project/projectInfo/cur"
  }, 
  { //修改项目信息
    "name": "/projectInfo/update",
    "params": "id,type",
    "package": "views/project/projectInfo/cur"
  }, 
  { //查看项目信息
    "name": "/projectInfo/view",
    "params": "id,type",
    "package": "views/project/projectInfo/cur" 
  },  
  { //查看项目信息日志列表
    "name": "/projectInfo/viewLog", 
    "params": "id",
    "package": "views/project/projectInfo/logList" 
  }, 
  { //未施工项目
    "name": "/project/buildList",
    "package": "views/project/projectBuild/list"
  }, 
  { //未完工项目
    "name": "/project/finishList",
    "package": "views/project/projectFinish/list"
  }, 
  { //未竣工项目  
    "name": "/project/endList",
    "package": "views/project/projectEnd/list"
  },
  { //竣工项目查看
    "name": "/project/endListView",
    "package": "views/project/projectEndView/list"
  },
  { //草稿项目
    "name": "/project/draftList",
    "package": "views/project/projectDrafts/list"
  }, 
  { //项目日志列表
    "name": "/project/logList",
    "package": "views/project/projectLog/list"
  }, 
  { //新增项目日志
    "name": "/projectLog/create",
    "package": "views/project/projectLog/cur"
  },
  { //查看项目日志
    "name": "/projectLog/view",
    "params": "id,type",
    "package": "views/project/projectLog/cur" 
  }, 
  { //修改项目日志
    "name": "/projectLog/update",
    "params": "id,type",
    "package": "views/project/projectLog/cur"
  },
  /*===================== end 项目管理=====================*/

  /*===================== start 应急预案=====================*/
  { //预案管理列表
    "name": "/planManage/list",
    "package": "views/emerg/planManage/list",
  },
  { //预案管理新增
    "name": "/planManage/create",
    "package": "views/emerg/planManage/cur",
  },
  { //预案管理修改
    "name": "/planManage/update",
    "params": "id,type",
    "package": "views/emerg/planManage/cur",
  },
  { //预案管理查看
    "name": "/planManage/view",
    "params": "id,type",
    "package": "views/emerg/planManage/cur",
  },
  /*===================== end 应急预案=====================*/


  /*===================== 短信管理 start ====================*/
  { //预置模板 - 列表
    "name": "/masModel/list",
    "package": "views/message/masModel/list/"
  }, 
  { //预置模板 - 新增
    "name": "/masModel/create",
    "package": "views/message/masModel/cur/"
  }, 
  { //预置模板 - 修改
    "name": "/masModel/update",
    "params": "id,type",
    "package": "views/message/masModel/cur/"
  }, 
  { //预置模板 - 查看
    "name": "/masModel/view",
    "params": "id,type",
    "package": "views/message/masModel/cur/"
  }, 
  { //短信发送 - 列表
    "name": "/masSend/list",
    "package": "views/message/masSend/list/",
  }, 
  { //短信发送 - 新增
    "name": "/masSend/create",
    "package": "views/message/masSend/cur/",
  }, 
  { //短信发送 - 修改
    "name": "/masSend/update",
    "params": "id,type",
    "package": "views/message/masSend/cur/",
  }, 
  { //短信发送 - 查看
    "name": "/masSend/view",
    "params": "id,type",
    "package": "views/message/masSend/cur/",
  },
  { //短信记录 - 列表
    "name": "/masRecord/list",
    "package": "views/message/masRecord/list/"
  }, 
  { //短信统计 - 列表
    "name": "/masCount/list",
    "package": "views/message/masCount/list/"
  },
  /*===================== 短信管理 end ====================*/

  /*===================== 图档管理 start ====================*/
  { //图档类别 - 列表
    "name": "/documentType/list",
    "package": "views/document/type/list"
  },
  { //图档目录 - 列表
    "name": "/documentDirectory/list",
    "package": "views/document/directory/list"
  },
  { //图档文件 - 列表
    "name": "/documentFile/list",
    "package": "views/document/file/list"
  },
  { //图档文件 new - 列表
    "name": "/documentFileNew/list",
    "package": "views/document/docFile/list"
  },
  { //图档文件 new - 列表
    "name": "/documentBorrow/list",
    "package": "views/document/borrow/list"
  },
/*===================== 图档管理 end ====================*/
];