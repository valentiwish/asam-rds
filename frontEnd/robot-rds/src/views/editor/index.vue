<template>
  <div class="page-content">
    <!-- 左侧导航栏区域 -->
    <div class="page-robot">
      <!-- 机器人分组添加按钮区域 -->
      <div class="robot-title">
        <div>机器人</div>
        <div>
          <Icon type="md-add" color="#6CB4EC" size="18" @click="group = !group" style="cursor: pointer;" />
        </div>
      </div>
      <!-- 机器人分组列表区域 -->
      <!-- collapseName为默认展开的分组名，这里取第一个分组名 -->
      <Collapse class="robot-collapse" :value="collapseName">
        <!-- :name为当前面板的 name，与 Collapse的 value 对应，不填为索引值-->
        <Panel v-for="(item, index) in robotGroup" :key="index" :name="item.groupName">
          <div style="display: inline-block;width: 80%;">
            <div style="display: flex;justify-content: space-between;">
              <div @click="setPanel('group', item)">{{ item.groupName }}</div>
              <div @click.stop.prevent="() => { }">
                <Icon v-if="item.abnormal" type="md-checkmark-circle" color="#116FBB" size="18" />
                <Icon v-if="!item.abnormal" type="md-alert" color="#116FBB" size="18" />
                <Icon type="md-add" color="#116FBB" size="18" @click="getRobotsList(item.groupName)" />
                <Icon type="md-trash" color="#116FBB" size="18" @click="deleteGroup(item)" />
              </div>
            </div>
          </div>
          <!-- 通过具名插槽来传递父组件的属性给子组件 -->
          <template #content>
            <div @click="setPanel('robot', r)" v-for="r in item.robots" :key="r.id" :class="currentPanel.vehicleId == r.vehicleId ? 'robot-active' : ''">
              <Row class="robot-row" style="cursor: pointer;display: flex;justify-content: space-between;padding:8px 10px;">
                <!-- 机器人名称 -->
                <Col span="12">
                  <span>{{ r.vehicleId }}</span>
                </Col>
                <!-- 机器人电量 -->
                <Col span="7" class="flex-end">
                  <span style="margin-right: 5px;">{{ r.batteryLevel }}%</span>
                  <Battery ref="battery" :option="batteryOption" :percent="r.batteryLevel" :charging="r.charging"></Battery>
                </Col>
                <Col span="4" class="flex-end">
                  <!-- 加到这里 -->
                  <!-- <Icon type="md-camera" size="18" @click="getImagersList(r.id)" /> -->
                  <!--绑定热成像仪-->
                  <Icon type="md-camera" size="18" @click="bind(r)" style="cursor: pointer;" />
                  <!-- 删除 -->
                  <Icon type="md-trash" size="20" @click="deleteRobot(r)" />
                </Col>
              </Row>
            </div>
          </template>
        </Panel>
      </Collapse>
    </div>
    <!-- 左侧导航栏区域结束 -->

    <!-- 中间地图编辑区域 -->
    <div style="width: 65%;position: relative;">
      <!-- 地图组件，布尔变量editable传递给子组件可编辑状态，penClick触发地图点击事件，可传入站点、机器人(4个anchor可以确定位置及大小)或区域(区域类型、站点、中心点、区域几何信息等) -->
      <TopologyEditor ref="topo" :editable="editable" @penClick="penClick"></TopologyEditor>
      <!-- 底部操作按钮 -->
      <div style="height: 50px;line-height: 50px;position: relative;user-select: none;">
        <div class="flex-end">
          <!-- 区域绘制 -->
          <div style="width: 100%;text-align: center;position: absolute;">
            <Icon type="md-brush" size="18" @click="drawRect" v-if="drawingRect" color="#1070BC" />
            <Icon type="md-brush" size="18" @click="drawRect" v-if="!drawingRect" color="#38465A" />
            <span @click="drawRect" :style="{ color: drawingRect ? '#1070BC' : '#38465A' }">
              {{ drawingRect ? '绘制完成' :
              '开始绘制'
              }}
            </span>
          </div>
          <!-- 告警按钮 -->
          <div class="flex-end" @click="showWarningModel" style="z-index: 100;">
            <!-- 若有告警则为红色，同时显示告警数量 -->
            <Icon type="ios-alert" size="18" :color="(warningList.length > 0 || fatalList.length > 0) ? 'red' : ''" />
            <span style="margin:0 20px 0 5px;" :style="{ color: (warningList.length > 0 || fatalList.length > 0) ? 'red' : '' }">
              告警
              ({{ warningList.length + fatalList.length + warningsList.length + errorList.length + noticeList.length
              }})
            </span>
          </div>
        </div>
      </div>
      <!-- 告警弹窗 -->
      <div v-if="showWarning" style="position: absolute;padding: 10px;bottom: 50px;right: 0;width: 360px;height: 200px;background-color: white;user-select: none;">
        <div class="warning-title">
          <!-- 通过.warning-title .warning对应的CSS样式控制字体（包括下划线）样式 -->
          <div @click="robotWarning = true; robotWarningList = warningList;" class="name" :class="robotWarning ? 'warning' : ''">告警（{{ warningList.length }}）</div>
          <div @click="robotWarning = false; warningCheck = 'fatal'; robotWarningList = fatalList;" class="name" :class="!robotWarning ? 'warning' : ''">机器人告警（{{ fatalList.length + warningsList.length + errorList.length + noticeList.length }}）</div>
        </div>
        <!-- 机器人告警弹窗信息展示区 -->
        <div style="overflow-y: auto;height: 140px;margin: 10px 0;" class="warning-panel">
          <!-- 告警级别过滤 -->
          <div v-if="!robotWarning" class="warning-header">
            <div @click="warningCheck = 'fatal'; robotWarningList = fatalList;" class="warning-header-title" :class="warningCheck == 'fatal' ? 'warning-active' : ''">严重</div>
            <div @click="warningCheck = 'warnings'; robotWarningList = warningsList" class="warning-header-title" :class="warningCheck == 'warnings' ? 'warning-active' : ''">错误</div>
            <div @click="warningCheck = 'error'; robotWarningList = errorList" class="warning-header-title" :class="warningCheck == 'error' ? 'warning-active' : ''">警告</div>
            <div @click="warningCheck = 'notice'; robotWarningList = noticeList" class="warning-header-title" :class="warningCheck == 'notice' ? 'warning-active' : ''">通知</div>
          </div>
          <!-- 告警列表 -->
          <Collapse simple accordion>
            <Panel v-for="(item, index) in robotWarningList" :key="index">
              <div style="display: inline-block;width:90%;" class="warning">
                <div style="width:100%;display: flex;align-items: center;justify-content: space-between;">
                  <div style="width: 150px;overflow: hidden;text-overflow:ellipsis;white-space: nowrap;">{{ item.msg }}</div>
                  <div>{{ item.time }}</div>
                </div>
              </div>
              <p slot="content">{{ item.msg }}</p>
            </Panel>
          </Collapse>
        </div>
      </div>
      <!-- 图层按钮 -->
      <div style="position: absolute;top: 30px;left: 20px;">
        <div @click="switchBg(!bkImage)" style="text-align: center;cursor:pointer;height: 30px;width:30px;border: #27a9e3 solid 1px;border-radius: 2px;margin-left: 10px;background-color: white;">
          <img :src="'./static/images/editor/bgC.png'" style="width:24px;margin-top: 2px;" />
        </div>
      </div>
    </div>
    <!-- 中间地图编辑区域结束 -->

    <!-- 右侧属性栏区域，通过改变对 infoPanel 赋值来控制显示哪个属性栏 -->
    <div style="width: 20%;padding: 5px;overflow-y: auto;height: 100%;color: #29456A;background-color: #B0BAC6;" :rules="ruleRobotGroup">
      <!-- 分组信息 -->
      <div v-if="infoPanel == 'group'" style="color: #29456A;background-color: #B0BAC6;user-select: none;">
        <div class="prop-info-divider">
          <div>类型</div>
          <div style="background-color: #A0AEBD;width:88%;height:2px"></div>
        </div>
        <div class="prop-info-robot">
          <div>
            名称：
            <Input style="width:auto" v-model="currentPanel.groupName" />
          </div>
        </div>
        <div class="prop-info-divider">
          <div>描述</div>
          <div style="background-color: #A0AEBD;width:88%;height:2px"></div>
        </div>
        <div class="prop-info-robot" style="padding: 10px;">
          <div>
            <Input v-model="currentPanel.groupDes" type="textarea" />
          </div>
        </div>
        <div class="prop-info-divider">
          <div>属性</div>
          <div style="background-color: #A0AEBD;width:88%;height:2px"></div>
        </div>
        <div class="prop-info-robot">
          <span>isPooling：</span>
          <Select v-model.number="currentPanel.pooling" style="width:200px">
            <Option :value="1">true</Option>
            <Option :value="0">false</Option>
          </Select>
        </div>
        <div style="padding: 10px;text-align: right;">
          <Button type="primary" @click="changeGroupProperty">修改</Button>
        </div>
      </div>
      <!-- 机器人信息 -->
      <div v-if="infoPanel == 'robot'">
        <RobotProps :currentPanel="currentPanel" :property="robotPropertyList" @changeProperty="changeProperty" @deleteProperty="deleteRobotProp"></RobotProps>
      </div>
      <!-- 点位信息 -->
      <div v-if="infoPanel == 'point'">
        <div class="prop-info-divider">
          <div>类型</div>
          <div style="background-color: #A0AEBD;width:88%;height:2px"></div>
        </div>
        <div class="prop-info-robot">
          <div>
            名称：
            <Input style="width:auto" v-model="currentPanel.instanceName" readonly />
          </div>
        </div>
        <div class="prop-info-divider">
          <div>属性</div>
          <div style="background-color: #A0AEBD;width:88%;height:2px"></div>
        </div>
        <div class="prop-info-robot">
          <div v-for="(item, index) in propertyList" :key="index" style="display: flex;align-items: center;margin: 5px 0;">
            <div style="text-align: left;width: 50%;">{{ item.name }}</div>
            <div style="text-align: right;width: 50%;">
              <Button type="primary" @click="bindModal(item.code)">绑定</Button>
            </div>
          </div>
        </div>
      </div>
      <!-- 区域信息 -->
      <div v-if="infoPanel == 'area'">
        <div class="prop-info-divider">
          <div>信息</div>
          <div style="background-color: #A0AEBD;width:88%;height:2px"></div>
        </div>
        <div class="prop-info-robot">
          <div>
            <span style="display: inline-block;width: 60px;">名称：</span>
            <Input style="width:auto" v-model="currentPanel.areaName" readonly />
          </div>
        </div>
        <div class="prop-info-robot">
          <div>
            <span style="display: inline-block;width: 60px;">类型：</span>
            <Input style="width:auto" :value="getAreaType(currentPanel.areaType)" readonly />
          </div>
        </div>
        <div class="prop-info-robot">
          <div>
            <span style="display: inline-block;width: 60px;">关键点：</span>
            <Input style="width:auto" :value="currentPanel.areaCenterPoint" readonly />
          </div>
        </div>
        <div class="prop-info-robot">
          <div>
            <span style="display: inline-block;width: 60px;">站点：</span>
            <Input style="width:auto" :value="JSON.parse(currentPanel.areaContainPoints)" readonly />
          </div>
        </div>
        <div class="prop-info-robot" style="text-align: right;">
          <Button type="primary" @click="deleteArea">删除</Button>
        </div>
        <div class="prop-info-divider">
          <div>相邻区域</div>
          <div style="background-color: #A0AEBD;width:82%;height:2px"></div>
        </div>
        <div class="prop-info-robot">
          <div v-for="(item, index) in neiborList" :key="index" style="display: flex;align-items: center;margin: 5px 0;">
            <div style="text-align: left;width: 50%;">{{ item.bindRegionName }}</div>
            <div style="text-align: right;width: 50%;">
              <Button type="primary" @click="deleteNeibor(item.id)">删除</Button>
            </div>
          </div>
          <div style="text-align: center;">
            <Button v-if="!neiborArea" type="primary" @click="neiborArea = true">添加</Button>
            <Input v-if="neiborArea" :value="neiborNameList.join('、')" type="textarea" placeholder="请选择地图中相邻区域" class="neibor" readonly />
            <div>
              <Button v-if="neiborArea" type="primary" @click="setNeiborArea" style="margin-top: 10px;">确认</Button>
              <Button v-if="neiborArea" type="default" @click="clearNeibor" style="margin-top: 10px;">取消</Button>
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- 右侧属性栏区域结束 -->

    <!-- 添加机器人分组 -->
    <Modal v-model="group" title="添加机器人分组" class="robot-modal" footer-hide>
      <Form ref="robotGroup" :model="groupInfo" :label-width="80" :rules="ruleRobotGroup">
        <FormItem prop="groupName" label="分组名称">
          <Input type="text" v-model="groupInfo.groupName" placeholder="分组名称">
            <template #prepend>
              <Icon type="ios-person-outline"></Icon>
            </template>
          </Input>
        </FormItem>
        <FormItem prop="groupDes" label="分组描述">
          <Input type="textarea" v-model="groupInfo.groupDes" placeholder="分组描述">
            <template #prepend>
              <Icon type="ios-lock-outline"></Icon>
            </template>
          </Input>
        </FormItem>
        <FormItem>
          <Button type="primary" @click="addNewGroup">确定</Button>
          <Button type="default" @click="cancel">取消</Button>
        </FormItem>
      </Form>
    </Modal>
    <!--添加机器人-->
    <Modal v-model="newRobot" title="添加机器人" footer-hide @on-cancel="cancel">
      <Table :columns="robotColumns" :data="robotList" class="warning-table" @on-selection-change="selectionRows"></Table>
      <div style="text-align: right;margin-top: 20px;">
        <Button type="primary" @click="addNewRobot">确认</Button>
        <Button type="default" @click="cancel">取消</Button>
      </div>
    </Modal>
    <!-- <Modal v-model="newImager" title="添加热成像仪" footer-hide @on-cancel="cancel">
      <Table :columns="imagerColumns" :data="imagerList" class="warning-table" @on-selection-change="selectionImagersRows"></Table>
      <div style="text-align: right;margin-top: 20px;">
        <Button type="primary" @click="addNewImager">确认</Button>
        <Button type="default" @click="cancel">取消</Button>
      </div>
    </Modal>-->
    <!--绑定热成像仪-->
    <!-- <Modal v-model="modal_add" title="绑定热成像仪" :closable="true">
      <update-modal ref="addform" @changeVal="changeVal"></update-modal>
      <div slot="footer">
        <Button type="primary" class="ivu-btn ivu-btn-primary" @click="save" :loading="loading">保存</Button>
        <Button type="ghost" @click="modal_add = false">关闭</Button>
      </div>
    </Modal>-->
    <Modal v-model="modal_add" title="绑定热成像仪" class="imager-modal" footer-hide>
      <Form ref="robotImager" :model="robotImager" :label-width="120" :rules="ruleRobotImager">
        <!-- <FormItem prop="robotId" label="机器人id">
          <Input type="text" v-model="robotImager.robotId" placeholder="请输入机器人id">
          <template #prepend>
          </template>
          </Input>
        </FormItem>-->
        <FormItem prop="currentIp" label="热成像仪IP">
          <Input type="text" v-model="robotImager.currentIp" placeholder="请输入热成像仪IP">
            <template #prepend></template>
          </Input>
        </FormItem>
        <FormItem prop="imagerName" label="热成像仪名称">
          <Input type="text" v-model="robotImager.imagerName" placeholder="请输入热成像仪名称">
            <template #prepend></template>
          </Input>
        </FormItem>
        <FormItem>
          <Button type="primary" @click="bindImager">确定</Button>
          <Button type="default" @click="cancel">取消</Button>
        </FormItem>
      </Form>
    </Modal>
    <!--绑定机器人列表-->
    <Modal v-model="binding" title="选择机器人" footer-hide>
      <Table :columns="robotColumns" :data="bindingRobotList" class="warning-table" @on-selection-change="bindingSelection"></Table>
      <div style="text-align: right;margin-top: 10px;">
        <Button type="primary" @click="bindingProperty">确认</Button>
      </div>
    </Modal>

    <!--绑定光通信站列表-->
    <Modal v-model="bindDms" title="绑定光通信站" footer-hide>
      <Form ref="dms" :model="dms" :rules="ruleValidateType">
        <FormItem label="光通信站名称:" prop="dmsName">
          <Input placeholder="请输入光通信站名称" v-model="dms.dmsName" style="width:200px;" />
          <br />
        </FormItem>
        <FormItem label="光通信站IP:" prop="dmsIp">
          &nbsp;&nbsp;&nbsp;
          <Input placeholder="请输入光通信站IP" v-model="dms.dmsIp" style="width:200px;" />
        </FormItem>
        <!-- <div style="display: flex; justify-content: space-between;margin-top: 10px;">
        <div style="margin-left: 20px;">
          光通信站名称:<Input placeholder="请输入光通信站名称" v-model="dmsName" style="width:200px;margin:10px 10px;" /> <br>
          光通信站IP:&nbsp;&nbsp; <Input placeholder="请输入光通信站IP" v-model="dmsIp" style="width:200px;margin:10px 10px;" /> 
        </div>
        </div>-->
        <div style="text-align: right;margin-top: 10px;">
          <Button type="primary" @click="bindingDms">确认</Button>
        </div>
      </Form>
    </Modal>

    <!--绑定智能充电桩列表-->
    <Modal v-model="bindChargePile" title="绑定智能充电桩" footer-hide>
      <Form ref="chargePile" :model="chargePile" :rules="ruleValidateTypeByChargePile">
        <FormItem label="智能充电桩名称:" prop="chargePileName">
          <Input placeholder="请输入智能充电桩名称" v-model="chargePile.chargePileName" style="width:200px;" />
          <br />
        </FormItem>
        <FormItem label="智能充电桩IP:" prop="chargePileIp">
          &nbsp;&nbsp;&nbsp;
          <Input placeholder="请输入智能充电桩IP" v-model="chargePile.chargePileIp" style="width:200px;" />
        </FormItem>
        <div style="text-align: right;margin-top: 10px;">
          <Button type="primary" @click="bindingChargePile">确认</Button>
        </div>
      </Form>
    </Modal>

    <!--区域信息-->
    <Modal v-model="areaPanel" title="添加区域" footer-hide>
      <Form ref="chargePile" :model="area" label-width="80">
        <FormItem label="区域名称:" prop="areaName">
          <Input placeholder="请输入区域名称" v-model="area.areaName" />
          <br />
        </FormItem>
        <!-- <FormItem label="机器人:" prop="robotName">
          <Select v-model="area.robotName">
            <Option v-for="item in robotsInfo" :value="item.vehicleId" :key="item.vehicleId">{{ item.vehicleId }}
            </Option>
          </Select>
        </FormItem>-->
        <FormItem label="中心点:" prop="areaCenterPoint">
          <Select v-model="area.areaCenterPoint">
            <Option v-for="item in rectPointList" :value="item.id" :key="item.id">{{ item.id }}</Option>
          </Select>
        </FormItem>
        <FormItem label="区域类型:" prop="areaType">
          <Select v-model="area.areaType">
            <Option value="0" key="type0">普通区域</Option>
            <Option value="1" key="type1">充电区域</Option>
            <Option value="2" key="type2">管控区域</Option>
            <Option value="3" key="type3">自动门区域</Option>
            <Option value="4" key="type3">电梯区域</Option>
          </Select>
        </FormItem>
        <div style="text-align: center;margin-top: 10px;">
          <Button type="primary" @click="saveArea">确认</Button>
        </div>
      </Form>
    </Modal>
  </div>
