<template>
    <Button 
        :type="selftype" 
        :disabled="disabled" 
        :class="'allowbtn '+allow" 
        :size="size" 
        :title="$slots.default[0].text + allow + otherText" 
        :ghost="size =='small'"
        v-if="isShow"
        @click="emitclick">
        <i :class="selffaicon" class="fa" v-if="selffaicon"></i> <slot></slot>
    </Button>
</template>

<script>
    import defaultOption from "./option.js";
    import CONFIG from "@/config/index"
    export default {
        name:"allowbtn",
        props: {
            'size':{
                default:"default",
            },
            'allow':{
                default:''
            }, 
            'disabled':{
                default:false
            },
            'type':{
                default:null
            }, 
            'faicon':{
                default:null
            }
        },
        data: function() {
            return {
                selftype:"",
                selffaicon:""
            }
        },
        computed:{
            otherText:function(){
                if(CONFIG.authority.button){
                    return "";
                }
                return "【按钮权限未启用】"
            },
            isShow: function() {
                if(!CONFIG.authority.button || ["allowsearch","allowreset"].indexOf(this.allow)>-1 ){
                    return true;
                }
                let obj = this.$store.state.auth.sysMenuMap[this.$route.name];
                if(obj){
                    return obj.allow.some((j) => {
                        return this.allow == j;
                    }); 
                }
                else{
                    return false;
                }
            }
        },
        methods: {
            emitclick: function() {
                this.$emit("click");
            },
            setSearchBtnStyle: function() {
                var tag = this.allow,textTag = null;
                if(this.$slots.default && this.$slots.default.length>0){
                    textTag = this.$slots.default[0].text;
                }
                var obj = defaultOption[tag];
                if(!obj){
                    obj = defaultOption[textTag];
                }
                if(obj){
                    this.selffaicon = obj.faicon;
                    this.selftype = obj.type;
                }
                //没有配置样式的情况下优化采用开发者配置，如果开发者未配置则采用默认配置
                else{
                    if(this.faicon){
                        this.selffaicon = this.faicon;
                    }
                    else{
                        this.selffaicon = "fa fa-file-text-o";
                    }

                    if(this.type){
                        this.selftype = this.type;
                    }
                    else{
                        this.selftype = "primary";
                    }
                }
            },
        },
        created: function() {
            this.setSearchBtnStyle();
        }
    }
</script>

<style scoped>
    .ivu-btn span i{
        margin-right:2px;
    }
</style>
