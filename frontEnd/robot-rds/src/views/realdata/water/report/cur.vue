<template>
    <div>
        <Form :label-width="150" ref="report" :model="report" :rules="ruleValidate" :class="{'form-view':type=='view'}">
            
            <Form-item label="水量日期" prop="reportDate">
                <template v-if="type=='view'">{{report.reportDate}}</template>
                <DatePicker v-else v-model="report.reportDate" :disabled="factorySelect" type="date" :options="options" :clearable="false" placeholder="请选择水量产生日期"></DatePicker>
            </Form-item>

            <Form-item label="水厂名称" prop="factoryId">
                <template v-if="'view'==type">{{report.factoryName}}</template>
                <Select v-else v-model="report.factoryId" :disabled="factorySelect" @on-change="changeFactory" placeholder="请选择水厂">
                    <Option v-for="(obj, index) in factorys" :key="index" :value="obj.id">{{obj.name}}</Option>
                </Select>
            </Form-item>

            <div v-if="report.factoryId">
                <Form-item label="总进水量（m³）" prop="inSum">
                    <template v-if="type=='view'">{{report.inSum}}</template>
                    <InputNumber v-else v-model="report.inSum" :min="0" :max="999999" style="width:80%"></InputNumber>
                    <span style="font-size: 14px;">&nbsp;m<sup>3</sup></span>
                </Form-item>

                <Card>
                    <h2 slot="title">出水量信息</h2>
                    <Form-item v-for="(item, index) in branchWaters" :key="index" :label="item.title">
                        <template v-if="type=='view'">{{item.value}}</template>
                        <InputNumber v-else v-model="item.value" :min="0" :max="999999" @on-change="getOutSum" style="width:80%"></InputNumber>
                        <span style="font-size: 14px;">&nbsp;m<sup>3</sup></span>
                    </Form-item>

                    <Form-item label="总出水量（m³）" :class="{'form-view':true}">
                        <template style="width:80%">{{report.outSum}}</template>
                        <span style="font-size: 14px;">&nbsp;m<sup>3</sup></span>
                    </Form-item>
                </Card>
            </div>
        </Form>
    </div>
</template>
<script>
    export default {
        props:["id","type"],
        data() {
            return {
                factorys: [], //水厂
                factorySelect: false, //水厂是否可选
                report: { //水量
                    id: '',
                    factoryId: '',
                    reportDate: '',
                    month: '',
                    inSum: 0,
                    outSum: 0
                },
                branchWaters: [], //支线水量信息
                options: {
                    disabledDate (date) {
                        return date && date.valueOf() > Date.now();
                    }
                },
                ruleValidate: {
                    factoryId: [
                        { type: 'number', required: true, message: '水厂不能为空', trigger: 'change' }, 
                        { validator: this.checkFactoryReport, trigger: 'blur' },                      
                    ],
                    reportDate: [
                        { type: 'date', required: true, message: '日期不能为空', trigger: 'change' },
                        { validator: this.checkFactoryReport, trigger: 'blur' },
                    ],
               }
           }
        },
        methods: {
            //校验水厂当前日期的水量是否已上报
            checkFactoryReport: function(rule, value, callback){
                if (this.factorySelect){
                    callback();
                }else {
                    var reportDate = "";
                    if (this.report.reportDate){
                        reportDate = new Date(this.report.reportDate).Format("yyyy-MM-dd");
                    }
                    this.$ajax.post("/service_basedata/report/checkFactoryReport", 
                            {'reportDate': reportDate, 'factoryId': this.report.factoryId})
                        .then((res) => {
                            if (res.data){
                                callback();
                            } else {
                                callback("此水厂当天水量已上报");
                            }
                        })
                        .catch((error) => {
                            callback('校验失败');
                        })
                }
            },
            getOutSum: function () {
                this.report.outSum = 0;
                if (this.branchWaters && this.branchWaters.length > 0){
                    this.branchWaters.map((j, i) => {
                        this.report.outSum += Number(j.value);
                    });
                }
            },
            save(cb){
                this.$refs['report'].validate((valid) => {
                    if (valid) {
                        if (this.report.reportDate){
                            this.report.reportDate = new Date(this.report.reportDate).Format("yyyy-MM-dd");
                            this.report.month = Number(this.report.reportDate.split("-")[1]);
                        }
                        if (this.report.createTime){
                            this.report.createTime = new Date(this.report.createTime).Format("yyyy-MM-dd hh:mm:ss");
                        }
                        var waterReportVO = this.report;
                        waterReportVO.jsonBranchWater = JSON.stringify(this.branchWaters);
                        this.$ajax.post("/service_basedata/report/save", waterReportVO)
                        .then((res) => {
                            if (200 == res.data.code){
                                cb && cb(true);
                                this.$refs['report'].resetFields();
                                this.$Message.success('保存成功');
                            }else {
                                this.$Message.error('保存出错');
                            }
                        })
                        .catch((err) => {
                            cb && cb(false);
                            if(!error.url){console.info(error);}
                        })
                   } else {
                        cb && cb(false);
                        this.$Message.error('表单校验失败!');
                    }
                })
            },
            //重新加载数据
            getData:function() {
                this.$ajax.post("/service_basedata/report/findById", {"id": this.id})
                    .then((res) => {
                        if (200 == res.data.code){
                            this.report = res.data.data;
                            if (this.report){
                                if (this.report.reportDate){
                                    this.report.reportDate = new Date(this.report.reportDate).Format("yyyy-MM-dd")
                                }
                                if (this.report.branchWaterList){
                                    this.branchWaters = this.report.branchWaterList; 
                                }else {

                                }
                            }
                        }else {
                            this.$Message.error('获取数据失败!');
                        }
                     })
                    .catch((err) => {
                        if(!err.url){console.info(err);}
                   })
            },
            resetFields: function() {
                this.$refs.report.resetFields();
            },
            getBaseData: function(){
                this.$ajax.post("/service_basedata/report/getBaseData")
                    .then((res) => {
                        if (200 == res.data.code){
                            this.factorys = res.data.waterFactoryVOList;
                        }else {
                            this.$Message.error('基础数据获取失败');
                        }
                    })
                    .catch((err) => {if (!err.url){console.info(err);}})
            },
            changeFactory: function(factoryId){
                var factory = {};
                this.branchWaters = [];
                this.factorys.some((j) => {
                    if (factoryId == j.id){
                        factory = j;
                        return true;    
                    }else {
                        return false;
                    }
                });
                if (factory.waterBranchVOList && factory.waterBranchVOList.length > 0){
                    this.branchWaters = factory.waterBranchVOList.map((j) => {
                        var obj = {
                            key: "bw" + j.id,
                            title: j.name + "（m³）",
                            value: 0
                        }
                        return obj;
                    });
                }else {
                    this.branchWaters.push({
                        key: "fw" + factory.id,
                        title: factory.name + "（m³）",
                        value: 0
                    });
                }
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
                    this.factorySelect = true;
                    this.getData();
                }else {
                    this.factorySelect = false;
                }
            }
        },
    }
</script>
