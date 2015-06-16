package com.iskyshop.foundation.service;

import java.util.List;
import java.util.Map;

import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import com.iskyshop.foundation.domain.Award;

public interface IAwardService {

	/**
	 * 保存一个Award，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(Award award);
	
	/**
	 * 根据一个ID得到Award
	 * 
	 * @param id
	 * @return
	 */
	Award getObjById(Long id);
	
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<Award> query(String query, Map params, int begin, int max);
	
	/**
	 * 通过一个查询对象得到Award
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
}
