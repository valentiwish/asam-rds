<template>
    <div>
        <page-title>子系统维护</page-title>
        <div class="page-search">
            <div class="page-search-title">查询条件</div>
            <div class="page-search-content">
                 <Form :model="systemInfo" :label-width="100">
                <FormItem label="系统名称">
                    <Input v-model.trim="systemInfo.name" placeholder="请输入系统名称" maxlength="50">
                    </Input>
                </FormItem>
                <FormItem label="系统编码">
                    <Input v-model.trim="systemInfo.appid" placeholder="请输入系统编码" maxlength="50">
                    </Input>
                </FormItem>
            </Form>
                <div class="search-btn">
                    <allowBtn allow="allowsearch" type="primary" icon="ios-search"  @click="search()">查询</allowBtn>
                    <allowBtn allow="allowreset" type="error" icon="ios-refresh-empty"  @click="reset()">重置</allowBtn>
                </div>
            </div>
        </div>
        <div class="page-tools">
            <allowBtn allow="新增" type="success" faicon="fa fa-plus"  @click="modal_add=true">新增</allowBtn>
        </div>
        <div class="page-content">
            <data-grid :option="gridOption" ref="grid"></data-grid>
        </div>
    <!-- 新增-->
    <Modal v-model="modal_add" title="新增系统配置">
        <update-modal ref="addform"  @changeVal="changeVal"  v-if="modal_add"></update-modal>
        <div slot="footer">
            <Button type="primary" class="ivu-btn ivu-btn-primary" @click="save" :loading="loading">保存</Button>
            <Button type="ghost" class="ivu-btn ivu-btn-primary" @click="modal_add=false">关闭</Button>
        </div>
    </Modal>
    <!-- 更新-->
    <Modal v-model="modal_update" title="修改系统配置" >
        <update-modal :id="id" ref="updateform"  @changeVal="changeVal" v-if="modal_update"></update-modal>
        <div slot="footer">
            <Button type="primary" class="ivu-btn ivu-btn-primary" @click="update" :loading="loading">保存</Button>
            <Button type="ghost" class="ivu-btn ivu-btn-primary" @click="modal_update=false">关闭</Button>
        </div>
    </Modal>
    <!-- 查看-->
    <Modal v-model="modal_view" title="查看系统配置">
        <update-modal :id="viewid" type="view" v-if="modal_view"></update-modal>
        <div slot="footer">
            <Button type="ghost" class="ivu-btn ivu-btn-primary" @click="modal_view=false">关闭</Button>
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
            loading:false,
                id: '', //页面修改赋值
                viewid: '', //页面查看赋值
                systemInfo: {
                    name: '',
                    appid: '',
                },
                modal_update: false, //修改弹窗
                modal_view: false, //查看弹窗
                modal_add: false, //新增弹窗
                //以上是界面v-model的所有参数
            "gridOption": {
                    "url": "/service_user/systemInfo/list",
                    "columns": [ //列配置
                        {
                            "title": "系统名称",
                            "key": "name",
                            "align": "center"
                        },
                        {
                            "title": "系统编码",
                            "key": "appid",
                            "align": "center"
                        },
                        {
                            "title": "链接地址",
                            "key": "systemUrl",
                            "align": "center"
                        },
                        {
                            "title": "安全域名",
                            "key": "domain",
                            "align": "center"
                        },
                        {
                            "title": "系统提供商",
                            "key": "providername",
                            "align": "center"
                        },
                        /* {
                            "title": "是否展示",
                            "key": "enable",
                            "align": "center",
                            render(h, params) {
                                if(params.row.enable==1){
                                    return "是";
                                }else{
                                    return "否";
                                }
                            }
                        }, */
                        {
                            key: 'id',
                            title: '操作',
                            align: "center",
                            width: "230px",
                            render(h, params) {
                                return h('div', [
                                    h('allowBtn', {
                                        props: {
                                            type:"ghost",
                                            size: 'small',
                                            allow: "查看",
                                            faicon:"fa fa-file-text-o",
                                            ghost:true,
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
                                            type:"ghost",
                                            size: 'small',
                                            allow: '修改',
                                            faicon:"fa fa-edit",
                                            ghost:true,
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
                                            type:"ghost",
                                            size: 'small',
                                            allow: "删除",
                                            faicon:"fa fa-close",
                                            ghost:true,
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
                    var url = "/service_user/systemInfo/delete";
                    this.$ajax.post(url, { 'id': idvalue })
                        .then((res) => {
                            that.$refs['grid'].loadData();
                        })
                        .catch((error) => {
                            if(!error.url){console.info(error);}
                        })
                }
            })
        },
        //以下二个是界面的方法
        search: function() {
            this.$refs.grid.load({ 'name':this.systemInfo.name, 'appid': this.systemInfo.appid });
        },
        reset: function() {
            this.systemInfo.name = "";
            this.systemInfo.appid = "";
            this.$refs.grid.load({ });
        },
        //以下二个是子界面按钮的方法
        save: function(cb) {
            this.loading = true;
            this.$refs['addform'].save((flag) => {
                this.loading=false;
                if (flag == true) {
                    this.modal_add = false;
                    this.$refs.grid.reLoad({});
                }
            });
        },
        update: function(cb) {
            this.loading = true;
            this.$refs['updateform'].save((flag) => {
                this.loading=false;
                if (flag == true) {
                    this.modal_update = false;
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
    created: function() {
        //this.getData();
    }
}

</script>
