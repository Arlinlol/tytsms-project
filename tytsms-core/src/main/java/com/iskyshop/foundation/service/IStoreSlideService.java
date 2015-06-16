package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.foundation.domain.StoreSlide;

public interface IStoreSlideService {
	/**
	 * 保存一个StoreSlide，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(StoreSlide instance);
	
	/**
	 * 根据一个ID得到StoreSlide
	 * 
	 * @param id
	 * @return
	 */
	StoreSlide getObjById(Long id);
	
	/**
	 * 删除一个StoreSlide
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除StoreSlide
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到StoreSlide
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个StoreSlide
	 * 
	 * @param id
	 *            需要更新的StoreSlide的id
	 * @param dir
	 *            需要更新的StoreSlide
	 */
	boolean update(StoreSlide instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<StoreSlide> query(String query, Map params, int begin, int max);
}
