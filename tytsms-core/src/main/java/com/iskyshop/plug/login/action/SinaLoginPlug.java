package com.iskyshop.plug.login.action;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.protocol.Protocol;
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
 * Title: SinaLoginPlug.java
 * </p>
 * 
 * <p>
 * Description:
 * 新浪登录插件,完成新浪账号登录返回到商城系统，登录成功返回自动完成用户注册并登陆，跳转到绑定页面，用户可以选择修改默认密码123456，也可以绑定已有用户
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
public class SinaLoginPlug {
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
	private String sina_login_url = "https://api.weibo.com/oauth2/authorize";
	private String sina_token_url = "https://api.weibo.com/oauth2/access_token";
	private String sina_token_info_url = "https://api.weibo.com/oauth2/get_token_info";

	/**
	 * 导向新浪微博登录授权页面
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/sina_login_api.htm")
	public void sina_login_api(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		SysConfig config = this.configService.getSysConfig();
		String url = this.sina_login_url + "?client_id="
				+ config.getSina_login_id() + "&redirect_uri="
				+ CommUtil.getURL(request) + "/sina_login_bind.htm";
		response.sendRedirect(url);
	}

	/**
	 * 新浪微博授权成功后返回商城，自动注册用户并登录
	 * 
	 * @param request
	 * @param response
	 * @param code
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	@RequestMapping("/sina_login_bind.htm")
	public String sina_login_bind(HttpServletRequest request,
			HttpServletResponse response, String code) throws HttpException,
			IOException {
		String sina_openid = "-1";
		String userName = "";
		String redirect_uri = CommUtil.encode(CommUtil.getURL(request)
				+ "/sina_login_bind.htm");
		String auth_url = "https://api.weibo.com/oauth2/authorize?client_id="
				+ this.configService.getSysConfig().getSina_login_id()
				+ "&response_type=code&redirect_uri=" + redirect_uri;
		String token_url = "https://api.weibo.com/oauth2/access_token?client_id="
				+ this.configService.getSysConfig().getSina_login_id()
				+ "&client_secret="
				+ this.configService.getSysConfig().getSina_login_key()
				+ "&grant_type=authorization_code&redirect_uri="
				+ redirect_uri
				+ "&code=" + code;
		HttpClient client = new HttpClient();
		Protocol myhttps = new Protocol("https",
				new MySecureProtocolSocketFactory(), 443);
		Protocol.registerProtocol("https", myhttps);
		PostMethod method = new PostMethod(token_url);
		int status = client.executeMethod(method);
		if (status == HttpStatus.SC_OK) {
			Map map = Json.fromJson(HashMap.class,
					method.getResponseBodyAsString());
			String access_token = CommUtil.null2String(map.get("access_token"));
			String token_info_url = "https://api.weibo.com/oauth2/get_token_info";
			method = new PostMethod(token_info_url);
			method.addParameter("access_token", access_token);
			status = client.executeMethod(method);
			if (status == HttpStatus.SC_OK) {
				map = Json.fromJson(HashMap.class,
						method.getResponseBodyAsString());
				sina_openid = CommUtil.null2String(map.get("uid"));
				String user_info_url = "https://api.weibo.com/2/users/show.json?access_token="
						+ access_token + "&uid=" + sina_openid;
				GetMethod get = new GetMethod(user_info_url);
				status = client.executeMethod(get);
				if (status == HttpStatus.SC_OK) {
					map = Json.fromJson(HashMap.class,
							get.getResponseBodyAsString());
					userName = CommUtil.null2String(map.get("name"));
					userName = this.generic_username(userName);
				}
			}
		}
		if (SecurityUserHolder.getCurrentUser() == null) {// 使用新浪微博账号登录
			List<User> users = this.userService.query("select obj from User obj where obj.sina_openid="+sina_openid,
					null, -1,-1);
			if (users.size()==0) {
				User user = new User();
				user.setUserName(userName);
				user.setUserRole("BUYER");
				user.setSina_openid(sina_openid);
				user.setAddTime(new Date());
				user.setPassword(Md5Encrypt.md5("123456").toLowerCase());
				this.userService.save(user);
				Map params = new HashMap();
				params.put("type", "BUYER");
				List<Role> roles = this.roleService.query(
						"select obj from Role obj where obj.type=:type",
						params, -1, -1);
				user.getRoles().addAll(roles);
				if (this.configService.getSysConfig().isIntegral()) {
					user.setIntegral(this.configService.getSysConfig()
							.getMemberRegister());
					this.userService.update(user);
					IntegralLog log = new IntegralLog();
					log.setAddTime(new Date());
					log.setContent("注册赠送积分:"
							+ +this.configService.getSysConfig()
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
				request.getSession(false).setAttribute("bind", "sina");
				return "redirect:" + CommUtil.getURL(request)
						+ "/iskyshop_login.htm?username="
						+ CommUtil.encode(user.getUsername())
						+ "&password=123456";
			} else {
				request.getSession(false).removeAttribute("verify_code");
				return "redirect:" + CommUtil.getURL(request)
						+ "/iskyshop_login.htm?username="
						+ CommUtil.encode(users.get(0).getUsername()) + "&password="
						+ Globals.THIRD_ACCOUNT_LOGIN + users.get(0).getPassword();
			}
		} else {// 用户已经登录，在用户中心绑定新浪微博
			List<User> users = this.userService.query("select obj from User obj where obj.sina_openid="+sina_openid,
					null, -1,-1);
			if(users.size()==0){
				User user = this.userService.getObjById(SecurityUserHolder
						.getCurrentUser().getId());
				user.setSina_openid(sina_openid);
				this.userService.update(user);
				return "redirect:" + CommUtil.getURL(request)
						+ "/buyer/account_bind.htm";				
			}
			return "redirect:" + CommUtil.getURL(request)
					+ "/buyer/account_bind.htm?error=true";
		}

	}

	/**
	 * 完成新浪微博登录信息，这里可以修改默认密码123456，也可以绑定已有账号信息
	 * 
	 * @param request
	 * @param response
	 * @param sina_bind_type
	 * @param sina_openid
	 * @param userName
	 * @param password
	 * @param email
	 * @return
	 */
	@RequestMapping("/sina_login_bind_finish.htm")
	public String sina_login_bind_finish(HttpServletRequest request,
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
	 * 生成不重复的用户名
	 * 
	 * @param userName
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
	 * 测试新浪微博登录
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		SysConfig config = new SysConfig();
		config.setSina_login_id("3863193702");
		config.setSina_login_key("16b62bbfc99c0d9028c199566429c798");
		String redirect_uri = CommUtil
				.encode("http://iskyshop.eicp.net/sina_login_bind.htm");
		String auth_url = "https://api.weibo.com/oauth2/authorize?client_id="
				+ config.getSina_login_id()
				+ "&response_type=code&redirect_uri=" + redirect_uri;
		System.out.println(auth_url);
		String token_url = "https://api.weibo.com/oauth2/access_token?client_id="
				+ config.getSina_login_id()
				+ "&client_secret="
				+ config.getSina_login_key()
				+ "&grant_type=authorization_code&redirect_uri="
				+ redirect_uri
				+ "&code=d729149f1c0db4a07a4b04fd45a5741d";
		System.out.println(token_url);
		HttpClient client = new HttpClient();
		Protocol myhttps = new Protocol("https",
				new MySecureProtocolSocketFactory(), 443);
		Protocol.registerProtocol("https", myhttps);
		PostMethod method = new PostMethod(token_url);
		int status = client.executeMethod(method);
		if (status == HttpStatus.SC_OK) {
			Map map = Json.fromJson(HashMap.class,
					method.getResponseBodyAsString());
			String access_token = CommUtil.null2String(map.get("access_token"));
			System.out.println("access_token:" + access_token);
			String token_info_url = "https://api.weibo.com/oauth2/get_token_info";
			method = new PostMethod(token_info_url);
			method.addParameter("access_token", access_token);
			status = client.executeMethod(method);
			if (status == HttpStatus.SC_OK) {
				map = Json.fromJson(HashMap.class,
						method.getResponseBodyAsString());
				String uid = CommUtil.null2String(map.get("uid"));
				System.out.println("uid:" + uid);
				String user_info_url = "https://api.weibo.com/2/users/show.json?access_token="
						+ access_token + "&uid=" + uid;
				GetMethod get = new GetMethod(user_info_url);
				status = client.executeMethod(get);
				if (status == HttpStatus.SC_OK) {
					map = Json.fromJson(HashMap.class,
							get.getResponseBodyAsString());
					System.out.println(method.getResponseBodyAsString());
					String userName = CommUtil.null2String(map.get("name"));
					System.out.println("userName:" + userName);
				}
			}
		}

	}
}
