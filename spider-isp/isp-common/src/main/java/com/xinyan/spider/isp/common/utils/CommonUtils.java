package com.xinyan.spider.isp.common.utils;

import java.util.Properties;


/**
 * CommonUtils
 *
 * @author yyj
 * @version 1.0 16-6-18
 */
public class CommonUtils {

    /**
     * 判断是否windows系统
     *
     * @return
     */
    public static boolean isWindowsOs(){
        boolean flag=false;
        Properties prop = System.getProperties();
        String os = prop.getProperty("os.name");
        if(os.startsWith("win") || os.startsWith("Win")){
            flag=true;
        }
        return flag;
    }
}
