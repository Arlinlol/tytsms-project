package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.foundation.domain.PredepositCash;

public interface IPredepositCashService {
	/**
	 * 保存一个PredepositCash，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(PredepositCash instance);
	
	/**
	 * 根据一个ID得到PredepositCash
	 * 
	 * @param id
	 * @return
	 */
	PredepositCash getObjById(Long id);
	
	/**
	 * 删除一个PredepositCash
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除PredepositCash
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到PredepositCash
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个PredepositCash
	 * 
	 * @param id
	 *            需要更新的PredepositCash的id
	 * @param dir
	 *            需要更新的PredepositCash
	 */
	boolean update(PredepositCash instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<PredepositCash> query(String query, Map params, int begin, int max);
}
