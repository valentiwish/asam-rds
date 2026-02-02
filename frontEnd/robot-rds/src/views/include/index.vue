<template>
  <div style="height:100%;">
    <pageHeader v-show="$root.showLayout"/>
    <Layout :style="{transition: 'all ease 300ms',position:'absolute',top: $root.showLayout ? ($store.state.theme.minHeader ? '60px' : '86px') : '0',bottom:0,left:0,right:0}">
        <Sider style="background: #fff;" v-model="isCollapsed"  :collapsible="collapsible"  :collapsed-width="64" v-show="$root.showLayout && showPageMenu">
            <page-menu :collapsed="isCollapsed" :class="menuitemClasses" style="height:100%;"></page-menu>
        </Sider>
        <Layout style="background: #fff;height:100%;overflow-y: auto;">
            <div class="page-container">
                <keep-alive>
                    <router-view v-if="$route.meta.keepAlive" style="height:100%;"></router-view>>
                </keep-alive>
                <transition name="bounce" mode="out-in">
                    <router-view v-if="!$route.meta.keepAlive" :key="activeKey" style="height:100%;"></router-view>
                </transition>
            </div>
        </Layout>
    </Layout>
  </div>
</template>
<script>
const Vue = require("vue");
import checkRouter from "@/libs/checkRouter.js";

import pageHeader from "./header.vue";
import pageMenu from "./pagemenu/index.vue";
import pageTitle from "./pageTitle";
import allowBtn from "./allowBtn/index.vue";
import Bus from "@/components/bus.js";

//操作权限指令 比allowBtn更灵活 需要在权限模块获取成功后注入权限
import "./operationauthority.js"

//注册全局组件
Vue.component("page-title", pageTitle);
Vue.component("allowBtn", allowBtn);


export default {
  name: "adminindex",
  components: {
    pageHeader,
    pageMenu
  },
  data(){
    return {
      isCollapsed:true,
      collapsible:true, //是否支持侧边栏收起
    }
  },
  computed: {
    showPageMenu(){
      if(this.$route.meta.hidemenu){
        return false;
      }
      else if(this.sideMenuData && this.sideMenuData.children.length>0){
        return true;
      }
      else{
        return false;
      }
    },
      rotateIcon () {
          return [
              'menu-icon',
              this.isCollapsed ? 'rotate-icon' : ''
          ];
      },
      menuitemClasses: function () {
          return [
              'menu-item',
              this.isCollapsed ? 'collapsed-menu' : ''
          ]
      },
      sideMenuData(){
          var obj = this.$store.state.auth.sysMenuData.find((j)=>{
              return j.id === this.$store.state.auth.mainMenuId;
          });
          return obj;
      }
  },
  methods: {
    //页面路由权限拦截
    checkRouter(){
      var checkData = checkRouter(this.$route);
      if(checkData){
        this.$router.replace(checkData);
      }
      else if(this.$route.name == '/000'){
        this.$router.replace("/frontsys");
      }
    }
  },
  mounted: function () {
    //获取权限列表
    this.$store.dispatch("auth/getAuthMoudles",()=>{
      //校验页面权限
      this.checkRouter();
      //开始校验操作权限
      Bus.$emit("authCheckReady")
    })
    //获取用户信息
    this.$store.dispatch("auth/getUserInfo");
    //获取待办列表
    // this.$store.dispatch("user/getListMyTask");;
  },
  created: function () {
    
  },
};
</script>

<style>
@import "./index.css";

</style>
