<template>
    <div>
        <page-title></page-title>
        <Row>
         <Col span="4">
            <div class="page-left">
                <Tree :data="baseData" @on-select-change="loadTree"></Tree>
            </div>
            </Col>
            <Col span="20">
            <div class="page-search">
                <div class="page-search-title">查询条件</div>
                <div class="page-search-content">
                    <Form :label-width="100" :model="org"  @submit.native.prevent="submit">
                        <FormItem label="组织机构编码">
                            <Input v-model.trim="org.code" placeholder="请输入组织机构编码" maxlength="20">
                            </Input>
                        </FormItem>
                        <FormItem label="组织机构名称">
                            <Input v-model.trim="org.name" placeholder="请输入组织机构名称" maxlength="50">
                            </Input>
                        </FormItem>
                    </Form>
                    <div class="search-btn">
                        <allowBtn allow="allowsearch" icon="ios-search"  @click="search()">查询</allowBtn>
                        <allowBtn allow="allowreset" icon="ios-refresh-empty"  @click="clear()">重置</allowBtn>
                        <!-- <button type="success" icon="plus" @click="addModal();">新增</button> -->
                    </div>
                </div>
            </div>
            <div class="page-tools">
                <allowBtn allow="create"  type="success" faicon="fa-plus"  @click="addModal();">新增</allowBtn>
            </div>
            <div class="page-content">
                <Spin size="large" fix v-if="spinShow"></Spin>
                <data-grid :option="gridOption" ref="grid"></data-grid>
            </div>
           </Col>
        </Row>

        <!-- 新增属性-->
        <Modal v-model="modal_add" title="新增组织机构信息">
            <update-modal ref="addform" @changeVal="changeVal"></update-modal>
            <div slot="footer">
                <Button type="primary" class="ivu-btn ivu-btn-primary" @click="save" :loading="loading">保存</Button>
                <Button type="ghost" style="margin-left: 8px" @click="modal_add=false">关闭</Button>
            </div>
        </Modal>
        <!-- 更新属性-->
        <Modal v-model="modal_update" title="更新组织机构信息">
            <update-modal :id="id"  ref="updateform" @changeVal="changeVal"></update-modal>
            <div slot="footer">
                <Button type="primary" class="ivu-btn ivu-btn-primary" @click="update" :loading="loading">保存</Button>
                <Button type="ghost" style="margin-left: 8px" @click="modal_update=false">关闭</Button>
            </div>
        </Modal>
        <!-- 查看属性-->
        <Modal v-model="modal_view" title="查看组织机构信息" >
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
        components: { dataGrid,updateModal},
        data: function() {
            var that = this;
            return {
                loading:false,
                id: '',  //页面修改赋值
                viewid:'',  //页面查看赋值
                modal_add:false,  //控制模块新增弹窗
                modal_update: false,  //控制模块修改弹窗
                modal_view: false, //控制模块查看弹窗
                org: {//搜索区域模块名称
                    code: '',
                    name: '',
                },
                baseData: [], //树形结构数据
                spinShow: true, //未进入页面时，加载过程中显示加载中
                "gridOption": {
                    "url":"/service_user/organization/list",
                    "data": [],
                    //表格高度偏移
                    "tableHeightOffset":190,
                    "columns": [
                        {
                            "title": "组织机构编码",
                            "key": "code",
                            "align": "center",
                        },
                        {
                            "title": "组织机构名称",
                            "key": "name",
                            "align": "center",
                        },
                        /* {
                            "title": "联系人",
                            "key": "linker",
                            "align":"center",
                        }, */
                        {
                            "title": "电话",
                            "key": "telephone",
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
                                            faicon:'fa-file-text-o',
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
                                        faicon:'fa-edit',
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
                                        faicon:'fa-close',
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
                this.modal_add=true;
            },
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
                    // 获取服务器数据
                    this.$ajax.post("/service_user/organization/deleteInfoById", { 'orgId': obj.id })
                    .then((res) => {
                        if (res.data.code == "200") {
                            this.$Message.success('删除成功');
                            this.$refs.grid.reLoad({});
                        } else {
                            this.$Message.error('删除出错，信息为' + data.msg);
                        }
                    })
                 }
               })
            },
            //以下二个是界面的方法
            search: function() {
                this.$refs.grid.load({ 'code':this.org.code,'name':this.org.name});
            },
            clear: function() {
                this.org.code = "";
                this.org.name="";
                this.loadPageTree();
                this.$refs.grid.load({ });
            },
            //以下二个是子界面按钮的方法
            save:function(cb){
                this.loading = true;
                this.$refs['addform'].save((flag)=>{
                    if(flag == true){
                    this.modal_add = false;
                    this.loading=false;
                    this.$refs.grid.reLoad({});
                    this.loadPageTree();
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
                    this.loadPageTree();
                    this.viewid="";
                    this.id="";
                }
            });
            },
            //以下二个是左侧数操作
            loadTree: function(obj) {//点击加载树
                if (obj.length > 0) {
                    this.$refs.grid.load({'parentId': obj[0].id});
                }
            },
            loadPageTree: function() {//初始化树结构
                this.$ajax.post("/service_user/organization/getOrgTree")
                    .then(res => {
                    if (res.data.code == "200" && res.data.data.length > 0) {
                        this.baseData = this.$utils.formatTreeData(res.data.data);
                        this.spinShow = false;
                        this.baseData [0].expand = true;
                    }else{
                        this.spinShow = false;
                    }
                })
                .catch((error) => {
                    this.$Message.error('获取页面组织机构树失败！');
                });
            },
            //加载旋转方法
            changeVal:function(){
                this.loading=false;
            },
        },
        created: function() {
            this.loadPageTree();
        }

    };

</script>
