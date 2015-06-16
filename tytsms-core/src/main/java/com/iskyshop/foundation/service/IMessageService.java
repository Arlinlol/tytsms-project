package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.foundation.domain.Message;

public interface IMessageService {
	/**
	 * 保存一个Message，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(Message instance);
	
	/**
	 * 根据一个ID得到Message
	 * 
	 * @param id
	 * @return
	 */
	Message getObjById(Long id);
	
	/**
	 * 删除一个Message
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除Message
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到Message
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个Message
	 * 
	 * @param id
	 *            需要更新的Message的id
	 * @param dir
	 *            需要更新的Message
	 */
	boolean update(Message instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<Message> query(String query, Map params, int begin, int max);
}
