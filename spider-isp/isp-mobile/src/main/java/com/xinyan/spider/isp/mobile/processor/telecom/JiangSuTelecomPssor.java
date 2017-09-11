package com.xinyan.spider.isp.mobile.processor.telecom;

import java.util.ArrayList;
import java.util.Calendar;
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
import com.xinyan.spider.isp.common.utils.StringUtils;
import com.xinyan.spider.isp.mobile.model.CarrierInfo;
import com.xinyan.spider.isp.mobile.parser.telecom.JiangSuTelecomParser;

/**
 * 江苏电信处理类
 * 
 * @description
 * @author yyj
 * @date 2016年9月6日 下午3:38:43
 * @version V1.0
 */
@Component
public class JiangSuTelecomPssor extends AbstractTelecomPssor {

	protected static Logger logger = LoggerFactory.getLogger(JiangSuTelecomPssor.class);

	@Autowired
	private JiangSuTelecomParser parser;

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
			logger.info("==>[{}]1.用户正常认证开始.....", context.getTaskId());
			getPage(webClient, "http://js.189.cn/service", HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, null);
			
			String returnCode = telecomLogin(webClient, context, true);
			result.setCode(returnCode);
			result.setMsg(StatusCode.getMsg(returnCode));

			logger.info("==>[{}]1.用户正常认证结束{}", context.getTaskId(), result);
		} catch (Exception e) {
			logger.error("==>[{}]1.用户正常认证出现异常:[{}]", context.getTaskId(), e);
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

		try {
			// 1.开始登陆
			logger.info("==>1.[{}]用户图片认证开始.....", context.getTaskId());
			String returnCode = telecomLoginByIMG(webClient, context);
			result.setCode(returnCode);
			result.setMsg(StatusCode.getMsg(returnCode));
			
			logger.info("==>1.[{}].用户图片认证结束{}", context.getTaskId(), result);
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
			logger.info("==>[{}]1.用户短信认证开始.....", context.getTaskId());
			List<NameValuePair> reqParam = new ArrayList<>();
			reqParam.add(new NameValuePair("inventoryVo.accNbr", context.getUserName()));
			reqParam.add(new NameValuePair("inventoryVo.productId", "2000004"));
			reqParam.add(new NameValuePair("inventoryVo.action", "check"));
			reqParam.add(new NameValuePair("inventoryVo.inputRandomPwd", context.getUserInput()));
			Page page = getPage(webClient, "http://js.189.cn/queryValidateSecondPwdAction.action", HttpMethod.GET,
					reqParam,Constants.MAX_SENDMSG_TIIMES,null, null);
			String tmp = page.getWebResponse().getContentAsString();
			if (StringUtils.isNotEmpty(tmp) && tmp.contains("\"TSR_CODE\":\"0000\",")) {
				result.setResult(StatusCode.登陆成功);
			} else {
				result.setResult(StatusCode.发送短信验证码失败);
			}
		} catch (Exception e) {
			logger.error("==>[{}]1.用户短信认证出现异常:[{}]", context.getTaskId(), e);
			result.setResult(StatusCode.登陆出错);
		} finally {
			logger.info("==>[{}]1.用户短信认证结束{}", context.getTaskId(), result);
			// 设置回调
			if (!StatusCode.请输入短信验证码.getCode().equals(result.getCode())
					&& !StatusCode.请输入图片验证码.getCode().equals(result.getCode())) {
				sendLoginMsg(result, context, false, webClient);
			}
		}
		return result;
	}

