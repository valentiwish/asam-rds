<template>
    <div>
        <page-title></page-title>
        <div class="page-search">
            <div class="page-search-title">查询条件</div>
            <div class="page-search-content">
                <Form ref="s" v-model="s" :label-width="100">
                    <FormItem label="变量名">
                        <Input v-model.trim="s.tagName" maxlength="50" placeholder="请输入变量名"></Input>
                    </FormItem>

                    <FormItem label="构造名">
                        <Input v-model.trim="s.buildName" maxlength="50" placeholder="请输入构造名"></Input>
                    </FormItem>

                    <FormItem label="BIM模型编码">
                        <Input v-model.trim="s.bimCode" maxlength="50" placeholder="请输入BIM模型编码"></Input>
                    </FormItem>

                    <FormItem label="类型">
                        <Select v-model="s.type" clearable filterable placeholder="请选择类型">
                            <Option v-for="(obj, index) in types" :value="obj.typeCode" :key="index">{{obj.typeCode}} - {{obj.typeName}}</Option>
                        </Select>
                    </FormItem>

                    <FormItem label="泵房">
                        <Select v-model="s.firstCode" clearable filterable placeholder="请选择泵站">
                            <Option v-for="(obj, index) in sites" :value="obj.siteCode" :key="index">{{obj.siteCode}} - {{obj.showName}}</Option>
                        </Select>
                    </FormItem>

                    <!-- 泵组级联下拉 -->
                    <Form-item label="泵组">
                        <Cascader v-model="s.secondCode" clearable :data="nodes" placeholder="请选择泵组" filterable></Cascader>
                    </Form-item>
                </Form>
                <div class="search-btn">
                    <allowBtn allow="allowsearch" icon="ios-search" @click="search">查询</allowBtn>
                    <allowBtn allow="allowreset" icon="ios-refresh-empty" @click="reset">重置</allowBtn>
                    <allowBtn allow="create" type="success" icon="plus" @click="addModal">新增</allowBtn>
                </div>
            </div>
        </div>
        <div class="page-content">
            <div style="width:100%;height:50px;">
                <div style="float:right">
                    <allowBtn allow="规则下载" icon="plus" @click="download('mmgz')">命名规则下载</allowBtn>
                    <allowBtn allow="模板下载" icon="plus" @click="download('mb')">模板下载</allowBtn>
                    <webuploader style="display: inline-block;" :option="option" ref="webuploader" @uploadSuccess="uploadSuccess" @uploadStart="uploadStart"></webuploader>
                    <allowBtn allow="导出" type="dashed" icon="plus" @click="exportExcel">数据导出</allowBtn>
                </div>
            </div>
            <data-grid :option="gridOption" ref="grid"></data-grid>
        </div>

        <!-- 新增-->
        <Modal v-model="modal_add" title="新增点变量信息">
            <update-modal ref="addform"></update-modal>
            <div slot="footer">
                <Button type="primary" class="ivu-btn ivu-btn-primary" @click="save" :loading="loading">保存</Button>
                <Button type="ghost" style="margin-left: 8px" @click="modal_add=false">关闭</Button>
            </div>
        </Modal>
        <!-- 更新-->
        <Modal v-model="modal_update" title="更新点变量信息">
            <update-modal ref="updateform" :id="id"></update-modal>
            <div slot="footer">
                <Button type="primary" class="ivu-btn ivu-btn-primary" @click="update" :loading="loading">保存</Button>
                <Button type="ghost" style="margin-left: 8px" @click="modal_update=false">关闭</Button>
            </div>
        </Modal>
        <!-- 查看-->
        <Modal v-model="modal_view" title="查看点变量信息" >
            <update-modal :id="viewid" type="view"></update-modal>
            <div slot="footer">
                <Button type="ghost" @click="modal_view=false">关闭</Button>
            </div>
        </Modal>
    </div>
</template>

