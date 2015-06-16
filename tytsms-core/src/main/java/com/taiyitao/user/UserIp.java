package com.taiyitao.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.IdEntity;

/** 
 * Copyright (c) 2015,泰易淘科技有限公司 All rights reserved.
 *
 * @Description	:	用户IP实体
 *
 * @Package		:	com.taiyitao.user
 *
 * @ClassName	:	UserIp.java
 *
 * @Created 	:	2015年2月7日
 *
 * @Author		: 	nickey
 *
 * @Version	:	1.0.0
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "user_ip")
public class UserIp extends IdEntity{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1176739128845229847L;
	
	@Column(name="user_id",nullable=true)
	private Long userId;
	
	@Column(name="user_name")
	private String userName;
	
	@Column(name="user_ip")
	private String userIp;
	
	@Column(name="login_address")
	private String loginAddress;
	
	@Column(name="login_info")
	private String loginInfo;

	public UserIp() {
		
	}
	
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getLoginAddress() {
		return loginAddress;
	}

	public void setLoginAddress(String loginAddress) {
		this.loginAddress = loginAddress;
	}

	public String getLoginInfo() {
		return loginInfo;
	}

	public void setLoginInfo(String loginInfo) {
		this.loginInfo = loginInfo;
	}

	public String getUserIp() {
		return userIp;
	}

	public void setUserIp(String userIp) {
		this.userIp = userIp;
	}
	
	

}
