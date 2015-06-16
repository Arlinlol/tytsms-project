package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.foundation.domain.Group;

public interface IGroupService {
	/**
	 * 保存一个Group，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(Group instance);
	
	/**
	 * 根据一个ID得到Group
	 * 
	 * @param id
	 * @return
	 */
	Group getObjById(Long id);
	
	/**
	 * 删除一个Group
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除Group
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到Group
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个Group
	 * 
	 * @param id
	 *            需要更新的Group的id
	 * @param dir
	 *            需要更新的Group
	 */
	boolean update(Group instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<Group> query(String query, Map params, int begin, int max);
}
