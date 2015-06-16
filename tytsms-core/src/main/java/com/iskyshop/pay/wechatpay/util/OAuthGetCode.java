package com.iskyshop.pay.wechatpay.util;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class OAuthGetCode extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request,response);
	}
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String appid=ConfigUtils.APPID;
		String redirectUrl=ConfigUrlUtils.YUMING+"PayDemo/PayServlet";
		String state="0";
		request.setAttribute("appid", appid);
		request.setAttribute("redirect_url",redirectUrl);
		request.setAttribute("outTradeNum", state);
		request.getRequestDispatcher("jsp/utils/OAuthGetCode.jsp").forward(request, response);
	}

}
