<template>
  <div class="page-content" style="background: #efefef; padding: 15px">
    <div style="height: 100%; background: #fff; position: relative; border-radius: 15px">
      <div class="header">
        <!-- <Button type="primary" @click="toWifiRun">WIFI运行</Button> -->
        <Button type="primary" @click="dialogTableVisiblenoadd = true">WIFI运行</Button>
        <!-- <Button type="primary" @click="toDmsRun">DMS运行</Button> -->
        <Button type="primary" @click="dmsDialogTableVisiblenoadd = true">DMS运行</Button>
        <Button type="primary" @click="save">保存</Button>
        <Button type="primary" @click="dialogTableVisible = true">任务输入参数</Button>
        <Button type="primary" @click="delOne">删除</Button>
        <!-- <Button type="primary">运行</Button> -->
        <Button type="primary" @click="delAll">任务全部删除</Button>

      </div>
      <div class="content-left">
        <Row type="flex" style="height: 100%" :gutter="10">
          <Col span="12">
          <div class="drag-left1">
            <div class="title">机器人运行指令</div>
            <draggable v-model="arr3" @end="end1" :options="{ group: { name: 'itxst', pull: 'clone' }, sort: true }"
              animation="300" :move="onMove" :scroll="true">
              <transition-group>
                <div :class="item.id == 1 ? 'item forbid' : 'item'" v-for="item in arr3" :key="item.id">
                  {{ item.name }}
                </div>
              </transition-group>
            </draggable>
          </div>
          <div class="drag-left1" style='margin-top: 15px;'>
            <div class="title">云台控制指令</div>
            <draggable v-model="arr4" @end="end1" :options="{ group: { name: 'itxst', pull: 'clone' }, sort: true }"
              animation="300" :move="onMove" :scroll="true">
              <transition-group>
                <div :class="item.id == 1 ? 'item forbid' : 'item'" v-for="item in arr4" :key="item.id">
                  {{ item.name }}
                </div>
              </transition-group>
            </draggable>
          </div>
          </Col>
          <Col span="12">
          <div class="drag-left2">
            <div class="title">任务参数</div>
            <draggable v-model="arr2" :group="groupB" animation="300" dragClass="dragClass" ghostClass="ghostClass"
              chosenClass="chosenClass">
              <transition-group :style="style">
                <div class="item" v-for="item in tableData" :key="item.variableName">
                  <div class="item-row">
                    <span>{{ item.label }}</span>
                    <el-button icon="el-icon-plus" @click="addToInput(item.variableName)" size="mini"
                      circle></el-button>
                  </div>
                  <span class="good-text">{{ item.variableName }}</span>
                </div>
              </transition-group>
            </draggable>
          </div>
          <div class="drag-left2" style='margin-top: 15px;'>
            <div class="title">常用参数<Button size="small" type="default" ghost
                @click="dialogTableVisiblecloud = true">获取云台参数</Button></div>
            <draggable v-model="arr2" :group="groupB" animation="300" dragClass="dragClass" ghostClass="ghostClass"
              chosenClass="chosenClass">
              <transition-group :style="style">
                <div class="item" v-for="item in cloudData" :key="item.variableName">
                  <div class="item-row">
                    <span>{{ item.label }}</span>
                    <el-button icon="el-icon-plus" @click="addToInput(item.variableName)" size="mini"
                      circle></el-button>
                  </div>
                  <span class="good-text">{{ item.variableName }}</span>
                </div>
              </transition-group>
            </draggable>
          </div>
          </Col>
        </Row>
      </div>
      <div class="content-right">
        <div class="title">任务工作台</div>
        <!-- {{ taskList }} -->
        <task-item ref="taskitem" v-model="taskList" :activeInputValue="this[this.activeInput]"
          @inputFocus="setInputFocus"></task-item>
      </div>
      <el-dialog title="任务输入参数" :visible.sync="dialogTableVisible">
        <editgrid :tableHeaders="tableHeaders" :initialData="tableData" :showAddButton="true" ref="editGrid"></editgrid>
        <div slot="footer" class="dialog-footer">
          <el-button @click="dialogTableVisible = false">取 消</el-button>
          <el-button type="primary" @click="saveData">确 定</el-button>
        </div>
      </el-dialog>
      <el-dialog title="获取云台参数" :visible.sync="dialogTableVisiblecloud">
        <Row :gutter="16" style="display:flex;align-items: center;text-align:right;margin-bottom:10px">
          <Col span="2">云台ip地址:</Col>
          <Col span="21"><Input v-model="ipAddress" placeholder="请输入内容"></Input></Col>
        </Row>
        <Row :gutter="16" style="display:flex;align-items: center;text-align:right">
          <Col span="2">例子1:</Col>
          <Col span="21"><Input v-model="example1" placeholder="请输入内容"></Input></Col>
        </Row>
        <div slot="footer" class="dialog-footer">
          <el-button @click="dialogTableVisiblecloud = false">取 消</el-button>
          <el-button type="primary" @click="saveToCloudData">确 定</el-button>
        </div>
      </el-dialog>
      <el-dialog title="WIFI运行" :visible.sync="dialogTableVisiblenoadd">
        <editgrid :tableHeaders="tableHeaderss" :initialData="tableData" :showAddButton="false" ref="editGrid">
        </editgrid>
        <div slot="footer" class="dialog-footer">
          <el-button @click="dialogTableVisiblenoadd = false">取 消</el-button>
          <el-button type="primary" @click="toWifiRun()">确 定</el-button>
        </div>
      </el-dialog>

      <el-dialog title="DMS运行" :visible.sync="dmsDialogTableVisiblenoadd">
        <editgrid :tableHeaders="tableHeaderss" :initialData="tableData" :showAddButton="false" ref="editGrid">
        </editgrid>
        <div slot="footer" class="dialog-footer">
          <el-button @click="dmsDialogTableVisiblenoadd = false">取 消</el-button>
          <el-button type="primary" @click="toDmsRun()">确 定</el-button>
        </div>
      </el-dialog>
    </div>
  </div>
