var obj = {
    //业务系统标识
    appid:"rms",
    baseUrl:window.location.origin,
    service_rms:window.location.origin+'/service_rms',
    service_user:window.location.origin+'/service_user',
    fileServer:window.location.origin+'/service_file',
    socketServer: 'http://192.168.13.228:7003',
    pusher:{
        //消息推送服务地址（需要远程访问服务时，这里必须写服务端的地址，而非localhost）
        url:"http://192.168.13.228:7003/",
        projectPrefix:"AGV",
    },
    authority:{
        //路由校验白名单
        whiteRouterList:["*","/login","/home","/403","/404","/500","/000"],
        router:true,
        button:true
    }
}
export default obj;