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
                orgId: '',
                listModal: false,
                "userGridOption": {
                    auto: false,
                    "url": "/user/getlistByedu",
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
                        "title": "部门名称",
                        "key": "orgName",
                        "align": "center",
                        ellipsis: true
                    },
                    {
                        "title": "博士后",
                        "key": "y1",
                        "align": "center",
                        "render": function(h, params) {
                            return h('Button', {
                                props: {
                                    type: "text",
                                    size: "small"
                                },
                                on: {
                                    click: () => {
                                        that.clicEdu(params.row.orgName, "博士后")
                                    }
                                }
                            }, params.row.y1 ? params.row.y1 : '0');
                        }
                    },
                    {
                        "title": "博士",
                        "key": "y2",
                        "align": "center",
                        "render": function(h, params) {
                            return h('Button', {
                                props: {
                                    type: "text",
                                    size: "small"
                                },
                                on: {
                                    click: () => {
                                        that.clicEdu(params.row.orgName, "博士")
                                    }
                                }
                            }, params.row.y2 ? params.row.y2 : '0');
                        }
                    },
                    {
                        "title": "硕士",
                        "key": "y3",
                        "align": "center",
                        "render": function(h, params) {
                            return h('Button', {
                                props: {
                                    type: "text",
                                    size: "small"
                                },
                                on: {
                                    click: () => {
                                        that.clicEdu(params.row.orgName, "硕士")
                                    }
                                }
                            }, params.row.y3 ? params.row.y3 : '0');
                        }
                    },
                    {
                        "title": "本科",
                        "key": "y4",
                        "align": "center",
                        "render": function(h, params) {
                            return h('Button', {
                                props: {
                                    type: "text",
                                    size: "small"
                                },
                                on: {
                                    click: () => {
                                        that.clicEdu(params.row.orgName, "本科")
                                    }
                                }
                            }, params.row.y4 ? params.row.y4 : '0');
                        }
                    },
                    {
                        "title": "专科",
                        "key": "y5",
                        "align": "center",
                        "render": function(h, params) {
                            return h('Button', {
                                props: {
                                    type: "text",
                                    size: "small"
                                },
                                on: {
                                    click: () => {
                                        that.clicEdu(params.row.orgName, "专科")
                                    }
                                }
                            }, params.row.y5 ? params.row.y5 : '0');
                        }
                    },
                    {
                        "title": "高中",
                        "key": "y6",
                        "align": "center",
                        "render": function(h, params) {
                            return h('Button', {
                                props: {
                                    type: "text",
                                    size: "small"
                                },
                                on: {
                                    click: () => {
                                        that.clicEdu(params.row.orgName, "高中")
                                    }
                                }
                            }, params.row.y6 ? params.row.y6 : '0');
                        }
                    },
                    {
                        "title": "初中",
                        "key": "y7",
                        "align": "center",
                        "render": function(h, params) {
                            return h('Button', {
                                props: {
                                    type: "text",
                                    size: "small"
                                },
                                on: {
                                    click: () => {
                                        that.clicEdu(params.row.orgName, "初中")
                                    }
                                }
                            }, params.row.y7 ? params.row.y7 : '0');
                        }
                    },
                    {
                        "title": "小学",
                        "key": "y8",
                        "align": "center",
                        "render": function(h, params) {
                            return h('Button', {
                                props: {
                                    type: "text",
                                    size: "small"
                                },
                                on: {
                                    click: () => {
                                        that.clicEdu(params.row.orgName, "小学")
                                    }
                                }
                            }, params.row.y8 ? params.row.y8 : '0');
                        }
                    },
                    {
                        "title": "未知",
                        "key": "y9",
                        "align": "center",
                        "render": function(h, params) {
                            return h('Button', {
                                props: {
                                    type: "text",
                                    size: "small"
                                },
                                on: {
                                    click: () => {
                                        that.clicEdu(params.row.orgName, "未知")
                                    }
                                }
                            }, params.row.y9 ? params.row.y9 : '0');
                        }
                    },
                    {
                        "title": "合计",
                        "key": "y10",
                        "align": "center",
                        "render": function(h, params) {
                            return h('Button', {
                                props: {
                                    type: "text",
                                    size: "small"
                                },
                                on: {
                                    click: () => {
                                        that.clicEdu(params.row.orgName, "")
                                    }
                                }
                            }, params.row.y10 ? params.row.y10 : '0');
                        }
                    }
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
                        // orient: 'vertical',
                        // left: 'left',
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
                        radius: '55%',
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
                xAxis: [],
                yAxis1: [],
                yAxis2: [],
                yAxis3: [],
                yAxis4: [],
                yAxis5: [],
                yAxis6: [],
                yAxis7: [],
                yAxis8: [],
                yAxis9: [],
                orgTree: [],
                list: [],
                search: {
                    orgId: "",
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
            clicEdu: function(orgName, eduName) {
                this.listModal = true;
                if (!orgName) {
                    orgName = "";
                }
                if (!eduName) {
                    eduName = "";
                }
                this.$refs.userGrid.load({
                    "orgName": orgName,
                    "eduName": eduName,
                });

            },
            hjClick: function(param) {
                this.listModal = true;
                if (param) {
                    this.$refs.userGrid.load({
                        "eduName": param,
                        "orgId": this.orgId
                    });
                } else {
                    this.$refs.userGrid.load({ "orgId": this.orgId });
                }

            },
            getOrgTree: function() {
                this.$ajax.post("/service_user/organization/getOrgTree", { "type": 4 })
                    .then(res => {
                        if (res.data.code == 200) {
                            this.orgTree = this.$utils.Flat2TreeDataForCascader(res.data.data);
                            this.orgTreeData = res.data.data;
                        }
                    })
                    .catch((error) => {
                        if (!error.url) { console.info(error); }
                    })
            },
            onClick: function(name) {
                this.initChart();
            },
            importFile: function() {
                var data = "";
                if (this.list && this.list.length > 0) {
                    if (this.selection && this.selection.length > 0) {
                        //算合计
                        var obj = {
                            orgName: '合计',
                            y1: 0,
                            y2: 0,
                            y3: 0,
                            y4: 0,
                            y5: 0,
                            y6: 0,
                            y7: 0,
                            y8: 0,
                            y9: 0,
                            y10: 0,
                        };
                        this.selection.forEach((j, i) => {
                            obj.y1 = obj.y1 + (j.y1 ? Number(j.y1) : 0);
                            obj.y2 = obj.y2 + (j.y2 ? Number(j.y2) : 0);
                            obj.y3 = obj.y3 + (j.y3 ? Number(j.y3) : 0);
                            obj.y4 = obj.y4 + (j.y4 ? Number(j.y4) : 0);
                            obj.y5 = obj.y5 + (j.y5 ? Number(j.y5) : 0);
                            obj.y6 = obj.y6 + (j.y6 ? Number(j.y6) : 0);
                            obj.y7 = obj.y7 + (j.y7 ? Number(j.y7) : 0);
                            obj.y8 = obj.y8 + (j.y8 ? Number(j.y8) : 0);
                            obj.y9 = obj.y9 + (j.y9 ? Number(j.y9) : 0);
                            obj.y10 = obj.y10 + (j.y10 ? Number(j.y10) : 0);
                        });

                        this.selection.push(obj);
                        data = JSON.stringify(this.selection);
                    } else {
                        data = JSON.stringify(this.list);
                    }
                    this.$utils.download({
                        name: "员工学历统计.xls",
                        url: "/reporthuman/importStatisticsEducation",
                        postData: {
                            "objects": data,
                        }
                    })
                } else {
                    this.$Message.error("暂未查到数据");
                    return;
                }
            },
            //根据查询条件
            search2: function() {
                this.selection = [];
                this.initChart();
            },
            //清除缓存数据
            clear: function() {
                this.carName = '';
                this.userName = '';
            },
            //取消操作
            cal: function() {
                this.search.orgId = "";
            },
            /*创建图表一*/
            createChartOne: function() {
                this.$nextTick(() => {
                    this.myChart1 = this.echarts.init(this.$refs.charts, 'light');
                    // 使用刚指定的配置项和数据显示图表。
                    this.option1.series[0].data = this.seriesData;
                    this.myChart1.setOption(this.option1);
                })
                this.myChart.clear();
                var option = {
                    /* title : {
                      text: this.text,
             }, */
                    color: ['#217AD4', '#3FC204', "#B8D432", "#FFB900", "#10B9E4", "#0E1EE3", "#A137D7", "#5849E0"],
                    tooltip: {
                        trigger: 'axis'
                    },
                    legend: {

                    },
                    toolbox: {
                        show: true,
                        feature: {
                            mark: { show: true },
                            dataView: { show: false, readOnly: false },
                            magicType: { show: true, type: ['line', 'bar'] },
                            restore: { show: false },
                            saveAsImage: { show: true },

                        }
                    },
                    dataZoom: [{ // 第一个 dataZoom 组件
                        xAxisIndex: [0],
                        end: 50
                    }],
                    calculable: true,
                    xAxis: [{
                        type: 'category',
                        data: this.xAxis,
                        axisLabel: { interval: 0 }
                    }],
                    yAxis: [{
                        type: 'value'
                    }],
                    series: [{
                            name: '博士后',
                            type: 'bar',
                            barMaxWidth: '30',
                            stack: '人数',
                            data: this.yAxis1,
                        },
                        {
                            name: '博士',
                            type: 'bar',
                            barMaxWidth: '30',
                            stack: '人数',
                            data: this.yAxis2,
                        },
                        {
                            name: '硕士',
                            type: 'bar',
                            barMaxWidth: '30',
                            stack: '人数',
                            data: this.yAxis3,
                        },
                        {
                            name: '本科',
                            type: 'bar',
                            barMaxWidth: '30',
                            stack: '人数',
                            data: this.yAxis4,
                        },
                        {
                            name: '专科',
                            type: 'bar',
                            barMaxWidth: '30',
                            stack: '人数',
                            data: this.yAxis5,
                        },
                        {
                            name: '高中',
                            type: 'bar',
                            barMaxWidth: '30',
                            stack: '人数',
                            data: this.yAxis6,
                        },
                        {
                            name: '初中',
                            type: 'bar',
                            stack: '人数',
                            data: this.yAxis7,
                        },
                        {
                            name: '小学',
                            type: 'bar',
                            barMaxWidth: '30',
                            stack: '人数',
                            data: this.yAxis8,
                        },
                        {
                            name: '未知',
                            type: 'bar',
                            barMaxWidth: '30',
                            stack: '人数',
                            data: this.yAxis9,
                        },
                    ]
                };
                // 使用刚指定的配置项和数据显示图表。
                this.myChart.setOption(option);
            },
            initChart: function() {
                if (!this.myChart) {
                    this.myChart = this.echarts.init(document.getElementById('main'), 'light');
                }
                this.myChart.showLoading();
                var orgId = "";
                if (this.search.orgId.length > 0) {
                    orgId = this.search.orgId[this.search.orgId.length - 1];
                }
                if (this.search.type === "") {
                    this.search.type = 1;
                }
                if (this.search.type == 1) {
                    this.text = "人员占比";
                } else {
                    this.text = "人数";
                }
                this.orgId = orgId;
                this.$ajax.post("/reporthuman/getHumanStatisticsEducationData", { "orgId": orgId })
                    .then(res => {
                        // this.gridOption.data = res.data.list;
                        this.list = res.data.list;
                        this.xAxis = res.data.xAxis;
                        this.yAxis1 = res.data.yAxis1;
                        this.yAxis2 = res.data.yAxis2;
                        this.yAxis3 = res.data.yAxis3;
                        this.yAxis4 = res.data.yAxis4;
                        this.yAxis5 = res.data.yAxis5;
                        this.yAxis6 = res.data.yAxis6;
                        this.yAxis7 = res.data.yAxis7;
                        this.yAxis8 = res.data.yAxis8;
                        this.yAxis9 = res.data.yAxis9;
                        this.seriesData = res.data.bList;
                        this.option1.legend.data = res.data.titleList;
                        if (res.data.list) {
                            if (res.data.list.length > 10) {
                                this.height = 477;
                            } else {
                                this.height = 48 * res.data.list.length;
                            }
                        }
                        this.tableList = res.data.tableList;
                        this.sumList = res.data.sumList;
                        this.createChartOne();
                    })
                this.myChart.hideLoading();
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
            this.getOrgTree();
        },

    };

</script>
