package com.iskyshop.manage.admin.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.Authentication;
import org.springframework.security.BadCredentialsException;
import org.springframework.security.concurrent.SessionInformation;
import org.springframework.security.concurrent.SessionRegistry;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.security.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.annotation.Log;
import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.exception.CaptchaErrorException;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.I18nUtils;
import com.iskyshop.core.tools.QRCodeEncoderHandler;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.core.tools.database.DatabaseTools;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.IntegralLog;
import com.iskyshop.foundation.domain.LogType;
import com.iskyshop.foundation.domain.StoreStat;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.SystemTip;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.SystemTipQueryObject;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IIntegralLogService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.IStoreStatService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.ISystemTipService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.MsgTools;
import com.iskyshop.manage.admin.tools.StatTools;
import com.iskyshop.pay.wechatpay.util.ConfigContants;
import com.iskyshop.pay.wechatpay.util.TytsmsStringUtils;
import com.iskyshop.uc.api.UCClient;

/**
 * 
 * <p>
 * Title: BaseManageAction.java
 * </p>
 * 
 * <p>
 * Description: 平台管理基础控制，这里包含平台管理的基础方法、系统全局配置信息的保存、修改及一些系统常用请求
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
 * @date 2014-5-9
 * 
 * @version iskyshop_b2b2c 1.0
 */
