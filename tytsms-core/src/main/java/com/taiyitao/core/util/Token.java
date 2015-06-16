package com.taiyitao.core.util;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

/**
 * 
 * 功能描述：防重复提交令牌类
 * <p>
 * 版权所有：广州泰易淘网络科技有限公司
 * <p>
 * 未经本公司许可，不得以任何方式复制或使用本程序任何部分
 * 
 * @author lixiandi 新增日期：2014年9月22日
 * @author lixiandi 修改日期：2014年9月22日
 * 
 */
public  class Token {

	private static final String TOKEN_LIST_NAME = "tokenList";

	public static final String TOKEN_STRING_NAME = "token";

	private static final Token instances = new Token();

	private Token() {
	}

	public static Token getInstance() {
		return instances;
	}

	private static List<String> getTokenList(HttpSession session) {
		Object obj = session.getAttribute(TOKEN_LIST_NAME);
		if (obj != null) {
			return (List<String>) obj;
		} else {
			List<String> tokenList = new ArrayList();
			session.setAttribute(TOKEN_LIST_NAME, tokenList);
			return tokenList;
		}
	}

	private static void saveTokenString(String tokenStr, HttpSession session) {
		List<String> tokenList = getTokenList(session);
		tokenList.add(tokenStr);
		session.setAttribute(TOKEN_LIST_NAME, tokenList);
	}

	private static String generateTokenString() {
		return new Long(System.currentTimeMillis()).toString();
	}

	/**
	 * 
	 * 方法用途和描述：产生一个令牌字符串，并保存到session，返回该令牌字符串
	 * <p>
	 * 方法的实现逻辑描述（如果是接口方法可以不写）：
	 * 
	 * @param session
	 * @return
	 * 
	 * @author lixiandi 新增日期：2014年9月22日
	 * @author lixiandi 修改日期：2014年9月22日
	 */
	public static String getTokenString(HttpSession session) {
		String tokenStr = generateTokenString();
		saveTokenString(tokenStr, session);
		return tokenStr;
	}

	/**
	 * 
	 * 方法用途和描述：验证token是否合法，如果session中存在token，返回true，否则返回false
	 * <p>
	 * 方法的实现逻辑描述（如果是接口方法可以不写）：
	 * 
	 * @param tokenStr
	 * @param session
	 * @return true: session中存在token; false: session为null或者session中不存在token
	 * 
	 * @author lixiandi 新增日期：2014年9月22日
	 * @author lixiandi 修改日期：2014年9月22日
	 */
	public static boolean isTokenStringValid(String tokenStr, HttpSession session) {
		boolean valid = false;
		if (session != null) {
			List<String> tokenList = getTokenList(session);
			if (tokenList.contains(tokenStr)) {
				valid = true;
				tokenList.remove(tokenStr);
			}
		}
		return valid;
	}

	// 这两个方法供页面使用
	public String getTOKEN_LIST_NAME() {
		return TOKEN_LIST_NAME;
	}

	public String getTOKEN_STRING_NAME() {
		return TOKEN_STRING_NAME;
	}

	// 供页面使用
	public Token getToken() {
		return Token.getInstance();
	}
}
