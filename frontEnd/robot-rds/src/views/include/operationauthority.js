import Vue from 'vue';
import store from "@/store/index.js";
import CONFIG from "@/config/index.js";
import Bus from "@/components/bus.js";

var removeChild = function(el, binding, vnode){
    if(store.state.auth.checkReady){
        let allow = binding.value;
        let obj = store.state.auth.sysMenuMap[_asam_vmInstance.$route.name];
        if(obj){
            var flag = obj.allow.some((j) => {
                return allow == j;
            });
            // 如果没有权限就移除此节点
            if(!flag && el.parentNode){
                el.parentNode.removeChild(el);
            }
            else{
                $(el).removeClass("operationauthority-hide");
            }
        }
        else{
            if(el.parentNode){
                el.parentNode.removeChild(el);
            }
        }
    }
}
var check = function(el, binding, vnode){
    // 从自定义指令中获取当前指令允许的权限
    let allow = binding.value;
    if(CONFIG.authority.button && ["allowsearch","allowreset"].indexOf(allow) ==-1){
        if(store.state.auth.checkReady){
            removeChild(el, binding, vnode);
        }
        //等待权限初始化完成后再决定师傅删除
        else{
            $(el).addClass("operationauthority-hide");
            Bus.$once("authCheckReady",()=>{
                removeChild(el, binding, vnode);
            })
        }
    }
}

Vue.directive('allow', {
    inserted: (el, binding, vnode) => {
       check(el, binding, vnode);
    }
})
