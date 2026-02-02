<template>
    <div>
        <Form :label-width="120" ref="position" :model="position" :rules="ruleValidate" :class="{'form-view':type=='view'}">
            <Form-item label="名称" prop="name">
                <template v-if="type=='view'">{{position.name}}</template>
                <Input v-else v-model.trim="position.name" placeholder="请输入名称，不超过30个字符" maxlength="50"></Input>
            </Form-item>

            <Form-item label="职责" prop="duty">
                <template v-if="type=='view'">{{position.duty}}</template>
                <Input v-else v-model.trim="position.duty" placeholder="请输入职责，不超过30个字符" maxlength="50"></Input>
            </Form-item>

            <Form-item label="任职条件" class="singleline">
                <template v-if="type=='view'">{{position.conditions}}</template>
                <Input v-else v-model.trim="position.conditions"  maxlength="100" type="textarea" placeholder="请输入任职条件，不超过100个汉字..."></Input>
            </Form-item>
            
            <Form-item label="备注" class="singleline">
                <template v-if="type=='view'">{{position.remark}}</template>
                <Input v-else v-model.trim="position.remark"  maxlength="100" type="textarea" placeholder="请输入备注，不超过100个汉字..."></Input>
            </Form-item>
        </Form>
    </div>
</template>
<script>
    export default {
        props:["id","type"],
        data() {
            return {
                position: { //职务
                    id: '',
                    name: '',
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
                    this.$ajax.post("/service_user/position/checkName", {'name': value, 'id': this.id})
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
                this.$refs['position'].validate((valid) => {
                    if (valid) {
                        if (this.position && this.position.createTime){
                            this.position.createTime = new Date(this.position.createTime).Format("yyyy-MM-dd hh:mm:ss");
                        }
                        if (this.position && this.position.updateTime){
                            this.position.updateTime = new Date(this.position.updateTime).Format("yyyy-MM-dd hh:mm:ss");
                        }
                        var humPositionVO = this.position;
                        this.$ajax.post("/service_user/position/save", humPositionVO)
                        .then((res) => {
                            if (200 == res.data.code){
                                cb && cb(true);
                                this.$refs['position'].resetFields();
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
                this.$ajax.post("/service_user/position/findById", {"id": this.id})
                    .then((res) => {
                        this.position = res.data.data;
                     })
                    .catch((err) => {
                        if(!err.url){console.info(err);}
                   })
            },
            resetFields: function() {
                this.$refs.position.resetFields();
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
