package com.xinyan.spider.isp.mobile.processor.telecom;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xinyan.spider.isp.base.CacheContainer;
import com.xinyan.spider.isp.base.Context;
import com.xinyan.spider.isp.base.StatusCode;
import com.xinyan.spider.isp.common.utils.JavaScriptUtils;
import com.xinyan.spider.isp.common.utils.PageUtils;
import com.xinyan.spider.isp.common.utils.StringUtils;
import com.xinyan.spider.isp.mobile.model.CarrierInfo;
import com.xinyan.spider.isp.mobile.model.CarrierPackageItemInfo;
import com.xinyan.spider.isp.mobile.parser.telecom.ShangHaiTelecomParser;
import com.xinyan.spider.isp.mobile.processor.AbstractProcessor;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.xinyan.spider.isp.base.Result;
import com.xinyan.spider.isp.common.Constants;
import com.xinyan.spider.isp.common.utils.DateUtils;

/**
 * 上海电信处理类
 * @description
 * @author yyj
 * @date 2016年8月8日 下午3:38:43
 * @version V1.0
 */
@Component
public class ShangHaiTelecomPssor extends AbstractProcessor {

	protected static Logger logger= LoggerFactory.getLogger(ShangHaiTelecomPssor.class);

	private static final String BASE_URL = "http://service.sh.189.cn";

	@Autowired
	private ShangHaiTelecomParser parser;

	/**
	 * 正常登录
	 * @param webClient
	 * @param context
	 * @return
	 */
	@Override
	public Result doLogin(WebClient webClient, Context context){
		Result result = new Result();
		try {
			//1.开始登陆
			logger.info("==>[{}]登录开始...", context.getTaskId());
			Map<String, String> header = new HashMap<>();
			header.put("referer", "");
			Page resultPage = getPage(webClient, "http://service.sh.189.cn/service/biz/zqLogin/init", HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, header);
			header.clear();
			//图片
			header.put("Referer", "http://service.sh.189.cn/service/biz/zqLogin/init");
			String verifyCode = getVerifyCode(webClient, "http://service.sh.189.cn/service/createValidate.do?key=loginValidate&n=0.6578722170124696", header);
			if (StringUtils.isNotEmpty(verifyCode)) {
				result.setResult(StatusCode.请输入图片验证码);
				context.setTaskSubStatus(Constants.VALIDATE_TYPE_CODE_IMG);
				sendValidataMsg(result, context, true, webClient, Constants.DEQUEUE_TIME_OUT, verifyCode);
			} else {
				result.setResult(StatusCode.发送图片验证码失败);
				}
		}catch (Exception e){
			logger.error("==>登录出现异常:[{}]", e);
			result.setResult(StatusCode.登陆出错);
		}finally{
			// 设置回调
			if (!StatusCode.请输入短信验证码.getCode().equals(result.getCode())
					&& !StatusCode.请输入图片验证码.getCode().equals(result.getCode())) {
				sendLoginMsg(result, context, true, webClient);
			}
		}
			return result;

	}
	/**
	 * 短信验证登录
	 * @param webClient
	 * @param context
	 * @return
	 * @throws Exception
	 */
	@Override
	public Result doLoginBySMS(WebClient webClient, Context context){
		Result result = new Result();
		try{
			logger.info("==>[{}]短信验证登录...", context.getTaskId());

			if(StringUtils.isNotEmpty(context.getUserInput())){
				List<NameValuePair> reqParam = new ArrayList<>();
				reqParam.add(new NameValuePair("loginType","1000004"));
				reqParam.add(new NameValuePair("randCode",context.getParam2()));
				reqParam.add(new NameValuePair("accountpwd",context.getUserInput()));
				reqParam.add(new NameValuePair("account",context.getUserName()));
				reqParam.add(new NameValuePair("cardNo","请输入政企认证卡卡号"));
				reqParam.add(new NameValuePair("cardPwd",""));
				Page resultPage = getPage(webClient, "http://service.sh.189.cn/service/biz/zqLogin/login", HttpMethod.GET, reqParam,Constants.MAX_SENDMSG_TIIMES,null, null);
				if(StringUtils.contains(resultPage.getWebResponse().getContentAsString(), "\"code\":true")){
					result.setResult(StatusCode.登陆成功);
				}else{
					result.setResult(StatusCode.短信验证码错误);
				}
			}else{
				result.setResult(StatusCode.请输入短信验证码);
			}
			logger.info("==>[{}]短信验证登录结束{}", context.getTaskId(), result);
		}catch (Exception e){
			logger.error("==>登录出现异常:[{}]", e);
			result.setResult(StatusCode.登陆出错);
		}finally {
			Map<String,String> header = new HashMap<>();
			header.put("Referer", "http://service.sh.189.cn/service/biz/zqLogin/init");
			Page resultPage = getPage(webClient, "http://service.sh.189.cn/service/biz/uiss_mobile_login_zq/login", HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, header);
			header.clear();
			header.put("Referer","http://service.sh.189.cn/service/account");
			resultPage = getPage(webClient, "http://service.sh.189.cn/service/mytelecom/cusInfo", HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, header);
			sendLoginMsg(result, context,true, webClient);
			close(webClient);
			//try
			//页面缓
		}
		return result;
	}

