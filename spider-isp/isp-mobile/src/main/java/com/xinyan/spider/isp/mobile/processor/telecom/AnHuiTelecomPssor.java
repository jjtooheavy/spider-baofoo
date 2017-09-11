package com.xinyan.spider.isp.mobile.processor.telecom;

import java.util.*;

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
import com.xinyan.spider.isp.common.utils.RegexUtils;
import com.xinyan.spider.isp.common.utils.StringUtils;
import com.xinyan.spider.isp.mobile.model.CarrierInfo;
import com.xinyan.spider.isp.mobile.parser.telecom.AnHuiTelecomParser;

/**
 * 安徽电信处理类
 * @description
 * @author yyj
 * @date 2017年4月21日 下午3:38:43
 * @version V1.0
 */
@Component
public class AnHuiTelecomPssor extends AbstractTelecomPssor{
    protected static Logger logger = LoggerFactory.getLogger(AnHuiTelecomPssor.class);

    @Autowired
    private AnHuiTelecomParser parser;

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

            String url = "http://ah.189.cn/service/account/init.action";
            getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, null);
            
            url = "http://ah.189.cn/sso/login?returnUrl=/service/account/init.action";
            getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, null);

            JavaScriptUtils.invoker("js/AnHuiTelecomRsa.js" ,"bodyRSA", "");
            String password = JavaScriptUtils.invoker("js/AnHuiTelecomRsa.js" ,"encryptedString", context.getPassword());
            context.setPassword(password);

