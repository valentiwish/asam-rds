<template>
    <div>
        <Form :label-width="120" ref="org" :rules="ruleValidate" :model="org" :class="{'form-view':type=='view'}">
            <Form-item label="上级组织机构" prop="parentId">
                <template v-if="type=='view'">{{org.parentName}}</template>
                <Cascader v-else :data="carModelTree" v-model="carModelIds" placeholder="请选择上级组织机构" change-on-select></Cascader>
                <!--<Cascader :data="data" v-model="value1"></Cascader>-->
            </Form-item>
            <Form-item label="组织机构编码" prop="code">
                <template v-if="type=='view'">{{org.code}}</template>
                <Input v-else v-model.trim="org.code" placeholder="请输入机构编码，不超过30个字符" maxlength="30">
                </Input>
            </Form-item>
            <Form-item label="组织机构名称" prop="name">
                <template v-if="type=='view'">{{org.name}}</template>
                <Input v-else v-model.trim="org.name" placeholder="请输入机构名称，不超过30个字符" maxlength="50">
                </Input>
            </Form-item>
            <!-- <Form-item label="简称" prop="shortName">
                <template v-if="type=='view'">{{org.shortName}}</template>
                <Input v-else v-model.trim="org.shortName" placeholder="请输入机构简称，不超过10个字符" maxlength="10">
                </Input>
            </Form-item> -->
            <!-- <Form-item label="联系人" prop="linker">
                <template v-if="type=='view'">{{org.linker}}</template>
                <Input v-else v-model.trim="org.linker" placeholder="请输入联系人，不超过20个字符" maxlength="20">
                </Input>
            </Form-item> -->
            <Form-item label="联系电话" prop="telephone">
                <template v-if="type=='view'">{{org.telephone}}</template>
                <Input v-else v-model.trim="org.telephone" placeholder="请输入联系电话，不超过20个字符" maxlength="20">
                </Input>
            </Form-item>
            <!-- <Form-item label="联系地址" prop="address">
                <template v-if="type=='view'">{{org.address}}</template>
                <Input v-else v-model.trim="org.address"  maxlength="100" type="textarea" placeholder="请输入联系地址，不超过100个汉字...">
                </Input>
            </Form-item> -->
            <Form-item label="部门职能描述" prop="responsibility">
                <template v-if="type=='view'">{{org.responsibility}}</template>
                <Input v-else v-model.trim="org.responsibility"  maxlength="100" type="textarea" placeholder="请输入部门职能，不超过100个汉字...">
                </Input>
            </Form-item>
           <!--  <Form-item label="备注" prop="remark" class="singleline">
                <template v-if="type=='view'">{{org.remark}}</template>
                <Input v-else v-model.trim="org.remark"  maxlength="100" type="textarea" placeholder="请输入备注，不超过100个汉字...">
                </Input>
            </Form-item> -->
        </Form>
    </div>
</template>
<script>
    import Tree from "@/components/org-tree"

    export default {
        components: {Tree},
        props:["id","type"],
        data() {
            return {
                carModelTreeData: [],
                carModelTree: [],
                carModelIds: [],
                oldcode:'',
                oldname:'',
                org: {
                    id: '',
                    parentId: '',
                    code: '',
                    name: '',
                    shortName: '',
                    telephone: '',
                    linker: '',
                    address: '',
                    responsibility: '',
                    remark: ''
                },
                ruleValidate: {
                    /*parentId: [
                        { required: true, validator: this.checkParentId, trigger: 'change' },
                    ],*/
                    code: [
                        { required: true, message: '请输入组织机构编码', trigger: 'blur' },
                        { max: 30, message: '编码不超过30个字符', trigger: 'blur' },
                        {
                            validator: (rule, value, callback) => {
                                if(!/^[a-zA-Z0-9_]+$/.test(value)){
                                  callback(new Error("由字母/数字/下划线组成组成"));
                                }else{
                                    callback();
                                }
                            }, trigger: 'blur'
                        },{ validator: this.checkCode, trigger: 'blur' }
                     ],
                     name: [
                            { required: true, message: '请输入组织机构名称', trigger: 'blur' },
                            { max: 50, message: '编码不超过50个字符', trigger: 'blur' },
                    ],
               }
           }
        },
        methods: {
            checkParentId: function(rule, value, callback){
                var count = this.carModelIds.length;
                if (0 == count){
                    callback("请选择上级组织机构");
                }else{
                    //获取当前父类子的父类ids,如果该ids包含它自己的id则校验失败
                    if(this.carModelIds.length>0){
                        var curId = this.carModelIds[this.carModelIds.length-1];
                        var arr = [];
                        arr = this.$utils.getTreeInParentsId(this.carModelTreeData, curId);
                        arr.some((j,i)=>{
                            if(j==this.id){
                            callback("不能选择其本身及其子部门");
                            return ;
                        }
                    })
                        callback();
                    }else{
                        if(this.id){
                            callback("上级部门部门为空");

                        }else{
                            callback();
                        }
                    }
                }
            },
            //校验编码
            checkCode:function(rule,value,callback){
                if(value){
                    this.$ajax.post("/service_user/organization/checkCode",{'code':value, 'id': this.id})
                    .then((res) => {
                        if(res.data){
                            callback();
                        }else{
                            callback("编码已存在");
                        }
                })
                .catch((error) => {
                        callback('校验失败');
                })
                }
            },
            save(cb){
                this.org.parentId = this.carModelIds[this.carModelIds.length-1];
                
                if (this.org && this.org.createTime){
                    this.org.createTime = new Date(this.org.createTime).Format("yyyy-MM-dd hh:mm:ss")
                }
                if (this.org && this.org.updateTime){
                    this.org.updateTime = new Date(this.org.updateTime).Format("yyyy-MM-dd hh:mm:ss")
                }
                
                var orgVO = this.org;
                this.$refs['org'].validate((valid) => {
                    if (valid) {
                        this.$ajax.post("/service_user/organization/save",orgVO)
                            .then((res) => {
                            if(res.data.code == "200"){
                                cb && cb(true);
                                this.$refs['org'].resetFields();
                                this.$Message.success('保存成功');
                                }else{
                                    this.$Message.error('保存出错');
                                }
                           })
                            .catch((error) => {
                                    if(!error.url){console.info(error);}
                            })
                            } else {
                                this.$emit("changeVal");
                                this.$Message.error('表单校验失败!');
                             }
                   })
            },
            getOrgTree: function () {
                this.$ajax.post("/service_user/organization/getOrgTree")
                    .then((res) => {
                        this.carModelTreeData = res.data.data;
                        this.carModelTree = this.$utils.Flat2TreeDataForCascader(res.data.data);
                    })
                        .catch((error) => {
                            if(!error.url){console.info(error);}
                    });
            },
            //重新加载数据
            getData:function() {
                this.$ajax.post("/service_user/organization/getInfoById",{"id":this.id})
                    .then((res) => {
                        this.org=res.data.data;
                        this.oldcode = this.org.code;
                        this.oldname = this.org.name;
                        //加载树数据
                        if(this.org!=null){
                            this.carModelIds = this.$utils.getTreeInParentsId(this.carModelTreeData, this.org.parentId);
                        }
                     })
                    .catch((error) => {
                        if(!error.url){console.info(error);}
                   })
                },
                resetFields: function() {
                    this.$refs.org.resetFields();
                },
        },
        watch:{
            id:function() {
                if (this.id != "") {
                    this.getData();
                }
            }
        },
        created: function() {
            this.getOrgTree();
        }
    }
</script>
