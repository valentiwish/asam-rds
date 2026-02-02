<template>
    <div class="login">
        <div class="login-title">
            <img :src="'./static/img/login/login_logo.png'" alt="">
        </div>
        <div class="login-content">
            <img :src="'./static/img/login/login_img.png'" class="login-content_leftimg">
            <div class="login-form">
                <div class="login-form-title">
                    <img :src="'./static/img/login/login_arrow.png'">
                    天慧云平台
                </div>
                <div class="input-item">
                    <Icon type="ios-person" />
                    <input v-model="userInfo.username" ref="username" @keyup.enter="loginAction()" placeholder="请输入用户名" />
                </div>
                <div class="input-item">
                    <Icon type="md-lock" />
                    <input v-model="userInfo.userpassword" ref="userpassword" @keyup.enter="loginAction()" type="password" placeholder="请输入密码"/>
                </div>
                <div class="input-item" v-if="checkcodeuuid" style="text-align:left;">
                    <input v-model.trim="userInfo.checkcode" ref="checkcode" @keyup.enter="loginAction()" placeholder="请输入右侧验证码" style="padding-right:100px;margin-top:0;"/>
                    <div v-html="yanzhengmasvg" class="yanzhengmasvg" @click="changecheckcode"></div>
                </div>
                
                <!-- <div style="padding:15px 0;">
                    <Checkbox v-model="userInfo.checked">记住密码</Checkbox>
                     <span style="float:right;">找回密码</span>
                </div> -->
                <div @click="loginAction()" class="login-btn">
                    <Icon type="ios-loading" class="ivu-load-loop" v-show="loading"/>
                    <span v-show="!loading">登 录</span>
                </div>
            </div>
            
        </div>
        <img src="/service-sso/auth/csrfToken" style="display:none;" />

        <div class="footer">
            &copy; 2020 陕西省“四联合一主体”工业物联网工程技术研究中心
            <br/>
            西安航天自动化股份有限公司 陕西省物联网与智能控制技术研究中心
        </div>
    </div>
</template>

<script>
import md5 from 'js-md5';
import config from '@/config/index.js'
import {ssoLoginAction,ssoCreatecode} from '@/api/auth';
export default {
    name:'login',
    components:{     
    },
    data:function(){
        return{
            rember:"",
            loading:false,
            loginName:"",
            password:"",
            loginType:"0",
            loginbgList:["login-bg1.svg","login-bg2.svg","login-bg3.svg"],
            loginbg:"",
            timer:null,
            userInfo: {
                username: '',
                userpassword: '',
                checkcode:'',
                checked: true
            },
            checkcodeuuid:null,
            yanzhengmasvg:null
        }
    },
    methods: {
        MD5(str){
            return md5(str).toUpperCase();
        },
        getCookie(name) {
            var arr,reg = new RegExp('(^| )' + name + '=([^;]*)(;|$)')
            if ((arr = document.cookie.match(reg))) return unescape(arr[2])
            else return null
        },
        getNonceStr:function(){
            return Math.random().toString(36).substr(2, 15);
        },
        creatSign(obj){
            var arr = Object.keys(obj);
            arr.sort();
            var arr1 = arr.map(function(j,i){
                return j + "="+obj[j];
            });
            return this.MD5(arr1.join("&"));
        },
        getPostData(){
            var obj = {
                "loginName": this.userInfo.username,
                "password": this.MD5(this.MD5(this.userInfo.userpassword))
            };
            if(this.checkcodeuuid){
                obj.checkcodeuuid = this.checkcodeuuid;
                obj.checkcode = this.userInfo.checkcode;
            }
            // obj.sign = this.creatSign(obj);
            // delete obj.password;
            return obj;
        },
        changecheckcode: function (url, data) {
            ssoCreatecode('/service-sso/auth/createcode')
            .then((res0) => {
                let res = res0.data;
                if (res.code == 200) {
                    this.checkcodeuuid = res.data.uuid;
                    this.yanzhengmasvg = res.data.img;
                } else {
                    this.$Message.error(res.msg)
                }
            },()=>{
                this.$Message.error("网络异常，请稍候再试！")
            })
        },
        loginAction(){
            if(this.loading){
                return false;
            }
            if (this.userInfo.username == '') {
                this.$Message.error('用户名不能为空');
                this.$refs.username.focus();
            } 
            else if (this.userInfo.userpassword == '') {
                this.$Message.error('密码不能为空');
                this.$refs.userpassword.focus();
            }
            else if(this.checkcodeuuid && !this.userInfo.checkcode){
                this.$Message.error('请输入验证码');
                this.$nextTick(()=>{
                    this.$refs.checkcode.focus();
                })
            } 
            else if (!this.userInfo.checked) {
                this.$Message.error('必须同意协议');
            }
            else {
                var data = this.getPostData();
                this.loading = true;
                const msg = this.$Message.loading({
                    content: '正在登录...',
                    duration: 0
                });
                ssoLoginAction(JSON.stringify(data))
                .then((res0) => {
                    this.loading = false;
                    msg && msg();
                    let res = res0.data;
                    if (res.code == 200) {
                        localStorage.setItem('access_token', res.data.token);
                        this.$router.push('/home')
                    } 
                    else {
                        if(res.data && res.data.checkcode){
                            this.checkcodeuuid = res.data.checkcode.uuid;
                            this.yanzhengmasvg = res.data.checkcode.img;
                            this.$nextTick(()=>{
                                this.$refs.checkcode.focus();
                            })
                            
                        }
                        this.$Message.error(res.msg);
                    }
                },(e)=>{
                    this.loading = false;
                    msg && msg();
                    this.$Message.error("网络异常，请稍候再试！")
                })
            }
        },
        randombg(){
            this.loginbg = this.loginbgList[this.$utils.randomNum(0,this.loginbgList.length-1)];
            this.timer = setTimeout(()=>{
                this.randombg();
            },15000)
        }

    },
    mounted() {
        this.randombg();
        if(config.ssoOptions && config.ssoOptions.enable){
            this.rejectLogin();
        }
    },
    beforeDestroy(){
        clearTimeout(this.timer)
    }
}
</script>

