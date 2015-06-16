package com.iskyshop.view.web.action;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.Md5Encrypt;
import com.iskyshop.foundation.domain.Album;
import com.iskyshop.foundation.domain.IntegralLog;
import com.iskyshop.foundation.domain.Role;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IAlbumService;
import com.iskyshop.foundation.service.IIntegralLogService;
import com.iskyshop.foundation.service.IRoleService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.uc.api.UCClient;

/**
 * Discuz! Ucenter API for ISkyShop， 此类用来同步UC Server发出的操作指令, 可以根据业务需要添加相应的执行代码
 * ,www.iskyshop.com,erikzhang
 */
@Controller
public class UCViewAction {
	private static final long serialVersionUID = -7377364931916922413L;
	public static boolean IN_DISCUZ = true;
	public static String UC_CLIENT_VERSION = "1.5.0"; // note UCenter 版本标识
	public static String UC_CLIENT_RELEASE = "20081031";
	public static boolean API_DELETEUSER = true; // note 用户删除 API
	// 接口开关
	public static boolean API_RENAMEUSER = true; // note 用户改名 API
	// 接口开关
	public static boolean API_GETTAG = true; // note 获取标签 API 接口开关
	public static boolean API_SYNLOGIN = true; // note 同步登录 API
	// 接口开关
	public static boolean API_SYNLOGOUT = true; // note 同步登出 API
	// 接口开关
	public static boolean API_UPDATEPW = true; // note 更改用户密码 开关
	public static boolean API_UPDATEBADWORDS = true; // note 更新关键字列表 开关
	public static boolean API_UPDATEHOSTS = true; // note 更新域名解析缓存 开关
	public static boolean API_UPDATEAPPS = true; // note 更新应用列表 开关
	public static boolean API_UPDATECLIENT = true; // note 更新客户端缓存 开关
	public static boolean API_UPDATECREDIT = true; // note 更新用户积分 开关
	public static boolean API_GETCREDITSETTINGS = true; // note 向 UCenter
	// 提供积分设置 开关
	public static boolean API_GETCREDIT = true; // note 获取用户的某项积分 开关
	public static boolean API_UPDATECREDITSETTINGS = true; // note 更新应用积分设置 开关

	public static String API_RETURN_SUCCEED = "1";
	public static String API_RETURN_FAILED = "-1";
	public static String API_RETURN_FORBIDDEN = "-2";
	@Autowired
	private IUserService userService;
	@Autowired
	private IRoleService roleService;
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IIntegralLogService integralLogService;
	@Autowired
	private IAlbumService albumService;

	@RequestMapping("/api/uc_login.htm")
	@Transactional
	public void uc_login(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String result = uc_answer(request, response);
		response.getWriter().print(result);
	}

	/**
	 * 执行具体的Action 所有服务器发出的参数均可通过args来获得。 注意： request本身是不能得到参数值的。
	 * 
	 * @param request
	 * @param response
	 * @return 操作状态或操作结果
	 */
	private String uc_answer(HttpServletRequest request,
			HttpServletResponse response) {
		// 处理
		String code = request.getParameter("code");
		if (code == null)
			return API_RETURN_FAILED;
		Map<String, String> get = new HashMap<String, String>();
		code = new UCClient().uc_authcode(code, "DECODE");
		parse_str(code, get);
		if (get.isEmpty()) {
			return "Invalid Request";
		}
		if (time() - tolong(get.get("time")) > 3600) {
			return "Authracation has expiried";
		}
		String action = get.get("action");
		if (action == null)
			return API_RETURN_FAILED;
		if (action.equals("test")) {
			return API_RETURN_SUCCEED;

		} else if (action.equals("deleteuser")) {
			return API_RETURN_SUCCEED;
		} else if (action.equals("renameuser")) {
			return API_RETURN_SUCCEED;
		} else if (action.equals("gettag")) {
			if (!API_GETTAG)
				return API_RETURN_FORBIDDEN;
			// 同步代码
			return API_RETURN_SUCCEED;
		} else if (action.equals("synlogin")) {
			if (!API_SYNLOGIN)
				return (API_RETURN_FORBIDDEN);
			iskyshop_login(request, response, get);
		} else if (action.equals("synlogout")) {
			if (!API_SYNLOGOUT)
				return (API_RETURN_FORBIDDEN);
			iskyshop_logout(request, response, get);
		} else if (action.equals("updateclient")) {
			if (!API_UPDATECLIENT)
				return API_RETURN_FORBIDDEN;
			// 同步代码
			return API_RETURN_SUCCEED;
		} else if (action.equals("updatepw")) {
			if (!API_UPDATEPW)
				return API_RETURN_FORBIDDEN;
			iskyshop_update_pws(request, response, get);
			return API_RETURN_SUCCEED;
		} else if (action.equals("updatebadwords")) {
			if (!API_UPDATEBADWORDS)
				return API_RETURN_FORBIDDEN;
			// 同步代码
			return API_RETURN_SUCCEED;

		} else if (action.equals("updatehosts")) {
			if (!API_UPDATEHOSTS)
				return API_RETURN_FORBIDDEN;
			return API_RETURN_SUCCEED;
		} else if (action.equals("updateapps")) {
			if (!API_UPDATEAPPS)
				return API_RETURN_FORBIDDEN;
			return API_RETURN_SUCCEED;
		} else if (action.equals("updatecredit")) {
			// if(!UPDATECREDIT ) return API_RETURN_FORBIDDEN;
			return API_RETURN_SUCCEED;
		} else if (action.equals("getcreditsettings")) {
			// if(!GETCREDITSETTINGS ) return API_RETURN_FORBIDDEN;
			return "";// 积分值
		} else if (action.equals("updatecreditsettings")) {
			if (!API_UPDATECREDITSETTINGS)
				return API_RETURN_FORBIDDEN;
			// 同步代码
			return API_RETURN_SUCCEED;
		} else {
			return (API_RETURN_FORBIDDEN);
		}
		return "";
	}

