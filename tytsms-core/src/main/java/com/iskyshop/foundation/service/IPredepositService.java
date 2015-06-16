package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.foundation.domain.Predeposit;

public interface IPredepositService {
	/**
	 * 保存一个Predeposit，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(Predeposit instance);
	
	/**
	 * 根据一个ID得到Predeposit
	 * 
	 * @param id
	 * @return
	 */
	Predeposit getObjById(Long id);
	
	/**
	 * 删除一个Predeposit
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除Predeposit
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到Predeposit
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个Predeposit
	 * 
	 * @param id
	 *            需要更新的Predeposit的id
	 * @param dir
	 *            需要更新的Predeposit
	 */
	boolean update(Predeposit instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<Predeposit> query(String query, Map params, int begin, int max);
}
