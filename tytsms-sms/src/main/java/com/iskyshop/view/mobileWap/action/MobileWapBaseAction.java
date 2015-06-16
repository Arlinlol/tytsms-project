package com.iskyshop.view.mobileWap.action;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IUserService;

/**
 * 
 * <p>
 * Title: MobilePayViewAction.java
 * </p>
 * 
 * <p>
 * Description: 手机端订单在线支付回调控制器
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
 * @author hezeng
 * 
 * @date 2014-8-18
 * 
 * @version 1.0
 */
@Controller
public  class MobileWapBaseAction {
	
	@Autowired
	private  IUserService userService;
	
	public  Map  checkLogin(HttpServletRequest request ,String user_id) {
		Map params = new HashMap();
		boolean login = false;
		HttpSession session = request.getSession();
		if((user_id == null || ("").equals(user_id) )&& session.getAttribute("user_id") !=null){
			user_id = session.getAttribute("user_id").toString();
		}
		String openId = "" ;
		User user = (User) session.getAttribute("user") ;
		if((user_id == null || ("").equals(user_id) ) && session.getAttribute("openId") !=null) {
			openId = session.getAttribute("openId").toString();
			user = userService.getObjByProperty("weiXinOpenId",openId);
			if(user!=null &&user.getId()!=null){
				user_id =  user.getId().toString();
				session.putValue("user_id", user_id);
			}
		}
		if(CommUtil.isNotNull(user_id)){
			login = true;
			if(user == null){
				user = userService.getObjById(CommUtil.null2Long(user_id));
			}
		}
		session.putValue("user_id", user_id);
		session.putValue("user", user);
		params.put("user_id", user_id);
		params.put("login", login);
		params.put("user", user);
		return params;
	}

}
