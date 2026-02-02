<template>
    <div>
        <page-title>代办列表</page-title>
        <div class="page-search">
            <div class="page-search-title">查询条件</div>
            <div class="page-search-content">
                <Form :model="task" :label-width="120" @submit.native.prevent="search">
                    <Form-item label="任务名称">
                        <Input v-model.trim="task.taskName" placeholder="请输入任务名称" maxlength="30">
                        </Input>
                    </Form-item>
                    <Form-item label="任务编号">
                        <Input v-model.trim="task.taskId" placeholder="请输入任务编号" maxlength="30">
                        </Input>
                    </Form-item>
                    <Form-item label="流程发起人">
                        <Input v-model.trim="task.applyUserName" placeholder="请输入流程发起人姓名" maxlength="30">
                        </Input>
                    </Form-item>
                    <Form-item label="审批人">
                        <Input v-model.trim="task.assigneeName" placeholder="请输入登录人" maxlength="30">
                        </Input>
                    </Form-item>
                </Form>
                <div class="search-btn">
                    <allowBtn allow="allowsearch" type="primary" icon="ios-search"  @click="search()">查询</allowBtn>
                    <allowBtn allow="allowreset" type="error" icon="ios-refresh-empty"  @click="reset()">重置</allowBtn>
                </div>
            </div>

            <div class="page-content">
                <Tabs v-model="tabName" >
                <TabPane label="待办理" name="toHandle" icon="android-bar" >
                    <data-grid :option="gridOption" ref="grid"></data-grid>
                </TabPane>
                <TabPane label="已办理" name="handled" icon="ios-navigate-outline" >
                    <data-grid :option="gridOption" ref="grid1"></data-grid>
                </TabPane>
                </Tabs>
            </div>
        </div> 
    </div>
</template>
<script>
import dataGrid from "@/components/datagrid"
import CONFIG from '@/config/index.js'
// import updateModal from "./cur"
export default {
    components: { dataGrid},
    data: function() {
        var that = this;
        return {
            isHandle:0,
            task: {
                taskId: '',
                taskName: '',
                business: '',
                startDate: '',
                endDate: '',
                formKey:'',
                formKeyView:'',
                
            },
            //以上是界面v-model的所有参数
            "gridOption": {
                "auto":false,
                "url": "/service_user/workFlowTask/list",
                "columns": [ //列配置
                    {
                        "title": "任务名称",
                        "key": "taskName",
                        "align": "center"
                    },
                    {
                        "title": "任务编号",
                        "key": "taskId",
                        "align": "center"
                    },
                    {
                        "title": "发起人",
                        "key": "applyUserName",
                        "align": "center"
                    },
                    {
                        "title": "审批人",
                        "key": "assigneeName",
                        "align": "center"
                    },
                    {
                        "title": "发起时间",
                        "key": "applyDate",
                        "align": "center",
                        "render": function(h, param) {
                                var date = new Date(param.row.createTime);
                                return date.Format("yyyy-MM-dd");
                            }
                    },
                    {
                        "title": "结束时间",
                        "key": "endDate",
                        "align": "center",
                        "render": function(h, param) {
                                var date = new Date(param.row.createTime);
                                return date.Format("yyyy-MM-dd");
                            }
                    },
                    {
                        "title": "状态",
                        "key": "operUserName",
                        "align": "center",
                        "render": function(a, param) {
                                if (param.row.procState == 0) {return "草稿"}
                                else if (param.row.procState == 1) {return "审批中"}
                                else if (param.row.procState == 2) {return "审批完成"}
                                else if (param.row.procState == 3) {return "审批驳回"}
                                else if (param.row.procState == 4) {return "流程结束后已重新发起"}
                            }
                    },
                    {
                            key: 'tool',
                            title: '操作',
                            align: "center",
                            width:'130px',
                            render(h, params) {
                                if(params.row.isHandle==0){
                                    return h('div', [
                                    h('allowBtn', {
                                        props: {
                                            type: 'info',
                                            size: 'small',
                                            allow: '办理'
                                        },
                                        style: {
                                            marginRight: '5px'
                                        },
                                        on: {
                                            click: () => {
                                                that.handle(params.row)
                                            }
                                        }
                                    }, '办理'),
                                ]
                                );
                                }else{
                                    return h('div', [
                                    h('allowBtn', {
                                        props: {
                                            type: 'info',
                                            size: 'small',
                                            allow: '查看'
                                        },
                                        style: {
                                            marginRight: '5px'
                                        },
                                        on: {
                                            click: () => {
                                                that.view(params.row)
                                            }
                                        }
                                    }, '查看'),
                                ]
                                );
                                }

                            }
                        }
                ],
                "loadFilter": function(data) {
                    // console.log("data.data.list",data.data.list)
                    return data.data;
                },
            }
        }
    },
    methods: {
        handle(obj){
              window.open('https://'+obj.formKey+'/', '_blank')
        },
        view(obj){
            window.open('https://'+obj.formKey+'/', '_blank')
        },
        // doTask:function(obj){
        //     console.log(obj.id)
        //     // var formKey = row.formKey
        //     // var formKeyView = row.formKeyView

        //     // window.open('https://'+formKeyView+'/', '_blank')
        //     // window.open('https://www.baidu.com/', '_self')
        //     // this.$router.push('/workFlowTask/task/' + obj.id + '/update');
        //     // this.$router.push('/workFlowTask/task/' + row.id+"/view" );
        
        // },    
        search: function() {
            { var obj={
                "isHandle":0,
                "taskName":this.task.taskName,
                "taskId":this.task.taskId,
                "applyUserName":this.task.applyUserName,
                "assigneeName":this.task.assigneeName
                             }
                            //  console.log(obj)
                this.$refs.grid.load({"data":JSON.stringify(obj)} );
            }
            {var obj={
                "isHandle":1,
                "taskName":this.task.taskName,
                "taskId":this.task.taskId,
                "applyUserName":this.task.applyUserName,
                "assigneeName":this.task.assigneeName
                }
                // console.log(obj)
                this.$refs.grid1.load({"data":JSON.stringify(obj)} );
            }

            // if(this.tabName == 'toHandle') {
               
            // }
            // else{
                
            // }
        },
        reset: function() {
            this.task.taskName = "";
            this.task.taskId = "";            
            this.task.applyUserName = "";
            this.task.assigneeName = "";
            this.search();
        },
        // toHandle(){
        //     console.log("toHandle")
        //     this.task.isHandle=0
        //     this.$refs.grid.load({"data":JSON.stringify(this.task)} );
        // },
        // handled(){
        //     console.log("handled")
        //     this.task.isHandle=1
        //     this.$refs.grid.load({"data":JSON.stringify(this.task)} );
        // }
    
    },
   
    mounted: function () {
        { var obj={
                "isHandle":0,
            }
                this.$refs.grid.load({"data":JSON.stringify(obj)} );
           
            {var obj={
                "isHandle":1,
                }
       
                this.$refs.grid1.load({"data":JSON.stringify(obj)} );
            }
       

        }
    }
}

</script>
