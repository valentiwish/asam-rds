export default [


/*===================== start 综合事务=====================*/
/*===================== start 公章管理=====================*/
{
  "name": "/stamp/stampApplyList",
  "package": "views/affairs/stamp/stampApplyList"
}, 
//用印申请新增
{
  "name": "/stamp/stampApplyCreate",
  "package": "views/affairs/stamp/stampApplyCur"
},
//用印申请修改
{
  "name": "/stamp/stampApplyUpdate",
  "params": "id,type",
  "package": "views/affairs/stamp/stampApplyCur"
}, 
//用印申请查看
{
  "name": "/stamp/stampApplyView",
  "params": "id,type",
  "package": "views/affairs/stamp/stampApplyCur"
},
//公章维护
{
  "name": "/stamp/stampManage",
  "package": "views/affairs/stamp/stampManage"
},
//用章台账
{
  "name": "/stamp/useStamp",
  "package": "views/affairs/stamp/useStamp"
},
/*===================== end 公章管理=====================*/
/*===================== start 公文管理=====================*/
{    // 公文类型列表
  "name": "/doc/docType",
  "package": "views/affairs/doc/docType",
}, 
{ // 拟文-列表
  "name": "/doc/myDocList",
  "package": "views/affairs/doc/myDocList"
}, 
{ // 拟文-新增
  "name": "/doc/myDocCreate",
  "package": "views/affairs/doc/myDocCur"
}, 
{ // 拟文-修改
  "name": "/doc/myDocUpdate",
  "params": "id,type",
  "package": "views/affairs/doc/myDocCur"
},
{ // 拟文-查看
  "name": "/doc/myDocView",
  "params": "id,type",
  "package": "views/affairs/doc/myDocCur"
},  
{ // 拟文(表格样式)
  "name": "/myDocument/createTable",
  "package": "views/affairs/document/send/myDocument/curTable/",
  "meta": { withCss: true }
}, { // 拟文-查看 编辑
  "name": "/myDocument/cur",
  "params": "id,type",
  "package": "views/affairs/document/send/myDocument/cur/"
}, { // 发文管理-发文箱-列表
  "name": "/sendBox/list",
  "package": "views/affairs/document/send/sendBox/list/"
},{ // 收文登记 - 列表
  "name": "/documentReceiving/list",
  "package": "views/affairs/document/receive/myDocument/list/"
},{ // 收文登记 - 登记
  "name": "/documentReceiving/create",
  "package": "views/affairs/document/receive/myDocument/cur/"
},{ // 收文登记 - 修改
  "name": "/documentReceiving/update",
  "params": "id,type",
  "package": "views/affairs/document/receive/myDocument/cur/"
},{ // 收文登记 - 查看
  "name": "/documentReceiving/view",
  "params": "id,type",
  "package": "views/affairs/document/receive/myDocument/cur/"
},{ // 对已收文件进行发送
  "name": "/documentReceiving/sendHandle",
  "params": "id",
  "package": "views/affairs/document/receive/myDocument/send/"
},{ // 发函管理-我的拟函
  "name": "/myLetter/list",
  "package": "views/affairs/document/letter/send/myLetter/list/"
},{ // 发函管理-我的拟函-拟函
  "name": "/letter/create",
  "package": "views/affairs/document/letter/send/myLetter/cur/"
},{ // 发函管理-我的拟函-编辑
  "name": "/letter/update",
  "params": "id,type",
  "package": "views/affairs/document/letter/send/myLetter/cur/"
},{ // 发函管理-我的拟函-查看
  "name": "/letter/view",
  "params": "id,type",
  "package": "views/affairs/document/letter/send/myLetter/cur/"
},{ // 发函管理-发函箱
  "name": "/sendLetter/list",
  "package": "views/affairs/document/letter/send/sendBox/list/"
},{ // 收函管理-收函登记
  "name": "/receiveLetter/register",
  "package": "views/affairs/document/letter/receive/cur/"
},{ // 收函管理-收函箱
  "name": "/receiveLetter/list",
  "package": "views/affairs/document/letter/receive/list/"
},{ // 收函管理-收函箱-新增
  "name": "/receiveLetter/create",
  "package": "views/affairs/document/letter/receive/cur/"
},{ // 收函管理-收函箱-编辑
  "name": "/receiveLetter/update",
  "params": "id,type",
  "package": "views/affairs/document/letter/receive/cur/"
},{ // 收函管理-收函箱-查看
  "name": "/receiveLetter/view",
  "params": "id,type",
  "package": "views/affairs/document/letter/receive/cur/"
},{ // 收函管理-我的函件
  "name": "/myRecLetter/listPrimitive",
  "package": "views/affairs/document/letter/receive/myRecLetter/listPrimitive/"
},{ // 收函管理-我的函件-查看
  "name": "/myRecLetter/view",
  "params": "id,type",
  "package": "views/affairs/document/letter/receive/myRecLetter/handle/"
},{ // 收函管理-我的函件-办理
  "name": "/myRecLetter/handle",
  "package": "views/affairs/document/letter/receive/myRecLetter/handle/"
},{ // 收函管理-我的函件-工作流
  "name": "/myRecLetter/list",
  "package": "views/affairs/document/letter/receive/myRecLetter/list/"
},{ // 公文管理-公文柜-完成公文
  "name": "/finishDoc/list",
  "package": "views/affairs/document/cabinet/finishDoc/list/"
},{ // 公文管理-公文柜-完成公文-查看
  "name": "/finishDoc/view",
  "params": "id,type",
  "package": "views/affairs/document/cabinet/finishDoc/view/"
},
/*===================== end 公文管理=====================*/
/*===================== start 会议管理=====================*/
{ //会议管理 - 会议室 - 列表
  "name": "/meeting/list",
  "package": "views/affairs/meeting/meetingList",
  },{ //会议管理 - 会议 - 新增
  "name": "/meeting/create",
  "package": "views/affairs/meeting/meetingCur",
  },{ //会议管理 - 会议 - 查看
    "name": "/meeting/view",
    "params": "id,type",
    "package": "views/affairs/meeting/meetingCur",
    },{ //会议管理 - 会议室 - 列表
  "name": "/room/list",
  "package": "views/affairs/meeting/roomList",
  },{ //会议管理 - 会议室 - 新增
  "name": "/room/create",
  "package": "views/affairs/meeting/roomCur",
  },{ //会议管理 - 会议室 - 更新
    "name": "/room/update",
    "params": "id,type",
    "package": "views/affairs/meeting/roomCur",
  },{ //会议管理 - 会议室 - 查看
    "name": "/room/view",
    "params": "id,type",
    "package": "views/affairs/meeting/roomCur",
  },{ //会议管理 - 会议室使用记录 - 列表
    "name": "/useRecord/list",
    "package": "views/affairs/meeting/useRecordList",
  },
/*===================== end 会议管理=====================*/

/*===================== end 综合事务=====================*/


/*===================== start 车辆监控管理=====================*/

{ //车辆管理 - 车辆信息 - 列表
  "name": "/carInfo/list",
  "package": "views/car/baseData/carInfo/list",
  "meta": { keepAlive: true }
  },{ //车辆管理 - 车辆信息 - 新增
  "name": "/carInfo/create",
  "package": "views/car/baseData/carInfo/cur"
  },{ //车辆管理 - 车辆信息 - 修改
  "name": "/carInfo/update",
  "params": "id,type",
  "package": "views/car/baseData/carInfo/cur"
  },{ //车辆管理 - 车辆信息 - 查看
  "name": "/carInfo/view",
  "params": "id,type",
  "package": "views/car/baseData/carInfo/cur"
  },{ //车辆管理 - 运输公司管理 - 列表
  "name": "/partnerCompany/list",
  "package": "views/car/baseData/partnerCompany/list",
  "meta": { keepAlive: true }
  },{ //车辆管理 - 运输公司管理 - 新增
  "name": "/partnerCompany/create",
  "package": "views/car/baseData/partnerCompany/cur"
  },{ //车辆管理 - 运输公司管理 - 修改
  "name": "/partnerCompany/update",
  "params": "id,type",
  "package": "views/car/baseData/partnerCompany/cur"
  },{ //车辆管理 - 运输公司管理 - 查看
  "name": "/partnerCompany/view",
  "params": "id,type",
  "package": "views/car/baseData/partnerCompany/cur"
  },{ //车辆管理 - 车队管理 - 列表
  "name": "/carTeam/list",
  "package": "views/car/baseData/carTeam/list",
  "meta": { keepAlive: true }
  },
  { //车辆管理 - 车队管理 - 新增
  "name": "/carTeam/create",
  "package": "views/car/baseData/carTeam/cur"
  },
  { //车辆管理 - 车队管理 - 修改
  "name": "/carTeam/update",
  "params": "id,type",
  "package": "views/car/baseData/carTeam/cur"
  },
  { //车辆管理 - 车队管理 - 查看
  "name": "/carTeam/view",
  "params": "id,type",
  "package": "views/car/baseData/carTeam/cur"
  },{ //车辆管理 - 车辆定位
  "name": "/carLocation/list",
  "package": "views/car/vehicle/location/list",
  "meta": { keepAlive: true }
  },{ //车辆管理 - 车辆轨迹
  "name": "/carTrajectory/list",
  "package": "views/car/vehicle/trajectory/list",
  "meta": { keepAlive: true }
  },
  { //车辆管理 - 车辆申请
  "name": "/carApply/list",
  "package": "views/car/apply/list",
  "meta": { keepAlive: true }
  },
  { //车辆管理 - 车辆申请
  "name": "/carApply/create",
  "package": "views/car/apply/cur",
  },
  { //车辆管理 - 车辆申请
  "name": "/carApply/update",
    "params": "id,type,reStart",
  "package": "views/car/apply/cur",
  },
  { //车辆管理 - 车辆申请
  "name": "/carApply/view",
    "params": "id,type",
  "package": "views/car/apply/cur",
  },
  { //车辆管理 - 车辆申请记录
  "name": "/carApplyRecord/list",
  "package": "views/car/apply/record/list",
  "meta": { keepAlive: true }
  },
  { //车辆管理 - 车辆申请记录查看
  "name": "/carApplyRecord/view",
  "package": "views/car/apply/record/view",
  "meta": { keepAlive: true }
  },
  
  /*====================== 运营管理 start ======================*/
  { //运营管理 - 处置点管理 - 列表
  "name": "/point/list",
  "package": "views/car/vehicle/point/list",
  "meta": { keepAlive: true }
  },
  { //运营管理 - 处置点管理 - 新增
  "name": "/point/create",
  "package": "views/car/vehicle/point/cur"
  },
  { //运营管理 - 处置点管理 - 修改
  "name": "/point/update",
  "params": "id,type",
  "package": "views/car/vehicle/point/cur"
  },
  { //运营管理 - 处置点管理 - 查看
  "name": "/point/view",
  "params": "id,type",
  "package": "views/car/vehicle/point/cur"
  },
  { //运营管理 - 运输审批 - 列表
  "name": "/approval/list",
  "package": "views/car/vehicle/approval/list",
  "meta": { keepAlive: true }
  },
  { //运营管理 - 运输审批 - 审批
  "name": "/approval/update",
  "params": "id,type,tabName",
  "package": "views/car/vehicle/approval/cur"
  },
  { //运营管理 - 运输审批 - 查看
  "name": "/approval/view",
  "params": "id,type,tabName",
  "package": "views/car/vehicle/approval/cur"
  },
  { //运营管理 - 围栏管理 - 列表
  "name": "/grid/list",
  "package": "views/car/vehicle/grid/list",
  "meta": { keepAlive: true }
  },
  { //运营管理 - 围栏管理 - 新增
  "name": "/grid/create",
  "package": "views/car/vehicle/grid/cur"
  },
  { //运营管理 - 围栏管理 - 修改
  "name": "/grid/update",
  "params": "id,type",
  "package": "views/car/vehicle/grid/cur"
  },
  { //运营管理 - 围栏管理 - 查看
  "name": "/grid/view",
  "params": "id,type",
  "package": "views/car/vehicle/grid/cur"
  },
  { //运营管理 - 联单管理 - 列表
  "name": "/receipt/list",
  "package": "views/car/vehicle/receipt/list",
  "meta": { keepAlive: true }
  },
  { //运营管理 - 联单管理 - 查看
  "name": "/receipt/view",
  "params": "id,type",
  "package": "views/car/vehicle/receipt/cur"
  },
  /*====================== 运营管理 end ======================*/
  
  /*====================== 报警中心 start ======================*/
  { //报警中心 - GPS报警 - 列表
  "name": "/gpsAlarm/list",
  "package": "views/car/vehicle/gpsAlarm/list",
  "meta": { keepAlive: true }
  },
  { //报警中心 - GPS报警 - 查看
  "name": "/gpsAlarm/view",
  "params": "id,type",
  "package": "views/car/vehicle/gpsAlarm/cur"
  },
  /*====================== 报警中心 end ======================*/
  
  /*====================== 统计报表 start ======================*/
  { //统计报表 - 运输公司
  "name": "/staCompany/list",
  "package": "views/car/vehicle/statistics/staCompany/list",
  "meta": { keepAlive: true }
  },
  { //统计报表 - 处置点
  "name": "/staPoint/list",
  "package": "views/car/vehicle/statistics/staPoint/list",
  "meta": { keepAlive: true }
  },
  { //统计报表 - 运输台帐
  "name": "/transportBook/list",
  "package": "views/car/vehicle/statistics/transportBook/list",
  "meta": { keepAlive: true }
  },
  { //统计报表 - 运输公司
  "name": "/staFactory/list",
  "package": "views/car/vehicle/statistics/staFactory/list",
  "meta": { keepAlive: true }
  },
  /*====================== 统计报表 end ======================*/
  
  /*===================== 车辆监控管理 end ====================*/
    {//桶装水管理列表
  "name": "/workbench/waterSaleInfo",
  "package": "views/workbench/water/waterSaleInfo/list"
},{//桶装水套餐管理列表
  "name": "/workbench/waterCombo",
  "package": "views/workbench/water/waterCombo/list"
},{//桶装水送水管理列表
  "name": "/workbench/waterSale",
  "package": "views/workbench/water/waterSale/list"
},{//桶装水送水业务处理页面
  "name": "/workbench/waterSaleHandle",
  "params": "id,type",
  "package": "views/workbench/water/waterSale/cur"
},{//桶装水送水业务查看页面
  "name": "/workbench/waterSaleView",
  "params": "id,type",
  "package": "views/workbench/water/waterSale/cur"
},{//停水通知列表
  "name": "/workbench/waterStop",
  "package": "views/workbench/waterstop/list"
},{//送水业务待处理列表
  "name": "/workbench/ruTaskList",
  "package": "views/workbench/task/list"
},{//送水业务已处理列表
  "name": "/workbench/hisTaskList",
  "package": "views/workbench/task/hisList"
},
    
   
]