</template>

<script>
import TopologyEditor from '@/components/meta2d/topologyEditor.vue';
import Battery from '@/components/icon/battery.vue';
import RobotProps from "./robotProps.vue"
import config from "@/config";
import math from '@/libs/arithmetic.js'

const { subtract, multiply, divide } = math;

export default {
  name: 'home',
  components: { TopologyEditor, Battery, RobotProps },
  data () {
    const validateName = (rule, value, callback) => {
      if (value === '' || value == null) {
        callback(new Error('分组名称不能为空！'));
      } else {
        this.$ajax.post('/service_rms/robotGroup/checkName', { 'groupName': value })
          .then((res) => {
            if (!res.data.data) {
              callback(new Error('分组名称不能重复！'));
            }
            callback();
          })
      }
    };
    return {
      areaPanel: false,
      area: {},
      areaList: [],
      rectPointList: [],
      drawingRect: false,
      neiborArea: false,
      neiborList: [],
      neiborTempList: [],
      neiborNameList: [],
      showWarning: false,
      robotPropertyList: [],
      editable: false,
      bkImage: false,
      pointsName: [],
      pointsList: [],
      dms: {
        dmsName: '',
        dmsIp: '',
      },
      chargePile: {
        chargePileName: '',
        chargePileIp: '',
      },
      ruleValidateType: {
        dmsName: [
          { required: true, type: 'string', message: '光通信站名称不能为空', trigger: 'blur' },
          { validator: this.checkDmsName, trigger: 'blur' },
        ],
        dmsIp: [
          { required: true, type: 'string', message: '光通信站IP不能为空', trigger: 'blur' },
          { validator: this.checkIp, trigger: 'blur' },
          // { validator: this.checkDmsIp, trigger: 'blur' },
        ],
        // dmsPoint: [
        //     { required: true, type: 'string', message: '光通信站点位不能为空', trigger: 'blur' },
        //     // { validator: this.checkDmsPoint, trigger: 'blur' },
        // ],
      },
      ruleValidateTypeByChargePile: {
        chargePileName: [
          { required: true, type: 'string', message: '光通信站名称不能为空', trigger: 'blur' },
          { validator: this.checkChargePileName, trigger: 'blur' },
        ],
        chargePileIp: [
          { required: true, type: 'string', message: '光通信站IP不能为空', trigger: 'blur' },
          { validator: this.checkChargePileIp, trigger: 'blur' },
        ],
      },
      agv: {
        tags: 'agv',
        name: 'image',
        iconFamily: 'iconfont',
        icon: '\ue60c',
        text: '',
        width: 7,
        height: 7,
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
      infoProps: true,
      activePoint: {
        pos: {
          x: 0,
          y: 0
        }
      },
      agvPoint: {},
      navRobot: '',
      location: false,
      robot: false,
      robotsInfo: [],
      currentRobot: null,
      allRobots: 0,
      onlineRobots: 0,
      online: false,
      navigate: false,
      navigateStation: null,
      batteryOption: {
        style: {
          width: '20px',
          height: '14px'
        },
        highColor: '#0f70bc',
        lowColor: '#F8A241',
        isCharging: true,
        limit: 50
      },
      percent: 0,
      confidenceLimit: 0.8,
      warning: 0,
      warningList: [],
      robotWarning: true,
      robotWarningList: [],
      warningCheck: 'fatal',
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
      robotGroup: [],
      collapseName: '',
      collapseNames: [],
      group: false,
      modal_add: false,
      groupInfo: {
        groupName: null,
        groupDes: ''
      },
      robotImager: {
        robotName: '',
        currentIp: '',
        imagerName: '',
      },
      newRobot: false,
      // newImager: false,
      robotList: [],
      // imagerList: [],
      dmsIp: '',
      dmsName: '',
      robotColumns: [
        {
          type: 'selection',
          width: 60,
          align: 'center'
        },
        {
          title: 'IP',
          key: 'currentIp'
        },
        {
          title: '名称',
          key: 'vehicleId'
        },
        {
          title: '备注',
          key: 'robotNote'
        }
      ],
      checkRobots: [],
      checkImagers: [],
      mapList: ['T1-13', '13_1'],
      currentGroup: null,
      currentRobot: null,
      infoPanel: 'group',
      currentPanel: {},//作为可变对象存储当前面板的属性，
      ruleRobotGroup: {
        groupName: [
          { validator: validateName, trigger: 'blur' }
        ],
      },
      ruleRobotImager: {
        robotName: [
          { required: true, type: 'string', message: '机器人名称不能为空', trigger: 'blur' },
          // { validator: this.checkDmsName, trigger: 'blur' },
        ],
        currentIp: [
          { required: true, type: 'string', message: '热成像仪IP不能为空', trigger: 'blur' },
          { validator: this.checkIp, trigger: 'blur' }
          // { validator: this.checkDmsName, trigger: 'blur' },
        ],
        imagerName: [
          { required: true, type: 'string', message: '热成像仪名称不能为空', trigger: 'blur' },
          { validator: this.checkImagerName, trigger: 'blur' }
          // { validator: this.checkDmsName, trigger: 'blur' },
        ],
      },
      // 告警
      fatalList: [],
      warningsList: [],
      errorList: [],
      noticeList: [],
      propertyList: [],
      binding: false,
      bindDms: false,
      bindChargePile: false,
      bindingRobotList: [],
      bindingCheckList: [],
      property: null,
      robotStatusInfo: {},
      imageIcons: {
        "parkPoint": "\ue6f1",
        "dmsParkPoint": "\ue6f1",
        "chargePoint": "\ue6f2",
        "dmsPoint": "\ue716"
      }
    }
  },
  methods: {
    getAreaType (type) {
      switch (type) {
        case 0:
          return '普通区域';
        case 1:
          return '充电区域';
        case 2:
          return '管控区域';
        case 3:
          return '自动门区域';
        case 4:
          return '电梯区域';
        default:
          return '未知类型';
      }
    },

    showWarningModel () {
      this.showWarning = !this.showWarning;
      this.robotWarningList = this.warningList;
      this.robotWarning = true;
    },
    //绘制区域
    drawRect () {
      this.drawingRect = !this.drawingRect;
      //开始绘制
      if (this.drawingRect) {
        /*
         * 实际调用了topologyEditor的Meta2d组件的drawLine('line')方法
         */
        this.$refs.topo.drawLine('line');
      }
      //结束绘制，并存储绘制信息于area.areaInfo中
      else {
        this.$refs.topo.finishDrawLine();
        let data = this.$refs.topo.meta2d.store.active[0];
        let arr = [];
        //获取图层全部的点
        let points = this.$refs.topo.getByTag('point');
        //遍历图层的点，筛选出绘制区域内的所有点arr
        points.map(j => {
          if (this.$refs.topo.pointInRect({ x: j.x, y: j.y }, data)) {
            arr.push(j);
          }
        })
        //区域内的点交给全局变量rectPointList，供saveArea方法使用
        this.rectPointList = arr;
        this.areaPanel = true;
        data.dash = 2;
        data.lineDash = [10, 10];
        data.lineWidth = 0.5;
        data.tags = 'area';
        this.area.areaInfo = data;
      }
      // this.switchBg(!this.bkImage);
    },
    //保存区域信息
    saveArea () {
      let info = this.area.areaInfo;
      //清除Meta2d画布上的区域
      this.$refs.topo.delete(this.$refs.topo.getByTag('area'));
      if (this.area.areaType == "0") {
        info.color = '#8470FF';
        info.background = 'rgba(132,112,255,0.1)';
      } else if (this.area.areaType == "1") {
        info.color = '#22C30C';
        info.background = 'rgba(34,195,12,0.1)';
      } else if (this.area.areaType == "2") {
        info.color = '#FF4D4F'; //设置边框颜色为红色
        info.background = 'rgba(255,77,79,0.1)'; //设置背景颜色为浅红色
      } else if (this.area.areaType == "3") {
        info.color = '#FF8C00'; //设置边框颜色为橙色
        info.background = 'rgba(255,140,0,0.1)'; //设置背景颜色为浅橙色
      } else if (this.area.areaType == "4") {
        info.color = '#00CED1'; //设置边框颜色为青色
        info.background = 'rgba(0,206,209,0.1)'; //设置背景颜色为浅青色
      } else {
        info.color = '#A9A9A9';
        info.background = 'rgba(169,169,169,0.1)';
      }

      this.area.areaInfo = info;
      this.area.areaInfo.calculative = null;
      this.area.areaInfo.rect = this.$refs.topo.meta2d.getPenRect(info);
      this.$ajax.postJson('/service_rms/dmsEditor/save',
        {
          'robotName': this.area.robotName,
          'areaName': this.area.areaName,
          'areaCenterPoint': this.area.areaCenterPoint,
          'areaContainPoints': JSON.stringify(this.rectPointList.map(item => item.id)),//item.id即站点名称
          'areaInfo': JSON.stringify(this.area.areaInfo),
          'areaType': this.area.areaType,
        })
        .then((res) => {
          if (res.data.code == 200) {
            this.areaPanel = false;
            this.area = {};
            this.getArea();
            this.$Message.success('保存成功！');
          }
        })
    },
    // 获取区域列表
    getArea () {
      this.$ajax.post('/service_rms/dmsEditor/list')
        .then((res) => {
          if (res.data.code == 200) {
            //清除Meta2d画布上的区域
            let pens = this.$refs.topo.getByTag('area');
            this.$refs.topo.delete(pens);
            res.data.data.map(j => {
              if (j.areaInfo) {
                let data = JSON.parse(j.areaInfo);
                data.area = j;
                this.$refs.topo.addPen(data);
                if (data.rect) {
                  let rect = data.rect;
                  this.$refs.topo.meta2d.setPenRect(data, rect);
                }
                // data.area = j;
                // this.$refs.topo.addPen(data);
                this.$refs.topo.inactive();
              }
            })
          }
        })
    },
    //删除区域
    deleteArea () {
      this.$Modal.warning({
        title: '确定删除此区域吗？',
        onOk: () => {
          this.$ajax.post('/service_rms/dmsEditor/delete', { id: this.currentPanel.id })
            .then((res) => {
              if (res.data.code == 200) {
                this.$Message.success('删除成功');
                this.infoPanel = 'group';
                this.currentPanel = this.robotGroup[0];
                this.getArea();
              }
            })
        }
      });
    },
    //添加相邻区域
    setNeiborArea () {
      const idArray = this.neiborTempList.map(item => item.id);
      this.$ajax.postJson('/service_rms/dmsRegionRelate/save', { 'currentRegionId': this.currentPanel.id, 'bindRegionIds': JSON.stringify(idArray) })
        .then((res) => {
          if (res.data.code == 200) {
            this.clearNeibor();
            this.$Message.success('保存成功');
            this.getAreaNeibors(this.currentPanel.id)
          }
        })
    },
    getAreaNeibors (id) {
      let that = this;
      this.$ajax.post('/service_rms/dmsRegionRelate/list', { 'currentRegionId': id })
        .then((res) => {
          if (res.data.code == 200) {
            that.neiborList = res.data.data;
          }
        })
    },
    //删除相邻区域
    deleteNeibor (id) {
      this.$Modal.warning({
        title: '确定删除此相邻区域吗？',
        onOk: () => {
          this.$ajax.post('/service_rms/dmsRegionRelate/delete', { id: id })
            .then((res) => {
              if (res.data.code == 200) {
                this.$Message.success('删除成功');
                this.getAreaNeibors(this.currentPanel.id);
              }
            })
        }
      });
    },
    clearNeibor () {
      this.neiborArea = false;
      this.neiborNameList = [];
      this.neiborTempList = [];
    },
    collapse: function (index) {
      if (this.showWarning == index) {
        this.showWarning = -1;
      } else {
        this.showWarning = index;
      }
    },
    bind: function (obj) {
      this.modal_add = true;
      this.robotImager.robotName = obj.vehicleId;
    },
    checkIp: function (rule, value, callback) {
      if (value) {
        var ph = /^(((\d)|([1-9]\d)|(1\d{2})|(2[0-4]\d)|(25[0-5]))\.){3}((\d)|([1-9]\d)|(1\d{2})|(2[0-4]\d)|(25[0-5]))$/;
        if (!ph.test(value)) {
          callback("ip格式不正确");
        } else {
          callback();
        }
      }
    },
    //校验类型编码
    checkImagerName: function (rule, value, callback) {
      if (value) {
        this.$ajax.post("/service_rms/robotImager/checkImagerName", { 'id': this.id, 'imagerName': value })
          .then((res) => {
            if (res.data.data == false) {
              callback("抽检编码已经存在");

            } else {
              callback();
            }
          })
          .catch((error) => {
            callback('校验抽检编码失败');
          })
      }
    },
    //校验光通信站名称唯一性
    checkDmsName: function (rule, value, callback) {
      if (value) {
        this.$ajax.post("/service_rms/dms/checkDmsName", { 'dmsName': value, 'id': this.id })
          .then((res) => {
            if (res.data.data) {
              callback();
            } else {
              callback("光通信站名称已存在");
            }
          })
          .catch((error) => {
            callback('校验失败');
          })
      } else {
        callback("请输入光通信站名称");
      }
    },
    //校验光通信站IP唯一性
    checkDmsIp: function (rule, value, callback) {
      if (value) {
        this.$ajax.post("/service_rms/dms/checkDmsIp", { 'dmsIp': value, 'id': this.id })
          .then((res) => {
            if (res.data.data) {
              callback();
            } else {
              callback("光通信站名称IP已存在");
            }
          })
          .catch((error) => {
            callback('校验失败');
          })
      } else {
        callback("请输入光通信站IP");
      }
    },
    //校验光通信站点位唯一性
    checkDmsPoint: function (rule, value, callback) {
      if (value) {
        this.$ajax.post("/service_rms/dms/checkDmsPoint", { 'dmsPoint': value, 'id': this.id })
          .then((res) => {
            if (res.data.data) {
              callback();
            } else {
              callback("光通信站点位已存在");
            }
          })
          .catch((error) => {
            callback('校验失败');
          })
      } else {
        callback("请输入光通信站点位");
      }
    },
    //校验智能充电桩名称唯一性
    checkChargePileName: function (rule, value, callback) {
      if (value) {
        this.$ajax.post("/service_rms/chargePile/checkChargePileName", { 'chargePileName': value, 'id': this.id })
          .then((res) => {
            if (res.data.data) {
              callback();
            } else {
              callback("智能充电桩名称已存在");
            }
          })
          .catch((error) => {
            callback('校验失败');
          })
      } else {
        callback("请输入智能充电桩名称");
      }
    },
    //校验智能充电桩IP唯一性
    checkChargePileIp: function (rule, value, callback) {
      if (value) {
        this.$ajax.post("/service_rms/chargePile/checkChargePileIp", { 'chargePileIp': value, 'id': this.id })
          .then((res) => {
            if (res.data.data) {
              callback();
            } else {
              callback("智能充电桩IP已存在");
            }
          })
          .catch((error) => {
            callback('校验失败');
          })
      } else {
        callback("请输入智能充电桩IP");
      }
    },
    getMap: function () {
      debugger
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

          this.addPens(res.data.data, this.agv, "agv");
          //重复修改位置！！！！
          this.addPens(res.data.data, this.agv, "agv");
        })
    },
    getSocket: function () {
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
        // console.log("robot_push",data)
        let res = JSON.parse(data);
        switch (res.code) {
          case "robot_push_less_res":
            // console.log("robot_push",res)
            this.showAGVInfo(res.data);
            this.fatalList = [];
            this.errorList = [];
            this.warningsList = [];
            this.noticeList = [];
            this.addPens(res.data, this.agv, "agv");
            // this.robotsInfo = this.mergeObjectArrays(this.robotsInfo, res.data);
            this.robotsInfo = JSON.parse(JSON.stringify(res.data));

            //机器人告警
            res.data.map(item => {
              item.fatals.map(j => {
                let obj = {
                  time: new Date().Format('yyyy-MM-dd hh:mm:ss'),
                  msg: '[' + j.vehicleId + ']' + j.message
                }
                this.fatalList.push(obj);
              })
              item.warnings.map(j => {
                let obj = {
                  time: new Date().Format('yyyy-MM-dd hh:mm:ss'),
                  msg: '[' + j.vehicleId + ']' + j.message
                }
                this.warningsList.push(obj);
              })
              item.errors.map(j => {
                let obj = {
                  time: new Date().Format('yyyy-MM-dd hh:mm:ss'),
                  msg: '[' + j.vehicleId + ']' + j.message
                }
                this.errorList.push(obj);
              })
              item.notices.map(j => {
                let obj = {
                  time: new Date().Format('yyyy-MM-dd hh:mm:ss'),
                  msg: '[' + j.vehicleId + ']' + j.message
                }
                this.noticeList.push(obj);
              })
            })

            //获取右侧栏状态信息
            if (this.infoPanel == 'robot') {
              let result = this.robotsInfo.find(item => item.vehicleId === this.currentPanel.vehicleId);
              this.robotStatusInfo = Object.assign(this.robotStatusInfo, result);
              let temp = {
                chargeOnly: this.currentPanel.chargeOnly,
                chargeNeed: this.currentPanel.chargeNeed,
                chargeOk: this.currentPanel.chargeOk,
                chargeFull: this.currentPanel.chargeFull
              }
              this.currentPanel = { ...this.robotStatusInfo, ...temp };
            }

            //判断导航是否完成
            if (this.navigate && this.navigateStation != null) {
              let result = this.robotsInfo.find(item => item.vehicleId === this.currentPanel.vehicleId);
              if (result.currentStation == this.navigateStation) {
                this.navigate = false;
                this.navigateStation = null;
                setTimeout(() => {
                  this.$Message.success("导航完成");
                }, 1500)
              }
            }
            break;
          case "robot_config_downloadmap_res":
            this.analyze(res.data)
            break;
        }
      })
    },
    //解析地图
    analyze: function (data) {
      this.pointsList = [];
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
            "locked": 10,
            "zIndex": -1
          }
        ],
        "grid": true,
        "paths": {},
        "version": "1.0.12",
        "background": '#E9ECEC'
        // "bkImage": "https://drive.le5lecdn.com/2023/1205/1/1/bg.201aa3e5.png",
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

          let cx1, cy1, cx2, cy2;
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
          k.mapName = jsonData.header.mapName;
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
              mapName: jsonData.header.mapName,
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
        this.pointsList = this.pointsList.concat(points);
      })
      this.$refs.topo.drawData(map);
      this.$refs.topo.meta2d.fitView(true);
      this.pointsName = [];
      this.extraPoints();
      this.getArea();
      this.switchBg(this.bkImage);
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
      //meta2d.js插件中的方法
      const pen = this.$refs.topo.meta2d.findOne('bg');
      this.bkImage = flag;
      this.$refs.topo.meta2d.setVisible(pen, flag);
    },
    //添加特殊点
    extraPoints: function () {
      this.$ajax.post('/service_rms/robotBasic/allAttributedList')
        .then((res) => {
          if (this.getWarningList(res.data)) {
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
          }
        })
    },
    //地图点击事件
    penClick: function (data) {
      if (data.name == 'image') { //点击站点或机器人
        if (data.tags == 'agv') {
          let agvPoint = JSON.parse(data.info);
          //在pointsList数组中查找与AGV当前站点相匹配的点，并更新currentPanel显示的面板内容
          this.currentPanel = this.pointsList.find(item => item.instanceName == agvPoint.currentStation);
          this.infoPanel = 'point';
        } else if (data.tags == 'point' && data.info != undefined) {
          this.currentPanel = JSON.parse(data.info);
          this.infoPanel = 'point';
        }
      } else { //点击区域
        if (data.tags == 'area') {
          if (this.neiborArea) { //添加相邻区域
            let area = this.neiborList.find(j => j.bindRegionId == data.area.id);
            if (area) {
              this.$Message.warning('此区域已添加！');
            } else {
              let obj = this.neiborTempList.find(j => j.id == data.area.id);
              if (obj) {
                this.neiborNameList = this.neiborNameList.filter(item => item !== obj.areaName);
                this.neiborTempList = this.neiborTempList.filter(item => item.id !== obj.id);
              } else {
                this.neiborNameList.push(data.area.areaName);
                this.neiborTempList.push(data.area);
              }
            }
          } else { //查看信息
            this.currentPanel = data.area;
            this.infoPanel = 'area';
            this.getAreaNeibors(this.currentPanel.id);
          }
        }
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
        this.agvPoint = result;
        this.agvPoint["model"] = this.currentRobot.model;
        this.agvPoint["version"] = this.currentRobot.version;
        this.agvPoint["currentIp"] = this.currentRobot.currentIp;
        this.agvPoint["dspVersion"] = this.currentRobot.dspVersion;
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
    agvControl: function () {
      this.$ajax.post('/service_rms/monitor/getControl', { "robotName": "W600DS_7_1" })
        .then((res) => {
          this.getWarningList(res.data);
        })
    },
    navTo: function () {
      if (this.navigate) {
        this.$Message.info('请等待机器人停止');
      } else {
        if (this.navRobot != '') {
          let result = this.robotsInfo.find(item => item.vehicleId === this.navRobot);
          debugger
          this.$ajax.post('/service_rms/monitor/runTask',
            {
              "robotName": this.navRobot,
              "beginPort": result.currentStation,
              "endPort": this.activePoint.instanceName
            })
            .then((res) => {
              // console.log(res)
              if (res.data.code == 200) {
                this.$Message.success('开始导航');
                this.navigate = true;
                this.navigateStation = this.activePoint.instanceName;
              }
              this.getWarningList(res.data);
            })
        } else {
          this.$Message.error('请选择机器人！');
        }
      }
    },
    getWarningList: function (res) {
      if (res.code != 200) {
        let obj = {
          msg: res.msg,
          time: new Date().Format("yyyy-MM-dd hh:mm:ss"),
        }
        this.warningList.push(obj);
        this.$Message.error('操作出错！');
        return false;
      }
      return true;
    },
    searchPoint: function (e) {
      let res = this.$refs.topo.findOne(e);
      if (res) {
        this.$refs.topo.gotoCenter(res[0]);
      } else {
        // console.log("res", res)
      }
    },
    // 获取机器人分组列表
    getRobotInfo: function () {
      let that = this;
      that.$ajax.post('/service_rms/robotBasic/getGroupAndRobot')
        .then((res) => {
          if (that.getWarningList(res.data)) {
            that.robotGroup = res.data.data;
            if (that.robotGroup.length >= 1) {
              that.collapseName = that.robotGroup[0].groupName;
              that.currentPanel = that.robotGroup[0];
            }
          }
        })
    },
    //添加机器人
    addNewRobot: function () {
      this.$ajax.postJson('/service_rms/robotBasic/saveReal', { 'groupName': this.currentGroup, "data": this.checkRobots })
        .then((res) => {
          if (this.getWarningList(res.data)) {
            this.$Message.success("添加成功");
            this.$router.go(0);
          }
        })
    },
    //添加热成像仪
    // addNewImager: function () {
    //   this.$ajax.postJson('/service_rms/robotBasic/saveRealImager', { 'id': this.currentRobot, "data": this.checkImagers })
    //     .then((res) => {
    //       if (this.getWarningList(res.data)) {
    //         this.$Message.success("添加成功");
    //         this.$router.go(0);
    //       }
    //     })
    // },
    addNewGroup: function () {
      this.$refs['robotGroup'].validate((valid) => {
        if (valid) {
          this.$ajax.post('/service_rms/robotGroup/save', this.groupInfo)
            .then((res) => {
              if (this.getWarningList(res.data)) {
                this.cancel();
                this.getRobotInfo();
                this.$Message.success('保存成功!');
              }
            })
        } else {
          return false;
        }
      })
    },
    bindImager: function () {
      // console.log("this.robotImager.robotName", this.robotImager.robotName)
      this.$refs['robotImager'].validate((valid) => {
        if (valid) {
          this.$ajax.post('/service_rms/robotImager/save', { "data": JSON.stringify(this.robotImager) })
            .then((res) => {
              if (this.getWarningList(res.data)) {
                this.cancel();
                this.$Message.success('绑定成功!');
              }
            })
        } else {
          return false;
        }
      })
    },
    cancel: function () {
      this.group = false;
      this.modal_add = false;
      this.newRobot = false;
      // this.newImager = false;
      this.checkRobots = [];
      // this.checkImagers = [];
      this.currentGroup = null;
      this.currentRobot = null;
      this.$refs['robotGroup'].resetFields();
      this.$refs['robotImager'].resetFields();
      this.bindingCheckList = [];
      this.binding = false;
      this.bindDms = false;
      this.bindChargePile = false;
      this.property = null;
      this.dmsIp = '';
      this.dmsName = '';
    },
    deleteGroup: function (item) {
      if (item.robots.length > 0) {
        this.$Message.error("该分组下存在机器人，不能删除！");
      } else {
        this.$Modal.confirm({
          title: '确认删除' + item.groupName + '吗？',
          onOk: () => {
            this.$ajax.post('/service_rms/robotGroup/delete', { 'id': item.groupId })
              .then((res) => {
                if (this.getWarningList(res.data)) {
                  this.getRobotInfo();
                  this.$Message.success('删除成功');
                }
              })
          }
        });
      }
    },
    deleteRobot: function (item) {
      this.$Modal.confirm({
        title: '确认删除' + item.vehicleId + '吗？',
        onOk: () => {
          this.$ajax.post('/service_rms/robotBasic/delete', { 'id': item.id })
            .then((res) => {
              if (this.getWarningList(res.data)) {
                this.$Message.success('删除成功');
                this.$router.go(0);
              }
            })
        }
      });
    },
    getRobotsList: function (name) {
      this.newRobot = true;
      this.currentGroup = name;
      this.$ajax.post('/service_rms/robotBasic/getAddRobotInfo', { 'groupName': name })
        .then((res) => {
          if (this.getWarningList(res.data)) {
            this.robotList = res.data.data;
          }
        })
    },
    selectionRows: function (selection) {
      this.checkRobots = selection;
    },
    // selectionImagersRows: function (selection) {
    //   this.checkImagers = selection;
    // },
    // 设置当前右侧栏
    setPanel: function (type, info) {
      this.infoPanel = type;
      this.currentPanel = info;
      if (type == 'robot') {
        //获取已绑定属性
        this.$ajax.post('/service_rms/robotBasic/attributedList', { vehicleId: info.vehicleId })
          .then((res) => {
            if (this.getWarningList(res.data)) {
              this.robotPropertyList = res.data.data;
            }
          })
        //获取状态信息
        this.$ajax.post('/service_rms/robotRealTime/runInfo')
          .then((res) => {
            if (this.getWarningList(res.data)) {
              this.robotsInfo = res.data.data;
              let result = this.robotsInfo.find(item => item.vehicleId === this.currentPanel.vehicleId);
              this.robotStatusInfo = Object.assign({}, this.robotStatusInfo, result);
            }
          })
        this.robotStatusInfo = Object.assign({}, this.robotStatusInfo, this.robotsInfo.find(item => item.vehicleId === this.currentPanel.vehicleId));
        // this.currentPanel = this.robotStatusInfo;
      }
      this.extraPoints();
    },
    //修改属性
    changeProperty: function (data) {
      if (this.getWarningList(data)) {
        this.$Message.success('修改成功');
      }
    },
    //修改分组属性
    changeGroupProperty: function () {
      this.$Modal.confirm({
        title: '确认修改属性值吗？',
        onOk: () => {
          // console.log(this.currentPanel)
          this.$ajax.post('/service_rms/robotGroup/update',
            {
              'id': this.currentPanel.groupId,
              'groupName': this.currentPanel.groupName,
              'groupDes': this.currentPanel.groupDes,
              'pooling': this.currentPanel.pooling
            })
            .then((res) => {
              if (this.getWarningList(res.data)) {
                this.$Message.success('修改成功');
              }
            })
        }
      });
    },
    //获取机器人点位属性值
    getPointProperty: function () {
      this.$ajax.post('/service_rms/robotBasic/getAttributeList')
        .then((res) => {
          if (this.getWarningList(res.data)) {
            this.propertyList = res.data.data;
          }
        })
    },
    //校验是否绑定停放点和充电点
    checkBindPoints: function () {
      this.$ajax.post('/service_rms/robotBasic/checkBindPoints')
        .then((res) => {
          if (res.data.code != 200) {
            let obj = {
              msg: res.data.msg,
              time: new Date().Format("yyyy-MM-dd hh:mm:ss"),
            }
            this.warningList.push(obj);
            this.$Message.error(res.data.msg);
          }
        })
    },
    //获取绑定机器人列表
    bindModal: function (code) {
      //code==5光通信站，code==6光通信站待命点
      if (code == 5 || code == 6) {
        this.bindDms = true;
        //code==4充电点
      } else if (code == 4) {
        this.bindChargePile = true;
      } else {
        this.binding = true;
      }
      this.property = code;
      this.mapList = [];
      this.pointsList.map(j => {
        if (j.instanceName == this.currentPanel.instanceName) {
          this.mapList.push(j.mapName);
        }
      });
      this.$ajax.postJson('/service_rms/robotBasic/robotList', {
        mapNames: this.mapList
      })
        .then((res) => {
          if (this.getWarningList(res.data)) {
            this.bindingRobotList = res.data.data;
          }
        })
    },
    bindingSelection: function (data) {
      this.bindingCheckList = data;
    },
    bindingProperty: function () {
      let idArray = [];
      this.bindingCheckList.map(i => {
        idArray.push(i.vehicleId);
      })
      if (this.bindingCheckList.length > 0) {
        this.$ajax.postJson('/service_rms/attribute/bindAttribute', {
          code: this.property,
          vehicleIds: idArray,
          point: this.currentPanel.instanceName
        })
          .then((res) => {
            if (this.getWarningList(res.data)) {
              this.$Message.success("绑定成功！");
              this.cancel();
              window.location.reload()
            }
          })
      } else {
        this.$Message.warning("请选择机器人！");
      }
    },
    //绑定光通信站
    bindingDms: function () {
      let idArray = [];
      this.bindingRobotList.map(i => {
        idArray.push(i.vehicleId);
      })
      if (this.bindingRobotList.length > 0) {
        if (this.dms.dmsIp == '' && this.dms.dmsName == '') {
          this.$Message.warning("请填写光通信站IP或者名字！");
        } else {
          this.$ajax.postJson('/service_rms/attribute/bindDmsPoint', {
            code: this.property,
            vehicleIds: idArray,
            point: this.currentPanel.instanceName,
            dmsIp: this.dms.dmsIp,
            dmsName: this.dms.dmsName
          })
            .then((res) => {
              if (this.getWarningList(res.data)) {
                this.$Message.success("绑定成功！");
                this.cancel();
              }
            })
        }
      } else {
        this.$Message.warning("没有需要绑定的机器人！");
      }
    },
    //绑定智能充电桩
    bindingChargePile: function () {
      let idArray = [];
      this.bindingRobotList.map(i => {
        idArray.push(i.vehicleId);
      })
      if (this.bindingRobotList.length > 0) {
        if (this.chargePile.chargePileIp == '' && this.chargePile.chargePileName == '') {
          this.$Message.warning("请填写智能充电桩IP或者名字！");
        } else {
          this.$ajax.postJson('/service_rms/attribute/bindChargePile', {
            code: this.property,
            vehicleIds: idArray,
            point: this.currentPanel.instanceName,
            chargePileIp: this.chargePile.chargePileIp,
            chargePileName: this.chargePile.chargePileName
          })
            .then((res) => {
              if (this.getWarningList(res.data)) {
                this.$Message.success("绑定成功！");
                this.cancel();
              }
            })
        }
      } else {
        this.$Message.warning("没有需要绑定的机器人！");
      }
    },
    //删除机器人已绑定属性
    deleteRobotProp: function (e) {
      if (this.getWarningList(e.res)) {
        this.$Message.success('删除成功');
        this.setPanel('robot', e.data);
      }
    }
  },
  mounted () {
    this.getMap();
    this.getSocket();
    this.getOnlineRobot();
    this.getRobotInfo();
    this.getPointProperty();
    this.checkBindPoints();
  },
  created () {

  },
  beforeDestroy () {
    if (this._socketio) {
      this._socketio.disconnect();
    }
  }

}
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
  display: flex;
}

