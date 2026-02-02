<template>
  <div ref="jessibuca" style="width: 100%; height: 100%;position: relative;">
    <div
      ref="container"
      style="width: 100%; height: 100%; background-color: #000"
    >
      <div ref="containerVideo" style="width: 100%; height: 100%; background-color: #000" @dblclick="fullscreenSwich">
        
      </div>
      <div class="buttons-box" id="buttonsBox" v-if="controls">
        <div class="buttons-box-left">
          <Icon
            v-if="!playing"
            type="ios-play"
            class="jessibuca-btn"
            @click="playBtnClick"
          />
          <Icon
            v-if="playing"
            type="ios-pause"
            class="jessibuca-btn"
            @click="pause"
          />
          <Icon type="ios-square" class="jessibuca-btn" @click="destroy" />
          <Icon
            v-if="isNotMute"
            type="md-volume-up"
            class="jessibuca-btn"
            @click="jessibuca.mute()"
          />
          <Icon
            v-if="!isNotMute"
            type="md-volume-off"
            class="jessibuca-btn"
            @click="jessibuca.cancelMute()"
          />
        </div>
        <div class="buttons-box-right">
          <span class="jessibuca-btn">{{ kBps }} kb/s</span>
          <i
            class="fa fa-floppy-o jessibuca-btn"
            aria-hidden="true"
            title="截图"
            @click="jessibuca.screenshot('截图', 'png', 0.5)"
          ></i>
          <i class="fa fa-fw fa-refresh jessibuca-btn" title="刷新" aria-hidden="true" @click="playBtnClick"></i>
          <i class="fa fa-fw fa-eject jessibuca-btn" title="云台控制" aria-hidden="true" @click="showControl = !showControl"></i>
          <i
            v-if="!fullscreen"
            class="fa fa-expand jessibuca-btn"
            aria-hidden="true"
            title="全屏"
            @click="fullscreenSwich"
          ></i>
          <i
            v-if="fullscreen"
            class="fa fa-compress jessibuca-btn"
            aria-hidden="true"
            title="退出全屏"
            @click="fullscreenSwich"
          ></i>
        </div>
      </div>

      <div class="control-wrapper" v-show="showControl">
      <div
        class="control-btn control-top"
        @mousedown="ptzCamera('up')"
        @mouseup="ptzCamera('stop')"
      >
        <i class="el-icon-caret-top"></i>
        <div class="control-inner-btn control-inner"></div>
      </div>
      <div
        class="control-btn control-left"
        @mousedown="ptzCamera('left')"
        @mouseup="ptzCamera('stop')"
      >
        <i class="el-icon-caret-left"></i>
        <div class="control-inner-btn control-inner"></div>
      </div>
      <div
        class="control-btn control-bottom"
        @mousedown="ptzCamera('down')"
        @mouseup="ptzCamera('stop')"
      >
        <i class="el-icon-caret-bottom"></i>
        <div class="control-inner-btn control-inner"></div>
      </div>
      <div
        class="control-btn control-right"
        @mousedown="ptzCamera('right')"
        @mouseup="ptzCamera('stop')"
      >
        <i class="el-icon-caret-right"></i>
        <div class="control-inner-btn control-inner"></div>
      </div>
      <div class="control-round">
        <div class="control-round-inner">
          <i class="fa fa-pause-circle"></i>
        </div>
      </div>
      <div
        style="position: absolute; left: 7.25rem; top: 1.25rem"
        @mousedown="ptzCamera('zoomin')"
        @mouseup="ptzCamera('stop')"
      >
        <i
          class="el-icon-zoom-in control-zoom-btn"
          style="font-size: 1.875rem"
        ></i>
      </div>
      <div
        style="
          position: absolute;
          left: 7.25rem;
          top: 3.25rem;
          font-size: 1.875rem;
        "
        @mousedown="ptzCamera('zoomout')"
        @mouseup="ptzCamera('stop')"
      >
        <i class="el-icon-zoom-out control-zoom-btn"></i>
      </div>
      <div
        class="contro-speed"
        style="position: absolute; left: 4px; top: 7rem; width: 9rem"
      >
        <el-slider v-model="controSpeed" :max="255"></el-slider>
      </div>
    </div>

    </div>
  </div>
