package com.iskyshop.core.filter;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;
import org.springframework.web.util.HtmlUtils;

/**
 * 
 * 功能描述：系统html特殊字符过滤替换
 * <p>
 * 版权所有：广州泰易淘网络科技有限公司
 * <p>
 * 未经本公司许可，不得以任何方式复制或使用本程序任何部分
 * 
 * @author Frank 新增日期：2015年3月21日 上午10:28:45
 * @author Frank 修改日期：2015年3月21日 上午10:28:45
 *
 */
public class TytsmsHtmlCharacterFilter implements Filter {


	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;

		// 思路：得到提交数据，替换，重新设置进去
		// 使用包装对象来包裹原请求对象
		
	boolean isMultipart = ServletFileUpload.isMultipartContent(request);
	if (!isMultipart) {

		chain.doFilter(new HttpServletRequestWrapper(request) {

			@Override
			public String getParameter(String name) {
				if (StringUtils.isNotBlank(name)) {
					String[] names = new String[] { super.getParameter(name) };
					names = doFilter(names);
					return names[0];
				}
				return super.getParameter(name);
			}

			@Override
			public String[] getParameterValues(String name) {
				if (StringUtils.isNotBlank(name)) {
					String[] names = new String[] { super.getParameter(name) };
					names = doFilter(names);
					return names;
				}
				return super.getParameterValues(name);
			}

			private String[] doFilter(String[] names) {
					int i, length = names.length;
					for (i = 0; i < length; i++) {
						String name = names[i];
						if (StringUtils.isNotBlank(name)) {
							name=HtmlUtils.htmlEscape(name);
						}
						names[i] = name;
					}
						
					
				return names;
			}
		}, response);
	}else {
//		chain.doFilter(new DefaultMultipartHttpServletRequest(request) {
		chain.doFilter(new MultipartRequestWrapper(request) {

		}, response);
	}

	}

	@Override
	public void destroy() {

	}

}
