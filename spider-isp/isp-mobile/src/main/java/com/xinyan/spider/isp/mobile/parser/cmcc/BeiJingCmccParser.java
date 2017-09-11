package com.xinyan.spider.isp.mobile.parser.cmcc;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gargoylesoftware.htmlunit.Page;
import com.xinyan.spider.isp.base.CacheContainer;
import com.xinyan.spider.isp.base.Context;
import com.xinyan.spider.isp.base.ProcessorCode;
import com.xinyan.spider.isp.base.Result;
import com.xinyan.spider.isp.common.utils.DateUtils;
import com.xinyan.spider.isp.common.utils.RegexUtils;
import com.xinyan.spider.isp.common.utils.StringUtils;
import com.xinyan.spider.isp.mobile.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author：WenqiangPu
 * @Description
 * @Date：Created in 9:46 2017/7/26
 * @Modified By：
 */
@Component
public class BeiJingCmccParser {
	protected static Logger logger = LoggerFactory.getLogger(BeiJingCmccParser.class);

	/**
	 *
	 * 解析基本信息
	 *
	 * @Author: WenqiangPu
	 * @Description:
	 * @param context
	 * @param cacheContainer
	 * @param carrierInfo
	 * @return:
	 * @Date: 10:32 2017/7/26
	 */
	public Result basicInfoParse(Context context, CacheContainer cacheContainer, CarrierInfo carrierInfo) {
		Result result = new Result();
		// 个人基本信息解析
		Page page = cacheContainer.getPage(ProcessorCode.BASIC_INFO.getCode());
		String strBasicData = page.getWebResponse().getContentAsString();
		// 手机号、真实姓名、入网时间、联系地址、身份证号码、Email
		carrierInfo.getCarrierUserInfo().setMappingId(carrierInfo.getMappingId()); // 映射id
		carrierInfo.getCarrierUserInfo().setMobile(context.getUserName()); // 手机号码
		carrierInfo.getCarrierUserInfo().setName(RegexUtils.matchValue("\"name\":\"(.*?)\"", strBasicData)); // 姓名
		carrierInfo.getCarrierUserInfo().setCarrier("CHINA_MOBILE"); // 运营商
		// CHINA_MOBILE
		// 中国移动
		// CHINA_TELECOM
		// 中国电信
		// CHINA_UNICOM
		// 中国联通
//		if(context.getIdCard() != null) {
//			carrierInfo.getCarrierUserInfo().setIdCard(context.getIdCard().substring(0, context.getIdCard().length() - 4) + "****"); // 证件号
//		}
		carrierInfo.getCarrierUserInfo().setProvince("北京"); // 所属省份
		carrierInfo.getCarrierUserInfo().setAddress(RegexUtils.matchValue("\"address\":\"(.*?)\"", strBasicData)); // 地址
		String level = RegexUtils.matchValue("\"starLevel\":\"(.*?)\"", strBasicData);
		if(level.equals("0")){
			carrierInfo.getCarrierUserInfo().setLevel("无星级"); // 星级
		}else if(level.equals("1")){
			carrierInfo.getCarrierUserInfo().setLevel("一星级"); // 星级
		}else if(level.equals("2")){
			carrierInfo.getCarrierUserInfo().setLevel("二星级"); // 星级
		}else if(level.equals("3")){
			carrierInfo.getCarrierUserInfo().setLevel("三星级"); // 星级
		}else if(level.equals("4")){
			carrierInfo.getCarrierUserInfo().setLevel("四星级"); // 星级
		}else if(level.equals("5")){
			carrierInfo.getCarrierUserInfo().setLevel("五星级"); // 星级
		}
		carrierInfo.getCarrierUserInfo()
				.setOpenTime(DateUtils.dateToString(DateUtils
								.stringToDate(RegexUtils.matchValue("\"inNetDate\":\"(.*?)\"", strBasicData), "yyyyMMddHHmmss"),
						"yyyy-MM-dd")); // 入网时间，格式：yyyy-MM-dd
		carrierInfo.getCarrierUserInfo()
				.setState("00".equals(RegexUtils.matchValue("\"status\":\"(.*?)\"", strBasicData)) ? 0 : -1); // 帐号状态,
		// -1未知
		// 0正常
		// 1单向停机
		// 2停机
		// 3预销户
		// 4销户
		// 5过户
		// 6改号
		// 99号码不存在
		carrierInfo.getCarrierUserInfo()
				.setLastModifyTime(DateUtils.dateToString(DateUtils
								.stringToDate(RegexUtils.matchValue("\"sOperTime\":\"(.*?)\"", strBasicData), "yyyyMMddHHmmss"),
						"yyyy-MM-dd HH:mm:ss")); // 最近一次更新时间，格式: yyyy-MM-dd
		// HH:mm:ss

		// 余额、星级
		page = cacheContainer.getPage(ProcessorCode.AMOUNT.getCode());
		strBasicData = page.getWebResponse().getContentAsString();
		carrierInfo.getCarrierUserInfo().setAvailableBalance(
				(int) (Double.parseDouble(RegexUtils.matchValue("\"curFee\":\"(.*?)\"", strBasicData)) * 100));
		// 套餐
		page = cacheContainer.getPage(ProcessorCode.POINTS_VALUE.getCode());
		strBasicData = page.getWebResponse().getContentAsString();
		carrierInfo.getCarrierUserInfo()
				.setPackageName(RegexUtils.matchValue("\"curPlanName\":\"(.*?)\"", strBasicData)); // 套餐
		result.setSuccess();
		return result;
	}

