package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.foundation.domain.PredepositLog;

public interface IPredepositLogService {
	/**
	 * 保存一个PredepositLog，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(PredepositLog instance);
	
	/**
	 * 根据一个ID得到PredepositLog
	 * 
	 * @param id
	 * @return
	 */
	PredepositLog getObjById(Long id);
	
	/**
	 * 删除一个PredepositLog
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除PredepositLog
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到PredepositLog
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个PredepositLog
	 * 
	 * @param id
	 *            需要更新的PredepositLog的id
	 * @param dir
	 *            需要更新的PredepositLog
	 */
	boolean update(PredepositLog instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<PredepositLog> query(String query, Map params, int begin, int max);
}
