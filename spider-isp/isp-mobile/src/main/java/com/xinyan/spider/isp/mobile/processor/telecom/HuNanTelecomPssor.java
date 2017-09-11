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
import com.xinyan.spider.isp.common.utils.StringUtils;
import com.xinyan.spider.isp.mobile.model.CarrierInfo;
import com.xinyan.spider.isp.mobile.parser.telecom.HuNanTelecomParser;

/**
 * 湖南电信处理类
 * @description
 * @author yyj
 * @date 2017年4月21日 下午3:38:43
 * @version V1.0
 */
@Component
public class HuNanTelecomPssor extends AbstractTelecomPssor {

	protected static Logger logger = LoggerFactory.getLogger(HuNanTelecomPssor.class);

	@Autowired
	private HuNanTelecomParser parser;

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

			String url = "http://hn.189.cn/grouplogin";
			getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, null);

			String returnCode = telecomLogin(webClient, context, true); 
			if(StatusCode.系统忙.getCode().equals(returnCode)){
				url = "http://hn.189.cn/hnselfservice/homepage/home-page!topupDiv.action";
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
			String url = "http://hn.189.cn/hnselfservice/billquery/bill-query!queryBillList.action";

			Calendar calendar = Calendar.getInstance();

			List<NameValuePair> reqParam = new ArrayList<>();
			reqParam.clear();
    		reqParam.add(new NameValuePair("tm", "2033%E4%B8%8B%E5%8D%884:31:23"));
    		reqParam.add(new NameValuePair("tabIndex", "2"));
    		reqParam.add(new NameValuePair("queryMonth", DateFormatUtils.format(calendar, "yyyy-MM")));
    		reqParam.add(new NameValuePair("valicode", context.getUserInput()));
    		reqParam.add(new NameValuePair("accNbr", context.getUserName()));
    		reqParam.add(new NameValuePair("chargeType", "10"));
    		reqParam.add(new NameValuePair("_", Calendar.getInstance().getTimeInMillis()+""));
    		Page page = getPage(webClient, url, HttpMethod.GET, reqParam,Constants.MAX_SENDMSG_TIIMES,null, null);
    		
    		if(StringUtils.contains(page.getWebResponse().getContentAsString(), "导出Excel")){
    			logger.info("==>1.[{}]短信验证码正确.", context.getTaskId());	
    			result.setResult(StatusCode.登陆成功);
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
				sendLoginMsg(result, context, false, webClient);
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
		logger.info("==>[{}]正在请求发送短信验证码.....", context.getTaskId());
		Result result = new Result();
		
		String url = "http://www.189.cn/dqmh/ssoLink.do?method=linkTo&platNo=10019&toStUrl=http://hn.189.cn";
		Page page = getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, null);
		
		List<NameValuePair> reqParam = new ArrayList<>();
		reqParam.add(new NameValuePair("tm", "2033%E4%B8%8B%E5%8D%884:31:23"));
		reqParam.add(new NameValuePair("tabIndex", "2"));
		reqParam.add(new NameValuePair("queryMonth", ""));
		reqParam.add(new NameValuePair("valicode", ""));
		reqParam.add(new NameValuePair("accNbr", context.getUserName()));
		reqParam.add(new NameValuePair("chargeType", "10"));
		reqParam.add(new NameValuePair("_", Calendar.getInstance().getTimeInMillis()+""));
		
		url = "http://hn.189.cn/hnselfservice/billquery/bill-query!queryBillList.action";
		page = getPage(webClient, url, HttpMethod.GET, reqParam, Constants.MAX_SENDMSG_TIIMES,null,null);
		if(StringUtils.contains(page.getWebResponse().getContentAsString(), "清单查询结果")){
			logger.info("==>[{}]已经发送短信验证码...", context.getTaskId());

			// 通知前端发送成功
			result.setResult(StatusCode.请输入短信验证码);
			context.setTaskSubStatus(Constants.VALIDATE_TYPE_CODE_SMS);// 需要设置回调子状态
			sendValidataMsg(result, context, false, webClient, Constants.DEQUEUE_TIME_OUT, "");
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
			logger.error("==>[{}]湖南数据解析出现异常:[{}]", context.getTaskId(), ex);
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
			logger.info("==>1.1[{}]采集基本信息开始.....", context.getTaskId());

			url = "http://www.189.cn/dqmh/ssoLink.do?method=linkTo&platNo=10019&toStUrl=http://hn.189.cn";
			Page resultPage = getPage(webClient, url, HttpMethod.GET, null, Constants.MAX_SENDMSG_TIIMES,null,null);
			cacheContainer.putPage(ProcessorCode.BASIC_INFO.getCode(), resultPage);//添加缓存信息

			url = "http://hn.189.cn/hnselfservice/customerinfomanager/customer-info!queryTaocan.action";
			resultPage = getPage(webClient, url, HttpMethod.GET, null, Constants.MAX_SENDMSG_TIIMES,null,null);
			String pageInfo = resultPage.getWebResponse().getContentAsString();
			cacheContainer.putString(ProcessorCode.BASIC_INFO.getCode()+"1", pageInfo);//添加缓存信息

			url = "http://hn.189.cn/hnselfservice/customerinfomanager/customer-info!queryCustInfo.action";
			resultPage = getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, null);
			pageInfo = resultPage.getWebResponse().getContentAsString();
			cacheContainer.putString(ProcessorCode.BASIC_INFO.getCode()+"2", pageInfo);//添加缓存信息

			url = "http://hn.189.cn/webportal-wt/hnselfservice/billquery/bill-query!queryBanlance.action?_z=1&cityCode=hn&fastcode=10000278";
			resultPage = getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, null);
			pageInfo = resultPage.getWebResponse().getContentAsString();
			cacheContainer.putString(ProcessorCode.BASIC_INFO.getCode()+"3", pageInfo);//添加缓存信息
			
			result.setSuccess();
		} catch (Exception e) {
			logger.error("==>1.[{}]采集基本信息出现异常:[{}]", context.getTaskId(), e);
			result.setResult(StatusCode.解析基础信息出错);
			return result;
		}
		return result;
	}

	@Override
	public Result processCallRecordInfo(WebClient webClient, Context context, CarrierInfo carrierInfo,
			CacheContainer cacheContainer) {
		Result result = new Result();
		List<NameValuePair> reqParam = new ArrayList<>();
		List<Page> pages = new ArrayList<>();
		String url = "http://hn.189.cn/hnselfservice/billquery/qry-exp!exportBillList.action";

		try {
			Calendar calendar = Calendar.getInstance();
			// 查询最近6个月通话详单
			for (int i = 1; i < (context.getRecordSize() + 1); i++) {//包含当前月份
				final String month = DateFormatUtils.format(calendar, "yyyy-MM");

				String logFlag = String.format("==>2."+i+"["+context.getTaskId()+"]采集["+month+"]通话详单,第[{}]次尝试请求.....");
				reqParam.clear();
				reqParam.add(new NameValuePair("tabIndex", "2"));
				reqParam.add(new NameValuePair("queryMonth", month));
				reqParam.add(new NameValuePair("patitype", "2"));
				reqParam.add(new NameValuePair("valicode", "undefined"));
				reqParam.add(new NameValuePair("accNbr", context.getUserName()));
				reqParam.add(new NameValuePair("chargeType", "10"));

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
	public Result processSmsInfo(WebClient webClient, Context context, CarrierInfo carrierInfo,
			CacheContainer cacheContainer) {
		Result result = new Result();
		List<NameValuePair> reqParam = new ArrayList<>();
		List<Page> pages = new ArrayList<>();

		String url = "http://hn.189.cn/hnselfservice/billquery/qry-exp!exportBillList.action";
		try {
			Calendar calendar = Calendar.getInstance();
			// 查询最近6个月短信记录
			for (int i = 1; i < (context.getRecordSize() + 1); i++) {//包含当前月份
				calendar.set(Calendar.DAY_OF_MONTH, 1);
				final String transDateBegin = DateFormatUtils.format(calendar, "yyyyMMdd");
				calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
				String transDateEnd = DateFormatUtils.format(calendar, "yyyyMMdd");
				final String month = DateFormatUtils.format(calendar, "yyyy-MM");

				String logFlag = String.format("==>3."+i+"["+context.getTaskId()+"]采集["+month+"]短信记录,第[{}]次尝试请求.....");
				reqParam.clear();
				reqParam.add(new NameValuePair("tabIndex", "2"));
				reqParam.add(new NameValuePair("queryMonth", month));
				reqParam.add(new NameValuePair("patitype", "12"));
				reqParam.add(new NameValuePair("startDay", transDateBegin));
				reqParam.add(new NameValuePair("endDay", transDateEnd));
				reqParam.add(new NameValuePair("valicode", "undefined"));
				reqParam.add(new NameValuePair("accNbr", context.getUserName()));

				Page page = getPage(webClient, url, HttpMethod.GET, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, null);

				if(null == page){
					logger.info("==>3.[{}]采集[{}]短信记录结束,没有查询结果.", context.getTaskId(), month);
				}else if(StringUtils.contains(page.getWebResponse().getContentAsString(), "请输入验证码")){//请输入验证码
					result.setResult(StatusCode.短信验证码已失效);
	                return result;
				}else if(null != page){//添加缓存信息
					pages.add(page);
					logger.info("==>3.[{}]采集[{}]短信记录成功.", context.getTaskId(), month);
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
		List<String> pages = new ArrayList<>();
		String url = "http://hn.189.cn/hnselfservice/billquery/qry-exp!exportBillList.action";
		try {
			Calendar calendar = Calendar.getInstance();
			// 查询最近6个月上网记录
			for (int i = 1; i < (context.getRecordSize() + 1); i++) {//包含当前月份
				calendar.set(Calendar.DAY_OF_MONTH, 1);
				final String month = DateFormatUtils.format(calendar, "yyyy-MM");

				String logFlag = String.format("==>4."+i+"["+context.getTaskId()+"]采集["+month+"]上网记录,第[{}]次尝试请求.....");
				reqParam.clear();
				reqParam.add(new NameValuePair("tabIndex", "2"));
				reqParam.add(new NameValuePair("queryMonth", month));
				reqParam.add(new NameValuePair("patitype", "9"));
				reqParam.add(new NameValuePair("startDay", "1"));
				reqParam.add(new NameValuePair("endDay", "15"));
				reqParam.add(new NameValuePair("valicode", "undefined"));
				reqParam.add(new NameValuePair("accNbr", context.getUserName()));

				Page page = getPage(webClient, url, HttpMethod.GET, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, null);
				
				if(StringUtils.contains(page.getWebResponse().getContentAsString(), "请输入验证码")){//请输入验证码
					result.setResult(StatusCode.短信验证码已失效);
	                return result;
				}else if(null != page){//添加缓存信息
					String tmp = page.getWebResponse().getContentAsString();
					tmp = tmp.replaceAll("\\s", "").replaceAll("&nbsp;", "");
					int start = 0;
					if(tmp.indexOf("序号")> start){
						start = tmp.indexOf("序号");
					}
					int end = 51200;
					if(tmp.length() < end){
						end = tmp.length();
					}
					pages.add(tmp.substring(start, end));
					logger.info("==>4.[{}]采集[{}]上网记录成功.", context.getTaskId(), month);
				}
				calendar.add(Calendar.MONTH, -1);
			}

			cacheContainer.putStrings(ProcessorCode.NET_INFO.getCode(), pages);
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
			List<NameValuePair> reqParam = new ArrayList<>();
			List<Page> pages = new ArrayList<>();
			Map<String,String> header = new HashMap<>();
			header.put("referer","http://hn.189.cn/webportal-wt/login!getUserInfoToSession.do");

			String url = "http://hn.189.cn/hnselfservice/billquery/bill-query!queryUserBillDetail.action";
			// 查询最近6个月账单
			for (int i = 1; i < (context.getRecordSize() + 1); i++) {//前6个月(不算当月)-->可算当月
//				calendar.add(Calendar.MONTH, -1);
				final String month = DateFormatUtils.format(calendar, "yyyy-MM");
				reqParam.clear();
				reqParam.add(new NameValuePair("productId", context.getUserName()));
				reqParam.add(new NameValuePair("chargeType", "10"));
				reqParam.add(new NameValuePair("queryMonth", month));

				String logFlag = String.format("==>5."+i+"["+context.getTaskId()+"]采集["+month+"]账单信息,第[{}]次尝试请求.....");

				Page page = getPage(webClient, url,
						HttpMethod.GET, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, header);
				if(null == page){
					logger.info("==>5.[{}]采集[{}]账单信息结束,没有查询结果.", context.getTaskId(), month);
				}else if(StringUtils.contains(page.getWebResponse().getContentAsString(),"查不到相应月份的账户级账单")){
					logger.info("==>5.[{}]采集[{}]账单信息结束,查不到相应月份的账户级账单.", context.getTaskId(), month);
				}else if(null != page){//添加缓存信息
					logger.info("==>5.[{}]采集[{}]账单信息成功.", context.getTaskId(), month);
					pages.add(page);
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
			header.put("Referer", "http://hn.189.cn/webportal-wt/hnselfservice/billquery/bill-query!queryPackageUse.action");
			List<NameValuePair> reqParam = new ArrayList<>();
			List<Page> pages = new ArrayList<>();

			String url = "http://hn.189.cn/webportal-wt/hnselfservice/billquery/bill-query!queryPackageUse.action";
			
			// 查询最近6个月套餐信息
			for (int i = 1; i < (context.getRecordSize() + 1); i++) {
				calendar.set(Calendar.DAY_OF_MONTH, 1);
				final String queryMonth = DateFormatUtils.format(calendar, "yyyyMM");

				reqParam.clear();
				reqParam.add(new NameValuePair("queryMonth", queryMonth));
				String logFlag = String.format("==>6." + i + "["+context.getTaskId()+"]采集[" + queryMonth + "]套餐信息,第[{}]次尝试请求.....");

				Page page = getPage(webClient, url, HttpMethod.POST, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, header);
				if (null != page) {// 添加缓存信息
					pages.add(page);
					logger.info("==>6.[{}]采集[{}]套餐信息成功.", context.getTaskId(), queryMonth);
				} else {
					logger.info("==>6.[{}]采集[{}]套餐信息结束,没有查询结果.", context.getTaskId(), queryMonth);
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
		logger.info("==>7.[{}]暂无亲情号码取样");
		return new Result(StatusCode.SUCCESS);
	}

	@Override
	public Result processUserRechargeItemInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
		Result result = new Result();
		try {
			Calendar calendar = Calendar.getInstance();
			Map<String, String> header = new HashMap<>();
			header.put("Referer", "http://hn.189.cn/webportal-wt/hnselfservice/topupquery/topup-query!queryChargePaymentRecordList.do?_z=1&cityCode=hn&fastcode=10000276");
			List<NameValuePair> reqParam = new ArrayList<>();
			List<Page> pages = new ArrayList<>();
			String url = "http://hn.189.cn/webportal-wt/hnselfservice/topupquery/topup-query!queryChargePaymentRecordList.do";
			
			// 查询最近6个月充值记录
			for (int i = 1; i < (context.getRecordSize() + 1); i++) {
				final String queryMonth = DateFormatUtils.format(calendar, "yyyy-MM");
				calendar.set(Calendar.DAY_OF_MONTH, 1);
				final String beginDate = DateFormatUtils.format(calendar, "yyyy-MM-dd");
				calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
				final String endDate = DateFormatUtils.format(calendar, "yyyy-MM-dd");

				reqParam.clear();
				reqParam.add(new NameValuePair("queryType", queryMonth));
				reqParam.add(new NameValuePair("selectMonth", beginDate +","+ endDate));
				String logFlag = String.format("==>8." + i + "["+context.getTaskId()+"]采集[" + queryMonth + "]充值记录,第[{}]次尝试请求.....");
				Page page = getPage(webClient, url, HttpMethod.GET, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, header);
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
