<template>
  <div class="page-content" style="background:#efefef;padding:0;overflow: hidden;position: relative;">
    <TopologyEditor ref="topo" :editable="editable" @penClick="penClick"></TopologyEditor>
    <div class="robot-info-search">
      <!-- <Button type="primary" style="margin-right: 10px;" @click="search = !search">搜索</Button> -->
      <!-- <Button type="primary" style="margin-right: 10px;" @click="unity = true">Unity</Button> -->
      <div style="display: flex;">
        <div @click="infoProps = true;" style="background-color: #27a9e3;height: 30px;cursor:pointer;">
          <img :src="'./static/images/editor/agv-w.png'" style="width:28px;margin: 0 5px;" />
        </div>
        <Select slot="prepend" v-model="navRobot" style="width:120px;" placeholder="选择机器人">
          <Option v-for="item in robotsInfo" :value="item.vehicleId" :key="item.vehicleId">{{ item.vehicleId }}</Option>
        </Select>
        <div @click="navTo" style="background-color: #27a9e3;height: 30px;line-height: 30px;color: white;padding: 0 15px;cursor: pointer;">导航</div>
        <div @click="switchBg(!bkImage)"
             style="text-align: center;cursor:pointer;height: 30px;width:30px;border: #27a9e3 solid 1px;border-radius: 2px;margin-left: 10px;background-color: white;">
          <img :src="'./static/images/editor/bgC.png'" style="width:24px;margin-top: 2px;" />
        </div>
      </div>
      <div class="robot-info-box" v-if="!infoProps">
        <!-- <div class="info-box-title" style="display: flex;align-items: center;">
          <span>属性</span>
          <Select v-if="activePoint.instanceName" v-model="navRobot" style="width:100px;margin-left: 10px;" placeholder="选择机器人">
            <Option v-for="item in robotsInfo" :value="item.vehicleId" :key="item.vehicleId">{{ item.vehicleId }}</Option>
          </Select>
          <Button v-if="activePoint.instanceName" style="margin-left: 10px;border-radius: 10px;" type="primary" @click="navTo">导航到此站点</Button>
        </div> -->
        <div class="info-box-content" v-if="location">
          <Row style="width:100%;border-bottom: #bcc0c4 solid 1px;margin-bottom: 5px;">
            <div style="border-bottom: #27a9e3 solid 2px;width:50px;text-align: center;color: #27a9e3;padding-bottom: 5px;margin-bottom: -2px;">站点
            </div>
          </Row>
          <Row>
            <Col span="8">ID:</Col>
            <Col span="16">{{ activePoint.instanceName }}</Col>
          </Row>
          <Row>
            <Col span="8">名称:</Col>
            <Col span="16">{{ activePoint.instanceName }}</Col>
          </Row>
          <Row>
            <Col span="8">位置:</Col>
            <Col span="16">{{ activePoint.pos.x }}m,{{ activePoint.pos.y }}m</Col>
          </Row>
        </div>
        <div class="info-box-content" v-if="robot">
          <Row style="width:100%;border-bottom: #bcc0c4 solid 1px;margin-bottom: 5px;">
            <div style="border-bottom: #27a9e3 solid 2px;width:130px;text-align: center;color: #27a9e3;padding-bottom: 5px;margin-bottom: -2px;">机器人
              {{ agvPoint.vehicleId }}</div>
          </Row>
          <RobotInfo :agvPoint="agvPoint"></RobotInfo>
          <!-- <Row style="font-weight: bold;margin-bottom: 5px;">机器人{{ agvPoint.vehicleId }}</Row> -->
        </div>
      </div>
    </div>
    <div class="robot-info-button">
      <!-- <Button type="primary" style="margin-right: 10px;" @click="agvControl">抢占控制权</Button> -->
      <!-- <Icon v-if="infoProps" type="ios-information-circle-outline" size="32" class="info-props" @click="infoProps = false" /> -->
      <!-- <Icon v-if="!infoProps" type="ios-information-circle" size="32" class="info-props-active" @click="infoProps = true" /> -->
      <div style="position: relative;width: 300px;" id="searchDiv">
        <Input v-model="searchValue" :border="false" :enter-button="true" search placeholder="search..." @on-search="searchAll"
               @on-focus="searchTabsShow" />
        <Icon v-if="searchValue" @click="searchValue='';searchTypeTabs(searchType)" type="md-close-circle"
              style="cursor: pointer;position: absolute;right: 58px;top: 9px;z-index: 3;" />
        <!--搜索-->
        <div v-if="search" class="robot-info-point">
          <div class="warning-title search-title">
            <div @click="searchTypeTabs('robot')" class="name" ref="robotTab" :class="searchType=='robot'?'warning':''">
              机器人
            </div>
            <div @click="searchTypeTabs('point')" class="name" :class="searchType=='point'?'warning':''">
              站点
            </div>
            <div @click="searchTypeTabs('path')" class="name" :class="searchType=='path'?'warning':''">
              路径
            </div>
          </div>
          <div style="overflow-y: auto;height: 300px;margin: 10px 0;" class="warning-panel">
            <div v-if="searchList.length>0" style="display: inline-block;width:100%;" class="warning">
              <Row v-for="(item,index) in searchList" :key="index" style="cursor:pointer;width:100%;padding: 5px 0;" :style="getStyle(item)">
                <Col span="2">
                <Icon type="md-arrow-dropright" size="18" />
                </Col>
                <Col span="16">
                <div>{{ searchType=='robot'?item.vehicleId:item.instanceName.replace('-','→') }}</div>
                </Col>
                <Col span="2">
                <Icon type="ios-locate-outline" size="18" @click="searchPoint(searchType,item)" style="font-weight: bold;" />
                </Col>
                <Col span="3">
                <div @click="searchPoint(searchType,item)">定位</div>
                </Col>
              </Row>
            </div>
            <div v-else style="width:100%;text-align: center;">
              暂无数据
            </div>
          </div>
        </div>
      </div>
    </div>
    <Row class="robot-info-footer" style="position: relative;">
      <Col span="12">
      <div class="info-footer-online" @click="getOnlineRobotList()">
        <div class="info-online"></div>
        <span style="margin: 0 10px;">机器人</span>
        <span>在线：{{ onlineRobots }}/{{ allRobots }}</span>
      </div>
      </Col>
      <Col span="12" style="text-align: right;">
      <div style="display: inline-block;">
        <div style="display: flex;cursor: pointer;align-items: center;" @click="warning = !warning">
          <Icon type="ios-alert" size="18" :color="warningList.length>0?'red':''" />
          <span style="margin:0 20px 0 5px;" :style="{color:warningList.length>0?'red':''}">告警 ({{ warningList.length }})</span>
        </div>
      </div>
      </Col>
      <div v-if="warning"
           style="position: absolute;padding: 10px;bottom: 50px;right: 20px;width: 360px;height: 200px;background-color: white;user-select: none;">
        <div class="warning-title">
          告警（{{warningList.length}}）
        </div>
        <div style="overflow-y: auto;height: 140px;margin: 10px 0;" class="warning-panel">
          <Collapse simple accordion>
            <Panel v-for="(item,index) in warningList" :key="index">
              <div style="display: inline-block;width:90%;" class="warning">
                <div style="width:100%;display: flex;align-items: center;justify-content: space-between;">
                  <div style="width: 150px;overflow: hidden;text-overflow:ellipsis;white-space: nowrap;">
                    {{ item.msg }}</div>
                  <div>{{item.time}}</div>
                </div>
              </div>
              <p slot="content">{{ item.msg }}</p>
            </Panel>
          </Collapse>
        </div>
      </div>
    </Row>
    <!--机器人在线-->
    <div v-if="online" class="robot-info-online">
      <Row v-for="item in robotsOnline" :key="item.vehicleId" style="padding: 3px;">
        <Col span="2">
        <div class="info-online" :class="item.zaiXian ? 'info-green' : 'info-red'"></div>
        </Col>
        <Col span="10">
        <div style="margin: 0 10px;">{{ item.vehicleId }}</div>
        </Col>
        <Col span="3">
        <Battery ref="battery" :option="batteryOption" :percent="item.batteryLevel"></Battery>
        </Col>
        <Col span="3">
        <div>{{ item.batteryLevel }}</div>
        </Col>
        <Col span="5">
        <div :style="{ backgroundColor: item.confidence < confidenceLimit ? '#F8A241' : '#5ED268' }"
             style="background-color: #5ED268;color: white;border-radius: 5px;padding: 0 4px;margin: 0 10px;">{{
            (item.confidence - 0).toFixed(2) }}</div>
        </Col>
      </Row>
    </div>
  </div>
