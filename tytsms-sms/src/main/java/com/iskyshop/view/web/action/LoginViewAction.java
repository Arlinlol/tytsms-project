package com.iskyshop.view.web.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.httpclient.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.HtmlUtils;

import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.Md5Encrypt;
import com.iskyshop.foundation.domain.Album;
import com.iskyshop.foundation.domain.Area;
import com.iskyshop.foundation.domain.Document;
import com.iskyshop.foundation.domain.IntegralLog;
import com.iskyshop.foundation.domain.Role;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.VerifyCode;
import com.iskyshop.foundation.service.IAlbumService;
import com.iskyshop.foundation.service.IAreaService;
import com.iskyshop.foundation.service.IDocumentService;
import com.iskyshop.foundation.service.IIntegralLogService;
import com.iskyshop.foundation.service.IRoleService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.foundation.service.IVerifyCodeService;
import com.iskyshop.manage.admin.tools.MsgTools;
import com.iskyshop.uc.api.UCClient;
import com.iskyshop.uc.api.UCTools;
import com.iskyshop.view.web.tools.ImageViewTools;
import com.taiyitao.user.service.IUserIpService;

/**
 * 
 * <p>
 * Title: LoginViewAction.java
 * </p>
 * 
 * <p>
 * Description: 用户登录、注册管理控制器，用来管理用户登录、注册、密码找回、UC统一登录等功能
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
 * @date 2014-5-13
 * 
 * @version iskyshop_b2b2c 1.0
 */
