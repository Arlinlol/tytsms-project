/*package com.taiyitao.core.web.utils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.taiyitao.quartz.job.GenerateHomePageJob;

public class InitListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		// TODO Auto-generated method stub
		WebApplicationContext applicationContext =  WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext());
		String path = applicationContext.getServletContext().getRealPath("/");
		System.out.println("应用路径-------------------------------------="+path);
		
		GenerateHomePageJob job = (GenerateHomePageJob)applicationContext.getBean("generateHomePageJob");
		job.webappRoot = path;
		job.toHTML();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Auto-generated method stub
		
	}

}*/
