<template>
    <div>
        <Form :label-width="80" ref="operation" :model="operation" :rules="ruleValidate" :class="{'form-view':type=='view'}" @submit.native.prevent="save">
            <Form-item label="名称" prop="name">
                <template v-if="type=='view'">{{operation.name}}</template>
                <Input v-else v-model.trim="operation.name" maxlength="30" placeholder="请输入名称，由字母组成，按照驼峰规则命名"></Input>
            </Form-item>
            <Form-item label="描述" prop="description">
                <template v-if="type=='view'">{{operation.description}}</template>
                <Input v-else v-model.trim="operation.description" maxlength="50" placeholder="请输入描述，由汉字组成，不超过50个字符"></Input>
            </Form-item>
            <Form-item label="操作标识" prop="operationOID">
                <template v-if="type=='view'">{{operation.operationOID}}</template>
                <Input v-else v-model.trim="operation.operationOID" placeholder="请输入操作标识符,由数字组成，最小为1000" disabled></Input>
            </Form-item>
            <Form-item label="图标" prop="icon">
                <i v-if="type=='view'" :class="operation.icon"></i>
                <font-icon v-else v-model="operation.icon" placeholder="请选择图标"></font-icon>
            </Form-item>
            <Form-item label="样式" prop="icon">
                <Button v-if="type=='view'" :type="operation.style"><i :class="operation.icon"></i> {{operation.description}}</Button>
                <template v-else>
                    <Select v-model="operation.style" placeholder="请选择样式">
                        <Option :value="item" :label="item" v-for="item in styleList" :key="item.id">
                            <Button :type="item" size="small">{{item}}</Button>
                        </Option>
                    </Select>
                </template>
            </Form-item>
            <Form-item label="排序" prop="sort">
                <template v-if="type=='view'">{{operation.sort}}</template>
                <InputNumber :min="0" :max="100" v-else v-model.trim="operation.sort" placeholder="请输入排序"></InputNumber>
            </Form-item>
            <div style="padding:20px 10px;text-align:center;background:#f0f0f0;" v-show="operation.description">
                <h2 style="font-weight:100;margin-bottom:10px;">图标预览</h2>
                <Button :type="operation.style"><i :class="operation.icon"></i> {{operation.description}}</Button>
            </div>
        </Form>
    </div>
</template>
<script>
import fontIcon from "@/components/fontIcon"
import CONFIG from '@/config/index.js'

export default {
    name: 'index',
    components: { fontIcon },
    props: ["id", "type"],
    data() {
        var that = this;
        return {
            ops: {},
            operation: {
                icon: '',
                moudleId: '',
                name: '',
                description: '',
                sort: 0,
                operationOID: '1000',
                style: '',
            },
            ruleValidate: {
                name: [
                    { required: true, message: '请输入名称，由字母组成，按照驼峰命名', trigger: 'blur' },
                    { max: 30, message: '不超过30字', trigger: 'blur' },
                    {
                        validator: (rule, value, callback) => {
                            if (!/^[a-zA-Z]+$/.test(value)) {
                                callback(new Error("由字母组成，按照驼峰命名"));
                            } else {
                                callback();
                            }

                        },
                        trigger: 'blur'
                    }
                ],
                description: [
                    { required: true, message: '请输入描述，由汉字组成', trigger: 'blur' },
                    { max: 30, message: '不超过30字', trigger: 'blur' },
                    {
                        validator: (rule, value, callback) => {
                            if (!/^[\u4E00-\u9FA5]+$/.test(value)) {
                                callback(new Error("由汉字组成"));
                            } else {
                                callback();
                            }

                        },
                        trigger: 'blur'
                    }
                ],
            },
            styleList: ["default", "primary", "ghost", "dashed", "info", "success", "warning", "error"]
        }
    },
    methods: {

        save(cb) {
            this.$refs['operation'].validate((valid) => {
                if (valid) {
                    this.$ajax.post("/service_user/operation/addOperation", { "data": JSON.stringify(this.operation) })
                        .then((res) => {
                            if (res.data.code == "200") {
                                cb && cb(true);
                                this.$Message.success('保存成功');
                                this.$refs['operation'].resetFields();
                            } else {
                                this.$Message.error('保存出错');
                            }
                        })
                        .catch((error) => {
                            if (!error.url) { console.info(error); }
                        })
                } else {
                    this.$emit("changeVal");
                    this.$Message.error('表单校验失败!');
                }
            })
        },
        getData: function() {
            this.$ajax.post("/service_user/operation/findById", { "id": this.id })
                .then((res) => {
                    this.operation = res.data.data;
                })
                .catch((error) => {
                    if (!error.url) { console.info(error); }
                })
        },
        resetFields: function() {
            this.$refs.operation.resetFields();
        },
        checkName: function(rule, value, callback) {
            if (!/^[a-zA-Z]+$/.test(value)) {
                callback(new Error("由字母组成，按照驼峰命名"));
            } else {
                if (0 < value.length) {
                    this.$ajax.post("/service_user/operation/checkNameUnique", {
                            "id": (undefined == this.id ? null : this.operation.id),
                            "name": value
                        }).then(res => {
                            if (200 == res.data.code) {
                                callback();
                            } else {
                                callback("该名称已经被使用");
                            }
                        })
                        .catch((error) => {
                            callback("获取数据失败");
                        });
                } else {
                    callback();
                }
            }
        },
    },
    watch: {
        id: function() {
            if (this.id != "") {
                this.getData();
            }
        }
    },
    created: function() {
        //获取operationOID
        this.$ajax.post("/service_user/operation/getOperationOID")
            .then((res) => {
                if (res.data.data) {
                    this.ops = res.data.data;
                    this.operation.operationOID = parseInt(this.ops.operationOID) + 1;
                } else {
                    this.ops = {};
                    this.operation.operationOID = '1000';
                }
            })
    }
}

</script>
