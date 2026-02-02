import { ssoLoginAction,getUserInfo,getAuthMoudles,getOperationList} from '@/api/auth'
import CONFIG from '@/config/index'

const processSysMenuData = function (commit , sysMenuDataOriginal) {
  commit("setSysMenuDataOriginal", sysMenuDataOriginal);
  var sysMenuMap = {};
  var list = sysMenuDataOriginal.filter(function (j, i) {
    //构建模块KV对象
    const obj = JSON.parse(JSON.stringify(j));
    if (obj.isOperation) {
      if (obj.url) {
        obj.url.split(";").map((k, m) => {
          sysMenuMap[k] = obj;
        });
      }
    } else if (obj.url && obj.url != "#") {
      sysMenuMap[obj.url] = obj;
    }
    return j.isOperation != 1;
  });


  let sortArr = function (arr) {
    arr.sort(function (a, b) {
      return a.sort - b.sort;
    });
  };
  //遍历子节点
  let getChildren = function (id,deep) {
    var child = [];
    list.map(function (i, j) {
      if (i.pid == id && i.isDisplay!==0) {
        let children = getChildren(i.id,deep+1);
        sortArr(children);
        if (children.length > 0) {
          i["children"] = children;
        } else {
          i["disabled"] = true;
        }
        i['deep'] = deep;
        child.push(i);
      }
    });
    sortArr(child);
    return child;
  };
  //根据id获取节点对象
  let getObjById = function (id) {
    var ret = [];
    list.some(function (i, j) {
      if (i.id == id) {
        ret.push(i);
        return true;
      } else {
        return false;
      }
    });
    return ret;
  };

  //获取根节点
  var sysMenuData = [];
  list.map(function (i, j) {
    //根节点
    if (i.isDisplay!==0 && (i.pid == undefined || (i.pid && getObjById(i.pid).length == 0))) {
      i.deep=0;
      i["children"] = getChildren(i.id,1);
      sysMenuData.push(i);
    }
  });
  sortArr(sysMenuData);
  commit("setSysMenuData", sysMenuData);
  commit("setSysMenuMap", sysMenuMap);
};

const state = {
  checkReady:false,
  userInfo:{
    userName: '<i class="ivu-icon ivu-icon-load-c animate_rotate"></i> ...',
    roleIds: "",
    userId: "",
    loginName: "",
    phone: "",
    sex: ""
  },
  sysMenuMap:{},
  sysMenuData:[], 
  sysMenuDataOriginal:[],
  operationList:[],
  mainMenuId:"",
}

const mutations = {
  setCheckReady(state, n) {
    return state.checkReady = n;
  },
  setUserInfo(state, n) {
    return state.userInfo = n;
  },
  setSysMenuMap(state, n){
    return state.sysMenuMap = n;
  },
  setSysMenuData(state, n){
      return state.sysMenuData = n;
  },
  setSysMenuDataOriginal(state, n){
      return state.sysMenuDataOriginal = n;
  },
  setOperationList(state, n){
      return state.operationList = n;
  },
  setMainMenuId(state, n){
    return state.mainMenuId = n;
  },
}

const actions = {
  getUserInfo({ commit }){
    var userInfo = {};
    getUserInfo().then(
      (res) => {
        if (res.data.code === 200) {
          userInfo = res.data.data;
        } else {
          userInfo.userName = "未知用户";
        }
        commit("setUserInfo", userInfo);
      },
      () => {
        userInfo.userName = "未知用户";
        commit("setUserInfo", userInfo);
      }
    );
  },
  // user login
  login({ commit }, userInfo) {
    const { username, password } = userInfo
    return new Promise((resolve, reject) => {
      ssoLoginAction({ username: username.trim(), password: password }).then(response => {
        const { data } = response
        commit('SET_TOKEN', data.token)
        setToken(data.token)
        resolve()
      }).catch(error => {
        reject(error)
      })
    })
  },

  getAuthMoudles({ commit },cb) {
    return new Promise((resolve, reject) => {
      getAuthMoudles({appid:CONFIG.appid})
      .then(response => {
        if(response.data.code ==200){
          if(response.data.data.length>0){
            processSysMenuData(commit,response.data.data);
          }
        }
        commit("setCheckReady", true);
        resolve(response.data.data);
        cb();
      },(error)=>{
        reject(error);
        cb();
      })
    })
  },
  getOperationList({ commit, state }){
    return new Promise((resolve, reject) => {
      getOperationList().then(
        (res) => {
          let list = [];
          if (res.data.code == 200) {
            list = res.data.data;
            commit("setOperationList", list);
            resolve(list)
          } else {
            commit("setOperationList", list);
            reject([])
          }
        },
        () => {
          commit("setOperationList",[]);
          reject([])
        }
      );
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
