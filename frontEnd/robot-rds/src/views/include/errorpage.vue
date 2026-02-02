<template>
    <div class="errorpage">
        <template v-if="$route.path=='/403'">
            <h1>403 Unauthorized</h1>
            <p>对不起 您无权访问该页面 如有疑问请联系系统管理员</p>
            <div class="info">
                <p style="font-size:12px;word-break: break-all;word-wrap: break-word;" v-if="$route.query.from">{{$route.query.from}}</p>
            </div>
        </template>
        <template v-else-if="$route.path=='/500'">
            <h1>500 Server Error</h1>
            <p>服务器出错 相关信息已经报告系统管理员</p>
            <div class="info">
                <p style="font-size:12px;word-break: break-all;word-wrap: break-word;" v-if="$route.query.from">{{$route.query.from}}</p>
            </div>
        </template>
        <template v-else-if="$route.path=='/000'">
            <h1>您无权访问当前系统</h1>
            <p>您无权访问当前系统，请退出！</p>
        </template>
        <template v-else>
            <h1>404 Not Found</h1>
            <p>页面失联了，攻城狮正在紧急搜索中...</p>
            <div class="info">
                可能原因:
                <br/>服务器过热导致网站可能出现故障
                <p style="font-size:12px;word-break: break-all;word-wrap: break-word;" v-if="$route.query.from">{{$route.query.from}}</p>
            </div>
        </template>
        <br/>
        <br/>
        <template v-if="$route.path!='/000'">
            <Button @click="$router.back()"><Icon type="md-arrow-back"></Icon> 回到上一页</Button>
            <Button @click="loginout"><Icon type="md-log-out"></Icon> 退出系统</Button>
        </template>
        <template v-else>
            <Button @click="loginout" type="success"><Icon type="md-log-out"></Icon> 退出系统</Button>
        </template>
    </div>
</template>

<script>
    export default {
        name: 'errorpage',
        data() {
            return {
                
            }
        },
        methods: {
            loginout:function(){
                this.$root.loginout();
            }
        },
        mounted: function () {
            
        },
        created: function () {

        }
    }
</script>

<style scoped>
    .errorpage {
        height:100%;
        text-align: center;
        color: #848484;
        background: #fff;
        z-index:1000;
    }
    .errorpage .pager-header {
        position: relative;
        height: 86px;
        background: url(/lib/images/header/logo_bg.png) no-repeat,
        url(/lib/images/header/top_bg.gif) repeat-x;
        user-select: none;
        text-align: left;
    }
    .errorpage .layout-logo {
        display: inline-block;
        width: 320px;
        height: 86px;
        line-height: 86px;
        color: #fff;
        background: url(/lib/images/header/logo.png) center no-repeat;
        font-size: 26px;
        color: #29639A;
        text-indent: -10000em;
    }
    .errorpage h1 {
        font-weight: 100;
        font-size: 60px;
        padding-top: 200px;
    }

    .errorpage p {
        font-size: 26px;
        margin: 10px 0;
    }

    .errorpage .info {
        width: 435px;
        margin: 0 auto;
        text-align: left;
        font-size: 18px;
        line-height: 1.6;
        color: #aaa;
    }
</style>


