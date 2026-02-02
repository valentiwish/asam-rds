<template>
    <div class="page-content">
        <Form :label-width="120" ref="task" :model="task" :class="{'form-view':true}" inline>
            <Card  >
                <!-- <h2 slot="title">流程发起人</h2> -->
            
                <Form-item label="任务名称" prop="taskName">
                    <template v-if="type=='view'">{{task.taskName}}</template>
                    <Input v-else v-model="task.taskName"  maxlength="20"  placeholder="请输入任务名称"></Input>
                </Form-item>
             
                <Form-item label="任务编号" prop="taskId">
                    <template  v-if="type=='view'">{{task.taskId}}</template>
                    <Input v-else v-model="task.taskId"  maxlength="20"  placeholder="请输入任务编号"></Input>
                </Form-item>

                <Form-item label="发起时间" prop="applyDate">
                    <template v-if="type=='view'">{{task.applyDate ? new Date(task.applyDate).Format('yyyy-MM-dd') : ''}}</template>
                    <DatePicker v-else type="date" placeholder="请选择发起时间" :options="limitCardDate" v-model="task.applyDate" aria-required="true"></DatePicker>
                </Form-item>
                <Form-item label="结束时间" prop="endDate">
                    <template v-if="type=='view'">{{task.endDate ? new Date(task.endDate).Format('yyyy-MM-dd') : ''}}</template>
                    <DatePicker v-else type="date" placeholder="请选择发起时间" :options="limitCardDate" v-model="task.endDate" aria-required="true"></DatePicker>
                </Form-item>
            </Card>
            <Card>
                <!-- <h2 slot="title">审批人</h2> -->
                <!-- <Form-item label="审批人">
                    <template>{{task.taskName}}</template>
                </Form-item> -->
            </Card>
        </Form>
    </div>
</template>
<script>
    export default {
        data() {
            return {
                    task: {
                    taskId: '',
                    taskName: '',
                    business: '',
                    startDate: '',
                    endDate: '',
                    formKey: '',//办理地址url
                    formKeyView:'',//查看地址url
                },
                //校验人员信息
                ruleValidate: {
                        taskName: [
                            { type: 'string', required: true, message: '任务名称', trigger: 'blur' },
                        ],
                        taskId: [
                            { type: 'string', required: true, message: '任务编号', trigger: 'blur' },
                        ],
                        applyDate: [
                            {required: true, message: '发起时间', trigger: 'blur' },
                            { validator: this.checkApplyDate, trigger: 'blur' }
                        ],
                        endDate: [
                            { required:true, message: '结束时间', trigger: 'blur' },
                            { validator: this.checkEndDate, trigger: 'blur' }
                        ],
    
                },
             }
        },
        methods: {
            checkApplyDate:function(rule, value, callback) {
                if (value) {
                    callback();
                    } else {
                        callback("必填");
                    }
                },
          
            checkEndDate:function(rule, value, callback) {
                if (value) {
                    if(this.endDate<this.applyDate){
                        callback("结束时间填写错误，必须晚于发起时间");
                    }
                    callback();
                    } else {
                        callback("必填");
                    }
                    
                },
           
        resetFields: function() {
            this.$refs.task.resetFields();
        },
    
        save:function(cb){
            //校验
            if (this.task && this.task.startDate){
                    this.task.startDate = new Date(this.task.startDate).Format("yyyy-MM-dd")
                }
            if (this.task && this.task.endDate){
                this.task.endDate = new Date(this.task.endDate).Format("yyyy-MM-dd")
            }
            if (this.task && this.task.applyDate){
                this.task.applyDate = new Date(this.task.applyDate).Format("yyyy-MM-dd")
            }
            if(this.task.formKey==''){
                this.task.formKey='www.baidu.com';
            }
            if(this.task.formKeyView==''){
                this.task.formKeyView='www.baidu.com';
            }
            var obj = this.task;
            console.log(" save:function()obj",obj)
            // 发送数据
            this.$ajax.post("/service_user/workFlowTask/saveOrUpdate", {data:JSON.stringify(obj)})
                .then((res) => {
                    console.log("res",res)
                    if (200 == res.data.code){
                        cb && cb(true);
                        this.task = res.data.data;
                        this.$refs["task"].resetFields();
                        this.$Message.success('保存成功！');
                        // this.$emit("saveSuccess");
                    }else {
                        this.$Message.warning("网络异常");
                    }
                })
                .catch((err) => {
                    if (!err.url) {console.info(err);}
                })

        },
         //查询数据
        getData: function(id) {
                this.$ajax.post("/service_user/workFlowTask/findById", {"id": id})
                .then((res) => {
                    if (200 == res.data.code){
                        this.task = res.data.data;
                    }else {
                        this.$Message.warning("网络异常");
                    }
                })
                .catch((err) => {
                    if (!err.url) {console.info(err);}
                })
            },
        },
        // created: function() {
        //     this.id = this.$route.params.id;
        //     this.getData(this.id);
        // },
        
        watch: {
            
        },
    }
</script>