</template>
<script>
import draggable from "vuedraggable";
import taskItem from "./task-item.vue";
import editgrid from "@/components/editgrid";
export default {
  name: "task",
  components: { draggable, taskItem, editgrid },
  props: {
    value: {
      type: Array, // 假设这里是一个数组
      required: true,
    },
  },
  data() {
    return {
      ipAddress: '', // 存储云台ip地址的输入
      example1: '', // 存储例子1的输入
      btnvalue: null,
      inputName: null,
      tableHeaders: [
        { prop: "variableName", label: "变量名", type: "input", iseditable: true },
        { prop: "label", label: "标签", type: "input", iseditable: true },
        {
          prop: "dataType",
          label: "类型",
          type: "select",
          options: [
            { label: "字符串", value: "字符串" },
            { label: "布尔", value: "布尔" },
            { label: "整数", value: "整数" },
            { label: "浮点数", value: "浮点数" },
            { label: "JSON对象", value: "JSON对象" },
            { label: "JSON数组", value: "JSON数组" },
          ],
          iseditable: true,
        },
        {
          prop: "isRequired",
          label: "必填",
          type: "select",
          activeInput: 1, // 用于存储当前活跃的输入框的名称
          options: [
            { label: "是", value: "是" },
            { label: "否", value: "否" },
          ],
          iseditable: true,
        },
        {
          prop: "defaultValue",
          label: "默认值",
          type: "input",
          iseditable: true,
        },
      ],
      tableHeaderss: [
        { prop: "variableName", label: "变量名", type: "input", iseditable: false },
        { prop: "label", label: "参数名", type: "input", iseditable: false },
        {
          prop: "defaultValue",
          label: "参数值",
          type: "input",
          iseditable: true,
        },
      ],
      dialogTableVisible: false,
      dialogTableVisiblecloud: false,
      dialogTableVisiblenoadd: false,
      dmsDialogTableVisiblenoadd: false,
      drag: false,
      id: "",
      taskContent: "",
      groupB: {
        name: "site",
        pull: false, //可以拖入
        put: false, //可以拖出
      },
      //定义要被拖拽对象的数组
      arr1: [
        {
          id: "origin-1",
          name: "test_robot",
          parameters: [
            { id: "origin-2", name: "ifAsnc", value: "" },
            { id: "origin-3", name: "taskRecordld", value: "" },
          ],
          children: [],
        },
        {
          id: "origin-4",
          name: "Template",
          parameters: [
            { id: "origin-5", name: "ifAsnc", value: "" },
            { id: "origin-6", name: "taskRecordld", value: "" },
          ],
          children: [],
        },
        {
          id: "origin-7",
          name: "ML_back",
          parameters: [
            { id: "origin-8", name: "ifAsnc", value: "" },
            { id: "origin-9", name: "taskRecordld", value: "" },
          ],
          children: [],
        },
      ],
      arr2: [],
      arr3: [
        {
          id: 1,
          name: "选择执行机器人",
          parameters: [
            {
              id: "vehicle",
              name: "指定机器人",
              uuid: "",
              value: "",
            },
            {
              id: "group",
              name: "指定机器人组",
              uuid: "",
              value: "",
            },
            {
              id: "keyRoute",
              name: "关键路径",
              uuid: "",
              value: "",
            },
          ],
          children: [],
        },
        {
          id: 2,
          name: "机器人路径导航",
          parameters: [
            // {
            //   id: "vehicle", name: '指定机器人', value: ""
            // },
            {
              id: "targetSiteLabel",
              name: "目标站点名",
              uuid: "",
              value: "",
            },
          ],
          children: [],
        },
        {
          id: 3,
          name: "顶升",
          parameters: [
            {
              id: "targetSiteLabel",
              name: "顶升站点名",
              uuid: "",
              value: "",
            },
            {
              id: "jackHeight",
              name: "顶升高度",
              uuid: "",
              value: "",
            },
            {
              id: "modelType",
              name: "模型名称",
              uuid: "",
              value: "",
            }
          ],
          children: [],
        },
        {
          id: 4,
          name: "顶降",
          parameters: [
            {
              id: "targetSiteLabel",
              name: "顶降站点名",
              uuid: "",
              value: "",
            },
            {
              id: "jackHeight",
              name: "顶降高度",
              uuid: "",
              value: "",
            },
          ],
          children: [],
        },
        {
          id: 5,
          name: "执行DO",
          parameters: [
            {
              id: "targetSiteLabel",
              name: "执行站点名",
              uuid: "",
              value: "",
            },
            {
              id: "waitDoId",
              name: "DO编号",
              uuid: "",
              value: "",
            },
            {
              id: "waitDoStatus",
              name: "等待状态",
              uuid: "",
              value: "",
            },
            {
              id: "waitDoTime",
              name: "等待时间",
              uuid: "",
              value: "",
            },
          ],
          children: [],
        },
        {
          id: 9,
          name: "等待DI",
          parameters: [
            {
              id: "targetSiteLabel",
              name: "等待站点名",
              uuid: "",
              value: "",
            },
            {
              id: "waitDiId",
              name: "DI编号",
              uuid: "",
              value: "",
            },
            {
              id: "waitDiStatus",
              name: "触发状态",
              uuid: "",
              value: "",
            },
            {
              id: "waitDiTime",
              name: "等待时间",
              uuid: "",
              value: "",
            },
          ],
          children: [],
        },
        {
          id: 6,
          name: "叉货",
          parameters: [
            {
              id: "targetSiteLabel",
              name: "叉货站点名",
              uuid: "",
              value: "",
            },
            {
              id: "forkLiftHeight",
              name: "叉货高度",
              uuid: "",
              value: "",
            },
          ],
          children: [],
        },
        {
          id: 7,
          name: "辊筒下料",
          parameters: [
            {
              id: "targetSiteLabel",
              name: "下料站点名",
              uuid: "",
              value: "",
            },
          ],
          children: [],
        },
        {
          id: 8,
          name: "辊筒人工上料",
          parameters: [
            {
              id: "targetSiteLabel",
              name: "上料站点名",
              uuid: "",
              value: "",
            },
          ],
          children: [],
        },
        {
          id: 10,
          name: "辊筒自动上料",
          parameters: [
            {
              id: "targetSiteLabel",
              name: "上料站点名",
              uuid: "",
              value: "",
            },
          ],
          children: [],
        },
        {
          id: 11,
          name: "叉货BinTask",
          parameters: [
            {
              id: "targetSiteLabel",
              name: "叉货站点名",
              uuid: "",
              value: "",
            },
            {
              id: "forkLoadOrUnload",
              name: "load/unload",
              uuid: "",
              value: "",
            },
          ],
          children: [],
        },
        {
          id: 12,
          name: "通用Modbus读写",
          parameters: [

            {
              id: "targetSiteLabel",
              name: "读写站点名",
              uuid: "",
              value: "",
            },
            {
              id: "ipModbusHost",
              name: "Host",
              uuid: "",
              value: "",
            },
            {
              id: "port",
              name: "Port",
              uuid: "",
              value: "",
            },
            {
              id: "ipSlaveId",
              name: "Slave Id",
              uuid: "",
              value: "",
            },
            {
              id: "offset",
              name: "寄存器偏移地址",
              uuid: "",
              value: "",
            },
            {
              id: "operationType",
              name: "操作类型",
              uuid: "",
              value: "",
            },
            {
              id: "modbusWriteValue",
              name: "写入值",
              uuid: "",
              value: "",
            },

          ],
        },

        {
          id: 13,
          name: "通用Modbus等待",
          parameters: [
            {
              id: "targetSiteLabel",
              name: "等待站点名",
              uuid: "",
              value: "",
            },
            {
              id: "ipModbusHost",
              name: "Host",
              uuid: "",
              value: "",
            },
            {
              id: "port",
              name: "Port",
              uuid: "",
              value: "",
            },
            {
              id: "ipSlaveId",
              name: "Slave Id",
              uuid: "",
              value: "",
            },
            {
              id: "offset",
              name: "寄存器偏移地址",
              uuid: "",
              value: "",
            },
            {
              id: "modbusTargetValue",
              name: "目标值",
              uuid: "",
              value: "",
            },

          ],
        },

        {
          id: 14,
          name: "检查库位",
          parameters: [
            {
              id: "targetSiteLabel",
              name: "库位站点名",
              uuid: "",
              value: "",
            }
          ],
          children: [],
        },
        {
          id: 15,
          name: "BinTask",
          parameters: [
            {
              id: "targetSiteLabel",
              name: "目标站点名",
              uuid: "",
              value: "",
            },
            {
              id: "locationLabel",
              name: "库位名",
              uuid: "",
              value: "",
            },
            {
              id: "loadOrUnload",
              name: "load/unload",
              uuid: "",
              value: "",
            },
          ],
          children: [],
        },
        {
          id: 16,
          name: "自定义动作",
          parameters: [
            {
              id: "customizedActions",
              name: "动作脚本",
              uuid: "",
              value: "",
            },
            {
              id: "targetSiteLabel",
              name: "动作站点名",
              uuid: "",
              value: "",
            },
          ],
        },
      ],
     
      taskList: [],
      tableData: {},
      cloudData: [
        { variableName: 'var1', label: 'Variable 1' },
        { variableName: 'var2', label: 'Variable 2' },
        { variableName: 'var3', label: 'Variable 3' },
        // Add more data as needed
      ],
      taskParameterList: [],
      value3: false,
      styles: {
        height: "calc(100% - 55px)",
        overflow: "auto",
        paddingBottom: "53px",
        position: "static",
      },
      task: {
        id: "",
        taskName: "",
        taskContent: "",
        taskParameter: "",
        enabledState: "",
      },
      formData: {
        name: "",
        url: "",
        owner: "",
        type: "",
        approver: "",
        date: "",
        desc: "",
      },
      style: "min-height:120px;display: block;",
    };
  },

  created() {
    this.id = this.$route.params.id;
    if (this.id) {
      this.getTask();
    }
    this.fetchTableData(); // 在页面加载时调用接口获取数据
  },
  mounted() { },
  beforeDestroy() { },
  methods: {
    saveToCloudData() {
      // 将输入的数据添加到 cloudData 数组中
      this.cloudData.push({ variableName: this.example1, label: this.ipAddress, }); // 替换 'newVar' 和 'New Variable' 为实际的变量名和标签
      // 你可以添加更多字段到对象中，如果需要的话
      // 清空输入
      this.ipAddress = '';
      this.example1 = '';
      // 关闭对话框
      this.dialogTableVisiblecloud = false;
    },
    fetchTableData() {
      // 调用接口获取数据
      var url = "/service_rms/task/getTaskParameter";
      this.$ajax
        .post(url, { id: this.id })
        .then((res) => {
          this.tableData = res.data.data; // 将接口返回的数据赋值给tableData
        })
        .catch((error) => {
          console.error(error);
        });
    },
    setFocusOnChildInput() {
      this.$refs.taskitem.$refs.inputField.focus();
    },
    getTaskIndex(list, value) {
      let index = null;
      list.forEach((item, index) => {
        item.parameters.forEach((item, index2) => {
          if (item.uuid === this.inputName) {
            item.value = value;
          }
        });
      });
      return index;
    },
    addToInput(value) {
      this[this.activeInput] = value;
      // this.btnvalue = value
      console.log("全部的数据", this.taskList);
      if (!this.inputName) {
        alert("青先选一个按钮");
        return;
      }
      this.taskList.forEach((item, index) => {
        if (item.children.length > 0) {
          const index3 = this.getTaskIndex(item.children, value);
          if (index3) {
            this.taskList[index].children[index3].value = value;
          }
        }
      });
      this.taskList.forEach((item, index) => {
        item.parameters.forEach((item, index2) => {
          if (item.uuid === this.inputName) {
            this.taskList[index].parameters[index2].value = value;
          }
        });
      });
    },
    setInputFocus(item) {
      console.log(`数据来了----${item}`);
      this.inputName = item;
    },
    saveData() {
      // 获取需要保存的数据
      const newData = this.$refs.editGrid.form.item.map((item) => {
        const data = {};
        // 遍历表头
        this.tableHeaders.forEach((header) => {
          // 将每个prop的值添加到一个新的对象data中
          data[header.prop] = item[header.prop];
        });
        return data;
      });

      // 检查空值和undefined
      for (const item of newData) {
        for (const key in item) {
          // 如果存在空值或undefined，弹出错误提示框
          if (item[key] === "" || item[key] === undefined) {
            this.$message.error("请填写所有字段");
            return;
          }
        }
      }
      // 将处理后的数据赋值给tableData
      this.tableData = newData;
      this.dialogTableVisible = false;

      var url = "/service_rms/task/saveTaskParameter";
      this.$ajax
        .post(url, { data: JSON.stringify(this.tableData), id: this.id })
        .then((res) => {
          console.log(res);
          if (res.data.code == "200") {
            this.$Message.success("保存成功");
          } else {
            this.$Message.error("保存失败");
          }
        })
        .catch((error) => {
          if (!error.url) {
            console.info(error);
          }
        });
      console.log(this.tableData);
    },
    taskInput() {
      this.dialogTableVisible = true;
    },
    delAll() {
      this.taskList = [];
    },
    delOne() {
      let dlist = this.taskList.checkAllGroup;
      let slist = this.taskList;
      dlist.forEach((item) => {
        slist.forEach((item2, index) => {
          if (item == item2.name) {
            slist.splice(index, 1);
          }
        });
      });
    },
    toWifiRun: function () {
      // 获取需要保存的数据
      const newData = this.$refs.editGrid.form.item.map((item) => {
        const data = {};
        // 遍历表头
        this.tableHeaders.forEach((header) => {
          // 将每个prop的值添加到一个新的对象data中
          data[header.prop] = item[header.prop];
        });
        return data;
      });

      // 检查空值和undefined
      for (const item of newData) {
        for (const key in item) {
          // 如果存在空值或undefined，弹出错误提示框
          if (item[key] === "" || item[key] === undefined) {
            this.$message.error("请填写所有字段");
            return;
          }
        }
      }
      // 将处理后的数据赋值给taskParameterList
      this.taskParameterList = newData;
      this.dialogTableVisiblenoadd = false;
      let that = this;
      var url = "/service_rms/task/runTask";
      that.$ajax
        .post(url, {
          taskId: that.id,
          taskParameterList: JSON.stringify(that.taskParameterList),
        })
        .then((res) => {
          if (res.data.code != 200) {
            this.$Message.error(res.data.msg);
          } else {
            this.$Message.success("运行成功");
          }
        })
        .catch((error) => {
          if (!error.url) {
            console.info(error);
          }
        });
    },

    toDmsRun: function () {
      // 获取需要保存的数据
      const newData = this.$refs.editGrid.form.item.map((item) => {
        const data = {};
        // 遍历表头
        this.tableHeaders.forEach((header) => {
          // 将每个prop的值添加到一个新的对象data中
          data[header.prop] = item[header.prop];
        });
        return data;
      });

      // 检查空值和undefined
      for (const item of newData) {
        for (const key in item) {
          // 如果存在空值或undefined，弹出错误提示框
          if (item[key] === "" || item[key] === undefined) {
            this.$message.error("请填写所有字段");
            return;
          }
        }
      }
      // 将处理后的数据赋值给taskParameterList
      this.taskParameterList = newData;
      this.dmsDialogTableVisiblenoadd = false;
      let that = this;
      var url = "/service_rms/task/runDmsTask1";
      that.$ajax
        .post(url, {
          id: that.id,
          taskParameterList: JSON.stringify(that.taskParameterList),
        })
        .then((res) => {
          if (res.data.code != 200) {
            this.$Message.error(res.data.msg);
          } else {
            this.$Message.success("运行成功");
          }
        })
        .catch((error) => {
          if (!error.url) {
            console.info(error);
          }
        });
    },

    save() {
      let that = this;
      this.$Modal.confirm({
        title: "保存任务",
        content: "您确定要保存任务？",
        onOk: function () {
          var url = "/service_rms/task/saveTaskEdit";
          that.$ajax
            .post(url, { data: JSON.stringify(that.taskList), id: that.id })
            .then((res) => {
              if (res.data.code != 200) {
                this.$Message.error(res.data.msg);
              } else {
                this.$Message.success("保存成功");
              }
            })
            .catch((error) => {
              if (!error.url) {
                console.info(error);
              }
            });
        },
      });
    },
    getTask() {
      this.$ajax
        .post("/service_rms/task/findById", {
          id: this.id,
        })
        .then(
          (res) => {
            this.task = res.data.data;
            console.log("res", this.task.taskContent);
            this.taskList = JSON.parse(this.task.taskContent);
          },
          (error) => {
            console.log(error);
          }
        );
    },
  },
};
</script>

