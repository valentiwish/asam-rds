package com.robotCore.common.constant;

/**
 * @Description: 批量导入标题
 * @Author: zhangqi
 * @Create: 2021/4/15 15:49
 */
public class TitleConstant {
    /**
     * BOM单导入标题
     */
    public static final String BOM_CODE = "BOM编码";
    public static final String BOM_NAME = "BOM名称";
    public static final String BOM_PRODUCT_CODE = "产品编码";
    public static final String BOM_VERSION = "版本号";
    public static final String BOM_ITEM_BELONG_CODE = "归属BOM编码";
    public static final String BOM_ITEM_CHILD_MAT_CODE = "明细项物料编码";
    public static final String BOM_ITEM_MAT_FIGRUE = "物料图号";
    public static final String BOM_ITEM_AMOUNT = "数量";
    public static final String BOM_ITEM_UNIT = "计量单位";
    public static final String BOM_ITEM_PARENT_MAT_CODE = "父级物料编码";
    public static final String BOM_ITEM_KEY_MARK = "是否关重件";
    public static final String BOM_ITEM_MANUF_TYPE = "生产类型";
    public static final String BOM_ITEM_PRODUCT_TYPE = "产品类型";
    public static final String BOM_ITEM_REMARK = "备注";
    public static final String ROUTE_CODE = "工艺编码";
    public static final String ROUTE_NAME = "工艺名称";
    public static final String ROUTE_MAT_CODE = "关联物料编码";
    public static final String ROUTE_VERSION = "版本号";
    public static final String PROCESS_ROUTE = "关联工艺路线";
    public static final String PROCESS_SEQUENCE_NO = "工序顺序号";
    public static final String PROCESS_CODE = "工序编码";
    public static final String PROCESS_NAME = "工序名称";
    public static final String PROCESS_NO = "工序号";
    public static final String PROCESS_TYPE = "工序类型";
    public static final String PROCESS_DEPT = "责任单位";
    public static final String PROCESS_PROD_HOUR = "定额工时";
    public static final String PROCESS_PRICE_HOUR = "价格工时";
    public static final String PROCESS_DESP = "备注";
    public static final String PROCESS_KEY_MARK = "是否关键工序";
    public static final String PROCESS_SPEIAL_MARK = "是否特殊过程";
    public static final String PROCESS_EQUIP_CODE = "设备编码";
    public static final String PROCESS_TOOL_CODE = "工装编码";
    public static final String PROCESS_DIAGRAM_CODE = "工艺简图编码";
    public static final String PROCESS_PROGRAM_CODE = "数控程序编码";
    /**
     * 物料导入标题
     */
    public static final String MAT_NUMBER = "物料编号";
    public static final String MAT_CODE = "物料编码";
    public static final String MAT_NAME = "物料名称";
    public static final String MAT_DESP = "物料描述";
    public static final String PRODUCT_FIGURE_NO = "产品图号";
    public static final String PRODUCT_MODE = "产品型号";
    public static final String PRODUCT_CODE = "产品代号";
    public static final String MAT_BRAND_NO = "材料牌号";
    public static final String MAT_SPEC = "规格";
    public static final String MAT_CLASS = "物料分类";
    public static final String MAT_TYPE = "物料类型";
    public static final String PRODUCT_TYPE = "产品类型";
    public static final String UNIT = "计量单位";
    public static final String MAT_STATUS = "物料状态";
    public static final String MAT_ERP_CODE = "ERP编码";
    public static final String SECRET_LEVEL = "密级";
    public static final String REMARK = "备注";

    public static final String DICT_TYPE_MAT = "MAT_TYPE";
    public static final String DICT_TYPE_PRODUCT = "PROD_TYPE";
    public static final String DICT_PROCESS_TYPE = "PROCESS_TYPE";
    public static final String DICT_UNIT_TYPE = "TIME_UNIT";
    /**
     * 班组信息导入标题
     */
    public static final String TEAM_CODE = "班组编码";
    public static final String TEAM_NAME = "班组名称";
    public static final String TEAM_DEPT = "责任单位";
    public static final String TEAM_DUTY = "职责描述";
    public static final String TEAM_SECRET_LEVEL = "密级";
    public static final String TEAM_REMARK = "备注";


    /**
     * 生产订单导入标题
     */
    public static final String ORDER_ID = "标识号";
    public static final String ORDER_AUFNR = "订单编号";
    public static final String ORDER_AUFPL = "工艺路线号";
    public static final String ORDER_AUART = "订单类型";
    public static final String ORDER_WERKS = "场所编码";
    public static final String ORDER_GLTRP = "计划结束时间";
    public static final String ORDER_GSTRP = "计划开始时间";
    public static final String ORDER_ASTKZ = "订单状态";
    public static final String ORDER_GAMNG = "订单数量";
    public static final String ORDER_MEINS = "基本单位";
    public static final String ORDER_PLNBEZ = "物料编码";
    public static final String ORDER_MAKTX = "物料描述";
    public static final String ORDER_ZEINR = "图号";
    public static final String ORDER_ZTASK = "任务号";
    public static final String ORDER_ZPH = "批号";
    public static final String ORDER_CHARG = "批次";
    public static final String ORDER_ZMJ = "密级";
    public static final String ORDER_POSID = "WBS要素";
    public static final String ORDER_ZJD = "阶段";
    public static final String ORDER_KDAUF = "销售订单号";
    public static final String ORDER_KDPOS = "销售订单行项目";
    public static final String ORDER_ZKDPOSS = "产品序列号";
    public static final String ORDER_STKTX = "BOM版本号";
    public static final String ORDER_CREATEDATE = "创建时间";
    public static final String ORDER_UPDATEDATE = "更新时间";
    public static final String ORDER_REMARK = "备注";
    public static final String ORDER_RESCODE = "返回状态";
    public static final String ORDER_ERRMSG = "错误原因";
    public static final String ORDER_RETURNDATE = "回复时间";
    public static final String ORDER_MATNAME = "物料名称";
    public static final String ORDER_PRODUCTCODE = "产品代号";
    public static final String ORDER_STATUS = "状态";

