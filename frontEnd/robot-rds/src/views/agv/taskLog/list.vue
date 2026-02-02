<template>
    <div>
        <page-title></page-title>
        <div class="page-search">
            <div class="page-search-title">查询条件</div>
            <div class="page-search-content">
                <Form ref="taskLog" :model="taskLog" :label-width="100">
                    <FormItem label="MES运单ID" prop="mesWaybillId">
                        <Input v-model.trim="taskLog.mesWaybillId" placeholder="请输入MES运单ID，不超过50字" maxlength="50"></Input>
                    </FormItem>
                    <FormItem label="操作主体" prop="applicationName">
                        <Input v-model.trim="taskLog.applicationName" placeholder="请输入操作主体名称，不超过50字" maxlength="50"></Input>
                    </FormItem>
                    <FormItem label="主体IP" prop="requestIp">
                        <Input v-model.trim="taskLog.requestIp" placeholder="请输入主体IP地址，不超过50字" maxlength="50"></Input>
                    </FormItem>
                    <FormItem label="操作结果" prop="operateSuccess">
                        <Select  v-model="taskLog.operateSuccess"  placeholder="请选择操作结果">
                          <Option v-for="obj in successStates" :key="obj.id" :value="obj.id">{{obj.name}}</Option>
                      </Select>
                    </FormItem>
                </Form>
                <div class="search-btn">
                  <allowBtn allow="allowsearch" size="large" @click="search()">查询</allowBtn>
                  <allowBtn allow="allowreset" size="large" @click="reset">重置</allowBtn>
                  <allowBtn allow="新增" type="success" icon="plus" size="large" @click="modal_add=true">新增</allowBtn>
                </div>
            </div>
             <!-- 查看-->
             <Modal v-model="modal_view" title="查看操作内容">
                <update-modal :id="viewId" type="view"></update-modal>
                <div slot="footer">
                    <Button type="ghost" @click="modal_view=false">关闭</Button>
                </div>
            </Modal>
        </div>

        <div class="page-content">
            <data-grid :option="gridOption" ref="grid">
            </data-grid>
        </div>
    </div>
</template>
<script>
import dataGrid from "@/components/datagrid"
import fontIcon from "@/components/fontIcon"
import updateModal from "./cur"
export default {
    components: { dataGrid, fontIcon, updateModal },
    data: function () {
        var that = this;
        return {
            loading: false,
            id: '', //页面修改赋值
            viewId: '', //页面查看赋值
            taskLog: {
                id: '',
                mesWaybillId: '',
                applicationName: '',
                requestIp: '',
                operateBusiness:'',
                operateTime: '',
                operateContent: '',
                operateSuccess: '',
                operateMessage: '',
            },
            successStates:[
                  {"id":0,"name":"失败"},
                  {"id":1,"name":"成功"},
              ],
            modal_update: false, //修改弹窗
            modal_view: false, //查看弹窗
            modal_add: false, //新增弹窗
            //以上是界面v-model的所有参数
            gridOption: {
                  url: "/service_rms/taskLog/list",
                  auto:false,
                  columns: [ //列配置
                        { "title": "MES运单ID", "key": "mesWaybillId", "align": "center"},
                        { "title": "操作主体", "key": "applicationName", "align": "center"},
                        { "title": "主体IP", "key": "requestIp", "align": "center"},
                        { "title": "操作业务", "key": "operateBusiness", "align": "center"},
                        { "title": "操作时间", "key": "operateTime", "align": "center"}, 
                        { "title": "操作结果", "key": "operateSuccess", "align": "center"},
                        { "title": "操作日志信息", "key": "operateMessage", "align": "center"},
                        { "key": 'id', title: '操作', "align": "center", "width": "100px",
                          render(h, params) {
                              return h('div', [
                              h('allowBtn', {
                                    props: {
                                        type: 'primary',
                                        size: 'small',
                                        allow: '查看操作内容'
                                    },
                                    style: {
                                        marginRight: '5px'
                                    },
                                    on: {
                                        click: () => {
                                            that.toView(params.row)
                                        }
                                    }
                                }, '查看操作内容'),
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
            }
        },
        methods: {
            toView: function(obj) {
                this.viewId = obj.id;
                this.modal_view = true;
            },
            toDelete: function(obj) {
                let that = this;
                this.$Modal.confirm({
                    title: '删除',
                    content: '您确认删除此条数据？',
                    onOk: function() {
                        var idValue = obj.id;
                        var url = "/service_rms/taskLog/delete";
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
            search: function() {
                console.log(JSON.stringify(this.taskLog))
                this.$refs.grid.load({'data' : JSON.stringify(this.taskLog)});
            },
            reset: function() {
                this.taskLog.applicationName="";
                this.taskLog.requestIp="";
                this.taskLog.operateSuccess="";
                this.search();
            },
        },
        mounted: function() {
            this.search();
        }
  }
  </script>