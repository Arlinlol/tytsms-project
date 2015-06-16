package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.foundation.domain.IntegralGoodsCart;

public interface IIntegralGoodsCartService {
	/**
	 * 保存一个IntegralGoodsCart，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(IntegralGoodsCart instance);
	
	/**
	 * 根据一个ID得到IntegralGoodsCart
	 * 
	 * @param id
	 * @return
	 */
	IntegralGoodsCart getObjById(Long id);
	
	/**
	 * 删除一个IntegralGoodsCart
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除IntegralGoodsCart
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到IntegralGoodsCart
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个IntegralGoodsCart
	 * 
	 * @param id
	 *            需要更新的IntegralGoodsCart的id
	 * @param dir
	 *            需要更新的IntegralGoodsCart
	 */
	boolean update(IntegralGoodsCart instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<IntegralGoodsCart> query(String query, Map params, int begin, int max);
}
