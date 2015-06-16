package com.iskyshop.core.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserService;

/**
 * 
* <p>Title: SecondDomainFilter.java</p>

* <p>Description: 二级域名单点登录过滤器 </p>

* <p>Copyright: Copyright (c) 2014</p>

* <p>Company: 沈阳网之商科技有限公司 www.iskyshop.com</p>

* @author erikzhang

* @date 2014-4-24

* @version iskyshop_b2b2c 1.0
 */
@Component
public class SecondDomainFilter implements Filter {
	@Autowired
	private IUserService userService;
	@Autowired
	private ISysConfigService configService;

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		// TODO Auto-generated method stub
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		if (this.configService.getSysConfig().isSecond_domain_open()
				&& Globals.SSO_SIGN) {
			Cookie[] cookies = request.getCookies();
			String id = "";
			if (cookies != null) {
				for (Cookie cookie : cookies) {
					if (cookie.getName().equals("iskyshop_user_session")) {
						id = CommUtil.null2String(cookie.getValue());
					}
				}
				User user = this.userService.getObjById(CommUtil.null2Long(id));
				if (user != null)
					request.getSession(false).setAttribute("user", user);
			}
		}
		chain.doFilter(req, res);
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		// TODO Auto-generated method stub

	}

}
