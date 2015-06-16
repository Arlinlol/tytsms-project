package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import com.iskyshop.foundation.domain.Payment;

public interface IPaymentService {
	/**
	 * 保存一个Payment，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(Payment instance);

	/**
	 * 根据一个ID得到Payment
	 * 
	 * @param id
	 * @return
	 */
	Payment getObjById(Long id);

	/**
	 * 删除一个Payment
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);

	/**
	 * 批量删除Payment
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);

	/**
	 * 通过一个查询对象得到Payment
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);

	/**
	 * 更新一个Payment
	 * 
	 * @param id
	 *            需要更新的Payment的id
	 * @param dir
	 *            需要更新的Payment
	 */
	boolean update(Payment instance);

	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	public List<Payment> query(String query, Map params, int begin, int max);

	/**
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 */
	Payment getObjByProperty(String propertyName, String value);
}
