package com.xinyan.spider.isp.mobile.processor.telecom;

import java.util.*;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.xinyan.spider.isp.base.CacheContainer;
import com.xinyan.spider.isp.base.Context;
import com.xinyan.spider.isp.base.Result;
import com.xinyan.spider.isp.base.StatusCode;
import com.xinyan.spider.isp.common.Constants;
import com.xinyan.spider.isp.common.utils.IdentityUtils;
import com.xinyan.spider.isp.common.utils.JavaScriptUtils;
import com.xinyan.spider.isp.common.utils.RegexUtils;
import com.xinyan.spider.isp.common.utils.StringUtils;
import com.xinyan.spider.isp.mobile.model.CarrierInfo;
import com.xinyan.spider.isp.mobile.processor.AbstractProcessor;

/**
 * 中国电信公共处理类
 * Created by yyj on 17-4-21.
 */
public class AbstractTelecomPssor extends AbstractProcessor{

    /**
     * 中国电信公共登陆[正常]
     * @param webClient
     * @param context
     * @return
     */
    protected  String telecomLogin(WebClient webClient, Context context, boolean cookieFlag){
        final String logFlag = "==>["+context.getTaskId()+"]电信["+context.getArea()+"]统一登陆入口:";

        try{
            getLogger().info(logFlag + "开始正常认证...");
            String url = "http://www.189.cn/dqmh/my189/initMy189home.do";
            Map<String, String > header = new HashMap<>();
            header.put("referer","http://www.189.cn/login/index.html?ifindex=index");

            Page resultPage = getPage(webClient, url, HttpMethod.GET, null,Constants.MAX_SENDMSG_TIIMES,null, header);
            String result = resultPage.getWebResponse().getContentAsString();
            if ("timeout".equals(result)){//网站加载超时
                return StatusCode.网站加载超时.getCode();
            }

            url = "http://login.189.cn/login/ajax";
            List<NameValuePair> reqParam = new ArrayList<>();

            reqParam.add(new NameValuePair("m", "checkphone"));
            reqParam.add(new NameValuePair("phone", context.getUserName()));
            resultPage = getPage(webClient, url, HttpMethod.POST, reqParam,Constants.MAX_SENDMSG_TIIMES,null, null);
            result = resultPage.getWebResponse().getContentAsString();
            if ("timeout".equals(result)){//网站加载超时
                return StatusCode.网站加载超时.getCode();
            }
            String provinceId = RegexUtils.matchValue("\"ProvinceID\":\"(.*?)\"", result);
            context.setParam1(provinceId);
            String password = JavaScriptUtils.invoker("js/ChinaTelecomDes.js" ,"valAesEncryptSet", context.getPassword());
            context.setPassword(password);

            url = "http://login.189.cn/login";
            reqParam.clear();
            reqParam.add(new NameValuePair("Account", context.getUserName()));
            reqParam.add(new NameValuePair("UType", "201"));
            reqParam.add(new NameValuePair("ProvinceID", context.getParam1()));
            reqParam.add(new NameValuePair("AreaCode", ""));
            reqParam.add(new NameValuePair("CityNo", ""));
            reqParam.add(new NameValuePair("RandomFlag", "0"));
            reqParam.add(new NameValuePair("Password", context.getPassword()));
            reqParam.add(new NameValuePair("Captcha", ""));

            resultPage = getPage(webClient, url, HttpMethod.POST, reqParam,Constants.MAX_SENDMSG_TIIMES,null, null);

            result = resultPage.getWebResponse().getContentAsString();
            if ("timeout".equals(result)){//网站加载超时
                return StatusCode.网站加载超时.getCode();
            }

            if(StringUtils.contains(result, "请输入验证码")){
//                String verifyCode = getVerifyCode(webClient, "http://login.189.cn/captcha?311772c0199a4cc181a979142982a963&source=login&width=100&height=37&0." + new Date().getTime(), null);
                String verifyCode = getVerifyCode(webClient, "http://login.189.cn/captcha?"+ IdentityUtils.getUUID()+"&source=login&width=100&height=37&" + new Random().nextDouble(), 1004);
                if (StringUtils.isNotEmpty(verifyCode)) {
//                    Result r = new Result();
//                    getLogger().info(logFlag + "已经发送图片验证码...");
//                    // 通知前端发送成功
//                    r.setResult(StatusCode.请输入图片验证码);
//                    context.setTaskSubStatus(Constants.VALIDATE_TYPE_CODE_IMG);// 需要设置回调子状态
//                    sendValidataMsg(r, context, cookieFlag, webClient, Constants.DEQUEUE_TIME_OUT, verifyCode);
//                    return r.getCode();
                    context.setUserInput(verifyCode);
                    return doLoginBySMS(webClient, context).getCode();
                }else{
                    return StatusCode.获取图片验证码错误.getCode();
                }
            }else{
                return getResult(result, context.getUserName(), context.getTaskId());//获取登陆结果
            }
        }catch (Exception e){
            return StatusCode.登陆出错.getCode();
        }
    }

