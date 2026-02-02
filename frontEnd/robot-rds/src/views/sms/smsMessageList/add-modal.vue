<template>
    <div>
        <Form :label-width="108" ref="smsMessageList" :model="smsMessageList" :rules="ruleValidate">
            <Form-item label="发送批次" prop="batchNo" >
                <div class="showinput">{{smsMessageList.batchNo}}</div>
            </Form-item>
            <Form-item label="手机号码" prop="phones" >
                <Input v-model.trim="smsMessageList.phones" placeholder="请输入11位手机号码" maxlength="11" />
            </Form-item>
            <Form-item label="短信模板" prop="tempId" >
                <Select v-model="smsMessageList.tempId" @on-change="chooseTemplate" placeholder="请选择短信模板" >
                    <Option v-for="obj in smsMessageTemplates" :value="obj.id" :key="obj.id">{{obj.name}}</Option>
                </Select>
            </Form-item>
            <Form-item label="参数" prop="parameNames" >
                <div class="showinput">{{smsMessageList.parameNames}}</div>
            </Form-item>
            <FormItem label="参数内容" prop="parameData" >
                <Input v-model.trim="smsMessageList.parameData" type="textarea" placeholder="请按顺序填写参数对应的值,多个以英文逗号分隔开" maxlength="500" />
            </FormItem>
            <Form-item label="参数数量" >
                <div class="showinput" >{{smsMessageList.parameNum}}</div>
            </Form-item>
            <FormItem label="短信内容" prop="content" >
                <div class="showinput">{{smsMessageList.content}}</div>
            </FormItem>
        </Form>
    </div>
</template>
<script>
import fontIcon from '@/components/fontIcon'

