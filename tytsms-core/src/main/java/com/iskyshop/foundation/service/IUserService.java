package com.iskyshop.foundation.service;

import java.util.List;
import java.util.Map;

import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import com.iskyshop.foundation.domain.User;

public interface IUserService {
	/**
	 * 
	 * @param user
	 * @return
	 */
	boolean save(User user);

	/**
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);

	/**
	 * 
	 * @param user
	 * @return
	 */
	boolean update(User user);

	/**
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);

	/**
	 * 
	 * @param id
	 * @return
	 */
	User getObjById(Long id);

	/**
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 */
	User getObjByProperty(String propertyName, String value);

	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<User> query(String query, Map params, int begin, int max);
}