@Controller
public class LoginViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IRoleService roleService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IIntegralLogService integralLogService;
	@Autowired
	private IAlbumService albumService;
	@Autowired
	private IDocumentService documentService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private IVerifyCodeService verifyCodeService;
	@Autowired
	private ImageViewTools imageViewTools;
	@Autowired
	private MsgTools msgTools;
	@Autowired
	private UCTools ucTools;
	
	@Autowired
	private IUserIpService userIpService;

	/**
	 * 用户登录页面
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/user/login.htm")
	public ModelAndView login(HttpServletRequest request,
			HttpServletResponse response, String url) {
		ModelAndView mv = new JModelAndView("login.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		request.getSession(false).removeAttribute("verify_code");
		boolean domain_error = CommUtil.null2Boolean(request.getSession(false)
				.getAttribute("domain_error"));
		if (url != null && !url.equals("")) {
			request.getSession(false).setAttribute("refererUrl", url);
		}
	
		if (domain_error) {
			mv.addObject("op_title", "域名绑定错误，请与http://www.taiyitao.com联系");
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
		} else {
			mv.addObject("imageViewTools", imageViewTools);
		}
		mv.addObject("uc_logout_js",
				request.getSession(false).getAttribute("uc_logout_js"));
		String iskyshop_view_type = CommUtil.null2String(request.getSession(
				false).getAttribute("iskyshop_view_type"));
		if (iskyshop_view_type != null && !iskyshop_view_type.equals("")) {
			if (iskyshop_view_type.equals("weixin")) {
				String store_id = CommUtil.null2String(request
						.getSession(false).getAttribute("store_id"));
				mv = new JModelAndView("weixin/success.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "退出成功！");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/weixin/index.htm?store_id=" + store_id);
			}
		}
		return mv;
	}

	/**
	 * 用户注册页面
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/register.htm")
	public ModelAndView register(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("register.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		request.getSession(false).removeAttribute("verify_code");
		Document doc = this.documentService.getObjByProperty("mark",
				"reg_agree");
		doc.setContent(HtmlUtils.htmlUnescape(doc.getContent()));//反转义
		mv.addObject("doc", doc);
		mv.addObject("imageViewTools", imageViewTools);
		return mv;
	}

	/**
	 * 企业用户注册，打开企业注册入口页面
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	//@RequestMapping("/company/register.htm")
	public ModelAndView company_register(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("company_register.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		request.getSession(false).removeAttribute("verify_code");
		Document doc = this.documentService.getObjByProperty("mark",
				"reg_agree");
		mv.addObject("doc", doc);
		List<Area> areas = this.areaService.query(
				"select obj from Area obj where obj.parent.id is null", null,
				-1, -1);
		mv.addObject("areas", areas);
		return mv;
	}

	/**
	 * 注册完成
	 * 
	 * @param request
	 * @param userName
	 * @param password
	 * @param email
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	@RequestMapping("/register_finish.htm")
	@Transactional
	public String register_finish(HttpServletRequest request,
			HttpServletResponse response, String userName, String password,
			String email, String code, String user_type, String sms) throws HttpException,
			IOException {
		
		boolean reg = true;// 防止机器注册，如后台开启验证码则强行验证验证码
		
		//验证手机验证码
		/*HttpSession session = request.getSession();
		String smsCode = (String)session.getAttribute("sms");
		System.out.println(sms);
		if (smsCode == null || sms == null || !sms.equals(smsCode)) {
			reg = false;
		}*/
		
		if (code != null && !code.equals("")) {
			code = CommUtil.filterHTML(code);// 过滤验证码
		}
		if (this.configService.getSysConfig().isSecurityCodeRegister()) {
			if (!request.getSession(false).getAttribute("verify_code")
					.equals(code)) {
				reg = false;
			}
		}
		// 进一步控制用户名不能重复，防止在未开启注册码的情况下注册机恶意注册
		Map params = new HashMap();
		params.put("userName", userName);
		List users = this.userService.query(
				"select obj.id from User obj where obj.userName=:userName",
				params, -1, -1);
		if (users != null && users.size() > 0) {
			reg = false;
		}
		if (reg) {
			User user = new User();
			user.setUserName(userName);
			user.setUserRole("BUYER");
			user.setAddTime(new Date());
			user.setEmail(email);
			user.setAvailableBalance(BigDecimal.valueOf(0));
			user.setFreezeBlance(BigDecimal.valueOf(0));
			user.setMobile(request.getParameter("mobile"));
			if (user_type != null && !user_type.equals("")) {
				user.setUser_type(CommUtil.null2Int(user_type));
				user.setContact_user(request.getParameter("contact_user"));
				user.setDepartment(request.getParameter("department"));
				user.setTelephone(request.getParameter("telephone"));
				user.setCompany_name(request.getParameter("company_name"));
				Area area = this.areaService.getObjById(CommUtil.null2Long(request.getParameter("area_id")));
				user.setCompany_address(Area.getAreaInfo(area,"") +" "+request.getParameter("company_address"));
//				user.setCompany_address(area.getParent().getParent().getAreaName()+area.getParent().getAreaName()+area.getAreaName()+" "+request.getParameter("company_address"));
				if(request.getParameter("company_purpose")!=null&&!request.getParameter("company_purpose").equals("")){
					user.setCompany_purpose(request.getParameter("company_purpose").substring(0, request.getParameter("company_purpose").length()-1));
				}
				user.setCompany_url(request.getParameter("company_url"));
				user.setCompany_person_num(request.getParameter("company_person_num"));
				user.setCompany_trade(request.getParameter("company_trade"));
				user.setCompany_nature(request.getParameter("company_nature"));
			}
			user.setPassword(Md5Encrypt.md5(password).toLowerCase());
			params.clear();
			params.put("type", "BUYER");
			List<Role> roles = this.roleService.query(
					"select obj from Role obj where obj.type=:type", params,
					-1, -1);
			user.getRoles().addAll(roles);
			if (this.configService.getSysConfig().isIntegral()) {
				user.setIntegral(this.configService.getSysConfig()
						.getMemberRegister());
				this.userService.save(user);
				IntegralLog log = new IntegralLog();
				log.setAddTime(new Date());
				log.setContent("用户注册增加"
						+ this.configService.getSysConfig().getMemberRegister()
						+ "分");
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
			if (this.configService.getSysConfig().isUc_bbs()) {// 是否开启UC_bbs同步
				UCClient client = new UCClient();
				String ret = client.uc_user_register(userName, password, email);
				int uid = Integer.parseInt(ret);
				if (uid <= 0) {
					if (uid == -1) {
						System.out.print("用户名不合法");
					} else if (uid == -2) {
						System.out.print("包含要允许注册的词语");
					} else if (uid == -3) {
						System.out.print("用户名已经存在");
					} else if (uid == -4) {
						System.out.print("Email 格式有误");
					} else if (uid == -5) {
						System.out.print("Email 不允许注册");
					} else if (uid == -6) {
						System.out.print("该 Email 已经被注册");
					} else {
						System.out.print("未定义");
					}
				} else {
					this.ucTools.active_user(userName, user.getPassword(),
							email);
				}
			}
			return "redirect:iskyshop_login.htm?username="
					+ CommUtil.encode(userName) + "&password=" + password
					+ "&encode=true";
		} else {
			return "redirect:register.htm";
		}
	}

	/**
	 * springsecurity登录成功后跳转到该页面，如有登录相关处理可以在该方法中完成
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/user_login_success.htm")
	public ModelAndView user_login_success(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String url = CommUtil.getURL(request) + "/index.htm";
		String iskyshop_view_type = CommUtil.null2String(request.getSession(
				false).getAttribute("iskyshop_view_type"));
		if (iskyshop_view_type != null && !iskyshop_view_type.equals("")) {
			if (iskyshop_view_type.equals("weixin")) {
				String store_id = CommUtil.null2String(request
						.getSession(false).getAttribute("store_id"));
				mv = new JModelAndView("weixin/success.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				url = CommUtil.getURL(request) + "/weixin/index.htm?store_id="
						+ store_id;
			}
		}
		HttpSession session = request.getSession(false);
		if (session.getAttribute("refererUrl") != null
				&& !session.getAttribute("refererUrl").equals("")) {
			url = (String) session.getAttribute("refererUrl");
			session.removeAttribute("refererUrl");
		}
		if (this.configService.getSysConfig().isUc_bbs()) {
			String uc_login_js = CommUtil.null2String(request.getSession(false)
					.getAttribute("uc_login_js"));
			mv.addObject("uc_login_js", uc_login_js);
		}
		String bind = CommUtil.null2String(request.getSession(false)
				.getAttribute("bind"));
		if (!bind.equals("")) {
			mv = new JModelAndView(bind + "_login_bind.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			User user = SecurityUserHolder.getCurrentUser();
			mv.addObject("user", user);
			request.getSession(false).removeAttribute("bind");
		}
		mv.addObject("op_title", "登录成功");
		mv.addObject("url", url);
		return mv;
	}

	/**
	 * 弹窗登录页面
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/user_dialog_login.htm")
	public ModelAndView user_dialog_login(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("user_dialog_login.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		return mv;
	}

	/**
	 * 找回密码第一步
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/forget1.htm")
	public ModelAndView forget1(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("forget1.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		SysConfig config = this.configService.getSysConfig();
		if (!config.isEmailEnable() && !config.isSmsEnbale()) {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "系统关闭邮件及手机短信功能，不能找回密码");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		}
		return mv;
	}

	/**
	 * 找回密码第二步
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/forget2.htm")
	public ModelAndView forget2(HttpServletRequest request,
			HttpServletResponse response, String userName) {
		ModelAndView mv = new JModelAndView("forget2.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		SysConfig config = this.configService.getSysConfig();
		if (!config.isEmailEnable() && !config.isSmsEnbale()) {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "系统关闭邮件及手机短信功能，不能找回密码");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		} else {
			Map params = new HashMap();
			params.put("userName", userName);
			params.put("email", userName);
			params.put("mobile", userName);
			List<User> users = this.userService
					.query("select obj from User obj where obj.userName =:userName or obj.email=:email or obj.mobile =:mobile",
							params, -1, -1);
			if (users.size() > 0) {
				User user = users.get(0);
				if (!CommUtil.null2String(user.getEmail()).equals("")
						|| !CommUtil.null2String(user.getMobile()).equals("")) {
					mv.addObject("user", user);
				} else {
					mv = new JModelAndView("error.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "用户没有绑定邮箱和手机，无法找回");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/forget1.htm");
				}

			} else {
				mv = new JModelAndView("error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "不存在该用户");
				mv.addObject("url", CommUtil.getURL(request) + "/forget1.htm");
			}
		}
		return mv;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param userName
	 * @return
	 */
	@RequestMapping("/forget3.htm")
	@Transactional
	public ModelAndView forget3(HttpServletRequest request,
			HttpServletResponse response, String accept_type, String email,
			String mobile, String userName, String verify_code) {
		ModelAndView mv = new JModelAndView("forget3.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (accept_type.equals("email")) {
			VerifyCode vc = this.verifyCodeService.getObjByProperty("email",
					email);
			if (vc != null) {
				if (!vc.getCode().equals(verify_code)) {
					mv = new JModelAndView("error.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "验证码输入错误");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/forget2.htm?userName=" + userName);
				} else {
					String verify_session = CommUtil.randomString(64)
							.toLowerCase();
					mv.addObject("verify_session", verify_session);
					request.getSession(false).setAttribute("verify_session",
							verify_session);
					mv.addObject("userName", userName);
					this.verifyCodeService.delete(vc.getId());
				}
			} else {
				mv = new JModelAndView("error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "验证码输入错误");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/forget2.htm?userName=" + userName);
			}
		}
		if (accept_type.equals("mobile")) {
			VerifyCode vc = this.verifyCodeService.getObjByProperty("mobile",
					mobile);
			if (vc != null) {
				if (!vc.getCode().equals(verify_code)) {
					mv = new JModelAndView("error.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "验证码输入错误");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/forget2.htm?userName=" + userName);
				} else {
					String verify_session = CommUtil.randomString(64)
							.toLowerCase();
					mv.addObject("verify_session", verify_session);
					request.getSession(false).setAttribute("verify_session",
							verify_session);
					mv.addObject("userName", userName);
					this.verifyCodeService.delete(vc.getId());
				}
			} else {
				mv = new JModelAndView("error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "验证码输入错误");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/forget2.htm?userName=" + userName);
			}
		}
		return mv;
	}

	@RequestMapping("/forget4.htm")
	@Transactional
	public ModelAndView forget4(HttpServletRequest request,
			HttpServletResponse response, String userName, String password,
			String verify_session) {
		ModelAndView mv = new JModelAndView("forget4.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String verify_session1 = CommUtil.null2String(request.getSession(false)
				.getAttribute("verify_session"));
		if (!verify_session1.equals("")
				&& verify_session1.equals(verify_session)) {
			User user = this.userService.getObjByProperty("userName", userName);
			user.setPassword(Md5Encrypt.md5(password).toLowerCase());
			this.userService.update(user);
			request.getSession(false).removeAttribute("verify_session");
			mv.addObject("op_title", "密码修改成功，请使用新密码登录");
			mv.addObject("url", CommUtil.getURL(request) + "/user/login.htm");
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "会话过期，找回密码失败");
			mv.addObject("url", CommUtil.getURL(request) + "/forget1.htm");
		}
		return mv;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param code
	 * @param code_name
	 * @throws UnsupportedEncodingException
	 */

	@RequestMapping("/reg_code_get.htm")
	@Transactional
	public void reg_code_get(HttpServletRequest request,
			HttpServletResponse response, String accept_type, String email,
			String mobile, String userName) throws UnsupportedEncodingException {
		int ret = 0;// 0为发送失败，1为发送成功，-1为邮件发送失败，-2为手机短信发送失败
		if (accept_type.equals("email")) {
			if (this.configService.getSysConfig().isEmailEnable()) {
				String subject = this.configService.getSysConfig()
						.getWebsiteName() + " 邮件验证码";
				String code = CommUtil.randomString(4).toUpperCase();
				VerifyCode vc = this.verifyCodeService.getObjByProperty(
						"email", email);
				if (vc == null) {
					vc = new VerifyCode();
					vc.setAddTime(new Date());
					vc.setCode(code);
					vc.setEmail(email);
					vc.setUserName(userName);
					this.verifyCodeService.save(vc);
				} else {
					vc.setAddTime(new Date());
					vc.setCode(code);
					vc.setEmail(email);
					vc.setUserName(userName);
					this.verifyCodeService.update(vc);
				}
				String content = "您的邮件验证码为:" + code + ",验证码有效时间为30分钟！";
				boolean ret1 = this.msgTools.sendEmail(email, subject, content);
				if (ret1) {
					ret = 1;
				}
			} else {
				ret = -1;
			}
		}
		if (accept_type.equals("mobile")) {
			if (this.configService.getSysConfig().isSmsEnbale()) {
				String code = CommUtil.randomString(4);
				VerifyCode vc = this.verifyCodeService.getObjByProperty(
						"mobile", mobile);
				if (vc == null) {
					vc = new VerifyCode();
					vc.setAddTime(new Date());
					vc.setCode(code);
					vc.setMobile(mobile);
					vc.setUserName(userName);
					this.verifyCodeService.save(vc);
				} else {
					vc.setAddTime(new Date());
					vc.setCode(code);
					vc.setMobile(mobile);
					vc.setUserName(userName);
					this.verifyCodeService.update(vc);
				}
				String content = "您的手机验证码为:" + code + ",验证码有效时间为30分钟！【"
						+ this.configService.getSysConfig().getWebsiteName()
						+ "】";
				boolean ret1 = this.msgTools.sendSMS(mobile, content);
				if (ret1) {
					ret = 1;
				}

			} else {
				ret = -2;
			}
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

	/**
	 * 通过邮件发送新密码来找回密码
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/find_pws.htm")
	@Transactional
	public ModelAndView find_pws(HttpServletRequest request,
			HttpServletResponse response, String userName, String email,
			String code) {
		ModelAndView mv = new JModelAndView("success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		HttpSession session = request.getSession(false);
		String verify_code = (String) session.getAttribute("verify_code");
		if (code.toUpperCase().equals(verify_code)) {
			User user = this.userService.getObjByProperty("userName", userName);
			if (user.getEmail().equals(email.trim())) {
				String pws = CommUtil.randomString(6).toLowerCase();
				String subject = this.configService.getSysConfig().getTitle()
						+ "密码找回邮件";
				String content = user.getUsername() + ",您好！您通过密码找回功能重置密码，新密码为："
						+ pws;
				boolean ret = this.msgTools.sendEmail(email, subject, content);
				if (ret) {
					user.setPassword(Md5Encrypt.md5(pws));
					this.userService.update(user);
					mv.addObject("op_title", "新密码已经发送到邮箱:<font color=red>"
							+ email + "</font>，请查收后重新登录");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/user/login.htm");
				} else {
					mv = new JModelAndView("error.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "邮件发送失败，密码暂未执行重置");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/forget1.htm");
				}
			} else {
				mv = new JModelAndView("error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "用户名、邮箱不匹配");
				mv.addObject("url", CommUtil.getURL(request) + "/forget1.htm");
			}
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "验证码不正确");
			mv.addObject("url", CommUtil.getURL(request) + "/forget1.htm");
		}
		return mv;
	}

}
