<template>
    <div>
        <page-title>权限分配查看</page-title>
        <div class="page-content">
            <!-- <Tabs v-model="systemId" @on-click="loadRoles">
                <TabPane :label="item.name" :name="item.id" v-for="item in systemList" :key="item.id">
                    
                </TabPane>
            </Tabs> -->
            
            <div class="sys-nav" >
                <span style="margin-right:20px;">角色名称：{{role.name}}</span>
                <template v-if="systemList.length>0">
                    <span>子系统名称：</span>
                    <Select v-model="systemId" filterable @on-change="loadRoles" style="width:250px;margin-left:10px;">
                            <Option :value="item.id" :key="item.id" v-for="item in systemList">{{item.name}}</Option>
                    </Select>
                </template>
            </div>

            <div style="position:relative;min-height:300px;">
                <Spin fix v-if="spinShow"></Spin>
                <tree-openall :list="moudleList" ref="treeopenall" style="margin-left:50px;"></tree-openall>
                <div v-if="moudleList.length ==0" style="text-align:center;padding:20px 0;">暂无数据</div>
            </div>
            <div style="text-align:center;position:absolute;bottom:20px;right:0;left:200px;">
                <Button type="ghost" style="margin-left: 8px" @click="$router.back()">返回</Button>
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
            moudleList: [], // 模块集合 
            moudleSelectIdList: [], // 选中模块ID集合
            spinShow: false,
            systemList:[],
            systemId:null,
        }
    },
    computed: {

    },
    methods: {
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
                    "id": this.$route.params.id ,
                    "systemId": this.systemId
                })
                .then(res => {
                    resolve(res.data);
                },()=>{
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
                        reject();
                    })
            });
            Promise.all([a, b]).then((data) => {
                this.$refs.treeopenall.emptytext = "暂无数据";
                // 添加全展开属性
                var data0 = data[0];
                var data1 = data[1].data.mouIds;
                var data = data0.map(function(j) {
                    j.open = true;
                    j.checked = data1.some(function(id) {
                        return id == j.id;
                    });
                    j.disabled = true;
                    return j;
                });
                this.moudleList = UTILS.formatTreeData(data, "hidenocheckbranch");
                this.spinShow = false;
            })
        }
    },
    mounted: function() {
        
    },
    created: function() {
        this.getSystemList();
    },
}

</script>
