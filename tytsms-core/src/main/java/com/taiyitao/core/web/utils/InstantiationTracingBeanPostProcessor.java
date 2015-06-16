package com.taiyitao.core.web.utils;


import java.io.File;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.context.WebApplicationContext;

import com.taiyitao.quartz.job.GenerateHomePageJob;

public class InstantiationTracingBeanPostProcessor implements ApplicationListener<ContextRefreshedEvent>{
	protected Logger _log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private EntityManagerFactory emf;//Spring Quartz 获取Hibernate lazy Bean时，出现no session异常的解决办法（参考：OpenEntityManagerInViewFilter）
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		  if(event.getApplicationContext().getParent() == null){
			  System.out.println(event.getApplicationContext().getDisplayName());
			  WebApplicationContext applicationContext = (WebApplicationContext) event.getApplicationContext();
				String path = applicationContext.getServletContext().getRealPath("")+File.separator;
				System.out.println("应用路径（容器启动监听器）-------------------------------------="+path);
				
				boolean participate = false;

				if (TransactionSynchronizationManager.hasResource(emf)) {
					// Do not modify the EntityManager: just set the participate flag.
					participate = true;
				}else {
					try {
						EntityManager em = emf.createEntityManager();
						TransactionSynchronizationManager.bindResource(emf, new EntityManagerHolder(em));
						_log.debug("Opening JPA EntityManager in OpenEntityManagerInViewFilter");
					}
					catch (PersistenceException ex) {
						throw new DataAccessResourceFailureException("Could not create JPA EntityManager", ex);
					}
				}
				
				try {
					GenerateHomePageJob job = (GenerateHomePageJob)applicationContext.getBean("generateHomePageJob");
					job.webappRoot = path;
					job.toHTML();
				} finally {
					if (!participate) {
						EntityManagerHolder emHolder = (EntityManagerHolder)
								TransactionSynchronizationManager.unbindResource(emf);
						_log.debug("Closing JPA EntityManager in OpenEntityManagerInViewFilter");
						EntityManagerFactoryUtils.closeEntityManager(emHolder.getEntityManager());
					}
				}
		  }
	}
}
