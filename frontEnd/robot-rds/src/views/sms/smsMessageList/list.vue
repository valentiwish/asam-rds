<template>
    <div>
        <page-title></page-title>
        <div class="page-search">
            <div class="page-search-title">查询条件</div>
            <div class="page-search-content">
                <Form :label-width="85" @submit.native.prevent="search">
                    <FormItem label="创建时间">
                        <DatePicker type="daterange" v-model="checkDate" placement="right-start" style="width: 165px;" placeholder="请选择创建时间"></DatePicker>
                    </FormItem>
                    <FormItem label="手机号码" prop="phones">
                        <Input v-model.trim="smsMessageList.phones" placeholder="请输入手机号码" style="width: 165px;" maxlength="11" @keyup.enter.native="search()" />
                    </FormItem>
                    <FormItem label="短信内容" prop="content">
                        <Input v-model.trim="smsMessageList.content" placeholder="请输入短信内容" style="width: 165px;" maxlength="30" @keyup.enter.native="search()" />
                    </FormItem>
                    <FormItem label="发送状态" prop="sendStatus">
                        <Select v-model="smsName" placeholder="请选择发送状态" style="width: 165px;">
                            <Option v-for="obj in smsStateData" :value="obj.id" :key="obj.id" >{{obj.name}}</Option>
                        </Select>
                    </FormItem>
                    <FormItem label="模板名称" prop="tempName">
                        <Input v-model.trim="smsMessageList.tempName" placeholder="请输入短信模板名称" style="width: 165px;" maxlength="200" @keyup.enter.native="search()" />
                    </FormItem>
                </Form>
                <div class="search-btn">
                    <allow-btn class="btn" icon="ios-search" allow="allowsearch" type="primary" @click="search()">查询</allow-btn>
                    <allow-btn class="btn" icon="ios-refresh" allow="allowreset" type="error" @click="clear()">重置</allow-btn>                   
                </div>
            </div>
        </div>
        <div class="page-tools">
             <allow-btn class="btn" icon="md-add" allow="新增" type="success" @click="addMessage()">发送短信</allow-btn>
        </div>
        <div class="page-content">
            <data-grid :option="gridOption" ref="grid"></data-grid>
        </div>
        <!-- 发送短信-->
        <Modal v-model="modal_add" title="发送短信">
            <add-modal ref="addform" @changeVal="changeVal"></add-modal>
            <div slot="footer">
                <Button type="warning" class="ivu-btn ivu-btn-primary" @click="save" :loading="loading">存草稿</Button>
                <Button type="primary" class="ivu-btn ivu-btn-primary" @click="toSend" :loading="loading">发送</Button>
                <Button type="ghost" style="margin-left: 8px" @click="modal_add=false">关闭</Button>
            </div>
        </Modal>
        <!-- 查看短信-->
        <Modal v-model="modal_view" title="查看短信">
            <update-modal :id="viewid" type="view"></update-modal>
            <div slot="footer">
                <Button type="ghost" @click="modal_view=false">关闭</Button>
            </div>
        </Modal>
        <!-- 修改短信-->
        <Modal v-model="modal_update" title="修改短信">
            <update-modal :id="id" ref="updateform" @changeVal="changeVal"></update-modal>
            <div slot="footer">
                <Button type="primary" class="ivu-btn ivu-btn-primary" @click="update" :loading="loading">保存</Button>
                <Button type="ghost" style="margin-left: 8px" @click="modal_update=false">关闭</Button>
            </div>
        </Modal>
    </div>
