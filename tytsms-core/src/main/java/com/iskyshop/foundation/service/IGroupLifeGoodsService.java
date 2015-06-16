package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.foundation.domain.GroupLifeGoods;

public interface IGroupLifeGoodsService {
	/**
	 * 保存一个GroupLifeGoods，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(GroupLifeGoods instance);
	
	/**
	 * 根据一个ID得到GroupLifeGoods
	 * 
	 * @param id
	 * @return
	 */
	GroupLifeGoods getObjById(Long id);
	
	/**
	 * 删除一个GroupLifeGoods
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除GroupLifeGoods
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到GroupLifeGoods
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个GroupLifeGoods
	 * 
	 * @param id
	 *            需要更新的GroupLifeGoods的id
	 * @param dir
	 *            需要更新的GroupLifeGoods
	 */
	boolean update(GroupLifeGoods instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<GroupLifeGoods> query(String query, Map params, int begin, int max);
}