	/**
	 * 解析通话记录
	 *
	 * @Author: WenqiangPu
	 * @Description:
	 * @param context
	 * @param cacheContainer
	 * @param carrierInfo
	 * @return:
	 * @Date: 10:33 2017/7/26
	 */
	public Result callRecordParse(Context context, CacheContainer cacheContainer, CarrierInfo carrierInfo) {

		Result result = new Result();

		// 通话记录
		List<CarrierCallDetailInfo> calls = new ArrayList<>();
		List<Page> pages = cacheContainer.getPages(ProcessorCode.CALLRECORD_INFO.getCode());

		if (pages != null) {
			for (Page page : pages) {
				String strTemp = OpeStrToJson(page.getWebResponse().getContentAsString());
				JSONObject jsonObject = JSONObject.parseObject(strTemp);
				JSONArray callDataArr = jsonObject.getJSONArray("data");
				if (callDataArr != null) {
					for (int i = 0; i < callDataArr.size(); i++) {
						JSONObject callData = (JSONObject) callDataArr.get(i);
						CarrierCallDetailInfo carrierCallDetailInfo = new CarrierCallDetailInfo();
						carrierCallDetailInfo.setMappingId(carrierInfo.getMappingId());
						carrierCallDetailInfo.setBillMonth(DateUtils.dateToString(
								DateUtils.stringToDate(jsonObject.getString("startDate"), "yyyyMMdd"), "yyyy-MM")); // 通话月份，格式：yyyy-MM
						carrierCallDetailInfo.setPeerNumber(callData.getString("anotherNm"));// 对方号码
						carrierCallDetailInfo.setLocation(callData.getString("commPlac")); // 通话地(自己的)
						carrierCallDetailInfo.setTime(carrierCallDetailInfo.getBillMonth().substring(0, 4) + "-" + callData.getString("startTime")); // 通话时间
						carrierCallDetailInfo.setLocationType(callData.getString("commType")); // 通话地类型.
						// e.g.省内漫游
						if ("主叫".equals(callData.getString("commMode"))) {
							carrierCallDetailInfo.setDialType("DIAL");
						} else if ("被叫".equals(callData.getString("commMode"))) {
							carrierCallDetailInfo.setDialType("DIALED");
						} else {
							carrierCallDetailInfo.setDialType("");
						}
						String commTime = callData.getString("commTime").replace("时", ":").replace("分", ":").replace("秒", ":"); // 通话时长(单位:秒)
						if (!commTime.contains("--")) {
							String[] commTimeArr = commTime.split(":");
							if (commTimeArr.length == 3) {
								carrierCallDetailInfo.setDuration(String.valueOf(Integer.parseInt(commTimeArr[0]) * 60 * 60 + Integer.parseInt(commTimeArr[1]) * 60 + Integer.parseInt(commTimeArr[2])));
							} else if (commTimeArr.length == 2) {
								carrierCallDetailInfo.setDuration(String.valueOf(Integer.parseInt(commTimeArr[0]) * 60 + Integer.parseInt(commTimeArr[1])));
							} else if (commTimeArr.length == 1) {
								carrierCallDetailInfo.setDuration(String.valueOf(Integer.parseInt(commTimeArr[0])));
							} else {
								carrierCallDetailInfo.setDuration("");
							}
						}
						carrierCallDetailInfo.setFee((int) (Double.parseDouble(callData.getString("commFee")) * 100)); // 费用
						calls.add(carrierCallDetailInfo);
					}
				}
			}
		}

		carrierInfo.setCalls(calls);
		result.setSuccess();
		return result;
	}

