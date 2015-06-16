package com.iskyshop.pay.bill.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.iskyshop.pay.bill.config.BillConfig;

public class BillService {
	private static final String BILL_GATEWAY_NEW = "https://www.99bill.com/gateway/recvMerchantInfoAction.htm";

	public static String buildForm(BillConfig config,
			Map<String, String> sParaTemp, String strMethod,
			String strButtonName) {
		// 待请求参数数组
		List<String> keys = new ArrayList<String>(sParaTemp.keySet());

		StringBuffer sbHtml = new StringBuffer();

		sbHtml
				.append("<form id=\"99billsubmit\" name=\"99billsubmit\" action=\""
						+ BILL_GATEWAY_NEW + "\" method=\"" + strMethod + "\">");

		for (int i = 0; i < keys.size(); i++) {
			String name = (String) keys.get(i);
			String value = (String) sParaTemp.get(name);

			sbHtml.append("<input type=\"hidden\" name=\"" + name
					+ "\" value=\"" + value + "\"/>");
		}

		// submit按钮控件请不要含有name属性
		sbHtml.append("<input type=\"submit\" value=\"" + strButtonName
				+ "\" style=\"display:none;\"></form>");
		sbHtml
				.append("<script>document.forms['99billsubmit'].submit();</script>");
System.out.println(sbHtml.toString());
		return sbHtml.toString();
	}
}
