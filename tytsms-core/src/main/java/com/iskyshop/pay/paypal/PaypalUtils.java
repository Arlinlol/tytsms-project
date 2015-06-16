package com.iskyshop.pay.paypal;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

public class PaypalUtils {

	public final static String getPath(HttpServletRequest request, String path) {
		StringBuffer url = new StringBuffer();
		String s = request.getProtocol();
		url.append(s.substring(0, s.indexOf('/')).toLowerCase());
		url.append("://");
		url.append(request.getServerName());
		url.append(":");
		url.append(request.getServerPort());
		url.append(request.getContextPath());
		if (path.charAt(0) != '/') {
			url.append("/");
		}
		url.append(path);
		return url.toString();
	}

	public final static String encode(String value) {
		try {
			value = URLEncoder.encode(value, "UTF-8");
		} catch (UnsupportedEncodingException e) {
		}
		return value;
	}

	public final static Properties execute(PostMethod post) throws Exception {
		try {
			Properties props = new Properties();
			HttpClient client = new HttpClient();
			int statusCode = client.executeMethod(post);
			if (statusCode < 200 || statusCode >= 300) {
				throw new Exception(
						"HTTP request failed: response status code '"
								+ statusCode + "' received where 2xx expected");
			}
			props.load(post.getResponseBodyAsStream());
			return props;
		} finally {
			post.releaseConnection();
		}
	}
}