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
                    <Form ref="user" :model="user" :label-width="80">
                        <FormItem label="姓名" prop="userName" style="width: 280px">
                            <Input v-model.trim="user.userName" maxlength="50" placeholder="请输入姓名"></Input>
                        </FormItem>
                        <FormItem label="手机号" prop="userPhone" style="width: 280px">
                            <Input v-model.trim="user.userPhone" maxlength="50" placeholder="请输入工号"></Input>
                        </FormItem> 
                        <FormItem label="性别" prop="sex" style="width: 280px">
                            <Select change-on-select v-model.trim="user.sex" placeholder="请选择性别"  clearable>
                                <Option value="男">男</Option>
                                <Option value="女">女</Option>
                            </Select>
                        </FormItem>
                    </Form>
                    <div class="search-btn">
                        <allowBtn allow="allowsearch" icon="ios-search"  @click="search()">查询</allowBtn>
                        <allowBtn allow="allowreset" icon="ios-refresh-empty"  @click="reset()">重置</allowBtn>
                        
                    </div>
                </div>
            </div>
            <div class="page-tools">
                <allowBtn allow="create" type="success" faicon="fa-plus" @click="$router.push('/user/create')">新增</allowBtn>
            </div>
            <div class="page-content">
                <data-grid :option="gridOption" ref="grid"></data-grid>
            </div>
         </Col>
        </Row>
    </div>
</template>

<script>
    import dataGrid from "@/components/datagrid"

    export default {
        components: { dataGrid },
        data: function() {
            var that = this;
            return {
                user:{
                    userName: '',
                    userPhone: '',
                    sex: '',
                    companyId: '',
                    startAge: '',
                    endAge: '',
                },
                baseData: [], //树形结构数据
                spinShow: true, //未进入页面时，加载过程中显示加载中
                "gridOption":{
                    "url": "/service_user/human/list",
                    "data": [],
                    //表格高度偏移
                    "tableHeightOffset":190,
                    "columns":[  //列配置
                        {
                            "title": "姓名",
                            "key": "userName",
                            "align": "center"
                        },
                        {
                            "title": "手机号",
                            "key": "userPhone",
                            "align": "center"
                        },
                        {    "title": "性别",
                            "key": "sex",
                            "align": "center",
                        },
                        // {    "title": "出生日期",
                        //     "key": "birthday",
                        //     "align": "center",
                        //     "render": function (a, param) {
                        //         if(param.row.birthday){
                        //             return new Date(param.row.birthday).Format('yyyy-MM-dd');
                        //         }else{
                        //             return "";
                        //         }
                        //     },
                        // },
                        {   "title": "所在机构",
                            "key": "orgName",
                            "align": "center"
                        },
                        {    "title": "入职时间",
                            "key": "entryDate",
                            "align": "center",
                            "render": function(a, param) {
                                if(param.row.entryDate){
                                    return new Date(param.row.entryDate).Format('yyyy-MM-dd');
                                }else{
                                    return "";
                                }
                            
                            }
                        },
                       /*  {
                            "title": "工号",
                            "key": "userPhone",
                            "align": "center"
                        }, */
                        { key:'tool', title:'操作', align:"center", width:220, render(h, params){
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
                                        that.toView(params.row);
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
                                    that.toUpdate(params.row);
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
                                on: {
                                    click: () => {
                                    that.toDelete(params.row);
                        }
                        }
                        }, '删除')
                        ]);
                        }}],
                    "loadFilter":function(data){
                        return data.data;
                    }
                }
            }
        },
        methods: {
            reset: function(name) {
                this.user.userName = '';
                this.user.userPhone = '';
                this.user.sex = '';
                this.user.companyId = '',
                this.user.startAge = '';
                this.user.endAge = '';
                this.loadPageTree();
                this.$refs.grid.load({ });
            },
            search: function(){
                this.$refs.grid.load({
                    "name" : this.user.userName,
                    "userPhone" : this.user.userPhone,
                    "sex" : this.user.sex,
                    "companyId": this.user.companyId
                });
            },
            //以下三个的行方法
            toView: function(obj) {
                this.$router.push('/user/view/' + obj.id+"/view");
            },
            //跳转到修改页面
            toUpdate: function(obj) {
                this.$router.push('/user/update/' + obj.id+"/update" );
            },
            toDelete: function(obj) {
                //获取服务器数据
                let that = this;
                this.$Modal.confirm({
                    title: '删除',
                    content: '您确认删除该员工信息，删除后该员工的账号及角色权限将同步删除？',
                    onOk: function () {
                        // 跳转
                        this.$ajax.post("/service_user/human/delete", {'id' : obj.id})
                            .then((res) => {
                            this.$Message.success('删除成功');
                            that.$refs['grid'].loadData();
                    })
                        .catch((error)=>{
                            if(!error.url){console.info(error);}
                    })
                    }
                })
            },
            //获取左侧树数据
            loadPageTree:function(){
                this.$ajax.post("/service_user/organization/getOrgTree")
                    .then(res => {
                    if(res.data.data){
                        this.baseData = this.$utils.formatTreeData(res.data.data);
                        this.spinShow = false;
                        this.baseData [0].expand = true;
                    }else{
                        this.baseData = [];
                    }
                })
                    .catch((error) => {
                        if (!error.url) { console.info(error); }
                })
            },
            //点击左侧树
            loadTree: function(obj) {//点击加载树
                if (obj.length > 0) {
                    this.$refs.grid.load({'orgId': obj[0].id});
                }
            }
        },
        created: function() {
            //获取左侧树结构
            this.loadPageTree();
        }

    };

</script>
