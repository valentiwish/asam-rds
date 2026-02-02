<template>
    <div>
        <page-title></page-title>
        <div class="page-search">
            <div class="page-search-title">查询条件</div>
            <div class="page-search-content">
                <Form ref="s" v-model="s" :label-width="120">
                    <FormItem label="类型编号">
                        <Input v-model.trim="s.typeCode" maxlength="50" placeholder="请输入类型编号"></Input>
                    </FormItem>

                    <FormItem label="类型名称">
                        <Input v-model.trim="s.typeName" maxlength="50" placeholder="请输入类型名称"></Input>
                    </FormItem>
                </Form>
                <div class="search-btn">
                    <allowBtn allow="allowsearch" icon="ios-search" @click="search">查询</allowBtn>
                    <allowBtn allow="allowreset" icon="ios-refresh-empty" @click="reset">重置</allowBtn>
                </div>
            </div>
        </div>
        <div class="page-content">
            <data-grid :option="gridOption" ref="grid"></data-grid>
        </div>
    </div>
</template>

<script>
    import dataGrid from "@/components/datagrid"
    export default {
        components: {dataGrid},
        data: function () {
            var that = this;
            return {
                spinShow: true, //未进入页面时，加载过程中显示加载中
                s: {
                    typeCode: '',
                    typeName: '',
                },
                "gridOption": {
                    "data": [],
                    "auto": true,
                    "url": "/service_basedata/type/list",
                    "columns": [  //列配置
                        {
                            "title":"类型编码",
                            "key": "typeCode",
                            "align": "center",
                        },
                        {
                            "title":"类型名称",
                            "key": "typeName",
                            "align": "center",
                        },
                        {
                            "title":"变量数量",
                            "key": "varCount",
                            "align": "center",
                        },
                        {
                            "title":"备注",
                            "key": "remark",
                            "align": "center",
                        },
                        {
                            key: 'tool', title: '操作', align: "center", width: 280, render(h, params) {
                                return h('div', [
                                    h('allowBtn', {
                                        props: {
                                            type: 'primary',
                                            size: 'small',
                                            allow: "查看"
                                        },
                                        style: {
                                            marginRight: '5px'
                                        },
                                        on: {
                                            click: () => {
                                                that.toView(params.row);
                                            }
                                        }
                                    }, '查看'),
                                    h('allowBtn', {
                                        props: {
                                            type: 'primary',
                                            size: 'small',
                                            allow: "修改"
                                        },
                                        style: {
                                            marginRight: '5px'
                                        },
                                        on: {
                                            click: () => {
                                                that.toUpdate(params.row);
                                            }
                                        }
                                    }, '修改'),
                                    h('allowBtn', {
                                        props: {
                                            type: 'info',
                                            size: 'small',
                                            allow: "删除",
                                            disabled: params.row.varCount > 0 ? true : false
                                        },
                                        on: {
                                            click: () => {
                                                that.del(params.row);
                                            }
                                        }
                                    }, '删除')
                                ]);
                            }
                        }],
                    "loadFilter": function (data) {
                        return data.data;
                    }
                }
            }
        },
        methods: {
            reset: function () {
                this.s.typeCode = '';
                this.s.typeName = '';
                this.$refs.grid.load();
            },
            search: function () {
                this.$refs.grid.load({
                    "typeCode": this.s.typeCode,
                    "typeName": this.s.typeName
                });
            },
            //以下三个的行方法
            toUpdate: function (obj) {
                this.$router.push('/scadaType/update/' + obj.id + "/update");
            },
            toView: function (obj) {
                this.$router.push('/scadaType/view/' + obj.id + "/view");
            },
            del: function (obj) {
                let that = this;
                this.$Modal.confirm({
                    title: '删除',
                    content: '确认删除此条数据吗？',
                    onOk: function () {
                        // 跳转
                        this.$ajax.post("/service_basedata/type/delete", {'id': obj.id})
                            .then((res) => {
                                if ("000" == res.data.data.code){
                                    this.$Message.success(res.data.data.message);
                                    that.$refs['grid'].reLoad();
                                }
                                else if ("001" == res.data.data.code){
                                    this.$Message.warning(res.data.data.message);
                                }
                            })
                            .catch((error) => {
                                if (!error.url) {
                                    console.info(error);
                                }
                            })
                    }
                })
            }
        },
        created: function () {

        },
        mounted: function(){
            
        },
    };

</script>
