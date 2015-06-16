package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import com.iskyshop.foundation.domain.Evaluate;
import com.iskyshop.foundation.domain.Goods;

public interface IEvaluateService {
	/**
	 * 保存一个Evaluate，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(Evaluate instance);

	/**
	 * 根据一个ID得到Evaluate
	 * 
	 * @param id
	 * @return
	 */
	Evaluate getObjById(Long id);

	/**
	 * 删除一个Evaluate
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);

	/**
	 * 批量删除Evaluate
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);

	/**
	 * 通过一个查询对象得到Evaluate
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);

	/**
	 * 更新一个Evaluate
	 * 
	 * @param id
	 *            需要更新的Evaluate的id
	 * @param dir
	 *            需要更新的Evaluate
	 */
	boolean update(Evaluate instance);

	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<Evaluate> query(String query, Map params, int begin, int max);

	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<Goods> query_goods(String query, Map params, int begin, int max);
}