	public Result doLoginByIMG(WebClient webClient, Context context) {
		Result result = new Result();
		if(StringUtils.isEmpty(context.getUserInput())){
			result.setResult(StatusCode.请输入图片验证码);
			return result;
		}
		try {
			// 1.开始登陆
			logger.info("==>1.[{}]用户图片认证开始.....", context.getTaskId());
			context.setParam2(context.getUserInput());
			List<NameValuePair>reqParam = new ArrayList<>();
			reqParam.add(new NameValuePair("contentId", "10"));
			reqParam.add(new NameValuePair("devNo", context.getUserName()));
			reqParam.add(new NameValuePair("serviceNameId", "2"));
			reqParam.add(new NameValuePair("imageCode", context.getParam2()));
			reqParam.add(new NameValuePair("imageCodeKey", "loginValidate"));
			reqParam.add(new NameValuePair("_", "1504494270046"));
			Page resultPage = getPage(webClient, "http://service.sh.189.cn/service/biz/common/smsSend", HttpMethod.GET, reqParam,Constants.MAX_SENDMSG_TIIMES,null, null);
			if(StringUtils.contains(resultPage.getWebResponse().getContentAsString(), "\"RESULT\":true")) {
				logger.info("==>[{}]已经发送短信验证码...", context.getTaskId());
				//通知前端发送成功
				result.setResult(StatusCode.请输入短信验证码);
				context.setTaskSubStatus(Constants.VALIDATE_TYPE_CODE_SMS);//需要设置回调子状态
				sendValidataMsg(result, context, true, webClient, Constants.DEQUEUE_TIME_OUT,"");
				return result;
			}else if(StringUtils.contains(resultPage.getWebResponse().getContentAsString(), "\"RESULT\":\"图形验证码错误\"")){
				result.setResult(StatusCode.图片验证码错误);
			}else{
				result.setResult(StatusCode.发送短信验证码失败);
			}
			logger.info("==>1.[{}]用户图片认证结束[{}]", context.getTaskId(), result);
		} catch (Exception e) {
			logger.error("==>1.[{}]用户图片认证出现异常:[{}]", context.getTaskId(), e);
			result.setResult(StatusCode.登陆出错);
		} finally {
			// 设置回调
//			sendLoginMsg(result, context, true, webClient);
			if (!StatusCode.请输入短信验证码.getCode().equals(result.getCode())
					&& !StatusCode.请输入图片验证码.getCode().equals(result.getCode())) {
				sendLoginMsg(result, context, true, webClient);
			}
		}
		return result;
	}

	@Override
	public Result doCrawler(WebClient webClient, Context context) {
		Result result = new Result();

		//页面缓存
		CacheContainer cacheContainer = new CacheContainer();
		CarrierInfo carrierInfo = new CarrierInfo();
		context.setMappingId(carrierInfo.getMappingId());
		BeanUtils.copyProperties(context, carrierInfo);
		carrierInfo.getCarrierUserInfo().setMobile(context.getUserName());// 手机号码
		
		try {
			//1.采集基础信息
			getLogger().info("==>[{}]1.采集基本信息开始.....", context.getTaskId());
			result = processBaseInfo(webClient, context, carrierInfo, cacheContainer);
			getLogger().info("==>[{}]1.采集基本信息结束{}.", context.getTaskId(), result);
			if (!result.isSuccess()) { //采集基础信息失败
				return result;
			}

			//2.采集通话详单
			getLogger().info("==>[{}]2.采集通话详单开始.....", context.getTaskId());
			result = processCallRecordInfo(webClient, context, carrierInfo, cacheContainer);
			getLogger().info("==>[{}]2.采集通话详单结束{}.", context.getTaskId(), result);
			if (!result.isSuccess()) { //采集通话详单失败
				return result;
			}

			//3.采集短信记录
			getLogger().info("==>[{}]3.采集短信记录开始.....", context.getTaskId());
			result = processSmsInfo(webClient, context, carrierInfo, cacheContainer);
			getLogger().info("==>[{}]3.采集短信记录结束{}.", context.getTaskId(), result);
			if (!result.isSuccess()) { //采集短信记录失败
				return result;
			}

			//4.采集上网记录
			getLogger().info("==>[{}]4.采集上网记录开始.....", context.getTaskId());
			result = processNetInfo(webClient, context, carrierInfo, cacheContainer);
			getLogger().info("==>[{}]4.采集上网记录结束{}.", context.getTaskId(), result);
			if (!result.isSuccess()) { //采集上网记录失败
				return result;
			}

			//5.采集账单信息
			getLogger().info("==>[{}]5.采集账单信息开始.....", context.getTaskId());
			result = processBillInfo(webClient, context, carrierInfo, cacheContainer);
			getLogger().info("==>[{}]5.采集账单信息结束{}.", context.getTaskId(), result);
			if (!result.isSuccess()) { //采集账单信息失败
				return result;
			}

			//6.采集套餐信息
			getLogger().info("==>[{}]6.采集套餐信息开始.....", context.getTaskId());
			result = processPackageItemInfo(webClient, context, carrierInfo, cacheContainer);
			getLogger().info("==>[{}]6.采集套餐信息结束{}.", context.getTaskId(), result);
			if (!result.isSuccess()) {
				return result;
			}

			//7.采集亲情号码
			getLogger().info("==>[{}]7.采集亲情号码开始.....", context.getTaskId());
			result = processUserFamilyMember(webClient, context, carrierInfo, cacheContainer);
			getLogger().info("==>[{}]7.采集亲情号码结束{}.", context.getTaskId(), result);
			if (!result.isSuccess()) {
				return result;
			}

			//8.采集充值记录
			getLogger().info("==>[{}]8.采集充值记录开始.....", context.getTaskId());
			result = processUserRechargeItemInfo(webClient, context, carrierInfo, cacheContainer);
			getLogger().info("==>[{}]8.采集充值记录结束{}.", context.getTaskId(), result);
			if (!result.isSuccess()) {
				return result;
			}
			
			result.setData(carrierInfo);
		} catch (Exception ex) {
			logger.error("==>[{}]数据抓取出现异常:[{}]", context.getTaskId(), ex);
			result.setFail();
			return result;
		} finally {
			result.setResult(StatusCode.爬取成功);
			sendUpdateLog(result, context);
			
			result.setResult(StatusCode.解析成功);
			sendAnalysisMsg(result, context);
			getLogger().info("==>[{}]9.退出登陆开始.....", context.getTaskId());
			loginout(webClient);
			getLogger().info("==>[{}]9.退出登陆结束.", context.getTaskId());
			close(webClient);
		}

		getLogger().info("==>[{}]0.数据抓取结束{}\n,详情:{}", context.getTaskId(), result, result.getData());
		return result;
	}

