package com.iskyshop.view.weixin.action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;
import net.sf.json.util.PropertyFilter;

import org.apache.commons.httpclient.HttpException;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.Md5Encrypt;
import com.iskyshop.foundation.domain.Album;
import com.iskyshop.foundation.domain.Document;
import com.iskyshop.foundation.domain.IntegralLog;
import com.iskyshop.foundation.domain.Role;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IAlbumService;
import com.iskyshop.foundation.service.IDocumentService;
import com.iskyshop.foundation.service.IIntegralLogService;
import com.iskyshop.foundation.service.IRoleService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.MsgTools;

/**
 * 
 * <p>
 * Title: MobileLoginViewAction.java
 * </p>
 * 
 * <p>
 * Description: 手机端登录请求管理类
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
 * @date 2014-7-22
 * 
 * @version 1.0
 */
@Controller
public class WeiXinLoginViewAction  extends WeiXinBaseAction{
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IRoleService roleService;
	@Autowired
	private IIntegralLogService integralLogService;
	@Autowired
	private IAlbumService albumService;
	@Autowired
	private IDocumentService documentService;
	
	@Autowired
	private MsgTools msgTools;


	

	/**
	 * 手机客户端用户登录
	 * 
	 * @param request
	 * @param response
	 * @param store_id
	 * @return
	 */
	@RequestMapping("/weiXin/iskyshop_user_login.htm")
	public void mobile_login(HttpServletRequest request,
			HttpServletResponse response, String userName, String password) {
		HttpSession session = request.getSession();
		String code = "-300";// 100,登陆成功,-100账号不存在，-200,密码不正确，-300登录失败
		Map json_map = new HashMap();
		boolean verify = CommUtil.null2Boolean(request.getHeader("verify"));
		verify = true;
		String user_id = "";
		if (verify) {// 头文件验证成功
			String user_name = "";
			String login_token = "";
			if (userName != null && !userName.equals("") && password != null
					&& !password.equals("")) {
				password = Md5Encrypt.md5(password).toLowerCase();
				Map map = new HashMap();
				map.put("userName", userName);
				map.put("mobile", userName);
				map.put("email", userName);
				
				List<User> users = this.userService
						.query("select obj from User obj where obj.userName=:userName or  obj.mobile=:mobile or obj.email=:email  order by addTime asc",
								map, -1, -1);
				if (users.size() > 0) {
					for (User u : users) {
						if (!u.getPassword().equals(password)) {
							code = "-200";
						} else {
							if (u.getUserRole().equalsIgnoreCase("admin")) {
								code = "-100";
							} else {
								user_id = CommUtil.null2String(u.getId());
								user_name = u.getUserName();
								code = "100";
								login_token = CommUtil.randomString(12)
										+ user_id;
								u.setApp_login_token(login_token.toLowerCase());
								if(session.getAttribute("openId") !=null){
									String openId = session.getAttribute("openId").toString();
									u.setWeiXinOpenId(openId);
								}
								this.userService.update(u);
							}
						}
					}
				} else {
					code = "-100";
				}
			}
			if (code.equals("100")) {

				json_map.put("user_id", user_id.toString());
				json_map.put("userName", user_name);
				json_map.put("token", login_token);
			}
		}
		json_map.put("code", code);
		session.putValue("user_id", user_id);
		String json = Json.toJson(json_map, JsonFormat.compact());
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(json);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * 手机客户端用户登录
	 * 
	 * @param request
	 * @param response
	 * @param store_id
	 * @return
	 */
	@RequestMapping("/weiXin/login.htm")
	public ModelAndView mobile_login(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("weiXin/view/login.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		return mv;
	}

	/**
	 * 手机客户端注册完成
	 * 
	 * @param request
	 * @param userName
	 * @param password
	 * @param email
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	@RequestMapping("/weiXin/register_finish.htm")
	public void mobile_register(HttpServletRequest request,
			HttpServletResponse response, String userName, String password,
			String type ,String sms) throws HttpException, IOException {
		boolean verify = CommUtil.null2Boolean(request.getHeader("verify"));
		verify = true ;
		boolean reg = true;// 防止机器注册，如后台开启验证码则强行验证验证码
		String regErrorMsg = "";// 注册错误信息
		//验证手机验证码
		HttpSession session = request.getSession();
		String smsCode = (String)session.getAttribute("sms");
		System.out.println(sms);
		if (smsCode == null || sms == null || !sms.equals(smsCode)) {
			reg = false;
			regErrorMsg ="parmError";
		}
		
		Map params = new HashMap();
		User user = new User();
		String login_token = "";
		if (verify) {// 头文件验证成功
			// 进一步控制用户名不能重复，防止在未开启注册码的情况下注册机恶意注册
			params.put("userName", userName);
			List<User> users = this.userService.query(
					"select obj from User obj where obj.userName=:userName",
					params, -1, -1);
			if (users != null && users.size() > 0) {
				reg = false;
				regErrorMsg ="IsHave";
			}
			if (reg) {
				
				if(session.getAttribute("openId") !=null){
					String openId = session.getAttribute("openId").toString();
					user.setWeiXinOpenId(openId);
				}		
		
				
				user.setUserName(userName);
				user.setMobile(userName);
				user.setUserRole("BUYER");
				user.setAddTime(new Date());
				user.setAvailableBalance(BigDecimal.valueOf(0));
				user.setFreezeBlance(BigDecimal.valueOf(0));
				// 生成token
				login_token = CommUtil.randomString(12)
						+ CommUtil.null2String(user.getId());
				user.setApp_login_token(login_token.toLowerCase());
				user.setPassword(Md5Encrypt.md5(password).toLowerCase());
				user.setUser_form(type);
				params.clear();
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
					log.setContent("用户注册增加"
							+ this.configService.getSysConfig()
									.getMemberRegister() + "分");
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
				params.clear();
				params.put("user_id", user.getId().toString());
				params.put("userName", user.getUserName());
				params.put("token", login_token);
			}
		} else {
			reg = false;
		}
		params.put("ret", reg);
		params.put("regErrorMsg", regErrorMsg);
		session.putValue("user_id", user.getId().toString());
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(params, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 手机客户端查看注册协议
	 * 
	 */
	@RequestMapping("/weiXin/register_doc.htm")
	public ModelAndView mobile_register_doc(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("weiXin/view/doc.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Document doc = this.documentService.getObjByProperty("mark",
				"reg_agree");
		mv.addObject("doc", doc);
		return mv;
	}
	
	
	/**
	 * 手机客户端查看注册协议
	 * 
	 */
	@RequestMapping("/weiXin/register.htm")
	public ModelAndView mobile_register(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("weiXin/view/register.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		return mv;
	}

	
	
	@RequestMapping("/weiXin/user/verify_mobile.htm")
	public void verify_mobile(HttpServletRequest request,
			HttpServletResponse response, String mobile, String id) {
		boolean ret = true;
		User usersInfo = this.userService.getObjByProperty("mobile",mobile);
		if (usersInfo != null && usersInfo.getId() !=null) {
			ret = false;
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@RequestMapping("/weiXin/user/sendMsg.htm")
	public void sendMsg(HttpServletRequest request,HttpServletResponse response, String mobile) throws UnsupportedEncodingException{
		HttpSession session = request.getSession();
		Long smsBeginTime = (Long)session.getAttribute("smsBeginTime");
		//120秒内不允许重新发送短信
		if (smsBeginTime != null && System.currentTimeMillis() - smsBeginTime < 120*1000) {
			return ;
		}
		
		String ip = request.getRemoteAddr();
		User usersInfo = this.userService.getObjByProperty("mobile",mobile);
		if (usersInfo == null || usersInfo.getId() == null) {
			String sms = CommUtil.randomInt(6);
			String content = "短信验证码为：" + sms;
			boolean ret = msgTools.sendSMS(mobile, content);
			if (ret == true) {
				session.setAttribute("sms", sms);
				session.setAttribute("smsBeginTime", System.currentTimeMillis());
			}
		} else {
			System.out.println(ip);
		}
		
	}
	
	
}
