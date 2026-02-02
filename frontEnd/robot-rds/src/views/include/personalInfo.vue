<template>
    <Modal v-model="showModal" title="个人信息" :draggable="false">
        <Form ref="user" :model="user" :rules="ruleValidate" :label-width="108" :class="{'form-view':type=='view'}" inline>
            <div  style="float:right;width:130px;height:130px;border-radius:5px;overflow:hidden;text-align: left;margin:15px 40px 5px 5px;"  :class="{'form-view':false}"  v-if="type=='view'">
                <img v-if="user.photo" style="width:130px;" :src="getFileShowUrl(user.photo)">   
                <img v-else style="width:130px;" :src="'./static/images/header/default-photo.jpg'">
            </div>
                            
            <div style="float:right;position:relative;margin:15px 40px 5px 5px;text-align: left;width:130px;height:130px;overflow:hidden;border-radius:5px;"  v-if="type!='view'">
                <span title="点击更换头像" @click="openDialog" style="cursor:pointer;text-align:center;">
                    <img v-if="uploadimgurl" style="width:130px;" :src="uploadimgurl">
                    <img v-else-if="user.photo" style="width:130px;" :src="getFileShowUrl(user.photo)">   
                    <img v-else style="width:130px;" :src="'./static/images/user_img.jpg'">
                    <div style="position:absolute;top:0;width:100%;padding:3px 0;background:rgba(255,255,255,0.6);font-size:14px;color:#000;">更换头像</div>
                </span>
                <webuploader :option="option" ref="webuploader" @fileQueued="fileQueued"  @deleteFile="removeFile" @uploadSuccess="uploadSuccess"  @uploadFinished="uploadFinished" style="display:none;"></webuploader>          	
            </div>
        <div style="margin-right:250px;">
            <!-- <Form-item label="工号"  class="form-view">
                <template >{{user.jobNumber}}</template>
                </Form-item> -->
                <Form-item label="姓名" prop="userName">
                    <template v-if="type=='view'">{{user.userName}}</template>
                    <Input v-else v-model="user.userName"  maxlength="20"  placeholder="请输入姓名">{{user.userName}}</Input>
                </Form-item>
                <Form-item label="性别" class="form-view">
                    {{user.sex}}
                </Form-item>
                <Form-item label="入职时间" class="form-view">
                    {{user.entryDate}}
                </Form-item>
        </div>

            <Form-item label="出生日期" class="form-view">
                {{user.birthday}}
            </Form-item>

            <Form-item label="手机号" prop="userPhone">
                <template v-if="type=='view'">{{user.userPhone}}</template>
                <Input v-else v-model="user.userPhone" placeholder="请输入手机号"  maxlength="11">{{user.userPhone}}</Input>
            </Form-item>

            <!-- <Form-item label="固定电话" prop="telephone">
                <template v-if="type=='view'">{{user.telephone}}</template>
                <Input v-else v-model="user.telephone"  maxlength="12" placeholder="请输入固定电话，由区号（以数字0开始）和电话号码中间加上“-”组成"></Input>
            </Form-item>
 -->
            <Form-item label="邮箱" prop="email">
                <template v-if="type=='view'">{{user.email}}</template>
                <Input v-else v-model="user.email" placeholder="请输入邮箱"  maxlength="30">{{user.email}}</Input>
            </Form-item>
        </Form>
        <div slot="footer">
            <!-- <Button type="success" @click="toUpdateForm()" v-if="type=='view'">编辑</Button> -->
            <!-- <Button type="success" @click="toViewForm()" v-else>查看</Button> -->

            <Button type="primary" @click="handleSubmit('user')" v-if="type!='view'" :loading="loading">保存</Button>
            <Button type="ghost" @click="showModal=false;type='view'">关闭</Button>
        </div>
    </Modal>
</template>

