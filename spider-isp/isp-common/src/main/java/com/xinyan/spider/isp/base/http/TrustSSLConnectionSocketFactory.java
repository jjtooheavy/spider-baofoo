package com.xinyan.spider.isp.base.http;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;
import java.security.cert.CertificateException;

/**
 * Created by Yu Yangjun on 2016/7/2.
 */
public class
TrustSSLConnectionSocketFactory {

    /**
     * 创建SSL安全连接
     *
     * @return
     */
    public static SSLConnectionSocketFactory createSSLConnSocketFactory() {
        SSLConnectionSocketFactory connectionSocketFactory=null;
        try{
            SSLContext sslcontext = SSLContexts.custom()
                    //忽略掉对服务器端证书的校验
                    .loadTrustMaterial(new TrustStrategy() {
                        @Override
                        public boolean isTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                            return true;
                        }
                    }).build();
            connectionSocketFactory = new SSLConnectionSocketFactory(sslcontext, SSLConnectionSocketFactory.getDefaultHostnameVerifier());
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
       return connectionSocketFactory;
    }
}
