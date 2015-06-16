package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.foundation.domain.StoreNavigation;

public interface IStoreNavigationService {
	/**
	 * 保存一个StoreNavigation，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(StoreNavigation instance);
	
	/**
	 * 根据一个ID得到StoreNavigation
	 * 
	 * @param id
	 * @return
	 */
	StoreNavigation getObjById(Long id);
	
	/**
	 * 删除一个StoreNavigation
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除StoreNavigation
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到StoreNavigation
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个StoreNavigation
	 * 
	 * @param id
	 *            需要更新的StoreNavigation的id
	 * @param dir
	 *            需要更新的StoreNavigation
	 */
	boolean update(StoreNavigation instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<StoreNavigation> query(String query, Map params, int begin, int max);
}
