package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.foundation.domain.GoldRecord;

public interface IGoldRecordService {
	/**
	 * 保存一个GoldRecord，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(GoldRecord instance);
	
	/**
	 * 根据一个ID得到GoldRecord
	 * 
	 * @param id
	 * @return
	 */
	GoldRecord getObjById(Long id);
	
	/**
	 * 删除一个GoldRecord
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除GoldRecord
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到GoldRecord
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个GoldRecord
	 * 
	 * @param id
	 *            需要更新的GoldRecord的id
	 * @param dir
	 *            需要更新的GoldRecord
	 */
	boolean update(GoldRecord instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<GoldRecord> query(String query, Map params, int begin, int max);
}
