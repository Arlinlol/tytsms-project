package com.iskyshop.core.tools;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.ModelAndView;

import com.easyjf.beans.BeanUtils;
import com.easyjf.beans.BeanWrapper;
import com.iskyshop.core.annotation.Lock;
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.query.QueryObject;
/**
 * 
* <p>Title: WebForm.java</p>

* <p>Description: 表单对象和POJO转换类，该类可以将表单对象转换为POJO，也可以将查询表单转为查询对象QueryObject</p>

* <p>Copyright: Copyright (c) 2014</p>

* <p>Company: 沈阳网之商科技有限公司 www.iskyshop.com</p>

* @author erikzhang

* @date 2014-4-24

* @version iskyshop_b2b2c 1.0
 */
public class WebForm {
	/**
	 * 将表单中的对象载入对应的实体中，该方法可以处理所有基础类型值转换，但不包含使用@Lock标签保护的字段
	 * 
	 * @param map
	 *            通过toPO封装的MAP值
	 * @param obj
	 *            通过toPO封装的泛类型，可以是存在的obj，也可以是第一次新建的obj
	 * 
	 */
	public void Map2Obj(List<Map> maps, Object obj) {
		BeanWrapper wrapper = new BeanWrapper(obj);
		java.beans.PropertyDescriptor[] propertys = wrapper
				.getPropertyDescriptors();
		for (int i = 0; i < propertys.length; i++) {
			String name = propertys[i].getName();
			if (!wrapper.isWritableProperty(name)
					|| propertys[i].getWriteMethod() == null)
				continue;
			Object propertyValue = null;
			for (int j = 0; j < maps.size(); j++) {
				Map map = (Map) maps.get(j);
				Iterator keys = map.keySet().iterator();
				while (keys.hasNext()) {
					String key = (String) keys.next();
					if (key.equals(propertys[i].getName())) {
						Lock lock = null;
						lock = propertys[i].getWriteMethod().getAnnotation(
								Lock.class);// 获取set方法是否有Lock注解
						if (lock == null) {// 进一步获取字段上是否有注解
							java.lang.reflect.Field f;
							try {
								f = propertys[i].getWriteMethod()
										.getDeclaringClass().getDeclaredField(
												name);
								lock = f.getAnnotation(Lock.class);
							} catch (SecurityException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (NoSuchFieldException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						if (lock == null) {
							try {
								propertyValue = BeanUtils.convertType(map
										.get(key), propertys[i]
										.getPropertyType());
							} catch (Exception e) {
								if (propertys[i].getPropertyType().toString()
										.equals("int")) {
									propertyValue = 0;
								}
								if (propertys[i].getPropertyType().toString()
										.toLowerCase().indexOf("boolean") >= 0) {
									propertyValue = false;
								}
							}
							// System.out.println("复制值：" + propertyValue +
							// ",复制到："+ propertys[i].getName());
							wrapper.setPropertyValue(propertys[i].getName(),
									propertyValue);
						}
					}
				}
			}
		}
	}

	/**
	 * 把form表单中的数据转换成classType类对象，用户第一次新建对象
	 * 
	 * @param <T>
	 *            泛类型，和classType对应
	 * @param request
	 *            用户请求，封装表单数据
	 * @param classType
	 *            对象类型
	 * @return
	 */
	public <T> T toPo(HttpServletRequest request, Class<T> classType) {
		T obj = null;
		try {
			obj = classType.newInstance();
			Map map = request.getParameterMap();
			java.util.Enumeration enum1 = request.getParameterNames();
			List<Map> maps = new ArrayList<Map>();
			while (enum1.hasMoreElements()) {
				String paramName = (String) enum1.nextElement();
				String value = request.getParameter(paramName);
				Map m1 = new HashMap();
				m1.put(paramName, value);
				maps.add(m1);
			}
			Map2Obj(maps, obj);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}

	/**
	 * 把form表单中的数据转换成classType类对象，用在对象更新
	 * 
	 * @param request
	 *            前台请求，封装表单数据
	 * @param obj
	 *            数据库中已经存在的obj对象，泛类型
	 * @return
	 */
	public Object toPo(HttpServletRequest request, Object obj) {
		try {
			Map map = request.getParameterMap();
			java.util.Enumeration enum1 = request.getParameterNames();
			List<Map> maps = new ArrayList<Map>();
			while (enum1.hasMoreElements()) {
				String paramName = (String) enum1.nextElement();
				String value = request.getParameter(paramName);
				Map m1 = new HashMap();
				m1.put(paramName, value);
				maps.add(m1);
			}
			Map2Obj(maps, obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}

	/**
	 * 把查询表单转换为classType查询对象,满足基本类型及多对一类型转换查询
	 * 
	 * @param request
	 * @param qo
	 *            查询对象QO
	 * @param classType
	 *            查询对象
	 * @param mv
	 *            返回的视图
	 *            如：Store中有属性id,grade，其中id为Long类型，grade为StoreGrade类型，前台表单名称分别为:id,store.grade.xxx，store表示查询对象，grade表示多对一查询的一方对象名，id表示多对一查询一方的查询字段
	 */
	public void toQueryPo(HttpServletRequest request, QueryObject qo,
			Class classType, ModelAndView mv) {
		List<SysMap> sms = new ArrayList<SysMap>();
		try {
			Object obj = classType.newInstance();
			BeanWrapper wrapper = new BeanWrapper(obj);
			java.beans.PropertyDescriptor[] propertys = wrapper
					.getPropertyDescriptors();
			Map map = request.getParameterMap();
			List<Map> maps = new ArrayList<Map>();
			java.util.Enumeration enum1 = request.getParameterNames();
			while (enum1.hasMoreElements()) {
				String paramName = (String) enum1.nextElement();
				String value = request.getParameter(paramName);
				Map m1 = new HashMap();
				m1.put(paramName, value);
				maps.add(m1);
			}
			for (Map m : maps) {
				Iterator keyes = m.keySet().iterator();
				while (keyes.hasNext()) {
					String field = (String) keyes.next();
					if (field.indexOf("q_") == 0) {
						Object para = null;
						for (PropertyDescriptor pd : propertys) {
							if (pd.getName().equals(field.substring(2))) {
								para = BeanUtils.convertType(map.get(field), pd
										.getPropertyType());
							}
						}
						if (field.indexOf(".") > 0) {
							Class entity = Class.forName("com.iskyshop.domain."
									+ CommUtil.first2upper(field.substring(2,
											field.indexOf("."))));
							// System.out.println(entity);
							String propertyName = field.substring(field
									.indexOf(".") + 1, field.lastIndexOf("."));
							BeanWrapper entity_wrapper = new BeanWrapper(entity);
							PropertyDescriptor[] entity_propertys = entity_wrapper
									.getPropertyDescriptors();
							for (PropertyDescriptor pd : entity_propertys) {
								if (pd.getName().equals(propertyName)) {
									// 进一步计算字段类型
									BeanWrapper many_to_one_entity = new BeanWrapper(
											pd.getPropertyType());
									PropertyDescriptor[] many_to_one_entity_propertys = many_to_one_entity
											.getPropertyDescriptors();
									String many_to_one_propertyname = field
											.substring(field.lastIndexOf(".") + 1);
									for (PropertyDescriptor many_to_one_pd : entity_propertys) {
										if (many_to_one_pd.getName().equals(
												many_to_one_propertyname)) {
											para = BeanUtils.convertType(map
													.get(field), many_to_one_pd
													.getPropertyType());
										}
									}
								}
							}
						}
						boolean add = false;
						for (PropertyDescriptor pd : propertys) {
							if (field.indexOf(".") < 0) {
								if (pd.getName().equals(field.substring(2))) {
									add = true;
								}
							} else {
								if (pd.getName().equals(
										field.subSequence(
												field.indexOf(".") + 1, field
														.lastIndexOf(".")))) {
									add = true;
								}
							}
						}
						if (add && para != null && !para.equals("")) {
							if (field.indexOf(".") < 0) {
								if (para.getClass().toString().endsWith(
										"String")) {
									String expression = "like";
									qo.addQuery("obj." + field.substring(2),
											new SysMap(field.substring(2), "%"
													+ para + "%"), expression);
								} else {
									qo
											.addQuery(
													"obj." + field.substring(2),
													new SysMap(field
															.substring(2), para),
													"=");
								}
							} else {
								String pname = field.substring(field
										.indexOf(".") + 1);
								qo.addQuery("obj." + pname, new SysMap(pname
										.replace(".", "_"), para), "=");
							}
							SysMap sm = new SysMap();
							sm.setKey(field);
							sm.setValue(para);
							sms.add(sm);
						}
					}
				}
			}
			mv.addObject("sms", sms);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}