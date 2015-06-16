package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import com.iskyshop.foundation.domain.UserConfig;

public interface IUserConfigService {
	/**
	 * 保存一个UserConfig，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(UserConfig instance);

	/**
	 * 根据一个ID得到UserConfig
	 * 
	 * @param id
	 * @return
	 */
	UserConfig getObjById(Long id);

	/**
	 * 删除一个UserConfig
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);

	/**
	 * 批量删除UserConfig
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);

	/**
	 * 通过一个查询对象得到UserConfig
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);

	/**
	 * 更新一个UserConfig
	 * 
	 * @param id
	 *            需要更新的UserConfig的id
	 * @param dir
	 *            需要更新的UserConfig
	 */
	boolean update(UserConfig instance);

	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<UserConfig> query(String query, Map params, int begin, int max);

	/**
	 * 
	 * @return
	 */
	UserConfig getUserConfig();

}
