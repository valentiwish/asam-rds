<template>
    <div>
        <page-title></page-title>
        <div class="page-search">
            <div class="page-search-title">查询条件</div>
            <div class="page-search-content">
                <Form :model="smsMessageTemplate" :label-width="80" @submit.native.prevent="search">
                    <FormItem label="模板名称" prop="name">
                        <Input v-model.trim="smsMessageTemplate.name" placeholder="请输入模板名称" maxlength="50" @keyup.enter.native="search()"></Input>
                    </FormItem>
                    <FormItem label="参数名称" prop="parameNames">
                        <Input v-model.trim="smsMessageTemplate.parameNames" placeholder="请输入参数名称" maxlength="50" @keyup.enter.native="search()"></Input>
                    </FormItem>
                    <FormItem label="模板内容" prop="content">
                        <Input v-model.trim="smsMessageTemplate.content" placeholder="请输入模板内容" maxlength="50" @keyup.enter.native="search()"></Input>
                    </FormItem>
                    <FormItem label="启用状态" prop="isValid" >
                        <Select v-model="emplateName" placeholder="请选择启用状态">
                            <Option v-for="obj in valid" :value="obj.id" :key="obj.id">{{obj.name}}</Option>
                        </Select>
                    </FormItem>
                </Form>
                <div class="search-btn">
                    <allow-btn class="btn" icon="ios-search" allow="allowsearch" type="primary" @click="search()">查询</allow-btn>
                    <allow-btn class="btn" icon="ios-refresh" allow="allowreset" type="error" @click="clear()">重置</allow-btn>
                    
                </div>
            </div>
        </div>
        <div class="page-tools">
            <allow-btn class="btn" icon="md-add" allow="新增" type="success" @click="modal_add=true">新增模板</allow-btn>
        </div>
        <div class="page-content">
            <data-grid :option="gridOption" ref="grid"></data-grid>
        </div>
        <!-- 新增短信模板-->
        <Modal v-model="modal_add" title="新增短信模板">
            <update-modal :key="timer" ref="addform" @changeVal="changeVal" type="add"></update-modal>
            <div slot="footer">
                <Button type="primary" class="ivu-btn ivu-btn-primary" @click="save" :loading="loading">保存</Button>
                <Button type="ghost" style="margin-left: 8px" @click="close">关闭</Button>
            </div>
        </Modal>
        <!-- 查看短信模板-->
        <Modal v-model="modal_view" title="查看短信模板">
            <update-modal :id="viewid" type="view"></update-modal>
            <div slot="footer">
                <Button type="ghost" @click="modal_view=false">关闭</Button>
            </div>
        </Modal>
        <!-- 修改短信模板-->
        <Modal v-model="modal_update" title="修改短信模板">
            <update-modal :id="id" ref="updateform" type="update" @changeVal="changeVal"></update-modal>
            <div slot="footer">
                <Button type="primary" class="ivu-btn ivu-btn-primary" @click="update" :loading="loading">保存</Button>
                <!-- <Button type="ghost" style="margin-left: 8px" @click="modal_update=false">关闭</Button> -->
                <Button type="ghost" style="margin-left: 8px" @click="updateClose">关闭</Button>
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
            emplateName: '',
            loading: false,
            id: '', //页面修改赋值
            viewid: '', //页面查看赋值
            smsMessageTemplate: {
                id: '',
                name: '',
                content: '',
                parameNames: '',
                parameNum: '',
                isValid: '',
                updateTime: '',
            },
            valid: [
                { 'name': '启用', 'id': 1 },
                { 'name': '禁用', 'id': 0 }
            ],
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
                "url": "/service_sms/smsMessageTemplate/list",
                "contentType":"json",
                "columns": [ // 列配置
                    {
                        "title": "模板编码",
                        "align": "center",
                        "key": "code"
                    },
                    {
                        "title": "模板名称",
                        "align": "center",
                        "key": "name"
                    },
                    {
                        "title": "参数名称",
                        "align": "center",
                        "key": "parameNames"
                    },
                    {
                        "title": "模板内容",
                        "align": "center",
                        "key": "content",
                        "ellipsis": true
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
                        "title": "启用状态",
                        "align": "center",
                        "key": "isValid",
                        "render": function(h, params) {
                            var hh = h("div", [
                                h("span", {
                                    style: {
                                        marginRight: '5px'
                                    }
                                }, params.row.isValid == 1 ? "启用" : "禁用"),
                            ]);
                            return hh;
                        },
                    },
                    {
                        /* key: 'tool',
                         title: '操作',*/
                        "title": "操作",
                        "align": "center",
                        "key": "isValid",
                        "width": 220,
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
                                }, '查看'),
                                h('allow-btn', {
                                    props: {
                                        type: 'info',
                                        size: 'small',
                                        allow: "修改",
                                        icon: 'ios-create'
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
                                        type: 'error',
                                        size: 'small',
                                        allow: "删除",
                                        icon: 'md-close'
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
        //前三个为“行数据”中的方法
        toView: function(obj) {
            this.viewid = obj.id;
            this.modal_view = true;
        },
        toUpdate: function(obj) {
            this.id = obj.id;
            this.modal_update = true;
        },
        del: function(obj) {
            //获取服务器数据
            let that = this;
            this.$Modal.confirm({
                title: '删除',
                content: '您确认删除此条数据？',
                onOk: function() {
                    var idvalue = obj.id;
                    var url = "/service_sms/smsMessageTemplate/delete";
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
            if (this.emplateName == "启用") {
                this.smsMessageTemplate.isValid = 1;
            }
            if (this.emplateName == "禁用") {
                this.smsMessageTemplate.isValid = 0;
            }
            this.$refs.grid.load({
                'name': this.smsMessageTemplate.name,
                'content': this.smsMessageTemplate.content,
                'parameNames': this.smsMessageTemplate.parameNames,
                'isValid': this.smsMessageTemplate.isValid
            });
        },
        clear: function() {
            this.emplateName = "";
            this.smsMessageTemplate.name = "";
            this.smsMessageTemplate.content = "";
            this.smsMessageTemplate.parameNames = "";
            this.smsMessageTemplate.isValid = "";
            this.$refs.grid.load({
                'name': this.smsMessageTemplate.name,
                'content': this.smsMessageTemplate.content,
                'parameNames': this.smsMessageTemplate.parameNames,
                'isValid': this.smsMessageTemplate.isValid
            });
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
                this.timer = new Date().getTime();
            });
        },
        close: function(cb) {
            this.modal_add = false;
            // this.$refs['addform'].$refs['smsMessageTemplate'].resetFields();
            // this$refs['addform'].$refs['smsMessageTemplate'].reLoad();
            this.loading = false;
            this.timer = new Date().getTime();
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
        //关闭之后save按钮上的圆圈就没有了
        updateClose: function() {
            this.modal_update = false;
            this.loading = false;
        },
        //加载旋转方法
        changeVal: function() {
            this.loading = false;
        },
    },
    mounted() {
        
    }
}

</script>
<style>


</style>
