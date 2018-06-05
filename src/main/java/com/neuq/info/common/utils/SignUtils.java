package com.neuq.info.common.utils;

import com.neuq.info.common.utils.wxPayUtil.CommonUtil;
import com.neuq.info.config.WxPayConfig;
import lombok.extern.log4j.Log4j;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

/**
 * @author Lin Dexiang
 * @date 2018/5/27
 */

@Log4j
public class SignUtils {
    /**
     * @param parameters
     * */
    public static String creatSign(SortedMap<String, String> parameters) throws Exception {
        StringBuffer sb = new StringBuffer();
        Set es = parameters.entrySet();
        Iterator it = es.iterator();
        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            String k = (String)entry.getKey();
            Object v = entry.getValue();
            if(null != v && !"".equals(v)
                    && !"sign".equals(k) && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        sb.append("key=" + WxPayConfig.KEY_REFUND);
        String sign = CommonUtil.getMD5(sb.toString()).toUpperCase();
        log.info("sign=" + sign);
        return sign;
    }

    /**
     * @param characterEncoding 编码格式 utf-8
     * */
    public static String creatSign(String characterEncoding,
                                   SortedMap<String, String> parameters) {
        StringBuffer sb = new StringBuffer();
        Set es = parameters.entrySet();
        Iterator it = es.iterator();
        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            String k = (String)entry.getKey();
            Object v = entry.getValue();
            if(null != v && !"".equals(v)
                    && !"sign".equals(k) && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        sb.append("key=" + WxPayConfig.KEY_REFUND);
        String sign = MD5Utils.MD5Encode(sb.toString(), characterEncoding).toUpperCase();
        System.out.println(sign);
        return sign;
    }
}
