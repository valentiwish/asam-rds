/*
 页面组件包含xx.vue
 name 组件路由名称
 path 组件路由地址
 package  组件包地址

 命名建议
 一级页面 name字段1个/ 二级页面2个 三级页面3个 以此类推 建议不要超过4层页面跳转。
 */

import frontsys from '@/views/include/index'
const errorPage = resolve => require(['@/views/include/errorpage.vue'], resolve);

const modulesFiles = require.context('./modules', true, /\.js$/)

var modules = modulesFiles.keys().reduce((modules, modulePath) => {
    const value = modulesFiles(modulePath);
    modules= modules.concat(value.default);
    return modules
},[]);

modules = modules.concat([
  {
    name: '/403',
    component: errorPage
  },{
    name: '/500',
    component: errorPage
  },{
    name: '/404',
    component: errorPage
  },{
    name: '/000',
    component: errorPage
  },
  // {
    // name: '*',
    // component: errorPage,
    // redirect: '/404',
  // }
])

var viewComponents = [
    {
      path: '/',
      name: '/',
      component: frontsys,
      children: modules,
      redirect: '/home',
    }
  ];

  export default viewComponents;
