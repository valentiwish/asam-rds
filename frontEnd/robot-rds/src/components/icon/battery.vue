<template>
  <div>
    <div class="robot-info-battery">
      <!-- <div v-if="option.isCharging" style="display: flex;align-items: center;">
        <Icon type="md-battery-charging" :size="option.style.width.split('px')[0]" style="transform: rotate(90deg);"/>
      </div> -->
      <div v-if="percent<option.limit" :style="option.style">
        <div class="robot-info-border" :style="{borderColor:percent<option.limit?option.lowColor:option.highColor}">
          <Progress 
            :percent="percent"
            :stroke-color="option.lowColor" 
            hide-info 
            :class="charging?'charging':''"
            />
        </div>
      </div>
      <div v-if="percent>=option.limit" :style="option.style">
        <div class="robot-info-border" :style="{borderColor:percent<option.limit?option.lowColor:option.highColor}">
          <Progress
            :percent="percent"
            :stroke-color="option.highColor" 
            hide-info 
            :class="charging?'charging':''"/>
        </div>
      </div>
      <div class="battery-bot" :style="{backgroundColor:percent<option.limit?option.lowColor:option.highColor}"></div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'battery',
  props: {
    option: {
      type: Object,
      default: {
        style: {
          width: '30px',
          height: '18px'
        },
        highColor: '#5ED268',
        lowColor: '#F8A241',
        isCharging: false,
        limit: 50
      }
    },
    percent:{
      type:Number,
      default:100
    },
    charging:{
      type:Boolean,
      default:false
    }
  },
  data: function () {
    return {

    }
  },
  computed: {

  },
  methods: {

  },
  mounted: function () {
  }
}
</script>

<style scoped>
.robot-info-battery {
  display: flex;
  align-items: center;
  margin-right: 3px;
}
div /deep/ .robot-info-battery .ivu-progress-inner {
  display: inline-block;
  width: 100%;
  vertical-align: middle;
  position: relative;
  background-color: transparent;
  padding: 2px;
  border-radius: 3px;
}
.robot-info-border{
  border: solid 1px;
  border-radius: 3px;
  height: 100%;
}
div /deep/ .ivu-progress {
  height: 100%;
}
div /deep/ .ivu-progress-bg {
  background-color: #5ED268;
  border-radius: 3px;
  height: 100% !important;
}
.battery-bot {
  display: inline-block;
  background-color: #5ED268;
  width: 3px;
  height: 8px;
}
div /deep/ .ivu-progress-outer{
  display: flex;
  height: 100%;
}

div /deep/ .charging .ivu-progress-bg {
  animation-name: add;
  animation-duration: 3s;
  animation-iteration-count: infinite;
}
@keyframes add {
  0% {
    width: 0;
  }
  100% {
    width: 100%;
  }
}
</style>