package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import com.iskyshop.foundation.domain.GoodsClass;

public interface IGoodsClassService {
	/**
	 * 保存一个GoodsClass，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(GoodsClass instance);

	/**
	 * 根据一个ID得到GoodsClass
	 * 
	 * @param id
	 * @return
	 */
	GoodsClass getObjById(Long id);

	/**
	 * 删除一个GoodsClass
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);

	/**
	 * 批量删除GoodsClass
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);

	/**
	 * 通过一个查询对象得到GoodsClass
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);

	/**
	 * 更新一个GoodsClass
	 * 
	 * @param id
	 *            需要更新的GoodsClass的id
	 * @param dir
	 *            需要更新的GoodsClass
	 */
	boolean update(GoodsClass instance);

	/**
	 * 
	 * @param query
	 * @param params
	 * @return
	 */
	List<GoodsClass> query(String query, Map params, int begin, int max);

	/**
	 * 根据属性值获取对应的实体对象
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 */
	GoodsClass getObjByProperty(String propertyName, Object value);
}
