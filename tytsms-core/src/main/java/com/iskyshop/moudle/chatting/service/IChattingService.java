package com.iskyshop.moudle.chatting.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.moudle.chatting.domain.Chatting;

public interface IChattingService {
	/**
	 * 保存一个Chatting，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(Chatting instance);
	
	/**
	 * 根据一个ID得到Chatting
	 * 
	 * @param id
	 * @return
	 */
	Chatting getObjById(Long id);
	
	/**
	 * 删除一个Chatting
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除Chatting
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到Chatting
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个Chatting
	 * 
	 * @param id
	 *            需要更新的Chatting的id
	 * @param dir
	 *            需要更新的Chatting
	 */
	boolean update(Chatting instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<Chatting> query(String query, Map params, int begin, int max);
}
