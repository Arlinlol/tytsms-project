package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.foundation.domain.WaterMark;

public interface IWaterMarkService {
	/**
	 * 保存一个WaterMark，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(WaterMark instance);

	/**
	 * 根据一个ID得到WaterMark
	 * 
	 * @param id
	 * @return
	 */
	WaterMark getObjById(Long id);

	/**
	 * 删除一个WaterMark
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);

	/**
	 * 批量删除WaterMark
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);

	/**
	 * 通过一个查询对象得到WaterMark
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);

	/**
	 * 更新一个WaterMark
	 * 
	 * @param id
	 *            需要更新的WaterMark的id
	 * @param dir
	 *            需要更新的WaterMark
	 */
	boolean update(WaterMark instance);

	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<WaterMark> query(String query, Map params, int begin, int max);

	/**
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 */
	WaterMark getObjByProperty(String propertyName, Object value);
}
