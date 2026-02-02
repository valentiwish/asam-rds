<template>
    <div>
        <Form :label-width="100" ref="mobileVersion" :model="mobileVersion" :rules="ruleValidate" :class="{'form-view':type=='view'}" @submit.prevent="submit">
            <Form-item label="类型" prop="type">
                <template v-if="type=='view'">{{mobileVersion.type}}</template>
                <Select v-model="mobileVersion.type" placeholder="请选择app类型" v-else>
                    <Option v-for="templateType in templateTypeList" :key="templateType.textName" :value="templateType.textName">{{templateType.textName}}</Option>
                </Select>
            </Form-item>
            <Form-item label="版本名称" prop="name">
                <template v-if="type=='view'">{{mobileVersion.name}}</template>
                <Input v-model="mobileVersion.name" placeholder="请输入版本名称" v-else></Input>
            </Form-item>
            <Form-item label="版本号" prop="number"> 
                <template v-if="type=='view'">{{mobileVersion.number}}</template>
                <Input v-model="mobileVersion.number" placeholder="请输入版本号" v-else></Input>
            </Form-item>
            <Form-item label="状态" prop="state">	
                <template v-if="type=='view'">{{mobileVersion.state==1 ? '有效' : '无效'}}</template>		
                <Radio-group v-model="mobileVersion.state" placeholder="请选择状态" v-else>
                    <Radio :label="1">有效</Radio>
                    <Radio :label="0">无效</Radio>
                </Radio-group>
            </Form-item>    
            <Form-item label="附件" >
                <div  v-if="type=='view'"> 
                    <fileshow :list="fileArr"></fileshow>
                </div>            
                <webuploader v-else :option="option" ref="webuploaderFile" @uploadFinished="uploadFinished" @deleteFile="deleteFile" @uploadSuccess="uploadSuccess">
                    <span slot="text">最多上传1个apk附件，要求：<br/>①大小不超过10M；格式为apk。<br/></span>
                </webuploader>            
            </Form-item>    
        </Form>
    </div>
</template>

<script>

import fileshow from '@/components/fileshow'
import webuploader from '@/components/webuploader'

    export default {
        props:["id", "type","templateTypeList"],
        components: { webuploader ,fileshow},
        data() {
            return {
                fileArr:[],
                fileId: '',
                option: {
                    auto: true,
                    fileSingleSizeLimit: 10 * 1024 * 1024,
                    fileNumLimit: 1,
                    formData: {
                        "moudleName": "system", //定义大模块名称
                    },
                    accept: {
                        title: 'files',
                        extensions: "apk",
                        mimeTypes: "file/apk"
                    },
                    type:"select"
                },
                mobileVersion: {
                    id: '',
                    name: '',
                    number: '',
                    type: '',
                    state: 1,
                    file: {
                        id: ''
                    }
                },
                ruleValidate: {
                    name: [
                        { required: true, message: '请输入版本名称', trigger: 'blur' },
                        { validator: this.checkVersionName, trigger: 'blur' },
                    ],
                    type: [
                        { required: true, message: '请选择版本类型', trigger: 'blur' },
                    ],
                    number: [
                        { required: true,  message: '请选择版本号', trigger: 'blur' },
                        // { validator: this.checkNumber, trigger: 'blur' },
                    ]
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
            isshow: function(item) {
                return this.operations.some(function(j, i) {
                    return item.id == j;
                })
            },
            save(cb) { 
                this.saveCb =  cb;                          
                if (this.$refs.webuploaderFile.fileList.length > 0) {
                    this.$refs['mobileVersion'].validate((valid) => {                                                    
                        if (valid) { 
                            this.saveData(); 
                            //cb && cb(true); 
                        }else{
                            this.saveCb(false);
                            this.$Message.error('校验出错!');
                        }   
                    })                
                } else {
                    this.$Message.error('上传附件不能为空!');
                    this.saveCb(false);
                }
            },
            saveData(){
                this.mobileVersion.fileId = this.fileId;    
                if (this.mobileVersion && this.mobileVersion.createTime){
                    this.mobileVersion.createTime = new Date(this.mobileVersion.createTime).Format("yyyy-MM-dd hh:mm:ss")
                }
                if (this.mobileVersion && this.mobileVersion.updateTime){
                    this.mobileVersion.updateTime = new Date(this.mobileVersion.updateTime).Format("yyyy-MM-dd hh:mm:ss")
                } 
                var mobileVersionVO = this.mobileVersion;
                this.$ajax.post("/service_user/mobileVersion/save", mobileVersionVO)
                    .then((res) => {
                        if (200 == res.data.code){
                            this.$refs['mobileVersion'].resetFields();
                            this.$Message.success('保存成功');
                            this.saveCb(true);
                        }else {
                            this.$Message.error('保存出错，错误信息：' + res.data.msg);
                            this.saveCb(false);
                        }
                    })  
            },
            getData: function() {
                var a= new Promise((resolve, reject) => {
                    // 获取基本信息 根据id查询
                    this.$ajax.post("/service_user/mobileVersion/findById", { id: this.id })
                    .then(res => {
                        this.mobileVersion = res.data.data;  
                        resolve(res.data.data);                      
                    })
                    .catch((error) => {
                        if (!error.url) { console.info(error); }
                        reject();
                    });
                })
                Promise.all([a]).then((data) => {
                    if (null != this.mobileVersion.fileId) {
                        this.$ajax.post("/service_file/getFileByIds", { ids: "'"+this.mobileVersion.fileId+"'" })
                        .then(res => {
                            if(res.data.code == "200"){
                                if(res.data.data.length > 0){
                                    var fileObj = res.data.data[0]; 
                                    if(this.type!="view"){
                                        this.$refs.webuploaderFile.addDefaultFile([{
                                            id: fileObj.id,
                                            name: fileObj.filename,
                                            type: fileObj.suffix
                                        }]);
                                    }else{
                                        this.fileArr = res.data.data.map((obj) => {
                                            if(obj != null){
                                                this.fileId = obj.id;
                                                var name = obj.filename;
                                                var suffix = name.substring(name.lastIndexOf(".") + 1, name.length);
                                                var fileType = "";
                                                if (suffix == "gif" || suffix == "jpg" || suffix == "jpeg" || suffix == "bmp" || suffix == "png") {
                                                    fileType = "image";
                                                }
                                                return {
                                                    "md5": obj.id,
                                                    id: obj.id,
                                                    name: obj.filename,
                                                    type: fileType,
                                                    "src": this.$utils.getFileShowUrl(obj.id),
                                                    url:this.$utils.getFileDownloadUrl(obj.id),
                                                    downloadurl:this.$utils.getFileDownloadUrl(obj.id),
                                                    showurl:this.$utils.getFileShowUrl(obj.id),
                                                };
                                            }                                        
                                        });
                                    }
                                }          
                            }                                                    
                        })
                        .catch((error) => {
                            if (!error.url) { console.info(error); }
                        });
                    }
                })
               
            },   
            getFileShowUrl: function() {
                return this.$utils.getFileShowUrl(fileName);
            },
            getFileDownloadUrl: function(fileName) {
                return this.$utils.getFileDownloadUrl(fileName);
            }
        },
        created: function() { 
            this.mobileVersion.state = 1;
            this.id && this.getData();            
        },
        watch:{
        },
    }
</script>
