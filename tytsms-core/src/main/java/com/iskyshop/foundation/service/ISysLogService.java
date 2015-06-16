package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import com.iskyshop.foundation.domain.SysLog;

public interface ISysLogService {
	/**
	 * 保存一个SysLog，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(SysLog instance);

	/**
	 * 根据一个ID得到SysLog
	 * 
	 * @param id
	 * @return
	 */
	SysLog getObjById(Long id);

	/**
	 * 删除一个SysLog
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);

	/**
	 * 批量删除SysLog
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);

	/**
	 * 通过一个查询对象得到SysLog
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);

	/**
	 * 更新一个SysLog
	 * 
	 * @param id
	 *            需要更新的SysLog的id
	 * @param dir
	 *            需要更新的SysLog
	 */
	boolean update(SysLog instance);

	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<SysLog> query(String query, Map params, int begin, int max);
}
