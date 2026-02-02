<template>
    <div>
        <page-title></page-title>
        <div class="page-content">
        <Card class="card-blue">
            <h2 slot="title">阈值信息</h2>
            <Form :label-width="140" ref="threshold" :model="threshold" :rules="ruleValidate" inline :class="{'form-view':type=='view'}" @submit.prevent="submit">
                <Form-item v-if="threshold.id" :class="{'form-view': true}" label="站点">
                    <template>{{threshold.siteNO}} ： {{threshold.siteName}} </template>
                </Form-item>
                <Form-item v-else label="站点" prop="siteIds">
                    <Select v-model="threshold.siteIds" multiple @on-open-change="siteChange" placeholder="请选择站点名称">
                        <Option v-for="(item, index) in sites" :key="index" :value="item.id">{{item.siteCode}} - {{item.showName}}</Option>
                    </Select>
                </Form-item>

                <Form-item v-if="threshold.id" :class="{'form-view': true}" label="传感器信号">
                    <template>{{threshold.sensorName}} </template>
                </Form-item>
                <Form-item v-else label="传感器信号" prop="sensorId">
                    <Select v-model="threshold.sensorId" placeholder="请选择传感器信号">
                        <Option v-for="(item, index) in sensors" :key="index" :value="item.id">{{item.name}}</Option>
                    </Select>
                </Form-item>

                <Form-item v-if="type=='view'" label="站点位置:">
                    <template>{{threshold.sitePosition}}</template>
                </Form-item>
                <br/>

                <Form-item label="阈值类型" prop="typeId">
					<template v-if="type=='view'">{{threshold.typeName}}</template>
                    <Select v-else v-model="threshold.typeId" placeholder="请选择阈值类型">
                        <Option v-for="(item, index) in types" :key="index" :value="item.id">{{item.textName}}</Option>
                    </Select>
                </Form-item>

                <Form-item label="最小值（包含）" prop="minValue">
					<template v-if="type=='view'">{{threshold.minValue}}</template>
                    <InputNumber v-else v-model="threshold.minValue" :min="0" :max="999999" style="width:100%"></InputNumber>
                </Form-item>

                <Form-item label="最大值（不包含）" prop="maxValue">
					<template v-if="type=='view'">{{threshold.maxValue}}</template>
                    <InputNumber v-else v-model="threshold.maxValue" :min="0" :max="999999" style="width:100%"></InputNumber>
                </Form-item>

                <Form-item label="阈值单位" prop="unitId">
					<template v-if="type=='view'">{{threshold.unitName}}</template>
                    <Select v-else v-model="threshold.unitId" placeholder="请选择阈值类型">
                        <Option v-for="(item, index) in units" :key="index" :value="item.id">{{item.textName}}</Option>
                    </Select>
                </Form-item>

                <Form-item label="显示颜色" prop="color">
                    <span v-if="type=='view'" style="display:inline-block;width: 80px; height: 18px;" :style="{'background-color': threshold.color}"></span>
                    <Select v-else v-model="threshold.color" :style="{color:threshold.color}">
                        <Option v-for="(item, index) in colors" :label="item.name+' '+item.value" :value="item.value" :key="index">
                            <span style="display:inline-block;width:30px;height:15px;" :style="{'background-color':item.value}"></span>
                            <span>{{item.name}}</span>   
                        </Option>
                    </Select>
                </Form-item>
                
                <Form-item label="备注" class="singleline">
                    <template v-if="type=='view'">{{threshold.remark}}</template>
                    <Input v-else v-model.trim="threshold.remark" type="textarea" maxlength="250" placeholder="请输入备注，不超过250字" />
                    <div class="str_count" v-if="type!=='view'">{{threshold.remark ? threshold.remark.length : 0}}/250</div>
                </Form-item>
            </Form>
        </Card>

        <Card >
			<h2 slot="title">当前站点阈值信息</h2>
            <data-grid :option="gridOption" ref="grid"></data-grid>
		</Card>
		
        <div style="text-align: center;" slot="footer">
            <Button type="primary" class="ivu-btn ivu-btn-primary" v-if="type!='view'" :loading="loading" @click="save">保存</Button>
            <Button type="ghost" style="margin-left: 8px" v-if="type!='view'" @click="$router.back()">取消</Button>
            <Button type="ghost" style="margin-left: 8px" v-if="type=='view'" @click="$router.back()">返回</Button>
        </div>
        </div>
    </div>
