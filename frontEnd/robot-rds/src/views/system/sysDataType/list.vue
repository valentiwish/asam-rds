<template>
    <div>
        <page-title>数据字典类型</page-title>
        <div class="page-search">
            <div class="page-search-title">查询条件</div>
            <div class="page-search-content">
                <Form :model="sysDataType" :label-width="100">
                    <FormItem label="类型编码">
                        <Input v-model.trim="sysDataType.typeCode" placeholder="请输入类型编码" maxlength="50">
                        </Input>
                    </FormItem>
                    <FormItem label="类型名称">
                        <Input v-model.trim="sysDataType.typeName" placeholder="请输入类型名称" maxlength="50">
                        </Input>
                    </FormItem>
                </Form>
                <div class="search-btn">
                    <allowBtn allow="allowsearch" type="primay" @click="search()">查询</allowBtn>
                    <allowBtn allow="allowreset" type="error" @click="reset()">重置</allowBtn>
                    
                </div>
            </div>
            <!-- 新增-->
            <Modal v-model="modal_add" title="新增数据类型">
                <update-modal ref="addform" @changeVal="changeVal"></update-modal>
                <div slot="footer">
                    <Button type="primary" class="ivu-btn ivu-btn-primary" @click="save" :loading="loading">保存</Button>
                    <Button type="ghost" @click="modal_add=false">关闭</Button>
                </div>
            </Modal>
            <!-- 更新-->
            <Modal v-model="modal_update" title="修改数据类型">
                <update-modal :id="id" ref="updateform" @changeVal="changeVal"></update-modal>
                <div slot="footer">
                    <Button type="primary" class="ivu-btn ivu-btn-primary" @click="update" :loading="loading">保存</Button>
                    <Button type="ghost" @click="modal_update=false">关闭</Button>
                </div>
            </Modal>
            <!-- 查看-->
            <Modal v-model="modal_view" title="查看数据类型">
                <update-modal :id="viewid" type="view"></update-modal>
                <div slot="footer">
                    <Button type="ghost" @click="modal_view=false">关闭</Button>
                </div>
            </Modal>
        </div>
        <div class="page-tools">
            <allowBtn allow="create" type="success" faicon="fa-plus"  @click="addModal();">新增</allowBtn>
        </div>
        <div class="page-content">
            <data-grid :option="gridOption" ref="grid">
            </data-grid>
        </div>
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
            sysDataType: {
                typeCode: '',
                typeName: ''
            },
            modal_update: false, //修改弹窗
            modal_view: false, //查看弹窗
            modal_add: false, //新增弹窗
            //以上是界面v-model的所有参数
            "gridOption": {
                "url": "/service_user/sysDataType/list",
                "columns": [ //列配置
                    {
                        "title": "类型编码",
                        "key": "typeCode",
                        "align": "center"
                    },
                    {
                        "title": "类型名称",
                        "key": "typeName",
                        "align": "center"
                    },
                    {
                        key: 'id',
                        title: '操作',
                        align: "center",
                        width: "220px",
                        render(h, params) {
                            return h('div', [
                                h('allowBtn', {
                                    props: {
                                        type: 'ghost',
                                        size: 'small',
                                        allow: 'view',
                                        faicon:"fa-file-text-o",
                                    },
                                    style: {
                                        marginRight: '5px'
                                    },
                                    on: {
                                        click: () => {
                                            that.view(params.row)
                                        }
                                    }
                                }, '查看'),
                                h('allowBtn', {
                                    props: {
                                        type: 'ghost',
                                        size: 'small',
                                        allow: 'update',
                                        disabled: false,
                                        faicon:"fa-edit",
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
                                        type: 'ghost',
                                        size: 'small',
                                        allow: 'delete',
                                        disabled: false,
                                        faicon:"fa-close",
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
                            ])
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
        addModal: function() {
            this.$refs.addform.resetFields();
            this.modal_add=true;
        },
        //前三个为“行数据”中的方法
            view: function(obj) {
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
                        var url = "/service_user/sysDataType/delete";
                        this.$ajax.post(url, { 'id': idvalue })
                            .then((res) => {
                                if(res.data.code == "200"){
                                    that.$Message.success('删除成功。');
                                    that.$refs.grid.reLoad({});
                                }else{
                                    that.$Message.error('删除出错。');
                                }
                            })
                            .catch((error) => {
                                if(!error.url){console.info(error);}
                            })
                    }
                })
            },
            //以下二个是界面的方法
            search: function() {
                this.$refs.grid.load({ 'typeName':this.sysDataType.typeName, 'typeCode': this.sysDataType.typeCode });
            },
            reset: function() {
                this.sysDataType.typeName = "";
                this.sysDataType.typeCode = "";
                this.$refs.grid.load({ });
            },
            //以下二个是子界面按钮的方法
            save: function(cb) {
                this.loading = true;
                this.$refs['addform'].save((flag) => {
                    if (flag == true) {
                        this.modal_add = false;
                        this.loading=false;
                        this.$refs.grid.reLoad({});
                    }
                });
            },
            update: function(cb) {
                this.loading = true;
                this.$refs['updateform'].save((flag) => {
                    if (flag == true) {
                        this.modal_update = false;
                        this.loading=false;
                        this.$refs.grid.reLoad({});
                        this.viewid="";
                        this.id="";
                    }
                });
            },
            //加载旋转方法
            changeVal:function(){
                this.loading=false;
            },
    },
}

</script>
