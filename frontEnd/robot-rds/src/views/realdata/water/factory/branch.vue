<template>
	<div>
       <Form :label-width="108" :model="branch" ref="branch" :rules="ruleValidate" :class="{'form-view':type=='view'}" inline>
                <Form-item label="支线名称" prop="name">
					<template v-if="type=='view'">{{branch.name}}</template>
                    <Input v-else v-model.trim="branch.name" aria-modal="50" placeholder="请输入支线名称，不超过50字" />
                </Form-item>

                <Form-item label="流向">
					<template v-if="type=='view'">{{branch.flowDirection}}</template>
                    <Input v-else v-model.trim="branch.flowDirection" maxlength="50" placeholder="请输入流向，不超过50字" />
                </Form-item>
                
				<Form-item label="供水区域描述" class="singleline">
					<template v-if="type=='view'">{{branch.description}}</template>
                     <Input v-else v-model.trim="branch.description" type="textarea" maxlength="250" placeholder="请输入供水区域描述，不超过250字" />
                    <div class="str_count" v-if="type!=='view'">{{branch.description ? branch.description.length : 0}}/250</div>
                </Form-item>
            </Form>
		</div>
</template>
<script>
export default {
    components: {},
	props: ["id", "type", "branchObj"],
    data() {
        return {
			branch: { //支线
                id: '',
                name: '',
                flowDirection: '',
                description: ''
            },
            ruleValidate: {
                name: [
                    { required: true, message: '名称不能为空', trigger: 'blur' },
                    { validator: this.checkName, trigger: 'blur' },
                ]
            }
        }
    },
    methods: {
        checkName: function(rule, value, callback){
            if(value){
                this.$ajax.post("/service_basedata/branch/checkName", {'name': value, 'id': this.branch.id})
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
        save: function(cb) {
			this.$refs["branch"].validate((valid) => {
                if (valid) {				
					cb && cb(this.branch);
				 }else {
                        this.$Message.error('表单校验失败');
                 }
			});
		},
    },
	mounted: function() {
		// this.branch.factoryId = this.id;
	},
	watch: {
        "branchObj": function(n, o){
            this.branch = n;
        }
    },
	created: function() {
        
    }
}
</script>