</template>
<script>
import dataGrid from "@/components/datagrid"
export default {
    components: { dataGrid },
     data: function() {
        var that = this;
		return {
            loading: false,
            isOpenSite: false, //站点下拉是否打开
            isSiteChange: false, //站点是否变化
            id: '',
            type: '',
            sites: [], //站点下拉数据
            sensors: [], //传感器信号下拉数据
            types: [], //阈值类型下拉数据
            units: [], //阈值单位
            types_: {}, ///类型数组转对象用于列表数据赋值
            units_: {}, ///单位数组转对象用于列表数据赋值
            colors: [{ //显示颜色
                        id:"1",
                        name:" 绿色",
                        value:"#19c919"
                    },{
                        id:"2",
                        name:" 蓝色",
                        value:"#3366FF"
                    },{
                        id:"3",
                        name:" 黄色",
                        value: "#F2E92A"
                    },{
                        id:"4",
                        name:" 橙色",
                        value:"#FD9800"
                    },{
                        id:"5",
                        name:" 红色",
                        value:"#D92D29"
                    }], 
            threshold: {
                id: '',
                siteIds: [],
                siteId: '',
				color: '',
                sensorId: '',
                position: 1,
                typeId: '',
                unitId: '',
                minValue: 0,
                maxValue: 0,
                remark: ''
            },
            ruleValidate: {
                siteIds: [
                    { type: 'array', required: true, message: '站点不能为空', trigger: 'change' },
                ],
                sensorId: [
                    { type: 'number', required: true, message: '传感器信号不能为空', trigger: 'change' },
                ],
                typeId: [
                    { type: 'number', required: true, message: '阈值类型不能为空', trigger: 'change' },
                ],
                minValue: [
                    { required: true, type: 'float', validator: this.checkMinValue, trigger: 'blur' }
                ],
                maxValue: [
                    { required: true, type: 'float', validator: this.checkMaxValue, trigger: 'blur' }
                ],
                unitId: [
                    { type: 'number', required: true, message: '阈值单位不能为空', trigger: 'change' },
                ],
                color: [
                    { type: 'string', required: true, message: '显示颜色不能为空', trigger: 'blur' },
                ]
            },
			gridOption:{
				"header":true,
				"iconCls": "fa-power-off",
                "data":[],
                "auto": false,
				"columns":[
                    {
						"title":"站点",
						"key": "siteName",
                        "align": "center",
                    },
					{
						"title":"阈值类别",
						"key": "typeName",
                        "align": "center",
                    },
                    {
						"title":"传感器信号",
						"key": "sensorName",
                        "align": "center",
                    },
                    {
						"title":"站点位置",
						"key": "sitePosition",
                        "align": "center"
                    },
                    {
						"title":"最小值",
						"key": "minValue",
                        "align": "center",
                    },
                    {
						"title":"最大值",
						"key": "maxValue",
                        "align": "center",
                    },
                    {
						"title":"阈值单位",
						"key": "unitName",
                        "align": "center",
                    },
                    {
						"title":"显示颜色",
						"key": "color",
                        "align": "center",
                        render(h, p){
                            if (p.row.color){
                                return h('div', [
                                    h('div', {
                                        style: {
                                            width: '100%',
                                            height: '20px',
                                            background: p.row.color
                                        }
                                    })
                                ]);
                            }else {
                                return h('span', "暂未选择颜色");
                            }
                        }
                    }
                ],
                "loadFilter": function(data) {
                    return data.data;
                },
                }
            }
        },
    methods: {
        //校验最小值小于最大值
        checkMinValue: function(rule, value, callback) {
            var minValue = value;
            var maxValue = this.threshold.maxValue;
            if ((minValue || 0 == minValue) && (maxValue || 0 != maxValue)) {
                if (parseFloat(minValue) < parseFloat(maxValue)) {
                    callback();
                } else {
                    callback("最小值应小于最大值");
                }
            } else {
                callback();
            }
        },
        //校验最大值大于最小值
        checkMaxValue: function(rule, value, callback) {
            var minValue = this.threshold.minValue;
            var maxValue = value;
            if((minValue || 0 == minValue) && (maxValue || 0 == maxValue)){
                if (parseFloat(minValue) < parseFloat(maxValue)) {
                    callback();
                } else {
                    callback("最大值应大于最小值");
                }
            }else{
                callback();
            }
        },
        siteChange: function(flag){
            this.isOpenSite = flag;
            if (!flag && this.isSiteChange){
                var siteIds = "";
                if (this.threshold.siteIds && this.threshold.siteIds.length > 0 && this.threshold.sensorId){
                    siteIds = this.threshold.siteIds.join("#");
                    this.getThresholdListBySiteAndSensor(siteIds, this.threshold.sensorId);
                    this.$refs.grid.load();
                }
            }
        },
        getThresholdListBySiteAndSensor: function (siteIds, sensorId) {
            this.$ajax.post("/service_basedata/threshold/getThresholdListBySiteAndSensor", {"siteIds": siteIds, "sensorId": sensorId})
                .then((res) => {
                    if (200 == res.data.code){
                        this.gridOption.data = [];
                        this.gridOption.data = res.data.thresholdList && res.data.thresholdList.map((obj) => {
                            if (this.id == obj.id){
                                obj.cellClassName = {
                                    "site": "row-cell",
                                    "type": "row-cell",
                                    "minValue": "row-cell",
                                    "maxValue": "row-cell",
                                    "unit": "row-cell",
                                };
                            }
                            obj.typeName = this.types_[obj.typeId] ? this.types_[obj.typeId].textName : "";
                            obj.unitName = this.units_[obj.unitId] ? this.units_[obj.unitId].textName : "";
                            return obj;
                        });
                        this.isSiteChange = false;
                    }
                })
                .catch((error) => {
                    if (!error.url){
                        console.info(error);
                    }
                });
        },
        save: function() {
            this.$refs['threshold'].validate((valid) => {
                if (valid) {
                    this.loading = true;
                    var scadaThresholdVO = this.threshold;
                    if (this.threshold.createTime){
                        scadaThresholdVO.createTime = new Date(this.threshold.createTime).Format("yyyy-MM-dd hh:mm:ss");
                    }
                    if (5 != this.threshold.sensorId){
                        scadaThresholdVO.position = 0;
                    }
                    var url = "/service_basedata/threshold/save";
                    if ("update" == this.type){
                        url = "/service_basedata/threshold/update";
                    }else {
                        scadaThresholdVO.siteIds = this.threshold.siteIds && this.threshold.siteIds.join("#");
                    }
                    this.$ajax.post(url, scadaThresholdVO)
                        .then(res => {
                            if (200 == res.data.code) {
                                this.$Message.success("保存成功");
                                this.$router.push("/threshold/list");
                            }
                            else if (400 == res.data.code){
                                res.data.siteIds.split("#").forEach(element => {
                                    this.threshold.siteIds.push(Number(element));
                                });
                                var siteIds = this.threshold.siteIds.join("#");
                                this.getThresholdListBySiteAndSensor(siteIds, this.threshold.sensorId);
                                this.$Message.info("阈值范围存在冲突");
                            }
                            this.loading = false;
                        })
                        .catch((err) => {
                            if (!err.url) {
                                this.loading = false;
                                console.info(err);
                            }
                        })
                }
            })
        },
        getData: function(){
            this.$ajax.post("/service_basedata/threshold/findById", {"id": this.id})
                .then(res => {
                    if (res.data.code == 200) {
                        let threshold = res.data.data;
                        if (threshold){
                            threshold.typeName = this.types_[threshold.typeId] ? this.types_[threshold.typeId].textName : "";
                            threshold.unitName = this.units_[threshold.unitId] ? this.units_[threshold.unitId].textName : "";
                            this.threshold = threshold;
                            this.getThresholdListBySiteAndSensor(threshold.siteId, threshold.sensorId);
                        }
                    }
                })
                .catch((err) => {
                    if (!err.url) {
                        console.info(err);
                    }
                })
        },
        ///获取站点
        getBaseData: function(){
            this.$ajax.post("/service_basedata/threshold/getBaseData")
                .then((res) => {
                    if (200 == res.data.code){
                        this.sites = res.data.scadaSiteList;
                        this.sensors = res.data.scadaParameterList;
                    }else {
                        this.$Message.error('基础数据获取失败');
                    }
                })
                .catch((err) => {if(!err.url){console.info(err);}});
        },
        ///获取字典数据
        getDic: function(){
            let dataTypeCode = "THRESHOLD_TYPE,THRESHOLD_UNIT";
            this.$ajax.post("/userservice/sysData/findByTypeCodes", {"dataTypeCode": dataTypeCode})
                .then((res) => {
                    if (200 == res.data.code){
                        this.types = res.data.data.THRESHOLD_TYPE;
                        this.units = res.data.data.THRESHOLD_UNIT;
                        if (this.types && this.types.length > 0){
                            this.types.forEach(e => {
                                this.types_[e.id] = e;
                            });
                        }
                        if (this.units && this.units.length > 0){
                            this.units.forEach(e => {
                                this.units_[e.id] = e;
                            });
                        }
                        if (this.id){
                            this.getData();
                        }
                    }else {
                        this.$Message.error('基础数据获取失败');
                    }
                })
                .catch((err) => {if(!err.url){console.info(err);}});
        },
    },
    created: function() {
        this.getDic();
        this.getBaseData();
        //获取id,type
        this.id = this.$route.params.id;
        this.type = this.$route.params.type;
    },
    mounted: function(){
        
    },
    watch: {
        "threshold.siteIds": function (n, o) {
            this.isSiteChange = true;
            if (n && n.length > 0 &&
                this.gridOption.data && this.gridOption.data.length > 0){
                this.gridOption.data = this.gridOption.data.filter((obj) => {
                    return n.indexOf(obj.siteId) != -1;
                });
            }else {
                this.gridOption.data = [];
            }
        },
        "threshold.sensorId": function (n, o) {
            if (!this.isOpenSite && this.threshold.sensorId){
                var siteIds = "";
                if (this.threshold.siteIds && this.threshold.siteIds.length > 0){
                    siteIds = this.threshold.siteIds.join("#")
                    this.getThresholdListBySiteAndSensor(siteIds, n);
                }
            }
        }
    },
}
</script>
