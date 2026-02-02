const errorPage = resolve => require(['@/views/include/errorpage'], resolve);
export default [{
    "name": "/home",
    component: resolve => require(['@/views/home/index'], resolve),
    meta:{
      hidemenu:true
    }
  },{
    "name": "/dataease",
    "params":"linkId",
    component: resolve => require(['@/components/dataease.vue'], resolve),
    meta:{
      hidemenu:true
    }
  }];