</template>

<script>
import TopologyEditor from '@/components/meta2d/topologyEditor.vue';
import Battery from '@/components/icon/battery.vue';
import RobotInfo from './robotInfo.vue'
import math from '@/libs/arithmetic.js'
import config from "@/config";
const { subtract, multiply, divide } = math;
var vm;

export default {
  name: 'home',
  components: { TopologyEditor, Battery, RobotInfo },
  data () {
    return {
      searchValue: '',
      searchType: 'robot',
      editable: false,
      pointsName: [],
      pointsList: [],
      linesList: [],
      agv: {
        tags: 'AGV',
        name: 'image',
        iconFamily: 'iconfont',
        icon: '\ue60c',
        //image:'static/images/editor/agv.png',
        // iconFamily:'FontAwesome',
        // icon:'\uf17b',
        width: 7,
        height: 7,
        // info: {
        //   pos: {
        //     x: 0,
        //     y: 0
        //   }
        // }
      },
      infoProps: true,
      activePoint: {
        pos: {
          x: 0,
          y: 0
        }
      },
      extraPoint: {
        tags: 'extra',
        name: 'image',
        iconFamily: 'iconfont',
        icon: '\ue60c',
        width: 7,
        height: 7,
        info: '',
      },
      agvPoint: {},
      navRobot: '',
      location: false,
      robot: false,
      robotsInfo: [],
      robotsOnline: [],
      currentRobot: null,
      allRobots: 0,
      onlineRobots: 0,
      online: false,
      navigate: false,
      navigateStation: null,
      batteryOption: {
        style: {
          width: '28px',
          height: '16px'
        },
        highColor: '#5ED268',
        lowColor: '#F8A241',
        isCharging: false,
        limit: 50
      },
      percent: 0,
      confidenceLimit: 0.8,
      warning: false,
      warningList: [],
      columns: [
        {
          title: '时间',
          key: 'time'
        },
        {
          title: '告警内容',
          key: 'msg'
        }
      ],
      search: false,
      selectPoint: '',
      searchList: [],
      imageIcons: {
        "parkPoint": "\ue6f1",
        "dmsParkPoint": "\ue6f1",
        "chargePoint": "\ue6f2",
        "dmsPoint": "\ue716"
      },
      navRobotList: [],
      bkImage: false,
    }
  },
  methods: {
    getMap: function () {
      console.log("开始获取地图")
      let that = this;
      this.$ajax.post('/service_rms/robotBasic/downloadMap')
        .then((res) => {
          this.getWarningList(res.data);
          that.analyze(res.data.data)
          setTimeout(() => {
            this.getAGVPoint();
          }, 2000)
        })
    },
    getAGVPoint: function () {
      this.$ajax.post('/service_rms/robotRealTime/runInfo')
        .then((res) => {
          this.getWarningList(res.data);
          this.robotsInfo = JSON.parse(JSON.stringify(res.data.data));
          this.selectList = this.robotsInfo;
          this.robotsOnline = res.data.data;
          this.showAGVInfo(this.robotsInfo);
          this.addPens(res.data.data, this.agv, "agv");
          //重复修改位置！！！！
          this.addPens(res.data.data, this.agv, "agv");
          this.searchTypeTabs(this.searchType);
        })
    },
    getSocket: function () {
      console.log("开始连接")
      const socket = io(config.socketServer);
      this._socketio = socket;
      socket.on('connect', () => {
        console.log("连接成功")
        socket.emit("subscribe", ["AGV/businessData"]);
      });
      socket.on('disconnect', function () {
        console.log("连接失败")
      });
      socket.on('AGV/businessData', (data) => {
        let res = JSON.parse(data);
        switch (res.code) {
          case "robot_push_less_res":
            this.addPens(res.data, this.agv, "agv");
            // this.robotsInfo = this.mergeObjectArrays(this.robotsInfo, res.data);
            this.robotsInfo = JSON.parse(JSON.stringify(res.data))
            this.searchTypeTabs(this.searchType);
            console.log("robot_push",res.data)
            this.showAGVInfo(this.robotsInfo);
            //判断导航是否完成
            this.robotsInfo.map(j => {
              let result = this.navRobotList.find(item => item.vehicleId === j.vehicleId);
              if (result && j.currentStation == result.endPort && j.taskStatus == "导航完成") {
                this.$Message.success({
                  content: j.vehicleId + "导航完成",
                  duraton: 2000
                });
                this.navRobotList.splice(this.navRobotList.findIndex(item => item.vehicleId === j.vehicleId), 1);
              }
            })
            break;
          case "robot_config_downloadmap_res":
            this.analyze(res.data)
            break;
        }
      })
    },
    analyze: function (data) {
      console.log("mapdata",data)
      this.pointsList = [];
      // let color = ['rgba(117,173,245,0.3)', 'rgba(239,160,93,0.3)'];
      // let icons = ['\ue616', '\uf068'];
      let color = ['rgba(239,160,93,0.3)', 'rgba(117,173,245,0.3)', 'rgba(129,201,110,0.3)', 'rgba(177,159,236,0.3)'];
      let icons = ['\uf068', '\ue616', '\uf069', '\uf06b'];
      let map = {
        "scale": 1,
        "locked": 2,
        "rule": true, //标尺
        "pens": [
          {
            "name": "image",
            "id": 'bg',
            "width": 431.4 * 0.85,
            "height": 282.6 * 0.69,
            "image": 'static/images/editor/bg0.png',
            "isBottom": true,
            "imageRatio": false,
            "children": [],
            "x": -120,
            "y": -96,
            "zIndex": -1,
          }
        ],
        "grid": true,
        "paths": {},
        "background": "#F2F3F6",
        "version": "1.0.12"
      }
      data.map((j, index) => {
        let that = this;
        let jsonData = JSON.parse(j);
        let points = jsonData.advancedPointList;
        let lines = jsonData.advancedCurveList;
        lines.map(n => {

          let x = multiply(n.startPos.pos.x, 10);
          let y = multiply(-n.startPos.pos.y, 10);
          let ex = multiply(n.endPos.pos.x, 10);
          let ey = multiply(-n.endPos.pos.y, 10);
          console.log("n.controlPos1",n.controlPos1)

          let cx1,cy1,cx2,cy2;
          if (n.className == 'StraightPath') {
            cx1 = x;
            cy1 = y;
            cx2 = x;
            cy2 = y;
          } else {
            cx1 = multiply(n.controlPos1.x, 10);
            cy1 = multiply(-n.controlPos1.y, 10);
            cx2 = multiply(n.controlPos2.x, 10);
            cy2 = multiply(-n.controlPos2.y, 10);
          }

          let nextx = divide(subtract(cx1, x), subtract(ex, x));
          let nexty = divide(subtract(cy1, y), subtract(ey, y));

          let prevx = divide(subtract(cx2, x), subtract(ex, x));
          let prevy = divide(subtract(cy2, y), subtract(ey, y));

          let pen = {
            name: 'line',
            lineName: "curve",
            lineWidth: 1,
            type: 1,
            id: n.instanceName,
            color: color[index],
            width: subtract(ex, x),
            height: subtract(ey, y),
            x: x,
            y: y,
            ex: ex,
            ey: ey,
            locked: 10,
            anchors: [
              {
                x: 0,
                y: 0,
                next: {
                  x: nextx,
                  y: nexty
                }
              },
              {
                x: 1,
                y: 1,
                prev: {
                  x: prevx,
                  y: prevy
                }
              }
            ],
          }
          map.pens.push(pen)
        })
        points.map(k => {
          if (!that.pointsName.includes(k.instanceName)) {
            that.pointsName.push(k.instanceName);
            let pen = {
              name: 'image',
              id: k.instanceName,
              //text:k.instanceName,
              iconFamily: 'iconfont',
              activeColor: '#1890ff',
              width: 5,
              height: 5,
              fontSize: 2,
              locked: 3,
              icon: icons[index],
              // icon:'\ue616',
              type: 0,
              tags: 'point',
              //图标偏移
              x: k.pos.x * 10 - 2.5,
              y: -k.pos.y * 10 - 2.5,
              info: JSON.stringify(k),
              anchor: []
            }
            let label = {
              name: 'text',
              id: k.instanceName,
              text: k.instanceName,
              width: 2,
              height: 2,
              fontSize: 2,
              locked: 10,
              x: k.pos.x * 10,
              y: -k.pos.y * 10 + 2
            }
            map.pens.push(pen)
            map.pens.push(label)
          }
        });
        this.pointsList = this.mergeObjectArrays(this.pointsList, points);
        this.linesList = this.mergeObjectArrays(this.linesList, lines);
      })
      this.$refs.topo.drawData(map);
      this.$refs.topo.meta2d.fitView(true, 50);
      this.pointsName = [];
      this.extraPoints();
      this.switchBg(this.bkImage);
    },
    mergeObjectArrays (arr1, arr2) {
      let mergeArray = arr1;
      // 将arr2中的对象存入map
      arr2.map(obj => {
        let point = arr1.find(j => obj.instanceName == j.instanceName);
        if (!point) {
          mergeArray.push(obj)
        }
      });
      return mergeArray;
    },
    //背景图切换
    switchBg: function (flag) {
      const pen = this.$refs.topo.meta2d.findOne('bg');
      this.bkImage = flag;
      this.$refs.topo.meta2d.setVisible(pen, flag);
    },  
    addPens: function (data, pen, tag) {
      let finds = this.$refs.topo.getByTag(tag);
      if (finds.length > 0) {
        finds.map(j => {
          data.map(i => {
            if (tag == 'agv' && i.vehicleId === j.id) {
              this.$refs.topo.setPenRect(j, { x: i.x * 10 - 3.5, y: -i.y * 10 - 3.5, width: 7, height: 7 });
              return;
            } else if (tag == 'extra') {
              let info = JSON.parse(j.info);
              if (info.info == i.info) {
                this.$refs.topo.setPenRect(j, { x: i.x * 10 - 3.5, y: -i.y * 10 - 3.5, width: 7, height: 7 });
                return;
              }
            }
          });
        })
      } else {
        let pens = [];
        data.map(j => {
          let point = {
            id: j.vehicleId,
            tags: tag,
            name: pen.name,
            iconFamily: pen.iconFamily,
            image: pen.image,
            text: j.vehicleId,
            fontSize: 2,
            textBaseline: 'bottom',
            textTop: 2,
            width: pen.width,
            height: pen.height,
            x: j.x * 10 - 3.5,
            y: -j.y * 10 - 3.5,
            icon: pen.icon,
            info: JSON.stringify(j)
          }
          if (tag == 'extra') {
            point.icon = j.icon;
          }
          pens.push(point);
        })
        this.$refs.topo.addPens(pens);
      }
    },
    //添加特殊点
    extraPoints: function () {
      this.$ajax.post('/service_rms/robotBasic/allAttributedList')
        .then((res) => {
          let result = res.data.data;
          let pens = [];
          result.map(i => {
            let img = this.extraPoint;
            let point = this.pointsList.find(j => i.point == j.instanceName);
            if (point) {
              img.icon = this.imageIcons[i.attributeName];
              img.x = point.pos.x;
              img.y = point.pos.y + 0.7;
              img.info = point.instanceName;
              pens.push(JSON.parse(JSON.stringify(img)));
            }
          })
          let ex = this.$refs.topo.meta2d.find("extra");
          this.$refs.topo.meta2d.delete(ex);
          this.addPens(pens, this.extraPoint, "extra");
          this.addPens(pens, this.extraPoint, "extra");
        })
    },
    penClick: function (data) {
      if (data.name == 'image') { //点击站点或机器人
        if (data.tags == 'agv') {
          this.agvPoint = JSON.parse(data.info);
          let result = this.robotsInfo.find(item => item.vehicleId === data.id);
          this.agvPoint = Object.assign(this.agvPoint, result);
          // this.agvPoint["model"] = result.model;
          // this.agvPoint["version"] = result.version;
          // this.agvPoint["currentIp"] = result.currentIp;
          // this.agvPoint["dspVersion"] = result.dspVersion;
          this.location = false;
          this.robot = true;
          this.currentRobot = this.agvPoint;
        } else if (data.info != undefined) {
          this.robot = false;
          this.location = true;
          this.activePoint = JSON.parse(data.info);
        }
        this.infoProps = false;
      } else { //点击区域
        /* data.dash = 2;
        data.lineDash = [10, 10];
        data.color = '#22C30C';
        data.lineWidth = 0.5;
        this.$refs.topo.addPen(data); */
        let points = this.$refs.topo.getByTag('point');
        points.map(j => {
          if (this.$refs.topo.pointInRect({ x: j.x, y: j.y }, data)) {
            console.log(j.id)
          }
        })
      }
    },
    getStatus: function (status) {
      switch (status) {
        case 0:
          return '定位失败';
        case 1:
          return '定位正确';
        case 2:
          return '正在重定位';
        case 3:
          return '定位完成';
      }
      return '';
    },
    showAGVInfo: function (data) {
      if (this.currentRobot != null) {
        let result = data.find(item => item.vehicleId === this.currentRobot.vehicleId);
        // console.log(result,this.currentRobot)
        this.agvPoint = Object.assign(this.currentRobot, result);
        // this.agvPoint["model"] = this.currentRobot.model;
        // this.agvPoint["version"] = this.currentRobot.version;
        // this.agvPoint["currentIp"] = this.currentRobot.currentIp;
        // this.agvPoint["dspVersion"] = this.currentRobot.dspVersion;
      }
    },
    getOnlineRobot: function () {
      this.$ajax.post('/service_rms/monitor/onlineInfo')
        .then((res) => {
          this.allRobots = res.data.data.allRobots;
          this.onlineRobots = res.data.data.onlineRobots;
          this.getWarningList(res.data);
        })
    },
    getOnlineRobotList: function () {
      this.online = !this.online;
      this.$ajax.post('/service_rms/robotRealTime/runInfo')
        .then((res) => {
          this.getWarningList(res.data);
          this.robotsOnline = res.data.data;
        })
    },
    agvControl: function () {
      this.$ajax.post('/service_rms/monitor/getControl', { "robotName": "W600DS_7_1" })
        .then((res) => {
          this.getWarningList(res.data);
        })
    },
    navTo: function () {
      if (this.navRobot != '') {
        let result = this.robotsInfo.find(item => item.vehicleId === this.navRobot);
        this.$ajax.post('/service_rms/monitor/runTask',
          {
            "robotName": this.navRobot,
            "beginPort": result.currentStation,
            "endPort": this.activePoint.instanceName
          })
          .then((res) => {
            if (res.data.code == 200) {
              this.$Message.success(this.navRobot + '开始导航');
              setTimeout(() => {
                let obj = {
                  vehicleId: this.navRobot,
                  endPort: this.activePoint.instanceName
                }
                this.navRobotList.push(obj);
                this.navRobot = '';
              }, 1000);
              // this.navigate = true;
              // this.navigateStation = this.activePoint.instanceName;
            }
            this.getWarningList(res.data);
          })
      } else {
        this.$Message.error('请选择机器人！');
      }
    },
    getWarningList: function (res) {
      if (res.code != 200) {
        let obj = {
          msg: res.msg,
          time: new Date().Format("yyyy-MM-dd hh:mm:ss"),
        }
        this.warningList.push(obj);
      }
    },
    searchPoint: function (type, item) {
      this.selectPoint = item;
      let res = this.$refs.topo.findOne(type == 'robot' ? item.vehicleId : item.instanceName);
      if (res) {
        this.$refs.topo.gotoCenter(res[0]);
      }
    },
    searchTabsShow: function () {
      this.search = true;
      this.searchTypeTabs(this.searchType);
    },
    searchTypeTabs: function (type) {
      this.searchType = type
      switch (type) {
        case 'robot':
          this.searchList = this.robotsInfo.filter(j => j.vehicleId.includes(this.searchValue));
          break;
        case 'point':
          this.searchList = this.pointsList.filter(j => j.instanceName.includes(this.searchValue));
          break;
        case 'path':
          this.searchList = this.linesList.filter(j => j.instanceName.includes(this.searchValue));
          break;
      }
    },
    getStyle: function (item) {
      return this.selectPoint[this.searchType == 'robot' ? 'vehicleId' : 'instanceName'] == (this.searchType == 'robot' ? item.vehicleId : item.instanceName) ? 'color:#27a9e3;font-weight:bold' : ''
    }
  },
  mounted () {
    this.getSocket();
    this.getMap();
    this.getOnlineRobot();
    vm = this;
  },
  created () {

  },
  beforeDestroy () {
    if (this._socketio) {
      this._socketio.disconnect();
    }
  }
}

