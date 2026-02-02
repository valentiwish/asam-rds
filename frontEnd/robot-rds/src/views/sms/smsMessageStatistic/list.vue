<template>
    <div>
        <page-title></page-title>
        <div class="page-search">
            <div class="page-search-title">查询条件</div>
            <div class="page-search-content">
                <Form :label-width="80" @submit.native.prevent="search">
                    <FormItem label="发送时间">
                        <template>
                            <Row>
                                <Col span="12">
                                <DatePicker type="daterange" v-model="checkDate" placement="right-start" placeholder="请选择发送时间" style="width: 200px"></DatePicker>
                                </Col>
                            </Row>
                        </template>
                    </FormItem>
                </Form>
                <div class="search-btn">
                    <allow-btn class="btn" icon="ios-search" allow="allowsearch" type="primary" @click="search()">查询</allow-btn>
                    <allow-btn class="btn" icon="ios-refresh" allow="allowreset" type="error" @click="clear()">重置</allow-btn>
                </div>
            </div>
        </div>
        <div class="page-content">
            <data-grid :option="gridOption" ref="grid"></data-grid>
        </div>
    </div>
</template>
<script>
import dataGrid from "@/components/datagrid.vue";
export default {
    components: {
        dataGrid,
    },
    data() {
        var that = this;
        return {
            loading: false,
            checkDate: '',
            tempName: '',
            //以上是界面v-model的所有参数
            "gridOption": {
                "header": true,
                "iconCls": "fa-power-off",
                "data": [],
                "url": "/service_sms/SMSRecord/listStatis",
                "contentType":"json",
                "columns": [ // 列配置
                    {
                        "title": "模板名称",
                        "align": "center",
                        "key": "tempName",
                        render: function(h, params) {
                            return params.row.tempName;
                        }
                    },
                    {
                        "title": "发送数量",
                        "align": "center",
                        "key": "count",
                        render: function(h, param) {
                            return param.row.reNum;
                        }
                    },
                ],
                "loadFilter": function(data) {
                    return data.data;
                }
            }
        }
    },
    methods: {
        //以下二个是界面的方法
        search: function(obj) {
            let startDate = "";
            let endDate = "";
            if (this.checkDate[0] !== "" && this.checkDate[0] !== undefined) {
                startDate = new Date(this.checkDate[0]).Format('yyyy-MM-dd');
            }
            if (this.checkDate[1] !== "" && this.checkDate[1] !== undefined) {
                endDate = new Date(this.checkDate[1]).Format('yyyy-MM-dd');
            }
            this.$refs.grid.load({
                'startDate': startDate,
                'endDate': endDate,
            });

        },
        clear: function() {
            this.checkDate = "";
            this.$refs.grid.load({});
        },
        //加载旋转方法
        changeVal: function() {
            this.loading = false;
        },
    },
}

</script>
<style>


</style>
