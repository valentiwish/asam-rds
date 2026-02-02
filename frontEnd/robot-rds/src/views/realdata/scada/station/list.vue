<template>
    <div>
        <page-title></page-title>
        <div class="page-search">
            <div class="page-search-title">查询条件</div>
            <div class="page-search-content">
                <Form ref="s" v-model="s" :label-width="80">
                    <FormItem label="编号">
                        <Input v-model.trim="s.stationCode" maxlength="50" placeholder="请输入控制站编号"></Input>
                    </FormItem>

                    <FormItem label="展示名">
                        <Input v-model.trim="s.showName" maxlength="50" placeholder="请输入展示名"></Input>
                    </FormItem>

                    <FormItem label="所属站点">
                        <Select v-model="s.siteId" placeholder="请选择所属站点">
                            <Option v-for="(obj, index) in sites" :key="index" :value="obj.id">{{obj.showName}}</Option>
                        </Select>
                    </FormItem>
                </Form>
                <div class="search-btn">
                    <allowBtn allow="allowsearch" icon="ios-search" @click="search">查询</allowBtn>
                    <allowBtn allow="allowreset" icon="ios-refresh-empty" @click="reset">重置</allowBtn>
                    <allowBtn allow="create" type="success" icon="plus" @click="addModal">新增</allowBtn>
                </div>
            </div>
        </div>
        <div class="page-content">
            <data-grid :option="gridOption" ref="grid"></data-grid>
        </div>

        <!-- 新增-->
        <Modal v-model="modal_add" title="新增点变量信息">
            <update-modal :sites="sites" ref="addform"></update-modal>
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
    import updateModal from "./cur"
    export default {
        components: { dataGrid, updateModal },
        data: function() {
            var that = this;
            return {
                loading: false,
                id: '',  //页面修改赋值
                viewid:'',  //页面查看赋值
                modal_add:false,  //控制模块新增弹窗
                modal_update: false,  //控制模块修改弹窗
                modal_view: false, //控制模块查看弹窗
                sites: [],
                s: {
                    stationCode: '',
                    showName: '',
                    siteId: '',
                },
                spinShow: true, //未进入页面时，加载过程中显示加载中
                "gridOption": {
                    "url":"/service_basedata/station/list",
                    "data": [],
                    "columns": [
                        {
                            "title": "所属站点",
                            "key": "siteName",
                            "align": "center",
                        },
                        {
                            "title": "控制站编号",
                            "key": "stationCode",
                            "align": "center",
                        },
                        {
                            "title": "展示名",
                            "key": "showName",
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
                    content: '删除控制站时控制站下的变量会被同步删除，您确认删除此控制站吗？',
                    onOk: () => {
                        this.$ajax.post("/service_basedata/station/delete", { 'id': obj.id })
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
                this.$refs.grid.load({ 'stationCode': this.s.stationCode, 'showName': this.s.showName, 'siteId': this.s.siteId });
            },
            reset: function() {
                this.s.stationCode = "";
                this.s.showName = "";
                this.s.siteId = "";
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
            getBaseData: function(){
                this.$ajax.post("/service_basedata/site/selectList", {})
                        .then((res) => {
                            if (200 == res.data.code) {
                                this.sites = res.data.data;
                            } else {
                                this.$Message.error(res.data.msg);
                            }
                        })
                        .catch((err) => {if(!err.url){console.info(err);}});
            },
        },
        created: function() {
            
        },
        mounted: function(){
            this.getBaseData();
        },
        watch: {
            
        }
    };
</script>