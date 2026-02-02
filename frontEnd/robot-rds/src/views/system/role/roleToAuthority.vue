<template>
    <div>
        <page-title>权限分配</page-title>
        <div class="page-content">
            <div class="sys-nav" >
                <span style="margin-right:20px;">角色名称：{{role.name}}</span>
                <template v-if="systemList.length>0">
                    <span>子系统名称：</span>
                    <Select v-model="systemId" filterable @on-change="loadRoles" style="width:250px;margin-left:10px;">
                            <Option :value="item.id" :key="item.id" v-for="item in systemList">{{item.name}}</Option>
                    </Select>
                </template>
            </div>

            <div style="position:relative;min-height:300px;padding-bottom:100px;">
                <Spin  fix v-if="spinShow"></Spin>
                <tree-openall :list="moudleList" style="margin-left:50px;" ref="treeopenall"></tree-openall>
                <div v-if="moudleList.length ==0" style="text-align:center;padding:20px 0;">暂无数据</div>
            </div>

            <div style="position:absolute;bottom:0px;right:25px;left:215px;">
                <Alert type="warning">
                    请注意，每个子系统重新分配权限时需要点击按钮“分配权限”。
                    <div slot="desc" style="text-align:center;">
                        <Button type="primary" class="ivu-btn ivu-btn-primary" @click="handleSubmit()" :loading="spinShow || submitloading">分配权限</Button>
                        <Button type="ghost" style="margin-left: 8px" :loading="pinShow || submitloading" @click="$router.back()">取消</Button>
                    </div>
                </Alert>
            </div>
        </div>
    </div>
</template>
<script>
import treeOpenall from '@/components/treeOpenall'
import CONFIG from '@/config/index.js'
import UTILS from '@/libs/util.js'

export default {
    components: { treeOpenall },
    name: 'index',
    data() {
        var that = this;
        return {
            role: [], // 模块集合
            submitloading: false,
            moudleList: [], // 模块集合 
            moudleSelectIdList: [], // 选中模块ID集合
            spinShow: true,
            systemList:[],
            systemId:null,
        }
    },
    computed: {

    },
    created: function() {

    },
    methods: {
        handleSubmit: function() {
            if (this.submitloading) {
                return false;
            }
            this.submitloading = true;
            var nodes = this.$refs.treeopenall.getCheckedNodesAll();
            var moudleId = nodes.map(function(j) {
                return j.id;
            });
            var arrowIds = [];
            nodes.map(function(j) {
                if (j.myallow && j.myallow.length > 0) {
                    arrowIds.push({
                        allows: j.myallow,
                    })
                } else {
                    arrowIds.push({
                        allows: "",
                    })
                }
            });
            var url = "/service_user/role/assignAuthorityToRole";
            this.$ajax.post(url, { "roleId": this.role.id, "moudleId": JSON.stringify(moudleId),"systemId":this.systemId, "operIds": arrowIds })
                .then((res) => {
                    this.spinShow =false;
                    if (res.data.code == "200") {
                        this.$Message.success('权限分配成功。');
                        this.$router.push("/role/listRole");
                    } else {
                        this.$Message.error('权限分配出错,请检查是否已选中待分配的模块');

                    }
                    this.submitloading = false;
                },()=>{
                    this.submitloading = false;
                });
        },
        getSystemList(){
            this.$ajax.post("/service_user/systemInfo/allList")
            .then(({data})=>{
                if(data.code == 200){
                    this.systemList = data.data;
                    this.systemId = data.data[0].id;
                    this.loadRoles();
                }
                else{
                    this.systemList = [];
                }
            },()=>{
                this.systemList = [];
            })
        },
        loadRoles(){
            this.moudleList = [];
            this.spinShow = true;
            this.$refs.treeopenall.emptytext = "";

            // 显示模块的树结构
            var a = new Promise((resolve, reject) => {
                this.$ajax.post("/service_user/moudle/moudleTreeData", { 
                    "systemId": this.systemId
                })
                .then(res => {
                    resolve(res.data);
                })
                .catch((error) => {
                    this.$Notice.error({
                        title: '获取模块数据失败'
                    });
                    reject();
                })
            });

            var b = new Promise((resolve, reject) => {
                // 初始化角色信息
                var url = "/service_user/role/findRoleMoudleById";
                this.$ajax.post(url, { 
                    "id": this.$route.params.id,
                    "systemId": this.systemId 
                })
                .then(res => {
                    this.role = res.data.data.role;
                    resolve(res.data);
                },()=>{
                    this.$Message.error('获取模块角色信息失败');
                    reject();
                })
            });
            Promise.all([a, b]).then((data) => {
                this.$refs.treeopenall.emptytext = "暂无数据";
                // 添加全展开属性
                var data0 = data[0];
                var data1 = data[1].data.mouIds;
                var data = data0.map(function(j) {
                    //j.name=j.name+j.id;
                    j.open = true;
                    j.checked = data1.some(function(id) {
                        return id == j.id;
                    });
                    return j;
                });
                this.moudleList = UTILS.formatTreeData(data);
                this.spinShow = false;
            })
        }
    },
    created: function() {
        this.getSystemList();
    },
}

</script>
