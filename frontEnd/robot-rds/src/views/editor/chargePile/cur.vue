<template>
    <div>
        <div class="page-content">
            <Form :label-width="120" ref="chargePile" :model="chargePile" :class="{ 'form-view': type == 'view' }"
                :rules="ruleValidateType" @submit.prevent="submit">
                <!-- <Form-item label="充电桩名称" prop="chargePileName">
                    <template v-if="type == 'view'">{{ chargePile.chargePileName }}</template>
<Input v-else v-model.trim="chargePile.chargePileName" placeholder="请输入充电桩名称，不超过30字" maxlength="30"></Input>
</Form-item>
<Form-item label="充电桩IP" prop="chargePileIp">
    <template v-if="type == 'view'">{{ chargePile.chargePileIp }}</template>
    <Input v-else v-model.trim="chargePile.chargePileIp" placeholder="请输入充电桩IP，不超过30字" maxlength="30"></Input>
</Form-item>
<Form-item label="充电桩点位" prop="chargePilePoint">
    <template v-if="type == 'view'">{{ chargePile.chargePilePoint }}</template>
    <Input v-else v-model.trim="chargePile.chargePilePoint" :disabled="true" placeholder="请输入充电桩点位，不超过30字"
        maxlength="30"></Input>
</Form-item> -->
                <Card>
                    <h2 slot="title">基础信息</h2>
                    <Row>

                        <Col span="12">
                        <Form-item label="充电桩名称" prop="chargePileName">
                            <template v-if="type == 'view'">{{ chargePile.chargePileName }}</template>
                            <Input v-else v-model.trim="chargePile.chargePileName" placeholder="请输入充电桩名称，不超过30字"
                                maxlength="30"></Input>
                        </Form-item>
                        </Col>


                        <Col span="12">
                        <Form-item label="充电桩IP" prop="chargePileIp">
                            <template v-if="type == 'view'">{{ chargePile.chargePileIp }}</template>
                            <Input v-else v-model.trim="chargePile.chargePileIp" placeholder="请输入充电桩IP，不超过30字"
                                maxlength="30"></Input>
                        </Form-item>
                        </Col>

                        <Col span="12">
                        <Form-item label="充电桩点位" prop="chargePilePoint">
                            <template v-if="type == 'view'">{{ chargePile.chargePilePoint }}</template>
                            <Input v-else v-model.trim="chargePile.chargePilePoint" placeholder="请输入充电桩点位，不超过30字"
                                maxlength="30"></Input>
                        </Form-item>
                        </Col>

                        <Col span="12">
                        <Form-item label="通信类型" prop="communicationType">
                            <template v-if="type == 'view'">{{ chargePile.communicationType }}</template>
                            <Select v-else v-model="chargePile.communicationType" placeholder="请选择通信类型">
                                <Option value="Modbus" key="type0">Modbus</Option>
                                <Option value="TCP" key="type1">TCP</Option>
                            </Select>
                        </Form-item>
                        </Col>
                    </Row>
                </Card>
                <Card v-if="chargePile.communicationType === 'Modbus'">
                    <h2 slot="title">Modbus配置</h2>
                    <Row>
                        <Col span="12">
                        <Form-item label="Slave ID" prop="slaveId">
                            <template v-if="type == 'view'">{{ chargePile.slaveId }}</template>
                            <InputNumber v-else v-model.trim="chargePile.slaveId" :disabled="true"
                                placeholder="请输入Slave Id" maxlength="30"></InputNumber>
                        </Form-item>
                        </Col>
                        <Col span="12">
                        <Form-item label="手动伸出地址" prop="manualExtendOffset">
                            <template v-if="type == 'view'">{{ chargePile.manualExtendOffset }}</template>
                            <InputNumber v-else v-model.trim="chargePile.manualExtendOffset" :disabled="false"
                                placeholder="整型偏移量" maxlength="30"></InputNumber>
                        </Form-item>
                        </Col>
                        <Col span="12">
                        <Form-item label="手动缩回地址" prop="manualRetractOffset">
                            <template v-if="type == 'view'">{{ chargePile.manualRetractOffset }}</template>
                            <InputNumber v-else v-model.trim="chargePile.manualRetractOffset" :disabled="false"
                                placeholder="整型偏移量" maxlength="30"></InputNumber>
                        </Form-item>
                        </Col>
                        <Col span="12">
                        <Form-item label="强制启动地址" prop="forceStartOffset">
                            <template v-if="type == 'view'">{{ chargePile.forceStartOffset }}</template>
                            <InputNumber v-else v-model.trim="chargePile.forceStartOffset" :disabled="false"
                                placeholder="整型偏移量" maxlength="30"></InputNumber>
                        </Form-item>
                        </Col>
                        <Col span="12">
                        <Form-item label="启动按钮地址" prop="startOffset">
                            <template v-if="type == 'view'">{{ chargePile.startOffset }}</template>
                            <InputNumber v-else v-model.trim="chargePile.startOffset" :disabled="false"
                                placeholder="整型偏移量" maxlength="30"></InputNumber>
                        </Form-item>
                        </Col>
                        <Col span="12">
                        <Form-item label="暂停按钮地址" prop="pauseOffset">
                            <template v-if="type == 'view'">{{ chargePile.pauseOffset }}</template>
                            <InputNumber v-else v-model.trim="chargePile.pauseOffset" :disabled="false"
                                placeholder="整型偏移量" maxlength="30"></InputNumber>
                        </Form-item>
                        </Col>
                        <Col span="12">
                        <Form-item label="停止按钮地址" prop="stopOffset">
                            <template v-if="type == 'view'">{{ chargePile.stopOffset }}</template>
                            <InputNumber v-else v-model.trim="chargePile.stopOffset" :disabled="false"
                                placeholder="整型偏移量" maxlength="30"></InputNumber>
                        </Form-item>
                        </Col>
                        <Col span="12">
                        <Form-item label="复位按钮地址" prop="resetOffset">
                            <template v-if="type == 'view'">{{ chargePile.resetOffset }}</template>
                            <InputNumber v-else v-model.trim="chargePile.resetOffset" :disabled="false"
                                placeholder="整型偏移量" maxlength="30"></InputNumber>
                        </Form-item>
                        </Col>
                        <Col span="12">
                        <Form-item label="状态寄存器地址" prop="statusRegisterOffset">
                            <template v-if="type == 'view'">{{ chargePile.statusRegisterOffset }}</template>
                            <InputNumber v-else v-model.trim="chargePile.statusRegisterOffset" :disabled="false"
                                placeholder="整型偏移量" maxlength="30"></InputNumber>
                        </Form-item>
                        </Col>

                    </Row>
                </Card>


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
            chargePile: {
                chargePileName: '',
                chargePileIp: '',
                chargePilePoint: '',

                slaveId: 1,
                manualExtendOffset: 0,
                manualRetractOffset: 1,
                forceStartOffset: 2,
                startOffset: 3,
                pauseOffset: 4,
                stopOffset: 6,
                resetOffset: 7,
                statusRegisterOffset: 12,
                communicationType: "TCP",
            },
            ruleValidateType: {
                chargePileName: [
                    { required: true, type: 'string', message: '充电桩名称不能为空', trigger: 'blur' },
                    { validator: this.checkChargePileName, trigger: 'blur' },
                ],
                chargePileIp: [
                    { required: true, type: 'string', message: '充电桩IP不能为空', trigger: 'blur' },
                    { validator: this.checkChargePileIp, trigger: 'blur' },
                ],
                chargePilePoint: [
                    { required: true, type: 'string', message: '充电桩点位不能为空', trigger: 'blur' },
                ],
            }
        }
    },
    methods: {
        checkIp: function (rule, value, callback) {
            if (value) {
                var ph = /^(((\d)|([1-9]\d)|(1\d{2})|(2[0-4]\d)|(25[0-5]))\.){3}((\d)|([1-9]\d)|(1\d{2})|(2[0-4]\d)|(25[0-5]))$/;
                if (!ph.test(value)) {
                    callback("ip格式不正确");
                } else {
                    callback();
                }
            }
        },
        //校验智能充电桩名称唯一性
        checkChargePileName: function (rule, value, callback) {
            if (value) {
                this.$ajax.post("/service_rms/chargePile/checkChargePileName", { 'chargePileName': value, 'id': this.id })
                    .then((res) => {
                        if (res.data.data) {
                            callback();
                        } else {
                            callback("智能充电桩名称已存在");
                        }
                    })
                    .catch((error) => {
                        callback('校验失败');
                    })
            } else {
                callback("请输入智能充电桩名称");
            }
        },
        //校验智能充电桩IP唯一性
        checkChargePileIp: function (rule, value, callback) {
            if (value) {
                this.$ajax.post("/service_rms/chargePile/checkChargePileIp", { 'chargePileIp': value, 'id': this.id })
                    .then((res) => {
                        if (res.data.data) {
                            callback();
                        } else {
                            callback("智能充电桩名称IP已存在");
                        }
                    })
                    .catch((error) => {
                        callback('校验失败');
                    })
            } else {
                callback("请输入智能充电桩IP");
            }
        },
        //校验智能充电桩点位唯一性
        checkChargePilePoint: function (rule, value, callback) {
            if (value) {
                this.$ajax.post("/service_rms/chargePile/checkChargePilePoint", { 'chargePilePoint': value, 'id': this.id })
                    .then((res) => {
                        if (res.data.data) {
                            callback();
                        } else {
                            callback("智能充电桩点位已存在");
                        }
                    })
                    .catch((error) => {
                        callback('校验失败');
                    })
            } else {
                callback("请输入智能充电桩点位");
            }
        },
        save(cb) {
            var that = this;
            var chargePile = this.chargePile;
            this.$refs['chargePile'].validate((valid) => {
                if (true == valid) {
                    let scanner = this.scanner;
                    var url = "/service_rms/chargePile/save";
                    this.$ajax.post(url, { "data": JSON.stringify(this.chargePile) })
                        .then((res) => {
                            console.log(res)
                            if (res.data.code == "200") {
                                cb && cb(true);
                                this.$refs['chargePile'].resetFields();
                                that.$Message.success('保存成功');
                            } else {
                                that.$Message.error('保存失败');
                                this.$emit("changeVal");
                            }
                        })
                        .catch((error) => {
                            if (!error.url) { console.info(error); }
                        })
                } else {
                    cb && cb(false);
                    this.$emit("changeVal");
                    this.$Message.error('表单校验失败!');
                }
            })
        },
        getData: function () {
            //获取此模块模块基本信息 根据id查询
            this.$ajax.post("/service_rms/chargePile/findById", { "id": this.id })
                .then(res => {
                    this.chargePile = res.data.data;
                    console.log(this.chargePile);
                })
                .catch((error) => {
                    if (!error.url) { console.info(error); }
                });
        },
        resetFields: function () {
            this.$refs.chargePile.resetFields();
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