</template>

<script>
export default {
  name: "jessibuca",
  data() {
    return {
      jessibuca: null,
      playing: false,
      isNotMute: false,
      quieting: false,
      fullscreen: false,
      loaded: false, // mute
      speed: 0,
      performance: "", // 工作情况
      kBps: 0,
      btnDom: null,
      videoInfo: null,
      volume: 1,
      rotate: 0,
      vod: true, // 点播
      forceNoOffscreen: false,
      videoUrl: null,
      showControl:false,
      controls:false,
      controSpeed: 30,
    };
  },
  props: ["deviceId", "channelId"],
  mounted() {},
  watch: {
    deviceId: {
      handler() {
        this.start();
      },
    },
    channelId: {
      handler() {
        this.start();
      },
      immediate: true,
    },
  },
  methods: {
    start() {
      if (!this.deviceId || !this.channelId) {
        return;
      }
      console.log("deviceId",this.deviceId)
      console.log("channelId",this.channelId)

      this.$ajax
        .get(
          `/videoservice28181/api/play/start/${this.deviceId}/${this.channelId}`
        )
        .then(
          (res) => {
            if (res.data.code === 0) {
              this.video28181Modal = true;
              this.videoUrl = this.getUrlByStreamInfo(res.data.data);
              this.playBtnClick();
            } else {
              this.$Modal.warning({
                title: "拉流失败",
                content: res.data.msg,
              });
            }
          },
          (res) => {
            res = res.response;
            if (res.data.code === 0) {
              this.video28181Modal = true;
              this.videoUrl = this.getUrlByStreamInfo(res.data.data);
              this.playBtnClick();
            } else {
              this.$Modal.warning({
                title: "拉流失败",
                content: res.data.msg,
              });
            }
          }
        );
    },
    getUrlByStreamInfo(streamInfo) {
      if (location.protocol === "https:") {
        if (streamInfo.wss_flv === null) {
          return streamInfo.ws_flv;
        } else {
          return streamInfo.wss_flv;
        }
      } else {
        return streamInfo.ws_flv;
      }
    },
    create() {
      let options = {};

      this.jessibuca = new window.Jessibuca(
        Object.assign(
          {
            container: this.$refs.containerVideo,
            videoBuffer: 0.5, // 最大缓冲时长，单位秒
            isResize: true,
            isFlv: true,
            decoder: "./static/plug/jessibuca/decoder.js",
            // text: "WVP-PRO",
            // background: "bg.jpg",
            loadingText: "加载中",
            hasAudio:
              typeof this.hasAudio == "undefined" ? true : this.hasAudio,
            debug: false,
            supportDblclickFullscreen: false, // 是否支持屏幕的双击事件，触发全屏，取消全屏事件。
            operateBtns: {
              fullscreen: false,
              screenshot: false,
              play: false,
              audio: false,
            },
            record: "record",
            vod: this.vod,
            forceNoOffscreen: this.forceNoOffscreen,
            isNotMute: this.isNotMute,
          },
          options
        )
      );

      console.log(this.jessibuca);
      let _this = this;
      this.jessibuca.on("load", function () {
        console.log("on load init");
      });

      this.jessibuca.on("log", function (msg) {
        console.log("on log", msg);
      });
      this.jessibuca.on("record", function (msg) {
        console.log("on record:", msg);
      });
      this.jessibuca.on("pause", function () {
        _this.playing = false;
      });
      this.jessibuca.on("play", function () {
        _this.playing = true;
      });
      this.jessibuca.on("fullscreen", function (msg) {
        console.log("on fullscreen", msg);
        _this.fullscreen = msg;
      });

      this.jessibuca.on("mute", function (msg) {
        console.log("on mute", msg);
        _this.isNotMute = !msg;
      });
      this.jessibuca.on("audioInfo", function (msg) {
        // console.log("audioInfo", msg);
      });

      this.jessibuca.on("videoInfo", function (msg) {
        // this.videoInfo = msg;
        console.log("videoInfo", msg);
      });

      this.jessibuca.on("bps", function (bps) {
        // console.log('bps', bps);
      });
      let _ts = 0;
      this.jessibuca.on("timeUpdate", function (ts) {
        // console.log('timeUpdate,old,new,timestamp', _ts, ts, ts - _ts);
        _ts = ts;
      });

      this.jessibuca.on("videoInfo", function (info) {
        console.log("videoInfo", info);
      });

      this.jessibuca.on("error", function (error) {
        console.log("error", error);
      });

      this.jessibuca.on("timeout", function () {
        console.log("timeout");
      });

      this.jessibuca.on("start", function () {
        console.log("start");
      });

      this.jessibuca.on("performance", function (performance) {
        let show = "卡顿";
        if (performance === 2) {
          show = "非常流畅";
        } else if (performance === 1) {
          show = "流畅";
        }
        _this.performance = show;
      });
      this.jessibuca.on("buffer", function (buffer) {
        // console.log('buffer', buffer);
      });

      this.jessibuca.on("stats", function (stats) {
        // console.log('stats', stats);
      });

      this.jessibuca.on("kBps", function (kBps) {
        _this.kBps = Math.round(kBps);
      });

      // 显示时间戳 PTS
      this.jessibuca.on("videoFrame", function () {});

      //
      this.jessibuca.on("metadata", function () {});
    },
    playBtnClick: function (event) {
      this.play(this.videoUrl);
    },
    play: function (url) {
      if (this.jessibuca) {
        this.destroy();
      }
      this.create();
      this.jessibuca.on("play", () => {
        this.playing = true;
        this.loaded = true;
        this.quieting = this.jessibuca.quieting;
      });
      if (this.jessibuca.hasLoaded()) {
        this.jessibuca.play(url);
      } else {
        this.jessibuca.on("load", () => {
          console.log("load 播放");
          this.jessibuca.play(url);
        });
      }
    },
    pause: function () {
      if (this.jessibuca) {
        this.jessibuca.pause();
      }
      this.playing = false;
      this.err = "";
      this.performance = "";
    },
    destroy: function () {
      if (this.jessibuca) {
        this.jessibuca.destroy();
      }
      this.jessibuca = null;
      this.playing = false;
      this.err = "";
      this.performance = "";
    },
    fullscreenSwich: function () {
      let isFull = this.isFullscreen();
      // this.jessibuca.setFullscreen(!isFull);
      this.toggleFullscreen(!isFull,this.$refs.container);
      this.fullscreen = !isFull;
      setTimeout(() => {
        this.jessibuca.resize();
      }, 60);
    },
    toggleFullscreen(flag,ele){
      if(flag){
        if (ele.requestFullscreen) {
          ele.requestFullscreen();
        } else if (ele.mozRequestFullScreen) {
            ele.mozRequestFullScreen();
        } else if (ele.webkitRequestFullscreen) {
            ele.webkitRequestFullscreen();
        } else if (ele.msRequestFullscreen) {
            ele.msRequestFullscreen();
        }
      }
      else{
        if(document.exitFullScreen) {
          document.exitFullScreen();
        } else if(document.mozCancelFullScreen) {
            document.mozCancelFullScreen();
        } else if(document.webkitExitFullscreen) {
            document.webkitExitFullscreen();
        } else if(document.msExitFullscreen) {
            document.msExitFullscreen();
        }
      }
    },
    isFullscreen: function () {
      return (
        document.fullscreenElement ||
        document.msFullscreenElement ||
        document.mozFullScreenElement ||
        document.webkitFullscreenElement ||
        false
      );
    },
    ptzCamera: function (command) {
        console.log('云台控制：' + command);
        let that = this;

        this.$ajax.post(
          '/videoservice28181/api/ptz/control/' + this.deviceId + '/' + this.channelId + '?command=' + command + '&horizonSpeed=' + this.controSpeed + '&verticalSpeed=' + this.controSpeed + '&zoomSpeed=' + this.controSpeed
        )
        .then(function (res) {
          console.log(res)
        });
    },
  },
  destroyed() {
    if (this.jessibuca) {
      this.jessibuca.destroy();
    }
    this.playing = false;
    this.loaded = false;
    this.performance = "";
  },
};
</script>

