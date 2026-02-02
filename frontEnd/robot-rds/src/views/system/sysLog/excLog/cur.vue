<template>
    <div class="page-content">
        <Form :label-width="108" :class="{'form-view':true}" inline>
            <Card>

                <div style="text-align:center;float:right">
                    <Button type="ghost" @click="$router.back()">返回</Button>
                </div>

                <h2 slot="title">基本信息</h2>
                <Form-item label="登录账号">
                    <template>{{obj.operUserName}}</template>
                </Form-item>

                <Form-item label="操作人">
                    <template>{{obj.operUserName}}</template>
                </Form-item>

                <Form-item label="操作人电话">
                    <template>{{obj.operUserPhone}}</template>
                </Form-item>

                <Form-item label="操作IP">
                    <template>{{obj.operIp}}</template>
                </Form-item>

                <Form-item label="访问地址">
                    <template>{{obj.operUri}}</template>
                </Form-item>

                <Form-item label="操作时间">
                    <template>{{new Date(obj.operCreateTime).Format("yyyy-MM-dd hh:mm:ss")}}</template>
                </Form-item>

                <Form-item label="操作方法" class="singleline">
                    <template>{{obj.operMethod}}</template>
                </Form-item>

                <Form-item label="异常名称" class="singleline">
                    <template>{{obj.excName}}</template>
                </Form-item>

                <Form-item label="传入参数" class="singleline">
                    <template>{{obj.excRequParam}}</template>
                </Form-item>

                <Form-item label="异常详情" class="singleline">
                    <template>{{obj.excMessage}}</template>
                </Form-item>
            </Card>
        </Form>
    </div>
</template>
<script>
    export default {
        data() {
            return {
                id: '',
                obj: {}
             }
        },
        methods: {
            //查询数据
            getData: function(id) {
                this.$ajax.post("/service_user/sysLog/findExcById", {"id": id})
                .then((res) => {
                    if (200 == res.data.code){
                        this.obj = res.data.data;
                    }else {
                        this.$Message.warning("网络异常");
                    }
                })
                .catch((err) => {
                    if (!err.url) {console.info(err);}
                })
            },
        },
        created: function() {
            this.id = this.$route.params.id;
            this.getData(this.id);
        },
        watch: {
            
        },
    }
</script>
