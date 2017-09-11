package com.xinyan.spider.isp.mobile.processor.telecom;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.xinyan.spider.isp.base.CacheContainer;
import com.xinyan.spider.isp.base.Context;
import com.xinyan.spider.isp.base.ProcessorCode;
import com.xinyan.spider.isp.base.Result;
import com.xinyan.spider.isp.base.StatusCode;
import com.xinyan.spider.isp.common.Constants;
import com.xinyan.spider.isp.common.utils.DateUtils;
import com.xinyan.spider.isp.common.utils.RegexUtils;
import com.xinyan.spider.isp.common.utils.StringUtils;
import com.xinyan.spider.isp.mobile.model.CarrierInfo;
import com.xinyan.spider.isp.mobile.parser.telecom.HuBeiTelecomParser;

/**
 * 湖北电信处理类
 * @description
 * @author yyj
 * @date 2017年5月11日 下午3:38:43
 * @version V1.0
 */
@Component
public class HuBeiTelecomPssor extends AbstractTelecomPssor {

	protected static Logger logger = LoggerFactory.getLogger(HuBeiTelecomPssor.class);

	@Autowired
	private HuBeiTelecomParser parser;

	/**
	 * 正常认证登录
	 * @param webClient
	 * @param context
	 * @return
	 */
	@Override
	public Result doLogin(WebClient webClient, Context context) {
		Result result = new Result();
		try {
			// 1.开始登陆
			logger.info("==>1.[{}]用户正常认证开始.....", context.getTaskId());

			String url = "http://www.189.cn/hb/service/";
			getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, null);

			String returnCode = telecomLogin(webClient, context, true); 
			if(StatusCode.系统忙.getCode().equals(returnCode)){
				url = "http://www.189.cn/hb/service/";
				getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, null);
				returnCode = telecomLogin(webClient, context, true);
			}
			result.setCode(returnCode);
			result.setMsg(StatusCode.getMsg(returnCode));
			
			if(StatusCode.SUCCESS.getCode().equals(returnCode)){//发送短信验证码
				result = sendSMS(webClient, context);
			}

