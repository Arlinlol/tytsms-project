package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.foundation.domain.Advert;

public interface IAdvertService {
	/**
	 * 保存一个Advert，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(Advert instance);
	
	/**
	 * 根据一个ID得到Advert
	 * 
	 * @param id
	 * @return
	 */
	Advert getObjById(Long id);
	
	/**
	 * 删除一个Advert
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除Advert
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到Advert
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个Advert
	 * 
	 * @param id
	 *            需要更新的Advert的id
	 * @param dir
	 *            需要更新的Advert
	 */
	boolean update(Advert instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<Advert> query(String query, Map params, int begin, int max);
}
