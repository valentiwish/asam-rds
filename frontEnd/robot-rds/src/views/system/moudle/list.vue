<template>
    <div>
        <page-title>模块管理</page-title>
        <div style="position:relative;">
            <Spin fix v-if="spinShow"></Spin>

            <div class="sys-nav" v-if="systemList.length>0">
                <!-- <Tabs v-model="curSystem" @on-click="loadPageTree">
                    <TabPane :label="item.name" :name="item.id" v-for="item in systemList" :key="item.id">
                        
                    </TabPane>
                </Tabs> -->
                <span style="color:#2d8cf0;">子系统</span>
                <Select v-model="curSystem" filterable @on-change="loadPageTree" style="width:250px;margin-left:10px;">
                        <Option :value="item.id" :key="item.id" v-for="item in systemList">{{item.name}}</Option>
                </Select>
            </div>
            <Row>
                <Col span="4" style="padding:0 15px;">
                    <div style="opacity:0;height:3px;"></div> 
                    <!-- 设置属性 show-checkbox 可以对节点进行勾选。 -->
                    <Tree :data="baseData" @on-select-change="loadTree"></Tree>
                    
                </Col>
                <Col span="20">
                <div class="page-search">
                    <div class="page-search-title">查询条件</div>
                    <div class="page-search-content">
                        <Form :label-width="100" @submit.native.prevent="submit">
                            <FormItem label="模块名称">
                                <Input v-model.trim="moudlename" placeholder="请输入模块名称" maxlength="30"></Input>
                            </FormItem>
                            <FormItem label="父模块">
                                <Cascader :data="baseMoudleData" change-on-select v-model.trim="moudleUrl" placeholder="请选择父模块"></Cascader>
                            </FormItem>
                        </Form>
                        <div class="search-btn">
                            <allowBtn allow="allowsearch" type="primary" icon="ios-search" @click="search()">查询</allowBtn>
                            <allowBtn allow="allowreset" type="error" icon="ios-refresh" @click="clear()">重置</allowBtn>
                        </div>
                    </div>
                </div>
                <div class="page-tools">
                    <allowBtn allow="create" type="success" faicon="fa fa-plus" @click="toAdd()">新增模块
                    </allowBtn>
                </div>
                <div class="page-content" v-allow="'create'">
                    <data-grid :option="gridOption" ref="grid"></data-grid>
                </div>
                </Col>
            </Row>
        </div>
        <!-- 新增模块-->
        <Modal v-model="modal_add" title="新增模块">
            <update-modal :baseMoudleData="baseMoudleData" :baseCascader="baseCascader" :checkedID="checkedID" ref="addform" @changeVal="changeVal"></update-modal>
            <div slot="footer">
                <Button type="primary" class="ivu-btn ivu-btn-primary" @click="save" :loading="loading">保存</Button>
                <Button type="ghost" @click="modal_add=false">关闭</Button>
            </div>
        </Modal>

        <!-- 查看模块-->
        <Modal v-model="modal_view" title="查看模块">
            <update-modal :id="viewid" :baseMoudleData="baseMoudleData" :baseCascader="baseCascader" type="view" ref="viewform"></update-modal>
            <div slot="footer">
                <Button type="ghost" @click="modal_view=false;viewid=''">关闭</Button>
            </div>
        </Modal>

        <!-- 更新模块-->
        <Modal v-model="modal_update" title="更新模块">
            <update-modal :id="id" :baseMoudleData="baseMoudleData" :baseCascader="baseCascader" ref="updateform" @changeVal="changeVal"></update-modal>
            <div slot="footer">
                <Button type="primary" class="ivu-btn ivu-btn-primary" @click="update" :loading="loading">保存</Button>
                <Button type="ghost" @click="modal_update=false;id=''">关闭</Button>
            </div>
        </Modal>
        
    </div>
</template>
<script>
import dataGrid from "@/components/datagrid"
import CONFIG from '@/config/index.js'
import UTILS from '@/libs/util.js'
import updateModal from "./cur"

