package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.foundation.domain.GroupClass;

public interface IGroupClassService {
	/**
	 * 保存一个GroupClass，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(GroupClass instance);
	
	/**
	 * 根据一个ID得到GroupClass
	 * 
	 * @param id
	 * @return
	 */
	GroupClass getObjById(Long id);
	
	/**
	 * 删除一个GroupClass
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除GroupClass
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到GroupClass
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个GroupClass
	 * 
	 * @param id
	 *            需要更新的GroupClass的id
	 * @param dir
	 *            需要更新的GroupClass
	 */
	boolean update(GroupClass instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<GroupClass> query(String query, Map params, int begin, int max);
}
