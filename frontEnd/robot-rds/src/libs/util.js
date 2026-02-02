import Cookies from 'js-cookie'
// cookie保存的天数
import config from '@/config'
import { forEach, hasOneOf, objEqual } from '@/libs/tools'
const { title, cookieExpires, useI18n } = config

export const TOKEN_KEY = 'token'

export const setToken = (token) => {
  Cookies.set(TOKEN_KEY, token, { expires: cookieExpires || 1 })
}

export const getToken = () => {
  const token = Cookies.get(TOKEN_KEY)
  if (token) return token
  else return false
}

export const hasChild = (item) => {
  return item.children && item.children.length !== 0
}

const showThisMenuEle = (item, access) => {
  if (item.meta && item.meta.access && item.meta.access.length) {
    if (hasOneOf(item.meta.access, access)) return true
    else return false
  } else return true
}
/**
 * @param {Array} list 通过路由列表得到菜单列表
 * @returns {Array}
 */
export const getMenuByRouter = (list, access) => {
  let res = []
  forEach(list, item => {
    if (!item.meta || (item.meta && !item.meta.hideInMenu)) {
      let obj = {
        icon: (item.meta && item.meta.icon) || '',
        name: item.name,
        meta: item.meta
      }
      if ((hasChild(item) || (item.meta && item.meta.showAlways)) && showThisMenuEle(item, access)) {
        obj.children = getMenuByRouter(item.children, access)
      }
      if (item.meta && item.meta.href) obj.href = item.meta.href
      if (showThisMenuEle(item, access)) res.push(obj)
    }
  })
  return res
}

/**
 * @param {Array} routeMetched 当前路由metched
 * @returns {Array}
 */
export const getBreadCrumbList = (route, homeRoute) => {
  let homeItem = { ...homeRoute, icon: homeRoute.meta.icon }
  let routeMetched = route.matched
  if (routeMetched.some(item => item.name === homeRoute.name)) return [homeItem]
  let res = routeMetched.filter(item => {
    return item.meta === undefined || !item.meta.hideInBread
  }).map(item => {
    let meta = { ...item.meta }
    if (meta.title && typeof meta.title === 'function') {
      meta.__titleIsFunction__ = true
      meta.title = meta.title(route)
    }
    let obj = {
      icon: (item.meta && item.meta.icon) || '',
      name: item.name,
      meta: meta
    }
    return obj
  })
  res = res.filter(item => {
    return !item.meta.hideInMenu
  })
  return [{ ...homeItem, to: homeRoute.path }, ...res]
}

export const getRouteTitleHandled = (route) => {
  let router = { ...route }
  let meta = { ...route.meta }
  let title = ''
  if (meta.title) {
    if (typeof meta.title === 'function') {
      meta.__titleIsFunction__ = true
      title = meta.title(router)
    } else title = meta.title
  }
  meta.title = title
  router.meta = meta
  return router
}

export const showTitle = (item, vm) => {
  let { title, __titleIsFunction__ } = item.meta
  if (!title) return
  if (useI18n) {
    if (title.includes('{{') && title.includes('}}') && useI18n) title = title.replace(/({{[\s\S]+?}})/, (m, str) => str.replace(/{{([\s\S]*)}}/, (m, _) => vm.$t(_.trim())))
    else if (__titleIsFunction__) title = item.meta.title
    else title = vm.$t(item.name)
  } else title = (item.meta && item.meta.title) || item.name
  return title
}

/**
 * @description 本地存储和获取标签导航列表
 */
export const setTagNavListInLocalstorage = list => {
  localStorage.tagNaveList = JSON.stringify(list)
}
/**
 * @returns {Array} 其中的每个元素只包含路由原信息中的name, path, meta三项
 */
export const getTagNavListFromLocalstorage = () => {
  const list = localStorage.tagNaveList
  return list ? JSON.parse(list) : []
}

/**
 * @param {Array} routers 路由列表数组
 * @description 用于找到路由列表中name为home的对象
 */
export const getHomeRoute = (routers, homeName = 'home') => {
  let i = -1
  let len = routers.length
  let homeRoute = {}
  while (++i < len) {
    let item = routers[i]
    if (item.children && item.children.length) {
      let res = getHomeRoute(item.children, homeName)
      if (res.name) return res
    } else {
      if (item.name === homeName) homeRoute = item
    }
  }
  return homeRoute
}

/**
 * @param {*} list 现有标签导航列表
 * @param {*} newRoute 新添加的路由原信息对象
 * @description 如果该newRoute已经存在则不再添加
 */
