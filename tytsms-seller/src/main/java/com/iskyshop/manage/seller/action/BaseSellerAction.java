package com.iskyshop.manage.seller.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Article;
import com.iskyshop.foundation.domain.Complaint;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.Message;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.ReturnGoodsLog;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IArticleService;
import com.iskyshop.foundation.service.IComplaintService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IMessageService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IPayoffLogService;
import com.iskyshop.foundation.service.IReturnGoodsLogService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.seller.Tools.MenuTools;
import com.iskyshop.view.web.tools.AreaViewTools;
import com.iskyshop.view.web.tools.OrderViewTools;
import com.iskyshop.view.web.tools.StoreViewTools;

/**
 * 
 * <p>
 * Title: BaseSellerAction.java
 * </p>
 * 
 * <p>
 * Description: 商家后台基础管理器 主要功能包括商家后台的基础管理、快捷菜单设置等
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
public class BaseSellerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IMessageService messageService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IArticleService articleService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private StoreViewTools storeViewTools;
	@Autowired
	private OrderViewTools orderViewTools;
	@Autowired
	private AreaViewTools areaViewTools;
	@Autowired
	private IPayoffLogService payofflogService;
	@Autowired
	private IReturnGoodsLogService returngoodslogService;
	@Autowired
	private IOrderFormService orderformService;
	@Autowired
	private IComplaintService complaintService;
	@Autowired
	private MenuTools menuTools;

	/**
	 * 商城商家登录入口，商家登录后只能进行商家操作，不能进行购物等其他操作，系统严格区分商家、买家、管理员
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/seller/login.htm")
	public ModelAndView login(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/seller_login.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		request.getSession(false).removeAttribute("verify_code");// 如果系统未开启前台登录验证码，则需要移除session中保留的验证码信息
		return mv;
	}

	/**
	 * 商家后台顶部
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/seller/top.htm")
	public ModelAndView top(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/seller_top.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	/**
	 * 商家中心首页，该请求受系统ss权限管理，对应角色名为"商家中心",商家中心添加子账户时默认添加“商家中心”权限，“
	 * user_center_seller”不可更改
	 * 
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "商家中心", value = "/seller/index.htm*", rtype = "seller", rname = "商家中心", rcode = "user_center_seller", rgroup = "商家中心")
	@RequestMapping("/seller/index.htm")
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/seller_index.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Map params = new HashMap();
		params.put("class_mark", "new_func");
		params.put("display", true);
		List<Article> func_articles = this.articleService
				.query("select obj from Article obj where obj.articleClass.mark=:class_mark and obj.display=:display order by obj.addTime desc",
						params, 0, 5);
		params.clear();
		params.put("type", "store");// 只查询给商家看的文章信息
		params.put("display", true);
		params.put("class_mark", "new_func");
		List<Article> articles = this.articleService
				.query("select obj from Article obj where obj.type=:type and obj.articleClass.mark!=:class_mark and obj.display=:display order by obj.addTime desc",
						params, 0, 5);
		params.clear();
		params.put("store_id", SecurityUserHolder.getCurrentUser().getStore()
				.getId());
		params.put("goods_status", 0);
		List<Goods> goods_sale_list = this.goodsService
				.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status order by obj.goods_salenum desc",
						params, 0, 5);
		params.clear();
		params.put("store_id", SecurityUserHolder.getCurrentUser().getStore()
				.getId());
		params.put("goods_return_status", "5");
		List<ReturnGoodsLog> returngoods = this.returngoodslogService
				.query("select obj from ReturnGoodsLog obj where obj.store_id=:store_id and obj.goods_return_status=:goods_return_status order by addTime desc",
						params, -1, -1);
		params.clear();
		params.put("store_id", SecurityUserHolder.getCurrentUser().getStore()
				.getId().toString());
		List<OrderForm> orders = this.orderformService
				.query("select obj from OrderForm obj where obj.store_id=:store_id order by addTime desc",
						params, -1, -1);
		params.clear();
		params.put("status", 0);
		params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
		List<Message> msgs = this.messageService
				.query("select obj from Message obj where obj.status=:status and obj.toUser.id=:user_id and obj.parent.id is null",
						params, -1, -1);
		params.clear();
		params.put("status", 0);
		params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
		List<Complaint> complaints = this.complaintService
				.query("select obj from Complaint obj where obj.to_user.id=:user_id and obj.status=:status",
						params, -1, -1);
		mv.addObject("complaints", complaints);
		mv.addObject("msgs", msgs);
		mv.addObject("orders", orders);
		mv.addObject("returngoods", returngoods);
		mv.addObject("goods_sale_list", goods_sale_list);
		mv.addObject("articles", articles);
		mv.addObject("user", user);
		mv.addObject("store", user.getStore());
		mv.addObject("func_articles", func_articles);
		mv.addObject("storeViewTools", storeViewTools);
		mv.addObject("orderViewTools", orderViewTools);
		mv.addObject("menuTools", menuTools);
		return mv;
	}

	@SecurityMapping(title = "商家中心导航", value = "/seller/nav.htm*", rtype = "seller", rname = "商家中心导航", rcode = "user_center_seller", rgroup = "商家中心")
	@RequestMapping("/seller/nav.htm")
	public ModelAndView nav(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/seller_nav.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String op = CommUtil.null2String(request.getAttribute("op"));
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		mv.addObject("op", op);
		mv.addObject("user", user);
		return mv;
	}

	@SecurityMapping(title = "商家中心快捷功能设置保存", value = "/seller/store_quick_menu_save.htm*", rtype = "seller", rname = "商家中心快捷功能设置保存", rcode = "user_center_seller", rgroup = "商家中心")
	@RequestMapping("/seller/store_quick_menu_save.htm")
	public void store_quick_menu_save(HttpServletRequest request,
			HttpServletResponse response, String menus) {
		String[] menu_navs = menus.split(";");
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		List<Map> list = new ArrayList<Map>();
		for (String menu_nav : menu_navs) {
			if (!menu_nav.equals("")) {
				String[] infos = menu_nav.split(",");
				Map map = new HashMap();
				map.put("menu_name", infos[0]);
				map.put("menu_url", infos[1]);
				list.add(map);
			}
		}
		user.setStore_quick_menu(Json.toJson(list, JsonFormat.compact()));
		this.userService.update(user);
		String ret = Json.toJson(list, JsonFormat.compact());
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
	 * 商家后台操作成功提示页
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/seller/success.htm")
	public ModelAndView success(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/seller_success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("op_title",
				request.getSession(false).getAttribute("op_title"));
		mv.addObject("url", request.getSession(false).getAttribute("url"));
		request.getSession(false).removeAttribute("op_title");
		request.getSession(false).removeAttribute("url");
		return mv;
	}

	/**
	 * 商家后台操作错误提示页
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/seller/error.htm")
	public ModelAndView error(HttpServletRequest request,
			HttpServletResponse response) {
		User user = SecurityUserHolder.getCurrentUser();
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/seller_error.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("op_title",
				request.getSession(false).getAttribute("op_title"));
		mv.addObject("url", request.getSession(false).getAttribute("url"));
		request.getSession(false).removeAttribute("op_title");
		request.getSession(false).removeAttribute("url");
		return mv;
	}
}
