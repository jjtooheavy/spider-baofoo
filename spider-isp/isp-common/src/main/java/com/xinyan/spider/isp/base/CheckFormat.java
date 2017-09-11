package com.xinyan.spider.isp.base;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xinyan.spider.isp.common.utils.StringUtils;
import com.xinyan.spider.isp.common.utils.RegexChk;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description:检查输出数据是否符合标准
 * @author: york
 * @date: 2016-09-26 18:29
 * @version: v1.0
 */
public class CheckFormat {

    private static Map<String, String> map = new HashMap<String,String>();


    public static void inspectJson(String json) throws Exception {

        JSONObject info = JSONObject.parseObject(json);
        JSONArray jsonArray = null;

        String number = "^\\d+$";
        String standardNumber = "100";

        String money = "^\\d+\\.\\d{2}$";
        String standardMoney = "100.00";

        String month = "^\\d{4}-\\d{2}$";
        String standardMonth = "2016-09";

        String day = "^\\d{4}-\\d{2}-\\d{2}$";
        String standardDay = "2016-09-01";

        String time = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$";
        String standardTime = "2016-09-01 00:00:00";

        //检查基本信息
        JSONObject baseInfo = info.getJSONObject("baseInfo");
        {
            //检查入网时间
            check(baseInfo, "registerDate", day, standardDay);
            //检查星级
            String[] arr = {"1星", "2星", "3星", "4星", "5星"};
            check(baseInfo, "vipLevelstr", arr);
            //检查可用积分
            check(baseInfo, "pointsValuestr", number, standardNumber);
            //检查可用余额
            check(baseInfo, "amount", money, standardMoney);
        }

        //检查通话记录
        jsonArray = info.getJSONArray("callRecordInfo");
        for (Object call : jsonArray) {
            JSONObject callInfo = (JSONObject)call;

            //检查通话时间
            check(callInfo, "callDateTime", time, standardTime);
            //检查通话时长
            check(callInfo, "callTimeLength", number, standardNumber);
            //检查通话类型
            String[] arr = {"主叫", "被叫"};
            check(callInfo, "callType", arr);
        }

        //检查短信信息
        jsonArray = info.getJSONArray("smsInfo");
        for (Object sms : jsonArray) {
            JSONObject smsInfo = (JSONObject)sms;

            //检查发送时间
            check(smsInfo, "sendSmsTime", time, standardTime);
            //检查发送类型
            String[] arr = {"接收", "发送"};
            check(smsInfo, "sendType", arr);
        }

        //检查上网信息
        jsonArray = info.getJSONArray("netInfo");
        for (Object net : jsonArray) {
            JSONObject netInfo = (JSONObject)net;

            //检查上网时间
            check(netInfo, "netTime", time, standardTime);
            //检查上网时长
            check(netInfo, "onlineTime", number, standardNumber);
            //检查上网类型
            String[] arr = {"2G", "3G", "4G"};
            check(netInfo, "netType", arr);
        }

        //检查账单信息
        jsonArray = info.getJSONArray("bill");
        for (Object bill : jsonArray) {
            JSONObject billInfo = (JSONObject)bill;

            //检查账单月份
            check(billInfo, "startTime", month, standardMonth);
            //检查套餐消费
            check(billInfo, "comboCost", money, standardMoney);
            //检查总金额
            check(billInfo, "sumCost", money, standardMoney);
            //检查实际费用
            check(billInfo, "realCost", money, standardMoney);
        }

        //检查业务信息
        jsonArray = info.getJSONArray("businessInfo");
        for (Object busi : jsonArray) {
            JSONObject busiInfo = (JSONObject)busi;

            //检查业务开始时间
            check(busiInfo, "beginTime", time, standardTime);
            //检查业务消费
            check(busiInfo, "cost", money, standardMoney);
        }

        System.out.println("格式检查结果：\n-----------------------------------");
        if(map.size() == 0){System.out.println("格式检查通过！！！");}
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String log = "key=%s, value=[%s]";
            log = String.format(log, entry.getKey(), entry.getValue());
            System.out.println(log);
        }
        System.out.println("-----------------------------------");
    }

    /**
     * 匹配函数
     * @param info 匹配目标
     * @param key 匹配源
     * @param pattern 匹配正则
     * @param standard 标准格式
     * @return
     */
    private static void check(JSONObject info, String key, String pattern, String standard){

        if(info == null || StringUtils.isBlank(key)  || StringUtils.isBlank(pattern)  || StringUtils.isBlank(standard) ){
            return;
        }
        String value = info.getString(key);
        if(StringUtils.isNotBlank(value)){
            if(!RegexChk.startCheck(pattern, value)) {
                String strError = "%s[标准格式：%s]";
                strError = String.format(strError, key, standard);
                map.put(strError, value);
            }
        }

        return;
    }

    /**
     * 匹配函数
     * @param info 匹配目标
     * @param key 匹配源
     * @param arr 匹配数组
     * @return
     */
    private static void check(JSONObject info, String key, String[] arr){

        if(info == null || StringUtils.isBlank(key)  || arr.length == 0){
            return;
        }
        String value = info.getString(key);
        if(StringUtils.isNotBlank(value)){

            for (int i = 0; i< arr.length; i++) {
                String s = arr[i];
                if(value.equals(s)){
                    break;
                }
                if(i == arr.length - 1){
                    String strError = "%s[标准格式：%s]";
                    strError = String.format(strError, key, StringUtils.implodeStr(arr, "|"));
                    map.put(strError, value);
                }
            }
        }
    }

}
