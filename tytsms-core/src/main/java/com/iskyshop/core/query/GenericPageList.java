package com.iskyshop.core.query;

import java.util.Map;

import com.iskyshop.core.dao.IGenericDAO;
import com.iskyshop.core.query.support.IQuery;
import com.iskyshop.core.query.support.IQueryObject;

/**
 * 
 * <p>
 * Title: GenericPageList.java
 * </p>
 * 
 * <p>
 * Description: 面向对象分页类，该类用来进行数据查询并分页返回数据信息
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
public class GenericPageList extends PageList {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6730593239674387757L;
	
	protected String construct;

	protected String scope;

	protected Class cls;

	public GenericPageList(Class cls, IQueryObject queryObject, IGenericDAO dao) {
		this(cls, queryObject.getQuery(), queryObject.getParameters(), dao);
	}

	public GenericPageList(Class cls, String scope, Map paras, IGenericDAO dao) {
		this.cls = cls;
		this.scope = scope;
		IQuery query = new GenericQuery(dao);
		query.setParaValues(paras);
		this.setQuery(query);
	}
	/**
	 * 构造分页信息
	 * 
	 * @param cls
	 *            对应的实体类
	 * @param construct
	 *            查询构造函数，为空是查询所有字段，格式为 new Goods(id,goodsName)
	 * @param scope
	 *            查询条件
	 * @param paras
	 *            查询参数，采用占位符管理
	 * @param dao
	 *            对应的dao
	 */
	public GenericPageList(Class cls, String construct, String scope,
			Map paras, IGenericDAO dao) {
		this.cls = cls;
		this.scope = scope;
		this.construct = construct;
		IQuery query = new GenericQuery(dao);
		query.setParaValues(paras);
		this.setQuery(query);
	}
	
	/**
	 * 查询
	 * 
	 * @param currentPage
	 *            当前页数
	 * @param pageSize
	 *            一页的查询个数
	 */
	public void doList(int currentPage, int pageSize) {
		String totalSql = "select COUNT(obj) from " + cls.getName()
				+ " obj where " + scope;
		super.doList(pageSize, currentPage, totalSql, scope);
	}
}
