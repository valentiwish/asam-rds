<template>
    <div>
        <div class="page-content">
            <Form :label-width="80" ref="task" :model="task"  :class="{'form-view':type=='view'}" :rules="ruleValidateType" @submit.prevent="submit">   
                <Form-item label="任务名称"  prop="taskName">
                    <template v-if="type=='view'">{{task.taskName}}</template>
                    <Input v-else v-model.trim="task.taskName" placeholder="请输入任务名称，不超过30字" maxlength="30"></Input>
                </Form-item>
                <Form-item label="版本备注"  prop="remark">
                    <template v-if="type=='view'">{{task.remark}}</template>
                    <Input v-else v-model.trim="task.remark" placeholder="请输入版本备注，不超过30字" maxlength="30"></Input>
                </Form-item>
                <Form-item label="任务描述"  prop="versionDescription">
                    <template v-if="type=='view'">{{task.versionDescription}}</template>
                    <Input v-else v-model.trim="task.versionDescription" placeholder="请输入任务描述，不超过30字" maxlength="30"></Input>
                </Form-item>
                <FormItem label="优先级" prop="priority">
                    <template v-if="type=='view'">{{1 == task.priority ? '低优先级': (2 == task.priority ? '中优先级' : '高优先级')}}</template>
                    <Select v-else v-model.trim="task.priority">
                        <Option value="1">低优先级</Option>
                        <Option value="2">中优先级</Option>
                        <Option value="3">高优先级</Option>
                    </Select>
                </FormItem>
                <!-- <FormItem label="循环状态" prop="enabledState">
                    <template v-if="type=='view'">{{task.enabledState==1 ? '是' :'否'}}</template>
                    <Select v-else v-model.trim="task.enabledState" placeholder="请选择循环状态">
                        <Option value="0">否</Option>
                        <Option value="1">是</Option>
                    </Select>
                </FormItem> -->
            </Form>
        </div>
    </div>
</template>
<script>
import fontIcon from "@/components/fontIcon"
export default {
    components: { fontIcon },
        props: ["id", "type", "goodsType", "warehouseData", "warehouseTree"],
        data() {
            var that = this;
            return {
                type:'',          
                task: {
                    taskName: '',
                    remark: '',
                    versionDescription:'',
                    enabledState: 0,
                    priority: ''
                },  
                ruleValidateType: {
                    taskName: [
                        { required: true, type: 'string', message: '任务名称不能为空', trigger: 'blur' },
                        { validator: this.checkName, trigger: 'blur' },
                    ],
                }
            }
        },
        methods: {
            checkIp: function(rule, value, callback){
                console.log(rule)
                if(this.type=="add"){
                    if(/^((25[0-5]|2[0-4]\d|((1\d{2})|([1-9]?\d)))\.){3}(25[0-5]|2[0-4]\d|((1\d{2})|([1-9]?\d)))$/.test(value)) {
                        this.$ajax.post("/service_rms/scanner/checkIp", {"ip": value})
                                .then((res) => {
                                    console.log(res)
                                    if (res.data.code == "200") {
                                        callback()
                                    } else {
                                        callback("ip地址重复")
                                    }                                
                                })
                                .catch((error) => {
                                    callback("错误")
                        })
                    }else {
                        callback("非合法ip地址")
                    }
                }else if(this.type=="update"){
                    if(/^((25[0-5]|2[0-4]\d|((1\d{2})|([1-9]?\d)))\.){3}(25[0-5]|2[0-4]\d|((1\d{2})|([1-9]?\d)))$/.test(value)) {
                        callback()
                    }else {
                        callback("非合法ip地址")
                    }
                }else{
                    callback()
                }
            },
             //校验名称唯一性
             checkName: function(rule, value, callback){
                if(value){
                    this.$ajax.post("/service_rms/task/check",{'name': value, 'id': this.id})
                    .then((res) => {
                        if (res.data.data){
                            callback();
                        } else {
                            callback("名称已存在");
                        }
                    })
                    .catch((error) => {
                        callback('校验失败');
                    })
                } else {
                    callback("请输入名称");
                }
            },
            save(cb) {
                var that=this;
                var task=this.task;
                this.$refs['task'].validate((valid) => {
                    if (true == valid ) {
                        let scanner = this.scanner;
                        var url = "/service_rms/task/save";
                        this.$ajax.post(url, {"data": JSON.stringify(task)})
                            .then((res) => {
                                console.log(res)
                                if (res.data.code == "200") {
                                    cb && cb(true);
                                    this.$refs['task'].resetFields();
                                    that.$Message.success('保存成功');
                                } else {
                                    that.$Message.error('保存失败');
                                }                                
                            })
                            .catch((error) => {
                                if(!error.url){console.info(error);}
                            })
                    } else {
                        this.$emit("changeVal");
                        this.$Message.error('表单校验失败!');
                    }
                })                
            },
            getData: function() {
            //获取此模块模块基本信息 根据id查询
                this.$ajax.post("/service_rms/task/findById", { "id": this.id })
                    .then(res => {
                        this.task = res.data.data;
                        //因为要展示的是是字符串类型，因此需要把数字转换成字符串类型
                        this.task.enabledState=""+this.task.enabledState;
                        this.task.priority=""+this.task.priority;
                    })
                    .catch((error) => {
                        if (!error.url) { console.info(error); }
                    });
                },
            resetFields: function() {
                this.$refs.dms.resetFields();
            },
        },
        watch: {
            id: function() {
                if (this.id != "") {
                    this.getData();
                }
            }
        },
        mounted:function(){
        }
}
</script>