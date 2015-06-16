package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import com.iskyshop.foundation.domain.Partner;

public interface IPartnerService {
	/**
	 * 保存一个Partner，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(Partner instance);

	/**
	 * 根据一个ID得到Partner
	 * 
	 * @param id
	 * @return
	 */
	Partner getObjById(Long id);

	/**
	 * 删除一个Partner
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);

	/**
	 * 批量删除Partner
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);

	/**
	 * 通过一个查询对象得到Partner
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);

	/**
	 * 更新一个Partner
	 * 
	 * @param id
	 *            需要更新的Partner的id
	 * @param dir
	 *            需要更新的Partner
	 */
	boolean update(Partner instance);

	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<Partner> query(String query, Map params, int begin, int max);
}
