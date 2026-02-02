<template>
    <div>
    <page-title></page-title>
    <div class="page-content">
        <template>
            <Tabs v-model="tabName">
                <TabPane label="员工基本信息" icon="person"  name="user">
						 <Form ref="user" :model="user"  :label-width="108" :class="{'form-view':type=='view'}"  :rules="ruleValidate" inline>
							<Card>
                                <h2 slot="title">基本信息</h2>
                               <div style="float:right;text-align: left;width:400px;"   v-if="type=='view'">
                                    <img v-if="user.photoUrl" style="width:130px;" :src="$utils.getFileShowUrl(user.photoUrl)">
                                    <img v-else style="width:130px;" :src="'./static/images/header/default-photo.png'">
                                </div>
                                <div style="float:right;text-align: left;min-height:130px;width:400px;"  v-if="type!='view'">
                                    <span title="点击更换头像" @click="openDialog" style="cursor:pointer;text-align:center;">
                                        <img v-if="uploadimgurl" style="width:110px;" :src="uploadimgurl">
                                        <img v-else-if="user.photoUrl" style="width:120px;" :src="$utils.getFileShowUrl(user.photoUrl)">
                                        <img v-else style="width:130px;" :src="'./static/images/header/default-photo.png'">
                                    </span>
                                    <webuploader :option="option" ref="webuploader"  @fileQueued="fileQueued"  @deleteFile="removeFile" @uploadSuccess="uploadSuccess"  @uploadFinished="uploadFinished" style="display:none;"></webuploader>
                                </div>

                                <!-- <Form-item label="工号" prop="jobNumber">
                                    <template v-if="type=='view'">{{user.jobNumber}}</template>
                                    <Input v-else v-model="user.jobNumber" maxlength="10" placeholder="请输入工号，最多不超过10个字符"></Input>
                                </Form-item> -->
                                <Form-item label="姓名" prop="userName">
                                    <template v-if="type=='view'">{{user.userName}}</template>
                                    <Input v-else v-model="user.userName" maxlength="50" placeholder="请输入姓名，最多不超过50个字符"></Input>
                                </Form-item>
                                <Form-item label="性别" prop="sex">
                                    <template v-if="type=='view'">{{user.sex}}</template>
                                    <RadioGroup  v-else v-model="user.sex">
                                        <Radio label="男"  checked></Radio>
                                        <Radio label="女"></Radio>
                                    </RadioGroup>
                                </Form-item>
                                <Form-item label="当前部门" prop="orgId">
                                    <template v-if="type=='view'">{{user.orgName}}({{user.orgCode}})</template>
                                    <Cascader v-else :data="carModelTree" v-model="carModelIds" placeholder="请选择部门" change-on-select></Cascader>
                                </Form-item>
                                <Form-item label="出生日期" prop="birthday">
                                    <template v-if="type=='view'">{{user.birthday}}</template>
                                    <DatePicker v-else type="date" placeholder="请选择出生日期" :options="limitCardDate" v-model="user.birthday"></DatePicker>
                                </Form-item>
                                <Form-item label="婚否" prop="marryState">
                                    <template v-if="type=='view'">{{0 == user.marryState ? '未婚': (1 == user.marryState ? '已婚' : '未知')}}</template>
                                    <Select v-else v-model="user.marryState" placeholder="请选择婚否">
                                        <Option value="0">未婚</Option>
                                        <Option value="1">已婚</Option>
                                        <Option value="2">未知</Option>
                                    </Select>
                                </Form-item>
                                <Form-item label="联系电话" prop="linkPhone">
                                    <template v-if="type=='view'">{{user.linkPhone}}</template>
                                    <Input v-else v-model="user.linkPhone" maxlength="11" placeholder="请输入电话，最多不超过11个字符"></Input>
                                </Form-item>
                                <Form-item label="联系邮箱" prop="email">
                                    <template v-if="type=='view'">{{user.email}}</template>
                                    <Input v-else v-model="user.email"maxlength="30"   placeholder="请输入邮箱"></Input>
                                </Form-item>
                            </Card>
                             <Card>
                                 <h2 slot="title">其他信息</h2>
                                 <Form-item label="政治面貌" prop="politicsFaceId">
                                     <template v-if="type=='view'">{{user.politicsFaceName}}</template>
                                     <Select v-else v-model="user.politicsFaceId" placeholder="请选择政治面貌">
                                         <Option v-for="(obj,key) in politicsType" :key="key" :value="obj.id">{{obj.textName}}</Option>
                                     </Select>
                                 </Form-item>
                                 <Form-item label="户口类型" prop="residenceType">
                                     <template v-if="type=='view'">{{user.residenceType}}</template>
                                     <Select v-else v-model="user.residenceType" placeholder="请选择户口类型">
                                         <Option value="城镇">城镇</Option>
                                         <Option value="农业">农业</Option>
                                         <Option value="其他">其他</Option>
                                     </Select>
                                 </Form-item>
                                 <Form-item label="身份证" prop="cardNo">
                                     <template v-if="type=='view'">{{user.cardNo}}</template>
                                     <Input v-else v-model="user.cardNo"maxlength="18"  placeholder="请输入身份证，确保18位字符"></Input>
                                 </Form-item>
                                 <Form-item label="备用电话" prop="backupPhone">
                                     <template v-if="type=='view'">{{user.backupPhone}}</template>
                                     <Input v-else v-model="user.backupPhone" maxlength="11"  placeholder="请输入备用电话"></Input>
                                 </Form-item>
                                 <Form-item label="籍贯" prop="birthPlace">
                                     <template v-if="type=='view'">{{user.birthPlace}}</template>
                                     <Input v-else v-model="user.birthPlace" maxlength="15"  placeholder="请输入籍贯"></Input>
                                 </Form-item>
                                 <Form-item label="家庭地址" prop="address" class="singleline" >
                                     <template v-if="type=='view'">{{user.address}}</template>
                                     <Input v-else v-model="user.address" maxlength="100" type="textarea" placeholder="请输入家庭地址，最多不超过100个字符"></Input>
                                 </Form-item>
                                 <Form-item label="备注" prop="remark" class="singleline">
                                     <template v-if="type=='view'">{{user.remark}}</template>
                                     <Input v-else v-model.trim="user.remark"  maxlength="100" type="textarea"  placeholder="请输入备注，不超过100字"></Input>
                                 </Form-item>
                                 <Form-item v-if="type=='view'" label="创建人"  prop="createUserCode">
                                     <template>{{user.createUserCode}}</template>
                                 </Form-item>
                                 <Form-item v-if="type=='view'" label="创建时间"  prop="createTime">
                                     <template>{{user.createTime==null?'':new Date(user.createTime).Format('yyyy-MM-dd hh:mm:ss')}}</template>
                                 </Form-item>
                                 <Form-item v-if="type=='view'" label="修改人"  prop="updateUserCode">
                                     <template>{{user.updateUserCode}}</template>
                                 </Form-item>
                                 <Form-item v-if="type=='view'" label="修改时间"  prop="updateTime">
                                     <template>{{user.updateTime==null?'':new Date(user.updateTime).Format('yyyy-MM-dd hh:mm:ss')}}</template>
                                 </Form-item>
                             </Card>
						</Form>
                </TabPane>

                <TabPane label="任职信息" icon="android-calendar"  name="userDuty">
                    <Card>
                        <h2 slot="title">任职信息</h2>
                        <Form v-if="type!='view'" ref="userDuty" :model="userDuty" :label-width="108" :class="{'form-view': viewDuyShow == true}" inline>
                            <Form-item label="部门" prop="orgId">
                                <template v-if="viewDuyShow == true">{{userDuty.orgName}}({{userDuty.orgCode}})</template>
                                <Cascader v-else :data="carModelTree1" v-model="carModelIds1" placeholder="请选择部门" change-on-select clearable></Cascader>
                            </Form-item>
                            <Form-item label="职务" prop="positionId">
                                <template v-if="viewDuyShow == true">{{userDuty.positionName}}</template>
                                <Select v-else v-model="userDuty.positionId" placeholder="请选择职务">
                                    <Option v-for="(professionTmp,key) in positionTypes" :key="key" :value="professionTmp.id">{{professionTmp.name}}</Option>
                                </Select>
                            </Form-item>
                            <Form-item label="职务类型" prop="positionType">
                                <template v-if="viewDuyShow==true">{{userDuty.positionType}}</template>
                                <RadioGroup  v-else v-model="userDuty.positionType">
                                    <Radio label="主职" checked></Radio>
                                    <Radio label="副职"></Radio>
                                </RadioGroup>
                            </Form-item>
                            <Form-item label="岗位" prop="postId">
                                <template v-if="viewDuyShow == true">{{userDuty.postName}}</template>
                                <Select v-else v-model="userDuty.postId" placeholder="请选择岗位">
                                    <Option v-for="(post,key) in postType" :key="key" :value="post.id">{{post.name}}</Option>
                                </Select>
                            </Form-item>
                            <Form-item label="入职时间" prop="entryDate">
                                <template v-if="viewDuyShow == true">{{userDuty.entryDate}}</template>
                                <DatePicker v-else type="date" v-model="userDuty.entryDate" placeholder="请选择入职时间" :options="limitCardDate"></DatePicker>
                            </Form-item>
                            <Form-item label="转正时间" prop="shiftDate">
                                <template v-if="viewDuyShow == true">{{userDuty.shiftDate}}</template>
                                <DatePicker v-else type="date" v-model="userDuty.shiftDate" placeholder="请选择转正时间" ></DatePicker>
                            </Form-item>
                            <Form-item label="在职状态" prop="positionState">
                                <template v-if="viewDuyShow==true">{{userDuty.positionState}}</template>
                                <RadioGroup  v-else v-model="userDuty.positionState">
                                    <Radio label="在职" checked></Radio>
                                    <Radio label="离职"></Radio>
                                </RadioGroup>
                            </Form-item>
                            <Form-item label="离职时间" prop="leaveDate" v-if="userDuty.positionState=='离职'">
                                <template v-if="viewDuyShow == true">{{userDuty.leaveDate}}</template>
                                <DatePicker v-else type="date" v-model="userDuty.leaveDate" placeholder="请选择离职时间" ></DatePicker>
                            </Form-item>
                            <Form-item label="离职原因" prop="leaveType"  v-if="userDuty.positionState=='离职'">
                                <template v-if="type=='view'">{{userDuty.leaveType}}</template>
                                <Select v-else v-model="userDuty.leaveType" placeholder="请选择离职类型">
                                    <Option value="晋升">晋升</Option>
                                    <Option value="调岗">调岗</Option>
                                    <Option value="离职">离职</Option>
                                </Select>
                            </Form-item>
                        </Form>
                        <template v-if="type!='view'">
                            <div style="text-align:center;">
                                <Button v-if="showAddDuyButton"  type="info" @click="addUserDuty('userDuty')"><i class="fa fa-plus"></i> 添 加</Button>
                                <Button v-if="viewDuyShow == false" type="dashed" @click="resetForm('userDuty')"><i class="fa fa-minus"></i> 重 置</Button>
                                <Button type="info" v-if="updateDuyShow" @click="updateUserDuty('userDuty')"><i class="fa fa-plus"></i>确认修改</Button>
                            </div>
                            </br>
                        </template>
                        <data-grid :option="dutyOption" ref="dutyGrid"></data-grid>
                    </Card>
                </TabPane>
				<TabPane label="技术职称" icon="ios-bookmarks"  name="userTechnical">
                    <Card>
					    <h2 slot="title">技术职称</h2>
                        <Form v-if="type!='view'" ref="userTechnical" :model="userTechnical" :label-width="108" :class="{'form-view': viewEduShow == true}" inline>
                            <Form-item label="职称名称" prop="name">
                                <template v-if="viewTecShow == true">{{userTechnical.name}}</template>
                                <Input v-else v-model.trim="userTechnical.name" maxlength="30" type="text" placeholder="请输入职称名称"></Input>
                            </Form-item>
                            <Form-item label="认证时间" prop="date">
                                <template v-if="viewTecShow == true">{{userTechnical.date}}</template>
                                <DatePicker v-else v-model="userTechnical.date" type="date" placeholder="请选择认证时间" :options="limitCardDate"></DatePicker>
                            </Form-item>
                            <Form-item label="证书编号" prop="certificateNo">
                                <template v-if="viewTecShow == true">{{userTechnical.certificateNo}}</template>
                                <Input v-else v-model.trim="userTechnical.certificateNo" maxlength="30" placeholder="请输入证书编号"></Input>
                            </Form-item>
                            <Form-item label="颁发机构" prop="issuer">
                                <template v-if="viewTecShow == true">{{userTechnical.issuer}}</template>
                                <Input v-else v-model.trim="userTechnical.issuer" maxlength="30" placeholder="请输入颁发机构"></Input>
                            </Form-item>
                        </Form>
                        <template v-if="type!='view'">
                            <div style="text-align:center;">
                                <Button v-if="showAddTecButton" type="info" @click="addUserTechnical('userTechnical')"><i class="fa fa-plus"></i> 添 加</Button>
                                <Button type="dashed" @click="resetForm('userTechnical')"><i class="fa fa-minus"></i> 重 置</Button>
								<Button type="info" v-if="updateTecShow" @click="updateUserTechnical('userTechnical')"><i class="fa fa-plus"></i>确认修改</Button>
                            </div>
                            </br>
                        </template>
                        <data-grid :option="tecOption" ref="tecGrid"></data-grid>
                    </Card>
                </TabPane>

               <TabPane label="教育经历"  icon="university"  name="userEducation">
                    <Card>
                        <h2 slot="title">教育经历</h2>
                        <Form v-if="type!='view'" ref="userEducation" :model="userEducation"  :label-width="108" :class="{'form-view': viewEduShow == true}" inline>
                            <Form-item label="学历" prop="educationTypeId">
                                <template v-if="viewEduShow == true">{{userEducation.educationTypeName}}</template>
                                <Select v-else v-model.trim="userEducation.educationTypeId" placeholder="请选择专业类别"  clearable>
                                    <Option v-for="(majorCategory,key) in educationType" :key="key" :value="majorCategory.id">{{majorCategory.textName}}</Option>
                                </Select>
                            </Form-item>
                            <Form-item label="毕业院校" prop="school">
                                <template v-if="viewEduShow == true">{{userEducation.school}}</template>
                                <Input v-else v-model.trim="userEducation.school" maxlength="30" placeholder="请输入毕业院校"></Input>
                            </Form-item>
                            <Form-item label="专业名称" prop="speciality">
                                <template v-if="viewEduShow == true">{{userEducation.speciality}}</template>
                                <Input v-else v-model.trim="userEducation.speciality" maxlength="30" placeholder="请输入专业名称">{{userEducation.speciality}}</Input>
                            </Form-item>
                            <Form-item label="入学日期" prop="entryDate">
                                <template  v-if="viewEduShow == true">{{userEducation.entryDate}}</template>
                                <DatePicker v-else type="date" v-model="userEducation.entryDate" placeholder="请选择入学日期" :options="limitCardDate" ></DatePicker>
                            </Form-item>
                            <Form-item label="毕业日期" prop="graduateDate">
                                <template  v-if="viewEduShow == true">{{userEducation.graduateDate}}</template>
                                <DatePicker v-else v-model="userEducation.graduateDate" type="date" placeholder="请选择毕业日期" :options="limitCardDate"></DatePicker>
                            </Form-item>
                            <Form-item label="毕业方式" prop="graduateType">
                                <template v-if="viewEduShow == true">{{userEducation.graduateType}}</template>
                                <Select v-else v-model="userEducation.graduateType" placeholder="请选择毕业类型">
                                    <Option value="毕业">毕业</Option>
                                    <Option value="结业">结业</Option>
                                    <Option value="肄业">肄业</Option>
                                </Select>
                            </Form-item>
                        </Form>
                        <template v-if="type!='view'">
                            <div style="text-align:center;">
                                <Button v-if="showAddEduButton"  type="info" @click="addUserEducation('userEducation')"><i class="fa fa-plus"></i> 添 加</Button>
                                <Button type="dashed" @click="resetForm('userEducation')"><i class="fa fa-minus"></i> 重 置</Button>
                                <Button type="info" v-if="updateEduShow" @click="updateUserEducation('userEducation')"><i class="fa fa-plus"></i>确认修改</Button>
                            </div>
                            </br>
                        </template>
                        <data-grid :option="eduOption" ref="eduGrid"></data-grid>
                    </Card>
                </TabPane>

               <TabPane label="家庭主要成员" icon="ios-people"  name="userFamily">
                    <Card>
                        <h2 slot="title">家庭主要成员</h2>
                        <Form  v-if="type!='view'" ref="userFamily" :model="userFamily" :label-width="108" :class="{'form-view': viewFamShow == true}" inline>
                            <Form-item label="姓名" prop="name">
                                <template v-if="viewFamShow == true">{{userFamily.name}}</template>
                                <Input v-else v-model.trim="userFamily.name" maxlength="20" type="text" placeholder="请输入姓名">{{userFamily.name}}</Input>
                            </Form-item>
                            <Form-item label="关系" prop="relation">
                                <template v-if="viewFamShow == true">{{userFamily.relation}}</template>
                                <Input v-else v-model.trim="userFamily.relation" maxlength="15" type="text" placeholder="请输入关系">{{userFamily.relation}}</Input>
                            </Form-item>
                            <Form-item label="联系电话" prop="telephone">
                                <template v-if="viewFamShow == true">{{userFamily.telephone}}</template>
                                <Input v-else v-model.trim="userFamily.telephone" type="text" maxlength="11" placeholder="请输入联系电话">{{userFamily.telephone}}</Input>
                            </Form-item>
                            <Form-item label="出生日期" prop="birthday">
                                <template v-if="viewFamShow == true">{{userFamily.birthday}}</template>
                                <DatePicker v-else type="date" v-model="userFamily.birthday" placeholder="请选择出生日期" :options="limitCardDate"  format="yyyy-MM-dd"></DatePicker>
                            </Form-item>
                            <Form-item label="工作单位" prop="workCompany">
                                <template v-if="viewFamShow == true">{{userFamily.workCompany}}</template>
                                <Input v-else v-model.trim="userFamily.workCompany" maxlength="30" type="text" placeholder="请输入工作单位"></Input>
                            </Form-item>
                            <Form-item label="工作职务" prop="workContent">
                                <template v-if="viewFamShow == true">{{userFamily.workContent}}</template>
                                <Input v-else v-model.trim="userFamily.workContent" maxlength="30" type="text" placeholder="请输入工作职务"></Input>
                            </Form-item>
                        </Form>
                        <template v-if="type!='view'">
                            <div style="text-align:center;">
                                <Button v-if="showAddFamButton"  type="info"  @click="addUserFamily('userFamily')"><i class="fa fa-plus"></i> 添 加</Button>
                                <Button type="dashed" @click="resetForm('userFamily')"><i class="fa fa-minus"></i> 重 置</Button>
                                <Button type="info" v-if="updateFamShow"  @click="updateUserFamily('userFamily')"><i class="fa fa-plus"></i>确认修改</Button>
                            </div>
                            </br>
                        </template>
                        <data-grid :option="famOption" ref="famGrid"></data-grid>
                    </Card>
                </TabPane>

				<TabPane label="工作经历" icon="ios-paper"  name="userWork">
                    <Card>
					    <h2 slot="title">工作经历</h2>
                        <Form v-if="type!='view'" ref="userWork" :model="userWork" :label-width="108" :class="{'form-view': viewEduShow == true}" inline>
                            <Form-item label="开始时间" prop="startDate">
                                <template v-if="viewWorShow == true">{{userWork.startDate}}</template>
                                <DatePicker v-else v-model="userWork.startDate" type="date" placeholder="请选择开始时间" :options="limitCardDate"></DatePicker>
                            </Form-item>
							<Form-item label="结束时间" prop="endDate">
                                <template v-if="viewWorShow == true">{{userWork.endDate}}</template>
                                <DatePicker v-else v-model="userWork.endDate" type="date" placeholder="请选择结束时间" :options="limitCardDate"></DatePicker>
                            </Form-item>
                            <Form-item label="工作单位" prop="workCompany">
                                <template v-if="viewWorShow == true">{{userWork.workCompany}}</template>
                                <Input v-else v-model.trim="userWork.workCompany" maxlength="30" type="text" placeholder="请输入工作单位"></Input>
                            </Form-item>
                            <Form-item label="职务" prop="workContent">
                                <template v-if="viewWorShow == true">{{userWork.workContent}}</template>
                                <Input v-else v-model.trim="userWork.workContent" maxlength="30" placeholder="请输入职务"></Input>
                            </Form-item>
                        </Form>
                        <template v-if="type!='view'">
                            <div style="text-align:center;">
                                <Button v-if="showAddWorButton" type="info" @click="addUserWork('userWork')"><i class="fa fa-plus"></i> 添 加</Button>
                                <Button type="dashed" @click="resetForm('userWork')"><i class="fa fa-minus"></i> 重 置</Button>
								<Button type="info" v-if="updateWorShow" @click="updateUserWork('userWork')"><i class="fa fa-plus"></i>确认修改</Button>
                            </div>
                            </br>
                        </template>
                        <data-grid :option="workOption" ref="workGrid"></data-grid>
                    </Card>
                </TabPane>

				<TabPane label="培训经历" icon="social-buffer"  name="userTrain">
                    <Card>
					    <h2 slot="title">培训经历</h2>
                        <Form v-if="type!='view'" ref="userTrain" :model="userTrain" :label-width="108" :class="{'form-view': viewTraShow == true}" inline>
                            <Form-item label="开始时间" prop="startDate">
                                <template v-if="viewTraShow == true">{{userTrain.startDate}}</template>
                                <DatePicker v-else v-model="userTrain.startDate" type="date" placeholder="请选择开始时间" :options="limitCardDate"></DatePicker>
                            </Form-item>
                            <Form-item label="结束时间" prop="endDate">
                                <template v-if="viewTraShow == true">{{userTrain.endDate}}</template>
                                <DatePicker v-else v-model="userTrain.endDate" type="date" placeholder="请选择结束时间" :options="limitCardDate"></DatePicker>
                            </Form-item>
                            <Form-item label="培训机构" prop="company">
                                <template v-if="viewTraShow == true">{{userTrain.company}}</template>
                                <Input v-else v-model.trim="userTrain.company" maxlength="30" type="text" placeholder="请输入培训机构"></Input>
                            </Form-item>
                            <Form-item label="培训内容" prop="content">
                                <template v-if="viewTraShow == true">{{userTrain.content}}</template>
                                <Input v-else v-model.trim="userTrain.content" maxlength="30" placeholder="请输入培训内容"></Input>
                            </Form-item>
                        </Form>
                        <template v-if="type!='view'">
                            <div style="text-align:center;">
                                <Button v-if="showAddTraButton" type="info" @click="addUserTrain('userTrain')"><i class="fa fa-plus"></i> 添 加</Button>
                                <Button type="dashed" @click="resetForm('userTrain')"><i class="fa fa-minus"></i> 重 置</Button>
								<Button type="info" v-if="updateTraShow" @click="updateUserTrain('userTrain')"><i class="fa fa-plus"></i>确认修改</Button>
                            </div>
                            </br>
                        </template>
                        <data-grid :option="trainOption" ref="trainGrid"></data-grid>
                    </Card>
                </TabPane>
            </Tabs>
        </template>

        <div style="text-align:center;">
            <Button type="primary" @click="save()" :loading="loading">全部保存</Button>
            <Button type="ghost" @click="$router.back()" style="margin-left: 8px">取消</Button>
        </div>
    </div>
