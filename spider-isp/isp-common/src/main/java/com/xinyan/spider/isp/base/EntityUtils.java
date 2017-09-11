package com.xinyan.spider.isp.base;

import com.gargoylesoftware.htmlunit.util.NameValuePair;

import java.util.List;

/**
 * EntityUtils
 *
 * @author Yu Yangjun
 * @date 2016/7/3
 */
public class EntityUtils {

    /**
     * requst params
     *
     * @param params
     * @return
     */
    public static String toString(List<NameValuePair> params){
        StringBuilder paramStr=new StringBuilder();
        int i=0;
        for(NameValuePair nameValuePair:params){
            if(i++>0){
                paramStr.append("&");
            }
            paramStr.append(nameValuePair.getName()).append("=").append(nameValuePair.getValue());
        }
        return paramStr.toString();
    }
}