//            String verifyCode = getVerifyCode(webClient, "http://ah.189.cn/sso/VImage.servlet?random=0." + new Date().getTime(), null);
            String verifyCode = getVerifyCode(webClient, "http://ah.189.cn/sso/VImage.servlet?random=" + new Random().nextDouble(), 1004);
            if (StringUtils.isNotEmpty(verifyCode)) {
                context.setUserInput(verifyCode);
                return doLoginByIMG(webClient, context);
//                getLogger().info("==>1.[{}]已经发送图片验证码...");
//                // 通知前端发送成功
//                result.setResult(StatusCode.请输入图片验证码);
//                context.setTaskSubStatus(Constants.VALIDATE_TYPE_CODE_IMG);// 需要设置回调子状态
//                sendValidataMsg(result, context, true, webClient, Constants.DEQUEUE_TIME_OUT, verifyCode);
//                return result;
            }else{
            	result.setResult(StatusCode.获取图片验证码错误);
            }
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
            
            List<NameValuePair> reqParam = new ArrayList<>();
            reqParam.clear();

            reqParam.add(new NameValuePair("ssoAuth", "0"));
            reqParam.add(new NameValuePair("returnUrl", "/service/account/init.action"));
            reqParam.add(new NameValuePair("sysId", "1003"));
            reqParam.add(new NameValuePair("loginType", "4"));
            reqParam.add(new NameValuePair("accountType", "9"));
            reqParam.add(new NameValuePair("latnId", "551"));
            reqParam.add(new NameValuePair("loginName", context.getUserName()));
            reqParam.add(new NameValuePair("passType", "0"));
            reqParam.add(new NameValuePair("passWord", context.getPassword()));
            reqParam.add(new NameValuePair("validCode", context.getUserInput()));
            reqParam.add(new NameValuePair("remPwd", "0"));
            reqParam.add(new NameValuePair("csrftoken", null));

            String url = "http://ah.189.cn/sso/LoginServlet";
            Page page = getPage(webClient, url, HttpMethod.POST, reqParam,Constants.MAX_SENDMSG_TIIMES,null, null);
            if ("timeout".equals(page)){//网站加载超时
            	result.setResult(StatusCode.网站加载超时);
                return result;
            }else if(StringUtils.contains(page.getWebResponse().getContentAsString(),"验证码错误")){//验证码错误
                result.setResult(StatusCode.图片验证码错误);
                return result;
            }

            String loginValue = RegexUtils.matchValue(".*?name=\"SSORequestXML\" value=\"(.*?)\"",page.getWebResponse().getContentAsString());
            if(StringUtils.isBlank(loginValue)){
                result.setResult(StatusCode.用户名或密码错误);
                return result;
            }

            String body = "SSORequestXML=" + loginValue;
            url = "http://uam.ah.ct10000.com/ffcs-uam/login";

            page = getPage(webClient, url, HttpMethod.POST, null,Constants.MAX_SENDMSG_TIIMES,null, null, body);
            String pageInfo = page.getWebResponse().getContentAsString();
            if(StringUtils.contains(pageInfo,"使用人数较多")){//使用人数较多
                result.setResult(StatusCode.使用人数较多);
                return result;
            }else if(StringUtils.contains(pageInfo,"用户不存在")){//用户不存在
                result.setResult(StatusCode.用户不存在);
                return result;
            }else if(StringUtils.contains(pageInfo,"密码和账户名不匹配")){//密码和账户名不匹配
                result.setResult(StatusCode.用户名或密码错误);
                return result;
            }else if(StringUtils.contains(pageInfo,"手机（" + context.getUserName() + "）")){
                //登陆成功
                result.setResult(StatusCode.登陆成功);
            }else{
                result.setResult(StatusCode.登陆失败稍后再试);
                return result;
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
            // 1.开始登陆
            logger.info("==>1.[{}]用户短信认证开始.....", context.getTaskId());
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
            getLogger().error("==>[{}]浙江数据解析出现异常:[{}]", context.getTaskId(), ex);
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
        List<NameValuePair> reqParam = new ArrayList<>();
        String url = "";
        try {
            logger.info("==>1.1[{}]采集个人信息开始.....", context.getTaskId());
            //获取真实姓名、入网时间、地址、邮箱
            url = "http://ah.189.cn/service/manage/showCustInfo.action";
            Page page = getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, null);
            if ("timeout".equals(page)){//网站加载超时
                result.setResult(StatusCode.网站加载超时);
                return result;
            }
            cacheContainer.putPage(ProcessorCode.BASIC_INFO.getCode(), page);
            logger.info("==>1.1[{}]采集个人信息成功.", context.getTaskId());

            logger.info("==>1.2[{}]采集个人余额开始.....", context.getTaskId());
            //余额
            url = "http://ah.189.cn/service/account/usedBalance.action";
            reqParam.clear();
            reqParam.add(new NameValuePair("serviceNum", context.getUserName()));
            page = getPage(webClient, url, HttpMethod.POST, reqParam,Constants.MAX_SENDMSG_TIIMES,null, null);
            cacheContainer.putPage(ProcessorCode.AMOUNT.getCode(), page);
            logger.info("==>1.2[{}]采集个人余额成功.", context.getTaskId());
            
            logger.info("==>1.3[{}]采集地址信息开始.....", context.getTaskId());
            //地址
            url = "http://ah.189.cn/service/myAddress/init.action";
            page = getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, null);
            cacheContainer.putPage(ProcessorCode.OTHER_INFO.getCode(), page);
            logger.info("==>1.3[{}]采集地址信息成功.", context.getTaskId());
            
            logger.info("==>1.4[{}]采集套餐信息开始.....", context.getTaskId());
            //我的套餐
            url = "http://ah.189.cn/service/manage/getHandelProdList.action";
            reqParam.clear();
            reqParam.add(new NameValuePair("serviceNbr", ""));
            page = getPage(webClient, url, HttpMethod.POST, reqParam, Constants.MAX_SENDMSG_TIIMES,null,null);
            cacheContainer.putPage(ProcessorCode.POINTS_VALUE.getCode(), page);
            logger.info("==>1.4[{}]采集套餐信息成功.", context.getTaskId());

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
        String url = "";

        try {
            Calendar calendar = Calendar.getInstance();
            // 查询最近6个月通话详单
            for (int i = 1; i < (context.getRecordSize() + 1); i++) {//包含当前月份
                final String month = DateFormatUtils.format(calendar, "yyyy-MM");
                String logFlag = String.format("==>2."+i+"["+context.getTaskId()+"]采集["+month+"]通话详单.....");
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                final String transDateBegin = DateFormatUtils.format(calendar, "yyyy-MM-dd");
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                String transDateEnd="";
                if (i == 0){
                    transDateEnd = DateUtils.getCurrentDate();
                }else{
                    transDateEnd = DateFormatUtils.format(calendar, "yyyy-MM-dd");
                }

                reqParam.clear();
                reqParam.add(new NameValuePair("currentPage", "1"));
                reqParam.add(new NameValuePair("pageSize", "10000"));
                reqParam.add(new NameValuePair("effDate", transDateBegin));
                reqParam.add(new NameValuePair("expDate", transDateEnd));
                reqParam.add(new NameValuePair("serviceNbr", context.getUserName()));
                reqParam.add(new NameValuePair("operListID", "2"));
                reqParam.add(new NameValuePair("isPrepay", "1"));
                reqParam.add(new NameValuePair("pOffrType", "481"));
                final String param = "currentPage=1&pageSize=10000&effDate="+transDateBegin+"&expDate="+transDateEnd+"&serviceNbr="+context.getUserName()+"&operListID=2&isPrepay=1&pOffrType=481";
                String _v = JavaScriptUtils.invoker("js/AnHuiTelecomRsa.js","encryptedString",param);
                reqParam.add(new NameValuePair("_v", _v));
                url = "http://ah.189.cn/service/bill/feeDetailrecordList.action";

                Page page = getPage(webClient, url, HttpMethod.POST, reqParam,Constants.MAX_SENDMSG_TIIMES,null, null);
                String pageInfo = page.getWebResponse().getContentAsString();
                if(StringUtils.contains(pageInfo,"cxts2.png")){//没有详单信息
                    logger.info("==>2.[{}]采集[{}]完成,没有详单信息.", context.getTaskId(), month);
                }else{
                    final String fileName = RegexUtils.matchValue("<input type=\"hidden\" name=\"fileName\" id=\"fileName\" value=\"(.*?)\" />",pageInfo);
                    reqParam.clear();
                    reqParam.add(new NameValuePair("effDate", transDateBegin));
                    reqParam.add(new NameValuePair("expDate", transDateEnd));
                    reqParam.add(new NameValuePair("serviceNbr", context.getUserName()));
                    reqParam.add(new NameValuePair("operListID", "2"));
                    reqParam.add(new NameValuePair("mobilecallType", "2"));
                    reqParam.add(new NameValuePair("pOffrType", "481"));
                    reqParam.add(new NameValuePair("fileName", fileName));
                    url = "http://ah.189.cn/service/bill/exportExcel.action";
                    page = getPage(webClient, url, HttpMethod.POST, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, null);
                    if(null != page){//添加缓存信息
                        pages.add(page);
                        logger.info("==>2.[{}]采集[{}]通话详单成功.", context.getTaskId(), month);
                    }else{
                        logger.info("==>2.[{}]采集[{}]通话详单结束,没有查询结果.", context.getTaskId(), month);
                    }
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

        String url = "";
        try {
            Calendar calendar = Calendar.getInstance();
            // 查询最近6个月短信记录
            for (int i = 1; i < (context.getRecordSize() + 1); i++) {//包含当前月份
                final String month = DateFormatUtils.format(calendar, "yyyy-MM");
                String logFlag = String.format("==>3."+i+"["+context.getTaskId()+"]采集["+month+"]短信记录.....");

                calendar.set(Calendar.DAY_OF_MONTH, 1);
                final String transDateBegin = DateFormatUtils.format(calendar, "yyyy-MM-dd");
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                String transDateEnd="";
                if (i == 0){
                    transDateEnd = DateUtils.getCurrentDate();
                }else{
                    transDateEnd = DateFormatUtils.format(calendar, "yyyy-MM-dd");
                }

                reqParam.clear();
                reqParam.add(new NameValuePair("currentPage", "1"));
                reqParam.add(new NameValuePair("pageSize", "10000"));
                reqParam.add(new NameValuePair("effDate", transDateBegin));
                reqParam.add(new NameValuePair("expDate", transDateEnd));
                reqParam.add(new NameValuePair("serviceNbr", context.getUserName()));
                reqParam.add(new NameValuePair("operListID", "4"));
                reqParam.add(new NameValuePair("isPrepay", "1"));
                reqParam.add(new NameValuePair("pOffrType", "481"));
                final String param = "currentPage=1&pageSize=10000&effDate="+transDateBegin+"&expDate="+transDateEnd+"&serviceNbr="+context.getUserName()+"&operListID=4&isPrepay=1&pOffrType=481";
                String _v = JavaScriptUtils.invoker("js/AnHuiTelecomRsa.js","encryptedString",param);
                reqParam.add(new NameValuePair("_v", _v));
                url = "http://ah.189.cn/service/bill/feeDetailrecordList.action";

                Page page = getPage(webClient, url, HttpMethod.POST, reqParam, Constants.MAX_SENDMSG_TIIMES,null,null);
                String pageInfo = page.getWebResponse().getContentAsString();
                if(StringUtils.contains(pageInfo,"cxts2.png")){//没有详单信息
                    logger.info("==>3.[{}]采集[{}]完成,没有短信记录.", context.getTaskId(), month);
                }else{
                    final String fileName = RegexUtils.matchValue("<input type=\"hidden\" name=\"fileName\" id=\"fileName\" value=\"(.*?)\" />",pageInfo);

                    reqParam.clear();
                    reqParam.add(new NameValuePair("effDate", transDateBegin));
                    reqParam.add(new NameValuePair("expDate", transDateEnd));
                    reqParam.add(new NameValuePair("serviceNbr", context.getUserName()));
                    reqParam.add(new NameValuePair("operListID", "4"));
                    reqParam.add(new NameValuePair("mobilecallType", "2"));
                    reqParam.add(new NameValuePair("pOffrType", "481"));
                    reqParam.add(new NameValuePair("fileName", fileName));
                    url = "http://ah.189.cn/service/bill/exportExcel.action";
                    page = getPage(webClient, url, HttpMethod.POST, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, null);
                    if(null != page){//添加缓存信息
                        pages.add(page);
                        logger.info("==>3.[{}]采集[{}]短信记录成功.", context.getTaskId(), month);
                    }else{
                        logger.info("==>3.[{}]采集[{}]短信记录结束,没有查询结果.", context.getTaskId(), month);
                    }
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
        List<Page> pages = new ArrayList<>();
        String url = "";
        try {
            String desUuid = JavaScriptUtils.invoker("js/AnHuiTelecomRsa.js","encryptedString", "prodServiceNbr="+context.getUserName());
            if(StringUtils.isBlank(desUuid)){
                result.setResult(StatusCode.密码加密验证失败);
                return result;
            }
            cacheContainer.putString("desUuid",desUuid);

            Calendar calendar = Calendar.getInstance();
            // 查询最近6个月上网记录
            for (int i = 1; i < (context.getRecordSize() + 1); i++) {//包含当前月份
                final String month = DateFormatUtils.format(calendar, "yyyy-MM");
                String logFlag = String.format("==>4."+i+"["+context.getTaskId()+"采集["+month+"]上网记录,第[{}]次尝试请求.....");

                calendar.set(Calendar.DAY_OF_MONTH, 1);
                final String transDateBegin = DateFormatUtils.format(calendar, "yyyy-MM-dd");
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                String transDateEnd="";
                if (i == 0){
                    transDateEnd = DateUtils.getCurrentDate();
                }else{
                    transDateEnd = DateFormatUtils.format(calendar, "yyyy-MM-dd");
                }

                reqParam.clear();
                reqParam.add(new NameValuePair("currentPage", "1"));
                reqParam.add(new NameValuePair("pageSize", "10"));
                reqParam.add(new NameValuePair("effDate", transDateBegin));
                reqParam.add(new NameValuePair("expDate", transDateEnd));
                reqParam.add(new NameValuePair("uuid", desUuid));
                reqParam.add(new NameValuePair("operListId", "6"));
                url = "http://ah.189.cn/service/bill/queryDetail.action";//上网信息采集

                Page page = getPage(webClient, url, HttpMethod.POST, reqParam, Constants.MAX_SENDMSG_TIIMES,null,null);
                String pageInfo = page.getWebResponse().getContentAsString();
                if(StringUtils.contains(pageInfo,"没有符合条件的记录")){//没有详单信息
                    logger.info("==>4.[{}]采集[{}]完成,没有上网记录.", context.getTaskId(), month);
                }else{
                    reqParam.clear();
                    reqParam.add(new NameValuePair("begin_time", transDateBegin));
                    reqParam.add(new NameValuePair("end_time", transDateEnd));
                    reqParam.add(new NameValuePair("serviceNbr", context.getUserName()));
                    reqParam.add(new NameValuePair("operListId", "6"));

                    url = "http://ah.189.cn/service/bill/interExportExcel.action";
                    page = getPage(webClient, url, HttpMethod.POST, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, null);
                    if(null != page){//添加缓存信息
                        pages.add(page);
                        logger.info("==>4.[{}]采集[{}]上网记录成功.", context.getTaskId(), month);
                    }else{
                        logger.info("==>4.[{}]采集[{}]上网记录结束,没有查询结果.", context.getTaskId(), month);
                    }
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

            String url = "http://ah.189.cn/service/bill/initQueryBill.action?rnd=0.978628606768325";
            Page billPage = getPage(webClient, url, HttpMethod.GET, null, Constants.MAX_SENDMSG_TIIMES,null,null);
            String pageInfo = billPage.getWebResponse().getContentAsString();
            if ("timeout".equals(billPage)){//网站加载超时
                result.setResult(StatusCode.网站加载超时);
                return result;
            }
            String currentAccountID = RegexUtils.matchValue("currentAccountID=(.*?)&", pageInfo);
            String currentLatnId = RegexUtils.matchValue("currentLatnId=(.*?)&", pageInfo);
            String sixMonthStr = RegexUtils.matchValue("sixMonthStr=(.*?)&", pageInfo);
            String sixMonthFee = RegexUtils.matchValue("sixMonthFee=(.*?)'", pageInfo);

            List<NameValuePair> reqParam = new ArrayList<>();
            List<Page> pages = new ArrayList<>();

            // 查询最近6个月账单
            for (int i = 1; i < (context.getRecordSize() + 1); i++) {//前6个月(不算当月)
                calendar.add(Calendar.MONTH, -1);
                final String month = DateFormatUtils.format(calendar, "yyyyMM");
                String logFlag = String.format("==>5."+i+"["+context.getTaskId()+"采集["+month+"]账单信息,第[{}]次尝试请求.....");
                reqParam.clear();
                reqParam.add(new NameValuePair("uuid", cacheContainer.getString("desUuid")));
                reqParam.add(new NameValuePair("currentAccountID", currentAccountID));
                reqParam.add(new NameValuePair("currentLatnId", currentLatnId));
                reqParam.add(new NameValuePair("currentMonth", month));
                reqParam.add(new NameValuePair("sixMonthStr", sixMonthStr));
                reqParam.add(new NameValuePair("sixMonthFee", sixMonthFee));
                url = "http://ah.189.cn/service/bill/queryBillDetail.action";
                Page page = getPage(webClient, url, HttpMethod.POST, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, null);
                if(null != page){//添加缓存信息
                    pages.add(page);
                    logger.info("==>5.[{}]采集[{}]账单信息成功.", context.getTaskId(), month);
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
            String url = "http://ah.189.cn/service/bill/initRatableQry.action";
            // 查询套餐使用情况
            String logFlag = String.format("==>6.["+context.getTaskId()+"]套餐使用情况,第[{}]次尝试请求.....");
			Page page = getPage(webClient, url, HttpMethod.POST, null, Constants.MAX_RETRY_TIIMES, logFlag, null);
            if(null != page){
                logger.info("==>6.[{}]采集套餐使用情况成功.", context.getTaskId());
                cacheContainer.putPage(ProcessorCode.PACKAGE_ITEM.getCode(), page);
            }else{
                logger.info("==>6.[{}]采集套餐使用情况结束,没有查询结果.", context.getTaskId());
            }
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
            Calendar calendar = Calendar.getInstance();
            List<NameValuePair> reqParam = new ArrayList<>();
            String url = "http://ah.189.cn/service/pay/payrecordlist.action";
            
            final String transDateEnd = DateFormatUtils.format(calendar, "yyyy-MM-dd");
            calendar.add(Calendar.MONTH, -6);
            final String transDateBegin = DateFormatUtils.format(calendar, "yyyy-MM-dd");
            
            reqParam.clear();
            reqParam.add(new NameValuePair("currentPage", "1"));
            reqParam.add(new NameValuePair("pageSize", "10000"));
            reqParam.add(new NameValuePair("recordReq.effDate", transDateBegin));
            reqParam.add(new NameValuePair("recordReq.expDate", transDateEnd));
            reqParam.add(new NameValuePair("recordReq.serviceNbr", context.getUserName()));
            reqParam.add(new NameValuePair("recordReq.objType", "0"));
            String logFlag = String.format("==>8.["+context.getTaskId()+"]采集充值记录["+transDateBegin+"]-["+transDateEnd+"],第[{}]次尝试请求.....");
            Page page = getPage(webClient, url, HttpMethod.POST, reqParam, Constants.MAX_RETRY_TIIMES, logFlag, null);
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

    @Override
    protected Result loginout(WebClient webClient){
        Result result = new Result();
        logger.info("==>9.电信统一退出开始.....");
        String url = "http://www.189.cn/login/logout.do";
        getPage(webClient, url, HttpMethod.GET, null, null);

        result.setSuccess();
        return result;
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }
}
