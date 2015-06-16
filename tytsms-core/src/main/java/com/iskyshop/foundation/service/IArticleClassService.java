package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.foundation.domain.ArticleClass;

public interface IArticleClassService {
	/**
	 * 保存一个ArticleClass，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(ArticleClass instance);

	/**
	 * 根据一个ID得到ArticleClass
	 * 
	 * @param id
	 * @return
	 */
	ArticleClass getObjById(Long id);

	/**
	 * 删除一个ArticleClass
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);

	/**
	 * 批量删除ArticleClass
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);

	/**
	 * 通过一个查询对象得到ArticleClass
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);

	/**
	 * 更新一个ArticleClass
	 * 
	 * @param id
	 *            需要更新的ArticleClass的id
	 * @param dir
	 *            需要更新的ArticleClass
	 */
	boolean update(ArticleClass instance);

	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<ArticleClass> query(String query, Map params, int begin, int max);

	/**
	 * 
	 * @param propertyNam
	 * @param value
	 * @return
	 */
	ArticleClass getObjByPropertyName(String propertyName, Object value);
}
