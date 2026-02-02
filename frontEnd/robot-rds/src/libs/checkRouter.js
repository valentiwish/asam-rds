/* 
  页面权限校验
*/
import CONFIG from "@/config/index.js";
import store from "@/store/index.js";

export default function(route){
    var ret = null;
    if(CONFIG.authority.router && store.state.auth.checkReady){
        //路由校验白名单
        if(CONFIG.authority.whiteRouterList.indexOf(route.name) == -1 && !route.name.match("/_")){
          if(store.state.auth.sysMenuData.length == 0){
            ret = {
                path:"/000"
            }
          }
          else{
            //判断是否在权限列表内
            let obj = store.state.auth.sysMenuMap[route.name];
            if(!obj){
              obj = store.state.auth.sysMenuMap[route.path];
            }
            if(!obj){
                ret = {
                    path:"/403",
                    query:{from:route.fullPath}
                }
            }
          }  
        }
    }
    return ret;
}
