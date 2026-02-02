<template>
  <div class="page-content" style="background:#efefef;padding:10;">
    <div class="add-robot">
      <Form ref="humCompany" :model="robot" :class="{'form-view':type=='view'}" :rules="ruleValidate" inline>
        <Form-item prop="ip" style="height: 10px;width: 500px;">
          <Input v-model="robot.ip" class="input-id" placeholder=" 请输入 IP"></Input>
        </Form-item>
      </Form>
      <Button type="primary" icon="ios-refresh" class="update" :loading="loading" @click="refreshData">查询</Button>
      <Button type="primary" icon="ios-refresh" class="update" @click="reset">重置</Button>
      <Button type="primary" @click="modal1 = true"><span class="jia"> +</span> 添加设备</Button>
    </div>
    <div class="robot-body">
      <div class="body-title">
        <div>计算机名:</div>
        <div>DESKTOP-Q3LJSJS</div>
        <div style="margin-left: 10PX;">机器码:</div>
        <div>69AC1B3D-6EA6-3B98-9093-54D43FD7078a</div>
      </div>
    </div>
    <div>
      <Row type="flex" :gutter="16">
        <Col span="3" v-model="social" v-for="(item,index) in deviceList" :key="index">
        <div class="box-title">

          <img src="../../../static/images/home/robot.png" style="height: 20px;width: 20px;" />
          <div>{{item.online?"在线":"离线"}}</div>
          <div :class="item.online?'green':'red'" style="margin-left: 15px;"></div>
          <div>机器人</div>
          <img src="../../../static/images/home/del.png" style="height: 20px;width: 20px;" @click="del(item.id)" />
        </div>
        <div class="box-content">
          <div class="card-title">
            <div style="margin-left: 10px;">{{item.currentIp}}</div>
            <div class="truebody">实体</div>
          </div>
          <div style="margin-top: 20px;">
            <div style="line-height: 20px;margin-left: 15px;color: #fff;">地图:{{item.currentMap}}</div>
            <div style="line-height: 20px;margin-left: 15px;color: #fff;">名称:{{item.vehicleId}}</div>
            <div style="line-height: 20px;margin-left: 15px;color: #fff;">名称:{{item.typeName}}</div>
            <div style="line-height: 20px;margin-left: 15px;color: #fff;">备注:{{item.robotNote}}</div>
            <div style="line-height: 20px;margin-left: 15px;color: #fff;">版本:{{item.version}}</div>
          </div>
          <div class="card-buttom " @click="openDevices(item.currentIp,index,item.online)" v-if="flag">
            <div class="connect ">
              {{item.online?"断开连接":"开始连接"}}
            </div>
          </div>
        </div>
        </Col>
      </Row>
    </div>
    <Modal v-model="modal1" title="添加新设备" @on-ok="submit" @on-cancel="modal1 = false">
      <Form :model="formItem" :label-width="90" :rules="ruleValidate">
        <FormItem label="IP" prop="ip">
          <Input v-model="formItem.ip" placeholder="请输入ip"></Input>
        </FormItem>
        <Form-item label="机器人类型" prop="robotType">
          <Select v-model="formItem.typeName" placeholder="请选择机器人类型">
            <Option v-for="item in robotTypeList" :value="item.typeName" :key="item.id">{{item.typeName}}</Option>
          </Select>
        </Form-item>
      </Form>
    </Modal>
  </div>
</template>

<script>
import dataease from '@/components/dataease.vue';
import { addDevice, findDeviceListByIp, getDeviceList, delDevice, openDevice, overDevice } from '@/api/auth';

