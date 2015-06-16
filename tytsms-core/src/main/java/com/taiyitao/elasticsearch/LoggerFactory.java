package com.taiyitao.elasticsearch;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;



public class LoggerFactory extends Logger{
	 protected LoggerFactory(String name) {  
	        super(name);  
	    }  
	  
	    /** 
	     * 新建实例 
	     *  
	     * @author Geloin 
	     * @param name 
	     *            名称 
	     * @return 建立的实例 
	     */  
	    public static Logger getInstance(String name) {  
	        Logger log = Logger.getLogger(name);  
	        log.setLevel(Level.ERROR);  
	        return log;  
	    }  
	  
	    /** 
	     * 新建实例，并指定level 
	     *  
	     * @author Geloin 
	     * @param name 
	     *            名称 
	     * @param level 
	     *            level 
	     * @return 建立的实例 
	     */  
	    public static Logger getInstance(String name, Level level) {  
	        Logger log = Logger.getLogger(name);  
	        log.setLevel(level);  
	        return log;  
	    }  
	  
//	    /** 
//	     * 根据类名新建实例 
//	     *  
//	     * @author Geloin 
//	     * @param clazz 
//	     *            类名 
//	     * @return 实例 
//	     */  
//	    public static <T> Logger getInstance(Class<T> clazz) {  
//	        Logger log = Logger.getLogger(clazz.getName());  
//	        log.setLevel(Level.ERROR);  
//	        return log;  
//	    }  
	  
	    /** 
	     * 根据类名新建实例，并指定level 
	     *  
	     * @author Geloin 
	     * @param clazz 
	     *            类 
	     * @param level 
	     *            level 
	     * @return 实例 
	     */  
	    public static <T> Logger getInstance(Class<T> clazz, Level level) {  
	        Logger log = Logger.getLogger(clazz.getName());  
	        log.setLevel(level);  
	        return log;  
	    }  
}
