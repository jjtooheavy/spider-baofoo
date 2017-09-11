package com.xinyan.spider.isp.base;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @Description:基础校验类
 * @author: york
 * @date: 2016-08-02 16:58
 * @version: v1.0
 */
public class ValidataUtils {

    protected static Logger logger= LoggerFactory.getLogger(ValidataUtils.class);

    //共同校验
    public StatusCode CommomValida(Context context) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        StatusCode statusCode= StatusCode.SUCCESS;

        String methodName = "";
        if(!context.getTaskType().equals("education")){
            methodName = context.getTaskType() + "_"+ context.getArea();
        }
        else{

            methodName = context.getTaskType();
        }

        Object[] args = {context};
        @SuppressWarnings("rawtypes")
		Class[] argsClass = {context.getClass()};

        for (int i = 0, j = args.length; i < j; i++) {
            argsClass[i] = args[i].getClass();
        }
        try {

            Method method = getClass().getMethod(methodName, argsClass);
            return (StatusCode) method.invoke(null, args);

        } catch (SecurityException e) {

            e.printStackTrace();
            return statusCode;
        } catch (NoSuchMethodException e) {

            logger.error("==>不存在该校验方法：" + methodName);
            return StatusCode.输入有误;
        } catch (IllegalArgumentException e) {

            e.printStackTrace();
            return statusCode;
        }
    }
}



