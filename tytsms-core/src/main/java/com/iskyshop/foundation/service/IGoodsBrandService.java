package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import com.iskyshop.foundation.domain.GoodsBrand;

public interface IGoodsBrandService {
	/**
	 * 保存一个GoodsBrand，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(GoodsBrand instance);
	
	/**
	 * 根据一个ID得到GoodsBrand
	 * 
	 * @param id
	 * @return
	 */
	GoodsBrand getObjById(Long id);
	
	/**
	 * 删除一个GoodsBrand
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除GoodsBrand
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到GoodsBrand
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个GoodsBrand
	 * 
	 * @param id
	 *            需要更新的GoodsBrand的id
	 * @param dir
	 *            需要更新的GoodsBrand
	 */
	boolean update(GoodsBrand instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	 List<GoodsBrand> query(String query, Map params, int begin, int max);
}
