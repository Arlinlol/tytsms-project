package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.foundation.domain.IntegralGoodsOrder;

public interface IIntegralGoodsOrderService {
	/**
	 * 保存一个IntegralGoodsOrder，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(IntegralGoodsOrder instance);
	
	/**
	 * 根据一个ID得到IntegralGoodsOrder
	 * 
	 * @param id
	 * @return
	 */
	IntegralGoodsOrder getObjById(Long id);
	
	/**
	 * 删除一个IntegralGoodsOrder
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除IntegralGoodsOrder
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到IntegralGoodsOrder
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个IntegralGoodsOrder
	 * 
	 * @param id
	 *            需要更新的IntegralGoodsOrder的id
	 * @param dir
	 *            需要更新的IntegralGoodsOrder
	 */
	boolean update(IntegralGoodsOrder instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<IntegralGoodsOrder> query(String query, Map params, int begin, int max);
}
