package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.foundation.domain.ReturnGoodsLog;

public interface IReturnGoodsLogService {
	/**
	 * 保存一个ReturnGoodsLog，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(ReturnGoodsLog instance);
	
	/**
	 * 根据一个ID得到ReturnGoodsLog
	 * 
	 * @param id
	 * @return
	 */
	ReturnGoodsLog getObjById(Long id);
	
	/**
	 * 删除一个ReturnGoodsLog
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除ReturnGoodsLog
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到ReturnGoodsLog
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个ReturnGoodsLog
	 * 
	 * @param id
	 *            需要更新的ReturnGoodsLog的id
	 * @param dir
	 *            需要更新的ReturnGoodsLog
	 */
	boolean update(ReturnGoodsLog instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<ReturnGoodsLog> query(String query, Map params, int begin, int max);
}
