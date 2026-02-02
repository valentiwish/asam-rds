<template>
    <div class="page-title">
        <Icon type="md-paper-plane"></Icon>当前位置：
        <span style="font-weight:bold;cursor:pointer;" 
            @click="$router.push('/home')" title="回到首页">
            首页
        </span>
        <span class="splitline">/</span>
        <span v-html="html"></span>
    </div>
</template>
<script>
import CONFIG from "@/config/index.js";

export default {
  name: "page-title",
  data: function() {
    return {
      
    }
  },
  computed:{
    netDebugHtml:function(){
        if(config.netDebug.html){
            return "本页面首次加载用时"+this.$route.meta.useTime +"s"+" 本次加载用时"+this.$route.meta.curuseTime +"s";
        }
        else{
            return "";
        }
    },
    sysMenuDataOriginal(){
        return this.$store.state.auth.sysMenuDataOriginal;
    },
    html(){
        var obj = this.$store.state.auth.sysMenuMap[this.$route.name];
        if(obj){
            var arr = this.$utils.getTreeInParents(this.sysMenuDataOriginal,obj.id);
            let html = arr.map(function(j) {
                return j.name;
            });
            return html.join('<span class="splitline">/</span>')
        }
        else if (this.$slots.default && this.$slots.default.length == 1) {
            return this.$slots.default[0].text;
        }
        else{
            return ""
        }
    }
  }
};
</script>

<style scoped>
    .page-title {
        height:48px;
        font-size: 15px;
        color: #6d6d6d;
        padding: 14px 15px;
        /* border-bottom:1px solid #F4F3EF; */
        background: #F4F3EF;
        background: linear-gradient(to right,#F4F3EF,rgba(255,255,255,0));
    }
    .page-title .splitline{
        margin:0 10px;
        color:#b9b8b4;
    }

    .page-title i.fa {
        font-size: 16px;
    }

    .page-title>i.ivu-icon {
        color: #2395e1;
        margin-right: 8px;
    }

    .page-title+.ivu-tabs {
        margin: 0 15px;
    }
    .page-title /deep/ .splitline{
        display: inline-block;
        margin:0 5px;
        color:#c5c5c5;
    }
</style>