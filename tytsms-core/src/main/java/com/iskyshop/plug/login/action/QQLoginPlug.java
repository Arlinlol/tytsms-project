package com.iskyshop.plug.login.action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.Md5Encrypt;
import com.iskyshop.foundation.domain.Album;
import com.iskyshop.foundation.domain.IntegralLog;
import com.iskyshop.foundation.domain.Role;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IAlbumService;
import com.iskyshop.foundation.service.IIntegralLogService;
import com.iskyshop.foundation.service.IRoleService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;

/**
 * 
 * <p>
 * Title: QQLoginPlug.java
 * </p>
 * 
 * <p>
 * Description: QQ登录插件，该插件完成QQ登录授权，登录后自动完成用户注册并自动登录，默认注册用户密码为123456
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
 * @date 2014-5-16
 * 
 * @version iskyshop_b2b2c 1.0
 */
@Controller
public class QQLoginPlug {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IRoleService roleService;
	@Autowired
	private IAlbumService albumService;
	@Autowired
	private IIntegralLogService integralLogService;
	private String qq_login_url = "https://graph.qq.com/oauth2.0/authorize";
	private String qq_access_token = "https://graph.qq.com/oauth2.0/authorize";

	/**
	 * 导向QQ登录授权页面
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/qq_login_api.htm")
	public void qq_login_api(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String redirect_uri = CommUtil.encode(CommUtil.getURL(request)
				+ "/qq_login_bind.htm");
		String auth_url = "https://graph.qq.com/oauth2.0/authorize?response_type=code&client_id="
				+ this.configService.getSysConfig().getQq_login_id()
				+ "&redirect_uri="
				+ redirect_uri
				+ "&state=iskyshop&scope=get_user_info";
		response.sendRedirect(auth_url);
	}

	/**
	 * 完成QQ授权回到商城页面，读取QQ用户名信息，自动注册一个账号并完成登录，用户可以选择修改密码或者绑定已经有的账号
	 * 
	 * @param request
	 * @param response
	 * @param code
	 * @return
	 */
	@RequestMapping("/qq_login_bind.htm")
	public String qq_login_bind(HttpServletRequest request,
			HttpServletResponse response, String code) {
		String redirect_uri = CommUtil.encode(CommUtil.getURL(request)
				+ "/qq_login_bind.htm");
		String token_url = "https://graph.qq.com/oauth2.0/token?grant_type=authorization_code&client_id="
				+ this.configService.getSysConfig().getQq_login_id()
				+ "&client_secret="
				+ this.configService.getSysConfig().getQq_login_key()
				+ "&code=" + code + "&redirect_uri=" + redirect_uri;
		// access_token=1CA359B424836978AAA1424B83C1B5A3&expires_in=7776000&refresh_token=88E49894ECE227B7BEFE58877B292BA3
		String[] access_token_callback = CommUtil.null2String(
				this.getHttpContent(token_url, "UTF-8", "GET")).split("&");
		String access_token = access_token_callback[0].split("=")[1];
		String me_url = "https://graph.qq.com/oauth2.0/me?access_token="
				+ access_token;
		// callback(
		// {"client_id":"100359491","openid":"9A6383AD4B58E8B1ACF65DC68E0B3B68"}
		// );
		String me_callback = CommUtil
				.null2String(this.getHttpContent(me_url, "UTF-8", "GET"))
				.replaceAll("callback\\(", "").replaceAll("\\);", "");
		Map me_map = Json.fromJson(HashMap.class, me_callback);
		String qq_openid = CommUtil.null2String(me_map.get("openid"));
		String user_info_url = "https://graph.qq.com/user/get_user_info?access_token="
				+ access_token
				+ "&oauth_consumer_key="
				+ me_map.get("client_id") + "&openid=" + qq_openid;
		String user_info_callback = this.getHttpContent(user_info_url, "UTF-8",
				"GET");
		Map user_map = Json.fromJson(HashMap.class, user_info_callback);
		System.out.println("用户名：" + user_map.get("nickname"));
		if (SecurityUserHolder.getCurrentUser() == null) {// 使用QQ账号登录
			String userName = this.generic_username(CommUtil
					.null2String(user_map.get("nickname")));
			User user = this.userService.getObjByProperty("qq_openid",
					qq_openid);
			if (user == null) {
				user = new User();
				user.setUserName(userName);
				user.setUserRole("BUYER");
				user.setQq_openid(qq_openid);
				user.setAddTime(new Date());
				user.setPassword(Md5Encrypt.md5("123456").toLowerCase());
				Map params = new HashMap();
				params.put("type", "BUYER");
				List<Role> roles = this.roleService.query(
						"select obj from Role obj where obj.type=:type",
						params, -1, -1);
				user.getRoles().addAll(roles);
				if (this.configService.getSysConfig().isIntegral()) {
					user.setIntegral(this.configService.getSysConfig()
							.getMemberRegister());
					this.userService.save(user);
					IntegralLog log = new IntegralLog();
					log.setAddTime(new Date());
					log.setContent("注册赠送积分:"
							+ this.configService.getSysConfig()
									.getMemberRegister());
					log.setIntegral(this.configService.getSysConfig()
							.getMemberRegister());
					log.setIntegral_user(user);
					log.setType("reg");
					this.integralLogService.save(log);
				} else {
					this.userService.save(user);
				}
				// 创建用户默认相册
				Album album = new Album();
				album.setAddTime(new Date());
				album.setAlbum_default(true);
				album.setAlbum_name("默认相册");
				album.setAlbum_sequence(-10000);
				album.setUser(user);
				this.albumService.save(album);
				request.getSession(false).removeAttribute("verify_code");
				request.getSession(false).setAttribute("bind", "qq");
				return "redirect:" + CommUtil.getURL(request)
						+ "/iskyshop_login.htm?username="
						+ CommUtil.encode(user.getUsername())
						+ "&password=123456";
			} else {
				request.getSession(false).removeAttribute("verify_code");
				return "redirect:" + CommUtil.getURL(request)
						+ "/iskyshop_login.htm?username="
						+ CommUtil.encode(user.getUsername()) + "&password="
						+ Globals.THIRD_ACCOUNT_LOGIN + user.getPassword();
			}
		} else {// 用户已经登录，在用户中心绑定QQ账号
			User user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
			user.setQq_openid(qq_openid);
			this.userService.update(user);
			return "redirect:" + CommUtil.getURL(request)
					+ "/buyer/account_bind.htm";
		}

	}

