<template>
    <div>
        <div class="page-content">
            <Form :label-width="100" ref="robot" :model="robot"  :class="{'form-view':type=='view'}" :rules="ruleValidateType" @submit.prevent="submit">   
                  
                <Form-item label="机器人名称"  prop="vehicleId">
                    <Input v-model.trim="robot.vehicleId" placeholder="请输入任务名称，不超过30字" maxlength="30"></Input>
                </Form-item>
               
                <Form-item label="IP"  prop="currentIp">
                    <Input v-model.trim="robot.currentIp" placeholder="请输入备注，不超过30字" maxlength="30"></Input>
                </Form-item>

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
                unitType: [],   
                bomTypes:[
                    {"id":1,"name":"子件"},
                    {"id":2,"name":"物料"}
                ], 
                type:'',          
                categoryIds: [],
                categoryData: [],
                categoryTree: [],
                suppliersIds: [],
                suppliersData: [],
                suppliersTree: [],
                robot: {
                    vehicleId: '',
                    currentIp: '',
                },  
                ruleValidateType: {
                    vehicleId: [
                        { required: true, type: 'string', message: '机器人名称不能为空', trigger: 'blur' },
                    ],
                    currentIp: [
                        { validator: this.checkIp, trigger: 'blur' },
                    ],
                   
                }
            }
        },
        methods: {
            checkIp:function(rule, value, callback){
                if(value){
                    var ph = /^(((\d)|([1-9]\d)|(1\d{2})|(2[0-4]\d)|(25[0-5]))\.){3}((\d)|([1-9]\d)|(1\d{2})|(2[0-4]\d)|(25[0-5]))$/;
                    if(!ph.test(value)){ 
                        callback("ip格式不正确");
                    } else {
                        callback();
                    }
                } 
            },
            checkCodeUnique: function(rule, value, callback){
                if(this.type=="add"){
                        this.$ajax.post("/service_rms/scanner/checkCodeUnique", {"number": value})
                                .then((res) => {
                                    console.log(res)
                                    if (res.data.code == "200") {
                                        callback()
                                    } else {
                                        callback("编号重复")
                                    }                                
                                })
                                .catch((error) => {
                                    callback("错误")
                        })
                }else{
                    callback()
                }
            },
            save(cb) {
                var that=this;
                var robot=this.robot;
                this.$refs['robot'].validate((valid) => {
                    if (true == valid ) {
                        var url = "/service_rms/robotBasic/saveVirtual";
                        this.$ajax.post(url, {"data": JSON.stringify(robot)})
                            .then((res) => {
                                console.log(res)
                                if (res.data.code == "200") {
                                    cb && cb(true);
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
            getscannersById: function() {
                //获取此模块模块基本信息 根据id查询
                this.$ajax.post("/service_rms/scanner/findById", { "id": this.id })
                    .then(res => {
                        this.scanner = res.data.data;
       
                    })
                    .catch((error) => {
                        if(!error.url){console.info(error);}
                    });
            },
            getUnitParam: function() {
                this.$ajax.post("/userservice/sysData/findByTypeCode", {'dataTypeCode': "UNIT_TYPE" })
                    .then(res => {
                        this.unitType = res.data.data;
                    })
                    .catch((error) => {
                        if (!error.url) {
                            console.info(error);
                        }
                    });
            },
        },
        created: function() {
          
        },
        watch: {
           
        },
        
}
</script>