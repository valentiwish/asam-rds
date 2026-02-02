export default [
    { //维修工单列表
    "name": "/repair/order",
    "package": "views/repair/order/list"
    },
    { //新增维修工单
    "name": "/repair/orderCreate",
    "package": "views/repair/order/create"
    },
    { //查看维修工单
    "name": "/repair/orderView",
    "params": "id,type",
    "package": "views/repair/order/create"
    },
    { //修改维修工单
    "name": "/repair/orderUpdate",
    "params": "id,type",
    "package": "views/repair/order/create"
    },
    { //工单汇报列表
    "name": "/repair/reportList",
    "package": "views/repair/order/reportList"
    },
    { //工单汇报
    "name": "/repair/report",
    "params": "id",
    "package": "views/repair/order/report"
    },
    { //工单汇报查看
    "name": "/repair/reportView",
    "params": "id,type",
    "package": "views/repair/order/reportView"
    },
    
    
   
]