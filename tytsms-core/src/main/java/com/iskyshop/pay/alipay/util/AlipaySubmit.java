package com.iskyshop.pay.alipay.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;

import com.iskyshop.pay.alipay.config.AlipayConfig;
import com.iskyshop.pay.alipay.util.httpClient.HttpProtocolHandler;
import com.iskyshop.pay.alipay.util.httpClient.HttpRequest;
import com.iskyshop.pay.alipay.util.httpClient.HttpResponse;
import com.iskyshop.pay.alipay.util.httpClient.HttpResultType;

/* *
 *类名：AlipaySubmit
 *功能：支付宝各接口请求提交类
 *详细：构造支付宝各接口表单HTML文本，获取远程HTTP数据
 *版本：3.2
 *日期：2011-03-17
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
 */

public class AlipaySubmit {
	private static final String ALIPAY_GATEWAY_NEW = "https://mapi.alipay.com/gateway.do?";
	private static final String WAP_ALIPAY_GATEWAY_NEW = "http://wappaygw.alipay.com/service/rest.htm?";

	/**
	 * 生成要请求给支付宝的参数数组
	 * 
	 * @param sParaTemp
	 *            请求前的参数数组
	 * @return 要请求的参数数组
	 */
	private static Map<String, String> buildRequestPara(AlipayConfig config,
			Map<String, String> sParaTemp) {
		// 除去数组中的空值和签名参数
		Map<String, String> sPara = AlipayCore.paraFilter(sParaTemp);
		// 生成签名结果
		String mysign = AlipayCore.buildMysign(config, sPara);

		// 签名结果与签名方式加入请求提交参数组中
		sPara.put("sign", mysign);
		if (!sPara.get("service").equals("alipay.wap.trade.create.direct")
				&& !sPara.get("service").equals(
						"alipay.wap.auth.authAndExecute")) {
			sPara.put("sign_type", config.getSign_type());
		}
		return sPara;
	}

	/**
	 * 构造提交表单HTML数据
	 * 
	 * @param sParaTemp
	 *            请求参数数组
	 * @param gateway
	 *            网关地址
	 * @param strMethod
	 *            提交方式。两个值可选：post、get
	 * @param strButtonName
	 *            确认按钮显示文字
	 * @return 提交表单HTML文本
	 */
	public static String buildForm(AlipayConfig config,
			Map<String, String> sParaTemp, String gateway, String strMethod,
			String strButtonName) {
		// 待请求参数数组
		Map<String, String> sPara = buildRequestPara(config, sParaTemp);
		List<String> keys = new ArrayList<String>(sPara.keySet());
		StringBuffer sbHtml = new StringBuffer();
		sbHtml.append("<form id=\"alipaysubmit\" name=\"alipaysubmit\" enctype=\"multipart/form-data\" action=\""
				+ gateway
				+ "_input_charset="
				+ config.getInput_charset()
				+ "\" method=\"" + strMethod + "\">");

		for (int i = 0; i < keys.size(); i++) {
			String name = (String) keys.get(i);
			String value = (String) sPara.get(name);

			sbHtml.append("<input type=\"hidden\" name=\"" + name
					+ "\" value=\"" + value + "\"/>");
		}

		// submit按钮控件请不要含有name属性
		sbHtml.append("<input type=\"submit\" value=\"" + strButtonName
				+ "\" style=\"display:none;\"></form>");
		sbHtml.append("<script>document.forms['alipaysubmit'].submit();</script>");

		return sbHtml.toString();
	}

	/**
	 * MAP类型数组转换成NameValuePair类型
	 * 
	 * @param properties
	 *            MAP类型数组
	 * @return NameValuePair类型数组
	 */
	private static NameValuePair[] generatNameValuePair(
			Map<String, String> properties) {
		NameValuePair[] nameValuePair = new NameValuePair[properties.size()];
		int i = 0;
		for (Map.Entry<String, String> entry : properties.entrySet()) {
			nameValuePair[i++] = new NameValuePair(entry.getKey(),
					entry.getValue());
		}

		return nameValuePair;
	}

