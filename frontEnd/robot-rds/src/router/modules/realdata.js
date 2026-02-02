export default [
    { //站点列表
    "name": "/site/list",
    "package": "views/realdata/scada/site/list"
    },
    { //控制站列表
    "name": "/station/list",
    "package": "views/realdata/scada/station/list"
    },
    { //变量列表
    "name": "/variable/list",
    "package": "views/realdata/scada/variable/list"
    },
    { //类型列表
    "name": "/scadaType/list",
    "package": "views/realdata/scada/type/list"
    },
    { //类型修改
    "name": "/scadaType/update",
    "params": "id,type",
    "package": "views/realdata/scada/type/cur"
    },
    { //类型查看
    "name": "/scadaType/view",
    "params": "id,type",
    "package": "views/realdata/scada/type/cur"
    },
    { //水量上报
    "name": "/waterReport/list",
    "package": "views/realdata/water/report/list"
    },
    { //水厂列表
    "name": "/waterFactory/list",
    "package": "views/realdata/water/factory/list"
    },
    { //水厂新增
    "name": "/waterFactory/create",
    "package": "views/realdata/water/factory/cur"
    },
    { //水厂修改
    "name": "/waterFactory/update",
    "params": "id,type",
    "package": "views/realdata/water/factory/cur"
    },
    { //水厂查看
    "name": "/waterFactory/view",
    "params": "id,type",
    "package": "views/realdata/water/factory/cur"
    },
    { //阈值定义列表
    "name": "/threshold/list",
    "package": "views/realdata/threshold/list"
    },
    { //阈值新增
    "name": "/threshold/create",
    "package": "views/realdata/threshold/cur"
    },
    { //阈值修改
    "name": "/threshold/update",
    "params": "id,type",
    "package": "views/realdata/threshold/cur"
    },
    { //阈值查看
    "name": "/threshold/view",
    "params": "id,type",
    "package": "views/realdata/threshold/cur"
    }
]