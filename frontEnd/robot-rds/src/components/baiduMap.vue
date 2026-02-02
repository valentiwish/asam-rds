<template>
    <div style="height:620px;">
        <div id="allmap" style="height:620px;"></div>
    </div>
</template>

<script>
export default {
    name: 'baiduMap',
    data(){
        return{
          
        }
    },
    methods:{
        initMap(){
            // GL版命名空间为BMapGL
            // 按住鼠标右键，修改倾斜角和角度
            var bmap = new BMapGL.Map("allmap");    // 创建Map实例
            bmap.centerAndZoom(new BMapGL.Point(108.953077,34.266161), 12);  // 初始化地图,设置中心点坐标和地图级别
            bmap.enableScrollWheelZoom(true);     // 开启鼠标滚轮缩放
        }
    },
    mounted() {
    //    this.initMap();
    },
    beforeDestroy(){
        
    }
}
</script>

<style scoped>
    
</style>