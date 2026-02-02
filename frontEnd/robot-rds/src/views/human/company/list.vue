<template>
    <div>
        <page-title></page-title>
        <div class="page-search">
            <div class="page-search-title">查询条件</div>
            <div class="page-search-content">
                <Form ref="s" v-model="s" :label-width="80" @submit.native.prevent="submit">
                    <FormItem label="企业名称">
                        <Input v-model.trim="s.name" maxlength="50" placeholder="请输入企业名称"></Input>
                    </FormItem>
                    <FormItem label="企业简称">
                        <Input v-model.trim="s.shortName" maxlength="50" placeholder="请输入企业简称"></Input>
                    </FormItem>
                    <!-- <FormItem label="编码">
                        <Input v-model.trim="s.code" maxlength="50" placeholder="请输入企业编码"></Input>
                    </FormItem> -->
                    <FormItem label="联系电话">
                        <Input v-model.trim="s.phone" maxlength="50" placeholder="请输入联系电话"></Input>
                    </FormItem>
                </Form>
                <div class="search-btn">
                    <allowBtn allow="allowsearch" icon="ios-search" @click="search">查询</allowBtn>
                    <allowBtn allow="allowreset" icon="ios-refresh-empty" @click="reset">重置</allowBtn> 
                </div>
            </div>
        </div>
        
        <div class="page-tools">
            <allowBtn allow="create" type="success" faicon="fa fa-plus" @click="$router.push('/company/create')">新增</allowBtn>
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
                    shortName: '',
                    code: '',
                    phone: '',
                },
                spinShow: true, //未进入页面时，加载过程中显示加载中
                "gridOption": {
                    "url": "/service_user/company/list",
                    "data": [],
                    "columns": [  //列配置
                        {
                            "title": "企业名称",
                            "key": "name",
                            "align": "center"
                        },
                        {
                            "title": "企业简称",
                            "key": "shortName",
                            "align": "center"
                        },
                        {
                            "title": "地址信息",
                            "key": "address",
                            "align": "center",
                        },
                        /* {
                            "title": "企业编码",
                            "key": "code",
                            "align": "center"
                        }, */
                        {
                            "title": "联系电话",
                            "key": "phone",
                            "align": "center",
                        },
                        {
                            key: 'tool', 
                            title: '操作', 
                            align: "center",
                             width: 220, render(h, params) {
                                return h('div', [
                                    h('allowBtn', {
                                        props: {
                                            type: 'ghost',
                                            size: 'small',
                                            allow: "view",
                                            faicon:"fa-file-text-o"
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
                                            type: 'ghost',
                                            size: 'small',
                                            allow: "update",
                                            faicon:"fa-edit"
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
                                            type: 'ghost',
                                            size: 'small',
                                            allow: "delete",
                                            faicon:"fa-close"
                                        },
                                        on: {
                                            click: () => {
                                                that.toDelete(params.row);
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
                this.s.shortName = '';
                this.s.code = '';
                this.s.phone = '';
                this.$refs.grid.load();
            },
            search: function () {
                this.$refs.grid.load({
                    "name": this.s.name,
                    "shortName": this.s.shortName,
                    "code": this.s.code,
                    "phone": this.s.phone,
                });
            },
            //以下三个的行方法
            toView: function (obj) {
                this.$router.push('/company/view/' + obj.id + "/view");
            },
            //跳转到修改页面
            toUpdate: function (obj) {
                this.$router.push('/company/update/' + obj.id + "/update");
            },
            toDelete: function (obj) {
                //获取服务器数据
                let that = this;
                this.$Modal.confirm({
                    title: '删除',
                    content: '您确认删除此条数据？',
                    onOk: function () {
                        // 跳转
                        this.$ajax.post("/service_user/company/delete", {'id': obj.id})
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
