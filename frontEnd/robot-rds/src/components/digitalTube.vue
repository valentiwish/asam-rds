<template>
  <div class="digital-tube">
        <div v-for="(item,index) in text" class="digital-tube-item" :key="text.length-index">
            <div class="digital-tube-item-content digital-tube-item-content-1" :style="{'transform':getTop(item)}">
                <div class="digital-tube-item-icon shuzi" v-for="i in 10" :key="i">
                    <i :class="'iconfont icon-shuzi'+(i-1)"></i>
                </div>
                <div class="digital-tube-item-icon comma">
                    <span class="comma">,</span>
                </div>
                <div class="digital-tube-item-icon dot">
                    <span class="dot">.</span>
                </div>
                <div class="digital-tube-item-icon negative">
                    <span class="negative">-</span>
                </div>
            </div>

            <div class="digital-tube-item-content digital-tube-item-content-2">
                <div class="digital-tube-item-icon comma" v-if="item==','">
                    <span class="comma">,</span>
                </div>
                <div class="digital-tube-item-icon dot" v-else-if="item=='.'">
                    <span class="dot">.</span>
                </div>
                <div class="digital-tube-item-icon negative" v-else-if="item=='-'">
                    <span class="negative">-</span>
                </div>
                <div class="digital-tube-item-icon shuzi" v-else>
                    <i :class="'iconfont icon-shuzi'+item"></i>
                </div>
            </div>
            
        </div>

    </div>
</template>

<script>
export default {
    name:'digitalTube',
    props:{
        value: {
            type: [String, Number],
            default:""
        }
    },
    computed:{
        text:function(){
            var arr = (this.value+"").split(".");
            var ret = this.toThousands(arr[0]);
            if(arr[1] !=void 0){
                ret = ret+"."+arr[1].substring(0,2);
            }
            
            return ret;
        },
        posMapLen:function(){
            return Object.getOwnPropertyNames(this.posMap).length;
        }
    },
    data:function(){
        return {
            posMap:{
                "0":0,
                "1":1,
                "2":2,
                "3":3,
                "4":4,
                "5":5,
                "6":6,
                "7":7,
                "8":8,
                "9":9,
                ",":10,
                ".":11,
                "-":12
            }
        }
    },
    methods:{
        //数字加，
        "toThousands":function(num) {
            return (num || 0).toString().replace(/(\d)(?=(?:\d{3})+$)/g, '$1,');
        },
        getTop:function(item){
            var pos = (this.posMap[item]/(this.posMapLen-1))*100;
            return "translateY(-"+pos+"%)";
        }
    },
    watch:{
        
    }
}
</script>

<style scoped>
@import "./icon-number/iconfont.css";
.digital-tube{
    display:inline-block;
    font-size:inherit;
    height:35px;
    overflow:hidden
}
.digital-tube-item{
    display:inline-block;
    font-size:inherit;
    height:35px;
}
.digital-tube-item-content{
    transition:all ease 1500ms;
}
.digital-tube-item-content.digital-tube-item-content-2{
    display:none;
}
.digital-tube-item .iconfont{
    font-size:inherit;
}
.digital-tube-item-icon{
    text-align:center;
    letter-spacing:-3px;
}
@media(max-width:450px){
    .digital-tube-item-content.digital-tube-item-content-1{
        display:none;
    }
    .digital-tube-item-content.digital-tube-item-content-2{
        display:inline-block;
    }
}
</style>