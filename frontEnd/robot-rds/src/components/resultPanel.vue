<template>
    <div class="result-panel" v-if="value" ref="panel" :style="getStyle">
        <div class="result-panel_header" @dblclick="toggleMin">
            <span class="result-panel_title">{{title}}</span>
            <div class="close-tool" v-if="closeable"  >
                <span @click.stop="toggleMin">
                    <Icon type="md-remove" title="最小化" v-show="!isMin"/>
                    <Icon type="md-qr-scanner" title="最大化" v-show="isMin"/>
                </span>
                <span @click.stop="hide">
                    <Icon type="ios-close-circle-outline" title="关闭"/>
                </span>
                
            </div>
        </div>
        <div class="result-panel_content" v-show="!isMin">
            <slot></slot>
        </div>
    </div>
</template>

<script>
    export default {
        name:"result-panel",
        props:{
            title:{
                type: String,      
                default: ""
            },
            value:{
                type:Boolean,
                default:true
            },
            closeable:{
                type:Boolean,
                default:true
            },
            height:{
                type: [Number,String],      
                default:400
            }
        },
        data:function(){
            return {
               isMin:false,
               minHeight:40
            }
        },
        computed:{
            getStyle:function(){
                if(this.isMin){
                    return {
                        "height":this.minHeight+"px",
                        "display":"inline"
                    }
                }
                return {
                    "height":this.height+"px",
                };
            }
        },
        methods:{
            hide:function(){
                this.$emit("input",false);   
            },
            show:function(){
                this.$emit("input",true)   
            },
            toggleMapPos:function(flag){
                this.$root.getMap((instance)=>{
                    if(flag){
                        instance.bottomPos = 0;
                    }
                    else{
                        this.$nextTick(()=>{
                            if(this.$refs.panel){
                                var height = this.height;
                                if(this.isMin){
                                    height = this.minHeight;
                                }
                                this.$root.getMap((instance)=>{
                                    instance.bottomPos = height+"px";
                                })
                                
                            }
                        })
                    }
                })   
            },
            toggleMin:function(){
                this.isMin = !this.isMin;
                this.toggleMapPos(false);
            }
        },
        beforeDestroy: function () {
            this.toggleMapPos(true);
            this.$root.getMap(instance => {
                instance.unhighlight();
            })
        },
        watch:{
            value:{
                immediate:true,
                handler:function(n){
                    if(n){
                        this.isMin = false;
                    }
                    this.toggleMapPos(!n);
                    if(!n){
                        this.$root.getMap(instance => {
                            instance.unhighlight();
                        })
                    }
                }
            }
        }
    }
</script>

<style scoped>
    .result-panel{
        position: fixed;
        left:1px;
        right:1px;
        bottom:-1px;
        height:400px;
        background:#fff;
        box-shadow: 0 4px 12px rgba(0,0,0,.15);
        border-radius: 6px;
        z-index:10000;
    }
    .result-panel_header{
        font-size:18px;
        height:40px;
        line-height: 40px;
        background:#797979;
        color:#fff;
        padding:0 20px;
        font-weight:normal;
        user-select:none;

    }
    .result-panel_header .close-tool{
        position: absolute;
        top:0px;
        right:4px;
        padding:0 10px;
        cursor: pointer;
    }
    .result-panel_header .close-tool span{
        margin-left:10px;
    }
    .result-panel_content{
        position:absolute;
        top:40px;
        left:0;
        right:0;
        bottom:0;
        overflow:auto;
        padding:5px 15px;
    }
</style>