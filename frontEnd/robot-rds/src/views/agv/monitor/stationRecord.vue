<template>
  <div>
    <data-grid :option="gridOption" ref="grid"></data-grid>
  </div>
</template>
<script>
import dataGrid from "@/components/datagrid"

export default {
  name: 'stationRecord',
  props: [],
  components: {
    dataGrid
  },
  data () {
    var that = this;
    return {
      gridOption: {
        url: "/service_rms/robotInfo/list",
        auto: true,//该页面是否自动加载，不会自动调用list方法
        height:"450px",
        columns: [ //列配置
          {
            "title": "机器人名称",
            "key": "vehicleId",
            "align": "center",
            "width": "110px"
          },
          {
            "title": "机器人IP",
            "key": "currentIp",
            "align": "center"
          },
          {
            "title": '操作', 
            "width":"100px",
            "align": "center",
            render (h, params) {
              return h('div', [
                h('Button', {
                  on: {
                    click: () => {
                      that.toAccepted(params.row),
                        that.search();
                    }
                  }
                }, '查看')])
            }
          }
        ],
        "loadFilter": function (data) {
          return data.data;
        }
      },
    }
  },
  methods: {
    view: function (e) {
      console.log(e)
    }
  },
  mounted () {
  }
}
</script>
