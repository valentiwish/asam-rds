<template>
  <div>
      <page-title></page-title>
      <div class="page-search">
          <div class="page-search-title">查询条件</div>
          <div class="page-search-content">
              <Form ref="task" :model="task" :label-width="100">
                  <FormItem label="任务ID" prop="id">
                      <Input v-model.trim="task.id" placeholder="请输入任务id，不超过50字" maxlength="50"></Input>
                  </FormItem>
                  <FormItem label="任务名称" prop="taskName">
                      <Input v-model.trim="task.taskName" placeholder="请输入任务名称，不超过30字" maxlength="30"></Input>
                  </FormItem>
                  <FormItem label="循环执行" prop="state">
                      <Select  v-model="task.enabledState"  placeholder="请选择是否启用" @on-select="selectState">
                          <Option v-for="obj in states" :key="obj.id" :value="obj.id">{{obj.name}}</Option>
                      </Select>
                  </FormItem>
              </Form>
              <div class="search-btn">
                  <allowBtn allow="allowsearch" size="large" @click="search()">查询</allowBtn>
                  <allowBtn allow="allowreset" size="large" @click="reset">重置</allowBtn>
                  <allowBtn allow="新增" type="success" icon="plus" size="large" @click="modal_add=true">新增</allowBtn>
              </div>
          </div>
      </div>
      <div class="page-content">
          <data-grid :option="gridOption" ref="grid"></data-grid>
      </div>
      <!-- 新增-->
      <Modal v-model="modal_add" title="新增任务">
        <update-modal ref="addform" type="add"></update-modal>
        <div slot="footer">
            <Button type="primary" class="ivu-btn ivu-btn-primary" @click="save">保存</Button>
            <Button type="ghost" class="ivu-btn ivu-btn-primary" @click="close">关闭</Button>
        </div>
      </Modal>
      <!-- 更新-->
      <Modal v-model="modal_update" title="修改任务">
        <update-modal :id="id" ref="updateform" @changeVal="changeVal"></update-modal>
        <div slot="footer">
            <Button type="primary" class="ivu-btn ivu-btn-primary" @click="update" :loading="loading">保存</Button>
            <Button type="ghost" @click="modal_update=false">关闭</Button>
        </div>
      </Modal>
     <!-- WIFI运行-->
      <el-dialog title="WIFI运行" :visible.sync="dialogTableVisiblenoadd">
        <editgrid
          :tableHeaders="tableHeaderss"
          :initialData="tableData"
          :showAddButton="false"
          ref="editGrid"
        ></editgrid>
        <div slot="footer" class="dialog-footer">
          <el-button @click="dialogTableVisiblenoadd = false">取 消</el-button>
          <el-button type="primary" @click="wifiRun(id)">确 定</el-button>
        </div>
      </el-dialog>

       <!-- DMS运行-->
       <el-dialog title="DMS运行" :visible.sync="dmsDialogTableVisiblenoadd">
        <editgrid
          :tableHeaders="tableHeaderss"
          :initialData="tableData"
          :showAddButton="false"
          ref="editGrid"
        ></editgrid>
        <div slot="footer" class="dialog-footer">
          <el-button @click="dmsDialogTableVisiblenoadd = false">取 消</el-button>
          <el-button type="primary" @click="dmsRun(id)">确 定</el-button>
        </div>
      </el-dialog>

  </div>
