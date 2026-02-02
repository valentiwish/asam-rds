<template>
  <div class="pager-header layout" ref="layout" :class="{'minheader':$store.state.theme.minHeader}">
    <div class="layout-logo" title="机器人调度系统">
      <div class="layout-logo_bg"></div>
      <div class="layout-logo_text"  @dblclick="toggleFullScreen">
        <div class="logo-text">机器人调度系统</div>
      </div>
    </div>
    <div class="layout-menu" ref="layoutmenu">
      <div class="layout-menu-content" :style="{'width':layoutmenucontentW}">
        <div class="layout-menu-item" :class="{'active':selectMenu== obj.id }" v-for="(obj,index) in sysMenuData" @click="showPage(index,obj)"
             :title="obj.name" :key="obj.id">
          <div class="layout-menu-item-content">
            <div class="layout-menu-item-content_icon"><i :class="obj.icon+' fa-fw icon'"></i></div>
            <div class="layout-menu-item-content_title">{{obj.name}}</div>
          </div>
        </div>
      </div>
    </div>

    <div class="layout-right">
      <Badge :count="$store.state.todoListCount">
        <div class="pager-header-item layout-userinfo_content" @click="$router.push('/workbench/message')">
          <template v-if="userInfo.photoId">
            <img :src="$utils.getFileShowUrl(userInfo.photoId)" :style="getPhotoStyle">
          </template>
          <template v-else>
            <img :src="'./static/images/header/man.jpg'" v-if="userInfo.sex=='男'">
            <img :src="'./static/images/header/woman.jpg'" v-else-if="userInfo.sex=='女'">
            <img :src="'./static/images/header/default-photo.jpg'" v-else>
          </template>
        </div>
      </Badge>
      <div class="pager-header-item">
        <Dropdown @on-click="clickMenu" trigger="click">
          <div style="cursor:pointer;">
            <span style="vertical-align:middle;"> 欢迎您</span>
            <span v-html="userInfo.userName"
                  style="display:inline-block;max-width:83px;overflow:hidden;word-break:keep-all;white-space:nowrap;text-overflow:ellipsis;vertical-align:middle;"></span>
            <Icon type="md-arrow-dropdown"></Icon>
          </div>
          <DropdownMenu slot="list">
            <DropdownItem name="userinfo">
              <Icon type="ios-contact"></Icon> 个人信息
            </DropdownItem>
            <DropdownItem name="updatePwd">
              <Icon type="ios-key"></Icon> 修改密码
            </DropdownItem>
            <DropdownItem name="loginOut">
              <Icon type="md-power"></Icon> 退出系统
            </DropdownItem>
          </DropdownMenu>
        </Dropdown>
      </div>

      <!-- <div class="pager-header-item" @click="$router.push('/activiti/mytasklist')">
                <Icon type="ios-chatboxes-outline"/> 消息
                <span style="background:#D75233;padding:0 5px;" v-show="$store.state.todoListCount>0">
                    {{$store.state.todoListCount}}
                </span>
            </div> -->
      <!--  <div class="pager-header-item" @click="loginOut">
                <Icon type="ios-power-outline" />
                退出系统
            </div> -->
    </div>
    <!-- 个人信息-->
    <personal-info ref="userInfo"></personal-info>
    <!-- 修改密码 -->
    <change-pwd ref="changePwd" :userInfo="userInfo"></change-pwd>
  </div>
</template>

<script>

import changePwd from './changePwd'
import personalInfo from './personalInfo'

