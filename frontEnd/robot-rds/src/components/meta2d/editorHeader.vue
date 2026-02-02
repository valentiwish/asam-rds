<template>
  <div class="editor-header">
    <div class="editor-header-menu">
      <div class="header-menu-btn btn-green">
        <img src="static/images/editor/import.png"/>
        <span>导入</span>
      </div>
      <div class="header-menu-btn btn-red">
        <img src="static/images/editor/delete.png"/>
        <span>清空</span>
      </div>
      <div class="header-menu-btn btn-orange">
        <img src="static/images/editor/download.png"/>
        <span>保存</span>
      </div>
      <div class="header-menu-btn btn-blue">
        <img src="static/images/editor/export.png"/>
        <span>导出</span>
      </div>
      <div class="header-menu-btn btn-purple">
        <img src="static/images/editor/edit.png"/>
        <span>编辑</span>
      </div>
    </div>
    <div class="editor-header-btn">
    </div>
  </div>
</template>

<script>
export default {
  name: 'editorHeader',
  components: {},
  props: {
  },
  data () {
    return {

    }
  },
  methods: {
  },
  mounted () {
  },
  created () {
  },
  watch: {
  }
}
</script>

<style scoped>
.editor-header {
  position: absolute;
  top: 0;
  width: 100%;
  height: 50px;
  padding: 0 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border: 1px solid #f0f0f0;
  box-shadow: 0px 2px 4px #dad7d7;
  z-index: 1000;
}

.editor-header-menu {
  display: flex;
  justify-content: center;
  align-items: center;
}
.editor-header-btn Button {
  margin: 5px;

}
div /deep/ .editor-header-btn .ivu-btn {
  font-size: 16px;
  border-radius: 5px;
  padding: 0 10px;
}

.header-menu-btn {
  display: flex;
  align-items: center;
  margin: 0 5px;
  border-radius: 5px;
  padding: 3px 8px;
}
.header-menu-btn img {
  width: 25px;
  height: 25px;
}
.btn-green {
  border: #32c558 solid 1px;
  color: #32c558;
}
.btn-blue {
  border: #00c5ff solid 1px;
  color: #00c5ff;
}
.btn-orange {
  border: #ff8a00 solid 1px;
  color: #ff8a00;
}
.btn-red {
  border: #ff4d4c solid 1px;
  color: #ff4d4c;
}
.btn-purple {
  border: #6078f1 solid 1px;
  color: #6078f1;
}
</style>