</template>
<script>
import dataGrid from "@/components/datagrid"
import fontIcon from "@/components/fontIcon"
import updateModal from "./cur"
import editgrid from "@/components/editgrid"
export default {
  components: { dataGrid, fontIcon, updateModal, editgrid},
      data: function() {
          var that = this;
          return {
              id: '', //页面修改赋值
              viewId: '', //页面查看赋值
              task: {
                  id: '',
                  taskNumbe:'',
                  taskName: '',
                  taskContent: '',
                  enabledState: '',
                  priority: '',
              },
              states:[
                  {"id":0,"name":"否"},
                  {"id":1,"name":"是"},
              ],
              priorityState:[
                  {"id":1,"name":"低优先级"},
                  {"id":2,"name":"中优先级"},
                  {"id":3,"name":"高优先级"}
              ],
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
              taskParameterList: [],
              tableData: [],
              dialogTableVisiblenoadd: false,
              dmsDialogTableVisiblenoadd: false,
              modal_add:false,
              modal_update: false, //修改弹窗
              modal_view: false, //查看弹窗
              modal_add: false, //新增弹窗
              taskIdCheckList:[],
              gridOption: {
                  url: "/service_rms/task/list",
                  auto:false,//该页面是否自动加载，不会自动调用list方法
                  columns: [ //列配置
                      { "title": "任务序号", "key": "taskNumber", "align": "center", "width": "120px"},
                      { "title": "任务ID", "key": "id", "align": "center", "width": "300px"},
                      { "title": "任务名称", "key": "taskName", "align": "center"},
                      { "title": "备注", "key": "remark", "align": "center", "width": "200px"},
                      { "title": "优先级", "key": "version", "align": "center","width": "150px",
                          render: function(h, params) {
                              if (params.row.priority <= 1) {
                                return "低优先级";
                              } else if (params.row.priority == 2) {
                                return "中优先级";
                              } else {
                                return "高优先级";
                              }
                          }
                      },
                      { "title": "版本描述", "key": "versionDescription", "align": "center" },
                      { "title": "版本", "key": "version", "align": "center","width": "80px"},
                    //   { "title": "循环执行", "key": "enabledState", "align": "center" ,
                    //       render: function(h, params) {
                    //           return params.row.enabledState?"是":"否";
                    //       }
                    //   },
                      { "title": '循环执行', "key": 'enabledState', "align": "center", "width": "100px",
                            render(h, params) {
                                return h("i-switch", {
                                props: {
                                    value: params.row.enabledState == 1 ? true:false,
                                    disabled: false,
                                },
                                
                                on: {
                                    "on-change": (flag) => {
                                        params.row.enabledState = flag==true ? 1:0;
                                        that.updateLoopState(params.row);
                                    }
                                }
                                })
                            }
                        },
                      { "key": 'id', title: '操作', "align": "center", "width": "400px",
                          render(h, params) {
                              return h('div', [
                                h('allowBtn', {
                                      props: {
                                          type: 'info',
                                          size: 'small',
                                          allow: "修改"
                                      },
                                      style: {
                                          marginRight: '5px'
                                      },
                                      on: {
                                          click: () => {
                                              that.toUpdate(params.row)
                                          }
                                      }
                                  }, '修改'),
                                  h('allowBtn', {
                                      props: {
                                          type: 'info',
                                          size: 'small',
                                          allow: "WIFI运行"
                                      },
                                      style: {
                                          marginRight: '5px'
                                      },
                                      on: {
                                          click: () => {
                                              that.toWifiRun(params.row)
                                          }
                                      }
                                  }, 'WIFI运行'),
                                  h('allowBtn', {
                                      props: {
                                          type: 'info',
                                          size: 'small',
                                          allow: "DMS运行"
                                      },
                                      style: {
                                          marginRight: '5px'
                                      },
                                      on: {
                                          click: () => {
                                              that.toDmsRun(params.row)
                                          }
                                      }
                                  }, 'DMS运行'),
                                  h('allowBtn', {
                                      props: {
                                          type: 'primary',
                                          size: 'small',
                                          allow: '编辑',
                                      },
                                      style: {
                                          marginRight: '5px'
                                      },
                                      on: {
                                          click: () => {
                                              that.edit(params.row)
                                          }
                                      }
                                  }, "编辑"),
                                  h('allowBtn', {
                                      props: {
                                          type: 'info',
                                          size: 'small',
                                          allow: '删除',
                                      },
                                      style: {
                                          marginRight: '5px'
                                      },
                                      on: {
                                          click: () => {
                                              that.toDelete(params.row)
                                          }
                                      }
                                  }, "删除"),
                              ])
                          }
                      }
                  ],
                  "loadFilter": function (data) {
                      return data.data;
                  }
              },
              limitCardDate: {
                  disabledDate(date) {
                      return date && date.valueOf() > Date.now();
                  }
              },
              option: {
                  "server": "/service_rms/goods/uploadData",
                  auto: true,
                  accept: {
                      title: 'Excel',
                      extensions: 'xls,xlsx',
                      mimeTypes: 'application/excel'
                  },
                  type:"select",
                  uploadAccept: this.uploadAccept,
                  fileNumLimit: 1,
                  showUploadList: false,
                  showFileList: true
              }                
          }
      },
      mounted: function() {
          this.task.id = this.$route.params.taskId ? this.$route.params.taskId : "";
          this.search();
      },
      methods: {
          close:function(){
              this.modal_add=false;
              this.$refs.addform.scanner.scannerNumber='';
              this.$refs.addform.scanner.scannerIp= '';    
          },

        fetchTableData(id) {
            return new Promise((resolve, reject) => {
                var url = "/service_rms/task/getTaskParameter";
                this.$ajax
                .post(url, { id: id })
                .then((res) => {
                    this.id = id;
                    this.tableData = res.data.data;
                    resolve(); // 请求成功后使用resolve
                })
                .catch((error) => {
                    console.error(error);
                    reject(error); // 请求失败后使用reject
                });
            });
        },

        async toWifiRun(obj) {
            await this.fetchTableData(obj.id); // 使用await等待fetchTableData方法完成
            console.log("this.tableData", this.tableData);
            this.dialogTableVisiblenoadd = true;
            this.search();
        },

        wifiRun: function (id) {
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
                that.$ajax.post(url, { taskId: id, taskParameterList: JSON.stringify(that.taskParameterList) })
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

        async toDmsRun(obj) {
            await this.fetchTableData(obj.id); // 使用await等待fetchTableData方法完成
            console.log("this.tableData", this.tableData);
            this.dmsDialogTableVisiblenoadd = true;
            this.search();
        },

        dmsRun: function (id) {
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
            var url = "/service_rms/task/runDmsTaskByInner";
                that.$ajax.post(url, { taskId: id, taskParameterList: JSON.stringify(that.taskParameterList) })
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

        //更新机器人任务循环执行状态
          updateLoopState:function(obj){
            console.info(obj);
            let that = this;
            var url = "/service_rms/task/updateLoopState";
            console.info(url);
            this.$ajax.post(url, { "id": obj.id, "enabledState": obj.enabledState })
                    .then((res) => {
                        console.info("res",res);
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

          edit:function(obj){
              console.log(obj)
              this.$router.push("/agv/taskEdit/"+obj.id);
          },
          toView: function(obj) {
              this.viewId = obj.id;
              this.modal_view = true;
          },
          toUpdate: function(obj) {
              this.id = obj.id;
              this.modal_update = true;
          },
          toDelete: function(obj) {
              let that = this;
              this.$Modal.confirm({
                  title: '删除',
                  content: '您确认删除此条数据？',
                  onOk: function() {
                      var idValue = obj.id;
                      var url = "/service_rms/task/delete";
                      this.$ajax.post(url, { 'id': idValue })
                          .then((res) => {
                              that.search();
                          })
                          .catch((error) => {
                              if(!error.url){console.info(error);}
                          })
                  }
              })
          },
          selectState:function(obj){
              this.task.enabledState=obj.value;
              console.log(this.task)
          },
          search: function() {
            //   this.task.id = this.$router.query.data;
            //   console.log("this.task.id",this.$router.query.data)
              this.$refs.grid.reLoad({'data' : JSON.stringify(this.task)});
          },
          reset: function() {
              this.task.id="";
              this.task.taskName="";
              this.task.enabledState="";
              this.search();
          },
          save: function(cb) {
              this.$refs['addform'].save((flag) => {
                  if (flag == true) {
                      this.modal_add = false;
                      this.loading=false;
                      this.$refs.grid.reLoad({});
                  }
              });
          },
          update: function(cb) {
              this.$refs['updateform'].save((flag) => {
                  if (flag == true) {
                      this.modal_update = false;
                      this.$refs.grid.reLoad({});
                      this.viewId = "";
                      this.id = "";
                  }
              });
          },
          //加载旋转方法
          changeVal:function(){
             this.loading=false;
          },
      }
}
</script>

