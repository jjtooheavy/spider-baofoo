package com.xinyan.spider.isp.common.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * RegexUtils
 *
 * @author Yu Yangjun
 * @date 2016/8/18
 */
public class RegexUtils {

	
    /**
     * 根据正则表达式查找匹配的值
     * @param patternStr
     * @param source
     * @return
     */
    public static String matchValue(String patternStr, String source){

        //处理由于系统不同导致换行符差异
        patternStr = opePatterStr(patternStr);

        String[] result = matchMutiValue(patternStr,source);
        if(result!=null&&result.length>0){
            return result[0].trim();
        }
        return "";
    }

    /**
     * 根据正则表达式查找匹配的组
     * @param patternStr
     * @param source
     * @return
     */
    public static String[] matchMutiValue(String patternStr, String source){

        //处理由于系统不同导致换行符差异
        patternStr = opePatterStr(patternStr);
        List<String> resultList=new ArrayList<String>();
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher=pattern.matcher(source);
        while(matcher.find()){
            for(int i=0;i<matcher.groupCount();i++){
                resultList.add(matcher.group(i+1).trim());
            }
        }
        return resultList.toArray(new String[]{});
    }

    /**
     * 根据正则表达式和指定位置查找匹配的值
     * @param patternStr
     * @param source
     * @return
     */
    public static String matchValue(String patternStr, int iPos, String source){

        //处理由于系统不同导致换行符差异
        patternStr = opePatterStr(patternStr);
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher=pattern.matcher(source);
        if(matcher.find(iPos)){
            return matcher.group(1).trim();
        }

        return "";
    }

    /**
     * 根据正则表达式查找匹配的组
     * @param patternStr
     * @param source
     * @return
     */
    public static List<List<String>> matchesMutiValue(String patternStr, String source){

        //处理由于系统不同导致换行符差异
        patternStr = opePatterStr(patternStr);

        List<List<String>> resultList=new ArrayList<>();
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher=pattern.matcher(source);
        while(matcher.find()){

            List<String> groupList=new ArrayList<>();
            for(int i=0;i<matcher.groupCount();i++){
                groupList.add(matcher.group(i+1).trim());
            }
            resultList.add(groupList);
        }
        return resultList;
    }

    /**
     * 处理由于系统不同导致换行符差异
     * @param patternStr
     * @return
     */
    private static String opePatterStr(String patternStr){

        if(StringUtils.isNotBlank(patternStr)){
            String lineSeparator = System.getProperties().getProperty("line.separator");
            String osName = System.getProperties().getProperty("os.name");
            if(!StringUtils.contains(osName, "Window")){
                patternStr = patternStr.replace("\\r\\n", lineSeparator);
            }
        }
        return  patternStr;
    }

}
