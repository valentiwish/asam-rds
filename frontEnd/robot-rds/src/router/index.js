
const Vue = require('vue')
const Router = require('vue-router')
import viewComponents from './viewComponents';
import checkRouter from "@/libs/checkRouter.js";
Vue.use(Router);
var selfFn = function(obj){
    if(!obj.path){
        if (obj.params) {
            obj.path = obj.name + "/:" + obj.params.split(",").join("/:");
        } else {
            obj.path = obj.name;
        }
    }
    
    if(obj.children && obj.children.length>0){
        obj.children = obj.children.map((j,i)=>{
            return selfFn(j);
        })
    };
    if(!obj.component){
        obj.component = function(resolve) {
            var packageUrl = obj.package || "views"+obj.name+".vue";
            require(['@/'+ packageUrl], resolve)
        };
    }
    return obj;  
}
var routes = viewComponents.map(function(j,i){
    return selfFn(j);
});

var routerInstance = new Router({
	base: __dirname,
    mode: 'hash',
	routes: routes
});

routerInstance.beforeEach((to, from, next) => {
    if(routerInstance.app){
        routerInstance.app.$Loading.start();
    }
    //校验路由权限
    var checkData = checkRouter(to);
    if(checkData){
        next(checkData);
    }else{
        next();
    }
});

routerInstance.afterEach(route => {
    if(routerInstance.app){
        routerInstance.app.$Loading.finish()
    }
});


export default routerInstance;
