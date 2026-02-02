<template>
    <div>
        <page-title></page-title>
        <div class="page-content">
            <template>
                <!-- <Tabs> -->
                    <!-- <TabPane label="员工基本信息" icon="person"  name="user"> -->
                            <Form ref="user" :model="user"  :label-width="108"  inline>
                                <Card>
                                    <h2 slot="title">基本信息</h2>
                                <div style="float:right;text-align: left;width:400px;"  >
                                        <img v-if="user.photoUrl" style="width:130px;" :src="$utils.getFileShowUrl(user.photoUrl)">
                                        <img v-else style="width:130px;" :src="'./static/images/header/default-photo.png'">
                                    </div>
                                    <!-- <Form-item label="工号" prop="jobNumber">
                                        <template>{{user.jobNumber}}</template>
                                    </Form-item> -->
                                    <Form-item label="姓名" prop="userName">
                                        <template>{{user.userName}}</template>
                                    </Form-item>
                                    <Form-item label="部门" prop="orgId">
                                        <template>{{user.orgName}}</template>
                                    </Form-item>
                                    <Form-item label="性别" prop="sex">
                                        <template>{{user.sex}}</template>
                                    </Form-item>
                                   <!--  <Form-item label="婚否" prop="marryState">
                                        <template>{{0 == user.marryState ? '未婚': (1 == user.marryState ? '已婚' : '未知')}}</template>
                                    </Form-item> -->
                                    <Form-item label="入职时间" prop="entryDate">
                                        <template>{{user.entryDate}}</template>
                                    </Form-item>
                                    <!-- <Form-item label="紧急电话" prop="linkPhone">
                                        <template>{{user.linkPhone}}</template>
                                    </Form-item> -->
                                    <Form-item label="身份证" prop="cardNo">
                                        <template>{{user.cardNo}}</template>
                                    </Form-item>
                                    <Form-item label="联系邮箱" prop="email">
                                        <template>{{user.email}}</template>
                                    </Form-item>
                                </Card>
                                <!-- <Card>
                                    <h2 slot="title">入职信息</h2>
                                    <Form-item label="部门" prop="orgId">
                                        <template>{{user.orgName}}({{user.orgCode}})</template>
                                    </Form-item>
                                    <Form-item label="职务" prop="positionId">
                                        <template>{{user.positionName}}</template>
                                    </Form-item>
                                    <Form-item label="岗位" prop="postId">
                                        <template>{{user.postName}}</template>
                                    </Form-item>
                                    <Form-item label="入职时间" prop="entryTime">
                                        <template>{{user.entryDate}}</template>
                                    </Form-item>
                                    <Form-item label="转正时间" prop="shiftDate">
                                        <template>{{user.shiftDate}}</template>
                                    </Form-item>
                                </Card> -->
                                <!-- <Card>
                                    <h2 slot="title">其他信息</h2>
                                    <Form-item label="政治面貌" prop="politicsFaceId">
                                        <template>{{user.politicsFaceName}}</template>
                                    </Form-item>
                                    <Form-item label="户口类型" prop="residenceType">
                                        <template>{{user.residenceType}}</template>
                                    </Form-item>
                                    <Form-item label="备用电话" prop="backupPhone">
                                        <template>{{user.backupPhone}}</template>
                                    </Form-item>
                                    <Form-item label="家庭地址" prop="address" class="singleline" >
                                        <template>{{user.address}}</template>
                                    </Form-item>
                                    <Form-item label="籍贯" prop="birthPlace" class="singleline">
                                        <template>{{user.birthPlace}}</template>
                                    </Form-item>
                                    <Form-item label="备注" prop="remark" class="singleline">
                                        <template>{{user.remark}}</template>
                                    </Form-item>
                                </Card> -->
                            </Form>
                    <!-- </TabPane> -->

                    <!-- <TabPane label="任职信息" icon="android-calendar"  name="userDuty">
                        <Card>
                            <h2 slot="title">任职信息</h2>
                            <data-grid :option="dutyOption" ref="dutyGrid"></data-grid>
                        </Card>
                    </TabPane>

                    <TabPane label="技术职称" icon="ios-bookmarks"  name="userTechnical">
                        <Card>
                            <h2 slot="title">技术职称</h2>
                            <data-grid :option="tecOption" ref="tecGrid"></data-grid>
                        </Card>
                    </TabPane>

                    <TabPane label="教育经历"  icon="university"  name="userEducation">
                        <Card>
                            <h2 slot="title">教育经历</h2>
                            <data-grid :option="eduOption" ref="eduGrid"></data-grid>
                        </Card>
                    </TabPane>

                    <TabPane label="家庭主要成员" icon="ios-people"  name="userFamily">
                        <Card>
                            <h2 slot="title">家庭主要成员</h2>
                            <data-grid :option="famOption" ref="famGrid"></data-grid>
                        </Card>
                    </TabPane>

                    <TabPane label="工作经历" icon="ios-paper"  name="userWork">
                        <Card>
                            <h2 slot="title">工作经历</h2>
                            <data-grid :option="workOption" ref="workGrid"></data-grid>
                        </Card>
                    </TabPane>

                    <TabPane label="培训经历" icon="social-buffer"  name="userTrain">
                        <Card>
                            <h2 slot="title">培训经历</h2>
                            <data-grid :option="trainOption" ref="trainGrid"></data-grid>
                        </Card>
                    </TabPane> -->
                <!-- </Tabs> -->
            </template>

            <div style="text-align:center;">
                <allowBtn allow="update" type="primary" icon="plus"  @click="update()">编辑</allowBtn>
            </div>
        </div>
    </div>
