<template>
  <div>
    <draggable v-model="items" :options="dragOptions" class="outer-draggable">
      <!-- 外层拖拽元素 -->
      <div v-for="(item, index) in items" :key="index">
        {{ item.name }}
        <draggable v-model="item.children" :options="dragOptions" class="inner-draggable">
          <!-- 内层拖拽元素 -->
          <div v-for="(child, childIndex) in item.children" :key="childIndex">
            {{ child.name }}
          </div>
        </draggable>
      </div>
    </draggable>
  </div>
</template>

<script>
import draggable from 'vuedraggable';
export default {
  components: {
    draggable,
  },
  data() {
    return {
      items: [
        {
          name: 'Item 1',
          children: [
            { name: 'Child 1' },
            { name: 'Child 2' },
          ],
        },
        {
          name: 'Item 2',
          children: [
            { name: 'Child 3' },
            { name: 'Child 4' },
          ],
        },
      ],
      dragOptions: {
        group: 'nested', // 指定拖拽分组
      },
    };
  },
};
</script>

<style scoped>
.outer-draggable {
  padding: 10px;
  border: 1px solid #ccc;
}

.inner-draggable {
  margin-top: 5px;
  padding: 5px;
  border: 1px solid #eee;
}
</style>
