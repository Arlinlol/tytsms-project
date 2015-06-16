package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.foundation.domain.ComplaintSubject;

public interface IComplaintSubjectService {
	/**
	 * 保存一个ComplaintSubject，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(ComplaintSubject instance);
	
	/**
	 * 根据一个ID得到ComplaintSubject
	 * 
	 * @param id
	 * @return
	 */
	ComplaintSubject getObjById(Long id);
	
	/**
	 * 删除一个ComplaintSubject
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除ComplaintSubject
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到ComplaintSubject
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个ComplaintSubject
	 * 
	 * @param id
	 *            需要更新的ComplaintSubject的id
	 * @param dir
	 *            需要更新的ComplaintSubject
	 */
	boolean update(ComplaintSubject instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<ComplaintSubject> query(String query, Map params, int begin, int max);
}
