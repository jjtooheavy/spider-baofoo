package com.xinyan.spider.isp.common.utils;

import java.util.UUID;

/**
 * Created by YuYangjun on 2016/6/28.
 */
public class IdentityUtils {

    /**
     * 生成uuid
     * @return
     */
    public static String getUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replace("-","");
    }
}
