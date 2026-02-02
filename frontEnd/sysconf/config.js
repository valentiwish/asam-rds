/* 
   @desc 平台个系统统一配置文件
   @author zzk
   @datetime 2022.02.17
*/
(function(){
    var sysBaseUrl = window.location.origin;
   if(["127.0.0.1","localhost"].indexOf(window.location.hostname) > -1){
        sysBaseUrl = "http://192.168.13.228:9005";
        //sysBaseUrl = "http://8.142.35.135:9001";
    }
    if(sysBaseUrl.match("192.168.")){
        sysBaseUrl = "http://192.168.13.228:9005";
        //sysBaseUrl = "http://8.142.35.135:9001";
    }

    window._sysBaseConf = {
        // 平台名称
       // name:"克拉玛依智慧绿化供水",
        name:"西安航天智能制造",
        // 统一登录配置
        casclient:{
            //统一登录服务地址
			url:sysBaseUrl + "/casclient/",
            // 轮播
            banner:[{
                name:"3",
                url:"login_img3.jpg"
            },{
                name:"1",
                url:"login_img1.jpg"
            },{
                name:"2",
                url:"login_img2.jpg"
            }],
            // app扫码登录地址
            appInfo:{
                name:"智慧水务APP",
                url:""
            },
        },
        // 登录后默认跳转页面地址 导航页
        //navUrl:"http://192.168.13.245:9000/sysnav/",
        navUrl:sysBaseUrl + "/sysnav/",
        // 版权部分配置 主要应用于统一登录
        copyright:{
            name:"西安航天智能制造",
            namelink:"http://asam.dljs.casic.cn/",
            address:"中国·航天科工集团",
            phonehref:["02988782542","02988782541"],
            // 技术支持
            techSupport:{
                name:"西安航天自动化股份有限公司",
                url:"http://asam.dljs.casic.cn/"
            },
            /* beian:[{
                name:"京公网安备11040102100081号",
                url:"https://beian.miit.gov.cn/#/Integrated/index",
            },{
                name:"京公网安备11040102100081号",
                url:"https://beian.miit.gov.cn/#/Integrated/index",
            }], */
        },
        //dataease根地址
        //sysDataEaseBaseUrl:sysBaseUrl + "/dataease-api",
		sysDataEaseBaseUrl:"http://192.168.13.12",
        //积木报表根地址
        sysJmreportBaseUrl:sysBaseUrl + "/jmreport"
    };
}())

