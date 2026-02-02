package com.robotCore.common.utils;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import org.apache.commons.lang.text.StrTokenizer;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

/**
 * @Description: 获取真实IP
 * @Author: zhangqi
 * @Create: 2019-11-15 10:58
 */
public class IpUtil {
    private static final String N255 = "(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
    private static final Pattern PATTERN = Pattern.compile("^(?:" + N255 + "\\.){3}" + N255 + "$");

    private IpUtil() {

    }

    private static String longToIpV4(long longIp) {
        int octet3 = (int) ((longIp >> 24) % 256);
        int octet2 = (int) ((longIp >> 16) % 256);
        int octet1 = (int) ((longIp >> 8) % 256);
        int octet0 = (int) ((longIp) % 256);
        return octet3 + "." + octet2 + "." + octet1 + "." + octet0;
    }

    private static long ipV4ToLong(String ip) {
        String[] octets = ip.split("\\.");
        return (Long.parseLong(octets[0]) << 24) + (Integer.parseInt(octets[1]) << 16)
                + (Integer.parseInt(octets[2]) << 8) + Integer.parseInt(octets[3]);
    }

    private static boolean isIPv4Private(String ip) {
        long longIp = ipV4ToLong(ip);
        return (longIp >= ipV4ToLong("10.0.0.0") && longIp <= ipV4ToLong("10.255.255.255"))
                || (longIp >= ipV4ToLong("172.16.0.0") && longIp <= ipV4ToLong("172.31.255.255"))
                || longIp >= ipV4ToLong("192.168.0.0") && longIp <= ipV4ToLong("192.168.255.255");
    }

    private static boolean isIPv4Valid(String ip) {
        return PATTERN.matcher(ip).matches();
    }

    public static String getIpFromRequest(HttpServletRequest request) {
        String ip;
        boolean found = false;
        if ((ip = request.getHeader("x-forwarded-for")) != null) {
            StrTokenizer tokenizer = new StrTokenizer(ip, ",");
            while (tokenizer.hasNext()) {
                ip = tokenizer.nextToken().trim();
                if (isIPv4Valid(ip) && !isIPv4Private(ip)) {
                    found = true;
                    break;
                }
            }
        }
        if (!found) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * @Author: Created by crystal on 2020/4/27.
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ip = null;
        boolean found = false;

        // X-Forwarded-For：Squid 服务代理
        String ipAddresses = request.getHeader("X-Forwarded-For");
        String unknown = "unknown";
        if (null == ipAddresses || 0 == ipAddresses.length() || unknown.equalsIgnoreCase(ipAddresses)) {
            // Proxy-Client-IP：apache 服务代理
            ipAddresses = request.getHeader("Proxy-Client-IP");
        }

        if (null == ipAddresses || 0 == ipAddresses.length() || unknown.equalsIgnoreCase(ipAddresses)) {
            // WL-Proxy-Client-IP：weblogic 服务代理
            ipAddresses = request.getHeader("WL-Proxy-Client-IP");
        }

        if (null == ipAddresses || 0 == ipAddresses.length() || unknown.equalsIgnoreCase(ipAddresses)) {
            // HTTP_CLIENT_IP：有些代理服务器
            ipAddresses = request.getHeader("HTTP_CLIENT_IP");
        }

        if (null == ipAddresses || 0 == ipAddresses.length() || unknown.equalsIgnoreCase(ipAddresses)) {
            //X-Real-IP：nginx服务代理
            ipAddresses = request.getHeader("X-Real-IP");
        }

        // 有些网络通过多层代理，那么获取到的ip就会有多个，一般都是通过逗号（,）分割开来，并且第一个ip为客户端的真实IP
//        if (null != ipAddresses && 0  != ipAddresses.length()) {
//            StrTokenizer tokenizer = new StrTokenizer(ipAddresses, ",");
//            while (tokenizer.hasNext()) {
//                ip = tokenizer.nextToken().trim();
//                if (isIPv4Valid(ip) && isIPv4Private(ip)) {
//                    found = true;
//                    break;
//                }
//            }
//        }
//
//        // 还是不能获取到，最后再通过request.getRemoteAddr();获取
//        if (false == found) {
//            ip = request.getRemoteAddr();
//        }
        if(ObjectUtils.isEmpty(ipAddresses)) {
            ipAddresses = request.getRemoteAddr();
        }
        return ipAddresses;
    }

}
