package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import com.iskyshop.foundation.domain.InviteRegister;

public interface IInviteRegisterService {
	/**
	 * 保存一个InviteRegister，如果保存成功返回true，否则返回false
	 * 
	 * @param inviteRegister
	 * @return 是否保存成功
	 */
	boolean save(InviteRegister inviteRegister);
	
	/**
	 * 根据一个ID得到InviteRegister
	 * 
	 * @param id
	 * @return
	 */
	InviteRegister getObjById(Long id);
	
	/**
	 * 删除一个InviteRegister
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除InviteRegister
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到InviteRegister
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个InviteRegister
	 * 
	 * @param id
	 *            需要更新的InviteRegister的id
	 * @param dir
	 *            需要更新的InviteRegister
	 */
	boolean update(InviteRegister inviteRegister);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<InviteRegister> query(String query, Map params, int begin, int max);

}