export const getNewTagList = (list, newRoute) => {
  const { name, path, meta } = newRoute
  let newList = [...list]
  if (newList.findIndex(item => item.name === name) >= 0) return newList
  else newList.push({ name, path, meta })
  return newList
}

/**
 * @param {*} access 用户权限数组，如 ['super_admin', 'admin']
 * @param {*} route 路由列表
 */
const hasAccess = (access, route) => {
  if (route.meta && route.meta.access) return hasOneOf(access, route.meta.access)
  else return true
}

/**
 * 权鉴
 * @param {*} name 即将跳转的路由name
 * @param {*} access 用户权限数组
 * @param {*} routes 路由列表
 * @description 用户是否可跳转到该页
 */
export const canTurnTo = (name, access, routes) => {
  const routePermissionJudge = (list) => {
    return list.some(item => {
      if (item.children && item.children.length) {
        return routePermissionJudge(item.children)
      } else if (item.name === name) {
        return hasAccess(access, item)
      }
    })
  }

  return routePermissionJudge(routes)
}

/**
 * @param {String} url
 * @description 从URL中解析参数
 */
export const getParams = url => {
  const keyValueArr = url.split('?')[1].split('&')
  let paramObj = {}
  keyValueArr.forEach(item => {
    const keyValue = item.split('=')
    paramObj[keyValue[0]] = keyValue[1]
  })
  return paramObj
}

/**
 * @param {Array} list 标签列表
 * @param {String} name 当前关闭的标签的name
 */
export const getNextRoute = (list, route) => {
  let res = {}
  if (list.length === 2) {
    res = getHomeRoute(list)
  } else {
    const index = list.findIndex(item => routeEqual(item, route))
    if (index === list.length - 1) res = list[list.length - 2]
    else res = list[index + 1]
  }
  return res
}

/**
 * @param {Number} times 回调函数需要执行的次数
 * @param {Function} callback 回调函数
 */
export const doCustomTimes = (times, callback) => {
  let i = -1
  while (++i < times) {
    callback(i)
  }
}

/**
 * @param {Object} file 从上传组件得到的文件对象
 * @returns {Promise} resolve参数是解析后的二维数组
 * @description 从Csv文件中解析出表格，解析成二维数组
 */
export const getArrayFromFile = (file) => {
  let nameSplit = file.name.split('.')
  let format = nameSplit[nameSplit.length - 1]
  return new Promise((resolve, reject) => {
    let reader = new FileReader()
    reader.readAsText(file) // 以文本格式读取
    let arr = []
    reader.onload = function (evt) {
      let data = evt.target.result // 读到的数据
      let pasteData = data.trim()
      arr = pasteData.split((/[\n\u0085\u2028\u2029]|\r\n?/g)).map(row => {
        return row.split('\t')
      }).map(item => {
        return item[0].split(',')
      })
      if (format === 'csv') resolve(arr)
      else reject(new Error('[Format Error]:你上传的不是Csv文件'))
    }
  })
}

/**
 * @param {Array} array 表格数据二维数组
 * @returns {Object} { columns, tableData }
 * @description 从二维数组中获取表头和表格数据，将第一行作为表头，用于在iView的表格中展示数据
 */
export const getTableDataFromArray = (array) => {
  let columns = []
  let tableData = []
  if (array.length > 1) {
    let titles = array.shift()
    columns = titles.map(item => {
      return {
        title: item,
        key: item
      }
    })
    tableData = array.map(item => {
      let res = {}
      item.forEach((col, i) => {
        res[titles[i]] = col
      })
      return res
    })
  }
  return {
    columns,
    tableData
  }
}

export const findNodeUpper = (ele, tag) => {
  if (ele.parentNode) {
    if (ele.parentNode.tagName === tag.toUpperCase()) {
      return ele.parentNode
    } else {
      return findNodeUpper(ele.parentNode, tag)
    }
  }
}

export const findNodeUpperByClasses = (ele, classes) => {
  let parentNode = ele.parentNode
  if (parentNode) {
    let classList = parentNode.classList
    if (classList && classes.every(className => classList.contains(className))) {
      return parentNode
    } else {
      return findNodeUpperByClasses(parentNode, classes)
    }
  }
}

export const findNodeDownward = (ele, tag) => {
  const tagName = tag.toUpperCase()
  if (ele.childNodes.length) {
    let i = -1
    let len = ele.childNodes.length
    while (++i < len) {
      let child = ele.childNodes[i]
      if (child.tagName === tagName) return child
      else return findNodeDownward(child, tag)
    }
  }
}

