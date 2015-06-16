package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.foundation.domain.TransArea;

public interface ITransAreaService {
	/**
	 * 保存一个TransArea，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(TransArea instance);
	
	/**
	 * 根据一个ID得到TransArea
	 * 
	 * @param id
	 * @return
	 */
	TransArea getObjById(Long id);
	
	/**
	 * 删除一个TransArea
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除TransArea
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到TransArea
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个TransArea
	 * 
	 * @param id
	 *            需要更新的TransArea的id
	 * @param dir
	 *            需要更新的TransArea
	 */
	boolean update(TransArea instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<TransArea> query(String query, Map params, int begin, int max);
}
