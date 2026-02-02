<template>
    <div>
        <page-title></page-title>
        <div class="page-search">
            <div class="page-search-title">查询条件</div>
            <div class="page-search-content">
                <Form ref="s" v-model="s" :label-width="80">
                    <FormItem label="水厂名称">
                        <Select v-model="s.factoryId" placeholder="请选择水厂">
                            <Option v-for="(obj, index) in factorys" :key="index" :value="obj.id">{{obj.name}}</Option>
                        </Select>
                    </FormItem>

                    <FormItem label="累计日期">
                        <DatePicker type="daterange" v-model="s.reportDates" placement="bottom-end" placeholder="请选择水量产生日期"></DatePicker>
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
        <Modal v-model="modal_add" title="新增水量信息">
            <update-modal ref="addform"></update-modal>
            <div slot="footer">
                <Button type="primary" class="ivu-btn ivu-btn-primary" @click="save" :loading="loading">保存</Button>
                <Button type="ghost" style="margin-left: 8px" @click="modal_add=false">关闭</Button>
            </div>
        </Modal>
        <!-- 更新-->
        <Modal v-model="modal_update" title="更新水量信息">
            <update-modal :id="id" ref="updateform"></update-modal>
            <div slot="footer">
                <Button type="primary" class="ivu-btn ivu-btn-primary" @click="update" :loading="loading">保存</Button>
                <Button type="ghost" style="margin-left: 8px" @click="modal_update=false">关闭</Button>
            </div>
        </Modal>
        <!-- 查看-->
        <Modal v-model="modal_view" title="查看水量信息" >
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
    import row from "./row"
    export default {
        components: { dataGrid, updateModal, row },
        data: function() {
            var that = this;
            return {
                loading:false,
                id: '',  //页面修改赋值
                viewid:'',  //页面查看赋值
                modal_add:false,  //控制模块新增弹窗
                modal_update: false,  //控制模块修改弹窗
                modal_view: false, //控制模块查看弹窗
                spinShow: true, //未进入页面时，加载过程中显示加载中
                s: {
                    factoryId: '',
                    reportDates: ''
                },
                factorys: [],
                options: {
                    disabledDate (date) {
                        return date && date.valueOf() > Date.now();
                    }
                },
                "gridOption": {
                    "url":"/service_basedata/report/list",
                    "data": [],
                    "columns": [
                        {
                            title: "支线水量",
                            type: 'expand',
                            width: 100,
                            render: (h, params) => {
                                var branchs = params.row.branchWaterList;
                                return h(row, {
                                    props: {
                                        row: {branchs}
                                    }
                                })
                            }
                        },
                        {
                            "title": "水厂名称",
                            "key": "factoryName",
                            "align": "center",
                        },
                        {
                            "title": "累计日期",
                            "key": "reportDate",
                            "align": "center",
                            "render": function (h, p){
                                if (p.row){
                                    var date = new Date(p.row.reportDate).Format("yyyy-MM-dd")
                                    return h('span', date);
                                }
                            }
                        },
                        {
                            "title": "进水总量（m³）",
                            "key": "inSum",
                            "align":"center",
                        },
                        {
                            "title": "出水总量（m³）",
                            "key": "outSum",
                            "align":"center",
                        },
                        {
                            "title": "录入时间",
                            "key": "createTime",
                            "align":"center",
                            "render": function (h, p){
                                if (p.row){
                                    var date = new Date(p.row.createTime).Format("yyyy-MM-dd hh:mm")
                                    return h('span', date);
                                }
                            }
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
                    content: '确认删除后水厂支线水量信息会一并被删除？',
                    onOk: () => {
                        this.$ajax.post("/service_basedata/report/delete", { 'id': obj.id })
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
                var reportDate1 = "";
                var reportDate2 = "";
                if (this.s.reportDates && 2 == this.s.reportDates.length){
                    reportDate1 = this.s.reportDates[0] ? new Date(this.s.reportDates[0]).Format("yyyy-MM-dd") : "";
                    reportDate2 = this.s.reportDates[1] ? new Date(this.s.reportDates[1]).Format("yyyy-MM-dd") : "";
                }
                this.$refs.grid.load({ 'factoryId': this.s.factoryId, 'reportDate1': reportDate1, "reportDate2": reportDate2 });
            },
            reset: function() {
                this.s.factoryId = "";
                this.s.reportDates = [];
                this.$refs.grid.load({ });
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
            getBaseData: function(){
                this.$ajax.post("/service_basedata/report/getBaseData")
                    .then((res) => {
                        if (200 == res.data.code){
                            this.factorys = res.data.waterFactoryVOList;
                        }else {
                            this.$Message.error('基础数据获取失败');
                        }
                    })
                    .catch((err) => {if (!err.url){console.info(err);}})
            },
        },
        created: function() {
            
        },
        mounted: function(){
            this.getBaseData();
        },
    };

</script>
