<template>
    <div>
        <page-title>物联网设备</page-title>
        <div class="page-search">
            <div class="page-search-title">查询条件</div>
            <div class="page-search-content">
                <Form :model="iotEquipment" :label-width="100" ref="searchForm">
                    <FormItem label="设备编号" prop="equipmentCode">
                        <Input v-model.trim="iotEquipment.equipmentCode" placeholder="请输入设备编号，不超过50字"
                            maxlength="50"></Input>
                    </FormItem>
                    <FormItem label="设备名称" prop="equipmentName">
                        <Input v-model.trim="iotEquipment.equipmentName" placeholder="请输入设备名称，不超过50字"
                            maxlength="50"></Input>
                    </FormItem>
                    <FormItem label="绑定站点" prop="adjacentSite">
                        <Input v-model.trim="iotEquipment.adjacentSite" placeholder="请输入绑定站点名称，不超过50字"
                            maxlength="50"></Input>
                    </FormItem>
                    <FormItem label="设备IP" prop="equipmentIp">
                        <Input v-model.trim="iotEquipment.equipmentIp" placeholder="请输入设备IP，不超过50字"
                            maxlength="50"></Input>
                    </FormItem>
                </Form>
                <div class="search-btn">
                    <allowBtn allow="allowsearch" size="primay" @click="search()">查询</allowBtn>
                    <allowBtn allow="allowreset" size="error" @click=reset()>重置</allowBtn>
                    <allowBtn allow="新增" size="large" type="success" icon="plus" @click="addModal()">新增</allowBtn>
                </div>
            </div>
            <div class="page-content">
                <data-grid :option="gridOption" ref="grid">
                </data-grid>
            </div>
            <!-- 新增-->
            <Modal v-model="modal_add" title="新增IoT设备" :closable="true" :styles="{ width: '50%' }">
                <update-modal :equipmentType="equipmentType" ref="addform" @changeVal="changeVal"></update-modal>
                <div slot="footer">
                    <Button type="primary" class="ivu-btn ivu-btn-primary" @click="save" :loading="loading">保存</Button>
                    <Button type="ghost" @click="modal_add = false">关闭</Button>
                </div>
            </Modal>
            <!-- 更新-->
            <Modal v-model="modal_update" title="修改IoT设备" :closable="true" :styles="{ width: '50%' }">
                <update-modal :id="id" ref="updateform" @changeVal="changeVal"></update-modal>
                <div slot="footer">
                    <Button type="primary" class="ivu-btn ivu-btn-primary" @click="update"
                        :loading="loading">保存</Button>
                    <Button type="ghost" @click="modal_update = false">关闭</Button>
                </div>
            </Modal>
            <!-- 查看-->
            <Modal v-model="modal_view" title="查看IoT设备" :closable="true" :styles="{ width: '50%' }">
                <update-modal :id="viewid" type="view"></update-modal>
                <div slot="footer">
                    <Button type="ghost" @click="modal_view = false">关闭</Button>
                </div>
            </Modal>
        </div>
    </div>
</template>
<script>
import dataGrid from "@/components/datagrid"
import fontIcon from "@/components/fontIcon"
import updateModal from "./cur"
import { e } from "mathjs";