	/**
	 * * 完成QQ账号绑定，默认账号密码为123456，到这里用户已经自动登录，用户可以选择修改密码，或者绑定已有商城用户，如绑定现有用户，
	 * 需要输入用户密码进行确认，密码正确后， 自动完成绑定并用现有用户账户登录
	 * 
	 * 
	 * @param request
	 * @param response
	 * @param userName
	 * @param password
	 * @param bind_already
	 * @return
	 */
	@RequestMapping("/qq_login_bind_finish.htm")
	public String qq_login_bind_finish(HttpServletRequest request,
			HttpServletResponse response, String userName, String password,
			String bind_already) {
		String url = "redirect:" + CommUtil.getURL(request) + "/index.htm";
		if (!CommUtil.null2String(bind_already).equals("")) {
			User user = this.userService.getObjByProperty("userName", userName);
			if (user == null) {
				request.getSession(false).setAttribute("op_title", "用户绑定失败");
				request.getSession(false).setAttribute("url", url);
				url = "redirect:" + CommUtil.getURL(request) + "/error.htm";
			} else {
				if (Md5Encrypt.md5(password).toLowerCase()
						.equals(user.getPassword())) {
					user.setQq_openid(SecurityUserHolder.getCurrentUser()
							.getQq_openid());
					request.getSession(false).removeAttribute("verify_code");
					// 删除添加的用户
					this.userService.delete(SecurityUserHolder.getCurrentUser()
							.getId());
					url = "redirect:" + CommUtil.getURL(request)
							+ "/iskyshop_login.htm?username="
							+ CommUtil.encode(user.getUsername())
							+ "&password=" + password;
				} else {
					request.getSession(false)
							.setAttribute("op_title", "用户绑定失败");
					request.getSession(false).setAttribute("url",
							CommUtil.getURL(request) + "/index.htm");
					url = "redirect:" + CommUtil.getURL(request) + "/error.htm";
				}
			}

		} else {
			User user = SecurityUserHolder.getCurrentUser();
			user.setUserName(userName);
			user.setPassword(Md5Encrypt.md5(password).toLowerCase());
			this.userService.update(user);
		}
		request.getSession(false).removeAttribute("verify_code");
		request.getSession(false).removeAttribute("bind");
		return url;
	}

