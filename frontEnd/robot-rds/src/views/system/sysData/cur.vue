<template>
    <div>
        <Form :label-width="108" ref="sysData" :model="sysData" :rules="ruleValidate" :class="{'form-view':type=='view'}" @submit.prevent="submit">
            <Form-item label="数据类型" prop="dataTypeId">
                <template v-if="type=='view'">{{sysData.dataTypeId}}</template>
                <Select v-else v-model="sysData.dataTypeId" placeholder="请选择数据类型">
                    <Option v-for="obj in dataTypes" :value="obj.id"  :key="obj.id">{{obj.typeName}}</Option>
                </Select>
            </Form-item>
            <Form-item label="编码显示内容" prop="textName">
                <template v-if="type=='view'">{{sysData.textName}}</template>
                <Input v-else v-model.trim="sysData.textName" placeholder="请输入编码显示内容，不超过30个字符" maxlength="30">
                </Input>
            </Form-item>
            
            <Form-item label="排序" prop="sequence">
                <template v-if="type=='view'">{{sysData.sequence}}</template>
                <InputNumber :min="0" :max="999" v-else type="number" v-model.trim="sysData.sequence" placeholder="请输入排序"></InputNumber>
            </Form-item>
        </Form>
    </div>
</template>
<script>
// import fontIcon from "@/components/fontIcon"
import CONFIG from '@/config/index.js'
export default {
    name: 'index',
    // components: { fontIcon },
    props: ["id", "type", "dataTypes"],
    data() {
        var that = this;
        return {
            type: '',
            id: '',
            dataTypes: '',
            sysData: {
                textName: '',
                dataTypeId: '',
                sequence: 1
            },
            ruleValidate: {
                "dataTypeId": [
                    //{ required: true, message: '请选择数据类型', trigger: 'change' },
                    { required: true, validator: this.checkDataTypeId, trigger: 'change' },
                ],
                textName: [
                    { required: true, message: '请输入编码显示内容', trigger: 'blur' },
                    { max: 30, message: '不超过30字', trigger: 'blur' },
                    { validator: this.checkTextName, trigger: 'blur' }
                ],
            }
        }
    },
    methods: {
        checkDataTypeId: function(rule, value, callback) {
            if (value) {
                callback();
            } else {
                callback("请选择数据类型");
            }
        },
        save(cb) {
            this.$refs['sysData'].validate((valid) => {
                if (valid) {
                    var sysDataVO = this.sysData;
                    this.$ajax.post("/service_user/sysData/save", sysDataVO)
                        .then((res) => {
                            if (res.data.code == "200") {
                                cb && cb(true);
                                this.$refs['sysData'].resetFields();
                                this.$Message.success('保存成功');
                            } else {
                                cb && cb(false);
                                this.$Message.error('保存出错');
                            }
                        })
                        .catch((error) => {
                            cb && cb(false);
                            if (!error.url) { console.info(error); }
                        })
                } else {
                    this.$emit("changeVal");
                    this.$Message.error('表单校验失败!');
                }
            })
        },
        //校验
        checkTextName: function(rule, value, callback) {
            if (value) {
                this.$ajax.post("/service_user/sysData/check", { 'textName': value, 'id': this.id })
                    .then((res) => {
                        if (res.data) {
                            callback();
                        } else {
                            callback("名字已经存在");
                        }
                    })
                    .catch((error) => {
                        callback('校验失败');
                    })
            }
        },
        //重新加载数据
        getData: function() {
            this.$ajax.post("/service_user/sysData/findById", { "id": this.id })
                .then((res) => {
                    this.sysData = res.data.data;
                    this.sysData.dataTypeId = this.sysData.dataTypeId ? Number(this.sysData.dataTypeId) : null;
                    if(!res.data.data.sysDataType){
                        this.sysData.sysDataType={};
                    }
                })
                .catch((error) => {
                    if (!error.url) { console.info(error); }
                })
        },
        resetFields: function() {
            this.$refs.sysData.resetFields();
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
