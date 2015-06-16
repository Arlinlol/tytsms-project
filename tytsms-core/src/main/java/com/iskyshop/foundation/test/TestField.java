package com.iskyshop.foundation.test;

import java.beans.PropertyDescriptor;

import com.easyjf.beans.BeanUtils;
import com.easyjf.beans.BeanWrapper;
import com.iskyshop.core.tools.CommUtil;

public class TestField {

	/**
	 * @param args
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws ClassNotFoundException {
		// TODO Auto-generated method stub
		String field = "store.grade";
		if (field.indexOf(".") > 0) {
			Class entity = Class.forName("com.iskyshop.domain."
					+ CommUtil.first2upper(field.substring(
							field.indexOf("_") + 1, field.indexOf("."))));
			// System.out.println(entity);
			String propertyName = field.substring(field.indexOf(".") + 1);
			System.out.println("属性值:"+propertyName);
			BeanWrapper entity_wrapper = new BeanWrapper(entity);
			java.beans.PropertyDescriptor[] entity_propertys = entity_wrapper
					.getPropertyDescriptors();
			for (PropertyDescriptor pd : entity_propertys) {
				if(pd.getName().equals(propertyName)){
					System.out.println(pd.getName());
				}
				//System.out.println(pd.getName());
			}
		}
	}

}
