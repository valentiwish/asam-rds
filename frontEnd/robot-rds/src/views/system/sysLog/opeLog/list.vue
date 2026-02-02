<template>
    <div>
        <page-title>日志管理</page-title>
        <div class="page-search">
            <div class="page-search-title">查询条件</div>
            <div class="page-search-content">
                <Form :model="sysLog" :label-width="80" @submit.native.prevent="search">
                    <Form-item label="登录人">
                        <Input v-model.trim="sysLog.creater" placeholder="请输入登录人" maxlength="30">
                        </Input>
                    </Form-item>
                    <Form-item label="业务模块">
                        <Input v-model.trim="sysLog.business" placeholder="请输入业务模块" maxlength="30">
                        </Input>
                    </Form-item>
                    <FormItem label="开始时间">
                        <DatePicker type="date" placeholder="请输入开始时间" v-model="sysLog.startDate" format="yyyy-MM-dd hh:mm:ss"></DatePicker>
                    </FormItem>
                    <FormItem label="结束时间">
                        <DatePicker type="date" placeholder="请输入结束时间" v-model="sysLog.endDate" format="yyyy-MM-dd hh:mm:ss"></DatePicker>
                    </FormItem>
                </Form>
                <div class="search-btn">
                    <allowBtn allow="allowsearch" type="primary" icon="ios-search"  @click="search()">查询</allowBtn>
                    <allowBtn allow="allowreset" type="error" icon="ios-refresh-empty"  @click="reset()">重置</allowBtn>
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
import CONFIG from '@/config/index.js'

export default {
    components: { dataGrid},
    data: function() {
        var that = this;
        return {
            sysLog: {
                creater: '',
                business: '',
                startDate: '',
                endDate: ''
            },
            "gridOption": {
                "url": "/service_user/sysLog/opeList",
                "columns": [ //列配置
                    {
                        "title": "登录人",
                        "key": "operUserName",
                        "align": "center"
                    },
                    {
                        "title": "登录账号",
                        "key": "operLoginName",
                        "align": "center"
                    },
                    {
                        "title": "操作时间",
                        "key": "operCreateTime",
                        "align": "center",
                        "render": function(a, param) {
                            if(param.row.operCreateTime){
                                return new Date(param.row.operCreateTime).Format('yyyy-MM-dd hh:mm:ss');
                            }else{
                                return "";
                            }
                           
                        }
                    },
                    {
                        "title": "操作模块",
                        "key": "operModul",
                        "align": "center",
                    },
                    {
                        "title": "操作内容",
                        "key": "operType",
                        "align": "center",
                    },
                    {
                        key: 'id',
                        title: '操作',
                        align: "center",
                        width: 120,
                        render(h, params) {
                            return h('div', [
                                h('allowBtn', {
                                    props: {
                                        type: 'ghost',
                                        size: 'small',
                                        allow: "查看",
                                        faicon: "fa-file-text-o"
                                    },
                                    style: {
                                        marginRight: '5px'
                                    },
                                    on: {
                                        click: () => {
                                            that.tiView(params.row)
                                        }
                                    }
                                }, '查看')
                            ]);
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
        search: function() {
            var sDate = null;
            var eDate = null;
            if (this.sysLog.startDate != null && this.sysLog.startDate != '') {
                sDate = this.sysLog.startDate.Format("yyyy-MM-dd hh:mm:ss");
            }
            if (this.sysLog.endDate != null && this.sysLog.endDate != '') {
                eDate = this.sysLog.endDate.Format("yyyy-MM-dd hh:mm:ss");
            }
            this.$refs.grid.load({ 'creater':this.sysLog.creater, 'business': this.sysLog.business,'startDate':sDate, 'endDate': eDate });
        },
        reset: function() {
            this.sysLog.creater = "";
            this.sysLog.business = "";            
            this.sysLog.startDate = "";
            this.sysLog.endDate = "";
            this.$refs.grid.load({ });
        },
        tiView: function(obj){
            this.$router.push('/sysLog/view/' + obj.id);
        }
    },
    created: function() {
    }
}

</script>
