package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import com.iskyshop.foundation.domain.GoodsBrandCategory;

public interface IGoodsBrandCategoryService {
	/**
	 * 保存一个GoodsBrandCategory，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(GoodsBrandCategory instance);

	/**
	 * 根据一个ID得到GoodsBrandCategory
	 * 
	 * @param id
	 * @return
	 */
	GoodsBrandCategory getObjById(Long id);

	/**
	 * 删除一个GoodsBrandCategory
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);

	/**
	 * 批量删除GoodsBrandCategory
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);

	/**
	 * 通过一个查询对象得到GoodsBrandCategory
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);

	/**
	 * 更新一个GoodsBrandCategory
	 * 
	 * @param id
	 *            需要更新的GoodsBrandCategory的id
	 * @param dir
	 *            需要更新的GoodsBrandCategory
	 */
	boolean update(GoodsBrandCategory instance);

	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<GoodsBrandCategory> query(String query, Map params, int begin,
			int max);

	/**
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 */
	GoodsBrandCategory getObjByProperty(String propertyName, Object value);
}
