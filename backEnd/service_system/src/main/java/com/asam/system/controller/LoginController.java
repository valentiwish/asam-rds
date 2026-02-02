package com.asam.system.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.asam.common.base.controller.BaseController;
import com.asam.common.constant.Constant;
import com.asam.common.logUtil.ControllerLog;
import com.asam.common.logUtil.OpsLogType;
import com.asam.common.util.IpUtil;
import com.asam.common.util.ObjectUtil;
import com.asam.human.entity.UserRoleVo;
import com.asam.human.entity.UserVO;
import com.asam.human.service.HumUserService;
import com.asam.system.entity.SysAccount;
import com.asam.system.entity.SysIpFilter;
import com.asam.system.entity.SysSubsystem;
import com.asam.system.entity.SysVariable;
import com.asam.system.service.*;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.entity.R;
import com.utils.tools.TokenUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static com.asam.common.constant.Constant.*;

/**
 * @Description: 用户登录
 * @Author: fyy
 * @Create: 2022-04-20
 */
@RestController
@Api(value = "/login")
@RequestMapping(value = "/login")
public class LoginController extends BaseController {

    @Autowired
    private AccountService accountService;
    @Autowired
    private SystemInfoService systemInfoService;
    @Autowired
    private HumUserService humUserService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private SysIpFilterService sysIpFilterService;
    @Autowired
    private SysVariableService sysVariableService;

    @ApiOperation(value = "登录")
    @RequestMapping(value = "/login")
    public Object login(String data,HttpServletResponse response, HttpServletRequest request) {
        try {
            JSONObject map = JSON.parseObject(data);
            String loginName = map.getString("loginName");// 用户名
            String password = map.getString("password");// 密码
            String checkcode = map.getString("checkcode");  // 验证码
            String uniqueCode = map.getString("uniqueCode");// 扫描登录 此时不用用户名和密码 只要有账户直接登录
            String loginType = map.getString("loginType");// 登录类型

            // 验证码校验
            if (ObjectUtil.isNotEmpty(checkcode)) {
                String checkNumSession = request.getSession().getAttribute("checkNum") + "";   // 获取 SESSION 中保存的验证码
                if (!checkcode.equalsIgnoreCase(checkNumSession)) {
                    return R.error("验证码错误,请重新输入！");
                }
            }
            //三员管理拦截设置
            String ipAddress = IpUtil.getIpAddress(request);
            List<SysVariable> sysVariableList=sysVariableService.findList(null,"WHITELIST_FILTER");
            if(sysVariableList!=null&&sysVariableList.size()>0){
                SysVariable sysVariable =sysVariableList.get(0);
                if(sysVariable.getVarValue().equals("1")) {
                    if(loginName.equals(SYS_ADMIN) || loginName.equals(SAFETY_ADMIN) || loginName.equals(AUDITOR_ADMIN)) {
                        List<SysIpFilter> sysIpFilterList=sysIpFilterService.findList(null,ipAddress);
                        if(ObjectUtils.isEmpty(sysIpFilterList)) {
                            return R.error("IP限制，无法登陆");
                        }
                    }
                }
            }
            //校验账户信息
            UserVO userVO=null;
            if(ObjectUtil.isNotEmpty(uniqueCode)){
                userVO =  humUserService.getUserByLoginName(uniqueCode);
            }else{
                userVO =  humUserService.getUserByLoginName(loginName);
            }
            if(userVO==null){
                return R.error("账户不存在");
            }else {
                SysAccount account = accountService.findModel(userVO.getId());
                if (account.getState() == 2) {
                    return R.error("账户被禁用");
                } else {
                    if(ObjectUtil.isEmpty(uniqueCode)&&!StringUtils.equals(password, account.getPassword())){
                        return R.error("密码错误");
                    }else {
                        List<UserRoleVo> userRoleVoList = roleService.findRoleByLoginName(loginName);
                        if (Objects.nonNull(userRoleVoList) && Objects.nonNull(userVO)) {
                            userVO.setUserRoleVoList(userRoleVoList);
                        }
                        String token = TokenUtils.createToken(loginName, password, userVO);
                        response.addHeader("access-token", token);
                        Map<String, String> rMap = new HashMap<>();
                        rMap.put("token", token);
                        saveLog(data);//有了token后记录日志
                        //更新账户信息
                        account.setLastLoginTime(new Date());
                        account.setIp(ipAddress);
                        accountService.saveOrUpdate(account);
                        return R.ok(rMap);
                        //String redisKey = Constant.USER_TOKEN + ":" + loginName;
                        //redisUtil.stringWithSet(redisKey, token);// redis缓存token
                        // redisUtil.expire(redisKey, 60 * 60 * 24 * 30); // redis缓存时间，单位：秒  先注掉  让redis自己去设定
                    }
                }
            }
        }catch (Exception e){
            return R.error("登录异常");
        }
    }