	/**
	 * 解析短信记录
	 *
	 * @Author: WenqiangPu
	 * @Description:
	 * @param context
	 * @param cacheContainer
	 * @param carrierInfo
	 * @return:
	 * @Date: 10:34 2017/7/26
	 */
	public Result smsInfoParse(Context context, CacheContainer cacheContainer, CarrierInfo carrierInfo) {

		Result result = new Result();

		// 短信记录
		List<CarrierSmsRecordInfo> smses = new ArrayList<>();
		List<Page> pages = cacheContainer.getPages(ProcessorCode.SMS_INFO.getCode());
		if (pages != null) {
			for (Page page : pages) {
				String strTemp = OpeStrToJson(page.getWebResponse().getContentAsString());
				JSONObject jsonObject = JSONObject.parseObject(strTemp);
				JSONArray smsDataArr = jsonObject.getJSONArray("data");
				if (smsDataArr != null) {
					for (int i = 0; i < smsDataArr.size(); i++) {
						JSONObject smsData = (JSONObject) smsDataArr.get(i);
						CarrierSmsRecordInfo carrierSmsRecordInfo = new CarrierSmsRecordInfo();
						carrierSmsRecordInfo.setMappingId(carrierInfo.getMappingId());
						carrierSmsRecordInfo.setBillMonth(DateUtils.dateToString(
								DateUtils.stringToDate(jsonObject.getString("startDate"), "yyyyMMdd"), "yyyy-MM")); // 通话月份，格式：yyyy-MM
						carrierSmsRecordInfo.setTime(carrierSmsRecordInfo.getBillMonth().substring(0, 4) + "-" + smsData.getString("startTime")); // 发送时间
						carrierSmsRecordInfo.setPeerNumber(smsData.getString("anotherNm"));// 对方号码
						carrierSmsRecordInfo.setLocation(smsData.getString("commPlac")); // 通话地(自己的)
						if ("发送".equals(smsData.getString("commMode"))) {
							carrierSmsRecordInfo.setSendType("SEND");// SEND-发送;   // RECEIVE-收取
						} else if ("接收".equals(smsData.getString("commMode"))) {
							carrierSmsRecordInfo.setSendType("RECEIVE");
						} else {
							carrierSmsRecordInfo.setSendType("");
						}
						if (smsData.getString("infoType").contains("短信")) {
							carrierSmsRecordInfo.setMsgType("SMS");// SMS-短信;// MMS-彩信
						} else if (smsData.getString("infoType").contains("彩信")) {
							carrierSmsRecordInfo.setMsgType("MMS");
						} else {
							carrierSmsRecordInfo.setMsgType("");
						}
						// MMS-彩信
						carrierSmsRecordInfo.setServiceName(smsData.getString("infoType"));// 业务名称.
						// e.g.
						// 点对点(网内)
						carrierSmsRecordInfo.setFee((int) (Double.parseDouble(smsData.getString("commFee")) * 100));// 通话费(单位:分)
						smses.add(carrierSmsRecordInfo);
					}
				}
			}
		}

		carrierInfo.setSmses(smses);
		result.setSuccess();
		return result;
	}

