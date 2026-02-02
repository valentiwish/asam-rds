package com.robotCore.common.utils;

import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.social.support.URIBuilder;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

/***
 * @Description:
 * @Author: Created by pjb on 2020/11/14 14:49
 * @Modifier: Modify by pjb on 2020/11/14 14:49
 */
public class HttpClientUtil {

    /***
     * @Description: http 请求 参数类型为 MultiValueMap<String, String>
     * @param url HTTP URL
     * @param method 请求方式
     * @param params 请求参数
     * @Return: java.lang.String
     * @Date: 2020/11/14 14:50
     */
    public static String client(String url, HttpMethod method, MultiValueMap<String, String> params) {
        RestTemplate client = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<String> response = null;
        //设置超时
        SimpleClientHttpRequestFactory clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(10000); //单位为ms 建立连接超时
        clientHttpRequestFactory.setReadTimeout(10000); //单位为ms 建立连接成功后 从服务器读取超时
        client.setRequestFactory(clientHttpRequestFactory);
        if (HttpMethod.GET.equals(method)){
            // http get 请求
            headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
            HttpEntity<String> requestEntity = new HttpEntity<>(headers);
            // 链接与参数合并组成新的地址
            URI uri = URIBuilder.fromUri(url).queryParams(params).build();
            // 执行HTTP请求
            response = client.exchange(uri, HttpMethod.GET, requestEntity, String.class);
        }else {
            // 除 get 以外的其它请求，提交方式都是表单提交
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
            // 执行HTTP请求
            response = client.exchange(url, method, requestEntity, String.class);
        }
        return response.getBody();
    }
}
