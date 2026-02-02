<template>
    <div>
        <page-title></page-title>
        <div class="page-search">
            <div class="page-search-title">查询条件</div>
            <div class="page-search-content">
                <Form ref="s" v-model="s" :label-width="80"  @submit.native.prevent="submit">
                    <FormItem label="名称">
                        <Input v-model.trim="s.name" maxlength="50" placeholder="请输入职务名称"></Input>
                    </FormItem>
                    
                <Form-item label="职责" prop="duty">
                    <Input v-model.trim="s.duty" placeholder="请输入职责" maxlength="50"></Input>
                </Form-item>

                <Form-item label="任职条件" class="singleline">
                    <Input  v-model.trim="s.conditions"  maxlength="100"  placeholder="请输入任职条件"></Input>
                </Form-item>
            
                </Form>
                <div class="search-btn">
                    <allowBtn allow="allowsearch" icon="ios-search" @click="search">查询</allowBtn>
                    <allowBtn allow="allowreset" icon="ios-refresh-empty" @click="reset">重置</allowBtn>
                </div>
            </div>
        </div>
        <div class="page-tools">
            <allowBtn allow="create" type="success" faicon="fa-plus" @click="addModal">新增</allowBtn>
        </div>
        <div class="page-content">
            <data-grid :option="gridOption" ref="grid"></data-grid>
        </div>

        <!-- 新增属性-->
        <Modal v-model="modal_add" title="新增职务信息">
            <update-modal ref="addform"></update-modal>
            <div slot="footer">
                <Button type="primary" class="ivu-btn ivu-btn-primary" @click="save" :loading="loading">保存</Button>
                <Button type="ghost" style="margin-left: 8px" @click="modal_add=false">关闭</Button>
            </div>
        </Modal>
        <!-- 更新属性-->
        <Modal v-model="modal_update" title="更新职务信息">
            <update-modal :id="id" ref="updateform"></update-modal>
            <div slot="footer">
                <Button type="primary" class="ivu-btn ivu-btn-primary" @click="update" :loading="loading">保存</Button>
                <Button type="ghost" style="margin-left: 8px" @click="modal_update=false">关闭</Button>
            </div>
        </Modal>
        <!-- 查看属性-->
        <Modal v-model="modal_view" title="查看职务信息" >
            <update-modal :id="viewid" type="view"></update-modal>
            <div slot="footer">
                <Button type="ghost" @click="modal_view=false">关闭</Button>
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
                loading:false,
                id: '',  //页面修改赋值
                viewid:'',  //页面查看赋值
                modal_add:false,  //控制模块新增弹窗
                modal_update: false,  //控制模块修改弹窗
                modal_view: false, //控制模块查看弹窗
                s: {
                    name: '',
                    duty:'',
                    conditions:'',

                },
                spinShow: true, //未进入页面时，加载过程中显示加载中
                "gridOption": {
                    "url":"/service_user/position/list",
                    "data": [],
                    "columns": [
                        {
                            "title": "名称",
                            "key": "name",
                            "align": "center",
                        },
                        {
                            "title": "职责",
                            "key": "duty",
                            "align": "center",
                        },
                        {
                            "title": "任职条件",
                            "key": "conditions",
                            "align":"center",
                        },
                        {
                            "title": "备注",
                            "key": "remark",
                            "align":"center",
                        },
                        {
                            key: 'tool',
                            title: '操作',
                            align: "center",
                            "width": 260,
                            render(h, params) {
                                return h('div', [
                                    h('allowBtn', {
                                        props: {
                                            type: 'ghost',
                                            size: 'small',
                                            allow: 'view',
                                            faicon:"fa-file-text-o"
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
                                        type: 'ghost',
                                        size: 'small',
                                        allow: 'update',
                                        faicon:"fa-edit"
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
                                        type: 'ghost',
                                        size: 'small',
                                        allow: 'delete',
                                        faicon:"fa-close"
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
                    }
                }
            }
        },
        methods: {
            addModal: function() {
                this.$refs.addform.resetFields();
                this.modal_add = true;
            },
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
                this.$Modal.confirm({
                    title: '删除',
                    content: '您确认删除此条数据？',
                    onOk: () => {
                        this.$ajax.post("/service_user/position/delete", { 'id': obj.id })
                        .then((res) => {
                            if (200 == res.data.code) {
                                this.$Message.success('删除成功');
                                this.$refs.grid.reLoad({});
                            } else {
                                this.$Message.error('删除出错，信息为' + res.data.msg);
                            }
                        })
                    }
               })
            },
            //以下二个是界面的方法
            search: function() {
                this.$refs.grid.load(this.s);
            },
            reset: function() {
                this.s.name = "";
                this.s.duty = "";
                this.s.conditions = "";
            },
            //以下二个是子页面按钮的方法
            save: function(){
                this.loading = true;
                this.$refs['addform'].save((flag)=>{
                        if(flag){
                            this.modal_add = false;
                            this.$refs.grid.reLoad({});
                        }
                        this.loading = false;
                });
            },
            update:function(cb){
                this.loading = true;
                this.$refs['updateform'].save((flag)=>{
                    if(flag){
                        this.modal_update = false;
                        this.$refs.grid.reLoad({});
                        this.viewid = "";
                        this.id = "";
                    }
                    this.loading = false;
                });
            },
        },
        created: function() {
            
        }

    };

</script>
