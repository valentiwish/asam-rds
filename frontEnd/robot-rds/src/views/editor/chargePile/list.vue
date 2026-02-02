<template>
    <div>
        <page-title></page-title>
        <div class="page-search">
            <div class="page-search-title">查询条件</div>
            <div class="page-search-content">
                <Form ref="chargePile" :model="chargePile" :label-width="100">
                    <FormItem label="充电桩IP" prop="chargePileIp">
                        <Input v-model.trim="chargePile.chargePileIp" placeholder="请输入智能充电桩IP，不超过50字"
                            maxlength="50"></Input>
                    </FormItem>
                    <FormItem label="充电桩名称" prop="chargePileName">
                        <Input v-model.trim="chargePile.chargePileName" placeholder="请输入智能充电桩名称，不超过50字"
                            maxlength="50"></Input>
                    </FormItem>
                    <FormItem label="充电桩点位" prop="chargePilePoint">
                        <Input v-model.trim="chargePile.chargePilePoint" placeholder="请输入智能充电桩点位，不超过30字"
                            maxlength="30"></Input>
                    </FormItem>
                </Form>
                <div class="search-btn">
                    <allowBtn allow="allowsearch" size="large" @click="search()">查询</allowBtn>
                    <allowBtn allow="allowreset" size="large" @click="reset">重置</allowBtn>
                    <allowBtn allow="新增" type="success" icon="plus" size="large" @click="modal_add = true">新增</allowBtn>
                </div>
            </div>
            <!-- 新增 -->
            <Modal v-model="modal_add" title="新增充电桩">
                <update-modal ref="addform" type="add"></update-modal>
                <div slot="footer">
                    <Button type="primary" class="ivu-btn ivu-btn-primary" @click="save">保存</Button>
                    <Button type="ghost" @click="modal_add = false">关闭</Button>
                </div>
            </Modal>
            <!-- 更新-->
            <Modal v-model="modal_update" title="修改充电桩">
                <update-modal :id="id" ref="updateform" @changeVal="changeVal"></update-modal>
                <div slot="footer">
                    <Button type="primary" class="ivu-btn ivu-btn-primary" @click="update"
                        :loading="loading">保存</Button>
                    <Button type="ghost" @click="modal_update = false">关闭</Button>
                </div>
            </Modal>
            <!-- 查看-->
            <Modal v-model="modal_view" title="查看充电桩">
                <update-modal :id="viewId" type="view"></update-modal>
                <div slot="footer">
                    <Button type="ghost" @click="modal_view = false">关闭</Button>
                </div>
            </Modal>
        </div>
        <div class="page-content">
            <data-grid :option="gridOption" ref="grid">
            </data-grid>
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
            viewId: '', //页面查看赋值
            chargePile: {
                id: '',
                chargePileName: '',
                chargePileIp: '',
                chargePilePoint: '',
            },
            modal_update: false, //修改弹窗
            modal_view: false, //查看弹窗
            modal_add: false, //新增弹窗
            //以上是界面v-model的所有参数
            gridOption: {
                url: "/service_rms/chargePile/list",
                auto: false,
                columns: [ //列配置
                    // { "title": "充电桩Id", "key": "id", "align": "center"},
                    { "title": "充电桩名称", "key": "chargePileName", "align": "center" },
                    { "title": "充电桩IP", "key": "chargePileIp", "align": "center" },
                    { "title": "充电桩点位", "key": "chargePilePoint", "align": "center" },
                    { "title": "通信协议", "key": "communicationType", "align": "center" },
                    {
                        "key": 'id', title: '操作', "align": "center", "width": "400px",
                        render(h, params) {
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
                                            that.toView(params.row)
                                        }
                                    }
                                }, '查看'),
                                h('allowBtn', {
                                    props: {
                                        type: 'info',
                                        size: 'small',
                                        allow: "修改"
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
                                        type: 'info',
                                        size: 'small',
                                        allow: '删除',
                                    },
                                    style: {
                                        marginRight: '5px'
                                    },
                                    on: {
                                        click: () => {
                                            that.toDelete(params.row)
                                        }
                                    }
                                }, "删除"),
                            ])
                        }
                    }
                ],
                "loadFilter": function (data) {
                    console.log(data.data)
                    return data.data;
                }
            },
        }
    },
    methods: {
        addModal: function () {
            this.$refs.addform.resetFields();
            this.modal_add = true;
        },
        toView: function (obj) {
            this.viewId = obj.id;
            this.modal_view = true;
        },
        toUpdate: function (obj) {
            this.id = obj.id;
            this.modal_update = true;
        },
        toDelete: function (obj) {
            let that = this;
            this.$Modal.confirm({
                title: '删除',
                content: '您确认删除此条数据？',
                onOk: function () {
                    var idValue = obj.id;
                    var url = "/service_rms/chargePile/delete";
                    this.$ajax.post(url, { 'id': idValue })
                        .then((res) => {
                            that.search();
                        })
                        .catch((error) => {
                            if (!error.url) { console.info(error); }
                        })
                }
            })
        },
        search: function () {
            console.log(JSON.stringify(this.dms))
            this.$refs.grid.load({ 'data': JSON.stringify(this.dms) });
        },
        reset: function () {
            this.dms.dmsName = "";
            this.dms.dmsIp = "";
            this.dms.dmsPoint = "";
            this.search();
        },
        save: function (cb) {
            this.$refs['addform'].save((flag) => {
                if (flag == true) {
                    this.modal_add = false;
                    this.loading = false;
                    this.$refs.grid.reLoad({});
                }
            });
        },
        update: function (cb) {
            this.loading = true;
            this.$refs['updateform'].save((flag) => {
                if (flag == true) {
                    this.modal_update = false;
                    this.loading = false;
                    this.$refs.grid.reLoad({});
                    this.viewId = "";
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
        this.$refs.grid.load();
    }
}
</script>