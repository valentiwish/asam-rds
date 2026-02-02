<template>
    <div>
         <Form :label-width="108" ref="systemInfo" :model="systemInfo" :class="{'form-view':type=='view'}"
              :rules="ruleValidateType" @submit.prevent="submit">
            
            <Form-item label="系统名称" prop="name">
                <template v-if="type=='view'">{{systemInfo.name}}</template>
                <Input v-else v-model.trim="systemInfo.name" placeholder="请输入系统名称" maxlength="30">
                </Input>
            </Form-item>
            <Form-item label="系统编码" prop="appid">
                <template v-if="type=='view'">{{systemInfo.appid}}</template>
                <Input v-else v-model.trim="systemInfo.appid" placeholder="请输入应用编码" maxlength="20">
                </Input>
            </Form-item>
            <Form-item label="安全密钥" prop="appsecret">
                <template v-if="type=='view'">{{systemInfo.appsecret}}</template>
                <Input v-else v-model.trim="systemInfo.appsecret" placeholder="请输入密钥" maxlength="100">
                </Input>
            </Form-item>
            <Form-item label="链接地址" prop="systemUrl">
                <template v-if="type=='view'">{{systemInfo.systemUrl}}</template>
                <Input v-else v-model.trim="systemInfo.systemUrl" placeholder="请输入链接" maxlength="100">
                </Input>
            </Form-item>
            <Form-item label="安全域名" prop="domain">
                <template v-if="type=='view'">{{systemInfo.domain}}</template>
                <Input v-else v-model.trim="systemInfo.domain" placeholder="请输入域名" maxlength="50">
                </Input>
            </Form-item>
            <Form-item label="系统提供商" prop="providername">
                <template v-if="type=='view'">{{systemInfo.providername}}</template>
                <Input v-else v-model.trim="systemInfo.providername" placeholder="请输入系统提供商" maxlength="100">
                </Input>
            </Form-item>
            <!-- <Form-item label="是否启用" prop="enable">
                <template v-if="type=='view'">{{systemInfo.enable==1 ? '是' :'否'}}</template>
                <Radio-group v-else v-model="systemInfo.enable">
                    <Radio :label="1">是</Radio>
                    <Radio :label="0">否</Radio>
                </Radio-group>
            </Form-item> -->
            <Form-item label="是否新界面打开" prop="newWin">
                <template v-if="type=='view'">{{systemInfo.newWin==1 ? '是' :'否'}}</template>
                <Radio-group v-else v-model="systemInfo.newWin">
                    <Radio :label="1">是</Radio>
                    <Radio :label="0">否</Radio>
                </Radio-group>
            </Form-item>
            <Form-item label="系统logo">
                <div  v-if="type=='view'"> 
                     <fileshow :list="fileArr"></fileshow>
                </div>            
                <webuploader v-else :option="option" ref="webuploaderFile" @uploadFinished="uploadFinished" @deleteFile="deleteFile" @uploadSuccess="uploadSuccess">
                    <span slot="text">不超过1张（格式：gif，jpg，jpeg,png）<br/></span>
                </webuploader>     
            </Form-item>
            <Form-item label="排序" prop="sort">
                <template v-if="type=='view'">{{systemInfo.sort}}</template>
                <InputNumber :min="0" :max="100" v-else v-model="systemInfo.sort" placeholder="请输入排序"></InputNumber>
            </Form-item>
        </Form>
    </div>
