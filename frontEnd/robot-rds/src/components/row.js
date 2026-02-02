define(["vue","./bus"],function(Vue,Bus) {
    return {
        props: {
            row: Object,
            rowTemplate:String,
        },
        data:function(){
            return {
                template: function () {return;}
            }
        },
        render(h){
            return h('div', [this.template()]);
        },
        created:function(){
            this.template = Vue.compile(this.rowTemplate).render;
            this.$Bus = Bus;
        }
    };
});
