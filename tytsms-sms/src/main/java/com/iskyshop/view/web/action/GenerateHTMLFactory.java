package com.iskyshop.view.web.action;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import com.iskyshop.pay.wechatpay.util.ConfigContants;

/**
 * 通用生成静态化页面工厂类
 * @author lukdennis
 *
 */
public class GenerateHTMLFactory {

	private static Log log = LogFactory.getLog(GenerateHTMLFactory.class);
	private static GenerateHTMLFactory instance;
	private static VelocityEngine velocityEngine = new VelocityEngine();
	private static Properties prop = new Properties();
	
	private GenerateHTMLFactory(){};
	
	static {
		InputStream is = ConfigContants.class.getClassLoader().getResourceAsStream("velocity.property");
		try {
			prop.load(is);
		} catch (IOException e) {
			log.error(e);
		}
	}
	
	/**
	 * 获取静态化页面工厂实例
	 * @return
	 */
	public static GenerateHTMLFactory getInstance() {
		if(instance == null) {
			instance = new GenerateHTMLFactory();
		}
		return instance;
	}
	
	/**
	 * 生成静态化文件，读取vm目录下的Velocity模版文件
	 * 
	 * @param dataMap			所有需要输出到vm模版文件的数据都封装入map中
	 * @param webappPath		webapp根目录
	 * @param templatePath		velocity.properties目录
	 * @param outputHTMLPath	html静态文件输出目录
	 */
	public void GenerateHTML(Map<String, Object> dataMap, String webappPath, String templatePath, String outputHTMLPath) {
		try{
			File tp = new File(webappPath + ConfigContants.GENERATOR_FILES_MIDDLE_NAME);
			if(!tp.exists()){
				tp.mkdirs();
			}
			prop.put(Velocity.FILE_RESOURCE_LOADER_PATH,tp.getAbsolutePath() );
			velocityEngine.init(prop);
			VelocityContext context = new VelocityContext();  
			Template template = velocityEngine.getTemplate(templatePath, "UTF-8"); 
			if(!dataMap.isEmpty()) {
				
				Set<String> keySet = dataMap.keySet();
				Iterator<String> keys = keySet.iterator();
				while(keys.hasNext()) {
					String key = keys.next();
					Object value = dataMap.get(key);
					context.put(key, value);  
				}
			}
			String outputFileName = outputHTMLPath + "/mainOutput-new.html";
			String replacedFileName = outputHTMLPath + "/mainOutput.html";
			File savefile = new File(outputFileName);  
			if(!savefile.getParentFile().exists()) savefile.getParentFile().mkdirs();  
			FileOutputStream outstream = new FileOutputStream(savefile);   
			OutputStreamWriter writer = new OutputStreamWriter(outstream,"UTF-8");  
			BufferedWriter bufferWriter = new BufferedWriter(writer);   
			template.merge(context, bufferWriter);  
			bufferWriter.flush();  
			outstream.close(); 
			bufferWriter.close(); 
			
			
			FileUtils.copyFile(new File(outputFileName), new File(replacedFileName));
			
		}catch(Exception e){
			log.error(e);
		}
	}
}