</div>
</template>
<script>
import dataGrid from '@/components/datagrid'
import webuploader from '@/components/webuploader'
export default {
  components: { dataGrid,webuploader},
        template: Template,
        data() {
            var that = this;
            return {
                //上传头像
                option: {
                    //"server": "/waterservice/upload/uploadFile",
                    auto: true,
                    accept: {
                        title: 'Images',
                        extensions: 'jpg,jpeg,png',
                        mimeTypes: 'image/jpg,image/jpeg,image/png',
                    },
                    //uploadAccept: this.uploadAccept,
                    fileSingleSizeLimit: 10 * 1024 * 1024,
                    /*fileNumLimit: 1,*/
                    formData: {
                        "moudleName": "human" //这是定义大模块名称
                    },
                    beforeFileQueued:that.beforeFileQueued
                },
                uploadimgurl:null,//上传文件生成的缩略图
                id: '', // 业务ID，修改或者删除的时候，传递主键值
                old:'',
                type: this.$utils.getPageType(this), // type=update表示修改；type=view表示查看
                carModelTreeData: [],
                carModelTree: [],
                carModelIds: [],
                carModelTreeData1: [],
                carModelTree1: [],
                carModelIds1: [],
			    loading:false,
				tabName: 'user',
                educationType: [],//学历类别
                politicsType: [],//政治面貌
                postType: [],//岗位类型
                positionTypes: [],//职务类型
				//列表修改时，将添加按钮隐藏
                showAddDuyButton: true,
                showAddTecButton: true,
                showAddEduButton: true,
                showAddFamButton: true,
				showAddWorButton: true,
                showAddTraButton: true,
				//列表查看参数：显示下划线不能进行修改
				viewDuyShow: false,
				viewTecShow: false,
                viewEduShow: false,
				viewFamShow: false,
				viewTraShow: false,
				viewWorShow: false,
				//列表修改时，显示此Button，修改完成后，隐藏此按钮
				updateDuyShow: false,
				updateTecShow: false,
                updateEduShow: false,
				updateFamShow: false,
				updateTraShow: false,
				updateWorShow: false,
                //员工基本信息				
                user: {id:"", userName: '', jobNumber: '', sex: '男', marryState: '' , birthday: '', linkPhone: '', email: '', orgId: '',entryTime: '', shiftDate: '', politicsFaceId: '', residenceType: '', cardNo: '', backupPhone: '', address: '', birthPlace: '',photoUrl:'', remark: ''},
                //任职信息
                userDuty: {id: '', userId: '', orgId: '', postId: '', positionId : '', entryDate: '', shiftDate: '', leaveDate: '', leaveType: '',positionType:'主职',positionState:'在职'},
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
				//校验人员信息
                ruleValidate: {
                    jobNumber: [
                        { required:true, message: '请输入工号', trigger: 'blur' },
                        { max: 10, message: '编码类型不超过10个字符', trigger: 'blur' },
                        {
                            validator: (rule, value, callback) => {
                                if(!/^[a-zA-Z0-9_]+$/.test(value)){
                                    callback(new Error("由字母/数字/下划线组成组成"));
                                }else{
                                    callback();
                                }

                            }, trigger: 'blur'
                        },
                        { validator: this.checkCode, trigger: 'blur' }
                    ],
                    userName: [
                        { required:true, message: '请输入姓名', trigger: 'blur' },
                        { max: 50, message: '不超过50字', trigger: 'blur' },
                    ],
				},
                //日历控件
                limitCardDate: {
                    disabledDate(date) {
                        return date && date.valueOf() > Date.now();
                    }
                },
                //任职列表
                dutyOption: {
                    header: true,
                    data: [],
                    columns: function () {
                        var arr = [
                            { "title": "部门", "key": "orgName", "align": "center"},
                            { "title": "职务", "key": "positionName", "align": "center"},
                            { "title": "职务类型", "key": "positionType", "align": "center"},
                            { "title": "岗位", "key": "postName", "align": "center" },
                            { "title": "入职时间", "key": "entryDate", "align": "center"},
                            { "title": "在职状态", "key": "positionState", "align": "center"},
                        ];
                        if (that.$utils.getPageType(that) != "view" && that.type != "view") {
                            arr.push({
                                key: 'tool', title: '操作', align: "center", "width": 200,
                                render(h, params){
                                    return h('div', [
                                        h('Button', {
                                            props: {type: 'primary', size: 'small', allow: 'view'},
                                            style: {marginRight: '5px'},
                                            on: {
                                                click: () => {
                                                    that.toViewDuty(params.row)
                                                }
                                            }
                                        }, '查看'),
                                        h('Button', {
                                            props: {type: 'primary', size: 'small', allow: 'update'},
                                            style: {marginRight: '5px'},
                                            on: {
                                                click: () => {
                                                    that.toUpdateDuty(params.row)
                                                }
                                            }
                                        }, '修改'),
                                        h('Button', {
                                            props: {type: 'info', size: 'small', allow: 'delete'},
                                            style: {marginRight: '5px'},
                                            on: {
                                                click: () => {
                                                    that.deleteDuty(params.row)
                                                }
                                            }
                                        }, '删除')
                                    ]);
                                }
                            })
                        }
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
                        if (that.$utils.getPageType(that) != "view" && that.type != "view") {
                            arr.push({
                                key: 'tool', title: '操作', align: "center", "width": 200,
                                render(h, params){
                                    return h('div', [
                                        h('Button', {
                                            props: {type: 'primary', size: 'small', allow: 'update'},
                                            style: {marginRight: '5px'},
                                            on: {
                                                click: () => {
                                                    that.toUpdateEdu(params.row)
                                                }
                                            }
                                        }, '修改'),
                                        h('Button', {
                                            props: {type: 'info', size: 'small', allow: 'delete'},
                                            style: {marginRight: '5px'},
                                            on: {
                                                click: () => {
                                                    that.deleteEdu(params.row)
                                                }
                                            }
                                        }, '删除')
                                    ]);
                                }
                            })
                        }
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
                        if (that.$utils.getPageType(that) != "view" && that.type != "view") {
                            arr.push({
                                key: 'tool', title: '操作', align: "center", "width": 200,
                                render(h, params){
                                    return h('div', [
                                        h('Button', {
                                            props: {type: 'primary', size: 'small', allow: 'update'},
                                            style: {marginRight: '5px'},
                                            on: {
                                                click: () => {
                                                    that.toUpdateTec(params.row)
                                                }
                                            }
                                        }, '修改'),
                                        h('Button', {
                                            props: {type: 'info', size: 'small', allow: 'delete'},
                                            style: {marginRight: '5px'},
                                            on: {
                                                click: () => {
                                                    that.deleteTec(params.row)
                                                }
                                            }
                                        }, '删除')
                                    ]);
                                }
                            })
                        }
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
                            {"title": "工作单位", "key": "workCompany", "align": "center","ellipsis":true},
                            {"title": "工作职务", "key": "workContent", "align": "center","ellipsis":true},
                        ];
                        if (that.$utils.getPageType(that) != "view" && that.type != "view") {
                            arr.push({
                                key: 'tool', title: '操作', align: "center", "width": 200,
                                render(h, params){
                                    return h('div', [
                                        h('Button', {
                                            props: {type: 'primary', size: 'small', allow: 'update'},
                                            style: {marginRight: '5px'},
                                            on: {
                                                click: () => {
                                                    that.toUpdateFam(params.row)
                                                }
                                            }
                                        }, '修改'),
                                        h('Button', {
                                            props: {type: 'info', size: 'small', allow: 'delete'},
                                            style: {
                                                marginRight: '5px'
                                            },
                                            on: {
                                                click: () => {
                                                    that.deleteFam(params.row)
                                                }
                                            }
                                        }, '删除')
                                    ]);
                                }
                            })
                        }
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
                        if (that.$utils.getPageType(that) != "view" && that.type != "view") {
                            arr.push({
                                key: 'tool', title: '操作', align: "center", "width": 200,
                                render(h, params){
                                    return h('div', [
                                        h('Button', {
                                            props: {type: 'primary', size: 'small', allow: 'update'},
                                            style: {marginRight: '5px'},
                                            on: {
                                                click: () => {
                                                    that.toUpdateWork(params.row)
                                                }
                                            }
                                        }, '修改'),
                                        h('Button', {
                                            props: {type: 'info', size: 'small', allow: 'delete'},
                                            style: {marginRight: '5px'},
                                            on: {
                                                click: () => {
                                                    that.deleteWork(params.row)
                                                }
                                            }
                                        }, '删除')
                                    ]);
                                }
                            })
                        }
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
                        if (that.$utils.getPageType(that) != "view" && that.type != "view") {
                            arr.push({
                                key: 'tool', title: '操作', align: "center", "width": 200,
                                render(h, params){
                                    return h('div', [
                                        h('Button', {
                                            props: {type: 'primary', size: 'small', allow: 'update'},
                                            style: {marginRight: '5px'},
                                            on: {
                                                click: () => {
                                                    that.toUpdateTrain(params.row)
                                                }
                                            }
                                        }, '修改'),
                                        h('Button', {
                                            props: {type: 'info', size: 'small', allow: 'delete'},
                                            style: {marginRight: '5px'},
                                            on: {
                                                click: () => {
                                                    that.deleteTrain(params.row)
                                                }
                                            }
                                        }, '删除')
                                    ]);
                                }
                            })
                        }
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
            //校验工号
            checkCode:function(rule,value,callback){
                if(value){
                    this.$ajax.post("/service_user/user/check",{'jobNumber':value,'oldjobNumber':this.old})
                        .then((res) => {
                            if(res.data.data == false){
                                callback();
                            }else{
                                callback("工号已经存在");
                            }
                        })
                        .catch((error) => {
                            callback('校验失败');
                        })
                }
            },
			//查询数据
			getData: function() {
                var id = this.$route.params.id;
                id = (undefined != id) ? id : null;
                this.id = id;
                if (null != id) {
                    this.$ajax.post("/service_user/user/findById", {"id": this.id})
                        .then(res => {
                            if (res.data.code == 200) {
                                this.user = res.data.data;
                                if (null != this.user) {
                                    if (this.user.orgId != null) {//加载树数据
                                        this.carModelIds = this.$utils.getTreeInParentsId(this.carModelTreeData, this.user.orgId);
                                    }
                                    this.dutyOption.data = res.data.userDutyList==undefined?[]:res.data.userDutyList;//任职信息列表
                                    this.tecOption.data = res.data.userTechnicalList==undefined?[]:res.data.userTechnicalList;//技术职称列表
                                    this.eduOption.data = res.data.userEducationList==undefined?[]:res.data.userEducationList;//教育经历列表
                                    this.famOption.data = res.data.userFamilyList==undefined?[]:res.data.userFamilyList;//家庭主要成员信息列表
                                    this.workOption.data = res.data.userWorkList==undefined?[]:res.data.userWorkList;//工作经历列表
                                    this.trainOption.data = res.data.userTrainList==undefined?[]:res.data.userTrainList;//培训经历列表
                                    this.old=this.user.jobNumber;
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
            },
            //保存数据
            save:function () {
                this.$refs['user'].validate((valid) => {
                    if (valid) {
                        this.loading = true;
                        this.user.orgId = this.carModelIds[this.carModelIds.length - 1];
                        this.user.birthday = this.user.birthday!=''?new Date(this.user.birthday).Format('yyyy-MM-dd'):'';
                        this.$ajax.post("/service_user/user/save", {
                                "data": JSON.stringify(this.user),
                                "dutyData": JSON.stringify(this.dutyOption.data),
                                "technicalData": JSON.stringify(this.tecOption.data),
                                "educationData": JSON.stringify(this.eduOption.data),
                                "familyData": JSON.stringify(this.famOption.data),
                                "workData": JSON.stringify(this.workOption.data),
                                "trainData": JSON.stringify(this.trainOption.data)
                            })
                            .then(res => {
                                if (res.data.code == 200) {
                                    this.$refs['user'].resetFields();
                                    this.$refs['userDuty'].resetFields();
                                    this.$refs['userTechnical'].resetFields();
                                    this.$refs['userEducation'].resetFields();
                                    this.$refs['userFamily'].resetFields();
                                    this.$refs['userWork'].resetFields();
                                    this.$refs['userTrain'].resetFields();
                                    this.loading = false;
                                    this.$Message.success("保存成功");
                                    this.$router.push("/person/view");
                                }
                            })
                            .catch((error) => {
                                if (!error.url) {
                                    console.info(error);
                                }
                            })
                    }
                })
            },
            //增加任职信息
            addUserDuty: function () {
                if(this.userDuty){
                    //时间
                    this.userDuty.entryDate = this.userDuty.entryDate!=''?new Date(this.userDuty.entryDate).Format('yyyy-MM-dd'):'';
                    this.userDuty.shiftDate = this.userDuty.shiftDate!=''?new Date(this.userDuty.shiftDate).Format('yyyy-MM-dd'):'';
                    this.userDuty.leaveDate = this.userDuty.leaveDate!=''?new Date(this.userDuty.leaveDate).Format('yyyy-MM-dd'):'';
                    //部门
                    this.userDuty.orgId = this.carModelIds1[this.carModelIds1.length-1];
                    var str=null;
                    for (var i=0; i<this.carModelTreeData1.length; i++)
                    {
                        var strmodel=this.carModelTreeData1[i];
                        if(strmodel.id == this.userDuty.orgId) {
                            str=strmodel.name;
                            break;
                        }
                    }
                    //var str=this.carModelTreeData1[this.carModelIds1.length-1].name;
                    this.userDuty.orgName=str.substring(0,str.indexOf("("));
                    this.userDuty.orgCode=str.substring(str.indexOf("(")+1,str.indexOf(")"));
                    //岗位
                    for (var i=0; i<this.postType.length; i++)
                    {
                        var strmodel=this.postType[i];
                        if(strmodel.id == this.userDuty.postId ) {
                            this.userDuty.postName=strmodel.name;
                            break;
                        }
                    }
                    //任职
                    for (var i=0; i<this.positionTypes.length; i++)
                    {
                        var strmodel1=this.positionTypes[i];
                        if(strmodel1.id == this.userDuty.positionId ) {
                            this.userDuty.positionName=strmodel1.name;
                            break;
                        }
                    }
                    this.dutyOption.data.push(JSON.parse(JSON.stringify(this.userDuty)));
                    this.$refs.dutyGrid.reLoad({});
                    this.$refs['userDuty'].resetFields();
                    this.carModelIds1 = [];
                }
            },
            //查看任职信息
            toViewDuty: function (obj) {
                this.showAddDuyButton = false;
                this.viewDuyShow = true;
                this.updateDuyShow = false;
                this.userDuty = JSON.parse(JSON.stringify(obj));
            },
            //修改任职信息
            toUpdateDuty: function (obj) {
                this.showAddDuyButton = false;
                this.viewDuyShow = false;
                this.updateDuyShow = true;
                this.userDuty = JSON.parse(JSON.stringify(obj));
                if(this.userDuty.orgId!=null){
                    this.carModelIds1 = this.$utils.getTreeInParentsId(this.carModelTreeData1, this.userDuty.orgId);
                }
            },
            updateUserDuty: function () {
                if(this.userDuty){
                    var index = this.userDuty._index;
                    //时间
                    this.userDuty.entryDate = this.userDuty.entryDate!=''?new Date(this.userDuty.entryDate).Format('yyyy-MM-dd'):'';
                    this.userDuty.shiftDate = this.userDuty.shiftDate!=''?new Date(this.userDuty.shiftDate).Format('yyyy-MM-dd'):'';
                    this.userDuty.leaveDate = this.userDuty.leaveDate!=''?new Date(this.userDuty.leaveDate).Format('yyyy-MM-dd'):'';
                    //部门
                    this.userDuty.orgId = this.carModelIds1[this.carModelIds1.length-1];
                    var str=null;
                    for (var i=0; i<this.carModelTreeData1.length; i++)
                    {
                        var strmodel=this.carModelTreeData1[i];
                        if(strmodel.id == this.userDuty.orgId ) {
                            str=strmodel.name;
                            break;
                        }
                    }
                    //var str=this.carModelTreeData1[this.carModelIds1.length-1].name;
                    this.userDuty.orgName=str.substring(0,str.indexOf("("));
                    this.userDuty.orgCode=str.substring(str.indexOf("(")+1,str.indexOf(")"));
                    //岗位
                    for (var i=0; i<this.postType.length; i++)
                    {
                        var strmodel=this.postType[i];
                        if(strmodel.id == this.userDuty.postId ) {
                            this.userDuty.postName=strmodel.name;
                            break;
                        }
                    }
                    //任职
                    for (var i=0; i<this.positionTypes.length; i++)
                    {
                        var strmodel1=this.positionTypes[i];
                        if(strmodel1.id == this.userDuty.positionId ) {
                            this.userDuty.positionName=strmodel1.name;
                            break;
                        }
                    }
                    this.dutyOption.data.splice(index,1,JSON.parse(JSON.stringify(this.userDuty)));
                    this.updateDuyShow	= false;
                    this.showAddDuyButton = true;
                    this.userDuty	= {};
                    this.carModelIds1 = [];
                }
            },
            //删除任职信息
            deleteDuty: function (obj) {
                var that = this;
                this.$Modal.confirm({
                    title: '删除',
                    content: '您确认删除此条数据？',
                    onOk: function () {
                        that.dutyOption.data.splice(obj._index, 1);
                    }
                })
            },
            //新增技术职称
            addUserTechnical:function () {
                if (this.userTechnical) {
                    this.tecOption.data.push(JSON.parse(JSON.stringify(this.userTechnical)));
                    this.$refs.tecGrid.reLoad({});
                    this.$refs['userTechnical'].resetFields();
                }
            },
            //修改技术职称
            toUpdateTec: function (obj) {
                this.showAddTecButton = false;
                this.viewTecShow = false;
                this.updateTecShow	= true;
                this.userTechnical = JSON.parse(JSON.stringify(obj));
                this.userTechnical.date = this.userTechnical.date!=''?new Date(this.userTechnical.date).Format('yyyy-MM-dd'):'';
            },
            //更新技术职称
            updateUserTechnical:function () {
                this.viewTecShow = false;
                if (this.userTechnical) {
                    var index = this.userTechnical._index;
                    this.tecOption.data.splice(index, 1, JSON.parse(JSON.stringify(this.userTechnical)));
                    this.updateTecShow = false;
                    this.showAddTecButton = true;
                    this.userTechnical = {};
                }
            },
            //删除技术职称
            deleteTec: function (obj) {
                var that = this;
                this.$Modal.confirm({
                    title: '删除',
                    content: '您确认删除此条数据？',
                    onOk: function () {
                        that.tecOption.data.splice(obj._index, 1);
                    }
                })
            },
            //添加教育经历
            addUserEducation:function () {
                if (this.userEducation) {
                    //时间
                    this.userEducation.entryDate = this.userEducation.entryDate!=''?new Date(this.userEducation.entryDate).Format('yyyy-MM-dd'):'';
                    this.userEducation.graduateDate = this.userEducation.graduateDate!=''?new Date(this.userEducation.graduateDate).Format('yyyy-MM-dd'):'';
                    //学历类别
                    for (var i=0; i<this.educationType.length; i++)
                    {
                        var strmodel=this.educationType[i];
                        if(strmodel.id == this.userEducation.educationTypeId ) {
                            this.userEducation.educationTypeName=strmodel.textName;
                            break;
                        }
                    }
                    this.eduOption.data.push(JSON.parse(JSON.stringify(this.userEducation)));
                    this.$refs.eduGrid.reLoad({});
                    this.$refs['userEducation'].resetFields();
                }
            },
            //修改教育经历
            toUpdateEdu: function (obj) {
                this.showAddEduButton = false;
                this.viewEduShow = false;
                this.updateEduShow = true;
                this.userEducation = JSON.parse(JSON.stringify(obj));
            },
            //修改教育经历
            updateUserEducation:function () {
                if (this.userEducation) {
                    var index = this.userEducation._index;
                    //时间
                    this.userEducation.entryDate = this.userEducation.entryDate!=''?new Date(this.userEducation.entryDate).Format('yyyy-MM-dd'):'';
                    this.userEducation.graduateDate = this.userEducation.graduateDate!=''?new Date(this.userEducation.graduateDate).Format('yyyy-MM-dd'):'';
                    //学历类别
                    for (var i=0; i<this.educationType.length; i++)
                    {
                        var strmodel=this.educationType[i];
                        if(strmodel.id == this.userEducation.educationTypeId ) {
                            this.userEducation.educationTypeName=strmodel.textName;
                            break;
                        }
                    }
                    this.eduOption.data.splice(index,1,JSON.parse(JSON.stringify(this.userEducation)));
                    this.updateEduShow	= false;
                    this.showAddEduButton = true;
                    this.userEducation	= {};
                }
            },
            //删除教育经历
            deleteEdu: function (obj) {
                var that = this;
                this.$Modal.confirm({
                    title: '删除',
                    content: '您确认删除此条数据？',
                    onOk: function () {
                        that.eduOption.data.splice(obj._index, 1);
                    }
                })
            },
			//添加家庭主要成员
            addUserFamily:function () {
                if (this.userFamily) {
                    this.userFamily.birthday = this.userFamily.birthday!=''?new Date(this.userFamily.birthday).Format('yyyy-MM-dd'):'';
                    this.famOption.data.push(JSON.parse(JSON.stringify(this.userFamily)));
                    this.$refs.famGrid.reLoad({});
                    this.$refs['userFamily'].resetFields();
                }
            },
            //修改家庭主要成员
            toUpdateFam: function (obj) {
                this.showAddFamButton = false;
                this.viewFamShow = false;
                this.updateFamShow	= true;
                this.userFamily = JSON.parse(JSON.stringify(obj));
            },
            //修改家庭主要成员
            updateUserFamily:function () {
                if (this.userFamily) {
                    this.userFamily.birthday = this.userFamily.birthday!=''?new Date(this.userFamily.birthday).Format('yyyy-MM-dd'):'';
                    var index = this.userFamily._index;
                    this.famOption.data.splice(index,1,JSON.parse(JSON.stringify(this.userFamily)));
                    this.updateFamShow	= false;
                    this.showAddFamButton = true;
                    this.userFamily	= {};
                }
            },
            //删除家庭成员
            deleteFam: function (obj) {
                var that = this;
                this.$Modal.confirm({
                    title: '删除',
                    content: '您确认删除此条数据？',
                    onOk: function () {
                        that.famOption.data.splice(obj._index, 1);
                    }
                })
            },
           //添加工作经历
            addUserWork:function () {
                if (this.userWork) {
                    this.userWork.startDate = this.userWork.startDate!=''?new Date(this.userWork.startDate).Format('yyyy-MM-dd'):'';
                    this.userWork.endDate = this.userWork.endDate!=''?new Date(this.userWork.endDate).Format('yyyy-MM-dd'):'';
                    this.workOption.data.push(JSON.parse(JSON.stringify(this.userWork)));
                    this.$refs.workGrid.reLoad({});
                    this.$refs['userWork'].resetFields();
                }
            },
            //修改工作经历
            toUpdateWork: function (obj) {
                this.showAddWorButton = false;
                this.viewWorShow = false;
                this.updateWorShow	= true;
                this.userWork = JSON.parse(JSON.stringify(obj));
            },
            updateUserWork:function () {
                if (this.userWork) {
                    var index = this.userWork._index;
                    this.userWork.startDate = this.userWork.startDate!=''?new Date(this.userWork.startDate).Format('yyyy-MM-dd'):'';
                    this.userWork.endDate = this.userWork.endDate!=''?new Date(this.userWork.endDate).Format('yyyy-MM-dd'):'';
                    this.workOption.data.splice(index,1,JSON.parse(JSON.stringify(this.userWork)));
                    this.updateWorShow	= false;
                    this.showAddWorButton = true;
                    this.userWork	= {};
                }
            },
            //删除工作经历
            deleteWork: function (obj) {
                var that = this;
                this.$Modal.confirm({
                    title: '删除',
                    content: '您确认删除此条数据？',
                    onOk: function () {
                        that.workOption.data.splice(obj._index, 1);
                    }
                })
            },
            //添加培训经历
            addUserTrain:function () {
                if (this.userTrain) {
                    this.userTrain.startDate = this.userTrain.startDate!=''?new Date(this.userTrain.startDate).Format('yyyy-MM-dd'):'';
                    this.userTrain.endDate = this.userTrain.endDate!=''?new Date(this.userTrain.endDate).Format('yyyy-MM-dd'):'';
                    this.trainOption.data.push(JSON.parse(JSON.stringify(this.userTrain)));
                    this.$refs.trainGrid.reLoad({});
                    this.$refs['userTrain'].resetFields();
                }
            },
		   //修改培训经历
		   toUpdateTrain: function (obj) {
			    this.showAddTraButton = false;
			    this.viewTraShow = false;
                this.updateTraShow	= true;
                this.userTrain = JSON.parse(JSON.stringify(obj));
           },
            updateUserTrain:function () {
                if (this.userTrain) {
                    var index = this.userTrain._index;
                    this.userTrain.startDate = this.userTrain.startDate!=''?new Date(this.userTrain.startDate).Format('yyyy-MM-dd'):'';
                    this.userTrain.endDate = this.userTrain.endDate!=''?new Date(this.userTrain.endDate).Format('yyyy-MM-dd'):'';
                    this.trainOption.data.splice(index,1,JSON.parse(JSON.stringify(this.userTrain)));
                    this.updateTraShow	= false;
                    this.showAddTraButton = true;
                    this.userTrain	= {};
                }
            },
		   //删除培训经历
		   deleteTrain: function (obj) {
                var that = this;
                this.$Modal.confirm({
                    title: '删除',
                    content: '您确认删除此条数据？',
                    onOk: function () {
                        that.trainOption.data.splice(obj._index, 1);
                    }
                })
           },

		   //重置表单
		   resetForm: function(name) {
               if(name='userDuty') {
                   this.carModelIds1 = [];
               }
                this.$refs[name].resetFields();

           },
          //点击单个tab时重置表单
		   resetFormNew: function() {
			    //当点击某个tab时，将新增按钮显示，修改按钮隐藏
			    var tab = this.tabName;
               if(tab == 'userDuty'){
                   this.showAddDuyButton = true;
                   this.updateDuyShow = false;
               }else if(tab == 'userTechnical'){
                   this.showAddTecButton = true;
                   this.updateTecShow = false;
               }else if(tab == 'userEducation'){
                   this.showAddEduButton = true;
                   this.updateEduShow = false;
               }else if(tab == 'userFamily'){
					this.showAddFamButton = true; 
					this.updateFamShow = false;				
				}else if(tab == 'userWork'){
					this.showAddWorButton = true;
					this.updateWorShow = false;
				}else if(tab == 'userTrain'){
					this.showAddTraButton = true;
					this.updateTraShow = false;
				}
                //this.$refs[tab].resetFields();
           },
            //学历类型
            getEducationType: function() {
                this.$ajax.post("/service_user/sysData/findByType", {'dataTypeCode': "XLLB_TYPE" })
                    .then(res => {
                        this.educationType = res.data.data;
                    })
                    .catch((error) => {
                        if (!error.url) {
                            console.info(error);
                        }
                    });
            },
            //政治面貌类型
            getPoliticsType: function() {
                this.$ajax.post("/service_user/sysData/findByType", {'dataTypeCode': "ZZMM_TYPE" })
                    .then(res => {
                        this.politicsType = res.data.data;
                    })
                    .catch((error) => {
                        if (!error.url) {
                            console.info(error);
                        }
                    });
            },
            //岗位类型
            getPostType: function() {
                this.$ajax.post("/service_user/post/findAll")
                    .then(res => {
                        this.postType = res.data.data;
                    })
                    .catch((error) => {
                        if (!error.url) {
                            console.info(error);
                        }
                    });
            },
            //职务类型
            getPositionType: function() {
                this.$ajax.post("/service_user/position/findAll")
                    .then(res => {
                        this.positionTypes = res.data.data;
                    })
                    .catch((error) => {
                        if (!error.url) {
                            console.info(error);
                        }
                    });
            },
            //部门树
            getCarModelTree: function () {
                this.$ajax.post("/service_user/organization/getOrgTree")
                    .then((res) => {
                        this.carModelTreeData = res.data.data;
                        this.carModelTree = this.$utils.Flat2TreeDataForCascader(res.data.data);
                        this.carModelTreeData1 = res.data.data;
                        this.carModelTree1 = this.$utils.Flat2TreeDataForCascader(res.data.data);
                        this.getData();
                    })
                    .catch((error) => {
                        if(!error.url){console.info(error);}
                    });
            },
            //以下都是文件上传
            openDialog: function() {
                this.$refs.webuploader.open_dialog();
            },
            beforeFileQueued:function(){
                //清空上传队列
                var arr = this.$refs.webuploader.uploader.getFiles();
                arr.map((j,i)=>{
                    this.$refs.webuploader.uploader.removeFile(j);
                })
            },
            //当头像加入队列时生成缩略图
            fileQueued: function(obj) {
                this.$refs.webuploader.uploader.makeThumb(obj.file, (error, src) => {
                    if(!error){
                        this.uploadimgurl = src;
                    }
                }, 1, 1);
            },
            //上传头像
            upload: function() {
                this.$refs.webuploader.upload();
            },
            uploadFinished: function() {
                this.$Message.success('文件上传成功');
            },
            uploadSuccess: function(obj) {
                this.user.photoUrl = obj.response.data.id;
            },
            removeFile: function(obj) {
                if (obj.file.id != null) {
                    this.deleteFileId = obj.file.id;
                }
            },
        },
		watch: {
            tabName: function(n, o) {
				if(this.type != 'view'){
                   this.resetFormNew();
				}				
            }
        },
        mounted: function() {
            //获取类型信息
            this.getCarModelTree();
            this.getPostType();
            this.getPositionType();
            this.getEducationType();
            this.getPoliticsType();
		},
}
</script>
