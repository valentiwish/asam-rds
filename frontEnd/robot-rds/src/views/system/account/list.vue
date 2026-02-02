<template>
    <div>
        <page-title>账户管理</page-title>
        <div class="page-search">
            <div class="page-search-title">查询条件</div>
            <div class="page-search-content">
                <Form :label-width="80" @submit.native.prevent="search">
                    <!-- <FormItem label="账户名称"> -->
                        <!-- <Input v-model.trim="loginName" placeholder="请输入账户名称" maxlength="30"></Input> -->
                    <!-- </FormItem> -->
                    <FormItem label="用户名">
                        <Input v-model.trim="userName" placeholder="请输入用户名" maxlength="30"></Input>
                    </FormItem>
                </Form>
                <div class="search-btn">
                    <allowBtn allow="allowsearch" type="primary" icon="ios-search"  @click="search()">查询</allowBtn>
                    <allowBtn allow="allowreset" type="error" icon="ios-refresh-empty"  @click="clear()">重置</allowBtn>

                    <!-- 修改账户-->
                    <Modal v-model="updateModal" title="修改账户">
                        <Form :label-width="108"  ref="account" :model="account" :rules="ruleValidate" @submit.prevent="handleSubmit">
                            <Form-item label="账户名称" prop="loginName">
                                <Input v-model.trim="account.loginName" placeholder="请输入账户名"></Input>
                            </Form-item>
                        </Form>
                        <div slot="footer">
                            <Button type="primary" class="ivu-btn ivu-btn-primary" @click="handleSubmit('account')"  :loading="loading">保存</Button>
                            <Button  type="ghost" style="margin-left: 8px" @click="cal()">关闭</Button>
                        </div>
                    </Modal>

                    <!-- 分配角色-->
                    <Modal v-model="roleModal" title="分配角色">
                        <template>
                            <CheckboxGroup v-model="roleNames">
                                <Row type="flex">
                                    <Col span="8" v-for="item in roles" :key="item.id" style="margin-bottom:20px;">
                                        <Checkbox :label="item.name" :key="item.id"></Checkbox>
                                    </Col> 
                                </Row>
                            </CheckboxGroup>
                        </template>
                        <div slot="footer">
                            <Button type="primary" class="ivu-btn ivu-btn-primary" @click="assignRoleToUser()"  :loading="loading">保存</Button>
                            <Button  type="ghost" style="margin-left: 8px" @click="cal()">关闭</Button>
                        </div>
                    </Modal>
                </div>
            </div>
        </div>
        <div class="page-content">
            <data-grid :option="gridOption" ref="grid"></data-grid>
        </div>
    </div>
</template>
<script>
import dataGrid from "@/components/datagrid"
import CONFIG from '@/config/index.js'

