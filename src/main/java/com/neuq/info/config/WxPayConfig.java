package com.neuq.info.config;

/**
 * @AUTHOR lindexiang
 * @DATE 下午9:55
 */
public class WxPayConfig {
//    //微信支付的商户id
//    public static final String mch_id = "1493984102";
//    //微信支付的商户密钥
//    public static final String key = "15210725739Qqq5213079688Qqq52177";
//    //支付成功后的服务器回调url
//    public static final String notify_url = "https://??/??/weixin/api/wxNotify";
//    //签名方式，固定值
//    public static final String SIGNTYPE = "MD5";
//    //交易类型，小程序支付的固定值为JSAPI
//    public static final String TRADETYPE = "JSAPI";
//    //微信统一下单接口地址
//    public static final String pay_url = "https://api.mch.weixin.qq.com/pay/unifiedorder";


    public static final String APP_ID = "wx48000b86228fe774";

    public static final String APP_SECRET = "56660a838c0a74f96a8c1de3b84a295e";

    public static final String APP_KEY = "15210725739Qqq5213079688Qqq52177";

    public static final String MCH_ID = "1493984102";  //商户号

    public static final String URL_UNIFIED_ORDER = "https://api.mch.weixin.qq.com/pay/unifiedorder";

    public static final String URL_NOTIFY =  "https://daluotech.natapp4.cc/wxPay/paySuccess";

    public static final String URL_REFUND = "https://api.mch.weixin.qq.com/secapi/pay/refund";

    public static final String TIME_FORMAT = "yyyyMMddHHmmss";

    public static final int TIME_EXPIRE = 2;  //单位是day

    public static final String KEY_PATH = "/Users/lindexiang/softWare/apiclient_cert.p12";

    public static final String KEY_REFUND = "192006250b4c09247ec02edce69f6a2d";

}
