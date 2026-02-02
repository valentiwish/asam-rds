<template>
  <div class="topo-container">
    <EditorHeader v-if="editable"></EditorHeader>
    <div class="topo-main">
      <EditorIcon class="topo-icon" v-if="editable"></EditorIcon>
      <div id="meta2d" class="topo-canvas" style="width:100%;height:100%;"></div>
      <div class="topo-props"></div>
    </div>
  </div>
</template>

<script>
import { pointInRect } from './topoCalc';
import './constant';
import EditorHeader from './editorHeader.vue';
import EditorIcon from './editorIcon.vue';

export default {
  name: 'topologyEditor',
  components: { EditorIcon, EditorHeader },
  props: {
    editable: Boolean
  },
  data () {
    return {
      meta2d: null,
      finds: []
    }
  },
  methods: {
    initEditor: function () {
      if (this.meta2d == null) {
        this.meta2d = new Meta2d("meta2d");
      }
    },
    drawData: function (data) {
      this.initEditor();
      this.meta2d.open(data);
      this.meta2d.fitView(true, 70);
      this.meta2d.centerView();
      this.meta2d.on('click', this.onClick);
    },
    addPen: function (pen) {
      this.meta2d.addPen(pen);
    },
    addPens: function (pens) {
      this.meta2d.addPens(pens);
    },
    setPenRect: function (pen, rect) {
      this.meta2d.setPenRect(pen, rect);
    },
    addPoints: function (pens) {
      this.meta2d.addPens(pens);
    },
    onClick: function (e) {
      if (typeof e.pen !== "undefined") {
        this.$emit('penClick', e.pen);
      }
    },
    pointInRect: function (obj, pen) {
      let results = this.calculateAnchorCoordinates(pen, obj);
      let flag = this.isPointInPolygon(obj, results);
      return flag
    },
    isPointInPolygon (point, polygon) {
      const x = point.x, y = point.y;
      let inside = false;

      for (let i = 0, j = polygon.length - 1; i < polygon.length; j = i++) {
        const xi = polygon[i].actualX, yi = polygon[i].actualY;
        const xj = polygon[j].actualX, yj = polygon[j].actualY;

        const intersect = ((yi > y) !== (yj > y)) &&
          (x < (xj - xi) * (y - yi) / (yj - yi) + xi);
        if (intersect) inside = !inside;
      }
      return inside;
    },
    findOne: function (id) {
      return this.meta2d.find(id);
    },
    gotoCenter: function (pen) {
      this.meta2d.gotoView(pen);
    },
    drawLine () {
      this.meta2d.lock(0);
      this.meta2d.drawLine('line');
    },
    finishDrawLine () {
      this.meta2d.finishDrawLine();
      this.meta2d.lock(2);
    },
    delete (pens) {
      this.meta2d.delete(pens);
    },
    inactive () {
      this.meta2d.inactive();
    },
    getById (id) {
      return this.meta2d.find(id);
    },
    getByTag (tag) {
      return this.meta2d.find(tag);
    },
    calculateAnchorCoordinates (pen) {
      const results = [];
      const { x, y, ex, ey, anchors } = pen;
      const width = ex - x;
      const height = ey - y;

      anchors.forEach(anchor => {
        const actualX = x + (anchor.x * width);
        const actualY = y + (anchor.y * height);

        results.push({
          id: anchor.id || '',
          originalX: anchor.x,
          originalY: anchor.y,
          actualX: parseFloat(actualX.toFixed(1)),
          actualY: parseFloat(actualY.toFixed(1)),
          penId: pen.id,
          isStart: anchor.start || false
        });
      });

      return results;
    },
  },
  mounted () {
    this.initEditor();
  },
  created () {
  },
  watch: {
  }
}
</script>

<style scoped>
.topo-container {
  position: relative;
  width: 100%;
  height: calc(100% - 50px);
  background-color: #fcf5fc;
  border: 1px solid #f0f0f0;
}
.topo-main {
  height: 100%;
  overflow: hidden;
  position: relative;
  display: flex;
}
.topo-icon {
  flex: 0 200px;
  width: 0;
  height: 100%;
  overflow-y: auto;
  border-right: 1px solid #f0f0f0;
  box-shadow: 0px 5px 5px 1px #dad7d7;
  z-index: 100;
}
.topo-canvas {
  flex: 1;
  position: relative;
  background-color: #fff;
}
</style>