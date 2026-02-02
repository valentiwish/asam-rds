<template>
    <div>
        <Form :label-width="100" ref="roleData" :model="roleData" :rules="ruleValidate" :class="{'form-view':type=='view'}" @submit.prevent="submit">
            <Form-item label="角色编码" prop="roleCode">
                <template v-if="type=='view'">{{roleData.roleCode}}</template>
                <Input v-model.trim="roleData.roleCode" maxlength="10" placeholder="请输入角色编码" v-else></Input>
            </Form-item>
            <Form-item label="角色名称" prop="name">
                <template v-if="type=='view'">{{roleData.name}}</template>
                <Input v-model.trim="roleData.name" maxlength="10" placeholder="请输入角色名称" v-else></Input>
            </Form-item>
            <Form-item label="角色级别" prop="roleLevel">
                <template v-if="type=='view'">{{roleData.roleLevel}}</template>
                <InputNumber :max="5" :min="1" v-model.trim="roleData.roleLevel" style="width: 100%;" placeholder="请输入角色级别" v-else></InputNumber>
            </Form-item>
            <Form-item label="sql脚本" prop="sqlScript">
                <template v-if="type=='view'">{{roleData.sqlScript}}</template>
                <div v-else>
                    <Input v-model.trim="roleData.sqlScript" maxlength="500" type="textarea" placeholder="请输入sql脚本，不超过500个字符"></Input>
                    <div class="str_count">{{roleData.sqlScript ? roleData.sqlScript.length : 0}}/500</div>
                </div>
            </Form-item>
            <Form-item label="角色描述" prop="description">
                <template v-if="type=='view'">{{roleData.description}}</template>
                <div v-else>
                    <Input v-model.trim="roleData.description" maxlength="100" type="textarea" placeholder="请输入角色描述，不超过100个汉字"></Input>
                    <div class="str_count">{{roleData.description ? roleData.description.length : 0}}/120</div>
                </div>
            </Form-item>
        </Form>
    </div>
</template>
<script>
import CONFIG from '@/config/index.js'
import UTILS from '@/libs/util.js'

export default {
    name: 'index',
    props: ["id", "type"],
    data() {
        var that = this;
        return {
            loading: false,
            oldName: '', //旧角色名称（唯一性校验）
            oldRoleCode:'',
            roleData: {
                id: '',
                name: '',
                roleLevel: '1',
                sqlScript:'',
                description: ''
            },
            ruleValidate: {
                roleCode: [
                    { required: true, message: '请输入角色编码', trigger: 'blur' },
                    { max: 10, trigger: 'blur', message: '角色编码最多10个字符' },
                    { validator: this.checkRoleCode, trigger: 'blur' },
                ],
                name: [
                    { required: true, message: '请输入角色名称', trigger: 'blur' },
                    { max: 10, trigger: 'blur', message: '角色名称最多10个字符' },
                    { validator: this.checkRoleName, trigger: 'blur' },
                ],
                description: [
                    { type: 'string', max: 120, message: '角色描述，不超过120个汉字', trigger: 'blur' }
                ]
            }
        }
    },
    methods: {
        isshow: function(item) {
            return this.operations.some(function(j, i) {
                return item.id == j;
            })
        },
        save(cb) {
            this.$refs['roleData'].validate((valid) => {
                if (valid) {
                    this.loading = true;
                    var url = "/service_user/role/addRole";
                    this.roleData.createTime = new Date();
                    this.$ajax.post(url, { "data": JSON.stringify(this.roleData) })
                        .then((res) => {
                            if (res.data.code == "200") {
                                this.$refs['roleData'].resetFields();
                                cb && cb(true);
                                this.$Message.success('保存成功');

                            } else {
                                this.$Message.error('保存出错，信息为' + data.msg);
                            }
                        })
                        .catch((error) => {
                            if (!error.url) { console.info(error); }
                        })
                } else {
                    this.$emit("changeVal");
                    this.$Message.error('表单校验失败!');
                }
            })
        },
        update: function(cb) {
            this.$refs['roleData'].validate((valid) => {
                if (valid) {
                    var url = "/service_user/role/addRole";
                    this.$ajax.post(url, { "data": JSON.stringify(this.roleData) })
                        .then((res) => {
                            if (res.data.code == "200") {
                                this.$refs['roleData'].resetFields();
                                cb && cb(true);
                                this.$Message.success('保存成功');
                            } else {
                                this.$Message.error('保存出错，信息为' + data.msg);
                            }

                        })
                        .catch((error) => {
                            if (!error.url) { console.info(error); }
                        })
                } else {
                    this.$emit("changeVal");
                    this.$Message.error('表单校验失败!');
                }
            })
        },
        getData: function() {
            //获取此模块模块基本信息 根据id查询
            this.$ajax.post("/service_user/role/findRoleById", { id: this.id })
                .then(res => {
                    this.roleData = res.data.data;
                    this.oldName = this.roleData.name;
                    this.oldRoleCode = this.roleData.roleCode;
                })
                .catch((error) => {
                    if (!error.url) { console.info(error); }
                });
        },
        checkRoleCode: function(rule, value, callback) {
            this.$ajax.post("/service_user/role/checkRoleCode", { 'roleCode': value, "oldRoleCode": this.oldRoleCode })
                .then((res) => {
                    if (res.data.code == "200" && res.data.data == false) {
                        callback();
                    } else {
                        callback("此角色编码已经被占用。");
                    }
                })
                .catch((error) => {
                    callback('此角色编码不能使用。');
                })
        },
        checkRoleName: function(rule, value, callback) {
            this.$ajax.post("/service_user/role/checkRoleName", { 'rolename': value, "oldName": this.oldName })
                .then((res) => {
                    if (res.data.code == "200" && res.data.data == false) {
                        callback();
                    } else {
                        callback("此角色名称已经被占用。");
                    }
                })
                .catch((error) => {
                    callback('此角色名称不能使用。');
                })
        }
    },
    watch: {
        id: function() {
            if (this.id != "") {
                this.getData();
            }
        }
    },
}

</script>
