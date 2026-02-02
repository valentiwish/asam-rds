import config from "@/config/index";
import Bus from "@/components/bus.js";
var pushersocket = null,
puserkeys=[],
lastData={}
export default {
    name: 'mixins_pusher',
    data(){
        return {

        }
    },
    methods:{
        _initPusher(){
            if(!pushersocket && config.pusher && config.pusher.url){
                var socket = io(config.pusher.url);
                socket.on('connect',()=> {
                    var list = ["/businessData","/realTimeData"]
                    this.pusherSubscribe(list);
                    if(puserkeys.length>0){
                        this.pusherSubscribe(puserkeys);
                    }
                });
                socket.on('disconnect', function(e) {
                    console.log(e)
                });
                socket.on('reconnect', function(e) {
                    console.log("重连")
                });
                pushersocket = socket;
            } 
        },
        pusherSubscribeCallback(obj){
            // console.log(obj)
        },
        pusherSubscribe(keys){
            this._initPusher();
            if(typeof keys == "string"){
                keys = [keys];
            }
            var completeKeys = keys.map((key)=>{
                var hasInArr = true;
                //从数组中增加
                if(puserkeys.indexOf(key) ==-1){
                    puserkeys.push(key);
                    hasInArr =false;
                }
                //从服务器获取缓存数据
                if(!hasInArr){
                    // 接收实时推送数据
                    var completeKey = config.pusher.projectPrefix+key;
                    pushersocket.on(completeKey,(data)=> {
                        //去除项目标识符
                        var obj = {
                            key:key,
                            datatime:new Date().getTime(),
                            data:data
                        }
                        lastData[key] = obj;
                        if(typeof this.pusherSubscribeCallback == "function"){
                            this.pusherSubscribeCallback(obj); 
                        }
                        Bus.$emit("pusherSubscribeCallback",obj)
                    });
                }
                else if(lastData[key]){
                    this.pusherSubscribeCallback(lastData[key]);
                    Bus.$emit("pusherSubscribeCallback",lastData[key])
                }

                return completeKey;
            })
            if(pushersocket){
                pushersocket.emit('subscribe',completeKeys);
            }  
        },
        pusherUnsubscribe(keys){
            keys = keys.map((key)=>{
                return config.pusher.projectPrefix+key
            })
            
            if(pushersocket){
                pushersocket.emit('unsubscribe',keys);
            }
            //从数组中删除
            keys.forEach((key)=>{
                var indexOf = puserkeys.indexOf(key);
                if(indexOf >-1){
                    puserkeys.splice(indexOf,1);
                }
            })
        }
    },
    beforeDestroy(){
        // if(pushersocket){
        //     pushersocket.disconnect();
        //     pushersocket.destroy();
        // }
    },
    created(){
        setTimeout(()=>{
            this._initPusher();
        },500)
    }
    
}