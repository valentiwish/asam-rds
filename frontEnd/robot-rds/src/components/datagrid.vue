<template>
    <div>
        <Table ref="table" 
            :data="list" 
            highlight-row
            :columns="columnsOption" 
            :row-class-name ="rowClassName"
            :no-data-text="option.emptyInfo || '查询结果为空'" 
            :border="border" 
            :show-header="showHeader" 
            :show-summary="option.showSummary"
            :sum-text="option.sumText"
            :summary-method="option.summaryMethod"
            :span-method="option.spanMethod"
            :stripe="true"
            :height="height"
            :loading="isLoading"
            @on-select="onselectHandle"
            @on-select-all="onselectALLHandle"
            @on-selection-change="onselectionChangeHandle" 
            @on-select-cancel="onselectCancelHandle"
            @on-row-dblclick='handleRowdblClick'
            @on-row-click='handleRowClick'
            @on-current-change="oncurrentchange"
        ></Table>
        
        <div class="module-datagrid-page" :class="{'hastools':($slots.tools && $slots.tools.length>0)}" v-if="option.pagination!=false">
            <div class="module-datagrid-tools" v-if="$slots.tools" >
            <slot name='tools'></slot>
            </div>
            <Page :total="pagination.countRecord" :current="pagination.currentPage" @on-change="changePage" :page-size="pagination.pageSize" size="small" :page-size-opts="pageSizeOpts" show-total show-elevator show-sizer @on-page-size-change="pageSizeChange">
                共{{pagination.countRecord}}条数据 每页{{pagination.pageSize}}条数据 第{{pagination.currentPage}}/{{totalPage===0 ? 1 : totalPage}}页
            </Page>
            <!-- <div class="clearfix"></div> -->
        </div>
    </div>
</template>