<script>
    import dataGrid from "@/components/datagrid"
    import webuploader from "@/components/webuploader"
    import updateModal from "./cur"
    export default {
        components: { dataGrid, webuploader, updateModal },
        data: function() {
            var that = this;
            return {
                loading: false,
                id: '',  //页面修改赋值
                viewid:'',  //页面查看赋值
                modal_add:false,  //控制模块新增弹窗
                modal_update: false,  //控制模块修改弹窗
                modal_view: false, //控制模块查看弹窗
                types: [], //变量类型
                sites: [], //泵站下拉
                stations: [], //泵组下拉
                nodes: [], //泵组级联下拉
                s: {
                    tagName: '',
                    showName: '',
                    firstCode: '',
                    secondCode: '',
                    type: '',
                    bimCode: ''
                },
                spinShow: true, //未进入页面时，加载过程中显示加载中
                option: {
                    server: "/service_basedata/variable/uploadData",
                    btnText: '变量上传',
                    auto: true,
                    accept: {
                        title: 'Excel',
                        extensions: 'xls',
                        mimeTypes: 'application/vnd.ms-excel'
                    },
                    type:"select",
                    uploadAccept: this.uploadAccept,
                    fileNumLimit: 1,
                    formData: {
                        "cover": false
                    },
                    showUploadList: false,
                    showFileList: true
                },
                "gridOption": {
                    "url":"/service_basedata/variable/list",
                    "data": [],
                    "columns": [
                        {
                            "title": "变量名",
                            "key": "tagName",
                            "align": "center",
                        },
                        {
                            "title": "展示名",
                            "key": "showName",
                            "align": "center",
                        },
                        {
                            "title": "类型",
                            "key": "type",
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
                                            type: 'primary',
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
                            }, '修改'),
                                h('allowBtn', {
                                    props: {
                                        type: 'primary',
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
                        this.$ajax.post("/service_basedata/variable/delete", { 'id': obj.id })
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
            //以下二个是界面的方法
            search: function() {
                let type = this.s.type ? this.s.type : "";
                let firstCode = this.s.firstCode ? this.s.firstCode : "";
                let stationId = this.s.secondCode && this.s.secondCode.length > 0 ? this.s.secondCode[this.s.secondCode.length - 1] : "";
                let secondCode = "";
                this.stations.some((o) => {
                    if (o.id == stationId){
                        secondCode = o.stationCode;
                        return true;
                    }else {
                        return false;
                    }
                })
                this.$refs.grid.load({ 'tagName': this.s.tagName, 'showName': this.s.showName, 'type': this.s.type, 
                                    "firstCode": firstCode, "secondCode": secondCode, "bimCode": this.s.bimCode});
            },
            reset: function() {
                this.s.tagName = "";
                this.s.showName = "";
                this.s.type = "";
                this.s.firstCode = "";
                this.s.secondCode = "";
                this.s.bimCode = "";
                this.$refs.grid.load({});
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
            update: function(cb){
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
            download: function(flag){
                var fileName = "";
                if ("mmgz" == flag){
                    fileName = "工业数据变量命名规则.pdf";
                    window.open("/service_basedata/file/download?folder=file&fileName=" + fileName, '_blank');
                }
                else if ("mb" == flag){
                    fileName = "点变量模板.xls";
                    window.open("/service_basedata/file/download?folder=excel&fileName=" + fileName, '_blank');
                }
            },
            report: function(){
                
            },
            upload: function() {
                this.$refs.webuploader.upload();
            },
            uploadStart: function() {
                this.spinShow = true;
            },
            uploadSuccess: function() {
                this.spinShow = false;
            },
            uploadAccept: function(object, ret) {
                this.$refs.webuploader.uploader.reset();
                if ("1111" == object.response.data.code) {
                    this.$Modal.success({
                        title: '导入成功',
                        content: object.response.data.reason
                    });
                    this.$refs.grid.load({});
                    return true;
                }else {
                    this.$Modal.warning({
                        title: '导入失败',
                        content: object.response.data.reason
                    });
                    this.$refs.webuploader.deleteFile(object.response.file.id);
                }
            },
            exportExcel: function(){
                window.open("/service_basedata/variable/exportExcel?fileName=点变量数据", '_blank');
            },
            // /获取下拉数据
            getBase: function(){
                let ajax = this.$ajax.post("/service_user/variable/getBase");
                ajax.then((res) => {
                    if (200 == res.data.code){
                        this.types = res.data.types;
                        this.sites = res.data.sites;
                        this.stations = res.data.stations;
                        let nodes = res.data.nodes;
                        this.nodes = this.$utils.Flat2TreeDataForCascader(nodes);
                    }
                }).catch((err) => {
                    if (!err.url){
                        console.info(err)
                    }
                })
            }
        },
        // 页面加载即调用
        created: function() {
            
        },
        mounted: function(){
            this.getBase();
        },
        watch: {
            
        }
    };
</script>