package com.xinyan.spider.isp.common.utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * HttpUtils
 *
 * @author Yu Yangjun
 * @date 2016/7/7
 */
public class HttpUtils {
	
	 public static HttpResponse get(String url) {
	        CloseableHttpClient httpClient = HttpClients.createDefault();
	        CloseableHttpResponse response = null;
	        HttpGet method=new HttpGet(url);
	        try{
	            response = httpClient.execute(method);
	        }catch (Exception ex){
	            throw new RuntimeException(ex);
	        }
	        return response;
	}
}
