<template>
    <div>
        <div class="page-content">

            <Form :label-width="120" ref="iotEquipment" :model="iotEquipment" :class="{ 'form-view': type == 'view' }"
                :rules="ruleValidateType" @submit.prevent="submit">
                <Card>
                    <h2 slot="title">基础信息</h2>
                    <Row>
                        <Col span="12">
                        <Form-item label="设备类型" prop="equipmentType">
                            <template v-if="type == 'view'">{{ iotEquipment.equipmentType }}</template>
                            <Input v-else v-model.trim="iotEquipment.equipmentType" :disabled="true"
                                placeholder="请输入设备类型，不超过30字" maxlength="30"></Input>
                        </Form-item>
                        </Col>

                        <Col span="12">
                        <Form-item label="设备编号" prop="equipmentCode">
                            <template v-if="type == 'view'">{{ iotEquipment.equipmentCode }}</template>
                            <Input v-else v-model.trim="iotEquipment.equipmentCode" :disabled="false" :placeholder="iotEquipment.communicationType === 'TiBOSHI TCP'
                                ? '1号梯为0x00，2号梯为0x01，依此类推'
                                : '请输入设备编号，不超过30字'" maxlength="30">
                            </Input>
                        </Form-item>
                        </Col>


                        <Col span="12">
                        <Form-item label="设备名称" prop="equipmentName">
                            <template v-if="type == 'view'">{{ iotEquipment.equipmentName }}</template>
                            <Input v-else v-model.trim="iotEquipment.equipmentName" :disabled="false"
                                placeholder="请输入设备名称，不超过30字" maxlength="30"></Input>
                        </Form-item>
                        </Col>

                        <Col span="12">
                        <Form-item label="通信类型" prop="communicationType">
                            <template v-if="type == 'view'">{{ iotEquipment.communicationType }}</template>
                            <Select v-else v-model="iotEquipment.communicationType" placeholder="请选择通信类型">
                                <Option value="Modbus" key="type0">Modbus</Option>
                                <Option value="MQTT" key="type1">MQTT</Option>
                                <Option value="TiBOSHI TCP" key="type2">TiBOSHI TCP</Option>
                            </Select>
                        </Form-item>
                        </Col>

                        <Col span="24">
                        <Form-item label="绑定站点" prop="adjacentSite">
                            <template v-if="type == 'view'">{{ iotEquipment.adjacentSite }}</template>
                            <Input v-else v-model.trim="iotEquipment.adjacentSite" :disabled="false"
                            type="textarea" placeholder="请输入绑定站点" ></Input>
                        </Form-item>
                        </Col>

                        <Col span="12">
                        <Form-item label="设备区域" prop="equipmentRegion">
                            <template v-if="type == 'view'">{{ iotEquipment.equipmentRegion }}</template>
                            <Input v-else v-model.trim="iotEquipment.equipmentRegion" :disabled="false"
                                placeholder="请输入设备所在区域，不超过30字" maxlength="30"></Input>
                        </Form-item>
                        </Col>

                        <Col span="12" v-if="iotEquipment.equipmentType === 'automaticDoor'">
                        <Form-item label="门内部是否可以装光通信站" prop="equipmentIsInstallDms">
                            <template v-if="type == 'view'">{{ iotEquipment.equipmentIsInstallDms }}</template>
                            <Select v-else v-model="iotEquipment.equipmentIsInstallDms" placeholder="请选择门内部是否可以装光通信站">
                                <Option value="是" key="type0">是</Option>
                                <Option value="否" key="type1">否</Option>
                            </Select>
                        </Form-item>
                        </Col>
                    </Row>
                </Card>
                <Card v-if="iotEquipment.communicationType === 'Modbus'">
                    <h2 slot="title">Modbus配置</h2>
                    <Row>
                        <Col span="12">
                        <Form-item label="设备IP" prop="equipmentIp">
                            <template v-if="type == 'view'">{{ iotEquipment.equipmentIp }}</template>
                            <Input v-else v-model.trim="iotEquipment.equipmentIp" placeholder="请输入设备IP，不超过30字"
                                maxlength="30"></Input>
                        </Form-item>
                        </Col>
                        <Col span="12">
                        <Form-item label="设备端口" prop="equipmentPort">
                            <template v-if="type == 'view'">{{ iotEquipment.equipmentPort }}</template>
                            <Input v-else v-model.trim="iotEquipment.equipmentPort" placeholder="请输入设备端口，不超过30字"
                                maxlength="30"></Input>
                        </Form-item>
                        </Col>
                        <Col span="12">
                        <Form-item label="Slave ID" prop="slaveId">
                            <template v-if="type == 'view'">{{ iotEquipment.slaveId }}</template>
                            <InputNumber v-else v-model.trim="iotEquipment.slaveId" :disabled="true"
                                placeholder="请输入Slave Id" maxlength="30"></InputNumber>
                        </Form-item>
                        </Col>
                        <Col span="12">
                        <Form-item label="设备状态地址" prop="equipStateOffset">
                            <template v-if="type == 'view'">{{ iotEquipment.equipStateOffset }}</template>
                            <InputNumber v-else v-model.trim="iotEquipment.equipStateOffset" :disabled="false"
                                placeholder="整型偏移量" maxlength="30"></InputNumber>
                        </Form-item>
                        </Col>
                        <Col span="12">
                        <Form-item label="写入地址" prop="writeOffset">
                            <template v-if="type == 'view'">{{ iotEquipment.writeOffset }}</template>
                            <InputNumber v-else v-model.trim="iotEquipment.writeOffset" :disabled="false"
                                placeholder="整型偏移量" maxlength="30"></InputNumber>
                        </Form-item>
                        </Col>
                    </Row>
                </Card>
                <Card v-if="iotEquipment.communicationType === 'MQTT'">
                    <h2 slot="title">MQTT配置</h2>
                    <Row>
                        <Col span="12">
                        <Form-item label="设备IP" prop="equipmentIp">
                            <template v-if="type == 'view'">{{ iotEquipment.equipmentIp }}</template>
                            <Input v-else v-model.trim="iotEquipment.equipmentIp" placeholder="请输入设备IP，不超过30字"
                                maxlength="30"></Input>
                        </Form-item>
                        </Col>
                        <Col span="12">
                        <Form-item label="主题" prop="topic">
                            <template v-if="type == 'view'">{{ iotEquipment.topic }}</template>
                            <Input v-else v-model.trim="iotEquipment.topic" :disabled="false" placeholder="请输入主题"
                                maxlength="30"></Input>
                        </Form-item>
                        </Col>
                        <Col span="12">
                        <Form-item label="开启" prop="open">
                            <template v-if="type == 'view'">{{ iotEquipment.open}}</template>
                            <Input v-else v-model.trim="iotEquipment.open" :disabled="false" placeholder="请输入开启字段内容"
                                maxlength="30"></Input>
                        </Form-item>
                        </Col>
                        <Col span="12">
                        <Form-item label="关闭" prop="close">
                            <template v-if="type == 'view'">{{ iotEquipment.close }}</template>
                            <Input v-else v-model.trim="iotEquipment.close" :disabled="false" placeholder="请输入关闭字段内容"
                                maxlength="30"></Input>
                        </Form-item>
                        </Col>
                        <Col span="12">
                        <Form-item label="开启结束" prop="openEnd">
                            <template v-if="type == 'view'">{{ iotEquipment.openEnd }}</template>
                            <Input v-else v-model.trim="iotEquipment.openEnd" :disabled="false" placeholder="请输入开启结束字段内容"
                                maxlength="30"></Input>
                        </Form-item>
                        </Col>
                        <Col span="12">
                        <Form-item label="关闭结束" prop="closeEnd">
                            <template v-if="type == 'view'">{{ iotEquipment.closeEnd }}</template>
                            <Input v-else v-model.trim="iotEquipment.closeEnd" :disabled="false" placeholder="请输入关闭结束字段内容"
                                maxlength="30"></Input>
                        </Form-item>
                        </Col>
                    </Row>
                </Card>
                <Card v-if="iotEquipment.communicationType === 'TiBOSHI TCP'">
                    <h2 slot="title">TiBOSHI TCP</h2>
                    <Row>
                        <Col span="12">
                        <Form-item label="设备IP" prop="equipmentIp">
                            <template v-if="type == 'view'">{{ iotEquipment.equipmentIp }}</template>
                            <Input v-else v-model.trim="iotEquipment.equipmentIp" placeholder="请输入设备IP，不超过30字"
                                maxlength="30"></Input>
                        </Form-item>
                        </Col>
                        <Col span="12">
                        <Form-item label="组编号" prop="groupCode">
                            <template v-if="type == 'view'">{{ iotEquipment.groupCode }}</template>
                            <InputNumber v-else v-model.trim="iotEquipment.groupCode" :min="0" :value="0"
                                :disabled="false" placeholder="1号组为0，2号组为1，依此类推" style="width: 100%">
                            </InputNumber>
                        </Form-item>
                        </Col>
                    </Row>
                </Card>
                <Card>
                    <h2 slot="title">备注信息</h2>
                    <Form-item label="说明" prop="remark">
                        <template v-if="type == 'view'">{{ iotEquipment.remark }}</template>
                        <Input v-else v-model.trim="iotEquipment.remark" type="textarea" placeholder="请填写说明">
                        </Input>
                    </Form-item>
                </Card>

            </Form>
        </div>
    </div>
