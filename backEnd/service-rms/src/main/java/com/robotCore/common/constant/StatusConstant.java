package com.robotCore.common.constant;

/**
 * @Description: 状态常量
 * @Author: Created by lufan on 2021/4/18.
 */
public class StatusConstant {

    /**
     * 计划-新建
     */
    public static final Integer PLAN_CREATED = 0;


    /**
     * 计划-执行中
     */
    public static final Integer PLAN_EXECUTED = 1;

    /**
     * 计划-挂起
     */
    public static final Integer PLAN_SUSPEND = 2;

    /**
     * 计划完成
     */
    public static final Integer PLAN_FINISHED = 3;

    /**
     * 计划结束
     */
    public static final Integer PLAN_CLOSE = 4;


    /**
     * 计划来源-手工创建
     */
    public static final Integer PLAN_SOURCE_MANUAL = 0;

    /**
     * 计划来源-文件导入
     */
    public static final Integer PLAN_SOURCE_IMPORT = 1;

    /**
     * 计划来源-第三方对接
     */
    public static final Integer PLAN_SOURCE_INTEGRATE = 2;

    /**
     * 工单新建
     */
    public static final Integer ORDER_CREATED = 0;

    /**
     * 工单展开
     */
    public static final Integer ORDER_SPREAD = 1;

    /**
     * 工单发布
     */
    public static final Integer ORDER_RELEASED = 2;

    /**
     * 工单接收
     */
    public static final Integer ORDER_RECEIVED = 3;

    /**
     * 工单执行
     */
    public static final Integer ORDER_EXECUTED = 4;

    /**
     * 工单挂起
     */
    public static final Integer ORDER_SUSPEND = 5;

    /**
     * 工单完成
     */
    public static final Integer ORDER_FINISHED = 6;

    /**
     * 工单关闭
     */
    public static final Integer ORDER_CLOSE = 7;


    /**
     * 派工单新建
     */
    public static final Integer DISPATCH_CREATED = 0;

    /**
     * 派工单发布
     */
    public static final Integer DISPATCH_RELEASED = 1;

    /**
     * 执行中
     */
    public static final Integer DISPATCH_PRODUCED = 2;

    /**
     * 挂起
     */
    public static final Integer DISPATCH_SUSPEND = 3;

    /**
     * 完成
     */
    public static final Integer DISPATCH_FINISHED = 4;

    /**
     * 派工单关闭
     */
    public static final Integer DISPATCH_CLOSE = 5;

    /**
     * 下料单新建
     */
    public static final Integer PICK_CREATED = 0;

    /**
     * 下料单发布
     */
    public static final Integer PICK_RELEASED = 1;

    /**
     * 下料单执行中
     */
    public static final Integer PICK_EXECUTED= 2;

    /**
     * 下料单暂停
     */
    public static final Integer PICK_SUSPEND= 3;

    /**
     * 下料单完成
     */
    public static final Integer PICK_FINISHED= 4;

    /**
     * 质检单创建
     */
    public static final Integer INSPECT_CREATED= 0;

    /**
     * 质检单完成
     */
    public static final Integer INSPECT_FINISHED = 1;

    /**
     * 质检单报工完成
     */
    public static final Integer INSPECT_RECORD = 2;

    /**
     * 生产记录类型-加工
     */
    public static final Integer RECODE_PROD = 1;

    /**
     * 生产记录类型-质检
     */
    public static final Integer RECODE_INSPECT = 2;

    /**
     * 生产记录类型-总装
     */
    public static final Integer RECODE_ASSEMBLE = 3;

    /**
     * 系统中所有的status：status = 0，无效；status = 1，草稿；status = 2，审批中；status = 3，审批驳回（驳回到发起人、流程结束）；status = 4，审批完成(正常审批结束)；status = 5流程执行中
     */
    /**
     * 草稿
     */
    public static final int STATUS_DRAFT = 1;
    /**
     * 审批中
     */
    public static final int STATUS_PENDING = 2;
    /**
     * 审批通过
     */
    public static final int STATUS_DOPT = 4;
    /**
     * 审批驳回
     */
    public static final int STATUS_REJECT = 3;
    /**
     * 执行中
     */
    public static final int STATUS_EXECUTE = 5;
    /**
     * 流程结束
     */
    public static final int STATUS_OVER = 6;

    /**
     * 发布
     */
    public static final int STATUS_RELEASE = 7;

    /**
     * 物料批次状态-新建
     */
    public static final Integer BATCH_CREATED = 0;

    /**
     * 物料批次状态-生产（质检）中
     */
    public static final Integer BATCH_PROD = 1;

    /**
     * 物料批次状态-生产（质检）完成
     */
    public static final Integer BATCH_HANDLE = 2;

    /**
     * 物料批次状态-报工结束
     */
    public static final Integer BATCH_FINISHED = 3;

    /**
     * 物料批次状态-质检准备中
     */
    public static final Integer BATCH_READY_TO_CHECK = 4;


    /**
     * 用户任务状态-新建
     */
    public static final Integer USER_TASK_CREATED = 0;

    /**
     * 用户任务状态-完成
     */
    public static final Integer USER_TASK_FINISHED = 1;

    /**
     * 用户任务阅读状态-未读
     */
    public static final Integer USER_TASK_NOT_READ = 0;

    /**
     * 用户任务阅读状态-已读
     */
    public static final Integer USER_TASK_READ = 1;

    /**
     * 用户任务-未超期
     */
    public static final Integer USER_TASK_NOT_OVERDUE = 0;

    /**
     * 用户任务-超期
     */
    public static final Integer USER_TASK_OVERDUE = 1;


    /**
     * 维保单-新建
     */
    public static final Integer MAINTAIN_ORDER_CREATED = 0;

    /**
     * 维保单-已发布
     */
    public static final Integer MAINTAIN_ORDER_PUBLISH = 1;

    /**
     * 维保单-执行中
     */
    public static final Integer MAINTAIN_ORDER_EXECUTING = 2;

    /**
     * 维保单-暂停
     */
    public static final Integer MAINTAIN_ORDER_PAUSE = 3;

    /**
     * 维保单-完成
     */
    public static final Integer MAINTAIN_ORDER_FINISHED = 4;

    /**
     * 维保单-废弃
     */
    public static final Integer MAINTAIN_ORDER_END = 5;

    /**
     * 生产记录-创建
     */
    public static final Integer PROD_CREATED = 0;

    /**
     * 生产记录-完工
     */
    public static final Integer PROD_FINISHED = 1;

    /**
     * 生产记录报工
     */
    public static final Integer PROD_REPORTED = 2;

}