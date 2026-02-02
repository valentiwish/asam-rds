package com.robotCore.common.constant;

/**
 * @Description: 描述
 * @Author: Created by lufan on 2021/4/25.
 */
public class TypeConstant {

    /**
     *  厂所组织机构编码：210所：210,389厂：389,359厂：359
     */
    public static final String TOP_ORG_CODE = "210";

    /**
     * 生产领料
     */
    public static final Integer PLAN_PICK = 0;

    /**
     * 销售领料
     */
    public static final Integer NON_PLAN_PICK = 1;


    /**
     * 工序类型-检验
     */
    public static final String PROCESS_TESTING = "TESTING";

    /**
     * 工序类型-下料
     */
    public static final String PROCESS_BLANKING = "BLANKING";

    /**
     * 工序类型-生产
     */
    public static final String PROCESS_PRODUCE = "PRODUCTION";

    /**
     * 工序顺序检测-验证
     */
    public static final String PROCESS_VERIFY = "1";

    /**
     * 工序设备限制
     */
    public static final String EQUIP_PROCESS_LIMIT = "1";

    /**
     * 工序设备程序限制，是否与MDC交互
     */
    public static final String EQUIP_PROGRAM_LIMIT = "1";


    /**
     * 组织机构代码：三室
     */
    public static final String DEPT_CODE_CRAFT = "CRAFT_ROOM";

    /**
     * 组织机构代码：生产中心
     */
    public static final String DEPT_CODE_PORDUCT_CENTER = "workshopten";

    /**
     * 组织机构代码：科研生产处
     */
    public static final String DEPT_CODE_KEYAN = "keyan";

    /**
     * 组织机构代码：军品项目办
     */
    public static final String DEPT_CODE_AYMY_PORDUCT = "army_projectoffice";


    /**
     * 组织机构代码：四车间
     */
    public static final String ROLE_CODE_WORKSHOP_FOUR = "workshop_four";
    /**
     * 组织机构代码：复材
     */
    public static final String ROLE_CODE_FUCAI = "fucai_center";


}