</template>
<script>
import fontIcon from "@/components/fontIcon"
import { Card, InputNumber } from "element-ui";
import Row from '../../realdata/water/report/row.vue';
export default {
    components: { fontIcon },
    props: ["id", "type", "equipmentType"],
    data() {
        var that = this;
        return {
            iotEquipment: {
                equipmentType: this.equipmentType,
                equipmentCode: "",
                equipmentName: "",
                communicationType: "",
                equipmentIp: "",
                equipmentPort: "",
                equipmentIsInstallDms: "",
                adjacentSite: "",
                slaveId: 1,
                equipStateOffset: "",
                writeOffset: "",
                topic: "",
                groupCode: 0,
                equipmentRegion:"",
                open:"",
                close:"",
                openEnd:"",
                closeEnd:""


            },
            // ruleValidateType: {
            //     equipmentCode: [
            //         { required: true, type: 'string', message: '编码不能为空', trigger: 'blur' },
            //         // { validator: this.checkDmsName, trigger: 'blur' },
            //     ],
            //     equipmentName: [
            //         { required: true, type: 'string', message: '名称不能为空', trigger: 'blur' },
            //         // { validator: this.checkDmsName, trigger: 'blur' },
            //     ],
            //     equipmentIp: [
            //         { required: true, type: 'string', message: 'IP不能为空', trigger: 'blur' },
            //         // { validator: this.checkIp, trigger: 'blur' },
            //     ],
            //     equipmentType: [
            //         { required: true, type: 'number', message: '类型不能为空', trigger: 'blur' },
            //     ],
            //     slaveId: [
            //         { required: true, type: 'number', message: 'Slave ID不能为空', trigger: 'blur' },
            //     ],
            //     equipStateOffset: [
            //         { required: true, type: 'number', message: '设备状态地址不能为空', trigger: 'blur' },
            //     ],
            //     writeOffset: [
            //         { required: true, type: 'number', message: '写入地址不能为空', trigger: 'blur' },
            //     ],
            // }
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

        resetFields: function () {
            this.iotEquipment.equipmentCode = "";
            this.iotEquipment.equipmentName = "";
            this.iotEquipment.communicationType = "";
            this.iotEquipment.equipmentIp = "";
            this.iotEquipment.equipmentPort = "";
            this.iotEquipment.equipmentIsInstallDms = "";
            this.iotEquipment.adjacentSite = "";
            this.iotEquipment.slaveId = 1;
            this.iotEquipment.equipStateOffset = "";
            this.iotEquipment.writeOffset = "";
            this.iotEquipment.topic = "";
            this.iotEquipment.groupCode = 0;
            this.iotEquipment.equipmentRegion = "";
            this.iotEquipment.close = "";
            this.iotEquipment.open = "";
            this.iotEquipment.openEnd = "";
            this.iotEquipment.closeEnd = "";

        },
        save(cb) {
            this.$refs['iotEquipment'].validate((valid) => {
                if (valid) {
                    this.$ajax.post("/service_rms/iotEquipment/save", { "data": JSON.stringify(this.iotEquipment) })
                        .then((res) => {
                            if (res.data.code == "200") {
                                cb && cb(true);
                                this.$refs['iotEquipment'].resetFields();
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
            this.$ajax.post("/service_rms/iotEquipment/findById", { "id": this.id })
                .then(res => {
                    this.iotEquipment = res.data.data;
                })
                .catch((error) => {
                    if (!error.url) { console.info(error); }
                });
        },

        getBaseData: function (id, type) {
            var a = new Promise((resolve, reject) => {
                this.$ajax.post("/dataservice/mdDictItem/findByTypeCode", { "code": "ASSEMBLE_LINE" })
                    .then(res => {
                        this.sourceList = res.data.data;
                        resolve(res.data.data);
                    })
                    .catch((error) => {
                        if (!error.url) {
                            console.info(error);
                            reject();
                        }
                    })
            });
            var b = new Promise((resolve, reject) => {
                this.$ajax.post("/dataservice/mdDictItem/findByTypeCode", { "code": "EQUIPMENT_STATE" })
                    .then(res => {
                        this.equipStatusList = res.data.data;
                        resolve(res.data.data);
                    })
                    .catch((error) => {
                        if (!error.url) {
                            console.info(error);
                            reject();
                        }
                    })
            });
            Promise.all([a, b]).then(() => {
                // this.type = this.$route.params.type;
                // this.id = this.$route.params.id;  

                if (id) {
                    this.getData(id);
                }
            }).catch((error) => {
                // 处理任何Promise 'a'或'b'中的错误
                console.error('Promise.all 错误:', error);
            });

        },
        // getData: function(id) {
        //     this.$ajax.post("/dataservice/equipManage/findById",{id:id})
        //         .then(res => {
        //             if(res.data.code == 200) {
        //                 this.equipManage = res.data.data;
        //                 this.sourceList.map((j) => {
        //                     if(j.id == this.equipManage.source) {
        //                         this.equipManage.sourceName = j.itemName;
        //                         return true;
        //                     } 
        //                 });

        //                 this.equipStatusList.map((j) => {
        //                     if(j.itemCode == this.equipManage.equipStatus) {
        //                         this.equipManage.equipStatusName = j.itemName;
        //                         return true;
        //                     } 
        //                 });

        //                   //文档附件回显
        //                   this.fileArr=[];
        //                     if(this.equipManage.fileId){
        //                         this.fileId = this.equipManage.fileId.split(";");
        //                         this.fileName = this.equipManage.fileName.split(";");
        //                     }
        //                     if (this.fileId && this.fileId.length > 0) {
        //                         this.fileArr = this.fileId.map((obj, k) => {
        //                             if (obj != null) {
        //                                 var name = this.fileName[k];
        //                                 var suffix = name.substring(name.lastIndexOf(".") + 1, name.length);
        //                                 var fileType = "";
        //                                 if (suffix == "gif" || suffix == "jpg" || suffix == "jpeg" || suffix == "bmp" || suffix == "png") {
        //                                     fileType = "image";
        //                                 }
        //                                 return {
        //                                     "md5": obj,
        //                                     id: obj,
        //                                     name: name,
        //                                     type: fileType,
        //                                     "src": this.$utils.getFileShowUrl(obj),
        //                                     url: this.$utils.getFileDownloadUrl(obj),
        //                                     downloadurl: this.$utils.getFileDownloadUrl(obj),
        //                                     showurl: this.$utils.getFileShowUrl(obj),
        //                                 };
        //                             }
        //                         });
        //                         this.$refs.webuploader.addDefaultFile(this.fileArr);
        //                     }
        //             } 
        //             //this.oldName = this.roleData.name;
        //         })
        //         .catch((error) => {
        //             if (!error.url) { console.info(error); }
        //         });
        // },

    },
    watch: {
        id: function () {
            if (this.id != "") {
                this.getData();
            }
        },
        equipmentType: function (newType) {
            if (newType) {
                console.log("equipmentType", newType);
                this.iotEquipment = {
                    ...this.iotEquipment,
                    equipmentType: newType,
                };
            }
        }
    },
    mounted: function () {

    }

}
</script>