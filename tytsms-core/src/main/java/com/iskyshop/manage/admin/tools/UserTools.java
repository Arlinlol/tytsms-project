package com.iskyshop.manage.admin.tools;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.concurrent.SessionInformation;
import org.springframework.security.concurrent.SessionRegistry;
import org.springframework.stereotype.Component;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IUserService;

/**
 * 
 * <p>
 * Title: UserTools.java
 * </p>
 * 
 * <p>
 * Description: 在线用户查询管理工具类，查询所有登录用户，判断用户是否在线
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * Company: 沈阳网之商科技有限公司 www.iskyshop.com
 * </p>
 * 
 * @author erikzhang
 * 
 * @date 2014-7-28
 * 
 * @version iskyshop_b2b2c 1.0
 */
@Component
public class UserTools {
	@Autowired
	private SessionRegistry sessionRegistry;
	@Autowired
	private IUserService userSerivce;

	/**
	 * 获取所有在线用户列表
	 * 
	 * @return
	 */
	public List<User> query_user() {
		List<User> users = new ArrayList<User>();
		Object[] objs = this.sessionRegistry.getAllPrincipals();
		for (int i = 0; i < objs.length; i++) {
			User user = this.userSerivce.getObjByProperty("userName",
					CommUtil.null2String(objs[i]));
			// System.out.println(user.getUsername());
			users.add(user);
			// 第二个参数是否单点登录时，第二个登录用户被弹出
			SessionInformation[] ilist = this.sessionRegistry.getAllSessions(
					objs[i], true);
			// System.out.println(user.getUsername() + ilist.length + "处登录");
			if (ilist.length > 1) {
				for (int j = 1; j < ilist.length; j++) {
					SessionInformation sif = ilist[j];
					user = (User) this.userSerivce.getObjByProperty("userName",
							CommUtil.null2String(sif.getPrincipal()));
					// 以下两种方法踢出用户
					sif.expireNow();
					this.sessionRegistry.removeSessionInformation(sif
							.getSessionId());
				}
			}
		}
		return users;
	}

	/**
	 * 根据用户名判断用户是否在线
	 * 
	 * @param userName
	 * @return
	 */
	public boolean userOnLine(String userName) {
		boolean ret = false;
		List<User> users = this.query_user();
		for (User user : users) {
			if (user != null
					&& user.getUsername()
							.equals(CommUtil.null2String(userName))) {
				ret = true;
				break;
			}
		}
		return ret;
	}

	/**
	 * 判断是否有管理员在线
	 * 
	 * @return
	 */
	public boolean adminOnLine() {
		boolean ret = false;
		List<User> users = this.query_user();
		for (User user : users) {
			if (user != null && user.getUserRole().equals("ADMIN")) {
				ret = true;
				break;
			}
		}
		return ret;
	}
}
