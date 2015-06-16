package com.iskyshop.manage.seller.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.Md5Encrypt;
import com.iskyshop.foundation.domain.Role;
import com.iskyshop.foundation.domain.RoleGroup;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.UserQueryObject;
import com.iskyshop.foundation.service.IEvaluateService;
import com.iskyshop.foundation.service.IGoodsCartService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IOrderFormLogService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IRoleGroupService;
import com.iskyshop.foundation.service.IRoleService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;

/**
 * 
 * <p>
 * Title: SubAccountSellerAction.java
 * </p>
 * 
 * <p>
 * Description:
 * 卖家子账户管理，卖家根据店铺等级，可以有多个子账户，子账户可以协助卖家管理店铺，卖家自行添加子账户信息，并可以自行给子账户赋予相关卖家中心权限
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
 * @date 2014-6-10
 * 
 * @version iskyshop_b2b2c 1.0
 */
@Controller
public class SubAccountSellerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IRoleGroupService roleGroupService;
	@Autowired
	private IRoleService roleService;
	@Autowired
	private IEvaluateService evaluateService;
	@Autowired
	private IGoodsCartService goodsCartService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IOrderFormLogService orderFormLogService;
	@Autowired
	private IGoodsService goodsService;

	@SecurityMapping(title = "子账户列表", value = "/seller/sub_account_list.htm*", rtype = "seller", rname = "子账户管理", rcode = "sub_account_seller", rgroup = "我的店铺")
	@RequestMapping("/seller/sub_account_list.htm")
	public ModelAndView sub_account_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/sub_account_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		mv.addObject("store", store);
		UserQueryObject uqo = new UserQueryObject(currentPage, mv, orderBy,
				orderType);
		uqo.addQuery("obj.parent.id", new SysMap("user_ids", user.getId()), "=");
		IPageList pList = this.userService.list(uqo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("user", user);
		return mv;
	}

	@SecurityMapping(title = "子账户添加", value = "/seller/sub_account_add.htm*", rtype = "seller", rname = "子账户管理", rcode = "sub_account_seller", rgroup = "我的店铺")
	@RequestMapping("/seller/sub_account_add.htm")
	public ModelAndView sub_account_add(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/sub_account_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		if (store == null) {
			mv = new JModelAndView(
					"user/default/sellercenter/seller_error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "您尚未开设店铺");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
		}

		if (user.getChilds().size() >= store.getGrade().getAcount_num()) {
			mv = new JModelAndView(
					"user/default/sellercenter/seller_error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "您的店铺等级不能继续添加子账户,请升级店铺等级");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/seller/sub_account_list.htm");
		}
		mv.addObject("store", store);
		Map params = new HashMap();
		params.put("type", "SELLER");
		List<RoleGroup> rgs = this.roleGroupService
				.query("select obj from RoleGroup obj where obj.type=:type order by obj.addTime asc",
						params, -1, -1);
		mv.addObject("rgs", rgs);
		mv.addObject("user", user);
		return mv;
	}

	@SecurityMapping(title = "子账户编辑", value = "/seller/sub_account_edit.htm*", rtype = "seller", rname = "子账户管理", rcode = "sub_account_seller", rgroup = "我的店铺")
	@RequestMapping("/seller/sub_account_edit.htm")
	public ModelAndView sub_account_edit(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/sub_account_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		if (store == null) {
			mv = new JModelAndView(
					"user/default/sellercenter/seller_error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "您尚未开设店铺");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
		}
		mv.addObject("store", store);
		Map params = new HashMap();
		params.put("type", "SELLER");
		List<RoleGroup> rgs = this.roleGroupService
				.query("select obj from RoleGroup obj where obj.type=:type order by obj.addTime asc",
						params, -1, -1);
		mv.addObject("rgs", rgs);
		mv.addObject("obj", this.userService.getObjById(CommUtil.null2Long(id)));
		mv.addObject("user", user);
		return mv;
	}

	/**
	 * 过滤名称中的html代码
	 * 
	 * @param inputString
	 * @return
	 */
	private String clearContent(String inputString) {
		String htmlStr = inputString; // 含html标签的字符串
		String textStr = "";
		java.util.regex.Pattern p_script;
		java.util.regex.Matcher m_script;
		java.util.regex.Pattern p_style;
		java.util.regex.Matcher m_style;
		java.util.regex.Pattern p_html;
		java.util.regex.Matcher m_html;

		java.util.regex.Pattern p_html1;
		java.util.regex.Matcher m_html1;

		try {
			String regEx_script = "<[//s]*?script[^>]*?>[//s//S]*?<[//s]*?///[//s]*?script[//s]*?>"; // 定义script的正则表达式{或<script[^>]*?>[//s//S]*?<///script>
			String regEx_style = "<[//s]*?style[^>]*?>[//s//S]*?<[//s]*?///[//s]*?style[//s]*?>"; // 定义style的正则表达式{或<style[^>]*?>[//s//S]*?<///style>
			String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
			String regEx_html1 = "<[^>]+";
			p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
			m_script = p_script.matcher(htmlStr);
			htmlStr = m_script.replaceAll(""); // 过滤script标签

			p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
			m_style = p_style.matcher(htmlStr);
			htmlStr = m_style.replaceAll(""); // 过滤style标签

			p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
			m_html = p_html.matcher(htmlStr);
			htmlStr = m_html.replaceAll(""); // 过滤html标签

			p_html1 = Pattern.compile(regEx_html1, Pattern.CASE_INSENSITIVE);
			m_html1 = p_html1.matcher(htmlStr);
			htmlStr = m_html1.replaceAll(""); // 过滤html标签

			textStr = htmlStr;
		} catch (Exception e) {
			System.err.println("Html2Text: " + e.getMessage());
		}
		return textStr;// 返回文本字符串
	}

	@SecurityMapping(title = "子账户保存", value = "/seller/sub_account_save.htm*", rtype = "seller", rname = "子账户管理", rcode = "sub_account_seller", rgroup = "我的店铺")
	@RequestMapping("/seller/sub_account_save.htm")
	public void sub_account_save(HttpServletRequest request,
			HttpServletResponse response, String id, String userName,
			String trueName, String sex, String birthday, String QQ,
			String telephone, String mobile, String password, String role_ids) {
		boolean ret = true;
		String msg = "保存成功";
		User parent = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		if (parent.getParent() != null) {
			parent = parent.getParent();
		}
		Store store = parent.getStore();
		userName = this.clearContent(userName);
		if (CommUtil.null2String(id).equals("")&&parent.getChilds().size() >= store.getGrade().getAcount_num()) {
			ret = false;
			msg = "已经超过子账户上线";
		} else {
			if (CommUtil.null2String(id).equals("")) {
				User user = new User();
				user.setAddTime(new Date());
				user.setUserName(userName);
				user.setTrueName(trueName);
				user.setSex(CommUtil.null2Int(sex));
				user.setBirthday(CommUtil.formatDate(birthday));
				user.setQQ(QQ);
				user.setMobile(mobile);
				user.setTelephone(telephone);
				user.setParent(parent);
				user.setUserRole("SELLER");
				user.setPassword(Md5Encrypt.md5(password).toLowerCase());
				Map params = new HashMap();
				params.put("type", "BUYER");
				List<Role> roles = this.roleService.query(
						"select obj from Role obj where obj.type=:type",
						params, -1, -1);
				user.getRoles().clear();
				user.getRoles().addAll(roles);
				for (String role_id : role_ids.split(",")) {
					if (!role_id.equals("")) {
						Role role = this.roleService.getObjById(CommUtil
								.null2Long(role_id));
						if (role.getType().equals("SELLER")) {
							user.getRoles().add(role);
						}
					}
				}
				// 默认赋予“商家中心”权限
				params.clear();
				params.put("type", "SELLER");
				params.put("roleCode", "ROLE_USER_CENTER_SELLER");
				List<Role> center_roles = this.roleService
						.query("select obj from Role obj where obj.type=:type and obj.roleCode=:roleCode",
								params, -1, -1);
				for (Role r : center_roles) {
					user.getRoles().add(r);
				}
				this.userService.save(user);
			} else {
				User user = this.userService.getObjById(CommUtil.null2Long(id));
				user.setUserName(userName);
				user.setTrueName(trueName);
				user.setSex(CommUtil.null2Int(sex));
				user.setBirthday(CommUtil.formatDate(birthday));
				user.setQQ(QQ);
				user.setMobile(mobile);
				user.setTelephone(telephone);
				user.getRoles().clear();
				Map params = new HashMap();
				params.put("type", "SELLER");
				List<Role> roles = this.roleService.query(
						"select obj from Role obj where obj.type=:type",
						params, -1, -1);
				user.getRoles().addAll(roles);
				for (String role_id : role_ids.split(",")) {
					if (!role_id.equals("")) {
						Role role = this.roleService.getObjById(CommUtil
								.null2Long(role_id));
						if (role.getType().equals("SELLER")) {
							user.getRoles().add(role);
						}
					}
				}
				// 默认赋予“商家中心”权限
				params.clear();
				params.put("type", "SELLER");
				params.put("roleCode", "ROLE_USER_CENTER_SELLER");
				List<Role> center_roles = this.roleService
						.query("select obj from Role obj where obj.type=:type and obj.roleCode=:roleCode",
								params, -1, -1);
				for (Role r : center_roles) {
					user.getRoles().add(r);
				}
				ret = this.userService.update(user);
				msg = "更新成功";
			}
		}
		Map map = new HashMap();
		map.put("ret", ret);
		map.put("msg", msg);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(map, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SecurityMapping(title = "子账户删除", value = "/seller/sub_account_del.htm*", rtype = "seller", rname = "子账户管理", rcode = "sub_account_seller", rgroup = "我的店铺")
	@RequestMapping("/seller/sub_account_del.htm")
	public String sub_account_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId) {
		User user = this.userService.getObjById(CommUtil.null2Long(mulitId));
		user.getRoles().clear();
		this.userService.delete(user.getId());
		return "redirect:sub_account_list.htm";
	}

}