export default {
    components: { dataGrid, updateModal},
    data: function() {
        var that = this;
        return {
            systemList:[],
            curSystem:null,
            
            loading: false,
            checkedID: '',
            moudleDataList: [],
            baseMoudleData: [],
            baseCascader: [],
            id: '', //页面修改赋值
            viewid: '', //页面查看赋值
            baseData: [], //树形结构数据
            moudlename: '', //搜索区域模块名称   
            moudleUrl: '',
            modal_update: false, //控制模块修改弹窗
            modal_view: false, //控制模块查看弹窗
            modal_add: false, //控制模块新增弹窗
            spinShow: true, //未进入页面时，加载过程中显示加载中
            "gridOption": {
                // "url":"/moudle/list",
                "data": [],
                //表格高度偏移
                "tableHeightOffset":245,
                "columns": [ // 列配置
                    {
                        "title": "模块名称",
                        "key": "name",
                        "align": "center",
                        "width": 150,
                    },
                    {
                        "title": "父模块",
                        "key": "parent",
                        "align": "center",
                        "width": 150,
                        "render": function(a, param) {
                            if (typeof(param.row.parentName) == "undefined") {
                                return "";
                            } else {
                                return param.row.parentName
                            }
                        }
                    },
                    {
                        "title": "地址",
                        "key": "actionUrl",
                        "align": "left",
                    },
                    /* {
                        "title": "操作标识",
                        "key": "allow",
                        "align": "center",
                        "width": 100,
                    }, */
                    {
                        "title": "排序",
                        "key": "sort",
                        "align": "center",
                        "width": 100,
                    },
                    {
                        "title": "状态",
                        "key": "enable",
                        "align": "center",
                        "width": 100,
                        "render": function(h, param) {
                            if (param.row.enable == 0) {
                                return h("Tag",{
                                    props:{
                                        color:"warning"
                                    }
                                },"未启用");
                            }
                            if (param.row.enable == 1) {
                                return h("Tag",{
                                    props:{
                                        color:"success"
                                    }
                                },"启用");
                            }
                        }
                    },
                    {
                        key: 'tool',
                        title: '操作',
                        align: "center",
                        "width": 220,
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
        toView: function(obj) {
            this.$refs.viewform.clearFields();
            this.viewid = null;
            this.$nextTick(()=>{
                this.viewid = obj.id;
                this.modal_view = true;
            })
        },
        //打开修改模块弹窗
        toUpdate: function(obj) {
            this.$refs.updateform.clearFields();
            this.id = null;
            this.$nextTick(()=>{
                this.id = obj.id;
                this.modal_update = true;
            })
        },
        toAdd(){
            this.modal_add=true;
            this.$refs.addform.clearFields();

            this.$nextTick(()=>{
                this.$refs.addform.moudleData.systemId = this.curSystem;
            })
        },
        save: function(cb) {
            this.loading = true;
            this.$refs['addform'].save((flag) => {
                this.loading = false;
                if (flag == true) {
                    this.$refs.grid.reLoad({});
                    this.loadPageTree();
                    this.$refs.addform.clearFields();
                    this.$Message.success('保存成功');
                    this.modal_add = false;
                }
            });
        },
        update: function(cb) {
            this.loading = true;
            this.$refs['updateform'].update((flag) => {
                this.loading = false;
                if (flag == true) {
                    this.$refs.grid.reLoad({});
                    this.loadPageTree();

                    this.viewid = null;
                    this.$refs.updateform.clearFields();
                    this.modal_update = false;
                }
            });
        },
        del: function(obj) {
            this.$Modal.confirm({
                title: '删除',
                content: '您确认删除此条数据？',
                onOk: () => {
                    // 获取服务器数据
                    this.$ajax.post("/service_user/moudle/delete", { 'id': obj.id })
                        .then((res) => {
                            if (res.data.code == "200") {
                                this.$Message.success('删除成功');
                                this.$refs.grid.reLoad({});
                                this.loadPageTree();
                            } else {
                                this.$Message.error('删除出错，信息为' + data.msg);
                            }
                        })
                }
            })
        },
        cal: function() {
            this.modal_view = false;
            this.clear();
        },
        search: function() {
            var parentid = null;
            if (this.moudleUrl.length > 0) {
                parentid = this.moudleUrl[this.moudleUrl.length - 1];
            }

            this.gridOption.data = this.moudleDataList.filter((j, i) => {
                if (j.name != "" && parentid) {
                    return j.name.match(this.moudlename) && j.parentId == parentid
                } else if (j.name != "") {
                    return j.name.match(this.moudlename);
                } else if (parentid) {
                    return j.pid == parentid;
                } else {
                    return true;
                }
            });

            this.$refs.grid.load({});
        },
        clear: function() {
            this.moudlename = '';
            this.moudleUrl = [];
            this.loadPageTree();
        },
        loadTree: function(obj) {
            if (obj.length > 0) {
                this.gridOption.data = [];
                this.$refs.grid.pagination.currentPage = 1;
                this.moudleDataList.some((j, i) => {
                    if (obj[0].id == j.id){
                        this.gridOption.data.push(j);
                        return true;
                    }else {
                        return false;
                    }
                })
                this.getChildrens(obj[0].id);
                this.checkedID = obj[0].id;
                this.$refs['addform'].refreshData(this.checkedID);
            } else {
                this.gridOption.data = this.moudleDataList;
            }
        },
        ///递归获取所有子模块
        getChildrens: function(pId){
            this.moudleDataList.forEach((j, i) => {
                if (pId == j.parentId){
                    this.gridOption.data.push(j);
                    this.getChildrens(j.id);
                }
            });
        },
        loadPageTree: function() {
            this.spinShow = true;
            //加载树结构
            this.$ajax.post("/service_user/moudle/getMoudleData",{
                systemId:this.curSystem
            })
            .then(res => {
                if (res.data.moudleTree) {
                    this.baseData = UTILS.formatTreeData(res.data.moudleTree, "disabledbranch");
                    this.baseMoudleData = UTILS.Flat2TreeDataForCascader(res.data.moudleTree);
                    this.spinShow = false;
                    this.baseCascader = res.data.moudleTree;
                    this.gridOption.data = this.moudleDataList = res.data.data;
                } else {
                    this.baseMoudleData = [];
                }
            },()=>{
                this.$Message.error('获取页面树结构数据失败。');
            })
        },
        getSystemList(){
            this.$ajax.post("/service_user/systemInfo/allList")
            .then(({data})=>{
                if(data.code == 200){
                    this.systemList = data.data;
                    if(data.data.length>0){
                        this.curSystem = data.data[0].id;
                        this.loadPageTree();
                    }
                }
                else{
                    this.systemList = [];
                }
            },()=>{
                this.systemList = [];
            })
        },
        //加载旋转方法
        changeVal: function() {
            this.loading = false;
        },
    },
    //做了页面缓存 页面被激活时刷新数据
    activated: function() {
        this.getSystemList();
    }
}

</script>

<style scoped>
    .sys-nav{
        padding:10px 15px; 
        border-bottom:1px solid #efefef;
    }
</style>