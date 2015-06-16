package com.iskyshop.moudle.chatting.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.moudle.chatting.domain.ChattingConfig;

public interface IChattingConfigService {
	/**
	 * 保存一个ChattingConfig，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(ChattingConfig instance);
	
	/**
	 * 根据一个ID得到ChattingConfig
	 * 
	 * @param id
	 * @return
	 */
	ChattingConfig getObjById(Long id);
	
	/**
	 * 删除一个ChattingConfig
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除ChattingConfig
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到ChattingConfig
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个ChattingConfig
	 * 
	 * @param id
	 *            需要更新的ChattingConfig的id
	 * @param dir
	 *            需要更新的ChattingConfig
	 */
	boolean update(ChattingConfig instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<ChattingConfig> query(String query, Map params, int begin, int max);
}