//	private Result commonLogin(WebClient webClient, Context context) {
//		Map<String, String> header = new HashMap<>();
//		header.put("Referer", "http://js.189.cn/service");
//		List<NameValuePair> reqParam = new ArrayList<>();
//		reqParam.add(new NameValuePair("logonPattern", "2"));
//		reqParam.add(new NameValuePair("userType", "2000004"));
//		reqParam.add(new NameValuePair("newUamType", "-1"));
//		reqParam.add(new NameValuePair("productId", context.getUserName()));
//		reqParam.add(new NameValuePair("userPwd", context.getPassword()));
//		reqParam.add(new NameValuePair("validateCode", context.getUserInput()));
//		Page page = getPage(webClient, "http://js.189.cn/self_service/validateLogin.action", HttpMethod.POST, reqParam,
//				header);
//		String errorMsg = RegexUtils.matchValue("var errorMsg = '(.*?)';", page.getWebResponse().getContentAsString());
//		if (StringUtils.isEmpty(errorMsg)) {
//			JSONObject info = JSONObject.parseObject(page.getWebResponse().getContentAsString());
//			String ssoRequestXML = info.getString("requestXml");
//			if (StringUtils.isNotEmpty(ssoRequestXML)) {
//				String systemCode = info.getString("systemCode");
//				reqParam = new ArrayList<>();
//				reqParam.add(new NameValuePair("SSORequestXML", ssoRequestXML));
//				reqParam.add(new NameValuePair("paramUam", ""));
//				reqParam.add(new NameValuePair("ds", ""));
//				reqParam.add(new NameValuePair("systemCode", systemCode));
//
//				page = getPage(webClient, "https://uam.telecomjs.com/sso/JsLoginIn", HttpMethod.POST, reqParam, null);
//				page.getWebResponse().getResponseHeaderValue("Location");
//				page = getPage(webClient, "http://js.189.cn/user_getSessionInfoforCookie.action", HttpMethod.GET, null, null);
//
//				info = JSONObject.parseObject(page.getWebResponse().getContentAsString());
//				String isLogin = info.getString("isLogin");
//				if ("yes".equals(isLogin)) {
//					reqParam = new ArrayList<>();
//					reqParam.add(new NameValuePair("inventoryVo.accNbr", context.getUserName()));
//					reqParam.add(new NameValuePair("inventoryVo.productId", "2000004"));
//					reqParam.add(new NameValuePair("inventoryVo.action", "generate"));
//					reqParam.add(new NameValuePair("inventoryVo.inputRandomPwd", ""));
//					page = getPage(webClient, "http://js.189.cn/queryValidateSecondPwdAction.action", HttpMethod.GET,
//							reqParam, null);
//					String tmp = page.getWebResponse().getContentAsString();
//					if (StringUtils.isNotEmpty(tmp) && tmp.contains("\"TSR_CODE\":\"0000\",")) {
//						logger.info("==>[{}]已经发送短信验证码...", context.getTaskId());
//						Result result = new Result();
//						// 通知前端发送成功
//						result.setResult(StatusCode.请输入短信验证码);
//						context.setTaskSubStatus(Constants.VALIDATE_TYPE_CODE_SMS);// 需要设置回调子状态
//						sendValidataMsg(result, context, false, webClient, Constants.DEQUEUE_TIME_OUT, "");
//						return result;
//					} else {
//						return new Result(StatusCode.发送短信验证码失败);
//					}
//				} else {
//					return new Result(StatusCode.登陆失败);
//				}
//			} else {
//				return new Result(StatusCode.登陆失败);
//			}
//		} else if (errorMsg.contains("验证码错误")) {
//			return new Result(StatusCode.图片验证码错误);
//		} else if (errorMsg.contains("帐号或密码不正确")) {
//			return new Result(StatusCode.用户名或密码错误);
//		} else {
//			logger.info("==>[{}]江苏电信登陆失败:{}", context.getTaskId(), errorMsg);
//			return new Result(StatusCode.登陆失败);
//		}
//	}

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
		} catch (Exception ex) {
			logger.error("==>[{}]江苏电信数据解析出现异常:[{}]", context.getTaskId(), ex);
			result.setFail();
			return result;
		} finally {
			sendAnalysisMsg(result, context);
		}

		getLogger().info("==>[{}]数据抓取结束{}\n,详情:{}", context.getTaskId(), result, result.getData());
		return result;
	}

	@Override
	public Result processBaseInfo(WebClient webClient, Context context, CarrierInfo carrierInfo,
			CacheContainer cacheContainer) {
		Result result = new Result();
		String url = "";
		try {
			url = "http://www.189.cn/login/skip/uam.do?method=skip&shopId=10011&toStUrl=http://js.189.cn/service";
			Page page = getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, null);
			
			logger.info("==>1[{}]采集基本信息开始.....", context.getTaskId());
			url = "http://js.189.cn/getSessionInfo.action";
			page = getPage(webClient, url, HttpMethod.POST, null,Constants.MAX_SENDMSG_TIIMES,null, null);
			cacheContainer.putPage(ProcessorCode.BASIC_INFO.getCode(), page);// 添加缓存信息
			
			logger.info("==>1[{}]采集基本信息结束.",context.getTaskId());
			result.setSuccess();
		} catch (Exception e) {
			logger.error("==>1.[{}]采集基本信息出现异常:[{}]",context.getTaskId(), e);
			result.setResult(StatusCode.解析基础信息出错);
			return result;
		}
		return result;
	}

	@Override
	public Result processCallRecordInfo(WebClient webClient, Context context, CarrierInfo carrierInfo,
			CacheContainer cacheContainer) {
		Result result = new Result();
		Map<String, String> header = new HashMap<>();
		header.put("Referer", "http://js.189.cn/service/bill");
		List<NameValuePair> reqParam = new ArrayList<>();
		List<Page> pages = new ArrayList<>();

		String url = "http://js.189.cn/queryVoiceMsgAction.action";
		try {
			Calendar calendar = Calendar.getInstance();
			// 查询最近6个月通话详单
			for (int i = 1; i < (context.getRecordSize() + 1); i++) {
				calendar.set(Calendar.DAY_OF_MONTH, 1);
				final String transDateBegin = DateFormatUtils.format(calendar, "yyyyMMdd");
				calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
				final String month = DateFormatUtils.format(calendar, "yyyy-MM");

				String transDateEnd = "";
				if (1 == i) {
					transDateEnd = DateUtils.getCurrentDate2();
				} else {
					transDateEnd = DateFormatUtils.format(calendar, "yyyyMMdd");
				}

				String logFlag = String.format("==>1."+i+"[" + context.getTaskId() + "]采集[" + month + "]通话详单,第[{}]次尝试请求.....");
				reqParam.clear();
				reqParam.add(new NameValuePair("inventoryVo.accNbr", context.getUserName()));
				reqParam.add(new NameValuePair("inventoryVo.getFlag", "1"));
				reqParam.add(new NameValuePair("inventoryVo.begDate", transDateBegin));
				reqParam.add(new NameValuePair("inventoryVo.endDate", transDateEnd));
				reqParam.add(new NameValuePair("inventoryVo.family", "4"));
				reqParam.add(new NameValuePair("inventoryVo.accNbr97", ""));
				reqParam.add(new NameValuePair("inventoryVo.productId", "4"));
				reqParam.add(new NameValuePair("inventoryVo.acctName", context.getUserName()));

				Page page = getPage(webClient, url, HttpMethod.POST, reqParam, Constants.MAX_SENDMSG_TIIMES, logFlag, header);
				if (null != page) {// 添加缓存信息
					pages.add(page);
					logger.info("==>2.[{}]采集[{}]通话详单成功.", context.getTaskId(), month);
				} else {
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
	public Result processSmsInfo(WebClient webClient, Context context, CarrierInfo carrierInfo,
			CacheContainer cacheContainer) {
		Result result = new Result();
		Map<String, String> header = new HashMap<>();
		header.put("Referer", "http://js.189.cn/service/bill");
		List<NameValuePair> reqParam = new ArrayList<>();
		List<Page> pages = new ArrayList<>();

		String url = "http://js.189.cn/mobileInventoryAction.action";
		try {
			Calendar calendar = Calendar.getInstance();
			// 查询最近6个月短信记录
			for (int i = 1; i < (context.getRecordSize() + 1); i++) {
				calendar.set(Calendar.DAY_OF_MONTH, 1);
				final String transDateBegin = DateFormatUtils.format(calendar, "yyyyMMdd");
				calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
				final String month = DateFormatUtils.format(calendar, "yyyy-MM");

				String transDateEnd = "";
				if (1 == i) {
					transDateEnd = DateUtils.getCurrentDate2();
				} else {
					transDateEnd = DateFormatUtils.format(calendar, "yyyyMMdd");
				}

				String logFlag = String.format("==>3." + i + "[" + context.getTaskId() + "]采集[" + month + "]短信记录,第[{}]次尝试请求.....");
				reqParam.clear();
				reqParam.add(new NameValuePair("inventoryVo.accNbr", context.getUserName()));
				reqParam.add(new NameValuePair("inventoryVo.getFlag", "1"));
				reqParam.add(new NameValuePair("inventoryVo.begDate", transDateBegin));
				reqParam.add(new NameValuePair("inventoryVo.endDate", transDateEnd));
				reqParam.add(new NameValuePair("inventoryVo.family", "4"));
				reqParam.add(new NameValuePair("inventoryVo.accNbr97", ""));
				reqParam.add(new NameValuePair("inventoryVo.productId", "4"));
				reqParam.add(new NameValuePair("inventoryVo.acctName", context.getUserName()));

				Page page = getPage(webClient, url, HttpMethod.POST, reqParam, Constants.MAX_SENDMSG_TIIMES, logFlag, header);

				if (null != page) {// 添加缓存信息
					pages.add(page);
					logger.info("==>3.[{}]采集[{}]短信记录成功.", context.getTaskId(), month);
				} else {
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
		Map<String, String> header = new HashMap<>();
		header.put("Referer", "http://js.189.cn/service/bill");
		List<NameValuePair> reqParam = new ArrayList<>();
		List<Page> pages = new ArrayList<>();
		String url = "http://js.189.cn/queryNewDataMsgListAction.action";
		try {
			Calendar calendar = Calendar.getInstance();
			// 查询最近6个月上网记录
			for (int i = 1; i < (context.getRecordSize() + 1); i++) {
				calendar.set(Calendar.DAY_OF_MONTH, 1);
				final String transDateBegin = DateFormatUtils.format(calendar, "yyyyMMdd");
				calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
				final String month = DateFormatUtils.format(calendar, "yyyy-MM");

				String transDateEnd = "";
				if (1 == i) {
					transDateEnd = DateUtils.getCurrentDate2();
				} else {
					transDateEnd = DateFormatUtils.format(calendar, "yyyyMMdd");
				}

				String logFlag = String.format("==>4." + i + "[" + context.getTaskId() + "]采集[" + month + "]上网记录,第[{}]次尝试请求.....");
				reqParam.clear();
				reqParam.add(new NameValuePair("inventoryVo.accNbr", context.getUserName()));
				reqParam.add(new NameValuePair("inventoryVo.getFlag", "1"));
				reqParam.add(new NameValuePair("inventoryVo.begDate", transDateBegin));
				reqParam.add(new NameValuePair("inventoryVo.endDate", transDateEnd));
				reqParam.add(new NameValuePair("inventoryVo.family", "4"));
				reqParam.add(new NameValuePair("inventoryVo.accNbr97", ""));
				reqParam.add(new NameValuePair("inventoryVo.productId", "4"));
				reqParam.add(new NameValuePair("inventoryVo.acctName", context.getUserName()));

				Page page = getPage(webClient, url, HttpMethod.POST, reqParam, Constants.MAX_SENDMSG_TIIMES, logFlag, header);
				if (null != page) {// 添加缓存信息
					pages.add(page);
					logger.info("==>4.[{}]采集[{}]上网记录成功.", context.getTaskId(), month);
				} else {
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
	public Result processBillInfo(WebClient webClient, Context context, CarrierInfo carrierInfo,
			CacheContainer cacheContainer) {
		Result result = new Result();
		try {
			Calendar calendar = Calendar.getInstance();
			Map<String, String> header = new HashMap<>();
			header.put("Referer", "http://js.189.cn/service/bill?tabFlag=billing1");
			List<NameValuePair> reqParam = new ArrayList<>();
			List<Page> pages = new ArrayList<>();

			String url = "http://js.189.cn/chargeQuery/chargeQuery_queryCustBill.action";
			// 查询最近6个月账单
			for (int i = 1; i < (context.getRecordSize() + 1); i++) {
				final String transDateBegin = DateFormatUtils.format(calendar, "yyyyMM");
				final String month = DateFormatUtils.format(calendar, "yyyy-MM");

				reqParam.clear();
				reqParam.add(new NameValuePair("billingCycleId", transDateBegin));
				reqParam.add(new NameValuePair("queryFlag", "0"));
				reqParam.add(new NameValuePair("productId", "2"));
				reqParam.add(new NameValuePair("accNbr", context.getUserName()));
				String logFlag = String.format("==>5." + i + "[" + context.getTaskId() + "]采集[" + month + "]账单信息,第[{}]次尝试请求.....");

				Page page = getPage(webClient, url, HttpMethod.POST, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, header);
				if (null != page) {// 添加缓存信息
					pages.add(page);
					logger.info("==>5.[{}]采集[{}]账单信息成功.", context.getTaskId(), month);
				} else {
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
	public Result processPackageItemInfo(WebClient webClient, Context context, CarrierInfo carrierInfo,
			CacheContainer cacheContainer) {
		Result result = new Result();
		try {
			Calendar calendar = Calendar.getInstance();
			Map<String, String> header = new HashMap<>();
			header.put("Referer", "http://js.189.cn/service/bill?tabFlag=billing1");
			List<NameValuePair> reqParam = new ArrayList<>();
			List<Page> pages = new ArrayList<>();

			String url = "http://js.189.cn/userLogin/service/queryWare.action";
			// 查询最近6个月套餐信息
			for (int i = 1; i < (context.getRecordSize() + 1); i++) {
				calendar.set(Calendar.DAY_OF_MONTH, 1);
				final String transDateBegin = DateFormatUtils.format(calendar, "yyyyMMdd");
				final String month = DateFormatUtils.format(calendar, "yyyy-MM");

				reqParam.clear();
				reqParam.add(new NameValuePair("userID", context.getUserName()));
				reqParam.add(new NameValuePair("userType", "4"));
				reqParam.add(new NameValuePair("selectTime", transDateBegin));
				reqParam.add(new NameValuePair("Action", "post"));
				reqParam.add(new NameValuePair("Name", "forWare"));
				String logFlag = String.format("==>6." + i + "[" + context.getTaskId() + "]采集[" + month + "]套餐信息,第[{}]次尝试请求.....");

				Page page = getPage(webClient, url, HttpMethod.POST, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, header);
				if (null != page) {// 添加缓存信息
					pages.add(page);
					logger.info("==>6.[{}]采集[{}]套餐信息成功.", context.getTaskId(), month);
				} else {
					logger.info("==>6.[{}]采集[{}]套餐信息结束,没有查询结果.", context.getTaskId(), month);
				}
				calendar.add(Calendar.MONTH, -1);
			}
			cacheContainer.putPages(ProcessorCode.PACKAGE_ITEM.getCode(), pages);
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
		logger.info("==>7.暂无亲情号码取样");
		return new Result(StatusCode.SUCCESS);
	}

	@Override
	public Result processUserRechargeItemInfo(WebClient webClient, Context context, CarrierInfo carrierInfo,
			CacheContainer cacheContainer) {
		Result result = new Result();
		try {
			Calendar calendar = Calendar.getInstance();
			Map<String, String> header = new HashMap<>();
			header.put("Referer", "http://js.189.cn/queryRechargeInfo/forwadToQueryRecharge.action");
			List<NameValuePair> reqParam = new ArrayList<>();
			List<Page> pages = new ArrayList<>();
			String url = "http://js.189.cn/queryRechargeRecord.action";
			
			// 查询最近6个月充值记录
			for (int i = 1; i < (context.getRecordSize() + 1); i++) {
				calendar.set(Calendar.DAY_OF_MONTH, 1);
				final String beginDate = DateFormatUtils.format(calendar, "yyyyMMdd");
				calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
				final String endDate = DateFormatUtils.format(calendar, "yyyyMMdd");

				reqParam.clear();
				reqParam.add(new NameValuePair("queryType", "0"));
				reqParam.add(new NameValuePair("queryNbr", context.getUserName()));
				reqParam.add(new NameValuePair("proType", "4"));
				reqParam.add(new NameValuePair("begDate", beginDate));
				reqParam.add(new NameValuePair("endDate", endDate));
				reqParam.add(new NameValuePair("Action", "post"));
				reqParam.add(new NameValuePair("Name", "queryRecharge"));

				String logFlag = String.format("==>8." + i + "[" + context.getTaskId() + "]采集[" + beginDate + "]充值记录,第[{}]次尝试请求.....");
				Page page = getPage(webClient, url, HttpMethod.POST, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, header);
				if (null != page) {// 添加缓存信息
					pages.add(page);
					logger.info("==>8.[{}]采集[{}]充值记录成功.", context.getTaskId(), beginDate);
				} else {
					logger.info("==>8.[{}]采集[{}]充值记录结束,没有查询结果.", context.getTaskId(), beginDate);
				}
				calendar.add(Calendar.MONTH, -1);
			}
			cacheContainer.putPages(ProcessorCode.RECHARGE_INFO.getCode(), pages);
			result.setSuccess();
		} catch (Exception e) {
			logger.error("==>8.解析充值记录出现异常:[{}]", context.getTaskId(), e);
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
