package com.xinyan.spider.isp.common.utils;

/**
 * 日志跟踪ID
 *
 * @author yyj
 * @version 1.0 2016-7-9
 */
public class SessionUtil {

    /**
     * 为每个请求生成唯一的sessionId
     *
     * @return
     */
    public static String getSessionId() {
        return System.currentTimeMillis() + "-" + (int) ((Math.random() * 10000));
    }

}