    /**
     * 工序导入标题
     */
    public static final String PROCESS_ID = "标识号";
    public static final String PROCESS_AUFNR = "订单编号";
    public static final String PROCESS_ZTEXT = "工艺路线版本号";
    public static final String PROCESS_VORNR = "工序序号";
    public static final String PROCESS_ARBID = "工作中心";
    public static final String PROCESS_STEUS = "控制码";
    public static final String PROCESS_ITXAL = "工序描述";
    public static final String PROCESS_CREATEDATE = "创建时间";
    public static final String PROCESS_REMARK = "备注";
    public static final String PROCESS_STATE = "状态码";
    public static final String PROCESS_OPERATOR = "操作人员";
    public static final String PROCESS_MANUDATE = "操作日期";



    /**
     * 模板导入标题
     */
    public static final String TEMPLATE_ID = "标识号";
    public static final String TEMPLATE_COMPANYNAME = "厂所名称";
    public static final String TEMPLATE_TEMPLATECODE = "模板编码";
    public static final String TEMPLATE_TEMPLATENAME = "模板名称";
    public static final String TEMPLATE_MATCODE = "产品编码";
    public static final String TEMPLATE_MATNMAE = "产品名称";
    public static final String TEMPLATE_PRODUCTFIGURENO = "产品图号";
    public static final String TEMPLATE_PRODUCTCOE = "产品代号";
    public static final String TEMPLATE_ROUTEFILECODE = "工艺文件编号";
    public static final String TEMPLATE_MARKPHASE = "标记阶段";
    public static final String TEMPLATE_ROUTENAME = "工艺名称";
    public static final String TEMPLATE_PROCESSNAME = "工序名称";
    public static final String TEMPLATE_PROCESSNO = "工序号";
    public static final String TEMPLATE_TEMPLATESTATUS = "模板状态";
    public static final String TEMPLATE_VERSIONNO = "版本号";
    public static final String TEMPLATE_CREATEID = "创建人id";
    public static final String TEMPLATE_UPDATEID = "修改人id";
    public static final String TEMPLATE_CREATEUSER = "创建人";
    public static final String TEMPLATE_UPDATEUSER = "修改人";
    public static final String TEMPLATE_CREATETIME = "创建时间";
    public static final String TEMPLATE_UPDATETIME = "修改时间";
    public static final String TEMPLATE_REMARK = "备注";
    public static final String TEMPLATE_SECRETLEVELNO = "密级";
    public static final String TEMPLATE_STATE = "状态";


    /**
     * 投料导入标题
     */
    public static final String GROUP_UID = "主键";
    public static final String GROUP_ID = "标识号";
    public static final String GROUP_PID = "模板ID";
    public static final String GROUP_DID = "工单记录ID";
    public static final String GROUP_NAME = "检验组名称";
    public static final String GROUP_LIST = "检验组内容";
    public static final String GROUP_TYPE = "类型";
    public static final String GROUP_CREATEID = "创建人id";
    public static final String GROUP_UPDATEID = "修改人id";
    public static final String GROUP_CREATEUSER = "创建人";
    public static final String GROUP_UPDATEUSER = "修改人";
    public static final String GROUP_CREATETIME = "创建时间";
    public static final String GROUP_UPDATETIME = "修改时间";
    public static final String GROUP_REMARK = "备注";
    public static final String GROUP_SECRETLEVELNO = "密级";
    public static final String GROUP_STATE = "状态";

    /**
     * 记录数据导入标题
     */
    public static final String RECORD_ID = "主键";
    public static final String RECORD_DOCKETID = "记录单ID";
    public static final String RECORD_TEMPLATEID = "模板ID";
    public static final String RECORD_PARAMVALUE = "参数值";
    public static final String RECORD_INSPECTITEM = "检测项";
    public static final String RECORD_INSPECTINDICAT = "检测指标";
    public static final String RECORD_MATECODE = "产品编码";
    public static final String RECORD_NAMETYPE = "参数类型";
    public static final String RECORD_SPECIALVALUE = "特殊值";
    public static final String RECORD_GROUPNO = "分组码";
    public static final String RECORD_OPERATEUSERID = "操作人ID";
    public static final String RECORD_OPERATEUSER = "操作人";
    public static final String RECORD_OPERATETIME = "操作时间";
    public static final String RECORD_RECORDUSERID = "记录人ID";
    public static final String RECORD_RECORDUSER = "记录人";
    public static final String RECORD_RECORDTIME = "记录时间";
    public static final String RECORD_CHECKUSERID = "检验人ID";
    public static final String RECORD_CHECKUSER = "检验人";
    public static final String RECORD_CHECKTIME = "检验时间";
    public static final String RECORD_PID = "检验组ID";
    public static final String RECORD_ORDERNUM = "列排序";
    public static final String RECORD_MATNAME = "产品名称";
    public static final String RECORD_CREATEID = "创建人id";
    public static final String RECORD_UPDATEID = "修改人id";
    public static final String RECORD_CREATEUSER = "创建人";
    public static final String RECORD_UPDATEUSER = "修改人";
    public static final String RECORD_CREATETIME = "创建时间";
    public static final String RECORD_UPDATETIME = "修改时间";
    public static final String RECORD_REMARK = "备注";
    public static final String RECORD_SECRETLEVELNO = "密级";
    public static final String RECORD_STATE = "状态";
}
