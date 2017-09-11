package com.xinyan.spider.isp.base.http;


import com.xinyan.spider.isp.common.utils.CollectionUtil;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.List;


/**
 * Created by Yu Yangjun on 2016/7/2.
 */
public class WebClient {

    private CloseableHttpClient httpClient;

    public WebClient(){
        httpClient=HttpClientManager.getHttpClient();
    }

    /**
     * 获取页面
     * @param url
     * @return
     */
    public String getPage(String url){
       return doGet(url,null);
    }

    /**
     * 获取页面
     * @param url
     * @return
     */
    public String getPage(String url,List<NameValuePair> params){
        return doGet(url,params);
    }

    /**
     * 获取页面
     *
     * @param url
     * @return
     */
    public String getPage(String url, HttpMethod httpMethod, List<NameValuePair> params){
        if(HttpMethod.GET==httpMethod){
           return doGet(url,params);
        }else{
           return doPost(url,params);
        }
    }

    /**
     * 获取页面
     * @param url
     * @return
     */
    public byte[] getImages(String url){
        byte[] image=null;
        HttpEntity entity = null;
        CloseableHttpResponse response=null;
        HttpGet method=new HttpGet(url);
        try{
            response=httpClient.execute(method);
            if(response.getStatusLine().getStatusCode()== HttpStatus.SC_OK){
                entity=response.getEntity();
                @SuppressWarnings("resource")
				ByteArrayOutputStream os = new ByteArrayOutputStream();
                os.write(entity.getContent());
                image=os.toByteArray();
            }
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
        return image;
    }


    /**
     * 获取页面
     * @param url
     * @return
     */
    public BufferedImage getImage(String url){
        BufferedImage bufferedImage=null;
        HttpEntity entity = null;
        CloseableHttpResponse response=null;
        HttpGet method=new HttpGet(url);
        try{
            response=httpClient.execute(method);
            if(response.getStatusLine().getStatusCode()== HttpStatus.SC_OK){
                entity=response.getEntity();
                bufferedImage=ImageIO.read(entity.getContent());
            }
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
        return bufferedImage;
    }



    /**
     * get 请求
     * @param url
     * @return
     */
    private String doGet(String url,List<NameValuePair> params){
        String result="";
        HttpEntity entity = null;
        CloseableHttpResponse response=null;
        HttpGet method=new HttpGet(url);
        try{
            if(CollectionUtil.isNotEmpty(params)){
                String str = EntityUtils.toString(new UrlEncodedFormEntity(params,Consts.UTF_8));
                method.setURI(new URI(method.getURI().toString() + "?" + str));
            }
            response=httpClient.execute(method);
            if(response.getStatusLine().getStatusCode()== HttpStatus.SC_OK){
                entity=response.getEntity();
                result=EntityUtils.toString(entity);
            }
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
        return result;
    }

    /**
     * post 请求
     * @param url
     * @return
     */
    private String doPost(String url,List<NameValuePair> params){
        String result="";
        HttpEntity entity = null;
        CloseableHttpResponse response=null;
        HttpPost method=new HttpPost(url);
        try{
            if(CollectionUtil.isNotEmpty(params)){
                UrlEncodedFormEntity encodedFormEntity=new UrlEncodedFormEntity(params, Consts.UTF_8);
                method.setEntity(encodedFormEntity);
            }
            response=httpClient.execute(method);
            if(response.getStatusLine().getStatusCode()== HttpStatus.SC_OK){
                entity=response.getEntity();
                result=EntityUtils.toString(entity);
            }
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
        return result;
    }
}
