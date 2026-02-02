<template>
    <div>
        <Table ref="table" :data="list" :columns="columnsOption" :highlight-row='highlightRow' :row-class-name = "rowClassName" :no-data-text="option.emptyInfo || '查询结果为空'" :border="border" :show-header="showHeader" stripe :height="height" size="small" :loading="isLoading" @on-select="onselectHandle" @on-select-all="onselectALLHandle" @on-selection-change="onselectionChangeHandle" @on-select-cancel="onselectCancelHandle" @on-row-click="onRowClick" @on-current-change="onCurrentChange"></Table>
        
        <div class="module-datagrid-page" :class="{'hastools':($slots.tools && $slots.tools.length>0)}" v-if="option.pagination!=false">
            <div class="module-datagrid-tools" v-if="$slots.tools" >
               <slot name='tools'></slot>
            </div>
			
			<div class="module-datagrid-toolsexport">
			   <Button type="info" icon="ios-download-outline" size="small" @click="exportDataModal">导出数据</Button>
            </div>
            <Page :total="pagination.countRecord" :current="pagination.currentPage" @on-change="changePage" :page-size="pagination.pageSize" size="small" :page-size-opts="pageSizeOpts" show-total show-elevator  @on-page-size-change="pageSizeChange">
                共{{pagination.countRecord}}条数据 每页{{pagination.pageSize}}条数据 第{{pagination.currentPage}}/{{totalPage===0 ? 1 : totalPage}}页
            </Page>
            <!-- <div class="clearfix"></div> -->
        </div>
		
		<Modal
			v-model="exportmodal"
			title="导出数据">

			<Alert>请选择需要导出的数据列</Alert>
            <Transfer
                :data="exportDataTransfer"
                :titles="['未选数据列','已选数据列']"
                :render-format="exportDatarenderFormat"
                :target-keys="exportDataTransfertargetKeys"
                @on-change="handleChange"></Transfer>

            <br/>
            
            <div>
                导出文件名：
                <Input placeholder="请输入导出的文件名" style="width: auto" v-model.trim="exportDataFileName"/>
                .csv
            </div>
            <br/>
            <RadioGroup v-model="exportDatacurrentType" type="button" size="large">
                <Radio label="curpage"><Icon type="ios-download-outline" />导出当前页数据</Radio>
                <Radio label="all"><Icon type="ios-cloud-download-outline" />导出全量数据</Radio>
            </RadioGroup>
            <br/><br/>
            <Alert type="warning" show-icon v-if="exportDatacurrentType=='all' && pagination.countRecord>800">
                由于数据量较大，将自动导出多个文件。
            </Alert>
			
			<div slot="footer">
				<Button type="success" @click="exportData">确认导出</Button>
				<Button @click="exportmodal = false">关闭</Button>
			</div>
			
		</Modal>
    </div>
</template>

