<template>
  <div>
  <draggable
    v-model="value"
    :options="dragOptions"
    class="outer-draggable"
    :group="groupC"
    animation="300"
    dragClass="dragClass"
    ghostClass="ghostClass"
    chosenClass="chosenClass"
    @add="addComponent"
  >
    <div v-for="item in value" :key="item.id" class="item">
      <div class="drag-border"></div>
      <div class="item-title">
        <div>
          {{ item.name
          }}<Button
            style="height: 20px; width: 5px; color: gray"
            type="text"
            ghost
            @click="handleDelete(item)"
            ><Icon type="md-close-circle"
          /></Button>
        </div>
        <!-- <CheckboxGroup v-model="checkAllGroup" >
          <Checkbox :label=" item.name " @change="handleCheckboxChange(item)"></Checkbox>
        </CheckboxGroup> -->
      </div>
      <div class="item-parms" v-if="item.parameters && item.parameters.length > 0">
        <div class="item-parms-item" v-for="(item2,index) in item.parameters" :key="item2.id">
          {{ item2.name }}
          <el-input
            ref="inputField"
            v-model="item2.value"
            style="width: 200px;height:30px"
            @focus="setActiveInput(item2.uuid)"
          />
        </div>
      </div>
      <div class="item-child">
        <task-item v-model="item.children"   @inputFocus="$emit('inputFocus', $event)"></task-item>
      </div>
    </div>
  </draggable>
  </div>
</template>
<script>
import draggable from "vuedraggable";
import { Table, TableColumn, Button, Input, Select, Option, Form } from "element-ui";
import "element-ui/lib/theme-chalk/index.css";
export default {
  name: "task-item",
  components: { draggable,ElTable: Table,
    ElTableColumn: TableColumn,
    ElButton: Button,
    ElInput: Input,
    ElSelect: Select,
    ElOption: Option,
    ElForm: Form, },

  props: {
    value: {
      type: Array,
      default: [],
    },
    isActive: {
      type: Boolean,
      required: true
    }
    
  },

  data() {
    return {
      dragOptions: {
        group: "nested", // 指定拖拽分组
      },
      groupC: {
        name: "site",
        pull: true,
        put: true,
      },
      indeterminate: true,
      checkAll: false,
      checkAllGroup: [],
    };
  },

  created() {},

  mounted() {},
  beforeDestroy() {},

  methods: {
    setFocusOnChildInput() {
      this.$refs.inputField.focus();
    },
    setActiveInput(uuid) {
      console.log(`当前是---${uuid}----输入框`);
      this.$emit('inputFocus',uuid)
    },
    handleDelete(item) {
      const index = this.value.findIndex((i) => i === item);
      if (index !== -1) {
        this.value.splice(index, 1);
      }
    },
    addComponent() {
  var list = JSON.parse(JSON.stringify(this.value));
  list.forEach((j) => {
    if (typeof j.id == "number" || j.id.match("origin")) {
      j.id = "item-" + new Date().getTime();
    }
    if (j.parameters) {
      j.parameters.forEach((k) => {
        if (!k.uuid.startsWith("parameter-")) {
          k.uuid = "parameter-" + new Date().getTime() + "-" + k.id;
        }
      });
    }
  });
  this.$emit("input", list);
}
  },
};
</script>

<style scoped>
.outer-draggable {
  padding: 10px;
  margin: 10px;
  border: 1px dashed #cccccc;
  border-radius: 15px;
}

.item {
  margin-bottom: 20px;
  border: 1px solid #cad8e8;
  border-radius: 15px;
  overflow: hidden;
}

.item-title {
  padding: 5px 20px;
  background: #dae8ff;
  border-radius: 15px 15px 0 0;
}

.item-parms {
  margin: 10px;
}

.item-parms-item {
  display: inline-block;
  background: #dae8ff;
  padding: 5px 5px;
  border-radius: 7px;
  margin-right: 5px;
  height: 40px;
}

.drag-border {
  border-bottom: 1px solid #e9e9e9;
  padding-bottom: 6px;
  margin-bottom: 6px;
}
.item-parms-item >>> .el-input__inner { 
  height: 30px;
}
</style>