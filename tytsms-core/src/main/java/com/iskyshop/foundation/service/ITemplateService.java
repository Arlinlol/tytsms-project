package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.foundation.domain.Template;

public interface ITemplateService {
	/**
	 * 保存一个Template，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(Template instance);

	/**
	 * 根据一个ID得到Template
	 * 
	 * @param id
	 * @return
	 */
	Template getObjById(Long id);

	/**
	 * 删除一个Template
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);

	/**
	 * 批量删除Template
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);

	/**
	 * 通过一个查询对象得到Template
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);

	/**
	 * 更新一个Template
	 * 
	 * @param id
	 *            需要更新的Template的id
	 * @param dir
	 *            需要更新的Template
	 */
	boolean update(Template instance);

	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<Template> query(String query, Map params, int begin, int max);

	/**
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 */
	Template getObjByProperty(String propertyName, Object value);
}
