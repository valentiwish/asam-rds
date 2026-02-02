<template>
    <div>
        <Card>
            <div v-if="!row.branchs || 0 == row.branchs.length">
                <h5>此水厂无支线信息</h5>
            </div>
            <Form v-else :label-width="100" :class="{'form-view':true}" inline>
                <Form-item v-for="(branch, index) in row.branchs" :label="branch.title" :key="index">
                    <template>{{branch.value}} m³</template>
                </Form-item>
            </Form>
        </Card> 
    </div>
</template>
<script>
    export default {
        props: ["row"],
        data(){
            return{

            }
        },
        methods:{
            
        },
        watch:{
            
        },
    };
</script>
<style scoped>
    .expand-row{
        margin-bottom: 16px;
    }
</style>