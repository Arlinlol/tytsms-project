package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.foundation.domain.StorePoint;

public interface IStorePointService {
	/**
	 * 保存一个StorePoint，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(StorePoint instance);
	
	/**
	 * 根据一个ID得到StorePoint
	 * 
	 * @param id
	 * @return
	 */
	StorePoint getObjById(Long id);
	
	/**
	 * 删除一个StorePoint
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除StorePoint
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到StorePoint
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个StorePoint
	 * 
	 * @param id
	 *            需要更新的StorePoint的id
	 * @param dir
	 *            需要更新的StorePoint
	 */
	boolean update(StorePoint instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<StorePoint> query(String query, Map params, int begin, int max);
}
