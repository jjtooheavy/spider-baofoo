package com.xinyan.spider.isp.common.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description:格式化数据
 * @author: york
 * @date: 2016-09-26 18:26
 * @version: v1.0
 */
public class FormatUtils {

	protected static Logger logger= LoggerFactory.getLogger(FormatUtils.class);


    /**
     * @Description:格式化钱相关数据、
     * @param @param money
     * @return  格式：【100/100.00】
     * */
    public static String formatMoney(String money){

        try{
            
            if(StringUtils.isBlank(money)){
                return "";
            }

            String retMoney = money;
            retMoney = retMoney.replaceAll(" ", "");
            retMoney = retMoney.replaceAll(",", "");
            retMoney = retMoney.replaceAll("元/月", "");
            retMoney = retMoney.replaceAll("￥", "");

            //去掉【元】
            if(retMoney.contains("元")){
                retMoney = retMoney.replaceAll("元", "");
            }

            //去掉【万】
            if(retMoney.contains("万")){
                retMoney = retMoney.replaceAll("万", "");

                //去掉【万】以后判断是否是浮点类型、
                if(StringUtils.isFloat(retMoney)){
                    BigDecimal bigDecimalA =new BigDecimal(retMoney);
                    BigDecimal bigDecimalB = new BigDecimal("10000");
                    bigDecimalA = bigDecimalA.multiply(bigDecimalB);
                    retMoney = bigDecimalA.toString();
                }
                //记录错误转化日志
                else{
                    return money;
                }
            }
            retMoney = moneyZerofill(retMoney);
            return retMoney;

        }catch (Exception ex){
        	logger.info("格式化转换异常：" + money);
            return  money;
            
        }
     
    }

    /**
     * @Description:格式化日期
     * @param @param retDate
     * @param @param bDay
     *
     * @return
     *      bDay：false ==》格式：【yyyy-mm】
     *      bDay：true ==》格式：【yyyy-mm-dd】
     * */
    public static String formatDate(String date, boolean bDay){

        try{
            
            if(StringUtils.isBlank(date)){
                return "";
            }
            
            String retDate = date.trim();
            retDate = retDate.replace("日", " 日").replace("时", "").replace("分","").replace("秒","");
              
            //mm[-/]dd[ HH:mm:ss]
            if(Pattern.compile("^\\d{2}[-/]\\d{2}[ \\d{2}:\\d{2}:\\d{2}]").matcher(retDate).find()){

                retDate = retDate.replaceAll("/", "-");
                String year = DateUtils.getCurrentYear();
                if(bDay) retDate = year + "-" + retDate.substring(0,5);
                else  retDate = year + "-" + retDate.substring(0,2);

            }
            else if(Pattern.compile("^\\d{4}年[\\d{1}]$").matcher(retDate).find()){
                if(bDay) retDate = retDate.substring(0,4) + "-0"+ retDate.substring(5,6)+ "-01";
                else retDate = retDate.substring(0,4) + "-0"+ retDate.substring(5,6);
            }
            //yyyy[-/年]m[-/月][dd[日][ HH:mm:ss]]
            else if(Pattern.compile("^\\d{4}[-/年]\\d{1,2}[[-/月]\\d{1,2}[日]?[ \\d{2}:\\d{2}:\\d{2}]?]?").matcher(retDate).find()){
                if(retDate.contains("年") && retDate.contains("月")){

                    retDate = retDate.replace("年", "-").replace("日", "");

                    //判断月是否是最后一个字符
                    boolean bLastMonth = retDate.indexOf("月") == retDate.length()-1 ? true :false;
                    if(bLastMonth){
                        retDate = retDate.replace("月", "");
                    }
                    else{
                        retDate = retDate.replace("月", "-");
                    }

                    //2011-01|2011-1 
                    if(retDate.length() < 8){
                        retDate = retDate + "-01";
                    }
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    retDate = format.format(format.parse(retDate));
                }

                if(bDay) retDate =retDate.length() >= 10 ? retDate.substring(0,4) + "-"+ retDate.substring(5,7)  + "-" + retDate.substring(8,10):retDate.substring(0,4) + "-"+ retDate.substring(5,7)  + "-01";
                else  retDate = retDate.substring(0,4) + "-"+ retDate.substring(5,7) ;

            }
            //yyyymm[dd]
            else if(Pattern.compile("^\\d{6}[\\d{2}]*$").matcher(retDate).find()){
                if(bDay) retDate = retDate.length() >= 8 ? retDate.substring(0,4) + "-"+ retDate.substring(4,6)+ "-"+ retDate.substring(6,8): retDate.substring(0,4) + "-"+ retDate.substring(4,6)+ "-01";
                else retDate = retDate.substring(0,4) + "-"+ retDate.substring(4,6);
            }

            //判断格式是否匹配、不匹配则记录日志文件
            if(!Pattern.compile("^\\d{4}-\\d{2}-\\d{2}").matcher(retDate).find() && !Pattern.compile("^\\d{4}-\\d{2}").matcher(retDate).find()){

                //记录日志
                return date;
            }

            return retDate;

        }catch (Exception ex){
            logger.info("格式化转换异常：" + date);
            return  date;
        }
    }

