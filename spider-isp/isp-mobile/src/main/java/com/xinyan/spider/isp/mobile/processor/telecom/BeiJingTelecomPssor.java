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
import com.xinyan.spider.isp.common.utils.RegexUtils;
import com.xinyan.spider.isp.common.utils.StringUtils;
import com.xinyan.spider.isp.mobile.model.CarrierInfo;
import com.xinyan.spider.isp.mobile.parser.telecom.BeiJingTelecomParser;

/**
 * 北京电信处理类
 * @description
 * @author yyj
 * @date 2017年5月31日 下午3:38:43
 * @version V1.0
 */
@Component
public class BeiJingTelecomPssor extends AbstractTelecomPssor {

	protected static Logger logger = LoggerFactory.getLogger(BeiJingTelecomPssor.class);

	@Autowired
	private BeiJingTelecomParser parser;

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

			String url = "http://www.189.cn/bj/";
			getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, null);
			String returnCode = telecomLogin(webClient, context, true);
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
			logger.info("==>1.[{}]用户图片认证结束[{}]", context.getTaskId(), result);
		} catch (Exception e) {
			logger.error("==>1.[{}]用户图片认证出现异常:[{}]", context.getTaskId(), e);
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
			String url = "http://bj.189.cn/iframe/feequery/detailValidCode.action";
			List<NameValuePair> reqParam = new ArrayList<>();
			reqParam.clear();
			reqParam.add(new NameValuePair("requestFlag", "asynchronism"));
			reqParam.add(new NameValuePair("accNum", context.getUserName()));
			reqParam.add(new NameValuePair("randCode", context.getUserInput()));
			context.setParam1(context.getUserInput());

			Page page = getPage(webClient, url, HttpMethod.POST, reqParam,Constants.MAX_SENDMSG_TIIMES,null, null);
			if(page.getWebResponse().getContentAsString().contains("\"ILoginType\":4")){
				logger.info("==>1.[{}]短信验证码正确.", context.getTaskId());
				result.setResult(StatusCode.登陆成功);
			}else{
				result.setResult(StatusCode.短信验证码错误);
			}
		} catch (Exception e) {
			logger.error("==>1.[{}]用户短信认证出现异常:[{}]", context.getTaskId(), e);
			result.setResult(StatusCode.登陆出错);
		} finally {
			logger.info("==>1.[{}]用户短信认证结束[{}]", context.getTaskId(), result);
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
		String url = "http://www.189.cn/login/sso/ecs.do?method=linkTo&platNo=10001&toStUrl=http://bj.189.cn/iframe/feequery/detailBillIndex.action?fastcode=01390638&cityCode=bj";
		Page page = getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, null);

		HashMap<String, String > header = new HashMap<>();
		header.put("Referer","http://bj.189.cn/iframe/feequery/detailBillIndex.action?fastcode=01390638&cityCode=bj");
		url = "http://bj.189.cn/iframe/feequery/smsRandCodeSend.action";
		List<NameValuePair> reqParam = new ArrayList<>();
		reqParam.add(new NameValuePair("accNum", context.getUserName()));
		page = getPage(webClient, url, HttpMethod.POST, reqParam,Constants.MAX_SENDMSG_TIIMES,null, null);
		if(page.getWebResponse().getContentAsString().contains("\"ILoginType\":4")){
			logger.info("==>[{}]已经发送短信验证码...", context.getTaskId());
			// 通知前端发送成功
			result.setResult(StatusCode.请输入短信验证码);
			context.setTaskSubStatus(Constants.VALIDATE_TYPE_CODE_SMS);// 需要设置回调子状态
			sendValidataMsg(result, context, true, webClient, Constants.DEQUEUE_TIME_OUT, "");
		}else{//获发送短信验证码失败
			result.setResult(StatusCode.发送短信验证码失败);
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
			getLogger().error("==>[{}]北京数据解析出现异常:[{}]", context.getTaskId(), ex);
			result.setFail();
			return result;
		} finally {
			sendAnalysisMsg(result, context);
		}

		getLogger().info("==>[{}]数据抓取结束[{}]\n,详情:{}", context.getTaskId(), result, result.getData());
		return result;
	}

	@Override
	public Result processBaseInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
		Result result = new Result();
        Map<String, String> header = new HashMap<>();
        List<NameValuePair> reqParam = new ArrayList<>();
		String url = "";
		try {
			url = "http://www.189.cn/login/sso/ecs.do?method=linkTo&platNo=10001&toStUrl=http://bj.189.cn/iframe/custservice/modifyUserInfo.action?fastcode=10000181&cityCode=bj";
			getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, null);

			logger.info("==>1.1[{}]采集基本信息开始.....", context.getTaskId());
			url = "http://bj.189.cn/iframe/custservice/modifyUserInfo.action?fastcode=10000181&cityCode=bj";
			Page page = getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, null);
			if(null != page){//添加缓存信息
				cacheContainer.putPage(ProcessorCode.BASIC_INFO.getCode(), page);
			}
			logger.info("==>1.1[{}]采集套餐信息开始.....", context.getTaskId());
			reqParam.add(new NameValuePair("requestFlag","asynchronism"));
			reqParam.add(new NameValuePair("orderRelaQuerypnum",context.getUserName()));
			url = "http://bj.189.cn/iframe/custquery/orderRelaQuery.action";
			page = getPage(webClient, url, HttpMethod.POST, reqParam,Constants.MAX_SENDMSG_TIIMES,null, null);
			if(null != page){//添加缓存信息
				cacheContainer.putPage(ProcessorCode.OTHER_INFO.getCode(), page);
			}
			url = "http://www.189.cn/dqmh/userCenter/userInfo.do?method=editUserInfo_new&fastcode=10000177&cityCode=bj";
			page = getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, null);
			if(null != page){//添加缓存信息
				cacheContainer.putPage(ProcessorCode.POINTS_VALUE.getCode(), page);
			}
			logger.info("==>1.1[{}]采集基本信息结束.", context.getTaskId());

			logger.info("==>1.2[{}]采集个人余额开始.....", context.getTaskId());
			
			url = "http://www.189.cn/login/sso/ecs.do?method=linkTo&platNo=10001&toStUrl=http://bj.189.cn/iframe/feequery/custFeeIndex.action?fastcode=01390635&tab=1&cityCode=bj";
            getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, null);

            header.clear();
            header.put("referer","http://bj.189.cn/iframe/feequery/custFeeIndex.action?fastcode=01390635&tab=1&cityCode=bj");
            reqParam.clear();
            reqParam.add(new NameValuePair("requestFlag", "asynchronism"));
            reqParam.add(new NameValuePair("p1QueryFlag", "2"));
            reqParam.add(new NameValuePair("accNum", context.getUserName()));
            reqParam.add(new NameValuePair("time", Calendar.getInstance().getTimeInMillis()+""));
            //获取余额
            url = "http://bj.189.cn/iframe/feequery/qryBalance.action";
            page = getPage(webClient, url, HttpMethod.POST, reqParam,Constants.MAX_SENDMSG_TIIMES,null, header);
            if(null != page){
            	cacheContainer.putPage(ProcessorCode.AMOUNT.getCode(), page);
            }
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
		String url = "http://bj.189.cn/iframe/feequery/billDetailQuery.action";
		try {
			Calendar calendar = Calendar.getInstance();
			String tmp;
			
			// 查询最近6个月通话详单
			for (int i = 1; i < (context.getRecordSize() + 1); i++) {//包含当前月份
				final String transDateBegin = DateFormatUtils.format(calendar, "yyyy年MM月");
				final String month = DateFormatUtils.format(calendar, "yyyy-MM");
				int pageNo = 1;
				String logFlag = String.format("==>2."+i+"["+context.getTaskId()+"]采集["+month+"]第["+pageNo+"]页通话详单,第[{}]次尝试请求.....");
				reqParam.clear();
				reqParam.add(new NameValuePair("requestFlag", "synchronization"));
				reqParam.add(new NameValuePair("billDetailType", "1"));
				reqParam.add(new NameValuePair("qryMonth", transDateBegin));
				reqParam.add(new NameValuePair("startTime", "1"));
				reqParam.add(new NameValuePair("endTime", calendar.getActualMaximum(Calendar.DAY_OF_MONTH)+""));
				reqParam.add(new NameValuePair("accNum", context.getUserName()));
				Page page = getPage(webClient, url, HttpMethod.POST, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, null);
				if(null != page){
					logger.info("==>2.[{}]采集[{}]第[{}]页通话详单成功.", context.getTaskId(), month, pageNo);
					pages.add(page);					
					tmp = page.getWebResponse().getContentAsString();
					String total = RegexUtils.matchValue("共<span class=.+>(\\d*?)</span>条</label> ", tmp);
					int pageSize = 1;
					if(StringUtils.isNotEmpty(total)){
						try{
							pageSize = (Integer.parseInt(total)+ 49)/50; //每页50条记录
						}catch(Exception e){}
					}
					for(; pageNo<pageSize; ){
						pageNo++;
						logFlag = String.format("==>2."+i+"["+context.getTaskId()+"]采集["+month+"]通话详单第["+pageNo+"]页,第[{}]次尝试请求.....");
						reqParam.clear();
						reqParam.add(new NameValuePair("requestFlag", "synchronization"));
						reqParam.add(new NameValuePair("billDetailType", "1"));
						reqParam.add(new NameValuePair("qryMonth", transDateBegin));
						reqParam.add(new NameValuePair("startTime", "1"));
						reqParam.add(new NameValuePair("endTime", calendar.getActualMaximum(Calendar.DAY_OF_MONTH)+""));
						reqParam.add(new NameValuePair("accNum", context.getUserName()));
						reqParam.add(new NameValuePair("billPage", pageNo+""));
						page = getPage(webClient, url, HttpMethod.POST, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, null);
						if(null != page){
							logger.info("==>2.[{}]采集[{}]第[{}]页通话详单成功.", context.getTaskId(), month, pageNo);
							pages.add(page);
						}
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
		String url = "http://bj.189.cn/iframe/feequery/billDetailQuery.action";
		try {
			Calendar calendar = Calendar.getInstance();
			String tmp;
			// 查询最近6个月短信记录
			for (int i = 1; i < (context.getRecordSize() + 1); i++) {//包含当前月份
				final String transDateBegin = DateFormatUtils.format(calendar, "yyyy年MM月");
				final String month = DateFormatUtils.format(calendar, "yyyy-MM");
				int pageNo = 1;
				String logFlag = String.format("==>2."+i+"["+context.getTaskId()+"]采集["+month+"]短信记录第["+pageNo+"]页,第[{}]次尝试请求.....");
				reqParam.clear();
				reqParam.add(new NameValuePair("requestFlag", "synchronization"));
				reqParam.add(new NameValuePair("billDetailType", "2"));
				reqParam.add(new NameValuePair("qryMonth", transDateBegin));
				reqParam.add(new NameValuePair("startTime", "1"));
				reqParam.add(new NameValuePair("endTime", calendar.getActualMaximum(Calendar.DAY_OF_MONTH)+""));
				reqParam.add(new NameValuePair("accNum", context.getUserName()));

				Page page = getPage(webClient, url, HttpMethod.POST, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, null);
				if(null != page){
					logger.info("==>3.[{}]采集[{}]第[{}]页短信记录成功.", context.getTaskId(), month, pageNo);
					pages.add(page);
					tmp = page.getWebResponse().getContentAsString();
					String total = RegexUtils.matchValue("共<span class=.+>(\\d*?)</span>条</label> ", tmp);
					int pageSize = 1;
					if(StringUtils.isNotEmpty(total)){
						try{
							pageSize = (Integer.parseInt(total)+ 49)/50; //每页50条记录
						}catch(Exception e){}
					}
					for(; pageNo<pageSize; ){
						pageNo++;
						logFlag = String.format("==>2."+i+"["+context.getTaskId()+"]采集["+month+"]短信记录第["+pageNo+"]页,第[{}]次尝试请求.....");
						reqParam.clear();
						reqParam.add(new NameValuePair("requestFlag", "synchronization"));
						reqParam.add(new NameValuePair("billDetailType", "2"));
						reqParam.add(new NameValuePair("qryMonth", transDateBegin));
						reqParam.add(new NameValuePair("startTime", "1"));
						reqParam.add(new NameValuePair("endTime", calendar.getActualMaximum(Calendar.DAY_OF_MONTH)+""));
						reqParam.add(new NameValuePair("accNum", context.getUserName()));
						reqParam.add(new NameValuePair("billPage", pageNo+""));
						page = getPage(webClient, url, HttpMethod.POST, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, null);
						if(null != page){
							logger.info("==>3.[{}]采集[{}]第[{}]页短信记录成功.", context.getTaskId(), month, pageNo);
							pages.add(page);
						}
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
		String url = "http://bj.189.cn/iframe/feequery/billDetailQuery.action";
		List<Page> pages = new ArrayList<>();
		try {
			Calendar calendar = Calendar.getInstance();
			String tmp;
			// 查询最近6个月上网记录
			for (int i = 1; i < (context.getRecordSize() + 1); i++) {//包含当前月份
				calendar.set(Calendar.DAY_OF_MONTH, 1);
				final String transDateBegin = DateFormatUtils.format(calendar, "yyyy年MM月");
				final String month = DateFormatUtils.format(calendar, "yyyyMM");
				int pageNo = 1;
				String logFlag = String.format("==>4."+i+"["+context.getTaskId()+"]采集["+month+"]上网记录第["+pageNo+"]页,第[{}]次尝试请求.....");
				reqParam.clear();
				reqParam.add(new NameValuePair("requestFlag", "synchronization"));
				reqParam.add(new NameValuePair("billDetailType", "3"));
				reqParam.add(new NameValuePair("qryMonth", transDateBegin));
				reqParam.add(new NameValuePair("startTime", "1"));
				reqParam.add(new NameValuePair("endTime", calendar.getActualMaximum(Calendar.DAY_OF_MONTH)+""));
				reqParam.add(new NameValuePair("accNum", context.getUserName()));

				Page page = getPage(webClient, url, HttpMethod.POST, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, null);
				if(null != page){
					logger.info("==>4.[{}]采集[{}]第[{}]页上网记录成功.", context.getTaskId(), month, pageNo);
					pages.add(page);
					tmp = page.getWebResponse().getContentAsString();
					String total = RegexUtils.matchValue("共<span class=.+>(\\d*?)</span>条</label> ", tmp);
					int pageSize = 1;
					if(StringUtils.isNotEmpty(total)){
						try{
							pageSize = (Integer.parseInt(total)+ 49)/50; //每页50条记录
						}catch(Exception e){}
					}
					for(; pageNo<pageSize; ){
						pageNo++;
						logFlag = String.format("==>4."+i+"["+context.getTaskId()+"]采集["+month+"]上网记录第["+pageNo+"]页,第[{}]次尝试请求.....");
						reqParam.clear();
						reqParam.add(new NameValuePair("requestFlag", "synchronization"));
						reqParam.add(new NameValuePair("billDetailType", "3"));
						reqParam.add(new NameValuePair("qryMonth", transDateBegin));
						reqParam.add(new NameValuePair("startTime", "1"));
						reqParam.add(new NameValuePair("endTime", calendar.getActualMaximum(Calendar.DAY_OF_MONTH)+""));
						reqParam.add(new NameValuePair("accNum", context.getUserName()));
						reqParam.add(new NameValuePair("billPage", pageNo+""));
						page = getPage(webClient, url, HttpMethod.POST, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, null);
						if(null != page){
							logger.info("==>4.[{}]采集[{}]第[{}]页上网记录成功.", context.getTaskId(), month, pageNo);
							pages.add(page);
						}
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

			header.put("Referer", "http://bj.189.cn/iframe/feequery/custFeeIndex.action?fastcode=01390635&tab=1&cityCode=bj");
			String url = "http://www.189.cn/login/sso/ecs.do?method=linkTo&platNo=10001&toStUrl=http://bj.189.cn/iframe/feequery/billQuery.action";
			Page page = getPage(webClient, url, HttpMethod.GET, null, null);

			reqParam.clear();
			reqParam.add(new NameValuePair("accNum", context.getUserName()));
			reqParam.add(new NameValuePair("requestFlag", "synchronization"));
			url = "http://bj.189.cn/iframe/feequery/billQuery.action";
			page = getPage(webClient, url, HttpMethod.POST, reqParam,Constants.MAX_SENDMSG_TIIMES,null, header);

			// 查询最近6个月账单
			for (int i = 1; i < (context.getRecordSize() + 1); i++) {//前6个月(不算当月)
				final String month = DateFormatUtils.format(calendar, "yyyyMM");
				String logFlag = String.format("==>5." + i + "[" + context.getTaskId() + "]采集[" + month + "]账单信息,第[{}]次尝试请求.....");

				reqParam.clear();
				reqParam.add(new NameValuePair("accNum", context.getUserName()));
				reqParam.add(new NameValuePair("billReqType", "3"));
				reqParam.add(new NameValuePair("billCycle", month));
				url = "http://bj.189.cn/iframe/feequery/billInfoQuery.action";

				page = getPage(webClient, url, HttpMethod.GET, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, header);
				if (null != page) {
					logger.info("==>5.[{}]采集[{}]账单信息成功.", context.getTaskId(), month);
					pages.add(page);
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
	public Result processPackageItemInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer) {
		Result result = new Result();
		try {
			Map<String, String> header = new HashMap<>();
			List<NameValuePair> reqParam = new ArrayList<>();
			header.put("Referer", "http://bj.189.cn/iframe/feequery/custBusinessIndex.action?tab=2&fastcode=20000662&cityCode=bj");

			String url = "http://bj.189.cn/iframe/custquery/orderRelaQuery.action";
			String logFlag = String.format("==>6.["+context.getTaskId()+"]采集[主套餐]套餐信息,第[{}]次尝试请求.....");
			reqParam.clear();
			reqParam.add(new NameValuePair("requestFlag", "asynchronism"));
			Page page = getPage(webClient, url, HttpMethod.POST, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, header);
			if (null != page) {// 添加缓存信息
				logger.info("==>6.[{}]采集[主套餐]套餐信息成功.", context.getTaskId());
				cacheContainer.putPage(ProcessorCode.PACKAGE_ITEM.getCode() + "1", page);
			} else {
				logger.info("==>6.[{}]采集[主套餐]套餐信息结束,没有查询结果.", context.getTaskId());
			}

			url = "http://bj.189.cn/iframe/custquery/orderPPP.action";
			logFlag = String.format("==>6.["+context.getTaskId()+"]采集[基础业务]套餐信息,第[{}]次尝试请求.....");
			reqParam.clear();
			reqParam.add(new NameValuePair("requestFlag", "asynchronism"));
			page = getPage(webClient, url, HttpMethod.POST, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, header);
			if (null != page) {// 添加缓存信息
				logger.info("==>6.[{}]采集[基础业务]套餐信息成功.", context.getTaskId());
				cacheContainer.putPage(ProcessorCode.PACKAGE_ITEM.getCode() + "2", page);
			} else {
				logger.info("==>6.[{}]采集[基础业务]套餐信息结束,没有查询结果.", context.getTaskId());
			}

			url = "http://bj.189.cn/iframe/custquery/ismpList.action";
			logFlag = String.format("==>6.["+context.getTaskId()+"]采集[增值业务]套餐信息,第[{}]次尝试请求.....");
			reqParam.clear();
			reqParam.add(new NameValuePair("requestFlag", "asynchronism"));
			page = getPage(webClient, url, HttpMethod.POST, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, header);
			if (null != page) {// 添加缓存信息
				logger.info("==>6.[{}]采集[增值业务]套餐信息成功.", context.getTaskId());
				cacheContainer.putPage(ProcessorCode.PACKAGE_ITEM.getCode() + "3", page);
			} else {
				logger.info("==>6.[{}]采集[增值业务]套餐信息结束,没有查询结果.", context.getTaskId());
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
			header.put("Referer", "http://bj.189.cn/iframe/local/queryVCPayFee.action?fastcode=01400641&cityCode=bj");
			List<NameValuePair> reqParam = new ArrayList<>();
			List<Page> pages = new ArrayList<>();
			String url = "http://bj.189.cn/iframe/local/queryPaymentRecord.action";
			
			// 查询最近6个月充值记录
			for (int i = 1; i < (context.getRecordSize() + 1); i++) {
				final String queryMonth = DateFormatUtils.format(calendar, "yyyyMM");
				final String startDate = DateUtils.getFirstDay(calendar, "yyyy-MM-dd");
				final String endDate = DateUtils.getLastDay(calendar, "yyyy-MM-dd");
				reqParam.clear();
				reqParam.add(new NameValuePair("requestFlag", "synchronization"));
				reqParam.add(new NameValuePair("paymentHistoryQueryIn.startDate", startDate));
				reqParam.add(new NameValuePair("paymentHistoryQueryIn.endDate", endDate));
				reqParam.add(new NameValuePair("accNum", context.getUserName()));
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
