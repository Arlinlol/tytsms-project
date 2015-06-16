package com.iskyshop.core.security.support;

import org.springframework.dao.DataAccessException;
import org.springframework.security.AuthenticationException;
import org.springframework.security.AuthenticationServiceException;
import org.springframework.security.BadCredentialsException;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.security.providers.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.providers.dao.SaltSource;
import org.springframework.security.providers.encoding.Md5PasswordEncoder;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UserDetailsService;
import org.springframework.util.Assert;

import com.iskyshop.core.constant.Globals;

/**
 * 
 * <p>
 * Title: ShopAuthenticationProvider.java
 * </p>
 * 
 * <p>
 * Description:
 * 重写SpringSecurityAbstractUserDetailsAuthenticationProvider，用来控制第三方账号登陆
 * ，第三方账号登录可以使用MD5后密码进行登录 ,MD5密码登录使用Globals.THIRD_ACCOUNT_LOGIN作为前缀标识，系统自动不进行处理
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
 * @date 2014-4-24
 * 
 * @version iskyshop_b2b2c 1.0
 */
public class ShopAuthenticationProvider extends
		AbstractUserDetailsAuthenticationProvider {
	private PasswordEncoder passwordEncoder = new Md5PasswordEncoder();
	private SaltSource saltSource;
	private UserDetailsService userDetailsService;
	private boolean includeDetailsObject = true;

	protected void additionalAuthenticationChecks(UserDetails userDetails,
			UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {
		Object salt = null;
		if (this.saltSource != null) {
			salt = this.saltSource.getSalt(userDetails);
		}
		if (authentication.getCredentials() == null) {
			throw new BadCredentialsException(this.messages.getMessage(
					"AbstractUserDetailsAuthenticationProvider.badCredentials",
					"Bad credentials"), this.includeDetailsObject ? userDetails
					: null);
		}
		String presentedPassword = authentication.getCredentials().toString();
		if (presentedPassword.indexOf(Globals.THIRD_ACCOUNT_LOGIN) >= 0) {
			presentedPassword = presentedPassword
					.substring(Globals.THIRD_ACCOUNT_LOGIN.length());
			if (!presentedPassword.equals(userDetails.getPassword())) {
				throw new BadCredentialsException(
						this.messages.getMessage(
								"AbstractUserDetailsAuthenticationProvider.badCredentials",
								"Bad credentials"),
						this.includeDetailsObject ? userDetails : null);
			}
		} else {
			if (!this.passwordEncoder.isPasswordValid(
					userDetails.getPassword(), presentedPassword, salt))
				throw new BadCredentialsException(
						this.messages.getMessage(
								"AbstractUserDetailsAuthenticationProvider.badCredentials",
								"Bad credentials"),
						this.includeDetailsObject ? userDetails : null);
		}
	}

	protected void doAfterPropertiesSet() throws Exception {
		Assert.notNull(this.userDetailsService,
				"A UserDetailsService must be set");
	}

	protected final UserDetails retrieveUser(String username,
			UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {
		UserDetails loadedUser;
//		try {
		    this.hideUserNotFoundExceptions=false;
			loadedUser = getUserDetailsService().loadUserByUsername(username);
//		} catch (Exception repositoryProblem) {
//		} catch (DataAccessException repositoryProblem) {
//			System.out.println(repositoryProblem.getStackTrace());
//			throw new AuthenticationServiceException(
//					repositoryProblem.getMessage(), repositoryProblem);
//		}

		if (loadedUser == null) {
			throw new AuthenticationServiceException(
					"UserDetailsService returned null, which is an interface contract violation");
		}

		return loadedUser;
	}

	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	protected PasswordEncoder getPasswordEncoder() {
		return this.passwordEncoder;
	}

	public void setSaltSource(SaltSource saltSource) {
		this.saltSource = saltSource;
	}

	protected SaltSource getSaltSource() {
		return this.saltSource;
	}

	public void setUserDetailsService(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	protected UserDetailsService getUserDetailsService() {
		return this.userDetailsService;
	}

	protected boolean isIncludeDetailsObject() {
		return this.includeDetailsObject;
	}

	/** @info */
	public void setIncludeDetailsObject(boolean includeDetailsObject) {
		this.includeDetailsObject = includeDetailsObject;
	}

}
