<template>
    <Cascader :data="citydata" v-model="value1" @on-change="onchange"></Cascader>
</template>

<script>
import citydata from '@/data/city'
export default {
    name: 'distpicker',
    props: {
        value: {
            default: '',
        }
    },
    data: function() {
        return {
            citydata:citydata,
            value1:[],
            change:false
        }
    },
    methods: {
        onchange:function(value){
            this.change = true;
            this.$emit('input', value);
            this.$nextTick(()=>{
                this.change = false;
            })
        },
        setVal:function(value){
            this.value1 = value;
        },
        getNodes(arr){
            if(!arr){
                arr = this.value1;
            }
            var arr = [],lastObj = citydata;
            var findOne = (item) =>{
                return lastObj.find((j,i)=>{
                    j.value === item;
                });
            }
            arr.map((item)=>{
                var obj = this.findOne(item);
                if(obj.children){
                    lastObj = obj.children;
                }
                arr.push(obj);
            })
        }
    },
    watch: {
        value:function(){
            if(this.change ===false){
                this.setVal(this.value)
            }
        }
    },
    created: function() {
        this.setVal(this.value)
    }

}
</script>

<style scoped>

</style>