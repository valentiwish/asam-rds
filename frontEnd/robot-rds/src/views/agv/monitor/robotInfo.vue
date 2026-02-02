<template>
  <div>
    <div>
      <span>模型:</span>
      <span>{{ agvPoint.model }}</span>
    </div>
    <div>
      <span>设备ID:</span>
      <span>{{ agvPoint.vehicleId }}</span>
    </div>
    <div>
      <span>IP:</span>
      <span>{{ agvPoint.currentIp }}</span>
    </div>
    <div>
      <span>DI:</span>
      <span>{{ agvPoint.dI }}</span>
    </div>
    <div>
      <span>DO:</span>
      <span>{{ agvPoint.dO }}</span>
    </div>
    <div>
      <span>备注:</span>
      <span>{{ agvPoint.robotNote }}</span>
    </div>
    <div>
      <span>Robokit版本号:</span>
      <span>{{ agvPoint.version }}</span>
    </div>
    <div>
      <span>地图名:</span>
      <span>{{ agvPoint.currentMap }}</span>
    </div>
    <div>
      <span>固件版本号:</span>
      <span>{{ agvPoint.dspVersion }}</span>
    </div>
    <div>
      <span>机器人位置:</span>
      <span>{{ agvPoint.x }}m,{{ agvPoint.y }}m</span>
    </div>
    <div>
      <span>累计行驶里程:</span>
      <span>{{ agvPoint.odo }}</span>
    </div>
    <div>
      <span>今日累计行驶里程:</span>
      <span>{{ agvPoint.todayOdo }}</span>
    </div>
    <div>
      <span>累计运行时间:</span>
      <span>{{ agvPoint.totalTime }}</span>
    </div>
    <div>
      <span>本次运行时间:</span>
      <span>{{ agvPoint.time }}</span>
    </div>
    <div>
      <span>是否被阻挡:</span>
      <span>{{ agvPoint.blocked ? '是' : '否' }}</span>
    </div>
    <div>
      <span>实际速度:</span>
      <span>{{ agvPoint.vx }},{{ agvPoint.vy }}</span>
    </div>
    <div>
      <span>当前站点:</span>
      <span>{{ agvPoint.currentStation }}</span>
    </div>
    <div>
      <span>地图:</span>
      <span>{{ agvPoint.currentMap }}</span>
    </div>
    <div>
      <span>抱闸:</span>
      <span>{{ agvPoint.brake ? '是' : '否' }}</span>
    </div>
    <div>
      <span>电压:</span>
      <span>{{ agvPoint.voltage }}</span>
    </div>
    <div>
      <span>电池电流:</span>
      <span>{{ agvPoint.current }}</span>
    </div>
    <div>
      <span>重定位状态:</span>
      <span>{{ getStatus(agvPoint.relocStatus) }}</span>
    </div>
    <div>
      <span>急停:</span>
      <span>{{ agvPoint.softEmc ? '是' : '否' }}</span>
    </div>
    <div>
      <span>软急停:</span>
      <span>{{ agvPoint.softEmc ? '是' : '否' }}</span>
    </div>
    <div>
      <span>置信度:</span>
      <span>{{ agvPoint.confidence }}</span>
    </div>
    <div>
      <span>电量:</span>
      <span>{{ agvPoint.batteryLevel + '%'}}</span>
    </div>
    <!-- <div>
          <span>充电:</span>
          <span>{{ agvPoint.charging ? '是' : '否' }}</span>
        </div> -->
  </div>
</template>

<script>
export default {
  name: 'robotInfo',
  props:['agvPoint'],
  data () {
    return {

    }
  },
  methods:{
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
  }

}
</script>