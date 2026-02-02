<template>
    <div>
        <page-title>菜单管理</page-title>
        <div class="page-search">
            <div class="page-search-title">查询条件</div>
            <div class="page-search-content">
                <Form :label-width="80" @submit.native.prevent="search">
                    <FormItem label="名称">
                        <Input v-model.trim="name" placeholder="请输入名称" maxlength="30"></Input>
                    </FormItem>
                    <FormItem label="描述">
                        <Input v-model.trim="description" placeholder="请输入描述" maxlength="30"></Input>
                    </FormItem>
                </Form>
                <div class="search-btn">
                    <allowBtn allow="allowsearch" type="primary" icon="ios-search"  @click="search()">查询</allowBtn>
                    <allowBtn allow="allowreset" type="error" icon="ios-refresh-empty"  @click="clear()">重置</allowBtn>
                    <allowBtn allow="create" type="success" icon="plus"  @click="$refs.addform.resetFields();toAddPage()">新增菜单操作</allowBtn>
                </div>
            </div>
        </div>
        <div class="page-content">
            <data-grid :option="gridOption" ref="grid"></data-grid>
        </div>
        <!-- 新增菜单操作按钮-->
        <Modal v-model="modal_add" title="新增菜单操作按钮">
            <update-modal ref="addform" @changeVal="changeVal"></update-modal>
            <div slot="footer">
                <Button type="primary" class="ivu-btn ivu-btn-primary" @click="save" :loading="loading">保存</Button>
                <Button type="ghost" style="margin-left: 8px" @click="modal_add=false">关闭</Button>
            </div>
        </Modal>
        <!-- 更新菜单操作按钮-->
        <Modal v-model="modal_update" title="更新菜单操作按钮">
            <update-modal :id="id" ref="updateform" @changeVal="changeVal"></update-modal>
            <div slot="footer">
                <Button type="primary" class="ivu-btn ivu-btn-primary" @click="update" :loading="loading">保存</Button>
                <Button type="ghost" style="margin-left: 8px" @click="modal_update=false">关闭</Button>
            </div>
        </Modal>
        <!-- 查看菜单操作按钮-->
        <Modal v-model="modal_view" title="查看菜单操作按钮">
            <update-modal :id="viewid" type="view"></update-modal>
            <div slot="footer">
                <Button type="ghost" style="margin-left: 8px" @click="modal_view=false">关闭</Button>
            </div>
        </Modal>
    </div>
</template>
<script>
import dataGrid from "@/components/datagrid"
import CONFIG from '@/config/index.js'
import updateModal from "./cur"

export default {
    components: { dataGrid, updateModal },
    data: function() {
        var that = this;
        return {
            loading: false,
            id: '', //页面修改赋值
            viewid: '', //页面查看赋值
            modal_update: false, //控制菜单修改弹窗
            modal_view: false, //控制菜单查看弹窗
            modal_add: false, //控制菜单新增弹窗
            description: '', //查询条件：描述
            name: '', //查询条件：名称
            "gridOption": {
                "url": "/service_user/operation/list",
                "columns": [ //列配置
                    {
                        "title": "名称",
                        "key": "name",
                        "align": "center"

                    },
                    {
                        "title": "描述",
                        "key": "description",
                        "align": "center"

                    },
                    {
                        "title": "操作标识符",
                        "key": "operationOID",
                        "align": "center"

                    },
                    {
                        "title": "状态",
                        "key": "state",
                        "align": "center",
                        "render": function(a, param) {
                            if (param.row.state == 0) {
                                return "无效";
                            }
                            if (param.row.state == 1) {
                                return "有效";
                            }
                        }
                    },
                    {
                        key: 'id',
                        title: '操作',
                        align: "center",
                        width: 300,
                        render(h, params) {
                            return h('div', [
                                h('allowBtn', {
                                    props: {
                                        type: 'info',
                                        size: 'small',
                                        allow: 'view'
                                    },
                                    style: {
                                        marginRight: '5px'
                                    },
                                    on: {
                                        click: () => {
                                            that.toView(params.row)
                                        }
                                    }
                                }, '查看'),
                                h('allowBtn', {
                                    props: {
                                        type: 'primary',
                                        size: 'small',
                                        allow: 'update'
                                    },
                                    style: {
                                        marginRight: '5px'
                                    },
                                    on: {
                                        click: () => {
                                            that.toUpdate(params.row)
                                        }
                                    }
                                }, "修改"),
                                h('allowBtn', {
                                    props: {
                                        type: 'info',
                                        size: 'small',
                                        allow: 'delete'
                                    },
                                    style: {
                                        marginRight: '5px'
                                    },
                                    on: {
                                        click: () => {
                                            that.del(params.row)
                                        }
                                    }
                                }, '删除'),
                            ]);
                        }
                    }
                ],
                "loadFilter": function(data) {
                    return data.data;
                },
            }
        }
    },
    methods: {
        //打开新增数据类型弹窗
            toAddPage:function (){
                this.modal_add=true;
            },
            //打开修改数据类型弹窗
            toUpdate: function (obj) {
                this.id=obj.id;
                this.modal_update = true;
            },
            save:function(cb){
                this.loading = true;
                this.$refs['addform'].save((flag)=>{
                    if(flag == true){
                        this.modal_add = false;
                        this.loading=false;
                        this.$refs.grid.reLoad({});
                        this.clear();
                    }
                });
            },
            update:function(cb){
                this.loading = true;
                this.$refs['updateform'].save((flag)=>{
                    if(flag == true){
                        this.modal_update = false;
                        this.loading=false;
                        this.$refs.grid.reLoad({});
                        this.viewid="";
                        this.id="";
                    }
                });
            },
            //删除
            del:function(obj){
                var that=this;
                this.$Modal.confirm({
                    title: '删除',
                    content: '您确认删除此条数据？',
                    onOk: function () {
                        // 跳转
                        var idvalue=obj.id;
                        var url = "/service_user/operation/delete";
                        this.$ajax.post(url,{'id':idvalue })
                        .then((res) => {
                           that.$refs.grid.reLoad({});
                        })
                        .catch((error)=>{
                            if(!error.url){console.info(error);}
                        })
                    }
                })
            },
            search:function(){
                this.$refs.grid.load({'description': this.description,'name':this.name});
            },
            toView:function(obj){
                this.viewid=obj.id;
                this.modal_view = true;
                this.$refs.grid.load({ });
            },
            ////清除缓存的数据
            clear:function(){
                this.description = '';
                this.name = '';
            },
            //加载旋转方法
            changeVal:function(){
                this.loading=false;
            },
    },
}

</script>
