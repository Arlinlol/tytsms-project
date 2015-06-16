package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.foundation.domain.PayoffLog;

public interface IPayoffLogService {
	/**
	 * 保存一个PayoffLog，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(PayoffLog instance);
	
	/**
	 * 根据一个ID得到PayoffLog
	 * 
	 * @param id
	 * @return
	 */
	PayoffLog getObjById(Long id);
	
	/**
	 * 删除一个PayoffLog
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除PayoffLog
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到PayoffLog
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个PayoffLog
	 * 
	 * @param id
	 *            需要更新的PayoffLog的id
	 * @param dir
	 *            需要更新的PayoffLog
	 */
	boolean update(PayoffLog instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<PayoffLog> query(String query, Map params, int begin, int max);
}
