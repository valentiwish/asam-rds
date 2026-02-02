<template>
    <div>
        <page-title></page-title>
        <div class="page-search">
            <div class="page-search-title">查询条件</div>
            <div class="page-search-content">
                <Form :model="fileTemplate" :label-width="80"  @submit.native.prevent="search" style="padding-right:290px">
                    <FormItem label="模板编号" prop="no">
                        <Input placeholder="请输入模板编号，不超过20字" maxlength="20" v-model.trim="fileTemplate.no"></Input>
                    </FormItem>
                    <FormItem label="模板名称" prop="name">
                        <Input placeholder="请输入模板名称，不超过20字" maxlength="20" v-model.trim="fileTemplate.name"></Input>
                    </FormItem>
                    <FormItem label="模板分类" prop="templateTypeId">
                        <Select v-model="fileTemplate.templateTypeId" placeholder="请选择模板分类">
                            <Option v-for="templateType in templateTypeList" :key="templateType.id" :value="templateType.id">{{templateType.textName}}</Option>
                        </Select>
                    </FormItem>
                    <FormItem label="创建时间">
                        <DatePicker type="date" placeholder="请输入创建时间" v-model="fileTemplate.createTime" format="yyyy-MM-dd"></DatePicker>
                    </FormItem>
                </Form>
                <div class="search-btn">
                    <allowBtn allow="allowsearch" icon="ios-search" @click="search">查询</allowBtn>
                    <allowBtn allow="allowreset" icon="ios-refresh-empty" @click="reset">重置</allowBtn>  
                </div>
            </div>
        </div>
        <div class="page-tools">
            <allowBtn allow="create" type="success" faicon="fa-plus" @click="mv_add=true">新增模板</allowBtn>
        </div>
        <div class="page-content">
            <data-grid :option="gridOption" ref="grid"></data-grid>
        </div>

        <!-- 新增模板-->
        <Modal v-model="mv_add" title="新增模板信息">
            <div style="min-height:210px;">
                <update-modal ref="addform" :templateTypeList="templateTypeList" v-if="mv_add"></update-modal>
            </div>
            <div slot="footer">
                <Button type="primary" class="ivu-btn ivu-btn-primary" @click="save">保存</Button>
                <Button type="ghost" class="ivu-btn ivu-btn-primary" @click="mv_add=false">关闭</Button>
            </div>
        </Modal>
        <!-- 更新模板-->
        <Modal v-model="mv_update" title="修改模板信息">
            <div style="min-height:250px;">
                <update-modal :id="id"  :templateTypeList="templateTypeList" ref="updateform" v-if="mv_update"></update-modal>
            </div>
            <div slot="footer">
                <Button type="primary" class="ivu-btn ivu-btn-primary" @click="update">保存</Button>
                <Button type="ghost" class="ivu-btn ivu-btn-primary" @click="mv_update=false">关闭</Button>
            </div>
        </Modal>
        <!-- 查看模板-->
        <Modal v-model="mv_view" title="查看模板信息">
            <div style="min-height:250px;">
                <update-modal :id="viewid"  :templateTypeList="templateTypeList" ref="updateform" type="view" v-if="mv_view"></update-modal>
            </div>  
            <div slot="footer">
                <Button type="ghost" class="ivu-btn ivu-btn-primary" @click="mv_view=false">关闭</Button>
            </div>
        </Modal>
    </div>
</template>

