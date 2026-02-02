<template>
    <div>
        <div class="page-content">

            <Form :label-width="120" ref="modbusEquipment" :model="modbusEquipment"
                :class="{ 'form-view': type == 'view' }" :rules="ruleValidateType" @submit.prevent="submit">
                <Card>
                    <h2 slot="title">基础信息</h2>
                    <Row>
                        <Col span="12">
                        <Form-item label="设备类型" prop="equipmentType">
                            <template v-if="type == 'view'">{{ modbusEquipment.equipmentType }}</template>
                            <Input v-else v-model.trim="modbusEquipment.equipmentType" :disabled="true"
                                placeholder="请输入设备类型，不超过30字" maxlength="30"></Input>
                        </Form-item>
                        </Col>

                        <Col span="12">
                        <Form-item label="设备编号" prop="equipmentCode">
                            <template v-if="type == 'view'">{{ modbusEquipment.equipmentCode }}</template>
                            <Input v-else v-model.trim="modbusEquipment.equipmentCode" :disabled="false"
                                placeholder="请输入设备编号，不超过30字" maxlength="30"></Input>
                        </Form-item>
                        </Col>

                        <Col span="12">
                        <Form-item label="设备名称" prop="equipmentName">
                            <template v-if="type == 'view'">{{ modbusEquipment.equipmentName }}</template>
                            <Input v-else v-model.trim="modbusEquipment.equipmentName" :disabled="false"
                                placeholder="请输入设备名称，不超过30字" maxlength="30"></Input>
                        </Form-item>
                        </Col>

                        <Col span="12">
                        <Form-item label="相邻站点" prop="adjacentSite">
                            <template v-if="type == 'view'">{{ modbusEquipment.adjacentSite }}</template>
                            <Input v-else v-model.trim="modbusEquipment.adjacentSite" :disabled="false"
                                placeholder="请输入相邻站点，不超过30字" maxlength="30"></Input>
                        </Form-item>
                        </Col>
                    </Row>
                </Card>
                <Card>
                    <h2 slot="title">Modbus配置</h2>
                    <Row>
                        <Col span="12">
                        <Form-item label="设备IP" prop="equipmentIp">
                            <template v-if="type == 'view'">{{ modbusEquipment.equipmentIp }}</template>
                            <Input v-else v-model.trim="modbusEquipment.equipmentIp" placeholder="请输入设备IP，不超过30字"
                                maxlength="30"></Input>
                        </Form-item>
                        </Col>
                        <Col span="12">
                        <Form-item label="Slave ID" prop="slaveId">
                            <template v-if="type == 'view'">{{ modbusEquipment.slaveId }}</template>
                            <InputNumber v-else v-model.trim="modbusEquipment.slaveId" :disabled="true"
                                placeholder="请输入Slave Id" maxlength="30"></InputNumber>
                        </Form-item>
                        </Col>
                        <Col span="12">
                        <Form-item label="设备状态地址" prop="equipStateOffset">
                            <template v-if="type == 'view'">{{ modbusEquipment.equipStateOffset }}</template>
                            <InputNumber v-else v-model.trim="modbusEquipment.equipStateOffset" :disabled="false"
                                placeholder="整型偏移量" maxlength="30"></InputNumber>
                        </Form-item>
                        </Col>
                        <Col span="12">
                        <Form-item label="写入地址" prop="writeOffset">
                            <template v-if="type == 'view'">{{ modbusEquipment.writeOffset }}</template>
                            <InputNumber v-else v-model.trim="modbusEquipment.writeOffset" :disabled="false"
                                placeholder="整型偏移量" maxlength="30"></InputNumber>
                        </Form-item>
                        </Col>
                    </Row>
                </Card>
                <Card>
                    <h2 slot="title">备注信息</h2>
                    <Form-item label="说明" prop="remark">
                        <template v-if="type == 'view'">{{ modbusEquipment.remark }}</template>
                        <Input v-else v-model.trim="modbusEquipment.remark" type="textarea" placeholder="请填写说明">
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
            modbusEquipment: {
                equipmentType: this.equipmentType,
                equipmentCode: "",
                equipmentName: "",
                equipmentIp: "",
                adjacentSite: "",
                slaveId: 1,                                                                                                                                                                 
                equipStateOffset: "",
                writeOffset: "",

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
            this.modbusEquipment.equipmentCode = "";
            this.modbusEquipment.equipmentName = "";
            this.modbusEquipment.equipmentIp = "";
            this.modbusEquipment.adjacentSite = "";
            this.modbusEquipment.slaveId = 1;
            this.modbusEquipment.equipStateOffset = "";
            this.modbusEquipment.writeOffset = "";
        },
        save(cb) {
            this.$refs['modbusEquipment'].validate((valid) => {
                if (valid) {
                    this.$ajax.post("/service_rms/modbusEquipment/save", { "data": JSON.stringify(this.modbusEquipment) })
                        .then((res) => {
                            if (res.data.code == "200") {
                                cb && cb(true);
                                this.$refs['modbusEquipment'].resetFields();
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
            this.$ajax.post("/service_rms/modbusEquipment/findById", { "id": this.id })
                .then(res => {
                    this.modbusEquipment = res.data.data;
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
                this.modbusEquipment = {
                    ...this.modbusEquipment,
                    equipmentType: newType,
                };
            }
        }
    },
    mounted: function () {

    }

}
</script>