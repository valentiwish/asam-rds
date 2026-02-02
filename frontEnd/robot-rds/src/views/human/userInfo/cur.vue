<template>
    <div>
        <Form ref="user" :model="user"  :label-width="125" :class="{'form-view':type=='view'}"  :rules="ruleValidate" inline>
            <Card>
                <h2 slot="title">基本信息</h2>
                <div style="float:right;text-align: left;width:400px;"   v-if="type=='view'">
                    <img v-if="user.photo" style="width:130px;" :src="$utils.getFileShowUrl(user.photo)">
                    <img v-else style="width:130px;" src="static/img/header/default-photo.jpg">
                </div>
                <div style="float:right;text-align: left;min-height:130px;width:400px;"  v-if="type!='view'">
                        <span title="点击更换头像" @click="openDialog" style="cursor:pointer;text-align:center;">
                            <img v-if="uploadimgurl" style="width:110px;" :src="uploadimgurl">
                            <img v-else-if="user.photo" style="width:120px;" :src="$utils.getFileShowUrl(user.photo)">
                            <img v-else style="width:130px;" src="static/images/header/user_img.jpg">
                        </span>
                    <webuploader :option="option" ref="webuploader"  @fileQueued="fileQueued"  @deleteFile="removeFile" @uploadSuccess="uploadSuccess"  @uploadFinished="uploadFinished" style="display:none;"></webuploader>
                </div>

                <Form-item label="姓名" prop="userName">
                    <template v-if="type=='view'">{{user.userName}}</template>
                    <Input v-else v-model="user.userName" maxlength="50" placeholder="请输入姓名，最多不超过50个字符"></Input>
                </Form-item>

                <Form-item label="账号" prop="userCode">
                    <template v-if="type=='view'">{{user.userCode}}</template>
                    <Input v-else v-model="user.userCode" maxlength="50" placeholder="请输入账号，最多不超过50个字符"></Input>
                </Form-item>

                <Form-item label="手机号" prop="userPhone">
                    <template v-if="type=='view'">{{user.userPhone}}</template>
                    <Input v-else v-model="user.userPhone"  maxlength="11"   placeholder="请输入手机号，不超过11个字符"></Input>
                </Form-item>

                <Form-item label="人员类型" prop="isStaff">
                    <template v-if="type=='view' && 1==user.isStaff">公司员工</template>
                    <!-- <template v-if="type=='view' && 2==user.isStaff">运输队管理员</template>
                    <template v-if="type=='view' && 3==user.isStaff">司机</template> -->
                    <RadioGroup v-else-if="type!='view'" v-model="user.isStaff">
                        <Radio :label=1>公司员工</Radio>
                        <!-- <Radio :label=2>运输队管理员</Radio>
                        <Radio :label=3>司机</Radio> -->
                    </RadioGroup>
                </Form-item>

                <Form-item v-if="2==user.isStaff||3==user.isStaff" label="所属运输公司" prop="companyId">
                    <template v-if="type=='view'">{{user.companyName}}</template>
                    <Select v-else v-model="user.companyId" placeholder="请选择所属运输公司">
                        <Option v-for="item in companys" :value="item.id" :key="item.id">{{item.name}}</Option>
                    </Select>
                </Form-item>

                <!-- <Form-item v-if="1==user.isStaff" label="工号" prop="jobNumber">
                    <template v-if="type=='view'">{{user.jobNumber}}</template>
                    <Input v-else v-model="user.jobNumber" maxlength="10" placeholder="请输入工号，最多不超过10个字符"></Input>
                </Form-item> -->

                <Form-item label="性别" prop="sex">
                    <template v-if="type=='view'">{{user.sex}}</template>
                    <RadioGroup  v-else v-model="user.sex">
                        <Radio label="男"  checked></Radio>
                        <Radio label="女"></Radio>
                    </RadioGroup>
                </Form-item>

                <Form-item v-if="1==user.isStaff" label="所在部门" prop="orgModelIds">
                    <template v-if="type=='view'">{{user.orgName}}</template>
                    <Cascader v-else :data="orgModelTree" v-model="orgModelIds" placeholder="请选择部门" change-on-select></Cascader>
                </Form-item>

                <Form-item label="出生日期" prop="birthday">
                    <template v-if="type=='view'">{{user.birthday ? new Date(user.birthday).Format('yyyy-MM-dd') : ''}}</template>
                    <DatePicker v-else type="date" placeholder="请选择出生日期" :options="limitCardDate" v-model="user.birthday"></DatePicker>
                </Form-item>
                <!-- <br v-if="2==user.isStaff"/> -->                
                <!-- <br v-if="1==user.isStaff"/> -->

                <Form-item label="邮箱" prop="email">
                    <template v-if="type=='view'">{{user.email}}</template>
                    <Input v-else v-model="user.email"  maxlength="30"   placeholder="请输入邮箱"></Input>
                </Form-item>
                <Form-item label="入职时间" prop="entryDate">
                    <template v-if="type=='view'">{{user.entryDate ? new Date(user.entryDate).Format('yyyy-MM-dd') : ''}}</template>
                    <DatePicker v-else type="date" placeholder="请选择入职时间" :options="limitCardDate" v-model="user.entryDate"></DatePicker>
                </Form-item>
            </Card>
            <Card>
                <h2 slot="title">其他信息</h2>
                <Form-item label="政治面貌" prop="politicsFaceId">
                    <template v-if="type=='view'">{{user.politicsFaceName}}</template>
                    <Select v-else v-model="user.politicsFaceId" placeholder="请选择政治面貌">
                        <Option v-for="obj in politicsType" :value="obj.id" :key="obj.id">{{obj.textName}}</Option>
                    </Select>
                </Form-item>
                <!-- <Form-item label="婚姻" prop="marryState">
                    <template v-if="type=='view'">{{0 == user.marryState ? '未婚': (1 == user.marryState ? '已婚' : '未知')}}</template>
                    <Select v-else v-model="user.marryState" placeholder="请选择婚否">
                        <Option value="0">未婚</Option>
                        <Option value="1">已婚</Option>
                        <Option value="2">未知</Option>
                    </Select>
                </Form-item> -->
                <!-- <Form-item label="户口类型" prop="residenceType">
                    <template v-if="type=='view'">{{user.residenceType}}</template>
                    <Select v-else v-model="user.residenceType" placeholder="请选择户口类型">
                        <Option value="城镇">城镇</Option>
                        <Option value="农业">农业</Option>
                        <Option value="其他">其他</Option>
                    </Select>
                </Form-item> -->

                <Form-item label="身份证号" prop="cardID">
                    <template v-if="type=='view'">{{user.cardID}}</template>
                    <Input v-else v-model="user.cardID"  maxlength="18"  placeholder="请输入身份证号，确保18位字符"></Input>
                </Form-item>

                <Form-item label="紧急电话" prop="telephone">
                    <template v-if="type=='view'">{{user.telephone}}</template>
                    <Input v-else v-model="user.telephone"  maxlength="11"  placeholder="请输入紧急备用电话"></Input>
                </Form-item>
