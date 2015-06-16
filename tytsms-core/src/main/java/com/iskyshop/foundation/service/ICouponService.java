package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.foundation.domain.Coupon;

public interface ICouponService {
	/**
	 * 保存一个Coupon，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(Coupon instance);
	
	/**
	 * 根据一个ID得到Coupon
	 * 
	 * @param id
	 * @return
	 */
	Coupon getObjById(Long id);
	
	/**
	 * 删除一个Coupon
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除Coupon
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到Coupon
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个Coupon
	 * 
	 * @param id
	 *            需要更新的Coupon的id
	 * @param dir
	 *            需要更新的Coupon
	 */
	boolean update(Coupon instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<Coupon> query(String query, Map params, int begin, int max);
}