export default {
    components: { dataGrid, fontIcon, updateModal },
    data: function () {
        var that = this;
        return {
            loading: false,
            id: '', //页面修改赋值
            viewid: '', //页面查看赋值
            equipmentType: this.getEquipmentType(),
            iotEquipment: {
                equipmentCode: '',
                equipmentName: '',
                communicationType: '',
                adjacentSite: '',
                equipmentIp: '',
                equipmentRegion: '',
            },
            modal_update: false, //修改弹窗
            modal_view: false, //查看弹窗
            modal_add: false, //新增弹窗
            //以上是界面v-model的所有参数
            "gridOption": {
                "auto": false,//是否自动刷新
                "url": "/service_rms/iotEquipment/list",
                "columns": [ //列配置
                    {
                        "title": "设备编号",
                        "key": "equipmentCode",
                        "align": "center"
                    },
                    {
                        "title": "设备名称",
                        "key": "equipmentName",
                        "align": "center"
                    },
                    {
                        "title": "通信类型",
                        "key": "communicationType",
                        "align": "center"
                    },
                    {
                        "title": "设备IP",
                        "key": "equipmentIp",
                        "align": "center"
                    },
                    // {
                    //     "title": "Slave ID",
                    //     "key": "slaveId",
                    //     "align": "center"
                    // },
                    // {
                    //     "title": "设备状态地址",
                    //     "key": "equipStateOffset",
                    //     "align": "center"
                    // },
                    // {
                    //     "title": "写入地址",
                    //     "key": "writeOffset",
                    //     "align": "center"
                    // },
                    {
                        "title": "绑定站点",
                        "key": "adjacentSite",
                        "align": "center",
                        "ellipsis": true
                    },
                    // {
                    //     "title": "所在区域",
                    //     "key": "equipmentRegion",
                    //     "align": "center"
                    // },
                    {
                        title: "在线状态",
                        key: "equipmentStatus",
                        align: "center",
                        width: 120,
                        render: function (h, p) {
                            var obj = {
                                0: {
                                    name: "离线",
                                    color: "magenta"
                                },
                                1: {
                                    name: "在线",
                                    color: "success"
                                }
                            }
                            const linkState = p.row.equipmentStatus;
                            if (obj[linkState]) {
                                return h("Tag", {
                                    props: {
                                        color: obj[linkState].color,
                                    }
                                }, obj[linkState].name)
                            }
                            return "";
                        }
                    },
                    {
                        title: '操作',
                        key: 'tools',
                        align: "center",
                        width: "270px",
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
                    return data.data;
                },
            }
        }
    },
    methods: {
        addModal: function () {
            //方式1：
            this.$refs['addform'].resetFields();
            //方式2：
            // this.$refs['addform'].$refs['modbusEquipment'].resetFields();
            // this.$refs['addform'].modbusEquipment.equipmentType = this.getEquipmentType();
            this.modal_add = true;
        },
        view: function (obj) {
            this.viewid = obj.id;
            this.modal_view = true;
        },
        toUpdate: function (obj) {
            this.id = obj.id;
            this.modal_update = true;
        },

        del: function (obj) {
            //获取服务器数据
            let that = this;

            this.$Modal.confirm({
                title: '删除',
                content: '您确认删除此条数据？',
                onOk: function () {
                    var idValue = obj.id;
                    var url = "/service_rms/iotEquipment/delete";
                    this.$ajax.post(url, { 'id': idValue })
                        .then((res) => {
                            if (res.data.code == "200") {
                                that.$Message.success('删除成功。');
                                that.$refs.grid.reLoad({});
                            } else {
                                that.$Message.error('删除出错。');
                            }
                        })
                        .catch((error) => {
                            if (!error.url) { console.info(error); }
                        })
                }
            })
        },
        search: function () {
            this.$refs.grid.load({ data: JSON.stringify(this.iotEquipment), equipmentType: this.getEquipmentType() });
        },
        reset: function () {
            this.$refs.searchForm.resetFields();
            this.$refs.grid.load({ equipmentType: this.getEquipmentType() });
        },
        save: function (cb) {
            this.loading = true;
            this.$refs['addform'].save((flag) => {
                console.log("flag", flag)
                if (flag == true) {
                    this.modal_add = false;
                    this.$refs.grid.reLoad({});
                } else {
                    this.$Message.error('保存出错');
                }
                this.loading = false;
            });
        },
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

        toConnect: function (obj) {
            this.iotEquipment = obj;
            let robotIp = obj.robotIp;
            let url = "/service_rms/iotEquipment/toConnect";
            this.$ajax.post(url, { ip: robotIp })
                .then((res) => {
                    if (res.data.code != 200) {
                        this.$Message.error(res.data.msg);
                        this.iotEquipment.robotConnectState = 0;
                    } else {
                        this.$Message.success("连接成功");
                        this.iotEquipment.robotConnectState = 1;
                    }
                    //存入最新状态
                    let urlSave = "/service_rms/iotEquipment/save";
                    this.$ajax.post(urlSave, { "data": JSON.stringify(this.iotEquipment) })
                        .then((res) => {
                            if (res.data.code == "200") {
                                that.$Message.success('状态已更新');
                            } else {
                                that.$Message.error('状态更新失败');
                            }
                        })
                        .catch((error) => {
                            if (!error.url) { console.info(error); }
                        })
                    // this.$refs.grid.reLoad({});
                })
                .catch((error) => {
                    if (!error.url) {
                        console.info(error);
                    }
                });
        },
        checkConnect: function () {
            let url = "/service_rms/iotEquipment/checkConnect";

            this.$ajax.post(url, { equipmentType: this.getEquipmentType() })
                .then((res) => {
                    if (res.data) {
                        this.$refs.grid.load({ equipmentType: this.getEquipmentType() });
                        this.$Message.success("状态已更新");

                    }
                })
                .catch((error) => {
                    if (!error.url) {
                        console.info(error);
                    }
                });
        },
        getEquipmentType: function () {
            let varEquipmentType = this.$route.name;
            this.equipmentType = varEquipmentType.split("/").pop();
            console.log("equipmentType", this.equipmentType);
            return this.equipmentType;

        },

    },
    watch: {
        "$route.name": function (newName, oldName) {
            if (newName) {
                this.$refs.grid.load({ equipmentType: this.getEquipmentType() });
                this.checkConnect();

            }
        }
    },
    mounted: function () {
        this.$refs.grid.load({ equipmentType: this.getEquipmentType() });
        this.checkConnect();

    },
}
</script>