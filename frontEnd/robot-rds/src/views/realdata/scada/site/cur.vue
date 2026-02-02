<template>
    <div>
        <Form :label-width="140" ref="site" :model="site" :rules="ruleValidate" :class="{'form-view':type=='view'}">
            <Form-item label="站点编号" prop="siteCode">
                <template v-if="type=='view'">{{site.siteCode}}</template>
                <Input v-else v-model.trim="site.siteCode" maxlength="50" placeholder="请输入站点编码，不超过50个字符"></Input>
            </Form-item>

            <Form-item label="站点名称" prop="showName">
                <template v-if="type=='view'">{{site.showName}}</template>
                <Input v-else v-model.trim="site.showName" maxlength="50" placeholder="请输入展示名称，不超过50个字符"></Input>
            </Form-item>

            <Form-item label="站点位置">
                <template v-if="type=='view'">{{site.location}}</template>
                <Input v-else v-model.trim="site.location" maxlength="50" placeholder="请输入站点位置，不超过50个字符"></Input>
            </Form-item>

            <Form-item label="坐标（WGS84）">
                <template v-if="type=='view'">{{site.coordinate84}}</template>
                <Input v-else v-model.trim="site.coordinate84" style="width:70%" placeholder="请输入坐标" />
                <!-- <Button v-if="type=='view'" type="primary" @click="showGisMap('view')" style="width:25%;float:right;margin-top:1px">展示位置</Button> -->
                <!-- <Button v-else type="primary" @click="showGisMap('update')" style="width:25%;float:right;margin-top:1px">选择位置</Button> -->
            </Form-item>

            <Form-item label="备注">
                <template v-if="type=='view'">{{site.remark}}</template>
                <Input v-else v-model.trim="site.remark" type="textarea" maxlength="250" placeholder="请输入备注，不超过250个字符"></Input>
            </Form-item>
        </Form>

        <!-- 地图-->
        <Modal v-model="gisMap" title="位置信息" width="65%">
            <div class="result">
                位置: <input id="cityName" type="text"/>
                <Icon class="search" type="md-search" size="80px" @click="theLocation" />
                <div id="searchResultPanel" style="border:1px solid #C0C0C0;width:150px;height:auto; display:none;"></div>
            </div>
            <ul class = "drawing-panel">
                <li v-if="'view'!=type" class="bmap-btn" id="marker" @click="draw"></li>
                <li><span class="coor-text"><Icon type="md-pin" />&nbsp;&nbsp;{{coordinate}}</span></li>
            </ul>
            <div id="bmap" style="height:620px;"></div>
            <div slot="footer">
                <Button v-if="'view'!=type" type="primary" class="ivu-btn ivu-btn-primary" @click="getCoordinate">确认</Button>
                <Button type="ghost" class="ivu-btn ivu-btn-primary" @click="gisMap=false">关闭</Button>
            </div>
        </Modal>
    </div>
