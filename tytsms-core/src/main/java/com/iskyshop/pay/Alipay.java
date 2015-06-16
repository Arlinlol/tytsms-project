package com.iskyshop.pay;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.iskyshop.core.tools.Md5Encrypt;

public class Alipay {
	public static String CreateUrl_type1(String paygateway, String service,
			String sign_type, String out_trade_no, String input_charset,
			String partner, String agent, String key, String seller_email,
			String body, String subject, String price, String quantity,
			String show_url, String payment_type, String discount,
			String logistics_type, String logistics_fee,
			String logistics_payment, String return_url) {
		// String notify_url,需要的请把参数加入以上的createurl
		Map params = new HashMap();
		params.put("service", service);
		params.put("out_trade_no", out_trade_no);
		params.put("show_url", show_url);
		params.put("quantity", quantity);
		params.put("partner", partner);
		params.put("agent", agent);
		params.put("payment_type", payment_type);
		params.put("discount", discount);
		params.put("body", body);
		// params.put("notify_url", notify_url);
		params.put("price", price);
		params.put("return_url", return_url);
		params.put("seller_email", seller_email);
		params.put("logistics_type", logistics_type);
		params.put("logistics_fee", logistics_fee);
		params.put("logistics_payment", logistics_payment);
		params.put("subject", subject);
		params.put("_input_charset", input_charset);

		String prestr = "";

		prestr = prestr + key;
		//System.out.println("prestr=" + prestr);

		String sign = Md5Encrypt.md5(getContent_type3(params, key));

		String parameter = "";
		parameter = parameter + paygateway;

		List keys = new ArrayList(params.keySet());
		for (int i = 0; i < keys.size(); i++) {
			try {
				parameter = parameter
						+ keys.get(i)
						+ "="
						+ URLEncoder.encode((String) params.get(keys.get(i)),
								input_charset) + "&";
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
		}
		parameter = parameter + "sign=" + sign + "&sign_type=" + sign_type;

		return parameter;

	}

	/**
	 * 生成url方法 网关
	 * 
	 * @param paygateway
	 *            服务参数
	 * @param service
	 *            签名类型
	 * @param sign_type
	 *            外部订单号
	 * @param out_trade_no
	 *            编码机制
	 * @param input_charset
	 *            合作者ID
	 * @param partner
	 *            安全校验码
	 * @param key
	 *            商品展示地址
	 * @param show_url
	 *            商品描述
	 * @param body
	 *            商品价格
	 * @param total_fee
	 *            支付类型
	 * @param payment_type
	 *            卖家账户
	 * @param seller_email
	 *            商品名称
	 * @param subject
	 *            异步返回地址
	 * @param notify_url
	 *            同步返回地址
	 * @param return_url
	 *            支付方式
	 * @param paymethod
	 *            默认银行
	 * @param defaultbank
	 * @return
	 */
	public static String CreateUrl_type3(String paygateway, String service,
			String sign_type, String out_trade_no, String input_charset,
			String partner, String key, String show_url, String body,
			String total_fee, String payment_type, String seller_email,
			String subject, String notify_url, String return_url) {
		Map params = new HashMap();
		params.put("service", service);
		params.put("partner", partner);
		params.put("subject", subject);
		params.put("body", body);
		params.put("out_trade_no", out_trade_no);
		params.put("total_fee", total_fee);
		params.put("show_url", show_url);
		params.put("payment_type", payment_type);
		params.put("seller_email", seller_email);
		// params.put("it_b_pay", it_b_pay);
		params.put("return_url", return_url);
		params.put("notify_url", notify_url);
		// params.put("paymethod", paymethod);
		// params.put("defaultbank", defaultbank);
		params.put("_input_charset", input_charset);
		String prestr = "";
		prestr = prestr + key;
		String sign = Md5Encrypt.md5(getContent_type3(params, key));
		String parameter = "";
		parameter = parameter + paygateway;
		List keys = new ArrayList(params.keySet());
		for (int i = 0; i < keys.size(); i++) {
			try {
				parameter = parameter
						+ keys.get(i)
						+ "="
						+ URLEncoder.encode((String) params.get(keys.get(i)),
								input_charset) + "&";
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
		}

		parameter = parameter + "sign=" + sign + "&sign_type=" + sign_type;

		return parameter;

	}

	/**
	 * 功能：将安全校验码和参数排序 参数集合
	 * 
	 * @param params
	 *            安全校验码
	 * @param privateKey
	 */
	private static String getContent_type3(Map params, String privateKey) {
		List keys = new ArrayList(params.keySet());
		Collections.sort(keys);

		String prestr = "";

		for (int i = 0; i < keys.size(); i++) {
			String key = (String) keys.get(i);
			String value = (String) params.get(key);

			if (i == keys.size() - 1) {
				prestr = prestr + key + "=" + value;
			} else {
				prestr = prestr + key + "=" + value + "&";
			}
		}

		return prestr + privateKey;
	}

	public static String CreateUrl_type2(String paygateway, String service,
			String sign_type, String out_trade_no, String input_charset,
			String partner, String key, String seller_email, String body,
			String subject, String price, String quantity, String show_url,
			String payment_type, String discount, String logistics_type,
			String logistics_fee, String logistics_payment, String return_url) {
		// String notify_url,需要的请把参数加入以上的createurl
		Map params = new HashMap();
		params.put("service", service);
		params.put("out_trade_no", out_trade_no);
		params.put("show_url", show_url);
		params.put("quantity", quantity);
		params.put("partner", partner);
		params.put("payment_type", payment_type);
		params.put("discount", discount);
		params.put("body", body);
		// params.put("notify_url", notify_url);
		params.put("price", price);
		params.put("return_url", return_url);
		params.put("seller_email", seller_email);
		params.put("logistics_type", logistics_type);
		params.put("logistics_fee", logistics_fee);
		params.put("logistics_payment", logistics_payment);
		params.put("subject", subject);
		params.put("_input_charset", input_charset);
		String prestr = "";

		prestr = prestr + key;
		// System.out.println("prestr=" + prestr);

		String sign = Md5Encrypt.md5(getContent_type2(params, key));

		String parameter = "";
		parameter = parameter + paygateway;
		// System.out.println("prestr=" + parameter);
		List keys = new ArrayList(params.keySet());
		for (int i = 0; i < keys.size(); i++) {
			String value = (String) params.get(keys.get(i));
			if (value == null || value.trim().length() == 0) {
				continue;
			}
			try {
				parameter = parameter + keys.get(i) + "="
						+ URLEncoder.encode(value, input_charset) + "&";
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
		}

		parameter = parameter + "sign=" + sign + "&sign_type=" + sign_type;

		return sign;

	}

	private static String getContent_type2(Map params, String privateKey) {
		List keys = new ArrayList(params.keySet());
		Collections.sort(keys);

		String prestr = "";

		boolean first = true;
		for (int i = 0; i < keys.size(); i++) {
			String key = (String) keys.get(i);
			String value = (String) params.get(key);
			if (value == null || value.trim().length() == 0) {
				continue;
			}
			if (first) {
				prestr = prestr + key + "=" + value;
				first = false;
			} else {
				prestr = prestr + "&" + key + "=" + value;
			}
		}
		return prestr + privateKey;
	}
}
