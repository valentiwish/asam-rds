<template>
    <div>
        <Form :label-width="108" ref="smsMessageRecord" :model="smsMessageRecord" :class="{'form-view':type=='view'}" @submit.prevent="submit">
            <Form-item label="批次号" prop="batchNo">
                <template v-if="type=='view'">{{smsMessageRecord.batchNo}}</template>
            </Form-item>
            <Form-item label="短信内容" prop="content">
                <template v-if="type=='view'">{{smsMessageRecord.content}}</template>
            </Form-item>
            <Form-item label="模板名称" prop="tempName">
                <template v-if="type=='view'">{{smsMessageRecord.tempName}}</template>
            </Form-item>
            <Form-item label="接收电话" prop="phone">
                <template v-if="type=='view'">{{smsMessageRecord.phone}}</template>
            </Form-item>
            <Form-item label="发送日期" prop="sendDate">
                <template v-if="type=='view'">{{smsMessageRecord.sendDate}}</template>
            </Form-item>
            <FormItem label="发送人" prop="sendUserName">
                <template v-if="type=='view'">{{smsMessageRecord.sendUserName}}</template>
            </FormItem>
            <FormItem label="发送人单位" prop="sendUserOrg">
                <template v-if="type=='view'">{{smsMessageRecord.sendUserOrg}}</template>
            </FormItem>
            <FormItem label="发送人电话" prop="sendUserPhone">
                <template v-if="type=='view'">{{smsMessageRecord.sendUserPhone}}</template>
            </FormItem>
        </Form>
    </div>
</template>
<script>
export default {
    components: {
    },
    props: ["id", 'type'],
    data() {
        return {
            smsMessageRecord: {
                accountName: '',
                accountNo: '',
                batchNo: '',
                content: '',
                tempId: '',
                tempName: '',
                phone: '',
                sendDate: '',
                sendStatus: '',
                sendUserId: '',
                sendUserName: '',
                sendUserOrg: '',
                sendUserPhone: '',
            },
        }
    },
    methods: {
        //重新加载数据
        getData: function() {
            this.$ajax.postJson("/service_sms/SMSRecord/findById", {
                    "id": this.id
                })
                .then((res) => {
                    this.smsMessageRecord = res.data.data;

                    if (null != res.data.data.sendDate) {
                        this.smsMessageRecord.sendDate = new Date(res.data.data.sendDate).Format('yyyy-MM-dd hh:mm:ss');
                    }
                    if (res.data.data.sendStatus == 0) {
                        this.smsMessageRecord.sendStatus = "未发送";
                    }
                    if (res.data.data.sendStatus == 1) {
                        this.smsMessageRecord.sendStatus = "已发送";
                    }
                    var obj = JSON.parse(this.smsMessageRecord.tempName);
                    this.smsMessageRecord.tempId = obj.id;
                    this.smsMessageRecord.tempName = obj.name;
                })
                .catch((error) => {
                    if (!error.url) {
                        console.info(error);
                    }
                })
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
<style>


</style>
