package com.neuq.info.common.utils;

import com.neuq.info.common.utils.wxPayUtil.CommonUtil;
import com.neuq.info.common.utils.wxPayUtil.HttpUtil;
import com.neuq.info.common.utils.wxPayUtil.RandomUtils;
import com.neuq.info.config.WxPayConfig;
import lombok.extern.log4j.Log4j;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author Lin Dexiang
 * @date 2018/5/27
 */

@Log4j
public class PublicKeyUtil {

    public static String getPublicKey() throws Exception {
        SortedMap<String, String> parameters = new TreeMap<String, String>();
        String nonce_str = RandomUtils.generateMixString(30);
        parameters.put("mch_id", WxPayConfig.MCH_ID);
        parameters.put("nonce_str", nonce_str);
        parameters.put("sign_type", "MD5");
        // 创建签名
        String sign = SignUtils.creatSign(parameters);
        log.info("sign=" + sign);

        TreeMap<String, String> tmap = new TreeMap<String, String>();
        tmap.put("mch_id", WxPayConfig.MCH_ID);
        tmap.put("nonce_str", nonce_str);
        tmap.put("sign_type", "MD5");
        tmap.put("sign", sign);
        String xml = CommonUtil.getRequestXml(tmap);//将请求参数转换为请求报文
        log.info(xml);
        //带证书请求
        StringBuffer xml1 = HttpUtil.httpClientCustomSSL(WxPayConfig.GET_PUBLIC_KEY_URL, xml);//发送http的post请求获取公钥报文
        Map<String, String> result = CommonUtil.parseXml(xml1.toString());//解析腾迅返回的公钥xml并获取公钥元素
        log.info(xml1);
        return result.get("pub_key");


    }
}
