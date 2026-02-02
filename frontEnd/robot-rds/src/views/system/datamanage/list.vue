<template>
    <div>
        <page-title></page-title>
        <div class="page-search">
            <div class="page-search-title">查询条件</div>
            <div class="page-search-content">
                <Form :label-width="100" @submit.native.prevent="search">
                    <FormItem label="操作类别">
                        <!-- <Input v-model.trim="searchObj.type" placeholder="请输入操作类别，最多输入20字" maxlength="20"></Input> -->
                        <Select v-model="searchObj.type" placeholder="请选择操作类别">
                            <Option :key="copy" :value="copy">复制数据</Option>
                            <Option :key="restore" :value="restore">恢复数据</Option>
                        </Select>
                    </FormItem>
                    <FormItem label="操作时间">
                        <Input v-model.trim="searchObj.manageTime" placeholder="请输入操作时间，最多输入20字" maxlength="20"></Input>
                    </FormItem>
                </Form>
                <div class="search-btn">
                    <allowBtn allow="allowsearch" icon="ios-search" @click="search">查询</allowBtn>
                    <allowBtn allow="allowreset" icon="ios-refresh-empty" @click="clear">重置</allowBtn>
                    
                </div>
            </div>
        </div>
        <div class="page-tools">
            <allowBtn allow="数据备份" type="success" faicon="fa-database" @click="toCopyData">数据备份</allowBtn>
            <allowBtn allow="数据恢复" type="primary" faicon="fa-history" @click="toRestoreData">数据恢复</allowBtn>
        </div>
        <div class="page-content">
            <data-grid :option="gridOption" ref="grid"></data-grid>
        </div>

        <!-- 备份数据-->
        <Modal v-model="mv_copy" title="备份数据">
            <div>
                <update-modal type="copy" ref="addform" v-if="mv_copy" @saveSuccess="saveSuccess"></update-modal>
            </div>
            <div slot="footer">
                <Button type="primary" class="ivu-btn ivu-btn-primary" @click="save">立即备份</Button>
                <Button type="ghost" class="ivu-btn ivu-btn-primary" @click="mv_copy=false">关闭</Button>
            </div>
        </Modal>
        <!-- 恢复数据-->
        <Modal v-model="mv_restore" title="恢复数据">
            <div>
                <update-modal :id="id" type="restore" ref="updateform" v-if="mv_restore" @updateSuccess="updateSuccess"></update-modal>
            </div>
            <div slot="footer">
                <Button type="primary" class="ivu-btn ivu-btn-primary" @click="update">恢复数据</Button>
                <Button type="ghost" class="ivu-btn ivu-btn-primary" @click="mv_restore=false">关闭</Button>
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
                    "type":'copy',
                    "manageTime":''
                },
                viewid:'',
                mv_copy: false, //控制模块新增弹窗
                mv_restore: false, //控制模块修改弹窗    
                "gridOption": {
                    "checkbox": {
                        "enable": false
                    },
                    "header": true,
                    "iconCls": "fa-power-off",
                    "data": [],
                    "url": "/service_user/dataManage/list",
                    "columns": [ //列配置
                        {
                            "title": "IP地址",
                            "align": "center",
                            "key": "ip",
                        },{
                            "title": "端口",
                            "align": "center",
                            "key": "port",
                        },{
                            "title": "操作时间",
                            "align": "center",
                            "key": "manageTime",
                            "render": function (a, param) {
                                if (param.row.manageTime) {
                                    return new Date(param.row.manageTime).Format("yyyy-MM-dd hh:mm")
                                }else{
                                    return "";
                                }
                            },
                        },{
                            "title": "操作类别",
                            "align": "center",
                            "key": "type",
                            "render": function (a, param) {
                                param.row.type == "copy" ? '复制' : '恢复'
                            },
                        },{
                            "title": "操作人",
                            "align": "center",
                            "key": "managePerson",
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
                                            type: 'ghost',
                                            size: 'small',
                                            allow: "数据恢复",
                                            faicon: "fa-history"
                                        },
                                        style: {
                                            marginRight: '5px'
                                        },
                                        on: {
                                            click: () => {
                                                that.toDownload(params.row);
                                            }
                                        }
                                    }, '下载备份文件')
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
                this.$refs.grid.load({ "type": this.searchObj.type, "manageTime": this.searchObj.manageTime});
            },
            clear: function() {
                this.searchObj.type = "";
                this.searchObj.manageTime = "";
            },
            //保存数据
            save: function(cb) {
                this.$refs['addform'].save();
            },
            toCopyData: function() {
                this.$Modal.confirm({
                    title: '确认备份',
                    content: '<p>为保证平台功能正常，请您在系统平台空闲时间段进行数据备份。</p><p>建议您在21:00--09:00进行数据备份。</p>',
                    onOk: () => {
                        this.mv_copy = true;
                    },
                    onCancel: () => {
                        this.mv_copy = false;
                    }
                });                
            },
            update: function(cb) {
                this.$refs['addform'].update();
            },
            toRestoreData: function() {
                this.mv_restore = true;
            },
            toDownload: function(obj) {
                this.id = obj.id;
            },
            saveSuccess: function() {
                this.mv_copy = false;
                this.$refs.grid.reLoad({});
            },
            updateSuccess: function() {
                this.mv_restore = false;
                this.$refs.grid.reLoad({});
            },
        },
        created: function() {
            
        },
        mounted: function(){

        },
        watch: {
            
        }
    };
</script>