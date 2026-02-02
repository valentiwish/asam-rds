<template>
    <div v-if="list && list.length>0">
        <template v-for="obj in list">
            <Submenu :name="obj.id"  
                v-if="obj.children && obj.children.length>0" 
                :key="obj.id">
                <template>
                    <i :class="obj.icon+' fa-fw'" slot="icon"></i>
                    <span slot="title">{{obj.name}}</span>
                </template>
                <menuList :list="obj.children" :key="obj.id"/>
            </Submenu>
            <MenuItem :name="obj.url"  :title="obj.name+' '+obj.url" v-else :key="obj.id">
                <i :class="obj.icon+' fa-fw'" slot="icon"></i>
                <span slot="title">{{obj.name}}</span>
            </MenuItem>
        </template>
    </div>
</template>

<script>
export default {
    name:"menuList",
    props: {
        list: Array
    },
    data: function () {
        return {
            
        };
    },
    computed:{
        
    },
    methods: {
        
    }
}
</script>