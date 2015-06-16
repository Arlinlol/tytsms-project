package com.iskyshop.view.web.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.MsgTools;
import com.taiyitao.core.util.RandomUtil;

/**
 * Copyright (c) 2015,泰易淘科技有限公司 All rights reserved.
 *
 * @Description : 手机短信息处理类
 *
 * @Package : com.iskyshop.view.web.action
 *
 * @ClassName : MessageAction.java
 *
 * @Created : 2015年2月7日
 *
 * @Author : nickey
 *
 * @Version : 1.0.0
 */
@Controller
public class MessageAction {

	@Autowired
	private IUserService userSerivce;

	@Autowired
	private ISysConfigService configService;

	@Autowired
	private MsgTools msgTools;

	/**
	 * Desc:发送短信验证信息，如果用户已经注册，不能发送短信，防止恶意刷短信
	 * 
	 * @param request
	 * @param response
	 * @param mobile
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping("/sendMsg.htm")
	public void sendMsg(HttpServletRequest request,
			HttpServletResponse response, String mobile)
			throws UnsupportedEncodingException {
		HttpSession session = request.getSession();
		Long smsBeginTime = (Long) session.getAttribute("smsBeginTime");
		// 120秒内不允许重新发送短信
		if (smsBeginTime != null
				&& System.currentTimeMillis() - smsBeginTime < 120 * 1000) {
			return;
		}

		String ip = request.getRemoteAddr();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("mobile", mobile);
		List<User> users = userSerivce.query(
				"select obj from User obj where obj.mobile=:mobile", params,
				-1, -1);
		if (users == null || users.size() == 0) {
			String sms = RandomUtil.getRandomString(6);
			String content = "短信验证码为："
					+ sms
					+ "（泰易淘www.taiyitao.com手机动态码，请完成验证）。如有疑问请与我们客服人员联系，谢谢您的支持！";
			boolean ret = this.msgTools.sendSMS(mobile, content);
			if (ret == true) {
				session.setAttribute("sms", sms);
				session.setAttribute("smsBeginTime", System.currentTimeMillis());
			}
		} else {
			System.out.println(ip);
		}
	}

	/**
	 * Desc:注册时候验证短信
	 * 
	 * @param request
	 * @param response
	 * @param message
	 * @author nickey
	 */
	@RequestMapping("/verify_sms.htm")
	public void validateMessage(HttpServletRequest request,
			HttpServletResponse response, String sms) {

		HttpSession session = request.getSession();
		String message = (String) session.getAttribute("sms");
		Long smsBeginTime = (Long) session.getAttribute("smsBeginTime");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			if (smsBeginTime != null && sms != null && message != null
					&& sms.equals(message)) {

				writer.print(true);
			} else {
				writer.print(false);
			}
		} catch (IOException e) {

		}

	}

}
