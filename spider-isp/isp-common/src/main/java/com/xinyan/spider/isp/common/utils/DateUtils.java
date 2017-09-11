package com.xinyan.spider.isp.common.utils;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间工具类
 */
public class DateUtils {

	public static final String PATTERN_YEAR = "yyyy";
	public static final String PATTERN1 = "yyyy-MM-dd";
	public static final String PATTERN2 = "yyyyMMdd";
	public static final String PATTERN3 = "yyyyMMddhhmmss";
	public static final String PATTERN4 = "MM/dd/yyyy hh:mm:ss";
	public static final String PATTERN5 = "yyyyMM";
	public static final String PATTERN6 = "yyyy-MM";
	public static final String PATTERN7 = "yyyy-MM-dd hh:mm:ss";
	/**
	 * 获取当前月份
	 * @return
	 */
	public static String getCurrentMonth2() {
		SimpleDateFormat format = new SimpleDateFormat(PATTERN6);
		return format.format(new Date());
	}
	/**
	 * 获取当前月份
	 * @return
	 */
	public static String getCurrentMonth() {
		SimpleDateFormat format = new SimpleDateFormat(PATTERN5);
		return format.format(new Date());
	}
	/**
	 * 获取当前日期
	 * @return
	 */
	public static String getCurrentDate() {
		SimpleDateFormat format = new SimpleDateFormat(PATTERN1);
		return format.format(new Date());
	}

	/**
	 * 获取当前日期
	 * @return
	 */
	public static String getCurrentDate1() {
		SimpleDateFormat format = new SimpleDateFormat(PATTERN2);
		return format.format(new Date());
	}

	/**
	 * 获取当前年
	 * @return
	 */
	public static String getCurrentYear() {
		SimpleDateFormat format = new SimpleDateFormat(PATTERN_YEAR);
		return format.format(new Date());
	}

	/**e
	 * 获取当前日期
	 * @return
	 */
	public static String getCurrentDate2() {
		SimpleDateFormat format = new SimpleDateFormat(PATTERN2);
		return format.format(new Date());
	}

	/**
	 * 获取制定毫秒数之前的日期
	 * 
	 * @param timeDiff
	 * @return
	 */
	public static String getDesignatedDate(long timeDiff) {
		SimpleDateFormat format = new SimpleDateFormat(PATTERN1);
		long nowTime = System.currentTimeMillis();
		long designTime = nowTime - timeDiff;
		return format.format(designTime);
	}

	/**
	 * 
	 * 获取前几天的日期
	 */
	public static String getPrefixDate(String count) {
		Calendar cal = Calendar.getInstance();
		int day = 0 - Integer.parseInt(count);
		cal.add(Calendar.DATE, day); // int amount 代表天数
		Date datNew = cal.getTime();
		SimpleDateFormat format = new SimpleDateFormat(PATTERN1);
		return format.format(datNew);
	}

	/**
	 * 
	 * 获取前几天的日期
	 */
	public static String getPrefixDate2(String count) {
		Calendar cal = Calendar.getInstance();
		int day = 0 - Integer.parseInt(count);
		cal.add(Calendar.DATE, day); // int amount 代表天数
		Date datNew = cal.getTime();
		SimpleDateFormat format = new SimpleDateFormat(PATTERN2);
		return format.format(datNew);
	}

	/**
	 * 日期转换成字符串
	 * @param date
	 * @return
	 */
	public static String dateToString(Date date, String pattern) {
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		return format.format(date);
	}

