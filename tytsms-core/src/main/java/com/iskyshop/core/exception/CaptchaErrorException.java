package com.iskyshop.core.exception;

import org.springframework.security.BadCredentialsException;

/**
 * 定义验证码错误异常
 */
public class CaptchaErrorException extends BadCredentialsException {
	private static final long serialVersionUID = -372929279327416389L;

	public CaptchaErrorException(String msg) {
		super(msg);
	}

	public CaptchaErrorException(String msg, Object extraInformation) {
		super(msg, extraInformation);
	}

	public CaptchaErrorException(String msg, Throwable t) {
		super(msg, t);
	}
}