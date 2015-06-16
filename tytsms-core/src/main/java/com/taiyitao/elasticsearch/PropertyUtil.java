package com.taiyitao.elasticsearch;

import java.io.File;
import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertyUtil {
	private static Map<String, String> propertiesMap = null;

    static {  
        try {  
            String path = PropertyUtil.class.getResource("elasticsearch.properties").getPath();  
            File file = new File(path);  
            propertiesMap = new HashMap<String, String>();  
            if (file.getName().endsWith(".properties")) {  
                Properties prop = new Properties();  
                prop.load(new FileInputStream(file));  
                Enumeration<Object> keys = prop.keys();  
                while (keys.hasMoreElements()) {  
                    String key = keys.nextElement().toString();  
                    String value = prop.getProperty(key);  
                    propertiesMap.put(key, value);  
                }  
            } 
        } catch (Exception e) {  
        	 e.printStackTrace();  
        }  
    }  
	  
	    /** 
	     * 获取配置属性. 
	     *  
	     * @param name 
	     * @return String 
	     */  
	    public static String getProperty(String name) {  
	        return (String) propertiesMap.get(name);  
	    }  
	    
	    
	    public static void main(String[] args){
	    	System.out.println(PropertyUtil.getProperty("ELASTICSEARCH_CLUSTER_NAME"));
	    }
  
}