    @ControllerLog(type = OpsLogType.LOGIN, value = "账户管理")
    private void saveLog(String data){  }

    @ControllerLog(type = OpsLogType.LOGOUT, value = "账户管理")
    @ApiOperation(value = "登出")
    @RequestMapping(value = "/logout")
    public Object logout(HttpServletRequest request){
        //用户退出逻辑
        String token = request.getHeader(Constant.TOKEN_KEY);
        if (StringUtils.isEmpty(token)) {
            return R.error("TOKEN不存在");
        }else{
            HttpSession session = request.getSession(false);
            if(session!=null) {
                session.invalidate();//使Session变成无效，及用户退出
            }
            return R.ok("退出成功");
        }
    }

    @ApiOperation(value = "检查token有效性")
    @RequestMapping(value = "/checkToken")
    public Object checkToken(String token){
        return TokenUtils.verifyToken(token);
    }

    @ApiOperation(value = "获取验证码")
    @RequestMapping(value = "/getIdentifyingCode")
    private void getIdentifyingCode(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("image/jpeg");
        HttpSession session = request.getSession();
        int width = 66;
        int height = 26;
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        char[] rands = new char[4];
        for (int i = 0; i < 4; i++) {
            int rand = (int) (Math.random() * 36);
            rands[i] = chars.charAt(rand);
        }
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        for (int i = 0; i < 120; i++) {
            int x = (int) (Math.random() * width);
            int y = (int) (Math.random() * height);
            int red = (int) (Math.random() * 255);
            int green = (int) (Math.random() * 255);
            int blue = (int) (Math.random() * 255);
            g.setColor(new Color(red, green, blue));
            g.drawOval(x, y, 1, 0);
        }
        g.setColor(Color.BLACK);
        g.setFont(new Font(null, Font.ITALIC | Font.BOLD, 18));
        g.drawString("" + rands[0], 1, 17);
        g.drawString("" + rands[1], 16, 15);
        g.drawString("" + rands[2], 31, 18);
        g.drawString("" + rands[3], 46, 16);
        g.dispose();
        response.reset();
        ServletOutputStream sos = response.getOutputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "JPEG", baos);
        byte[] buffer = baos.toByteArray();
        session.setAttribute("checkNum", String.valueOf(rands));
        response.setContentLength(buffer.length);
        sos.write(buffer);
        baos.close();
        sos.close();
    }

    @ApiOperation(value = "根据 appid 获取配置对象，不包括密钥字段，不经过权限拦截")
    @RequestMapping(value = "/getDataByAppid")
    public Object getDataByAppid(String appid) {
        SysSubsystem systemInfoVO=null;
        List<SysSubsystem> list = systemInfoService.findList(appid,1,null);//String appid, Integer enable, Collection<String> ids
        if(list!=null&&list.size()>0) {
            systemInfoVO = list.get(0);
        }
        return R.ok(systemInfoVO);
    }

    @ApiOperation(value = "获取当前登录人")
    @RequestMapping(value = "/getLoginUser")
    public Object getLoginUser(){
        UserVO user=getCurUser();
        return R.ok(user);
    }

    @ApiOperation(value = "吉大正元", notes = "用户登录")
    @PostMapping(value = "/getDnName")
    public Object getDnName(HttpServletRequest request) throws Exception {
        Map<String, Object> result = new HashMap<>(2);
        Map<String, String> map = new HashMap<String, String>(3);
        // 用户证书主题
        String dn = request.getHeader("dnname");
        if (StringUtils.isEmpty(dn)) {
            StringBuilder sb = new StringBuilder();
            // 本地测试登录账户
            sb.append("E=127.0.0.1").append(",");
            sb.append("CN=管理员1").append(",");
            sb.append("T=admin66").append(",");
            sb.append("ON=0212").append(",");
            sb.append("O=航天科工").append(",");
//            sb.append("T=610121198606225560");
            dn = sb.toString();
        } else {
            byte[] bytes = dn.getBytes("ISO8859-1");
            dn = new String(bytes, "UTF-8");
        }

        // 取用户客户端IP
        String tStrClientIP = request.getHeader("clientip");
        if (tStrClientIP == null) {
            tStrClientIP = "127.0.0.1";
        }
        if (StringUtils.isNotBlank(dn) && StringUtils.isNotBlank(tStrClientIP)) {
            String dnArr[] = dn.split(",");
            for (String dnStr : dnArr) {
                if (dnStr.contains("=")) {
                    String arr[] = dnStr.split("=");
                    map.put(arr[0], String.valueOf(arr[1]));
                }
            }
            Map<String, String> rMap = new HashMap<>();
            rMap.put("checkcode", "checkcode");
            rMap.put("checkcodeuuid", "checkcodeuuid");
            if (map.containsKey("T")) {
                UserVO userVO = humUserService.getUserByLoginName(map.get("T"));
                if (Objects.nonNull(userVO) && userVO.getUserName().equals(map.get("CN"))) {
                    List<SysAccount> sysAccountList=accountService.findList(null,null,map.get("T"));
                    if(ObjectUtil.isNotEmpty(sysAccountList)){
                        SysAccount sysAccount =sysAccountList.get(0);
                        result = this.loginClss(sysAccount.getLoginName(),sysAccount.getPassword());
                    }

                } else {
                    result.put("data", rMap);
                    result.put("code", 201);
                    result.put("msg", "身份信息认证失败");
                }
            } else {
                result.put("data", rMap);
                result.put("code", 201);
                result.put("msg", "吉大正元身份认证信息未获取");
            }
        } else {
            result.put("code", 500);
            result.put("msg", "吉大正元身份认证信息未获取");
        }
        return result;
    }

    private Map<String, Object> loginClss(String loginName,String password) {
        Map<String, Object> result = new HashMap<>(2);
        try {
            //使用登录名去匹配用户表中的电话号码，身份证，邮箱，和登录账号，查询用户信息
            UserVO userVO = humUserService.getUserByLoginName(loginName);
            if (Objects.nonNull(userVO)) {
                ///根据用户中的登录账号和登录时填写的密码，查询账户信息，验证登录名和密码
                SysAccount sysAccount = accountService.findModel(userVO.getId());
                Map<String, String> rMap = new HashMap<>();
                ///账户存在
                if (Objects.nonNull(sysAccount)) {
                    ///校验密码是否正确
                    if (StringUtils.equals(password, sysAccount.getPassword())) {
                        String token = TokenUtils.createToken(loginName, password,userVO);
                        result.put("code", 200);
                        rMap.put("token", token);
                    } else {
                        result.put("code", 201);
                        result.put("msg", "密码错误");
                        rMap.put("checkcode", "checkcode");
                        rMap.put("checkcodeuuid", "checkcodeuuid");
                    }
                } else {
                    result.put("code", 201);
                    result.put("msg", "账户不存在");
                }
                result.put("data", rMap);
            } else {
                result.put("code", 201);
                result.put("msg", "账户不存在");
            }
        } catch (Exception e) {
            result.put("code", 401);
        }
        return result;
    }
}