	/**
	 * 构造模拟远程HTTP的POST请求，获取支付宝的返回XML处理结果
	 * 
	 * @param sParaTemp
	 *            请求参数数组
	 * @param gateway
	 *            网关地址
	 * @return 支付宝返回XML处理结果
	 * @throws Exception
	 */
	public static String sendPostInfo(AlipayConfig config,
			Map<String, String> sParaTemp, String gateway) throws Exception {
		// 待请求参数数组
		Map<String, String> sPara = buildRequestPara(config, sParaTemp);

		HttpProtocolHandler httpProtocolHandler = HttpProtocolHandler
				.getInstance();

		HttpRequest request = new HttpRequest(HttpResultType.BYTES);
		// 设置编码集
		request.setCharset(config.getInput_charset());

		request.setParameters(generatNameValuePair(sPara));
		request.setUrl(gateway + "_input_charset=" + config.getInput_charset());

		HttpResponse response = httpProtocolHandler.execute(request);
		if (response == null) {
			return null;
		}

		String strResult = response.getStringResult();

		return strResult;
	}

	public static String buildRequest(AlipayConfig config, String type,
			Map<String, String> sParaTemp, String strParaFileName,
			String strFilePath) throws HttpException, IOException {
		// 待请求参数数组
		Map<String, String> sPara = buildRequestPara(config, sParaTemp);
		HttpProtocolHandler httpProtocolHandler = HttpProtocolHandler
				.getInstance();
		HttpRequest request = new HttpRequest(HttpResultType.BYTES);
		// 设置编码集
		request.setCharset(config.getInput_charset());
		request.setParameters(generatNameValuePair(sPara));
		if (type.equals("web")) {
			request.setUrl(ALIPAY_GATEWAY_NEW + "_input_charset="
					+ config.getInput_charset());
		}
		if (type.equals("wap")) {
			request.setUrl(WAP_ALIPAY_GATEWAY_NEW + "_input_charset="
					+ config.getInput_charset());
		}
		HttpResponse response = httpProtocolHandler.execute(request,
				strParaFileName, strFilePath);
		if (response == null) {
			return null;
		}
		String strResult = response.getStringResult();
		return strResult;
	}

	/**
	 * 解析远程模拟提交后返回的信息，获得token
	 * 
	 * @param text
	 *            要解析的字符串
	 * @return 解析结果
	 * @throws Exception
	 */
	public static String getRequestToken(AlipayConfig config, String text)
			throws Exception {
		String request_token = "";
		// 以“&”字符切割字符串
		String[] strSplitText = text.split("&");
		// 把切割后的字符串数组变成变量与数值组合的字典数组
		Map<String, String> paraText = new HashMap<String, String>();
		for (int i = 0; i < strSplitText.length; i++) {

			// 获得第一个=字符的位置
			int nPos = strSplitText[i].indexOf("=");
			// 获得字符串长度
			int nLen = strSplitText[i].length();
			// 获得变量名
			String strKey = strSplitText[i].substring(0, nPos);
			// 获得数值
			String strValue = strSplitText[i].substring(nPos + 1, nLen);
			// 放入MAP类中
			paraText.put(strKey, strValue);
		}

		if (paraText.get("res_data") != null) {
			String res_data = paraText.get("res_data");
			// 解析加密部分字符串（RSA与MD5区别仅此一句）
			if (config.getSign_type().equals("0001")) {
				res_data = RSA.decrypt(res_data, config.getPrivate_key(),
						config.getInput_charset());
			}
			// token从res_data中解析出来（也就是说res_data中已经包含token的内容）
			Document document = DocumentHelper.parseText(res_data);
			request_token = document.selectSingleNode(
					"//direct_trade_create_res/request_token").getText();
		}
		return request_token;
	}
}
