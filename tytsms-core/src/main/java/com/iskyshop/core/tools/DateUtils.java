package com.iskyshop.core.tools;


import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 
 * Copyright (c) 2014,泰易淘科技有限公司 
 * All rights reserved.
 *
 * 日期转换工具类
 * 
 * @author cty 创建日期：2014-8-20
 */
public class DateUtils {

	public final static String module = DateUtils.class.getName();
	
	/**
	 * 返回字符串格式 yyyy-MM-dd HH:mm:ss
	 * @param currentTime
	 * @return
	 */
	public static String getStringDate(Date currentTime) {
	   SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	   String dateString = formatter.format(currentTime);
	   return dateString;
	}
	
	/**
	 * 返回字符串格式 yyyy-MM-dd
	 * @param currentTime
	 * @return
	 */
	public static String getStringDateShort(Date currentTime) {
		   SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		   String dateString = formatter.format(currentTime);
		   return dateString;
	}
	
	
	
	/**
	 * 返回时间类型 yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static Date getNowDate() {
		   Date currentTime = new Date();
		   SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		   String dateString = formatter.format(currentTime);
		   ParsePosition pos = new ParsePosition(8);
		   Date currentTime_2 = formatter.parse(dateString, pos);
		   return currentTime_2;
	}
	
	/**
	 * 返回时间类型 yyyy-MM-dd
	 * @return
	 */
	public static Date getDate(String dateString) {
	    Date currentTime = null;
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	    try {
		   currentTime = formatter.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		} 
		return currentTime;
	}
	
	/**
	 * 返回时间类型 yyyy-MM-dd
	 * @return
	 */
	public static Date getDateTime(String dateString) {
	    Date currentTime = null;
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	    try {
		   currentTime = formatter.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		} 
		return currentTime;
	}
	
	/**
	 * 给日期累加 天数 
	 * @param dateTime
	 * @param num
	 * @return
	 */
	public static String getDateDay(String dateTime,int num){
		SimpleDateFormat format = new   SimpleDateFormat("yyyy-MM-dd hh:mm:ss");  
		try {
		   Date dd = format.parse(dateTime);
		   Calendar  calendar = Calendar.getInstance();
		   calendar.setTime(dd);
		   calendar.add(Calendar.DAY_OF_MONTH, num);  
		   return format.format(calendar.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * 返回时间类型 yyyy-MM-dd
	 * @return
	 */
	public static Date getObjectDate(Object dateString) {
		Date  currentTime = null;
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	    try {
	    	currentTime = formatter.parse(dateString.toString());
		} catch (ParseException e) {
			e.printStackTrace();
		} 
		return currentTime;
	}
	
	/**
	 * 返回sql时间类型 yyyy-MM-dd
	 * @param dateString
	 * @return
	 */
	public static java.sql.Date getSqlDate(String dateString){
		java.sql.Date date = java.sql.Date.valueOf(dateString);
		return date;
	}
	
	/**
	 * 返回sql时间类型 yyyy-MM-dd
	 * @param dateString
	 * @return
	 */
	public static java.sql.Date getCurSqlDate(){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		java.sql.Date date = java.sql.Date.valueOf(formatter.format(new Date()));
		return date;
	}
	
	/**
	 * 获取当前系统时间的时分秒
	 * getStringHHmmss
	 * @param date
	 * @return
	 * String
	 */
	public static String getStringHHmmss(Date date){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(date);
		return dateString.substring(dateString.lastIndexOf(' ')+1,dateString.length());
	}
	
	/**
	 * 获取当前系统时间
	 * getString
	 * @param date
	 * @return
	 * String
	 */
	public static String getString(Date date){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		return formatter.format(date);
	}
	
	/**
	 * 返回字符串格式 yyyy-MM-dd HH:mm:ss
	 * @param currentTime
	 * @return
	 */
	public static String getStringDate(Timestamp currentTime) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(currentTime);
		return dateString;
	}
	
	/**
	 * 返回字符串格式 yyyy-MM-dd
	 * @param currentTime
	 * @return
	 */
	public static String getStringDateShort(Timestamp currentTime) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = formatter.format(currentTime);
		return dateString;
	}
	
	/**
	 * 返回指定格式的时间格式
	 * getString
	 * @param currentTime
	 * @param pattern
	 * @return
	 * String
	 */
	public static String getStringDateFormat(Timestamp currentTime,String pattern) {
		
		if(pattern==null||"".equals(pattern)){return "";}
		SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		String dateString = formatter.format(currentTime);
		return dateString;
	}
	
	public static Timestamp getStringToTimestamp(String dateStr){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		  format.setLenient(false);
		  try {
			  return new Timestamp(format.parse(dateStr).getTime());
		  } catch (ParseException e) {
		   e.printStackTrace();
		  }
		  return null;
	}
	
	/**
	 * 返回时间类型 yyyy-MM-dd
	 * @return
	 */
	public static Date getCurDate() {
	    Date currentTime = null;
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	    try {
		   currentTime = formatter.parse(formatter.format(new Date()));
		} catch (ParseException e) {
			e.printStackTrace();
		} 
		return currentTime;
	}
	
	
	/** 
     * method 将字符串类型的日期转换为一个timestamp（时间戳记java.sql.Timestamp） 
     * @param dateString 
     *            需要转换为timestamp的字符串 
     * @return dataTime timestamp 
     */  
	 public final static java.sql.Timestamp string2Time(String dateString)  
	            throws java.text.ParseException {  
	        DateFormat dateFormat;  
	        dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm");  
	        dateFormat.setLenient(false);  
	        java.util.Date timeDate = dateFormat.parse(dateString);// util类型  
	        java.sql.Timestamp dateTime = new java.sql.Timestamp(timeDate.getTime());// Timestamp类型,timeDate.getTime()返回一个long型  
	        return dateTime;  
	 }  
		/**
		 * 两个时间之间的天数
		 * 
		 * @param date1
		 * @param date2
		 * @return
		 */
		public static long getDays(String date1, String date2) {
			if (date1 == null || date1.equals(""))
				return 0;
			if (date2 == null || date2.equals(""))
				return 0;
			// 转换为标准时间
			SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
			java.util.Date date = null;
			java.util.Date mydate = null;
			try {
				date = myFormatter.parse(date1);
				mydate = myFormatter.parse(date2);
			} catch (Exception e) {
			}
			long day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000);
			System.out.println("day:"+day);
			return day;
		}
}
