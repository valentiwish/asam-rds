<template>
    <Menu ref="submenu" :collapse="collapsed" :theme="theme" :active-name="activeName" width="auto" :open-names="openNames" @on-select="showSubPage" accordion>
        <!-- 显示一级标题 -->
        <!-- <MenuGroup :title="sideMenuData.name" v-if="sideMenuData">
            <menu-list :list="sideMenuData.children"></menu-list>
        </MenuGroup> -->
        <!-- 不显示一级标题 -->
        <menu-list :list="sideMenuData.children" v-if="sideMenuData"></menu-list>
    </Menu>

</template>

<script>
import menuList from './menuList'
import Bus from '@/components/bus.js';

export default {
    name:'pagemenu',
    components: {menuList},
    props:["theme","collapsed"],
    data: function () {
        return {
            selectMenu: 0,
            showSysMenu: false,
            openNames: [],
            activeName: "",
        }
    },
    computed:{
        sysMenuData(){
            return this.$store.state.auth.sysMenuData;
        },
        sysMenuDataOriginal(){
            return this.$store.state.auth.sysMenuDataOriginal;
        },
        sideMenuData(){
            var obj = this.sysMenuData.find((j)=>{
                return j.id === this.$store.state.auth.mainMenuId;
            });
            return obj;
        }
    },
    methods: {
        showSubPage: function (url) {
            if(url){
                if(this.$utils.checkIsUrl(url)){
                    window.open(url)
                }
                else{
                    this.$router.push({
                        path:url
                    });
                }
            } 
        },
        setOpenNames: function () {
            var arr = this.sysMenuDataOriginal.filter((j) => {
                var flag = false;
                if (j.isOperation) {
                    if (j.url) {
                        flag = j.url.split(";").some((k, m) => {
                            return k == this.$route.name;
                        })
                    }
                } else {
                    flag = j.url == this.$route.name
                }
                return flag; //j.url == this.$route.name;
            });
            if (arr.length > 0) {
                var parents = this.$utils.getTreeParentsId(this.sysMenuDataOriginal, arr[0].id);
                this.openNames = parents; 
            } else {
                this.openNames = [];
            };
        },
        setActiveName: function () {
            //判断是否为二级页面
            var arr = this.sysMenuDataOriginal.filter((j) => {
                //return j.url == this.$route.name;
                var flag = false;
                if (j.isOperation) {
                    if (j.url) {
                        flag = j.url.split(";").some((k, m) => {
                            return k == this.$route.name;
                        })
                    }
                } else {
                    flag = j.url == this.$route.name
                }
                return flag; //j.url == this.$route.name;
            });
            this.activeName = arr.length > 0 ? ((arr[0].isOperation || !arr[0].isDisplay) ? this.getParentUrl(arr[0].pid) : arr[0].url) : this.$route.path;
        },
        getParentUrl: function (id) {
            var ret = ""
            this.sysMenuDataOriginal.some(function (j) {
                if (j.id == id) {
                    ret = j.url;
                    return true;
                }
            });
            return ret;
        },
        resetNames(){
            this.setOpenNames();
            this.setActiveName();
            this.$nextTick(() => {
                if (this.$refs['submenu']) {
                    this.$refs['submenu'].updateOpened();
                    this.$refs['submenu'].updateActiveName();
                }

                this.$nextTick(()=>{
                    Bus.$emit("menuLoaded");
                })
            });
        }
    },
    created: function () {

    },
    mounted: function () {
        this.resetNames();
    },
    watch: {
        "sideMenuData": {
            immediate: true,
            handler: function () {
                this.resetNames();    
            }
        },
        "$route"(){
            this.resetNames();
        }
    }
}
</script>

<style scoped>
 
</style>