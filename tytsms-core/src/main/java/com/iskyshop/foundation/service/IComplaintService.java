package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.foundation.domain.Complaint;

public interface IComplaintService {
	/**
	 * 保存一个Complaint，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(Complaint instance);
	
	/**
	 * 根据一个ID得到Complaint
	 * 
	 * @param id
	 * @return
	 */
	Complaint getObjById(Long id);
	
	/**
	 * 删除一个Complaint
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除Complaint
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到Complaint
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个Complaint
	 * 
	 * @param id
	 *            需要更新的Complaint的id
	 * @param dir
	 *            需要更新的Complaint
	 */
	boolean update(Complaint instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<Complaint> query(String query, Map params, int begin, int max);
}