<!-- 
                <Form-item label="微信号" prop="weixinCode">
                    <template v-if="type=='view'">{{user.weixinCode}}</template>
                    <Input v-else v-model="user.weixinCode"  maxlength="50"  placeholder="请输入微信号"></Input>
                </Form-item> -->

                <!-- <Form-item label="籍贯" prop="nativePlace">
                    <template v-if="type=='view'">{{user.nativePlace}}</template>
                    <Input v-else v-model="user.nativePlace" maxlength="15"  placeholder="请输入籍贯"></Input>
                </Form-item> -->

                <Form-item label="家庭地址" prop="accountLocation" class="singleline" >
                    <template v-if="type=='view'">{{user.accountLocation}}</template>
                    <Input v-else v-model="user.accountLocation" maxlength="100" type="textarea" placeholder="请输入家庭地址，最多不超过100个字符"></Input>
                </Form-item> 

                <!-- <Form-item label="备注" prop="remark" class="singleline">
                    <template v-if="type=='view'">{{user.remark}}</template>
                    <Input v-else v-model.trim="user.remark"  maxlength="100" type="textarea"  placeholder="请输入备注，不超过100字"></Input>
                </Form-item> -->
            </Card>
            <div style="text-align:center;">
                <Button type="primary" @click="save()" v-if="type!='view'"  :loading="loading">保存</Button>
                <Button type="ghost" @click="$router.back()" v-if="type=='view'">返回</Button>
                <Button type="ghost" @click="$router.back()" v-else style="margin-left: 8px">取消</Button>
            </div>
        </Form>
    </div>
