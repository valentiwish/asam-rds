<template>
    <div>
        <Form ref="humCompany" :model="humCompany" :label-width="108" :class="{'form-view':type=='view'}" :rules="ruleValidate" inline>
            <Card>
                <h2 slot="title">基本信息</h2>

                <Form-item label="企业名称" prop="name">
                    <template v-if="type=='view'">{{humCompany.name}}</template>
                    <Input v-else v-model="humCompany.name" maxlength="100" placeholder="请输入企业名称，不超过100个字符"></Input>
                </Form-item>

                <Form-item label="企业简称" prop="shortName">
                    <template v-if="type=='view'">{{humCompany.shortName}}</template>
                    <Input v-else v-model="humCompany.shortName" maxlength="50" placeholder="请输入企业简称，不超过50个字符"></Input>
                </Form-item>
                <Form-item label="企业地址" prop="address">
                    <template v-if="type=='view'">{{humCompany.address}}</template>
                    <Input v-else v-model="humCompany.address" maxlength="100" placeholder="请输入企业地址，不超过100个字符"></Input>
                </Form-item>

                <Form-item label="上级公司">
                    <template v-if="type=='view'">{{humCompany.parentName}}</template>
                    <Cascader v-else :data="humCompanyTree" v-model="parentIds" placeholder="请选择上级公司"></Cascader>
                </Form-item>

                <Form-item label="统一社会信用代码">
                    <template v-if="type=='view'">{{humCompany.creditCode}}</template>
                    <Input v-else v-model="humCompany.creditCode" maxlength="20" placeholder="请输入统一社会信用代码，不超过50个字符"></Input>
                </Form-item>

                <!-- <Form-item label="详细地址">
                    <template v-if="type=='view'">{{humCompany.detailAddress}}</template>
                    <Input v-else v-model.trim="humCompany.detailAddress" maxlength="250" placeholder="请输入详细地址，不超过250字"></Input>
                </Form-item> -->

               <!--  <Form-item label="邮编" prop="zipCode">
                    <template v-if="type=='view'">{{humCompany.zipCode}}</template>
                    <Input v-else v-model="humCompany.zipCode" maxlength="10" placeholder="请输入邮编，不超过10个字符"></Input>
                </Form-item> -->

                <Form-item label="电话" prop="phone">
                    <template v-if="type=='view'">{{humCompany.phone}}</template>
                    <Input v-else v-model="humCompany.phone" maxlength="13" placeholder="请输入电话，不超过13个字符"></Input>
                </Form-item>

                <Form-item label="传真">
                    <template v-if="type=='view'">{{humCompany.fax}}</template>
                    <Input v-else v-model="humCompany.fax" maxlength="20" placeholder="请输入传真，不超过20个字符"></Input>
                </Form-item>
                <Form-item label="企业邮箱" prop="email">
                    <template v-if="type=='view'">{{humCompany.email}}</template>
                    <Input v-else v-model="humCompany.email" maxlength="100" placeholder="请输入企业邮箱，不超过100个字符"></Input>
                </Form-item>

                <!-- <Form-item label="企业编码">
                    <template v-if="type=='view'">{{humCompany.code}}</template>
                    <Input v-else v-model="humCompany.code" maxlength="100" placeholder="请输入企业编码，不超过100个字符"></Input>
                </Form-item> -->
                <!-- <br/> -->

                <!-- <Form-item label="经济类型">
                    <template v-if="type=='view'">{{humCompany.economyTypeName}}</template>
                    <Select v-else v-model="humCompany.economyTypeId" placeholder="请选择经济类型">
                        <Option v-for="(obj, index) in economyTypes" :key="index" :value="obj.id">{{obj.textName}}</Option>
                    </Select>
                </Form-item>

                <Form-item label="行业性质">
                    <template v-if="type=='view'">{{humCompany.industryPropertName}}</template>
                    <Select v-else v-model="humCompany.industryPropertId" placeholder="请选择行业性质">
                        <Option v-for="(obj, index) in industryPropertiess" :key="index" :value="obj.id">{{obj.textName}}</Option>
                    </Select>
                </Form-item>        

                <Form-item label="企业网站" prop="website" class=singleline>
                    <template v-if="type=='view'">{{humCompany.website}}</template>
                    <Input v-else v-model="humCompany.website" maxlength="100" placeholder="请输入企业网站，以http|ftp|https://开头"></Input>
                </Form-item> -->

                <Form-item label="企业简介" class="singleline">
                    <template v-if="type=='view'">{{humCompany.introduction}}</template>
                    <Input v-else v-model.trim="humCompany.introduction" maxlength="1000" type="textarea" placeholder="请输入企业简介，不超过1000字"></Input>
                </Form-item>
                
            </Card>
            <!-- <Card>
                <h2 slot="title">联系方式</h2>
                
            </Card> -->
            <div style="text-align:center;">
                <Button type="primary" @click="save" v-if="type!='view'" :loading="loading">保存</Button>
                <Button type="ghost" @click="$router.back()" v-if="type=='view'">返回</Button>
                <Button type="ghost" @click="$router.back()" v-else style="margin-left: 8px">取消</Button>
            </div>
        </Form>
    </div>
