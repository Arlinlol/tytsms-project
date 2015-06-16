package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import com.iskyshop.foundation.domain.GoodsType;

public interface IGoodsTypeService {
	/**
	 * 保存一个GoodsType，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(GoodsType instance);
	
	/**
	 * 根据一个ID得到GoodsType
	 * 
	 * @param id
	 * @return
	 */
	GoodsType getObjById(Long id);
	
	/**
	 * 删除一个GoodsType
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除GoodsType
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到GoodsType
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个GoodsType
	 * 
	 * @param id
	 *            需要更新的GoodsType的id
	 * @param dir
	 *            需要更新的GoodsType
	 */
	boolean update(GoodsType instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	 List<GoodsType> query(String query, Map params, int begin, int max);
}
