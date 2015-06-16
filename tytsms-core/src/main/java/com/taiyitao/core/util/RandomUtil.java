package com.taiyitao.core.util;

import java.util.Random;
import org.apache.commons.codec.digest.DigestUtils;

/** 
 * Copyright (c) 2015,泰易淘科技有限公司 All rights reserved.
 *
 * @Description	:	随机生成字符串，数字等工具类
 *
 * @Package		:	com.taiyitao.core.util
 *
 * @ClassName	:	RandomUtil.java
 *
 * @Created 	:	2015年2月7日
 *
 * @Author		: 	nickey
 *
 * @Version	:	1.0.0
 */
public class RandomUtil {

	/**
	 * Desc:随机生成一个数字字符串 用户手机验证码等
	 * @param length 数字长度
	 * @return
	 */
	public static String getRandomString(int length) {
		String str = "0123456789";
		Random random = new Random();
		StringBuffer sf = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(10);
			sf.append(str.charAt(number));
		}
		return sf.toString();
	}
	
	/**
	 * Desc:生成邀请码
	 * @param username
	 * @return
	 */
	public static String getInvitationCode(String username) {
		String invitationCode = new String(DigestUtils.md5(username));
		return invitationCode;
	}
}