</template>
<script>
    import richEditor from "@/components/richEditor"
    import webuploader from "@/components/webuploader"
    import fileshow from "@/components/fileshow"

    export default {
        components: { richEditor,webuploader,fileshow},
        data() {
            var that = this;
            return {
                loading:false,
                //上传头像
                option: {
                    auto: false,
                    accept: {
                        title: 'Images',
                        extensions: 'jpg,jpeg,png',
                        mimeTypes: 'image/jpg,image/jpeg,image/png',
                    },
                    fileSingleSizeLimit: 10 * 1024 * 1024,
                    fileNumLimit: 1,
                    formData: {
                        "moudleName": "human" //这是定义大模块名称
                    },
                    beforeFileQueued:that.beforeFileQueued
                },
                uploadimgurl:null,//上传文件生成的缩略图
                id: '', // 业务ID，修改或者删除的时候，传递主键值
                old:'',
                oldUserPhone:'',
                orgModelIds:[],
                orgModelTree:[],
                orgModelTreeData:[],
                type: '', // type=update表示修改；type=view表示查看
                politicsType: [],//政治面貌
                companys: [],  //公司下拉数据
                //员工基本信息
                user: {
                    id:"",
                    isStaff: 1,  //是否本公司人员
                    companyId: '',  //所属公司
                    userName: '',
                    userCode: '',
                    jobNumber: '',
                    companyName: '',
                    sex: '男',
                    marryState: '',
                    birthday: null,
                    linkPhone: '',
                    email: '',
                    orgId: '',
                    orgName: '',
                    politicsFaceId: '',
                    residenceType: '',
                    cardID: '',
                    telephone: '',
                    // weixinCode: '',
                    faxCode: '',
                    accountLocation: '',
                    nativePlace: '',
                    photo:'',
                    remark: '',
                    entryDate:null
                },
                //校验人员信息
                ruleValidate: {
                   /* jobNumber: [
                        { required:true, message: '请输入工号', trigger: 'blur' },
                        { max: 10, message: '编码类型不超过10个字符', trigger: 'blur' },
                        {
                            validator: (rule, value, callback) => {
                                if(!/^[a-zA-Z0-9_]+$/.test(value)){
                                   callback(new Error("工号由字母/数字/下划线组成！"));
                                }else{
                                    callback();
                                }
                           }, trigger: 'blur'
                        },{ validator: this.checkCode, trigger: 'blur' }
                    ],*/
                    isStaff: [
                        { type: 'number', required: true, message: '是否本公司员工必选', trigger: 'change' },
                    ],
                    companyId: [
                        { required: true, message: '所属运输公司不能为空', trigger: 'change' },
                    ],
                    userName: [
                        { required:true, message: '请输入姓名', trigger: 'blur' },
                        { max: 50, message: '不超过50字', trigger: 'blur' },
                    ],
                    userCode: [
                        { max: 50, message: '不超过50字', trigger: 'blur' },
                        { type: 'string', validator: this.checkUserCode, trigger: 'blur' },
                    ],
                    userPhone: [
                        { required: true, message: '手机号不能为空', trigger: 'blur' },
                        { type: 'string', pattern: /^((0\d{2,3}-\d{7,8})|(1[358479]\d{9}))$/, message: '请输入有效的手机号码', trigger: 'blur' },
                        // { type: 'string', validator: this.checkPhoneIsExist, trigger: 'blur' },
                    ],
                    /* orgModelIds: [
                        { required: true, type: "array", min: 1, message: '请选择部门', trigger: 'change' }
                    ], */
             },
            //日历控件
            limitCardDate: {
                disabledDate(date) {
                    return date && date.valueOf() > Date.now();
                }
            },
         }
        },
        methods: {
            checkUserCode: function(rule, value, callback){
                let ajax = this.$ajax.post("/service_user/human/checkUserCode", { "userCode": value, 'id': this.id});
                ajax.then((res) => {
                    if (200 == res.data.code) {
                        if (res.data.data) {
                            callback();
                        } else {
                            callback("此账号已存在");
                        }
                   }
                }).catch(() => {
                        callback("服务异常");
                })
            },
            checkPhoneIsExist: function(rule, value, callback) {
                this.$ajax.post("/service_user/user/checkPhoneIsExist", { "phone": value,'oldPhone':this.oldPhone})
                    .then((res) => {
                    if (res.data.code == "200") {
                        if (res.data.data == false) {
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
            //保存数据
            save:function () {
                this.$refs['user'].validate((valid) => {
                    if (valid) {
                        if (this.$refs.webuploader.uploader.getFiles().length == 0) {
                            this.saveUser(); 
                        } else {
                            this.upload();
                        }                        
                    }
                })
            },
            saveUser:function(){
                if(this.orgModelIds.length > 0){
                    this.user.orgId = this.orgModelIds[this.orgModelIds.length - 1];
                }
                
                // this.user.birthday = this.user.birthday!=''?new Date(this.user.birthday).Format('yyyy-MM-dd'):'';
                if(this.user.birthday){
                    this.user.birthday = new Date(this.user.birthday).Format('yyyy-MM-dd');
                }
                if(this.user.entryDate){
                    this.user.entryDate = new Date(this.user.entryDate).Format('yyyy-MM-dd');
                }  
                this.submitUserData(); 
            },
            submitUserData: function(){
                if (this.user && this.user.createTime){
                    this.user.createTime = new Date(this.user.createTime).Format("yyyy-MM-dd hh:mm:ss")
                }
                if (this.user && this.user.updateTime){
                    this.user.updateTime = new Date(this.user.updateTime).Format("yyyy-MM-dd hh:mm:ss")
                }
                if (1 != this.user.isStaff && this.companys && this.companys.length > 0){
                    this.companys.some((com) => {
                        if (com.id == this.user.companyId){
                            this.user.orgName = com.name;
                            this.user.companyName = com.name;
                            return true;
                        }else {
                            return false;
                        }
                    });
                }
                var userVO = this.user;   
				this.loading = true;
                this.$ajax.post("/service_user/human/save", userVO)
                        .then(res => {
                        if (res.data.code == 200) {
                            this.$Message.success("保存成功");
                            this.$router.push("/user/list");
                        }
                    })
                    .catch((error) => {
                            if (!error.url) {
                            console.info(error);
                        }
                    })
            },
            //以下都是文件上传
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
                this.$refs.webuploader.uploader.makeThumb(obj.file, (error, src) => {
                    if(!error){
                    this.uploadimgurl = src;
                }
                }, 1, 1);
            },
            //上传头像
            upload: function() {
                this.$refs.webuploader.upload();
            },
            uploadFinished: function() {                
                this.saveUser();
            },
            uploadSuccess: function(obj) {
                this.user.photo = obj.response.data.id;
            },
            removeFile: function(obj) {
                if (obj.file.id != null) {
                    this.deleteFileId = obj.file.id;
                }
            },
            //查询数据
            getData: function() {
                var a = new Promise((resolve, reject) => {
                    this.$ajax.post("/service_user/sysData/findByTypeCode", {'dataTypeCode': "ZZMM_TYPE" })
                    .then(res => {
                        this.politicsType = res.data.data;
                        resolve(res.data.data);              
                    })
                    .catch((error) => {
                        if (!error.url) {console.info(error);}
                        reject();
                    });
                })
                var b = new Promise((resolve, reject) => {
                    this.$ajax.post("/service_user/organization/getOrgTree")
                    .then((res) => {
                        this.orgModelTreeData = res.data.data;
                        this.orgModelTree = this.$utils.Flat2TreeDataForCascader(res.data.data);
                        resolve(res.data.data);              
                    })
                    .catch((error) => {
                        if(!error.url){console.info(error);}
                        reject();
                    });
                })
                /*///获取公司下拉数据companys
                var c = new Promise((resolve, reject) => {
                    this.$ajax.post("/service_rms/partnerCompany/findAll")
                    .then((res) => {
                        this.companys = res.data.data;
                        resolve(res.data.data);              
                    })
                    .catch((error) => {
                        if(!error.url){console.info(error);}
                        reject();
                    });
                });*/
                Promise.all([a, b]).then((data) => {
                    //获取id
                    this.type = this.$route.params.type;
                    this.id = (undefined != this.$route.params.id) ? this.$route.params.id : null;
                    if (null != this.id) {
                        this.$ajax.post("/service_user/human/findById", {"id": this.id})
                        .then(res => {
                            this.user = res.data.data;
                            if (null != this.user) {
                                var orgArr=[];
                                if (this.type == "update") {
                                    orgArr = this.$utils.getTreeInParentsId(this.orgModelTreeData, this.user.orgId);
                                }
                                this.$nextTick(() => {
                                    this.orgModelIds = orgArr;
                                });

                                this.old=this.user.jobNumber;
                                this.oldUserPhone = this.user.userPhone;
                                //当为修改时，回显头像
                                if (this.user.photo) {
                                    this.$refs.webuploader.addDefaultFile([{
                                        "src": this.$utils.getFileShowUrl(this.user.photo)
                                    }]);
                                }
                            } else {
                                this.$Message.warning("当前员工不存在,请检查员工工号");
                                return;
                            }
                        })
                        .catch((error) => {
                                if (!error.url) {console.info(error);}
                        })
                    }
                })
                
            },
        },
        created: function() {
            this.getData();
        }
    }
</script>
