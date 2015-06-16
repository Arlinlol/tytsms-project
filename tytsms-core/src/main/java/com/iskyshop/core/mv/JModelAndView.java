package com.iskyshop.core.mv;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.HttpInclude;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.UserConfig;
import com.iskyshop.pay.wechatpay.util.ConfigContants;

/**
 * 
 * <p>
 * Title: JModelAndView.java
 * </p>
 * 
 * <p>
 * Description: 顶级视图管理类，封装ModelAndView并进行系统扩展
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
public class JModelAndView extends ModelAndView {
	/**
	 * 普通视图，根据velocity配置文件的路径直接加载视图
	 * 
	 * @param viewName
	 *            视图名称
	 */
	public JModelAndView(String viewName) {
		super.setViewName(viewName);
	}

	/**
	 * 
	 * @param viewName
	 *            用户自定义的视图，可以添加任意路径
	 * @param request
	 */
	public JModelAndView(String viewName, SysConfig config, UserConfig uconfig,
			HttpServletRequest request, HttpServletResponse response) {
		String contextPath = request.getContextPath().equals("/") ? ""
				: request.getContextPath();
		String webPath = CommUtil.getURL(request);
		super.addObject("current_webPath", webPath);
		String port = request.getServerPort() == 80 ? "" : ":"
				+ CommUtil.null2Int(request.getServerPort());
		if (Globals.SSO_SIGN && config.isSecond_domain_open()
				&& !CommUtil.generic_domain(request).equals("localhost")
				&& !CommUtil.isIp(request.getServerName())) {
			webPath = "http://www." + CommUtil.generic_domain(request) + port
					+ contextPath;
		}
		super.setViewName(viewName);
		super.addObject("domainPath", CommUtil.generic_domain(request));
		if (config.getImageWebServer() != null
				&& !config.getImageWebServer().equals("")) {
			super.addObject("imageWebServer", config.getImageWebServer());
		} else {
			super.addObject("imageWebServer", webPath);
		}
		super.addObject("webPath", webPath);
		super.addObject("config", config);
		super.addObject("uconfig", uconfig);
		super.addObject("user", SecurityUserHolder.getCurrentUser());
		super.addObject("httpInclude", new HttpInclude(request, response));
		String query_url = "";
		if (request.getQueryString() != null
				&& !request.getQueryString().equals("")) {
			query_url = "?" + request.getQueryString();
		}
		super.addObject("current_url", request.getRequestURI() + query_url);
		boolean second_domain_view = false;
		String serverName = request.getServerName().toLowerCase();
		if (serverName.indexOf("www.") < 0 && serverName.indexOf(".") >= 0
				&& serverName.indexOf(".") != serverName.lastIndexOf(".")
				&& config.isSecond_domain_open()) {
			String secondDomain = serverName.substring(0,
					serverName.indexOf("."));
			second_domain_view = true;// 使用二级域名访问，相关js url需要处理，避免跨域
			super.addObject("secondDomain", secondDomain);
		}
		super.addObject("second_domain_view", second_domain_view);
		super.addObject("MIDDLE_NAME", ConfigContants.UPLOAD_IMAGE_MIDDLE_NAME);
		super.addObject("SITE_PROTOCOL",
				ConfigContants.TYTSMS_WEB_SITE_PROTOCOL);
		
		if (ConfigContants.TYTSMS_WEB_SITE_PROTOCOL != null
				&& "https".equals(ConfigContants.TYTSMS_WEB_SITE_PROTOCOL
						.trim()) && webPath != null
				&& webPath.split("://").length > 1) {
			super.addObject("httpsWebPath", "https://" + webPath.split("://")[1]);
		} else {
			super.addObject("httpsWebPath", webPath);
		}
	}

	/**
	 * 按指定路径加载视图，如不指定则系统默认路径加载
	 * 
	 * @param viewName
	 *            视图名称
	 * @param config
	 *            商城配置
	 * @param userPath
	 *            自定义路径，和type配合使用
	 * @param type
	 *            视图类型 0为后台，1为前台 大于1为自定义路径
	 */
	public JModelAndView(String viewName, SysConfig config, UserConfig uconfig,
			int type, HttpServletRequest request, HttpServletResponse response) {
		if (config.getSysLanguage() != null) {
			if (config.getSysLanguage().equals(Globals.DEFAULT_SYSTEM_LANGUAGE)) {
				if (type == 0) {
					super.setViewName(Globals.SYSTEM_MANAGE_PAGE_PATH
							+ viewName);
				}
				if (type == 1) {
					super.setViewName(Globals.SYSTEM_FORNT_PAGE_PATH + viewName);
				}
				if (type > 1) {
					super.setViewName(viewName);
				}

			} else {
				if (type == 0) {
					super.setViewName(Globals.DEFAULT_SYSTEM_PAGE_ROOT
							+ config.getSysLanguage() + "/system/" + viewName);
				}
				if (type == 1) {
					super.setViewName(Globals.DEFAULT_SYSTEM_PAGE_ROOT
							+ config.getSysLanguage() + "/shop/" + viewName);
				}
				if (type > 1) {
					super.setViewName(viewName);
				}
			}
		} else {
			super.setViewName(viewName);
		}
		super.addObject("CommUtil", new CommUtil());
		String contextPath = request.getContextPath().equals("/") ? ""
				: request.getContextPath();
		String webPath = CommUtil.getURL(request);
		String port = request.getServerPort() == 80 ? "" : ":"
				+ CommUtil.null2Int(request.getServerPort());
		super.addObject("current_webPath", webPath);
		if (Globals.SSO_SIGN && config.isSecond_domain_open()
				&& !CommUtil.generic_domain(request).equals("localhost")
				&& !CommUtil.isIp(request.getServerName())) {
			webPath = "http://www." + CommUtil.generic_domain(request) + port
					+ contextPath;
		}
		super.addObject("domainPath", CommUtil.generic_domain(request));
		super.addObject("webPath", webPath);
		if (config.getImageWebServer() != null
				&& !config.getImageWebServer().equals("")) {
			super.addObject("imageWebServer", config.getImageWebServer());
		} else {
			super.addObject("imageWebServer", webPath);
		}
		super.addObject("config", config);
		super.addObject("uconfig", uconfig);
		super.addObject("user", SecurityUserHolder.getCurrentUser());
		super.addObject("httpInclude", new HttpInclude(request, response));
		String query_url = "";
		if (request.getQueryString() != null
				&& !request.getQueryString().equals("")) {
			query_url = "?" + request.getQueryString();
		}
		super.addObject("current_url", request.getRequestURI() + query_url);
		boolean second_domain_view = false;
		String serverName = request.getServerName().toLowerCase();
		if (serverName.indexOf("www.") < 0 && serverName.indexOf(".") >= 0
				&& serverName.indexOf(".") != serverName.lastIndexOf(".")
				&& config.isSecond_domain_open()) {
			String secondDomain = serverName.substring(0,
					serverName.indexOf("."));
			second_domain_view = true;// 使用二级域名访问，相关js url需要处理，避免跨域
			super.addObject("secondDomain", secondDomain);
		}
		super.addObject("second_domain_view", second_domain_view);
		super.addObject("MIDDLE_NAME", ConfigContants.UPLOAD_IMAGE_MIDDLE_NAME);
		super.addObject("SITE_PROTOCOL",
				ConfigContants.TYTSMS_WEB_SITE_PROTOCOL);
		
		if (ConfigContants.TYTSMS_WEB_SITE_PROTOCOL != null
				&& "https".equals(ConfigContants.TYTSMS_WEB_SITE_PROTOCOL
						.trim()) && webPath != null
				&& webPath.split("://").length > 1) {
			super.addObject("httpsWebPath", "https://" + webPath.split("://")[1]);
		} else {
			super.addObject("httpsWebPath", webPath);
		}
	}

}