	/**
	 * 模拟http访问，返回相关数据
	 * 
	 * @param url
	 * @param charSet
	 * @param method
	 * @return
	 */
	public static String getHttpContent(String url, String charSet,
			String method) {
		HttpURLConnection connection = null;
		String content = "";
		try {
			URL address_url = new URL(url);
			connection = (HttpURLConnection) address_url.openConnection();
			connection.setRequestMethod("GET");
			// 设置访问超时时间及读取网页流的超时时间,毫秒值
			connection.setConnectTimeout(1000000);
			connection.setReadTimeout(1000000);
			// 得到访问页面的返回值
			int response_code = connection.getResponseCode();
			if (response_code == HttpURLConnection.HTTP_OK) {
				InputStream in = connection.getInputStream();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(in, charSet));
				String line = null;
				while ((line = reader.readLine()) != null) {
					content += line;
				}
				return content;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
		return "";
	}

	/**
	 * 生成不重复的用户名，这里使用1000000作为随即因子，系统用户超过千万级，可以修改该因子
	 * 
	 * @param userName
	 *            第三方账号用户名
	 * @return
	 */
	private String generic_username(String userName) {
		String name = userName;
		User user = this.userService.getObjByProperty("userName", name);
		if (user != null) {
			for (int i = 1; i < 1000000; i++) {
				name = name + i;
				user = this.userService.getObjByProperty("userName", name);
				if (user == null) {
					break;
				}
			}
		}
		return name;
	}

	/**
	 * 测试QQ登录，需要手工在浏览器中输入相关信息配合测试工作
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) {
		SysConfig config = new SysConfig();
		config.setQq_login_id("100359491");
		config.setQq_login_key("a34bcaef0487e650238983abc0fbae7c");
		String redirect_uri = CommUtil
				.encode("http://iskyshop.eicp.net/qq_login_bind.htm");
		String auth_url = "https://graph.qq.com/oauth2.0/authorize?response_type=code&client_id="
				+ config.getQq_login_id()
				+ "&redirect_uri="
				+ redirect_uri
				+ "&state=iskyshop&scope=get_user_info";
		System.out.println(auth_url);

		String token_url = "https://graph.qq.com/oauth2.0/token?grant_type=authorization_code&client_id="
				+ config.getQq_login_id()
				+ "&client_secret="
				+ config.getQq_login_key()
				+ "&code=9873676D49030659CF025A0B9FF9F0B8&redirect_uri="
				+ redirect_uri;

		// System.out.println(token_url);
		// System.out.println("返回值为：" + getHttpContent(token_url));
		String me_url = "https://graph.qq.com/oauth2.0/me?access_token=1CA359B424836978AAA1424B83C1B5A3";
		// System.out.println(me_url);

		String user_info_url = "https://graph.qq.com/user/get_user_info?access_token=1CA359B424836978AAA1424B83C1B5A3&oauth_consumer_key=100359491"
				+ "&openid=9A6383AD4B58E8B1ACF65DC68E0B3B68";
		// System.out.println(user_info_url);
		System.out.println("返回值为："
				+ getHttpContent(user_info_url, "UTF-8", "GET"));
	}
}