	private void parse_str(String str, Map<String, String> sets) {
		if (str == null || str.length() < 1)
			return;
		String[] ps = str.split("&");
		for (int i = 0; i < ps.length; i++) {
			String[] items = ps[i].split("=");
			if (items.length == 2) {
				sets.put(items[0], items[1]);
			} else if (items.length == 1) {
				sets.put(items[0], "");
			}
		}
	}

	protected long time() {
		return System.currentTimeMillis() / 1000;
	}

	private static long tolong(Object s) {
		if (s != null) {
			String ss = s.toString().trim();
			if (ss.length() == 0) {
				return 0L;
			} else {
				return Long.parseLong(ss);
			}
		} else {
			return 0L;
		}
	}

	/**
	 * 其他应用登陆设置登陆状态 请修改此方法或都在子类复盖此方法来实现你的逻辑
	 * 
	 * @param request
	 * @param response
	 * @param get
	 */
	protected void iskyshop_login(HttpServletRequest request,
			HttpServletResponse response, Map<String, String> args) {
		boolean admin_login = CommUtil.null2Boolean(request.getSession(false)
				.getAttribute("admin_login"));// 判断是否管理员登录，管理员登录不需要进行重复登录处理
		if (!admin_login) {
			String userName = args.get("username");
			String password = "";
			User user = this.userService.getObjByProperty("userName", userName);
			if (user != null) {
				password = user.getPassword();
			} else {
				user = new User();
				user.setUserName(userName);
				user.setUserRole("BUYER");
				user.setAddTime(new Date());
				// user.setEmail(email);
				user.setPassword(Md5Encrypt.md5(password).toLowerCase());
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
			}
			String url = CommUtil.getURL(request)
					+ "/iskyshop_login.htm?username="
					+ CommUtil.encode(userName) + "&password="
					+ Globals.THIRD_ACCOUNT_LOGIN + password + "&encode=true";
			try {
				response.sendRedirect(url);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 其他应用登陆设置登陆状态 请修改此方法或都在子类复盖此方法来实现你的逻辑
	 * 
	 * @param request
	 * @param response
	 * @param get
	 * @throws MalformedURLException
	 */
	protected void iskyshop_logout(HttpServletRequest request,
			HttpServletResponse response, Map<String, String> args) {
		String url = CommUtil.getURL(request) + "/iskyshop_logout.htm";
		try {
			response.sendRedirect(url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 其他应用登陆设置登陆状态 请修改此方法或都在子类复盖此方法来实现你的逻辑
	 * 
	 * @param request
	 * @param response
	 * @param args
	 */
	protected void iskyshop_update_pws(HttpServletRequest request,
			HttpServletResponse response, Map<String, String> args) {
		User user = SecurityUserHolder.getCurrentUser();

	}
}