</template>
<script>
    export default {
        props:["id", "type"],
        data() {
            return {
                gisMap: false,  //地图modal
                bmap: null,  //地图实例
                pickup: false,  //拾取
                coordinate: '',  //拾取的坐标值
                suggestName: '', //位置搜索
                site: { //站点
                    id: '',
                    siteCode: '',
                    showName: '',
                    location: '',
                    coordinate84: '',
                },
                ruleValidate: {
                    siteCode: [
                        { required: true, message: '站点编码不能为空', trigger: 'blur' },
                        { validator: this.checkSiteCode, trigger: 'blur' },
                    ],
                    showName: [
                        { required: true, message: '站点名称不能为空', trigger: 'blur' },
                        { validator: this.checkShowName, trigger: 'blur' },
                    ]
               }
           }
        },
        methods: {
            //校验编码有效性
            checkSiteCode: function(rule, value, callback){
                if(value){
                    this.$ajax.post("/service_basedata/site/checkSiteCode", {'siteCode': value, 'id': this.id})
                    .then((res) => {
                        if (res.data.flag){
                            callback();
                        } else {
                            callback("编码已存在");
                        }
                    })
                    .catch((error) => {
                        callback('校验失败');
                    })
                } else {
                    callback("请输入站点编号");
                }
            },
            //校验名称唯一性
            checkShowName: function(rule, value, callback){
                if(value){
                    this.$ajax.post("/service_basedata/site/checkShowName", {'showName': value, 'id': this.id})
                    .then((res) => {
                        if (res.data){
                            callback();
                        } else {
                            callback("展示名已存在");
                        }
                    })
                    .catch((error) => {
                        callback('校验失败');
                    })
                } else {
                    callback("请输入展示名称");
                }
            },
            save(cb){
                this.$refs['site'].validate((valid) => {
                    if (valid) {
                        var scadaSiteVO = this.site;
                        this.$ajax.post("/service_basedata/site/save", scadaSiteVO)
                        .then((res) => {
                            if (200 == res.data.code){
                                cb && cb(true);
                                this.$refs['site'].resetFields();
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
                this.$ajax.post("/service_basedata/site/findById", {"id": this.id})
                    .then((res) => {
                        this.site = res.data.data;
                     })
                    .catch((err) => {
                        if(!err.url){console.info(err);}
                   })
            },
            resetFields: function() {
                this.site.location = "";
                this.site.coordinate84 = "";
                this.$refs.site.resetFields();
            },

            //弹出GIS
            showGisMap: function(flag){
                this.gisMap = true;
                this.$nextTick(()=>{
                    if(!this.bmap){
                        this.initMap();
                    }
                })
            },
            //初始化地图
            initMap: function(){
                // GL版命名空间为BMapGL
                // 按住鼠标右键，修改倾斜角和角度
                var x = 108.953077, y = 34.266161;
                
                if (this.site.coordinate84){
                    this.coordinate = this.site.coordinate84;
                    var poi = this.coordinate.split(",");
                    x = poi[0], y = poi[1];
                }
                this.bmap = new BMapGL.Map("bmap");  //创建baidu地图实例
                var point = new BMapGL.Point(x, y);  //设置地图中心点坐标
                this.bmap.centerAndZoom(point, 12);  //初始化地图，设置中心点坐标和地图级别
                this.drawPoint(x, y);
                this.bmap.enableScrollWheelZoom(true);  //开启鼠标滚轮缩放
                var element = document.getElementById('marker');
                if (element){
                    element.style.backgroundPositionY = '0';
                    this.pickup = true;
                }
            },
            //点击拾取点
            draw: function(event){
                var element = event.target;
                var eleMap = document.getElementById('mask');
                if (this.pickup){
                    element.style.backgroundPositionY = '-52px';
                    eleMap.style.cursor = 'crosshair';
                    var that = this;
                    this.bmap.addEventListener('click', (e) => {
                        this.coordinate = e.latlng.lng + ',' + e.latlng.lat;
                        this.drawPoint(e.latlng.lng, e.latlng.lat);
                    });
                    this.pickup = false;
                }else {
                    element.style.backgroundPositionY = '0';
                    eleMap.style.cursor = 'pointer';
                    this.bmap.removeEventListener('click');
                    this.pickup = true;
                }
            },
            //绘制点
            drawPoint: function(x, y){
                var point = new BMapGL.Point(x, y);  //设置点坐标
                var marker = new BMapGL.Marker(point);  //创建标注   
                this.bmap.clearOverlays();  //移除上一个标注
                this.bmap.addOverlay(marker);  //创建新的标注
            },
            //根据地名进行搜索
            theLocation: function(){
                var city = document.getElementById("cityName").value;
                if(city){
                    this.bmap.centerAndZoom(city, 15);  //用城市名设置地图中心点
                }
            },
            //坐标赋值
            getCoordinate: function(){
                this.site.coordinate84 = this.coordinate;
                this.gisMap = false;
            },
        },
        created: function() {
            
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

<style scoped>
    ul li {list-style: none;}
    .drawing-panel {
        z-index: 999;
        position: absolute;
        top: 67px;
        height: 47px;
        box-shadow: 0 2px 6px 0 rgba(27, 142, 236, 0.5);
    }
    .bmap-btn {
        width: 64px;
        height: 100%;
        float: left;
        cursor: pointer;
        background-image: url(//api.map.baidu.com/library/DrawingManager/1.4/src/bg_drawing_tool.png);
    }
    .coor-text {
        background-color: aqua;
        width: 300px;
        line-height: 47px;
        position: absolute;
    }
    .result {
        z-index: 99;
        padding: 7px 10px;
        position: absolute;
        top: 67px;
        right: 17px;
        width: 300px;
        background: #fff;
        box-shadow: 0 2px 6px 0 rgba(27, 142, 236, 0.5);
        border-radius: 7px;
    }
    #cityName {
        width: 200px; 
        margin-right: 10px;
        height: 25px;
        border: 1px solid rgba(27, 142, 236, 0.5);
        border-radius: 5px;
    }
    .search {
        position: absolute;
        top: 13px;
        right: 17px;
    }
</style>