</template>
<script>
import dataGrid from '@/components/datagrid'
import webuploader from '@/components/webuploader'

export default {
  components: { dataGrid,webuploader},
    data() {
        var that = this;
        return {
            userId:'',
            //员工基本信息				
            user: {id:"", userName: '', jobNumber: '', sex: '男', marryState: '' , birthday: '', linkPhone: '', email: '', orgId: '', positionId: '', postId: '', entryTime: '', shiftDate: '', politicsFaceId: '', residenceType: '', cardNo: '', backupPhone: '', address: '', birthPlace: '',photoUrl:'', remark: ''},
            //任职信息
            userDuty: {id: '', userId: '', orgId: '', postId: '', positionId : '', entryDate: '', shiftDate: '', leaveDate: '', leaveType: ''},
            //技术职称
            userTechnical: {id: '', userId: '', name: '', date: '', issuer: ''},
            //教育
            userEducation: {id: '', userId: '', educationTypeId: '', school: '', speciality: '', entryDate: '', graduateDate: '', graduateType: ''},
            //家庭主要成员
            userFamily: {id: '', userId: '', name: '', relation: '', birthday: '', telephone: '', workCompany: '', workContent: ''},
            //工作经历
            userWork: {id: '', userId: '', startDate: '', endDate: '', workCompany: '', workContent: ''},
            //培训经历
            userTrain: {id: '', userId: '', startDate: '', endDate: '', company: '', content: ''},
            //任职列表
            dutyOption: {
                header: true,
                data: [],
                columns: function () {
                    var arr = [
                        { "title": "部门", "key": "orgName", "align": "center"},
                        { "title": "职务", "key": "positionName", "align": "center"},
                        { "title": "岗位", "key": "postName", "align": "center" },
                        { "title": "入职时间", "key": "entryDate", "align": "center"},
                        { "title": "转正时间", "key": "shiftDate", "align": "center"},
                        { "title": "离职时间", "key": "leaveDate", "align": "center"},
                        { "title": "离职原因", "key": "leaveType", "align": "center" ,"ellipsis":true},
                    ];
                    return arr;
                },
                loadFilter: function (data) {
                    return data.data;
                },
                pagination: {
                    "currentPage": 1,
                    "countRecord": 0,
                    "pageSize": 15
                }
            },
            //教育经历列表
            eduOption: {
                header: true,
                data: [],
                columns: function () {
                    var arr = [
                        {"title": "专业类别", "key": "educationTypeName", "align": "center"},
                        {"title": "毕业院校", "key": "school", "align": "center" },
                        {"title": "专业", "key": "speciality", "align": "center" },
                        { "title": "入学时间", "key": "entryDate", "align": "center"},
                        { "title": "毕业时间", "key": "graduateDate", "align": "center"},
                        {"title": "毕业类型", "key": "graduateType", "align": "center" },
                    ];
                    return arr;
                },
                loadFilter: function (data) {
                    return data.data;
                },
                pagination: {
                    "currentPage": 1,
                    "countRecord": 0,
                    "pageSize": 15
                }
            },
            //技术职称列表
            tecOption: {
                header: true,
                data: [],
                columns: function () {
                    var arr = [
                        {"title": "职称名称", "key": "name", "align": "center"},
                        {"title": "认证时间", "key": "date", "align": "center"},
                        {"title": "证书编号", "key": "certificateNo", "align": "center"},
                        {"title": "颁发机构", "key": "issuer", "align": "center","ellipsis":true}
                    ];
                    return arr;
                },
                loadFilter: function (data) {
                    return data.data;
                },
                pagination: {
                    "currentPage": 1,
                    "countRecord": 0,
                    "pageSize": 15
                }
            },
            //家庭主要成员列表
            famOption: {
                header: true,
                data: [],
                columns: function () {
                    var arr = [
                        {"title": "姓名", "key": "name", "align": "center"},
                        {"title": "关系", "key": "relation", "align": "center"},
                        {"title": "出生日期", "key": "birthday", "align": "center"},
                        {"title": "联系电话", "key": "telephone", "align": "center"},
                        {"title": "工作单位", "key": "workOrganization", "align": "center","ellipsis":true},
                        {"title": "工作职务", "key": "workContent", "align": "center","ellipsis":true},
                    ];
                    return arr;
                },
                loadFilter: function (data) {
                    return data.data;
                },
                pagination: {
                    "currentPage": 1,
                    "countRecord": 0,
                    "pageSize": 15
                }
            },
            //工作经历列表
            workOption: {
                header: true,
                data: [],
                columns: function () {
                    var arr = [
                        {"title": "开始日期", "key": "startDate", "align": "center"},
                        {"title": "结束日期", "key": "endDate", "align": "center"},
                        {"title": "工作单位", "key": "workCompany", "align": "center", "ellipsis":true},
                        {"title": "任职职务", "key": "workContent", "align": "center", "ellipsis":true}
                    ];
                    return arr;
                },
                loadFilter: function (data) {
                    return data.data;
                },
                pagination: {
                    "currentPage": 1,
                    "countRecord": 0,
                    "pageSize": 15
                }
            },
            //培训经历列表
            trainOption: {
                header: true,
                data: [],
                columns: function () {
                    var arr = [
                        {"title": "开始日期", "key": "startDate", "align": "center"},
                        {"title": "结束日期", "key": "endDate", "align": "center"},
                        {"title": "培训机构", "key": "company", "align": "center", "ellipsis":true},
                        {"title": "培训内容", "key": "content", "align": "center", "ellipsis":true}
                    ];
                    return arr;
                },
                loadFilter: function (data) {
                    return data.data;
                },
                pagination: {
                    "currentPage": 1,
                    "countRecord": 0,
                    "pageSize": 15
                }
            },
        }				
    },
    methods: {
        //查询数据
        getData: function() {
            this.$ajax.post("/service_user/account/getUserInfo")
                .then((res) => {
                    if (res.data.code == 200) {
                        this.userId = res.data.data.id;
                        var id = (undefined != this.userId) ? this.userId : null;
                        if (null != id) {
                            this.$ajax.post("/service_user/user/findById", {"id": id})
                                .then(res => {
                                    if (res.data.code == 200) {
                                        this.user = res.data.data;
                                        if (null != this.user) {
                                            this.dutyOption.data = res.data.userDutyList==undefined?[]:res.data.userDutyList;//任职信息列表
                                            this.tecOption.data = res.data.userTechnicalList==undefined?[]:res.data.userTechnicalList;//技术职称列表
                                            this.eduOption.data = res.data.userEducationList==undefined?[]:res.data.userEducationList;//教育经历列表
                                            this.famOption.data = res.data.userFamilyList==undefined?[]:res.data.userFamilyList;//家庭主要成员信息列表
                                            this.workOption.data = res.data.userWorkList==undefined?[]:res.data.userWorkList;//工作经历列表
                                            this.trainOption.data = res.data.userTrainList==undefined?[]:res.data.userTrainList;//培训经历列表
                                        } else {
                                            this.$Message.warning("当前员工不存在,请检查员工工号");
                                            return;
                                        }
                                    }
                                })
                                .catch((error) => {
                                    if (!error.url) {
                                        console.info(error);
                                    }
                                })
                        }
                    } else {
                        this.$Message.warning("当前员工不存在,请检查员工工号");
                        return;
                    }
                })
                .catch((error) => {
                    if (!error.url) {
                        console.info(error);
                    }
                })
        },
        //保存数据
        update:function () {
            this.$router.push('/person/update/' +this.userId);
        },
    },
    mounted: function() {
      //得到实体数据
      this.getData();
    },
}
</script>
