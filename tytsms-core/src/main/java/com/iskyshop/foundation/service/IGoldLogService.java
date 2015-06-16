package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.foundation.domain.GoldLog;

public interface IGoldLogService {
	/**
	 * 保存一个GoldLog，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(GoldLog instance);
	
	/**
	 * 根据一个ID得到GoldLog
	 * 
	 * @param id
	 * @return
	 */
	GoldLog getObjById(Long id);
	
	/**
	 * 删除一个GoldLog
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除GoldLog
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到GoldLog
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个GoldLog
	 * 
	 * @param id
	 *            需要更新的GoldLog的id
	 * @param dir
	 *            需要更新的GoldLog
	 */
	boolean update(GoldLog instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<GoldLog> query(String query, Map params, int begin, int max);
}