			logger.info("==>1.[{}]用户正常认证结束{}", context.getTaskId(), result);
		} catch (Exception e) {
			logger.error("==>1.[{}]用户正常认证出现异常:[{}]", context.getTaskId(), e);
			result.setResult(StatusCode.登陆出错);
		} finally {
			// 设置回调
			if (!StatusCode.请输入短信验证码.getCode().equals(result.getCode())
					&& !StatusCode.请输入图片验证码.getCode().equals(result.getCode())) {
				sendLoginMsg(result, context, false, webClient);
			}
		}
		return result;
	}

	/**
	 * 图片认证登录
	 * @param webClient
	 * @param context
	 * @return
	 */
	@Override
	public Result doLoginByIMG(WebClient webClient, Context context) {
		Result result = new Result();

		if(StringUtils.isEmpty(context.getUserInput())){
			result.setResult(StatusCode.请输入图片验证码);
            return result;
		}

		try {
			// 1.开始登陆
			logger.info("==>1.[{}]用户图片认证开始.....", context.getTaskId());
			String returnCode = telecomLoginByIMG(webClient, context);
			result.setCode(returnCode);
			result.setMsg(StatusCode.getMsg(returnCode));
			
			if(StatusCode.SUCCESS.getCode().equals(returnCode)){//发送短信验证码
				result = sendSMS(webClient, context);
			}
			
			logger.info("==>1.[{}]用户图片认证结束{}", context.getTaskId(), result);
		} catch (Exception e) {
			logger.error("==>1.[{}]用户图片认证出现异常:[{}]", context.getTaskId(), e);
			result.setResult(StatusCode.登陆出错);
		} finally {
			// 设置回调
			if (!StatusCode.请输入短信验证码.getCode().equals(result.getCode())
					&& !StatusCode.请输入图片验证码.getCode().equals(result.getCode())) {
				sendLoginMsg(result, context, false, webClient);
			}
		}
		return result;
	}

	/**
	 * 短信认证登录
	 * @param webClient
	 * @param context
	 * @return
	 */
	@Override
	public Result doLoginBySMS(WebClient webClient, Context context) {
		Result result = new Result();
		try {
			// 1.开始登陆
			logger.info("==>1.[{}]用户短信认证开始.....", context.getTaskId());


			String url = "http://hb.189.cn/feesquery_checkCDMAFindWeb.action";
			List<NameValuePair> reqParam = new ArrayList<>();
			reqParam.clear();
			reqParam.add(new NameValuePair("random", context.getUserInput()));
			reqParam.add(new NameValuePair("sentType", "C"));
			Page page = getPage(webClient, url, HttpMethod.POST, reqParam, Constants.MAX_SENDMSG_TIIMES,null,null);
			if("0".equals(page.getWebResponse().getContentAsString())){
				result.setResult(StatusCode.短信验证码错误);
			}else if("1".equals(page.getWebResponse().getContentAsString())){
				logger.info("==>1.[{}]短信验证码正确.", context.getTaskId());
				result.setResult(StatusCode.登陆成功);

				url = "http://hb.189.cn/feesquery_querylist.action";
				String startDate = DateUtils.getFirstDay(new Date(), "yyyyMMdd");//指定月月初
				reqParam.clear();
				reqParam.add(new NameValuePair("startMonth", startDate.substring(0,6)+"0000"));
				reqParam.add(new NameValuePair("type", "1"));
				reqParam.add(new NameValuePair("random", context.getUserInput()));
				page = getPage(webClient, url, HttpMethod.POST, reqParam,Constants.MAX_SENDMSG_TIIMES,null, null);
			}else{
				result.setResult(StatusCode.短信验证码错误);
			}
		} catch (Exception e) {
			logger.error("==>1.[{}]用户短信认证出现异常:[{}]", context.getTaskId(), e);
			result.setResult(StatusCode.登陆出错);
		} finally {
			logger.info("==>1.[{}]用户短信认证结束{}", context.getTaskId(), result);
			// 设置回调
			if (!StatusCode.请输入短信验证码.getCode().equals(result.getCode())
					&& !StatusCode.请输入图片验证码.getCode().equals(result.getCode())) {
				sendLoginMsg(result, context, true, webClient);
			}
		}
		return result;
	}

	/**
	 * 发送短信验证码
	 * @param webClient
	 * @param context
	 * @return
	 */
	private Result sendSMS(WebClient webClient, Context context) {
		Result result = new Result();
		HashMap<String, String > header = new HashMap<>();
		String url = "http://www.189.cn/dqmh/ssoLink.do?method=linkTo&platNo=10018&toStUrl=http://hb.189.cn/SSOtoWSSNew?toWssUrl=/pages/selfservice/feesquery/detailListQuery.jsp&trackPath=SYleftDH";
		header.put("Referer", "http://login.189.cn/login");
		Page page = getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, header);
		String cityCode = RegexUtils.matchValue("id=\"CITYCODE\" name=\"CITYCODE\" value=\"(.*?)\"", page.getWebResponse().getContentAsString());
		context.setParam1(cityCode);

		logger.info("==>[{}]正在请求发送短信验证码.....", context.getTaskId());
		url = "http://hb.189.cn/feesquery_sentPwd.action";
		List<NameValuePair> reqParam = new ArrayList<>();
		reqParam.add(new NameValuePair("productNumber", context.getUserName()));
		reqParam.add(new NameValuePair("cityCode", cityCode));
		reqParam.add(new NameValuePair("sentType", "C"));
		reqParam.add(new NameValuePair("ip", "0"));
		page = getPage(webClient, url, HttpMethod.POST, reqParam,Constants.MAX_SENDMSG_TIIMES,null, null);
		if(page.getWebResponse().getContentAsString().contains("尊敬的客户，您获取随机码操作过于频繁")){
			result.setResult(StatusCode.短信验证码发送频繁);
            return result;
		}else if(page.getWebResponse().getContentAsString().contains("请注意查收")){
			logger.info("==>[{}]已经发送短信验证码...", context.getTaskId());
			// 通知前端发送成功
			result.setResult(StatusCode.请输入短信验证码);
			context.setTaskSubStatus(Constants.VALIDATE_TYPE_CODE_SMS);// 需要设置回调子状态
			sendValidataMsg(result, context, true, webClient, Constants.DEQUEUE_TIME_OUT, "");
		}else{//获发送短信验证码失败
			result.setResult(StatusCode.发送短信验证码失败);
            return result;
		}
		return result;
	}

	@Override
	public Result doCrawler(WebClient webClient, Context context) {
		// 页面缓存
		CacheContainer cacheContainer = new CacheContainer();
		CarrierInfo carrierInfo = new CarrierInfo();
		context.setMappingId(carrierInfo.getMappingId());
		BeanUtils.copyProperties(context, carrierInfo);
		carrierInfo.getCarrierUserInfo().setMobile(context.getUserName());// 手机号码

		Result result = telecomCrawler(webClient, context, carrierInfo, cacheContainer);
		if (!result.isSuccess()) { // 采集基础信息失败
			return result;
		}

		try {
			result = parser.parse(context, carrierInfo, cacheContainer);
			if (!result.isSuccess()) {
				return result;
			}
		} catch (Exception ex) {
			getLogger().error("==>[{}]湖北数据解析出现异常:[{}]", context.getTaskId(), ex);
			result.setFail();
			return result;
		} finally {
			sendAnalysisMsg(result, context);
		}

		getLogger().info("==>[{}]数据抓取结束{}\n,详情:{}", context.getTaskId(), result, result.getData());
		return result;
	}

	@Override
	public Result processBaseInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
		Result result = new Result();		
		String url = "";
		try {
			logger.info("==>1.1[{}]采集基本信息开始.....", context.getTaskId());
			url = "http://hb.189.cn/pages/selfservice/custinfo/userinfo/userInfo.action";
			Page page = getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, null);
			cacheContainer.putPage(ProcessorCode.BASIC_INFO.getCode(), page);
			logger.info("==>1.1[{}]采集基本信息结束.", context.getTaskId());

			logger.info("==>1.2[{}]采集个人余额开始.....", context.getTaskId());
			url = "http://hb.189.cn/pages/selfservice/feesquery/feesyue.jsp";
			page = getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, null);
			String cityName = RegexUtils.matchValue("var cityname=\"(.*?)\"", page.getWebResponse().getContentAsString());
			String userName = RegexUtils.matchValue("var username=\"(.*?)\"", page.getWebResponse().getContentAsString());

			url = "http://hb.189.cn/queryFeesYue.action";
			HashMap<String, String > header = new HashMap<>();
			header.clear();
			header.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			List<NameValuePair> reqParam = new ArrayList<>();
			reqParam.clear();
			reqParam.add(new NameValuePair("cityname", cityName));
			reqParam.add(new NameValuePair("username", userName));
			cacheContainer.putString("CITYNAME", cityName);
			List<Page> pages = new ArrayList<>();
			page = getPage(webClient, url, HttpMethod.POST, reqParam,Constants.MAX_SENDMSG_TIIMES,null, header);
			pages.add(page);
			//将套餐名称放入
			url = "http://hb.189.cn/pages/selfservice/feesquery/getTaocanDetail.action";
			page = getPage(webClient, url, HttpMethod.POST, null,Constants.MAX_SENDMSG_TIIMES,null, null);
			pages.add(page);
			//将星级放入
			url = "http://hb.189.cn/hbuserCenter.action?COLLCC=2091490402&";
			page = getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, null);
			pages.add(page);
			cacheContainer.putPages(ProcessorCode.AMOUNT.getCode(), pages);
			logger.info("==>1.2[{}]采集个人余额结束.", context.getTaskId());

			result.setSuccess();
		} catch (Exception e) {
			logger.error("==>1.[{}]采集基本信息出现异常:[{}]", context.getTaskId(), e);
			result.setResult(StatusCode.解析基础信息出错);
			return result;
		}
		return result;
	}

	@Override
	public Result processCallRecordInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
		Result result = new Result();
		List<NameValuePair> reqParam = new ArrayList<>();
		List<Page> pages = new ArrayList<>();
		String url = "http://hb.189.cn/feesquery_querylist.action";
		try {
			Calendar calendar = Calendar.getInstance();
			// 查询最近6个月通话详单
			for (int i = 1; i < (context.getRecordSize() + 1); i++) {//包含当前月份
				final String month = DateFormatUtils.format(calendar, "yyyyMM");
				String logFlag = String.format("==>2."+i+"["+context.getTaskId()+"]采集["+month+"]通话详单,第[{}]次尝试请求.....");
				reqParam.clear();
				reqParam.add(new NameValuePair("startMonth", month+"010000"));
				reqParam.add(new NameValuePair("type", "1"));
				reqParam.add(new NameValuePair("prod_type", "1"));
				reqParam.add(new NameValuePair("pagecount", "10000"));
				Page page = getPage(webClient, url, HttpMethod.POST, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, null);
				if(null != page){//添加缓存信息
					if (!page.getWebResponse().getContentAsString().contains("系统没有查到")){
						logger.info("==>2.[{}]采集[{}]通话详单成功.", context.getTaskId(), month);
						pages.add(page);
					}else{
						logger.info("==>2.[{}]采集[{}]通话详单结束,没有查询结果.", context.getTaskId(), month);
					}
				}else{
					logger.info("==>2.[{}]采集[{}]通话详单结束,没有查询结果.", context.getTaskId(), month);
				}
				calendar.add(Calendar.MONTH, -1);
			}
			cacheContainer.putPages(ProcessorCode.CALLRECORD_INFO.getCode(), pages);
			result.setSuccess();
		} catch (Exception e) {
			logger.error("==>2.[{}]采集通话详情出现异常:[{}]", context.getTaskId(), e);
			result.setResult(StatusCode.解析通话详情出错);
			return result;
		}
		return result;
	}

	@Override
	public Result processSmsInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
		Result result = new Result();
		List<NameValuePair> reqParam = new ArrayList<>();
		List<Page> pages = new ArrayList<>();
		String url = "http://hb.189.cn/feesquery_querylist.action";
		try {
			Calendar calendar = Calendar.getInstance();
			// 查询最近6个月短信记录
			for (int i = 1; i < (context.getRecordSize() + 1); i++) {//包含当前月份
				final String month = DateFormatUtils.format(calendar, "yyyyMM");
				String logFlag = String.format("==>3."+i+"["+context.getTaskId()+"]采集["+month+"]短信记录,第[{}]次尝试请求.....");
				reqParam.clear();
				reqParam.add(new NameValuePair("startMonth", month+"010000"));
				reqParam.add(new NameValuePair("type", "3"));
				reqParam.add(new NameValuePair("prod_type", "1"));
				reqParam.add(new NameValuePair("pagecount", "10000"));
				Page page = getPage(webClient, url, HttpMethod.POST, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, null);
				if(null != page){
					if (!page.getWebResponse().getContentAsString().contains("系统没有查到")){
						logger.info("==>3.[{}]采集[{}]短信记录成功.", context.getTaskId(), month);
						pages.add(page);
					}else{
						logger.info("==>3.[{}]采集[{}]短信记录结束,没有查询结果.", context.getTaskId(), month);
					}
				}else{
					logger.info("==>3.[{}]采集[{}]短信记录结束,没有查询结果.", context.getTaskId(), month);
				}
				calendar.add(Calendar.MONTH, -1);
			}
			cacheContainer.putPages(ProcessorCode.SMS_INFO.getCode(), pages);
			result.setSuccess();
		} catch (Exception e) {
			logger.error("==>3.[{}]采集短信记录出现异常:[{}]", context.getTaskId(), e);
			result.setResult(StatusCode.解析上网记录出错);
			return result;
		}
		return result;
	}

	@Override
	public Result processNetInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
		Result result = new Result();
		List<NameValuePair> reqParam = new ArrayList<>();
		HashMap<String, String > header = new HashMap<>();
		header.clear();
		header.put("Referer", "http://hb.189.cn/pages/selfservice/feesquery/detailListQuery.jsp");
		String  url = "http://hb.189.cn/feesquery_querylist.action";
		List<Page> pages = new ArrayList<>();
		try {
			Calendar calendar = Calendar.getInstance();
			// 查询最近6个月上网记录
			for (int i = 1; i < (context.getRecordSize() + 1); i++) {//包含当前月份
				calendar.set(Calendar.DAY_OF_MONTH, 1);
				final String month = DateFormatUtils.format(calendar, "yyyyMM");
				String logFlag = String.format("==>4."+i+"["+context.getTaskId()+"]采集["+month+"]上网记录,第[{}]次尝试请求.....");
				reqParam.clear();
				reqParam.add(new NameValuePair("startMonth", month+"010000"));
				reqParam.add(new NameValuePair("type", "2"));
				reqParam.add(new NameValuePair("prod_type", "1"));
				reqParam.add(new NameValuePair("pagecount", "10000"));
				Page page = getPage(webClient, url, HttpMethod.POST, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, header);
				if(null != page){
					if (!page.getWebResponse().getContentAsString().contains("系统没有查到您")){
						logger.info("==>4.[{}]采集[{}]上网记录成功.", context.getTaskId(), month);
						pages.add(page);
					}else{
						logger.info("==>4.[{}]采集[{}]上网记录结束,没有查询结果.", context.getTaskId(), month);
					}
				}else{
					logger.info("==>4.[{}]采集[{}]上网记录结束,没有查询结果.", context.getTaskId(), month);
				}
				calendar.add(Calendar.MONTH, -1);
			}

			cacheContainer.putPages(ProcessorCode.NET_INFO.getCode(), pages);
			result.setSuccess();
		} catch (Exception e) {
			logger.error("==>4.[{}]采集上网记录出现异常:[{}]", context.getTaskId(), e);
			result.setResult(StatusCode.解析上网记录出错);
			return result;
		}
		return result;
	}

	@Override
	public Result processBillInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
		Result result = new Result();
		try {
			Calendar calendar = Calendar.getInstance();
			List<NameValuePair> reqParam = new ArrayList<>();
			List<Page> pages = new ArrayList<>();
			Map<String,String> header = new HashMap<>();
			header.put("Referer", "http://hb.189.cn/pages/selfservice/feesquery/newBOSSQueryCustBill.action");
			header.put("Content-Type", "text/html; charset=GBK");
			String url = "http://hb.189.cn/pages/selfservice/feesquery/newBOSSQueryCustBill.action";
			String cityName = cacheContainer.getString("CITYNAME");
			// 查询最近6个月账单
			for (int i = 1; i < (context.getRecordSize() + 1); i++) {//前6个月(不算当月)
				calendar.add(Calendar.MONTH, -1);
				final String month = DateFormatUtils.format(calendar, "yyyyMM");
				String logFlag = String.format("==>5."+i+"["+context.getTaskId()+"]采集["+month+"]账单信息,第[{}]次尝试请求.....");
				reqParam.clear();
				reqParam.add(new NameValuePair("billbeanos.citycode", context.getParam1()));
				reqParam.add(new NameValuePair("billbeanos.btime", month + "01"));
				reqParam.add(new NameValuePair("billbeanos.accnbr", context.getUserName()));
				reqParam.add(new NameValuePair("billbeanos.paymode", "2"));
				reqParam.add(new NameValuePair("skipmethod.cityname", cityName));
				Page page = getPage(webClient, url, HttpMethod.POST, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, header);
				if(null != page){
					if (page.getWebResponse().getContentAsString().contains("抱歉,客户化帐单查询失败，请稍候再试")){
						logger.info("==>5.[{}]采集[{}]账单信息结束,没有查询结果.", context.getTaskId(), month);
					}else{
						logger.info("==>5.[{}]采集[{}]账单信息成功.", context.getTaskId(), month);
						pages.add(page);
					}
				}else{
					logger.info("==>5.[{}]采集[{}]账单信息结束,没有查询结果.", context.getTaskId(), month);
				}
			}
			cacheContainer.putPages(ProcessorCode.BILL_INFO.getCode(), pages);
			result.setSuccess();
		} catch (Exception e) {
			logger.error("==>5.[{}]采集账单信息出现异常:[{}]", context.getTaskId(), e);
			result.setResult(StatusCode.解析账单信息出错);
			return result;
		}
		return result;
	}

	@Override
	public Result processPackageItemInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
		Result result = new Result();
		try {
			Map<String, String> header = new HashMap<>();
			header.put("Referer", "http://hb.189.cn/pages/selfservice/feesquery/taocan.jsp");
			String url = "http://hb.189.cn/pages/selfservice/feesquery/getTaocanDetail.action";
			String logFlag = String.format("==>6.["+context.getTaskId()+"]采集[已办理]套餐信息,第[{}]次尝试请求.....");

			Page page = getPage(webClient, url, HttpMethod.POST, null, Constants.MAX_RETRY_TIIMES, logFlag, header);
			if (null != page) {// 添加缓存信息
				logger.info("==>6.[{}]采集[已办理]套餐信息成功.", context.getTaskId());
				cacheContainer.putPage(ProcessorCode.PACKAGE_ITEM.getCode(), page);
			} else {
				logger.info("==>6.[{}]采集[已办理]套餐信息结束,没有查询结果.", context.getTaskId());
			}
			result.setSuccess();
		} catch (Exception e) {
			logger.error("==>6.[{}]解析套餐信息出现异常:[{}]", context.getTaskId(), e);
			result.setResult(StatusCode.解析办理业务信息出错);
			return result;
		}
		return result;
	}

	@Override
	public Result processUserFamilyMember(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
		logger.info("==>7.[{}]暂无亲情号码取样");
		return new Result(StatusCode.SUCCESS);
	}

	@Override
	public Result processUserRechargeItemInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
		Result result = new Result();
		try {
			Calendar calendar = Calendar.getInstance();
			Map<String, String> header = new HashMap<>();
			header.put("Referer", "http://hb.189.cn/pages/selfservice/payment/payhistory.jsp");
			List<NameValuePair> reqParam = new ArrayList<>();
			List<Page> pages = new ArrayList<>();
			String url = "http://hb.189.cn/showIntegralOldList.action";
			
			// 查询最近6个月充值记录
			for (int i = 1; i < (context.getRecordSize() + 1); i++) {
				final String queryMonth = DateFormatUtils.format(calendar, "yyyyMM");
				reqParam.clear();
				reqParam.add(new NameValuePair("time", queryMonth + "0000"));
				reqParam.add(new NameValuePair("pagenum", "1"));
				String logFlag = String.format("==>8." + i + "["+context.getTaskId()+"]采集[" + queryMonth + "]充值记录,第[{}]次尝试请求.....");
				Page page = getPage(webClient, url, HttpMethod.POST, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, header);
				if (null != page) {// 添加缓存信息
					pages.add(page);
					logger.info("==>8.[{}]采集[{}]充值记录成功.", context.getTaskId(), queryMonth);
				} else {
					logger.info("==>8.[{}]采集[{}]充值记录结束,没有查询结果.", context.getTaskId(), queryMonth);
				}
				calendar.add(Calendar.MONTH, -1);
			}
			cacheContainer.putPages(ProcessorCode.RECHARGE_INFO.getCode(), pages);
			result.setSuccess();
		} catch (Exception e) {
			logger.error("==>8.[{}]解析充值记录出现异常:[{}]", context.getTaskId(), e);
			result.setResult(StatusCode.解析充值记录出错);
			return result;
		}
		return result;
	}

	@Override
	protected Result loginout(WebClient webClient){
		Result result = new Result();
		logger.info("==>9.电信统一退出开始.....");
		String url = "http://www.189.cn/login/logout.do";
		getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, null);

		result.setSuccess();
		return result;
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}

}
