<template>
    <div>
        <Form :label-width="100" ref="fileTemplate" :model="fileTemplate" :rules="ruleValidate" :class="{'form-view':type=='view'}" @submit.prevent="submit">
        <Form-item label="编号" prop="no">
            <template v-if="type=='view'">{{fileTemplate.no}}</template>
            <Input v-else v-model.trim="fileTemplate.no" placeholder="请输入模板编号，不超过20字"></Input>            
        </Form-item>
        <Form-item label="名称" prop="name">
            <template v-if="type=='view'">{{fileTemplate.name}}</template>
            <Input v-else v-model.trim="fileTemplate.name" maxlength="20" placeholder="请输入模板名称，不超过20字"></Input> 
        </Form-item>
        <Form-item label="分类" prop="templateTypeId">
            <template v-if="type=='view'">{{fileTemplate.templateTypeName}}</template>
            <Select v-else v-model="fileTemplate.templateTypeId" placeholder="请选择模板分类">
                <Option v-for="templateType in templateTypeList" :key="templateType.id" :value="templateType.id">{{templateType.textName}}</Option>
            </Select>
        </Form-item>       
        <Form-item label="说明">          
            <template v-if="type=='view'">{{fileTemplate.description}}</template>
            <Input v-else v-model.trim="fileTemplate.description" maxlength="150" placeholder="请输入模板说明，不超过150字"></Input> 
        </Form-item>
        <Form-item label="状态">          
             <template v-if="type=='view'">{{1 == fileTemplate.state ? "有效" : "无效"}}</template>             
             <Radio-group v-else v-model="fileTemplate.state" placeholder="请选择状态">
                <Radio :label=1>有效</Radio>
                <Radio :label=0>无效</Radio>
             </Radio-group>
        </Form-item>    
        <Form-item label="附件" >
            <div  v-if="type=='view'"> 
                 <fileshow :list="fileArr"></fileshow>
            </div>            
            <webuploader v-else :option="option" ref="webuploaderFile" @uploadFinished="uploadFinished" @deleteFile="deleteFile" @uploadSuccess="uploadSuccess">
                <span slot="text">最多上传1个附件，要求：<br/>①大小不超过10M；<br/></span>
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
                        extensions: 'gif,jpg,jpeg,bmp,png,xls,xlsx,doc,docx,pdf',
                        mimeTypes: 'file/gif, file/jpg, file/jpeg, file/bmp, file/png, file/xls, file/xlsx, file/doc, file/docx, file/pdf'
                    },
                    type:"select"
                },
                fileTemplate: {
                    id: '',
                    no: '',
                    name: '',
                    templateTypeId: '',
                    description: '',
                    state: 1,
                    fileId: ''
                },
                ruleValidate: {
                    no: [
                        { required: true, message: '请输入模板编号', trigger: 'blur' },
                        { validator: this.checkNoUnique, trigger: 'blur' },
                    ],
                    name: [
                        { required: true, message: '请输入模板名称', trigger: 'blur' },
                        { validator: this.checkNameUnique, trigger: 'blur' },
                    ],
                    "templateTypeId": [
                        { required: true,type:"number", message: '请选择模板分类', trigger: 'change' },
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
                    this.$refs['fileTemplate'].validate((valid) => {                                                    
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
                this.fileTemplate.fileId = this.fileId;    
                if (this.fileTemplate && this.fileTemplate.createTime){
                    this.fileTemplate.createTime = new Date(this.fileTemplate.createTime).Format("yyyy-MM-dd hh:mm:ss")
                }
                if (this.fileTemplate && this.fileTemplate.updateTime){
                    this.fileTemplate.updateTime = new Date(this.fileTemplate.updateTime).Format("yyyy-MM-dd hh:mm:ss")
                } 
                var fileTemplateVO = this.fileTemplate;
                this.$ajax.post("/service_user/fileTemplate/save", fileTemplateVO)
                    .then((res) => {
                        if (200 == res.data.code){
                            this.$refs['fileTemplate'].resetFields();
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
                    this.$ajax.post("/service_user/fileTemplate/findById", { id: this.id })
                    .then(res => {
                        this.fileTemplate = res.data.data;  
                        resolve(res.data.data);                      
                    })
                    .catch((error) => {
                        if (!error.url) { console.info(error); }
                        reject();
                    });
                })
                Promise.all([a]).then((data) => {
                    if (null != this.fileTemplate.fileId) {
                        this.$ajax.post("/service_file/getFileByIds", { ids: "'"+this.fileTemplate.fileId+"'" })
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
            checkNoUnique: function(rule, value, callback) {                
                this.$ajax.post("/service_user/fileTemplate/checkNo", { 'id': this.fileTemplate.id, 'no': value })
                    .then((res) => {
                        if (res.data.flag){
                            callback();
                        } else {
                            callback(res.data.msg);
                        }
                    },()=>{
                        callback('此模板编号不能使用！');
                    })
            },
            checkNameUnique: function(rule, value, callback) {                
                this.$ajax.post("/service_user/fileTemplate/checkName", { 'id': this.fileTemplate.id, 'name': value })
                    .then((res) => {
                        if (res.data.flag){
                            callback();
                        } else {
                            callback(res.data.msg);
                        }
                    },()=>{
                        callback('此模板名称不能使用！');
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
            this.fileTemplate.state = 1;
            this.id && this.getData();            
        },
        watch:{
        },
    }
</script>