</template>
<script>
import fileshow from '@/components/fileshow'
import webuploader from '@/components/webuploader'
import CONFIG from '@/config/index.js'
export default {
    name: 'index',
    components: { webuploader,fileshow },
    props: ["id", "type"],
    data() {
        var that = this;
        return {
            option: {
                auto: true,
                type:"select",
                btnText: "上传附件",
                accept: {
                    title: 'files',
                    extensions: 'gif,jpg,jpeg,png',
                    mimeTypes: 'file/gif,file/jpg,file/jpeg,file/png'
                },
                fileNumLimit: 1,
                formData: {
                    "moudleName": "sys"  //这是定义大模块名称
                },
            },
            fileArr:[],
            fileId: '',
            saveCb:null,
            systemInfo: {
                name: '',
                appid: '',
                appsecret: '',
                fileId: '',
                systemUrl: '',
                domain: 'all',
                providername: '西航',
                enable:1,
                sort:1,
                newWin:0,
                sort:1,
            },
            ruleValidateType: {
                appid: [
                    {required: true, message: '请输入应用编码', trigger: 'blur'},
                    {max: 100, message: '应用编码不超过100个字符', trigger: 'blur'},
                ],
                name: [
                    {required: true, message: '请输入系统名称', trigger: 'blur'},
                    {max: 30, message: '不超过30字', trigger: 'blur'},
                ],
                appsecret: [
                    {required: true, message: '请输入密钥', trigger: 'blur'},
                    {max: 100, message: '密钥不超过30字', trigger: 'blur'},
                ],
                systemUrl: [
                    {required: true, message: '请输入链接', trigger: 'blur'},
                    {max: 100, message: '链接不超过100字', trigger: 'blur'},
                ],
                domain: [
                    {
                        required: true, validator: (rule, value, callback) => {
                        if (value) {
                            if (value == 'all') {
                                callback();
                            } else {
                                var reg = new RegExp('^((http://)|(https://))?([a-zA-Z0-9]([a-zA-Z0-9\-]{0,61}[a-zA-Z0-9])?\.)+[a-zA-Z]{2,6}(/)');
                                if (reg.test(value) == true) {
                                    callback();
                                } else {
                                    callback(new Error("请输入合法的域名"));
                                }
                            }
                        } else {
                            callback(new Error("链接必须填写"));
                        }
                    }, trigger: 'blur'
                    },

                ],
                providername: [
                    {required: true, message: '请输入服务商', trigger: 'blur'},
                    {max: 100, message: '服务商不超过100字', trigger: 'blur'},
                ],
                status:[{required: true}]
            },
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
        },
        deleteFile: function(obj) {
            this.fileId = '';
        },
        //新增界面的保存方法
        save(cb) { 
            this.saveCb =  cb;                          
            if (this.$refs.webuploaderFile.fileList.length > 0) {
                this.$refs['systemInfo'].validate((valid) => {                                                    
                    if (valid) { 
                        this.saveData(); 
                    }else{
                        this.saveCb(false);
                        this.$Message.error('校验出错!');
                    }   
                })                
            } else {
                this.$refs['systemInfo'].validate((valid) => {                                                    
                    if (valid) { 
                        this.saveData();
                    }else{
                        this.saveCb(false);
                        this.$Message.error('校验出错!');
                    }   
                })
            }
        },
        saveData(){
            this.systemInfo.fileId = this.fileId; 
            var url = "/service_user/systemInfo/save";
            this.$ajax.post(url, {"data": JSON.stringify(this.systemInfo)})
                .then((res) => {
                    if (res.data.code == "200") {
                        this.$refs['systemInfo'].resetFields();
                        this.$Message.success('保存成功');
                        this.saveCb(true);
                    } else {
                        this.$Message.error('保存失败');
                        this.saveCb(false);
                    }
                },()=>{
                    this.$Message.error('保存失败');
                    this.saveCb(false);
                })
        },
        getData: function () {
            var a= new Promise((resolve, reject) => {
                //获取此模块模块基本信息 根据id查询
                this.$ajax.post("/service_user/systemInfo/findById", {"id": this.id})
                .then(res => {
                    this.systemInfo = res.data.data;
                    resolve(res.data.data);                
                })
                .catch((error) => {
                    if (!error.url) {
                        console.info(error);
                        reject();
                    }
                });
            })
            Promise.all([a]).then((data) => {
                if (null != this.systemInfo.fileId) {
                    
                    this.$ajax.post("/service_file/getFileByIds", { ids: "'"+this.systemInfo.fileId +"'"})
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
        }
    },
    created(){
        if (this.id) {
            this.getData();
        }
    },
}

</script>
