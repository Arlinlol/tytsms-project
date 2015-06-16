package com.iskyshop.core.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.AuthenticationException;
import org.springframework.security.ui.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.iskyshop.core.tools.CommUtil;

/**
 * 
 * <p>
 * Title: LoginUrlEntryPoint.java
 * </p>
 * 
 * <p>
 * Description: SpringSeurity验证切入点，这里用来辨识是否通过过验证
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * Company: 沈阳网之商科技有限公司 www.iskyshop.com
 * </p>
 * 
 * @author erikzhang
 * 
 * @date 2014-4-24
 * 
 * @version iskyshop_b2b2c 1.0
 */
@Component
public class LoginUrlEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(ServletRequest req, ServletResponse res,
			AuthenticationException authException) throws IOException,
			ServletException {
		// TODO Auto-generated method stub
		String targetUrl = null;
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		String url = request.getRequestURI();
		if (request.getQueryString() != null
				&& !request.getQueryString().equals("")) {
			url = url + "?" + request.getQueryString();
		}
		request.getSession(false).setAttribute("refererUrl", url);
		// 取得登陆前的url
		String refererUrl = request.getHeader("Referer");
		// TODO 增加处理逻辑
		targetUrl = refererUrl;
		if (url.indexOf("/admin/") >= 0) {
			targetUrl = request.getContextPath() + "/admin/login.htm";
			request.getSession(false).removeAttribute("refererUrl");
		} else {
			if (url.indexOf("/seller/") >= 0) {
				targetUrl = request.getContextPath() + "/seller/login.htm";
			} else {
				targetUrl = request.getContextPath() + "/user/login.htm";
				if (CommUtil
						.null2String(
								request.getSession().getAttribute(
										"iskyshop_view_type")).equals("weixin")) {
					targetUrl = request.getContextPath() + "/weixin/login.htm";
				}
			}
		}
		response.sendRedirect(targetUrl);
	}
}
