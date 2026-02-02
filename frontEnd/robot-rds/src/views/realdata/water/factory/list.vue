<template>
    <div>
        <page-title></page-title>
        <div class="page-search">
            <div class="page-search-title">查询条件</div>
            <div class="page-search-content">
                <Form ref="s" v-model="s" :label-width="80">
                    <FormItem label="名称">
                        <Input v-model.trim="s.name" maxlength="50" placeholder="请输入名称" />
                    </FormItem>

                    <FormItem label="地址">
                        <Input v-model.trim="s.address" maxlength="50" placeholder="请输入地址" />
                    </FormItem>
                </Form>
                <div class="search-btn">
                    <allowBtn allow="查询" icon="ios-search" @click="search">查询</allowBtn>
                    <allowBtn allow="重置" class="allowreset" icon="ios-refresh-empty" @click="reset">重置</allowBtn>
                    <allowBtn allow="新增" type="success" icon="plus" @click="$router.push('/waterFactory/create')">新增
                    </allowBtn>
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
                s: {
                    name: '',
                    address: ''
                },
                spinShow: true, //未进入页面时，加载过程中显示加载中
                "gridOption": {
                    "url": "/service_basedata/factory/list",
                    "data": [],
                    "columns": [  //列配置
                        {
                            "title": "名称",
                            "key": "name",
                            "align": "center"
                        },
                        {
                            "title": "地址",
                            "key": "address",
                            "align": "center"
                        },
                        {
                            "title": "联系电话",
                            "key": "tel",
                            "align": "center",
                        },
                        {
                            "title": "描述",
                            "key": "description",
                            "align": "center"
                        },
                        {
                            key: 'tool', title: '操作', align: "center", width: 300, render(h, params) {
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
                                            allow: "删除"
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
                this.s.name = '';
                this.s.address = '';
                this.$refs.grid.load();
            },
            search: function () {
                this.$refs.grid.load({
                    "name": this.s.name,
                    "address": this.s.address,
                });
            },
            //以下三个的行方法
            toUpdate: function (obj) {
                this.$router.push('/waterFactory/update/' + obj.id + "/update");
            },
            toView: function (obj) {
                this.$router.push('/waterFactory/view/' + obj.id + "/view");
            },
            del: function (obj) {
                let that = this;
                this.$Modal.confirm({
                    title: '删除',
                    content: '确认删除后水厂支线信息会一并被删除？',
                    onOk: function () {
                        // 跳转
                        this.$ajax.post("/service_basedata/factory/delete", {'id': obj.id})
                            .then((res) => {
                                this.$Message.success('删除成功');
                                that.$refs['grid'].reLoad();
                            })
                            .catch((error) => {
                                if (!error.url) {
                                    console.info(error);
                                }
                            })
                    }
                })
            },
        },
        created: function () {

        }
    };

</script>