export default {
    components: {
        fontIcon
    },
    props: ["id"],

    data() {
        return {
            old: '',
            type: '',
            id: '',
            dataTypes: '',
            phonesCounts: '',
            smsMessageList: {
                id: '',
                batchNo: '',
                content: '',
                tempId: '',
                tempCode: '',
                tempName: '',
                parameNames: '',
                parameData: '',
                phones: '',
                sendData: '',
                sendStatus: '',
                sendUserId: '',
                sendUserName: '',
                sendUserOrg: '',
                snedUserPhone: '',
                updateTime: '',
                name: ''
            },
            smsMessageTemplate: {
                id: '',
                name: '',
                content: '',
                parameNames: '',
                parameNum: '',
                isValid: '',
                updateTime: '',
            },
            smsMessageTemplates: [],
            ruleValidate: {
                phones: [{
                        required: true,
                        message: '请输入手机号码',
                        trigger: 'blur'
                    },
                    {
                        validator: this.validatorphones,
                        trigger: 'blur'
                    }
                ],
                parameData: [{
                    required: true,
                    message: '请输入模板内容',
                    trigger: 'blur'
                }, {
                    validator: this.validatorcontent,
                    trigger: 'blur'
                }],
            },
        }
    },
    methods: {
        back: function() {
            this.$emit('changeVal', false);
        },
        //生成批次号
        createBatchNo: function() {
            let date = new Date().Format("yyyyMMddhhmmss");
            const a = Math.floor(Math.random() * 10000);
            this.smsMessageList.batchNo = date + a;
        },
        //获取当前登录用户信息
        // getAccountInfo: function() {
        //     this.$ajax.postJson("/service_sms/account/getAccountInfo")
        //         .then((res) => {
        //             this.smsMessageList.sendUserId = res.data.data.user.id;
        //             this.smsMessageList.sendUserName = res.data.data.user.userName;
        //             this.smsMessageList.sendUserOrg = res.data.data.user.orgName;
        //             this.smsMessageList.sendUserPhone = res.data.data.user.linkPhone;
        //         })
        // },
        //获取短信模板信息
        getSmsMessageTemplates: function() {
            this.$ajax.postJson("/service_sms/smsMessageTemplate/getSmsMessageTemplates")
                .then((res) => {
                    if (res.data.code == 200) {
                        this.smsMessageTemplates = res.data.data;
                    }
                })
        },
        //选择短信模板
        chooseTemplate: function(tempId) {
            if (tempId) {
                this.smsMessageTemplates.some((j,index) => {
                    if(j.id == tempId){
                       this.smsMessageTemplate = j; 
                       return true;
                    } else{
                        return false;
                    }
                })                
                this.smsMessageList.tempId = this.smsMessageTemplate.id;
                this.smsMessageList.tempCode = this.smsMessageTemplate.code;
                this.smsMessageList.tempName = this.smsMessageTemplate.name;
                this.smsMessageList.parameNames = this.smsMessageTemplate.parameNames;
                this.smsMessageList.parameNum = this.smsMessageTemplate.parameNum;
                this.smsMessageList.content = this.smsMessageTemplate.content;
            }
        },
        //校验手机号码格式是否正确
        validatorphones: function(rule, value, callback) {
            if (value) {
                if (value.indexOf(",") == -1) {
                    if (value.length == 11 &&
                        /^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$/.test(value)) {
                        callback();
                    } else {
                        var a = [];
                        a = value.split(",");
                        callback("请输入11位有效电话号码")
                    }
                } else {
                    var a = value.split(",");
                    for (var i = 0; i < a.length; i++) {
                        if (a[i].length == 11 &&
                            /^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$/.test(a[i])) {
                            callback();
                        } else {
                            callback("请输入11位有效电话号码");
                        }
                    }
                }
            }
        },
        //校验参数内容
        validatorcontent: function(rule, value, callback) {
            if (value) {
                this.smsMessageList.parameData = this.smsMessageList.parameData.replace(/，/g, ",");
                let content = this.smsMessageList.parameData.split(",");
                let paramas = this.smsMessageList.parameNames.split(",");
                if (content.length != 0 && paramas.length != 0) {
                    if (content.length != paramas.length) {
                        callback(new Error("请输入正确的参数内容"));
                    } else {
                        let a = this.smsMessageList.parameNames.split(",");
                        let b = this.smsMessageList.parameData.split(",");
                        let c = new Array(a.length + 1);
                        let d = new Array(a.length);
                        c[0] = this.smsMessageTemplate.content;
                        for (let i = 0; i < a.length; i++) {
                            d[i] = c[i].replace(a[i], b[i]);
                            c[i + 1] = d[i];
                        }
                        this.smsMessageList.content = c[c.length - 1];
                        callback();
                    }
                } else {
                    callback();
                }
            }
        },
        //发送短信
        send(cb) {
            this.loading = true;
            // this.smsMessageList.tempName = this.smsMessageList.name;
            this.$refs['smsMessageList'].validate((valid) => {
                if (valid) {
                    this.$ajax.postJson("/service_sms/SMS/sendMessages", this.smsMessageList)
                        .then((res) => {
                            this.loading = false;
                            if (res.data.code == 200) {
                                cb && cb(true);
                                // this.$refs['smsMessageList'].resetFields();
                                this.$Message.success('发送成功');
                            } else {
                                this.$Message.error('发送失败')
                            }
                        })
                } else {
                    this.$emit("changeVal");
                    this.$Message.error('表单校验失败!');
                }
            })
        },
        //保存草稿
        save(cb) {
            // this.smsMessageList.tempName = this.smsMessageList.name;
            this.$refs['smsMessageList'].validate((valid) => {
                if (valid) {
                    this.$ajax.postJson("/service_sms/SMS/save", this.smsMessageList,{'Content-Type': 'application/json;'})
                        .then((res) => {
                            if (res.data.code == 200) {
                                cb && cb(true);
                                // this.$refs['smsMessageList'].resetFields();
                                this.$Message.success('保存成功');
                            } else {
                                this.$Message.error('保存失败')
                            }
                        })
                } else {
                    this.$emit("changeVal");
                    this.$Message.error('表单校验失败!');
                }
            })
        },
        //获取短信模板信息
       /*  getData: function() {
            this.$ajax.postJson("/service_sms/smsMessageTemplate/findSmsMessageTemplateById", {
                    "id": this.id
                })
                .then((res) => {
                    this.smsMessageTemplate = res.data.data;
                })
        }, */
    },
    watch: {
        /* id: function() {
            if (this.id != "") {
                this.getData();
            }
        } */
    },

    mounted: function() {
        // this.getAccountInfo();
        this.getSmsMessageTemplates();
    }
}

</script>
<style>
.showinput {
    border-bottom: 1px solid #e3e3e3;
    width: 100%;
    height: 34px;
}

</style>