<script>
    import CONFIG from "@/config/index"
    import $utils from "@/libs/util";
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
				exportmodal:false,
				exportDataTransfer:[],
				exportDataTransfertargetKeys:[],
                exportDatacurrentType:"curpage",
                exportDataFileName:"导出的GIS数据"
            }
        },
        computed: {
            "height": function() {
                //return this.option.height || 525;
                return this.option.height || "";
            },
            "highlightRow":function() {
                //return this.option.height || 525;
                return this.option.highlightRow || false;
            },
            "border":function(){
                return this.option.border || false;
            },
            "columnsOption": function() {
                var columns = [];
                if (typeof this.option.columns == "function") {
                    columns =  this.option.columns();
                } else {
                    columns = this.option.columns;
                }
                columns.map((j,k)=>{
                    if("string" == typeof j.width){
                        j.width = Number(j.width.replace("px",""));
                    }
                    if(j.ellipsis && j.align=="center"){
                        j.align = "left";
                    }
                });
                return columns;
            },
            "showHeader": function() {
                return this.option.header;
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
                } 
                else if (this.option.data) {
                    this.pagination.countRecord = this.option.data.length;
                    var list = this.option.data.filter((data, index) => {
                        return index <= this.currentPageEnd - 1 && index >= this.currentPageStart - 1;
                    });
                    return list;
                }
            }
        },
        "methods": {
			"exportDataModal":function(){
                if(this.isLoading){
                    this.$Message.warning('数据正在加载，请稍候！');
                }
                else{
                    if(this.pagination.countRecord ==0){
                        this.$Message.warning('暂无数据可导出');
                    }
                    else{
                        this.exportmodal = true;
                        this.exportDataTransfer = this.columnsOption;
                    } 
                }
			},
			"exportData":function(){
                if(this.exportDataFileName ==""){
                    this.$Message.error('请输入导出文件名');
                }
                else if(this.exportDataTransfertargetKeys.length==0){
                    this.$Message.error('请选择需要导出的数据列');
                }
                else{
                    var columns = this.columnsOption.filter((j,i)=>{
                        return this.exportDataTransfertargetKeys.indexOf(j.key) >-1;
                    }),
                    outFields = this.exportDataTransfertargetKeys.join(","),
                        fileName = this.exportDataFileName+"_"+new Date().Format("yyyy-MM-dd hh:mm:ss");

                    if(!this.option.url){
                        var data =[]; 
                        if(this.exportDatacurrentType =="curpage"){
                            data = this.processExportData(this.list,this.option.fields);
                        }
                        else{
                            data = this.processExportData(this.option.data,this.option.fields);
                            
                        }
                        this.$refs.table.exportCsv({
                            filename: fileName,
                            columns: columns,
                            data:data,
                            quoted:true
                        });
                        this.$Message.info('文件导出完成');
                        
                    }
                    else{
                        var msg = this.$Message.loading({
                            content: '正在读取数据...',
                            duration: 0
                        });

                        if(this.exportDatacurrentType =="curpage"){
                            this.exportDataForFile(this.pagination.currentPage-1,this.pagination.pageSize,outFields,fileName,columns,()=>{
                                this.$Message.info('文件导出完成');
                                msg && msg();
                            });
                        }
                        else{
                            if(this.pagination.countRecord > 1000){
                                var curPage=0,
                                    page = Math.ceil(this.pagination.countRecord / 1000);

                                var fn = ()=>{
                                    this.exportDataForFile(curPage,1000,outFields,fileName+"(第"+(curPage+1)+"页)",columns,()=>{
                                        curPage++;
                                        if(curPage < page){
                                            fn();
                                        }
                                        else{
                                            this.$Message.info('文件导出完成');
                                            msg && msg();
                                        }  
                                    });
                                };
                                fn();
                            }
                            else{
                                this.exportDataForFile(0,1000,outFields,fileName,columns,()=>{
                                    this.$Message.info('文件导出完成');
                                    msg && msg();
                                });
                            }
                        }
                    }
                    
                }	
			},
            "exportDataForFile":function(curPage,pageSize,outFields,fileName,columns,cb){
                this.getExportData ({
                    "resultRecordCount": pageSize,
                    "resultOffset": curPage * pageSize,
                    "returnGeometry":false,
                    "outFields":outFields
                },(flag,data,fields)=>{
                    if(flag){
                        data = this.processExportData(data,fields);
                        this.$refs.table.exportCsv({
                            filename: fileName,
                            columns: columns,
                            data:data,
                            quoted:true
                        });

                    }
                    else{
                        this.$Message.error(curPage+'文件导出失败');
                    }
                    cb && cb(flag,data)
                });
            },
            "processExportData":function(data,fields){
                var fieldsMap = {};
                fields.map((j,i)=>{
                    fieldsMap[j.name] = j;
                })
                return data.map((j,i)=>{
                    for(let k in j){
                        var fieldObj = fieldsMap[k];
                        if(fieldObj){
                            if(fieldObj.type == "esriFieldTypeDate"){
                                if(j[k]){
                                    j[k] = new Date(j[k]).Format("yyyy-MM-dd")
                                }
                            }
                            if(j.name == "attachment_id"){
                                if(j[k]){
                                    j[k] = $utils.getFileShowUrl(j[k]);
                                }
                            }
                        }
                        if(!j[k]){
                            j[k] = "";
                        }
                    }
                    return j;
                })
            },
            "getExportData":function(arg,cb){
                var obj = Object.assign({},this.lastRequestParms, arg);
                this.getAsynData(this.option.url, obj)
                .then((res) => {
                    if(res.data.error){
                        cb(false);
                    }
                    else{
                        if(res.data.features.length>0){
                            var data = res.data.features.map((j,i)=>{
                                return j.attributes;
                            })
                            cb(true,data,res.data.fields); 
                        }
                        else{
                            cb(false);
                        }
                    }
                },()=>{
                    cb(false);
                })
            },
			"handleChange":function(newTargetKeys){
				this.exportDataTransfertargetKeys = newTargetKeys;
			},
			"exportDatarenderFormat":function(item){
				return item.title;
			},
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
            //初始化加载,保留分页参数。arg为请求参数
            "reLoad": function(arg) {
                this.loadData(Object.assign(this.lastRequestParms, arg));
            },
            //重新加载，arg为请求参数
            "load": function(arg) {
                this.pagination.currentPage = 1;
                this.loadData(arg,(fields)=>{
                    this.setColumns(fields);
                });
            },
            "setColumns":function(fields){
                var columns = fields.map((j,i)=>{
                    var obj = {
                        "title": j.alias,
                        "key": j.name,
                        "minWidth":100,
                        "fixed": i<2 ? 'left' :'',
                    }
                    if(j.type == "esriFieldTypeDate"){
                        obj.render = function(h,obj){
                            if(obj.row[j.name]){
                                return h("span",{},new Date(obj.row[j.name]).Format("yyyy-MM-dd"));
                            }
                            else{
                                return "";
                            }
                        }
                    }
                    if(j.name == "attachment_id"){
                        obj.render = function(h,obj){
                            if(obj.row[j.name]){
                                return h("a",{
                                    attrs: {
                                        href:$utils.getFileShowUrl(obj.row[j.name]),
                                        target:'_new',
                                    }
                                },"查看");
                            }
                            else{
                                return "";
                            }
                        }
                    }
                    return obj;
                });

                this.option.columns = columns;
            },
            "loadData": function(asyndata,cb) {
                var that = this;
                if (this.option.url) {
                    var obj = {
                        "resultRecordCount": this.pagination.pageSize,
                        "resultOffset": this.currentPageStart-1,
                    }
                    if (asyndata) {
                        this.lastRequestParms = asyndata || {};
                        obj = Object.assign(asyndata, obj);
                    }
                    this.isLoading = true;
                    this.getAsynData(this.option.url, obj,this.option.options || {})
                        .then((res) => {
                            if(res.data.error){
                                this.asynData = [];
                            }
                            else{
                                if(res.data.features.length>0){
                                    cb && cb(res.data.fields)
                                    this.asynData = res.data.features.map((j,i)=>{
                                        return j.attributes;
                                    })
                                }
                                else{
                                    this.asynData = [];
                                }
                            }
                            this.isLoading = false;

                        },()=>{
                            this.asynData = [];
                            this.isLoading = false;
                        });

                }
                else if (this.option.data) {
                    //本地数据
                    if (this.pagination) {
                        this.pagination.countRecord = this.option.data.length;
                    };
                }
            },
            "getAsynData": function(url, data,options) {
                return this.$ajax.post(url, data,options|| {})
            },
            pageSizeChange:function(size){
                this.pagination.pageSize = size;
                this.pagination.currentPage = 1;
                this.reLoad({});
            },
            changePage: function(currentPage) {
                this.pagination.currentPage = currentPage;
                this.reLoad({});
                this.$emit("changePage",this.pagination);
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
            onRowClick:function(column,index){
                this.$emit("on-row-click",column,index)
            },
            onCurrentChange:function(currentRow,oldCurrentRow){
                 this.$emit("on-current-change",currentRow,oldCurrentRow)
            },
            selectAll:function(status){
                this.$refs.table.selectAll(status);
            },
            getSelection:function(){
                return this.$refs.table.getSelection();
            },
            getTableInstance:function(){
                return this.$refs.table;
            }
        },
        created: function() {

        },
        mounted:function(){

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
	.module-datagrid-toolsexport{
		float:left;
	}
</style>