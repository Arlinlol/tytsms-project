package com.iskyshop.core.tools;

import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.support.RequestContext;

public class I18nUtils {
	
	/**
	 * 动态参数国际化
	 * @param src
	 * @param param
	 * @return
	 */
	public static String i18n(HttpServletRequest request,String key,String...param){
		RequestContext requestContext = new RequestContext(request);
		String src = requestContext.getMessage(key);
		if(CommUtil.isNotNull(src)){
			 Object[] parameter = new Object[param.length]; 
			 for(int i=0;i<param.length;i++){
				 parameter[i]=param[i]; 
			 }
			 src =  MessageFormat.format(src, parameter);
		}
		return src;
	}
	
	public static void main(String[] args){
		String str = "我是{0}, 今年{1}岁, {2}{3}人!";
		//System.out.println(I18nUtils.i18n(str, "司冬雪","26","河北","保定"));
	}

}