export default {
  name: 'home',
  components: {
    dataease, findDeviceListByIp
  },
  data () {
    return {
      value: "",
      formItem: {
        ip: "",
        typeName: ""
      },
      states: [
        { "id": 0, "name": "否" },
        { "id": 1, "name": "是" },
      ],
      robotTypeList: [],
      modal1: false,
      single: false,
      flag: true,
      deviceList: [],
      flag: true,
      social: [],
      state: false,
      robotData: [],
      robot: {
        ip: ""
      },
      loading: false,
      ruleValidate: {
        ip: [
          { validator: this.checkIp, trigger: 'blur' },
        ],
      },

    }
  },
  methods: {

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
    reset: function () {
      this.robot.ip = "";
      this.loading = false;
      this.getData();
    },
    openDevices (ip, index, online) {
      console.log("online", online)
      if (!online) {
        openDevice({ ip })
          .then((res0) => {
            let res = res0.data;
            if (res.code == 200) {
              this.getData();
              this.getRealTimeInfo();
              this.$Message.success("链接设备成功");
            }
            else {
              this.getData();
              this.$Message.error("链接设备失败");
            }
          }, (e) => {
            this.$Message.error("异常，请稍候再试！")
          })
      } else {
        overDevice({ ip })
          .then((res0) => {
            let res = res0.data;
            if (res.code == 200) {
              this.getData();
              this.$Message.success("断开设备成功");
            }
            else {
              this.$Message.error("断开链接失败！")
              this.$Message.error(res.msg);
            }
          }, (e) => {
            this.$Message.error("异常，请稍候再试！")
          })
      }

    },

    //机器人连接成功，即获取机器人实时信息
    getRealTimeInfo: function () {
      this.$ajax.post('/service_rms/robotRealTime/runInfo')
        .then((res) => {
          if (res.data.code != 200) {
            this.$Message.error("获取机器人实时在线数据失败");
          }
        })
    },


    allPc () {
      this.single = this.single ? false : true;
      if (this.social.length > 0) {
        this.social = [];
      } else {
        this.social = ["192.168.1.1"];
      }
    },
    del (id) {
      delDevice({ id })
        .then((res0) => {
          let res = res0.data;
          if (res.code == 200) {
            this.getData()
            this.$Message.success("删除机器人成功！");
          }
          else {
            this.$Message.error(res.msg);
          }
        }, (e) => {
          this.loading = false;
          this.$Message.error("删除机器人失败，请重新尝试")
        })
    },

    submit () {
      let ip = this.formItem.ip//传给接口
      let typeName = this.formItem.typeName//传给接口
      var data = { "ip": ip, "typeName": typeName }
      addDevice(data)
        .then((res0) => {
          let res = res0.data;
          if (res.code == 200) {
            this.$Message.success("添加机器人成功！");
            this.getData()
          }
          else {

            this.$Message.error("未找到机器人，请检查IP！");
          }
        }, (e) => {

          this.$Message.error("未找到机器人，请检查IP！")
        })
    },
    refreshData () {
      this.loading = true;
      var robotIp = this.robot.ip;
      this.$ajax.post("/service_rms/robotConnect/listByIp", { 'robotIp': robotIp })
        .then((res) => {
          console.log("resd", res)
          if (res.data.code == 200) {
            this.loading = false;
            let list = res.data.data || []
            list.forEach(item => {
              item.state = true
            });
            this.deviceList = list;
          } else {
            this.loading = false;
            this.$Message.error("获取机器人信息失败！")
          }

        })
        .catch((e) => {
          this.loading = false;
          this.$Message.error("异常，请稍候再试！")
        })

    },
    getData () {
      getDeviceList({})
        .then((res) => {
          let list = res.data.data || []
          list.forEach(item => {
            item.state = true
          });
          this.deviceList = list
          console.log("this.robotTypeList", list)


        }, (e) => {
          this.loading = false;
          msg && msg();
          this.$Message.error("异常，请稍候再试！")
        })
    },
    getRobotTypeList: function () {
      this.$ajax.post("/service_rms/robotConnect/typeList")
        .then((res) => {
          if (res.data.code == 200) {
            this.robotTypeList = res.data.data;
          }
        })
        .catch((error) => {
          if (!error.url) { console.info(error); }
        });
    },

  },
  mounted () {
    this.getData();
    this.getRobotTypeList();
    this.state = sessionStorage.getItem('flag') ? sessionStorage.getItem('flag') : false;
    console.log("sessionStorage", this.state)
  },
  created () {

  },
  beforeDestroy () {

  }
}
</script>

<style scoped>
.add-robot {
  display: flex;
  align-items: center;
}

.add-robot >>> .ivu-input {
  width: 98%;
  border-radius: 15px;
}

.add-robot >>> .ivu-btn {
  margin-right: 10px;
}

.robot-body {
  display: flex;
  align-items: center;
  justify-content: space-between;
  color: black;
  margin-top: 10px;
}

.body-title {
  display: flex;
  align-items: center;
}

.robot-box {
  display: flex;
  flex-wrap: wrap;
  margin: 20px;
}

.box-title {
  display: flex;
  align-items: center;
  justify-content: space-around;
  padding: 6px;
  color: #fff;
  background: #3b4861;
}

.red {
  width: 15px;
  height: 15px;
  border-radius: 50%;
  background: red;
}

.green {
  width: 15px;
  height: 15px;
  border-radius: 50%;
  background: green;
}

.box-content {
  background: #666;
  padding: 5px;
}

.card-title {
  display: flex;
  align-items: center;
  color: #fff;
}

.truebody {
  margin-left: 10px;
  padding: 1px 3px;
  color: #33cccc;
  border: solid 1px #33cccc;
  border-radius: 3px;
  font-size: 10px;
}

.card-buttom {
  position: relative;
  right: 0px;
  width: 400px;
  margin-top: 10px;
}

.connect {
  display: flex;
  align-items: center;
  background: #515151;
  width: 30%;
  justify-content: center;
  padding: 5px;
  border-radius: 3px;
  border-color: gray;
  color: #fff;
  cursor: pointer;
}
</style>