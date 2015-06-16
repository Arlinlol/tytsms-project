package com.iskyshop.pay.wechatpay.util;

public class ConfigUrlUtils {
	private static final String configUrl="ConfigURL.properties";
	
	public static final String COMMON_PAY_INTERFACE=GetPropertiesValue.getValue(configUrl, "common_pay_interface");//统一支付接口
	public static final String YUMING=GetPropertiesValue.getValue(configUrl, "yuming");//项目域名
}
