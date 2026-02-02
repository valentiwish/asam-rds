<template>
    <div>
        <Form :label-width="80" ref="smsMessageTemplate" :model="smsMessageTemplate" :rules="ruleValidate" :class="{'form-view':type=='view'}" @submit.prevent="submit">
            <Form-item label="模板名称" prop="name">
                <template v-if="type=='view'">{{smsMessageTemplate.name}}</template>
                <Input v-else v-model.trim="smsMessageTemplate.name" placeholder="请输入模板名称" maxlength="100" />
                <!-- 其中的smsMessageTemple.name是不是前端中entity中的是一个，不是
               是在Script模块里面的一个内容 -->
                <!-- <Input v-else v-model.trim="smsMessageTemplate.name" placeholder="请输入模版名称" maxlength="100"/> -->
            </Form-item>
            <Form-item label="模板编码" prop="code">
                <template v-if="type=='view'">{{smsMessageTemplate.code}}</template>
                <Input v-else v-model.trim="smsMessageTemplate.code" placeholder="模板编码格式：SMS_+9位数字" maxlength="100">
                </Input>
            </Form-item>
            <Form-item label="参数" prop="parameNames">
                <template v-if="type=='view'">{{smsMessageTemplate.parameNames}}</template>
                <Input v-else v-model.trim="smsMessageTemplate.parameNames" @on-blur="parameNamesChange" placeholder="请输入参数，参数之间用英文逗号隔开" maxlength="500">
                </Input>
            </Form-item>
            <Row>
                <Col span="12">
                <Form-item label="参数数量" prop="paramNum" style="width: 260px;">
                    <template v-if="type=='view'">{{smsMessageTemplate.parameNum}}</template>
                    <InputNumber v-else v-model.trim="smsMessageTemplate.parameNum" min="1" max="10" placeholder="请输入参数数量">
                    </InputNumber>
                </Form-item>
                </Col>
                <Col span="12">
                <FormItem label="启用状态" prop="isValid" style="width: 260px;">
                    <template v-if="type=='view'">{{smsMessageTemplate.isValid==1?"启用":"禁用"}}</template>
                    <Select v-else v-model="smsMessageTemplate.isValid" placeholder="请选择启用状态">
                        <Option v-for="obj in valid" :value="obj.id" :key="obj.id">{{obj.name}}</Option>
                    </Select>
                </FormItem>
                </Col>
            </Row>
            <Form-item label="模板内容" prop="content">
                <template v-if="type=='view'">{{smsMessageTemplate.content}}</template>
                <Input type="textarea" v-else v-model.trim="smsMessageTemplate.content" placeholder="请输入模板内容，名字用参数代替" maxlength="500">
                </Input>
            </Form-item>
        </Form>
    </div>
</template>
<script>

export default {
    components: {

    },
    props: ["id", 'type'],
    data() {
        return {
            old: '',
            type: '',
            id: '',
            dataTypes: '',
            smsMessageTemplate: {
                id: '',
                code:'',
                name: '',
                content: '',
                parameNames: '',
                parameNum: '',
                isValid: '',
                updateTime: '',
            },
            smsMessageTemplateTemp: {
                id: '',
                name: '',
                content: '',
                parameNames: '',
                parameNum: '',
                isValid: '',
                updateTime: '',
            },
            valid: [
                { 'name': '启用', 'id': 1 },
                { 'name': '禁用', 'id': 0 }
            ],
            // 表单校验
            ruleValidate: {
                name: [{
                        required: true,
                        message: '请输入模板名称',
                        trigger: 'blur'
                    },
                    {
                        validator: this.validatorName,
                        trigger: 'blur'
                    }
                ],
                code: [{
                        required: true,
                        message: '请输入模板编码',
                        trigger: 'blur'
                    },
                    { validator: this.checkCodeUnique, trigger: 'blur' }
                ],
                content: [{
                        required: true,
                        message: '请输入模板内容，不超过500字',
                        trigger: 'blur'
                    },
                ],
                parameNames: [{
                    required: true,
                    message: '请输入参数',
                    trigger: 'blur'
                }],
                // 将启用状态设置成为必选项
                zone: [{
                    required: true,
                    message: '请选择启用状态',
                    trigger: 'blur'
                }]
            },
        }
    },
    methods: {
        checkCodeUnique: function(rule, value, callback) {
            if (0 == rule) {
                callback("请输入模板编码");
            } else {
                if (!/^SMS[_][0-9]\d{8}$/.test(value)) {
                    callback(new Error("模板编码必须由以首字母以SMS_开头，后接9位数字组成的字符"));
                }
                this.$ajax.postJson("/service_sms/smsMessageTemplate/checkTmplCode", {
                        'id': this.id,
                        'code': value
                    }).then((res) => {
                        if (res.data.data == false) {
                            callback("该模板编码已经使用");
                        } else {
                            callback();
                        }
                    })
                    .catch((error) => {
                        callback('校验失败');
                    })
            }
        },
        parameNamesChange:function(obj){
            if(this.smsMessageTemplate.parameNames.indexOf(",")>-1){
                this.smsMessageTemplate.parameNum = this.smsMessageTemplate.parameNames.split(",").length;
            }else{
                this.smsMessageTemplate.parameNum = 1;
            }
        },
        validatorName: function(rule, value, callback) {
            if (this.type == 'add') {
                if (value) {
                    this.$ajax.postJson("/service_sms/smsMessageTemplate/checkNameUnique", {
                            'id': this.id,
                            "name": value
                        }).then((res) => {
                            if (res.data.data == false) {
                                callback('该模板名称已经使用');
                            } else {
                                callback();
                            }
                        })
                        .catch((error) => {
                            if (!error.url) {
                                callback('校验失败');
                            }
                        })
                }
            } else {
                callback();
            }
        },
        save(cb) {
            var obj = this.smsMessageTemplate;
            this.$refs['smsMessageTemplate'].validate((valid) => {
                if (valid) {
                    this.$ajax.postJson("/service_sms/smsMessageTemplate/save", obj)
                        .then((res) => {
                            if (res.data.code == "200") {
                                cb && cb(true);
                                this.$refs['smsMessageTemplate'].resetFields();
                                this.$Message.success('保存成功');
                            } else {
                                this.$Message.error(res.data.message);
                            }
                        })
                } else {
                    this.$emit("changeVal");
                    this.$Message.error('表单校验失败!');
                }


            })
        },
        //重新加载数据
        getData: function() {
            this.$ajax.postJson("/service_sms/smsMessageTemplate/findById", { "id": this.id })
                .then((res) => {
                    this.smsMessageTemplate = res.data.data;
                })
                .catch((error) => {
                    if (!error.url) {
                        console.info(error);
                    }
                })
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
<style scoped>
</style>