@Controller
public class BaseManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IIntegralLogService integralLogService;
	@Autowired
	private DatabaseTools databaseTools;
	@Autowired
	private IStoreStatService storeStatService;
	@Autowired
	private ISystemTipService systemTipService;
	@Autowired
	private MsgTools msgTools;
	@Autowired
	private StatTools statTools;
	@Autowired
	private SessionRegistry sessionRegistry;

	/**
	 * 用户登录后去向控制，根据用户角色UserRole进行控制,该请求不纳入权限管理
	 * 
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@Log(title = "用户登陆", type = LogType.LOGIN)
	@RequestMapping("/login_success.htm")
	public void login_success(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		if (SecurityUserHolder.getCurrentUser() != null) {
			User user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
			if (this.configService.getSysConfig().isIntegral()) {
				if (user.getLoginDate() == null
						|| user.getLoginDate().before(
								CommUtil.formatDate(CommUtil
										.formatShortDate(new Date())))) {
					user.setIntegral(user.getIntegral()
							+ this.configService.getSysConfig()
									.getMemberDayLogin());
					IntegralLog log = new IntegralLog();
					log.setAddTime(new Date());
					log.setContent("用户"
							+ CommUtil.formatLongDate(new Date())
							+ "登录增加"
							+ this.configService.getSysConfig()
									.getMemberDayLogin() + "分");
					log.setIntegral(this.configService.getSysConfig()
							.getMemberRegister());
					log.setIntegral_user(user);
					log.setType("login");
					this.integralLogService.save(log);
				}
			}
			user.setLoginDate(new Date());
			user.setLoginIp(CommUtil.getIpAddr(request));
			user.setLoginCount(user.getLoginCount() + 1);
			user.setLastLoginDate(new Date());
			user.setLastLoginIp(CommUtil.getIpAddr(request));
			this.userService.update(user);
			HttpSession session = request.getSession(false);
			session.setAttribute("user", user);
			session.setAttribute("userName", user.getUsername());
			session.setAttribute("lastLoginDate", new Date());// 设置登录时间
			session.setAttribute("loginIp", CommUtil.getIpAddr(request));// 设置登录IP
			session.setAttribute("login", true);// 设置登录标识
			String role = user.getUserRole();
			String url = CommUtil.getURL(request) + "/user_login_success.htm";
			if (!CommUtil.null2String(
					request.getSession(false).getAttribute("refererUrl"))
					.equals("")) {
				url = CommUtil.null2String(request.getSession(false)
						.getAttribute("refererUrl"));
			}
			String login_role = (String) session.getAttribute("login_role");
			if (Globals.SSO_SIGN
					&& this.configService.getSysConfig().isSecond_domain_open()) {
				// 获取密匙数据
				Cookie iskyshop_user_session = new Cookie(
						"iskyshop_user_session", user.getId().toString());
				iskyshop_user_session.setDomain(CommUtil
						.generic_domain(request));
				// iskyshop_user_session.setMaxAge(60 * 20);
				response.addCookie(iskyshop_user_session);
			}
			boolean ajax_login = CommUtil.null2Boolean(session
					.getAttribute("ajax_login"));
			if (ajax_login) {
				response.setContentType("text/plain");
				response.setHeader("Cache-Control", "no-cache");
				response.setCharacterEncoding("UTF-8");
				PrintWriter writer;
				try {
					writer = response.getWriter();
					writer.print("success");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				if (login_role.equalsIgnoreCase("admin")) {
					if (role.indexOf("ADMIN") >= 0) {
						url = CommUtil.getURL(request) + "/admin/index.htm";
						request.getSession(false).setAttribute("admin_login",
								true);
					}
				}
				if (login_role.equalsIgnoreCase("seller")
						&& role.indexOf("SELLER") >= 0) {
					url = CommUtil.getURL(request) + "/seller/index.htm";
					request.getSession(false)
							.setAttribute("seller_login", true);
				}
				if (!CommUtil.null2String(
						request.getSession(false).getAttribute("refererUrl"))
						.equals("")) {
					url = CommUtil.null2String(request.getSession(false)
							.getAttribute("refererUrl"));
					request.getSession(false).removeAttribute("refererUrl");
				}
				response.sendRedirect(url);
			}
		} else {
			String url = CommUtil.getURL(request) + "/index.htm";
			response.sendRedirect(url);
		}

	}

	/**
	 * 用户成功退出后的URL导向
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/logout_success.htm")
	public void logout_success(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession(false);
		boolean admin_login = CommUtil.null2Boolean(session
				.getAttribute("admin_login"));
		boolean seller_login = CommUtil.null2Boolean(session
				.getAttribute("seller_login"));
		String targetUrl = CommUtil.getURL(request) + "/user/login.htm";
		if (admin_login) {
			targetUrl = CommUtil.getURL(request) + "/admin/login.htm";
		}
		if (seller_login) {
			targetUrl = CommUtil.getURL(request) + "/seller/login.htm";
		}
		//
		String userName = CommUtil
				.null2String(session.getAttribute("userName"));
		//System.out.println(userName);
		Object[] objs = this.sessionRegistry.getAllPrincipals();
		for (int i = 0; i < objs.length; i++) {
			if (CommUtil.null2String(objs[i]).equals(userName)) {
				SessionInformation[] ilist = this.sessionRegistry
						.getAllSessions(objs[i], true);
				for (int j = 0; j < ilist.length; j++) {
					SessionInformation sif = ilist[j];
					// 以下踢出用户
					sif.expireNow();
					this.sessionRegistry.removeSessionInformation(sif
							.getSessionId());
				}
			}
		}
		//
		session.removeAttribute("admin_login");
		session.removeAttribute("seller_login");
		session.removeAttribute("user");
		session.removeAttribute("userName");
		session.removeAttribute("login");
		session.removeAttribute("role");
		session.removeAttribute("cart");
		((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest().getSession(false).removeAttribute("user");
		if (Globals.SSO_SIGN
				&& this.configService.getSysConfig().isSecond_domain_open()) {
			Cookie[] cookies = request.getCookies();
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("iskyshop_user_session")) {
					cookie.setMaxAge(0);
					cookie.setValue("");
					cookie.setDomain(CommUtil.generic_domain(request));
					response.addCookie(cookie);
				}
			}
		}
		// UC论坛同步退出
		if (this.configService.getSysConfig().isUc_bbs()) {
			UCClient uc = new UCClient();
			String uc_logout_js = uc.uc_user_synlogout();
			request.getSession(false)
					.setAttribute("uc_logout_js", uc_logout_js);
		}
		session.invalidate();
		response.sendRedirect(targetUrl);
	}

	@RequestMapping("/login_error.htm")
	public ModelAndView login_error(HttpServletRequest request,
			HttpServletResponse response) {
		String login_role = (String) request.getSession(false).getAttribute(
				"login_role");
		ModelAndView mv = null;
		String iskyshop_view_type = CommUtil.null2String(request.getSession(
				false).getAttribute("iskyshop_view_type"));
		if (iskyshop_view_type != null && !iskyshop_view_type.equals("")) {
			if (iskyshop_view_type.equals("weixin")) {
				String store_id = CommUtil.null2String(request
						.getSession(false).getAttribute("store_id"));
				mv = new JModelAndView("weixin/error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("url", CommUtil.getURL(request)
						+ "/weixin/index.htm?store_id=" + store_id);
			}
		} else {
			if (login_role == null)
				login_role = "user";
			if (login_role.equalsIgnoreCase("admin")) {
				mv = new JModelAndView("admin/blue/login_error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
			}
			if (login_role.equalsIgnoreCase("seller")) {
				mv = new JModelAndView("error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("url", CommUtil.getURL(request)
						+ "/seller/login.htm");
			}
			if (login_role.equalsIgnoreCase("user")) {
				mv = new JModelAndView("error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("url", CommUtil.getURL(request)
						+ "/user/login.htm");
			}
		}
		
		Exception exception = (Exception) request.getSession(false).getAttribute(
				"SPRING_SECURITY_LAST_EXCEPTION");
		if ((exception instanceof CaptchaErrorException)) {
			mv.addObject("op_title", I18nUtils.i18n(request, "tytsms.login.captchaerror"));//验证码错误
			I18nUtils.i18n(request, "tytsms.login.captchaerror");
		} else if ((exception instanceof UsernameNotFoundException)) {
			mv.addObject("op_title", I18nUtils.i18n(request, "tytsms.login.usernamenotfound"));//用户名不存在
		} else if ((exception instanceof BadCredentialsException)) {
			mv.addObject("op_title", I18nUtils.i18n(request, "tytsms.login.passworderror"));//密码不正确
//		} else if ((exception instanceof AuthenticationException)) {
//			mv.addObject("op_title", "AuthenticationException");
		}else
			mv.addObject("op_title", I18nUtils.i18n(request, "tytsms.login.loginfail"));//登录失败
		return mv;
	}

	/**
	 * 管理页面
	 * 
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "商城后台管理", value = "/admin/index.htm*", rtype = "admin", rname = "商城后台管理", rcode = "admin_index", display = false, rgroup = "设置")
	@RequestMapping("/admin/index.htm")
	public ModelAndView manage(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/manage.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (this.configService.getSysConfig().isUc_bbs()) {
			String uc_login_js = CommUtil.null2String(request.getSession(false)
					.getAttribute("uc_login_js"));
			mv.addObject("uc_login_js", uc_login_js);
		}
		return mv;
	}

	@SecurityMapping(title = "欢迎页面", value = "/admin/welcome.htm*", rtype = "admin", rname = "欢迎页面", rcode = "admin_index", display = false, rgroup = "设置")
	@RequestMapping("/admin/welcome.htm")
	public ModelAndView welcome(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/welcome.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Properties props = System.getProperties();
		mv.addObject("os", props.getProperty("os.name"));
		mv.addObject("java_version", props.getProperty("java.version"));
		mv.addObject("shop_version", Globals.DEFAULT_SHOP_VERSION);
		mv.addObject("database_version",
				this.databaseTools.queryDatabaseVersion());
		mv.addObject("web_server_version", request.getSession(false)
				.getServletContext().getServerInfo());
		List<StoreStat> stats = this.storeStatService.query(
				"select obj from StoreStat obj order by obj.addTime desc",
				null, -1, -1);
		Map params = new HashMap();
		params.put("st_status", 0);
		List<SystemTip> sts = this.systemTipService
				.query("select obj from SystemTip obj where obj.st_status=:st_status order by obj.st_level desc",
						params, -1, -1);
		StoreStat stat = null;
		if (stats.size() > 0) {
			stat = stats.get(0);
		} else {
			stat = new StoreStat();
		}
		mv.addObject("stat", stat);
		mv.addObject("sts", sts);
		return mv;
	}

	@SecurityMapping(title = "系统提醒页", value = "/admin/sys_tip_list.htm*", rtype = "admin", rname = "系统提示页", rcode = "admin_index", display = false, rgroup = "设置")
	@RequestMapping("/admin/sys_tip_list.htm")
	public ModelAndView sys_tip_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView("admin/blue/sys_tip_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		SystemTipQueryObject qo = new SystemTipQueryObject(currentPage, mv,
				orderBy, orderType);
		qo.setOrderBy("st_status asc,obj.st_level desc,obj.addTime");
		qo.setOrderType("desc");
		IPageList pList = this.systemTipService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}

	@SecurityMapping(title = "系统提醒删除", value = "/admin/sys_tip_del.htm*", rtype = "admin", rname = "系统提示页", rcode = "admin_index", display = false, rgroup = "设置")
	@RequestMapping("/admin/sys_tip_del.htm")
	public String sys_tip_del(HttpServletRequest request, String mulitId) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				SystemTip st = this.systemTipService.getObjById(CommUtil
						.null2Long(id));
				this.systemTipService.delete(Long.parseLong(id));
			}
		}
		return "redirect:sys_tip_list.htm";
	}

	@SecurityMapping(title = "系统提醒处理", value = "/admin/sys_tip_do.htm*", rtype = "admin", rname = "系统提示页", rcode = "admin_index", display = false, rgroup = "设置")
	@RequestMapping("/admin/sys_tip_do.htm")
	public String sys_tip_do(HttpServletRequest request, String mulitId) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				SystemTip st = this.systemTipService.getObjById(CommUtil
						.null2Long(id));
				st.setSt_status(1);
				this.systemTipService.save(st);
			}
		}
		return "redirect:sys_tip_list.htm";
	}

	@SecurityMapping(title = "关于我们", value = "/admin/aboutus.htm*", rtype = "admin", rname = "关于我们", rcode = "admin_index", display = false, rgroup = "设置")
	@RequestMapping("/admin/aboutus.htm")
	public ModelAndView aboutus(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/aboutus.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	@SecurityMapping(title = "站点设置", value = "/admin/set_site.htm*", rtype = "admin", rname = "站点设置", rcode = "admin_set_site", rgroup = "设置")
	@RequestMapping("/admin/set_site.htm")
	public ModelAndView site_set(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/set_site_setting.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	@SecurityMapping(title = "上传设置", value = "/admin/set_image.htm*", rtype = "admin", rname = "上传设置", rcode = "admin_set_image", rgroup = "设置")
	@RequestMapping("/admin/set_image.htm")
	public ModelAndView set_image(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/set_image_setting.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	@Transactional
	@SecurityMapping(title = "保存商城配置", value = "/admin/sys_config_save.htm*", rtype = "admin", display = false, rname = "保存商城配置", rcode = "admin_config_save", rgroup = "设置")
	@RequestMapping("/admin/sys_config_save.htm")
	public ModelAndView sys_config_save(HttpServletRequest request,
			HttpServletResponse response, String id, String list_url,
			String op_title, String app_download, String android_download,
			String ios_download) {
		SysConfig obj = this.configService.getSysConfig();
		WebForm wf = new WebForm();
		SysConfig sysConfig = null;
		if (id.equals("")) {
			sysConfig = wf.toPo(request, SysConfig.class);
			sysConfig.setAddTime(new Date());
		} else {
			sysConfig = (SysConfig) wf.toPo(request, obj);
		}
		// 图片上传开始logo
		String uploadFilePath = ConfigContants.UPLOAD_IMAGE_MIDDLE_NAME;
		String saveFilePathName = TytsmsStringUtils.generatorImagesFolderServerPath(request)
				+ uploadFilePath
				+ File.separator + "system";
		Map map = new HashMap();
		try {
			String fileName = this.configService.getSysConfig()
					.getWebsiteLogo() == null ? "" : this.configService
					.getSysConfig().getWebsiteLogo().getName();
			map = CommUtil.saveFileToServer(configService,request, "websiteLogo",
					saveFilePathName, fileName, null);
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory logo = new Accessory();
					logo.setName(CommUtil.null2String(map.get("fileName")));
					logo.setExt((String) map.get("mime"));
					logo.setSize(BigDecimal.valueOf((CommUtil.null2Double(map
							.get("fileSize")))));
					logo.setPath(uploadFilePath + "/system");
					logo.setWidth((Integer) map.get("width"));
					logo.setHeight((Integer) map.get("height"));
					logo.setAddTime(new Date());
					this.accessoryService.save(logo);
					sysConfig.setWebsiteLogo(logo);
				}
			} else {
				if (map.get("fileName") != "") {
					Accessory logo = sysConfig.getWebsiteLogo();
					logo.setName(CommUtil.null2String(map.get("fileName")));
					logo.setExt(CommUtil.null2String(map.get("mime")));
					logo.setSize(BigDecimal.valueOf((CommUtil.null2Double(map
							.get("fileSize")))));
					logo.setPath(uploadFilePath + "/system");
					logo.setWidth(CommUtil.null2Int(map.get("width")));
					logo.setHeight(CommUtil.null2Int(map.get("height")));
					this.accessoryService.update(logo);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 默认商品图片
		map.clear();
		try {
			map = CommUtil.saveFileToServer(configService,request, "goodsImage",
					saveFilePathName, null, null);
			String fileName = this.configService.getSysConfig().getGoodsImage() == null ? ""
					: this.configService.getSysConfig().getGoodsImage()
							.getName();
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory photo = new Accessory();
					photo.setName(CommUtil.null2String(map.get("fileName")));
					photo.setExt(CommUtil.null2String(map.get("mime")));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					photo.setPath(uploadFilePath);
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("heigh")));
					photo.setAddTime(new Date());
					this.accessoryService.save(photo);
					sysConfig.setGoodsImage(photo);
				}
			} else {
				if (map.get("fileName") != "") {
					Accessory photo = sysConfig.getGoodsImage();
					photo.setName(CommUtil.null2String(map.get("fileName")));
					photo.setExt(CommUtil.null2String(map.get("mime")));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					photo.setPath(uploadFilePath + "/system");
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("height")));
					this.accessoryService.update(photo);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 默认店铺标识
		map.clear();
		try {
			map = CommUtil.saveFileToServer(configService,request, "storeImage",
					saveFilePathName, null, null);
			String fileName = this.configService.getSysConfig().getStoreImage() == null ? ""
					: this.configService.getSysConfig().getStoreImage()
							.getName();
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory photo = new Accessory();
					photo.setName((String) map.get("fileName"));
					photo.setExt((String) map.get("mime"));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					photo.setPath(uploadFilePath);
					photo.setWidth((Integer) map.get("width"));
					photo.setHeight((Integer) map.get("heigh"));
					photo.setAddTime(new Date());
					this.accessoryService.save(photo);
					sysConfig.setStoreImage(photo);
				}
			} else {
				if (map.get("fileName") != "") {
					Accessory photo = sysConfig.getStoreImage();
					photo.setName(CommUtil.null2String(map.get("fileName")));
					photo.setExt(CommUtil.null2String(map.get("mime")));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					photo.setPath(uploadFilePath + "/system");
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("height")));
					this.accessoryService.update(photo);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 默认会员图片
		map.clear();
		try {
			map = CommUtil.saveFileToServer(configService,request, "memberIcon",
					saveFilePathName, null, null);
			String fileName = this.configService.getSysConfig().getMemberIcon() == null ? ""
					: this.configService.getSysConfig().getMemberIcon()
							.getName();
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory photo = new Accessory();
					photo.setName((String) map.get("fileName"));
					photo.setExt((String) map.get("mime"));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					photo.setPath(uploadFilePath);
					photo.setWidth((Integer) map.get("width"));
					photo.setHeight((Integer) map.get("heigh"));
					photo.setAddTime(new Date());
					this.accessoryService.save(photo);
					sysConfig.setMemberIcon(photo);
				}
			} else {
				if (map.get("fileName") != "") {
					Accessory photo = sysConfig.getMemberIcon();
					photo.setName(CommUtil.null2String(map.get("fileName")));
					photo.setExt(CommUtil.null2String(map.get("mime")));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					photo.setPath(uploadFilePath + "/system");
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("height")));
					this.accessoryService.update(photo);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(StringUtils.isNotEmpty(app_download)){
			sysConfig.setApp_download(CommUtil.null2Int(app_download));
		}
		if(StringUtils.isNotEmpty(android_download)){
			sysConfig.setAndroid_download(android_download);
		}
		if(StringUtils.isNotEmpty(ios_download)){
			sysConfig.setIos_download(ios_download);
		}
		if (CommUtil.null2Int(sysConfig.getApp_download()) == 1) {// 开启app下载生成下载链接二维码
			String path = TytsmsStringUtils.generatorImagesFolderServerPath(request)
					+ uploadFilePath + File.separator + "app";
			CommUtil.createFolder(path);
			// 生成下载地址二维码
			String download_url = CommUtil.getURL(request)
					+ "/app/download.htm";
			QRCodeEncoderHandler handler = new QRCodeEncoderHandler();
			handler.encoderQRCode(download_url, path +File.separator+ "app_dowload.png");
		}
		if (id.equals("")) {
			this.configService.save(sysConfig);
		} else {
			this.configService.update(sysConfig);
		}
		for (int i = 0; i < 4; i++) {
			try {
				map.clear();
				String fileName = "";
				if (sysConfig.getLogin_imgs().size() > i) {
					fileName = sysConfig.getLogin_imgs().get(i).getName();
				}
				map = CommUtil.saveFileToServer(configService,request, "img" + i,
						saveFilePathName, fileName, null);
				if (fileName.equals("")) {
					if (map.get("fileName") != "") {
						Accessory img = new Accessory();
						img.setName(CommUtil.null2String(map.get("fileName")));
						img.setExt((String) map.get("mime"));
						img.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
								.get("fileSize"))));
						img.setPath(uploadFilePath + "/system");
						img.setWidth((Integer) map.get("width"));
						img.setHeight((Integer) map.get("height"));
						img.setAddTime(new Date());
						img.setConfig(sysConfig);
						this.accessoryService.save(img);
					}
				} else {
					if (map.get("fileName") != "") {
						Accessory img = sysConfig.getLogin_imgs().get(i);
						img.setName(CommUtil.null2String(map.get("fileName")));
						img.setExt(CommUtil.null2String(map.get("mime")));
						img.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
								.get("fileSize"))));
						img.setPath(uploadFilePath + "/system");
						img.setWidth(CommUtil.null2Int(map.get("width")));
						img.setHeight(CommUtil.null2Int(map.get("height")));
						img.setConfig(sysConfig);
						this.accessoryService.update(img);
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("op_title", op_title);
		mv.addObject("list_url", list_url);
		return mv;
	}

	@SecurityMapping(title = "Email设置", value = "/admin/set_email.htm*", rtype = "admin", rname = "Email设置", rcode = "admin_set_email", rgroup = "设置")
	@RequestMapping("/admin/set_email.htm")
	public ModelAndView set_email(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/set_email_setting.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	@SecurityMapping(title = "短信设置", value = "/admin/set_sms.htm*", rtype = "admin", rname = "短信设置", rcode = "admin_set_sms", rgroup = "设置")
	@RequestMapping("/admin/set_sms.htm")
	public ModelAndView set_sms(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/set_sms_setting.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	@SecurityMapping(title = "SEO设置", value = "/admin/set_seo.htm*", rtype = "admin", rname = "SEO设置", rcode = "admin_set_seo", rgroup = "设置")
	@RequestMapping("/admin/set_seo.htm")
	public ModelAndView set_seo(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/set_seo_setting.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	@SecurityMapping(title = "二级域名设置", value = "/admin/set_second_domain.htm*", rtype = "admin", rname = "二级域名设置", rcode = "admin_set_second_domain", rgroup = "设置")
	@RequestMapping("/admin/set_second_domain.htm")
	public ModelAndView set_second_domain(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/set_second_domain.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	@SecurityMapping(title = "二级域名设置保存", value = "/admin/set_second_domain_save.htm*", rtype = "admin", rname = "二级域名设置", rcode = "admin_set_second_domain", rgroup = "设置")
	@RequestMapping("/admin/set_second_domain_save.htm")
	public ModelAndView set_second_domain_save(HttpServletRequest request,
			HttpServletResponse response, String id, String domain_allow_count,
			String sys_domain, String second_domain_open) {
		String serverName = request.getServerName().toLowerCase();
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		// System.out.println("二级域名："+Globals.SSO_SIGN);
		if (Globals.SSO_SIGN) {
			SysConfig config = this.configService.getSysConfig();
			config.setDomain_allow_count(CommUtil.null2Int(domain_allow_count));
			config.setSys_domain(sys_domain);
			config.setSecond_domain_open(CommUtil
					.null2Boolean(second_domain_open));
			if (id.equals("")) {
				this.configService.save(config);
			} else
				this.configService.update(config);
			mv.addObject("op_title", "二级域名保存成功");
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/admin/set_second_domain.htm");
		} else {
			SysConfig config = this.configService.getSysConfig();
			config.setDomain_allow_count(CommUtil.null2Int(domain_allow_count));
			config.setSys_domain(sys_domain);
			config.setSecond_domain_open(false);
			if (id.equals("")) {
				this.configService.save(config);
			} else
				this.configService.update(config);
			mv = new JModelAndView("admin/blue/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "当前网站无法开启二级域名");
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/admin/set_second_domain.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "QQ互联登录", value = "/admin/set_site_qq.htm*", rtype = "admin", rname = "二级域名设置", rcode = "admin_set_second_domain", rgroup = "设置")
	@RequestMapping("/admin/set_site_qq.htm")
	public ModelAndView set_site_qq(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/set_second_domain.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	@SecurityMapping(title = "充值接口设置", value = "/admin/set_ofcard.htm*", rtype = "admin", rname = "充值设置", rcode = "admin_set_ofcard", rgroup = "交易")
	@RequestMapping("/admin/set_ofcard.htm")
	public ModelAndView set_ofcard(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/set_ofcard_setting.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	@SecurityMapping(title = "充值接口", value = "/admin/set_ofcard.htm*", rtype = "admin", rname = "充值设置", rcode = "admin_set_ofcard", rgroup = "交易")
	@RequestMapping("/admin/set_ofcard_save.htm")
	public ModelAndView set_ofcard_save(HttpServletRequest request,
			HttpServletResponse response, String id, String ofcard,
			String ofcard_userid, String ofcard_userpws,
			String ofcard_mobile_profit) {
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		SysConfig config = this.configService.getSysConfig();
		if (CommUtil.null2String(id).equals("")) {
			config.setOfcard(CommUtil.null2Boolean(ofcard));
			config.setOfcard_userid(ofcard_userid);
			config.setOfcard_userpws(ofcard_userpws);
			config.setOfcard_mobile_profit(BigDecimal.valueOf(CommUtil
					.null2Double(ofcard_mobile_profit)));
			this.configService.save(config);
		} else {
			config.setOfcard(CommUtil.null2Boolean(ofcard));
			config.setOfcard_userid(ofcard_userid);
			config.setOfcard_userpws(ofcard_userpws);
			config.setOfcard_mobile_profit(BigDecimal.valueOf(CommUtil
					.null2Double(ofcard_mobile_profit)));
			this.configService.update(config);
		}
		mv.addObject("op_title", "充值接口保存成功");
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/admin/set_ofcard.htm");
		return mv;
	}

	/**
	 * 管理员退出，清除管理员权限数据,退出后，管理员可以作为普通登录用户进行任意操作，该请求在前台将不再使用，保留以供二次开发使用
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/admin/logout.htm")
	public String logout(HttpServletRequest request,
			HttpServletResponse response) {
		User user = SecurityUserHolder.getCurrentUser();
		if (user != null) {
			Authentication authentication = new UsernamePasswordAuthenticationToken(
					SecurityContextHolder.getContext().getAuthentication()
							.getPrincipal(), SecurityContextHolder.getContext()
							.getAuthentication().getCredentials(),
					user.get_common_Authorities());
			SecurityContextHolder.getContext()
					.setAuthentication(authentication);
		}
		return "redirect:../index.htm";
	}

	/**
	 * 登录页面
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/admin/login.htm")
	public ModelAndView login(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/login.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = SecurityUserHolder.getCurrentUser();
		request.getSession(false).removeAttribute("verify_code");
		if (user != null) {
			mv.addObject("user", user);
		}
		return mv;
	}

	@RequestMapping("/success.htm")
	public ModelAndView success(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("op_title",
				request.getSession(false).getAttribute("op_title"));
		mv.addObject("url", request.getSession(false).getAttribute("url"));
		request.getSession(false).removeAttribute("op_title");
		request.getSession(false).removeAttribute("url");
		return mv;
	}

	/**
	 * 默认错误页面
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/error.htm")
	public ModelAndView error(HttpServletRequest request,
			HttpServletResponse response) {
		User user = SecurityUserHolder.getCurrentUser();
		ModelAndView mv = new JModelAndView("error.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (user != null && user.getUserRole().equalsIgnoreCase("ADMIN")) {
			mv = new JModelAndView("admin/blue/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);

		}
		mv.addObject("op_title",
				request.getSession(false).getAttribute("op_title"));
		mv.addObject("list_url", request.getSession(false).getAttribute("url"));
		mv.addObject("url", request.getSession(false).getAttribute("url"));
		request.getSession(false).removeAttribute("op_title");
		request.getSession(false).removeAttribute("url");
		return mv;
	}

	/**
	 * 默认异常出现
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/exception.htm")
	public ModelAndView exception(HttpServletRequest request,
			HttpServletResponse response) {
		User user = (User) request.getSession().getAttribute("user");
		ModelAndView mv = new JModelAndView("error.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (user != null && user.getUserRole().equalsIgnoreCase("ADMIN")) {
			mv = new JModelAndView("admin/blue/exception.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
		} else {
			mv.addObject("op_title", "系统出现异常");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		}
		return mv;
	}

	/**
	 * 超级后台默认无权限页面
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/authority.htm")
	public ModelAndView authority(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/authority.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		boolean domain_error = CommUtil.null2Boolean(request.getSession(false)
				.getAttribute("domain_error"));
		if (domain_error) {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "域名绑定错误，请与http://www.iskyshop.com联系");
		}
		return mv;
	}

	/**
	 * 语言验证码处理
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/voice.htm")
	public ModelAndView voice(HttpServletRequest request,
			HttpServletResponse response) {
		return new JModelAndView("include/flash/soundPlayer.swf",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), request, response);
	}

	/**
	 * flash获取验证码
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/getCode.htm")
	public void getCode(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		HttpSession session = request.getSession(false);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter writer = response.getWriter();
		writer.print("result=true&code="
				+ (String) session.getAttribute("verify_code"));
	}

	/**
	 * 测试新编辑器
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/editor.htm")
	public ModelAndView editor(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/editor_test.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	/**
	 * 系统编辑器图片上传
	 * 
	 * @param request
	 * @param response
	 * @throws ClassNotFoundException
	 */
	@RequestMapping("/upload.htm")
	public void upload(HttpServletRequest request, HttpServletResponse response)
			throws ClassNotFoundException {
		String uploadFilePath = ConfigContants.UPLOAD_IMAGE_MIDDLE_NAME;
		String saveFilePathName = TytsmsStringUtils.generatorImagesFolderServerPath(request)
				+ uploadFilePath
				+ File.separator + "common";
		String webPath = request.getContextPath().equals("/") ? "" : request
				.getContextPath();
		if (this.configService.getSysConfig().getAddress() != null
				&& !this.configService.getSysConfig().getAddress().equals("")) {
			webPath = this.configService.getSysConfig().getAddress() + webPath;
		}
		JSONObject obj = new JSONObject();
		try {
			Map map = CommUtil.saveFileToServer(configService,request, "imgFile",
					saveFilePathName, null, null);
			String url = webPath + "/"
					+uploadFilePath
					+ "/common/" + map.get("fileName");
			obj.put("error", 0);
			obj.put("url", url);
		} catch (IOException e) {
			obj.put("error", 1);
			obj.put("message", e.getMessage());
			e.printStackTrace();
		}
		response.setContentType("text/html");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(obj.toJSONString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@RequestMapping("/js.htm")
	public ModelAndView js(HttpServletRequest request,
			HttpServletResponse response, String js) {
		ModelAndView mv = new JModelAndView("resources/js/" + js + ".js",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 2, request, response);
		return mv;
	}

	@RequestMapping("/admin/test_mail.htm")
	public void test_email(HttpServletResponse response, String email) {
		String subject = this.configService.getSysConfig().getTitle() + "测试邮件";
		boolean ret = this.msgTools.sendEmail(email, subject, subject);
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

	@RequestMapping("/admin/test_sms.htm")
	public void test_sms(HttpServletResponse response, String mobile)
			throws UnsupportedEncodingException {
		String content = this.configService.getSysConfig().getTitle()
				+ "亲,如果您收到短信，说明发送成功！";
		boolean ret = this.msgTools.sendSMS(mobile, content);
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

	@RequestMapping("/admin/user_msg_save.htm")
	public void user_msg_save(HttpServletResponse response, String msg)
			throws HttpException, IOException {
		HttpClient client = new HttpClient();
		PostMethod method = new PostMethod(
				"http://www.iskyshop.com/user_msg.htm");
		method.addParameter("msg", msg);
		int status = client.executeMethod(method);
		boolean ret = false;
		if (status == HttpStatus.SC_OK) {
			ret = true;
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
	 * 商城平台样式设置，默认样式为blue
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "websiteCss设置", value = "/admin/set_websiteCss.htm*", rtype = "admin", rname = "站点设置", rcode = "admin_set_site", rgroup = "设置")
	@RequestMapping("/admin/set_websiteCss.htm")
	public void set_websiteCss(HttpServletRequest request,
			HttpServletResponse response, String webcss) {
		SysConfig obj = this.configService.getSysConfig();
		if (!webcss.equals("blue") && !webcss.equals("black")) {
			webcss = "blue";
		}
		obj.setWebsiteCss(webcss);
		this.configService.update(obj);
	}

}
