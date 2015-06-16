package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import com.iskyshop.foundation.domain.Plate;

public interface IPlateService {

	/**
	 * 保存一个Plate，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(Plate instance);
	
	/**
	 * 根据一个ID得到Plate
	 * 
	 * @param id
	 * @return
	 */
	Plate getObjById(Long id);
	
	/**
	 * 删除一个Plate
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除Plate
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到Plate
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个Plate
	 * 
	 * @param id
	 *            需要更新的Plate的id
	 * @param dir
	 *            需要更新的Plate
	 */
	boolean update(Plate instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<Plate> query(String query, Map params, int begin, int max);
}
