<template>
    <div>
        <Form :label-width="100" ref="moudleData" :model="moudleData" :rules="ruleValidate" :class="{'form-view':type=='view'}">
            <Spin fix v-if="isLoading">
                <Icon type="load-c" size=18 class="demo-spin-icon-load"></Icon>
                <div>暂无数据...</div>
            </Spin>
            <Form-item label="所属系统" prop="systemId">
                <Tag color="success" size="large">{{moudleDataName}}</Tag>
                <!-- <Select placeholder="请选择系统名称" disabled v-model="moudleData.systemId">
                    <Option v-for="system in systemList" :value="system.id" :key="system.id">{{system.name}}</Option>
                </Select> -->
            </Form-item>
            <Form-item label="资源名称" prop="name">
                <template v-if="type=='view'">{{moudleData.name}}</template>
                <Input v-else v-model.trim="moudleData.name" maxlength="50" placeholder="请输入资源名称"></Input>
            </Form-item>
            <Form-item label="父模块" prop="parent">
                <template v-if="type=='view'">{{moudleData.parent.join("/")}}</template>
                <Cascader v-else filterable :data="baseMoudleData" v-model="moudleData.parent" change-on-select></Cascader>
            </Form-item>
            <Form-item label="链接地址" prop="actionUrl">
                <template v-if="type=='view'">{{moudleData.actionUrl}}</template>
                <template v-else>
                    <Input v-model.trim="moudleData.actionUrl" maxlength="200" placeholder="存在，以“/”开头，否则为“#”"></Input>

                    <div style="padding:5px 0;word-break:break-all;">
                        <div>可视化BI引入，配置路由名以<code>/dataease/</code>开头，如<code>/dataease/:linkId</code></div>
                        <div>积木报表引入，配置路由名以<code>/jmreport/</code>开头，如<code>/jmreport/:linkId</code></div>
                        <div>手机端云化模式以<code>/:hybrid/</code>开头,如<code>/:hybrid/https://www.baidu.com/</code></div>
                    </div>
                </template>
            </Form-item>
            
            <Form-item label="模块图片" prop="imgUrl">
                <template v-if="type=='view'">
                    <div v-if="uploadimgurl">
                        <img style="width:150px;" :src="uploadimgurl">
                    </div>
                </template>
                <div  v-else>
                    <Button type="primary" @click="openDialog" :loading="makethumbloading">选择图片</Button>
                    <div style="margin-top:10px;" v-if="uploadimgurl"><img style="width:150px;" :src="uploadimgurl"></div>
                    <webuploader style="display:none;" :option="option" ref="webuploader" @fileQueued="fileQueued" @uploadFinished="uploadFinished" @deleteFile="deleteFile" @uploadSuccess="uploadSuccess">
                        <span slot="text">最多上传1个附件，要求：<br/>①大小不超过10M；<br/></span>
                    </webuploader>
                </div>
            </Form-item>
            <Form-item label="模块图标" prop="icon">
                <i :class="moudleData.icon" v-if="type=='view'"></i>
                <font-icon v-model="moudleData.icon" v-else></font-icon>
            </Form-item>
            <Form-item label="操作标识" prop="operations">
                <Tag :closable="type!='view'" 
                    color="success" 
                    @on-close="handleAllowClose" 
                    :name="item"
                    :key="item"
                    v-for="item in operations">{{item}}</Tag>
                <div v-if="operations.length ==0 ">暂无数据</div>
                <Input v-model.trim="allowText" style="width:180px;" placeholder="请输入操作标识符" v-if="type != 'view'">
                    <Button slot="append" icon="md-add" @click="handleAllowAdd"></Button>
                </Input>

                <div style="padding:5px 0;word-break:break-all;" v-if="type != 'view'">
                    <div><code>/home</code>页面如引用DataEase链接，配置操作标识以<code>/dataease/</code>开头，如<code>/dataease/zumKJEYU</code></div>
                </div>
            </Form-item>

            <!-- <Form-item label="操作标识" prop="parent">
                <template v-if="type=='view'">
                    <div v-for="item in opers" :key="item.id">
                        <Tag color="green">{{item.description}}</Tag>
                        <template v-if="item.url">访问地址：{{item.url}}</template>
                    </div>
                </template>
                <template v-else>
                    <Select clearable multiple v-model="operations">
                        <Option v-for="item in allOperations" :value="item.id" :key="item.id">{{ item.description }}</Option>
                    </Select>
                    <Row v-for="(item,index) in allCheckedOperations" :key="item.id" style="margin-top:10px;">
                        <Input v-model.trim="item.url" placeholder="按钮访问地址" v-if="isshow(item)" maxlength="500">
                        <span slot="prepend">{{item.description}}</span>
                        </Input>
                    </Row>
                </template>
            </Form-item> -->
            <Form-item label="排序" prop="sort">
                <template v-if="type=='view'">{{moudleData.sort}}</template>
                <InputNumber :min="0" :max="15" v-else v-model="moudleData.sort" placeholder="请输入排序"></InputNumber>
            </Form-item>
            <Form-item label="显示到菜单" prop="isDisplay">
                <template v-if="type=='view'">{{moudleData.isDisplay ? '显示' :'不显示'}}</template>
                <Radio-group v-else v-model="moudleData.isDisplay">
                    <Radio :label="1">显示</Radio>
                    <Radio :label="0">不显示</Radio>
                </Radio-group>
            </Form-item>
            <Form-item label="启用状态" prop="enable">
                <template v-if="type=='view'">{{moudleData.enable ? '启用' :'禁用'}}</template>
                <Radio-group v-else v-model="moudleData.enable">
                    <Radio :label="1">启用</Radio>
                    <Radio :label="0">禁用</Radio>
                </Radio-group>
            </Form-item>
            <Form-item label="模块描述" prop="description">
                <template v-if="type=='view'">{{moudleData.description}}</template>
                <Input v-else v-model.trim="moudleData.description" maxlength="100" placeholder="请输入模块描述，不超过100个字符"></Input>
            </Form-item>
            <Form-item label="自定义参数" prop="jsonCss">
                <template v-if="type=='view'">{{moudleData.jsonCss}}</template>
                <Input v-else v-model.trim="moudleData.jsonCss" maxlength="100" autosize type="textarea" placeholder="请输入json文本，不超过1000个字符"></Input>
            </Form-item>
        </Form>
    </div>
