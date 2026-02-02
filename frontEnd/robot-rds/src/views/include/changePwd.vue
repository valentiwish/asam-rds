<template>
  <!-- 修改密码 -->
  <Modal v-model="value" title="修改密码" width="500" :closable="true">
    <Form
      :label-width="100"
      ref="account"
      :model="account"
      :rules="ruleValidate"
      @submit.prevent="submit"
    >
      <!-- <Form-item label="当前账号">
        <template>{{account.loginName}}</template>
      </Form-item> -->

      <Form-item label="原始密码" prop="password">
        <Input v-model.trim="account.password" placeholder="请输入原始密码" type="password"></Input>
      </Form-item>

      <Form-item label="新密码" prop="newPwd">
        <Input v-model.trim="account.newPwd" placeholder="请设置新密码" type="password"></Input>
      </Form-item>
      <Form-item style="margin-bottom:2px;">
          <Progress :percent="passwordStrength.percent" :stroke-width="5" :status="passwordStrength.status">
              <Icon type="checkmark-circled" v-show="account.newPwd.length>0"></Icon>
              <span v-show="account.newPwd.length>0">{{passwordStrength.text}}</span>
          </Progress>
      </Form-item>
      <Form-item label="确认密码" prop="newPwdRep">
        <Input v-model.trim="account.newPwdRep" placeholder="请再次输入新密码" type="password"></Input>
      </Form-item>
    </Form>
    <div slot="footer">
      <button type="primary" class="ivu-btn ivu-btn-primary" @click="handleSubmit('account')">保存</button>
      <Button type="ghost" style="margin-left: 8px" @click="closeModal()">关闭</Button>
    </div>
  </Modal>
</template>

<script>
export default {
  name: "changePwd",
  props: ["value"],
  components: {},
  computed: {},
  data: function () {
    return {
      account: {
        id: "",
        loginName:"",
        password: "",
        newPwd: "",
        newPwdRep: "",
      },
      passwordStrength:{
          text:"非常弱",
          percent:0,
          status:"wrong"
      },      
      ruleValidate: { // 员工管理
          password: [
              { required: true, message: '请输入原始密码', trigger: 'blur' }
          ],                 
          newPwd: [
              { required: true, message: '请输入新密码', trigger: 'blur' }
          ],              
          newPwdRep: [
              { required: true, message: '请输入确认密码', trigger: 'blur' }
          ],
      }
    };
  },
  methods: {
    openModal: function () {
      this.value = true;
    },
    submitAsyn(){
      this.$ajax
        .post("/service_user/account/changePwd", {
          accountId: this.account.id,
          oldPwd: this.account.password,
          newPwd: this.account.newPwd,
        })
        .then((res) => {
          if (200 == res.data.code) {
            this.$Message.success("密码修改成功");
            this.closeModal();
          } else {
            this.$Message.error(res.data.message);
          }
        })
        .catch((res) => {
          if (!error.url) {
            console.info(error);
          }
        });
    },
    handleSubmit(name) {
      this.$refs[name].validate((valid) => {
        if(valid){
          if (this.checkPwdSame()) {
            if(this.passwordStrength.percent<60){
                this.$Modal.confirm({
                    title: '提醒',
                    content: '您的新密码强度较弱，是否提交？',
                    onOk: () => {
                        this.submitAsyn();
                    }
                });
            }  
            else{
                this.submitAsyn();
            }
          }
        }else{
          this.$Message.error('密码修改表单校验失败!');
        }        
      });
    },
    checkPwdSame() {
      /* if ("" != this.account.newPwd && "" == this.account.newPwdRep) {
        this.$Message.success("请输入确认密码");
        return false;
      } else  */if (this.account.newPwd == this.account.newPwdRep) {
        return true;
      } else {
        this.$Message.error("确认密码与新密码不一致，请检查后重新输入");
        return false;
      }
    },
    closeModal() {
      this.value = false;
      this.value = false;
      this.account.id = null;
      this.account.password = "";
      this.account.newPwd = "";
      this.account.newPwdRep = "";
    },
    getUser: function () {
      this.$ajax.post("/service_user/account/getAccountInfo").then(
        (res) => {
          if (res.data.code == 200) {
            this.account.id = res.data.data.id;
            this.account.loginName = res.data.data.loginName;
            this.account.password = "";
          }
        },
        () => {}
      );
    },
    //校验密码强度
    checkStrength:function(){
        var value = this.account.newPwd,
            reg=/^[0-9]{6,20}$|^[a-zA-Z]{6,20}$/, //全是数字或全是字母 6-16个字符
            reg1=/^[A-Za-z0-9]{6,20}$/, //数字、26个英文字母 6-16个字符
            reg2=/^\w{6,20}$/;  // 由数字、26个英文字母或者下划线组成的字符串 6-16个字符
        if(value){
          if(value.length==0){
              this.passwordStrength = {
                  text:"非常弱",
                  percent:0,
                  status:"wrong"
              }
          }
          else if(value.length<6){
              this.passwordStrength = {
                  text:"非常弱",
                  percent:10,
                  status:"wrong"
              }
          }
          else if(value.match(reg)){
              this.passwordStrength = {
                  text:"强度较弱",
                  percent:40,
                  status:"wrong"
              }
          }
          else if(value.match(reg1)){
              this.passwordStrength = {
                  text:"强度适中",
                  percent:60,
                  status:"success"
              }
          }
          else if(value.match(reg2)){
              this.passwordStrength = {
                  text:"密码较强",
                  percent:100,
                  status:"success"
              }
          }
        }
        else{
          this.passwordStrength = {
              text:"非常弱",
              percent:10,
              status:"wrong"
          }
        }
        
    }
  },
  mounted() {},
  created: function () {
    if (this.value) {
      this.getUser();
    }
  },
  watch: {
    value: function (n) {
      if (n) {
        this.getUser();
      }
    },
    "account.newPwd":function(){
        this.checkStrength();
    },
  },
};
</script>

<style scoped>
</style>