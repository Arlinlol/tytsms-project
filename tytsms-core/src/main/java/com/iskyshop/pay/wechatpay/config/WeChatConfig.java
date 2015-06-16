package com.iskyshop.pay.wechatpay.config;
/**
 * 微信支付配置类
 * @author DevinYang
 *
 */
public class WeChatConfig {
	/**-------------------统一支付接口参数begin--------------------------*/			
	private String appid;		//微信分配的公众账号 ID（必填）
	private String mch_id;		//微信支付分配的商户号（必填）
	private String nonce_str;	//随机字符串，不长于 32 位（必填）
	private String sign;		//签名（必填）
	private String body;		//商品描述（必填）
	private String out_trade_no;//商户系统内部的订单号 ,32个字符内、可包含字母, 确保在商户系统唯一 （必填）
	private String total_fee;	//订单总金额，单位为分，不能带小数点（必填）
	private String spbill_create_ip;//订单生成的机器 IP（必填）
	private String notify_url;	//接收微信支付成功通知（必填）
	private String trade_type;	//交易类型 （JSAPI、 NATIVE、 APP）（必填）
	private String openid;		//用户在商户 appid 下的唯一标识， trade_type 为 JSAPI时，此参数必传，获取方式见表头说明。（必填）
	
	
	private String attach;		//附加数据，原样返回（非必填）
	private String device_info;	//微信支付分配的终端设备号（非必填）
	private String time_start;	//订 单 生 成 时 间 ， 格 式 为yyyyMMddHHmmss（非必填）
	private String time_expire;	//订 单 失 效 时 间 ， 格 式 为yyyyMMddHHmmss（非必填）
	private String goods_tag;	//商品标记，该字段不能随便填，不使用请填空（非必填）
	private String product_id;	//只在 trade_type 为 NATIVE时需要填写。此 id 为二维码中包含的商品 ID，商户自行维护。（非必填）
	/**-------------------统一支付接口参数end--------------------------*/	
	
	
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public String getMch_id() {
		return mch_id;
	}
	public void setMch_id(String mch_id) {
		this.mch_id = mch_id;
	}
	public String getNonce_str() {
		return nonce_str;
	}
	public void setNonce_str(String nonce_str) {
		this.nonce_str = nonce_str;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getOut_trade_no() {
		return out_trade_no;
	}
	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}
	public String getTotal_fee() {
		return total_fee;
	}
	public void setTotal_fee(String total_fee) {
		this.total_fee = total_fee;
	}
	public String getSpbill_create_ip() {
		return spbill_create_ip;
	}
	public void setSpbill_create_ip(String spbill_create_ip) {
		this.spbill_create_ip = spbill_create_ip;
	}
	public String getNotify_url() {
		return notify_url;
	}
	public void setNotify_url(String notify_url) {
		this.notify_url = notify_url;
	}
	public String getTrade_type() {
		return trade_type;
	}
	public void setTrade_type(String trade_type) {
		this.trade_type = trade_type;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getAttach() {
		return attach;
	}
	public void setAttach(String attach) {
		this.attach = attach;
	}
	public String getDevice_info() {
		return device_info;
	}
	public void setDevice_info(String device_info) {
		this.device_info = device_info;
	}
	public String getTime_start() {
		return time_start;
	}
	public void setTime_start(String time_start) {
		this.time_start = time_start;
	}
	public String getTime_expire() {
		return time_expire;
	}
	public void setTime_expire(String time_expire) {
		this.time_expire = time_expire;
	}
	public String getGoods_tag() {
		return goods_tag;
	}
	public void setGoods_tag(String goods_tag) {
		this.goods_tag = goods_tag;
	}
	public String getProduct_id() {
		return product_id;
	}
	public void setProduct_id(String product_id) {
		this.product_id = product_id;
	}

	
	
	
	
}
