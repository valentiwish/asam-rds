package com.asam.common.constant;

/**
 * @Description: 数据字典常量——系统级别
 * @Author: mln
 * @Create: 2018-08-10 14:34
 */
public class Constant {

    public static final String USER_TOKEN = "USER_TOKEN";
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

    public static final String SYS_ADMIN = "SYS_ADMIN";//系统管理员

    public static final String SAFETY_ADMIN = "SYS_SAFETY_ADMIN";//安全管理员

    public static final String AUDITOR_ADMIN = "SYS_AUDITOR_ADMIN";//安全审计员
}
