package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.foundation.domain.Activity;

public interface IActivityService {
	/**
	 * 保存一个Activity，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(Activity instance);
	
	/**
	 * 根据一个ID得到Activity
	 * 
	 * @param id
	 * @return
	 */
	Activity getObjById(Long id);
	
	/**
	 * 删除一个Activity
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除Activity
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到Activity
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个Activity
	 * 
	 * @param id
	 *            需要更新的Activity的id
	 * @param dir
	 *            需要更新的Activity
	 */
	boolean update(Activity instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<Activity> query(String query, Map params, int begin, int max);
}