document.addEventListener("click", function (e) {
  var div = document.getElementById("searchDiv");
  if (vm.search && !div.contains(e.target)) {
    vm.search = false;
  }
});
</script>

<style scoped>
/* 告警信息 */
/* 修改滚动条颜色 */
::-webkit-scrollbar {
  width: 4px;
  background-color: #f5f5f5;
}

/* 修改滚动条上下箭头和小圆圈的颜色 */
::-webkit-scrollbar-thumb {
  background-color: #cbd3dd;
}

/* 修改滚动条悬停时的颜色 */
::-webkit-scrollbar-thumb:hover {
  background-color: #999;
}
.warning-panel /deep/ .ivu-collapse > .ivu-collapse-item > .ivu-collapse-header > i {
  margin-right: 5px;
}
.warning-panel /deep/ .ivu-collapse-content > .ivu-collapse-content-box {
  padding-bottom: 0;
  line-height: 1.5;
}
.warning-panel /deep/ .ivu-collapse,
.warning-panel /deep/ .ivu-collapse .ivu-collapse-item {
  border: none;
}
.warning-panel /deep/ .ivu-collapse-item-active .warning,
.warning-panel /deep/ .ivu-collapse-item-active .ivu-icon-ios-arrow-forward:before {
  color: #ed5f5a !important;
}
.warning-panel /deep/ .ivu-collapse > .ivu-collapse-item > .ivu-collapse-header {
  padding-left: 0;
}
.warning-header {
  display: flex;
  justify-content: space-evenly;
  margin-bottom: 5px;
  z-index: 9999;
  position: relative;
}
.warning-header-title {
  cursor: pointer;
  width: 33%;
  padding: 5px 0;
  text-align: center;
}
.warning-active {
  background: #cbd3dd;
  color: #29456a;
}
.warning-title {
  width: 100%;
  text-align: center;
  padding: 5px 0;
  line-height: 1.5;
  cursor: pointer;
  color: #ed5f5a;
  border-bottom: #ed5f5a solid 2px;
}
.search-title {
  display: flex;
  color: #373737;
  border-bottom: none;
}
.search-title .name {
  color: #373737;
  width: 33%;
  border-bottom: #bcc0c4 solid 1px;
  text-align: center;
}
.search-title .warning {
  color: #27a9e3;
  border-bottom: #27a9e3 solid 2px;
}
.robot-info-button {
  position: absolute;
  right: 20px;
  top: 20px;
  display: flex;
  align-items: center;
}

