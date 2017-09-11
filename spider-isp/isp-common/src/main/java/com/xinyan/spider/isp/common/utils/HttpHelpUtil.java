package com.xinyan.spider.isp.common.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gargoylesoftware.htmlunit.HttpMethod;

/**
 * Created by BF100395 on 2017/2/28.
 */
public class HttpHelpUtil {
    public  static Logger log=  LoggerFactory.getLogger(HttpHelpUtil.class);

    public static HttpPost buildPost(String url, HttpContentType httpContentType, Map<String,String> header, Map<String,Object> body){
        HttpPost httpPost = new HttpPost(url);
        buildHeader(header,httpPost);
        if(HttpContentType.JSON.getCode().equals(httpContentType.getCode())){
            StringEntity entity =  buildBody(body);
            entity.setContentEncoding("UTF-8");
            entity.setContentType("application/json");
            httpPost.setEntity(entity);
        }else {
            UrlEncodedFormEntity entity =  buildParams(body);
            entity.setContentEncoding("UTF-8");
            httpPost.setEntity(entity);
        }
        return httpPost;
    }

    public static HttpGet buildGet(String url, Map<String,String> header, Map<String,Object> body){
        HttpGet httpGet = new HttpGet(url);
        buildHeader(header,httpGet);
        return httpGet;
    }

    /**
     * 组装http头信息
     * @param header 头信息参数
     * @param httpRequest http请求（GET or POST）
     */
    public static void buildHeader(Map<String,String> header, HttpRequestBase httpRequest){
        if(header==null||header.isEmpty()){
            return;
        }
        for (String key:header.keySet()){
            httpRequest.setHeader(key,header.get(key));
        }
    }



    public static String buildGetUrl(String url,Map<String,Object> body) {
        if(body==null||body.isEmpty()){
            return url;
        }
        String params = "";
        try {
            for (String key:body.keySet()) {
                if(params.length()>0) {
                    params=params+"&";
                }
                params = key + "=" + URLEncoder.encode((String)body.get(key), "utf-8");
            }
        }catch (UnsupportedEncodingException e){

            log.info("url参数转换失败");

            return url;
        }
        return url+"?"+params;
    }

    /**
     * 获取body体信息串
     * @param body body参数
     * @return
     */
    public static StringEntity buildBody(Map<String,Object> body){
        if(body==null||body.isEmpty()){
            return new StringEntity("","utf-8");
        }
        JSONObject requestJson = new JSONObject();
        for (String key:body.keySet()){
            requestJson.put(key,body.get(key));
        }
        return new StringEntity(requestJson.toString(),"utf-8");
    }

    /**
     * 获取body体信息串
     * @param params params参数
     * @return
     */
    public static UrlEncodedFormEntity buildParams(Map<String,Object> params){
        if(params==null||params.isEmpty()){
            return null;
        }
        List<BasicNameValuePair> qParams = new ArrayList<>();
        for (String key:params.keySet()){
            qParams.add(new BasicNameValuePair(key, (String) params.get(key)));
        }
        UrlEncodedFormEntity entity = null;
        try {
            entity = new UrlEncodedFormEntity(qParams, "utf-8");
        } catch (UnsupportedEncodingException e) {
            log.error("参数封装异常:{}",e);
        }
        return entity;
    }

    /**
     * 带参url处理
     * @param pathUrl 带参url
     * @param pathParams 需要替换的参数
     * @return
     */
    public static String buildPathUrl(String pathUrl,Map<String,String> pathParams){
        log.info("处理前url:{}",pathUrl);
        String pathUrlResult = pathUrl;
        if(pathParams==null||pathParams.isEmpty()){
            return pathUrlResult;
        }
        for (String key:pathParams.keySet()){
            String replaceKey = "{"+key+"}";
            pathUrlResult = pathUrlResult.replace(replaceKey,pathParams.get(key));
        }
        log.info("处理后url:{}",pathUrlResult);
        return pathUrlResult;
    }

    public static String execute(HttpRequestBase httpRequest){
        CloseableHttpClient client = HttpClientBuilder.create().build();
        try {
            HttpResponse response = client.execute(httpRequest);
            if(HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
                return EntityUtils.toString(response.getEntity());
            }else {
                log.error("http响应code：{}",response.getStatusLine().getStatusCode());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static HttpEntity excuteEntity(HttpRequestBase httpRequest){
        CloseableHttpClient client = HttpClientBuilder.create().build();
        try {
            HttpResponse response = client.execute(httpRequest);
            if(HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
                return response.getEntity();
            }else {
                log.error("http响应code：{}",response.getStatusLine().getStatusCode());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 执行http请求
     * @param httpMethod GET OR POST
     * @param url http路径
     * @param httpContentType 请求方式 json form
     * @param header http头参数
     * @param body body or param 参数
     * @return 响应JSON内容
     */
    public static JSONObject execute(HttpMethod httpMethod, String url, HttpContentType httpContentType, Map<String,String> header, Map<String,Object> body){
        if(HttpMethod.POST == httpMethod){
            HttpPost httpPost = buildPost(url, httpContentType, header, body);
            String result = execute(httpPost);
            if(null == result){
            	return null;
            }else if(StringUtils.isEmpty(result)){
            	return JSON.parseObject("{'taskType':''}");
            }else {
            	return JSON.parseObject(result);
            }
        }else {
            HttpGet httpGet = buildGet(url, header,body);
            String result = execute(httpGet);
            if(null == result){
            	return null;
            }else if(StringUtils.isEmpty(result)){
            	return JSON.parseObject("{'taskType':''}");
            }else {
            	return JSON.parseObject(result);
            }
        }
    }

    public static void main(String[] args) {
        String url = "http://tgw.baofoo.com/{data}/{bank}/v2/cards/all/{trade_no}";
        Map<String,String> map = new HashMap<String, String>();
        map.put("data","1231");
        map.put("bank","asdasd");
        map.put("trade_no","123123123123");
        String resultUrl = buildPathUrl(url,map);
        System.out.println(resultUrl);
    }
}
