package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import com.iskyshop.foundation.domain.GoodsTypeProperty;

public interface IGoodsTypePropertyService {
	/**
	 * 保存一个GoodsTypeProperty，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(GoodsTypeProperty instance);

	/**
	 * 根据一个ID得到GoodsTypeProperty
	 * 
	 * @param id
	 * @return
	 */
	GoodsTypeProperty getObjById(Long id);

	/**
	 * 删除一个GoodsTypeProperty
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);

	/**
	 * 批量删除GoodsTypeProperty
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);

	/**
	 * 通过一个查询对象得到GoodsTypeProperty
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);

	/**
	 * 更新一个GoodsTypeProperty
	 * 
	 * @param id
	 *            需要更新的GoodsTypeProperty的id
	 * @param dir
	 *            需要更新的GoodsTypeProperty
	 */
	boolean update(GoodsTypeProperty instance);

	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<GoodsTypeProperty> query(String query, Map params, int begin, int max);

}
