package com.iskyshop.pay.wechatpay.util;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.jdom.JDOMException;

import com.iskyshop.foundation.domain.Payment;
import com.iskyshop.pay.wechatpay.config.WeChatConfig;
public class WXJSPay {

	public static Map jsApiPay(WeChatConfig wechatconfig,Payment payment) throws JDOMException, IOException {	
		String noceStr=Sha1Util.getNonceStr();//随机字符串
		String timeStamp=Sha1Util.getTimeStamp();//时间戳
		
		//调用统一下单接口，获取prepayid-----------------------------------------
		TreeMap<String, String> contentMap = new TreeMap<String, String>();
		//设置必填参数
		contentMap.put("appid", wechatconfig.getAppid());// 公众账号 ID
		contentMap.put("mch_id", wechatconfig.getMch_id());// 商户号
		contentMap.put("nonce_str", noceStr);// 随机字符串		
		contentMap.put("body", wechatconfig.getBody());// 商品描述
		contentMap.put("out_trade_no",wechatconfig.getOut_trade_no());// 商户订单号
		contentMap.put("total_fee",wechatconfig.getTotal_fee());// 订单总金额
		contentMap.put("spbill_create_ip",wechatconfig.getSpbill_create_ip());// 订单生成的机器IP
		contentMap.put("notify_url",wechatconfig.getNotify_url()); // 通知地址
		contentMap.put("trade_type",wechatconfig.getTrade_type()); // 交易类型
		contentMap.put("openid",wechatconfig.getOpenid()); // 用户标识
		contentMap.put("attach", wechatconfig.getAttach());//附加数据
		//非必填参数，商户可根据实际情况选填
//		contentMap.put("attach","xxxxxx");
//		contentMap.put("device_info","xxxxxx");
//		contentMap.put("time_start","xxxxxx");
//		contentMap.put("time_expire","xxxxxx");
//		contentMap.put("goods_tag","xxxxxx");
//		contentMap.put("product_id","xxxxxx");
		
		String wxpackage = WeiXinSignAndPackage.createPackage(contentMap,payment);
		contentMap.put("sign", wxpackage);
		String result=WeiXinSignAndPackage.getPrepayId(contentMap,payment);//调用统一接口返回的值
		
		//JSAPI支付
		Map<String,String> map=XMLUtil.doXMLParse(result);//调用统一接口返回的值转换为XML格式
		
		TreeMap<String, String> wxPayParamMap = new TreeMap<String, String>();
		wxPayParamMap.put("appId", wechatconfig.getAppid());
		wxPayParamMap.put("timeStamp",timeStamp );		
		wxPayParamMap.put("nonceStr",noceStr);
		wxPayParamMap.put("package", "prepay_id="+map.get("prepay_id"));
		wxPayParamMap.put("signType", "MD5");
		String paySign=WeiXinSignAndPackage.createPaySign(wxPayParamMap,payment);//支付得到的签名
		wxPayParamMap.put("paySign", paySign);
		wxPayParamMap.put("payMoney", wechatconfig.getTotal_fee());//到前段显示使用，支付不需要此参数
		
		return wxPayParamMap;
	}
	
	/**
	 * 二维码扫描
	 * @param openid
	 * @param total_fee
	 * @param out_trade_num
	 * @return
	 * @throws JDOMException
	 * @throws IOException
	 */
	public static String qcscan(WeChatConfig wechatconfig, Payment payment,String realPath) throws JDOMException, IOException {	
		try {
			String noceStr=Sha1Util.getNonceStr();//随机字符串
			String timeStamp=Sha1Util.getTimeStamp();//时间戳
			// 接口package部分-内容----------------------------------------------------------
			TreeMap<String, String> contentMap = new TreeMap<String, String>();
			contentMap.put("appid", payment.getAppid()); // 公众账号 ID
			contentMap.put("mch_id", payment.getMch_id()); // 商户号
			contentMap.put("nonce_str", noceStr); // 随机字符串		
			contentMap.put("body", wechatconfig.getBody()); // 商品描述
			contentMap.put("out_trade_no",wechatconfig.getOut_trade_no()); // 商户订单号
			contentMap.put("total_fee",wechatconfig.getTotal_fee()); // 订单总金额
			contentMap.put("spbill_create_ip",wechatconfig.getSpbill_create_ip()); // 订单生成的机器IP
			contentMap.put("notify_url",wechatconfig.getNotify_url()); // 通知地址
			contentMap.put("trade_type",wechatconfig.getTrade_type()); // 交易类型
			contentMap.put("product_id", wechatconfig.getProduct_id());
			contentMap.put("attach", wechatconfig.getAttach());//附加数据
//			contentMap.put("openid",wechatconfig.getOpenid()); // 用户标识
			String sign = WeiXinSignAndPackage.createPackage(contentMap,payment);
			contentMap.put("sign", sign);
			String result=WeiXinSignAndPackage.getPrepayId(contentMap,payment);//调用统一接口返回的值
			System.out.println("微信返回结果："+result);
			Map<String,String> map=XMLUtil.doXMLParse(result);//调用统一接口返回的值转换为XML格式
			String code_url = "";
			String return_code  = map.get("return_code");
			String result_code  = map.get("result_code");
			String qrcode = realPath+"/upload/qrcode/";
			if("SUCCESS".equals(return_code) && "SUCCESS".equals(result_code)){
				code_url = map.get("code_url");
//				QRCodeUtil.encode(code_url,qrcode,wechatconfig.getOut_trade_no());
			}
//			return "/upload/qrcode/"+wechatconfig.getOut_trade_no()+".JPG";
			return code_url;
			}catch (Exception e) {
				e.printStackTrace();
			}
		return "";
	}
	

}
