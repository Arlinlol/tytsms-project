package com.iskyshop.pay.wechatpay.util;

public class ConfigUtils {
	private static final String configUrl="Config.properties";
	public static final String APPID=GetPropertiesValue.getValue(configUrl, "appId");//appid
	public static final String APPSECRET=GetPropertiesValue.getValue(configUrl, "appSecret");//appsecret
	public static final String PATERNER_KEY=GetPropertiesValue.getValue(configUrl, "paternerKey");// 商户支付密钥
	public static final String MCHI_ID=GetPropertiesValue.getValue(configUrl, "mch_id");//商户号
	public static final String BODY=GetPropertiesValue.getValue(configUrl, "body");//商品描述
	public static final String SPBILL_CREATE_IP=GetPropertiesValue.getValue(configUrl, "spbill_create_ip");//支付IP地址
	public static final String SIGNTYPE=GetPropertiesValue.getValue(configUrl, "signType");//加密方式
	public static final String DEVICE_INFO=GetPropertiesValue.getValue(configUrl, "device_info");//微信支付分配的终端设备号
	public static final String NOTIFY_URL=GetPropertiesValue.getValue(configUrl, "notify_url");//回调地址
	public static final String TRADE_TYPE_JS=GetPropertiesValue.getValue(configUrl, "trade_type_js");//交易类型
	public static final String INPUT_CHARSET=GetPropertiesValue.getValue(configUrl, "input_charset");//编码
	public static final String PREPAY_ID_URL=GetPropertiesValue.getValue(configUrl, "prepay_id_url");//统一支付接口
	
}
