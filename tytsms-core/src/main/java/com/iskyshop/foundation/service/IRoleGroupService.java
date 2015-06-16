package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import com.iskyshop.foundation.domain.RoleGroup;

public interface IRoleGroupService {
	/**
	 * 保存一个RoleGroup，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(RoleGroup instance);

	/**
	 * 根据一个ID得到RoleGroup
	 * 
	 * @param id
	 * @return
	 */
	RoleGroup getObjById(Long id);

	/**
	 * 删除一个RoleGroup
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);

	/**
	 * 批量删除RoleGroup
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);

	/**
	 * 通过一个查询对象得到RoleGroup
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);

	/**
	 * 更新一个RoleGroup
	 * 
	 * @param id
	 *            需要更新的RoleGroup的id
	 * @param dir
	 *            需要更新的RoleGroup
	 */
	boolean update(RoleGroup instance);

	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<RoleGroup> query(String query, Map params, int begin, int max);

	/**
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 */
	RoleGroup getObjByProperty(String propertyName, Object value);
}
