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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.UnsupportedEncodingException;
import java.io.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * StringUtils
 *
 * @author yyj
 * @version 1.0 16-6-18
 */
public class StringUtils {

    public static final String EMPTY = "";

    /**
     * 判断字符串是否不为空串
     *
     * @param str
     * @return
     */
    public static boolean isNotNULL(String str) {
        return str == null ? false : true;
    }

    /**
     * 判断字符串是否不为空串
     *
     * @param str
     * @return
     */
    public static boolean isNULL(String str) {
        return str == null ? true : false;
    }

    /**
     * 判断字符串是否为空串(null or "")
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        return ((str == null) || (str.length() == 0));
    }

    /**
     * 判断字符串是否不为空串
     *
     * @param str
     * @return
     */
    public static boolean isNotEmpty(String str) {
        return (!(isEmpty(str)));
    }

    /**
     * 判断字符串是否为空白串
     *
     * @param str
     * @return
     */
    public static boolean isBlank(String str) {
        int strLen;
        if ((str == null) || ((strLen = str.length()) == 0))
            return true;
        for (int i = 0; i < strLen; ++i) {
            if (!(Character.isWhitespace(str.charAt(i)))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断字符串是否不为空白串
     *
     * @param str
     * @return
     */
    public static boolean isNotBlank(String str) {
        return (!(isBlank(str)));
    }
    
    /**
     * @Description:判断字符串不在长度
     * @param @param str
     * @param @return 
     * @throws
     */
    public static boolean isNotLeng(String str, int leng) {
    	return (!(isLeng(str, leng)));
    }
    
    public static boolean isNotLeng(String str, int begLen, int leng) {
    	return (!(isLeng(str,begLen, leng)));
    }
    
    /**
     * @Description:判断字符串在长度里
     * @param @param str
     * @param @return 
     * @throws
     */
    public static boolean isLeng(String str, int leng) {
    	if(isBlank(str)) {
    		return (0 == leng);
    	}
    	
    	return (length(trim(str)) <= leng);
    }
    
    /**
     * @Description:字符串实际长度
     * @param @param str
     * @param @return 
     * @throws
     */
    public static int length(String str) {
    	
    	if(isBlank(str)) {
    		return 0;
    	}
    	
    	byte[] buf = str.getBytes();
    	int len = str.length();
		
		int count = 0;
		int i = 0;
		for (i = len - 1; i >= 0; i--) {
			if (buf[i] < 0) {
				count++;
			} else {
				break;
			}
		}
		
		return (len + count);
    }
    
    /**
     * @Description:判断字符串在长度里
     * @param @param str
     * @param @param begLen
     * @param @param endLen
     * @param @return 
     * @throws
     */
    public static boolean isLeng(String str, int begLen, int endLen) {
    	if(isBlank(str)) {
    		return (0 == endLen);
    	}
    	
    	if (begLen < 0) {
			 return false;
	    }
	     
	    if ((endLen - begLen) < 0) {
	    	 return false;
	    }
    	
	     str = trim(str);
    	return ((length(str) >= begLen) && (length(str) <= endLen));
    }

    /**
     * trim字符串
     *
     * @param str
     * @return
     */
    public static String trim(String str) {
        return ((str == null) ? null : str.trim());
    }

    /**
     * trim字符串,如果为空串则为null
     *
     * @param str
     * @return
     */
    public static String trimToNull(String str) {
        String ts = trim(str);
        return ((isEmpty(ts)) ? null : ts);
    }

    /**
     * trim字符串,如果为空则变为空串
     *
     * @param str
     * @return
     */
    public static String trimToEmpty(String str) {
        return ((str == null) ? "" : str.trim());
    }

    /**
     * getBytes
     *
     * @param content
     * @param charset
     * @return
     */
    public static byte[] getBytes(String content, String charset) {
        if (StringUtils.isNULL(content)) {
            content = EMPTY;
        }
        if (StringUtils.isBlank(charset)) {
            throw new IllegalArgumentException("charset can not null");
        }
        try {
            return content.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("charset is not valid,charset is:" + charset);
        }
    }

    /**
     * getBytes
     *
     * @param content
     * @return
     */
    public static byte[] getBytes(String content) {
        if (StringUtils.isNULL(content)) {
            content = EMPTY;
        }
        return content.getBytes();
    }

    public static String getString(byte[] binaryData, String charset) {
        try {
            return new String(binaryData,charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("charset is not valid,charset is:" + charset);
        }
    }

    /**
     * bean 转化为String
     *
     * @param bean
     * @return
     */
    public static String beanToString(Object bean){
        return ToStringBuilder.reflectionToString(bean, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    /**
     * bean转化为json
     *
     * @param bean
     * @return
     * @throws Exception
     */
    public static String beanToJson(Object bean) {
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,true);  
            return JsonFormatTool.formatJson(objectMapper.writeValueAsString(bean));
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /**
     * 字符串填充
     *
     * @param a
     * @param num
     * @return
     */
    public static String fill(char a,int num){
        StringBuilder builder=new StringBuilder();
        for(int i=0;i<num;i++){
            builder.append(a);
        }
        return builder.toString();
    }

    public static String capitalize(final String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }

        final char firstChar = str.charAt(0);
        if (Character.isTitleCase(firstChar)) {
            // already capitalized
            return str;
        }

        return new StringBuilder(strLen)
                .append(Character.toTitleCase(firstChar))
                .append(str.substring(1))
                .toString();
    }
    
	/**
	 * 截断字符串
	 * 
	 * @param str
	 * @param len
	 * @param suffix
	 * @return
	 */
	public static String truncate(String str, int beginIndex, int endIndex) {
		
		 if(isEmpty(str)) {
			 return str;
		 }
		 if (beginIndex < 0) {
			 return str;
	     }
	     if (endIndex > str.length()) {
	    	 return str;
	     }
	     
	     if ((endIndex - beginIndex) < 0) {
	    	 return str;
	     }
		
		return str.substring(beginIndex, endIndex);
	}
	
	/**
	 * @Description:集合转换String
	 * @param @param list
	 * @param @return 
	 * @throws
	 */
	public static String listConverString(List<String> list) {
		StringBuffer sb = new StringBuffer();
		if(list == null || list.isEmpty()) {
			return "";
		}
		for (String string : list) {
			if(sb.length() > 0 && StringUtils.isNotBlank(string)) {
				sb.append(",");
			}
			if(StringUtils.isNotBlank(string)) {
				sb.append(string);
			}
		}
		return sb.toString();
	}

    /**
     * 源串是否包含目标字符
     *
     * @param source
     * @param destination
     * @return
     */
    public static boolean contains(String source,String destination){
        boolean flag=false;
        if(isBlank(source)||isBlank(destination)){
            return flag;
        }
        if(source.contains(destination)){
            flag=true;
        }
        return flag;
    }
    
    /**
     * @Description:替换文本
     * @param @param html
     * @param @return 
     * @throws
     */
    public static String replaceHtml(String html) {
    	if(StringUtils.isBlank(html)) {
    		return "";
    	}
    	
        html = html.replaceAll("//\\]\\]\\>", "");
        html = html.replaceAll("//\\<\\!\\[CDATA\\[", "");
        html = html.replaceAll("\\<script.*?\\>|\\</script\\>", "");

        if(StringUtils.isBlank(html)) {
    		return "";
    	}
        
        html = html.trim();
        return html;
    }

    /**
     * @Description:判断某个字符串为数字
     * @param @param html
     * @param @return
     * @throws
     * */
    public static boolean isNumeric(String str){
        for (int i = str.length();--i>=0;){
            if (!Character.isDigit(str.charAt(i))){
                return false;
            }
        }
        return true;
    }

    /**
     * @Description:判断某个字符串为数字或者是浮点数
     * @param @param html
     * @param @return
     * @throws
     * */
    public static boolean isFloat(String html) {

        if(StringUtils.isBlank(html)) {
            return false;
        }

        return html.matches("[0-9]+\\.?[0-9]*");
    }


    /**
     * @Description:验证手机号码
     * @param @param phone
     * @param @return 
     * @throws
     */
    public static boolean checkPhone(String phone) {
    	String reg = "^[1][3,4,5,7,8][0-9]{9}$";
    	if(RegexChk.startCheck(reg, phone)) {
    		return true;
    	}
    	return false;
    }
    
    /**
	 * 身份证校验
	 * 
	 * @return
	 */
	public static boolean checkIDCard(String value) {
		String regular = "";

		if (StringUtils.isBlank(value)) {
			return false;
		} else if (15 == value.length()) {// 15位身份证正则表达式
			regular = "^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{2}[xX\\d]$";
			return getCheckIDCard(value, regular);
		} else if (18 == value.length()) {// 18位身份证正则表达式
			regular = "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}[xX\\d]$";
			return getCheckIDCard(value, regular);
		} else {
			return false;
		}
		
	}
	
	private static boolean getCheckIDCard(String value, String regular) {

		Pattern pattern = Pattern.compile(regular);
		Matcher matcher = pattern.matcher(value);
		if (!matcher.matches()) {
			return false;
		}
		return true;
	}
	
	
    /** 
     * 校验银行卡卡号 
     * @param cardId 
     * @return 
     */  
    public static boolean checkBankCard(String cardId) {  
          char bit = getBankCardCheckCode(cardId.substring(0, cardId.length() - 1));  
          if(bit == 'N'){  
              return false;  
          }  
          return cardId.charAt(cardId.length() - 1) == bit;  
    }  
     
    /** 
     * 从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位 
     * @param nonCheckCodeCardId 
     * @return 
     */  
    public static char getBankCardCheckCode(String nonCheckCodeCardId){  
        if(nonCheckCodeCardId == null || nonCheckCodeCardId.trim().length() == 0  
                || !nonCheckCodeCardId.matches("\\d+")) {  
         //如果传的不是数据返回N  
            return 'N';  
        }  
        char[] chs = nonCheckCodeCardId.trim().toCharArray();  
        int luhmSum = 0;  
        for(int i = chs.length - 1, j = 0; i >= 0; i--, j++) {  
            int k = chs[i] - '0';  
            if(j % 2 == 0) {  
                k *= 2;  
                k = k / 10 + k % 10;  
            }  
            luhmSum += k;             
        }  
        return (luhmSum % 10 == 0) ? '0' : (char)((10 - luhmSum % 10) + '0');  
    }

    /**
     * 去除HTml标签
     * @param htmlStr
     * @return
     */
    public static String delHTMLTag(String htmlStr){
        String regEx_script="<script[^>]*?>[\\s\\S]*?<\\/script>"; //定义script的正则表达式
        String regEx_style="<style[^>]*?>[\\s\\S]*?<\\/style>"; //定义style的正则表达式
        String regEx_html="<[^>]+>"; //定义HTML标签的正则表达式

        Pattern p_script=Pattern.compile(regEx_script,Pattern.CASE_INSENSITIVE);
        Matcher m_script=p_script.matcher(htmlStr);
        htmlStr=m_script.replaceAll(""); //过滤script标签

        Pattern p_style=Pattern.compile(regEx_style,Pattern.CASE_INSENSITIVE);
        Matcher m_style=p_style.matcher(htmlStr);
        htmlStr=m_style.replaceAll(""); //过滤style标签

        Pattern p_html=Pattern.compile(regEx_html,Pattern.CASE_INSENSITIVE);
        Matcher m_html=p_html.matcher(htmlStr);
        htmlStr=m_html.replaceAll(""); //过滤html标签

        return htmlStr.trim(); //返回文本字符串
    }


    /**
     * InputStream转String
     * @param is
     * @return
     */
    public static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {

        } finally {
            try {
                is.close();
            } catch (IOException e) {

            }
        }

        return sb.toString();
    }

    /**
     * String[]转String
     * @param arr
     * @return
     */
    public static String implodeStr(String[] arr, String delimiter ) {
        StringBuilder sb = new StringBuilder(arr.length*3); // StringBuilder(arr.length*3)性能比StringBuilder()高

        int offset = arr.length - 1;
        for( int i = 0; i < offset; i++ )
        {
            sb.append(arr[i]).append(delimiter);
        }
        sb.append(arr[offset]);

        return sb.toString();
    }






}
