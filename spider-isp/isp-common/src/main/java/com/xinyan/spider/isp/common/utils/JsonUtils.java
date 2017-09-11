package com.xinyan.spider.isp.common.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by Yu Yangjun on 2016/6/30.
 */
public class JsonUtils {

    /**
     * json转化为bean
     *
     * @param json
     * @param beanClass
     * @param <T>
     * @return
     */
    public static <T> T json2Bean(String json,Class<T> beanClass){
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            return objectMapper.readValue(json,beanClass);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /**
     * bean转化为json
     *
     * @param bean
     * @return
     * @throws Exception
     */
    public static String beanToJson(Object bean) {
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(bean);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }


    /**
     * to jsonNode
     *
     * @param json
     * @return
     */
    public static JsonNode toJsonNode(String  json) {
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readValue(json, JsonNode.class);
            return rootNode;
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

}
