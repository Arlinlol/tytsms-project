package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import com.iskyshop.foundation.domain.ActivityGoods;

public interface IActivityGoodsService {
	/**
	 * 保存一个ActivityGoods，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(ActivityGoods instance);
	
	/**
	 * 根据一个ID得到ActivityGoods
	 * 
	 * @param id
	 * @return
	 */
	ActivityGoods getObjById(Long id);
	
	/**
	 * 删除一个ActivityGoods
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除ActivityGoods
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到ActivityGoods
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个ActivityGoods
	 * 
	 * @param id
	 *            需要更新的ActivityGoods的id
	 * @param dir
	 *            需要更新的ActivityGoods
	 */
	boolean update(ActivityGoods instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<ActivityGoods> query(String query, Map params, int begin, int max);
}