<script>
    import Bus from '@/components/bus.js';
    export default {
        name: 'dataGrid',
        props: ['option'],
        data: function() {
            return {
                isLoading: false,
                pageSizeOpts:[10,15,20,30,40],
                pagination: {
                    countRecord: 0,
                    currentPage: 1,
                    pageSize: 15
                },
                asynData: [],
                lastRequestParms: {},
                tableHeight:450,
            }
        },
        computed: {
            "height": function() {
                if(void 0 != this.option.height){
                    return this.option.height;
                }
                else{
                    return this.tableHeight;
                }
            },
            "contentType"(){
                if(this.option.contentType && this.option.contentType.match("json")){
                    return {
                        "Content-Type":"application/json;"
                    }
                }
                else{
                    return {};
                }
            },
            "border":function(){
                if(void 0 == this.option.border){
                    return true;
                }
                return this.option.border;
            },
            "columnsOption": function() {
                var columns = [];
                if (typeof this.option.columns == "function") {
                    columns =  this.option.columns();
                } else {
                    columns = this.option.columns;
                }
                columns.forEach((j,k)=>{
                    if("string" == typeof j.width){
                        j.width = Number(j.width.replace("px",""));
                    }
                    if(j.ellipsis){
                        j.tooltip = true;
                    }
                    if(void 0 == j.resizable){
                        j.resizable = true;
                    }
                    if(void 0 != j.width){
                        if(j.width > 170){
                            j.minWidth = 170;
                        }
                    }
                    else if(void 0 == j.minWidth){
                        j.minWidth = 170;
                    }
                    if(j.key == "tool" || j.title == "操作"){
                        j.fixed= "right";
                    }
                    if(j.show == void 0){
                        j.show = true;
                    }
                });
                return columns.filter((j,i)=>{
                    return j.show;
                });
            },
            "showHeader": function() {
                if(void 0 !=this.option.showHeader){
                    return true;
                }
                return this.option.showHeader;
            },
            "queryPage":function(){
                return this.option.queryPage !== false;
            },
            "currentPageStart": function() {
                return (this.pagination.currentPage - 1) * this.pagination.pageSize + 1;
            },
            "currentPageEnd": function() {
                return this.pagination.currentPage * this.pagination.pageSize <= this.pagination.countRecord ? this.pagination.currentPage * this.pagination.pageSize : this.pagination.countRecord;
            },
            "totalPage": function() {
                return Math.ceil(this.pagination.countRecord / this.pagination.pageSize);
            },
            "list": function() {
                if (this.option.url) {
                    return this.asynData;
                } else if (this.option.data) {
                    this.pagination.countRecord = this.option.data.length;
                    var list = this.option.data.filter((data, index) => {
                        return index <= this.currentPageEnd - 1 && index >= this.currentPageStart - 1;
                    });
                    return list;
                }
            }
        },
        "methods": {
            "rowClassName":function(row,index){
                if(typeof this.option['row-class-name'] =="function"){
                    return  this.option['row-class-name'](row,index);
                }
                else if(typeof this.option['row-class-name'] =="string"){
                    return this.option['row-class-name'];
                }
                else{
                    return "";
                }
            },
            "getAsynData": function(url, data,headers) {
                return this.$ajax.post(url, data,headers|| {})
            },
            //初始化加载,保留分页参数。arg为请求参数
            "reLoad": function(arg) {
                this.loadData(Object.assign(this.lastRequestParms, arg));
            },
            //重新加载，arg为请求参数
            "load": function(arg) {
                this.pagination.currentPage = 1;
                this.loadData(arg);
            },
            "loadData": function(asyndata) {
                if (this.option.url) {
                    var obj = {
                        "pageSize": this.pagination.pageSize,
                        "currentPage": this.pagination.currentPage,
                    }
                    if (asyndata) {
                        this.lastRequestParms = asyndata || {};
                        
                    }
                    obj = Object.assign(this.option.defaultPayload || {}, this.lastRequestParms, obj);
                    this.isLoading = true;
                    var headers = Object.assign({},this.option.headers || {},this.contentType)
                    this.getAsynData(this.option.url, obj,headers)
                        .then((res) => {
                            let data = this.option.loadFilter(res.data);
                            this.pagination.countRecord = data.countRecord;
                            if(!data.list){
                                this.asynData = [];
                            }
                            else{
                                this.asynData = data.list;
                            }
                            
                            this.isLoading = false;
                        })
                        .catch(() => {
                            this.asynData = [];
                            this.isLoading = false;
                        })
                } else if (this.option.data) {
                    //本地数据
                    if (this.pagination) {
                        this.pagination.countRecord = this.option.data.length;
                    };
                }
            },
            pageSizeChange:function(size){
                this.pagination.pageSize = size;
                this.pagination.currentPage = 1;
                this.reLoad({});
            },
            changePage: function(currentPage) {
                this.$emit("on-page-change",currentPage);
                this.pagination.currentPage = currentPage;
                if(this.queryPage){
                    this.$router.push({
                        fullPath:this.$route.fullPath,
                        query:{
                            page:this.pagination.currentPage
                        } 
                    }); 
                }
                else{
                    this.reLoad({});
                }  
            },
            onselectHandle:function(selection,row){
                this.$emit("on-select",{selection,row})
            },
            onselectALLHandle:function(selection){
                this.$emit("on-select-all",selection)
            },
            onselectionChangeHandle:function(selection){
                this.$emit("on-selection-change",selection)
            },
            onselectCancelHandle:function(selection, row){
                this.$emit("on-select-cancel",selection, row)
            },
            handleRowdblClick:function(selection,row) {
                this.$emit("on-row-dblclick",selection,row)
            },
            handleRowClick:function(selection,row) {
                this.$emit("on-row-click",selection,row)
            },
            oncurrentchange(currentRow,oldCurrentRow){
                this.$emit("on-current-change", currentRow,oldCurrentRow)
            },
            selectAll:function(status){
                this.$refs.table.selectAll(status);
            },
            getSelection:function(){
                return this.$refs.table.getSelection();
            },
            getTableInstance:function(){
                return this.$refs.table;
            },
            setTableAutoHeight(){
                if(this.$refs.table){
                    var tableoffsettop = this.$refs.table.$el.offsetTop;
                    this.tableHeight = window.innerHeight - tableoffsettop - (this.option.tableHeightOffset == void 0 ? 145 : this.option.tableHeightOffset) + (this.$root.showLayout ? 0 : $(".pager-header").outerHeight());
                }
            }
        },
        created: function() {
            if(this.queryPage){
                try{
                    if(this.$route.query.page){
                        this.pagination.currentPage = Number(this.$route.query.page);
                    }
                }
                catch(e){};
            }

            if(this.option.pagination){
                this.pagination = Object.assign({},this.pagination,this.option.pagination)
            }
        },
        mounted:function(){
            if(void 0 == this.option.auto){
                this.option.auto = true;
            }
            //项目中使用默认分页配置
            if (this.option.auto !== false) {
                this.loadData();
            }
            this.setTableAutoHeight();
            $(window).on("resize",this.setTableAutoHeight);
            Bus.$on("menuLoaded",this.setTableAutoHeight);
        },
        watch:{
            "$route.query.page":function(n,o){
                if(this.queryPage){
                    try{
                        if(n){
                            this.pagination.currentPage = Number(n);
                            this.reLoad({});
                        }
                    }
                    catch(e){};
                }
            }
        },
        beforeDestroy(){
            $(window).off("resize",this.setTableAutoHeight);
            Bus.$off("menuLoaded",this.setTableAutoHeight);
        }
    }
</script>

<style scoped>
    .module-datagrid-page{
        text-align: right;
        background: #fff;
        color:#8a8e98;
        padding:10px 20px;
        border-bottom: 1px solid #e9eaec;
        border-left: 1px solid #e9eaec;
        border-right: 1px solid #e9eaec;
        border-top: 1px solid #e9eaec;
    }
</style>

<style>
    .module-datagrid-page .ivu-page-total {
        font-size: 14px;
        vertical-align: middle;
        font-weight:bold;
        float:left;
    }
    .module-datagrid-page.hastools .ivu-page-total{
        float:none;
    }
    .module-datagrid-page.hastools .module-datagrid-tools{
         float:left;
    }
</style>
