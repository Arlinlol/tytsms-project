package com.iskyshop.foundation.service;

import java.util.List;
import java.util.Map;

import com.iskyshop.foundation.domain.Share;

public interface IShareService {

	/**
	 * 保存一个Share，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(Share instance);
	
	/**
	 * 根据一个ID得到Message
	 * 
	 * @param id
	 * @return
	 */
	Share getObjById(Long id);
	

	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<Share> query(String query, Map params, int begin, int max);
}