<script>
import webuploader from "@/components/webuploader.vue"
export default {
    components: {webuploader},
    data() {
        var that = this;
        return { 
            loading:false,
            //上传头像
            option: {
                accept: {
                    title: 'Images',
                    extensions: 'jpg,jpeg,png',
                    mimeTypes: 'image/jpg, image/jpeg, image/png',
                },
                //uploadAccept: this.uploadAccept,
                fileSingleSizeLimit: 10 * 1024 * 1024,
                fileNumLimit: 100,
                formData: {
                    "moudleName": "human" //这是定义大模块名称
                },
                beforeFileQueued:that.beforeFileQueued
            },
            uploadimgurl:null,//上传文件生成的缩略图
                
            type:'view',  // type=update表示修改；type=view表示查看
            showModal:false,
            id: '', // 业务ID，修改或者删除的时候，传递主键值
            sexs: [],
            userOrg: [],
            orgTree: [],
            orgTreeData: [],
            minVal: 0,
            maxVal: 50,
            stepVal: 1,		
            // 基本信息
            positions: [],
            professions: [],
            posts: [],                
            user: {
                jobNumber: '',
                userName: '',
                cardID: '',
                sex: { id: '' },
                birthday: '', 
                userPhone: '',
                telephone: '',
                email: '',
                state: '1',
                photo: { id: '' }
            },
            ruleValidate: { // 员工管理
                jobNumber: [
                    { required: true, max: 10, message: '请输入工号，最多不超过10个字符', trigger: 'blur' }
                ],                 
                userName: [
                    { required: true, message: '请输入姓名，最多不超过20个字符', trigger: 'blur' }
                ],
                cardID: [
                    { required: true, message: '请输入身份证号', trigger: 'blur' },
                    {
                        type: 'string',
                        validator: function(rule, value, callback) {
                            if (!value) {
                                callback(new Error("请输入身份证号"));
                            } else if (/(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/.test(value) == false) {
                                callback(new Error("请输入正确的身份证号"));
                            } else {
                                callback();
                            }
                        },
                        trigger: 'blur'
                    },
                    { type: 'string', validator: this.checkCardIDIsExist, trigger: 'blur' }
                ],
                'sex.id': [
                    { type: 'number', message: '请选择性别', trigger: 'change' }
                ],
                birthday: [
                    { required: true, type: "string", message: '请选择出生日期', trigger: 'change' }
                ],                     
                userPhone: [
                    { required: true,  message: '请输入电话，最多不超过11个字符', trigger: 'blur' },
                    // { type: 'string', pattern: /^((0\d{2,3}-\d{7,8})|(1[35847]\d{9}))$/, message: '请输入有效的手机号码', trigger: 'blur' },
                    { type: 'string', validator: this.checkPhone, trigger: 'blur' }
                ],                    					
                telephone: [
                    { message: '请输入固定电话', trigger: 'blur' },
                    { type: 'string', pattern: /0\d{2,3}-\d{7,8}/, message: '请输入固定电话，由区号和电话号码中间加上“-”组成，区号以数字0开始', trigger: 'blur' },
                ],
                email: [
                    {  message: '邮箱不能为空', trigger: 'blur' },
                    {  type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
                ]
                
            },                
            limitCardDate: {
                disabledDate(date) {
                    return date && date.valueOf() > Date.now();
                }
            }
        }
    },
    methods: {
        getFileShowUrl(){
            return "";
        },
        openDialog: function() {
            this.$refs.webuploader.open_dialog();
        },
        beforeFileQueued:function(){
            //清空上传队列
            var arr = this.$refs.webuploader.uploader.getFiles();
            arr.map((j,i)=>{
                this.$refs.webuploader.uploader.removeFile(j);
            })
        },
        //当头像加入队列时生成缩略图
        fileQueued: function(obj) {
            var msg = this.$Message.loading({
                content: '正在读取图片...',
                duration: 0
            });
            this.$refs.webuploader.uploader.makeThumb(obj.file, (error, src) => {
                msg();
                this.$Message.success('读取图片信息成功！');
                if(!error){
                    this.uploadimgurl = src;
                }
            }, 1, 1);
        },
        //上传头像
        upload: function() {
            this.$refs.webuploader.upload();
        },
        uploadFinished: function(obj) {
            if(!obj.error){
                this.sureSaveUser();
            }
            else{
                this.loading =false;
            }
            
        },
        uploadSuccess: function(obj) {
            this.user.photo = {};
            this.user.photo.id = obj.response.data.id;
        },
        /*uploadAccept: function(obj) {
            if (obj.response.code=="200") {                           
                return true;
            } else {
                return false;
            }
        },    */
        removeFile: function(obj) {
            if (obj.file.id != null) {
                this.deleteFileId = obj.file.id;
            }
        },
        
        birthdaychange:function(a,b){
            this.user.birthday = a;
        },
        openModal: function() {
            this.showModal = true;
        },
        closeModal: function() {
            this.showModal = false;
        },
        //确定保存员工信息
        sureSaveUser() {
            if (null != this.user.birthday) {
                this.user.birthday = new Date(this.user.birthday).Format('yyyy-MM-dd');
            }                        
            if (null != this.user.entryTime) {
                this.user.entryTime = new Date(this.user.entryTime).Format('yyyy-MM-dd');
            }
            if (null != this.user.formalTime) {
                this.user.formalTime = new Date(this.user.formalTime).Format('yyyy-MM-dd');
            }
            if (null != this.user.departureTime) {
                this.user.departureTime = new Date(this.user.departureTime).Format('yyyy-MM-dd');
            }
            // 保存包括员工基础信息             
            this.$ajax.post('/service_user/human/updateUserInfo', {user:this.user})
            .then(res => {
                this.$Message.success('保存成功');
                this.type = 'view';
                this.getUserInfoByTkId();
                this.loading = false;

                //调用事件总线  用户信息修改  userinfochange
                Bus.$emit("userinfochange",{});
            },()=>{
                this.loading = false;
            });			
        },
        handleSubmit(name) {
            if(this.loading){
                return false;
            }
            this.loading = true;
            this.$refs['user'].validate((valid) => {
                if (valid) {
                    if (this.$refs.webuploader.uploader.getFiles().length == 0) {
                        this.sureSaveUser();
                    } else {
                        this.upload();
                    }
                } else {
                    this.$Message.error('个人信息表单校验失败!');
                    this.loading = false;
                }
            });    
        },
        checkCardIDIsExist: function(rule, value, callback) {
            this.$ajax.post("/service_user/human/checkCardIDIsExist", { "cardID": value, "id": this.user.id })
                .then((res) => {
                    if (res.data.code == "200") {
                        if (res.data.data) {
                            callback();
                        } else {
                            callback("此身份证号已存在");
                        }
                    }
                })
                .catch(() => {
                    callback("此身份证号不能使用");
                })
        },
        checkPhone: function(rule, value, callback) {
            this.$ajax.post("/service_user/human/checkPhoneIsExist", { "phone": value, "id": this.user.id })
                .then((res) => {
                        if (res.data.code == "200") {
                        if (res.data.data) {
                            callback();
                        } else {
                            callback("此手机号已存在");
                        }
                    }
                })
                .catch(() => {
                    callback("此手机号不能使用");
                })
        },
        /*
        checkTelephone: function (rule, value, callback) {
            var uPattern = /0\d{2,3}-\d{7,8}/;
            if (!uPattern.test(this.user.telephone)) {
                callback('请输入固定电话，由区号和电话号码中间加上“-”组成，区号以数字0开始');
            } else {
                callback();
            }
        },
        */
        //根据身份证号计算性别
        calculateGender: function() {
            var value = this.user.cardID;
            var size = value.length;
            var index = 6; // 年份所在位置
            var year = null;
            if (18 == size) {
                year = value.substring(index, index + 4);
                index = index + 4;
            } else {
                year = value.substring(index, index + 2);
                index = index + 2;
            }
            var month = value.substring(index, index + 2);
            var day = value.substring(index + 2, index + 4);
            // 根据身份证号码，获取年龄
            var birthday = new Date(year + "-" + month + "-" + day).Format("yyyy-MM-dd");
            this.user.birthday = birthday;
            // 根据身份证获取性别
            var cardIDSex = parseInt(value.substring(size - 2, size - 1)) % 2;
            if (1 == cardIDSex) {
                this.user.sex.id = this.sexs[0].id;
            } else {
                this.user.sex.id = this.sexs[1].id;
            }
            //根据身份证获取年龄
            var nowDateTime = new Date();
            var birthDate = new Date(birthday);
            var age = nowDateTime.getFullYear() - birthDate.getFullYear();
            //再考虑月、天的因素;.getMonth()获取的是从0开始的，这里进行比较，不需要加1
            if (nowDateTime.getMonth() < birthDate.getMonth() || (nowDateTime.getMonth() == birthDate.getMonth() && nowDateTime.getDate() < birthDate.getDate())) {
                age--;
            }
            this.user.age = age; 
        },
        getUserInfoByTkId() {
            var ret = [];
            this.$ajax.post("/service_user/human/getUserInfoByTkId")
                .then(res => {
                    if(res.data.code===200){
                        this.user = res.data.data;
                        /*
                        ret = this.$utils.getTreeParentsId(this.orgTreeData, res.data.data.user.org);
                        ret.push(res.data.data.user.org + '');
                        this.user.org = ret;
                        */
                        if (null != this.user.birthday) {
                            this.user.birthday = new Date(this.user.birthday).Format('yyyy-MM-dd');
                        }                        
                        if (null != this.user.entryTime) {
                            this.user.entryTime = new Date(this.user.entryTime).Format('yyyy-MM-dd');
                        }
                        if (null != this.user.formalTime) {
                            this.user.formalTime = new Date(this.user.formalTime).Format('yyyy-MM-dd');
                        }
                        if (null != this.user.departureTime) {
                            this.user.departureTime = new Date(this.user.departureTime).Format('yyyy-MM-dd');
                        }
                    }
                })
        },
        toUpdateForm() {
            this.type = 'update';
            this.getUserInfoByTkId();
        },
        toViewForm() {
            this.type = 'view';
            this.getUserInfoByTkId();
            if(null != this.user.birthday) {
                this.user.birthday = new Date(this.user.birthday).Format("yyyy-MM-dd");
            }                
        },
        getOrgTree: function() {
            this.$ajax.post("/service_user/organization/getOrgTree", {"parentId": 1})
                .then(res => {
                    if (null != res.data.data) {
                        this.orgTree = this.$utils.Flat2TreeDataForCascader(res.data.data);
                        this.orgTreeData = res.data.data;
                    }
                })
                .catch((error) => {
                    if (!error.url) { console.info(error); }
                })
        },
        getUserParam: function() {
            this.$ajax.post("/service_user/sysData/findByTypeCode", {'dataTypeCode': "USER_SEX" }).then(res => {
                if (null != res.data.data) {
                    this.sexs = res.data.data;
                }
            }).catch((error) => {
                if (!error.url) { console.info(error); }
            })
        }
    },
    created: function() {
        
    },
    watch:{
        showModal:function(n){
            if(n){
                    // 获取组织机构树形结构
                this.getOrgTree();

                // 获取用户相关参数信息
                this.getUserParam();
                this.getUserInfoByTkId();
            }
        }

    }
}
</script>

<style scoped>
    
</style>