    /**
     * 保留二位小数
     * @param money
     * @return
     */
    public static String moneyZerofill(String money){
    	if(StringUtils.isBlank(money)){
             return "";
        }
    	 
    	try {
    		money = money.replace(",", "");
        	DecimalFormat df=new DecimalFormat("######################.00");
     		BigDecimal bd=new BigDecimal(money);
     		String val=df.format(bd);
     		return val.startsWith(".")?"0"+val:val;
		} catch (Exception e) {
			 logger.info("格式化转换异常：" + money);
	         return  money;
		}
    	
    }

    /**
     * 时间转化统一格式【yyyy-MM-dd hh:mm:ss】
     * @param time
     * @return
     */
    public static String formatDateTime(String time) {

        String retValue = "";

        if(StringUtils.isBlank(time)) {
            return "";
        }
        try{
            time = time.replaceAll("年","-").replaceAll("月","-").replaceAll("日","").replaceAll("/","-");
            if(Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$").matcher(time).find()){
                retValue = time + " 00:00:00";
            }
            else if(Pattern.compile("^\\d{8}$").matcher(time).find()){
                retValue = time.substring(0,4) + "-" + time.substring(4,6)  + "-" + time.substring(6,8) +  " 00:00:00";
            }else if(Pattern.compile("^\\d{14}$").matcher(time).find()){
                retValue = time.substring(0,4) + "-" + time.substring(4,6)  + "-" + time.substring(6,8) + " "+ time.substring(8,10)
                        + ":"+ time.substring(10,12)+ ":"+ time.substring(12,14);
            }

            //检验转化是否成功
            if(!Pattern.compile("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$").matcher(retValue).find()){
                return time;
            }
        }
        catch (Exception e) {
            logger.info("格式化转换异常：" + time);
            return  time;
        }
        return retValue;
    }

    /**
     * 时间转化成秒
     * @param time
     * @return
     */
    public static String formatSecondLength(String time) {

        String retValue = "";

    	if(StringUtils.isBlank(time)) {
    		return "";
    	}
        try {
            time = time.replaceAll("小时", ":").replaceAll("时", ":").replaceAll("分", ":").replaceAll("秒", "");
            if (Pattern.compile("^\\d+:\\d+:\\d+$").matcher(time).find()) {
                String[] arr = RegexUtils.matchMutiValue("^(.*?):(.*?):(.*?)$", time);
                if (arr.length == 3) {
                    retValue = String.valueOf(Integer.parseInt(arr[0]) * 3600 + Integer.parseInt(arr[1]) * 60 + Integer.parseInt(arr[2]));

                }
            } else if (Pattern.compile("^\\d+:\\d+$").matcher(time).find()) {
                String[] arr = RegexUtils.matchMutiValue("^(.*?):(.*?)$", time);
                if (arr.length == 2) {
                    retValue = String.valueOf(Integer.parseInt(arr[0]) * 60 + Integer.parseInt(arr[1]));
                }
            } else if (Pattern.compile("\\d+$").matcher(time).find()) {
                String[] arr = RegexUtils.matchMutiValue("^(.*?)$", time);
                if (arr.length == 1) {
                    retValue = String.valueOf(Integer.parseInt(arr[0]));
                }
            }

            //检验转化是否成功
            if (!Pattern.compile("\\d+$").matcher(retValue).find()) {
                return time;
            }
        }

        catch (Exception e) {
            logger.info("格式化转换异常：" + time);
            return  time;
        }
        return retValue;
    }
}
