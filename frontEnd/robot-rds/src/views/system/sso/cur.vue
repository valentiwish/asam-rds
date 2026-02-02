<template>
    <div>
        <Form :label-width="100" ref="ssoService" :model="ssoService" :rules="ruleValidate" :class="{'form-view':type=='view'}" @submit.prevent="submit">
            <Form-item label="应用编号" prop="appid">
                <template v-if="type=='view'">{{ssoService.appid}}</template>
                <Input v-model.trim="ssoService.appid" placeholder="请输入应用编号" maxlength="50" v-else></Input>
            </Form-item>
            <Form-item label="应用密钥" prop="appsecret">
                <template v-if="type=='view'">{{ssoService.appsecret}}</template>
                <Input v-model.trim="ssoService.appsecret" placeholder="请输入应用密钥" maxlength="50" v-else></Input>
            </Form-item>
            <Form-item label="安全域名" prop="domain">
                <template v-if="type=='view'">{{ssoService.domain}}</template>
                <Input v-model.trim="ssoService.domain" placeholder="请输入安全域名，多个以,分割" maxlength="50" v-else></Input>
            </Form-item>    
            <Form-item label="服务商名称" prop="providername">  
                <template v-if="type=='view'">{{ssoService.providername}}</template>
                <Input v-model.trim="ssoService.providername" placeholder="请输入服务商名称" maxlength="50" v-else></Input>
            </Form-item>    
            <Form-item label="描述" prop="description">  
                <template v-if="type=='view'">{{ssoService.description}}</template>
                <div v-else>
                    <Input v-model.trim="ssoService.description" type="textarea" maxlength="255" placeholder="请输入描述，不超过255个字符"></Input>
                    <div class="str_count">{{ssoService.description ? ssoService.description.length : 0}}/255</div>
                </div>
            </Form-item>
        </Form>
    </div>
</template>
<script>
    export default {
        props:["id", "type"],
        data() {
            return {                
                ssoService: {
                    id: null,
                    appid: '',
                    appsecret: '',
                    domain: '',
                    description:'',
                    providername:''
                },
                ruleValidate: {
                    appid: [
                        { required: true, message: '请输入应用编号', trigger: 'blur' },
                        { max: 50, trigger: 'blur', message: '应用编号最多10个字符' },
                    ],
                    appsecret: [
                        { required: true, message: '请输入应用密钥', trigger: 'blur' },
                        { max: 50, trigger: 'blur', message: '应用密钥最多10个字符' },
                    ],
                    domain: [
                        { required: true, message: '请输入安全域名', trigger: 'blur' },
                        { max: 50, trigger: 'blur', message: '安全域名最多10个字符' },
                    ],
                    providername: [
                        { required: true, message: '请输入服务商名称', trigger: 'blur' },
                        { max: 50, trigger: 'blur', message: '服务商名称最多50个字符' },
                    ],
                    description: [
                        { max: 255, trigger: 'blur', message: '描述最多255个字符' },
                    ],
                }
           }
        },
        methods: {     
            save() {
                this.$refs['ssoService'].validate((valid) => {
                    if (valid) {
                        if (this.ssoService && this.ssoService.createTime){
                            this.ssoService.createTime = new Date(this.ssoService.createTime).Format("yyyy-MM-dd hh:mm:ss")
                        }
                        if (this.ssoService && this.ssoService.updateTime){
                            this.ssoService.updateTime = new Date(this.ssoService.updateTime).Format("yyyy-MM-dd hh:mm:ss")
                        }
                        var ssoVO = this.ssoService;
                        this.$ajax.post("/service_user/ssoService/save",ssoVO)
                            .then((res) => {
                                if (res.data.code == "200") {
                                    this.$Message.success('保存成功！');
                                    this.$emit("saveSuccess");
                                } else {
                                    this.$Message.error('保存出错，信息为' + data.msg);
                                }
                            })
                            .catch((error) => {
                                if (!error.url) { console.info(error); }
                            })
                    }
                })
            },
            getData: function() {
                //获取基本信息 根据id查询
                this.$ajax.post("/service_user/ssoService/findById", { id: this.id })
                    .then(res => {
                        this.ssoService = res.data.data; 
                    })
                    .catch((error) => {
                        if (!error.url) { console.info(error); }
                    });
            },
        },
        created: function() {
            if (this.id) {
                this.getData();
            }
        },
        watch:{
            
        },
    }
</script>
