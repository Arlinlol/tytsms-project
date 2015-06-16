package com.iskyshop.core.ehcache;

import java.util.Enumeration;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.constructs.blocking.LockTimeoutException;
import net.sf.ehcache.constructs.web.AlreadyCommittedException;
import net.sf.ehcache.constructs.web.AlreadyGzippedException;
import net.sf.ehcache.constructs.web.filter.FilterNonReentrantException;
import net.sf.ehcache.constructs.web.filter.SimplePageFragmentCachingFilter;

import org.apache.commons.lang.StringUtils;

import com.iskyshop.core.tools.CommUtil;

/**
 * 
* <p>Title: PageCacheFiler.java</p>

* <p>Description:Eacache文件缓存处理过滤器，系统相对固定页面及资源文件，纳入缓存管理，避免每次都重复加载资源文件 </p>

* <p>Copyright: Copyright (c) 2014</p>

* <p>Company: 沈阳网之商科技有限公司 www.iskyshop.com</p>

* @author erikzhang

* @date 2014-4-24

* @version iskyshop_b2b2c 1.0
 */
public class PageCacheFiler extends SimplePageFragmentCachingFilter {
	private final static String FILTER_URL_PATTERNS = "patterns";
	private static String[] cacheURLs;

	private void init() throws CacheException {
		String patterns = filterConfig.getInitParameter(FILTER_URL_PATTERNS);
		patterns = "/floor.htm,/advert_invoke.htm,error.css,goods.css,groupbuy.css,index.css,integral.css,public.css,seller.css,sparegoods.css,user_phptp.css,user.css,window.css,phone.css,jcarousellite_1.0.1.min.js,jquery.ad-gallery.js,jquery.bigcolorpicker.js,jquery.cookie.js,jquery.fancybox-1.3.4.pack.js,jquery.form.js,jquery.jqzoom-core.js,jquery.KinSlideshow.min.js,jquery.lazyload.js,jquery.metadata.js,jquery.poshytip.min.js,jquery.rating.pack.js,jquery.shop.base.js,jquery.shop.common.js,jquery.shop.edit.js,jquery.validate.min.js,jquery.zh.cn.js,jquery-1.6.2.js,jquery-ui-1.8.21.js,swfobject.js,swfupload.js,swfupload.queue.js";
		cacheURLs = StringUtils.split(patterns, ",");
	}

	@Override
	protected void doFilter(final HttpServletRequest request,

	final HttpServletResponse response, final FilterChain chain)

	throws AlreadyGzippedException, AlreadyCommittedException,

	FilterNonReentrantException, LockTimeoutException, Exception {
		if (cacheURLs == null) {
			init();
		}
		String url = request.getRequestURI();
		String include_url = CommUtil.null2String(request
				.getAttribute("javax.servlet.include.request_uri"));
		boolean flag = false;
		if (cacheURLs != null && cacheURLs.length > 0) {
			for (String cacheURL : cacheURLs) {
				if (include_url.trim().equals("")) {
					if (url.contains(cacheURL.trim())) {
						flag = true;
						break;
					}
				} else {
					if (include_url.contains(cacheURL.trim())) {
						flag = true;
						break;
					}
				}

			}
		}
		// 如果包含我们要缓存的url 就缓存该页面，否则执行正常的页面转向
		if (flag) {
			super.doFilter(request, response, chain);
		} else {
			chain.doFilter(request, response);
		}
	}

	@SuppressWarnings("unchecked")
	private boolean headerContains(final HttpServletRequest request,
			final String header, final String value) {
		logRequestHeaders(request);
		final Enumeration accepted = request.getHeaders(header);
		while (accepted.hasMoreElements()) {
			final String headerValue = (String) accepted.nextElement();
			if (headerValue.indexOf(value) != -1) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @see net.sf.ehcache.constructs.web.filter.Filter#acceptsGzipEncoding(javax.servlet.http.HttpServletRequest)
	 * 
	 *      <b>function:</b> 兼容ie6/7 gzip压缩
	 * 
	 * @author erikzhang
	 * 
	 * @createDate 2012-7-4 上午11:07:11
	 * 
	 */

	@Override
	protected boolean acceptsGzipEncoding(HttpServletRequest request) {
		boolean ie6 = headerContains(request, "User-Agent", "MSIE 6.0");
		boolean ie7 = headerContains(request, "User-Agent", "MSIE 7.0");
		return acceptsEncoding(request, "gzip") || ie6 || ie7;
	}

	/**
	 * 这个方法非常重要，重写计算缓存key的方法，没有该方法include的url值会出错
	 */
	@Override
	protected String calculateKey(HttpServletRequest httpRequest) {
		// TODO Auto-generated method stub
		String url = httpRequest.getRequestURI();
		String include_url = CommUtil.null2String(httpRequest
				.getAttribute("javax.servlet.include.request_uri"));
		StringBuffer stringBuffer = new StringBuffer();
		if (include_url.equals("")) {
			stringBuffer.append(httpRequest.getRequestURI()).append(
					httpRequest.getQueryString());
			String key = stringBuffer.toString();
			return key;
		} else {
			stringBuffer
					.append(CommUtil.null2String(httpRequest
							.getAttribute("javax.servlet.include.request_uri")))
					.append(CommUtil.null2String(httpRequest
							.getAttribute("javax.serlvet.include.query_string")));
			String key = stringBuffer.toString();
			return key;
		}
	}
}
