<template>
     <router-view></router-view>
</template>

<script>
export default {
    name: 'App'
}
</script>

<style>
  .clearfix{
    clear:both;
  }
  .main-container{
    position: relative;
    width:500px;/* width:1300px; */
    margin:auto;
  }
  .ivu-carousel-dots-inside{
    right:10px;
    text-align:right;
  }
  .ivu-carousel-dots li button{
    height:5px;
    background: #1196DB;
  }
  .section-border{
    position: relative;
    border:1px solid #E7E7E7;
  }
  .clearfix{
    clear: both;
  }
</style>
