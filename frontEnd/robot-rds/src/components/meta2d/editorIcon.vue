<template>
  <div class="editor-icon">
    <Collapse accordion>
      <Panel name="common" v-if="oftenIcons" class="editor-icon-title">
        常用图形({{ oftenIcons.length }})
        <template #content>
          <div class="icon-content" @dragstart="onDragstart($event)">
            <div v-show="oftenIcons.length < 1" class="empty">
              <span>暂无常用图标</span>
            </div>
            <div class="icon" v-for="(icon, index) in oftenIcons" :key="index" draggable="true" :data-index="index">
              <i :class="`t-icon ${icon.icon}`" :title="icon.name"></i>
            </div>
          </div>
        </template>
      </Panel>
      <Panel :name="group.groupName" v-for="(group, index) in icons" :key="index" >
        {{ group.groupName }}
        <template #content>
          <div class="icon-content" @dragstart="onDragstart($event,group.icons)">
            <div class="icon" v-for="(icon, index) in group.icons" :key="index" draggable="true" :data-index="index">
              <i :class="`t-icon ${icon.icon}`" :title="icon.name"></i>
            </div>
          </div>
        </template>
      </Panel>
    </Collapse>
  </div>
</template>

<script>
import { IconGroups } from '../../data/constant';
import './iconfont.css'
import store from '../../store/modules/editor'


export default {
  name: 'editorIcon',
  components: {},
  props: {
  },
  data () {
    return {
      icons:Object.values(IconGroups),
      oftenIcons:store.oftenIcons
    }
  },
  methods: {
    onDragstart:function(e,icons){
      let index = e.target && e.target instanceof HTMLElement ? Number(e.target.dataset['index']) : -1;
      if (index > -1) {
        let icon = icons[index];
        e.dataTransfer.setData('Text', JSON.stringify(icon.pen));
        store.mutations.addOftenIcon(icon)
      }
    }
  },
  mounted () {
    // let oftenIcons = this.$store.state.topology.oftenIcons
    // console.log(this.$store.state)
  },
  created () {
  },
  watch: {
  }
}
</script>
<style scoped>
.editor-icon {
  height: 100%;
  width: 200px;
  user-select: none;
}

.icon-content {
  margin: 0 auto;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  margin: auto;
}
.icon{
    flex: 0 40px;
    width: 0;
    text-align: center;
    height: 40px;
    display: flex;
    justify-content: center;
    align-items: center;
    border-radius: 5px;
    color: rgba(0, 0, 0, 0.8509803922);
}
.icon > i {
    font-size: 24px;
}
.empty {
    width: 100%;
    font-size: 12px;
    color: #afafaf;
    text-align: center;
  }
div /deep/ .ivu-collapse>.ivu-collapse-item>.ivu-collapse-header {
  color: #6078f1;
}
div /deep/ .ivu-collapse {
  border: none;
}
div /deep/ .ivu-collapse>.ivu-collapse-item {
  border: none;
}
div /deep/ .ivu-collapse>.ivu-collapse-item-active>.ivu-collapse-header {
  background-color: #6078f1;
  color: #fff;
}
</style>