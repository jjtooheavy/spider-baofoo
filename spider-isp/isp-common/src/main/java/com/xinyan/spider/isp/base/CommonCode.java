package com.xinyan.spider.isp.base;

import java.util.HashSet;
import java.util.Set;

/**
 * CommonCode
 *
 * @author Yu Yangjun
 * @date 2016/7/6
 */
public class CommonCode {
    private static Set<String> areaSet=new HashSet<String>();
    private static Set<String> bizTypeSet=new HashSet<String>();
    private static Set<String> accTypeSet=new HashSet<String>();
    static
    {
        areaSet.add("1100");//北京
        areaSet.add("3100");//上海
        areaSet.add("4403");//深圳
        areaSet.add("3701");//山东_济南 
        areaSet.add("4400");//广东_广州
        areaSet.add("3205");//江苏_苏州 
        areaSet.add("5000");//重庆
        areaSet.add("2201");//吉林－长春
        areaSet.add("3501");//福建－福州
        areaSet.add("3401");//安徽－合肥
        areaSet.add("4501");//广西－南宁
        areaSet.add("4103");//河南－洛阳
        areaSet.add("4201");//湖北－武汉
        areaSet.add("3300");//浙江
        areaSet.add("2100");//辽宁
        areaSet.add("2300");//黑龙江
        areaSet.add("5200");//贵州
    }

    static
    {
        bizTypeSet.add("housefund");//公积金
        bizTypeSet.add("socialsecurity");//社保
        bizTypeSet.add("education");//学信网
    }

    static
    {
        accTypeSet.add("1");//身份证
        accTypeSet.add("2");//个人登记号
        accTypeSet.add("3");//用户名
        accTypeSet.add("4");//公积金
        accTypeSet.add("5");//联名卡
    }

    public static boolean containsArea(String code){
        boolean flag=false;
        if(areaSet.contains(code)){
            flag=true;
        }
        return flag;
    }

    public static boolean containsBizType(String code){
        boolean flag=false;
        if(bizTypeSet.contains(code)){
            flag=true;
        }
        return flag;
    }

    public static boolean containsAccType(String code){
        boolean flag=false;
        if(accTypeSet.contains(code)){
            flag=true;
        }
        return flag;
    }
}
