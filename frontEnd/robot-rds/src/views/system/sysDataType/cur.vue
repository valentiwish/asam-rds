<template>
    <div>
        <Form :label-width="108" ref="sysDataType" :model="sysDataType" :class="{'form-view':type=='view'}" :rules="ruleValidateType" @submit.prevent="submit">
            <Form-item label="类型编码" prop="typeCode">
                <template v-if="type=='view'">{{sysDataType.typeCode}}</template>
                <Input v-else v-model.trim="sysDataType.typeCode" placeholder="请输入类型编码，由字母/下划线组成，不超过20个字符" maxlength="20">
                </Input>
            </Form-item>
            <Form-item label="类型名称" prop="typeName">
                <template v-if="type=='view'">{{sysDataType.typeName}}</template>
                <Input v-else v-model.trim="sysDataType.typeName" placeholder="请输入类型名称" maxlength="30">
                </Input>
            </Form-item>
        </Form>
    </div>
</template>
<script>
export default {
    name: 'index',
    props: ["id", "type"],
    data() {
        var that = this;
        return {
            old: '',
            type: '',
            id: '',
            sysDataType: {
                typeCode: '',
                typeName: '',
            },
            ruleValidateType: {
                typeCode: [
                    { required: true, message: '请输入编码类型', trigger: 'blur' },
                    { max: 100, message: '编码类型不超过100个字符', trigger: 'blur' },
                    {
                        validator: (rule, value, callback) => {
                            if (!/^[a-zA-Z_]+$/.test(value)) {
                                callback(new Error("由字母/下划线组成"));
                            } else {
                                callback();
                            }

                        },
                        trigger: 'blur'
                    },
                    { validator: this.checkTypeCode, trigger: 'blur' }
                ],
                typeName: [
                    { required: true, message: '请输入编码名称', trigger: 'blur' },
                    { max: 30, message: '不超过30字', trigger: 'blur' },
                    { validator: this.checkTypeName, trigger: 'blur' }
                ],
            },
        }
    },
    methods: {
        //新增界面的保存方法
        save(cb) {
            this.$refs['sysDataType'].validate((valid) => {
                if (valid) {
                    var sysDataTypeVO = this.sysDataType;
                    this.$ajax.post("/service_user/sysDataType/save", sysDataTypeVO)
                        .then((res) => {
                            if (res.data.code == "200") {
                                cb && cb(true);
                                this.$refs['sysDataType'].resetFields();
                                this.$Message.success('保存成功');
                            } else {
                                this.$Message.error('保存失败');
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
        //校验类型编码
        checkTypeCode: function(rule, value, callback) {
            if (value) {
                this.$ajax.post("/service_user/sysDataType/check", { 'code': value, 'id': this.id })
                    .then((res) => {
                        if (res.data) {
                            callback();
                        } else {
                            callback("类型编码已经存在");
                        }
                    })
                    .catch((error) => {
                        callback('校验类型编码失败');
                    })
            }
        },
        getData: function() {
            //获取此模块模块基本信息 根据id查询
            this.$ajax.post("/service_user/sysDataType/findById", { "id": this.id })
                .then(res => {
                    this.sysDataType = res.data.data;
                    this.old = this.sysDataType.typeCode;
                })
                .catch((error) => {
                    if (!error.url) { console.info(error); }
                });
        },
        resetFields: function() {
            this.$refs.sysDataType.resetFields();
        },
    },
    watch: {
        id: function() {
            if (this.id != "") {
                this.getData();
            }
        }
    }
}

</script>
