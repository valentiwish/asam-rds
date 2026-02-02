<template>
    <div>
        <page-title></page-title>
        <div class="page-search">
        <div class="page-search-content">
           <Form :model="search" :label-width="100">
            
                <Form-item label="部门名称" >
                    <Cascader  :data="orgTree" change-on-select v-model="search.orgId"    placeholder="请选择部门名称"></Cascader>
                </Form-item>
                <div class="search-btn" >
                    <allowBtn allow="allowsearch"  @click="search2()">查询</allowBtn>
                    <allowBtn allow="allowreset"   @click="cal()">重置</allowBtn>
        <allowBtn allow="export" icon="plus" @click="importFile()">导出</allowBtn >
                </div>
            </Form>
        </div>
    </div>


    <div class="page-content">
        <Row>
            <Col span="8">
                <div ref="charts" style="height:400px;position: relative;">
                    <div ref="chart" id="main" style=" width:100%;height:400px"></div>
                </div>
            </Col>
            <Col span="16">
                <div>
                    <Table :columns="columns" :data="tableList" border show-summary :summary-method="handleSummary" :height="height"  @on-selection-change="selectChange"></Table>
                </div>
            </Col>
        </Row>
    </div>
    <Modal v-model="listModal" width="1500" title="人员列表">
        <div class="page-content">
            <data-grid :option="userGridOption" ref="userGrid"></data-grid>
        </div>
        <div slot="footer">
            <Button type="ghost" style="margin-left: 8px" @click="listModal=false">关闭</Button>
        </div>
    </Modal>
    </div>
</template>

