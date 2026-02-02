<template>
    <div>
        <div class="page-content">
            <Form :label-width="130" ref="type1" :model="type1"  :class="{'form-view':type=='view'}" :rules="ruleValidateType" @submit.prevent="submit">   
                <Form-item label="机器人类型名称"  prop="typeName">
                    <template v-if="type=='view'">{{type1.typeName}}</template>
                    <Input v-else v-model.trim="type1.typeName" placeholder="请输入机器人类型名称，不超过30字" maxlength="30"></Input>
                </Form-item>
            </Form>
        </div>
    </div>
</template>
<script>
import fontIcon from "@/components/fontIcon"
export default {
    components: { fontIcon },
        props: ["id", "type"],
        data() {
            var that = this;
            return {
                type1: {
                    id: '',
                    typeName: ''
                },  
                ruleValidateType: {
                    typeName: [
                        { required: true, type: 'string', message: '机器人类型名称不能为空', trigger: 'blur' },
                        { validator: this.checkTypeName, trigger: 'blur' },
                    ],
                }
            }
        },
        methods: {
             //校验机器人类型名称唯一性
             checkTypeName: function(rule, value, callback){
                if(value){
                    this.$ajax.post("/service_rms/robotType/checkTypeName",{'typeName': value, 'id': this.id})
                    .then((res) => {
                        if (res.data.data){
                            callback();
                        } else {
                            callback("机器人类型名称已存在");
                        }
                    })
                    .catch((error) => {
                        callback('校验失败');
                    })
                } else {
                    callback("请输入机器人类型名称");
                }
            },  
            save(cb) {
                var that=this;
                var type1=this.type1;
                console.log("11111",type1);
                this.$refs['type1'].validate((valid) => {
                    if (true == valid ) {
                        let scanner = this.scanner;
                        var url = "/service_rms/robotType/save";
                        this.$ajax.post(url, {"data": JSON.stringify(type1)})
                            .then((res) => {
                                console.log("222",JSON.stringify(type1));
                                console.log(res)
                                if (res.data.code == "200") {
                                    cb && cb(true);
                                    this.$refs['type1'].resetFields();
                                    that.$Message.success('保存成功');
                                } else {
                                    that.$Message.error('保存失败');
                                    this.$emit("changeVal");
                                }                                
                            })
                            .catch((error) => {
                                if(!error.url){console.info(error);}
                            })
                    } else {
                        cb && cb(false);
                        this.$emit("changeVal");
                        this.$Message.error('表单校验失败!');
                    }
                })                
            },
            getData: function() {
            //获取此模块模块基本信息 根据id查询
                this.$ajax.post("/service_rms/robotType/findById", { "id": this.id })
                    .then(res => {
                        this.type1 = res.data.data;
                    })
                    .catch((error) => {
                        if (!error.url) { console.info(error); }
                    });
                },
            resetFields: function() {
                 this.$refs.type1.resetFields();
            },
        },
        watch: {
            id: function() {
                if (this.id != "") {
                    this.getData();
                }
            }
        },
}
</script>