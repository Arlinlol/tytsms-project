package com.taiyitao.user.service;

import java.util.List;
import java.util.Map;

import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import com.taiyitao.user.UserIp;

/** 
 * Copyright (c) 2015,泰易淘科技有限公司 All rights reserved.
 *
 * @Description	:	用户IP信息处理业务接口
 *
 * @Package		:	com.taiyitao.user.service
 *
 * @ClassName	:	IUserIpService.java
 *
 * @Created 	:	2015年2月7日
 *
 * @Author		: 	nickey
 *
 * @Version	:	1.0.0
 */
public interface IUserIpService {
	
	/**
	 * Desc:查询所有UserIp
	 * @return
	 * @author nickey
	 * date:	2015-02-06
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * Desc:根据ID获取UserIp
	 * @param id
	 * @return
	 * @author nickey
	 * date:	2015-02-06
	 */
	UserIp getUserIpById(Long id);
	
	/**
	 * Desc:保存UserIp
	 * @param userIp
	 * @return
	 * @author nickey
	 * date:	2015-02-06
	 */
	boolean save(UserIp userIp);
	
	/**
	 * Desc:删除UserIp
	 * @param userIp
	 * @return
	 * @author nickey
	 * date:	2015-02-06
	 */
	boolean delete(Long id);
	
	/**
	 * Desc:批量删除UserIp
	 * @param userIp
	 * @return
	 * @author nickey
	 * date:	2015-02-06
	 */
	boolean batcherDelete(List<Long> ids);
	
	/**
	 * Desc:根据条件查询UserIp
	 * @param hql HQL查询语句
	 * @param params 查询条件
	 * @param begin 
	 * @param max
	 * @return
	 * @author nickey 
	 * date:	2015-02-06
	 */
	List<UserIp> query(String hql, Map<String, Object> params, Integer begin, Integer max);
	
	/**
	 * Desc:跟新UserIp
	 * @param userIp
	 * @return
	 * @author nickey
	 * date:	2015-02-06
	 */
	boolean update(UserIp userIp);
	
}
