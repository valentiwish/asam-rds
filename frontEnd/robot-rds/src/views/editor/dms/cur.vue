<template>
    <div>
        <div class="page-content">
            <Form :label-width="120" ref="dms" :model="dms"  :class="{'form-view':type=='view'}" :rules="ruleValidateType" @submit.prevent="submit">   
                <!-- <Form-item label="光通信站类型"  prop="dmsType">
                    <template v-if="type=='view'">{{dms.dmsType}}</template>
                    <Input v-else v-model.trim="dms.dmsType" placeholder="请输入光通信站名称，不超过30字" maxlength="30"></Input>
                </Form-item> -->
                <Form-item label="光通信站名称"  prop="dmsName">
                    <template v-if="type=='view'">{{dms.dmsName}}</template>
                    <Input v-else v-model.trim="dms.dmsName" placeholder="请输入光通信站名称，不超过30字" maxlength="30"></Input>
                </Form-item>
                <Form-item label="光通信站IP"  prop="dmsIp">
                    <template v-if="type=='view'">{{dms.dmsIp}}</template>
                    <Input v-else v-model.trim="dms.dmsIp" placeholder="请输入光通信站IP，不超过30字" maxlength="30"></Input>
                </Form-item>
                <Form-item label="光通信站点位"  prop="dmsPoint">
                    <template v-if="type=='view'">{{dms.dmsPoint}}</template>
                    <Input v-else v-model.trim="dms.dmsPoint" :disabled="true" placeholder="请输入光通信站点位，不超过30字" maxlength="30"></Input>
                </Form-item>
            </Form>
        </div>
    </div>
</template>
<script>
import fontIcon from "@/components/fontIcon"
export default {
    components: { fontIcon },
        props: ["id", "type"],
        data() {
            var that = this;
            return {
                dms: {
                    dmsType: '',
                    dmsName: '',
                    dmsIp: '',
                    dmsPoint:''
                },  
                ruleValidateType: {
                    dmsName: [
                        { required: true, type: 'string', message: '光通信站名称不能为空', trigger: 'blur' },
                        { validator: this.checkDmsName, trigger: 'blur' },
                    ],
                    dmsIp: [
                        { required: true, type: 'string', message: '光通信站IP不能为空', trigger: 'blur' },
                        { validator: this.checkIp, trigger: 'blur' },
                        // { validator: this.checkDmsIp, trigger: 'blur' },
                    ],
                    dmsPoint: [
                        { required: true, type: 'string', message: '光通信站点位不能为空', trigger: 'blur' },
                        // { validator: this.checkDmsPoint, trigger: 'blur' },
                    ],
                }
            }
        },
        methods: {
            checkIp:function(rule, value, callback){
                if(value){
                    var ph = /^(((\d)|([1-9]\d)|(1\d{2})|(2[0-4]\d)|(25[0-5]))\.){3}((\d)|([1-9]\d)|(1\d{2})|(2[0-4]\d)|(25[0-5]))$/;
                    if(!ph.test(value)){ 
                        callback("ip格式不正确");
                    } else {
                        callback();
                    }
                } 
            },
             //校验光通信站名称唯一性
             checkDmsName: function(rule, value, callback){
                if(value){
                    this.$ajax.post("/service_rms/dms/checkDmsName",{'dmsName': value, 'id': this.id})
                    .then((res) => {
                        if (res.data.data){
                            callback();
                        } else {
                            callback("光通信站名称已存在");
                        }
                    })
                    .catch((error) => {
                        callback('校验失败');
                    })
                } else {
                    callback("请输入光通信站名称");
                }
            },
             //校验光通信站IP唯一性
             checkDmsIp: function(rule, value, callback){
                if(value){
                    this.$ajax.post("/service_rms/dms/checkDmsIp",{'dmsIp': value, 'id': this.id})
                    .then((res) => {
                        if (res.data.data){
                            callback();
                        } else {
                            callback("光通信站名称IP已存在");
                        }
                    })
                    .catch((error) => {
                        callback('校验失败');
                    })
                } else {
                    callback("请输入光通信站IP");
                }
            },
             //校验光通信站点位唯一性
             checkDmsPoint: function(rule, value, callback){
                if(value){
                    this.$ajax.post("/service_rms/dms/checkDmsPoint",{'dmsPoint': value, 'id': this.id})
                    .then((res) => {
                        if (res.data.data){
                            callback();
                        } else {
                            callback("光通信站点位已存在");
                        }
                    })
                    .catch((error) => {
                        callback('校验失败');
                    })
                } else {
                    callback("请输入光通信站点位");
                }
            },
            save(cb) {
                var that=this;
                var dms=this.dms;
                this.$refs['dms'].validate((valid) => {
                    if (true == valid ) {
                        let scanner = this.scanner;
                        var url = "/service_rms/dms/save";
                        this.$ajax.post(url, {"data": JSON.stringify(this.dms)})
                            .then((res) => {
                                console.log(res)
                                if (res.data.code == "200") {
                                    cb && cb(true);
                                    this.$refs['dms'].resetFields();
                                    that.$Message.success('保存成功');
                                } else {
                                    that.$Message.error('保存失败');
                                    this.$emit("changeVal");
                                }                                
                            })
                            .catch((error) => {
                                if(!error.url){console.info(error);}
                            })
                    } else {
                        cb && cb(false);
                        this.$emit("changeVal");
                        this.$Message.error('表单校验失败!');
                    }
                })                
            },
            getData: function() {
            //获取此模块模块基本信息 根据id查询
                this.$ajax.post("/service_rms/dms/findById", { "id": this.id })
                    .then(res => {
                        this.dms = res.data.data;
                    })
                    .catch((error) => {
                        if (!error.url) { console.info(error); }
                    });
                },
            resetFields: function() {
                 this.$refs.dms.resetFields();
            },
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