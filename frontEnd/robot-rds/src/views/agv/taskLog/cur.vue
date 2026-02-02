<template>
    <div>
        <div class="page-content">
            <Form :label-width="120" ref="taskLog" :model="taskLog"  :class="{'form-view':type=='view'}" @submit.prevent="submit">   
                <Form-item label="操作内容"  prop="operateContent">
                    <template v-if="type=='view'">{{taskLog.operateContent}}</template>
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
                taskLog: {
                    operateContent: ''
                }
            }
        },
        methods: {
            getData: function() {
            //获取此模块模块基本信息 根据id查询
                this.$ajax.post("/service_rms/taskLog/findById", { "id": this.id })
                    .then(res => {
                        this.taskLog = res.data.data;
                    })
                    .catch((error) => {
                        if (!error.url) { console.info(error); }
                    });
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