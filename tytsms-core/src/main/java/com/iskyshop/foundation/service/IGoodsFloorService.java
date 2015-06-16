package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.foundation.domain.GoodsFloor;

public interface IGoodsFloorService {
	/**
	 * 保存一个GoodsFloor，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(GoodsFloor instance);
	
	/**
	 * 根据一个ID得到GoodsFloor
	 * 
	 * @param id
	 * @return
	 */
	GoodsFloor getObjById(Long id);
	
	/**
	 * 删除一个GoodsFloor
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除GoodsFloor
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到GoodsFloor
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个GoodsFloor
	 * 
	 * @param id
	 *            需要更新的GoodsFloor的id
	 * @param dir
	 *            需要更新的GoodsFloor
	 */
	boolean update(GoodsFloor instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<GoodsFloor> query(String query, Map params, int begin, int max);
}
