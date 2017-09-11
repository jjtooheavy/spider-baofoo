package com.xinyan.spider.isp.common.utils;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * Md5Utils
 *
 * @author yyj
 * @version 1.0 16-6-18
 */
public class Md5Utils {
    /**
     * sign
     *
     * @param text
     * @return
     */
    public static String sign(String text) {
        return DigestUtils.md5Hex(text);
    }

    /**
     * sign
     *
     * @param text
     * @param charset
     * @return
     */
    public static String sign(String text, String charset) {
        return DigestUtils.md5Hex(StringUtils.getBytes(text, charset));
    }


    /**
     * verify
     *
     * @param text
     * @param sign
     * @return
     */
    public static boolean verify(String text, String sign) {
        String mySign = DigestUtils.md5Hex(text);
        if (mySign.equals(sign)) {
            return true;
        }
        return false;
    }

    /**
     * verify
     *
     * @param text
     * @param sign
     * @param charset
     * @return
     */
    public static boolean verify(String text, String sign, String charset) {
        String mySign = DigestUtils.md5Hex(StringUtils.getBytes(text, charset));
        if (mySign.equals(sign)) {
            return true;
        }
        return false;
    }
}
