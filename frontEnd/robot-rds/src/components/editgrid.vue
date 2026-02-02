<template>
  <el-form ref="form" :model="form">
    <el-table :data="form.item">
      <!-- 表格列循环 -->
      <el-table-column
        v-for="column in tableHeaders"
        :key="column.prop"
        :label="column.label"
      >
        <template v-slot="scope">
          <!-- 下拉选择编辑状态 -->
          <el-select v-if="column.type === 'select' && column.iseditable" v-model="scope.row[column.prop]">
            <!-- 下拉选项循环 -->
            <el-option
              v-for="item in column.options"
              :label="item.label"
              :value="item.value"
              :key="item.value"
            ></el-option>
          </el-select>
          <!-- 输入框编辑状态 -->
          <el-input
            v-else-if="column.type === 'input' && column.iseditable"
            v-model="scope.row[column.prop]"
            clearable
          ></el-input>
          <!-- 只读状态 -->
          <span v-else>{{ scope.row[column.prop] }}</span>
          <!-- 若还有其他类型的字段，继续添加相应的条件渲染逻辑 -->
        </template>
      </el-table-column>
      <!-- 操作列 -->
      <el-table-column label="操作" v-if="showAddButton">
        <template v-slot="scope">
          <el-button v-if="showAddButton" @click="delList(scope.row, scope.$index)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 按钮操作区域 -->
    <div style="display: flex; justify-content: center; margin-top: 10px" v-if="showAddButton">
      <el-button type="primary" @click="addList" size="mini"
        >新增行<i class="el-icon-plus el-icon--right"></i
      ></el-button>
    </div>
  </el-form>
</template>

<script>
import { Table, TableColumn, Button, Input, Select, Option, Form } from "element-ui";
import "element-ui/lib/theme-chalk/index.css";
export default {
  components: {
    ElTable: Table,
    ElTableColumn: TableColumn,
    ElButton: Button,
    ElInput: Input,
    ElSelect: Select,
    ElOption: Option,
    ElForm: Form,
  },
  props: {
    tableHeaders: {
      type: Array,
      required: true,
    },
    initialData: {
      type: Array,
      default: () => [],
    },
    showAddButton: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      form: {
        item: JSON.parse(JSON.stringify(this.initialData)),
        removeIdList: [],
      },
    };
  },
  watch: {
    initialData(newVal) {
      // 当 initialData 更新时，更新 form.item
      this.form.item = JSON.parse(JSON.stringify(newVal));
   
    },
  },
  created() {
    console.log("子组件中的showAddButton属性值为：", this.showAddButton);
  },
  methods: {
    addList() {
      // 添加新行时，确保新行具有足够的属性
      const newItem = this.tableHeaders.reduce((item, header) => {
        item[header.prop] = header.defaultValue || null;
        return item;
      }, {});
      this.form.item.push(newItem);
    },
    delList(item, index) {
      // 从data中删除该行数据
      this.form.item.splice(index, 1);

      // 如果该行数据之前已存在于数据库中，则将其id添加到removeIdList中
      if (item.id) {
        this.form.removeIdList.push(item.id);
      }
    },
  },
};
</script>

<style scoped></style>