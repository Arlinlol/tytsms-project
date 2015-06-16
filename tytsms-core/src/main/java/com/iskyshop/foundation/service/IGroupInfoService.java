package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import com.iskyshop.foundation.domain.GroupInfo;
import com.iskyshop.foundation.domain.User;

public interface IGroupInfoService {
	/**
	 * 保存一个GroupInfo，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(GroupInfo instance);
	
	/**
	 * 根据一个ID得到GroupInfo
	 * 
	 * @param id
	 * @return
	 */
	GroupInfo getObjById(Long id);
	
	/**
	 * 删除一个GroupInfo
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除GroupInfo
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到GroupInfo
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个GroupInfo
	 * 
	 * @param id
	 *            需要更新的GroupInfo的id
	 * @param dir
	 *            需要更新的GroupInfo
	 */
	boolean update(GroupInfo instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<GroupInfo> query(String query, Map params, int begin, int max);
	
	GroupInfo getObjByProperty(String propertyName, String value);
}
