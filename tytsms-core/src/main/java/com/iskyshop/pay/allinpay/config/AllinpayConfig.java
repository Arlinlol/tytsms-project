package com.iskyshop.pay.allinpay.config;


public class AllinpayConfig {

	// ↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
	// 合作身份者ID，以2088开头由16位纯数字组成的字符串
	private String partner = "";

	// 交易安全检验码，由数字和字母组成的32位字符串
	private String key = "";

	// 签约支付宝账号或卖家收款支付宝帐户
	private String seller_email = "";

	// 支付宝服务器通知的页面 要用 http://格式的完整路径，不允许加?id=123这类自定义参数
	// 必须保证其地址能够在互联网中访问的到
	private String notify_url = "";

	// 当前页面跳转后的页面 要用 http://格式的完整路径，不允许加?id=123这类自定义参数
	// 域名不能写成http://localhost/create_direct_pay_by_user_jsp_utf8/return_url.jsp
	// ，否则会导致return_url执行无效
	private String return_url = "";

	// ↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

	// 调试用，创建TXT日志路径
	private String log_path = "D:\\alipay_log_" + System.currentTimeMillis()
			+ ".txt";

	// 字符编码格式 目前支持 gbk 或 utf-8 值为1
	private String input_charset = "1";

	// 签名方式 不需修改,无线的产品中，签名方式为rsa时，sign_type需赋值为0001而不是RSA
	private String sign_type = "MD5";

	// 访问模式,根据自己的服务器是否支持ssl访问，若支持请选择https；若不支持请选择http
	private String transport = "http";

	// 商户的私钥
	// 如果签名方式设置为“0001”时，请设置该参数
	public static String private_key = "";

	// 支付宝的公钥
	// 如果签名方式设置为“0001”时，请设置该参数
	public static String ali_public_key = "";

	public static String getPrivate_key() {
		return private_key;
	}

	public static void setPrivate_key(String private_key) {
		AllinpayConfig.private_key = private_key;
	}

	public static String getAli_public_key() {
		return ali_public_key;
	}

	public static void setAli_public_key(String ali_public_key) {
		AllinpayConfig.ali_public_key = ali_public_key;
	}

	public String getPartner() {
		return partner;
	}

	public void setPartner(String partner) {
		this.partner = partner;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getSeller_email() {
		return seller_email;
	}

	public void setSeller_email(String seller_email) {
		this.seller_email = seller_email;
	}

	public String getNotify_url() {
		return notify_url;
	}

	public void setNotify_url(String notify_url) {
		this.notify_url = notify_url;
	}

	public String getReturn_url() {
		return return_url;
	}

	public void setReturn_url(String return_url) {
		this.return_url = return_url;
	}

	public String getLog_path() {
		return log_path;
	}

	public void setLog_path(String log_path) {
		this.log_path = log_path;
	}

	public String getInput_charset() {
		return input_charset;
	}

	public void setInput_charset(String input_charset) {
		this.input_charset = input_charset;
	}

	public String getSign_type() {
		return sign_type;
	}

	public void setSign_type(String sign_type) {
		this.sign_type = sign_type;
	}

	public String getTransport() {
		return transport;
	}

	public void setTransport(String transport) {
		this.transport = transport;
	}

}