<script>
    import dataGrid from "@/components/datagrid";
    import updateModal from "./cur";
    export default {
        components: { dataGrid, updateModal },
        data: function() {
            var that = this;
            return {
                loading: false,
                id: '',  //页面修改赋值
                viewid:'',  //页面查看赋值    
                mv_add: false, //控制模块新增弹窗
                mv_update: false, //控制模块修改弹窗
                mv_view: false, //控制模块查看弹窗     
                fileTemplate: {
                    name: '',
                    templateTypeId: '',
                    no: '',
                    createTime: ''
                },      
                templateTypeList:[],
                "gridOption": {
                    "url":"/service_user/fileTemplate/list",
                    "data": [],
                    "columns": [
                        {
                            "title": "模板编号",
                            "align": "center",
                            "key": "no"
                        },{
                            "title": "模板名称",
                            "align": "center",
                            "key": "name"
                        },{ 
                            "title": "模板分类",                            
                            "align": "center",
                            "key": "templateTypeName",
                        },/* {
                            "title": "状态",
                            "align": "center",
                            render(h, params) {
                                if (params.row.state == 0) {
                                    return "无效";
                                } else if (params.row.state == 1) {
                                    return "有效";
                                }
                            }
                        }, */{
                            "title": "创建时间",
                            "align": "center",
                            "key": "createTime",
                            // width: 180,
                            "render": function (a, param) {
                                if (param.row.createTime) {
                                    return new Date(param.row.createTime).Format("yyyy-MM-dd hh:mm")
                                }else{
                                    return "";
                                }
                            },
                        },
                        {
                         "title": "说明",
                         "align": "center",
                         "key": "description",
                         "ellipsis": true
                        },
                        {
                            key: 'tool',
                            title: '操作',
                            "align": "center",
                            "width": "230px",
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
                                                that.toView(params.row)
                                            }
                                        }
                                    }, '查看'),
                                    h('allowBtn', {
                                        props: {
                                            type: 'ghost',
                                            size: 'small',
                                            allow: "update",
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
                                            allow: "delete",
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
                    }
                }
            }
        },
        methods: {
            //打开修改模块弹窗
            toUpdate: function(obj) {
                this.id = obj.id;
                this.mv_update = true;
            },
            toView: function(obj){                
                this.viewid = obj.id;
                this.mv_view = true;
            },
            del: function(obj) {
                this.$Modal.confirm({
                    title: '删除',
                    content: '您确认删除此条数据？',
                    onOk: () => {
                        this.$ajax.post("/service_user/fileTemplate/delete", { 'id': obj.id })
                        .then((res) => {
                            if (200 == res.data.code) {
                                this.$Message.success('删除成功');
                                this.$refs.grid.reLoad();
                            } else {
                                this.$Message.error('删除出错，信息为' + res.data.msg);
                            }
                        })
                    }
               })
            },
            search: function() {
                var startTime = "";
                if (this.fileTemplate.createTime != null && this.fileTemplate.createTime != '') {
                    startTime = this.fileTemplate.createTime.Format("yyyy-MM-dd");
                }
                this.$refs.grid.load({
                    "name": this.fileTemplate.name,
                    "templateType": this.fileTemplate.templateTypeId,
                    "no": this.fileTemplate.no,
                    "createTime": startTime
                });
            },        
            reset: function() {
                this.fileTemplate.name = "";
                this.fileTemplate.templateTypeId = "";
                this.fileTemplate.no = "";
                this.fileTemplate.createTime = "";
            },
            //以下二个是子页面按钮的方法
            save: function(){
                this.loading = true;
                this.$refs['addform'].save((flag)=>{
                    if(flag){
                        this.mv_add = false;
                        this.$refs.grid.reLoad({});
                    }
                    this.loading = false;
                });
            },
            update: function(cb){
                this.loading = true;
                this.$refs['updateform'].save((flag)=>{
                    if(flag){
                        this.mv_update = false;
                        this.$refs.grid.reLoad({});
                        this.viewid = "";
                        this.id = "";
                    }
                    this.loading = false;
                });
            },
        },
        created: function() {            
            this.$ajax.post("/service_user/sysData/findByTypeCode",{"dataTypeCode":"FILE_TEMPLATE_TYPE"})
                .then((res) => {
                    if(res.data.code == "200"){
                        this.templateTypeList = res.data.data;
                    }
                    
            })
        },
    };
</script>