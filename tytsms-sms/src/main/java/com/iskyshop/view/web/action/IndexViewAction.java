package com.iskyshop.view.web.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Article;
import com.iskyshop.foundation.domain.ArticleClass;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsBrand;
import com.iskyshop.foundation.domain.GoodsCart;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.GoodsFloor;
import com.iskyshop.foundation.domain.Partner;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IArticleClassService;
import com.iskyshop.foundation.service.IArticleService;
import com.iskyshop.foundation.service.IGoodsBrandService;
import com.iskyshop.foundation.service.IGoodsCartService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IGoodsFloorService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGroupGoodsService;
import com.iskyshop.foundation.service.IGroupService;
import com.iskyshop.foundation.service.IMessageService;
import com.iskyshop.foundation.service.INavigationService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IPartnerService;
import com.iskyshop.foundation.service.IRoleService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.moudle.chatting.service.IChattingLogService;
import com.iskyshop.view.web.tools.GoodsClassViewTools;
import com.iskyshop.view.web.tools.GoodsFloorViewTools;
import com.iskyshop.view.web.tools.GoodsViewTools;
import com.iskyshop.view.web.tools.NavViewTools;
import com.iskyshop.view.web.tools.StoreViewTools;

/**
 * 
 * <p>
 * Title: Complaint.java
 * </p>
 * 
 * <p>
 * Description:商城首页控制器
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
 * @author erikzhang、jinxinzhe、hezeng
 * 
 * @date 2014-4-25
 * 
 * @version iskyshop_b2b2c 1.0
 */
