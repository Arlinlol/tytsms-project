package com.iskyshop.pay.paypal;

import java.util.List;

import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.tools.CommUtil;

public class PaypalTools {
	public static String buildForm(List<SysMap> list) {
		StringBuffer sb = new StringBuffer();
		sb.append("<body onLoad=\"javascript:document.paypal.submit()\">");
		sb
				.append("<form action=\"https://www.paypal.com/cgi-bin/webscr\" method=\"POST\" name=\"paypal\">");
		for (SysMap sm : list) {
			sb.append("<input type=\"hidden\" name=\""
					+ CommUtil.null2String(sm.getKey()) + "\"    value=\""
					+ CommUtil.null2String(sm.getValue()) + "\" size=\"100\">");
		}
		sb.append("</form><body>");
		return sb.toString();
	}
}
