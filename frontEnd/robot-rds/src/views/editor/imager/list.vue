<template>
    <div>
        <page-title>热成像仪</page-title>
        <div class="page-search">
            <div class="page-search-title">查询条件</div>
            <div class="page-search-content">
                <Form :model="robotImager" :label-width="100">
                    <FormItem label="机器人名称">
                        <Input v-model.trim="robotImager.robotName" placeholder="请输入机器人名称，不超过50字" maxlength="50"></Input>
                    </FormItem>
                    <FormItem label="热成像仪IP">
                        <Input v-model.trim="robotImager.currentIp" placeholder="请输入热成像仪IP，不超过50字" maxlength="50"></Input>
                    </FormItem>
                    <FormItem label="热成像仪名称">
                        <Input v-model.trim="robotImager.imagerName" placeholder="请输入热成像仪名称，不超过50字" maxlength="50"></Input>
                    </FormItem>
                </Form>
                <div class="search-btn">
                    <allowBtn allow="allowsearch" size="primay" @click="search()">查询</allowBtn>
                    <allowBtn allow="allowreset" size="error" @click=reset()>重置</allowBtn>
                    <!-- <allowBtn allow="新增" size="large" type="success" icon="plus" @click="addModal()">新增</allowBtn> -->
                </div>
            </div>
            <div class="page-content">
                <data-grid :option="gridOption" ref="grid">
                </data-grid>
            </div>
            <!-- 新增-->
            <!-- <Modal v-model="modal_add" title="新增热成像仪" :closable="true">
                <update-modal ref="addform" @changeVal="changeVal"></update-modal>
                <div slot="footer">
                    <Button type="primary" class="ivu-btn ivu-btn-primary" @click="save" :loading="loading">保存</Button>
                    <Button type="ghost" @click="modal_add = false">关闭</Button>
                </div>
            </Modal> -->
            <!-- 更新-->
            <Modal v-model="modal_update" title="修改热成像仪" :closable="true">
                <update-modal :id="id" ref="updateform" @changeVal="changeVal"></update-modal>
                <div slot="footer">
                    <Button type="primary" class="ivu-btn ivu-btn-primary" @click="update" :loading="loading">保存</Button>
                    <Button type="ghost" @click="modal_update = false">关闭</Button>
                </div>
            </Modal>
            <!-- 查看-->
            <Modal v-model="modal_view" title="查看热成像仪" :closable="true">
                <update-modal :id="viewid" type="view"></update-modal>
                <div slot="footer">
                    <Button type="ghost" @click="modal_view=false">关闭</Button>
                </div>
            </Modal>
        </div>
    </div>
</template>
<script>
import dataGrid from "@/components/datagrid"
import fontIcon from "@/components/fontIcon"
import updateModal from "./cur"

export default {
    components: { dataGrid, fontIcon, updateModal },
    data: function () {
        var that = this;
        return {
            loading: false,
            id: '', //页面修改赋值
            viewid: '', //页面查看赋值
            robotImager: {
                robotName: '',
                currentIp: '',
                imagerName: '',
            },
            modal_update: false, //修改弹窗
            modal_view: false, //查看弹窗
            // modal_add: false, //新增弹窗
            //以上是界面v-model的所有参数
            "gridOption": {
                "url": "/service_rms/robotImager/list",
                "columns": [ //列配置
                    {
                        "title": "机器人名称",
                        "key": "robotName",
                        "align": "center"
                    },
                    {
                        "title": "热成像仪IP",
                        "key": "currentIp",
                        "align": "center"
                    },
                    {
                        "title": "热成像仪名称",
                        "key": "imagerName",
                        "align": "center"
                    },
                    {
                        title: '操作',
                        key: 'tools',
                        align: "center",
                        width: "400px",
                        render(h, params) {
                            // console.log("h",h)
                            // console.log("params",params)
                            return h('div', [
                                h('allowBtn', {
                                    props: {
                                        type: 'primary',
                                        size: 'small',
                                        allow: '查看'
                                    },
                                    style: {
                                        marginRight: '5px'
                                    },
                                    on: {
                                        click: () => {
                                            that.view(params.row)
                                        }
                                    }
                                }, '查看'),
                                h('allowBtn', {
                                    props: {
                                        type: 'info',
                                        size: 'small',
                                        allow: "修改",
                                        disabled: false
                                    },
                                    style: {
                                        marginRight: '5px'
                                    },
                                    on: {
                                        click: () => {
                                            that.toUpdate(params.row)
                                        }
                                    }
                                }, '修改'),
                                h('allowBtn', {
                                    props: {
                                        type: 'error',
                                        size: 'small',
                                        allow: '删除',
                                        disabled: false
                                    },
                                    style: {
                                        marginRight: '5px'
                                    },
                                    on: {
                                        click: () => {
                                            that.del(params.row)
                                        }
                                    }
                                }, '删除')
                            ])
                        }
                    }
                ],
                "loadFilter": function (data) {
                    console.log(data.data)
                    return data.data;
                },
            }
        }
    },
    methods: {
        // addModal: function() {
        //     this.$refs.addform.resetFields();//调用的是addform页面上也就是cur上的resetFields
        //     this.modal_add=true;
        // },
        view: function(obj) {
            this.viewid = obj.id;
            this.modal_view = true;
        },
        toUpdate: function(obj) {
            this.id = obj.id;
            this.modal_update = true;
        },
        del: function (obj) {
            //获取服务器数据
            let that = this;

            this.$Modal.confirm({
                title: '删除',
                content: '您确认删除此条数据？',
                onOk: function() {
                    var idValue = obj.id;
                    var url = "/service_rms/robotImager/delete";
                    this.$ajax.post(url, { 'id': idValue })
                        .then((res) => {
                            if(res.data.code == "200"){
                                that.$Message.success('删除成功。');
                                that.$refs.grid.reLoad({});
                            }else{
                                that.$Message.error('删除出错。');
                            }
                        })
                        .catch((error) => {
                            if(!error.url){console.info(error);}
                        })
                }
            })
        },
        search: function () {
            this.$refs.grid.load({'robotName':this.robotImager.robotName, 'currentIp':this.robotImager.currentIp,'imagerName':this.robotImager.imagerName});
        },
        reset: function () {
            this.robotImager.robotName = "";
            this.robotImager.currentIp = "";
            this.robotImager.imagerName = "";
            this.$refs.grid.load({ });
        },
        // save: function (cb) {
        //     this.loading = true;
        //     this.$refs['addform'].save((flag) => {
        //         console.log("flag",flag)
        //         if (flag == true) {
        //             this.modal_add = false;
        //             this.$refs.grid.reLoad({});
        //         }else {
        //             this.$Message.error('保存出错');
        //         }
        //         this.loading=false;
        //     });
        // },
        update: function (cb) {
            this.loading = true;
            this.$refs['updateform'].save((flag) => {
                if (flag == true) {
                    this.modal_update = false;
                    this.loading = false;
                    this.$refs.grid.reLoad({});
                    this.viewid = "";
                    this.id = "";
                }
            });
        },
        //加载旋转方法
        changeVal: function () {
            this.loading = false;
        },
    },
    mounted: function () {
        this.search();
    }
}
</script>