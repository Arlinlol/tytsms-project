package com.iskyshop.foundation.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.IdEntity;

/**
 * 
 * <p>
 * Title: MobileVerifyCode.java
 * </p>
 * 
 * <p>
 * Description:验证码(包括手机验证码、邮件验证码)保存,该实体不需要缓存，存在即时性,系统开通短信发送后，用户修改手机号码，
 * 可以使用手机短信验证用户的合法性,验证码有效期为30分钟，30分钟后系统定时器会自动删除验证码信息
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
 * @date 2014-4-25
 * 
 * @version iskyshop_b2b2c 1.0
 */
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "verifycode")
public class VerifyCode extends IdEntity {
	private String userName;// 索取验证码的用户名
	private String email;// 验证码对应的邮箱号
	private String mobile;// 验证码对应的手机号码
	private String code;// 对应的验证码

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
