<template>
    <div class="multiple-cascader">
        <Cascader :data="treeData" trigger="hover" v-model="selectval" @on-change="onchange" @on-visible-change="onvisiblechange" :disabled="disabled !== false">
            <div class="ivu-select ivu-select-multiple" :class="{'ivu-select-visible':visible,'ivu-select-disabled':disabled !== false}">
                <div class="ivu-select-selection">
                    <div class="ivu-tag ivu-tag-checked" v-for="(obj,index) in selectedData" :key="obj.id">
                        <span class="ivu-tag-text">{{obj.label}}</span>
                        <i class="ivu-icon ivu-icon-ios-close-empty" @click="removelist(index)" v-if="!(disabled !== false)"></i>
                    </div>
                    <span v-if="selectedData.length===0" class="multiple-cascader-placeholder">{{placeholder}}</span>
                    <i class="ivu-icon ivu-icon-ios-close ivu-select-arrow" :class="{'canshow':selectedData.length>0 && !(disabled !== false)}" @click="removeAll"></i>
                    <i class="ivu-icon ivu-icon-arrow-down-b ivu-select-arrow"></i>
                </div>
            </div>
        </Cascader>
    </div>
</template>

<script>
    export default {
        name: 'multipleCascader',
        props: {
            value: {
                default: '',
            },
            data: {
                default: [],
            },
            disabled: {
                default: false,
            },
            placeholder:{
                type:String,
                default:""
            }
        },
        data: function() {
            return {
                selectval: [],
                selectedData: [],
                visible: false,
                handle: false,
            }
        },
        computed: {
            treeData: function() {
                return this.$utils.Flat2TreeDataForCascader(this.data);
            },

        },
        methods: {
            onchange: function(value, selectedData) {
                var flag = this.selectedData.some(function(j) {
                    return value[value.length - 1] == j.value;
                });
                !flag && this.selectedData.push(selectedData[selectedData.length - 1]);
                this.$nextTick(() => {
                    this.selectval = [];
                })
            },
            removelist: function(index) {
                if (this.disabled === false) {
                    this.selectedData.splice(index, 1);
                }
            },
            removeAll: function() {
                this.selectedData = [];
            },
            onvisiblechange: function(flag) {
                this.visible = flag;
            },
            setVal: function() {
                let list = this.value.map((j) => {
                    return this.$utils.getTreeNodeById(this.data, j);
                });
                this.handle = true;
                this.selectedData = list.map(function(j) {
                    j.label = j.name;
                    j.value = j.id;
                    return j;
                });
                this.$nextTick(() => {
                    this.handle = false;
                })
            }
        },
        watch: {
            selectedData: function() {
                if (this.handle == false) {
                    let a = this.selectedData.map(function(j) {
                        return j.value;
                    });
                    this.$emit('input', a);

                }

            },
            value: function(n) {
                this.setVal();
            }
        },
        created: function() {
            this.setVal();
        }
    }
</script>

<style scoped>
    .multiple-cascader .ivu-select-selection .ivu-icon-ios-close{
        display:none;
        right:20px;
    }
    .multiple-cascader .ivu-select-selection:hover .ivu-icon-ios-close.canshow{
        display:inline-block;
    }
    .multiple-cascader .ivu-select-selection .multiple-cascader-placeholder{
        line-height:28px;
        color:#BBBEC4;
        margin-left:2px;
    }
</style>
