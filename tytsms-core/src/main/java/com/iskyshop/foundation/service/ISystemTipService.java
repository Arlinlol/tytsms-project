package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.foundation.domain.SystemTip;

public interface ISystemTipService {
	/**
	 * 保存一个SystemTip，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(SystemTip instance);
	
	/**
	 * 根据一个ID得到SystemTip
	 * 
	 * @param id
	 * @return
	 */
	SystemTip getObjById(Long id);
	
	/**
	 * 删除一个SystemTip
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除SystemTip
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到SystemTip
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个SystemTip
	 * 
	 * @param id
	 *            需要更新的SystemTip的id
	 * @param dir
	 *            需要更新的SystemTip
	 */
	boolean update(SystemTip instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<SystemTip> query(String query, Map params, int begin, int max);
}