	/**
	 * 解析上网记录
	 *
	 * @Author: WenqiangPu
	 * @Description:
	 * @param context
	 * @param cacheContainer
	 * @param carrierInfo
	 * @return:
	 * @Date: 10:34 2017/7/26
	 */
	public Result netInfoParse(Context context, CacheContainer cacheContainer, CarrierInfo carrierInfo) {
		Result result = new Result();

		// 上网记录
		List<CarrierNetDetailInfo> nets = new ArrayList<>();
		List<Page> pages = cacheContainer.getPages(ProcessorCode.NET_INFO.getCode());

		if (pages != null) {
			for (Page page : pages) {
				String strTemp = OpeStrToJson(page.getWebResponse().getContentAsString());
				JSONObject jsonObject = JSONObject.parseObject(strTemp);
				JSONArray netDataArr = jsonObject.getJSONArray("data");
				if (netDataArr != null) {
					for (int i = 0; i < netDataArr.size(); i++) {
						JSONObject netData = (JSONObject) netDataArr.get(i);
						CarrierNetDetailInfo carrierNetDetailInfo = new CarrierNetDetailInfo();
						carrierNetDetailInfo.setMappingId(carrierInfo.getMappingId());
						carrierNetDetailInfo.setBillMonth(DateUtils.dateToString(
								DateUtils.stringToDate(jsonObject.getString("startDate"), "yyyyMMdd"), "yyyy-MM")); // 通话月份，格式：yyyy-MM
						carrierNetDetailInfo.setTime(
								carrierNetDetailInfo.getBillMonth().substring(0, 4) + "-" + netData.getString("startTime")); // 上网时间
						if (StringUtils.isNotEmpty(netData.getString("commTime"))) {
							String[] durationArr = netData.getString("commTime").replace("时",":").replace("分",":").replace("秒","").split(":");
							int duration = 0;
							if (durationArr.length == 3) {
								duration = Integer.parseInt(durationArr[0]) * 60 * 60
										+ Integer.parseInt(durationArr[1]) * 60 + Integer.parseInt(durationArr[2]);
							} else if (durationArr.length == 2) {
								duration = Integer.parseInt(durationArr[0]) * 60 + Integer.parseInt(durationArr[1]);
							} else {
								duration = Integer.parseInt(durationArr[0]);
							}
							carrierNetDetailInfo.setDuration(duration); // 上网时长
						}
						int sumFlow = Integer.parseInt(netData.getString("sumFlow"));
						carrierNetDetailInfo.setSubflow(sumFlow);// 流量使用量，单位:KB
						carrierNetDetailInfo.setLocation(netData.getString("commPlac"));// 流量使用地点
						carrierNetDetailInfo.setNetType(netData.getString("netType"));// 网络类型
						carrierNetDetailInfo.setServiceName(netData.getString("meal"));// 业务名称.
						// e.g.
						// 点对点(网内)
						carrierNetDetailInfo.setFee((int) (Double.parseDouble(netData.getString("commFee")) * 100));// 通话费(单位:分)
						nets.add(carrierNetDetailInfo);
					}
				}
			}
		}

		carrierInfo.setNets(nets);
		result.setSuccess();
		return result;
	}