<style scoped>
    .login{
        position: absolute;
        top:0;
        left:0;
        bottom:0;
        right:0;
        color:#555;
        overflow: hidden;
        background: linear-gradient(to right,#1761b7, #43c4ff);
    }
    .login-title{
        position: absolute;
        top:30px;
        left:50px;
    }
    .footer{
        position:absolute;
        bottom:30px;
        left:0;
        right:0;
        text-align:center;
        line-height:25px;
        color:#fff;
    }
    .login-content{
        position:absolute;
        left:50%;
        top:50%;
        width:1100px;
        height:400px;
        margin-top:-230px;
        margin-left:-550px;
        background: linear-gradient(to right,rgba(255,255,255,0),rgba(255,255,255,0), rgba(255,255,255,0.3));
        user-select:none;
    }
    .login-content_leftimg{
        position:absolute;
        z-index:10;
        bottom:0;
        width:780px;
    }
    .login-form{
        position:absolute;
        top:0;
        right:80px;
        z-index:11;
        width:300px;
        font-size:16px;
        padding:60px 0;
    }
    .login-form-title{
        font-size:26px;
        color:#fff;
    }
    .login-form-title img{
        vertical-align: middle;
        margin-right:5px;
    }
    .login-form input{
        width:100%;
        height:44px;
        padding:2px 10px;
        background: rgba(255,255,255,0.3);
        color:#fff;
        border:none;
        border-radius:8px;
        font-size:16px;
        outline:none;
        text-align: center;
    }
    .login-form input::-webkit-input-placeholder {
        color: #efefef;
        font-size:16px;
        text-align: center;
        font-weight:bold;
    }
    .login-btn{
        margin-top:25px;
        height:44px;
        border-radius:50px;
        text-align: center;
        color:#fff;
        background: #F1E287;
        background: linear-gradient(to left,#4797f6, #58f1dd);
        border:1px solid #efefef;
        box-shadow: 0 2px 2px 1px #2F77A2;
        line-height: 44px;
        font-size:22px;
        cursor:pointer;
    }
    .login-btn:hover,
    .login-btn:active{
        background: #49b6ef;
    }
    .input-item {
        margin-top: 25px;
        position: relative;
        text-align: center;
        vertical-align: middle;
    }

    .input-item .ivu-icon {
        width: 22px;
        vertical-align: middle;
        position: absolute;
        left: 10px;
        top:10px;
        color:#fff;
        font-size:24px;
    }
    .yanzhengmasvg{
        position: absolute;
        display: inline-block;
        top:0;
        right:0;
        width:100px;
        vertical-align: middle;
    }
</style>
<style>
  .yanzhengmasvg svg{
    width: 100px;
    height: 40px;
    vertical-align: middle;
  }
</style>