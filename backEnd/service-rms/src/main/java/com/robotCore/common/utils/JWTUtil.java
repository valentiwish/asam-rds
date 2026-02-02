package com.robotCore.common.utils;

import com.utils.tools.TokenUtils;
import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;

/**
 * @Description: 描述
 * @Author: Created by lufan on 2021/2/7.
 */
public class JWTUtil extends TokenUtils {

    /**
     * 获得token中的信息，无需secret解密也能获得
     */
    public static String getLoginName(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("userId").asString();
        } catch (JWTDecodeException e) {
            return null;
        }
    }
}