export default {
    components: { dataGrid },
    data: function() {
        var that = this;
        return {
            loading:false,
            userName: '', //查询条件（用户名）
            flags: false, //全选用
            oldName: '', //旧账户名称（唯一性校验）
            loginName: '',
            id: '',
            updateModal: false,
            roleModal: false,
            roleIds: [], //系统中的所有角色
            roleNames: [], //系统中的所有角色
            roles:[],
            account: {
                id:'',
                loginName: '',
            },
            ruleValidate: {
                loginName: [
                    { required: true, message: '请输入账号', trigger: 'blur' },
                    { max: 20, message: '账号不超过20', trigger: 'blur' },
                    { validator: this.checkLoginName, trigger: 'blur' }
                ]
            },
            "gridOption": {
                "url": "/service_user/account/list",
                "columns": [
                    // {
                        // "title": "账户名称",
                        // "key": "userName",
                        // "align": "center",
                    // },
                    {
                        "title": "用户名",
                        "key": "userName",
                        "align": "center",
                    },
                    {
                        "title": "创建时间",
                        "key": "createTime",
                        "align": "center",
                        "render": function(h, p){
                            if (p.row && p.row.createTime){
                                let createTime = new Date(p.row.createTime).Format("yyyy-MM-dd hh:mm");
                                return h('span', {}, createTime);
                            }
                        }
                    },
                    {
                        "title": "启用状态",
                        "key": "state",
                        "align": "center",
                        "render": function(h, params) {
                            var hh = h("div", [
                                h("span", {
                                    style: {
                                        marginRight: '5px'
                                    }
                                }, params.row.state == 1 ? "启用" : "禁用"),
                                h("i-switch", {
                                    props: {
                                        size:"small",
                                        value: params.row.state == 1 ? true : false,
                                    },
                                    scopedSlots: {
                                        "default": props => createElement('span', props.value),
                                    },
                                    on: {
                                        "on-change": (flag) => {
                                             if(flag == true){
                                                params.row.state = 1;
                                            }else{
                                                params.row.state = 0;
                                            }
                                            params.row.state = flag;
                                            that.setUserStatus(params.row, flag);
                                        }
                                    }
                                })
                            ]);
                            return hh;
                        }
                    },
                    {
                        key: 'id',
                        title: '操作',
                        align: "center",
                        width: 230,
                        render(h, params) {
                            return h('div', [
                                // h('allowBtn', {
                                //     props: {
                                //         type: 'primary',
                                //         size: 'small',
                                //         allow: 'update',
                                //         // disabled: params.row.state == 1 ? false : true
                                //     },
                                //     style: {
                                //         marginRight: '5px'
                                //     },
                                //     on: {
                                //         click: () => {
                                //             that.toUpdate(params.row)
                                //         }
                                //     }
                                // }, "修改"),
                                h('allowBtn', {
                                    props: {
                                        type: 'ghost',
                                        size: 'small',
                                        allow: 'resetPwd',
                                        faicon:"fa-history"
                                        // disabled: params.row.state == 1 ? false : true
                                    },
                                    style: {
                                        marginRight: '5px'
                                    },
                                    on: {
                                        click: () => {
                                            that.resetPwd(params.row)
                                        }
                                    }
                                }, '重置密码'),
                                h('allowBtn', {
                                    props: {
                                        type: 'ghost',
                                        size: 'small',
                                        allow: 'assignRole',
                                        faicon:"fa-superpowers"
                                        // disabled: params.row.state == 1 ? false : true
                                    },
                                    style: {
                                        marginRight: '5px'
                                    },
                                    on: {
                                        click: () => {
                                            that.toAssignRoleToUser(params.row)
                                        }
                                    }
                                }, '分配角色'),
                                /*  h('allowBtn', {
                                    props: {
                                        type: 'info',
                                        size: 'small',
                                        allow: 'delete',
                                        disabled: params.row.state == 1 ? false : true
                                    },
                                    style: {
                                        marginRight: '5px'
                                    },
                                    on: {
                                        click: () => {
                                            that.del(params.row)
                                        }
                                    }
                                }, '删除'),*/
                            ]);
                        }
                    }
                ],
                "loadFilter": function(data) {
                    return data.data;
                },
            },
            //账户分配角色表格 配置
            roleOption: {
                data: [],
                "columns": [{
                        title: '角色编码',
                        key: 'code',
                        "align": "center",
                    },
                    {
                        title: '角色名称',
                        key: 'name',
                        "align": "center"
                    },
                    {
                        title: '操作',
                        key: 'id',
                        "align": "center",
                        render: (h, params) => {
                            return h("i-switch", {
                                props: {
                                    value: params.row.status
                                },
                                on: {
                                    "on-change": (flag) => {
                                        params.row.status = flag;
                                        that.upUserStatus(params.row, flag);
                                    }
                                }
                            });
                        }
                    }
                ]
            }
        }
    },
    methods: {
        upUserStatus: function(obj, flag) {
            this.roleOption.data.some((j, i) => {
                if (j.id == obj.id) {
                    j.status = flag;
                    this.roleOption.data.splice(i, 1, j)
                }
            })
        },
        //跳转到修改账户
        toUpdate: function(obj) {
            var id = obj.id;
            this.updateModal = true;
            if (id) {
                var url = "/service_user/account/findById";
                this.$ajax.post(url, { "id": id })
                    .then((res) => {
                        this.account = res.data.data;
                        this.id = id;
                        this.oldName = this.account.loginName;
                    })
                    .catch((error) => {
                        if (!error.url) { console.info(error); }
                    })
            }
        },
        setUserStatus: function(obj, flag) {
            this.$ajax.post("/service_user/account/setUserStatus", { "id": obj.id, "flag": flag })
                .then((res) => {})
                .catch((error) => {
                    if (!error.url) { console.info(error); }
                })
        },

        //查询
        search: function() {
            this.$refs.grid.load({ 'loginName': this.loginName, 'userName': this.userName });
        },
        //校验数据
        handleSubmit(name) {
            this.$refs[name].validate((valid) => {
                if (valid) {
                    this.loading = true;
                    if (this.updateModal) {
                        var url = "/service_user/account/update";
                        this.$ajax.post(url, { "loginName": this.account.loginName, "id": this.id })
                            .then((res) => {
                                this.updateModal = false;
                                this.$Message.success('修改成功');
                                this.loading=false;
                                this.$refs.grid.reLoad({});
                            })
                            .catch((error) => {
                                if (!error.url) { console.info(error); }
                            })
                    }
                } else {
                        this.$Message.error('表单校验失败!');
                }
            })
        },

        //清除缓存
        clear: function() {
            this.account.loginName = '';
            this.account.loginName = '';
            this.account.state = '1';
            this.roleIds=[];
            this.roleNames=[];
            this.loginName = '';
            this.oldName = '';
            this.userName = '';
            // this.$refs.grid.load({ });
        },
        //取消
        cal: function() {
            this.updateModal = false;
            this.roleModal = false;
            this.clear();
        },

        //重置密码
        resetPwd: function(obj) {
            var that = this;
            this.$Modal.confirm({
                title: '重置',
                content: '您确认重置密码为“123456”？',
                onOk: function() {
                    // 跳转
                    var idvalue = obj.id;
                    this.$ajax.post("/service_user/account/resetPwd", {'accountId': idvalue})
                    .then((res) => {
                        that.$refs.grid.reLoad({});
                    })
                }
            })
        },
        //加载所有角色
        loadRoles: function() {
            this.$ajax.post("/service_user/role/findAll", {})
                .then(res => {
                    this.roles = res.data.data;
                })
                .catch((error) => {
                    if (!error.url) {
                        console.info(error);
                    }
                });
        },
        //跳转到分配角色页面
        toAssignRoleToUser: function(obj) {
            var accountId = obj.id;
            //加载所有角色
            //this.loadRoles();
            //展开模态框
            this.roleModal = true;
            //将账户id放入ID
            this.id = accountId;
            //根据Id查询角色
            var url = "/service_user/account/findRolesByAccountId";
            this.$ajax.post(url, { "id": accountId })
                .then((res) => {
                    this.roleIds = res.data.data;
                    this.roleIds.map((j,i) =>{
                        this.roles.map(mj=>{
                            if(mj.id == j){
                                this.roleNames.push(mj.name)
                            }
                        })
                    });
                })
                .catch((error) => {
                    if (!error.url) { console.info(error); }
                })
        },
        //分配角色
        assignRoleToUser: function() {
            this.loading = true;
            //把角色名字转化为角色id
            var newroleIds=[];
            this.roleNames.map((j,i) =>{
                this.roles.map(mj=>{
                    if(mj.name == j){
                        newroleIds.push(mj.id)
                    }
                })
            });
            this.$ajax.post("/service_user/account/assignRolesByAccountId", { "accountId": this.id, "roleIds":  JSON.stringify(newroleIds) })
                .then((res) => {
                    this.roleModal = false;
                    this.$Message.success('分配成功');
                    this.loading=false;
                    this.clear();
                })
                .catch((error) => {
                    if (!error.url) { console.info(error); }
                })
        },
        checkLoginName: function(rule, value, callback) {
            this.$ajax.post("/service_user/account/check", { 'loginName': value, 'oldName': this.oldName })
                .then((res) => {
                    if (res.data.data == false) {
                        callback();
                    } else {
                        callback("账户名称已存在,请重新输入");
                    }
                })
                .catch((error) => {
                    callback('此账户不能使用。');
                })
        },
    },
    created: function() {
        this.loadRoles();
    }
}
</script>