export default {
  name: 'header',
  components: {
    changePwd, personalInfo
  },
  data: function () {
    return {
      baseUserData: [],
      baseCascader: [],
      photoSize: {
        width: 0,
        height: 0
      },
      selectMenu: "/home",
      layoutmenucontentW: "1500px",
    }
  },
  computed: {
    sysMenuDataOriginal () {
      return this.$store.state.auth.sysMenuDataOriginal;
    },
    sysMenuData () {
      return this.$store.state.auth.sysMenuData;
    },
    userInfo () {
      if (this.$store.state.auth && this.$store.state.auth.userInfo) {
        return this.$store.state.auth.userInfo;

      } else {
        var obj = {};
        return obj
      }

    },
    getPhotoStyle () {
      if (this.photoSize.width == this.photoSize.height) {
        return {
          width: "100%"
        }
      }
      else if (this.photoSize.width > this.photoSize.height) {
        return {
          width: "auto",
          height: "100%",
          transform: "translateX(-25%)"
        }
      }
      else {
        return {
          height: "auto",
          width: "100%"
        }
      }

    }
  },
  methods: {
    goSysNav () {
      if (window._sysBaseConf) {
        window.location.href = window._sysBaseConf.navUrl;
      }
    },
    toggleFullScreen: function (e) {
      this.$store.commit("theme/toggleThemeMinHeader");
    },
    showPage: function (index, obj) {
      this.selectMenu = obj.id;
      var getUrl = function (obj) {
        var url = null;
        if (obj.url && obj.url != "#") {
          url = obj.url;
        }
        else if (obj.children && obj.children.length > 0) {
          var url = getUrl(obj.children[0]);
        };
        return url;
      }
      //跳转页面
      this.$nextTick(() => {
        var url = getUrl(obj);
        if (url) {
          if (this.$utils.checkIsUrl(url)) {
            window.open(url)
          }
          else {
            this.$router.push(url);
          }
        }
      })
    },
    loginOut: function () {
      this.$root.loginout();
    },
    userinfo: function () {
      this.$refs.userInfo && this.$refs.userInfo.openModal();
    },
    updatePwd: function () {
      this.$refs.changePwd && this.$refs.changePwd.openModal();
    },
    clickMenu: function (a) {
      this[a] && this[a]();
    },
    "setLayoutmenucontentW": function () {
      var itemW = 110;
      this.layoutmenucontentW = itemW * this.sysMenuData.length + 'px'
    },
    socketio () {
      var that = this;
      this.$ajax.post("/service_user/company/obtainCurUser")
        .then((res) => {
          console.log("res", res)
          var userId = res.data.data.id;
          var url = "http://192.168.13.233:9099?userId=" + userId
          var socket = io(url, { transports: ['websocket'] });
          // this.socket = io(url,{transports:['websocket']});
          // this.socket = io("http://192.168.13.206:9099?userId=192.168.13.206_8080",{transports:['websocket']});
          // this.socket = io("http://192.168.13.213:9099?userId=aaa",{transports:['websocket']});
          //this.socket = io("http://192.168.13.206:9099");
          //this.socket = io("http://192.168.13.245:9000");
          console.log(socket, 'ddd')
          socket.on('connect', () => {
            console.log("连接成功")
            socket.emit("subscribe", ["push_to_user", 'data/realDataByType', 'all']);
          });
          socket.on('push_to_user', function (res) {
            let temp = {
              time: new Date().Format("yyyy-MM-dd hh:mm:ss"),
              desc: res
            }
            that.$Notice.info({
              title: '待办消息通知',
              desc: res
            });
          });
        })
        .catch((err) => {
          if (!err.url) { console.info(err); }
        })
    },
    addMouseWheelEvent: function (element, func) {
      if (typeof element.onmousewheel == "object") {
        element.onmousewheel = function (e) {
          e.stopPropagation();
          e.preventDefault();
          return func(e);
        };
      }
      if (typeof element.onmousewheel == "undefined") {
        element.addEventListener("DOMMouseScroll", function (e) {
          e.deltaY = 100 * (e.detail < 0 ? -1 : 1);
          e.stopPropagation();
          e.preventDefault();
          return func(e);
        }, false);
      }
    },
    activeMenu: function () {
      this.sysMenuDataOriginal.some((j, i) => {
        if (j.url == this.$route.name) {
          if (!j.pid) {
            this.selectMenu = j.id;
          } else {
            var arr = this.$utils.getTreeParents(this.sysMenuDataOriginal, j.id);
            if (arr.length > 0) {
              this.selectMenu = arr[0].id;
            } else {
              this.selectMenu = j.id;
            }
          }
          this.$store.commit("auth/setMainMenuId", this.selectMenu);
          return true;
        } else {
          return false;
        }
      });
    },
  },
  mounted: function () {
    if (this.$refs.layout) {
      var layoutmenu = this.$refs.layoutmenu;
      this.addMouseWheelEvent(this.$refs.layout, function (e) {

        layoutmenu.scrollLeft = layoutmenu.scrollLeft + e.deltaY;
        return false;
      });
    }
    this.$nextTick(() => {
      this.activeMenu();
      this.setLayoutmenucontentW();
    });
    $(window).on("resize", () => {
      this.setLayoutmenucontentW();
    });
  },
  created: function () {
    //this.socketio();
  },
  watch: {
    "$route.path": function () {
      this.activeMenu();
    },
    "sysMenuData": function () {
      this.activeMenu();
      this.setLayoutmenucontentW();
    },
    "userInfo": {
      immediate: true,
      handler: function (n) {
        if (n.photoId) {
          var img = new Image();
          img.src = this.$utils.getFileShowUrl(n.photoId);
          img.onload = () => {
            this.photoSize = {
              width: img.width,
              height: img.height
            }
          }
        }

      }
    }
  },
}
</script>

