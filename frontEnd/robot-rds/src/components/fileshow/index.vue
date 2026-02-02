<template>
    <div class="fileshow">
        <fileshow-item  @onRemove="onRemove" :item="item" v-for="item in list" :key="item.id"/>
    </div>
</template>

<script>
import fileshowItem from "./item";
export default {
    name: 'fileshow',
    props: ['list'],
    components:{fileshowItem},
    data: function() {
        return {
            
        }
    },
    methods:{
        getIcon:function(type){
            type=type.toUpperCase();
            var icon = this.icons[type];
            if(!icon){
                return "fa-file";
            }
            return icon;   
        },
        onRemove:function(item){
            this.$emit("onRemove",item);
        }
    }
};
</script>