@Controller
public class IndexViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private IGoodsBrandService goodsBrandService;
	@Autowired
	private IPartnerService partnerService;
	@Autowired
	private IRoleService roleService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IArticleClassService articleClassService;
	@Autowired
	private IArticleService articleService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IMessageService messageService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private INavigationService navigationService;
	@Autowired
	private IGroupGoodsService groupGoodsService;
	@Autowired
	private IGroupService groupService;
	@Autowired
	private IGoodsFloorService goodsFloorService;
	@Autowired
	private IGoodsCartService goodsCartService;
	@Autowired
	private NavViewTools navTools;
	@Autowired
	private GoodsViewTools goodsViewTools;
	@Autowired
	private StoreViewTools storeViewTools;
	@Autowired
	private GoodsFloorViewTools gf_tools;
	@Autowired
	private GoodsClassViewTools gcViewTools;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IChattingLogService chattinglogService;

	private int index_recommend_count = 5;// 首页推荐商品及推荐用户喜欢的商品个数，所有在这个页面位置的商品都以该数量作为查询基准，定义为一个参数，便于修改

	/**
	 * 前台公用顶部页面，使用自定义标签httpInclude.include("/top.htm")完成页面读取
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/top.htm")
	public ModelAndView top(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("top.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("navTools", navTools);
		return mv;
	}

	/**
	 * 前台公用导航主菜单页面，使用自定义标签httpInclude.include("/nav.htm")完成页面读取
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/nav.htm")
	public ModelAndView nav(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("nav.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map params = new HashMap();
		params.put("display", true);
		List<GoodsClass> gcs = this.goodsClassService
				.query("select obj from GoodsClass obj where obj.parent.id is null and obj.display=:display order by obj.sequence asc",
						params, 0, 8);
		mv.addObject("gcs", gcs);
		mv.addObject("navTools", navTools);
		mv.addObject("gcViewTools", gcViewTools);
		return mv;
	}

	/**
	 * 带有全部商品分类的导航菜单，使用自定义标签httpInclude.include("/nav1.htm")完成页面读取
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/nav1.htm")
	public ModelAndView nav1(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("nav1.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map params = new HashMap();
		params.put("display", true);
		List<GoodsClass> gcs = this.goodsClassService
				.query("select obj from GoodsClass obj where obj.parent.id is null and obj.display=:display order by obj.sequence asc",
						params, 0, 8);
		mv.addObject("gcs", gcs);
		mv.addObject("navTools", navTools);
		mv.addObject("gcViewTools", gcViewTools);
		return mv;
	}

	/**
	 * 前台公用head页面，包含系统logo及全文搜索，使用自定义标签httpInclude.include("/head.htm")完成页面读取
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/head.htm")
	@Transactional
	public ModelAndView head(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("head.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String type = CommUtil.null2String(request.getAttribute("type"));
		String cart_session_id = "";
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("cart_session_id")) {
					cart_session_id = CommUtil.null2String(cookie.getValue());
				}
			}
		}
		if (cart_session_id.equals("")) {
			cart_session_id = UUID.randomUUID().toString();
			Cookie cookie = new Cookie("cart_session_id", cart_session_id);
			cookie.setDomain(CommUtil.generic_domain(request));
		}
		List<GoodsCart> carts_list = new ArrayList<GoodsCart>();// 用户整体购物车
		List<GoodsCart> carts_cookie = new ArrayList<GoodsCart>();// 未提交的用户cookie购物车
		List<GoodsCart> carts_user = new ArrayList<GoodsCart>();// 未提交的用户user购物车
		User user = SecurityUserHolder.getCurrentUser();
		Map cart_map = new HashMap();
		if (user != null) {
			user = userService.getObjById(user.getId());
			if (!cart_session_id.equals("")) {
				cart_map.clear();
				cart_map.put("cart_session_id", cart_session_id);
				cart_map.put("cart_status", 0);
				carts_cookie = this.goodsCartService
						.query("select obj from GoodsCart obj where obj.cart_session_id=:cart_session_id and obj.cart_status=:cart_status ",
								cart_map, -1, -1);
				// 如果用户拥有自己的店铺，删除carts_cookie购物车中自己店铺中的商品信息
				if (user.getStore() != null) {
					for (GoodsCart gc : carts_cookie) {
						if (gc.getGoods() != null && gc.getGoods().getGoods_type() == 1) {// 该商品为商家商品
							if (gc.getGoods().getGoods_store().getId()
									.equals(user.getStore().getId())) {
								this.goodsCartService.delete(gc.getId());
							}
						}
					}
				}
				cart_map.clear();
				cart_map.put("user_id", user.getId());
				cart_map.put("cart_status", 0);
				carts_user = this.goodsCartService
						.query("select obj from GoodsCart obj where obj.user.id=:user_id and obj.cart_status=:cart_status ",
								cart_map, -1, -1);
			} else {
				cart_map.clear();
				cart_map.put("user_id", user.getId());
				cart_map.put("cart_status", 0);
				carts_user = this.goodsCartService
						.query("select obj from GoodsCart obj where obj.user.id=:user_id and obj.cart_status=:cart_status ",
								cart_map, -1, -1);
			}
		} else {
			if (!cart_session_id.equals("")) {
				cart_map.clear();
				cart_map.put("cart_session_id", cart_session_id);
				cart_map.put("cart_status", 0);
				carts_cookie = this.goodsCartService
						.query("select obj from GoodsCart obj where obj.cart_session_id=:cart_session_id and obj.cart_status=:cart_status ",
								cart_map, -1, -1);
			}
		}
		// 将cookie购物车与user购物车合并，并且去重
		if (user != null) {
			for (GoodsCart cookie : carts_cookie) {
				boolean add = true;
				for (GoodsCart gc2 : carts_user) {
					if (cookie.getGoods().getId()
							.equals(gc2.getGoods().getId())) {
						if (cookie.getSpec_info().equals(gc2.getSpec_info())) {
							add = false;
							this.goodsCartService.delete(cookie.getId());
						}
					}
				}
				if (add) {// 将cookie去重并添加到cart_list中
					cookie.setCart_session_id(null);
					cookie.setUser(user);
					this.goodsCartService.update(cookie);
					carts_list.add(cookie);
				}
			}
		} else {
			for (GoodsCart gc : carts_cookie) {// 将carts_cookie添加到cart_list中
				carts_list.add(gc);
			}
		}
		for (GoodsCart gc : carts_user) {// 将carts_user添加到cart_list中
			carts_list.add(gc);
		}
		mv.addObject("carts", carts_list);
		mv.addObject("type", type.equals("") ? "goods" : type);
		return mv;
	}

	/**
	 * 用户登录页顶部,使用自定义标签httpInclude.include("/login_head.htm")完成页面读取
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/login_head.htm")
	public ModelAndView login_head(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("login_head.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		return mv;
	}

	/**
	 * 首页商品楼层数据，该数据纳入系统缓存页面 
	 * 使用自定义标签httpInclude.include("/floor.htm")完成页面读取
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/floor.htm")
	public ModelAndView floor(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("floor.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map params = new HashMap();
		params.put("gf_display", true);
		List<GoodsFloor> floors = this.goodsFloorService
				.query("select obj from GoodsFloor obj where obj.gf_display=:gf_display and obj.parent.id is null order by obj.gf_sequence asc",
						params, -1, -1);
		mv.addObject("floors", floors);
		mv.addObject("gf_tools", this.gf_tools);
		mv.addObject("url", CommUtil.getURL(request));
		return mv;
	}

	/**
	 * 前台公用顶部导航页面，使用自定义标签httpInclude.include("/footer.htm")完成页面读取
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/footer.htm")
	public ModelAndView footer(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("footer.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("navTools", navTools);
		return mv;
	}
	

	
	

	/**
	 * 商城首页
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/index.htm")
	public void index(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			String url = "";
			if(!CommUtil.isMobileDevice(request)){
				url = "index.html";
			}else {
				url = "mobileWap/index.htm";
			}
		//	response.sendRedirect(url);
			RequestDispatcher dispatcher = request.getRequestDispatcher(url);
			dispatcher .forward(request, response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		ModelAndView mv = new JModelAndView("generator/indexOutput.html",
//				configService.getSysConfig(),
//				this.userConfigService.getUserConfig(), 1, request, response);
//		Map params = new HashMap();
//		params.put("audit", 1);
//		params.put("recommend", true);
//		List<GoodsBrand> gbs = this.goodsBrandService
//				.query("select obj from GoodsBrand obj where obj.audit=:audit and obj.recommend=:recommend order by obj.sequence",
//						params, -1, -1);
//		mv.addObject("gbs", gbs);
//		params.clear();
//		List<Partner> img_partners = this.partnerService
//				.query("select obj from Partner obj where obj.image.id is not null order by obj.sequence asc",
//						params, -1, -1);
//		mv.addObject("img_partners", img_partners);
//		List<Partner> text_partners = this.partnerService
//				.query("select obj from Partner obj where obj.image.id is null order by obj.sequence asc",
//						params, -1, -1);
//		mv.addObject("text_partners", text_partners);
//		params.clear();
//		Set marks = new TreeSet();// 排除首页有商家4个分类及商家独享的文章信息，erikzhang
//		marks.add("gonggao");
//		marks.add("guize");
//		marks.add("anquan");
//		marks.add("zhinan");
//		marks.add("shangjiaxuzhi");
//		marks.add("chatting_article");
//		marks.add("new_func");
//		params.put("marks", marks);
//		List<ArticleClass> acs = this.articleClassService
//				.query("select obj from ArticleClass obj where obj.parent.id is null and obj.mark not in (:marks) order by obj.sequence asc",
//						params, 0, 8);
//		mv.addObject("acs", acs);
//		params.clear();
//		String[] class_marks = { "gonggao", "guize", "anquan", "zhinan" }; // 首页右上角公告区分类的标识，通过后台添加
//		Map articles = new LinkedHashMap();
//		for (String m : class_marks) {
//			params.put("class_mark", m);
//			params.put("display", true);
//			List<Article> article = this.articleService
//					.query("select obj from Article obj where obj.articleClass.parent.mark=:class_mark and obj.display=:display order by obj.addTime desc",
//							params, 0, 3);
//			articles.put(m, article);
//		}
//		mv.addObject("articles", articles);
//		params.clear();
//		params.put("store_recommend", true);
//		params.put("goods_status", 0);
//		List<Goods> store_recommend_goods_list = this.goodsService
//				.query("select obj from Goods obj where obj.store_recommend=:store_recommend and obj.goods_status=:goods_status order by obj.store_recommend_time desc",
//						params, -1, -1);
//		List<Goods> store_recommend_goods = new ArrayList<Goods>();
//		int max = store_recommend_goods_list.size() >= this.index_recommend_count ? this.index_recommend_count
//				: (store_recommend_goods_list.size() - 1);
//		for (int i = 0; i < max; i++) {
//			store_recommend_goods.add(store_recommend_goods_list.get(i));
//		}
//		mv.addObject("store_recommend_goods", store_recommend_goods);
//		mv.addObject("store_recommend_goods_count", (int) Math.ceil(CommUtil
//				.div(store_recommend_goods_list.size(),
//						this.index_recommend_count)));
//		mv.addObject("goodsViewTools", goodsViewTools);
//		mv.addObject("storeViewTools", storeViewTools);
//		if (SecurityUserHolder.getCurrentUser() != null) {
//			mv.addObject("user", this.userService.getObjById(SecurityUserHolder
//					.getCurrentUser().getId()));
//		}
//		params.clear();

//		params.put("show_index", true);
//		params.put("audit", 1);
//		List<GoodsBrand> goodsBrands = this.goodsBrandService
//				.query("select obj from GoodsBrand obj where obj.show_index=:show_index and obj.audit=:audit order by obj.sequence asc",
//						params, 0, 4);
//		mv.addObject("goodsBrands", goodsBrands);
		// 猜您喜欢 根据cookie商品的分类 销量查询 如果没有cookie则按销量查询
//		List<Goods> your_like_goods = new ArrayList<Goods>();
//		Long your_like_GoodsClass = null;
//		int you_like_goods_count = 0;
//		Cookie[] cookies = request.getCookies();
//		if (cookies != null) {
//			for (Cookie cookie : cookies) {
//				if (cookie.getName().equals("goodscookie")) {
//					String[] like_gcid = cookie.getValue().split(",", 2);
//					Goods goods = this.goodsService.getObjById(CommUtil
//							.null2Long(like_gcid[0]));
//					if (goods != null && !goods.equals("")&&goods.getGc()!=null) {
//						your_like_GoodsClass = goods.getGc().getId();
//						your_like_goods = this.goodsService.query(
//								"select obj from Goods obj where obj.goods_status=0 and obj.gc.id = "
//										+ your_like_GoodsClass
//										+ " and obj.id is not " + goods.getId()
//										+ " order by obj.goods_salenum desc",
//								null, 0, 5);
//						int gcs_size = your_like_goods.size();
//						you_like_goods_count = (int) Math.ceil(CommUtil.div(
//								gcs_size, this.index_recommend_count));
//						if (gcs_size < 5) {
//							List<Goods> like_goods = this.goodsService
//									.query("select obj from Goods obj where obj.goods_status=0 and obj.id is not "
//											+ goods.getId()
//											+ " order by obj.goods_salenum desc",
//											null, 0, 5 - gcs_size);
//							for (int i = 0; i < like_goods.size(); i++) {
//								// 去除重复商品
//								int k = 0;
//								for (int j = 0; j < your_like_goods.size(); j++) {
//									if (like_goods
//											.get(i)
//											.getId()
//											.equals(your_like_goods.get(j)
//													.getId())) {
//										k++;
//									}
//								}
//								if (k == 0) {
//									your_like_goods.add(like_goods.get(i));
//								}
//							}
//						}
//						break;
//					}
//				} else {
//					your_like_goods = this.goodsService
//							.query("select obj from Goods obj where obj.goods_status=0 order by obj.goods_salenum desc",
//									null, 0, 5);
//				}
//			}
//		} else {
//			your_like_goods = this.goodsService
//					.query("select obj from Goods obj where obj.goods_status=0 order by obj.goods_salenum desc",
//							null, 0, 5);
//		}
//		mv.addObject("your_like_goods", your_like_goods);
//		mv.addObject("navTools", navTools);
//		
//		return mv;
	}
	
	

	
	@RequestMapping("/th_index.htm")
	public ModelAndView th_index(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("index.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map params = new HashMap();
		params.put("audit", 1);
		params.put("recommend", true);
		List<GoodsBrand> gbs = this.goodsBrandService
				.query("select obj from GoodsBrand obj where obj.audit=:audit and obj.recommend=:recommend order by obj.sequence",
						params, -1, -1);
		mv.addObject("gbs", gbs);
		params.clear();
		List<Partner> img_partners = this.partnerService
				.query("select obj from Partner obj where obj.image.id is not null order by obj.sequence asc",
						params, -1, -1);
		mv.addObject("img_partners", img_partners);
		List<Partner> text_partners = this.partnerService
				.query("select obj from Partner obj where obj.image.id is null order by obj.sequence asc",
						params, -1, -1);
		mv.addObject("text_partners", text_partners);
		params.clear();
		Set marks = new TreeSet();// 排除首页有商家4个分类及商家独享的文章信息，erikzhang
		marks.add("gonggao");
		marks.add("guize");
		marks.add("anquan");
		marks.add("zhinan");
		marks.add("shangjiaxuzhi");
		marks.add("chatting_article");
		marks.add("new_func");
		params.put("marks", marks);
		List<ArticleClass> acs = this.articleClassService
				.query("select obj from ArticleClass obj where obj.parent.id is null and obj.mark not in (:marks) order by obj.sequence asc",
						params, 0, 8);
		mv.addObject("acs", acs);
		params.clear();
		String[] class_marks = { "gonggao", "guize", "anquan", "zhinan" }; // 首页右上角公告区分类的标识，通过后台添加
		Map articles = new LinkedHashMap();
		for (String m : class_marks) {
			params.put("class_mark", m);
			params.put("display", true);
			List<Article> article = this.articleService
					.query("select obj from Article obj where obj.articleClass.parent.mark=:class_mark and obj.display=:display order by obj.addTime desc",
							params, 0, 3);
			articles.put(m, article);
		}
		mv.addObject("articles", articles);
		params.clear();
		params.put("store_recommend", true);
		params.put("goods_status", 0);
		List<Goods> store_recommend_goods_list = this.goodsService
				.query("select obj from Goods obj where obj.store_recommend=:store_recommend and obj.goods_status=:goods_status order by obj.store_recommend_time desc",
						params, -1, -1);
		List<Goods> store_recommend_goods = new ArrayList<Goods>();
		int max = store_recommend_goods_list.size() >= this.index_recommend_count ? this.index_recommend_count
				: (store_recommend_goods_list.size() - 1);
		for (int i = 0; i < max; i++) {
			store_recommend_goods.add(store_recommend_goods_list.get(i));
		}
		mv.addObject("store_recommend_goods", store_recommend_goods);
		mv.addObject("store_recommend_goods_count", (int) Math.ceil(CommUtil
				.div(store_recommend_goods_list.size(),
						this.index_recommend_count)));
		mv.addObject("goodsViewTools", goodsViewTools);
		mv.addObject("storeViewTools", storeViewTools);
		if (SecurityUserHolder.getCurrentUser() != null) {
			mv.addObject("user", this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId()));
		}
		params.clear();

		params.put("show_index", true);
		params.put("audit", 1);
		List<GoodsBrand> goodsBrands = this.goodsBrandService
				.query("select obj from GoodsBrand obj where obj.show_index=:show_index and obj.audit=:audit order by obj.sequence asc",
						params, 0, 4);
		mv.addObject("goodsBrands", goodsBrands);
		// 猜您喜欢 根据cookie商品的分类 销量查询 如果没有cookie则按销量查询
		List<Goods> your_like_goods = new ArrayList<Goods>();
		Long your_like_GoodsClass = null;
		int you_like_goods_count = 0;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("goodscookie")) {
					String[] like_gcid = cookie.getValue().split(",", 2);
					Goods goods = this.goodsService.getObjById(CommUtil
							.null2Long(like_gcid[0]));
					if (goods != null && !goods.equals("")&&goods.getGc()!=null) {
						your_like_GoodsClass = goods.getGc().getId();
						your_like_goods = this.goodsService.query(
								"select obj from Goods obj where obj.goods_status=0 and obj.gc.id = "
										+ your_like_GoodsClass
										+ " and obj.id is not " + goods.getId()
										+ " order by obj.goods_salenum desc",
								null, 0, 5);
						int gcs_size = your_like_goods.size();
						you_like_goods_count = (int) Math.ceil(CommUtil.div(
								gcs_size, this.index_recommend_count));
						if (gcs_size < 5) {
							List<Goods> like_goods = this.goodsService
									.query("select obj from Goods obj where obj.goods_status=0 and obj.id is not "
											+ goods.getId()
											+ " order by obj.goods_salenum desc",
											null, 0, 5 - gcs_size);
							for (int i = 0; i < like_goods.size(); i++) {
								// 去除重复商品
								int k = 0;
								for (int j = 0; j < your_like_goods.size(); j++) {
									if (like_goods
											.get(i)
											.getId()
											.equals(your_like_goods.get(j)
													.getId())) {
										k++;
									}
								}
								if (k == 0) {
									your_like_goods.add(like_goods.get(i));
								}
							}
						}
						break;
					}
				} else {
					your_like_goods = this.goodsService
							.query("select obj from Goods obj where obj.goods_status=0 order by obj.goods_salenum desc",
									null, 0, 5);
				}
			}
		} else {
			your_like_goods = this.goodsService
					.query("select obj from Goods obj where obj.goods_status=0 order by obj.goods_salenum desc",
							null, 0, 5);
		}
		mv.addObject("your_like_goods", your_like_goods);
		mv.addObject("navTools", navTools);
		
		return mv;
	}
	

	/**
	 * 商城关闭时候导向的请求
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/close.htm")
	public ModelAndView close(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("close.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		return mv;
	}

	@RequestMapping("/404.htm")
	public ModelAndView error404(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("404.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String iskyshop_view_type = CommUtil.null2String(request.getSession(
				false).getAttribute("iskyshop_view_type"));
		if (iskyshop_view_type != null && !iskyshop_view_type.equals("")) {
			if (iskyshop_view_type.equals("weixin")) {
				String store_id = CommUtil.null2String(request
						.getSession(false).getAttribute("store_id"));
				mv = new JModelAndView("weixin/404.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("url", CommUtil.getURL(request)
						+ "/weixin/index.htm?store_id=" + store_id);
			}
		}
		return mv;
	}

	@RequestMapping("/500.htm")
	public ModelAndView error500(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("500.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String iskyshop_view_type = CommUtil.null2String(request.getSession(
				false).getAttribute("iskyshop_view_type"));
		if (iskyshop_view_type != null && !iskyshop_view_type.equals("")) {
			if (iskyshop_view_type.equals("weixin")) {
				String store_id = CommUtil.null2String(request
						.getSession(false).getAttribute("store_id"));
				mv = new JModelAndView("weixin/500.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("url", CommUtil.getURL(request)
						+ "/weixin/index.htm?store_id=" + store_id);
			}
		}
		return mv;
	}

	/**
	 * 商城商品分类导航页
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/goods_class.htm")
	public ModelAndView goods_class(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("goods_class.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map params = new HashMap();
		params.put("display", true);
		List<GoodsClass> gcs = this.goodsClassService
				.query("select obj from GoodsClass obj where obj.parent.id is null and obj.display=:display order by obj.sequence asc",
						params, -1, -1);
		params.put("recommend", true);
		List<GoodsClass> recommend_gcs = this.goodsClassService
				.query("select obj from GoodsClass obj where obj.parent.id is null and obj.display=:display and obj.recommend=:recommend order by obj.sequence asc",
						params, 0, 7);
		mv.addObject("gcs", gcs);
		mv.addObject("recommend_gcs", recommend_gcs);
		return mv;
	}

	/**
	 * 首页推荐商品换一组请求，随机出一组推荐商品信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/switch_recommend_goods.htm")
	public ModelAndView switch_recommend_goods(HttpServletRequest request,
			HttpServletResponse response, String recommend_goods_random) {
		ModelAndView mv = new JModelAndView("switch_recommend_goods.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map params = new HashMap();
		params.put("store_recommend", true);
		params.put("goods_status", 0);
		List<Goods> store_recommend_goods_list = this.goodsService
				.query("select obj from Goods obj where obj.store_recommend=:store_recommend and obj.goods_status=:goods_status order by obj.store_recommend_time desc",
						params, -1, -1);
		List<Goods> store_recommend_goods = new ArrayList<Goods>();
		int begin = CommUtil.null2Int(recommend_goods_random) * 5;
		if (begin > store_recommend_goods_list.size() - 1) {
			begin = 0;
		}
		int max = begin + 5;
		if (max > store_recommend_goods_list.size()) {
			begin = begin - (max - store_recommend_goods_list.size());
			max = max - 1;
		}
		for (int i = 0; i < store_recommend_goods_list.size(); i++) {
			if (i >= begin && i < max) {
				store_recommend_goods.add(store_recommend_goods_list.get(i));
			}
		}
		mv.addObject("store_recommend_goods", store_recommend_goods);
		return mv;
	}
	
	

	/**
	 * ajax异步获取商品信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/ajax_you_like.htm")
	public void ajax_you_like(HttpServletRequest request,
			HttpServletResponse response, String recommend_goods_random) {
		Map<String,Object> dataMap = new HashMap<String, Object>();
		List<Goods> your_like_goods = new ArrayList<Goods>();
		Long your_like_GoodsClass = null;
		int you_like_goods_count = 0;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("goodscookie")) {
					String[] like_gcid = cookie.getValue().split(",", 2);
					Goods goods = this.goodsService.getObjById(CommUtil
							.null2Long(like_gcid[0]));
					if (goods != null && !goods.equals("")&&goods.getGc()!=null) {
						your_like_GoodsClass = goods.getGc().getId();
						your_like_goods = this.goodsService.query(
								"select obj from Goods obj where obj.goods_status=0 and obj.gc.id = "
										+ your_like_GoodsClass
										+ " and obj.id is not " + goods.getId()
										+ " order by obj.goods_salenum desc",
								null, 0, 5);
						int gcs_size = your_like_goods.size();
						you_like_goods_count = (int) Math.ceil(CommUtil.div(
								gcs_size, this.index_recommend_count));
						if (gcs_size < 5) {
							List<Goods> like_goods = this.goodsService
									.query("select obj from Goods obj where obj.goods_status=0 and obj.id is not "
											+ goods.getId()
											+ " order by obj.goods_salenum desc",
											null, 0, 5 - gcs_size);
							for (int i = 0; i < like_goods.size(); i++) {
								// 去除重复商品
								int k = 0;
								for (int j = 0; j < your_like_goods.size(); j++) {
									if (like_goods
											.get(i)
											.getId()
											.equals(your_like_goods.get(j)
													.getId())) {
										k++;
									}
								}
								if (k == 0) {
									your_like_goods.add(like_goods.get(i));
								}
							}
						}
						break;
					}
				} else {
					your_like_goods = this.goodsService
							.query("select obj from Goods obj where obj.goods_status=0 order by obj.goods_salenum desc",
									null, 0, 5);
				}
			}
		} else {
			your_like_goods = this.goodsService
					.query("select obj from Goods obj where obj.goods_status=0 order by obj.goods_salenum desc",
							null, 0, 5);
		}
		
		List<Map> list = new ArrayList<Map>();
		for(Goods good:your_like_goods){
			Map<String,Object> map  = new HashMap<String, Object>();
			if(good.getGoods_main_photo()!=null){
				map.put("goods_main_photo", true);
				map.put("main_photo_path", good.getGoods_main_photo().getPath());
				map.put("main_photo_name", good.getGoods_main_photo().getName());
				map.put("main_photo_ext", good.getGoods_main_photo().getExt());
			}
			map.put("id", good.getId());
			map.put("goods_type", good.getGoods_type());
			if(good.getGoods_store()!=null && good.getGoods_store().getStore_second_domain()!=null){
				map.put("store_second_domain", good.getGoods_store().getStore_second_domain());
			}
			map.put("store_price", good.getStore_price());
			map.put("goods_name", good.getGoods_name());
			
			
			list.add(map);
		}
		dataMap.put("list", list);
		String webPath = CommUtil.getURL(request);
		SysConfig config= configService.getSysConfig();
		String contextPath = request.getContextPath().equals("/") ? ""
				: request.getContextPath();
		String port = request.getServerPort() == 80 ? "" : ":"
				+ CommUtil.null2Int(request.getServerPort());
		if (Globals.SSO_SIGN && config.isSecond_domain_open()
				&& !CommUtil.generic_domain(request).equals("localhost")
				&& !CommUtil.isIp(request.getServerName())) {
			webPath = "http://www." + CommUtil.generic_domain(request) + port+ contextPath;
		}
		dataMap.put("webPath", webPath);
		if (config.getImageWebServer() != null
				&& !config.getImageWebServer().equals("")) {
			dataMap.put("imageWebServer", webPath);
		} else {
			dataMap.put("imageWebServer", webPath);
		}
		dataMap.put("config_goodsImage_path", config.getGoodsImage().getPath());
		dataMap.put("config_goodsImage_name", config.getGoodsImage().getName());
		dataMap.put("second_domain_open", config.isSecond_domain_open());
		dataMap.put("domainPath", CommUtil.generic_domain(request));
		String str = Json.toJson(dataMap);
		//System.out.println(str);
		response.setContentType("application/json");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(str);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 异步加载头部内容
	 * 说明：用户信息获取首先获取cookie中的值，不存在再获取session中的值，不存在最后查询数据库，减少数据库查询
	 * @param request
	 * @param response
	 */
	@RequestMapping("/ajax_head.htm")
	public void ajax_head(HttpServletRequest request,
			HttpServletResponse response){
		Map<String,Object> dataMap = new HashMap<String, Object>();
		//cookie中获取用户信息
		/*Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("iskyshop_user_session")) {
					String id = CommUtil.null2String(cookie.getValue());
				}
			}
		}*/
		HttpSession session = request.getSession(false); 
		User user = (User) session.getAttribute("user");
		SysConfig config = configService.getSysConfig();
		String str = "";
		if(user!=null){
			dataMap.put("user", true);
			dataMap.put("user_userName", user.getUserName());
			dataMap.put("config_websiteName", config.getWebsiteName());
			dataMap.put("user_userRole", user.getUserRole());
			str = Json.toJson(dataMap);
		}
		response.setContentType("application/json");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(str);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	/**
	 * 首页推荐商品换一组请求，随机出一组推荐商品信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/switch_like_goods.htm")
	public ModelAndView switch_like_goods(HttpServletRequest request,
			HttpServletResponse response, String recommend_goods_random) {
		ModelAndView mv = new JModelAndView("switch_like_goods.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		List<Goods> index_like_goods = new ArrayList<Goods>();
		List<Goods> your_like_goods = new ArrayList<Goods>();
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("goodscookie")) {
					index_like_goods = new ArrayList<Goods>();
					String[] like_gcid = cookie.getValue().split(",");
					Set<Long> gc_ids = new HashSet<Long>();
					for (String gcid : like_gcid) {
						gc_ids.add(this.goodsService
								.getObjById(CommUtil.null2Long(gcid)).getGc()
								.getParent().getId());
					}
					Map params = new HashMap();
					params.put("ids", gc_ids);
					your_like_goods = this.goodsService
							.query("select obj from Goods obj where obj.goods_status=0 and obj.gc.parent.id in (:ids) order by obj.goods_salenum desc",
									params, -1, -1);
					int begin = CommUtil.null2Int(recommend_goods_random)
							* this.index_recommend_count;
					if (begin > your_like_goods.size() - 1) {
						begin = 0;
					}
					int max = begin + this.index_recommend_count;
					if (max > your_like_goods.size()) {
						begin = begin - (max - your_like_goods.size());
						max = max - 1;
					}
					for (int i = 0; i < your_like_goods.size(); i++) {
						if (i >= begin && i < max) {
							index_like_goods.add(your_like_goods.get(i));
						}
					}
					break;
				} else {
					your_like_goods = this.goodsService
							.query("select obj from Goods obj where obj.goods_status=0 order by obj.goods_salenum desc",
									null, -1, -1);
					int begin = CommUtil.null2Int(recommend_goods_random)
							* this.index_recommend_count;
					if (begin > your_like_goods.size() - 1) {
						begin = 0;
					}
					int max = begin + this.index_recommend_count;
					if (max > your_like_goods.size()) {
						begin = begin - (max - your_like_goods.size());
						max = max - 1;
					}
					for (int i = 0; i < your_like_goods.size(); i++) {
						if (i >= begin && i < max) {
							index_like_goods.add(your_like_goods.get(i));
						}
					}
				}
			}
		} else {
			your_like_goods = this.goodsService
					.query("select obj from Goods obj where obj.goods_status=0 order by obj.goods_salenum desc",
							null, -1, -1);
			int begin = CommUtil.null2Int(recommend_goods_random)
					* this.index_recommend_count;
			if (begin > your_like_goods.size() - 1) {
				begin = 0;
			}
			int max = begin + this.index_recommend_count;
			if (max > your_like_goods.size()) {
				begin = begin - (max - your_like_goods.size());
				max = max - 1;
			}
			for (int i = 0; i < your_like_goods.size(); i++) {
				if (i >= begin && i < max) {
					index_like_goods.add(your_like_goods.get(i));
				}
			}
		}
		mv.addObject("your_like_goods", index_like_goods);
		return mv;
	}

	/**
	 * 系统只允许单用户登录，第二次登陆后提出先前用户的请求
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/outline.htm")
	public ModelAndView outline(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("error.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("op_title", "该用户在其他地点登录，您被迫下线！");
		mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		return mv;
	}
	
	
	/**
	 * 异步获取购物车商品数量
	 * @param request
	 * @param response
	 */
	@RequestMapping("/ajax_carts.htm")
	public void ajax_getCarts(HttpServletRequest request,
			HttpServletResponse response){
		//String type = CommUtil.null2String(request.getAttribute("type"));
		String type = "goods";
		String cart_session_id = "";
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("cart_session_id")) {
					cart_session_id = CommUtil.null2String(cookie.getValue());
				}
			}
		}
		if (cart_session_id.equals("")) {
			cart_session_id = UUID.randomUUID().toString();
			Cookie cookie = new Cookie("cart_session_id", cart_session_id);
			cookie.setDomain(CommUtil.generic_domain(request));
		}
		List<GoodsCart> carts_list = new ArrayList<GoodsCart>();// 用户整体购物车
		List<GoodsCart> carts_cookie = new ArrayList<GoodsCart>();// 未提交的用户cookie购物车
		List<GoodsCart> carts_user = new ArrayList<GoodsCart>();// 未提交的用户user购物车
		User user = SecurityUserHolder.getCurrentUser();
		Map cart_map = new HashMap();
		if (user != null) {
			user = userService.getObjById(user.getId());
			if (!cart_session_id.equals("")) {
				cart_map.clear();
				cart_map.put("cart_session_id", cart_session_id);
				cart_map.put("cart_status", 0);
				carts_cookie = this.goodsCartService
						.query("select obj from GoodsCart obj where obj.cart_session_id=:cart_session_id and obj.cart_status=:cart_status ",
								cart_map, -1, -1);
				// 如果用户拥有自己的店铺，删除carts_cookie购物车中自己店铺中的商品信息
				if (user.getStore() != null) {
					for (GoodsCart gc : carts_cookie) {
						if (gc.getGoods().getGoods_type() == 1) {// 该商品为商家商品
							if (gc.getGoods().getGoods_store().getId()
									.equals(user.getStore().getId())) {
								this.goodsCartService.delete(gc.getId());
							}
						}
					}
				}
				cart_map.clear();
				cart_map.put("user_id", user.getId());
				cart_map.put("cart_status", 0);
				carts_user = this.goodsCartService
						.query("select obj from GoodsCart obj where obj.user.id=:user_id and obj.cart_status=:cart_status ",
								cart_map, -1, -1);
			} else {
				cart_map.clear();
				cart_map.put("user_id", user.getId());
				cart_map.put("cart_status", 0);
				carts_user = this.goodsCartService
						.query("select obj from GoodsCart obj where obj.user.id=:user_id and obj.cart_status=:cart_status ",
								cart_map, -1, -1);
			}
		} else {
			if (!cart_session_id.equals("")) {
				cart_map.clear();
				cart_map.put("cart_session_id", cart_session_id);
				cart_map.put("cart_status", 0);
				carts_cookie = this.goodsCartService
						.query("select obj from GoodsCart obj where obj.cart_session_id=:cart_session_id and obj.cart_status=:cart_status ",
								cart_map, -1, -1);
			}
		}
		// 将cookie购物车与user购物车合并，并且去重
		if (user != null) {
			for (GoodsCart cookie : carts_cookie) {
				boolean add = true;
				for (GoodsCart gc2 : carts_user) {
					if (cookie.getGoods().getId()
							.equals(gc2.getGoods().getId())) {
						if (cookie.getSpec_info().equals(gc2.getSpec_info())) {
							add = false;
							this.goodsCartService.delete(cookie.getId());
						}
					}
				}
				if (add) {// 将cookie去重并添加到cart_list中
					cookie.setCart_session_id(null);
					cookie.setUser(user);
					this.goodsCartService.update(cookie);
					carts_list.add(cookie);
				}
			}
		} else {
			for (GoodsCart gc : carts_cookie) {// 将carts_cookie添加到cart_list中
				carts_list.add(gc);
			}
		}
		for (GoodsCart gc : carts_user) {// 将carts_user添加到cart_list中
			carts_list.add(gc);
		}
		
		response.setContentType("application/json");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(carts_list.size());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * ajax加载head部分用户中心 并从cookie中查询浏览记录
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/head_ajax_usercenter.htm")
	public ModelAndView head_ajax_usercenter(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("head_ajax_usercenter.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = null;
		if (SecurityUserHolder.getCurrentUser() != null) {
			user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
			mv.addObject("user", user);
			int[] status = new int[] { 10, 30, 50 }; // 已提交 已发货 已完成
			for (int i = 0; i < status.length; i++) {
				int size = this.orderFormService.query(
						"select obj.id from OrderForm obj where obj.order_cat!=2 and obj.user_id="
								+ user.getId().toString()
								+ " and obj.order_status =" + status[i] + "",
						null, -1, -1).size();
				mv.addObject("order_size_" + status[i], size);
			}
		}
		List<Goods> objs = new ArrayList<Goods>();
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("goodscookie")) {
					String[] goods_id = cookie.getValue().split(",");
					int j = 5;
					if (j > goods_id.length) {
						j = goods_id.length;
					}
					for (int i = 0; i < j; i++) {
						Goods goods = new Goods();
						goods = this.goodsService.getObjById(CommUtil
								.null2Long(goods_id[i]));
						if (goods != null)
							objs.add(goods);
					}
				}
			}
		}
		mv.addObject("objs", objs);
		return mv;
	}
}