<style scoped>
.pager-header {
  position: relative;
  height: 86px;
  background-color: rgba(59, 72, 97);
  user-select: none;
  z-index: 1000;
  /* background-image:url(../../../static/images/header/logo_bg.jpg); */
  background-repeat: no-repeat;
  background-position: left bottom;
  background-size: auto 100%;
}
.layout-logo {
  display: inline-block;
  height: 86px;
  line-height: 86px;
  color: #fff;
  font-size: 20px;
}
.layout-logo_bg {
  display: inline-block;
  width: 86px;
  height: 86px;
  /* background: url(/sysconf/source/sys-logo.png) center no-repeat; */
  background-image: url(../../../static/images/header/logo_kg.png);
  background-repeat: no-repeat;
  background-size: auto 40px;
  background-position: center center;
}
.layout-logo_text {
  display: inline-block;
  color: #fff;
  vertical-align: top;
  font-weight: bold;
}
.logo-text {
  background-image: linear-gradient(to bottom, white 35%, #394659 100%);
  color: transparent;
  -webkit-background-clip: text;
  font-size: 34px;
  width: 250px;
  letter-spacing: 1px;
  font-style: italic;
  font-weight: normal;
  font-family: 'MyFont';
}
.layout-menu {
  position: absolute;
  left: 350px;
  right: 230px;
  top: 0;
  height: 86px;
  color: #fff;
  font-size: 16px;
  overflow: hidden;
}
.layout-menu::-webkit-scrollbar {
  width: 4px;
  height: 1px;
}

.layout-menu::-webkit-scrollbar-thumb {
  border-radius: 2px;
  background-color: #088b90;
}
.layout-menu .layout-menu-item {
  display: inline-block;
  padding: 7px 5px;
  cursor: pointer;
  height: 86px;
  color: #fff;
  line-height: 2;
  width: 110px;
  text-align: center;
  background: url(../../../static/images/header/nav_line.gif) no-repeat right center;
}
.layout-menu .layout-menu-item .layout-menu-item-content {
  padding: 10px 0;
  transition: all 200ms ease-in-out;
}
.layout-menu .layout-menu-item .layout-menu-item-content .layout-menu-item-content_icon {
  font-size: 16px;
  vertical-align: middle;
}
.layout-menu .layout-menu-item .layout-menu-item-content .layout-menu-item-content_title {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  line-height: 1;
  font-size: 15px;
  transition: all 200ms ease-in-out;
}

.layout-menu .layout-menu-item:last-child {
  background: none;
}

.layout-menu .layout-menu-item .icon {
  width: 20px;
  height: 20px;
  font-size: 18px;
  transition: all 200ms ease-in-out;
}

.layout-menu .layout-menu-item:hover {
  background: linear-gradient(rgba(79, 187, 213, 0.6), rgba(79, 187, 213, 0.1));
  color: #fff;
}
.layout-menu .layout-menu-item.active {
  background: url(../../../static/images/header/nav_arrow.png) no-repeat bottom center, linear-gradient(rgb(30 130 233), rgb(53 129 199));
  color: #fff;
}

.layout-menu .layout-menu-item:hover .layout-menu-item-content,
.layout-menu .layout-menu-item.active .layout-menu-item-content {
  color: #fff;
}
.layout-menu .layout-menu-item.active > div,
.layout-menu .layout-menu-item:hover > div {
  text-shadow: 0px 0px 5px #131313;
}

.layout-menu .layout-menu-item.active .icon,
.layout-menu .layout-menu-item:hover .icon {
  background-position: 0;
}

.layout-menu .layout-menu-item:hover {
  opacity: 0.9;
}

.layout-menu .layout-menu-item .sys-badge {
  display: inline-block;
  width: 20px;
  height: 20px;
  border-radius: 20px;
  background: #ff4700;
  color: #fff;
  line-height: 18px;
  font-size: 14px;
  text-align: center;
  border: 1px solid #fff;
}

.layout-right {
  position: absolute;
  right: 10px;
  top: 25px;
  font-size: 15px;
  text-align: left;
  color: #efefef;
}
.layout-right .layout-userinfo_content {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  vertical-align: middle;
  border: 1px solid #079583;
  box-shadow: 0 0 1px 1px #079583;
  transition: all 200ms ease-in-out;
  overflow: hidden;
}
.layout-right .layout-userinfo_content img {
  width: 100%;
}
.layout-copyright {
  position: absolute;
  right: 20px;
  top: 0;
  padding: 25px 0;
}
.layout-copyright img {
  width: 80px;
}

.pager-header-item {
  display: inline-block;
  vertical-align: middle;
  margin-left: 15px;
  cursor: pointer;
}
.pager-header-item:hover {
  color: #fff;
}
.pager-header-item .ivu-select-dropdown {
  margin-right: 10px;
}
.pager-header-item .ivu-select-dropdown .ivu-dropdown-menu {
  min-width: 120px;
}

.pager-header-item .ivu-select-dropdown .ivu-dropdown-menu .ivu-dropdown-item {
  font-size: 14px !important;
}
.pager-header-item .ivu-select-dropdown .ivu-dropdown-menu .ivu-dropdown-item .ivu-icon {
  display: inline-block;
  width: 20px;
  text-align: center;
}
.minheader.pager-header {
  height: 60px;
}
.minheader .layout-logo {
  height: 60px;
}
.minheader .layout-logo_bg {
  height: 60px;
}
.minheader .layout-logo_text {
  font-size: 20px;
  line-height: 60px;
  height: 60px;
}
.minheader .layout-menu .layout-menu-item {
  height: 60px;
}
.minheader .layout-menu .layout-menu-item .layout-menu-item-content .layout-menu-item-content_icon {
  display: inline-block;
  font-size: 14px;
  vertical-align: top;
  margin-top: 3px;
}
.minheader .layout-menu .layout-menu-item .layout-menu-item-content .layout-menu-item-content_title {
  display: inline-block;
  font-size: 14px;
  max-width: 75px;
  overflow: hidden;
}
.minheader .layout-right {
  top: 10px;
}
</style>