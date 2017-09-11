package com.xinyan.spider.isp.common.utils;

/**
 * @Author：WenqiangPu
 * @Description
 * @Date：Created in 19:47 2017/9/7
 * @Modified By：
 */
public class UnitTrans {
    public static String tran(String unit){
        if(StringUtils.isNotEmpty(unit)){
            if("03".equals(unit)){
                return "KB";
            }else{
                return "分";
            }
        }
        return "";
    }
}
