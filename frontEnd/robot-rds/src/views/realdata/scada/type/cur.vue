<template>
    <div>
        <page-title></page-title>
        <div class="page-content">
        <Card class="card-blue">
            <h2 slot="title">类型信息</h2>
            <Form :label-width="120" ref="scadaType" :model="scadaType" :rules="ruleValidate" inline :class="{'form-view':type=='view'}" @submit.prevent="submit">
                <Form-item label="类型编码" prop="typeCode">
					<template v-if="type=='view'">{{scadaType.typeCode}}</template>
                    <Input v-else v-model="scadaType.typeCode" maxlength="250" placeholder="请输入类型编码，不超过250字"></Input>
                </Form-item>

                <Form-item label="类型名称" prop="typeName">
					<template v-if="type=='view'">{{scadaType.typeName}}</template>
                    <Input v-else v-model="scadaType.typeName" maxlength="250" placeholder="请输入类型名称，不超过250字"></Input>
                </Form-item>
                
                <Form-item label="备注" class="singleline">
                    <template v-if="type=='view'">{{scadaType.remark}}</template>
                    <Input v-else v-model.trim="scadaType.remark" type="textarea" maxlength="250" placeholder="请输入备注，不超过250字" />
                    <div class="str_count" v-if="type!=='view'">{{scadaType.remark ? scadaType.remark.length : 0}}/250</div>
                </Form-item>
            </Form>
        </Card>

        <Card >
			<h2 slot="title">当前类型变量</h2>
            <data-grid :option="gridOption" ref="grid"></data-grid>
		</Card>
		
        <div style="text-align: center;" slot="footer">
            <Button type="primary" class="ivu-btn ivu-btn-primary" v-if="type!='view'" :loading="loading" @click="save">保存</Button>
            <Button type="ghost" style="margin-left: 8px" v-if="type!='view'" @click="$router.back()">取消</Button>
            <Button type="ghost" style="margin-left: 8px" v-if="type=='view'" @click="$router.back()">返回</Button>
        </div>
        </div>
    </div>
</template>
<script>
import dataGrid from "@/components/datagrid"
export default {
    components: { dataGrid },
     data: function() {
        var that = this;
		return {
            loading: false,
            id: '',
            type: '',
            scadaType: {
                id: '',
                typeCode: '',
                typeName: '',
                oldCode: '',
            },
            ruleValidate: {
                typeCode: [
                    { type: 'string', required: true, message: '类型编码不能为空', trigger: 'blur' },
                ],
                typeName: [
                    { type: 'string', required: true, message: '类型名称不能为空', trigger: 'blur' },
                ],
            },
			gridOption:{
				"header":true,
				"iconCls": "fa-power-off",
                "data":[],
                "auto": false,
				"columns":[
                    {
                        "title": "变量名",
                        "key": "tagName",
                        "align": "center",
                    },
                    {
                        "title": "展示名",
                        "key": "showName",
                        "align": "center",
                    },
                    {
                        "title": "类型",
                        "key": "type",
                        "align":"center",
                    }
                ],
                "loadFilter": function(data) {
                    return data.data;
                },
            }
        }
    },
    methods: {
        save: function() {
            let that = this;
            this.$refs['scadaType'].validate((valid) => {
                if (valid) {
                    this.loading = true;
                    if (this.scadaType.oldCode != this.scadaType.typeCode){
                        this.$Modal.confirm({
                            title: '修改类型编码会同步修改变量数据类型',
                            content: '确认修改类型编码吗？',
                            onOk: function (){
                                that.saveData();
                            }
                        })
                    }else {
                        this.saveData();
                    }
                }
            })
        },
        saveData: function(){
            var url = "/service_basedata/type/save";
            this.$ajax.post(url, this.scadaType)
                .then(res => {
                    if (200 == res.data.code) {
                        this.$Message.success("保存成功");
                        this.$router.push("/scadaType/list");
                    }
                    this.loading = false;
                })
                .catch((err) => {
                    if (!err.url) {
                        this.loading = false;
                        console.info(err);
                    }
                })
        },
        getData: function(){
            this.$ajax.post("/service_basedata/type/findById", {"id": this.id})
                .then(res => {
                    if (res.data.code == 200) {
                        this.scadaType = res.data.scadaType;
                        this.gridOption.data = res.data.variables;
                        this.scadaType.oldCode = this.scadaType.typeCode;
                    }
                })
                .catch((err) => {
                    if (!err.url) {
                        console.info(err);
                    }
                })
        }
    },
    created: function() {
        //获取id,type ZXBF_MCUIO3_WL.QSB_TZSW_MEM
        this.id = this.$route.params.id;
        this.type = this.$route.params.type;
    },
    mounted: function(){
        this.getData();
    },
    watch: {
        
    },
}
</script>
