package com.iskyshop.pay.wechatpay.util;

import java.util.Map;

public class WechatpayTools {
	public static String buildForm(Map<String, String> paramMap) {
		StringBuffer sb = new StringBuffer();
		String param = "\"appId\":\"" + paramMap.get("appId") + "\",\"timeStamp\":\"" + paramMap.get("timeStamp")
				+ "\",\"nonceStr\":\"" + paramMap.get("nonceStr") + "\",\"package\":\""
				+ paramMap.get("package") + "\",\"signType\" : \"MD5" + "\",\"paySign\":\""
				+ paramMap.get("paySign") + "\"";
		sb.append("<body>");
		sb.append("<script type=\"text/javascript\">");
		sb.append("jQuery(document).ready(function(){");
		sb.append("var str = window.navigator.userAgent;");
		sb.append("var version = str.substring(8, 11);");
		sb.append("if (version != \"5.0\") {");
		sb.append("alert(\"微信浏览器系统版本过低，请将微信升级至5.0以上\");");
		sb.append("} else {");
		sb.append("WeixinJSBridge.invoke('getBrandWCPayRequest', {");
		sb.append(param);
		sb.append("},function(res){");
		sb.append("if (res.err_msg == \"get_brand_wcpay_request:ok\") {");
		sb.append("alert(\"支付成功\");");
		sb.append("} else if (res.err_msg == \"get_brand_wcpay_request:cancel\") {");
		sb.append("alert(\"取消支付\");");
		sb.append("} else if (res.err_msg == \"get_brand_wcpay_request:fail\") {");
		sb.append("alert(\"支付失败\");");
		sb.append("}");
		sb.append("});");
		sb.append("}");
		sb.append("});");
		
		sb.append("</script>");
		sb.append("</body>");
		System.out.println(sb.toString());
		return sb.toString();
	}
}
