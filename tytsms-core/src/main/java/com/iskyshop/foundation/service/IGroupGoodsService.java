package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.foundation.domain.GroupGoods;

public interface IGroupGoodsService {
	/**
	 * 保存一个GroupGoods，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(GroupGoods instance);
	
	/**
	 * 根据一个ID得到GroupGoods
	 * 
	 * @param id
	 * @return
	 */
	GroupGoods getObjById(Long id);
	
	/**
	 * 删除一个GroupGoods
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除GroupGoods
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到GroupGoods
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个GroupGoods
	 * 
	 * @param id
	 *            需要更新的GroupGoods的id
	 * @param dir
	 *            需要更新的GroupGoods
	 */
	boolean update(GroupGoods instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<GroupGoods> query(String query, Map params, int begin, int max);
}