<script>
    import dataGrid from "@/components/datagrid"

    export default {
        components: { dataGrid },
        data: function() {
            var that = this;
            return {
                selection: [],
                postId: '',
                listModal: false,
                "userGridOption": {
                    auto: false,
                    "url": "/user/getlist",
                    "data": [],
                    "columns": [ //列配置
                        {
                            "title": "工号",
                            "key": "jobNumber",
                            "align": "center"
                        }, {
                            "title": "姓名",
                            "key": "userName",
                            "align": "center"
                        }, {
                            "title": "手机号",
                            "key": "phone",
                            "align": "center"
                        }, {
                            "title": "性别",
                            "key": "sex",
                            "align": "center",
                            "render": function(a, param) {
                                if (param.row.sex) {
                                    return param.row.sex.codeText;
                                }
                            }
                        }, {
                            "title": "出生日期",
                            "key": "birthday",
                            "align": "center",
                            "render": function(a, param) {
                                if (param.row.birthday) {
                                    return param.row.birthday.substring(0, 10);
                                }
                            }
                        }, {
                            "title": "身份证号",
                            "key": "cardID",
                            "align": "center",
                            "ellipsis": true
                        }, {
                            "title": "所在机构",
                            "key": "orgName",
                            "align": "center",
                            "ellipsis": true
                        }, {
                            "title": "岗位",
                            "key": "postName",
                            "align": "center",
                            "ellipsis": true
                        }, {
                            key: 'tool',
                            title: '操作',
                            align: "center",
                            width: 300,
                            render(h, params) {
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
                                ]);
                            }
                        }
                    ],
                    "loadFilter": function(data) {
                        if (data.data && data.data.list) {
                            if (that.selection && that.selection.length > 0) {
                                data.data.list.forEach((k, m) => {
                                    that.selection.some((j, i) => {
                                        if (j.id == k.id) {
                                            k._checked = true;
                                            data.data.list.splice(m, 1, k);
                                            return true;
                                        } else {
                                            return false;
                                        }
                                    })
                                })
                            }
                        }
                        return data.data;
                    }
                },
                height: 0,
                "columns": [ //列配置
                    {
                        type: 'selection',
                        width: 60,
                        align: 'center',
                    },
                    {
                        "title": "岗位名称",
                        "key": "name",
                        "align": "center",
                    },
                    {
                        "title": "人数",
                        "key": "count",
                        "align": "center",
                        "render": function(h, params) {
                            return h('Button', {
                                props: {
                                    type: "text",
                                    size: "small"
                                },
                                on: {
                                    click: () => {
                                        that.clicPost(params.row.name)
                                    }
                                }
                            }, params.row.count);
                        }
                    },
                ],
                tableList: [],
                sumList: [],
                myChart1: null,
                seriesData: [],
                option1: {
                    title: {
                        text: '',
                        subtext: '',
                        x: 'center'
                    },
                    tooltip: {
                        trigger: 'item',
                        formatter: "{a} <br/>{b} : {c} ({d}%)"
                    },
                    legend: {
                        data: []
                    },
                    toolbox: {
                        show: true,
                        feature: {
                            dataView: { show: true, readOnly: false },
                            magicType: { show: false, type: ['line', 'bar'] },
                            restore: { show: true },
                            saveAsImage: { show: true }
                        }
                    },
                    series: [{
                        name: '',
                        type: 'pie',
                        radius: '60%',
                        center: ['50%', '60%'],
                        label: {
                            formatter: '{b}:{c}人\n占比:{d}%'
                        },
                        data: [],
                        itemStyle: {
                            emphasis: {
                                shadowBlur: 10,
                                shadowOffsetX: 0,
                                shadowColor: 'rgba(0, 0, 0, 0.5)'
                            }
                        }
                    }]
                },
                tabVal: "0",
                flag: true, //是否有数据
                myChart: null,
                text: "",
                org: [], //横坐标部门
                res: [], //部门人员占比
                list: [],
                postList: [], //岗位列表
                xAxis: [], //x轴
                yAxis: [], //y轴
                search: {
                    post: { id: '' },
                },
            }
        },
        methods: {
            handleSummary({ columns, data }) {
                const sums = {};
                columns.forEach((column, index) => {
                    const key = column.key;
                    if (index === 1) {
                        sums[key] = {
                            key,
                            value: '合计'
                        };
                        return;
                    }
                    const values = data.map(item => Number(item[key]));
                    if (!values.every(value => isNaN(value))) {
                        const v = values.reduce((prev, curr) => {
                            const value = Number(curr);
                            if (!isNaN(value)) {
                                return prev + curr;
                            } else {
                                return prev;
                            }
                        }, 0);
                        sums[key] = {
                            key,
                            value: v
                        };
                    } else {
                        sums[key] = {
                            key,
                            value: ''
                        };
                    }
                });

                return sums;
            },
            selectChange: function(selection) {
                this.selection = [];
                if (selection && selection.length > 0) {
                    this.selection = selection;
                }
            },
            toView: function(obj) {
                this.$router.push('/user/view/' + obj.id);
            },
            clicPost: function(name) {
                this.listModal = true;
                if (!name) {
                    name = "";
                }
                this.$refs.userGrid.load({
                    "postName": name,
                });

            },
            hjClick: function() {
                this.listModal = true;
                this.$refs.userGrid.load({
                    "postId": this.postId,
                });
            },
            getInitData: function() {
                this.$ajax.post("/reporthuman/getInitData", { "type": 4 })
                    .then(res => {
                        if (res.data.code == 200) {
                            this.postList = res.data.postList;
                        }
                    })
                    .catch((error) => {
                        if (!error.url) { console.info(error); }
                    })
            },
            onClick: function(name) {
                this.initChart();

            },
            //根据查询条件
            search2: function() {
                this.selection = [];
                this.initChart();
            },
            importFile: function() {
                var data = "";
                if (this.list && this.list.length > 0) {
                    if (this.selection && this.selection.length > 0) {
                        //算合计
                        var obj = {
                            name: '合计',
                            count: 0,
                        };
                        this.selection.forEach((j, i) => {
                            obj.count = obj.count + Number(j.count);
                        });
                        this.selection.push(obj);
                        data = JSON.stringify(this.selection);
                    } else {
                        data = JSON.stringify(this.list);
                    }
                    this.$utils.download({
                        name: "清远水务员工岗位统计.xls",
                        url: "/reporthuman/importStatisticsPost",
                        postData: {
                            "objects": data,
                        }
                    })
                } else {
                    this.$Message.error("暂未查到数据");
                    return;
                }
            },
            //清除缓存数据
            clear: function() {
                this.search.post = {};
            },
            //取消操作
            cal: function() {
                this.search.post = {};
            },
            /*创建图表一*/
            createChartOne: function() {
                this.$nextTick(() => {
                    this.myChart1 = this.echarts.init(this.$refs.charts, 'light');
                    // 使用刚指定的配置项和数据显示图表。
                    this.option1.series[0].data = this.seriesData;
                    this.myChart1.setOption(this.option1);
                })
                if (!this.myChart) {
                    this.myChart = this.echarts.init(document.getElementById('main'));
                }
                this.myChart.clear();
                var option = {
                    color: ['#28A7DE', '#FF9900'],
                    // title : {
                    //  text: this.text,
                    // },
                    tooltip: {
                        trigger: 'axis'
                    },
                    // dataZoom: [
                    //    { // 第一个 dataZoom 组件
                    //        xAxisIndex: [0] ,// 表示这个 dataZoom 组件控制 第一个 和 第三个 xAxis
                    //        end:50
                    //    }
                    // ],
                    legend: {
                        //data:['男性','女性']
                    },

                    toolbox: {
                        show: true,
                        feature: {
                            mark: { show: true },
                            dataView: { show: false, readOnly: false },
                            magicType: { show: true, type: ['line', 'bar'] },
                            restore: { show: false },
                            saveAsImage: { show: true },
                            dataZoom: { show: false, xAxisIndex: [0, 1] }
                        }
                    },

                    calculable: true,
                    xAxis: [{
                        type: 'category',
                        data: this.xAxis,
                        axisLabel: {
                            interval: 0,
                            rotate: 40
                        }
                    }],
                    yAxis: [{
                        type: 'value'
                    }],
                    series: [{
                            name: '人数',
                            type: 'bar',
                            stack: '人数',
                            barMaxWidth: '30',
                            data: this.yAxis,

                        },
                        //                {
                        // name:'女性',
                        // type:'bar',
                        // stack: '人数',
                        // data:this.resFemale,
                        //                }
                    ]
                };
                // 使用刚指定的配置项和数据显示图表。
                this.myChart.setOption(option);
            },
            initChart: function() {
                var postId = "";
                if (this.search.post && this.search.post.id) {
                    postId = this.search.post.id;
                }
                this.postId = postId;
                this.$ajax.post("/reporthuman/getHumanStatisticsPostData", { "postId": postId })
                    .then(res => {
                        this.xAxis = res.data.xAxis;
                        this.yAxis = res.data.yAxis;
                        this.list = res.data.data;
                        //this.gridOption.data = res.data.data;
                        this.seriesData = res.data.bList;
                        this.option1.legend.data = res.data.titleList;
                        if (res.data.data) {
                            if (res.data.data.length > 10) {
                                this.height = 477;
                            } else {
                                this.height = 48 * res.data.data.length;
                            }
                        }
                        this.tableList = res.data.tableList;
                        this.sumList = res.data.sumList;
                        this.createChartOne();
                    })
            }
        },
        mounted: function() {
            var that = this;
            require(['echarts'], (echarts) => {
                this.echarts = echarts;
                this.initChart();
            })
        },
        created: function() {
            this.getInitData();
        },

    };

</script>
