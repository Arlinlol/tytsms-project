package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import com.iskyshop.foundation.domain.AdvertPosition;

public interface IAdvertPositionService {
	/**
	 * 保存一个AdvertPosition，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(AdvertPosition instance);
	
	/**
	 * 根据一个ID得到AdvertPosition
	 * 
	 * @param id
	 * @return
	 */
	AdvertPosition getObjById(Long id);
	
	/**
	 * 删除一个AdvertPosition
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除AdvertPosition
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到AdvertPosition
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个AdvertPosition
	 * 
	 * @param id
	 *            需要更新的AdvertPosition的id
	 * @param dir
	 *            需要更新的AdvertPosition
	 */
	boolean update(AdvertPosition instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<AdvertPosition> query(String query, Map params, int begin, int max);
	
	
	public AdvertPosition getAdvertPositionById(Long id);
}
