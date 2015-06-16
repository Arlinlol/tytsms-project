package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.foundation.domain.GoodsCart;

public interface IGoodsCartService {
	/**
	 * 保存一个GoodsCart，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(GoodsCart instance);
	
	/**
	 * 根据一个ID得到GoodsCart
	 * 
	 * @param id
	 * @return
	 */
	GoodsCart getObjById(Long id);
	
	/**
	 * 删除一个GoodsCart
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除GoodsCart
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到GoodsCart
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个GoodsCart
	 * 
	 * @param id
	 *            需要更新的GoodsCart的id
	 * @param dir
	 *            需要更新的GoodsCart
	 */
	boolean update(GoodsCart instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<GoodsCart> query(String query, Map params, int begin, int max);
	
	/**
	 * 合并购物车中同一产品的信息
	 * 
	 * @return
	 */
	public List<GoodsCart> cart_calc(List<GoodsCart> carts_list);
}
