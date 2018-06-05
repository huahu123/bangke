package com.neuq.info.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;

import javax.net.ssl.SSLContext;

import com.neuq.info.config.WxPayConfig;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;



/**
 * 读取证书
 * @author 小郑
 *
 */
@SuppressWarnings("deprecation")
public class ReadSSl {
    private static ReadSSl readSSL = null;

    private ReadSSl(){

    }

    public static ReadSSl getInstance(){
        if(readSSL == null){
            readSSL = new ReadSSl();
        }
        return readSSL;
    }
    /**
     *  读取 apiclient_cert.p12 证书
     * @return
     * @throws Exception
     */
    public  SSLConnectionSocketFactory readCustomSSL() throws Exception{
        /**
         * 注意PKCS12证书 是从微信商户平台-》账户设置-》 API安全 中下载的
         */
        KeyStore keyStore  = KeyStore.getInstance("PKCS12");
        /*
            此处要改
            wxconfig.SSLCERT_PATH :常量，指向证书的位置。例如：
            “E:/apache-tomcat-6.0.14/webapps-066/ayzk/WEB-INF/classes/apiclient_cert.p12”
        */
        FileInputStream instream = new FileInputStream(new File(WxPayConfig.KEY_PATH));
        try {
         /*
            此处要改
            wxconfig.SSLCERT_PASSWORD:常量，指向证书的密码。例如：
            “123456..”
        */
            keyStore.load(instream, WxPayConfig.MCH_ID.toCharArray());
        } finally {
            instream.close();
        }
        /*
            此处要改
            wxconfig.mch_id:常量，指向商户号。例如：
            “123456789..”
        */
        SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, WxPayConfig.MCH_ID.toCharArray()).build();

        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory( sslcontext, new String[] { "TLSv1" }, null,
                SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
        return sslsf;
    }

}
