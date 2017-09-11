/*
 * Copyright 2010 by IPS. Floor 3,Universal Industrial Building, 
 * Tian Yaoqiao Road 1178,Shanghai, P.R. China，200300. All rights reserved.
 *
 * This software is the confidential and proprietary information of IPS
 * ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with IPS.
 */
package com.xinyan.spider.isp.common.utils;

import java.io.InputStream;

import sun.misc.BASE64Encoder;

/**
 * Base64
 *
 * @author yyj
 * @version 1.0 13-5-18
 */
public class Base64 {

    /**
     * decode
     *
     * @param text
     * @return
     */
    public static String decode(String text) {
        return new String(org.apache.commons.codec.binary.Base64.decodeBase64(text));
    }

    public static String decode(byte[] binaryData) {
        return new String(org.apache.commons.codec.binary.Base64.decodeBase64(binaryData));
    }

    public static byte[] decodeByte(String text) {
        return org.apache.commons.codec.binary.Base64.decodeBase64(text);
    }

    public static byte[] decodeByte(String text,String charset) {
        return org.apache.commons.codec.binary.Base64.decodeBase64(StringUtils.getBytes(text, charset));
    }


    /**
     * decode
     *
     * @param text
     * @param charset
     * @return
     */
    public static String decode(String text,String charset) {
        return StringUtils.getString(org.apache.commons.codec.binary.Base64.decodeBase64(text),charset);
    }

    /**
     * encode
     *
     * @param text
     * @return
     */
    public static String encode(String text) {
        return new String(org.apache.commons.codec.binary.Base64.encodeBase64(StringUtils.getBytes(text)));
    }

    public static String encode(byte[] binaryData) {
        return new String(org.apache.commons.codec.binary.Base64.encodeBase64(binaryData));
    }

    /**
     * encode
     *
     * @param text
     * @param charset
     * @return
     */
    public static String encode(String text,String charset) {
        return new String(org.apache.commons.codec.binary.Base64.encodeBase64(StringUtils.getBytes(text, charset)));
    }

    /**
     * isBase64
     *
     * @param text
     * @return
     */
    public static boolean isBase64(String text){
        return org.apache.commons.codec.binary.Base64.isBase64(text);
    }

    /**
     * isBase64
     *
     * @param binaryData
     * @return
     */
    public static boolean isBase64(byte[] binaryData){
        return org.apache.commons.codec.binary.Base64.isBase64(binaryData);
    }

    /**
     * 通过读取图片流转BASE64
     * @param imgInputStream
     * @return
     */

    public static String getBase64ByImgInputStream(InputStream imgInputStream) {// 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        byte[] data = null;
        // 读取图片字节数组
        try {
            data = new byte[imgInputStream.available()];
            imgInputStream.read(data);
            imgInputStream.close();
        } catch (Exception e) {
        }
        // 对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);// 返回Base64编码过的字节数组字符串
    }


}
