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
import com.xinyan.spider.isp.base.CharsetCode;
import com.xinyan.spider.isp.base.Context;
import com.xinyan.spider.isp.base.ProcessorCode;
import com.xinyan.spider.isp.base.Result;
import com.xinyan.spider.isp.base.StatusCode;
import com.xinyan.spider.isp.common.Constants;
import com.xinyan.spider.isp.common.utils.DateUtils;
import com.xinyan.spider.isp.common.utils.JavaScriptUtils;
import com.xinyan.spider.isp.common.utils.RegexUtils;
import com.xinyan.spider.isp.common.utils.StringUtils;
import com.xinyan.spider.isp.mobile.model.CarrierInfo;
import com.xinyan.spider.isp.mobile.parser.telecom.ZheJiangTelecomParser;

/**
 * 浙江电信处理类
 * @description
 * @author yyj
 * @date 2017年4月21日 下午3:38:43
 * @version V1.0
 */
@Component
public class ZheJiangTelecomPssor extends AbstractTelecomPssor{
    protected static Logger logger = LoggerFactory.getLogger(ZheJiangTelecomPssor.class);

    @Autowired
    private ZheJiangTelecomParser parser;

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

            String url = "http://www.189.cn/zj/service/";
            getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, null);

            String password = JavaScriptUtils.invoker("js/ChinaTelecomDes.js" ,"valAesEncryptSet", context.getPassword());
            context.setPassword(password);
            
            url = "http://login.189.cn/login";
            List<NameValuePair> reqParam = new ArrayList<>();
            reqParam.clear();
            reqParam.add(new NameValuePair("Account", context.getUserName()));
            reqParam.add(new NameValuePair("UType", "201"));
            reqParam.add(new NameValuePair("ProvinceID", "12"));
            reqParam.add(new NameValuePair("AreaCode", ""));
            reqParam.add(new NameValuePair("CityNo", ""));
            reqParam.add(new NameValuePair("RandomFlag", "0"));
            reqParam.add(new NameValuePair("Password", context.getPassword()));
            reqParam.add(new NameValuePair("Captcha", ""));
            Page page =getPage(webClient, url, HttpMethod.POST, reqParam,Constants.MAX_SENDMSG_TIIMES,null, null);
            if(StringUtils.contains("用户密码错误", page.getWebResponse().getContentAsString())){//用户密码错误
            	result.setResult(StatusCode.用户名或密码错误);
            }else{
                url = "http://www.189.cn/login/index.do";
                page = getPage(webClient, url, HttpMethod.POST, null,Constants.MAX_SENDMSG_TIIMES, null,null);
            	if(null != page){
            		String pageInfo = page.getWebResponse().getContentAsString();
            		pageInfo = pageInfo.replaceAll("\"", "'");
            		if(StringUtils.contains(pageInfo,"'code':'0'")){
            			result = sendSMS(webClient, context);//发送短信验证码
            		}else{
            			result.setResult(StatusCode.用户名或密码错误);
            		}
            	}else{
            		result.setResult(StatusCode.用户名或密码错误);
            	}            	
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
            
            String url = "http://login.189.cn/login";
            List<NameValuePair> reqParam = new ArrayList<>();
            reqParam.clear();
            reqParam.add(new NameValuePair("Account", context.getUserName()));
            reqParam.add(new NameValuePair("UType", "201"));
            reqParam.add(new NameValuePair("ProvinceID", "12"));
            reqParam.add(new NameValuePair("AreaCode", ""));
            reqParam.add(new NameValuePair("CityNo", ""));
            reqParam.add(new NameValuePair("RandomFlag", "0"));
            reqParam.add(new NameValuePair("Password", context.getPassword()));
            reqParam.add(new NameValuePair("Captcha", context.getUserInput()));

            Page page = getPage(webClient, url, HttpMethod.POST, reqParam,Constants.MAX_SENDMSG_TIIMES, null,null);
            if(StringUtils.contains("用户密码错误", page.getWebResponse().getContentAsString())){//用户密码错误
            	result.setResult(StatusCode.用户名或密码错误);
            }else{
                url = "http://www.189.cn/login/index.do";
                page = getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES, null,null);
            	if(null != page){
            		String pageInfo = page.getWebResponse().getContentAsString();
            		pageInfo = pageInfo.replaceAll("\"", "'");
            		if(StringUtils.contains(pageInfo,"'code':'0'")){
            			result = sendSMS(webClient, context);//发送短信验证码
            		}else{
            			result.setResult(StatusCode.用户名或密码错误);
            		}
            	}else{
            		result.setResult(StatusCode.用户名或密码错误);
            	}            	
            }

            logger.info("==>1.[{}]用户图片认证结束{}", context.getTaskId(), result);
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
            Map<String, String > header = new HashMap<>();
            List<NameValuePair> reqParam = new ArrayList<>();

            // 1.开始登陆
            logger.info("==>1.[{}]用户短信认证开始.....", context.getTaskId());

            String url = "http://zj.189.cn/zjpr/cdr/getCdrDetail.htm";
            header.clear();
            header.put("Content-Type", "application/x-www-form-urlencoded");
            header.put("Referer", "http://zj.189.cn/zjpr/cdr/getCdrDetailInput.htm");
            reqParam.clear();
            
            reqParam.add(new NameValuePair("cdrCondition.pagenum", "1"));
            reqParam.add(new NameValuePair("cdrCondition.pagesize", "100"));
            reqParam.add(new NameValuePair("cdrCondition.productnbr", context.getUserName()));
            reqParam.add(new NameValuePair("cdrCondition.areaid", context.getParam1()));
            reqParam.add(new NameValuePair("cdrCondition.cdrlevel", ""));
            reqParam.add(new NameValuePair("cdrCondition.productid", context.getParam2()));
            reqParam.add(new NameValuePair("cdrCondition.product_servtype", context.getParam3()));
            reqParam.add(new NameValuePair("cdrCondition.recievenbr", context.getParam4()));
            reqParam.add(new NameValuePair("cdrCondition.cdrmonth", DateUtils.getCurrentMonth()));
            reqParam.add(new NameValuePair("cdrCondition.cdrtype", "11"));
            reqParam.add(new NameValuePair("username", context.getIdName()));
            reqParam.add(new NameValuePair("idcard", context.getIdCard()));
            reqParam.add(new NameValuePair("cdrCondition.randpsw", context.getUserInput()));
            context.setParam6(context.getUserInput());
            
            Page page = getPage(webClient, url, HttpMethod.POST, reqParam,Constants.MAX_SENDMSG_TIIMES,null, header, CharsetCode.GB2312);
            if(page.getWebResponse().getContentAsString().contains("随机验证码输入有误")){
                result.setResult(StatusCode.短信验证码错误);
            }else if(page.getWebResponse().getContentAsString().contains("出错提示")){
                result.setResult(StatusCode.短信验证码错误);
            }else{
                logger.info("==>1.[{}]短信验证码正确.", context.getTaskId());
                result.setResult(StatusCode.登陆成功);
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
        logger.info("==>1.[{}]正在请求发送短信验证码.....", context.getTaskId());
        Result result = new Result();
        Map<String, String > header = new HashMap<>();
        
        String url = "http://www.189.cn/login/sso/ecs.do?method=linkTo&platNo=10012&toStUrl=http://zj.189.cn/service";
        getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, null);
        url = "http://zj.189.cn/service";
        getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, null);

        
        url = "http://zj.189.cn/bfapp/buffalo/cdrService";
        header.clear();
        header.put("Content-Type", "text/xml;charset=UTF-8");
        header.put("Referer", "http://zj.189.cn/zjpr/cdr/getCdrDetailInput.htm");
        String bodyStr = "<buffalo-call><method>querycdrasset</method></buffalo-call>";
        Page page = getPage(webClient, url, HttpMethod.POST, null,Constants.MAX_SENDMSG_TIIMES ,null,header, bodyStr);
        String pageInfo = page.getWebResponse().getContentAsString();
        
        context.setParam1(RegexUtils.matchValue("area_id</string><long>(.*?)</long>", pageInfo));
        context.setParam2(RegexUtils.matchValue("integration_id</string><string>(.*?)</string>", pageInfo));
        context.setParam3(RegexUtils.matchValue("serv_type_id</string><string>(.*?)</string", pageInfo));
        context.setParam4(RegexUtils.matchValue("serv_type_name</string><string>(.*?)</string>", pageInfo));
        context.setParam5(RegexUtils.matchValue("product_id</string><long>(.*?)</long>",pageInfo));
        context.setIdName(RegexUtils.matchValue("cust_name</string><string>(.*?)</string>", pageInfo));
        context.setIdCard(RegexUtils.matchValue("cust_reg_nbr</string><string>(.*?)</string>", pageInfo));
        
        //获取发送短信验证码参数
        url = "http://zj.189.cn/bfapp/buffalo/VCodeOperation";       
        bodyStr = "<buffalo-call><method>SendVCodeByNbr</method><string>"+context.getUserName()+"</string></buffalo-call>";
        page = getPage(webClient, url, HttpMethod.POST, null,Constants.MAX_SENDMSG_TIIMES,null, header, bodyStr);

        if(page.getWebResponse().getContentAsString().contains("成功")){
            logger.info("==>1.[{}]已经发送短信验证码...", context.getTaskId());
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
            logger.error("==>[{}]浙江数据解析出现异常:[{}]", context.getTaskId(), ex);
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
        Map<String, String > header = new HashMap<>();
        String url = "";
        String bodyStr = "";
        try {
            logger.info("==>1.1[{}]抓取个人积分开始.....", context.getTaskId());
            url = "http://zj.189.cn/bfapp/buffalo/demoService";
            header.clear();
            header.put("Content-Type", "text/xml;charset=UTF-8");
            bodyStr = "<buffalo-call><method>getjifen</method></buffalo-call>";
            Page page = getPage(webClient, url, HttpMethod.POST, null,Constants.MAX_SENDMSG_TIIMES,null, header,bodyStr);
            if(null != page){
                cacheContainer.putPage(ProcessorCode.POINTS_VALUE.getCode(), page);
            }
            logger.info("==>1.1[{}]采集个人积分成功.", context.getTaskId());

            logger.info("==>1.2[{}]采集个人余额开始.....", context.getTaskId());
            bodyStr = "<buffalo-call><method>getBlanceSts</method></buffalo-call>";
            page = getPage(webClient, url, HttpMethod.POST, null,Constants.MAX_SENDMSG_TIIMES,null, header,bodyStr);
            if(page != null){
                cacheContainer.putPage(ProcessorCode.AMOUNT.getCode(), page);
            }
            logger.info("==>1.2[{}]采集个人余额成功.", context.getTaskId());

            logger.info("==>1.3[{}]采集个人基本信息开始.....", context.getTaskId());
            url = "http://zj.189.cn/bfapp/buffalo/demoService";
            bodyStr = "<buffalo-call><method>getAllProductWithCustId_D</method></buffalo-call>";
            page = getPage(webClient, url, HttpMethod.POST, null,Constants.MAX_SENDMSG_TIIMES,null,header, bodyStr);
            String pageInfo = page.getWebResponse().getContentAsString();
            if(StringUtils.isNotEmpty(pageInfo)){
                cacheContainer.putString(ProcessorCode.BASIC_INFO.getCode(), pageInfo);
            }
            logger.info("==>1.3[{}]采集个人基本信息成功.", context.getTaskId());
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
        try {
            result = GetContentInfo(webClient, cacheContainer, context, "11", "通话详单", ProcessorCode.CALLRECORD_INFO.getCode());
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
        try {
            result = GetContentInfo(webClient, cacheContainer, context, "21", "短信记录", ProcessorCode.SMS_INFO.getCode());
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
        try {
            result = GetContentInfo(webClient, cacheContainer, context, "41", "上网记录", ProcessorCode.NET_INFO.getCode());
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
            String url = "http://zj.189.cn/zjpr/bill/getBillDetail.htm";

            // 查询最近6个月账单
            for (int i = 1; i < (context.getRecordSize() + 1); i++) {//前6个月(不算当月)
                calendar.add(Calendar.MONTH, -1);
                final String month = DateFormatUtils.format(calendar, "yyyyMM");
                String logFlag = String.format("==>5."+i+"["+context.getTaskId()+"采集["+month+"]账单信息,第[{}]次尝试请求.....");
                reqParam.clear();
                reqParam.add(new NameValuePair("pr_billDomain.bill_month", month));
                reqParam.add(new NameValuePair("pr_billDomain.product_id", context.getParam5()));
                reqParam.add(new NameValuePair("pr_billDomain.query_type", "1"));
                reqParam.add(new NameValuePair("pr_billDomain.bill_type", "0"));
                reqParam.add(new NameValuePair("flag", "htzd"));
				Page page = getPage(webClient, url, HttpMethod.POST, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, null);
                if(page.getWebResponse().getContentAsString().contains("帐户名称：")){
                    logger.info("==>5.[{}]采集[{}]账单信息成功.", context.getTaskId(), month);
                    pages.add(page);
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
            Calendar calendar = Calendar.getInstance();
            List<NameValuePair> reqParam = new ArrayList<>();
            Map<String, String> header = new HashMap<>();
            header.put("Content-Type", "application/x-www-form-urlencoded");
            header.put("Referer", "Referer: http://zj.189.cn/zjpr/servicenew/tcAmount/tcAmount.html");
            List<Page> pages = new ArrayList<>();
            String url = "http://zj.189.cn/zjpr/servicenew/tcAmount/queryTaoCan.html";

            // 查询最近6个月套餐
            for (int i = 1; i < (context.getRecordSize() + 1); i++) {
                final String month = DateFormatUtils.format(calendar, "yyyyMM");
                String logFlag = String.format("==>6."+i+"["+context.getTaskId()+"采集["+month+"]套餐信息,第[{}]次尝试请求.....");
                reqParam.clear();
                reqParam.add(new NameValuePair("queryDate", month));
                reqParam.add(new NameValuePair("type", "tc"));
				Page page = getPage(webClient, url, HttpMethod.POST, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, null);
                if(null != page){
                    logger.info("==>6.[{}]采集[{}]套餐信息成功.", context.getTaskId(), month);
                    pages.add(page);
                }else{
                    logger.info("==>6.[{}]采集[{}]套餐信息结束,没有查询结果.", context.getTaskId(), month);
                }
                calendar.add(Calendar.MONTH, -1);
            }
            cacheContainer.putPages(ProcessorCode.PACKAGE_ITEM.getCode(), pages);
            result.setSuccess();
        } catch (Exception e) {
            logger.error("==>6.[{}]采集账单信息出现异常:[{}]", context.getTaskId(), e);
            result.setResult(StatusCode.解析账单信息出错);
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
            Map<String, String> header = new HashMap<>();
            header.put("Content-Type", "application/x-www-form-urlencoded");
            List<NameValuePair> reqParam = new ArrayList<>();
            String url = "http://zj.189.cn/zjpr/service/paym/payment_recharge.html";
            reqParam.clear();
            reqParam.add(new NameValuePair("pgPHS.startDate", "6"));
            String logFlag = String.format("==>8.["+context.getTaskId()+"]采集充值记录[半年以内],第[{}]次尝试请求.....");
            Page page = getPage(webClient, url, HttpMethod.GET, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, header);
            if (null != page) {// 添加缓存信息
            	cacheContainer.putPage(ProcessorCode.RECHARGE_INFO.getCode(), page);
                logger.info("==>8.[{}]采集充值记录[半年以内]成功.", context.getTaskId());
            } else {
                logger.info("==>8.[{}]采集充值记录[半年以内]结束,没有查询结果.", context.getTaskId());
            }
           
            result.setSuccess();
        } catch (Exception e) {
            logger.error("==>8.[{}]采集充值记录出现异常:[{}]", context.getTaskId(), e);
            result.setResult(StatusCode.解析充值记录出错);
            return result;
        }
        return result;
    }

//    result = GetContentInfo(webClient, cacheContainer, context, "3", "通话详单", "02", ProcessorCode.CALLRECORD_INFO.getCode());
    public Result  GetContentInfo(WebClient webClient, CacheContainer cacheContainer, Context context, String
            cdrtype, String strType,String ccType){
        List<NameValuePair> reqParam = new ArrayList<>();
        Map<String, String > header = new HashMap<>();
        Result result = new Result();
        header.put("Content-Type", "application/x-www-form-urlencoded");
        header.put("Referer", "http://zj.189.cn/zjpr/cdr/getCdrDetailInput.htm");
        List<Page> pages = new ArrayList<>();
        String url = "http://zj.189.cn/zjpr/cdr/getCdrDetail.htm";

        try {
            Calendar calendar = Calendar.getInstance();
            // 查询最近6个月通话详单
            for (int i = 1; i < (context.getRecordSize() + 1); i++) {//包含当前月份
                final String month = DateFormatUtils.format(calendar, "yyyyMM");
                String logFlag = String.format("==>2."+i+"["+context.getTaskId()+"]采集["+month+"]"+strType+".....");
                for(int j=1;j<100;j++){
                    reqParam.clear();
                    reqParam.add(new NameValuePair("cdrCondition.pagenum", String.valueOf(j)));
                    reqParam.add(new NameValuePair("cdrCondition.pagesize", "100"));
                    reqParam.add(new NameValuePair("cdrCondition.productnbr", context.getUserName()));
                    reqParam.add(new NameValuePair("cdrCondition.areaid", context.getParam1()));
                    reqParam.add(new NameValuePair("cdrCondition.cdrlevel", ""));
                    reqParam.add(new NameValuePair("cdrCondition.productid", context.getParam2()));
                    reqParam.add(new NameValuePair("cdrCondition.product_servtype", context.getParam3()));
                    reqParam.add(new NameValuePair("cdrCondition.recievenbr", context.getParam4()));
                    reqParam.add(new NameValuePair("cdrCondition.cdrmonth", month));
                    reqParam.add(new NameValuePair("cdrCondition.cdrtype", cdrtype));
                    reqParam.add(new NameValuePair("username", context.getIdName()));
                    reqParam.add(new NameValuePair("idcard", context.getIdCard()));
                    reqParam.add(new NameValuePair("cdrCondition.randpsw", context.getParam6()));

                    Page page = getPage(webClient, url, HttpMethod.POST, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, header,CharsetCode.GB2312);
                    if(page.getWebResponse().getContentAsString().contains("ErrorNo=61010")){
                        logger.info("==>2.[{}]采集[{}]{}结束,没有查询结果.", context.getTaskId(), month,strType);
                        break;
                    }else if(null != page){//添加缓存信息
                        logger.info("==>2.[{}]采集[{}]{}成功.", context.getTaskId(), month,strType);
                        pages.add(page);
                    }
                }
                calendar.add(Calendar.MONTH, -1);
            }
            cacheContainer.putPages(ccType, pages);
            result.setSuccess();
        } catch (Exception e) {
            logger.error("==>2.[{}]采集[{}]出现异常:[{}]", context.getTaskId(),strType, e);
            result.setResult(StatusCode.采集信息出错);
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
