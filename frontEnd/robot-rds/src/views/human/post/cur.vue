<template>
    <div>
        <Form :label-width="120" ref="post" :model="post" :rules="ruleValidate" :class="{'form-view':type=='view'}">
            <Form-item label="名称" prop="name">
                <template v-if="type=='view'">{{post.name}}</template>
                <Input v-else v-model.trim="post.name" placeholder="请输入名称，不超过30个字符" maxlength="50"></Input>
            </Form-item>

            <Form-item label="所属部门">
                <template v-if="type=='view'">{{post.orgName}}</template>
                <Cascader v-else :data="orgTree" v-model="orgIds" placeholder="请选择所属部门"></Cascader>
            </Form-item>

            <Form-item label="职责" prop="duty">
                <template v-if="type=='view'">{{post.duty}}</template>
                <Input v-else v-model.trim="post.duty" placeholder="请输入职责，不超过30个字符" maxlength="50"></Input>
            </Form-item>

            <Form-item label="任职条件" prop="remark" class="singleline">
                <template v-if="type=='view'">{{post.conditions}}</template>
                <Input v-else v-model.trim="post.conditions"  maxlength="100" type="textarea" placeholder="请输入任职条件，不超过100个汉字..."></Input>
            </Form-item>
            
            <Form-item label="备注" prop="remark" class="singleline">
                <template v-if="type=='view'">{{post.remark}}</template>
                <Input v-else v-model.trim="post.remark"  maxlength="100" type="textarea" placeholder="请输入备注，不超过100个汉字..."></Input>
            </Form-item>
        </Form>
    </div>
</template>
<script>
    export default {
        props:["id", "type"],
        data() {
            return {
                orgTreeData: [], //部门树扁平数据
                orgTree: [], //部门树
                orgIds: [],
                post: { //岗位
                    id: '',
                    name: '',
                    orgId: '',
                    duty: '',
                    conditions: '',
                    remark: ''
                },
                ruleValidate: {
                     name: [
                        { required: true, message: '名称不能为空', trigger: 'blur' },
                        { validator: this.checkName, trigger: 'blur' },
                    ],
                    duty: [
                        { required: true, message: '职责不能为空', trigger: 'blur' },
                    ],
               }
           }
        },
        methods: {
            //校验名称唯一性
            checkName: function(rule, value, callback){
                if(value){
                    this.$ajax.post("/service_user/post/checkName",{'name': value, 'id': this.id})
                    .then((res) => {
                        if (res.data){
                            callback();
                        } else {
                            callback("名称已存在");
                        }
                    })
                    .catch((error) => {
                        callback('校验失败');
                    })
                } else {
                    callback("请输入名称");
                }
            },
            save(cb){
                this.$refs['post'].validate((valid) => {
                    if (valid) {
                        if (this.post && this.post.createTime){
                            this.post.createTime = new Date(this.post.createTime).Format("yyyy-MM-dd hh:mm:ss");
                        }
                        if (this.post && this.post.updateTime){
                            this.post.updateTime = new Date(this.post.updateTime).Format("yyyy-MM-dd hh:mm:ss");
                        }
                        this.post.orgId = this.orgIds && this.orgIds.length > 0 ? this.orgIds[this.orgIds.length - 1] : null;
                        var humPostVO = this.post;
                        this.$ajax.post("/service_user/post/save", humPostVO)
                        .then((res) => {
                            if (200 == res.data.code){
                                cb && cb(true);
                                this.$refs['post'].resetFields();
                                this.$Message.success('保存成功');
                            }else {
                                this.$Message.error('保存出错');
                            }
                        })
                        .catch((err) => {
                            cb && cb(false);
                            if(!error.url){console.info(error);}
                        })
                   } else {
                        cb && cb(false);
                        this.$Message.error('表单校验失败!');
                    }
                })
            },
            //重新加载数据
            getData:function () {
                this.$ajax.post("/service_user/post/findById", {"id": this.id})
                    .then((res) => {
                        this.post = res.data.data;
                        //加载树数据
                        if(this.post){
                            this.orgTreeData.some((j, i) => {
                                if (this.post.orgId == j.id){
                                    this.post.orgName = j.name;
                                    return true;
                                }else {
                                    return false;
                                }
                            })
                            this.orgIds = this.$utils.getTreeInParentsId(this.orgTreeData, this.post.orgId);
                        }
                     })
                    .catch((err) => {
                        if(!err.url){console.info(err);}
                   })
            },
            resetFields: function() {
                this.$refs.post.resetFields();
            },
            //接收父页面部门树
            loadOrgTree: function (orgTreeData){
                this.orgTreeData = orgTreeData;
                if (this.orgTreeData && this.orgTreeData.length > 0){
                    this.orgTree = this.$utils.Flat2TreeDataForCascader(this.orgTreeData);
                }
            }
        },
        created: function () {
            
        },
        mounted: function (){
            
        },
        watch:{
            id: function() {
                if (this.id) {
                    this.getData();
                }
            }
        },
    }
</script>
