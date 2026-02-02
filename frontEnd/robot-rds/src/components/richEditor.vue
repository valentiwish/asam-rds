<template>
    <div>
        <vue-ueditor-wrap
            v-model="message" 
            :config="editorConfig" 
            @ready="ready"
        />
        <script id="content" class="richeditor" type="text/plain"></script>
    </div>
</template>

<script>
    import VueUeditorWrap from 'vue-ueditor-wrap'
    export default{
        name: 'richEditor',
        components: {
            VueUeditorWrap
        },
        props: {
            value: {
                default: ''
            },
            maximumWords:{
                type: Number,
                default: 10000,
            },
        },
        data(){
            return {
                message: '',
                // 简单配置
                editorConfig: {
                    // 编辑器不自动被内容撑高
                    autoHeightEnabled: false,
                    elementPathEnabled:false,
                    maximumWords :this.maximumWords,
                    // 初始容器高度
                    initialFrameHeight: 300,  
                    // 初始容器宽度
                    initialFrameWidth: '100%',
                    UEDITOR_HOME_URL: './static/plug/ueditor/',
                    serverUrl: '/ueditor/php/controller.php',
                }
            }
        },
        "methods": {
            setContent: function(data) {
                this.message = data;
            },
            getContent: function(data) {
                return this.message;
            }
        },
        mounted: function() {
            
        },
        watch:{
            message(){
                this.$emit("input",this.message);
            }
        }
    }
</script>

<style>
    
</style>
