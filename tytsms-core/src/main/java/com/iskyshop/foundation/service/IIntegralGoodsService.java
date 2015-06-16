package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.foundation.domain.IntegralGoods;

public interface IIntegralGoodsService {
	/**
	 * 保存一个IntegralGoods，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(IntegralGoods instance);
	
	/**
	 * 根据一个ID得到IntegralGoods
	 * 
	 * @param id
	 * @return
	 */
	IntegralGoods getObjById(Long id);
	
	/**
	 * 删除一个IntegralGoods
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除IntegralGoods
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到IntegralGoods
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个IntegralGoods
	 * 
	 * @param id
	 *            需要更新的IntegralGoods的id
	 * @param dir
	 *            需要更新的IntegralGoods
	 */
	boolean update(IntegralGoods instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<IntegralGoods> query(String query, Map params, int begin, int max);
}
