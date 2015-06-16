package com.iskyshop.core.query;

import java.util.List;
import java.util.Map;

import com.iskyshop.core.dao.IGenericDAO;
import com.iskyshop.core.query.support.IQuery;

/**
 * 
 * <p>
 * Title: GenericQuery.java
 * </p>
 * 
 * <p>
 * Description:面向对象基础查询类，通过查询对象的封装完成查询信息
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * Company: 沈阳网之商科技有限公司 www.iskyshop.com
 * </p>
 * 
 * @author erikzhang
 * 
 * @date 2014-4-24
 * 
 * @version iskyshop_b2b2c 1.0
 */
public class GenericQuery implements IQuery {

	private IGenericDAO dao;

	private int begin;

	private int max;

	private Map params;

	public GenericQuery(IGenericDAO dao) {
		this.dao = dao;
	}

	public List getResult(String condition) {
		// TODO Auto-generated method stub
		return dao.find(condition, this.params, begin, max);
	}
	/**
	 * 根据查询条件查询结果(所有结果值)，返回结果数据集合List
	 */
	public List getResult(String construct, String condition) {
		// TODO Auto-generated method stub
		return dao.find(construct, condition, this.params, begin, max);
	}
	
	public List getResult(String condition, int begin, int max) {
		// TODO Auto-generated method stub
		Object[] params = null;
		return this.dao.find(condition, this.params, begin, max);
	}

	public int getRows(String condition) {
		// TODO Auto-generated method stub
		int n = condition.toLowerCase().indexOf("order by");
		Object[] params = null;
		if (n > 0) {
			condition = condition.substring(0, n);
		}
		List ret = dao.query(condition, this.params, 0, 0);
		if (ret != null && ret.size() > 0) {
			return ((Long) ret.get(0)).intValue();
		} else {
			return 0;
		}
	}

	public void setFirstResult(int begin) {
		this.begin = begin;
	}

	public void setMaxResults(int max) {
		this.max = max;
	}

	@Override
	public void setParaValues(Map params) {
		// TODO Auto-generated method stub
		this.params = params;
	}

}
