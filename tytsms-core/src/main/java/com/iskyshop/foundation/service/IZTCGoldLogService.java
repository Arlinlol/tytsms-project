package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.foundation.domain.ZTCGoldLog;

public interface IZTCGoldLogService {
	/**
	 * 保存一个ZTCGlodLog，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(ZTCGoldLog instance);
	
	/**
	 * 根据一个ID得到ZTCGlodLog
	 * 
	 * @param id
	 * @return
	 */
	ZTCGoldLog getObjById(Long id);
	
	/**
	 * 删除一个ZTCGlodLog
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除ZTCGlodLog
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到ZTCGlodLog
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个ZTCGlodLog
	 * 
	 * @param id
	 *            需要更新的ZTCGlodLog的id
	 * @param dir
	 *            需要更新的ZTCGlodLog
	 */
	boolean update(ZTCGoldLog instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<ZTCGoldLog> query(String query, Map params, int begin, int max);
}
