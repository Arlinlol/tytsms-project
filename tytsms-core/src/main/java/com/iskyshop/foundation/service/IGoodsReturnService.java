package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.foundation.domain.GoodsReturn;

public interface IGoodsReturnService {
	/**
	 * 保存一个GoodsReturn，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(GoodsReturn instance);
	
	/**
	 * 根据一个ID得到GoodsReturn
	 * 
	 * @param id
	 * @return
	 */
	GoodsReturn getObjById(Long id);
	
	/**
	 * 删除一个GoodsReturn
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除GoodsReturn
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到GoodsReturn
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个GoodsReturn
	 * 
	 * @param id
	 *            需要更新的GoodsReturn的id
	 * @param dir
	 *            需要更新的GoodsReturn
	 */
	boolean update(GoodsReturn instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<GoodsReturn> query(String query, Map params, int begin, int max);
}
