<template>
    <div class="cover-img">
        <div class="cover-img_content" :style="{'background-image':'url('+src+')'}">
            <img :src="src" alt="">
        </div>
    </div>
</template>

<script>
export default {
    name: 'coverimg',
    props:{
        src:{
            type:String,
            default:""
        }
    },
    data:function(){
        return {
                
        }
    },
    computed:{

    },
    methods:{

    },
    mounted:function(){
        
    }
}
</script>

<style scoped>
    .cover-img{
        overflow: hidden;
    }
    .cover-img img{
        display: none;
    }
    .cover-img_content{
        background-repeat:no-repeat;
        background-position:center center;
        background-size:cover;
        transition: transform 200ms linear;
        width:100%;
        height:100%;
    }
    .cover-img:hover .cover-img_content{
        transform: scale(1.05,1.05);
    }
</style>