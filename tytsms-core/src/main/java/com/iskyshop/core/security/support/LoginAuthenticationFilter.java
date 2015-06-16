package com.iskyshop.core.security.support;

import java.io.IOException;
import java.util.LinkedList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationException;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.security.ui.webapp.AuthenticationProcessingFilter;
import org.springframework.security.util.TextUtils;

import com.iskyshop.core.exception.CaptchaErrorException;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.uc.api.UCClient;
import com.iskyshop.uc.api.XMLHelper;

/**
 * 
* <p>Title: LoginAuthenticationFilter.java</p>

* <p>Description: 重写SpringSecurity登录验证过滤器,验证器重新封装封装用户登录信息，可以任意控制用户与外部程序的接口，如整合UC论坛等等</p>

* <p>Copyright: Copyright (c) 2014</p>

* <p>Company: 沈阳网之商科技有限公司 www.iskyshop.com</p>

* @author erikzhang

* @date 2014-4-24

* @version iskyshop_b2b2c 1.0
 */
public class LoginAuthenticationFilter extends AuthenticationProcessingFilter {
	@Autowired
	private ISysConfigService configService;

	public Authentication attemptAuthentication(HttpServletRequest request)
			throws AuthenticationException {
		// 状态， admin表示后台，user表示前台,seller表示商家
		String login_role = request.getParameter("login_role");
		if (login_role == null || login_role.equals(""))
			login_role = "user";
		HttpSession session = request.getSession();
		session.setAttribute("login_role", login_role);
		session.setAttribute("ajax_login",
				CommUtil.null2Boolean(request.getParameter("ajax_login")));
		boolean flag = true;
		if (session.getAttribute("verify_code") != null) {
			String code = request.getParameter("code") != null ? request
					.getParameter("code").toUpperCase() : "";
			if (!session.getAttribute("verify_code").equals(code)) {
				flag = false;
				throw new CaptchaErrorException("验证码错误");
			}
		}
		if (!flag) {
			String username = obtainUsername(request);
			String password = "";// 验证码不正确清空密码禁止登陆
			username = username.trim();
			UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
					username, password);
			if ((session != null) || (getAllowSessionCreation())) {
				request.getSession().setAttribute(
						"SPRING_SECURITY_LAST_USERNAME",
						TextUtils.escapeEntities(username));
			}
			setDetails(request, authRequest);
			return getAuthenticationManager().authenticate(authRequest);
		} else {
			String username = "";
			if (CommUtil.null2Boolean(request.getParameter("encode"))) {
				username = CommUtil.decode(obtainUsername(request)) + ","
						+ login_role;
			} else
				username = obtainUsername(request) + "," + login_role;
			String password = obtainPassword(request);
			// 论坛登录，同时将论坛登录的信息保存到session中，页面中获取改js并执行
			if (this.configService.getSysConfig().isUc_bbs()) {
				String uc_login_js = this.uc_Login(
						CommUtil.decode(obtainUsername(request)), password);
				request.getSession(false).setAttribute("uc_login_js",
						uc_login_js);
			}
			username = username.trim();
			UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
					username, password);
			if ((session != null) || (getAllowSessionCreation())) {
				request.getSession().setAttribute(
						"SPRING_SECURITY_LAST_USERNAME",
						TextUtils.escapeEntities(username));
			}
			setDetails(request, authRequest);
			return getAuthenticationManager().authenticate(authRequest);
			// return super.attemptAuthentication(request);
		}
	}

	protected void onSuccessfulAuthentication(HttpServletRequest request,
			HttpServletResponse response, Authentication authResult)
			throws IOException {
		// TODO Auto-generated method stub
		request.getSession(false).removeAttribute("verify_code");

		super.onSuccessfulAuthentication(request, response, authResult);
	}

	protected void onUnsuccessfulAuthentication(HttpServletRequest request,
			HttpServletResponse response, AuthenticationException failed)
			throws IOException {
		// TODO Auto-generated method stub
		String uri = request.getRequestURI();
		super.onUnsuccessfulAuthentication(request, response, failed);
	}

	private static String uc_Login(String username, String pws) {
		String ucsynlogin = "";
		UCClient e = new UCClient();
		String result = e.uc_user_login(username, pws);
		LinkedList<String> rs = XMLHelper.uc_unserialize(result);
		if (rs.size() > 0) {
			int uid = Integer.parseInt(rs.get(0));
			String uname = rs.get(1);
			String password = rs.get(2);
			String email = rs.get(3);
			if (uid > 0) {
				// System.out.println("登录成功");
				// System.out.println(uname);
				// System.out.println(password);
				// System.out.println(email);
				ucsynlogin = e.uc_user_synlogin(uid);
				// System.out.println("登录成功" + ucsynlogin);
				// 本地登陆代码
				// TODO ... ....
			} else if (uid == -1) {
				System.out.println("用户不存在,或者被删除");
			} else if (uid == -2) {
				// System.out.println("密码错");
			} else {
				// System.out.println("未定义");
			}
		} else {
			// System.out.println("登录失败");
			// System.out.println(result);
		}
		return ucsynlogin;
	}
}