<style>
.buttons-box {
  width: 100%;
  height: 28px;
  background-color: rgba(43, 51, 63, 0.7);
  position: absolute;
  display: -webkit-box;
  display: -ms-flexbox;
  display: flex;
  left: 0;
  bottom: 0;
  user-select: none;
  z-index: 10;
}
.jessibuca-btn {
  width: 20px;
  color: rgb(255, 255, 255);
  line-height: 27px;
  margin: 0px 10px;
  padding: 0px 2px;
  cursor: pointer;
  text-align: center;
  font-size: 0.8rem !important;
}
.buttons-box-right {
  position: absolute;
  right: 0;
}

.control-wrapper {
  position: absolute;
  right:60px;
  bottom:40px;
  z-index:1000;
  width: 6.25rem;
  height: 6.25rem;
  max-width: 6.25rem;
  max-height: 6.25rem;
}

.control-panel {
    position: relative;
    top: 0;
    left: 5rem;
    height: 11rem;
    max-height: 11rem;
}

.control-btn {
    display: flex;
    justify-content: center;
    position: absolute;
    width: 44%;
    height: 44%;
    border-radius: 5px;
    border: 1px solid #fff;
    box-sizing: border-box;
    transition: all 0.3s linear;
}
.control-btn:hover {
    cursor:pointer
}

