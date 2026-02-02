<template>
    <div>
        <Form :label-width="120" ref="station" :model="station" :rules="ruleValidate" :class="{'form-view':type=='view'}">
            <Form-item label="所属站点" prop="siteId">
                <template v-if="type=='view'">{{station.siteName}}</template>
                <Select v-else v-model="station.siteId" placeholder="请选择所属站点">
                    <Option v-for="(obj, index) in sites" :key="index" :value="obj.id">{{obj.showName}}</Option>
                </Select>
            </Form-item>

            <Form-item label="编码" prop="stationCode">
                <template v-if="type=='view'">{{station.stationCode}}</template>
                <Input v-else v-model.trim="station.stationCode" placeholder="请输入编码，不超过30个字符" maxlength="50"></Input>
            </Form-item>

            <Form-item label="展示名称" prop="showName">
                <template v-if="type=='view'">{{station.showName}}</template>
                <Input v-else v-model.trim="station.showName" placeholder="请输入展示名称，不超过30个字符" maxlength="50"></Input>
            </Form-item>

            <Form-item label="备注">
                <template v-if="type=='view'">{{station.remark}}</template>
                <Input v-else v-model.trim="station.remark" type="textarea" maxlength="250" placeholder="请输入备注，不超过250个字符"></Input>
            </Form-item>
        </Form>
    </div>
</template>
<script>
    export default {
        props:["id", "type"],
        data() {
            return {
                sites: [],
                station: { //控制站
                    id: '',
                    siteId: '',
                    stationCode: '',
                    showName: '',
                    remark: ''
                },
                ruleValidate: {
                    siteId: [
                        { type: 'number', required: true, message: '所属站点不能为空', trigger: 'blur' },
                    ],
                    stationCode: [
                        { required: true, message: '编码不能为空', trigger: 'blur' },
                        { type: 'string', pattern: /^[A-Z]{1,10}[0-9]{1,3}$/, message: '大写字母开头数字结尾', trigger: 'blur' },
                        { validator: this.checkStationCode, trigger: 'blur' },
                    ],
                    showName: [
                        { required: true, message: '展示名称不能为空', trigger: 'blur' },
                        { validator: this.checkShowName, trigger: 'blur' },
                    ],
               }
           }
        },
        methods: {
            //校验名称唯一性
            checkStationCode: function(rule, value, callback){
                if(value){
                    this.$ajax.post("/service_basedata/station/checkStationCode", {'stationCode': value, 'id': this.id})
                    .then((res) => {
                        if (res.data.flag){
                            callback();
                        } else {
                            callback(res.data.msg);
                        }
                    })
                    .catch((error) => {
                        callback('校验失败');
                    })
                } else {
                    callback("请输入站点编号");
                }
            },
            //校验编码唯一性
            checkShowName: function(rule, value, callback){
                if(value){
                    this.$ajax.post("/service_basedata/station/checkShowName", {'showName': value, 'id': this.id})
                    .then((res) => {
                        if (res.data){
                            callback();
                        } else {
                            callback("编号已存在");
                        }
                    })
                    .catch((error) => {
                        callback('校验失败');
                    })
                } else {
                    callback("请输入职务名称");
                }
            },
            save(cb){
                this.$refs['station'].validate((valid) => {
                    if (valid) {
                        var scadaStationVO = this.station;
                        this.$ajax.post("/service_basedata/station/save", scadaStationVO)
                        .then((res) => {
                            if (200 == res.data.code){
                                cb && cb(true);
                                this.$refs['station'].resetFields();
                                this.$Message.success('保存成功');
                            }else {
                                this.$Message.error('保存出错');
                            }
                        })
                        .catch((err) => {
                            cb && cb(false);
                            if(!err.url){console.info(err);}
                        })
                   } else {
                        cb && cb(false);
                        this.$Message.error('表单校验失败!');
                    }
                })
            },
            //重新加载数据
            getData:function() {
                this.$ajax.post("/service_basedata/station/findById", {"id": this.id})
                    .then((res) => {
                        this.station = res.data.data;
                     })
                    .catch((err) => {
                        if(!err.url){console.info(err);}
                   })
            },
            resetFields: function() {
                this.$refs.station.resetFields();
            },
            getBaseData: function(){
                this.$ajax.post("/service_basedata/site/selectList", {})
                        .then((res) => {
                            if (200 == res.data.code) {
                                this.sites = res.data.data;
                            } else {
                                this.$Message.error(res.data.msg);
                            }
                        })
                        .catch((err) => {if(!err.url){console.info(err);}});
            },
        },
        created: function() {
            
        },
        mounted: function(){
            this.getBaseData();
        },
        watch:{
            id: function() {
                if (this.id) {
                    this.getData();
                }
            }
        },
    }
</script>
