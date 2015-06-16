package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import com.iskyshop.foundation.domain.VerifyCode;

public interface IVerifyCodeService {
	/**
	 * 保存一个MobileVerifyCode，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(VerifyCode instance);

	/**
	 * 根据一个ID得到MobileVerifyCode
	 * 
	 * @param id
	 * @return
	 */
	VerifyCode getObjById(Long id);

	/**
	 * 删除一个MobileVerifyCode
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);

	/**
	 * 批量删除MobileVerifyCode
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);

	/**
	 * 通过一个查询对象得到MobileVerifyCode
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);

	/**
	 * 更新一个MobileVerifyCode
	 * 
	 * @param id
	 *            需要更新的MobileVerifyCode的id
	 * @param dir
	 *            需要更新的MobileVerifyCode
	 */
	boolean update(VerifyCode instance);

	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<VerifyCode> query(String query, Map params, int begin, int max);

	/**
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 */
	VerifyCode getObjByProperty(String propertyName, Object value);
}
