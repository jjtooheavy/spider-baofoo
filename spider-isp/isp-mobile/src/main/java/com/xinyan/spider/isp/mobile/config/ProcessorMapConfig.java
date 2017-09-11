package com.xinyan.spider.isp.mobile.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by johnny on 2017/6/9.
 */
@Configuration
public class ProcessorMapConfig {
    @Bean
    public Map<String, String> processorMap() {
        Map<String, String> map = new HashMap<>();
        map.put("dx_010", "beiJingTelecomPssor");
        map.put("dx_022", "tianJinTelecomPssor");
        map.put("dx_021", "shangHaiTelecomPssor");
        map.put("dx_025", "jiangSuTelecomPssor");
        map.put("dx_571", "zheJiangTelecomPssor");
        map.put("dx_551", "anHuiTelecomPssor");
        map.put("dx_027", "huBeiTelecomPssor");
        map.put("dx_731", "huNanTelecomPssor");
        map.put("dx_371", "heNanTelecomPssor");
        map.put("dx_024", "liaoNingTelecomPssor");
        map.put("dx_931", "ganSuTelecomPssor");
        map.put("dx_451", "heiLongJiangTelecomPssor");
        map.put("dx_591", "fujianTelecomPssor");
        map.put("dx_531", "shanDongTelecomPssor");
        map.put("dx_851", "guiZhouTelecomPssor");
        map.put("yd_010", "beiJingCmccPssor");
        map.put("yd_021", "shangHaiCmccPssor");
        map.put("yd_591", "fuJianCmccPssor");
        map.put("yd_025", "jiangSuCmccPssor");
        map.put("yd_371", "heNanCmccPssor");
        map.put("yd_027", "huBeiCmccPssor");
        map.put("yd_531", "shanDongCmccPssor");
        map.put("yd_571", "zheJiangCmccPssor");
        map.put("yd_024", "liaoNingCmccPssor");
        map.put("yd_451", "heiLongJiangCmccPssor");
        map.put("yd_851", "guiZhouCmccPssor");
        map.put("yd_731", "huNanCmccPssor");
        map.put("yd_931", "ganSuCmccPssor");
        map.put("lt", "chinaUnicomPssor");
        return map;
    }
}
