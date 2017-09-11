package com.xinyan.spider.isp.base;

import com.gargoylesoftware.htmlunit.WebClient;

/**
 * 页面处理 processor
 */
public interface PageProcessor {

    /**
     * 登陆
     * @param webClient
     * @param context
     * @return
     */
    Result login(WebClient webClient, Context context);

    /**
     * 抓取和解析
     * @param webClient
     * @param context
     * @return
     */
    Result crawler(WebClient webClient, Context context);

}