    /**
     * 中国电信公共登陆[图片]
     * @param webClient
     * @param context
     * @return
     */
    protected String telecomLoginByIMG(WebClient webClient, Context context){
        final String logFlag = "==>["+context.getTaskId()+"]电信["+context.getArea()+"]统一登陆入口:";
        try{
            getLogger().info(logFlag + "开始图片认证...");
            if(StringUtils.isNotEmpty(context.getUserInput())){
                List<NameValuePair> reqParam = new ArrayList<>();
                String url = "http://login.189.cn/login";

                reqParam.add(new NameValuePair("Account", context.getUserName()));
                reqParam.add(new NameValuePair("UType", "201"));
                reqParam.add(new NameValuePair("ProvinceID", context.getParam1()));
                reqParam.add(new NameValuePair("AreaCode", ""));
                reqParam.add(new NameValuePair("CityNo", ""));
                reqParam.add(new NameValuePair("RandomFlag", "0"));
                reqParam.add(new NameValuePair("Password", context.getPassword()));
                reqParam.add(new NameValuePair("Captcha", context.getUserInput()));

                Page resultPage = getPage(webClient, url, HttpMethod.POST, reqParam,Constants.MAX_SENDMSG_TIIMES,null, null);

                String result = resultPage.getWebResponse().getContentAsString();
                if ("timeout".equals(result)){//网站加载超时
                    return StatusCode.网站加载超时.getCode();//需要重新尝试
                }else{
                    return getResult(result, context.getUserName(), context.getTaskId());//获取登陆结果
                }
            }else{
                return StatusCode.请输入图片验证码.getCode();
            }
        }catch (Exception e){
            return StatusCode.登陆出错.getCode();
        }
    }

    /**
     * 中国电信公共爬取
     * @param webClient
     * @param context
     * @param carrierInfo
     * @param cacheContainer
     * @return
     */
    protected Result telecomCrawler(WebClient webClient, Context context, CarrierInfo carrierInfo, CacheContainer cacheContainer){
        Result result = new Result();
        try {
            // 1.采集基础信息
            getLogger().info("==>1.[{}]采集基本信息开始.....", context.getTaskId());
            result = processBaseInfo(webClient, context, carrierInfo, cacheContainer);
            getLogger().info("==>1.[{}]采集基本信息结束{}.", context.getTaskId(), result);
            if (!result.isSuccess()) { // 采集基础信息失败
                return result;
            }

            // 2.采集通话详单
            getLogger().info("==>2.[{}]采集通话详单开始.....", context.getTaskId());
            result = processCallRecordInfo(webClient, context, carrierInfo, cacheContainer);
            getLogger().info("==>2.[{}]采集通话详单结束{}.", context.getTaskId(), result);
            if (!result.isSuccess()) { // 采集通话详单失败
                return result;
            }

            // 3.采集短信记录
            getLogger().info("==>3.[{}]采集短信记录开始.....", context.getTaskId());
            result = processSmsInfo(webClient, context, carrierInfo, cacheContainer);
            getLogger().info("==>3.[{}]采集短信记录结束{}.", context.getTaskId(), result);
            if (!result.isSuccess()) { // 采集短信记录失败
                return result;
            }

            // 4.采集上网记录
            getLogger().info("==4.[{}]采集上网记录开始.....", context.getTaskId());
            result = processNetInfo(webClient, context, carrierInfo, cacheContainer);
            getLogger().info("==>4.[{}]采集上网记录结束{}.", context.getTaskId(), result);
            if (!result.isSuccess()) { // 采集上网记录失败
                return result;
            }

            // 5.采集账单信息
            getLogger().info("==>5.[{}]采集账单信息开始.....", context.getTaskId());
            result = processBillInfo(webClient, context, carrierInfo, cacheContainer);
            getLogger().info("==>5.[{}]采集账单信息结束{}.", context.getTaskId(), result);
            if (!result.isSuccess()) { // 采集账单信息失败
                return result;
            }

            // 6.采集套餐信息
            getLogger().info("==>6.[{}]采集套餐信息开始.....", context.getTaskId());
            result = processPackageItemInfo(webClient, context, carrierInfo, cacheContainer);
            getLogger().info("==>6.[{}]采集套餐信息结束{}.", context.getTaskId(), result);
            if (!result.isSuccess()) {
                return result;
            }

            // 7.采集亲情号码
            getLogger().info("==>7.[{}]采集亲情号码开始.....", context.getTaskId());
            result = processUserFamilyMember(webClient, context, carrierInfo, cacheContainer);
            getLogger().info("==>7.[{}]采集亲情号码结束{}.", context.getTaskId(), result);
            if (!result.isSuccess()) {
                return result;
            }

            // 8.采集充值记录
            getLogger().info("==>8.[{}]采集充值记录开始.....", context.getTaskId());
            result = processUserRechargeItemInfo(webClient, context, carrierInfo, cacheContainer);
            getLogger().info("==>8.[{}]采集充值记录结束{}.", context.getTaskId(), result);
            if (!result.isSuccess()) {
                return result;
            }

            result.setResult(StatusCode.爬取成功);
        } catch (Exception ex) {
            getLogger().error("==>[{}]数据抓取出现异常:[{}]", context.getTaskId(), ex);
            result.setFail();
            return result;
        } finally {
            sendUpdateLog(result, context);
            getLogger().info("==>9.[{}]退出登陆开始.....", context.getTaskId());
            loginout(webClient);
            getLogger().info("==>9.[{}]退出登陆结束.", context.getTaskId());
            close(webClient);
        }
        return result;
    }

