<template>
    <Drawer
      v-model="visible"
      :title="title"
      direction="rtl"
      :draggable="draggable"
      :width="width"
      @on-resize-width="resizeWidth"
      @on-close="close($event)">
      <iframe v-if="visible" :src="src" frameborder="0"></iframe>
      <div class="iframe-mask" v-show="showMask"></div>
    </Drawer>
</template>

<script>
    export default{
        name: 'drawerIframe',
        props: {
            draggable:{
                type: String,
                default: "70%",
            },
            title:{
                type: String,
                default: "", 
            },
            width:{
                type: String,
                default: "70%",
            },
            visible: {
                type: Boolean,
                default: false
            },
            src:{
                type: String,
                default: "",
            },
        },
        data(){
            return {
                showMask:false
            }
        },
        "methods": {
            resizeWidth(width){
                clearTimeout(this.timer);
                this.showMask = true;
                this.timer = setTimeout(()=>{
                    this.showMask = false;
                },200)
            },
            close(evt){
                console.log(evt)
                this.visible = false;
                this.$emit("close",evt)
            }
        },
        mounted: function() {
            
        },
        watch:{
            message(){
                this.$emit("input",this.message);
            }
        }
    }
</script>

<style scoped>
  iframe,.iframe-mask{
    position: absolute;
    left:0;
    top:0;
    width:100%;
    height:100%;
    border: none;
  }
</style>