	@Override
	public Result processBaseInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer){
		Result result = new Result();
		try{
			Map<String, String> header = new HashMap<>();
			carrierInfo.getCarrierUserInfo().setCarrier("CHINA_TELECOM");//运营商    CHINA_MOBILE 中国移动    CHINA_TELECOM 中国电信    CHINA_UNICOM 中国联通
			carrierInfo.getCarrierUserInfo().setProvince("上海");//所属省份
			Page resultPage = getPage(webClient, BASE_URL+"/service/my/basicinfo.do", HttpMethod.POST, null,Constants.MAX_SENDMSG_TIIMES,null, null);
			if(null!=resultPage){//操作成功
				JSONObject info = JSONObject.parseObject(resultPage.getWebResponse().getContentAsString());
				if("0".equals(info.getString("CODE"))){
					logger.info("==>1.1解析个人信息开始.....");

					JSONObject userInfo = info.getJSONObject("RESULT");
					carrierInfo.getCarrierUserInfo().setName(userInfo.getString("CustNAME"));//姓名
					carrierInfo.getCarrierUserInfo().setIdCard(userInfo.getString("MainIdenNumber"));//证件号
					carrierInfo.getCarrierUserInfo().setCity("上海");//所属城市
					//帐号状态, -1未知 0正常 1单向停机 2停机 3预销户 4销户 5过户 6改号 99号码不存在
					if("活动".equals(userInfo.getString("CustStatus"))){
						carrierInfo.getCarrierUserInfo().setState(0);//帐号状态
					}
					logger.info("==>1.1解析个人信息结束.");
				}else{
					result.setResult(StatusCode.解析基础信息出错);
	                return result;
				}
			}else{
				result.setResult(StatusCode.解析基础信息出错);
                return result;
			}

			resultPage = getPage(webClient, BASE_URL+"/service/service/authority/query/getUserStar.do", HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, null);
			if(null!=resultPage){//操作成功
				logger.info("==>1.2解析用户等级开始.....");
				JSONObject info = JSONObject.parseObject(resultPage.getWebResponse().getContentAsString());
				carrierInfo.getCarrierUserInfo().setLevel(info.getString("result"));//帐号星级
				logger.info("==>1.2解析用户等级结束.");
			}else{
				result.setResult(StatusCode.解析用户等级出错);
                return result;
			}

			List<NameValuePair> reqParam = new ArrayList<>();
			reqParam.add(new NameValuePair("DeviceId", context.getUserName()));
			resultPage = getPage(webClient, BASE_URL+"/service/service/authority/queryInfo/getMsgByDeviceId.do", HttpMethod.POST, reqParam,Constants.MAX_SENDMSG_TIIMES,null, null);
			if(null!=resultPage){//操作成功
				logger.info("==>1.3解析入网时间开始.....");
				JSONObject info = JSONObject.parseObject(resultPage.getWebResponse().getContentAsString());
				carrierInfo.getCarrierUserInfo().setPackageName(info.getString("parentPromotionProductName"));//套餐名称
				String opendate = info.getString("installDate");//入网时间
				
				carrierInfo.getCarrierUserInfo().setOpenTime(DateUtils.dateToString(DateUtils.stringToDate(opendate, "MM/dd/yyyy"), "yyyy-MM-dd"));//入网时间，格式：yyyy-MM-dd
				logger.info("==>1.3解析入网时间结束.");
			}else{
				result.setResult(StatusCode.解析入网时间出错);
                return result;
			}
			reqParam = new ArrayList<>();
			reqParam.add(new NameValuePair("deviceNo", context.getUserName()));
			reqParam.add(new NameValuePair("type", "4"));
			resultPage = getPage(webClient, BASE_URL+"/service/service/authority/query/querySolidify.do", HttpMethod.GET, reqParam,Constants.MAX_SENDMSG_TIIMES,null, null);
			if(null!=resultPage){//操作成功
				logger.info("==>1.4解析可用余额开始.....");
				JSONObject info = JSONObject.parseObject(resultPage.getWebResponse().getContentAsString());
				Double amount = Double.parseDouble(info.getString("amount")) * 100;
				carrierInfo.getCarrierUserInfo().setAvailableBalance(amount.intValue());//当前可用余额（单位: 分）
				logger.info("==>1.4解析可用余额结束.");
			}else{
				result.setResult(StatusCode.解析积分余额信息出错);
                return result;
			}
			carrierInfo.getCarrierUserInfo().setLastModifyTime(DateUtils.dateToString(new Date(), "yyyy-MM-dd hh:mm:ss"));//最近一次更新时间，格式: yyyy-MM-dd HH:mm:ss
			result.setSuccess();
		}catch (Exception e){
			logger.error("==>1.解析基本信息出现异常:[{}]", e);
			result.setResult(StatusCode.解析基础信息出错);
			return result;
		}
		return result;
	}

	@Override
	public Result processCallRecordInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer){
		Result result = new Result();
		try{
			//二次验证码
			String url = "http://service.sh.189.cn/service/query/detail";
			Page resultPage = getPage(webClient, url, HttpMethod.POST, null,Constants.MAX_SENDMSG_TIIMES,null, null);
			if(StringUtils.contains(resultPage.getWebResponse().getContentAsString(),"需要验证手机号才可以查看详单哦")){//需要登录
				result = GetMsgCode(context,webClient);
				if(!result.isSuccess()){
					return result;
				}
			}
			Calendar calendar = Calendar.getInstance();
			List<NameValuePair> reqParam = new ArrayList<>();;
			url = "http://service.sh.189.cn/service/service/authority/query/billdetailQuery.do";

			//查询最近6个月通话详单
			for(int i=1; i<(context.getRecordSize()+1); i++){
				String transDateBegin = DateFormatUtils.format(calendar, "yyyy/MM");
				reqParam.clear();
				reqParam.add(new NameValuePair("begin", "0"));
				reqParam.add(new NameValuePair("end", "10"));
				reqParam.add(new NameValuePair("flag", "1"));
				reqParam.add(new NameValuePair("devNo", context.getUserName()));
				reqParam.add(new NameValuePair("bill_type", "SCP"));
				if (1 == i) {
					reqParam.add(new NameValuePair("dateType", "now"));
					reqParam.add(new NameValuePair("startDate", DateUtils.getFirstDay(calendar, "yyyy-MM-dd")));
					reqParam.add(new NameValuePair("endDate", DateUtils.getCurrentDate()));
				} else {
					reqParam.add(new NameValuePair("dateType", "his"));
					reqParam.add(new NameValuePair("queryDate", transDateBegin));
					reqParam.add(new NameValuePair("startDate", ""));
					reqParam.add(new NameValuePair("endDate", ""));
				}
				
				String logFlag = String.format("==>2."+i+"解析["+transDateBegin+"]第[1]页通话详单,第[{}]次尝试请求.....");
				Page page = getPage(webClient, url, HttpMethod.GET, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, null);

				if(null != page){//有通话详单结果
					JSONObject callDetail = JSONObject.parseObject(page.getWebResponse().getContentAsString());
					if("0".equals(callDetail.getString("CODE"))){//操作成功
						int sumRow = parser.getSumRow(callDetail);
						if(sumRow>0){
							reqParam.clear();
							reqParam.add(new NameValuePair("begin", "0"));
							reqParam.add(new NameValuePair("end", sumRow+""));
							reqParam.add(new NameValuePair("flag", "1"));
							reqParam.add(new NameValuePair("devNo", context.getUserName()));
							reqParam.add(new NameValuePair("bill_type", "SCP"));
							if (1 == i) {
								reqParam.add(new NameValuePair("dateType", "now"));
								reqParam.add(new NameValuePair("startDate", DateUtils.getFirstDay(calendar, "yyyy-MM-dd")));
								reqParam.add(new NameValuePair("endDate", DateUtils.getCurrentDate()));
							} else {
								reqParam.add(new NameValuePair("dateType", "his"));
								reqParam.add(new NameValuePair("queryDate", transDateBegin));
								reqParam.add(new NameValuePair("startDate", ""));
								reqParam.add(new NameValuePair("endDate", ""));
							}
							
							logFlag = String.format("==>2."+i+"解析["+transDateBegin+"]第[2]页通话详单,第[{}]次尝试请求.....");
							page = getPage(webClient, BASE_URL+"/service/service/authority/query/billdetailQuery.do",
									HttpMethod.GET, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, null);
							if(null!=page){//有通话详单结果
								JSONObject cd = JSONObject.parseObject(page.getWebResponse().getContentAsString());
								if("0".equals(cd.getString("CODE"))){////操作成功
									parser.callRecordParse(cd, carrierInfo);
								}else{
									getLogger().error("==>2.{}解析[{}]通话详单出错了.", i, transDateBegin);
								}
							}else{
								getLogger().error("==>2.{}解析[{}]解析通话详情出错.", i, transDateBegin);
							}
						}
					}else if(StringUtils.contains(page.getWebResponse().getContentAsString(), "ME10001")){
						getLogger().info("==>2.{}解析[{}]通话详单结束,没有查询结果.", i, transDateBegin);
					}else{
						getLogger().error("==>2.{}解析[{}]通话详单出错了.", i, transDateBegin);
					}
				}else{
					getLogger().info("==>2.{}解析[{}]通话详单结束,没有查询结果.", i, transDateBegin);
				}
				if(1<i){
					calendar.add(Calendar.MONTH, -1);
				}
			}
			result.setSuccess();
		}catch (Exception e){
			logger.error("==>2.解析通话详情出现异常:[{}]", e);
			result.setResult(StatusCode.解析通话详情出错);
			return result;
		}
		return result;
	}

	@Override
	public Result processSmsInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer){
		Result result = new Result();
		try{
			Calendar calendar = Calendar.getInstance();
			List<NameValuePair> reqParam = new ArrayList<>();
			String url = "http://service.sh.189.cn/service/service/authority/query/billdetailQuery.do";
			
			//查询最近6个月短信记录
			for(int i=1; i<(context.getRecordSize()+1); i++){
				
				String transDateBegin = DateFormatUtils.format(calendar, "yyyy/MM");
				reqParam.clear();
				reqParam.add(new NameValuePair("begin", "0"));
				reqParam.add(new NameValuePair("end", "10"));
				reqParam.add(new NameValuePair("flag", "1"));
				reqParam.add(new NameValuePair("devNo", context.getUserName()));
				reqParam.add(new NameValuePair("bill_type", "SMSC"));
				if (1 == i) {
					reqParam.add(new NameValuePair("dateType", "now"));
					reqParam.add(new NameValuePair("startDate", DateUtils.getFirstDay(calendar, "yyyy-MM-dd")));
					reqParam.add(new NameValuePair("endDate", DateUtils.getCurrentDate()));
				} else {
					reqParam.add(new NameValuePair("dateType", "his"));
					reqParam.add(new NameValuePair("queryDate", transDateBegin));
					reqParam.add(new NameValuePair("startDate", ""));
					reqParam.add(new NameValuePair("endDate", ""));
				}
				
				String logFlag = String.format("==>3."+i+"解析["+transDateBegin+"]第[1]页短信记录,第[{}]次尝试请求.....");
				Page page = getPage(webClient, url, HttpMethod.GET, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, null);

				if(null!=page){//有短信记录结果
					JSONObject sms = JSONObject.parseObject(page.getWebResponse().getContentAsString());
					if("0".equals(sms.getString("CODE"))){//操作成功
						int sumRow = parser.getSumRow(sms);
						if(sumRow>0){
							reqParam.clear();
							reqParam.add(new NameValuePair("begin", "0"));
							reqParam.add(new NameValuePair("end", sumRow+""));
							reqParam.add(new NameValuePair("flag", "1"));
							reqParam.add(new NameValuePair("devNo", context.getUserName()));
							reqParam.add(new NameValuePair("bill_type", "SMSC"));
							if (1 == i) {
								reqParam.add(new NameValuePair("dateType", "now"));
								reqParam.add(new NameValuePair("startDate", DateUtils.getFirstDay(calendar, "yyyy-MM-dd")));
								reqParam.add(new NameValuePair("endDate", DateUtils.getCurrentDate()));
							} else {
								reqParam.add(new NameValuePair("dateType", "his"));
								reqParam.add(new NameValuePair("queryDate", transDateBegin));
								reqParam.add(new NameValuePair("startDate", ""));
								reqParam.add(new NameValuePair("endDate", ""));
							}
							
							logFlag = String.format("==>3."+i+"解析["+transDateBegin+"]第[2]页短信记录,第[{}]次尝试请求.....");

							page = getPage(webClient, url, HttpMethod.GET, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, null);
							if(null!=page){//有通话详单结果
								JSONObject cd = JSONObject.parseObject(page.getWebResponse().getContentAsString());
								if("0".equals(cd.getString("CODE"))){////操作成功
									parser.smsParse(cd, carrierInfo);
								}else{
									getLogger().error("==>3.{}解析[{}]短信记录出错了.", i, transDateBegin);
								}
							}else{
								getLogger().error("==>3.{}解析[{}]短信记录出错了.", i, transDateBegin);
							}
						}
					}else if(StringUtils.contains(page.getWebResponse().getContentAsString(), "ME10001")){
						getLogger().info("==>3.{}解析[{}]短信记录结束,没有查询结果.", i, transDateBegin);
					}else{
						getLogger().error("==>3.{}解析[{}]短信记录出错了.", i, transDateBegin);
					}
				}else{
					getLogger().info("==>3.{}解析[{}]短信记录结束,没有查询结果.", i, transDateBegin);
				}
				if(1<i){
					calendar.add(Calendar.MONTH, -1);
				}
			}
			result.setSuccess();
		}catch (Exception e){
			logger.error("==>3.解析短信记录出现异常:[{}]", e);
			result.setResult(StatusCode.解析短信记录出错);
			return result;
		}
		return result;
	}

	@Override
	public Result processNetInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer){
		Result result = new Result();
		try{
			Calendar calendar = Calendar.getInstance();
			String url = "http://service.sh.189.cn/service/service/authority/query/billdetailQuery.do";
			List<NameValuePair> reqParam = new ArrayList<>();

			//查询最近6个月上网记录
			for(int i=1; i<(context.getRecordSize()+1); i++){
				final String transDateBegin = DateFormatUtils.format(calendar, "yyyy/MM");
				reqParam.clear();
				reqParam.add(new NameValuePair("begin", "0"));
				reqParam.add(new NameValuePair("end", "10"));
				reqParam.add(new NameValuePair("flag", "1"));
				reqParam.add(new NameValuePair("devNo", context.getUserName()));
				reqParam.add(new NameValuePair("bill_type", "AAA"));
				if (1 == i) {
					reqParam.add(new NameValuePair("dateType", "now"));
					reqParam.add(new NameValuePair("startDate", DateUtils.getFirstDay(calendar, "yyyy-MM-dd")));
					reqParam.add(new NameValuePair("endDate", DateUtils.getCurrentDate()));
				} else {
					reqParam.add(new NameValuePair("dateType", "his"));
					reqParam.add(new NameValuePair("queryDate", transDateBegin));
					reqParam.add(new NameValuePair("startDate", ""));
					reqParam.add(new NameValuePair("endDate", ""));
				}

				String logFlag = String.format("==>4."+i+"解析["+transDateBegin+"]第[1]页上网记录,第[{}]次尝试请求.....");

				Page page = getPage(webClient, url, HttpMethod.GET, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, null);

				if(null!=page){//有上网记录结果
					JSONObject sms = JSONObject.parseObject(page.getWebResponse().getContentAsString());
					if("0".equals(sms.getString("CODE"))){//操作成功
						int sumRow = parser.getSumRow(sms);
						if(sumRow>0){
							reqParam.clear();
							reqParam.add(new NameValuePair("begin", "0"));
							reqParam.add(new NameValuePair("end", sumRow+""));
							reqParam.add(new NameValuePair("flag", "1"));
							reqParam.add(new NameValuePair("devNo", context.getUserName()));
							reqParam.add(new NameValuePair("bill_type", "AAA"));
							if (1 == i) {
								reqParam.add(new NameValuePair("dateType", "now"));
								reqParam.add(new NameValuePair("startDate", DateUtils.getFirstDay(calendar, "yyyy-MM-dd")));
								reqParam.add(new NameValuePair("endDate", DateUtils.getCurrentDate()));
							} else {
								reqParam.add(new NameValuePair("dateType", "his"));
								reqParam.add(new NameValuePair("queryDate", transDateBegin));
								reqParam.add(new NameValuePair("startDate", ""));
								reqParam.add(new NameValuePair("endDate", ""));
							}
							
							logFlag = String.format("==>4."+i+"解析["+transDateBegin+"]第[2]页上网记录,第[{}]次尝试请求.....");

							page = getPage(webClient, url, HttpMethod.GET, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, null);
							if(null!=page){//有通话详单结果
								JSONObject cd = JSONObject.parseObject(page.getWebResponse().getContentAsString());
								if("0".equals(cd.getString("CODE"))){////操作成功
									parser.netParse(cd, carrierInfo);
								}else{
									getLogger().error("==>4.{}解析[{}]上网记录出错了.", i, transDateBegin);
								}
							}else{
								getLogger().error("==>4.{}解析[{}]上网记录出错了.", i, transDateBegin);
							}
						}
					}else if(StringUtils.contains(page.getWebResponse().getContentAsString(), "ME10001")){
						getLogger().info("==>4.{}解析[{}]上网记录,没有查询结果.", i, transDateBegin);
					}else{
						getLogger().error("==>4.{}解析[{}]上网记录出错了.", i, transDateBegin);
					}
				}else{
					getLogger().info("==>4.{}解析[{}]上网记录结束,没有查询结果.", i, transDateBegin);
				}
				if(1<i){
					calendar.add(Calendar.MONTH, -1);
				}
			}
			result.setSuccess();
		}catch (Exception e){
			logger.error("==>4.解析短信记录出现异常:[{}]", e);
			result.setResult(StatusCode.解析上网记录出错);
			return result;
		}
		return result;
	}

	@Override
	public Result processBillInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer){
		Result result = new Result();
		try{
			//查询最近6个月账单信息
			List<NameValuePair> reqParam = new ArrayList<>();
			reqParam.add(new NameValuePair("device", context.getUserName()));
			reqParam.add(new NameValuePair("acctNum", ""));

			logger.info("==>5.解析[近6个月]账单信息开始.....");
			Page historyBillPage = getPage(webClient, BASE_URL+"/service/mobileBill.do", HttpMethod.POST, reqParam,Constants.MAX_SENDMSG_TIIMES,null, null);

			if(null!=historyBillPage){//有账单信息结果
				JSONObject historyBill = JSONObject.parseObject(historyBillPage.getWebResponse().getContentAsString());
				int totalRecord = 0;
				if("0".equals(historyBill.getString("CODE"))){//操作成功
					String echartsInvoice = historyBill.getString("echartsInvoice");
					String accNbr = historyBill.getString("fzxhNO");
					context.setParam1(accNbr);
					JSONObject invoice = JSONObject.parseObject(echartsInvoice);
					String dateArray = invoice.getString("dateArray").replace("[", "").replace("]", "").replace("\"", "");
					String jeArray = invoice.getString("jeArray").replace("[", "").replace("]", "").replace("\"", "");

					String breadcolor = invoice.getString("breadcolor").replace("[", "").replace("]", "").replace("\"", "");
					JSONArray invoiceList = historyBill.getJSONArray("invoiceList");
					if(null != invoiceList && CollectionUtils.isNotEmpty(invoiceList)){//有账单记录
						JSONObject b;
						for (int j = 0; j < invoiceList.size(); j++) {
							b = (JSONObject) invoiceList.get(j);
							reqParam = new ArrayList<>();
							reqParam.add(new NameValuePair("dateArray", dateArray));
							reqParam.add(new NameValuePair("jeArray", jeArray));
							reqParam.add(new NameValuePair("breadcolors", breadcolor));
							reqParam.add(new NameValuePair("noPayDate", b.getString("billDate")));
							reqParam.add(new NameValuePair("accNbr", context.getParam1()));
							reqParam.add(new NameValuePair("billingCycle", b.getString("billDate")));
							reqParam.add(new NameValuePair("balanceDue", b.getString("balanceDue")));
							reqParam.add(new NameValuePair("invoiceNo", b.getString("invoiceNo")));
							reqParam.add(new NameValuePair("deviceNum", context.getUserName()));
							
							try{
								Map<String,String> header = new HashMap<>();
								header.put("Referer","http://service.sh.189.cn/service/query/bill");
								header.put("User-Agent","Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 10.0; WOW64; Trident/7.0; .NET4.0C; .NET4.0E; .NET CLR 2.0.50727; .NET CLR 3.0.30729; .NET CLR 3.5.30729;  QIHU 360EE)");
								Page billPage = getPage(webClient, BASE_URL + "/service/invoiceJump", HttpMethod.POST, reqParam,Constants.MAX_SENDMSG_TIIMES,null, header);
								parser.billParse(billPage, carrierInfo);
							}catch(Exception ex){}
						}
					}else{
						getLogger().info("==>5.解析[近6个月]账单信息结束,没有查询结果.");
					}
					getLogger().info("==>5.解析[近6个月]账单信息结束,共[{}]记录.", totalRecord);
				}else{
					getLogger().error("==>5.解析[近6个月]账单信息出错了.");
				}
			}else{
				getLogger().info("==>5.解析[近6个月]账单信息结束,没有查询结果.");
			}
			result.setSuccess();
		}catch (Exception e){
			logger.error("==>5.解析账单信息出现异常:[{}]", e);
			result.setResult(StatusCode.解析账单信息出错);
			return result;
		}
		return result;
	}

	@Override
	public Result processPackageItemInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer){
		Result result = new Result();
		try{
			//查询最近已办理业务信息
			List<NameValuePair> reqParam = new ArrayList<>();
			reqParam.add(new NameValuePair("devNo", context.getUserName()));

			logger.info("==>6.解析套餐信息开始.....");
			Page page = getPage(webClient, "http://service.sh.189.cn/service/service/authority/query/details", HttpMethod.POST, reqParam,Constants.MAX_SENDMSG_TIIMES,null, null);

			if(null!=page){//有办理业务信息结果
				JSONObject handleRecord = JSONObject.parseObject(page.getWebResponse().getContentAsString());
					if(null != handleRecord){
						JSONArray records = handleRecord.getJSONArray("packageMap");
						if(null != records && CollectionUtils.isNotEmpty(records)){//有办理业务信息记录
							JSONObject r;
							CarrierPackageItemInfo cpi;

							for (int j = 0; j < records.size(); j++) {
								r = (JSONObject) records.get(j);
								cpi = new CarrierPackageItemInfo();
							    cpi.setMappingId(carrierInfo.getMappingId());//映射id 
							    cpi.setItem(r.getString("productOffName"));//套餐项目名称 
							    String judgeNum = r.getString("judgeNum");
							    if("10".equals(judgeNum)){//流量
							    	String total = r.getString("ratableTotal");
							    	if(StringUtils.isNotEmpty(total)){
									    Double db = new Double(total) * 1024;
									    cpi.setTotal(db+"");//项目总量 
									}
							    	
							    	String used = r.getString("ratableUsed");
							    	if(StringUtils.isNotEmpty(used)){
									    Double db = new Double(used) * 1024;
									    cpi.setUsed(db+"");//项目已使用量 
									}
							    	cpi.setUnit("KB");//单位：语音-分; 流量-KB; 短/彩信-条 
							    }else if("13".equals(judgeNum)){//语音
							    	cpi.setUnit("分");//单位：语音-分; 流量-KB; 短/彩信-条 
							    	cpi.setTotal(r.getString("ratableTotal"));//项目总量 
							    	cpi.setUsed(r.getString("ratableUsed"));//项目已使用量 
							    }
							   
							    cpi.setBillStartDate(DateUtils.getFirstDay(new Date(), "yyyy-MM-dd"));//账单起始日, 格式为yyyy-MM-dd 
							    cpi.setBillEndDate(DateUtils.getCurrentDate());//账单结束日, 格式为yyyy-MM-dd 
							    carrierInfo.getPackages().add(cpi);
							}
						}
					}
					getLogger().info("==>6.解析套餐信息结束,共[{}]记录.", carrierInfo.getPackages().size());
				}else{
					getLogger().error("==>6.解析套餐信息出错了.");
				}
		
			result.setSuccess();
		}catch (Exception e){
			logger.error("==>6.解析套餐信息出现异常:[{}]", e);
			result.setResult(StatusCode.解析办理业务信息出错);
			return result;
		}
		return result;
	}

	@Override
	public Result processUserFamilyMember(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer){
		logger.info("==>7.暂无亲情号码取样");
		return new Result(StatusCode.SUCCESS);
	}

	@Override
	public Result processUserRechargeItemInfo(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer){
		Result result = new Result();
		try{
			Calendar calendar = Calendar.getInstance();

			//查询最近6个月充值记录
			for(int i=1; i<(context.getRecordSize()+1); i++){
				calendar.set(Calendar.DAY_OF_MONTH, 1);
				final String beginDate = DateFormatUtils.format(calendar, "yyyy-MM-dd");
				calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
				final String endDate = DateFormatUtils.format(calendar, "yyyy-MM-dd");
				
				List<NameValuePair> reqParam = new ArrayList<>();
				reqParam.add(new NameValuePair("begin", "0"));
				reqParam.add(new NameValuePair("end", "10"));
				reqParam.add(new NameValuePair("time", "0." + calendar.getTimeInMillis()));
				reqParam.add(new NameValuePair("channel_wt", "1"));
				reqParam.add(new NameValuePair("total", "on"));
				reqParam.add(new NameValuePair("payment_no", context.getUserName()));
				reqParam.add(new NameValuePair("exchange_date", ""));
				reqParam.add(new NameValuePair("beginDate", beginDate));
				reqParam.add(new NameValuePair("endDate", endDate));
				reqParam.add(new NameValuePair("exchangeType", ""));
				reqParam.add(new NameValuePair("channelf", "1"));
				reqParam.add(new NameValuePair("prodType", "5"));
				reqParam.add(new NameValuePair("chargeFzxh", context.getParam1()));

				String logFlag = String.format("==>8."+i+"解析["+beginDate+"]第[1]页充值记录,第[{}]次尝试请求.....");
				Page rechargePage = getPage(webClient, BASE_URL+"/service/service/authority/query/rechargePage.do",
						HttpMethod.GET, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, null);

				if(null != rechargePage){
					JSONObject recharge = JSONObject.parseObject(rechargePage.getWebResponse().getContentAsString());
					if(null != recharge && StringUtils.isNotEmpty(recharge.getString("count"))){//有充值记录结果
						int sumRow = parser.rechargeParse(recharge, carrierInfo);
						if(sumRow>10){
							reqParam = new ArrayList<>();
							reqParam.add(new NameValuePair("begin", "11"));
							reqParam.add(new NameValuePair("end", (sumRow-10)+""));
							reqParam.add(new NameValuePair("flag", "1"));
							reqParam.add(new NameValuePair("time", "0." + calendar.getTimeInMillis()));
							reqParam.add(new NameValuePair("channel_wt", "1"));
							reqParam.add(new NameValuePair("total", "on"));
							reqParam.add(new NameValuePair("payment_no", context.getUserName()));
							reqParam.add(new NameValuePair("exchange_date", ""));
							reqParam.add(new NameValuePair("beginDate", beginDate));
							reqParam.add(new NameValuePair("endDate", endDate));
							reqParam.add(new NameValuePair("exchangeType", ""));
							reqParam.add(new NameValuePair("channelf", "1"));
							reqParam.add(new NameValuePair("prodType", "5"));
							reqParam.add(new NameValuePair("chargeFzxh", context.getParam1()));
							logFlag = String.format("==>8."+i+"解析["+beginDate+"]第[2]页充值记录,第[{}]次尝试请求.....");

							rechargePage = getPage(webClient, BASE_URL+"/service/service/authority/query/rechargePage.do",
									HttpMethod.GET, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, null);
							if(null!=rechargePage){
								JSONObject cd = JSONObject.parseObject(rechargePage.getWebResponse().getContentAsString());
								if(null != cd && recharge.getInteger("count")>0){//有充值记录结果
									parser.rechargeParse(recharge, carrierInfo);
								}else{
									getLogger().error("==>8.{}解析[{}]充值记录出错了.", i, beginDate);
								}
							}else{
								getLogger().error("==>8.{}解析[{}]充值记录出错了.", i, beginDate);
							}
						}
					}else{
						getLogger().info("==>8.{}解析[{}]充值记录,没有查询结果.", i, beginDate);
					}
				}else{
					getLogger().info("==>8.{}解析[{}]充值记录结束,没有查询结果.", i, beginDate);
				}
				calendar.add(Calendar.MONTH, -1);
			}

			result.setSuccess();
		}catch (Exception e){
			logger.error("==>8.解析充值记录出现异常:[{}]", e);
			result.setResult(StatusCode.解析充值记录出错);
			return result;
		}
		return result;
	}
    //验证码
	public Result GetMsgCode(Context context, WebClient webClient) {
		HashMap<String, String> header = new HashMap<>();
		Result result = new Result();
		List<NameValuePair> reqParam = new ArrayList<>();
		String url = "";
		Page page = null;
		try {
			header.put("Referer", "http://service.sh.189.cn/service/query/detail");
			header.put("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36");
			url = "http://service.sh.189.cn/service/service/authority/query/billdetail/sendCode.do?flag=1&devNo="+context.getUserName()+"&dateType=&moPingType=LOCAL&startDate=&endDate=";
			page = getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, header);
			if (page.getWebResponse().getContentAsString().contains("\"result\":true")) {//短信发送成功
				result.setResult(StatusCode.爬取中);
				context.setTaskSubStatus(Constants.VALIDATE_TYPE_CODE_SMS);
				sendUpdateLog(result, context);
				boolean isFlag = sendValidataMsgByCrawling(context, Constants.ENQUEUE_TIME_OUT, Constants.VALIDATE_TYPE_CODE_SMS, null);
				if (isFlag) {
					String verify_code = rotation(context,webClient,120);//获取到验证码
					if("SMS_CODE".equals(verify_code)){
						header.put("Referer", "http://service.sh.189.cn/service/query/detail");
						header.put("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36");
						url = "http://service.sh.189.cn/service/service/authority/query/billdetail/sendCode.do?flag=1&devNo="+context.getUserName()+"&dateType=&moPingType=LOCAL&startDate=&endDate=";
						page = getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, header);
						isFlag = sendValidataMsgByCrawling(context, Constants.ENQUEUE_TIME_OUT, Constants.VALIDATE_TYPE_CODE_SMS, null);
						if (isFlag) {
							verify_code = rotation(context,webClient,120);
						}
					}//处理
					url = "http://service.sh.189.cn/service/service/authority/query/billdetail/validate.do";
					reqParam.add(new NameValuePair("input_code", verify_code));
					reqParam.add(new NameValuePair("selDevid", context.getUserName()));
					reqParam.add(new NameValuePair("flag", "nocw"));
					reqParam.add(new NameValuePair("checkCode", "验证码"));
					page = getPage(webClient, url,
							HttpMethod.GET, reqParam, Constants.MAX_RETRY_TIIMES, null, null);
					if(StringUtils.contains(page.getWebResponse().getContentAsString(),"\"CODE\":\"0\"")){//验证成功
						result.setSuccess();
					}else{
						result.setResult(StatusCode.短信验证码错误);
					}
					return result;
				} else {
					logger.info("==>[{}]1.1发送短信验证码失败.", context.getTaskId());
					result.setResult(StatusCode.发送短信验证码失败);
					return result;
				}
			} else {
				logger.info("==>上海电信动态密码发送失败!");
				return new Result(StatusCode.发送短信验证码失败);
			}

		} catch (Exception ex) {
			logger.error("==短信验证码异常:[{}]", ex);
			result.setResult(StatusCode.解析办理业务信息出错);
			return result;
		}
	}
	
	@Override
	public Result loginout(WebClient webClient){
		try{
			logger.info("==>9.上海电信退出开始.....");
			getPage(webClient, BASE_URL + "/service/DQlogoutLocalToJT", HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, null);
		}catch(Exception e){
			logger.error("==>退出出错,影响主流程", e);
		}
		return new Result(StatusCode.SUCCESS);
	}

	@Override
	public Logger getLogger(){
		return logger;
	}
}