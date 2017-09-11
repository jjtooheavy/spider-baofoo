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

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;


/**
 * DES
 *
 * @author yyj
 * @version 1.0 13-6-5
 */
public class DesUtils {

    /**
     * encryptDES
     *
     * @param encryptString
     * @param encryptKey
     * @param iv
     * @return
     */
    public static String encryptDES(String encryptString, String encryptKey,String iv)  {
        try{
            SecretKeySpec key = new SecretKeySpec(encryptKey.getBytes("UTF-8"), "DES");
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedData = cipher.doFinal(encryptString.getBytes("UTF-8"));
            return Hex.encodeHexString(encryptedData);
        }catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /**
     * decryptDES
     *
     * @param decryptString
     * @param decryptKey
     * @param iv
     * @return
     */
    public static String decryptDES(String decryptString, String decryptKey,String iv)  {
        try{
            byte[] byteMi = Base64.decodeByte(decryptString,"UTF-8");
            IvParameterSpec zeroIv = new IvParameterSpec(iv.getBytes("UTF-8"));
            SecretKeySpec key = new SecretKeySpec(decryptKey.getBytes("UTF-8"), "DES");
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
            byte decryptedData[] = cipher.doFinal(byteMi);
            return new String(decryptedData,"UTF-8");
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /**
     * encrypt3DES
     *
     * @param encryptString
     * @param encryptKey
     * @param iv
     * @return
     */
    public static String encrypt3DES(String encryptString, String encryptKey,String iv)  {
        try{
            SecretKeySpec key = new SecretKeySpec(encryptKey.getBytes("UTF-8"), "DESede");
            Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedData = cipher.doFinal(encryptString.getBytes("UTF-8"));
            return Hex.encodeHexString(encryptedData);
        }catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /**
     * decrypt3DES
     *
     * @param decryptString
     * @param decryptKey
     * @param iv
     * @return
     */
    public static String decrypt3DES(String decryptString, String decryptKey,String iv) {
        try{
            byte[] byteMi = Base64.decodeByte(decryptString,"UTF-8");
            IvParameterSpec zeroIv = new IvParameterSpec(iv.getBytes("UTF-8"));
            SecretKeySpec key = new SecretKeySpec(decryptKey.getBytes("UTF-8"), "DESede");
            Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
            byte decryptedData[] = cipher.doFinal(byteMi);
            return new String(decryptedData,"UTF-8");
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }


}
