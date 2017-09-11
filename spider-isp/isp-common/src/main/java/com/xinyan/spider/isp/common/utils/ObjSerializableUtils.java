package com.xinyan.spider.isp.common.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.extern.slf4j.Slf4j;

/**
 * 对象序列化和反序列化工具
 * Created by heliang on 2017/3/16.
 */
@Slf4j
public class ObjSerializableUtils {
	
    private static Logger logger = LoggerFactory.getLogger(ObjSerializableUtils.class);

    private static final String TEMP_ENCODING = "ISO-8859-1";
    private static final String DEFAULT_ENCODING = "UTF-8";

    /**
     * 序列化对象
     * @param obj 对象
     * @return 序列化后的字符串
     * @throws IOException
     */
    public static String writeToStr(Object obj) throws IOException {
        // 此类实现了一个输出流，其中的数据被写入一个 byte 数组。
        // 缓冲区会随着数据的不断写入而自动增长。可使用 toByteArray() 和 toString() 获取数据。
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // 专用于java对象序列化，将对象进行序列化
        ObjectOutputStream objectOutputStream = null;
        String serStr = null;
        try {
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(obj);
            serStr = byteArrayOutputStream.toString(TEMP_ENCODING);
            serStr = java.net.URLEncoder.encode(serStr, DEFAULT_ENCODING);
        } catch (IOException e) {
            logger.error("序列化对象失败");
        } finally {
            objectOutputStream.close();
        }
        return serStr;
    }

    /**
     * 字符串对象反序列化
     * @param serStr 字符串
     * @param tClass 需要反序列化的对象class
     * @param <T>
     * @return 指定对象
     * @throws IOException
     */
    public static <T> T deserializeFromStr(String serStr,Class<T> tClass) throws IOException {
        ByteArrayInputStream byteArrayInputStream = null;
        ObjectInputStream objectInputStream = null;
        try {
            String deserStr = java.net.URLDecoder.decode(serStr, DEFAULT_ENCODING);
            byteArrayInputStream = new ByteArrayInputStream(deserStr.getBytes(TEMP_ENCODING));
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            return (T) objectInputStream.readObject();
        } catch (IOException e) {
            logger.error("反序列化对象失败");
        } catch (ClassNotFoundException e) {
            logger.error("反序列化对象失败，类不存在");
        } finally {
            objectInputStream.close();
            byteArrayInputStream.close();
        }
        return null;
    }

//    public static void main(String[] args) {
//        UserLoginDto userLoginDto = new UserLoginDto();
//        userLoginDto.setUserName("1231");
//        try {
//            String str = writeToStr(userLoginDto);
//            System.out.println(str);
//            UserLoginDto deObj = deserializeFromStr(str,UserLoginDto.class);
//            System.out.println(deObj);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
