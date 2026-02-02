<template>
    <div>
        <page-title>角色管理</page-title>
        <div class="page-search">
            <div class="page-search-title">查询条件</div>
            <div class="page-search-content">
                <Form :label-width="80" @submit.native.prevent="submit">
                    <FormItem label="角色名称">
                        <Input v-model.trim="rolename" placeholder="请输入角色名称" maxlength="30"></Input>
                    </FormItem>
                    <FormItem label="角色级别">
                        <InputNumber v-model.trim="rolelevel" placeholder="请输入角色级别" style="width: 100%;" :min="1" :max="5"></InputNumber>
                    </FormItem>
                </Form>
                <div class="search-btn">
                    <allowBtn allow="allowsearch" type="primary"  @click="search()">查询</allowBtn>
                    <allowBtn allow="allowreset"  @click="clear()">重置</allowBtn>
                </div>
            </div>
        </div>
        <div class="page-tools">
            <allowBtn allow="create" type="success" faicon="fa fa-plus"  @click="role_add=true">新增角色</allowBtn>
        </div>
        <div class="page-content">
            <data-grid :option="gridOption" ref="grid"></data-grid>
        </div>
        <!-- 新增角色-->
        <Modal v-model="role_add" title="新增角色">
            <update-modal ref="addform" @changeVal="changeVal"></update-modal>
            <div slot="footer">
                <Button type="primary" class="ivu-btn ivu-btn-primary" @click="save" :loading="loading">保存</Button>
                <Button type="ghost" @click="role_add=false">关闭</Button>
            </div>
        </Modal>
        <!-- 更新角色-->
        <Modal v-model="role_update" title="更新角色">
            <update-modal :id="id" ref="updateform" @changeVal="changeVal"></update-modal>
            <div slot="footer">
                <Button type="primary" class="ivu-btn ivu-btn-primary" @click="update" :loading="loading">保存</Button>
                <Button type="ghost" @click="role_update=false">关闭</Button>
            </div>
        </Modal>
        <!-- 查看角色-->
        <Modal v-model="role_view" title="查看角色">
            <update-modal :id="viewid" type="view"></update-modal>
            <div slot="footer">
                <Button type="ghost" @click="role_view=false">关闭</Button>
            </div>
        </Modal>
        <!-- 角色账户-->
        <Modal v-model="role_account" title="角色账户"  width="1100">
            <div>
                角色名称：{{roleNameObj}}
                <Button type="primary" style="float:right;" @click="addAccount">新增账户</Button>
            </div>
            <br/>
            <data-grid :option="accountsOption" ref="accountsgrid"></data-grid>
            <div v-show="accountFlag">
                <h1 class="equip-card-cur-title">选择账户信息</h1>
                <Transfer :data="allAccounts" :list-style="listStyle" :target-keys="pointTargetKeys" v-model="pointTargetKeys" filterable :filter-method="filterMethod" @on-change="handleChange"></Transfer>
            </div>
            <div slot="footer">
                <Button type="primary" class="ivu-btn ivu-btn-primary" @click="assignAccount" :loading="loading" v-show="accountFlag">分配</Button>
                <Button type="ghost" class="ivu-btn ivu-btn-primary" @click="role_account=false">关闭</Button>
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
            accountFlag: false,
            allAccounts: [], //所有账户
            listStyle: {
                width: '47%',
                height: '300px'
            },
            pointTargetKeys: [], //目标框key数组
            loading: false,
            rolename: '',
            rolelevel: 1,
            id: '',
            viewid: '', //页面查看赋值
            role_update: false, //控制模块修改弹窗
            role_view: false, //控制模块查看弹窗
            role_add: false, //控制模块新增弹窗
            role_account: false, //控制模块账户弹窗
            "gridOption": {
                "checkbox": {
                    "enable": false
                },
                "header": true,
                "iconCls": "fa-power-off",
                "data": [],
                "url": "/service_user/role/list",
                "columns": [ // 列配置
                    {
                        "title": "角色编码",
                        "align": "center",
                        "key": "roleCode"
                    },{
                        "title": "角色名称",
                        "align": "center",
                        "key": "name"
                    },
                    {
                        "title": "角色级别",
                        "align": "center",
                        "key": "roleLevel"
                    },
                    {
                        "title": "创建时间",
                        "align": "center",
                        "key": "createTime",
                        "render": function(a, param) {
                            return new Date(param.row.createTime).Format('yyyy-MM-dd hh:mm:ss');
                        }
                    },
                    {
                        key: 'tool',
                        title: '操作',
                        "align": "center",
                        "width": 480,
                        render(h, params) {
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
                                            that.toView(params.row)
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
                                            that.toUpdate(params.row)
                                        }
                                    }
                                }, '修改'),
                                h('allowBtn', {
                                    props: {
                                        type: 'ghost',
                                        size: 'small',
                                        allow: "create",
                                        faicon:"fa-file-text-o"
                                    },
                                    style: {
                                        marginRight: '5px'
                                    },
                                    on: {
                                        click: () => {
                                            that.toAccountsOfRole(params.row)
                                        }
                                    }
                                }, '账户信息'),
                                h('allowBtn', {
                                    props: {
                                        type: 'ghost',
                                        size: 'small',
                                        allow: "viewAuthority",
                                        faicon:"fa-window-restore"
                                    },
                                    style: {
                                        marginRight: '5px'
                                    },
                                    on: {
                                        click: () => {
                                            that.viewRoleAuthority(params.row)
                                        }
                                    }
                                }, '查看权限'),
                                h('allowBtn', {
                                    props: {
                                        type: 'ghost',
                                        size: 'small',
                                        allow: "assignAuthority",
                                        faicon:"fa-superpowers"
                                    },
                                    style: {
                                        marginRight: '5px'
                                    },
                                    on: {
                                        click: () => {
                                            that.roleToAuthority(params.row)
                                        }
                                    }
                                }, '分配权限'),
                                h('allowBtn', {
                                    props: {
                                        type: 'ghost',
                                        size: 'small',
                                        allow: "delete",
                                        faicon:"fa-close"
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
                            ]);
                        }
                    }
                ],
                "loadFilter": function(data) {
                    return data.data;
                }
            },
            //角色对应账户列表
            accountsOption: {
                data: [],
                queryPage: false,
                height:350,
                "columns": [{
                    title: '账户名称',
                    key: 'userName',
                    "align": "center",
                    render: (h, params) => {
                        return params.row.userName;
                    }
                },
                {
                    title: '创建时间',
                    key: 'createTime',
                    "align": "center",
                    render: (h, params) => {
                        let createTime = "";
                        if (params.row && params.row.createTime){
                            createTime = new Date(params.row.createTime).Format("yyyy-MM-dd hh:mm");
                        }
                        return h('span', {}, createTime);
                    }
                },
                {
                    title: '操作',
                    key: 'tool',
                    "align": "center",
                    render: (h, params) => {
                        return h('div', [
                            h('allowBtn', {
                                props: {
                                    type: 'info',
                                    size: 'small',
                                    allow: "delete"
                                },
                                style: {
                                    marginRight: '5px'
                                },
                                on: {
                                    click: () => {
                                        that.delRoleAccount(params.row)
                                    }
                                }
                            }, '移除')
                        ]);
                    }
                }]
            }
        }
    },
    methods: {
        //打开修改模块弹窗
        toUpdate: function(obj) {
            this.id = obj.id;
            this.role_update = true;

        },
        // 保存数据
        save: function(cb) {
            this.loading = true;
            this.$refs['addform'].save((flag) => {
                if (flag == true) {
                    this.role_add = false;
                    this.loading = false;
                    this.$refs.grid.reLoad({});
                }
            });
        },
        update: function(cb) {
            this.loading = true;
            this.$refs['updateform'].update((flag) => {
                if (flag == true) {
                    this.role_update = false;
                    this.loading = false;
                    this.$refs.grid.reLoad({});
                    this.viewid = "";
                }
            });
        },        
        //打开角色账户弹窗
        toAccountsOfRole: function(obj) {
            this.id = obj.id;
            this.roleNameObj = obj.name;
            if(this.accountFlag){
                this.accountFlag= false;
            }
            this.role_account = true;
            this.getAccountsOfRole();
        },
        //穿梭框发生变化时处理新key值
        handleChange(newTargetKeys) {
            this.pointTargetKeys = newTargetKeys;
        },
        addAccount: function() {
            this.allAccounts = [];
            this.pointTargetKeys = [];
            //点击新增账户按钮后，获取所有的账户信息，并弹出穿梭框。
            this.$ajax.post("/service_user/account/findAccountsList", { "roleId": this.id })
                .then((res) => {
                    if (res.data.code == "200") {
                        //穿梭框数据构造
                        var arr2 = [];
                        var array = res.data.data;
                        if (this.accountsOption.data != null && this.accountsOption.data.length > 0) {
                            var array2 = this.accountsOption.data;
                            for (var i = 0; i < array.length; i++) { //用来遍历的数组使用两个数组长度比较长的，不然可能没有效果
                        
                                var stra = array[i];　　
                                var count = 0;　　
                                for (var j = 0; j < array2.length; j++) {　　　　
                                    var strb = array2[j];　　　　
                                    if (stra.id == strb.id) {　　　　
                                        count++;　　　　　　
                                    }　　　　
                                }　　
                                if (count === 0) {　　　　
                                    arr2.push(stra);　　
                                }　
                            }
                            this.buildMap(arr2);
                        } else {
                            this.buildMap(array);
                        }

                    } else {
                        this.$Message.error('获取账户信息列表出错，请检查。');
                    }

                })
                .catch((error) => {
                    //this.$Message.error('获取设备的点变量树失败');
                })
            this.accountFlag = true;
        },            
        //构造穿梭框数据类型
        buildMap: function(value) {
            if (value) {
                this.allAccounts = [];
                value.forEach((j, i) => {
                    this.allAccounts.push({
                        key: j.id,
                        label: j.userName
                    });
                })
            }
        },
        assignAccount: function() {
            if (this.loading) return;
            this.loading = true;
            if (this.pointTargetKeys.length > 0) {
                this.$ajax.post("/service_user/account/assignAccountToRole", { "roleId": this.id, "accountIds":JSON.stringify(this.pointTargetKeys)})
                    .then((res) => {
                        if (res.data.code == "200") {
                            this.$Message.success('分配成功');
                            this.loading = false;
                            this.accountFlag = false;
                            this.getAccountsOfRole();
                        } else {
                            this.$Message.error('分配出错，信息为' + data.msg);
                        }
                    })
            } else {
                this.loading = false;
                this.$Message.error('未选择任何账户信息');
            }

        },
        getAccountsOfRole: function() {

            //根据角色id查找所有账户信息
            this.$ajax.post("/service_user/role/getAccountsByRoleId", { "roleId": this.id })
                .then((res) => {
                    this.accountsOption.data = res.data.data;
                    this.$refs.accountsgrid && this.$refs.accountsgrid.reLoad({});
                })
        },
        //移除角色对应账户           
        delRoleAccount: function(obj) {
            this.$Modal.confirm({
                title: '删除',
                content: '您确认从该角色中移除此账户？',
                onOk: () => {
                    // 获取服务器数据
                    this.$ajax.post("/service_user/role/delRoleAccount", { "roleId": this.id, "accountId": obj.id })
                        .then((res) => {
                            if (res.data.code == "200") {
                                this.getAccountsOfRole();
                                this.addAccount();
                                this.$Message.success('移除成功！');
                            } else {
                                this.$Message.error('移除出错，请检查');
                            }
                        })
                }
            })
        },
        clear: function() {
            this.rolename = "";
            this.rolelevel = "";
            this.$refs.grid.load({});
        },
        del: function(obj) {
            var that = this;
            this.$Modal.confirm({
                title: '删除',
                content: '您确认删除此条数据？',
                onOk: function() {
                    // 获取服务器数据
                    var idvalue = obj.id;
                    var url = "/service_user/role/delete";
                    this.$ajax.post(url, { 'id': idvalue })
                        .then((res) => {
                            if (res.data.code == "200") {
                                this.$Message.success('删除成功');
                                that.$refs.grid.reLoad({});
                            } else {
                                this.$Message.error('删除出错，信息为' + data.msg);
                            }
                        })
                }
            })
        },
        search: function() {
            this.$refs.grid.load({ "name": this.rolename, "rolelevel": this.rolelevel });
        },
        toView: function(obj) {
            this.viewid = obj.id;
            this.role_view = true;
        },
        roleToAuthority: function(obj) {
            this.$router.push("/role/roleToAuthority/" + obj.id);
        },
        viewRoleAuthority: function(obj) {
            this.$router.push("/role/viewRoleAuthority/" + obj.id);
        },
        //加载旋转方法
        changeVal: function() {
            this.loading = false;
        },
    },
    wacth: {
        'sysData.sequence': function(n, o) {
            // console.log(n,o)
        }
    }
}

</script>
