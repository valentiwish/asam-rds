<template>
    <div>
        <Form :label-width="120" ref="profession" :model="profession" :rules="ruleValidate" :class="{'form-view':type=='view'}">
            <Form-item label="名称" prop="name">
                <template v-if="type=='view'">{{profession.name}}</template>
                <Input v-else v-model.trim="profession.name" placeholder="请输入名称，不超过30个字符" maxlength="50"></Input>
            </Form-item>

            <Form-item label="任职条件" class="singleline">
                <template v-if="type=='view'">{{profession.conditions}}</template>
                <Input v-else v-model.trim="profession.conditions"  maxlength="100" type="textarea" placeholder="请输入任职条件，不超过100个汉字..."></Input>
            </Form-item>
            
            <Form-item label="备注" class="singleline">
                <template v-if="type=='view'">{{profession.remark}}</template>
                <Input v-else v-model.trim="profession.remark"  maxlength="100" type="textarea" placeholder="请输入备注，不超过100个汉字..."></Input>
            </Form-item>
        </Form>
    </div>
</template>
<script>
    export default {
        props:["id","type"],
        data() {
            return {
                profession: { //工种
                    id: '',
                    name: '',
                    conditions: '',
                    remark: ''
                },
                ruleValidate: {
                     name: [
                        { required: true, message: '请输入名称', trigger: 'blur' },
                        { validator: this.checkName, trigger: 'blur' },
                    ],
               }
           }
        },
        methods: {
            //校验名称唯一性
            checkName: function(rule, value, callback){
                if(value){
                    this.$ajax.post("/service_user/profession/checkName",{'name': value, 'id': this.id})
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
                    callback("请输入职务名称");
                }
            },
            save(cb){
                this.$refs['profession'].validate((valid) => {
                    if (valid) {
                        if (this.profession && this.profession.createTime){
                            this.profession.createTime = new Date(this.profession.createTime).Format("yyyy-MM-dd hh:mm:ss");
                        }
                        if (this.profession && this.profession.updateTime){
                            this.profession.updateTime = new Date(this.profession.updateTime).Format("yyyy-MM-dd hh:mm:ss");
                        }
                        var humProfessionVO = this.profession;
                        this.$ajax.post("/service_user/profession/save", humProfessionVO)
                        .then((res) => {
                            if (200 == res.data.code){
                                cb && cb(true);
                                this.$refs['profession'].resetFields();
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
            getData:function() {
                this.$ajax.post("/service_user/profession/findById", {"id": this.id})
                    .then((res) => {
                        this.profession = res.data.data;
                     })
                    .catch((err) => {
                        if(!err.url){console.info(err);}
                   })
            },
            resetFields: function() {
                this.$refs.profession.resetFields();
            },
        },
        created: function() {
            
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