export const showByAccess = (access, canViewAccess) => {
  return hasOneOf(canViewAccess, access)
}

/**
 * @description 根据name/params/query判断两个路由对象是否相等
 * @param {*} route1 路由对象
 * @param {*} route2 路由对象
 */
export const routeEqual = (route1, route2) => {
  const params1 = route1.params || {}
  const params2 = route2.params || {}
  const query1 = route1.query || {}
  const query2 = route2.query || {}
  return (route1.name === route2.name) && objEqual(params1, params2) && objEqual(query1, query2)
}

/**
 * 判断打开的标签列表里是否已存在这个新添加的路由对象
 */
export const routeHasExist = (tagNavList, routeItem) => {
  let len = tagNavList.length
  let res = false
  doCustomTimes(len, (index) => {
    if (routeEqual(tagNavList[index], routeItem)) res = true
  })
  return res
}

export const localSave = (key, value) => {
  localStorage.setItem(key, value)
}

export const localRead = (key) => {
  return localStorage.getItem(key) || ''
}

// scrollTop animation
export const scrollTop = (el, from = 0, to, duration = 500, endCallback) => {
  if (!window.requestAnimationFrame) {
    window.requestAnimationFrame = (
      window.webkitRequestAnimationFrame ||
      window.mozRequestAnimationFrame ||
      window.msRequestAnimationFrame ||
      function (callback) {
        return window.setTimeout(callback, 1000 / 60)
      }
    )
  }
  const difference = Math.abs(from - to)
  const step = Math.ceil(difference / duration * 50)

  const scroll = (start, end, step) => {
    if (start === end) {
      endCallback && endCallback()
      return
    }

    let d = (start + step > end) ? end : start + step
    if (start > end) {
      d = (start - step < end) ? end : start - step
    }

    if (el === window) {
      window.scrollTo(d, d)
    } else {
      el.scrollTop = d
    }
    window.requestAnimationFrame(() => scroll(d, end, step))
  }
  scroll(from, to, step)
}

/**
 * @description 根据当前跳转的路由设置显示在浏览器标签的title
 * @param {Object} routeItem 路由对象
 * @param {Object} vm Vue实例
 */
export const setTitle = (routeItem, vm) => {
  const handledRoute = getRouteTitleHandled(routeItem)
  const pageTitle = showTitle(handledRoute, vm)
  const resTitle = pageTitle ? `${title} - ${pageTitle}` : title
  window.document.title = resTitle
}



