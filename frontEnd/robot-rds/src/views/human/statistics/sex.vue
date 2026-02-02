<template>
    <div>
        <page-title></page-title>
        <div class="page-search">
        <div class="page-search-content">
           <Form :model="search" :label-width="100">
            
                <Form-item label="部门名称" >
                    <Cascader  :data="orgTree" change-on-select v-model="search.orgId" placeholder="请选择部门名称"></Cascader>
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
                    "url": "/service_user/user/getlist",
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
                columns: [ //列配置
                    {
                        type: 'selection',
                        width: 60,
                        align: 'center',
                    },
                    {
                        "title": "部门名称",
                        "key": "name",
                        "align": "center",
                        ellipsis: true
                    }, {
                        "title": "男性",
                        "key": "m",
                        "align": "center",
                        ellipsis: true,
                        "render": function(h, params) {
                            return h('Button', {
                                props: {
                                    type: "text",
                                    size: "small"
                                },
                                on: {
                                    click: () => {
                                        that.clickm(params.row)
                                    }
                                }
                            }, params.row.m);
                        }

                    }, {
                        "title": "女性",
                        "key": "f",
                        "align": "center",
                        ellipsis: true,
                        "render": function(h, params) {
                            return h('Button', {
                                props: {
                                    type: "text",
                                    size: "small"
                                },
                                on: {
                                    click: () => {
                                        that.clickf(params.row)
                                    }
                                }
                            }, params.row.f);
                        }
                    }, {
                        "title": "合计",
                        "key": "mf",
                        "align": "center",
                        ellipsis: true,
                        "render": function(h, params) {
                            return h('Button', {
                                props: {
                                    type: "text",
                                    size: "small"
                                },
                                on: {
                                    click: () => {
                                        that.clickmf(params.row)
                                    }
                                }
                            }, params.row.mf);
                        }
                    }
                ],
                tableList: [],
                sumList: [],
                seriesData: [],
                tabVal: "0",
                flag: true, //是否有数据
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
                        data: ['男', '女']
                    },
                    toolbox: {
                        show: true,
                        feature: {
                            dataView: {
                                show: true,
                                readOnly: false
                            },
                            magicType: {
                                show: false,
                                type: ['line', 'bar']
                            },
                            restore: {
                                show: true
                            },
                            saveAsImage: {
                                show: true
                            }
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
                myChart1: null,
                myChart: null,
                text: "",
                org: [], //横坐标部门
                res: [], //部门人员占比
                list: [],
                resMan: [], //部门男性人员占比
                resFemale: [], //部门女性人员占比
                orgTree: [],
                search: {
                    orgId: "",
                    type: "1"
                },
            }
        },
        methods: {
            //合计行
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
            //选择点击事件
            selectChange: function(selection) {
                this.selection = [];
                if (selection && selection.length > 0) {
                    this.selection = selection;
                }
            },
            //查看人员详细情况
            toView: function(obj) {
                this.$router.push('/user/view/' + obj.id);
            },
            //男性员工数的点击事件
            clickm: function(obj) {
                this.listModal = true;
                this.getUserList(1, obj);

            },
            //女性员工数的点击事件
            clickf: function(obj) {
                this.listModal = true;
                this.getUserList(2, obj);

            },
            //合计员工数的点击事件
            clickmf: function(obj) {
                this.listModal = true;
                this.getUserList(3, obj);

            },
            //获取相应的员工信息
            getUserList: function(type, obj) {
                if (1 == type) {
                    //男性
                    this.$refs.userGrid.load({
                        "sexName": "男",
                        "orgName": obj.name
                    });

                } else if (2 == type) {
                    //女性
                    this.$refs.userGrid.load({
                        "sexName": "女",
                        "orgName": obj.name
                    });

                } else if (3 == type) {
                    //合计
                    this.$refs.userGrid.load({
                        "orgName": obj.name
                    });

                }

            },
            //获取部门树结构
            getOrgTree: function() {
                this.$ajax.post("/service_user/organization/getOrgTree", {
                        "type": 4
                    })
                    .then(res => {
                        if (res.data.code == 200) {
                            this.orgTree = this.$utils.Flat2TreeDataForCascader(res.data.data);
                            this.orgTreeData = res.data.data;
                        }
                    })
                    .catch((error) => {
                        if (!error.url) {
                            console.info(error);
                        }
                    })
            },
            //根据查询条件
            search2: function() {
                this.selection = [];
                this.initChart();
            },
            //导出文件
            importFile: function() {
                var data = "";
                if (this.list && this.list.length > 0) {
                    if (this.selection && this.selection.length > 0) {
                        //算合计
                        var obj = {
                            name: '合计',
                            m: 0,
                            f: 0,
                            mf: 0,
                        };
                        this.selection.forEach((j, i) => {
                            obj.m = obj.m + Number(j.m);
                            obj.f = obj.f + Number(j.f);
                            obj.mf = obj.mf + Number(j.mf);
                        });
                        this.selection.push(obj);
                        data = JSON.stringify(this.selection);

                    } else {
                        data = JSON.stringify(this.list);
                    }

                    this.$utils.download({
                        name: "员工性别统计.xls",
                        url: "/reporthuman/importStatisticsSex",
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
                this.carName = '';
                this.userName = '';
            },
            //取消操作
            cal: function() {
                this.search.orgId = "";
                this.search.type = "";
            },
            //初始化图表
            initChart: function() {
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
                this.$ajax.post("/reporthuman/getHumanStatisticsSexData", {
                        "orgId": orgId,
                        "type": 0
                    })
                    .then(res => {
                        this.org = res.data.xAxis;
                        this.resMan = res.data.yMAxis;
                        this.resFemale = res.data.yFAxis;
                        this.list = res.data.list;
                        if (res.data.list) {
                            if (res.data.list.length > 10) {
                                this.height = 477;
                            } else {
                                this.height = 48 * res.data.list.length;
                            }
                        }

                        //this.gridOption.data = res.data.list;
                        this.seriesData = res.data.bList;
                        // this.tableList = res.data.tableList;
                        //this.tableList = this.tableList.slice(0,8)
                        // this.sumList = res.data.sumList;
                        this.createChartOne();
                    })
            },
            /*创建图表一*/
            createChartOne: function() {
                if (!this.myChart) {
                    this.myChart = this.echarts.init(document.getElementById('main'));
                }
                this.$nextTick(() => {
                    this.myChart1 = this.echarts.init(this.$refs.charts, 'light');
                    // 使用刚指定的配置项和数据显示图表。
                    this.option1.series[0].data = this.seriesData;
                    this.myChart1.setOption(this.option1);

                })

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
                        data: ['男性', '女性']
                    },

                    toolbox: {
                        show: true,
                        feature: {
                            mark: {
                                show: true
                            },
                            dataView: {
                                show: false,
                                readOnly: false
                            },
                            magicType: {
                                show: true,
                                type: ['line', 'bar']
                            },
                            restore: {
                                show: false
                            },
                            saveAsImage: {
                                show: true
                            },
                            dataZoom: {
                                show: false,
                                xAxisIndex: [0, 1]
                            }
                        }
                    },

                    calculable: true,
                    xAxis: [{
                        type: 'category',
                        data: this.org,
                        axisLabel: {
                            interval: 0,
                            rotate: 40
                        }
                    }],
                    yAxis: [{
                        type: 'value'
                    }],
                    series: [{
                        name: '男性',
                        type: 'bar',
                        stack: '人数',
                        barMaxWidth: '30',
                        data: this.resMan,

                    }, {
                        name: '女性',
                        type: 'bar',
                        stack: '人数',
                        barMaxWidth: '30',
                        data: this.resFemale,
                    }]
                };
                // 使用刚指定的配置项和数据显示图表。
                this.myChart.setOption(option);

            },
        },
        mounted: function() {
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
