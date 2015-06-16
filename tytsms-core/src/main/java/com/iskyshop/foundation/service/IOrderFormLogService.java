package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.foundation.domain.OrderFormLog;

public interface IOrderFormLogService {
	/**
	 * 保存一个OrderFormLog，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(OrderFormLog instance);
	
	/**
	 * 根据一个ID得到OrderFormLog
	 * 
	 * @param id
	 * @return
	 */
	OrderFormLog getObjById(Long id);
	
	/**
	 * 删除一个OrderFormLog
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除OrderFormLog
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到OrderFormLog
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个OrderFormLog
	 * 
	 * @param id
	 *            需要更新的OrderFormLog的id
	 * @param dir
	 *            需要更新的OrderFormLog
	 */
	boolean update(OrderFormLog instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<OrderFormLog> query(String query, Map params, int begin, int max);
}
