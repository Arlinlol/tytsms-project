package com.iskyshop.pay.wechatpay.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * 功能描述：config常量辅助类
 * <p>
 * 版权所有：广州泰易淘网络科技有限公司
 * <p>
 * 未经本公司许可，不得以任何方式复制或使用本程序任何部分
 * 
 * @author Frank 新增日期：2015年3月13日 下午5:07:03
 * @author Frank 修改日期：2015年3月13日 下午5:07:03
 *
 */
public class ConfigContants {
	
	private static Log log = LogFactory.getLog(ConfigContants.class);
	
	private static Properties prop = null;
	
	public static final boolean IS_UPLOAD_SERVER_MODEL;
	
	public static   String UPLOAD_IMAGE_SERVER_PATH =null;
	
	public static   String UPLOAD_IMAGE_MIDDLE_NAME =null;
	
	public static   String GENERATOR_FILES_SERVER_PATH =null;
	
	public static   String GENERATOR_FILES_MIDDLE_NAME  =null;
	
	public static   String TYTSMS_WEB_SITE  =null;
	
	public static   String TYTSMS_WEB_SITE_PROTOCOL  =null;
	
	public static   String TYTSMS_GENERATOR_SERVICE = null;
	
	public static   String LUCENE_DIRECTORY = null;
	
	public static 	String TYTSMS_TEMPORARY_DIR = null;
	
	public static  	String TYTSMS_SCANNING_CMD = null;
	
	public static   String I18N_LANGUAGE = null;
	
	public static   String LOGISTICS_INTERFACE_URL = null;
	

	public static   String ELASTICSEARCH_CLUSTER_NAME = null;	
	public static   String ELASTICSEARCH_TRANSPORT_HOST = null;	
	public static   String ELASTICSEARCH_TRANSPORT_PORT = null;		
	public static   String ELASTICSEARCH_CLUSTER_LIST = null;
	
//	public static   String JS_CSS_V = null;
	
	
	
	static {
		InputStream is = ConfigContants.class.getClassLoader().getResourceAsStream("config.properties");
		try {
			prop = new Properties();
			prop.load(is);
		} catch (IOException e) {
			log.error(e);
		}
		IS_UPLOAD_SERVER_MODEL ="true".equals(prop.getProperty("IS_UPLOAD_SERVER_MODEL"))?true:false;
		UPLOAD_IMAGE_SERVER_PATH = prop.getProperty("UPLOAD_IMAGE_SERVER_PATH");
		UPLOAD_IMAGE_MIDDLE_NAME = prop.getProperty("UPLOAD_IMAGE_MIDDLE_NAME");
		GENERATOR_FILES_SERVER_PATH = prop.getProperty("GENERATOR_FILES_SERVER_PATH");
		GENERATOR_FILES_MIDDLE_NAME = prop.getProperty("GENERATOR_FILES_MIDDLE_NAME");
		TYTSMS_WEB_SITE = prop.getProperty("TYTSMS_WEB_SITE");
		TYTSMS_WEB_SITE_PROTOCOL = prop.getProperty("TYTSMS_WEB_SITE_PROTOCOL");
		TYTSMS_GENERATOR_SERVICE =  prop.getProperty("TYTSMS_GENERATOR_SERVICE");
		LUCENE_DIRECTORY = prop.getProperty("LUCENE_DIRECTORY");
		TYTSMS_TEMPORARY_DIR = prop.getProperty("TYTSMS_TEMPORARY_DIR");
		TYTSMS_SCANNING_CMD = prop.getProperty("TYTSMS_SCANNING_CMD");
		I18N_LANGUAGE = prop.getProperty("I18N_LANGUAGE");
//		JS_CSS_V =  prop.getProperty("JS_CSS_V");
		LOGISTICS_INTERFACE_URL = prop.getProperty("LOGISTICS_INTERFACE_URL");
		ELASTICSEARCH_CLUSTER_NAME = prop.getProperty("ELASTICSEARCH_CLUSTER_NAME");
		ELASTICSEARCH_TRANSPORT_HOST = prop.getProperty("ELASTICSEARCH_TRANSPORT_HOST");
		ELASTICSEARCH_TRANSPORT_PORT = prop.getProperty("ELASTICSEARCH_TRANSPORT_PORT");
		ELASTICSEARCH_CLUSTER_LIST = prop.getProperty("ELASTICSEARCH_CLUSTER_LIST");
	}
	
}
