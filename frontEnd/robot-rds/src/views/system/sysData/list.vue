<template>
    <div>
        <page-title>数据字典</page-title>
        <div class="page-search">
            <div class="page-search-title">查询条件</div>
            <div class="page-search-content">
                <Form :model="sysData" :label-width="80" @submit.native.prevent="search">
                    <FormItem label="数据类型">
                        <Select v-model.trim="sysData.dataTypeId" placeholder="请选择数据类型">
                            <Option v-for="obj in dataTypes" :value="obj.id" :key="obj.id">{{obj.typeName}}</Option>
                        </Select>
                    </FormItem>
                    <Form-item label="显示内容">
                        <Input v-model.trim="sysData.textName" placeholder="请输入显示内容" maxlength="30">
                        </Input>
                    </Form-item>
                </Form>
                <div class="search-btn">
                    <allowBtn allow="allowsearch" type="primary" icon="ios-search"  @click="search()">查询</allowBtn>
                    <allowBtn allow="allowreset" type="error" icon="ios-refresh-empty"  @click="reset()">重置</allowBtn>
                    
                </div>
            </div>
        </div>
        <div class="page-tools">
            <allowBtn allow="create" type="success" faicon="fa-plus"  @click="addModal();">新增</allowBtn>
        </div>
        <div class="page-content">
            <data-grid :option="gridOption" ref="grid"></data-grid>
        </div>
        <!-- 新增属性-->
        <Modal v-model="modal_add" title="新增数据属性">
            <update-modal :dataTypes="dataTypes" ref="addform" @changeVal="changeVal"></update-modal>
            <div slot="footer">
                <Button type="primary" class="ivu-btn ivu-btn-primary" @click="save" :loading="loading">保存</Button>
                <Button type="ghost" style="margin-left: 8px" @click="modal_add=false">关闭</Button>
            </div>
        </Modal>
        <!-- 更新属性-->
        <Modal v-model="modal_update" title="更新数据属性">
            <update-modal :id="id" :dataTypes="dataTypes" ref="updateform" @changeVal="changeVal"></update-modal>
            <div slot="footer">
                <Button type="primary" class="ivu-btn ivu-btn-primary" @click="update" :loading="loading">保存</Button>
                <Button type="ghost" style="margin-left: 8px" @click="modal_update=false">关闭</Button>
            </div>
        </Modal>
        <!-- 查看属性-->
        <Modal v-model="modal_view" title="查看数据属性">
            <update-modal :id="viewid" :dataTypes="dataTypes" type="view"></update-modal>
            <div slot="footer">
                <Button type="ghost" @click="modal_view=false">关闭</Button>
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
            changeDate:'',
            loading: false,
            id: '', //页面修改赋值
            viewid: '', //页面查看赋值
            modal_add: false, //控制模块新增弹窗
            modal_update: false, //控制模块修改弹窗
            modal_view: false, //控制模块查看弹窗
            add_type_modal: false, //新增属性弹框
            sysData: {
                dataTypeId: '',
                textName: ''
            },
            dataTypes: [],
            /*sysDataType: {
                id: '',
                typeName: '',
            },*/
            "gridOption": {
                "url": "/service_user/sysData/list",
                "columns": [ //列配置
                    {
                        "title": "编码显示内容",
                        "key": "textName",
                        "align": "center"

                    },
                    {
                        "title": "数据类型编码",
                        "key": "dataTypeCode",
                        "align": "center",
                    },
                    {
                        "title": "数据类型名称",
                        "key": "dataTypeName",
                        "align": "center",
                    },
                    {
                        "title": "显示顺序",
                        "key": "sequence",
                        "align": "center",
                    },
                    {
                        key: 'id',
                        title: '操作',
                        align: "center",
                        width: 220,
                        render(h, params) {
                            return h('div', [
                                h('allowBtn', {
                                    props: {
                                        type: 'ghost',
                                        size: 'small',
                                        allow: "view",
                                        faicon:"fa-file-text-o"
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
                                }, "修改"),
                                h('allowBtn', {
                                    props: {
                                        type: 'ghost',
                                        size: 'small',
                                        allow: "delete",
                                        disabled: false,
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
                                }, '删除')
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
            del:function(obj){
                this.$Modal.confirm({
                    title: '删除',
                    content: '您确认删除此条数据？',
                    onOk:()=>{
                        // 获取服务器数据
                        this.$ajax.post("/service_user/sysData/delete",{'id':obj.id })
                            .then((res) => {
                                if(res.data.code == "200"){
                                    this.$Message.success('删除成功。');
                                    this.$refs.grid.reLoad({});
                                }else{
                                    this.$Message.error('删除出错。');
                                }
                            })
                    }
                })
            },
            //以下二个是界面的方法
            search: function() {
                if(this.changeDate=="1") {
                    this.sysData.dataTypeId= "";
                }
                this.$refs.grid.load({ 'textName':this.sysData.textName, 'dataTypeId': this.sysData.dataTypeId });
                this.changeDate="";
            },
            reset: function() {
                this.sysData.dataTypeId = "";
                this.sysData.textName = "";
                this.changeDate="1";
                this.$refs.grid.load({ });
            },
            //以下二个是子界面按钮的方法
            save:function(cb){
                this.loading = true;
                this.$refs['addform'].save((flag)=>{
                    if(flag == true){
                        this.modal_add = false;
                        this.$refs.grid.reLoad({});
                    }else {
                        this.$Message.error('保存出错');
                    }
                    this.loading = false;
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
            //重新加载类型列表数据
            getData: function() {
                //获取此模块模块基本信息 根据id查询
                this.$ajax.post("/service_user/sysDataType/findAll",{ })
                    .then(res => {
                        this.dataTypes = res.data.data;
                    })
                    .catch((error) => {
                        if(!error.url){console.info(error);}
                    });
            },
            //加载旋转方法
            changeVal:function(){
                this.loading=false;
            },
    },
    created: function() {
        this.getData();
    }
}

</script>
