package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.foundation.domain.Transport;

public interface ITransportService {
	/**
	 * 保存一个Transport，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(Transport instance);
	
	/**
	 * 根据一个ID得到Transport
	 * 
	 * @param id
	 * @return
	 */
	Transport getObjById(Long id);
	
	/**
	 * 删除一个Transport
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除Transport
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到Transport
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个Transport
	 * 
	 * @param id
	 *            需要更新的Transport的id
	 * @param dir
	 *            需要更新的Transport
	 */
	boolean update(Transport instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<Transport> query(String query, Map params, int begin, int max);
}
