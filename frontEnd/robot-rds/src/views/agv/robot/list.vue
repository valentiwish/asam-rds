<template>
    <div>
        <page-title></page-title>
        <div class="page-search">
            <div class="page-search-title">查询条件</div>
            <div class="page-search-content">
                <Form ref="robot" :model="robot" :label-width="100">
                    <FormItem label="机器人名称" prop="vehicleId">
                        <Input v-model.trim="robot.vehicleId" placeholder="请输入任务名称，不超过30字" maxlength="30"></Input>
                    </FormItem>
                    <FormItem label="机器人IP" prop="currentIp">
                        <Input v-model.trim="robot.currentIp" placeholder="请输入任务名称，不超过30字" maxlength="30"></Input>
                    </FormItem>
                    <FormItem label="机器人组" prop="groupName">
                        <Input v-model.trim="robot.groupName" placeholder="请输入任务名称，不超过30字" maxlength="30"></Input>
                    </FormItem>
                </Form>
                <div class="search-btn">
                    <allowBtn allow="allowsearch" size="large" @click="search()">查询</allowBtn>
                    <allowBtn allow="allowreset" size="large" @click="reset">重置</allowBtn>
                </div>
            </div>
        </div>
        <div class="page-content">
            <data-grid :option="gridOption" ref="grid"></data-grid>
        </div>
        <div class="page-content">
            <data-grid :option="gridOptionOrder" ref="gridOrder" @on-current-change="selectRow" :loading="gridLoading">
            </data-grid>
        </div>
        <Modal v-model="modal_add" title="新增机器人">
            <update-modal ref="addform" type="add"></update-modal>
            <div slot="footer">
                <Button type="primary" class="ivu-btn ivu-btn-primary" @click="save">保存</Button>
                <Button type="ghost" class="ivu-btn ivu-btn-primary" @click="close">关闭</Button>
            </div>
        </Modal>
    </div>
