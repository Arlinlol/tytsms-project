package com.iskyshop.pay.wechatpay.util;



import java.io.InputStream;
import java.util.Properties;
/**
 * 获取指定属性名的属性值
 * @author ZL
 * @version 1.0
 */
public class GetPropertiesValue {
	private static GetPropertiesValue getPropValueIstance; //本类自己实例
	private static Properties properties = new Properties();
	private static InputStream is;
	
	/**
	 * 获取本类对象(单例模式)
	 * @return 本类对象
	 */
	public static GetPropertiesValue getInstance(){
		if(getPropValueIstance == null){
			getPropValueIstance = new GetPropertiesValue();
		}
		return getPropValueIstance;
	}
	
	/**
	 * 返回properties文件中属性的值
	 * @param path properties文件路径
	 * @param PropertyName properties文件中的属性名
	 * @return properties文件中属性的值
	 */
	public static String getValue(String path, String PropertyName){
		try {
			is = GetPropertiesValue.class.getClassLoader().getResourceAsStream(path);
			properties.load(is);
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("异常:路径或文件名有误!路径前请不要加\"/\"");
		}
		String propertyValue = (String) properties.get(PropertyName);
		return propertyValue;
	}

}