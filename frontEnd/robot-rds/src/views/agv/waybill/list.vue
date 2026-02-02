<template>
    <div>
        <page-title></page-title>
        <div class="page-search">
            <!-- <div class="page-search-title">查询条件</div>
            <div class="page-search-content">
                <Form ref="waybill" :model="waybill" :label-width="100">
                    <FormItem label="运单ID" prop="id">
                        <Input v-model.trim="waybill.id" placeholder="请输入运单ID，不超过50字" maxlength="50"></Input>
                    </FormItem>
                    <FormItem label="机器人名称" prop="robotName">
                        <Input v-model.trim="waybill.robotName" placeholder="请输入机器人名称，不超过50字" maxlength="50"></Input>
                    </FormItem>
                    <FormItem label="状态" prop="waybillStatus">
                        <Input v-model.trim="waybill.waybillStatus" placeholder="请输入光通信站点位，不超过30字" maxlength="30"></Input>
                    </FormItem>
                </Form>
                <div class="search-btn">
                  <allowBtn allow="allowsearch" size="large" @click="getData()">查询</allowBtn>
                  <allowBtn allow="allowreset" size="large" @click="reset">重置</allowBtn>
                  <allowBtn allow="新增" type="success" icon="plus" size="large" @click="modal_add=true">新增</allowBtn>
                </div>
            </div> -->

            <!--查看运单运行详情-->
            <Modal v-model="runInfo" title="查看运行详情" footer-hide @on-cancel="closeModel">
                <Table :columns="runInfoColumns" :data="runInfoList" class="warning-table"
                    @on-selection-change="selectionRows"></Table>
                <div style="display: flex; justify-content: space-between;margin-top: 10px;">
                    <Button type="primary" @click="closeModel">关闭</Button>
                </div>
            </Modal>

            <!--查看错误详情-->
            <Modal v-model="errorInfo" title="查看错误详情" footer-hide @on-cancel="cancel">
                <Table :columns="errorInfoColumns" :data="errorInfoList" class="warning-table"
                    @on-selection-change="selectionRows"></Table>
            </Modal>
        </div>
        <div class="page-content">
            <data-grid :option="gridOption" ref="grid">
            </data-grid>
        </div>
    </div>