</template>
<script>
import dataGrid from "@/components/datagrid.vue";
import addModal from "./add-modal.vue";
import updateModal from "./update-modal.vue";
export default {
    components: {
        dataGrid,
        addModal,
        updateModal
    },
    data() {
        var that = this;
        return {
            smsName: '',
            smsStateData: [
                { 'name': '已发送', 'id': 1 },
                { 'name': '未发送', 'id': 0 }
            ],
            loading: false,
            id: '', //页面修改赋值
            viewid: '', //页面查看赋值
            checkDate: '',
            smsMessageList: {
                id: '',
                batchNo: '',
                content: '',
                tempId: '',
                tempName: '',
                parameNames: '',
                parameData: '',
                phones: '',
                sendData: '',
                sendStatus: '',
                sendUserId: '',
                sendUserName: '',
                sendUserOrg: '',
                snedUserPhone: '',
                updateTime: '',
            },
            smsMessageTemplate: {
                id: '',
                name: '',
                content: '',
                parameNames: '',
                parameNum: '',
                isValid: '',
                updateTime: '',
            },
            modal_add: false, //新增弹窗
            modal_view: false, //查看弹窗
            modal_update: false, //修改弹窗
            //以上是界面v-model的所有参数
            "gridOption": {
                // "checkbox": {
                //     "enable": false
                // },
                "header": true,
                "iconCls": "fa-power-off",
                "data": [],
                "url": "/service_sms/SMS/list",
                "contentType":"json",
                "columns": [ // 列配置
                    {
                        "title": "批次号",
                        "align": "center",
                        "key": "batchNo"
                    },
                    {
                        "title": "创建时间",
                        "align": "center",
                        "key": "createTime",
                        "render": function(a, param) {
                            return new Date(param.row.createTime).Format('yyyy-MM-dd hh:mm:ss');
                        }
                    },
                    {
                        "title": "发送时间",
                        "align": "center",
                        "key": "sendDate",
                        "render": function(a, param) {
                            return new Date(param.row.sendDate).Format('yyyy-MM-dd hh:mm:ss');
                        }
                    },
                    {
                        "title": "手机号码",
                        "align": "center",
                        "key": "phones",
                    },
                    {
                        "title": "短信内容",
                        "align": "center",
                        "key": "content",
                        "ellipsis": true
                    },
                    {
                        "title": "状态",
                        "align": "center",
                        "key": "sendStatus",
                        "render": function(h, params) {
                            var hh = h("div", [
                                h("span", {
                                    style: {
                                        marginRight: '5px'
                                    }
                                }, params.row.sendStatus == 1 ? "已发送" : "未发送"),
                            ]);
                            return hh;
                        }
                    },
                    {
                        "title": "模板名称",
                        "align": "center",
                        "key": "tempName"
                    },
                    {
                        key: 'tool',
                        title: '操作',
                        "align": "center",
                        "width": 290,
                        render(h, params) {
                            return h('div', [
                                h('allow-btn', {
                                    props: {
                                        type: 'primary',
                                        size: 'small',
                                        allow: "查看",
                                        icon: 'md-document',
                                    },
                                    style: {
                                        marginRight: '5px'
                                    },
                                    on: {
                                        click: () => {
                                            that.toView(params.row);
                                        }
                                    }
                                }, '查看'),
                                h('allow-btn', {
                                    props: {
                                        type: 'info',
                                        size: 'small',
                                        allow: "修改",
                                        icon: 'ios-create',
                                        disabled: params.row.sendStatus == 1 ? true : false,
                                    },
                                    style: {
                                        marginRight: '5px'
                                    },
                                    on: {
                                        click: () => {
                                            that.toUpdate(params.row)
                                        }
                                    }
                                }, '修改'),
                                h('allow-btn', {
                                    props: {
                                        type: 'info',
                                        size: 'small',
                                        allow: "修改",
                                        icon: 'ios-create',
                                        disabled: params.row.sendStatus == 1 ? true : false,
                                    },
                                    style: {
                                        marginRight: '5px'
                                    },
                                    on: {
                                        click: () => {
                                            that.toSend(params.row)
                                        }
                                    }
                                }, '发送'),
                                h('allow-btn', {
                                    props: {
                                        type: 'error',
                                        size: 'small',
                                        allow: "删除",
                                        icon: 'md-close',
                                        disabled: params.row.sendStatus == 1 ? true : false,
                                    },
                                    style: {
                                        marginRight: '5px'
                                    },
                                    on: {
                                        click: () => {
                                            that.del(params.row)
                                        }
                                    }
                                }, '删除')
                            ]);
                        }
                    }
                ],
                "loadFilter": function(data) {
                    return data.data;
                }
            }
        }
    },
    methods: {
        //跳转到发送短信页面
        addMessage: function() {
            this.modal_add = true;
            this.$refs['addform'].$refs['smsMessageList'].resetFields();
            this.$refs['addform'].createBatchNo();
        },
        //查看
        toView: function(obj) {
            this.viewid = obj.id;
            this.modal_view = true;
        },
        //修改
        toUpdate: function(obj) {
            this.id = obj.id;
            this.modal_update = true;
        },
        //发送
        toSend: function(obj) {
            var objList = [];
            objList.push(JSON.stringify(obj));
            //获取服务器数据
            let that = this;
            this.$Modal.confirm({
                title: '短信发送',
                content: '您确认发送此条短信？',
                onOk: function() {
                    var url = "/service_sms/SMS/sendMessages";
                    this.$ajax.postJson(url, objList,{'Content-Type': 'application/json;'})
                        .then((res) => {
                            if(res.data.code == "200"){                                
                                this.$Message.success('短信发送成功');
                                that.$refs['grid'].loadData();
                            }else{
                                this.$Message.error('短信发送失败');
                            }
                        })
                        .catch((error) => {
                            if (!error.url) {
                                console.info(error);
                            }
                        })
                }
            })
        },
        //删除
        del: function(obj) {
            //获取服务器数据
            let that = this;
            this.$Modal.confirm({
                title: '删除',
                content: '您确认删除此条数据？',
                onOk: function() {
                    var idvalue = obj.id;
                    var url = "/service_sms/SMS/delete";
                    this.$ajax.postJson(url, {
                            'id': idvalue
                        })
                        .then((res) => {
                            that.$refs['grid'].loadData();
                        })
                        .catch((error) => {
                            if (!error.url) {
                                console.info(error);
                            }
                        })
                }
            })
        },
        //以下二个是界面的方法
        search: function() {
            let startDate = "";
            let endDate = "";
            if (this.checkDate[0] !== "" && this.checkDate[0] !== undefined) {
                startDate = new Date(this.checkDate[0]).Format('yyyy-MM-dd');
            }
            if (this.checkDate[1] !== "" && this.checkDate[1] !== undefined) {
                endDate = new Date(this.checkDate[1]).Format('yyyy-MM-dd');
            }
            if (this.smsName == "已发送") {
                this.smsMessageList.sendStatus = 1;
            }
            if (this.smsName == "未发送") {
                this.smsMessageList.sendStatus = 0;
            }

            this.$refs.grid.load({
                'startDate': startDate,
                'endDate': endDate,
                'phones': this.smsMessageList.phones,
                'content': this.smsMessageList.content,
                'sendStatus': this.smsMessageList.sendStatus,
                'tempName': this.smsMessageList.tempName
            });
        },
        clear: function() {
            this.smsName = "";
            this.checkDate = "";
            this.smsMessageList.phones = "";
            this.smsMessageList.content = "";
            this.smsMessageList.sendStatus = "";
            this.smsMessageList.tempName = "";
            this.$refs.grid.load({});
        },
        //以下二个是子界面按钮的方法
        save: function(cb) {
            this.loading = true;
            this.$refs['addform'].save((flag) => {
                if (flag == true) {
                    this.modal_add = false;
                    this.loading = false;
                    this.$refs.grid.reLoad({});
                }
            });
        },
        update: function(cb) {
            this.loading = true;
            this.$refs['updateform'].save((flag) => {
                if (flag == true) {
                    this.modal_update = false;
                    this.loading = false;
                    this.$refs.grid.reLoad({});
                    this.viewid = "";
                    this.id = "";
                }
            });
        },
        //加载旋转方法
        changeVal: function(val) {
            this.modal_add = val;
            this.$refs.grid.loadData();
            this.loading = false;
        },
    },
    mounted: function() {
    }
}

</script>
<style>


</style>
