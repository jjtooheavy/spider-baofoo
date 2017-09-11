package com.xinyan.spider.isp.mobile.service;

import com.xinyan.spider.isp.common.captcha.UuApi;
import com.xinyan.spider.isp.common.captcha.UuConfig;
import com.xinyan.spider.isp.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * 验证码识别
 * @author Yuyangjun on 08/09/2017 5:07 PM
 */
@Component
public class CaptchaService {

    @Value("${softid}")
    private String softid;
    @Value("${softkey}")
    private String softkey;
    @Value("${username}")
    private String username;
    @Value("${password}")
    private String password;

    /**
     * 验证码识别
     * @param image
     * @param codeType
     * @return
     */
    private String recognition(byte[] image ,int codeType){
        UuApi uuApi = new UuApi();
        uuApi.setSoftInfo(softid, softkey);
        uuApi.userLogin(username, password);
        String result=uuApi.upload(image,String.valueOf(codeType),true);
        if(StringUtils.isNotBlank(result)){
            String[] arrayResult = result.split("\\|");
            if (arrayResult.length == 2) {
                result= arrayResult[1];
            }else {
                result=uuApi.getResult(result);
            }
        }
        return result;
    }

    /**
     * 验证码识别
     * @param image
     * @param codeType
     * @return
     */
    public String recognition(BufferedImage image,int codeType){
        String result="";
        ByteArrayOutputStream outputStream;
        try{
            outputStream=new ByteArrayOutputStream();
            ImageIO.write(image, "png", outputStream);
            outputStream.flush();
            byte[] imagebytes=outputStream.toByteArray();
            result=recognition(imagebytes,codeType);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
        return result;
    }

    /**
     * 验证码识别
     * @param in
     * @param codeType
     * @return
     */
    public String recognition(InputStream in,int codeType){
        String result="";
        ByteArrayOutputStream outputStream;
        try{
            outputStream=new ByteArrayOutputStream();
            int c;
            byte buffer[]=new byte[1024];
            while((c=in.read(buffer))!=-1){
                for(int i=0;i<c;i++)
                    outputStream.write(buffer[i]);
            }
            in.close();

            outputStream.flush();
            byte[] imagebytes=outputStream.toByteArray();
            result=recognition(imagebytes,codeType);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
        return result;
    }
}