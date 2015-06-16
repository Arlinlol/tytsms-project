package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.foundation.domain.IntegralLog;

public interface IIntegralLogService {
	/**
	 * 保存一个IntegralLog，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(IntegralLog instance);
	
	/**
	 * 根据一个ID得到IntegralLog
	 * 
	 * @param id
	 * @return
	 */
	IntegralLog getObjById(Long id);
	
	/**
	 * 删除一个IntegralLog
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除IntegralLog
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到IntegralLog
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个IntegralLog
	 * 
	 * @param id
	 *            需要更新的IntegralLog的id
	 * @param dir
	 *            需要更新的IntegralLog
	 */
	boolean update(IntegralLog instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<IntegralLog> query(String query, Map params, int begin, int max);
}
