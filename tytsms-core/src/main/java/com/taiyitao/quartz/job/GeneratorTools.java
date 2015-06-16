package com.taiyitao.quartz.job;

import java.io.File;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iskyshop.foundation.domain.GeneratorType;
import com.iskyshop.pay.wechatpay.util.ConfigContants;
import com.iskyshop.pay.wechatpay.util.HttpTool;

@Component
public class GeneratorTools {
	@Autowired
	private GenerateHomePageJob generateHomePageJob;
	
	protected  Logger _log = LoggerFactory.getLogger(getClass());
	
	public void generator(HttpServletRequest request,GeneratorType type,String[] ids,String[] classIds,String msg){
		String path = request.getSession().getServletContext().getRealPath("")+File.separator;
		_log.info("###############"+msg+"###########################");
		generateHomePageJob.webappRoot=path;
		String generator = ConfigContants.TYTSMS_GENERATOR_SERVICE;
		StringBuffer buf = new StringBuffer();
		buf.append("type="+type.name());
		if(ids!=null && ids.length>0){
			buf.append("&ids="+StringUtils.join(ids));
		}
		if(classIds!=null && classIds.length>0){
			buf.append("&classIds="+StringUtils.join(classIds));
		}
		if(generator!=null && !generator.equals("")){
			String[] services = generator.split(",");
			for(String service:services){
				 HttpTool.sendPostUrl(service,buf.toString(), "UTF-8");
			}
		}else{
			_log.info("静态文件生成服务器配置为空，请确认config.properties文件中是否有配置该信息");
		}
	}
}