.control-btn i {
    font-size: 20px;
    color: #fff;
    display: flex;
    justify-content: center;
    align-items: center;
}
.control-btn i:hover {
    cursor:pointer
}
.control-zoom-btn{
  color: #fff;
}
.control-zoom-btn:hover {
    cursor:pointer
}

.control-round {
    position: absolute;
    top: 21%;
    left: 21%;
    width: 58%;
    height: 58%;
    background: #fff;
    border-radius: 100%;
}

.control-round-inner {
    position: absolute;
    left: 13%;
    top: 13%;
    display: flex;
    justify-content: center;
    align-items: center;
    width: 70%;
    height: 70%;
    font-size: 40px;
    color: #78aee4;
    border: 1px solid #78aee4;
    border-radius: 100%;
    transition: all 0.3s linear;
}

.control-inner-btn {
    position: absolute;
    width: 60%;
    height: 60%;
    background: #fafafa;
}

.control-top {
    top: -8%;
    left: 27%;
    transform: rotate(-45deg);
    border-radius: 5px 100% 5px 0;
}

.control-top i {
    transform: rotate(45deg);
    border-radius: 5px 100% 5px 0;
}

.control-top .control-inner {
    left: -1px;
    bottom: 0;
    border-top: 1px solid #78aee4;
    border-right: 1px solid #78aee4;
    border-radius: 0 100% 0 0;
}

.control-top .fa {
    transform: rotate(45deg) translateY(-7px);
}

.control-left {
    top: 27%;
    left: -8%;
    transform: rotate(45deg);
    border-radius: 5px 0 5px 100%;
}

.control-left i {
    transform: rotate(-45deg);
}

.control-left .control-inner {
    right: -1px;
    top: -1px;
    border-bottom: 1px solid #78aee4;
    border-left: 1px solid #78aee4;
    border-radius: 0 0 0 100%;
}

.control-left .fa {
    transform: rotate(-45deg) translateX(-7px);
}

.control-right {
    top: 27%;
    right: -8%;
    transform: rotate(45deg);
    border-radius: 5px 100% 5px 0;
}

.control-right i {
    transform: rotate(-45deg);
}

.control-right .control-inner {
    left: -1px;
    bottom: -1px;
    border-top: 1px solid #78aee4;
    border-right: 1px solid #78aee4;
    border-radius: 0 100% 0 0;
}

.control-right .fa {
    transform: rotate(-45deg) translateX(7px);
}

.control-bottom {
    left: 27%;
    bottom: -8%;
    transform: rotate(45deg);
    border-radius: 0 5px 100% 5px;
}

.control-bottom i {
    transform: rotate(-45deg);
}

.control-bottom .control-inner {
    top: -1px;
    left: -1px;
    border-bottom: 1px solid #78aee4;
    border-right: 1px solid #78aee4;
    border-radius: 0 0 100% 0;
}

.control-bottom .fa {
    transform: rotate(-45deg) translateY(7px);
}
</style>
