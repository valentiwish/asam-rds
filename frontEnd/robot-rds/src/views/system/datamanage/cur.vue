<template>
    <div>
        <Form :label-width="100" ref="dataManage" :model="dataManage" :rules="ruleValidate" :class="{'form-view':type=='view'}" @submit.prevent="submit">
            <Form-item label="IP地址" prop="ip">
                <template v-if="type=='view'">{{dataManage.ip}}</template>
                <Input v-model.trim="dataManage.ip" placeholder="请输入IP地址" maxlength="50" v-else></Input>
            </Form-item>
            <Form-item label="端口" prop="port">
                <template v-if="type=='view'">{{dataManage.port}}</template>
                <InputNumber v-model.trim="dataManage.port" placeholder="请输入端口" maxlength="50" v-else></InputNumber>
            </Form-item>
            <Form-item label="用户名" prop="sysUserName">
                <template v-if="type=='view'">{{dataManage.sysUserName}}</template>
                <Input v-model.trim="dataManage.sysUserName" placeholder="请输入用户名" maxlength="50" v-else></Input>
            </Form-item>    
            <Form-item label="密码" prop="sysPassword">  
                <template v-if="type=='view'">{{dataManage.sysPassword}}</template>
                <Input v-model.trim="dataManage.sysPassword" placeholder="请输入密码" maxlength="50" v-else></Input>
            </Form-item>  
            <Form-item label="备份地址" prop="address" v-if="type=='copy'">
                <Input v-model.trim="dataManage.address" placeholder="请输入备份地址，以\\隔开" maxlength="50"></Input>
            </Form-item> 
            <Form-item label="数据库" prop="sysDataBase">  
                <Input v-model.trim="dataManage.sysDataBase" placeholder="请输入数据库" maxlength="50"></Input>
            </Form-item>
            <Form-item label="数据表" prop="tables" v-if="type=='copy'">  
                <Input v-model.trim="dataManage.tables" placeholder="请输入数据表" maxlength="50" ></Input>
            </Form-item>
            <Form-item label="选择文件"  v-if="type=='restore'">
                <Input v-model.trim="dataManage.address" placeholder="请输入sql文件所在地址，以\\隔开" maxlength="50"></Input>
                <!-- <webuploader :option="option" ref="webuploaderFile" @uploadFinished="uploadFinished" @deleteFile="deleteFile" @uploadSuccess="uploadSuccess">
                    <span slot="text">最多上传1个附件，要求：<br/>①大小不超过100M；格式为.sql；<br/></span>
                </webuploader>   -->          
            </Form-item>    
        </Form>
    </div>
</template>
<script>
import fileshow from '@/components/fileshow'
import webuploader from '@/components/webuploader'
    export default {
        props:["id", "type"],
        components: { webuploader ,fileshow},
        data() {
            return {   
                fileArr:[],    
                option: {
                    auto: true,
                    fileSingleSizeLimit: 100 * 1024 * 1024,
                    fileNumLimit: 1,
                    formData: {
                        "moudleName": "system", //定义大模块名称
                    },
                    accept: {
                        title: 'files',
                        extensions: 'sql',
                        mimeTypes: 'file/sql'
                    },
                    type:"select"
                },      
                dataManage: {
                    id: null,
                    ip: 'localhost',
                    port: '3306',
                    sysUserName: 'root',
                    sysPassword:'asam',
                    sysDataBase:'water_fk',
                    tables:'',
                    type:''
                },
                ruleValidate: {
                    ip: [
                        { required: true, message: '请输入IP地址', trigger: 'blur' },
                    ],
                    port: [
                        { required: true, type:'number',message: '请输入端口', trigger: 'blur' },
                    ],
                    userName: [
                        { required: true, message: '请输入用户名', trigger: 'blur' },
                    ],
                    password: [
                        { required: true, message: '请输入密码', trigger: 'blur' },
                    ],
                }
           }
        },
        methods: {    
            upload: function() {
                this.$refs.webuploaderFile.upload();
            },
            uploadSuccess: function(obj) {
                this.fileId = obj.response.data.id;
            },
            uploadFinished:function() {
                // this.saveData();
                /*if (flag) {
                    this.$Message.success('模板文件上传成功');
                } else {
                    this.$Message.error('模板文件上传失败');
                }*/
            },
            deleteFile: function(obj) {
                this.fileId = '';
            }, 
            save() {
                this.$refs['dataManage'].validate((valid) => {
                    if (valid) {     
                        this.dataManage.type = this.type;                   
                        var dataManage = this.dataManage;
                        this.$ajax.post("/service_user/dataManage/copy",dataManage)
                            .then((res) => {
                                if (res.data.code == "200") {
                                    this.$Message.success('数据库复制成功！');
                                    this.$emit("saveSuccess");
                                } else {
                                    this.$Message.error('复制出错，信息为' + data.msg);
                                }
                            })
                            .catch((error) => {
                                if (!error.url) { console.info(error); }
                            })
                    }
                })
            },
            update() {
                this.$refs['dataManage'].validate((valid) => {
                    if (valid) {     
                        this.dataManage.type = this.type;                   
                        var dataManage = this.dataManage;  
                        this.$ajax.post("/service_user/dataManage/restore",dataManage)
                        .then((res) => {
                            if (res.data.code == "200") {
                                this.$Message.success('数据库恢复成功！');
                                this.$emit("updateSuccess");
                            } else {
                                this.$Message.error('恢复出错，信息为' + data.msg);
                            }
                        }) 
                    }
                })
            },
            getData: function() {
                //获取基本信息 根据id查询
                this.$ajax.post("/service_user/dataManage/findById", { id: this.id })
                    .then(res => {
                        this.dataManage = res.data.data; 
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
