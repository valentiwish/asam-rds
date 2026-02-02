<template>
    <div class="iframe-wrap">
        <iframe :src="src" frameborder="0" ref="iframe"></iframe>
        <img :src="'./static/images/loading.webp'"  v-if="isLoading" class="loading-img">
        <div class="error-text" v-if="error">
            <img :src="'./static/images/custom-empty-image.png'">
            <div style="margin-top:15px;">当前页面未配置{{allowDataEaseUrl}}</div>
        </div>
    </div>
</template>

<script>
    export default{
        name: 'dataease',
        data(){
            return {
                src:"",
                isLoading:true,
                error:false,
            }
        },
        computed:{
            allowDataEaseUrl: function() {
                var ret = null;
                let obj = this.$store.state.auth.sysMenuMap[this.$route.name];
                if(obj && obj.allow){
                    obj.allow.some((j) => {
                        if(j.match("/dataease/")){
                            ret = j.replace("/dataease/","");
                            return true;
                        }
                        return false;
                    }); 
                }
                else{
                    ret= null;
                }
                return ret;
            }  
        },
        methods: {
            setUrl(){
                this.error = false;
                this.isLoading = true;
                var baseUrl= window.location.origin + "/dataease-api";
                if(_sysBaseConf && _sysBaseConf.sysDataEaseBaseUrl){
                    baseUrl = _sysBaseConf.sysDataEaseBaseUrl;
                }
                if(this.$route.params && this.$route.params.linkId){
                    this.src= baseUrl + "/link/"+this.$route.params.linkId;
                }
                else if(this.allowDataEaseUrl){
                    this.src= baseUrl + "/link/"+this.allowDataEaseUrl;
                }
                else{
                    this.error = true;
                    this.isLoading = false;
                }
            }
        },
        mounted(){
            if(this.$refs.iframe){
                this.$refs.iframe.onload = ()=>{
                    this.isLoading = false; 
                }
            }
        },
        created(){
            this.setUrl();
        },
        watch:{
            "$route.params.linkId"(){
                this.setUrl();
            },
            "allowDataEaseUrl"(){
                this.setUrl();
            }
        }
    }
</script>

<style scoped>
    .iframe-wrap,iframe{
        width:100%;
        height:100%;
        vertical-align: top;
    }
    .loading-img{
        position: absolute;
        left:50%;
        top:50%;
        width:300px;
        margin-left:-150px;
        margin-top:-200px;
    }
    .error-text{
        position: absolute;
        left:50%;
        top:50%;
        width:200px;
        margin-left:-100px;
        margin-top:-120px;
        text-align: center;
        font-size:20px;
        user-select: none;
    }
    .error-text img{
        width:100px;
    }
</style>