</template>
<script>
import dataGrid from "@/components/datagrid"
import webuploader from "@/components/webuploader"
import fontIcon from "@/components/fontIcon"
import updateModal from "./cur"
export default {
    components: { dataGrid, fontIcon, webuploader, updateModal },
    data: function () {
        var that = this;
        return {
            // 修改此处
            id: '', //页面修改赋值
            viewId: '', //页面查看赋值
            robot: {
                vehicleId: '',
                currentIp: '',
                groupName: '',
                leisureState: '',
                orderState: '',
                batteryLevel: '',
                confidence: '',
                robotType: '',
                locationState: '',
                lockPath: '',
                lockPoint: '',
            },
            states: [
                { "id": 1, "name": "是" },
                { "id": 2, "name": "否" },
            ],
            modal_add: false,
            modal_update: false, //修改弹窗
            modal_view: false, //查看弹窗
            modal_add: false, //新增弹窗
            gridOption: {
                url: "/service_rms/robotInfo/list",
                auto: true,
                columns: [ //列配置
                    // { "title": "机器人id", "key": "id", "align": "center"},
                    // { "type": "selection", "title": "全选", "key": "id", "align": "center", "width": "80px"},
                    { "title": "机器人名称", "key": "vehicleId", "align": "center" },
                    { "title": "机器人IP", "key": "currentIp", "align": "center" },
                    { "title": "机器人品牌", "key": "robotType", "align": "center" },
                    { "title": "机器人组", "key": "groupName", "align": "center", "width": "100px" },
                    { "title": "电量", "key": "batteryLevel", "align": "center", "width": "100px" },
                    { "title": "锁定点位", "key": "lockPoint", "align": "center", "width": "100px" },
                    {
                        "title": "接单状态", "key": "orderState", "align": "center", "width": "150px",
                        "render": function (h, p) {
                            var obj = {
                                0: {
                                    name: "不可接单",
                                    color: "magenta"
                                },
                                1: {
                                    name: "可以接单",
                                    color: "success"
                                }
                            }
                            if (obj[p.row.orderState]) {
                                return h("Tag", {
                                    props: {
                                        color: obj[p.row.orderState].color,
                                    }
                                }, obj[p.row.orderState].name)
                            }
                            return "";
                        }
                    },
                    {
                        "title": "空闲状态", "key": "leisureState", "align": "center", "width": "150px",
                        "render": function (h, p) {
                            var obj = {
                                0: {
                                    name: "不空闲",
                                    color: "magenta"
                                },
                                1: {
                                    name: "空闲",
                                    color: "success"
                                }
                            }
                            if (obj[p.row.leisureState]) {
                                return h("Tag", {
                                    props: {
                                        color: obj[p.row.leisureState].color,
                                    }
                                }, obj[p.row.leisureState].name)
                            }
                            return "";
                        }
                    },
                    {
                        "title": "控制权状态", "key": "controlState", "align": "center", "width": "200px",
                        "render": function (h, p) {
                            var obj = {
                                0: {
                                    name: "已抢占WIFI控制权",
                                    color: "success"
                                },
                                1: {
                                    name: "已抢占DMS控制权",
                                    color: "green"
                                },
                                2: {
                                    name: "控制权被抢占",
                                    color: "magenta"
                                }
                            }
                            if (obj[p.row.controlState]) {
                                return h("Tag", {
                                    props: {
                                        color: obj[p.row.controlState].color,
                                    }
                                }, obj[p.row.controlState].name)
                            }
                            return "";
                        }
                    },
                    {
                        "title": "定位状态", "key": "locationState", "align": "center", "width": "150px",
                        "render": function (h, p) {
                            var obj = {
                                0: {
                                    name: "定位失败",
                                    color: "magenta"
                                },
                                1: {
                                    name: "定位正确",
                                    color: "success"
                                },
                                2: {
                                    name: "正在重定位",
                                    color: "green"
                                },
                                3: {
                                    name: "定位完成",
                                    color: "success"
                                }
                            }
                            if (obj[p.row.locationState]) {
                                return h("Tag", {
                                    props: {
                                        color: obj[p.row.locationState].color,
                                    }
                                }, obj[p.row.locationState].name)
                            }
                            return "";
                        }
                    },
                    {
                        "title": "导航状态", "key": "navigationState", "align": "center", "width": "150px",
                        "render": function (h, p) {
                            var obj = {
                                0: {
                                    name: "没有导航",
                                    color: "default"
                                },
                                1: {
                                    name: "目前不可能出现该状态",
                                    color: "cyan"
                                },
                                2: {
                                    name: "导航正在运行",
                                    color: "green"
                                },
                                3: {
                                    name: "导航暂停",
                                    color: "warning"
                                },
                                4: {
                                    name: "导航完成",
                                    color: "success"
                                },
                                5: {
                                    name: "导航失败",
                                    color: "magenta"
                                },
                                6: {
                                    name: "取消导航",
                                    color: "warning"
                                }
                            }
                            if (obj[p.row.navigationState]) {
                                return h("Tag", {
                                    props: {
                                        color: obj[p.row.navigationState].color,
                                    }
                                }, obj[p.row.navigationState].name)
                            }
                            return "";
                        }
                    },
                    // { "title": "锁定路径", "key": "lockPath", "align": "center", "width": "100px" },
                    { "title": "置信度", "key": "confidence", "align": "center", "width": "100px" },
                    { "title": "当前地图", "key": "currentMap", "align": "center", "width": "150px" },
                    {
                        "key": 'id', title: '操作', "align": "center", "width": "420px",
                        render(h, params) {
                            return h('div', [
                                h('allowBtn', {
                                    props: {
                                        type: 'info',
                                        size: 'small',
                                        allow: "可接单"
                                    },
                                    style: {
                                        marginRight: '5px'
                                    },
                                    on: {
                                        click: () => {
                                            that.toAccepted(params.row),
                                                that.search();
                                        }
                                    }
                                }, '可接单'),
                                h('allowBtn', {
                                    props: {
                                        type: 'info',
                                        size: 'small',
                                        allow: "空闲"
                                    },
                                    style: {
                                        marginRight: '5px'
                                    },
                                    on: {
                                        click: () => {
                                            that.setLeisure(params.row)
                                        }
                                    }
                                }, '空闲'),
                                h('allowBtn', {
                                    props: {
                                        type: 'success',
                                        size: 'small',
                                        allow: '解除占用',
                                    },
                                    style: {
                                        marginRight: '5px'
                                    },
                                    on: {
                                        click: () => {
                                            that.liftRobotLockPathAndPoint(params.row)
                                        }
                                    }
                                }, "解除占用"),
                                h('allowBtn', {
                                    props: {
                                        type: 'primary',
                                        size: 'small',
                                        allow: '不接单且占用资源',
                                    },
                                    style: {
                                        marginRight: '5px'
                                    },
                                    on: {
                                        click: () => {
                                            that.toNotAcceptButRes(params.row)
                                        }
                                    }
                                }, "不接单且占用资源"),
                                h('allowBtn', {
                                    props: {
                                        type: 'error',
                                        size: 'small',
                                        allow: '接单不占用资源',
                                    },
                                    style: {
                                        marginRight: '5px'
                                    },
                                    on: {
                                        click: () => {
                                            that.toNotAcceptNotRes(params.row)
                                        }
                                    }
                                }, "接单不占用资源"),
                                h('allowBtn', {
                                    props: {
                                        type: 'warning',
                                        size: 'small',
                                        allow: 'WIFI控制权',
                                    },
                                    style: {
                                        marginRight: '5px'
                                    },
                                    on: {
                                        click: () => {
                                            that.getControl(params.row)
                                        }
                                    }
                                }, "WIFI控制权"),
                                h('allowBtn', {
                                    props: {
                                        type: 'info',
                                        size: 'small',
                                        allow: 'DMS控制权',
                                    },
                                    style: {
                                        marginRight: '5px'
                                    },
                                    on: {
                                        click: () => {
                                            that.releaseControl(params.row)
                                        }
                                    }
                                }, "DMS控制权"),
                                h('allowBtn', {
                                    props: {
                                        type: 'info',
                                        size: 'small',
                                        allow: '继续',
                                    },
                                    style: {
                                        marginRight: '5px'
                                    },
                                    on: {
                                        click: () => {
                                            that.continueNav(params.row)
                                        }
                                    }
                                }, "继续"),
                                h('allowBtn', {
                                    props: {
                                        type: 'info',
                                        size: 'small',
                                        allow: '暂停',
                                    },
                                    style: {
                                        marginRight: '5px'
                                    },
                                    on: {
                                        click: () => {
                                            that.pauseNav(params.row)
                                        }
                                    }
                                }, "暂停"),
                                h('allowBtn', {
                                    props: {
                                        type: 'success',
                                        size: 'small',
                                        allow: '确认定位',
                                    },
                                    style: {
                                        marginRight: '5px'
                                    },
                                    on: {
                                        click: () => {
                                            that.confirmLoc(params.row)
                                        }
                                    }
                                }, "确认定位"),
                                h('allowBtn', {
                                    props: {
                                        type: 'success',
                                        size: 'small',
                                        allow: '释放站点',
                                    },
                                    style: {
                                        marginRight: '5px'
                                    },
                                    on: {
                                        click: () => {
                                            that.unlockPoint(params.row)
                                        }
                                    }
                                }, "释放点区"),

                            ])
                        }
                    }
                ],
                "loadFilter": function (data) {
                    that.gridOption.data = data.data.list;
                    return data.data;
                }
            },
            limitCardDate: {
                disabledDate(date) {
                    return date && date.valueOf() > Date.now();
                }
            },
            option: {
                "server": "/service_rms/goods/uploadData",
                auto: true,
                accept: {
                    title: 'Excel',
                    extensions: 'xls,xlsx',
                    mimeTypes: 'application/excel'
                },
                type: "select",
                uploadAccept: this.uploadAccept,
                fileNumLimit: 1,
                showUploadList: false,
                showFileList: true
            }
        }
    },
    methods: {
        // 释放当前机器人锁定的站点和占用的区域
        unlockPoint: function (row) {
            var that = this;
            var robotName = row.vehicleId;
            this.$Modal.confirm({
                title: '提示',
                content: '您确认要释放机器人站点吗？',
                onOk: function () {
                    var url = "/service_rms/robotInfo/releasePointsAndAreas";
                    this.$ajax.post(url, { 'robotId': robotName })
                        .then((res) => {
                            console.log(res);
                            if (res.data.code == '200') {
                                setTimeout(() => {
                                    that.$Modal.success({
                                        title: `成功释放机器人${robotName}的站点：${res.data.data}及其占用的区域`
                                    })
                                    that.search()
                                }, 500)
                            } else {
                                setTimeout(() => {
                                    that.$Modal.warning({ 
                                        title: '操作失败',
                                    })
                                }, 500)
                            }
                        })
                        .catch((error) => {
                            setTimeout(() => {
                                that.$Modal.warning({
                                    title: '操作失败',
                                })
                            }, 500)
                            if (!error.url) { console.info(error); }
                        })
                }
            })
        },
        selectionRows: function (selection) {
            this.checkRobots = selection;
        },
        close: function () {
            this.modal_add = false;
            this.$refs.addform.scanner.scannerNumber = '';
            this.$refs.addform.scanner.scannerIp = '';

        },
        toAccepted: function (obj) {
            var that = this;
            var robotName = obj.vehicleId;
            var batteryLevel = obj.batteryLevel.replace("%", "") / 100;
            this.$Modal.confirm({
                title: '提示',
                content: '您确认设置机器人可接单吗？',
                onOk: function () {
                    var idValue = obj.id;
                    var url = "/service_rms/monitor/accepted";
                    this.$ajax.post(url, { 'robotName': robotName, 'batteryLevel': batteryLevel })
                        .then((res) => {
                            if (res.data.code == '200') {
                                setTimeout(() => {
                                    that.$Modal.success({
                                        title: '操作成功',
                                    })
                                    that.search()
                                }, 500)
                            } else {
                                setTimeout(() => {
                                    that.$Modal.warning({
                                        title: '操作失败',
                                    })
                                }, 500)
                            }
                        })
                        .catch((error) => {
                            setTimeout(() => {
                                that.$Modal.warning({
                                    title: '操作失败',
                                })
                            }, 500)
                            if (!error.url) { console.info(error); }
                        })
                }
            })
        },
        setLeisure: function (obj) {
            var that = this;
            var robotName = obj.vehicleId;
            this.$Modal.confirm({
                title: '提示',
                content: '要设置机器人为空闲状态吗？',
                onOk: function () {
                    var url = "/service_rms/monitor/setLeisure";
                    this.$ajax.post(url, { 'robotName': robotName })
                        .then((res) => {
                            if (res.data.code == '200') {
                                setTimeout(() => {
                                    that.$Modal.success({
                                        title: '操作成功',
                                    })
                                    that.search()
                                }, 500)
                            } else {
                                setTimeout(() => {
                                    that.$Modal.warning({
                                        title: '操作失败',
                                    })
                                }, 500)
                            }
                        })
                        .catch((error) => {
                            setTimeout(() => {
                                that.$Modal.warning({
                                    title: '操作失败',
                                })
                            }, 500)
                            if (!error.url) { console.info(error); }
                        })
                }
            })
        },
        toNotAcceptButRes: function (obj) {
            var that = this;
            var robotName = obj.vehicleId;
            this.$Modal.confirm({
                title: '提示',
                content: '您确认设置机器人的状态为不接单且占用资源吗？',
                onOk: function () {
                    var url = "/service_rms/monitor/notOrderButRes";
                    this.$ajax.post(url, { 'robotName': robotName })
                        .then((res) => {
                            if (res.data.code == '200') {
                                setTimeout(() => {
                                    that.$Modal.success({
                                        title: '操作成功',
                                    })
                                    that.search()
                                }, 500)
                            } else {
                                setTimeout(() => {
                                    that.$Modal.warning({
                                        title: '操作失败',
                                    })
                                }, 500)
                            }
                        })
                        .catch((error) => {
                            setTimeout(() => {
                                that.$Modal.warning({
                                    title: '操作失败',
                                })
                            }, 500)
                            if (!error.url) { console.info(error); }
                        })
                }
            })
        },
        toNotAcceptNotRes: function (obj) {
            var that = this;
            var robotName = obj.vehicleId;
            this.$Modal.confirm({
                title: '提示',
                content: '您确认设置机器人的状态为不接单不占用资源吗？',
                onOk: function () {
                    var url = "/service_rms/monitor/notOrderNotRes";
                    this.$ajax.post(url, { 'robotName': robotName })
                        .then((res) => {
                            if (res.data.code == '200') {
                                setTimeout(() => {
                                    that.$Modal.success({
                                        title: '操作成功',
                                    })
                                    that.search()
                                }, 500)
                            } else {
                                setTimeout(() => {
                                    that.$Modal.warning({
                                        title: '操作失败',
                                    })
                                }, 500)
                            }
                        })
                        .catch((error) => {
                            setTimeout(() => {
                                that.$Modal.warning({
                                    title: '操作失败',
                                })
                            }, 500)
                            if (!error.url) { console.info(error); }
                        })
                }
            })
        },
        getControl: function (obj) {
            var that = this;
            var robotName = obj.vehicleId;
            this.$Modal.confirm({
                title: '提示',
                content: '您确认获取机器人控制权吗？',
                onOk: function () {
                    var url = "/service_rms/monitor/getControl";
                    this.$ajax.post(url, { 'robotName': robotName })
                        .then((res) => {
                            if (res.data.code == '200') {
                                setTimeout(() => {
                                    that.$Modal.success({
                                        title: '操作成功',
                                    })
                                    that.search()
                                }, 500)
                            } else {
                                setTimeout(() => {
                                    that.$Modal.warning({
                                        title: '操作失败',
                                    })
                                }, 500)
                            }
                        })
                        .catch((error) => {
                            setTimeout(() => {
                                that.$Modal.warning({
                                    title: '操作失败',
                                })
                            }, 500)
                            if (!error.url) { console.info(error); }
                        })
                }
            })
        },
        releaseControl: function (obj) {
            var that = this;
            var robotName = obj.vehicleId;
            this.$Modal.confirm({
                title: '提示',
                content: '您确认释放机器人控制权吗？',
                onOk: function () {
                    var url = "/service_rms/monitor/releaseControl";
                    this.$ajax.post(url, { 'robotName': robotName })
                        .then((res) => {
                            if (res.data.code == '200') {
                                setTimeout(() => {
                                    that.$Modal.success({
                                        title: '操作成功',
                                    })
                                    that.search()
                                }, 500)
                            } else {
                                setTimeout(() => {
                                    that.$Modal.warning({
                                        title: '操作失败',
                                    })
                                }, 500)
                            }
                        })
                        .catch((error) => {
                            setTimeout(() => {
                                that.$Modal.warning({
                                    title: '操作失败',
                                })
                            }, 500)
                            if (!error.url) { console.info(error); }
                        })
                }
            })
        },
        confirmLoc: function (obj) {
            var that = this;
            var robotName = obj.vehicleId;
            this.$Modal.confirm({
                title: '提示',
                content: '要确认机器人定位吗？',
                onOk: function () {
                    var url = "/service_rms/monitor/confirmLoc";
                    this.$ajax.post(url, { 'robotName': robotName })
                        .then((res) => {
                            if (res.data.code == '200') {
                                setTimeout(() => {
                                    that.$Modal.success({
                                        title: '操作成功',
                                    })
                                    that.search()
                                }, 500)
                            } else {
                                setTimeout(() => {
                                    that.$Modal.warning({
                                        title: '操作失败',
                                    })
                                }, 500)
                            }
                        })
                        .catch((error) => {
                            setTimeout(() => {
                                that.$Modal.warning({
                                    title: '操作失败',
                                })
                            }, 500)
                            if (!error.url) { console.info(error); }
                        })
                }
            })
        },
        liftRobotLockPathAndPoint: function (obj) {
            var that = this;
            var robotName = obj.vehicleId;
            this.$Modal.confirm({
                title: '提示',
                content: '要解除该机器人的路径和点位占用吗？',
                onOk: function () {
                    var url = "/service_rms/monitor/liftRobotLockPathAndPoint";
                    this.$ajax.post(url, { 'robotName': robotName })
                        .then((res) => {
                            if (res.data.code == '200') {
                                setTimeout(() => {
                                    that.$Modal.success({
                                        title: '操作成功',
                                    })
                                    that.search()
                                }, 500)
                            } else {
                                setTimeout(() => {
                                    that.$Modal.warning({
                                        title: '操作失败',
                                    })
                                }, 500)
                            }
                        })
                        .catch((error) => {
                            setTimeout(() => {
                                that.$Modal.warning({
                                    title: '操作失败',
                                })
                            }, 500)
                            if (!error.url) { console.info(error); }
                        })
                }
            })
        },
        continueNav: function (obj) {
            var that = this;
            var robotName = obj.vehicleId;
            this.$Modal.confirm({
                title: '提示',
                content: '要继续当前导航吗？',
                onOk: function () {
                    var url = "/service_rms/monitor/continueNav";
                    this.$ajax.post(url, { 'robotName': robotName })
                        .then((res) => {
                            if (res.data.code == '200') {
                                setTimeout(() => {
                                    that.$Modal.success({
                                        title: '操作成功',
                                    })
                                    that.search()
                                }, 500)
                            } else {
                                setTimeout(() => {
                                    that.$Modal.warning({
                                        title: '操作失败',
                                    })
                                }, 500)
                            }
                        })
                        .catch((error) => {
                            setTimeout(() => {
                                that.$Modal.warning({
                                    title: '操作失败',
                                })
                            }, 500)
                            if (!error.url) { console.info(error); }
                        })
                }
            })
        },
        pauseNav: function (obj) {
            var that = this;
            var robotName = obj.vehicleId;
            this.$Modal.confirm({
                title: '提示',
                content: '要暂停当前导航吗？',
                onOk: function () {
                    var url = "/service_rms/monitor/pauseNav";
                    this.$ajax.post(url, { 'robotName': robotName })
                        .then((res) => {
                            if (res.data.code == '200') {
                                setTimeout(() => {
                                    that.$Modal.success({
                                        title: '操作成功',
                                    })
                                }, 500)
                                that.search()
                            } else {
                                setTimeout(() => {
                                    that.$Modal.warning({
                                        title: '操作失败',
                                    })
                                }, 500)
                            }
                        })
                        .catch((error) => {
                            setTimeout(() => {
                                that.$Modal.warning({
                                    title: '操作失败',
                                })
                            }, 500)
                            if (!error.url) { console.info(error); }
                        })
                }
            })
        },
        edit: function (obj) {
            console.log(obj)
            this.$router.push("/agv/taskEdit/" + obj.id);
        },
        toView: function (obj) {
            this.viewId = obj.id;
            this.modal_view = true;
        },
        toUpdate: function (obj) {
            this.id = obj.id;
            this.modal_update = true;
        },
        toDelete: function (obj) {
            let that = this;
            this.$Modal.confirm({
                title: '删除',
                content: '您确认删除此条数据？',
                onOk: function () {
                    var idValue = obj.id;
                    var url = "/task/delete";
                    this.$ajax.post(url, { 'id': idValue })
                        .then((res) => {
                            that.$refs['grid'].loadData();
                        })
                        .catch((error) => {
                            if (!error.url) { console.info(error); }
                        })
                }
            })
        },
        search: function () {
            console.log(JSON.stringify(this.task))
            this.$refs.grid.load({ 'data': JSON.stringify(this.robot) });
        },
        reset: function () {
            // this.robot.vehicleId = "";
            this.$refs.robot.resetFields();
            this.search();
        },
        save: function (cb) {
            this.$refs['addform'].save((flag) => {
                if (flag == true) {
                    this.modal_add = false;
                    this.search();
                }
            });
        },
        update: function (cb) {
            this.$refs['updateform'].save((flag) => {
                if (flag == true) {
                    this.modal_update = false;
                    this.$refs.grid.reLoad({});
                    this.viewId = "";
                    this.id = "";
                }
            });
        },

        // //实时获取机器人信息列表
        // getRealTimeRobotInfosList: function () {
        // this.$ajax.post('/service_rms/robotInfo/list', {})
        //     .then((res) => {
        //         if (res.data.code == "200") {
        //             this.gridOption.data = res.data.data.list;
        //             console.log("gridOption", this.gridOption.data)
        //         }
        //     })
        // },

        pusherSubscribeCallback(obj) {
            // console.log("接收到的数据为：", obj);
            let key = obj.key;
            //推送时间
            let datetime = new Date(obj.datatime).Format("yyyy-MM-dd hh:mm:ss");
            // realTime数据
            if (key == "/realTimeData") {
                console.log("realTime数据为", obj.data);
                try {
                    let data = JSON.parse(obj.data);
                    if (data.code == "200") {

                    }
                } catch (error) {
                    console.error("解析JSON字符串时出错:", error);
                }
            }
            // 业务数据
            if (key == "/businessData") {
                console.log("businessData数据为", obj.data);
                try {
                    let data = JSON.parse(obj.data);
                    if (data.code == "200") {
                        // 重新加载grid数据
                        this.$refs.grid.load();
                    }
                } catch (error) {
                    console.error("解析JSON字符串时出错:", error);
                }
            }
        }
    },
    mounted: function () {
        this.search();
        //监听实时数据推送
        this.pusherSubscribe(["/realTimeData", "/businessData"]);
    },
}
</script>