.warning-title .name {
  width: 50%;
  text-align: center;
  padding: 5px 0;
  border-bottom: #bcc0c4 solid 1px;
  cursor: pointer;
}

.warning-title .warning {
  color: #ed5f5a;
  border-bottom: #ed5f5a solid 2px;
}

.page-content {
  background: #ccd4df;
  padding: 0;
  overflow: hidden;
  position: relative;
  display: flex;
}

.page-robot {
  width: 15%;
}

.robot-info-button {
  position: absolute;
  right: 20px;
  top: 20px;
  display: flex;
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
  right: 20px;
  top: 60px;
  width: 290px;
  background-color: #fff;
  border: solid 1px #ccc;
  max-height: 580px;
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
  background-color: #fff;
  padding: 0 20px;
  color: #000;
  user-select: none;
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
  bottom: 60px;
  left: 10px;
  width: 300px;
  padding: 0 0 10px 10px;
  background-color: #f2f3f6;
  border: solid 1px #d9d9d9;
  box-shadow: 0 3px 6px -4px rgba(0, 0, 0, 0.12), 0 6px 16px 0 rgba(0, 0, 0, 0.08), 0 9px 28px 8px rgba(0, 0, 0, 0.05);
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

div /deep/ .warning-table .ivu-table-cell {
  padding: 0 10px;
}

.robot-info-search {
  position: absolute;
  top: 20px;
  left: 20px;
}

.robot-info-point {
  position: absolute;
  top: 60px;
  left: 20px;
  background-color: #fff;
  border: solid 1px #ccc;
  padding: 20px;
  max-height: 580px;
  overflow-y: auto;
}

.robot-point-radio {
  padding: 5px 0;
}

.robot-title {
  display: flex;
  justify-content: space-between;
  height: 49px;
  align-items: center;
  padding: 0 17px;
  background-color: #1070bc;
  color: #fff;
}

/* 折叠面板 */
.robot-collapse {
  background-color: #d9e0e8;
}

div /deep/ .robot-collapse .ivu-collapse-content {
  background-color: #ccd4df;
  color: #0f70bc;
  padding: 0;
}

div /deep/ .ivu-collapse > .ivu-collapse-item > .ivu-collapse-header {
  color: #244067;
}

div /deep/ .robot-info-panel .ivu-card-head::after {
  top: 14px;
}

div /deep/ .robot-info-panel .ivu-card {
  margin-bottom: 10px;
}

div /deep/ .robot-info-panel .ivu-card-body {
  padding: 5px;
}

div /deep/ .robot-info-panel .ivu-form-item,
div /deep/ .robot-info-panel .ivu-tabs-bar {
  margin-bottom: 0;
}

div /deep/ .robot-info-panel .ivu-form-item-content {
  line-height: 28px;
}

div /deep/ .robot-info-panel .ivu-tabs-nav-container:focus .ivu-tabs-tab-focused {
  border-color: #fff !important;
}

div /deep/ .robot-info-panel .ivu-form .ivu-form-item-label {
  padding: 8px 12px 8px 0;
}

p {
  word-wrap: break-word;
}

.robot-active {
  background-color: #fff;
}

div /deep/ .robot-property .ivu-form-item {
  margin: 5px 0;
}

.flex-end {
  display: flex;
  cursor: pointer;
  align-items: center;
  justify-content: flex-end;
}

.prop-info-robot {
  background: #cbd3dd;
  padding: 10px 20px;
}

.prop-info-divider {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px;
}

.prop-info-robot /deep/ .ivu-input,
.prop-info-robot /deep/ .ivu-select-selection {
  background: #b0bac6;
}

.neibor {
  margin-top: 10px;
}

.neibor /deep/ textarea {
  background: #fff !important;
}
</style>