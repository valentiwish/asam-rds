const Vue = require('vue')
const iView = require('iView')


import router from './router'
import ajax from '@/libs/ajax.js'
import utils from '@/libs/util.js'
import App from './App'
import store from './store'
import config from '@/config/index.js'

import {Button,Dialog} from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css';
import mixinspusher from "@/mixins/pusher.js";
Vue.mixin(mixinspusher);

Vue.prototype.$ajax = ajax;
Vue.prototype.$utils = utils;
Vue.prototype.$config = config;

Vue.config.productionTip = false;
Vue.use(iView);

Vue.use(Button);
Vue.use(Dialog);


// 实际打包时应该不引入mock
/* eslint-disable */
// if (process.env.BUILD_ENV == 'dev') require('@/mock')

var vm = new Vue({
    el: '#app',
    router,
    store,
    components: {
        App
    },
    data(){
        return {
            //是否显示左侧和顶部导航，根据是否嵌入iframe计算。
            showLayout:window.self == window.top,
        }
    },
    template: '<App/>',
    methods:{
        GetQueryString:function(name){
            var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
			var r = window.location.search.substr(1).match(reg);
			if (r != null) return unescape(r[2]);
			return null;
        },
        rejectLogin:function(mode){
            localStorage.removeItem('access_token');
            //嵌入模式下运行
            if(!this.showLayout){
                top.location.replace(_sysBaseConf.casclient.url +(mode ? '?mode='+mode : ''));
            }
            //单体模式下运行
            else{
                if(_sysBaseConf.casclient && _sysBaseConf.casclient.url){
                    var url = window.location.origin + window.location.pathname;
                    if(window.location.hash != "#/login"){
                        url = url+ window.location.hash;
                    }
                    if(mode){
                        window.location.replace(_sysBaseConf.casclient.url +(mode ? '?mode='+mode : ''));
                    }
                    else{
                        window.location.replace(_sysBaseConf.casclient.url + "?redirecturl=" + encodeURIComponent(url) + "&appid="+this.$config.appid+"&state="+new Date().getTime()+(mode ? '&mode='+mode : ''));
                    }
                }
                else{
                    this.$router.push("/login");
                }
            }

        },
        loginout:function(){
            var that=this;
            this.$Modal.confirm({
                title: '系统退出确认',
                content: '您确认退出系统吗？',
                loading:true,
                onOk() {
                    let url = "/service_user/login/logout";
                    this.$ajax.post(url, {
                        token:localStorage.getItem('access_token')
                    })
                    .then((res)=>{
                        that.$Modal.remove();
                        localStorage.removeItem("access_token");
                        that.$nextTick(()=>{
                            that.rejectLogin("loginout");
                        })
                    },()=>{
                        that.$Modal.remove();
                        this.$Message.warning('网络异常，退出失败');
                    })
                }
            })
        },
    },
    mounted:function(){

    },
    created(){
        document.title = document.title + " "+_sysBaseConf.name;
        var token = this.GetQueryString("token");
		if (token) {
			localStorage.setItem("access_token", token);
            window.location.href = window.location.origin + window.location.pathname +window.location.hash;
		}
		else{
			if (!localStorage.getItem("access_token")) {
				this.rejectLogin();
			}
		}
    }
});

window._asam_vmInstance = vm;
export default vm;
Date.prototype.Format = function (fmt) {
    var o = {
        "M+": this.getMonth() + 1, //月份
        "d+": this.getDate(), //日
        "h+": this.getHours(), //小时
        "m+": this.getMinutes(), //分
        "s+": this.getSeconds(), //秒
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度
        "S": this.getMilliseconds() //毫秒
    };
    if(/(y+)/.test(fmt))
        fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));
    for(var k in o)
        if(new RegExp("("+ k +")").test(fmt))
    fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));
    return fmt;
};
