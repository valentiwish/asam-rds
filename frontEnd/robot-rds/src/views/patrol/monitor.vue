<template>
  <div class="page-content">
    <Row :gutter="16" style="height: 100%;">
      <Col span="18" style="height: 100%;">
      <div style="height: 37%;margin-right: 8px;">
        <Row style="height: 100%;" :gutter="16">
          <Col span="16" style="height: 100%;">
          <div style="height: calc(50% - 5px);margin-right: 8px">
            <Row style="height: 100%;" gutter="16">
              <Col span="8" style="height: 100%;">
              <div class="dashed-border" style="height: 100%;">
                <Row style="height: 100%;">
                  <Col span="14" style="height: 100%;padding:20px 0;text-align: center;" class="flex-column">
                  <div><img :src="'./static/images/patrol/monitor/agv01.png'" /></div>
                  <div style="font-size: 16px;">
                    <Select v-model="robotId" size="large" @on-change="selectRobot">
                      <Option v-for="item in robotList" :value="item.vehicleId" :key="item.vehicleId">{{ item.vehicleId }}</Option>
                    </Select>
                  </div>
                  </Col>
                  <Col span="10" class="flex-column" style="height: 100%;padding:20px 30px 20px 10px;align-items: baseline;">
                  <div style="display: flex;align-items: center;">
                    <span style="margin-right: 10px;">电量</span>
                    <img :src="'./static/images/patrol/monitor/battery.png'" />
                  </div>
                  <div style="color: #01b2f9;font-size: 16px;font-weight: bold;">{{currentRobot.batteryLevel}}%</div>
                  <div style="display: flex;align-items: center;">
                    <span style="margin-right: 10px;">速度</span>
                    <img :src="'./static/images/patrol/monitor/speed.png'" />
                  </div>
                  <div style="color: #01b2f9;font-size: 16px;font-weight: bold;">{{currentRobot.vx}}</div>
                  </Col>
                </Row>
              </div>
              </Col>
              <Col span="16" style="height: 100%;padding: 20px 20px;" class="dashed-border flex-column">
              <Row style="width: 100%;">
                <Col span="9" style="display: flex;align-items: center;">
                <img :src="'./static/images/patrol/monitor/car.png'" />
                <span style="margin-left: 8px;">巡检中</span>
                </Col>
                <Col span="5" style="text-align: right;">
                <img :src="'./static/images/patrol/monitor/play.png'" />
                </Col>
                <Col span="5" style="text-align: right;">
                <img :src="'./static/images/patrol/monitor/pause.png'" />
                </Col>
                <Col span="5" style="text-align: right;">
                <img :src="'./static/images/patrol/monitor/stop.png'" />
                </Col>
              </Row>
              <div style="padding: 10px 0 5px;width: 100%;">
                <Progress :percent="80" :stroke-color="['#3b82e4', '#00b0f9']" hide-info />
                <div style="color: #01b2f9;font-size: 16px;font-weight: bold;text-align: right;">80%</div>
              </div>
              <div style="display: flex;justify-content: space-between;width: 100%;">
                <span>正在执行：任务名称12345678</span><span>预计剩余时间：x分钟</span>
              </div>
              </Col>
            </Row>
          </div>
          <div class="dashed-border" style="margin-top: 10px;height: calc(50% - 5px);font-size: 16px;">
            <Row style="padding: 20px;">
              <Col span="6" style="text-align: center;">
              <div style="color: #01b2f9;font-size: 40px;margin-top: 5px;">12</div>
              <div>今日任务总数</div>
              </Col>
              <Col span="10">
              <div class="flex-row" style="margin-top: 10px;">
                <div class="flex-row"><span>已执行</span> <span class="number-size">8</span></div>
                <div class="flex-row"><span>异常数量</span> <span class="number-size" style="color: #fe6702;">8</span></div>
              </div>
              <div class="flex-row" style="margin-top: 20px;">
                <div class="flex-row"><span>未执行</span> <span class="number-size">8</span></div>
                <div class="flex-row"><span>正常数量</span> <span class="number-size">8</span></div>
              </div>
              </Col>
              <Col span="8" style="text-align: center;">
              <div>
                <img :src="'./static/images/patrol/monitor/time.png'" style="width: 24px;" />
                <span style="margin-left: 8px;color: #01b2f9;font-size: 36px;">10:00</span>
              </div>
              <div style="margin-top: 15px;">据下次任务还剩时间</div>
              </Col>
            </Row>
          </div>
          </Col>
          <Col span="8" class="dashed-border flex-column" style="height: 100%;padding: 20px 20px 20px 50px;font-size: 16px;align-items: normal;">
          <Row style="margin: 0 20px;">
            <Col span="12">
            <Row>
              <Col span="12" style="text-align: center;">
              <img :src="'./static/images/patrol/monitor/temp.png'" />
              </Col>
              <Col span="12">
              <div style="color: #01b2f9;">温度</div>
              <div>25℃</div>
              </Col>
            </Row>
            </Col>
            <Col span="12">
            <Row>
              <Col span="12" style="text-align: center;">
              <img :src="'./static/images/patrol/monitor/humi.png'" />
              </Col>
              <Col span="12">
              <div style="color: #01b2f9;">湿度</div>
              <div>25℃</div>
              </Col>
            </Row>
            </Col>
          </Row>
          <Row style="margin: 0 20px;">
            <Col span="12">
            <Row>
              <Col span="12" style="text-align: center;">
              <img :src="'./static/images/patrol/monitor/co2.png'" />
              </Col>
              <Col span="12">
              <div style="color: #01b2f9;">二氧化碳</div>
              <div>25℃</div>
              </Col>
            </Row>
            </Col>
            <Col span="12">
            <Row>
              <Col span="12" style="text-align: center;">
              <img :src="'./static/images/patrol/monitor/o2.png'" />
              </Col>
              <Col span="12">
              <div style="color: #01b2f9;">氧气</div>
              <div>25℃</div>
              </Col>
            </Row>
            </Col>
          </Row>
          <Row style="margin: 0 20px;">
            <Col span="12">
            <Row>
              <Col span="12" style="text-align: center;">
              <img :src="'./static/images/patrol/monitor/ch2o.png'" />
              </Col>
              <Col span="12">
              <div style="color: #01b2f9;">甲醛</div>
              <div>25℃</div>
              </Col>
            </Row>
            </Col>
            <Col span="12">
            <Row>
              <Col span="12" style="text-align: center;">
              <img :src="'./static/images/patrol/monitor/pm.png'" />
              </Col>
              <Col span="12">
              <div style="color: #01b2f9;">PM2.5</div>
              <div>25℃</div>
              </Col>
            </Row>
            </Col>
          </Row>
          </Col>
        </Row>
      </div>
      <div class="dashed-border" style="margin: 10px 0;height: 28%;padding: 30px;position: relative;">
        <div ref="map" style="width: 100%;height:100%;">
          <canvas ref="canvas"></canvas>
        </div>
        <canvas ref="point" style="width: 100%;height:100%;position: absolute;top: 0;left: 0;"></canvas>
        <div style="position: absolute;top: 43%;width:90%;display: flex;justify-content: center;">
          <div style="display: flex;align-items: center;">
            <img :src="'./static/images/patrol/monitor/00.png'" style="margin: 0 10px;"/>
            <span>未巡检</span>
          </div>
          <div style="display: flex;align-items: center;margin: 0 30px;">
            <img :src="'./static/images/patrol/monitor/11.png'" style="margin: 0 10px;"/>
            <span>已巡正常</span>
          </div>
          <div style="display: flex;align-items: center;">
            <img :src="'./static/images/patrol/monitor/22.png'" style="margin: 0 10px;"/>
            <span>已巡异常</span>
          </div>
        </div>
      </div>
      <div class="dashed-border" style="height:calc(35% - 20px)">
        <Tabs value="name1" style="height: 100%;">
          <TabPane label="巡检结果" name="name1">
            <div style="height: 100%;margin: 2px 1px 2px 3px;">
              <data-grid :option="patrolOption" ref="grid"></data-grid>
            </div>
          </TabPane>
          <TabPane label="环境告警" name="name2">
            <div style="height: 100%;margin: 2px 1px 2px 3px;">
              <data-grid :option="patrolOption"></data-grid>
            </div>
          </TabPane>
          <TabPane label="系统告警" name="name3">
            <div style="height: 100%;margin: 2px 1px 2px 3px;">
              <data-grid :option="patrolOption"></data-grid>
            </div>
          </TabPane>
        </Tabs>
      </div>
      </Col>
      <Col span="6" class="dashed-border" style="height: 100%;">
      <div name="videoDiv" style="height: calc(24% - 5px);margin: 5px 0;background-color: #AEB5C7;padding: 10px;position: relative;">
        <div style="width: 100%;height: 100%;background-color: black;">
          <wasmPlayer ref="videoPlayer" :deviceId="deviceId" :channelId="channelId2" fluent autoplay live />
        </div>
        <div class="video-span">
          <span>实时热成像视频</span>
        </div>
      </div>
      <div name="videoDiv" style="height: calc(24% - 5px);margin: 5px 0;background-color: #AEB5C7;padding: 10px;position: relative;">
        <div style="width: 100%;height: 100%;background-color: black;">
          <wasmPlayer ref="videoPlayer" :deviceId="deviceId" :channelId="channelId1" fluent autoplay live />
        </div>
        <div class="video-span">
          <span>实时可见光视频</span>
        </div>
      </div>
      <div name="videoDiv"
           style="height: calc(24% - 5px);margin: 5px 0;background-color: #AEB5C7;padding: 10px;position: relative;text-align: center;">
        <div style="width: 100%;height: 100%;background-color: black;">
          <img v-if="imgUrl" :src="'data:image/png;base64,'+imgUrl" style="height: 100%;" />
        </div>
        <div class="video-span">
          <span>实时处理后视频</span>
        </div>
      </div>
      <div style="height: calc(28% - 10px);margin: 5px 0;background-color: #d5e4fc;">
        <Row style="height: 100%;padding: 5px;" :gutter="8">
          <Col span="6" style="height: 100%;" class=" flex-column">
          <div class="control" :class="statusActive==0?'blue-gradient':''" @click="statusActive=0">
            <img :src="'./static/images/patrol/monitor/auto'+statusActive+'.png'" />
            <div>自动状态</div>
          </div>
          <div class="control" :class="statusActive==1?'blue-gradient':''" @click="statusActive=1">
            <img :src="'./static/images/patrol/monitor/oper'+statusActive+'.png'" />
            <div>手动状态</div>
          </div>
          <div class="control" :class="typeActive==0?'blue-gradient':''" @click="typeActive=0">
            点动
          </div>
          <div class="control" :class="typeActive==1?'blue-gradient':''" @click="typeActive=1">
            连续
          </div>
          </Col>
          <Col span="8" style="height: 100%;">
          <div class="white-border flex-column">
            <div @click="agvPause" class="control yellow-gradient" style="width: 75px;">
              停止
            </div>
            <div class="robot-block" style="width: 60px;margin: 9px auto;">
              <div><img @mousedown="agvAction('up')" @mouseup="agvMouseUp" :src="'./static/images/patrol/monitor/up.png'" /></div>
              <div><img :src="'./static/images/patrol/monitor/agv.png'" style="cursor: default;" /></div>
              <div><img @mousedown="agvAction('down')" @mouseup="agvMouseUp" :src="'./static/images/patrol/monitor/up.png'"
                     style="transform: rotate(-180deg);" /></div>
            </div>
            <div @click="agvCharge" class="control blue-gradient" style="width: 75px;">
              充电
            </div>
          </div>
          </Col>
          <Col span="10" style="height: 100%;">
          <div class="white-border flex-column">
            <div @click="ptzAction(8)" class="control yellow-gradient" style="width: 75px;">
              停止
            </div>
            <div class="robot-block" style="margin: 9px 0;padding: 5px 10px;width: 100%;">
              <div style="padding: 1.5px 0;">
                <img @mousedown="ptzAction(4)" @mouseup="ptzMouseUp" :src="'./static/images/patrol/monitor/bias.png'" />
                <img @mousedown="ptzAction(0)" @mouseup="ptzMouseUp" :src="'./static/images/patrol/monitor/up.png'" />
                <img @mousedown="ptzAction(6)" @mouseup="ptzMouseUp" :src="'./static/images/patrol/monitor/bias.png'"
                     style="transform: rotate(90deg);" />
              </div>
              <div style="padding: 1.5px 0;">
                <img @mousedown="ptzAction(2)" @mouseup="ptzMouseUp" :src="'./static/images/patrol/monitor/up.png'"
                     style="transform: rotate(-90deg);" />
                <span>云台</span>
                <img @mousedown="ptzAction(3)" @mouseup="ptzMouseUp" :src="'./static/images/patrol/monitor/up.png'"
                     style="transform: rotate(90deg);" />
              </div>
              <div style="padding: 1.5px 0;">
                <img @mousedown="ptzAction(5)" @mouseup="ptzMouseUp" :src="'./static/images/patrol/monitor/bias.png'"
                     style="transform: rotate(-90deg);" />
                <img @mousedown="ptzAction(1)" @mouseup="ptzMouseUp" :src="'./static/images/patrol/monitor/up.png'"
                     style="transform: rotate(-180deg);" />
                <img @mousedown="ptzAction(7)" @mouseup="ptzMouseUp" :src="'./static/images/patrol/monitor/bias.png'"
                     style="transform: rotate(180deg);" />
              </div>
            </div>
            <div @click="ptzZero" class="control blue-gradient" style="width: 75px;">
              回零
            </div>
          </div>
          </Col>
        </Row>
      </div>
      </Col>
    </Row>
    <Modal v-model="picture" footer-hide width="700px" mask-closable>
      <div style="padding: 30px 20px 20px;">
        <img :src="picUrl" style="width: 100%;" />
      </div>
    </Modal>
  </div>