	/**
	 * 解析账单信息
	 * @Author: WenqiangPu
	 * @Description:
	 * @param context
	 * @param cacheContainer
	 * @param carrierInfo
	 * @return:
	 * @Date: 10:35 2017/7/26
	 */
	public Result billParse(Context context, CacheContainer cacheContainer, CarrierInfo carrierInfo) {

		Result result = new Result();

		List<CarrierBillDetailInfo> bills = new ArrayList<>();
		Page page = cacheContainer.getPage(ProcessorCode.BILL_INFO.getCode());
		if(page != null) {
			JSONObject jsonObject = JSONObject.parseObject(page.getWebResponse().getContentAsString());
			JSONArray billDataArr = jsonObject.getJSONArray("data");
			if (billDataArr != null) {
				for (int i = 0; i < billDataArr.size(); i++) {
					JSONObject billData = (JSONObject) billDataArr.get(i);
					if (billData.getString("billStartDate") == null) {
						continue;
					}
					CarrierBillDetailInfo carrierBillDetailInfo = new CarrierBillDetailInfo();
					carrierBillDetailInfo.setMappingId(carrierInfo.getMappingId());
					carrierBillDetailInfo.setBillMonth(DateUtils
							.dateToString(DateUtils.stringToDate(billData.getString("billMonth"), "yyyyMM"), "yyyy-MM"));// 账单月，格式：yyyy-MM
					carrierBillDetailInfo.setBillStartDate(DateUtils.dateToString(
							DateUtils.stringToDate(billData.getString("billStartDate"), "yyyyMMdd"), "yyyy-MM-dd"));// 账期起始日期，格式：yyyy-MM-dd
					carrierBillDetailInfo.setBillEndDate(DateUtils
							.dateToString(DateUtils.stringToDate(billData.getString("billEndDate"), "yyyyMMdd"), "yyyy-MM-dd"));// 账期结束日期，格式：yyyy-MM-dd
					JSONArray billMaterialsArr = billData.getJSONArray("billMaterials");
					carrierBillDetailInfo.setBaseFee(
							(int) (Double.parseDouble(((JSONObject) billMaterialsArr.get(0)).getString("billEntriyValue"))
									* 100));// 套餐及固定费 单位分
					carrierBillDetailInfo.setExtraServiceFee(
							(int) (Double.parseDouble(((JSONObject) billMaterialsArr.get(4)).getString("billEntriyValue"))
									* 100));// 增值业务费 单位分
					carrierBillDetailInfo.setVoiceFee(
							(int) (Double.parseDouble(((JSONObject) billMaterialsArr.get(1)).getString("billEntriyValue"))
									* 100));// 语音费 单位分
					carrierBillDetailInfo.setSmsFee(
							(int) (Double.parseDouble(((JSONObject) billMaterialsArr.get(3)).getString("billEntriyValue"))
									* 100));// 短彩信费 单位分
					carrierBillDetailInfo.setWebFee(
							(int) (Double.parseDouble(((JSONObject) billMaterialsArr.get(2)).getString("billEntriyValue"))
									* 100));// 网络流量费 单位分
					carrierBillDetailInfo.setExtraFee(
							(int) (Double.parseDouble(((JSONObject) billMaterialsArr.get(6)).getString("billEntriyValue"))
									* 100));// 其它费用 单位分
					if (billData.getString("billFee").contains("-")) {
						carrierBillDetailInfo.setTotalFee(0);
						carrierBillDetailInfo.setActualFee(0);
					} else {
						carrierBillDetailInfo.setTotalFee((int) (Double.parseDouble(billData.getString("billFee")) * 100));// 总费用
						carrierBillDetailInfo.setActualFee((int) (Double.parseDouble(billData.getString("billFee")) * 100));// 个人实际费用
					}
					carrierBillDetailInfo.setDiscount(
							(int) (Double.parseDouble(((JSONObject) billMaterialsArr.get(7)).getString("billEntriyValue"))
									* 100));// 优惠费 单位分
					carrierBillDetailInfo.setExtraDiscount(
							(int) (Double.parseDouble(((JSONObject) billMaterialsArr.get(8)).getString("billEntriyValue"))
									* 100));// 其它优惠 单位分
					// 单位分
					bills.add(carrierBillDetailInfo);
				}
			}
		}
		carrierInfo.setBills(bills);
		result.setSuccess();
		return result;
	}