<style scoped>
.item {
  margin-bottom: 10px;
  /* 调整每个项目之间的间距 */
}

.item-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header {
  position: absolute;
  margin-top: 20px;
  margin-left: 20px;
  top: 0px;
  left: 0px;
  right: 0px;
  height: 35px;
}

.content-left {
  position: absolute;
  top: 70px;
  left: 20px;
  bottom: 20px;
  width: 600px;
}

.content-right {
  position: absolute;
  top: 70px;
  left: 640px;
  bottom: 20px;
  right: 20px;
  border-radius: 15px;
  border: 1px dashed #cccccc;
  overflow-y: auto;
  /* background: #F2F7FF; */
}

.ivu-btn {
  border-radius: 5px;
}

.drag-left1 {
  height: 49%;
  overflow-y: auto;
  background: #f2f7ff;
  border-radius: 15px;
}

.drag-left2 {
  height: 49%;
  overflow-y: auto;
  background: #f2f7ff;
  border-radius: 15px;
}

.demo-drawer-footer {
  width: 100%;
  position: absolute;
  bottom: 0;
  left: 0;
  border-top: 1px solid #e8e8e8;
  padding: 10px 16px;
  text-align: right;
  background: #fff;
}

/*定义要拖拽元素的样式*/
.ghostClass {
  background-color: rgb(207, 204, 204) !important;
}

.chosenClass {
  background-color: rgb(223, 220, 220) !important;
  opacity: 1 !important;
}

.dragClass {
  background-color: rgb(141, 185, 228) !important;
  opacity: 1 !important;
  box-shadow: none !important;
  outline: none !important;
  background-image: none !important;
}

.itxst {
  margin: 10px;
}

.title {
  display: flex;
  justify-content: space-between;
  padding: 6px 12px;
  font-size: 20px;
}

.item {
  padding: 6px 12px;
  margin: 0px 10px 0px 10px;
  border: solid 1px #d0dceb;
  background-color: #fff;
  border-radius: 10px;
}

.item:hover {
  background-color: #fdfdfd;
  cursor: move;
}

.item+.item {
  border-top: none;
  margin-top: 6px;
}

.inner-draggable {
  margin: 10px;
  border: 1px solid #d0dceb;
  background-color: #fff;
  border-radius: 10px;
}

.asd {
  margin: 10px;
  border: 1px solid #d0dceb;
  background-color: #fff;
  border-radius: 10px;
}

.inner-draggable {
  margin-top: 5px;
  padding: 5px;
  border: 1px solid #eee;
}

.good-text {
  font-size: 14px;
  color: #0066ffd9;
}
</style>