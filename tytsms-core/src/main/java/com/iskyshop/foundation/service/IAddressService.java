package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.foundation.domain.Address;

public interface IAddressService {
	/**
	 * 保存一个Address，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(Address instance);
	
	/**
	 * 根据一个ID得到Address
	 * 
	 * @param id
	 * @return
	 */
	Address getObjById(Long id);
	
	/**
	 * 删除一个Address
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除Address
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到Address
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个Address
	 * 
	 * @param id
	 *            需要更新的Address的id
	 * @param dir
	 *            需要更新的Address
	 */
	boolean update(Address instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<Address> query(String query, Map params, int begin, int max);
}
