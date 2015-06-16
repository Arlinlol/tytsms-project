package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.foundation.domain.CouponInfo;

public interface ICouponInfoService {
	/**
	 * 保存一个CouponInfo，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(CouponInfo instance);
	
	/**
	 * 根据一个ID得到CouponInfo
	 * 
	 * @param id
	 * @return
	 */
	CouponInfo getObjById(Long id);
	
	/**
	 * 删除一个CouponInfo
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除CouponInfo
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到CouponInfo
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个CouponInfo
	 * 
	 * @param id
	 *            需要更新的CouponInfo的id
	 * @param dir
	 *            需要更新的CouponInfo
	 */
	boolean update(CouponInfo instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<CouponInfo> query(String query, Map params, int begin, int max);
}