</template>
<script>
import dataGrid from '@/components/datagrid.vue'
import wasmPlayer from '@/components/wasmPlayer/jessibuca.vue';

export default {
  name: 'monitor',
  components: { dataGrid, wasmPlayer },
  data () {
    var that = this;
    this._socketio = null;
    return {
      deviceId: "34020000001320000001",
      //通道编号 必填
      channelId1: "34020000001310000001",
      channelId2: "34020000001310000002",
      statusActive: 0,
      typeActive: 0,
      patrolOption: {
        url: "/service_rms/railInspection/list",
        auto: true,//该页面是否自动加载，不会自动调用list方法
        height: 183,
        pagination: {
          pageSize: 3
        },
        columns: [ //列配置
          { "title": "编号", "key": "serialNumber", "align": "center", "width": 80 },
          { "title": "识别时间", "key": "inspectionTime", "align": "center" },
          { "title": "任务名称", "key": "taskName", "align": "center", "width": 100, ellipsis: true },
          { "title": "任务类型", "key": "taskType", "align": "center", "width": 100 },
          { "title": "巡检对象", "key": "inspectionObject", "align": "center" },
          { "title": "检测类型", "key": "inspectionType", "align": "center", "width": 100 },
          {
            "title": "检测结果", "key": "inspectionResult", "align": "center", "width": 100,
            render (h, params) {
              let color = "";
              switch (params.row.resultType) {
                case 0:
                  color = '#1361bf';
                  break;
                case 1:
                  color = '#ff4e00';
                  break;
                case 2:
                  color = 'red';
                  break;
              }
              return h('div', [
                h('span', {
                  style: {
                    color: color
                  }
                }, params.row.inspectionResult)
              ])
            }
          },
          {
            "title": "多媒体记录", "key": "taskName", "align": "center", width: '160px',
            render (h, params) {
              return h('div', [
                h('img', {
                  attrs: {
                    src: './static/images/patrol/monitor/record.png',
                    alt: '音频'
                  },
                  style: {
                    width: '24px',
                    height: '24px'
                  }
                }),
                h('img', {
                  attrs: {
                    src: './static/images/patrol/monitor/pic.png',
                    alt: '图片'
                  },
                  style: {
                    width: '24px',
                    height: '24px',
                    margin: '0 10px',
                    cursor: 'pointer'
                  },
                  on: {
                    click: () => {
                      that.picture = true;
                      console.log(that.$utils.getFileShowUrl(params.row.pictureId))
                      that.picUrl = that.$utils.getFileShowUrl(params.row.pictureId)
                    }
                  }
                }, ''),
                h('img', {
                  attrs: {
                    src: './static/images/patrol/monitor/video.png',
                    alt: '视频'
                  },
                  style: {
                    width: '24px',
                    height: '24px'
                  }
                }, '')
              ])
            }

          },
          {
            "title": "操作 ", "key": "taskName", "align": "center", width: '100px',
            render (h, params) {
              return h('div', [
                h('span', {
                  style: {
                    color: '#ff4e00',
                    cursor: 'pointer'
                  },
                  on: {
                    click: () => {
                      that.toDelete(params.row)
                    }
                  }
                }, '删除')
              ])
            }
          }
        ],
        "loadFilter": function (data) {
          return data.data;
        }
      },
      picture: false,
      picUrl: '',
      imgUrl: '',
      robotId: '',
      robotList: [],
      currentRobot: {},
      mapList: []
    }
  },
  methods: {
    getSocket: function () {
      const socket = io('http://127.0.0.1:7003');
      this._socketio = socket;
      socket.on('connect', () => {
        console.log("连接成功")
        socket.emit("subscribe", ["RailInspection/imageData"]);
      });
      socket.on('disconnect', function () {
        console.log("连接失败")
      });
      socket.on('RailInspection/imageData', (data) => {
        this.imgUrl = data;
      })
    },
    toDelete: function (obj) {
      console.log("obj", obj)
      let that = this;
      this.$Modal.confirm({
        title: '删除',
        content: '您确认删除此条数据？',
        onOk: function () {
          console.log("11111", obj.id);
          var idValue = obj.id;
          var url = "/service_rms/railInspection/delete";
          this.$ajax.post(url, { 'id': idValue })
            .then((res) => {
              if (res.data.code == '200') {
                setTimeout(() => {
                  that.$Modal.success({
                    title: '操作成功',
                  })
                }, 500)
                that.search()
              } else {
                setTimeout(() => {
                  that.$Modal.warning({
                    title: '操作失败',
                  })
                }, 500)
              }
            })
            .catch((error) => {
              if (!error.url) { console.info(error); }
            })
        }
      })
    },
    search: function () {
      console.log("sada", JSON.stringify(this.inspection))
      this.$refs.grid.load({ 'data': JSON.stringify(this.inspection) });
    },
    agvAction: function (code) {
      if (this.statusActive == 1) {
        console.log(code)
      } else {
        this.$Message.warning("请切换手动状态");
      }
    },
    agvMouseUp: function () {
      //点动状态鼠标抬起调用
      if (this.typeActive == 0 && this.statusActive == 1) {
        this.agvPause();
      }
    },
    agvPause: function () {
      if (this.statusActive == 1) {
        //agv停止接口
        console.log('agv停止')
      } else {
        this.$Message.warning("请切换手动状态");
      }
    },
    agvCharge: function () {
      if (this.statusActive == 1) {
        console.log('agv充电')
      } else {
        this.$Message.warning("请切换手动状态");
      }
    },
    ptzAction: function (code) {
      if (this.statusActive == 1) {
        console.log(code)
        var url = "/service_rms/railInspection/PTZController";
        this.$ajax.post(url, { 'flag': code })
          .then((res) => {
            if (res.data.code == '200') {
            } else {
              setTimeout(() => {
                that.$Modal.warning({
                  title: '操作失败',
                })
              }, 500)
            }
          })
          .catch((error) => {
            if (!error.url) { console.info(error); }
          })
      } else {
        this.$Message.warning("请切换手动状态");
      }
    },
    ptzMouseUp: function () {
      //点动状态鼠标抬起调用
      if (this.typeActive == 0 && this.statusActive == 1) {
        this.ptzAction(8);
      }
    },
    ptzPause: function () {
      if (this.statusActive == 1) {
        //agv停止接口
      } else {
        this.$Message.warning("请切换手动状态");
      }
    },
    ptzZero: function () {
      if (this.statusActive == 1) {
        console.log('回零')
        var url = "/service_rms/railInspection/toPresetPointControl";
        this.$ajax.post(url)
          .then((res) => {
            if (res.data.code == '200') {
            } else {
              setTimeout(() => {
                that.$Modal.warning({
                  title: '操作失败',
                })
              }, 500)
            }
          })
          .catch((error) => {
            if (!error.url) { console.info(error); }
          })
      } else {
        this.$Message.warning("请切换手动状态");
      }
    },
    // 机器人切换
    selectRobot: function (e) {
      this.currentRobot = this.robotList.find(j => j.vehicleId == e);
      this.robotId = e;
    },
    // 获取机器人信息
    getAGVPoint: function () {
      this.$ajax.post('/service_rms/robotRealTime/runInfo')
        .then((res) => {
          this.robotList = res.data.data;
          if (this.robotList.length > 0) {
            this.robotId = this.robotList[0].vehicleId;
            this.currentRobot = this.robotList[0];
          }
        })
    },
    getAGVMap: function () {
      this.$ajax.post('/service_rms/railInspection/getMap', { vehicleId: this.robotId })
        .then((res) => {
          this.mapList = res.data.data;
          setTimeout(() => {
            this.drawMap();
          }, 500);
        })
    },
    drawMap: function () {
      var element = this.$refs.map;
      var width = element.clientWidth;
      var height = element.clientHeight;
      this.$refs.canvas.width = width;
      this.$refs.canvas.height = height;
      this.$refs.point.width = width + 60;
      this.$refs.point.height = height + 60;
      //左上角（20，20）       右上角（width-20,20）
      //左下角（20，height-20）右下角（width-20,height-20）
      //半径 (height-20)/2
      let breaks = width - height / 2;
      var ctx = this.$refs.canvas.getContext('2d');
      var pCtx = this.$refs.point.getContext('2d');

      ctx.lineWidth = 10;
      ctx.strokeStyle = '#D5DAE0';
      ctx.lineJoin = 'round';

      ctx.beginPath();
      ctx.moveTo(20, 20);
      // 绘制U型线左侧的直线
      ctx.lineTo(breaks, 20);//上拐角
      // arc() 方法的参数依次为：圆心x坐标、圆心y坐标、半径、起始角度、结束角度、是否逆时针
      ctx.arc(breaks, height / 2, (height - 40) / 2, -Math.PI / 2, Math.PI / 2, false);
      ctx.lineTo(20, height - 20);
      pCtx.font = '18px Arial';
      pCtx.textAlign = 'left';
      ctx.stroke();
      //总长width-40
      this.mapList.push(
        // { name: 1, value: 0, status: 0 },
        // { name: 1, value: 10, status: 1 },
        // { name: 1, value: 20, status: 2 },
        // { name: 1, value: 30, status: 3 },
        // { name: 1, value: 33, status: 2 },
        // { name: 1, value: 34, status: 0 },
        // { name: 1, value: 34.5, status: 2 },
        // { name: 1, value: 35, status: 1 },
        // { name: 1, value: 35.5, status: 2 },
        // { name: 1, value: 36, status: 0 },
        // { name: 1, value: 37, status: 3 },
        // { name: 1, value: 40, status: 0 },
        // { name: 1, value: 50, status: 2 },
        // { name: 1, value: 60, status: 2 },
        // { name: 1, value: 70, status: 1 }
      )
      let maxValue = Math.max(...this.mapList.map(item => item.value));
      let multi = (2 * width - 80) / maxValue;

      const that = this;
      this.mapList.forEach((j, i) => {
        // 确保图片加载完成后再进行绘制
        let noImage = new Image()
        noImage.src = "./static/images/patrol/monitor/" + j.status + ".png";
        noImage.onload = function () {
          let p = j.value * multi;
          if (p < breaks - 20) {
            ctx.drawImage(noImage, p, 5);
            pCtx.fillText(j.name, p+30, 30);
          } else if (p >= breaks - 20 && p <= width - 40) {
            let y = that.getCircleEquation(breaks, height/ 2, (height - 40) / 2, p+20);
            ctx.drawImage(noImage, p+5, y[1]-15);
            pCtx.fillText(j.name, p+40, y[1]+10);
          } else if (p > width - 40 && p < width + height / 2 - 40) {
            let x = 2 * width - 80 - p;
            let y = that.getCircleEquation(breaks, height / 2, (height - 40) / 2, x+20);
            ctx.drawImage(noImage, x+5, y[0]-15);
            pCtx.fillText(j.name, x+5, y[0]+10);
          } else {
            ctx.drawImage(noImage, 2 * width - 80 - p, height - 35);
            pCtx.fillText(j.name, 2 * width - 80 - p+30, height - 10);
          }
        };
      })
    },
    getCircleEquation (h, k, radius, x) {
      const rSquared = radius * radius;
      // 计算 (x - h)^2 的值，即圆方程的一部分
      const xPart = (x - h) * (x - h);
      // 检查给定的x值是否在圆的定义域内
      if (xPart > rSquared) {
        // 如果 (x - h)^2 大于 r^2，则该x值对应的圆上没有y坐标
        return null;
      }
      // 计算 y 坐标可能的值
      const yDelta = Math.sqrt(rSquared - xPart);
      const y1 = k + yDelta; // 圆上半部分的y坐标
      const y2 = k - yDelta; // 圆下半部分的y坐标

      // 返回两个可能的y坐标值
      return [y1, y2];
    }
  },
  mounted () {
    this.search();
    this.getSocket()
    this.getAGVPoint();
  },
  watch: {
    robotId: function () {
      if (this.robotId) {
        this.getAGVMap();
      }
    }
  },
  beforeDestroy () {
    this._socketio = null;
  }
}
</script>
<style scoped>
.page-content {
  padding: 10px 20px;
  color: #323b4f;
}
.dashed-border {
  border: dashed #8acce8 1px;
}
.page-content /deep/ .ivu-tabs-nav .ivu-tabs-tab-active {
  color: #3787e5;
}
.page-content /deep/ .ivu-tabs-bar {
  border-bottom: 2px solid #d5e4fc;
  margin-bottom: 0;
}
.page-content /deep/ .ivu-tabs-content {
  height: 80%;
}
.control {
  border-radius: 4px;
  padding: 5px 0;
  text-align: center;
  background-color: #92bcef;
  color: #353c3e;
  cursor: pointer;
  margin: 0 auto;
  width: 100%;
}
.blue-gradient {
  background-image: linear-gradient(-90deg, #00b0f9 0%, #3b82e4 100%), linear-gradient(#ffffff, #ffffff);
  background-blend-mode: normal, normal;
  color: white;
}
.yellow-gradient {
  background-image: linear-gradient(90deg, #f5952d 0%, #fee68d 100%), linear-gradient(#92bcef, #92bcef);
  background-blend-mode: normal, normal;
  border-radius: 4px;
}
.white-border {
  border: solid white 2px;
  border-radius: 4px;
  height: 100%;
  padding: 10px;
  text-align: center;
}
.robot-block {
  background: white;
  margin: 5px 10px;
  border-radius: 4px;
  padding: 5px 10px;
  text-align: center;
}
.robot-block div {
  margin: 3px 0;
  display: flex;
  align-items: center;
  justify-content: space-evenly;
  color: #0199f9;
  font-weight: bold;
}
.robot-block img {
  cursor: pointer;
}
/* 表格样式 */
.page-content /deep/ .ivu-table th {
  background-color: #d5e4fc;
  color: #4b5975;
}
.page-content /deep/ .ivu-table-row-highlight td,
.page-content /deep/ .ivu-table-stripe .ivu-table-body tr.ivu-table-row-highlight:nth-child(2n) td,
.page-content /deep/ .ivu-table-stripe .ivu-table-fixed-body tr.ivu-table-row-highlight:nth-child(2n) td,
.page-content /deep/ tr.ivu-table-row-highlight.ivu-table-row-hover td {
  background-color: rgba(251, 202, 108, 0.1);
}
.page-content /deep/ .ivu-table {
  color: #323b4f;
  cursor: default;
}
.page-content /deep/ .ivu-table td,
.page-content /deep/ .ivu-table th {
  border-bottom: none;
}
.page-content /deep/ .ivu-table table {
  border-bottom: #d5e4fc solid 1px;
}
.page-content /deep/ .ivu-table-border td {
  border-right: #d5e4fc solid 1px;
}
.page-content /deep/ .ivu-table-border th {
  border-right: #fff solid 1px;
  font-weight: normal;
  color: #4b5975;
}
.page-content /deep/ .ivu-table-wrapper-with-border {
  border: none;
}
.page-content /deep/ .ivu-table-border:after {
  background-color: #fff;
}
.page-content /deep/ .ivu-table-stripe .ivu-table-body tr:nth-child(2n) td,
.page-content /deep/ .ivu-table-stripe .ivu-table-fixed-body tr:nth-child(2n) td {
  background-color: #fff;
}

.page-content /deep/ .ivu-table-stripe .ivu-table-body tr.ivu-table-row-hover td,
.page-content /deep/ .ivu-table-stripe .ivu-table-fixed-body tr.ivu-table-row-hover td {
  background-color: rgba(251, 202, 108, 0.1);
}
.flex-column {
  display: flex;
  align-items: center;
  flex-direction: column;
  justify-content: space-around;
}
.flex-row {
  display: flex;
  align-items: center;
  justify-content: space-around;
}
.number-size {
  font-weight: bold;
  font-size: 22px;
  color: #01b2f9;
  margin-left: 20px;
}
.video-span {
  position: absolute;
  bottom: 10px;
  width: calc(100% - 20px);
  height: 35px;
  background-color: rgba(0, 0, 0, 0.6);
  color: white;
  text-align: right;
  line-height: 35px;
  padding: 0 10px;
}
</style>