.info-props,
.info-props-active {
  cursor: pointer;
}

.info-props:hover,
.info-props-active {
  color: #6495ed;
}

.robot-info-box {
  position: absolute;
  left: 0px;
  top: 40px;
  width: 260px;
  background-color: #fff;
  max-height: 680px;
  overflow-y: auto;
  overflow-x: hidden;
}

.info-box-title {
  border-bottom: #ccc solid 1px;
  padding: 10px;
}

.info-box-content {
  padding: 10px;
}

.robot-info-footer {
  height: 50px;
  line-height: 50px;
  background-color: #ccd4df;
  padding: 0 20px;
  user-select: none;
  color: #404c5e;
}

.info-online {
  width: 20px;
  height: 20px;
  line-height: 30px;
  border-radius: 50%;
  background: linear-gradient(to bottom, #86ef77, #59ce66);
  display: inline-block;
}

.info-green {
  background: linear-gradient(to bottom, #86ef77, #59ce66);
}

.info-red {
  background: linear-gradient(to bottom, #ed989a, #ff0000);
}

.info-footer-online {
  display: flex;
  align-items: center;
  cursor: pointer;
  width: 170px;
}

.robot-info-online {
  position: absolute;
  bottom: 50px;
  left: 0px;
  width: 300px;
  padding: 13px 0 10px 10px;
  background-color: #fff;
  /* box-shadow: 0 3px 6px -4px rgba(0, 0, 0, 0.12), 0 6px 16px 0 rgba(0, 0, 0, 0.08), 0 9px 28px 8px rgba(0, 0, 0, 0.05); */
}

.info-online-list {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 10px;
}

.robot-info-battery {
  width: 30px;
  display: flex;
  align-items: center;
  margin-right: 3px;
}

div /deep/ .warning-table .ivu-table th {
  background-color: #c8daf7;
}

.robot-info-search {
  position: absolute;
  top: 20px;
  left: 20px;
}

.robot-info-search /deep/ .ivu-select-selection {
  border: white;
}

.robot-info-point {
  position: absolute;
  top: 32px;
  right: 0;
  background-color: #fff;
  padding: 20px;
  max-height: 580px;
  overflow-y: auto;
  width: 300px;
}

.robot-point-radio {
  padding: 5px 0;
}
.robot-info-button /deep/ .ivu-input {
  border: none;
}
.robot-info-button /deep/ .ivu-input-search,
div /deep/ .ivu-input-search:hover {
  background: #27a9e3 !important;
  border-color: #27a9e3 !important;
}
</style>