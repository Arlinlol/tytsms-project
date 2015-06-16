package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import com.iskyshop.foundation.domain.FootPoint;

public interface IFootPointService {
	/**
	 * 保存一个FootPoint，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(FootPoint instance);
	
	/**
	 * 根据一个ID得到FootPoint
	 * 
	 * @param id
	 * @return
	 */
	FootPoint getObjById(Long id);
	
	/**
	 * 删除一个FootPoint
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除FootPoint
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到FootPoint
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个FootPoint
	 * 
	 * @param id
	 *            需要更新的FootPoint的id
	 * @param dir
	 *            需要更新的FootPoint
	 */
	boolean update(FootPoint instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<FootPoint> query(String query, Map params, int begin, int max);
}
