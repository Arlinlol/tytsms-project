package com.iskyshop.core.zip;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
* <p>Title: CompressionFilter.java</p>

* <p>Description: 网页压缩过滤器，结合web.xml配置实现网页压缩，该过滤器在实际应用中可略去，web服务器均可以配置网页压缩功能</p>

* <p>Copyright: Copyright (c) 2014</p>

* <p>Company: 沈阳网之商科技有限公司 www.iskyshop.com</p>

* @author erikzhang

* @date 2014-4-24

* @version iskyshop_b2b2c 1.0
 */
public class CompressionFilter implements Filter {

	protected Log log = LogFactory.getFactory().getInstance(
			this.getClass().getName());

	@SuppressWarnings("unchecked")
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		boolean compress = false;
		if (request instanceof HttpServletRequest) {
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			Enumeration headers = httpRequest.getHeaders("Accept-Encoding");
			while (headers.hasMoreElements()) {
				String value = (String) headers.nextElement();
				if (value.indexOf("gzip") != -1) {
					compress = true;
				}
			}
		}

		if (compress) {// 如果浏览器支持则压缩
			HttpServletResponse httpResponse = (HttpServletResponse) response;
			httpResponse.addHeader("Content-Encoding", "gzip");
			CompressionResponse compressionResponse = new CompressionResponse(
					httpResponse);
			chain.doFilter(request, compressionResponse);
			compressionResponse.close();
		} else {// 如果浏览器不支持则不压缩
			chain.doFilter(request, response);
		}

	}

	public void init(FilterConfig config) throws ServletException {

	}

	public void destroy() {
	}

}
