package com.iskyshop.core.query;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.query.support.IQueryObject;
import com.iskyshop.core.tools.CommUtil;
/**
 * 
* <p>Title: QueryObject.java</p>

* <p>Description:基础查询对象，封装基础查询条件，包括页大小、当前页、排序信息等 </p>

* <p>Copyright: Copyright (c) 2014</p>

* <p>Company: 沈阳网之商科技有限公司 www.iskyshop.com</p>

* @author erikzhang

* @date 2014-4-24

* @version iskyshop_b2b2c 1.0
 */
public class QueryObject implements IQueryObject {
	
	private String construct;// 查询构造器，为空时查询obj所有字段

	protected Integer pageSize = 12;

	protected Integer currentPage = 0;

	protected String orderBy;

	protected String orderType;

	protected Map params = new HashMap();

	protected String queryString = "1=1";

	public String getConstruct() {
		return construct;
	}

	public void setConstruct(String construct) {
		this.construct = construct;
	}

	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	protected void setParams(Map params) {
		this.params = params;
	}

	public String getOrderType() {
		return orderType;
	}

	public Integer getCurrentPage() {
		if (currentPage == null) {
			currentPage = -1;
		}
		return currentPage;
	}

	public String getOrder() {
		return orderType;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public Integer getPageSize() {
		if (pageSize == null) {
			pageSize = -1;
		}
		return pageSize;
	}

	public QueryObject() {

	}
	/**
	 * 构造一个queryObject
	 * 
	 * @param construct
	 *            查询对象构造函数，如new Goods(id,goodsName)
	 * @param currentPage
	 *            当前页
	 * @param mv
	 *            需要封装的视图
	 * @param orderBy
	 *            排序字段
	 * @param orderType
	 *            排序类型
	 */
	public QueryObject(String construct, String currentPage, ModelAndView mv,
			String orderBy, String orderType) {
		if (construct != null && !construct.equals("")) {
			this.setConstruct(construct);
		}
		if (currentPage != null && !currentPage.equals("")) {
			this.setCurrentPage(CommUtil.null2Int(currentPage));
		}
		this.setPageSize(this.pageSize);
		if (orderBy == null || orderBy.equals("")) {
			this.setOrderBy("addTime");
			mv.addObject("orderBy", "addTime");
		} else {
			this.setOrderBy(orderBy);
			mv.addObject("orderBy", orderBy);
		}
		if (orderType == null || orderType.equals("")) {
			this.setOrderType("desc");
			mv.addObject("orderType", "desc");
		} else {
			this.setOrderType(orderType);
			mv.addObject("orderType", orderType);
		}
	}
	
	public QueryObject(String currentPage, ModelAndView mv, String orderBy,
			String orderType) {
		if (currentPage != null && !currentPage.equals(""))
			this.setCurrentPage(CommUtil.null2Int(currentPage));
		this.setPageSize(this.pageSize);
		if (orderBy == null || orderBy.equals("")) {
			this.setOrderBy("addTime");
			mv.addObject("orderBy", "addTime");
		} else {
			this.setOrderBy(orderBy);
			mv.addObject("orderBy", orderBy);
		}
		if (orderType == null || orderType.equals("")) {
			this.setOrderType("desc");
			mv.addObject("orderType", "desc");
		} else {
			this.setOrderType(orderType);
			mv.addObject("orderType", orderType);
		}
	}

	public PageObject getPageObj() {
		PageObject pageObj = new PageObject();
		pageObj.setCurrentPage(this.getCurrentPage());
		pageObj.setPageSize(this.getPageSize());
		if (this.currentPage == null || this.currentPage <= 0) {
			pageObj.setCurrentPage(1);
		}
		return pageObj;
	}

	public String getQuery() {
		customizeQuery();
		return queryString + orderString();
	}

	protected String orderString() {
		String orderString = " ";
		if (this.getOrderBy() != null && !"".equals(getOrderBy())) {
			orderString += " order by obj." + this.getOrderBy();
		}
		if (this.getOrderType() != null && !"".equals(getOrderType())) {
			orderString = orderString + " " + getOrderType();
		}
		return orderString;
	}

	public Map getParameters() {
		return this.params;
	}

	public IQueryObject addQuery(String field, SysMap para, String expression) {
		if (field != null && para != null) {
			queryString += " and " + field + " " + handleExpression(expression)
					+ ":" + para.getKey().toString();
			params.put(para.getKey(), para.getValue());
		}
		return this;
	}

	public IQueryObject addQuery(String field, SysMap para, String expression,
			String logic) {
		if (field != null && para != null) {
			queryString += " " + logic + " " + field + " "
					+ handleExpression(expression) + ":"
					+ para.getKey().toString();
			params.put(para.getKey(), para.getValue());
		}
		return this;
	}

	public IQueryObject addQuery(String scope, Map paras) {
		if (scope != null) {
			if (scope.trim().indexOf("and") == 0
					|| scope.trim().indexOf("or") == 0) {
				queryString += " " + scope;
			} else
				queryString += " and " + scope;
			if (paras != null && paras.size() > 0) {
				for (Object key : paras.keySet()) {
					params.put(key, paras.get(key));
				}
			}
		}
		return this;
	}

	@Override
	public IQueryObject addQuery(String para, Object obj, String field,
			String expression) {
		// TODO Auto-generated method stub
		if (field != null && para != null) {
			queryString += " and :" + para + " " + expression + " " + field;
			params.put(para, obj);
		}
		return this;
	}

	public IQueryObject addQuery(String para, Object obj, String field,
			String expression, String logic) {
		if (field != null && para != null) {
			queryString += " " + logic + " :" + para + " " + expression + " "
					+ field;
			params.put(para, obj);
		}
		return this;
	}

	private String handleExpression(String expression) {
		if (expression == null)
			return "=";
		else
			return expression;
	}

	public void customizeQuery() {

	}

}
