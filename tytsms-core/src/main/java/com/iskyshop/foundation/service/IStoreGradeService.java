package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.foundation.domain.StoreGrade;

public interface IStoreGradeService {
	/**
	 * 保存一个StoreGrade，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(StoreGrade instance);
	
	/**
	 * 根据一个ID得到StoreGrade
	 * 
	 * @param id
	 * @return
	 */
	StoreGrade getObjById(Long id);
	
	/**
	 * 删除一个StoreGrade
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除StoreGrade
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到StoreGrade
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个StoreGrade
	 * 
	 * @param id
	 *            需要更新的StoreGrade的id
	 * @param dir
	 *            需要更新的StoreGrade
	 */
	boolean update(StoreGrade instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<StoreGrade> query(String query, Map params, int begin, int max);
}
