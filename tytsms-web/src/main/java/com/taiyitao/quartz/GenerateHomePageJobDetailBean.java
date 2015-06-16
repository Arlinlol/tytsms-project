package com.taiyitao.quartz;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class GenerateHomePageJobDetailBean extends QuartzJobBean{

	protected Logger _log = LoggerFactory.getLogger(getClass());
	
	private String targetObject;
	
	private String targetMethod;
	
	private ApplicationContext applicationContext;
	
	private EntityManagerFactory emf;//Spring Quartz 获取Hibernate lazy Bean时，出现no session异常的解决办法（参考：OpenEntityManagerInViewFilter）
	
	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
		// TODO Auto-generated method stub
		System.out.println("开始执行job！！！");
		
		Object otargetObject = applicationContext.getBean(targetObject);
		Method method = null;
		boolean participate = false;

		if (TransactionSynchronizationManager.hasResource(emf)) {
			// Do not modify the EntityManager: just set the participate flag.
			participate = true;
		}
		else {
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
			method = otargetObject.getClass().getMethod(targetMethod);
			method.invoke(otargetObject);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		finally {
			if (!participate) {
				EntityManagerHolder emHolder = (EntityManagerHolder)
						TransactionSynchronizationManager.unbindResource(emf);
				_log.debug("Closing JPA EntityManager in OpenEntityManagerInViewFilter");
				EntityManagerFactoryUtils.closeEntityManager(emHolder.getEntityManager());
			}
		}
				
		
		_log.info("end execute Job -------------->>>>>>>>>>>>>>>>>>>");
	}

	public void setTargetObject(String targetObject) {
		this.targetObject = targetObject;
	}

	public void setTargetMethod(String targetMethod) {
		this.targetMethod = targetMethod;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public EntityManagerFactory getEmf() {
		return emf;
	}

	public void setEmf(EntityManagerFactory emf) {
		this.emf = emf;
	}

}