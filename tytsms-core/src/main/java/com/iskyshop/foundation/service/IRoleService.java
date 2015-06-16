package com.iskyshop.foundation.service;

import java.util.List;
import java.util.Map;

import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import com.iskyshop.foundation.domain.Role;

public interface IRoleService {
	/**
	 * 
	 * @param role
	 * @return
	 */
	boolean save(Role role);

	/**
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);

	/**
	 * 
	 * @param role
	 * @return
	 */
	boolean update(Role role);

	/**
	 * 
	 * @param id
	 * @return
	 */
	Role getObjById(Long id);

	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<Role> query(String query, Map params, int begin, int max);

	/**
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);

	/**
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 */
	Role getObjByProperty(String propertyName, Object value);
}
