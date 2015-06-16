package com.iskyshop.core.security.interceptor;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.ConfigAttributeDefinition;
import org.springframework.security.ConfigAttributeEditor;
import org.springframework.security.intercept.web.FilterInvocation;
import org.springframework.security.intercept.web.FilterInvocationDefinitionSource;
import org.springframework.security.util.AntUrlPathMatcher;
import org.springframework.security.util.RegexUrlPathMatcher;
import org.springframework.security.util.UrlMatcher;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.tools.CommUtil;

/**
 * 
 * <p>
 * Title: SecureResourceFilterInvocationDefinitionSource.java
 * </p>
 * 
 * <p>
 * Description:系統权限核心管理类，使用SpringSecurity完成角色、权限拦截
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
public class SecureResourceFilterInvocationDefinitionSource implements
		FilterInvocationDefinitionSource, InitializingBean {

	private UrlMatcher urlMatcher;

	private boolean useAntPath = true;

	private boolean lowercaseComparisons = true;

	/**
	 * @param useAntPath
	 *            the useAntPath to set
	 */
	public void setUseAntPath(boolean useAntPath) {
		this.useAntPath = useAntPath;
	}

	/**
	 * @param lowercaseComparisons
	 */
	public void setLowercaseComparisons(boolean lowercaseComparisons) {
		this.lowercaseComparisons = lowercaseComparisons;
	}

	public void afterPropertiesSet() throws Exception {

		// default url matcher will be RegexUrlPathMatcher
		this.urlMatcher = new RegexUrlPathMatcher();

		if (useAntPath) { // change the implementation if required
			this.urlMatcher = new AntUrlPathMatcher();
		}

		// Only change from the defaults if the attribute has been set
		if ("true".equals(lowercaseComparisons)) {
			if (!this.useAntPath) {
				((RegexUrlPathMatcher) this.urlMatcher)
						.setRequiresLowerCaseUrl(true);
			}
		} else if ("false".equals(lowercaseComparisons)) {
			if (this.useAntPath) {
				((AntUrlPathMatcher) this.urlMatcher)
						.setRequiresLowerCaseUrl(false);
			}
		}

	}

	public ConfigAttributeDefinition getAttributes(Object filter)
			throws IllegalArgumentException {
		FilterInvocation filterInvocation = (FilterInvocation) filter;
		String requestURI = filterInvocation.getRequestUrl();
		if (requestURI.indexOf("login.htm") < 0) {
			Map<String, String> urlAuthorities = this
					.getUrlAuthorities(filterInvocation);
			// System.out.println("当前用户为:" +
			// SecurityUserHolder.getCurrentUser());
			String grantedAuthorities = null;
			for (Iterator<Map.Entry<String, String>> iter = urlAuthorities
					.entrySet().iterator(); iter.hasNext();) {
				Map.Entry<String, String> entry = iter.next();
				String url = entry.getKey();
				// System.out.println("requestURI:" + requestURI + "权限值为："
				// + entry.getKey() + "匹配为："
				// + urlMatcher.pathMatchesUrl(url, requestURI));
				if (!CommUtil.null2String(url).equals("")
						&& urlMatcher.pathMatchesUrl(url, requestURI)) {
					grantedAuthorities = entry.getValue();
					break;
				}

			}
			// System.out.println("拦截器：" + grantedAuthorities);
			if (grantedAuthorities != null) {
				ConfigAttributeEditor configAttrEditor = new ConfigAttributeEditor();
				configAttrEditor.setAsText(grantedAuthorities);
				return (ConfigAttributeDefinition) configAttrEditor.getValue();
			}
		} else {
			if (requestURI.indexOf("login.htm") < 0) {
				ConfigAttributeEditor configAttrEditor = new ConfigAttributeEditor();
				configAttrEditor.setAsText("domain_error");
				filterInvocation.getHttpRequest().getSession()
						.setAttribute("domain_error", true);
				return (ConfigAttributeDefinition) configAttrEditor.getValue();
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.intercept.ObjectDefinitionSource#
	 * getConfigAttributeDefinitions()
	 */
	@SuppressWarnings("unchecked")
	public Collection getConfigAttributeDefinitions() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.security.intercept.ObjectDefinitionSource#supports
	 * (java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public boolean supports(Class clazz) {
		return true;
	}

	/**
	 * 
	 * @param filterInvocation
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String, String> getUrlAuthorities(
			FilterInvocation filterInvocation) {
		ServletContext servletContext = filterInvocation.getHttpRequest()
				.getSession().getServletContext();
		return (Map<String, String>) servletContext
				.getAttribute("urlAuthorities");
	}

}
