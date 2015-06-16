package com.iskyshop.pay.allinpay;

import java.util.List;

import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.tools.CommUtil;

public class AllinpayTools {
	public static String buildForm(List<SysMap> list,String gateway) {
		StringBuffer sb = new StringBuffer();
		sb.append("<body onLoad=\"javascript:document.allinpay.submit()\">");
		sb
				.append("<form action=\""+gateway+"?\" method=\"POST\" name=\"allinpay\">");
		for (SysMap sm : list) {
			sb.append("<input type=\"hidden\" name=\""
					+ CommUtil.null2String(sm.getKey()) + "\"    value=\""
					+ CommUtil.null2String(sm.getValue()) + "\" size=\"100\">");
		} 
		sb.append("</form></body>");
		return sb.toString();
	}
}
