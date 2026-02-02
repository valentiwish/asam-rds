<template>
    <div class="font-icon" :class="{'showicon':val!= ''}">
        <span class="icon"><i :class="val[1]"></i></span>
        <Cascader :data="data" v-model="val" trigger="hover" filterable></Cascader>
    </div>
</template>

<script>
    import fontIconData from '@/data/fontIcon';
    var catalog = [];
    var render = function(h, data) {
        return h("i", {
            class: "fa-fw selectshow-icon " + data.value,
            style: "margin-right:6px;"
        });
    };
    fontIconData.map(function(j, i) {
        var last = catalog[catalog.length - 1];
        if (last && last.label == j.pageName) {
            last.children.push({
                "label": j.iconName,
                "value": j.iconClass,
                "render": render
            });
        } else {
            catalog.push({
                "label": j.pageName,
                "value": j.pageName,
                "children": [{
                    "label": j.iconName,
                    "value": j.iconClass,
                    "render": render
                }]
            });
        }
    });
    export default{
        name: 'fontIcon',
        props: {
            value: {
                default: '',
            }
        },
        data(){
            return {
                data: catalog,
                dataOr: fontIconData,
                val: []
            }
        },
        methods: {
            setVal: function() {
                //回显数据
                var arr = this.dataOr.filter((j) => {
                    return j.iconClass == this.value;
                });
                if (arr.length > 0) {
                    this.val = [arr[0].pageName, this.value]
                } else {
                    this.val = [];
                }
            }
        },
        watch: {
            val: function() {
                this.$emit('input', this.val[1] || "");
            },
            value: function() {
                this.setVal();
            }
        }
    }
</script>

<style>
    .font-icon .icon {
        width: 30px;
        text-align: center;
        display: none;
        position: absolute;
        top: 0;
        left: 0;
        z-index: 1;
        border-right: 1px solid #dddee1;
    }

    .font-icon .ivu-cascader-menu .ivu-cascader-menu-item i.selectshow-icon {
        position: static;
        transform:none;
    }

    .font-icon.showicon .icon {
        display: inline;
    }

    .font-icon.showicon .ivu-input-wrapper input {
        padding-left: 40px;
    }

    .font-icon.showicon .ivu-cascader-label {
        left: 35px;
    }
</style>