	/**
	 * 解析套餐信息
	 *
	 * @Author: WenqiangPu
	 * @Description:
	 * @param context
	 * @param cacheContainer
	 * @param carrierInfo
	 * @return:
	 * @Date: 10:35 2017/7/26
	 */
	public Result packageItemInfoParse(Context context, CacheContainer cacheContainer, CarrierInfo carrierInfo) {
		Result result = new Result();

		List<CarrierPackageItemInfo> packageItemInfos = new ArrayList<>();
		Page page = cacheContainer.getPage(ProcessorCode.PACKAGE_ITEM.getCode());
		if(page != null){
			JSONObject packageItemInfoObj = JSONObject.parseObject(page.getWebResponse().getContentAsString());
			JSONArray dataArr = packageItemInfoObj.getJSONArray("data");
			if(dataArr != null) {
				JSONArray arrArr = ((JSONObject) dataArr.get(0)).getJSONArray("arr");
				for (Object object : arrArr) {
					JSONObject jsonObject = (JSONObject) object;
					JSONArray packJson = jsonObject.getJSONArray("resInfos");
					if(packJson!=null){
						for(Object obj:packJson){
							JSONObject jso = (JSONObject) obj;//
							JSONArray secResInfoArr = jso.getJSONArray("secResInfos");
							for (Object secoObj : secResInfoArr) {
								CarrierPackageItemInfo packageItemInfo = new CarrierPackageItemInfo();
								packageItemInfo.setMappingId(carrierInfo.getMappingId());
								JSONObject resConInfo = ((JSONObject) secoObj).getJSONObject("resConInfo");
								packageItemInfo.setItem(((JSONObject) secoObj).getString("resConName")); // 套餐项目名称
								packageItemInfo.setUsed(resConInfo.getString("useMeal")); // 项目已使用量
								if ("01".equals(resConInfo.getString("unit"))) {
									packageItemInfo.setUnit("分");// 单位：语音-分;
									// 流量-KB;
									// 短/彩信-条
								} else if ("03".equals(resConInfo.getString("unit"))) {
									packageItemInfo.setUnit("KB");// 单位：语音-分;
									// 流量-KB;
									// 短/彩信-条
								} else {
									packageItemInfo.setUnit("条");// 单位：语音-分;
									// 流量-KB;
									// 短/彩信-条
								}
								packageItemInfo.setTotal(resConInfo.getString("totalMeal"));//项目总量
								packageItemInfo.setBillStartDate(DateUtils.getFirstDay("yyyy-MM-dd", 0));
								packageItemInfo.setBillEndDate(DateUtils.getLastDay("yyyy-MM-dd", 0));
								packageItemInfos.add(packageItemInfo);
							}
						}
					}

				}
			}
		}
		carrierInfo.setPackages(packageItemInfos);
		result.setSuccess();
		return result;

	}

	/**
	 * 解析充值信息
	 *
	 * @Author: WenqiangPu
	 * @Description:
	 * @param context
	 * @param cacheContainer
	 * @param carrierInfo
	 * @return:
	 * @Date: 10:36 2017/7/26
	 */
	public Result userRechargeItemInfoParse(Context context, CacheContainer cacheContainer, CarrierInfo carrierInfo) {
		Result result = new Result();
		List<CarrierUserRechargeItemInfo> rechargeItemInfos = new ArrayList<>();
		Page page = cacheContainer.getPage(ProcessorCode.RECHARGE_INFO.getCode());
		if(page != null){
			JSONObject packageItemInfoObj = JSONObject.parseObject(page.getWebResponse().getContentAsString());
			JSONArray dataArr = packageItemInfoObj.getJSONArray("data");
			if (dataArr != null) {
				for (Object object : dataArr) {
					CarrierUserRechargeItemInfo rechargeItemInfo = new CarrierUserRechargeItemInfo();
					rechargeItemInfo.setMappingId(carrierInfo.getMappingId());
					JSONObject jsonObject = (JSONObject) object;
					rechargeItemInfo.setAmount((int) (Double.parseDouble(jsonObject.getString("payFee")) * 100));// 充值金额(单位:
					// 分)
					rechargeItemInfo.setBillMonth(DateUtils.dateToString(
							DateUtils.stringToDate(jsonObject.getString("payDate"), "yyyyMMddHHmmss"), "yyyy-MM"));// 充值月份，格式：yyyy-MM
					rechargeItemInfo.setType(jsonObject.getString("payTypeName"));// 充值方式.
					// e.g.
					// 现金
					rechargeItemInfo.setRechargeTime(DateUtils.dateToString(
							DateUtils.stringToDate(jsonObject.getString("payDate"), "yyyyMMddHHmmss"),
							"yyyy-MM-dd HH:mm:ss"));// 充值时间，格式：yyyy-MM-dd HH:mm:ss
					rechargeItemInfos.add(rechargeItemInfo);
				}
			}
		}
		carrierInfo.setRecharges(rechargeItemInfos);
		result.setSuccess();
		return result;
	}

	/**
	 * 处理字符串成json格式
	 *
	 * @param str
	 * @return
	 * @description
	 * @author heliang
	 * @create 2016-09-02 16:43
	 */
	private static String OpeStrToJson(String str) {
		String opeStr = "";
		String strOpe = "jQuery18308911999785481797_1494312492711(";
		if (StringUtils.isNotBlank(str) && StringUtils.contains(str, "(")) {
			opeStr = str.substring(strOpe.length(), str.length() - 1);
		} else {
			opeStr = str;
		}

		return opeStr;
	}
}
