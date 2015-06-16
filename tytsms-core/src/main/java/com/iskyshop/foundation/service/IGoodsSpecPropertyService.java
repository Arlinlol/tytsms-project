package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import com.iskyshop.foundation.domain.GoodsSpecProperty;

public interface IGoodsSpecPropertyService {
	/**
	 * 保存一个GoodsSpecProperty，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(GoodsSpecProperty instance);
	
	/**
	 * 根据一个ID得到GoodsSpecProperty
	 * 
	 * @param id
	 * @return
	 */
	GoodsSpecProperty getObjById(Long id);
	
	/**
	 * 删除一个GoodsSpecProperty
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除GoodsSpecProperty
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到GoodsSpecProperty
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个GoodsSpecProperty
	 * 
	 * @param id
	 *            需要更新的GoodsSpecProperty的id
	 * @param dir
	 *            需要更新的GoodsSpecProperty
	 */
	boolean update(GoodsSpecProperty instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	 List<GoodsSpecProperty> query(String query, Map params, int begin, int max);
}
