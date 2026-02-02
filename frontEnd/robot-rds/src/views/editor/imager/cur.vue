<template>
    <div>
        <div class="page-content">
            <Form :label-width="120" ref="robotImager" :model="robotImager" :class="{ 'form-view': type == 'view' }" :rules="ruleValidateType" @submit.prevent="submit">
                <Form-item label="机器人id" prop="robotName">
                    <template v-if="type == 'view'">{{ robotImager.robotName }}</template>
                    <Input v-else v-model.trim="robotImager.robotName" :disabled="true" placeholder="请输入机器人名称，不超过30字" maxlength="30"></Input>
                </Form-item>
                <Form-item label="热成像仪IP" prop="currentIp">
                    <template v-if="type == 'view'">{{ robotImager.currentIp }}</template>
                    <Input v-else v-model.trim="robotImager.currentIp" placeholder="请输入热成像仪IP，不超过30字" maxlength="30"></Input>
                </Form-item>
                <Form-item label="热成像仪名称" prop="imagerName">
                    <template v-if="type == 'view'">{{ robotImager.imagerName }}</template>
                    <Input v-else v-model.trim="robotImager.imagerName" placeholder="请输入热成像仪名称，不超过30字" maxlength="30"></Input>
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
            robotImager: {
                robotName: '',
                currentIp: '',
                imagerName: '',
            },
            ruleValidateType: {
                currentIp: [
                    { required: true, type: 'string', message: '热成像仪IP不能为空', trigger: 'blur' },
                    // { validator: this.checkDmsName, trigger: 'blur' },
                ],
                imagerName: [
                    { required: true, type: 'string', message: '热成像仪名称不能为空', trigger: 'blur' },
                    // { validator: this.checkDmsName, trigger: 'blur' },
                ],
            }
        }
    },
    methods: {
        // checkIp: function (rule, value, callback) {
        //     if (value) {
        //         var ph = /^(((\d)|([1-9]\d)|(1\d{2})|(2[0-4]\d)|(25[0-5]))\.){3}((\d)|([1-9]\d)|(1\d{2})|(2[0-4]\d)|(25[0-5]))$/;
        //         if (!ph.test(value)) {
        //             callback("ip格式不正确");
        //         } else {
        //             callback();
        //         }
        //     }
        // },
        //校验光通信站名称唯一性
        // checkDmsName: function (rule, value, callback) {
        //     if (value) {
        //         this.$ajax.post("/service_rms/dms/checkDmsName", { 'dmsName': value, 'id': this.id })
        //             .then((res) => {
        //                 if (res.data.data) {
        //                     callback();
        //                 } else {
        //                     callback("光通信站名称已存在");
        //                 }
        //             })
        //             .catch((error) => {
        //                 callback('校验失败');
        //             })
        //     } else {
        //         callback("请输入光通信站名称");
        //     }
        // },
        save(cb) {
            this.$refs['robotImager'].validate((valid) => {
                if (valid) {
                    this.$ajax.post("/service_rms/robotImager/save", {"data":JSON.stringify(this.robotImager)})
                        .then((res) => {
                            if (res.data.code == "200") {
                                cb && cb(true);
                                this.$refs['robotImager'].resetFields();
                                this.$Message.success('保存成功');
                            } else {
                                this.$Message.error('保存失败');
                                // this.$emit("changeVal");
                            }
                        })
                        .catch((error) => {
                            if (!error.url) { console.info(error); }
                        })
                } else {
                    // cb && cb(false);
                    this.$emit("changeVal");
                    this.$Message.error('表单校验失败!');
                }
            })
        },
        getData: function () {
            //获取此模块基本信息 根据id查询
            this.$ajax.post("/service_rms/robotImager/findById", { "id": this.id })
                .then(res => {
                    this.robotImager = res.data.data;
                })
                .catch((error) => {
                    if (!error.url) { console.info(error); }
                });
        },
        resetFields: function () {
            this.$refs.robotImager.resetFields();
        },
    },
    watch: {
        id: function () {
            if (this.id != "") {
                this.getData();
            }
        }
    },
}
</script>