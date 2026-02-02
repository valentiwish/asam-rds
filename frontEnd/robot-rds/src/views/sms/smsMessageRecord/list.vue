<template>
    <div>
        <page-title></page-title>
        <div class="page-search">
            <div class="page-search-title">查询条件</div>
            <div class="page-search-content">
                <Form :model="smsMessageRecord" :label-width="85" @submit.native.prevent="search">
                    <FormItem label="发送时间">
                        <DatePicker type="daterange" v-model="checkDate" placement="right-start" placeholder="请选择发送时间"></DatePicker>
                    </FormItem>
                    <FormItem label="手机号码" prop="phone">
                        <Input v-model.trim="smsMessageRecord.phone" placeholder="请输入手机号码" maxlength="30" @keyup.enter.native="search()" />
                    </FormItem>
                    <FormItem label="短信内容" prop="content">
                        <Input v-model.trim="smsMessageRecord.content" placeholder="请输入短信内容" maxlength="30" @keyup.enter.native="search()" />
                    </FormItem>
                </Form>
                <div class="search-btn">
                    <allow-btn class="btn" icon="ios-search" allow="allowsearch" type="primary" @click="search()">查询</allow-btn>
                    <allow-btn class="btn" icon="ios-refresh" allow="allowreset" type="error" @click="clear()">重置</allow-btn>
                </div>
            </div>
        </div>
        <div class="page-content">
            <data-grid :option="gridOption" ref="grid"></data-grid>
        </div>
        <!-- 查看短信记录-->
        <Modal v-model="modal_view" title="查看短信记录">
            <update-modal :id="viewid" type="view"></update-modal>
            <div slot="footer">
                <Button type="ghost" @click="modal_view=false">关闭</Button>
            </div>
        </Modal>
    </div>
</template>
<script>
import dataGrid from "@/components/datagrid.vue";
import updateModal from "./update-modal.vue"
export default {
    components: {
        dataGrid,
        updateModal
    },
    data() {
        var that = this;
        return {
            accountInfo: '',
            loading: false,
            id: '', //页面修改赋值
            viewid: '', //页面查看赋值
            checkDate: '',
            smsMessageRecord: {
                accountName: '',
                accountNo: '',
                batchNo: '',
                content: '',
                tempId: '',
                tempName: '',
                phone: '',
                sendDate: '',
                sendStatus: '',
                sendUserId: '',
                sendUserName: '',
                sendUserOrg: '',
                sendUserPhone: '',
            },
            status: [
                '未发送',
                '已发送'
            ],
            modal_view: false, //查看弹窗
            //以上是界面v-model的所有参数
            "gridOption": {
                "header": true,
                "iconCls": "fa-power-off",
                "data": [],
                "url": "/service_sms/SMSRecord/list",
                "contentType":"json",
                "columns": [ // 列配置
                    {
                        "title": "批次号",
                        "align": "center",
                        "key": "batchNo"
                    },
                    // {
                    //     "title": "接收人",
                    //     "align": "center",
                    //     "key": "accountName",
                    //     "render": function(h, params) {
                    //         var hh = h("div", [
                    //             h("span", {
                    //                 style: {
                    //                     marginRight: '5px'
                    //                 }
                    //             }, params.row.accountName == null ? "--" : params.row.accountName),
                    //         ]);
                    //         return hh;
                    //     },
                    // },
                    {
                        "title": "手机号码",
                        "align": "center",
                        "key": "phone"
                    },
                    {
                        "title": "短信内容",
                        "align": "center",
                        "key": "content",
                        "ellipsis": true
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
                        "title": "发送人",
                        "align": "center",
                        "key": "sendUserName"
                    },
                    {
                        key: 'tool',
                        title: '操作',
                        "align": "center",
                        "width": 160,
                        render(h, params) {
                            return h('div', [
                                h('allow-btn', {
                                    props: {
                                        type: 'primary',
                                        size: 'small',
                                        allow: "查看",
                                        icon: 'md-document'
                                    },
                                    style: {
                                        marginRight: '5px'
                                    },
                                    on: {
                                        click: () => {
                                            that.toView(params.row)
                                            // alert(params.row);
                                        }
                                    }
                                }, '查看')
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
        //前三个为“行数据”中的方法
        toView: function(obj) {
            this.viewid = obj.id;
            this.modal_view = true;
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
            this.$refs.grid.load({
                'startDate': startDate,
                'endDate': endDate,
                'accountName': this.smsMessageRecord.accountName,
                'accountNo': this.smsMessageRecord.accountNo,
                'phone': this.smsMessageRecord.phone,
                'content': this.smsMessageRecord.content
            });
        },
        clear: function() {
            this.checkDate = "";
            this.smsMessageRecord.accountName = "";
            this.smsMessageRecord.accountNo = "";
            this.smsMessageRecord.phone = "";
            this.smsMessageRecord.content = "";

            this.$refs.grid.load({});
        },
        //加载旋转方法
        changeVal: function() {
            this.loading = false;
        },
    },
    created() {}
}

</script>
<style>


</style>
