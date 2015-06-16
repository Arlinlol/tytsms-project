package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.foundation.domain.Consult;

public interface IConsultService {
	/**
	 * 保存一个Consult，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(Consult instance);
	
	/**
	 * 根据一个ID得到Consult
	 * 
	 * @param id
	 * @return
	 */
	Consult getObjById(Long id);
	
	/**
	 * 删除一个Consult
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除Consult
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到Consult
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个Consult
	 * 
	 * @param id
	 *            需要更新的Consult的id
	 * @param dir
	 *            需要更新的Consult
	 */
	boolean update(Consult instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<Consult> query(String query, Map params, int begin, int max);
}
