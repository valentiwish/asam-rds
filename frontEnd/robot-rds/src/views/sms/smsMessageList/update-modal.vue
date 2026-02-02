<template>
    <div>
        <Form :label-width="108" ref="smsMessageList" :model="smsMessageList" :rules="ruleValidate" :class="{'form-view':type=='view'}">
            <Form-item label="发送批次" prop="batchNo">
                <template v-if="type=='view'">{{smsMessageList.batchNo}}</template>
                <div v-else class="showinput">{{smsMessageList.batchNo}}</div>
            </Form-item>
            <Form-item label="手机号码" prop="phones">
                <template v-if="type=='view'">{{smsMessageList.phones}}</template>
                <Input v-else v-model.trim="smsMessageList.phones" placeholder="请输入手机号码"/>
            </Form-item>
            <Form-item label="短信模板" prop="tempId">
                <template v-if="type=='view'">{{smsMessageList.tempName}}</template>
                <Select v-else v-model="smsMessageList.tempId" @on-change="chooseState" :placeholder="smsMessageList.tempName">
                    <Option v-for="obj in smsMessageTemplates" :value="obj.id" :key="obj.id">{{obj.name}}</Option>
                </Select>
            </Form-item>
            <Form-item label="参数" prop="parameNames">
                <template v-if="type=='view'">{{smsMessageList.parameNames}}</template>
                <div v-else class="showinput">{{smsMessageList.parameNames}}</div>
            </Form-item>
            <FormItem label="参数内容" prop="parameData">
                <template v-if="type=='view'">{{smsMessageList.parameData}}</template>
                <Input v-else v-model.trim="smsMessageList.parameData" type="textarea" placeholder="请按顺序填写参数对应的值,多个以逗号分隔开" maxlength="500" />
            </FormItem>
            <FormItem label="短信内容" prop="content">
                <template v-if="type=='view'">{{smsMessageList.content}}</template>
                <div v-else class="showinput">{{smsMessageList.content}}</div>
            </FormItem>
            <Form-item v-if="type=='view'" label="发送人" prop="sendUserName">
                <template>{{smsMessageList.sendUserName}}</template>
            </Form-item>
            <Form-item v-if="type=='view'" label="发送人单位" prop="sendUserOrg">
                <template>{{smsMessageList.sendUserOrg}}</template>
            </Form-item>
            <Form-item v-if="type=='view'" label="发送人电话" prop="sendUserPhone">
                <template>{{smsMessageList.sendUserPhone}}</template>
            </Form-item>
        </Form>
    </div>
</template>
<script>
import fontIcon from '@/components/fontIcon'

export default {
    components: {
        fontIcon
    },
    props: ["id", "type"],
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
                tempId: null,
                tempName: '',
                parameNames: '',
                parameData: '',
                phones: '',
                sendData: '',
                sendStatus: '',
                sendUserId: '',
                sendUserName: '',
                sendUserOrg: '',
                sendUserPhone: '',
                updateTime: '',
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
                }],
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
        //获取短信模板信息
        getSmsMessageTemplates: function() {
            this.$ajax.postJson("/service_sms/smsMessageTemplate/getSmsMessageTemplates")
                .then((res) => {
                    if (res.data.code == 200) {
                        this.smsMessageTemplates = res.data.data;
                    }
                },(error) => {
                    if (!error.url) {}
                })
        },
        //选择短信模板
        chooseState: function(tempId) {
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
                this.smsMessageList.tempName = this.smsMessageTemplate.name;
                this.smsMessageList.parameNames = this.smsMessageTemplate.parameNames;
            }
        },
        //校验手机号码格式是否正确
        validatorphones: function(rule, value, callback) {
            if (value) {
                if (value.indexOf(",") == -1) {
                    if (value.length == 11 &&
                        /^(((13[0-9]{1})|(15[0-9]{1})|(18[0-9]{1})|(14[0-9]{1})|)+\d{8})$/.test(value)) {
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
                            /^(((13[0-9]{1})|(15[0-9]{1})|(18[0-9]{1})|(14[0-9]{1})|)+\d{8})$/.test(a[i])) {
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
        send: function() {
            this.$refs['smsMessageList'].validate((valid) => {
                if (valid) {
                    this.$ajax.postJson("/service_sms/SMS/sendMessages", this.smsMessageList,{'Content-Type': 'application/json;'})
                        .then((res) => {
                            if (res.data.code == 200) {
                                this.$emit('changeVal', false);
                                this.$refs['smsMessageList'].resetFields();
                                this.$Message.success('发送成功');
                            } else {
                                this.$Message.error('发送失败');
                            }
                        })
                        .catch((error) => {
                            if (!error.url) {
                                console.info(error);
                            }
                        })
                }
            })
        },
        //保存草稿
        save: function(cb) {
            this.$refs['smsMessageList'].validate((valid) => {
                if (valid) {
                    this.$ajax.postJson("/service_sms/SMS/save", this.smsMessageList,{'Content-Type': 'application/json;'})
                        .then((res) => {
                            if (res.data.code == 200) {
                                cb && cb(true);
                                this.$refs['smsMessageList'].resetFields();
                                this.$Message.success('保存成功');
                            } else {
                                this.$Message.error('保存失败')
                            }
                        })
                        .catch((error) => {
                            if (!error.url) {
                                console.info(error);
                            }
                        })
                }
            })
        },
        //重新加载数据
        getData: function() {
            this.$ajax.postJson("/service_sms/SMS/findById", {
                    "id": this.id
                })
                .then((res) => {
                    this.smsMessageList = res.data.data;
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
    },
    created(){
         this.getSmsMessageTemplates();
    },
    mounted: function() {
        // this.getAccountInfo();
       
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
