package com.xinyan.spider.isp.mobile.processor.telecom;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

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
import com.xinyan.spider.isp.common.utils.JavaScriptUtils;
import com.xinyan.spider.isp.common.utils.StringUtils;
import com.xinyan.spider.isp.mobile.model.CarrierInfo;
import com.xinyan.spider.isp.mobile.parser.telecom.TianJinTelecomParser;

/**
 * 天津电信处理类
 * @description
 * @author yyj
 * @date 2017年5月17日 下午4:32:21
 * @version V1.0
 */
@Component
public class TianJinTelecomPssor extends AbstractTelecomPssor {

	protected static Logger logger = LoggerFactory.getLogger(TianJinTelecomPssor.class);

	@Autowired
	private TianJinTelecomParser parser;

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
			logger.info("==>0.[{}]用户正常认证开始.....", context.getTaskId());

			String url = "http://www.189.cn/tj/service/";
			getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, null);

			String password = JavaScriptUtils.invoker("js/ChinaTelecomDes.js" ,"valAesEncryptSet", context.getPassword());
			context.setPassword(password);

			url = "http://login.189.cn/login";
			List<NameValuePair> reqParam = new ArrayList<>();
			reqParam.add(new NameValuePair("Account", context.getUserName()));
			reqParam.add(new NameValuePair("UType", "201"));
			reqParam.add(new NameValuePair("ProvinceID", "03"));
			reqParam.add(new NameValuePair("AreaCode", ""));
			reqParam.add(new NameValuePair("CityNo", ""));
			reqParam.add(new NameValuePair("RandomFlag", "0"));
			reqParam.add(new NameValuePair("Password", context.getPassword()));
			reqParam.add(new NameValuePair("Captcha", ""));

			Page page = getPage(webClient, url, HttpMethod.POST, reqParam,Constants.MAX_SENDMSG_TIIMES,null, null);

			if (null == page || "timeout".equals(page.getWebResponse().getContentAsString())){//网站加载超时
				result.setResult(StatusCode.网站加载超时);
			}else{
				String returnCode = getResult(page.getWebResponse().getContentAsString(), context.getUserName(), context.getTaskId());
				result.setCode(returnCode);
				result.setMsg(StatusCode.getMsg(returnCode));
			}
			logger.info("==>0.[{}]用户正常认证结束{}", context.getTaskId(), result);
		} catch (Exception e) {
			logger.error("==>0.[{}]用户正常认证出现异常:[{}]", context.getTaskId(), e);
			result.setResult(StatusCode.登陆出错);
		} finally {
			// 设置回调
			if (!StatusCode.请输入短信验证码.getCode().equals(result.getCode())
					&& !StatusCode.请输入图片验证码.getCode().equals(result.getCode())) {
				sendLoginMsg(result, context, true, webClient);
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
			logger.info("==>0.[{}]用户图片认证开始.....", context.getTaskId());
			HashMap<String, String > header = new HashMap<>();
			List<NameValuePair> reqParam = new ArrayList<>();
			
			reqParam.clear();
			reqParam.add(new NameValuePair("randomMode", "2"));
			reqParam.add(new NameValuePair("funcType", "detail"));
			reqParam.add(new NameValuePair("randValue", context.getUserInput()));
			String url = "http://tj.189.cn/tj/checkrand/checkRand.action";
			Page page = getPage(webClient, url, HttpMethod.POST, reqParam,Constants.MAX_SENDMSG_TIIMES,null, null);
			
			url = "http://tj.189.cn/tj/service/bill/sendRandomSmscode.action";
			header.clear();
			header.put("Referer", "http://tj.189.cn/tj/service/bill/detailBillQuery.action");
			reqParam.clear();
			reqParam.add(new NameValuePair("randomMode", "2"));
			reqParam.add(new NameValuePair("funcType", "detail"));
			reqParam.add(new NameValuePair("checkCode", context.getUserInput()));

			page = getPage(webClient, url, HttpMethod.POST, reqParam,Constants.MAX_SENDMSG_TIIMES,null, header);
			String pageInfo = page.getWebResponse().getContentAsString();
			if (StringUtils.isEmpty(pageInfo) || StringUtils.contains(pageInfo, "图形验证码校验失败")){
				logger.info("==>0.[{}]天津电信图形验证码校验失败!", context.getTaskId());
				result.setResult(StatusCode.图片验证码错误);
			}else if(StringUtils.contains(pageInfo, context.getUserName())){
				logger.info("==>0.[{}]短信发送成功.....", context.getTaskId());
				// 通知前端发送成功
				result.setResult(StatusCode.请输入短信验证码);
				context.setTaskSubStatus(Constants.VALIDATE_TYPE_CODE_SMS);// 需要设置回调子状态
				sendValidataMsg(result, context, true, webClient, Constants.DEQUEUE_TIME_OUT, "");
			}else{
				logger.info("==>0.[{}]天津电信动态密码发送失败!", context.getTaskId());
				result.setResult(StatusCode.发送短信验证码失败);
			}
			logger.info("==>0.[{}]用户图片认证结束{}", context.getTaskId(), result);
		} catch (Exception e) {
			logger.error("==>0.[{}]用户图片认证出现异常:[{}]", context.getTaskId(), e);
			result.setResult(StatusCode.登陆出错);
		} finally {
			// 设置回调
			if (!StatusCode.请输入短信验证码.getCode().equals(result.getCode())
					&& !StatusCode.请输入图片验证码.getCode().equals(result.getCode())) {
				sendLoginMsg(result, context, true, webClient);
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
			logger.info("==>0.[{}]用户短信认证开始.....", context.getTaskId());
			String url = "http://tj.189.cn/tj/service/bill/validateRandomcode.action";
			
			List<NameValuePair> reqParam = new ArrayList<>();

			reqParam.clear();
			reqParam.add(new NameValuePair("sRandomCode", context.getUserInput()));
			reqParam.add(new NameValuePair("randomMode", "1"));
			reqParam.add(new NameValuePair("funcType", "detail"));
			Page page = getPage(webClient, url, HttpMethod.POST, reqParam,Constants.MAX_SENDMSG_TIIMES,null, null);
			String pageInfo = page.getWebResponse().getContentAsString();
			if(pageInfo.contains("随机密码验证失败")){
				result.setResult(StatusCode.短信验证码错误);
                return result;
			}else if(StringUtils.contains(pageInfo, context.getUserName())){
				logger.info("==>0.[{}]短信验证码正确.", context.getTaskId());
				result.setResult(StatusCode.登陆成功);
			}else{
				result.setResult(StatusCode.短信验证码错误);
                return result;
			}
		} catch (Exception e) {
			logger.info("==>0.[{}]用户短信认证出现异常:[{}]", context.getTaskId(), e);
			result.setResult(StatusCode.登陆出错);
		} finally {
			logger.info("==>0.[{}]用户短信认证结束{}", context.getTaskId(), result);
			// 设置回调
			if (!StatusCode.请输入短信验证码.getCode().equals(result.getCode())
					&& !StatusCode.请输入图片验证码.getCode().equals(result.getCode())) {
				sendLoginMsg(result, context, true, webClient);
			}
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
			logger.error("==>[{}]天津数据解析出现异常:[{}]", context.getTaskId(), ex);
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
		HashMap<String, String > header = new HashMap<>();
		List<NameValuePair> reqParam = new ArrayList<>();
		String url = "";
		try {
			url = "http://www.189.cn/login/sso/ecs.do?method=linkTo&platNo=10002&toStUrl=http://tj.189.cn/tj/service/manage/modifyUserInfo.action";
			Page page = getPage(webClient, url, HttpMethod.GET, null, Constants.MAX_SENDMSG_TIIMES,null,null);
			
			logger.info("==>1.1[{}]采集基本信息开始.....", context.getTaskId());
			url = "http://tj.189.cn/tj/service/manage/modifyUserInfo.action?amp%3BcityCode=tj&fastcode=02241349";
			page = getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null,null);
			if(null != page){
				cacheContainer.putPage(ProcessorCode.BASIC_INFO.getCode(), page);
			}
			logger.info("==>1.1[{}]采集基本信息结束.", context.getTaskId());

			logger.info("==>1.2[{}]采集个人余额开始.....", context.getTaskId());
			url = "http://tj.189.cn/tj/service/bill/balanceQuery.action";
			header.clear();
			header.put("Referer", "http://tj.189.cn/tj/service/bill/feeQueryIndex.action?tab=3&amp;fastcode=02251357&amp;cityCode=tj");
			reqParam.clear();
			reqParam.add(new NameValuePair("requestFlag", "asynchronism"));
			reqParam.add(new NameValuePair("shijian", ""));
			page = getPage(webClient, url, HttpMethod.POST, reqParam,Constants.MAX_SENDMSG_TIIMES,null, header);
			if (null != page) {
				cacheContainer.putPage(ProcessorCode.AMOUNT.getCode(), page);
			}
			logger.info("==>1.2[{}]采集个人余额结束.", context.getTaskId());
			
			logger.info("==>1.3[{}]采集主套餐开始.....", context.getTaskId());
			url = "http://tj.189.cn/tj/service/bill/orderingRelaQuery.action";
			page = getPage(webClient, url, HttpMethod.POST, null,Constants.MAX_SENDMSG_TIIMES,null, null);
			if(null != page){
				cacheContainer.putPage(ProcessorCode.POINTS_VALUE.getCode(), page);
			}
			logger.info("==>1.3[{}]采集主套餐结束.", context.getTaskId());
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
		String url = "http://tj.189.cn/tj/exportToExcel";
		try {
			Calendar calendar = Calendar.getInstance();
			// 查询最近6个月通话详单
			for (int i = 1; i < (context.getRecordSize() + 1); i++) {//包含当前月份
				final String month = DateFormatUtils.format(calendar, "yyyyMM");
				String logFlag = String.format("==>2."+i+"["+context.getTaskId()+"]采集["+month+"]通话详单,第[{}]次尝试请求.....");
				String startDate = DateUtils.getFirstDay(calendar, "yyyy-MM-dd");//指定月月初
				String endDate = DateUtils.getLastDay(calendar,"yyyy-MM-dd");//指定月末
				if(1 == i){
					endDate = DateFormatUtils.format(calendar, "yyyy-MM-dd");
				}
				reqParam.clear();
				reqParam.add(new NameValuePair("pageNo", "-1"));
				reqParam.add(new NameValuePair("pageRecords", "-1"));
				reqParam.add(new NameValuePair("billDetailType", "1"));
				reqParam.add(new NameValuePair("beginTime", startDate));
				reqParam.add(new NameValuePair("endTime", endDate));
				reqParam.add(new NameValuePair("cardType", ""));
				reqParam.add(new NameValuePair("exporttxttype", "1"));

				Page page = getPage(webClient, url, HttpMethod.GET, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, null);
				if(null != page){//添加缓存信息
					logger.info("==>2.[{}]采集[{}]通话详单成功.", context.getTaskId(), month);
					pages.add(page);
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
		String url = "http://tj.189.cn/tj/exportToExcel";
		try {
			Calendar calendar = Calendar.getInstance();
			// 查询最近6个月短信记录
			for (int i = 1; i < (context.getRecordSize() + 1); i++) {//包含当前月份
				final String month = DateFormatUtils.format(calendar, "yyyyMM");
				String logFlag = String.format("==>3."+i+"["+context.getTaskId()+"]采集["+month+"]短信记录,第[{}]次尝试请求.....");
				String startDate = DateUtils.getFirstDay(calendar, "yyyy-MM-dd");//指定月月初
				String endDate = DateUtils.getLastDay(calendar,"yyyy-MM-dd");//指定月末
				if(1 == i){
					endDate = DateFormatUtils.format(calendar, "yyyy-MM-dd");
				}
				reqParam.clear();
				reqParam.add(new NameValuePair("pageNo", "-1"));
				reqParam.add(new NameValuePair("pageRecords", "-1"));
				reqParam.add(new NameValuePair("billDetailType", "2"));
				reqParam.add(new NameValuePair("beginTime", startDate));
				reqParam.add(new NameValuePair("endTime", endDate));
				reqParam.add(new NameValuePair("cardType", ""));
				reqParam.add(new NameValuePair("exporttxttype", "1"));
				Page page = getPage(webClient, url, HttpMethod.GET, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, null);
				if(null != page){
					logger.info("==>3.[{}]采集[{}]短信记录成功.", context.getTaskId(), month);
					pages.add(page);
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
		String url = "http://tj.189.cn/tj/exportToExcel";
		List<Page> pages = new ArrayList<>();
		try {
			Calendar calendar = Calendar.getInstance();
			// 查询最近6个月上网记录
			for (int i = 1; i < (context.getRecordSize() + 1); i++) {//包含当前月份
				calendar.set(Calendar.DAY_OF_MONTH, 1);
				final String month = DateFormatUtils.format(calendar, "yyyyMM");
				String logFlag = String.format("==>4."+i+"["+context.getTaskId()+"]采集["+month+"]上网记录,第[{}]次尝试请求.....");
				String startDate = DateUtils.getFirstDay(calendar, "yyyy-MM-dd");//指定月月初
				String endDate = DateUtils.getLastDay(calendar,"yyyy-MM-dd");//指定月末
				if(1 == i){
					endDate = DateFormatUtils.format(calendar, "yyyy-MM-dd");
				}
				reqParam.clear();
				reqParam.add(new NameValuePair("pageNo", "-1"));
				reqParam.add(new NameValuePair("pageRecords", "-1"));
				reqParam.add(new NameValuePair("billDetailType", "7"));
				reqParam.add(new NameValuePair("beginTime", startDate));
				reqParam.add(new NameValuePair("endTime", endDate));
				reqParam.add(new NameValuePair("cardType", ""));
				reqParam.add(new NameValuePair("exporttxttype", "1"));
				Page page = getPage(webClient, url, HttpMethod.GET, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, null);
				if(null != page){
					logger.info("==>4.[{}]采集[{}]上网记录成功.", context.getTaskId(), month);
					pages.add(page);
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
			String url = "http://tj.189.cn/tj/service/bill/queryBillInfo.action";
			// 查询最近6个月账单
			for (int i = 1; i < (context.getRecordSize() + 1); i++) {//前6个月(不算当月)
				final String month = DateFormatUtils.format(calendar, "yyyy年MM月");
				String logFlag = String.format("==>5."+i+"["+context.getTaskId()+"]采集["+month+"]账单信息,第[{}]次尝试请求.....");
				reqParam.clear();
				reqParam.add(new NameValuePair("billingCycle1", month));
				reqParam.add(new NameValuePair("dataCode", "009"));
				Page page = getPage(webClient, url, HttpMethod.POST, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, null);
				if(null != page){
					logger.info("==>5.[{}]采集[{}]账单信息成功.", context.getTaskId(), month);
					pages.add(page);
				}else{
					logger.info("==>5.[{}]采集[{}]账单信息结束,没有查询结果.", context.getTaskId(), month);
				}
				calendar.add(Calendar.MONTH, -1);
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
			String logFlag = String.format("==>6.["+context.getTaskId()+"]采集[已办理]套餐信息,第[{}]次尝试请求.....");
			Calendar calendar = Calendar.getInstance();

			String url = "http://tj.189.cn/tj/service/bill/cumulationInfoQuery.action";
			List<NameValuePair> reqParam = new ArrayList<>();
			reqParam.clear();
			reqParam.add(new NameValuePair("requestFlag", "asynchronism"));
			reqParam.add(new NameValuePair("shijian", calendar.getTime()+""));
			Page page = getPage(webClient, url, HttpMethod.POST, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, null);
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
		logger.info("==>7.[{}]暂无亲情号码取样", context.getTaskId());
		return new Result(StatusCode.SUCCESS);
	}

	@Override
	public Result processUserRechargeItemInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
		Result result = new Result();
		try {
			Calendar calendar = Calendar.getInstance();
			List<NameValuePair> reqParam = new ArrayList<>();
			List<Page> pages = new ArrayList<>();
			String url = "http://tj.189.cn/tj/service/bill/queryPaymentRecord.action";
			final String transDateEnd = DateFormatUtils.format(calendar, "yyyy-MM-dd");
			calendar.add(Calendar.MONTH, -6);
			final String transDateBegin = DateFormatUtils.format(calendar, "yyyy-MM-dd");
				
			reqParam.clear();
			reqParam.add(new NameValuePair("paymentHistoryQueryIn.startDate", transDateBegin));
			reqParam.add(new NameValuePair("paymentHistoryQueryIn.endDate", transDateEnd));
			reqParam.add(new NameValuePair("queryWebCard.page", "1"));
			reqParam.add(new NameValuePair("time", calendar.getTime()+""));
			String logFlag = String.format("==>8.["+context.getTaskId()+"]采集[6个月]充值记录,第[{}]次尝试请求.....");
			Page page = getPage(webClient, url, HttpMethod.GET, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, null);
			if (null != page) {// 添加缓存信息
				pages.add(page);
				logger.info("==>8.[{}]采集[6个月]充值记录成功.", context.getTaskId());
			} else {
				logger.info("==>8.[{}]采集[6个月]充值记录结束,没有查询结果.", context.getTaskId());
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