</template>
<script>
    export default {
        components: {},
        data() {
            var that = this;
            return {
                loading: false,
                id: '',
                type: '',
                humCompanyTreeData: [], //公司树扁平数据
                humCompanyTree: [], //公司树
                parentIds: [],
                economyTypes: [], //经济类型下拉数据
                industryPropertiess: [], //行业性质下拉数据
                humCompany: { //公司信息
                    id: '',
                    code: '',
                    name: '',
                    shortName: '',
                    address: '',
                    detailAddress: '',
                    zipCode: '',
                    phone: '',
                    fax: '',
                    creditCode: '',
                    economyTypeId: '',
                    economyTypeName: '',
                    industryPropertId: '',
                    industryPropertName: '',
                    email: '',
                    website: '',
                    introduction: '',
                    parentId: ''
                },
                //校验
                ruleValidate: {
                    name: [
                        { required:true, message: '名称不能为空', trigger: 'blur' },
                        { validator: this.checkName, trigger: 'blur' },
                    ],
                    shortName: [
                        { required: true, message: '简称不能为空', trigger: 'blur' },
                    ],
                    website: [
                        { type: 'string', pattern: /(http|ftp|https):\/\/[\w\-_]+(\.[\w\-_]+)+([\w\-\.,@?^=%&amp;:/~\+#]*[\w\-\@?^=%&amp;/~\+#])?/, message: '网站格式不正确', trigger: 'blur' },
                    ],
                    address: [
                        { required: true, message: '地址信息不能为空', trigger: 'blur' },
                    ],
                    email: [
                        { type: 'string', pattern: /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/, message: '邮箱格式不正确', trigger: 'blur' },
                    ],
                    zipCode: [
                        { type: 'string', pattern: /^\d{6}$/, message: '邮编格式不正确', trigger: 'blur' },
                    ],
                    phone: [
                        { validator: this.checkPhone, trigger: 'blur' },
                    ],
             },
         }
        },
        methods: {
            //校验名称唯一性
            checkName:function(rule, value, callback){
                if(value){
                    this.$ajax.post("/service_user/company/checkName", {'name': value, 'id': this.id})
                    .then((res) => {
                        if(res.data){
                            callback();
                        }else{
                            callback("名称已存在");
                        }
                    })
                    .catch((error) => {
                        callback('校验失败');
                    })
                } else {
                    callback('名称不能为空');
                }
            },
            checkPhone: function(rule, value, callback){
                if (value){
                    var ph = /^(0|86|17951)?(13[0-9]|15[012356789]|17[01678]|18[0-9]|14[57])[0-9]{8}$/;
                    var mb = /^(0[0-9]{2,3}\-)([2-9][0-9]{6,7})+(\-[0-9]{1,4})?$/;
                    if(!ph.test(value) && !mb.test(value)){ 
                        callback("电话号码格式不正确");
                    } else {
                        callback();
                    }
                } else {
                    callback();
                }
            },
            //保存数据
            save: function() {
                this.$refs['humCompany'].validate((valid) => {
                    if (valid) {
                        this.loading = true;
                        if (this.parentIds && this.parentIds.length > 0){
                            this.humCompany.parentId = this.parentIds[this.parentIds.length-1];
                        }
                        if (this.humCompany && this.humCompany.createTime){
                            this.humCompany.createTime = new Date(this.humCompany.createTime).Format("yyyy-MM-dd hh:mm:ss")
                        }
                        if (this.humCompany && this.humCompany.updateTime){
                            this.humCompany.updateTime = new Date(this.humCompany.updateTime).Format("yyyy-MM-dd hh:mm:ss")
                        }
                        var humCompanyVO = this.humCompany;
                        this.$ajax.post("/service_user/company/save", humCompanyVO)
                        .then(res => {
                            if (res.data.code == 200) {
                                this.$Message.success("保存成功");
                                this.$router.push("/company/list");
                            }
                        })
                        .catch((error) => {
                            if (!error.url) {console.info(error);}
                        })
                    }
                })
            },
            //查询数据
            getData: function(id) {
                this.$ajax.post("/service_user/company/findById", {"id": this.id})
                .then((res) => {
                    if (200 == res.data.code){
                        this.humCompany = res.data.data;
                        //回显树数据
                        if(this.humCompany && this.humCompany.parentId){
                            this.parentIds = this.$utils.getTreeInParentsId(this.humCompanyTreeData, this.humCompany.parentId);
                        }
                    }else {
                        this.$Message.warning("网络异常！");
                    }
                })
                .catch((err) => {
                    if (!error.url) {console.info(error);}
                })
            },
            getBaseData:function(){

                var a = new Promise((resolve, reject) => {
                    this.$ajax.post("/service_user/sysData/findByTypeCode", {'dataTypeCode': "ECONOMY_TYPE" })
                            .then(res => {
                            this.economyTypes = res.data.data;
                            resolve(res.data.data);
                       })
                        .catch((error) => {
                            if (!error.url) {
                            console.info(error);
                        }
                     });
                })
                var b = new Promise((resolve, reject) => {
                    //行业性质
                    this.$ajax.post("/service_user/sysData/findByTypeCode", {'dataTypeCode': "INDUSTRY_PROPERTIES" })
                        .then(res => {
                        this.industryPropertiess = res.data.data;
                        resolve(res.data.data);
                   })
                    .catch((error) => {
                        if (!error.url) {
                        console.info(error);
                    }
                     });               
                })
                var c = new Promise((resolve, reject) => {   
                    this.$ajax.post("/service_user/company/humCompanyTree")
                        .then((res) => {
                            this.humCompanyTreeData = res.data.data;
                            this.humCompanyTree = this.$utils.Flat2TreeDataForCascader(this.humCompanyTreeData);
                            resolve(res.data.data);
                        })
                            .catch((error) => {
                                if(!error.url){console.info(error);}
                        });
                })
                Promise.all([a,b,c]).then((data) => {                    
                    //获取id
                    this.id = this.$route.params.id;
                    this.type = this.$route.params.type;
                    if (this.id){
                        this.getData(this.id)
                    }
                })

            },

            
        },
        created: function() {
            this.getBaseData();
        },
        watch: {
            // 监听企业名称，同步到企业简称
            "humCompany.name": function(n, o){
                if (!this.type){
                    this.humCompany.shortName = n;
                }
            }
        },
    }
</script>
