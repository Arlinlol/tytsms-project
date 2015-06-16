package com.iskyshop.core.filter;

import java.io.IOException;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.ui.logout.LogoutFilter;
import org.springframework.security.ui.logout.LogoutHandler;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.SysLog;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.ISysLogService;
import com.iskyshop.foundation.service.IUserService;
/**
 * 
* <p>Title: NorLogoutFilter.java</p>

* <p>Description: SpringSecurity用户退出过滤器，重写LogoutFilter，用来记录用户退出信息，及清除用户登录时保存的相关session信息</p>

* <p>Copyright: Copyright (c) 2014</p>

* <p>Company: 沈阳网之商科技有限公司 www.iskyshop.com</p>

* @author erikzhang

* @date 2014-4-24

* @version iskyshop_b2b2c 1.0
 */
public class NorLogoutFilter extends LogoutFilter {
	@Autowired
	private ISysLogService sysLogService;
	@Autowired
	private IUserService userService;

	public void saveLog(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		String iskyshop_view_type = CommUtil.null2String(request
				.getParameter("iskyshop_view_type"));
		session.setAttribute("iskyshop_view_type", iskyshop_view_type);
		User u = (User) session.getAttribute("user");
		if (u != null) {
			User user = this.userService.getObjById(u.getId());
			/*user.setLastLoginDate(new Date());
			user.setLastLoginIp(CommUtil.getIpAddr(request));
			this.userService.update(user);*/
			SysLog log = new SysLog();
			log.setAddTime(new Date());
			log.setContent(user.getTrueName() + "于"
					+ CommUtil.formatTime("yyyy-MM-dd HH:mm:ss", new Date())
					+ "退出系统");
			log.setTitle("用户退出");
			log.setType(0);
			log.setUser(user);
			log.setIp(CommUtil.getIpAddr(request));
			this.sysLogService.save(log);
		}
	}

	public NorLogoutFilter(String logoutSuccessUrl, LogoutHandler[] handlers) {
		super(logoutSuccessUrl, handlers);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void doFilterHttp(HttpServletRequest request,
			HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if (requiresLogout(request, response)) {
			HttpSession session = request.getSession(false);
			if (null != session) {
				saveLog(request);
			}
		}
		super.doFilterHttp(request, response, chain);
	}

	@Override
	protected boolean requiresLogout(HttpServletRequest request,
			HttpServletResponse response) {
		return super.requiresLogout(request, response);
	}

	@Override
	protected String determineTargetUrl(HttpServletRequest request,
			HttpServletResponse response) {
		return super.determineTargetUrl(request, response);
	}

	@Override
	protected void sendRedirect(HttpServletRequest request,
			HttpServletResponse response, String url) throws IOException {
		super.sendRedirect(request, response, url);
	}

	@Override
	public void setFilterProcessesUrl(String filterProcessesUrl) {
		super.setFilterProcessesUrl(filterProcessesUrl);
	}

	@Override
	protected String getLogoutSuccessUrl() {
		return super.getLogoutSuccessUrl();
	}

	@Override
	protected String getFilterProcessesUrl() {
		return super.getFilterProcessesUrl();
	}

	@Override
	public void setUseRelativeContext(boolean useRelativeContext) {
		super.setUseRelativeContext(useRelativeContext);
	}

	@Override
	public int getOrder() {
		return super.getOrder();
	}
}