</template>
<script>
import fontIcon from "@/components/fontIcon"
import webuploader from '@/components/webuploader'
import UTILS from '@/libs/util.js'

export default {
    name: 'index',
    components: { fontIcon,webuploader },
    props: ["id", "type", "baseMoudleData", "baseCascader", "checkedID"],
    data:function() {
        var that = this;
        return {
            allowText:"",
            option: {
                auto: true,
                fileSingleSizeLimit: 10 * 1024 * 1024,
                fileNumLimit: 100,
                formData: {
                    "moudleName": "system", //定义大模块名称
                },
                accept: {
                    title: 'Images',
                    extensions: 'jpg,jpeg,png',
                    mimeTypes: 'image/jpg, image/jpeg, image/png',
                },
                type:"select",
                beforeFileQueued:that.beforeFileQueued
            },
            makethumbloading:false,
            uploadimgurl:"",
            isLoading: false,
            model1: '',
            operations: [], // 页面操作权限
            moudlename: '',
            systemList:[],
            moudleData: {
                systemId:"",
                name: '', // 模块名称
                id: '',
                actionUrl: '#',
                isOperation: 1, // 是否是表单元素
                imgUrl: '', //二级菜单图片
                icon: '', //三级菜单图标
                classType: '',
                sort: '1',
                enable: 1,
                description: '',
                parent: [],
                isDisplay:1, //默认显示到菜单列表 
                jsonCss:'',
            },
            ruleValidate: {
                systemId: [
                    { required: true,type:'number', message: '所属系统不为空', trigger: 'blur' }
                ],
                imgUrl: [],
                name: [
                    { required: true, message: '资源名称不为空', trigger: 'blur' },
                    { type: 'string', max: 30, message: '资源名称不超过30个字符', trigger: 'blur' }
                ],
                icon: [
                    { required: true, message: '模块图标不为空', trigger: 'blur' }
                ],
                description: [
                    { type: 'string', max: 100, message: '模块描述，不超过100个汉字', trigger: 'blur' }
                ],
                actionUrl: [{
                    type: 'string',
                    max: 200,
                    validator: (rule, value, callback) => {
                        if (value && value.length == 1) {
                            if (/^#/.test(value) == false) {
                                callback(new Error("为空时，请输入#"));
                            } else {
                                callback();
                            }
                        } else {
                            //zzk 20221103修改，支持网址录入
                            if(new RegExp("^(http|https)://","i").test(value)){
                                callback();
                            }
                            else if (/^\//.test(value) == false ) {
                                callback(new Error("请以'/'开头"));
                            } else if (value.length > 200) {
                                callback(new Error("链接地址不能超过200个字符"));
                            } else {
                                callback();
                            }
                        }
                    },
                    trigger: 'blur'
                }]
            }
        }
    },
    computed: {
        moudleDataName(){
            var obj = this.systemList.find((j)=>{
                return j.id == this.moudleData.systemId;
            });
            if(obj){
                return obj.name;
            }
            return "";
        }
    },
    created: function() {
        
    },
    methods: {
        handleAllowClose(event, name) {
            var index = this.operations.indexOf(name);
            this.operations.splice(index, 1)
        },
        handleAllowAdd() {
            if (!this.allowText) {
                this.$Message.warning('请输入操作标识符');
            }
            if (this.operations.indexOf(this.allowText) == -1) {
                this.operations.push(this.allowText);
                this.$nextTick(()=>{
                    this.allowText = "";
                })
            }
            else {
                this.$Message.warning('操作标识符已存在');
            }
        },
        openDialog: function() {
            this.$refs.webuploader.open_dialog();
        },
        //当加入队列时生成缩略图
        fileQueued: function(obj) {
            this.makethumbloading = true;
            this.$refs.webuploader.uploader.makeThumb(obj.file, (error, src) => {
                this.makethumbloading = false;
                if(!error){
                    this.uploadimgurl = src;
                }
            }, 1, 1);
        },
        beforeFileQueued:function(){
            this.moudleData.imgUrl = "";
            this.uploadimgurl = "";
            //清空上传队列
            if(this.$refs.webuploader){
                var arr = this.$refs.webuploader.uploader.getFiles();
                arr.map((j,i)=>{
                    this.$refs.webuploader.uploader.removeFile(j);
                })
            }
        },
        uploadSuccess: function(obj) {
            this.moudleData.imgUrl = obj.response.data.id;
            this.uploadimgurl = "/service_file/show?fileId="+obj.response.data.id;
        },
        uploadFinished:function() {
            
        },
        deleteFile: function() {
            this.moudleData.imgUrl = '';
        },
        isshow(item) {
            return this.operations.some(function(j, i) {
                return item.id == j
            })
        },
        save(cb) {
            //构造选中按钮
            var selectMenu = this.operations.map((j)=>{
                return {
                    "code": j,
                    "name": j,
                    "url": ''
                }
            })
            this.moudleData.systemName = this.moudleDataName;
            this.$refs['moudleData'].validate((valid) => {
                if (valid) {
                    var url = "/service_user/moudle/save";
                    this.$ajax.post(url, { 
                        "data": JSON.stringify(this.moudleData), 
                        "parent": JSON.stringify(this.moudleData.parent), 
                        "menu": JSON.stringify(selectMenu)
                    })
                    .then((res) => {
                        if (res.data.code == "200") {
                            this.clearFields();
                            cb && cb(true);
                            this.$Message.success('保存成功');
                        } else {
                            cb && cb(false);
                            this.$Message.error('保存出错，信息为' +res.data.msg);
                        }
                    },()=>{
                        cb && cb(false);
                        this.$Message.error('保存出错,通信异常');
                    })
                } else {
                    cb && cb(false);
                    this.$emit("changeVal");
                    this.$Message.error('表单校验失败!');
                }
            })
        },
        update: function(cb) {
            //构造选中按钮
            var selectMenu = this.operations.map((j)=>{
                return {
                    "code": j,
                    "name": j,
                    "url": ''
                }
            })

            this.moudleData.systemName = this.moudleDataName;
            this.$refs['moudleData'].validate((valid) => {
                if (valid) {
                    var url = "/service_user/moudle/update";
                    this.$ajax.post(url, { "data": JSON.stringify(this.moudleData), "id": this.id, "parent": JSON.stringify(this.moudleData.parent), "menu": JSON.stringify(selectMenu) })
                        .then((res) => {
                            if (res.data.code == "200") {
                                this.clearFields();
                                cb && cb(true);
                            } else {
                                cb && cb(false);
                                this.$Message.error('保存出错' + res.data.msg);
                            }
                        },()=>{
                            cb && cb(false);
                            this.$Message.error('保存出错,通信异常');
                        })
                } else {
                    this.$emit("changeVal");
                    cb && cb(false);
                    this.$Message.error('表单校验失败!');
                }
            })
        },
        getData: function() {
            //this.$refs.moudleData.resetFields();
            this.isLoading = true;
            //获取此模块模块基本信息 根据id查询
            this.$ajax.post("/service_user/moudle/findMoudleById", { id: this.id })
                .then(res => {
                    this.isLoading = false;
                    var data = res.data;
                    //操作赋值
                    if (data.data.operations && data.data.operations.length > 0) {
                        this.operations = data.data.operations.map((j)=>{
                            return j.description;
                        })
                    } else {
                        this.operations = [];
                    }

                    //其他输入框赋值
                    var parentId = data.data.moudle.id;
                    data.data.moudle.parent = [];
                    if (void 0 == data.data.moudle.isDisplay) {
                        data.data.moudle.isDisplay = true;
                    }
                    this.moudleData = data.data.moudle;
                    if(data.data.moudle.imgUrl){
                        this.uploadimgurl = "/service_file/show?fileId="+data.data.moudle.imgUrl;
                    }
                    var arr = [];
                    if (this.type == "view") {
                        arr = UTILS.getTreeParents(this.baseCascader, parentId).map(function(j, i) {
                            return j.name;
                        });
                    } else {
                        arr = UTILS.getTreeParentsId(this.baseCascader, parentId);
                    }
                    this.$nextTick(() => {
                        this.moudleData.parent = arr;
                    });
                })
                .catch((error) => {
                    this.isLoading = false;
                    if (!error.url) { console.info(error); }
                });
        },
        getBaseData:function(){
            //获取此模块模块基本信息 根据id查询
            this.$ajax.post("/service_user/systemInfo/allList")
            .then(res => {
                this.systemList = res.data.data;               
            },(res)=>{
                
            })
        },        
        clearFields: function() {
            this.moudleData.name = ''; // 模块名称
            this.moudleData.id = '';
            this.moudleData.actionUrl = '#';
            this.moudleData.isOperation = 1; // 是否是表单元素
            this.moudleData.imgUrl = ''; //二级菜单图片
            this.moudleData.icon = ''; //三级菜单图标
            this.moudleData.classType = '';
            this.moudleData.sort = '1';
            this.moudleData.enable = 1;
            this.moudleData.isDisplay = 1;
            this.moudleData.description = '';
            this.moudleData.parent = [];

            this.beforeFileQueued();
        },
        refreshData: function(checkedID) {
            if (checkedID != "") {
                var arr = UTILS.getTreeInParentsId(this.baseCascader, checkedID);
                this.$nextTick(() => {
                    this.moudleData.parent = arr;
                });
            }
        }
    },
    watch: {
        id: function(n,o) {
            if (this.id) {
                this.getData();
            }
        }
    },
    mounted: function() {
        this.getBaseData();
    }
}

</script>

<style scoped>
    code{
        background: #efefef;
        color:#0962bd;
        padding:0 5px;
        border-radius: 3px;
    }
</style>