export default {
  uuid: function (len, radix) {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
      var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
      return v.toString(16);
    });
  },
  randomNum: function (minNum, maxNum) {
    switch (arguments.length) {
      case 1:
        return parseInt(Math.random() * minNum + 1, 10);
        break;
      case 2:
        return parseInt(Math.random() * (maxNum - minNum + 1) + minNum, 10);
        //或者 Math.floor(Math.random()*( maxNum - minNum + 1 ) + minNum );
        break;
      default:
        return 0;
        break;
    }
  },
  checkIsUrl(value){
    return new RegExp("^(http|https)://","i").test(value);
  },
  getFileShowUrl: function (fileId) {
    return "/service_file/show?fileId=" + fileId;
  },
  getFileDownloadUrl: function (fileId) {
    return "/service_file/download?fileId=" + fileId;
  },
  formatDate(datestr, fmt) {
    if (typeof datestr == "string" && datestr.replace(/(^\s*)|(\s*$)/g, "") != "") {
      datestr = datestr.replace(/\-/g, '/');
      var dateobj = new Date(datestr);
      if (fmt) {
        return dateobj.Format(fmt);
      }
      return dateobj;
    }
    else {
      if (fmt) {
        return null;
      }
      else {
        return "";
      }
    }
  },
  //根据文件路径下载文件
  openUrl: function (url, blank) {
    var el = document.createElement("a");
    document.body.appendChild(el);
    el.href = url;
    if (blank) {
      el.target = '_new';
    }
    el.click();
    document.body.removeChild(el);
  },
  getRandomColor: function () {
    var r = Math.round(Math.random() * 255), g = Math.round(Math.random() * 255), b = Math.round(Math.random() * 255);
    var color = r << 16 | g << 8 | b;
    return "#" + color.toString(16);
  },
  hex2rgb: function (color) {
    var reg = /^#([0-9a-fA-f]{3}|[0-9a-fA-f]{6})$/;
    var sColor = color.toLowerCase();
    if (sColor && reg.test(sColor)) {
      if (sColor.length === 4) {
        var sColorNew = "#";
        for (var i = 1; i < 4; i += 1) {
          sColorNew += sColor.slice(i, i + 1).concat(sColor.slice(i, i + 1));
        }
        sColor = sColorNew;
      }
      //处理六位的颜色值  
      var sColorChange = [];
      for (var i = 1; i < 7; i += 2) {
        sColorChange.push(parseInt("0x" + sColor.slice(i, i + 2)));
      }
      return sColorChange;
    }
    else {
      return sColor;
    }
  },
  changePicColor: function (img, color) {
    var rgbcolor = this.hex2rgb(color);
    var canvas = document.createElement("canvas");
    canvas.width = img.width;
    canvas.height = img.height;
    var ctx = canvas.getContext("2d");
    ctx.drawImage(img, 0, 0);
    var c = ctx.getImageData(0, 0, img.width, img.height);
    //chrome浏览器报错，ie浏览器报安全错误信息，原因往下看
    for (var i = 0; i < c.height; ++i) {
      for (var j = 0; j < c.width; ++j) {
        var x = i * 4 * c.width + 4 * j;  //imagedata读取的像素数据存储在data属性里，是从上到下，从左到右的，每个像素需要占用4位数据，分别是r,g,b,alpha透明通道
        c.data[x] = rgbcolor[0];
        c.data[x + 1] = rgbcolor[1];
        c.data[x + 2] = rgbcolor[2];
      }
    }
    //ctx.putImageData(c, 40, 40);
    ctx.putImageData(c, 0, 0, 0, 0, c.width, c.height);    //裁剪效果见图1
    return canvas.toDataURL();          //返回canvas图片数据url
  },
  formatTreeData: function (data, renderChild) {
    let list = JSON.parse(JSON.stringify(data)),
      ret = [];
    /*将扁平数据转换为树形数据*/
    //遍历子节点
    let getChildren = function (id) {
      var child = [];
      // 获取根节点
      list.map(function (i) {
        if (i.pid == id) {
          i.title = i.name;
          i.expand = i.open;
          let children = getChildren(i.id);

          if (children.length > 0) {
            i['children'] = children;
            i.checked = i.children.every(function (w) {
              return w.checked;
            });
          } else if (typeof renderChild == "function") {
            i['render'] = renderChild;
          } else if (renderChild == "disabledbranch") {
            i['disabled'] = true;
          }

          if (renderChild == "hidenocheckbranch") {
            if (i.checked == true || i.children && i.children.length > 0) {
              child.push(i);
            }
          } else {
            child.push(i);
          }
        }
      })
      return child;
    };
    //根据id获取节点对象
    let getObjById = function (id) {
      let ret = [];
      list.some(function (j) {
        if (j.id == id) {
          ret.push(j);
          return true;
        } else {
          return false;
        }
      });
      return ret;
    }
    // 获取根节点
    list.map(function (i) {
      if (i.pid == "" || i.pid == undefined || i.pid && getObjById(i.pid).length == 0) {
        i.title = i.name;
        i.expand = i.open;
        i['children'] = getChildren(i.id);
        ret.push(i);
      }
    });
    return ret;
  },
  getTreeParents: function (data, inid) {
    let list = JSON.parse(JSON.stringify(data)),
      ret = [];
    //根据id获取节点对象
    let getObjById = function (id) {
      var r = null;
      list.some(function (i) {
        if (i.id == id) {
          r = i;
          return true;
        } else {
          return false;
        }
      })
      return r;
    };
    //获取父级
    let getParentNode = function (p) {
      let obj = getObjById(p);
      if (obj) {
        ret.push(obj);
        getParentNode(obj.pid);
      }
    };
    if (getObjById(inid)) {
      getParentNode(getObjById(inid).pid);
      return ret.reverse();
    } else {
      return ret;
    }
  },
  getTreeParentsId: function (data, id) {
    return this.getTreeParents(data, id).map(function (j) {
      return j.id;
    })
  },
  getTreeChildrens: function (data, inid) {
    var ret = [];
    var curObj = data.find((j, i) => {
      return j.id == inid;
    });
    if (curObj) {
      var getChild = function (pid) {
        data.map((j, i) => {
          if (pid == j.pid) {
            ret.push(j);
            getChild(j.id);
          }
        })
      };
      getChild(inid);
    }
    return ret;
  },
  getTreeChildrensId: function (data, inid) {
    return this.getTreeChildrens(data, inid).map(function (j) {
      return j.id;
    })
  },
  getTreeInParents: function (data, id) {
    var ret = this.getTreeParents(data, id);
    data.some(function (j) {
      if (j.id == id) {
        ret.push(j);
        return true;
      } else {
        return false;
      }
    });
    return ret;
  },
  getTreeInParentsId: function (data, id) {
    return this.getTreeInParents(data, id).map(function (j) {
      return j.id;
    });
  },
  getTreeNodeById: function (data, id) {
    let list = JSON.parse(JSON.stringify(data)),
      ret = null;
    list.some(function (j) {
      if (j.id == id) {
        ret = j;
        return true;
      } else {
        return false;
      }
    });
    return ret;
  },
  Flat2TreeDataForCascader: function (data) {
    let list = JSON.parse(JSON.stringify(data)),
      ret = [];
    /*将扁平数据转换为树形数据*/
    //遍历子节点
    let getChildren = function (id) {
      var child = [];
      // 获取根节点
      list.map(function (i) {
        if (i.pid == id) {
          i.label = i.name;
          i.value = i.id;
          let children = getChildren(i.id);
          if (children.length > 0) {
            i['children'] = children;
          }
          child.push(i);
        }
      })
      return child;
    };
    //根据id获取节点对象
    let getObjById = function (id) {
      let ret = [];
      list.some(function (j) {
        if (j.id == id) {
          ret.push(j);
          return true;
        } else {
          return false;
        }
      });
      return ret;
    }
    // 获取根节点
    list.map(function (i) {
      //根节点
      if (i.pid == undefined || i.pid == "" || i.pid && getObjById(i.pid).length == 0) {
        i.label = i.name;
        i.value = i.id;
        i['children'] = getChildren(i.id);
        ret.push(i);
      }
    })
    return ret;
  },
  Flat2TreeDataForCascaderLeaf: function (data, arr) {
    var childrenList = [], queryList = [];
    if (arr) {
      childrenList = arr;
      queryList = data;
    } else {
      queryList = this.Flat2TreeDataForCascader(data);
    }
    for (var i = 0; i < queryList.length; i++) {
      var sonList = queryList[i].children;
      if (sonList == undefined || sonList.length == 0) {
        childrenList.push(queryList[i]);
      } else {
        this.Flat2TreeDataForCascaderLeaf(sonList, childrenList);
      }
    }
    return childrenList;
  },
  dateFormat(date, fmt) {
    let ret;
    const opt = {
      "Y+": date.getFullYear().toString(),        // 年
      "m+": (date.getMonth() + 1).toString(),     // 月
      "d+": date.getDate().toString(),            // 日
      "H+": date.getHours().toString(),           // 时
      "M+": date.getMinutes().toString(),         // 分
      "S+": date.getSeconds().toString()          // 秒
      // 有其他格式化字符需求可以继续添加，必须转化成字符串
    };
    for (let k in opt) {
      ret = new RegExp("(" + k + ")").exec(fmt);
      if (ret) {
        fmt = fmt.replace(ret[1], (ret[1].length == 1) ? (opt[k]) : (opt[k].padStart(ret[1].length, "0")))
      };
    };
    return fmt;
  },
  download: function (attachment) {
    var downloadHandler = function (content, filename) {
      var eleLink = document.createElement('a');
      eleLink.download = filename;
      eleLink.style.display = 'none';
      // 字符内容转变成blob地址
      var blob = new Blob([content], { type: "application/octet-stream" });
      eleLink.href = URL.createObjectURL(blob);
      // 触发点击
      document.body.appendChild(eleLink);
      eleLink.click();
      // 然后移除
      document.body.removeChild(eleLink);
    };

    let that = this;
    var ajax = new XMLHttpRequest();
    ajax.responseType = 'blob';
    //ajax.open("GET", config.serverBaseUrl + attachment.url, true);
    ajax.open("GET", attachment.url, true);
    ajax.setRequestHeader('access-token', localStorage.access_token);
    ajax.onreadystatechange = function () {
      if (this.readyState == 4) {
        if (this.status == 200) {
          /*if(this.response.type == "application/octet-stream"){
           that.downloadHandler(this.response,attachment.displayName)
           }
           else{
           swal('您要下载的资源已被删除！','' , 'error')
           }*/
          downloadHandler(this.response, attachment.name);
        } else if (this.responseText != "") {
          //console.log(this.responseText);
        }
      } else if (this.readyState == 2) {
        if (this.status == 200) {
          this.responseType = "blob";
        } else {
          this.responseType = "text";
        }
      }
    };
    ajax.send(null);
  },
}
