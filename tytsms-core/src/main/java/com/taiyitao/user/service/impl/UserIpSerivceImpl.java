package com.taiyitao.user.service.impl;

import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iskyshop.core.dao.IGenericDAO;
import com.iskyshop.core.ip.IPSeeker;
import com.iskyshop.core.query.GenericPageList;
import com.iskyshop.core.query.PageObject;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.foundation.domain.ActivityGoods;
import com.iskyshop.foundation.domain.User;
import com.taiyitao.user.UserIp;
import com.taiyitao.user.service.IUserIpService;

/** 
 * Copyright (c) 2015,泰易淘科技有限公司 All rights reserved.
 *
 * @Description	:	用户IP信息处理业务实现
 *
 * @Package		:	com.taiyitao.user.service.impl
 *
 * @ClassName	:	UserIpSerivceImpl.java
 *
 * @Created 	:	2015年2月7日
 *
 * @Author		: 	nickey
 *
 * @Version	:	1.0.0
 */
@Service("userIpService")
@Transactional
public class UserIpSerivceImpl implements IUserIpService {
	
	private static Log _log = LogFactory.getLog(UserIpSerivceImpl.class.toString());
	

	@Resource(name = "userIpDao")
	private IGenericDAO<UserIp> userIpDao;

	@Override
	public IPageList list(IQueryObject properties) {
		// TODO Auto-generated method stub
		if (properties == null) {
			return null;
		}
		String query = properties.getQuery();
		Map params = properties.getParameters();
		GenericPageList pList = new GenericPageList(UserIp.class, query,
				params, this.userIpDao);
		if (properties != null) {
			PageObject pageObj = properties.getPageObj();
			if (pageObj != null)
				pList.doList(
						pageObj.getCurrentPage() == null ? 0 : pageObj
								.getCurrentPage(),
						pageObj.getPageSize() == null ? 0 : pageObj
								.getPageSize());
		} else
			pList.doList(0, -1);
		return pList;
	}

	@Override
	public UserIp getUserIpById(Long id) {
		// TODO Auto-generated method stub
		UserIp userIp = userIpDao.get(id);
		if (userIp != null) {
			return userIp;
		}
		return null;
	}

	@Override
	public boolean save(UserIp userIp) {
		// TODO Auto-generated method stub
		try {
			this.userIpDao.save(userIp);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean delete(Long id) {
		// TODO Auto-generated method stub
		try {
			this.userIpDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean batcherDelete(List<Long> ids) {
		// TODO Auto-generated method stub
		for (Long id : ids) {
			delete(id);
		}
		return true;
	}

	@Override
	public List<UserIp> query(String query, Map<String, Object> params,
			Integer begin, Integer max) {
		// TODO Auto-generated method stub
		return this.userIpDao.query(query, params, begin, max);
	}

	@Override
	public boolean update(UserIp userIp) {
		// TODO Auto-generated method stub
		try {
			this.userIpDao.update(userIp);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public void catchLoginInfo(JoinPoint j) {
		try {
			Object[] objs = j.getArgs();
			HttpServletRequest request = (HttpServletRequest)objs[0];
			StringBuffer sb = new StringBuffer();
			Enumeration<String> es = request.getHeaderNames();
			while (es.hasMoreElements()) {
				String key = es.nextElement();
				sb.append(key);
				sb.append("=");
				sb.append(request.getHeader(key));
				sb.append("|");
			}
			User user = SecurityUserHolder.getCurrentUser();
			String ip = request.getRemoteAddr();
			//调用纯真数据库查询地址
			IPSeeker ipSeeker = new IPSeeker(null, null);
			String loginAddress = ipSeeker.getIPLocation(ip).getCountry() + ":"
					+ ipSeeker.getIPLocation(ip).getArea();
			if("".equals(loginAddress) || loginAddress ==null){
				loginAddress = "广东省广州市";
			}else if("0:0:0:0:0:0:0:1".equals(ip)){
				loginAddress = "局域网";
			}
			UserIp userIp = new UserIp();
			userIp.setUserName(user.getUserName());
			userIp.setLoginAddress(loginAddress);
			userIp.setUserId(user.getId());
			userIp.setUserIp(ip);
			userIp.setAddTime(new Date());
			userIp.setLoginInfo(sb.toString());
			save(userIp);
		} catch (Exception e) {
			e.printStackTrace();
			_log.error("监控用户登录异常！异常信息："+e.getLocalizedMessage());
		}
	}

}