	/**
	 * 字符串转换日期
	 * @param str
	 * @return
	 */
	public static Date stringToDate(String str, String pattern) {
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		if (!str.equals("") && str != null) {
			try {
				return format.parse(str);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * @param sBegin
	 * @param sEnd
	 */
	// java中怎样计算两个时间如：“21:57”和“08:20”相差的分钟数、小时数 java计算两个时间差小时 分钟 秒 .
	public void timeSubtract(String sBegin, String sEnd) {
		SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date begin = null;
		Date end = null;
		try {
			begin = dfs.parse(sBegin);
			end = dfs.parse(sEnd);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		long between = (end.getTime() - begin.getTime()) / 1000;// 除以1000是为了转换成秒

		long day1 = between / (24 * 3600);
		long hour1 = between % (24 * 3600) / 3600;
		long minute1 = between % 3600 / 60;
		long second1 = between % 60;
		System.out.println("" + day1 + "天" + hour1 + "小时" + minute1 + "分" + second1 + "秒");
	}

	/**
	 * 生成时间随机数
	 * @return
	 */
	public static String getRandTimeByDate() {
		SimpleDateFormat sdf = new SimpleDateFormat(PATTERN3);
		return sdf.format(new Date());
	}

	/**
	 * @Description:获取当前添加时间
	 * @param @param minute
	 * @param @return
	 * @throws
	 */
	public static Date getNowAddTime(int minute) {
		Calendar nowTime = Calendar.getInstance();
		nowTime.add(Calendar.MINUTE, minute);
		return nowTime.getTime();
	}

	/**
	 * 短日期格式转换为长日期格式
	 * @param shortDate
	 * @return
	 */
	public static String getCustomDateByShortDate(String shortDate) {
		String result = shortDate;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(PATTERN2);
			SimpleDateFormat csdf = new SimpleDateFormat(PATTERN1);
			result = csdf.format(sdf.parse(shortDate));
		} catch (Exception ex) {
			// 忽略
		}
		return result;
	}

	/**
	 * 短年月日格式转换为长年月格式
	 * @param shortDate
	 * @return
	 */
	public static String getCustomYearMonth(String shortDate) {
		String result = shortDate;
		try {
			if (shortDate.length() == 6) {
				result = String.format("%s-%s", result.substring(0, 4), result.substring(4, 6));
			}

		} catch (Exception ex) {
			// 忽略
		}
		return result;
	}

	/**
	 * 获取当前年月
	 *
	 * @return
	 */
	public static String getCustomYearMont(String format) {
		SimpleDateFormat df = new SimpleDateFormat(format);
		return df.format(new Date());
	}

	/**
	 * 获取与当前年月差别的月份
	 *
	 * @return
	 */
	public static String getDiffMonth(String format, int i) {

		if(StringUtils.isBlank(format)){
			return "";
		}
		String formatTemp = format.replace("-", "").replace(".", "");
		String startMonth = getCustomYearMont(formatTemp);
		if(formatTemp.equalsIgnoreCase("MMYYYY")){
			startMonth = startMonth.substring(2,6)+startMonth.substring(0,2);
		}
		else if(formatTemp.equalsIgnoreCase("YYYYMM")){

		}
		else{
			return "";
		}

		startMonth += "01";
		try{
			Date dStart = new SimpleDateFormat("yyyyMMdd").parse(startMonth);
			Calendar dd = Calendar.getInstance();
			dd.setTime(dStart);
			dd.add(Calendar.MONTH, i);
			startMonth = new SimpleDateFormat(format).format(dd.getTime());
		}catch (Exception e){}
		return startMonth;
	}

	/**
	 * 获取与当前年月差别的日期
	 *
	 * @return
	 */
	public static String getDiffDay(String format, int i) {
		String startMonth = getCustomYearMont(format);
		startMonth = startMonth.replace("-", "");
		try{

			Date dStart = new SimpleDateFormat("yyyyMMdd").parse(startMonth);
			Calendar dd = Calendar.getInstance();
			dd.setTime(dStart);
			dd.add(Calendar.MONTH, i);
			startMonth = new SimpleDateFormat(format).format(dd.getTime());

		}
		catch (Exception ex)
		{
		}
		return startMonth;
	}

	/**
	 * 指定月第一天
	 * @return
	 */
	public static String getFirstDay(Calendar c, String format) {
		c.set(Calendar.DAY_OF_MONTH, 1);
	    return DateFormatUtils.format(c, format);
	}

	/**
	 * 指定月第一天
	 * @return
	 */
	public static String getFirstDay(Date d, String format) {
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		c.set(Calendar.DAY_OF_MONTH, 1);
	    return DateFormatUtils.format(c, format);
	}

	/**
	 * 指定月最后一天
	 * @return
	 */
	public static String getLastDay(Calendar c, String format) {
		c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
		return DateFormatUtils.format(c, format);
	}

	/**
	 * 指定月最后一天
	 * @return
	 */
	public static String getLastDay(Date d, String format) {
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
		return DateFormatUtils.format(c, format);
	}
	

	/**
	 * 指定月第一天
	 * @return
	 */
	public static String getFirstDay(String format, int i) {
		String month = getDiffMonth( "yyyyMM", i);
		if(month.length() != 6){
			return "";
		}
		String firstDay = month + "01";
		try{
			Date dStart = new SimpleDateFormat("yyyyMMdd").parse(firstDay);
			firstDay = new SimpleDateFormat(format).format(dStart.getTime());
		}
		catch (Exception ex) {}
		return firstDay;
	}

	/**
	 * 指定月最后一天
	 * @return
	 */
	public static String getLastDay(String format, int i) {
		String month = getDiffMonth( "yyyyMM", i+1);
		if(month.length() != 6){
			return "";
		}
		String firstDay = month + "01";
		String lastDay = "";
		try{
			Date dStart = new SimpleDateFormat("yyyyMMdd").parse(firstDay);
			Calendar dd = Calendar.getInstance();
			dd.setTime(dStart);
			dd.add(Calendar.DAY_OF_MONTH, -1);
			lastDay = new SimpleDateFormat(format).format(dd.getTime());
		}catch (Exception ex) {}
		return lastDay;
	}
	
	
	/**
	 * 获取和现在月比较的月份
	 * @return
	 */
	public static String getCompareDate(String date) {
		try{
			if(date.compareTo(getCurrentDate()) > 0 && date.length() >= 10){
				date = String.valueOf(Integer.parseInt(date.substring(0, 4))-1) + date.substring(4, date.length());
			}
		}
		catch (Exception ex){
		}

		return date;
	}


	/**
	 * yyyyMMddHHmmss转化成yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static String convertYearDateTime(String dateValidDate) {
		try{
			Date date = new SimpleDateFormat("yyyyMMddHHmmss").parse(dateValidDate);
			dateValidDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
		}
		catch (Exception ex){
		}
		return dateValidDate;
	}


}
