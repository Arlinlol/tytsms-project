package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.foundation.domain.ReportSubject;

public interface IReportSubjectService {
	/**
	 * 保存一个ReportSubject，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(ReportSubject instance);
	
	/**
	 * 根据一个ID得到ReportSubject
	 * 
	 * @param id
	 * @return
	 */
	ReportSubject getObjById(Long id);
	
	/**
	 * 删除一个ReportSubject
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除ReportSubject
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到ReportSubject
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个ReportSubject
	 * 
	 * @param id
	 *            需要更新的ReportSubject的id
	 * @param dir
	 *            需要更新的ReportSubject
	 */
	boolean update(ReportSubject instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<ReportSubject> query(String query, Map params, int begin, int max);
}