</template>
<script>
import dataGrid from "@/components/datagrid"
import fontIcon from "@/components/fontIcon"
import updateModal from "./cur"
export default {
    components: { dataGrid, fontIcon, updateModal },
    data: function () {
        var that = this;
        return {
            loading: false,
            id: '', //页面修改赋值
            b: false,//定时任务开启标识
            waybill: {
                id: '',
                taskId: '',
                taskName:'',
                robotName: '',
                robotGroupName: '',
                communicationType: '',
                priority: '',
                waybillStatus: '',
                createTime: '',
                orderTime: '',
                completeTime: '',
                executionTime: '',
                errorMessage: '',
            },
            errorInfoList: [],
            errorInfoColumns: [
                {
                    title: '错误码',
                    key: 'errorCode'
                },
                {
                    title: '错误次数',
                    key: 'errorFrequency'
                },
                {
                    title: '错误时间',
                    key: 'errorTime'
                },
                {
                    title: '错误描述',
                    key: 'errorDescription'
                }
            ],
            runInfoList: [],
            runInfoColumns: [
                {
                    title: '块ID',
                    key: 'blockId'
                },
                {
                    title: '库位',
                    key: 'storageLoc'
                },
                {
                    title: '操作',
                    key: 'operation'
                },
                {
                    title: '状态',
                    key: 'waybillStatus'
                }
            ],
            runInfo: false, //查看运行详情
            errorInfo: false, //查看错误详情
            modal_update: false, //修改弹窗
            modal_view: false, //查看弹窗
            modal_add: false, //新增弹窗
            //以上是界面v-model的所有参数
            gridOption: {
                //用于关闭自动刷新
                url: "/service_rms/waybill/list",
                stripe: false,
                auto: true,//是否自动刷新
                // highlightRow: true,//可以选中某一行
                data: [],
                columns: [ //列配置
                    { "title": "序号", "key": "serialNumber", "align": "center", "width": "80px"},
                    { "title": "任务名称", "key": "taskName", "align": "center" },
                    { "title": "机器人", "key": "robotName", "align": "center" },
                    { "title": "分组", "key": "robotGroupName", "align": "center", "width": "100px"},
                    { "title": "通信方式", "key": "communicationType", "align": "center","width": "150px"},
                    { "title": "优先级", "key": "priority", "align": "center", "width": "150px"},
                    {
                        "title": "状态", "key": "waybillStatus", "align": "center", "width": "150px",
                        "render": function (h, p) {
                            var obj = {
                                0: {
                                    name: "等待",
                                    color: "default"
                                },
                                1: {
                                    name: "正在执行",
                                    color: "green"
                                },
                                2: {
                                    name: "完成",
                                    color: "success"
                                },
                                3: {
                                    name: "终止",
                                    color: "magenta"
                                },
                                4: {
                                    name: "取消",
                                    color: "magenta"
                                }, 
                                5: {
                                    name: "等待放行",
                                    color: "green"
                                },
                                6: {
                                    name: "取货完成",
                                    color: "green"
                                },
                                7: {
                                    name: "放货完成",
                                    color: "green"
                                },
                            }
                            if (obj[p.row.waybillStatus]) {
                                return h("Tag", {
                                    props: {
                                        color: obj[p.row.waybillStatus].color,
                                    }
                                }, obj[p.row.waybillStatus].name)
                            }
                            return "";
                        }
                    },
                    // { "title": "创建时间", "key": "createTime", "align": "center"}, 
                    { "title": "接单时间", "key": "orderTime", "align": "center" },
                    { "title": "完成时间", "key": "completeTime", "align": "center" },
                    { "title": "执行耗时", "key": "executionTime", "align": "center", "width": "100px"},
                    {
                        "key": 'id', title: '操作', "align": "center", "width": "450px",
                        render(h, params) {
                            return h('div', [
                                h('allowBtn', {
                                    props: {
                                        type: 'primary',
                                        size: 'small',
                                        allow: '查看任务'
                                    },
                                    style: {
                                        marginRight: '5px'
                                    },
                                    on: {
                                        click: () => {
                                            that.getTaskView(params.row)
                                        }
                                    }
                                }, '查看任务'),
                                h('allowBtn', {
                                    props: {
                                        type: 'primary',
                                        size: 'small',
                                        allow: '运行详情'
                                    },
                                    style: {
                                        marginRight: '5px'
                                    },
                                    on: {
                                        click: () => {
                                            that.getRunInfosList(params.row)
                                        }
                                    }
                                }, '运行详情'),
                                h('allowBtn', {
                                    props: {
                                        type: 'info',
                                        size: 'small',
                                        allow: "错误详情"
                                    },
                                    style: {
                                        marginRight: '5px'
                                    },
                                    on: {
                                        click: () => {
                                            that.getErrorInfosList(params.row)
                                        }
                                    }
                                }, '错误详情'),
                                h('allowBtn', {
                                    props: {
                                        type: 'info',
                                        size: 'small',
                                        allow: '取消任务',
                                    },
                                    style: {
                                        marginRight: '5px'
                                    },
                                    on: {
                                        click: () => {
                                            that.pauseNav(params.row)
                                        }
                                    }
                                }, "取消任务"),
                                h('allowBtn', {
                                    props: {
                                        type: 'info',
                                        size: 'small',
                                        allow: '返回待命点',
                                    },
                                    style: {
                                        marginRight: '5px'
                                    },
                                    on: {
                                        click: () => {
                                            that.backParkPoint(params.row)
                                        }
                                    }
                                }, "返回待命点"),
                                h('allowBtn', {
                                    props: {
                                        type: 'info',
                                        size: 'small',
                                        allow: '删除',
                                    },
                                    style: {
                                        marginRight: '5px'
                                    },
                                    on: {
                                        click: () => {
                                            that.toDelete(params.row)
                                        }
                                    }
                                }, "删除"),
                            ])
                        }
                    }
                ],
                // pagination:{
                //     "countRecord":0, //总数据条数
                //     "pageSize":15,   //当前页面数据容量
                //     "curPage":1      //当前页码 
                // },
                "loadFilter": function (data) {
                    console.log(data.data)
                    return data.data;
                },
            },
        }
    },
    methods: {
        toDelete: function (obj) {
            let that = this;
            this.$Modal.confirm({
                title: '删除',
                content: '您确认删除此条数据？',
                onOk: function () {
                    var idValue = obj.id;
                    var url = "/service_rms/waybill/delete";
                    this.$ajax.post(url, { 'id': idValue })
                        .then((res) => {
                            that.search();
                        })
                        .catch((error) => {
                            if (!error.url) { console.info(error); }
                        })
                }
            })
        },
        getErrorInfosList: function (obj) {
            this.errorInfo = true;
            this.id = obj.id;
            this.$ajax.post('/service_rms/waybill/getErrorMsg', { 'id': this.id })
                .then((res) => {
                    if (this.getWarningList(res.data)) {
                        this.errorInfoList = res.data.data;
                    }
                })
        },

        getWarningList: function (res) {
            if (res.code != 200) {
                let obj = {
                    msg: res.msg,
                    time: new Date().Format("yyyy-MM-dd hh:mm:ss"),
                }
                this.warningList.push(obj);
                this.$Message.error('操作出错！');
                return false;
            }
            return true;
        },
        search: function () {
            console.log(JSON.stringify(this.waybill))
            this.$refs.grid.load({ 'data': JSON.stringify(this.waybill) });
        },
        reset: function () {
            this.waybill.id = "";
            this.waybill.robotName = "";
            this.waybill.waybillStatus = "";
            this.search();
        },
        save: function (cb) {
            this.$refs['addform'].save((flag) => {
                if (flag == true) {
                    this.modal_add = false;
                    this.loading = false;
                    this.$refs.grid.reLoad({});
                }
            });
        },
        update: function (cb) {
            this.loading = true;
            this.$refs['updateform'].save((flag) => {
                if (flag == true) {
                    this.modal_update = false;
                    this.loading = false;
                    this.$refs.grid.reLoad({});
                    this.viewId = "";
                    this.id = "";
                }
            });
        },
        //暂停导航
        pauseNav: function (obj) {
            this.$Modal.confirm({
                title: '取消导航',
                content: '要取消当前导航吗？',
                onOk: function () {
                    var id = obj.id;
                    var url = "/service_rms/waybill/pauseNav";
                    this.$ajax.post(url, { 'id': id })
                        .then((res) => {
                            that.search();
                            if (res.data.code != 200) {
                                this.$Message.error(res.data.msg);
                            } else {
                                this.$Message.success('任务取消成功');
                            }
                        })
                        .catch((error) => {
                            if (!error.url) { console.info(error); }
                        })
                }
            })
        },
        //继续导航
        continueNav: function (obj) {
            this.$Modal.confirm({
                title: '继续导航',
                content: '要继续当前导航吗？',
                onOk: function () {
                    var id = obj.id;
                    var url = "/service_rms/waybill/continueNav";
                    this.$ajax.post(url, { 'id': id })
                        .then((res) => {
                            if (res.data.code != 200) {
                                this.$Message.error(res.data.msg);
                            }
                        })
                        .catch((error) => {
                            if (!error.url) { console.info(error); }
                        })
                }
            })
        },
         //返回待命点导航
         backParkPoint: function (obj) {
            this.$Modal.confirm({
                title: '返回待命点',
                content: '要返回待命点吗？',
                onOk: function () {
                    var id = obj.id;
                    var url = "/service_rms/waybill/goBackParkPoint";
                    this.$ajax.post(url, { 'id': id })
                        .then((res) => {
                            if (res.data.code != 200) {
                                this.$Message.error(res.data.msg);
                            }
                        })
                        .catch((error) => {
                            if (!error.url) { console.info(error); }
                        })
                }
            })
        },
        //加载旋转方法
        changeVal: function () {
            this.loading = false;
        },
        //点击查看详情，获取运单列表
        getRunInfosList: function (obj) {
            this.runInfo = true;
            this.id = obj.id;
            this.b = true;
            this.$ajax.post('/service_rms/waybill/getRunInfo', { 'id': this.id })
                .then((res) => {
                    if (this.getWarningList(res.data)) {
                        this.runInfoList = res.data.data;
                    }
                })
        },
        //实时获取运单列表
        getRealTimeRunInfosList: function (obj) {
            this.$ajax.post('/service_rms/waybill/getRunInfo', { 'id': this.id })
                .then((res) => {
                    if (this.getWarningList(res.data)) {
                        this.runInfoList = res.data.data;
                    }
                })
        },
        getTaskView: function(obj){
            // this.taskId = obj.taskId;
            // this.$router.push({
            //     path: '/agv/task/',
            //     query: {data: obj}
            // });
            console.log("obj.taskId",obj.taskId)
            this.$router.push("/agv/task/" + obj.taskId);

        },
        closeModel: function () {
            console.log("closeModel")
            this.runInfo = false;
            this.b = false;
        },
        //重新加载类型列表数据
        getData: function () {
            //获取此模块模块基本信息 根据id查询
            this.$ajax.post("/service_rms/waybill/list", {})
                .then(res => {
                    this.gridOption.data = res.data.data.list;
                })
                .catch((error) => {
                    if (!error.url) { console.info(error); }
                });
        },
    },
    mounted() {
        this.getData();
        //定时任务刷新前端页面
        this.$nextTick(() => {
            clearInterval(this.intervalId);
            this.intervalId = setInterval(() => {
                this.getData();
                if (this.b) {
                    this.getRealTimeRunInfosList();
                }
            }, 2000)
        });
        this.$once('hook:beforeDestroy', () => {
            if (this.intervalId) {
                clearInterval(this.intervalId);
            }
        })
    },
    beforeDestroy() {
        clearInterval(this.intervalId);
    },
}
</script>