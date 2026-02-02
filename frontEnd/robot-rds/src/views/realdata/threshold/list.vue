<template>
    <div>
        <page-title></page-title>
        <div class="page-search">
            <div class="page-search-title">查询条件</div>
            <div class="page-search-content">
                <Form ref="s" v-model="s" :label-width="120">
                    <FormItem label="站点名称">
                        <Select v-model="s.siteId" placeholder="请选择站点名称">
                            <Option v-for="(item, index) in sites" :key="index" :value="item.id">{{item.siteCode}} - {{item.showName}}</Option>
                        </Select>
                    </FormItem>

                    <FormItem label="传感器类型">
                        <Select v-model="s.sensorId" placeholder="请选择传感器类型">
                            <Option v-for="(item, index) in sensors" :key="index" :value="item.id">{{item.name}}</Option>
                        </Select>
                    </FormItem>

                    <FormItem label="阈值类型">
                        <Select v-model="s.typeId" placeholder="请选择阈值类型">
                            <Option v-for="(item, index) in types" :key="index" :value="item.id">{{item.textName}}</Option>
                        </Select>
                    </FormItem>
                </Form>
                <div class="search-btn">
                    <allowBtn allow="allowsearch" icon="ios-search" @click="search">查询</allowBtn>
                    <allowBtn allow="allowreset" icon="ios-refresh-empty" @click="reset">重置</allowBtn>
                    <allowBtn allow="create" type="success" icon="plus" @click="$router.push('/threshold/create')">新增
                    </allowBtn>
                </div>
            </div>
        </div>
        <div class="page-content">
            <data-grid :option="gridOption" ref="grid"></data-grid>
        </div>
    </div>
</template>

<script>
    import dataGrid from "@/components/datagrid"

    export default {
        components: {dataGrid},
        data: function () {
            var that = this;
            return {
                sites: [], //站点下拉数据
                sensors: [], //传感器下拉数据
                types: [], //阈值类型下拉数据
                types_: {}, ///类型数组转对象用于列表数据赋值
                units_: {}, ///单位数组转对象用于列表数据赋值
                s: {
                    siteId: '',
                    sensorId: '',
                    typeId: ''
                },
                spinShow: true, //未进入页面时，加载过程中显示加载中
                "gridOption": {
                    "data": [],
                    "auto": false,
                    "url": "/service_basedata/threshold/list",
                    "columns": [  //列配置
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
                            "width": "100px",
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
                        },
                        {
                            key: 'tool', title: '操作', align: "center", width: 300, render(h, params) {
                                return h('div', [
                                    h('allowBtn', {
                                        props: {
                                            type: 'primary',
                                            size: 'small',
                                            allow: "view"
                                        },
                                        style: {
                                            marginRight: '5px'
                                        },
                                        on: {
                                            click: () => {
                                                that.toView(params.row);
                                            }
                                        }
                                    }, '查看'),
                                    h('allowBtn', {
                                        props: {
                                            type: 'primary',
                                            size: 'small',
                                            allow: "update"
                                        },
                                        style: {
                                            marginRight: '5px'
                                        },
                                        on: {
                                            click: () => {
                                                that.toUpdate(params.row);
                                            }
                                        }
                                    }, '修改'),
                                    h('allowBtn', {
                                        props: {
                                            type: 'info',
                                            size: 'small',
                                            allow: "delete"
                                        },
                                        on: {
                                            click: () => {
                                                that.del(params.row);
                                            }
                                        }
                                    }, '删除')
                                ]);
                            }
                        }],
                    "loadFilter": function (data) {
                        if (data.data && data.data.list && data.data.list.length > 0){
                            data.data.list.map(e => {
                                e.typeName = that.types_[e.typeId] ? that.types_[e.typeId].textName : "";
                                e.unitName = that.units_[e.unitId] ? that.units_[e.unitId].textName : "";
                            });
                        }
                        return data.data;
                    }
                }
            }
        },
        methods: {
            reset: function () {
                this.s.siteId = '';
                this.s.sensorId = '';
                this.s.typeId = '';
                this.$refs.grid.load();
            },
            search: function () {
                this.$refs.grid.load({
                    "siteId": this.s.siteId,
                    "sensorId": this.s.sensorId,
                    "typeId": this.s.typeId,
                });
            },
            //以下三个的行方法
            toUpdate: function (obj) {
                this.$router.push('/threshold/update/' + obj.id + "/update");
            },
            toView: function (obj) {
                this.$router.push('/threshold/view/' + obj.id + "/view");
            },
            del: function (obj) {
                let that = this;
                this.$Modal.confirm({
                    title: '删除',
                    content: '确认删除此条数据吗？',
                    onOk: function () {
                        // 跳转
                        this.$ajax.post("/service_basedata/threshold/delete", {'id': obj.id})
                            .then((res) => {
                                this.$Message.success('删除成功');
                                that.$refs['grid'].reLoad();
                            })
                            .catch((error) => {
                                if (!error.url) {
                                    console.info(error);
                                }
                            })
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
                let dataTypeCode = "THRESHOLD_SENSOR,THRESHOLD_TYPE,THRESHOLD_UNIT";
                this.$ajax.post("/userservice/sysData/findByTypeCodes", {"dataTypeCode": dataTypeCode})
                    .then((res) => {
                        if (200 == res.data.code){
                            this.types = res.data.data.THRESHOLD_TYPE;
                            let units = res.data.data.THRESHOLD_UNIT;
                            if (this.types && this.types.length > 0){
                                this.types.forEach(e => {
                                    this.types_[e.id] = e;
                                });
                            }
                            if (units && units.length > 0){
                                units.forEach(e => {
                                    this.units_[e.id] = e;
                                });
                            }
                            this.search();
                        }else {
                            this.$Message.error('基础数据获取失败');
                        }
                    })
                    .catch((err) => {if(!err.url){console.info(err);}});
            },
        },
        created: function () {

        },
        mounted: function(){
            this.getDic();
            this.getBaseData();
        },
    };

</script>
