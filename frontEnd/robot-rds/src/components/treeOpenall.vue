<template>
    <div class="treeopenall">
        <Tree :data="list" show-checkbox ref="treeOpenall" empty-text=""></Tree>
    </div>
</template>

<script>
export default {
    name: 'treeOpenall',
    props: ['list'],
    data: function() {
        return {

        }
    },
    methods: {
        getCheckedNodes: function() {
            return this.$refs.treeOpenall.getCheckedNodes();
        },
        getSelectedNodes: function() {
            return this.$refs.treeOpenall.getSelectedNodes();
        },
        getNodes: function() {
            var getChildrens = function(list) {
                var ret = [];
                for (let obj of list) {
                    ret.push(obj);
                    if (obj.children && obj.children.length > 0) {
                        for (let k of getChildrens(obj.children)) {
                            ret.push(k);
                        }
                    }
                };
                return ret;
            };
            return getChildrens(this.list);
        },
        getCheckedNodesAll: function() {
            return this.getNodes().filter(function(j) {
                return j.checked || j.checked == false && j.indeterminate == true;
            });
        }
    }
}
</script>

<style>
    .treeopenall .treeopenall {
        position: relative;
        border-left: 1px solid #efefef;
        margin-bottom: 8px;
        margin-top: 8px;
    }

    .treeopenall .treeopenall:before {
        content: "";
        position: absolute;
        top: 50%;
        left: -6px;
        border-top: 5px solid transparent;
        border-right: 6px solid #efefef;
        border-bottom: 5px solid transparent;
        margin-top: -3px;
    }

    .treeopenall td {
        padding: 2px 50px 2px 20px;
    }

    .treeopenall .ivu-tree-arrow {
        /* display: none; */
    }

    .treeopenall:after {
        content: "";
        display: block;
        clear: both;
    }

    .treeopenall .ivu-tree li ul {
        margin-left: 45px;
    }

    .treeopenall .ivu-tree-children {
        position: relative;
    }

    .treeopenall .ivu-tree-children:before {
        content: "";
        position: absolute;
        top: 18px;
        left: 24px;
        width: 1px;
        background: #DDDEE1;
        bottom: 9px;
    }

    .treeopenall .ivu-tree-children:last-child:after {
        content: "";
        position: absolute;
        top: 13px;
        left: -39px;
        width: 2px;
        background: #fff;
        bottom: 11px;
    }

    .treeopenall .ivu-tree>.ivu-tree-children:last-child:after {
        display: none;
    }

    .treeopenall .ivu-tree ul {
        font-size: 15px;
    }

    .treeopenall .ivu-tree>.ivu-tree-children:before {
        left: 7px;
    }

    .treeopenall .ivu-tree>.ivu-tree-children:after {
        left: -38px;
    }

    .treeopenall .ivu-tree-children>li:before {
        content: "";
        position: absolute;
        top: 12px;
        left: -38px;
        width: 56px;
        height: 1px;
        background: #DDDEE1;
        bottom: 0;
    }

    .treeopenall .ivu-tree>.ivu-tree-children>li:before {
        display: none
    }

    .treeopenall .ivu-tree .ivu-checkbox+span {
        display: none;
    }

    .treeopenall .ivu-tree-title {
        width: 90px;
    }

    .treeopenall .branchnode {
        display: inline-block;
        vertical-align: top;
        margin-top: 30px;
    }

    .treeopenall .ivu-tree li ul.branchnode {
        margin-left: 0;
    }

    .treeopenall .ivu-tree li ul.branchnode .ivu-tree-title {
        width: 60px;
    }

    .treeopenall .ivu-tree li ul.branchnode:first-of-type {
        margin-left: -70px;
    }

    .treeopenall .ivu-tree-children.branchnode>li:before {
        display: none;
    }

    .treeopenall .ivu-tree-children.branchnode:first-of-type>li:before {
        display: block;
        top: 19px;
        left: -48px;
        width: 67px;
    }

    .treeopenall .ivu-tree-children.branchnode:first-of-type>li:after {
        position: absolute;
        content: "";
        top: 20px;
        left: -48px;
        width: 3px;
        background: #ffffff;
        height: 10px;
    }

    .treeopenall .branchnode.ivu-tree-children:before,
    .treeopenall .ivu-tree-children.branchnode:last-child:after {
        display: none;
    }


    .ivu-tree ul li.ivu-select-item {
        padding: 7px 16px;
        margin: 0;
    }

    .treeopenall .ivu-tree ul.ivu-select-dropdown-list {
        margin: 0;
        padding: 0;
    }

    .treeopenall .ivu-tree .ivu-select.ivu-select-multiple .ivu-tag {
        background: #19be6b;
        color: #fff;
    }

    .treeopenall .ivu-tree .ivu-select.ivu-select-multiple .ivu-tag .ivu-icon-ios-close-empty {
        color: #fff;
    }
</style>