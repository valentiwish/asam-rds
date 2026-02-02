package com.robotCore.common.constant;

/**
 * @Description: 数据字典常量——系统级别
 * @Author: zhangqi
 * @Create: 2019-11-15 10:09
 */
public class Constant {
    /**
     * 返回给前端token名称
     */
    public static final String TOKEN_KEY = "access-token";
    /**
     * HTTP status code： HTTP状态码
     */
    public static final String STATUS_CODE = "code";
    /**
     * 返回数据存储的结果集
     */
    public static final String RESULT_SET = "data";

    /**
     * HTTP status code： 总和
     */
    public static final String STATUS_COUNT = "count";

    /**
     * 系统级错误信息
     */
    public static final String MESSAGE = "message";

    /**
     * 系统级错误信息
     */
    public static final String NONE = "message";
    /**
     * 系统中所有的state状态：state = 0，无效；state = 1，有效；
     */
    /**
     * 无效
     */

    public static final int STATE_INVALID = 0;
    /**
     * 有效
     */
    public static final int STATE_VALID = 1;
    /**
     * 已提交
     */
    public static final int STATE_SUBMIT = 2;
    /**
     * 常用码值：请求成功
     */
    public static final int SUCCESS = 200;
    /**
     * 常用码值：服务器错误
     */
    public static final int ERROR = 500;
    /**
     * 常用码值：数据校验失败
     */
    public static final int INVALID = 400;
    /**
     * 常用码值：未授权
     */
    public static final int UNAUTHORIZED = 401;
    /**
     * 服务器已经理解请求，但是拒绝执行它
     */
    public static final int FORBIDDEN = 403;
    /**
     * 常用码值：未找到
     */
    public static final int NOTFOUND = 404;
    /**
     * 请求行中指定的请求方法不能被用于请求相应的资源
     */
    public static final int METHOD_NOT_ALLOWED = 405;
    /**
     * 对于当前请求的方法和所请求的资源，请求中提交的实体并不是服务器中所支持的格式，因此请求被拒绝
     */
    public static final int UNSUPPORTED_MEDIA_TYPE = 415;
    /**
     * 创建未认证
     */
    public static final int NOTAUTH = 0;

    /**
     * 常用码值：PC端登录
     */
    public static final int PCLOGIN = 0;
    /**
     * 常用码值：移动端登录
     */
    public static final int MOBILELOGIN = 1;

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
     * 任务功能-工艺
     */
    public static final String FUNCTION_CODE_CRAFT = "CRAFT";

    /**
     * 可接单
     */
    public static final String ORDER_AVAILABLE = "可接单";

    /**
     * 可接单
     */
    public static final Integer ORDER_AVAILABLE_NUMBER = 1;

    /**
     * 不可接单
     */
    public static final String NOT_ORDER_AVAILABLE = "不可接单";

    /**
     * 不可接单
     */
    public static final Integer NOT_ORDER_AVAILABLE_NUMBER = 0;
}
