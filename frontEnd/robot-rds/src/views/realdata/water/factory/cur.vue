<template>
    <div>
        <page-title></page-title>
        <div class="page-content">
        <Card class="card-blue">
            <h2 slot="title">水厂信息</h2>
            <Form :label-width="100" ref="factory" :model="factory" :rules="ruleValidate" inline :class="{'form-view':type=='view'}" @submit.prevent="submit">
                <Form-item label="水厂名称" prop="name">
					<template v-if="type=='view'">{{factory.name}}</template>
                    <Input v-else v-model.trim="factory.name" maxlength="50" placeholder="请输入水厂名称，不超过50字" />
                </Form-item>

                <Form-item label="水厂电话" prop="tel">
					<template v-if="type=='view'">{{factory.tel}}</template>
                    <Input v-else v-model.trim="factory.tel" placeholder="请输入水厂电话" />
                </Form-item>

                <Form-item label="水厂地址">
					<template v-if="type=='view'">{{factory.address}}</template>
                    <Input v-else v-model.trim="factory.address" maxlength="50" placeholder="请输入水厂地址，不超过50字" />
                </Form-item>
                
                <Form-item label="水厂描述" class="singleline">
                    <template v-if="type=='view'">{{factory.description}}</template>
                    <Input v-else v-model.trim="factory.description" type="textarea" maxlength="250" placeholder="请输入水厂描述，不超过250字" />
                    <div class="str_count" v-if="type!=='view'">{{factory.description ? factory.description.length : 0}}/250</div>
                </Form-item>
            </Form>
        </Card>

        <Card >
			<h2 slot="title">水厂支线信息</h2>
            <div v-if="'view'!=type">
                <Button type="primary"  @click="toAdd" style="float: right">添加支线</Button>
                <br/><br/>
            </div>
            <data-grid :option="gridOption" ref="grid"></data-grid>
		</Card>

		<!-- 新增-->
		<Modal v-model="modal_add" title="新增支线信息">
			<update-modal ref="addForm" v-if="modal_add"></update-modal>
				<div slot="footer">
					<Button type="primary" class="ivu-btn ivu-btn-primary" @click="saveBranch" :loading="loading">保存</Button>
					<Button type="ghost" @click="modal_add=false">关闭</Button>
				</div>
		</Modal>
		<!-- 修改-->
		<Modal v-model="modal_update" title="修改支线信息">
			<update-modal :branchObj="branch" ref="updateForm" type="update"></update-modal>
				<div slot="footer">
					<Button type="primary" class="ivu-btn ivu-btn-primary" @click="updateBranch" :loading="loading">保存</Button>
					<Button type="ghost" @click="modal_update=false">关闭</Button>
				</div>
		</Modal>
		
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
import updateModal from "./branch"
export default {
    components: { dataGrid, updateModal},
     data: function() {
        var that = this;
		return {
            loading: false,
            id: '',
            type: '',
            factory: {
                id: '',
                name:'',
				tel:'',
                address:'',
                description:''
            },
            modal_add: false,
			modal_update: false, 
            modal_view: false,
            delBranchs: [],
            branch: {},
            ruleValidate: {
                name: [
                    { required: true, message: '名称不能为空', trigger: 'blur' },
                    { validator: this.checkName, trigger: 'blur' },
                ],
                tel: [
                    { validator: this.checkTel, trigger: 'blur' },
                ]
            },
			gridOption:{
				"header":true,
				"iconCls": "fa-power-off",
				"data":[],
				"columns":[
					{
						"title":"支线名称",
						"key": "name",
                        "align": "center",
                    },
                   {
						"title":"流向",
						"key": "flowDirection",
                        "align": "center",
                    },
                    {
						"title":"供水区域描述",
						"key": "description",
                        "align": "center",
                    },
                    {
                        key: 'tool',
                        title: '操作',
                        align: "center",
                        width: 200,
                        render(h, params) {
                            return h('div', [
                                    h('Button', {
                                        props: {
                                            type: 'primary',
                                            size: 'small',
                                            allow: 'update',
                                            disabled: 'view' == that.type ? true : false
                                        },
                                        style: {
                                            marginRight: '5px',
                                        },
                                        on: {
                                            click: () => {
                                                that.toUpdate(params.row)
                                            }
                                        }
                                    }, '修改'),
                                    h('Button', {
                                        props: {
                                            type: 'info',
                                            size: 'small',
                                            allow: 'delete',
                                            disabled: 'view' == that.type ? true : false,
                                        },
                                        style: {
                                            marginRight: '5px',
                                        },
                                        on: {
                                            click: () => {
                                                that.del(params.row)
                                            }
                                        }
                                    }, '删除')
                                ]);
                        }
                    }
                ],
                "loadFilter": function(data) {
                    return data.data;
                },
                }
            }
        },
    methods: {
        checkName: function(rule, value, callback){
            if(value){
                this.$ajax.post("/service_basedata/factory/checkName", {'name': value, 'id': this.id})
                .then((res) => {
                    if(res.data){
                        callback();
                    }else{
                        callback("名称已存在");
                    }
                })
                .catch((error) => {
                    callback('校验失败');
                })
            } else {
                callback('名称不能为空');
            }
        },
        checkTel: function(rule, value, callback){
            if (value){
                var ph = /^(0|86|17951)?(13[0-9]|15[012356789]|17[01678]|18[0-9]|14[57])[0-9]{8}$/;
                var mb = /^(0[0-9]{2,3}\-)([2-9][0-9]{6,7})+(\-[0-9]{1,4})?$/;
                if(!ph.test(value) && !mb.test(value)){ 
                    callback("电话号码格式不正确");
                } else {
                    callback();
                }
            } else {
                callback();
            }
        },
		//新增支线
		toAdd: function(){
			this.modal_add = true;
		},
		//修改支线
        toUpdate: function(obj){
            this.modal_update = true;
            this.branch = JSON.parse(JSON.stringify(obj));
		},
		del: function(obj) {
            let that = this;
            this.$Modal.confirm({
                title: '删除',
                content: '您确认删除此条数据？',
                onOk: function() {
                    that.gridOption.data.splice(obj._index, 1);
                    //先从页面删除，将已删除的支线进行缓存，点击保存时再从后端进行删除
                    if (obj.id){
                        obj.state = 0;
                        that.delBranchs.push(obj);
                    }
                }
            })
        },
		saveBranch: function(cb) {
            this.$refs['addForm'].save((branch) => {
                this.gridOption.data.push(branch);
                this.modal_add = false;
            });
        },
        updateBranch: function(cb) {
            this.$refs['updateForm'].save((branch) => {
                this.gridOption.data.splice(this.branch._index, 1, branch);
                this.modal_update = false;
                this.$refs.grid.reLoad({});
            });
         },
        save: function() {
            this.$refs['factory'].validate((valid) => {
                if (valid) {
                    this.loading = true;
                    var waterFactoryVO = this.factory;
                    var branchs = this.gridOption.data.concat(this.delBranchs);
                    waterFactoryVO.jsonWaterBranchVO = JSON.stringify(branchs);
                    waterFactoryVO.waterBranchVOList = [];
                    this.$ajax.post("/service_basedata/factory/save", waterFactoryVO) //{emulateJSON: true}
                        .then(res => {
                            if (res.data.code == 200) {
                                this.$Message.success("保存成功");
                                this.$router.push("/waterFactory/list");
                            }
                            this.loading = false;
                        })
                        .catch((err) => {
                            if (!err.url) {
                                this.loading = false;
                                console.info(err);
                            }
                        })
                }
            })
        },
        getData: function(){
            this.$ajax.post("/service_basedata/factory/findById", {"id": this.id})
                .then(res => {
                    if (res.data.code == 200) {
                        this.factory = res.data.data;
                        if (this.factory && this.factory.waterBranchVOList){
                            this.gridOption.data = this.factory.waterBranchVOList;
                        }
                    }
                })
                .catch((err) => {
                    if (!err.url) {
                        console.info(err);
                    }
                })
        },
    },
    created: function() {
        //获取id,type
        this.id = this.$route.params.id;
        this.type = this.$route.params.type;
        if (this.id){
            this.getData();
        }
    }
}
</script>
