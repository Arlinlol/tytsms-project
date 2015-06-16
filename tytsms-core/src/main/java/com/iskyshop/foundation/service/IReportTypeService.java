package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.foundation.domain.ReportType;

public interface IReportTypeService {
	/**
	 * 保存一个ReportType，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(ReportType instance);
	
	/**
	 * 根据一个ID得到ReportType
	 * 
	 * @param id
	 * @return
	 */
	ReportType getObjById(Long id);
	
	/**
	 * 删除一个ReportType
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除ReportType
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到ReportType
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个ReportType
	 * 
	 * @param id
	 *            需要更新的ReportType的id
	 * @param dir
	 *            需要更新的ReportType
	 */
	boolean update(ReportType instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<ReportType> query(String query, Map params, int begin, int max);
}
