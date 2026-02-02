<template>
    <div>
        <Form :label-width="120" ref="variable" :model="variable" :rules="ruleValidate" :class="{'form-view':type=='view'}">
            <Form-item label="变量名称" prop="tagName">
                <template v-if="type=='view'">{{variable.tagName}}</template>
                <Input v-else v-model.trim="variable.tagName" placeholder="请输入变量名称，不超过30个字符" maxlength="50"></Input>
            </Form-item>

            <Form-item label="展示名称" prop="showName">
                <template v-if="type=='view'">{{variable.showName}}</template>
                <Input v-else v-model.trim="variable.showName" placeholder="请输入展示名称，不超过30个字符" maxlength="50"></Input>
            </Form-item>

            <Form-item label="数据类型" prop="type">
                <template v-if="type=='view'">{{variable.type}}</template>
                <Input v-else v-model.trim="variable.type" placeholder="请输入数据了类型，不超过30个字符" maxlength="50"></Input>
            </Form-item>

            <Form-item label="数据表名" prop="tabName">
                <template v-if="type=='view'">{{variable.tabName}}</template>
                <Input v-else v-model.trim="variable.tabName" placeholder="请输入数据表名，不超过30个字符" maxlength="50"></Input>
            </Form-item>

            <Form-item label="数据字段名" prop="colName">
                <template v-if="type=='view'">{{variable.colName}}</template>
                <Input v-else v-model.trim="variable.colName" placeholder="请输入数据字段名，不超过30个字符" maxlength="50"></Input>
            </Form-item>

            <Form-item label="BIM模型编码" prop="bimCode">
                <template v-if="type=='view'">{{variable.bimCode}}</template>
                <Input v-else v-model.trim="variable.bimCode" placeholder="请输入BIM模型编码，不超过30个字符" maxlength="50"></Input>
            </Form-item>

            <Form-item label="GIS展示编码" prop="gisName">
                <template v-if="type=='view'">{{variable.gisName}}</template>
                <Input v-else v-model.trim="variable.gisName" placeholder="请输入展示名称，不超过30个字符" maxlength="50"></Input>
            </Form-item>

            <Form-item label="寄存器地址" prop="regAddr">
                <template v-if="type=='view'">{{variable.regAddr}}</template>
                <Input v-else v-model.trim="variable.regAddr" placeholder="请输入寄存器地址，不超过30个字符" maxlength="50"></Input>
            </Form-item>
        </Form>
    </div>
</template>
<script>
    export default {
        props:["id", "type"],
        data() {
            return {
                variable: { //点变量参数
                    id: '',
                    tagName: '',
                    showName: '',
                    type: '',
                    tabName: '',
                    colName: '',
                    bimCode: '',
                    gisName: '',
                    regAddr: ''
                },
                ruleValidate: {
                    tagName: [
                        { required: true, message: '变量名称不能为空', trigger: 'blur' },
                        { validator: this.checkTagName, trigger: 'blur' },
                    ],
                    showName: [
                        { required: true, message: '展示名称不能为空', trigger: 'blur' },
                        { validator: this.checkShowName, trigger: 'blur' },
                    ],
               }
           }
        },
        methods: {
            //校验名称唯一性
            checkTagName: function(rule, value, callback){
                if(value){
                    this.$ajax.post("/service_basedata/variable/checkTagName", {'tagName': value, 'id': this.id})
                    .then((res) => {
                        if (res.data.flag){
                            callback();
                        } else {
                            callback(res.data.msg);
                        }
                    })
                    .catch((error) => {
                        callback('校验失败');
                    })
                } else {
                    callback("请输入职务名称");
                }
            },
            //校验展示名唯一性
            checkShowName: function(rule, value, callback){
                if(value){
                    this.$ajax.post("/service_basedata/variable/checkShowName", {'showName': value, 'id': this.id})
                    .then((res) => {
                        if (res.data){
                            callback();
                        } else {
                            callback("编号已存在");
                        }
                    })
                    .catch((error) => {
                        callback('校验失败');
                    })
                } else {
                    callback("请输入变量名称");
                }
            },
            save(cb){
                this.$refs['variable'].validate((valid) => {
                    if (valid) {
                        var scadaVariableVO = this.variable;
                        this.$ajax.post("/service_basedata/variable/save", scadaVariableVO)
                        .then((res) => {
                            if (200 == res.data.code){
                                cb && cb(true);
                                this.$refs['variable'].resetFields();
                                this.$Message.success('保存成功');
                            }else {
                                this.$Message.error('保存出错，错误信息：' + res.data.msg);
                            }
                        })
                        .catch((err) => {
                            cb && cb(false);
                            if(!err.url){console.info(err);}
                        })
                   } else {
                        cb && cb(false);
                        this.$Message.error('表单校验失败!');
                    }
                })
            },
            //重新加载数据
            getData:function() {
                this.$ajax.post("/service_basedata/variable/findById", {"id": this.id})
                    .then((res) => {
                        this.variable = res.data.data;
                     })
                    .catch((err) => {
                        if(!err.url){console.info(err);}
                   })
            },
            resetFields: function() {
                this.$refs.variable.resetFields();
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
