package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.foundation.domain.RefundLog;

public interface IRefundLogService {
	/**
	 * 保存一个RefundLog，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(RefundLog instance);
	
	/**
	 * 根据一个ID得到RefundLog
	 * 
	 * @param id
	 * @return
	 */
	RefundLog getObjById(Long id);
	
	/**
	 * 删除一个RefundLog
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除RefundLog
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到RefundLog
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个RefundLog
	 * 
	 * @param id
	 *            需要更新的RefundLog的id
	 * @param dir
	 *            需要更新的RefundLog
	 */
	boolean update(RefundLog instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<RefundLog> query(String query, Map params, int begin, int max);
}
