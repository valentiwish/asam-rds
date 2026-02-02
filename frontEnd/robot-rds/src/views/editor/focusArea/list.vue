<template>
    <div>
        <page-title></page-title>
        <div class="page-search">
            <div class="page-search-title">查询条件</div>
            <div class="page-search-content">
                <Form ref="robotDmsEditor" :model="robotDmsEditor" :label-width="100">
                    <FormItem label="区域名称" prop="areaName">
                        <Input v-model.trim="robotDmsEditor.areaName" placeholder="请输入区域名称，不超过50字" maxlength="50"></Input>
                    </FormItem>
                    <FormItem label="区域中心点" prop="areaCenterPoint">
                        <Input v-model.trim="robotDmsEditor.areaCenterPoint" placeholder="请输入区域中心点，不超过50字" maxlength="50"></Input>
                    </FormItem>
                    <FormItem label="区域类型" prop="areaType">
                      <Select  v-model="robotDmsEditor.areaType"  placeholder="请选择区域类型" @on-select="selectAreaType">
                          <Option v-for="obj in areaType" :key="obj.id" :value="obj.id">{{obj.name}}</Option>
                      </Select>
                    </FormItem>
                    <FormItem label="区域占用状态" prop="occupiedStatus">
                      <Select  v-model="robotDmsEditor.occupiedStatus"  placeholder="请选择区域占用状态" @on-select="selectAreaOccupiedStatus">
                          <Option v-for="obj in occupiedStatus" :key="obj.id" :value="obj.id">{{obj.name}}</Option>
                      </Select>
                    </FormItem>
                </Form>
                <div class="search-btn">
                  <allowBtn allow="allowsearch" size="large" @click="search()">查询</allowBtn>
                  <allowBtn allow="allowreset" size="large" @click="reset">重置</allowBtn>
                </div>
            </div>
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
  export default {
    components: { dataGrid, fontIcon },
        data: function() {
            var that = this;
            return {
                loading: false,
                id: '', //页面修改赋值
                viewId: '', //页面查看赋值
                robotDmsEditor: {
                    id: '',
                    areaName: '',
                    areaCenterPoint: '',
                    areaContainPoints: '',
                    areaType: '',
                    occupiedStatus: '',
                    occupiedRobotName: ''
                },
                areaType:[
                  {"id":0,"name":"普通区域"},
                  {"id":1,"name":"充电区域"},
                  {"id":2,"name":"管控区域"},
                  {"id":3,"name":"自动门区域"},
                  {"id":4,"name":"电梯区域"},
                ],
                 occupiedStatus:[
                  {"id":0,"name":"未占用"},
                  {"id":1,"name":"占用"},
                ],
                modal_update: false, //修改弹窗
                modal_view: false, //查看弹窗
                modal_add: false, //新增弹窗
                //以上是界面v-model的所有参数
                gridOption: {
                  url: "/service_rms/dmsEditor/areaList",
                  auto:false,
                  columns: [ //列配置
                        { "title": "区域名称", "key": "areaName", "align": "center"},
                        { "title": "区域中心点", "key": "areaCenterPoint", "align": "center"},
                        { "title": "区域类型", "key": "areaType", "align": "center"},
                        { "title": "占用状态", "key": "occupiedStatus", "align": "center"},
                        { "title": "占用机器人名称", "key": "occupiedRobotName", "align": "center"}, 
                  ],
                  "loadFilter": function (data) {
                    console.log(data.data)
                      return data.data;
                  }
              },
            }
        },
        methods: {
            addModal: function() {
            this.$refs.addform.resetFields();
            this.modal_add=true;
            },
            toView: function(obj) {
                this.viewId = obj.id;
                this.modal_view = true;
            },
            toUpdate: function(obj) {
                this.id = obj.id;
                this.modal_update = true;
            },
            selectAreaType:function(obj){
              this.robotDmsEditor.areaType=obj.value;
            },
            selectAreaOccupiedStatus:function(obj){
              this.robotDmsEditor.occupiedStatus=obj.value;
            },
            search: function() {
                console.log(JSON.stringify(this.robotDmsEditor))
                this.$refs.grid.load({'data' : JSON.stringify(this.robotDmsEditor)});
            },
            reset: function() {
                this.robotDmsEditor.areaName="";
                this.robotDmsEditor.areaType="";
                this.robotDmsEditor.occupiedStatus="";
                this.search();
            },
            //加载旋转方法
            changeVal:function(){
                this.loading=false;
            },
        },
        mounted: function() {
            this.search();
        }
  }
  </script>