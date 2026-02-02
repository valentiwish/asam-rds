export default [
    /**===================== start 设备管理*===================== */
//设备需求单
{
    "name": "/materialDemand/list",
    "package": "equipment/materialDemand/list/"
  }, { //新增设备需求单
    "name": "/materialDemand/create",
    "package": "equipment/materialDemand/cur/"
  },
  { //修改设备需求单
    "name": "/materialDemand/update",
    "params": "id,type",
    "package": "equipment/materialDemand/cur/"
  }, { //查看设备需求单
    "name": "/materialDemand/view",
    "params": "id,type",
    "package": "equipment/materialDemand/cur/"
  }, 
  
  
  //--------------------------------------基础数据---------------------------
  // //仓库管理
  {
    "name": "/warehouse/list",
    "package": "views/equipment/warehouse/list"
  },
  //设备种类
  {
    "name": "/equiptype/list",
    "package": "views/equipment/equiptype/list"
  }, 
  //阈值定义
  {
    "name": "/materialsInfo/list",
    "package": "views/equipment/materialsInfo/list"
  },
  {//新增供货商列表
    "name": "/supplier/list",
    "package": "views/equipment/supplier/list"
  },   
  {//新增供货商新增
    "name": "/supplier/create",
    "package": "views/equipment/supplier/cur"
  },
  {//新增供货商修改
    "name": "/supplier/update",
    "params": "id,type",
    "package": "views/equipment/supplier/cur"
  }, 
  {//新增供货商查看
    "name": "/supplier/view",
    "params": "id,type",
    "package": "views/equipment/supplier/cur"
  }, 
  {//产品名录
    "name": "/supplier/supplierProduct",
    "package": "views/equipment/supplier/supplierProduct"
  },        
  //采购计划
  {
    "name": "/equipPurcha/list",
    "package": "views/equipment/equipPurcha/list"
  }, 
  {
    "name": "/equipPurcha/create",
    "package": "views/equipment/equipPurcha/cur"
  },
  {
    "name": "/equipPurcha/update",
    "params": "id,type",
    "package": "views/equipment/equipPurcha/cur"
  }, 
  {
    "name": "/equipPurcha/view",
    "params": "id,type",
    "package": "views/equipment/equipPurcha/cur"
  }, 
  {//执行采购
    "name": "/equipExecute/list",
    "package": "views/equipment/equipExecute/list"
  },
  {
    "name": "/equipExecute/update",
    "params": "id,type",
    "package": "views/equipment/equipExecute/cur"
  }, 
  {
    "name": "/equipExecute/view",
    "params": "id,type",
    "package": "views/equipment/equipExecute/view"
  },
  //设备验收
  {
    "name": "/materialCheckAccept/list",
    "package": "views/equipment/materialCheckAccept/list"
  }, {
    "name": "/materialCheckAccept/update",
    "params": "id,type",
    "package": "views/equipment/materialCheckAccept/cur"
  }, {
    "name": "/materialCheckAccept/view",
    "params": "id,type",
    "package": "views/equipment/materialCheckAccept/cur"
  }, 
  {//退换货
    "name": "/materialCheckAccept/returnList",
    "package": "views/equipment/materialCheckAccept/returnList"
  },
  {   //设备入库列表
    "name": "/materialInstorage/list",
    "package": "views/equipment/materialInstorage/list"
  },
  { //设备入库
    "name": "/materialInstorage/update",
    "params": "id,type",
    "package": "views/equipment/materialInstorage/cur"
  }, 
  { //查看设备入库单详情
    "name": "/materialInstorage/view",
    "params": "id",
    "package": "views/equipment/materialInstorage/view"
  },
  //---------------出库-----------------------------
  {//临时领用
    "name": "/tempOutstorageApply/list",
    "package": "views/equipment/tempOutstorageApply/list",
  }, {
    "name": "/tempOutstorageApply/create",
    "package": "views/equipment/tempOutstorageApply/cur"
  }, {
    "name": "/tempOutstorageApply/update",
    "params": "id,type",
    "package": "views/equipment/tempOutstorageApply/cur"
  }, {
    "name": "/tempOutstorageApply/view",
    "params": "id,type",
    "package": "views/equipment/tempOutstorageApply/cur"
  },
  //设备出库
  {
    "name": "/materialOutstorage/list",
    "package": "views/equipment/materialOutstorage/list",
  }, {
    "name": "/materialOutstorage/create",
    "package": "views/equipment/materialOutstorage/cur"
  }, {
    "name": "/materialOutstorage/update",
    "params": "id,type",
    "package": "views/equipment/materialOutstorage/cur"
  }, {
    "name": "/materialOutstorage/view",
    "params": "id,type",
    "package": "views/equipment/materialOutstorage/cur"
  },
  //------------------------库存管理-----------------------
  {
    "name": "/stock/list",
    "package": "views/equipment/stock/list"
  },
  {
    "name": "/stock/real",
    "package": "views/equipment/stock/real"
  },
  //------------------------设备安装-----------------------
  {
    "name": "/install/list",
    "package": "views/equipment/install/list"
  },
  {
    "name": "/install/create",
    "package": "views/equipment/install/cur"
  },
  {
    "name": "/install/view",
    "params": "id,type",
    "package": "views/equipment/install/cur"
  },
  /*===================== end 设备管理=====================*/
    
   
]