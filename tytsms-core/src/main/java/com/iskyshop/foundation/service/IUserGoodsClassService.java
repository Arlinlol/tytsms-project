package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.foundation.domain.UserGoodsClass;

public interface IUserGoodsClassService {
	/**
	 * 保存一个UserGoodsClass，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(UserGoodsClass instance);
	
	/**
	 * 根据一个ID得到UserGoodsClass
	 * 
	 * @param id
	 * @return
	 */
	UserGoodsClass getObjById(Long id);
	
	/**
	 * 删除一个UserGoodsClass
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除UserGoodsClass
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到UserGoodsClass
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个UserGoodsClass
	 * 
	 * @param id
	 *            需要更新的UserGoodsClass的id
	 * @param dir
	 *            需要更新的UserGoodsClass
	 */
	boolean update(UserGoodsClass instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<UserGoodsClass> query(String query, Map params, int begin, int max);
}
