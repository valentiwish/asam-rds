<template>
    <div>
        <page-title></page-title>
        <div class="page-search">
            <div class="page-search-title">查询条件</div>
            <div class="page-search-content">
                <Form :label-width="100" @submit.native.prevent="search">
                    <FormItem label="应用编号">
                        <Input v-model.trim="searchObj.appid" placeholder="请输入应用编号，最多输入20字" maxlength="20"></Input>
                    </FormItem>
                    <FormItem label="应用密钥">
                        <Input v-model.trim="searchObj.appsecret" placeholder="请输入应用密钥，最多输入20字" maxlength="20"></Input>
                    </FormItem>
                    <FormItem label="服务商名称">
                        <Input v-model.trim="searchObj.providername" placeholder="请输入服务商名称，最多输入20字" maxlength="20"></Input>
                    </FormItem>
                </Form>
                <div class="search-btn">
                    <allowBtn allow="allowsearch" icon="ios-search" @click="search">查询</allowBtn>
                    <allowBtn allow="allowreset" icon="ios-refresh-empty" @click="clear">重置</allowBtn>
                    <allowBtn allow="create" type="success" icon="plus" @click="mv_add=true">新增</allowBtn>
                </div>
            </div>
        </div>
        <div class="page-content">
            <data-grid :option="gridOption" ref="grid"></data-grid>
        </div>

        <!-- 新增-->
        <Modal v-model="mv_add" title="新增认证信息">
            <div>
                <update-modal ref="addform" v-if="mv_add" @saveSuccess="saveSuccess"></update-modal>
            </div>
            <div slot="footer">
                <Button type="primary" class="ivu-btn ivu-btn-primary" @click="save">保存</Button>
                <Button type="ghost" class="ivu-btn ivu-btn-primary" @click="mv_add=false">关闭</Button>
            </div>
        </Modal>
        <!-- 更新-->
        <Modal v-model="mv_update" title="修改认证信息">
            <div>
                <update-modal :id="id" ref="updateform" v-if="mv_update" @saveSuccess="saveSuccess"></update-modal>
            </div>
            <div slot="footer">
                <Button type="primary" class="ivu-btn ivu-btn-primary" @click="update">保存</Button>
                <Button type="ghost" class="ivu-btn ivu-btn-primary" @click="mv_update=false">关闭</Button>
            </div>
        </Modal>
        <!-- 查看-->
        <Modal v-model="mv_view" title="查看认证信息">
            <div>
                <update-modal ref="viewform" v-if="mv_view"  :id="viewid" type="view"></update-modal>
            </div>
            <div slot="footer">
                <Button type="ghost" class="ivu-btn ivu-btn-primary" @click="mv_view=false">关闭</Button>
            </div>
        </Modal>
    </div>
</template>

<script>
    import dataGrid from "@/components/datagrid"
    import updateModal from "./cur"
    export default {
        components: { dataGrid, updateModal },
        data: function() {
            var that = this;
            return {
                id: '',
                searchObj:{
                    "appid":'',
                    "appsecret":'',
                    "providername":''
                },
                viewid:'',
                mv_add: false, //控制模块新增弹窗
                mv_update: false, //控制模块修改弹窗    
                mv_view:false,  
                "gridOption": {
                    "checkbox": {
                        "enable": false
                    },
                    "header": true,
                    "iconCls": "fa-power-off",
                    "data": [],
                    "url": "/service_user/ssoService/list",
                    "columns": [ //列配置
                        {
                            "title": "应用编号",
                            "align": "center",
                            "key": "appid",
                        },{
                            "title": "应用密钥",
                            "align": "center",
                            "key": "appsecret",
                        },{
                            "title": "安全域名",
                            "align": "center",
                            "key": "domain"
                        },{
                            "title": "服务商名称",
                            "align": "center",
                            "key": "providername",
                        },
                        {
                            key: 'tool',
                            title: '操作',
                            "align": "center",
                            "width": "250px",
                            render(h, params) {
                                return h('div', [
                                    h('allowBtn', {
                                        props: {
                                            type: 'success',
                                            size: 'small',
                                            allow: "view"
                                        },
                                        style: {
                                            marginRight: '5px'
                                        },
                                        on: {
                                            click: () => {
                                                that.toDownload(params.row);
                                            }
                                        }
                                    }, '查看'),
                                    h('allowBtn', {
                                        props: {
                                            type: 'primary',
                                            size: 'small',
                                            allow: "update"
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
                                    h('allowBtn', {
                                        props: {
                                            type: 'info',
                                            size: 'small',
                                            allow: "delete"
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
            search: function() {
                this.$refs.grid.load({ "appid": this.searchObj.appid, "appsecret": this.searchObj.appsecret,"providername": this.searchObj.providername});
            },
            clear: function() {
                this.searchObj.appid = "";
                this.searchObj.appsecret = "";
                this.searchObj.providername = "";
            },
            //打开修改模块弹窗
            toUpdate: function(obj) {
                this.id = obj.id;
                this.mv_update = true;
            },
            //保存数据
            save: function(cb) {
                this.$refs['addform'].save();
            },
            update: function(cb) {
                this.$refs['updateform'].save();
            },
            saveSuccess: function() {
                this.mv_add = false;
                this.mv_update = false;
                this.$refs.grid.reLoad({});
            },
            toDownload:function(obj){
                this.viewid = obj.id;
                this.mv_view = true;
            },
            del: function(obj) {
                var that = this;
                this.$Modal.confirm({
                    title: '删除',
                    content: '您确认删除此条数据？',
                    onOk: function() {
                        // 获取服务器数据
                        var idvalue = obj.id;
                        var url = "/service_user/ssoService/delete";
                        this.$ajax.post(url, { 'id': idvalue })
                            .then((res) => {
                                if (res.data.code == "200") {
                                    this.$Message.success('删除成功');
                                    that.$refs.grid.reLoad({});
                                } else {
                                    this.$Message.error('删除出错，信息为' + data.msg);
                                }
                            })
                    }
                })
            }
        },
        created: function() {
            
        },
        mounted: function(){

        },
        watch: {
            
        }
    };
</script>