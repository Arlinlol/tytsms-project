package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.foundation.domain.Favorite;

public interface IFavoriteService {
	/**
	 * 保存一个Favorite，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(Favorite instance);
	
	/**
	 * 根据一个ID得到Favorite
	 * 
	 * @param id
	 * @return
	 */
	Favorite getObjById(Long id);
	
	/**
	 * 删除一个Favorite
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除Favorite
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到Favorite
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个Favorite
	 * 
	 * @param id
	 *            需要更新的Favorite的id
	 * @param dir
	 *            需要更新的Favorite
	 */
	boolean update(Favorite instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<Favorite> query(String query, Map params, int begin, int max);
}