    /**
     * 获取登陆结果
     * @param result
     * @param userName
     * @param taskId
     * @return
     */
    protected String getResult(String result, String userName, String taskId){
    	result = result.replaceAll("\\s", " ").replaceAll("\"", "'");
        if(StringUtils.contains(result, "验证码无效或过期")){
            return StatusCode.图片验证码错误.getCode();
        }else if(StringUtils.contains(result, "用户密码过于简单")){
            return StatusCode.用户密码过于简单.getCode();
        }else if(StringUtils.contains(result, "用户密码错误")){
            return StatusCode.用户名或密码错误.getCode();
        }else if(StringUtils.contains(result, "登录失败过多")){
            return StatusCode.登录密码出错已达上限.getCode();
        }else if(StringUtils.contains(result, "系统繁忙")){
            return StatusCode.系统忙.getCode();
        }else if(StringUtils.contains(result, "用户不存在")){
            return StatusCode.用户不存在.getCode();
        }

        String name = RegexUtils.matchValue("<p class='usrrin_name' id='xiugai'>\\s*<span style=''>(.*?)</span>",result);
        String phone = RegexUtils.matchValue("(\\d{3} \\d{4} \\d{4})", result);

    	getLogger().info("==>[{}]正在获取登陆 name=[{}], phone=[{}]", taskId, name, phone);
        if(StringUtils.isNotEmpty(name)){
            //登陆成功
            return StatusCode.SUCCESS.getCode();
        }else if(StringUtils.contains(result, userName)){
            //登陆成功
            return StatusCode.SUCCESS.getCode();
        }else if(StringUtils.isNotEmpty(phone)){
            if(phone.replace(" ", "").equals(userName)){
                //登陆成功
                return StatusCode.SUCCESS.getCode();
            }else{
                //登录手机不匹配
                return StatusCode.登录手机不匹配.getCode();//不需要重新尝试
            }
        }else if(StringUtils.contains(result,"toStUrl=http://js.189.cn/service/order")
                || StringUtils.contains(result,"本次登录号码：" + userName)
                || StringUtils.contains(result,"ecs新认证平台登陆")
                || StringUtils.contains(result,"您现在是中国电信")
                || StringUtils.contains(result,"http://www.189.cn/dqmh/my189/initMy189home.do")
                || StringUtils.contains(result,"window.top.location.href='http://www.189.cn/hb")){
            //登陆成功
            return StatusCode.SUCCESS.getCode();
        }else if(StringUtils.contains(result, "点击F5进行刷新")){
        	return StatusCode.系统忙.getCode();//系统忙，请稍后再试
        }else{
            return StatusCode.登陆失败稍后再试.getCode();//未检测到错误
        }
    }
}