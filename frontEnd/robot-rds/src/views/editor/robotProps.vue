<template>
  <div style="color: #29456A;background-color: #B0BAC6;user-select: none;">
    <div class="prop-header">
      <div @click="active=0" class="prop-header-title" :class="active == 0?'active':''">基本信息</div>
      <div @click="active=1" class="prop-header-title" :class="active == 1?'active':''">已绑定属性</div>
      <div @click="active=2" class="prop-header-title" :class="active == 2?'active':''">状态信息</div>
    </div>
    <!-- 基本信息区域 -->
    <div v-if="active==0">
      <!-- 信息概览 -->
      <div class="prop-info-robot">
        <Row>
          <Col span="8">机器人IP：</Col>
          <Col span="16">{{ currentPanel.currentIp }}</Col>
        </Row>
        <Row>
          <Col span="8">机器人名称：</Col>
          <Col span="16">{{ currentPanel.vehicleId }}</Col>
        </Row>
        <Row>
          <Col span="8">机器人备注：</Col>
          <Col span="16">{{ currentPanel.robotNote }}</Col>
        </Row>
        <Row>
          <Col span="8">机器人型号：</Col>
          <Col span="16">{{ currentPanel.model }}</Col>
        </Row>
        <Row>
          <Col span="8">Robokit版本：</Col>
          <Col span="16">{{ currentPanel.version }}</Col>
        </Row>
        <Row>
          <Col span="8">底层固件版本：</Col>
          <Col span="16">{{ currentPanel.dspVersion }}</Col>
        </Row>
        <Row>
          <Col span="8">当前地图：</Col>
          <Col span="16">{{ currentPanel.currentMap }}</Col>
        </Row>
        <Row>
          <Col span="8">MD5：</Col>
          <Col span="16">{{ currentPanel.currentMapMd5 }}</Col>
        </Row>
        <Row>
          <Col span="8">电量：</Col>
          <Col span="16">{{ currentPanel.batteryLevel }}</Col>
        </Row>
        <Row>
          <Col span="8">充电：</Col>
          <Col span="16">{{ currentPanel.charging ? '是' : '否' }}</Col>
        </Row>
        <Row>
          <Col span="8">置信度：</Col>
          <Col span="16">{{ currentPanel.confidence }}</Col>
        </Row>
      </div>
      <!-- 充电属性 -->
      <div class="prop-info-divider">
        <div>属性</div>
        <div style="background-color: #A0AEBD;width:85%;height:2px"></div>
      </div>
      <div class="prop-info-robot props">
        <Form :model="currentPanel" label-position="left" :label-width="100">
          <FormItem label="chargeOnly:">
            <InputNumber v-model="currentPanel.chargeOnly" style="width: auto" />
          </FormItem>
          <FormItem label="chargeNeed:">
            <InputNumber v-model="currentPanel.chargeNeed" style="width: auto" />
          </FormItem>
          <FormItem label="chargedOk:">
            <InputNumber v-model="currentPanel.chargeOk" style="width: auto" />
          </FormItem>
          <FormItem label="chargedFull:">
            <InputNumber v-model="currentPanel.chargeFull" style="width: auto" />
          </FormItem>
          <FormItem style="margin-top: 15px;text-align: right;margin-right: 35px;">
            <Button type="primary" @click="changeProperty">修改</Button>
          </FormItem>
        </Form>
      </div>
    </div>
    <!-- 已绑定属性 -->
    <div v-if="active==1">
      <Table :columns="propertyColumns" :data="property" class="warning-table"></Table>
    </div>
    <!-- 状态信息 -->
    <div v-if="active==2">
      <!-- <div class="prop-info-divider">
        <div>区域内机器人占用资源</div>
        <div style="background-color: #A0AEBD;width:51%;height:2px"></div>
      </div>
      <div class="prop-info-robot">
        <Row>
          <Col span="6">区域名：</Col>
          <Col span="18">12</Col>
        </Row>
        <Row>
          <Col span="6">绕行区域：</Col>
          <Col span="18">12</Col>
        </Row>
        <Row>
          <Col span="6">互斥组：</Col>
          <Col span="18">12</Col>
        </Row>
        <Row>
          <Col span="6">占用路径：</Col>
          <Col span="18">12</Col>
        </Row>
      </div> -->
      <div class="prop-info-divider">
        <div>机器人基本信息</div>
        <div style="background-color: #A0AEBD;width:64%;height:2px"></div>
      </div>
      <div class="prop-info-robot">
        <Row>
          <Col span="12">当前所在的区域：</Col>
          <Col span="12">{{  }}</Col>
        </Row>
        <Row>
          <Col span="12">当前所在机器人组：</Col>
          <Col span="12">{{ currentPanel.groupName }}</Col>
        </Row>
        <Row>
          <Col span="12">当前地图：</Col>
          <Col span="12">{{ currentPanel.currentMap }}</Col>
        </Row>
        <Row>
          <Col span="12">控制器湿度：</Col>
          <Col span="12">{{ currentPanel.controllerHumi }}%</Col>
        </Row>
        <Row>
          <Col span="12">控制器温度：</Col>
          <Col span="12">{{ currentPanel.controllerTemp }}℃</Col>
        </Row>
        <Row>
          <Col span="12">控制器电压：</Col>
          <Col span="12">{{ currentPanel.controllerVoltage }}V</Col>
        </Row>
        <Row>
          <Col span="12">底层固件版本：</Col>
          <Col span="12">{{ currentPanel.dspVersion }}</Col>
        </Row>
        <Row>
          <Col span="12">IP地址：</Col>
          <Col span="12">{{ currentPanel.currentIp }}</Col>
        </Row>
        <Row>
          <Col span="12">型号：</Col>
          <Col span="12">{{ currentPanel.model }}</Col>
        </Row>
        <Row>
          <Col span="12">备注：</Col>
          <Col span="12">{{ currentPanel.robotNote }}</Col>
        </Row>
        <Row>
          <Col span="12">版本：</Col>
          <Col span="12">{{ currentPanel.version }}</Col>
        </Row>
      </div>
      <div class="prop-info-divider">
        <div>机器人地图和模型文件变化</div>
        <div style="background-color: #A0AEBD;width:42%;height:2px"></div>
      </div>
      <div class="prop-info-robot">
        <Row>
          <Col span="4">地图：</Col>
          <Col span="20">{{ currentPanel.currentMap }}</Col>
        </Row>
        <Row>
          <Col span="4">MD5：</Col>
          <Col span="20">{{ currentPanel.currentMapMd5 }}</Col>
        </Row>
      </div>
      <!-- <div class="prop-info-divider">
        <div>机器人执行的当前运单</div>
        <div style="background-color: #A0AEBD;width:51%;height:2px"></div>
      </div>
      <div class="prop-info-robot">
        <Row>
          <Col span="6">运单信息：</Col>
          <Col span="18">12</Col>
        </Row>
        <Row>
          <Col span="6">候选单：</Col>
          <Col span="18">12</Col>
        </Row>
        <Row>
          <Col span="6">完成：</Col>
          <Col span="18">12</Col>
        </Row>
        <Row>
          <Col span="6">期限：</Col>
          <Col span="18">12</Col>
        </Row>
        <Row>
          <Col span="6">组：</Col>
          <Col span="18">12</Col>
        </Row>
        <Row>
          <Col span="6">id：</Col>
          <Col span="18">12</Col>
        </Row>
        <Row>
          <Col span="6">keyRoute：</Col>
          <Col span="18">12</Col>
        </Row>
        <Row>
          <Col span="6">信息：</Col>
          <Col span="18">12</Col>
        </Row>
        <Row>
          <Col span="6">优先权：</Col>
          <Col span="18">12</Col>
        </Row>
        <Row>
          <Col span="6">状态：</Col>
          <Col span="18">12</Col>
        </Row>
        <Row>
          <Col span="6">标签：</Col>
          <Col span="18">12</Col>
        </Row>
        <Row>
          <Col span="6">小车：</Col>
          <Col span="18">12</Col>
        </Row>
      </div> -->
      <div class="prop-info-divider">
        <div>机器人控制权详情</div>
        <div style="background-color: #A0AEBD;width:60%;height:2px"></div>
      </div>
      <div class="prop-info-robot">
        <Row>
          <Col span="14">控制权所有者描述：</Col>
          <Col span="10">{{ currentPanel.controlDesc }}</Col>
        </Row>
        <Row>
          <Col span="14">控制权所有者ip：</Col>
          <Col span="10">{{ currentPanel.controlIp }}</Col>
        </Row>
        <Row>
          <Col span="14">控制权是否被抢占：</Col>
          <Col span="10">{{ currentPanel.controlLocked }}</Col>
        </Row>
        <Row>
          <Col span="14">控制权所有者昵称信息：</Col>
          <Col span="10">{{ currentPanel.controlNickName }}</Col>
        </Row>
        <Row>
          <Col span="14">控制权所有者端口号：</Col>
          <Col span="10">{{ currentPanel.controlPort }}</Col>
        </Row>
        <Row>
          <Col span="14">抢占控制权的时间戳(s)：</Col>
          <Col span="10">{{ currentPanel.controlTimeT }}</Col>
        </Row>
        <Row>
          <Col span="14">控制权所有者类型：</Col>
          <Col span="10">{{ currentPanel.controlType }}</Col>
        </Row>
      </div>
      <div class="prop-info-divider">
        <div>rbk信息</div>
        <div style="background-color: #A0AEBD;width:81%;height:2px"></div>
      </div>
      <div class="prop-info-robot">
        <Row>
          <Col span="12">DI：</Col>
          <Col span="12">{{ currentPanel.di }}</Col>
        </Row>
        <Row>
          <Col span="12">DO：</Col>
          <Col span="12">{{ currentPanel.do }}</Col>
        </Row>
        <Row>
          <Col span="12">角度：</Col>
          <Col span="12">{{ currentPanel.angle }}</Col>
        </Row>
        <Row>
          <Col span="12">电池电量：</Col>
          <Col span="12">{{ currentPanel.batteryLevel }}%</Col>
        </Row>
        <Row>
          <Col span="12">是否被阻挡：</Col>
          <Col span="12">{{ currentPanel.blocked }}</Col>
        </Row>
        <Row>
          <Col span="12">是否抱闸：</Col>
          <Col span="12">{{ currentPanel.brake }}</Col>
        </Row>
        <Row>
          <Col span="12">电池是否正在充电：</Col>
          <Col span="12">{{ currentPanel.charging }}</Col>
        </Row>
        <Row>
          <Col span="12">定位置信度：</Col>
          <Col span="12">{{ currentPanel.confidence }}</Col>
        </Row>
        <Row>
          <Col span="12">电流：</Col>
          <Col span="12">{{ currentPanel.current }}</Col>
        </Row>
        <Row>
          <Col span="12">电压：</Col>
          <Col span="12">{{ currentPanel.voltage }}</Col>
        </Row>
        <Row>
          <Col span="12">当前地图：</Col>
          <Col span="12">{{ currentPanel.currentMap }}</Col>
        </Row>
        <Row>
          <Col span="12">当前所在的站点的id：</Col>
          <Col span="12">{{ currentPanel.currentStation }}</Col>
        </Row>
        <Row>
          <Col span="12">急停按钮状态：</Col>
          <Col span="12">{{ currentPanel.emergency }}</Col>
        </Row>
        <Row>
          <Col span="12">顶升机构状态</Col>
          <Col span="12">{{ currentPanel.jackStatus }}</Col>
        </Row>
        <Row>
          <Col span="12">控制权状态：</Col>
          <Col span="12">{{ currentPanel.controlStatus }}</Col>
        </Row>
        <Row>
          <Col span="12">导航状态：</Col>
          <Col span="12">{{ currentPanel.navigationStatus }}</Col>
        </Row>
        <Row>
          <Col span="12">定位状态：</Col>
          <Col span="12">{{ currentPanel.relocStatus }}</Col>
        </Row>
        <Row>
          <Col span="12">软急停状态：</Col>
          <Col span="12">{{ currentPanel.softEmc }}</Col>
        </Row>
        <Row>
          <Col span="12">当前的舵轮角度：</Col>
          <Col span="12">{{ currentPanel.steer }}</Col>
        </Row>
        <Row>
          <Col span="12">本次运行时间：</Col>
          <Col span="12">{{ currentPanel.time }}</Col>
        </Row>
        <Row>
          <Col span="12">累计运行时间：</Col>
          <Col span="12">{{ currentPanel.totalTime }}</Col>
        </Row>
        <Row>
          <Col span="12">今日累计行驶里程：</Col>
          <Col span="12">{{ currentPanel.todayOdo }}</Col>
        </Row>
        <Row>
          <Col span="12">累计行驶里程：</Col>
          <Col span="12">{{ currentPanel.odo }}</Col>
        </Row>
        <Row>
          <Col span="12">x方向实际的速度：</Col>
          <Col span="12">{{ currentPanel.vx }}</Col>
        </Row>
        <Row>
          <Col span="12">y方向实际的速度：</Col>
          <Col span="12">{{ currentPanel.vy }}</Col>
        </Row>
        <Row>
          <Col span="12">实际的角速度：</Col>
          <Col span="12">{{ currentPanel.w }}</Col>
        </Row>
        <Row>
          <Col span="12">x坐标：</Col>
          <Col span="12">{{ currentPanel.x }}m</Col>
        </Row>
        <Row>
          <Col span="12">y坐标：</Col>
          <Col span="12">{{ currentPanel.y }}m</Col>
        </Row>
      </div>
      <div class="prop-info-divider">
        <div>编号</div>
        <div style="background-color: #A0AEBD;width:86%;height:2px"></div>
      </div>
      <div class="prop-info-robot">
        <Row>
          <Col span="8">UUID：</Col>
          <Col span="16">{{ currentPanel.vehicleId }}</Col>
        </Row>
        <Row>
          <Col span="8">小车编号：</Col>
          <Col span="16">{{ currentPanel.vehicleId }}</Col>
        </Row>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  name: 'robotProps',
  props: ['currentPanel', 'property'],
  data () {
    var that = this;
    return {
      active: 0,
      property:[],
      propertyColumns: [
        {
          title: '图元',
          width: 70,
          key: 'point',
        },
        {
          title: '键',
          key: 'attributeName'
        },
        {
          title: '值',
          width: 40,
          key: 'attributeCode',
        },
        {
          key: 'id',
          title: '操作',
          width: 60,
          render (h, params) {
            return h('div', [
              h('Button', {
                props: {
                  type: 'primary',
                  size: 'small',
                  allow: '删除',
                },
                style: {
                },
                on: {
                  click: () => {
                    that.deleteRobotProp(params.row)
                  }
                }
              }, "删除"),
            ])
          }
        }
      ],
    }
  },
  methods: {
    //修改属性
    changeProperty: function () {
      this.$Modal.confirm({
        title: '确认修改属性值吗？',
        onOk: () => {
          this.$ajax.post('/service_rms/robotBasic/modifyAtt',
            {
              'vehicleId': this.currentPanel.vehicleId,
              'chargeOnly': this.currentPanel.chargeOnly,
              'chargeNeed': this.currentPanel.chargeNeed,
              'chargeOk': this.currentPanel.chargeOk,
              'chargeFull': this.currentPanel.chargeFull
            })
            .then((res) => {
              this.$emit("changeProperty", res.data);
            })
        }
      });
    },
    //删除机器人已绑定属性
    deleteRobotProp: function (e) {
      this.$Modal.confirm({
        title: '确认删除已绑定属性吗？',
        onOk: () => {
          this.$ajax.post('/service_rms/attribute/delete', { 'id': e.id })
            .then((res) => {
              this.$emit("deleteProperty", {
                res: res.data,
                data: this.currentPanel
              });
            })
        }
      });
    }
  },
  watch:{
  }
}
</script>
<style scoped>
.prop-header {
  display: flex;
  justify-content: space-evenly;
  margin-bottom: 5px;
  z-index: 1000;
  position: relative;
}
.prop-header-title {
  cursor: pointer;
  width: 33%;
  padding: 5px 0;
  text-align: center;
}
.active {
  background: #cbd3dd;
  color: #29456a;
}
.prop-info-robot {
  background: #cbd3dd;
  padding: 10px 20px;
}
.prop-info-robot /deep/ .ivu-form-item {
  margin-bottom: 0;
}
.props /deep/ .ivu-form-item {
  margin-bottom: 5px;
}
.prop-info-robot /deep/ .ivu-input-number-input,
.prop-info-robot /deep/ .ivu-input-number-handler {
  background: #b0bac6;
}
.prop-info-robot /deep/ .ivu-input-number {
  border: none;
}
.warning-table /deep/ .ivu-table td {
  background-color: #cbd3dd;
}
.prop-info-divider {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px;
}
</style>