package com.iskyshop.view.web.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.ActFileTools;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.Md5Encrypt;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.core.tools.XMLUtil;
import com.iskyshop.foundation.domain.Address;
import com.iskyshop.foundation.domain.Area;
import com.iskyshop.foundation.domain.CouponInfo;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsCart;
import com.iskyshop.foundation.domain.GoodsSpecProperty;
import com.iskyshop.foundation.domain.GroupGoods;
import com.iskyshop.foundation.domain.GroupInfo;
import com.iskyshop.foundation.domain.GroupLifeGoods;
import com.iskyshop.foundation.domain.Message;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.OrderFormLog;
import com.iskyshop.foundation.domain.Payment;
import com.iskyshop.foundation.domain.PayoffLog;
import com.iskyshop.foundation.domain.PredepositLog;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.Template;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IAddressService;
import com.iskyshop.foundation.service.IAreaService;
import com.iskyshop.foundation.service.ICouponInfoService;
import com.iskyshop.foundation.service.IGoodsCartService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGoodsSpecPropertyService;
import com.iskyshop.foundation.service.IGroupGoodsService;
import com.iskyshop.foundation.service.IGroupInfoService;
import com.iskyshop.foundation.service.IGroupLifeGoodsService;
import com.iskyshop.foundation.service.IIntegralGoodsOrderService;
import com.iskyshop.foundation.service.IMessageService;
import com.iskyshop.foundation.service.IOrderFormLogService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IPaymentService;
import com.iskyshop.foundation.service.IPayoffLogService;
import com.iskyshop.foundation.service.IPredepositLogService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.ITemplateService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.MsgTools;
import com.iskyshop.manage.admin.tools.OrderFormTools;
import com.iskyshop.manage.admin.tools.PaymentTools;
import com.iskyshop.manage.admin.tools.SendMsgAndEmTools;
import com.iskyshop.manage.admin.tools.UserTools;
import com.iskyshop.manage.buyer.Tools.CartTools;
import com.iskyshop.manage.seller.Tools.TransportTools;
import com.iskyshop.pay.tools.PayTools;
import com.iskyshop.pay.wechatpay.util.ConfigContants;
import com.iskyshop.pay.wechatpay.util.TytsmsStringUtils;
import com.iskyshop.view.web.tools.GoodsViewTools;
import com.iskyshop.view.web.tools.GroupViewTools;
import com.iskyshop.view.web.tools.StoreViewTools;
import com.taiyitao.elasticsearch.ElasticsearchUtil;
import com.taiyitao.elasticsearch.IndexVoTools;
import com.taiyitao.elasticsearch.IndexVo.IndexName;
import com.taiyitao.elasticsearch.IndexVo.IndexType;

/**
 * 
 * <p>
 * Title: CartViewAction.java
 * </p>
 * 
 * <p>
 * Description:购物控制器,包括购物车所有操作及订单相关操作
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
 * @author erikzhang、hezeng
 * 
 * @date 2014-5-14
 * 
 * @version iskyshop_b2b2c 1.0
 */
@Controller
public class CartViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGoodsSpecPropertyService goodsSpecPropertyService;
	@Autowired
	private IAddressService addressService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private IPaymentService paymentService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IGoodsCartService goodsCartService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IOrderFormLogService orderFormLogService;
	@Autowired
	private IUserService userService;
	@Autowired
	private ITemplateService templateService;
	@Autowired
	private IPredepositLogService predepositLogService;
	@Autowired
	private IGroupGoodsService groupGoodsService;
	@Autowired
	private ICouponInfoService couponInfoService;
	@Autowired
	private MsgTools msgTools;
	@Autowired
	private PaymentTools paymentTools;
	@Autowired
	private PayTools payTools;
	@Autowired
	private TransportTools transportTools;
	@Autowired
	private GoodsViewTools goodsViewTools;
	@Autowired
	private StoreViewTools storeViewTools;
	@Autowired
	private OrderFormTools orderFormTools;
	@Autowired
	private CartTools cartTools;
	@Autowired
	private IGroupLifeGoodsService groupLifeGoodsService;
	@Autowired
	private IGroupInfoService groupInfoService;
	@Autowired
	private IMessageService messageService;
	@Autowired
	private GroupViewTools groupViewTools;
	@Autowired
	private UserTools userTools;
	@Autowired
	private SendMsgAndEmTools sendMsgAndEmTools;
	@Autowired
	private IPayoffLogService payoffLogService;
	@Autowired
	private IIntegralGoodsOrderService igorderService;
	@Autowired
	private ElasticsearchUtil elasticsearchUtil;

	/**
	 * 计算并合并购车信息
	 * 
	 * @param request
	 * @return
	 */

	/**
	 * 用户登陆后清除用户购物车中自己店铺的商品，将cookie购物车与用户user购物车合并，去重复商品（相同商品不同规格不去掉）
	 * 
	 * @param request
	 * @param response
	 * @return
	 */

	private List<GoodsCart> cart_calc(HttpServletRequest request) {
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
		// 将cookie购物车与用户user购物车合并，去重
		if (user != null) {
			for (GoodsCart ugc : carts_user) {
				carts_list.add(ugc);
			}
			cart_map.clear();
			cart_map.put("cart_session_id", cart_session_id);
			cart_map.put("cart_status", 0);
			carts_cookie = this.goodsCartService
					.query("select obj from GoodsCart obj where obj.cart_session_id=:cart_session_id and obj.cart_status=:cart_status ",
							cart_map, -1, -1);// 将carts_cookie再查询一遍，如果用户拥有自己的店铺，删除carts_cookie购物车中自己店铺中的商品信息，但是carts_cookie中还保留该商品对象
			for (GoodsCart cookie : carts_cookie) {
				boolean add = true;
				for (GoodsCart gc2 : carts_user) {
					if (gc2.getGoods() != null && cookie.getGoods().getId()
							.equals(gc2.getGoods().getId())) {
						if (cookie.getSpec_info().equals(gc2.getSpec_info())) {
							add = false;
							this.goodsCartService.delete(cookie.getId());
						}
					}
				}
				if (add) {// 将cookie_cart转变为user_cart
					cookie.setCart_session_id(null);
					cookie.setUser(user);
					this.goodsCartService.update(cookie);
					carts_list.add(cookie);
				}
			}
		} else {
			for (GoodsCart cookie : carts_cookie) {
				carts_list.add(cookie);
			}
		}
		
		carts_list = this.goodsCartService.cart_calc(carts_list); 
		
		
		return carts_list;
	}

	@RequestMapping("/cart_menu_detail.htm")
	@Transactional
	public ModelAndView cart_menu_detail(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("cart_menu_detail.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		List<GoodsCart> carts = this.cart_calc(request);
		if (carts.size() > 0) {
			mv.addObject("total_price", this.calCartPrice(carts, ""));
			mv.addObject("carts", carts);
		}
		return mv;
	}

	/**
	 * 根据商品规格加载商品的数量、价格
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param gsp
	 */
	@RequestMapping("/add_goods_cart.htm")
	@Transactional
	public void add_goods_cart(HttpServletRequest request,
			HttpServletResponse response, String id, String count,
			String price, String gsp, String buy_type) {
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
			response.addCookie(cookie);
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
		// 将cookie购物车与用户user购物车合并，去重
		if (user != null) {
			for (GoodsCart ugc : carts_user) {
				carts_list.add(ugc);
			}
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
				if (add) {// 将cookie_cart转变为user_cart
					cookie.setCart_session_id(null);
					cookie.setUser(user);
					this.goodsCartService.update(cookie);
					carts_list.add(cookie);
				}
			}
		} else {
			for (GoodsCart cookie : carts_cookie) {
				carts_list.add(cookie);
			}
		}
		// 新添加购物车,排除没有重复商品后添加到carts_list中
		Goods goods = this.goodsService.getObjById(CommUtil.null2Long(id));
		GoodsCart obj = new GoodsCart();
		boolean add = true;
		obj.setAddTime(new Date());
		String[] gsp_ids = CommUtil.null2String(gsp).split(",");
		Arrays.sort(gsp_ids);
		double total_price = 0;
		int total_count = 0;
		for (GoodsCart gc : carts_list) {
			if (gsp_ids != null && gsp_ids.length > 0 && gc.getGsps() != null
					&& gc.getGsps().size() > 0) {
				String[] gsp_ids1 = new String[gc.getGsps().size()];
				for (int i = 0; i < gc.getGsps().size(); i++) {
					gsp_ids1[i] = gc.getGsps().get(i) != null ? gc.getGsps()
							.get(i).getId().toString() : "";
				}
				Arrays.sort(gsp_ids1);
				if (gc.getGoods().getId().toString().equals(id)
						&& Arrays.equals(gsp_ids, gsp_ids1)) {
					add = false;
				}
			} else {
				if (gc.getGoods() != null && gc.getGoods().getId().toString().equals(id)) {
					add = false;
				}
			}
		}

		if (add) {// 排除购物车中没有重复商品后添加该商品到购物车
			obj.setCart_gsp(gsp);
			obj.setAddTime(new Date());
			if (CommUtil.null2String(buy_type).equals("")) {
				obj.setCount(CommUtil.null2Int(count));
				obj.setPrice(BigDecimal.valueOf(CommUtil.null2Double(price)));
			}
			if (CommUtil.null2String(buy_type).equals("combin")) {// 组合销售只添加一件商品
				obj.setCount(1);// 设置组合销售套数
				obj.setCart_type("combin");// 设置组合销售标识
				obj.setPrice(goods.getCombin_price());// 设置为组合销售价格
			}
			obj.setGoods(goods);
			String spec_info = "";
			for (String gsp_id : gsp_ids) {
				GoodsSpecProperty spec_property = this.goodsSpecPropertyService
						.getObjById(CommUtil.null2Long(gsp_id));
				obj.getGsps().add(spec_property);
				if (spec_property != null) {
					spec_info = spec_property.getSpec().getName() + ":"
							+ spec_property.getValue() + " " + spec_info;
				}
			}
			if (user == null) {
				obj.setCart_session_id(cart_session_id);
			} else {
				obj.setUser(user);
			}
			obj.setSpec_info(spec_info);
			this.goodsCartService.save(obj);
			double cart_total_price = 0;
			for (GoodsCart gc : carts_list) {
				if (CommUtil.null2String(gc.getCart_type()).equals("")) {
					cart_total_price = cart_total_price
							+ CommUtil.null2Double(gc.getGoods()
									.getGoods_current_price()) * gc.getCount();
				}
				if (CommUtil.null2String(gc.getCart_type()).equals("combin")) { // 如果是组合销售购买，则设置组合价格
					cart_total_price = cart_total_price
							+ CommUtil.null2Double(gc.getGoods()
									.getCombin_price()) * gc.getCount();
				}
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print("");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 从购物车移除商品
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param count
	 * @param price
	 * @param spec_info
	 */
	@RequestMapping("/remove_goods_cart.htm")
	@Transactional
	public void remove_goods_cart(HttpServletRequest request,
			HttpServletResponse response, String id) {
		Double total_price = 0.00;
		String code = "100";// 100表示删除成功，200表示删除失败
		List<GoodsCart> carts = new ArrayList<GoodsCart>();
		if (id != null && !id.equals("")) {
			GoodsCart gc = this.goodsCartService.getObjById(CommUtil
					.null2Long(id));
			gc.getGsps().clear();
			this.goodsCartService.delete(CommUtil.null2Long(id));
			carts = this.cart_calc(request);
			request.getSession(false).setAttribute("cart", carts);
			total_price = this.calCartPrice(carts, "");
		} else {
			code = "200";
		}
		Map map = new HashMap();
		map.put("total_price", BigDecimal.valueOf(total_price));
		map.put("code", code);
		map.put("count", carts.size());
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

	/**
	 * 获得购物车中用户勾选需要购买的商品总价格
	 * 
	 * @param request
	 * @param response
	 */
	private double calCartPrice(List<GoodsCart> carts, String gcs) {
		double all_price = 0.0;
		if (CommUtil.null2String(gcs).equals("")) {
			for (GoodsCart gc : carts) {
				all_price = CommUtil.add(all_price,
						CommUtil.mul(gc.getCount(), gc.getPrice()));
			}
		} else {
			String[] gc_ids = gcs.split(",");
			for (GoodsCart gc : carts) {
				for (String gc_id : gc_ids) {
					if (gc.getId().equals(CommUtil.null2Long(gc_id))) {
						all_price = CommUtil.add(all_price,
								CommUtil.mul(gc.getCount(), gc.getPrice()));
					}
				}
			}
		}
		double d2 = Math.round(all_price * 100) / 100.0;
		BigDecimal bd = new BigDecimal(d2);
		BigDecimal bd2 = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
		return CommUtil.null2Double(bd2);
	}

	/**
	 * 获得商品佣金
	 * 
	 * @param request
	 * @param response
	 */
	private double getGoodscartCommission(GoodsCart gc) {
		double commission_price = CommUtil.mul(gc.getGoods().getGc()
				.getCommission_rate(),
				CommUtil.mul(gc.getPrice(), gc.getCount()));
		return commission_price;
	}

	/**
	 * 获得商品佣金
	 * 
	 * @param request
	 * @param response
	 */
	private double getOrderCommission(List<GoodsCart> gcs) {
		double commission_price = 0.00;
		for (GoodsCart gc : gcs) {
			commission_price = commission_price
					+ this.getGoodscartCommission(gc);
		}
		return commission_price;
	}

	/**
	 * 商品数量调整
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param store_id
	 */
	@RequestMapping("/goods_count_adjust.htm")
	@Transactional
	public void goods_count_adjust(HttpServletRequest request,
			HttpServletResponse response, String gc_id, String count, String gcs) {
		List<GoodsCart> carts = this.cart_calc(request);
		String code = "100";// 100表示修改成功，200表示库存不足,300表示团购库存不足
		double gc_price = 0.00;// 单位GoodsCart总价钱
		double total_price = 0.00;// 购物车总价钱
		String cart_type = "";// 判断是否为组合销售
		Map map = new HashMap();
		int temp_count = CommUtil.null2Int(count);
		Goods goods = null;
		if (gc_id != null && !gc_id.equals("")) {
			GoodsCart gc = this.goodsCartService.getObjById(CommUtil
					.null2Long(gc_id));
			if (gc.getId().toString().equals(gc_id)) {
				cart_type = CommUtil.null2String(gc.getCart_type());
				goods = gc.getGoods();
				if (cart_type.equals("")) {// 普通商品的处理
					if (goods.getGroup_buy() == 2) {
						GroupGoods gg = new GroupGoods();
						for (GroupGoods gg1 : goods.getGroup_goods_list()) {
							if (gg1.getGg_goods().getId().equals(goods.getId())) {
								gg = gg1;
								break;
							}
						}
						if (gg.getGg_count() >= CommUtil.null2Int(count)) {
							gc.setCount(CommUtil.null2Int(count));
							gc.setPrice(BigDecimal.valueOf(CommUtil
									.null2Double(gg.getGg_price())));
							this.goodsCartService.update(gc);
							gc_price = CommUtil.mul(gg.getGg_price(), count);
						} else {
							code = "300";
							if (gg.getGg_count() == 0) {
								gc.setCount(0);
								this.goodsCartService.update(gc);
							}
						}
					} else {
						String gsp = "";
						for (GoodsSpecProperty gs : gc.getGsps()) {
							gsp = gs.getId() + "," + gsp;
						}
						int inventory = goods.getGoods_inventory();
						if (("spec").equals(goods.getInventory_type())) {
							List<HashMap> list = Json.fromJson(ArrayList.class,
									goods.getGoods_inventory_detail());
							String[] gsp_ids = gsp.split(",");
							for (Map temp : list) {
								String[] temp_ids = CommUtil.null2String(
										temp.get("id")).split("_");
								Arrays.sort(gsp_ids);
								Arrays.sort(temp_ids);
								if (Arrays.equals(gsp_ids, temp_ids)) {
									inventory = CommUtil.null2Int(temp
											.get("count"));
								}
							}
						}
						if (inventory >= CommUtil.null2Int(count)) {
							if (gc.getId().toString().equals(gc_id)) {
								gc.setCount(CommUtil.null2Int(count));
								this.goodsCartService.update(gc);
								gc_price = CommUtil.mul(gc.getPrice(), count);
							}
						} else {
							code = "200";
							if (inventory == 0) {
								gc.setCount(0);
								this.goodsCartService.update(gc);
							}
						}
					}
				}
				map.put("count", gc.getCount());
			}
		}
		total_price = this.calCartPrice(carts, gcs);
		map.put("gc_price", CommUtil.formatMoney(gc_price));
		map.put("total_price", CommUtil.formatMoney(total_price));
		map.put("code", code);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			// System.out.println(Json.toJson(map, JsonFormat.compact()));
			writer.print(Json.toJson(map, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@RequestMapping("/goods_cart0.htm")
	@Transactional
	public ModelAndView goods_cart0(HttpServletRequest request,
			HttpServletResponse response, String gid) {
		ModelAndView mv = new JModelAndView("goods_cart0.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		// 同类其他商品
		Goods goods = this.goodsService.getObjById(CommUtil.null2Long(gid));
		if (goods != null) {
			Map map = new HashMap();
			map.put("goods_status", 0);
			map.put("gc_id", goods.getGc().getId());
			map.put("gid", CommUtil.null2Long(gid));
			List<Goods> class_goods = this.goodsService
					.query("select obj from Goods obj where obj.gc.id=:gc_id and obj.id!=:gid and obj.goods_status=:goods_status order by goods_salenum desc",
							map, 0, 9);
			mv.addObject("class_goods", class_goods);
		}
		// 当天直通车商品，并且随机显示6个,显示在goods_cart0.html您可能还需要以下商品中
		List<Goods> ztc_goods = this.goodsViewTools.query_Ztc_Goods(6);
		mv.addObject("ztc_goods", ztc_goods);
		String return_url = CommUtil.getURL(request) + "/goods_" + gid + ".htm";
		if (goods.getGoods_type() == 1) {
			if (this.configService.getSysConfig().isSecond_domain_open()
					&& goods.getGoods_store() != null
					&& goods.getGoods_store().getStore_second_domain() != "") {
				String store_second_domain = "http://"
						+ goods.getGoods_store().getStore_second_domain() + "."
						+ CommUtil.generic_domain(request);
				return_url = store_second_domain + "/goods_" + gid + ".htm";
			}
		}
		mv.addObject("return_url", return_url);
		return mv;
	}

	/**
	 * 确认购物车商品
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/goods_cart1.htm")
	public ModelAndView goods_cart1(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("goods_cart1.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		List<GoodsCart> carts = this.cart_calc(request);
		if (carts.size() > 0) {
			request.getSession(false).setAttribute("cart", carts);
			mv.addObject("all_price", this.calCartPrice(carts, ""));
			mv.addObject("cart", carts);
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "购物车信息为空");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		}
		// 当天直通车商品，
		mv.addObject("goodsViewTools", goodsViewTools);
		return mv;
	}

	/**
	 * 购物确认,填写用户地址，配送方式，支付方式等
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "确认购物车填写地址", value = "/goods_cart2.htm*", rtype = "buyer", rname = "购物流程2", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping("/goods_cart2.htm")
	public ModelAndView goods_cart2(HttpServletRequest request,
			HttpServletResponse response, String gcs) {
		ModelAndView mv = new JModelAndView("goods_cart2.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (gcs != null && !gcs.equals("")) {
			request.getSession(false).setAttribute("carts_gcs", gcs);
		} else {
			String session_gcs = CommUtil.null2String(request.getSession(false)
					.getAttribute("carts_gcs"));
			gcs = session_gcs;
		}
		List<GoodsCart> carts = this.cart_calc(request);
		boolean flag = true;
		if (carts.size() > 0) {
			for (GoodsCart gc : carts) {
				if (!gc.getUser().getId()
						.equals(SecurityUserHolder.getCurrentUser().getId())) {
					flag = false;
					break;
				}
			}
		}
		if (flag && carts.size() > 0) {
			Map params = new HashMap();
			params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
			List<Address> addrs = this.addressService
					.query("select obj from Address obj where obj.user.id=:user_id order by obj.addTime desc",
							params, -1, -1);
			mv.addObject("addrs", addrs);
			String cart_session = CommUtil.randomString(32);
			request.getSession(false)
					.setAttribute("cart_session", cart_session);
			mv.addObject("cart_session", cart_session);
			mv.addObject("transportTools", transportTools);
			mv.addObject("goodsViewTools", goodsViewTools);
			mv.addObject("order_goods_price", this.calCartPrice(carts, gcs));
			mv.addObject("carts", carts);
			List map_list = new ArrayList();
			List<Object> store_list = new ArrayList<Object>();
			for (GoodsCart gc : carts) {
				if (gc.getGoods().getGoods_type() == 1) {
					store_list.add(gc.getGoods().getGoods_store().getId());
				} else {
					store_list.add("self");
				}
			}
			HashSet hs = new HashSet(store_list);
			store_list.removeAll(store_list);
			store_list.addAll(hs);
			mv.addObject("store_ids", StringUtils.arrayToCommaDelimitedString(hs.toArray()));
			String[] gc_ids = gcs.split(",");
			for (Object sl : store_list) {
				if (sl != "self" && !sl.equals("self")) {// 商家商品
					List<GoodsCart> gc_list = new ArrayList<GoodsCart>();
					for (GoodsCart gc : carts) {
						for (String gc_id : gc_ids) {
							if (!CommUtil.null2String(gc_id).equals("")
									&& CommUtil.null2Long(gc_id).equals(
											gc.getId())) {
								if (gc.getGoods().getGoods_store() != null) {
									if (gc.getGoods().getGoods_store().getId()
											.equals(sl)) {
										gc_list.add(gc);
									}
								}
							}
						}

					}
					if (gc_list != null && gc_list.size() > 0) {
						Map map = new HashMap();
						map.put("store_id", sl);
						// System.out.println("-----------store:"+this.storeService.getObjById(CommUtil.null2Long(sl)));
						Store s=this.storeService.getObjById(CommUtil.null2Long(sl));
						map.put("store", s);
						double storeGoodsPrice = this.calCartPrice(gc_list, gcs);
						map.put("store_goods_price",storeGoodsPrice);
						map.put("gc_list", gc_list);
						SimpleDateFormat time=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						Date startDate = new Date();
						Date endDate = new Date();
						Date nowDate = new Date();
						try {
							 startDate = time.parse(ActFileTools.START_TIME);
							 endDate = time.parse(ActFileTools.END_TIME);
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						//活动专场免邮判断
						if (ActFileTools.STORE_IDS_LIST.contains(sl.toString()) && storeGoodsPrice >= ActFileTools.FREE_SHIPPING_VALUE && nowDate.after(startDate) && nowDate.before(endDate)) {
							map.put("free_shipping_value", true);
						}
						//店铺免邮额度判断
						if (s.getStore_free_price().compareTo(new BigDecimal("0"))>0 && storeGoodsPrice >= s.getStore_free_price().doubleValue()) {
							map.put("store_free_shipping_value", true);
						}
						
						map_list.add(map);
					}

				} else {// 自营商品
					List<GoodsCart> gc_list = new ArrayList<GoodsCart>();
					for (GoodsCart gc : carts) {
						for (String gc_id : gc_ids) {
							if (!CommUtil.null2String(gc_id).equals("")
									&& CommUtil.null2Long(gc_id).equals(
											gc.getId())) {
								if (gc.getGoods().getGoods_store() == null) {
									gc_list.add(gc);
								}
							}
						}
					}
					if (gc_list != null && gc_list.size() > 0) {
						Map map = new HashMap();
						map.put("store_id", sl);
						// System.out.println("========store:"+this.storeService.getObjById(CommUtil.null2Long(sl)));
						map.put("store_goods_price",
								this.calCartPrice(gc_list, gcs));
						map.put("gc_list", gc_list);
						map_list.add(map);
					}
				}
			}
			
			mv.addObject("storeViewTools", storeViewTools);
			mv.addObject("cartTools", cartTools);
			mv.addObject("transportTools", transportTools);
			mv.addObject("userTools", this.userTools);
			mv.addObject("map_list", map_list);
			mv.addObject("gcs", gcs);
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "购物车信息为空");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		}

		return mv;
	}

	@SecurityMapping(title = "完成订单提交进入支付", value = "/goods_cart3.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping("/goods_cart3.htm")
	@Transactional
	public ModelAndView goods_cart3(HttpServletRequest request,
			HttpServletResponse response, String cart_session, String store_id,
			String addr_id, String gcs) throws Exception {
		ModelAndView mv = new JModelAndView("goods_cart3.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String cart_session1 = (String) request.getSession(false).getAttribute(
				"cart_session");
		List<GoodsCart> order_carts = new ArrayList<GoodsCart>();
		Address addr = this.addressService.getObjById(CommUtil
				.null2Long(addr_id));
		String[] gc_ids = gcs.split(",");
		String store_ids[] = store_id.split(",");
		Set<String> read_store_ids = new HashSet<String>();
		for (String gc_id : gc_ids) {
			if (!gc_id.equals("")) {
				GoodsCart gc = this.goodsCartService.getObjById(CommUtil
						.null2Long(gc_id));
				//防止在购物第三步刷新页面出现空值异常@by Frank
				if(gc!=null &&gc.getGoods()!=null && gc.getGoods().getGoods_status()==0){
					order_carts.add(gc);//添加如果商品下架不进入结算处理
					for (int i = 0; i < store_ids.length; i++) {
						if (gc.getGoods().getGoods_store().getId().toString()==store_ids[i] ||store_ids[i].equals(gc.getGoods().getGoods_store().getId().toString())) {
							read_store_ids.add(store_ids[i]);
						}
					}
			     }
			}
		}
		if (order_carts.size() > 0 && addr != null) {
			// 验证购物车中是否存在库存为0的商品
			boolean inventory_very = true;
			for (GoodsCart gc : order_carts) {
				if (gc.getCount() == 0) {
					inventory_very = false;
				}
				int goods_inventory = CommUtil.null2Int(this
						.generic_default_info(gc.getGoods(), gc.getCart_gsp())
						.get("count"));// 计算商品库存信息
				if (goods_inventory == 0
						|| gc.getGoods().getGoods_inventory() < gc.getCount()) {
					inventory_very = false;
				}
			}
			if (inventory_very) {
				double all_of_price = 0;
				if (CommUtil.null2String(cart_session1).equals(cart_session)) {// 禁止重复提交订单信息
					request.getSession(false).removeAttribute("cart_session");// 删除订单提交唯一标示，用户不能进行第二次订单提交
					List<Map> child_order_maps = new ArrayList<Map>();
					String order_suffix = CommUtil.formatTime("yyyyMMddHHmmss",
							new Date());
					Object[] store_list = read_store_ids.toArray();
					for (int i = 0; i < store_list.length; i++) {// 根据店铺id，保存多个子订单
						String sid = (String) store_list[i];
						Store store = null;
						List<GoodsCart> gc_list = new ArrayList<GoodsCart>();
						List<Map> map_list = new ArrayList<Map>();
						if (sid != "self" && !sid.equals("self")) {
							store = this.storeService.getObjById(CommUtil
									.null2Long(sid));
						}
						for (GoodsCart gc : order_carts) {
							String goods_gsp_ids = "/";
							for (GoodsSpecProperty gsp : gc.getGsps()) {
								goods_gsp_ids = gsp.getId() + "/"
										+ goods_gsp_ids;
							}
							if (gc.getGoods().getGoods_type() == 1) {// 商家商品
								boolean add = false;
								for (String gc_id : gc_ids) {
									if (!CommUtil.null2String(gc_id).equals("")
											&& gc.getId().equals(
													CommUtil.null2Long(gc_id))) {// 判断是否是用户勾选要购买的商品
										add = true;
										break;
									}
								}
								if (add) {
									if (gc.getGoods().getGoods_store().getId()
											.equals(CommUtil.null2Long(sid))) {
										String goods_type = "";
										if ("combin" == gc.getCart_type()
												|| "combin".equals(gc
														.getCart_type())) {
											goods_type = "combin";
										}
										if ("group" == gc.getCart_type()
												|| "group".equals(gc
														.getCart_type())) {
											goods_type = "group";
										}
										Map json_map = new HashMap();
										json_map.put("goods_id", gc.getGoods()
												.getId());
										json_map.put("goods_name", gc
												.getGoods().getGoods_name());
										json_map.put("goods_choice_type", gc
												.getGoods()
												.getGoods_choice_type());
										json_map.put("goods_type", goods_type);
										json_map.put("goods_count",
												gc.getCount());
										json_map.put("goods_price",
												gc.getPrice());// 商品单价
										json_map.put(
												"goods_all_price",
												CommUtil.mul(gc.getPrice(),
														gc.getCount()));// 商品总价
										json_map.put("goods_commission_price",
												this.getGoodscartCommission(gc));// 设置该商品总佣金
										json_map.put("goods_commission_rate",
												gc.getGoods().getGc()
														.getCommission_rate());// 设置该商品的佣金比例
										json_map.put(
												"goods_payoff_price",
												CommUtil.subtract(
														CommUtil.mul(
																gc.getPrice(),
																gc.getCount()),
														this.getGoodscartCommission(gc)));// 该商品结账价格=该商品总价格-商品总佣金
										json_map.put("goods_gsp_val",
												gc.getSpec_info());
										json_map.put("goods_gsp_ids",
												goods_gsp_ids);
										if (gc.getGoods().getGoods_main_photo() != null) {
											json_map.put(
													"goods_mainphoto_path",
													gc.getGoods()
															.getGoods_main_photo()
															.getPath()
															+ "/"
															+ gc.getGoods()
																	.getGoods_main_photo()
																	.getName()
															+ "_small."
															+ gc.getGoods()
																	.getGoods_main_photo()
																	.getExt());
										} else {
											json_map.put(
													"goods_mainphoto_path",
													this.configService
															.getSysConfig()
															.getGoodsImage()
															.getPath()
															+ "/"
															+ this.configService
																	.getSysConfig()
																	.getGoodsImage()
																	.getName());
										}
										String goods_domainPath = CommUtil
												.getURL(request)
												+ "/goods_"
												+ gc.getGoods().getId()
												+ ".htm";
										String store_domainPath = CommUtil
												.getURL(request)
												+ "/store_"
												+ gc.getGoods()
														.getGoods_store()
														.getId() + ".htm";
										if (this.configService.getSysConfig()
												.isSecond_domain_open()
												&& gc.getGoods()
														.getGoods_store()
														.getStore_second_domain() != ""
												&& gc.getGoods()
														.getGoods_type() == 1) {
											String store_second_domain = "http://"
													+ gc.getGoods()
															.getGoods_store()
															.getStore_second_domain()
													+ "."
													+ CommUtil
															.generic_domain(request);
											goods_domainPath = store_second_domain
													+ "/goods_"
													+ gc.getGoods().getId()
													+ ".htm";
											store_domainPath = store_second_domain;
										}
										json_map.put("goods_domainPath",
												goods_domainPath);// 商品二级域名路径
										json_map.put("store_domainPath",
												store_domainPath);// 店铺二级域名路径
										map_list.add(json_map);
										gc_list.add(gc);
									}
								}

							} else {// 自营商品
								boolean add = false;
								for (String gc_id : gc_ids) {
									if (!CommUtil.null2String(gc_id).equals("")
											&& gc.getId().equals(
													CommUtil.null2Long(gc_id))) {// 判断是否是用户勾选要购买的商品
										add = true;
										break;
									}
								}
								if (add) {
									if (sid == "self" || sid.equals("self")) {
										String goods_type = "";
										if ("combin" == gc.getCart_type()
												|| "combin".equals(gc
														.getCart_type())) {
											goods_type = "combin";
										}
										if ("group" == gc.getCart_type()
												|| "group".equals(gc
														.getCart_type())) {
											goods_type = "group";
										}
										Map json_map = new HashMap();
										json_map.put("goods_id", gc.getGoods()
												.getId());
										json_map.put("goods_name", gc
												.getGoods().getGoods_name());
										json_map.put("goods_choice_type", gc
												.getGoods()
												.getGoods_choice_type());
										json_map.put("goods_type", goods_type);
										json_map.put("goods_count",
												gc.getCount());
										json_map.put("goods_price",
												gc.getPrice());// 商品单价
										json_map.put(
												"goods_all_price",
												CommUtil.mul(gc.getPrice(),
														gc.getCount()));// 商品总价
										json_map.put("goods_gsp_val",
												gc.getSpec_info());
										json_map.put("goods_gsp_ids",
												goods_gsp_ids);
										if (gc.getGoods().getGoods_main_photo() != null) {
											json_map.put(
													"goods_mainphoto_path",
													gc.getGoods()
															.getGoods_main_photo()
															.getPath()
															+ "/"
															+ gc.getGoods()
																	.getGoods_main_photo()
																	.getName()
															+ "_small."
															+ gc.getGoods()
																	.getGoods_main_photo()
																	.getExt());
										} else {
											json_map.put(
													"goods_mainphoto_path",
													this.configService
															.getSysConfig()
															.getGoodsImage()
															.getPath()
															+ "/"
															+ this.configService
																	.getSysConfig()
																	.getGoodsImage()
																	.getName());
										}
										json_map.put("goods_domainPath",
												CommUtil.getURL(request)
														+ "/goods_"
														+ gc.getGoods().getId()
														+ ".htm");// 商品二级域名路径
										map_list.add(json_map);
										gc_list.add(gc);
									}
								}
							}
						}
						
						if(gc_list!=null &&gc_list.size()>0 && map_list!=null&& map_list.size()>0){
							double goods_amount = this.calCartPrice(gc_list, gcs);// 订单中商品价格
						List<SysMap> sms = this.transportTools
								.query_cart_trans(gc_list, CommUtil
										.null2String(addr.getArea().getId()));
						String transport = request.getParameter("transport_"
								+ sid);
						if (CommUtil.null2String(transport).indexOf("平邮") < 0
								&& CommUtil.null2String(transport)
										.indexOf("快递") < 0
								&& CommUtil.null2String(transport).indexOf(
										"EMS") < 0) {
							if(CommUtil.null2String(transport).indexOf("包邮（活动专场）") >= 0){
								transport="包邮（活动专场）";
							}else if(CommUtil.null2String(transport).indexOf("商家包邮") >= 0){
								transport="商家包邮";
							}
							else{
								transport = "快递";
							}
						} else {
							if (CommUtil.null2String(transport).indexOf("平邮") >= 0) {
								transport = "平邮";
							}
							if (CommUtil.null2String(transport).indexOf("快递") >= 0) {
								transport = "快递";
							}
							if (CommUtil.null2String(transport).indexOf("EMS") >= 0) {
								transport = "EMS";
							}
						}
						double ship_price = 0.00;
						for (SysMap sm : sms) {
							if (CommUtil.null2String(sm.getKey()).indexOf(
									transport) >= 0) {
								ship_price = CommUtil
										.null2Double(sm.getValue());// 订单物流运费
							}
						}
						
						SimpleDateFormat time=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						Date startDate = new Date();
						Date endDate = new Date();
						Date nowDate = new Date();
						try {
							 startDate = time.parse(ActFileTools.START_TIME);
							 endDate = time.parse(ActFileTools.END_TIME);
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (CommUtil.null2String(transport).indexOf("包邮（活动专场）") >= 0 && ActFileTools.STORE_IDS_LIST.contains(sid.toString()) && goods_amount >= ActFileTools.FREE_SHIPPING_VALUE && nowDate.after(startDate) && nowDate.before(endDate)) {
							transport="包邮（活动专场）";
							ship_price = 0.00;
						}
						//店铺免邮额度判断
						if (CommUtil.null2String(transport).indexOf("商家包邮") >= 0 &&store.getStore_free_price().compareTo(new BigDecimal("0"))>0 && goods_amount >= store.getStore_free_price().doubleValue()) {
							transport="商家包邮";
							ship_price = 0.00;
						}
						double commission_amount = this
								.getOrderCommission(gc_list);// 订单总体佣金
						OrderForm of = new OrderForm();
						of.setAddTime(new Date());
						String order_store_id = "0";
						if (sid != "self" && !sid.equals("self")) {
							order_store_id = CommUtil
									.null2String(store.getId());
						}
						of.setOrder_id(SecurityUserHolder.getCurrentUser()
								.getId() + order_suffix + order_store_id);
						// 设置收货地址信息
						of.setReceiver_Name(addr.getTrueName());
						//cty 修改时间2015-3-12 增加内容 
                        if(addr.getArea() != null){
    	                	Map<String,Object> areaMap = CommUtil.getAreaInfo(addr.getArea(),1);
    						if(areaMap.size()>0){
    							of.setReceiver_area(areaMap.get("areaName").toString());
    						}
                        }
						of.setReceiver_area_info(addr.getArea_info());
						of.setReceiver_mobile(addr.getMobile());
						of.setReceiver_telephone(addr.getTelephone());
						of.setReceiver_zip(addr.getZip());
						of.setTransport(transport);
						of.setOrder_status(10);
						User buyer = this.userService.getObjById(CommUtil
								.null2Long(SecurityUserHolder.getCurrentUser()
										.getId()));
						of.setUser_id(buyer.getId().toString());
						of.setUser_name(buyer.getUserName());
						of.setGoods_info(Json.toJson(map_list,
								JsonFormat.compact()));// 设置商品信息json数据
						of.setMsg(request.getParameter("msg_" + sid));
						of.setInvoiceType(CommUtil.null2Int(request
								.getParameter("invoiceType_" + sid)));
						of.setInvoice(request.getParameter("invoice_" + sid));
						of.setShip_price(BigDecimal.valueOf(ship_price));
						of.setGoods_amount(BigDecimal.valueOf(goods_amount));
						double totalPrice = CommUtil.add(goods_amount,
								ship_price);// 订单总价
						of.setTotalPrice(BigDecimal.valueOf(totalPrice));
						String coupon_id = request.getParameter("coupon_id_"
								+ sid);
						if (coupon_id!=null &&!coupon_id.equals("")) {
							CouponInfo ci = this.couponInfoService
									.getObjById(CommUtil.null2Long(coupon_id));
							if (ci != null) {
								if (SecurityUserHolder.getCurrentUser().getId()
										.equals(ci.getUser().getId())) {
									ci.setStatus(1);
									this.couponInfoService.update(ci);
									Map coupon_map = new HashMap();
									coupon_map.put("couponinfo_id", ci.getId());
									coupon_map.put("couponinfo_sn",
											ci.getCoupon_sn());
									coupon_map.put("coupon_amount", ci
											.getCoupon().getCoupon_amount());
									double rate = CommUtil.div(ci.getCoupon()
											.getCoupon_amount(), goods_amount);
									coupon_map.put("coupon_goods_rate", rate);
									of.setCoupon_info(Json.toJson(coupon_map,
											JsonFormat.compact()));
									of.setTotalPrice(BigDecimal
											.valueOf(CommUtil.subtract(of
													.getTotalPrice(), ci
													.getCoupon()
													.getCoupon_amount())));
								}
							}
						}
						all_of_price = all_of_price
								+ CommUtil.null2Double(of.getTotalPrice());// 总订单价格
						if (sid.equals("self") || sid == "self") {
							of.setOrder_form(1);// 平台自营商品订单
						} else {
							of.setCommission_amount(BigDecimal
									.valueOf(commission_amount));// 该订单总体佣金费用
							of.setOrder_form(0);// 商家商品订单
							of.setStore_id(store.getId().toString());
							of.setStore_name(store.getStore_name());
						}
						of.setOrder_type("web");// 设置为PC网页订单
						if (i == store_list.length - 1) {
						
							of.setOrder_main(1);// 同时购买多个商家商品，最后一个订单为主订单，其他的作为子订单，以json信息保存，用在买家中心统一显示大订单，统一付款
							if (child_order_maps.size() > 0) {
								of.setChild_order_detail(Json.toJson(
										child_order_maps, JsonFormat.compact()));
							}
						}
						boolean flag = this.orderFormService.save(of);
						if (i == store_list.length - 1) {
							mv.addObject("order", of);// 将主订单信息封装到前台视图中
						}
						if (flag) {
							// 如果是多个店铺的订单同时提交，则记录子订单信息到主订单中，用在买家中心统一显示及统一付款
							if (store_list.length > 1) {
								Map order_map = new HashMap();
								order_map.put("order_id", of.getId());
								order_map.put("order_goods_info",
										of.getGoods_info());
								child_order_maps.add(order_map);
							}
							for (GoodsCart gc : gc_list) {// 删除已经提交订单的购物车信息
								for (String gc_id : gc_ids) {
									if (!CommUtil.null2String(gc_id).equals("")
											&& CommUtil.null2Long(gc_id)
													.equals(gc.getId())) {
										this.goodsCartService
												.delete(gc.getId());
									}
								}
							}
							OrderFormLog ofl = new OrderFormLog();
							ofl.setAddTime(new Date());
							ofl.setOf(of);
							ofl.setLog_info("提交订单");
							ofl.setLog_user(SecurityUserHolder.getCurrentUser());
							this.orderFormLogService.save(ofl);
							if (this.configService.getSysConfig()
									.isEmailEnable()) {// 如果系统启动了邮件功能，则发送邮件提示
								this.send_email(request, of, buyer.getEmail(),
										"email_tobuyer_order_submit_ok_notify");

								// this.sendMsgAndEmTools.sendEmail(request,"email_tobuyer_order_submit_ok_notify",buyer.getEmail(),
								// json);
							}
							if (this.configService.getSysConfig().isSmsEnbale()) {// 如果系统启用了短信功能，则发送短信提示
								this.send_sms(request, of, buyer.getMobile(),
										"sms_tobuyer_order_submit_ok_notify");
								Map map = new HashMap();
								// this.sendMsgAndEmTools.sendEmail(request,
								// "sms_tobuyer_order_submit_ok_notify",buyer.getMobile(),
								// json);
							}
						}
						}
							
					}
					mv.addObject("all_of_price",
							CommUtil.formatMoney(all_of_price));
					mv.addObject("paymentTools", paymentTools);
					request.getSession(false).removeAttribute("carts_gcs");
				} else {
					mv = new JModelAndView("error.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "订单已经失效");
					mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
				}
			} else {// 验证库存不成功，返回购物车，并给出提示！
				mv = new JModelAndView("error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "订单中商品库存为0，请删除后再提交订单");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/goods_cart1.htm");
			}

		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "订单信息错误");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		}
		return mv;
	}

	/**
	 * 根据商品及传递的规格信息，计算该规格商品的价格、库存量
	 * 
	 * @param goods
	 * @param gsp
	 * @return 价格、库存组成的Map
	 */
	private Map generic_default_info(Goods goods, String gsp) {
		double price = 0;
		Map map = new HashMap();
		int count = 0;
		if (goods.getGroup() != null && goods.getGroup_buy() == 2) {// 团购商品统一按照团购价格处理
			for (GroupGoods gg : goods.getGroup_goods_list()) {
				if (gg.getGroup().getId().equals(goods.getGroup().getId())) {
					count = gg.getGg_group_count() - gg.getGg_def_count();
					price = CommUtil.null2Double(gg.getGg_price());
				}
			}
		} else {
			count = goods.getGoods_inventory();
			price = CommUtil.null2Double(goods.getStore_price());
			if ("spec".equals(goods.getInventory_type())) {
				List<HashMap> list = Json.fromJson(ArrayList.class,
						goods.getGoods_inventory_detail());
				if (gsp != null) {
					String[] gsp_ids = gsp.split(",");
					for (Map temp : list) {
						String[] temp_ids = CommUtil
								.null2String(temp.get("id")).split("_");
						Arrays.sort(gsp_ids);
						Arrays.sort(temp_ids);
						if (Arrays.equals(gsp_ids, temp_ids)) {
							count = CommUtil.null2Int(temp.get("count"));
							price = CommUtil.null2Double(temp.get("price"));
						}
					}
				}

			}
		}
		map.put("price", price);
		map.put("count", count);
		return map;
	}

	@SecurityMapping(title = "订单支付详情", value = "/order_pay_view.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping("/order_pay_view.htm")
	public ModelAndView order_pay_view(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("order_pay.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		OrderForm of = this.orderFormService.getObjById(CommUtil.null2Long(id));
		User user = SecurityUserHolder.getCurrentUser();
		if (of != null && of.getUser_id().equals(user.getId().toString())) {
			if (of.getOrder_status() == 10) {
				if (of.getOrder_cat() == 1) {// 处理手机充值付款
					mv = new JModelAndView("recharge_order.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					String ofcard_userid = this.configService.getSysConfig()
							.getOfcard_userid();
					String ofcard_userpws = Md5Encrypt.md5(this.configService
							.getSysConfig().getOfcard_userpws());
					String rc_amount = CommUtil.null2String(of.getRc_amount());
					String mobile = of.getRc_mobile();
					String query_url = "http://api2.ofpay.com/telquery.do?userid="
							+ ofcard_userid
							+ "&userpws="
							+ ofcard_userpws
							+ "&phoneno="
							+ mobile
							+ "&pervalue="
							+ rc_amount
							+ "&version=6.0";
					String return_xml = this.getHttpContent(query_url,
							"gb2312", "POST");
					Map map = XMLUtil.parseXML(return_xml, true);
					double inprice = CommUtil.null2Double(map.get("inprice"));
					if (CommUtil.null2Double(map.get("inprice")) <= CommUtil
							.null2Double(rc_amount)) {
						inprice = CommUtil.add(map.get("inprice"),
								this.configService.getSysConfig()
										.getOfcard_mobile_profit());
						if (inprice > CommUtil.null2Double(rc_amount)) {
							inprice = CommUtil.null2Double(rc_amount);
						}
					}
					map.put("inprice", inprice);
					String recharge_pay_session = CommUtil.randomString(64);
					request.getSession(false).setAttribute(
							"recharge_pay_session", recharge_pay_session);
					mv.addObject("recharge_pay_session", recharge_pay_session);
					mv.addObject("map", map);
					mv.addObject("rc_amount", rc_amount);
					mv.addObject("mobile", mobile);
					mv.addObject("order", of);
				} else {
					mv.addObject("of", of);
					mv.addObject("paymentTools", this.paymentTools);
					mv.addObject("orderFormTools", this.orderFormTools);
					mv.addObject("url", CommUtil.getURL(request));
				}
			} else if (of.getOrder_status() < 10) {
				mv = new JModelAndView("error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "该订单已经取消");
				mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
			} else {
				mv = new JModelAndView("error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "该订单已付款");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/buyer/order.htm");
			}
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "该订单已失效");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "订单支付", value = "/order_pay.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping("/order_pay.htm")
	@Transactional
	public ModelAndView order_pay(HttpServletRequest request,
			HttpServletResponse response, String payType, String order_id) {
		ModelAndView mv = null;
		OrderForm order = this.orderFormService.getObjById(CommUtil
				.null2Long(order_id));
		if (order != null && order.getOrder_status() == 10) {
			if (CommUtil.null2String(payType).equals("")) {
				mv = new JModelAndView("error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "支付方式错误");
				mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
			} else {
				// 给订单添加支付方式 ,
				List<Payment> payments = new ArrayList<Payment>();
				Map params = new HashMap();
				params.put("mark", payType);
				payments = this.paymentService.query(
						"select obj from Payment obj where obj.mark=:mark",
						params, -1, -1);
				if(payments!=null && params.size()>0){
					order.setPayment(payments.get(0));
					order.setPayType(payType);
				}
				this.orderFormService.update(order);
				
				//如果存在子订单则也需要将支付方式存到子订单中（yxy 2015-04-21）
				if (order.getOrder_main() == 1&& !CommUtil.null2String(order.getChild_order_detail()).equals("")) {
					List<Map> maps = this.orderFormTools.queryGoodsInfo(order.getChild_order_detail());
					for (Map child_map : maps) {
						OrderForm child_order = this.orderFormService.getObjById(CommUtil.null2Long(child_map.get("order_id")));
						if(payments!=null && params.size()>0){
							child_order.setPayment(payments.get(0));
							child_order.setPayType(payType);
						}
						this.orderFormService.update(child_order);
					}
				}
				
				if (payType.equals("balance")) {// 使用预存款支付
					mv = new JModelAndView("balance_pay.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					double order_total_price = CommUtil.null2Double(order
							.getTotalPrice());
					if (!CommUtil.null2String(order.getChild_order_detail())
							.equals("")) {
						List<Map> maps = this.orderFormTools
								.queryGoodsInfo(order.getChild_order_detail());
						for (Map map : maps) {
							OrderForm child_order = this.orderFormService
									.getObjById(CommUtil.null2Long(map
											.get("order_id")));
							order_total_price = order_total_price
									+ CommUtil.null2Double(child_order
											.getTotalPrice());
						}
					}
					mv.addObject("order_total_price", order_total_price);
				} else if (payType.equals("payafter")) {// 使用货到付款
					mv = new JModelAndView("payafter_pay.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					String pay_session = CommUtil.randomString(32);
					request.getSession(false).setAttribute("pay_session",
							pay_session);
					mv.addObject("paymentTools", this.paymentTools);
					if (order.getOrder_form() == 0) {
						mv.addObject("store_id", this.orderFormService
								.getObjById(CommUtil.null2Long(order_id))
								.getStore_id());
					} else {
						mv.addObject("store_id", 0);
					}
					mv.addObject("pay_session", pay_session);
				} else {// 使用在线支付
					mv = new JModelAndView("line_pay.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					String code = request.getParameter("code");
					mv.addObject("code",code);
					mv.addObject("payType", payType);
					mv.addObject("url", CommUtil.getURL(request));
					mv.addObject("payTools", payTools);
					String type = "goods";
					if (order.getOrder_cat() == 2) {
						type = "group";// 订单属性为生活类团购
					}
					mv.addObject("type", type);
					mv.addObject("payment_id", order.getPayment().getId());
				}
				mv.addObject("order", order);
				mv.addObject("order_id", order.getId());
			}
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "参数错误，付款失败");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		}
		return mv;
	}
	@SecurityMapping(title = "订单支付", value = "/order_pay_wx.htm*", rtype = "buyer", rname = "购物流程3-微信", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping("/order_pay_wx.htm")
	@Transactional
	public ModelAndView order_pay_wx(HttpServletRequest request,
			HttpServletResponse response, String payType, String order_id) {
		ModelAndView mv = null;
		OrderForm order = this.orderFormService.getObjById(CommUtil
				.null2Long(order_id));
		if (order != null && order.getOrder_status() == 10) {
			if (CommUtil.null2String(payType).equals("")) {
				mv = new JModelAndView("error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "支付方式错误");
				mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
			} else {
				// 给订单添加支付方式 ,
				List<Payment> payments = new ArrayList<Payment>();
				Map params = new HashMap();
				params.put("mark", payType);
				payments = this.paymentService.query(
						"select obj from Payment obj where obj.mark=:mark",
						params, -1, -1);
				order.setPayment(payments.get(0));
				this.orderFormService.update(order);
				 // 使用在线支付
//					mv = new JModelAndView("line_pay.html",
//							configService.getSysConfig(),
//							this.userConfigService.getUserConfig(), 1, request,
//							response);
//					String code = request.getParameter("code");
//					mv.addObject("code",code);
//					mv.addObject("payType", payType);
//					mv.addObject("url", CommUtil.getURL(request));
//					mv.addObject("payTools", payTools);
					String type = "goods";
					if (order.getOrder_cat() == 2) {
						type = "group";// 订单属性为生活类团购
					}
//					mv.addObject("type", type);
//					mv.addObject("payment_id", order.getPayment().getId());
//				mv.addObject("order", order);
//				mv.addObject("order_id", order.getId());
				String code_url=payTools.genericWechatpay(CommUtil.getURL(request), order.getPayment().getId().toString(), type, order.getId().toString(),CommUtil.getIpAddr(request),request.getSession().getServletContext().getRealPath(""));
				response.setContentType("text/plain");
				response.setHeader("Cache-Control", "no-cache");
				response.setCharacterEncoding("UTF-8");
				PrintWriter writer;
				try {
					writer = response.getWriter();
					writer.print(code_url);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "参数错误，付款失败");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "订单货到付款", value = "/order_pay_payafter.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping("/order_pay_payafter.htm")
	@Transactional
	public ModelAndView order_pay_payafter(HttpServletRequest request,
			HttpServletResponse response, String payType, String order_id,
			String pay_msg, String pay_session) throws Exception {
		ModelAndView mv = new JModelAndView("success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String pay_session1 = CommUtil.null2String(request.getSession(false)
				.getAttribute("pay_session"));
		if (pay_session1.equals(pay_session)) {
			OrderForm order = this.orderFormService.getObjById(CommUtil
					.null2Long(order_id));
			order.setPay_msg(pay_msg);
			Map params = new HashMap();
			params.put("mark", "payafter");
			List<Payment> payments = this.paymentService.query(
					"select obj from Payment obj where obj.mark=:mark", params,
					-1, -1);
			if (payments.size() > 0) {
				order.setPayment(payments.get(0));
				order.setPayTime(new Date());
			}
			order.setOrder_status(16);
			this.orderFormService.update(order);
			if (order.getOrder_main() == 1
					&& !CommUtil.null2String(order.getChild_order_detail())
							.equals("")) {
				List<Map> maps = this.orderFormTools.queryGoodsInfo(order
						.getChild_order_detail());
				for (Map child_map : maps) {
					OrderForm child_order = this.orderFormService
							.getObjById(CommUtil.null2Long(child_map
									.get("order_id")));
					child_order.setOrder_status(16);
					if (payments.size() > 0) {
						child_order.setPayment(payments.get(0));
						child_order.setPayTime(new Date());
					}
					child_order.setPay_msg(pay_msg);
					this.orderFormService.update(child_order);
					// 向加盟商家发送付款成功短信提示，自营商品无需发送短信提示
					Store store = this.storeService.getObjById(CommUtil
							.null2Long(child_order.getStore_id()));
					if (this.configService.getSysConfig().isSmsEnbale()
							&& child_order.getOrder_form() == 0) {
						this.send_sms(request, child_order, store.getUser()
								.getMobile(),
								"sms_toseller_payafter_pay_ok_notify");
					}
					if (this.configService.getSysConfig().isEmailEnable()
							&& child_order.getOrder_form() == 0) {
						this.send_email(request, child_order, store.getUser()
								.getEmail(),
								"email_toseller_payafter_pay_ok_notify");
					}
				}
			}
			// 记录支付日志
			OrderFormLog ofl = new OrderFormLog();
			ofl.setAddTime(new Date());
			ofl.setLog_info("提交货到付款申请");
			ofl.setLog_user(SecurityUserHolder.getCurrentUser());
			ofl.setOf(order);
			this.orderFormLogService.save(ofl);
			request.getSession(false).removeAttribute("pay_session");
			mv.addObject("op_title", "货到付款提交成功，等待发货");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "订单已经支付，禁止重复支付");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
		}
		return mv;
	}
	/*
	 * 2015-03-13
	 * liuneng
	 * 发送消息
	 */
	public void sendEmailOrSms(HttpServletRequest request,OrderForm order,Store store,String main) throws Exception{
		if (this.configService.getSysConfig()
				.isEmailEnable()) {
			if (order.getOrder_form() == 0) {
				this.send_email(request, order, store
						.getUser().getEmail(),
						"email_toseller_balance_pay_ok_notify");
			}
			if(main.equals("main")){
				User buyer = this.userService.getObjById(CommUtil.null2Long(order.getUser_id()));
				this.send_email(request, order, buyer.getEmail(),"email_tobuyer_balance_pay_ok_notify");
			}

		}
		if (this.configService.getSysConfig().isSmsEnbale()) {
			if (order.getOrder_form() == 0) {
				this.send_sms(request, order, store.getUser().getMobile(),"sms_toseller_balance_pay_ok_notify");
			}
			if(main.equals("main")){
				User buyer = this.userService.getObjById(CommUtil.null2Long(order.getUser_id()));
				this.send_sms(request, order,buyer.getMobile(),"sms_tobuyer_balance_pay_ok_notify");
			}
		}
	}

	@SecurityMapping(title = "订单预付款支付", value = "/order_pay_balance.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping("/order_pay_balance.htm")
	@Transactional
	public ModelAndView order_pay_balance(HttpServletRequest request,
			HttpServletResponse response, String payType, String order_id,
			String pay_msg) throws Exception {
		ModelAndView mv = new JModelAndView("success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		OrderForm order = this.orderFormService.getObjById(CommUtil
				.null2Long(order_id));
		if (order != null && order.getOrder_status() < 20) {// 订单不为空且订单状态为未付款才可以正常使用预存款付款
			Map params = new HashMap();
			params.put("mark", "balance");
			List<Payment> payments = this.paymentService.query(
					"select obj from Payment obj where obj.mark=:mark", params,
					-1, -1);
			double order_total_price = CommUtil.null2Double(order
					.getTotalPrice());
			if (!CommUtil.null2String(order.getChild_order_detail()).equals("")
					&& order.getOrder_cat() != 2) {
				List<Map> maps = this.orderFormTools.queryGoodsInfo(order
						.getChild_order_detail());
				for (Map map : maps) {
					OrderForm child_order = this.orderFormService
							.getObjById(CommUtil.null2Long(map.get("order_id")));
					order_total_price = order_total_price
							+ CommUtil.null2Double(child_order.getTotalPrice());
				}
			}
			User user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
			if (CommUtil.null2Double(user.getAvailableBalance()) >= order_total_price) {
				order.setPay_msg(pay_msg);
				order.setOrder_status(20);
				if (payments.size() > 0) {
					order.setPayment(payments.get(0));
					order.setPayTime(new Date());
				}
				boolean ret = this.orderFormService.update(order);
				if (ret) {
					Store mainStore = this.storeService.getObjById(CommUtil.null2Long(order.getStore_id()));
					sendEmailOrSms(request,order,mainStore,"main");
					// 预存款付款成功后，执行子订单状态改变及发送提醒信息
					if (order.getOrder_main() == 1
							&& !CommUtil.null2String(
									order.getChild_order_detail()).equals("")
							&& order.getOrder_cat() != 2) {
						List<Map> maps = this.orderFormTools
								.queryGoodsInfo(order.getChild_order_detail());
						for (Map child_map : maps) {
							OrderForm child_order = this.orderFormService
									.getObjById(CommUtil.null2Long(child_map
											.get("order_id")));
							child_order.setOrder_status(20);
							if (payments.size() > 0) {
								child_order.setPayment(payments.get(0));
								child_order.setPayTime(new Date());
							}
							boolean boo=this.orderFormService.update(child_order);
							// 向加盟商家发送付款成功短信提示，自营商品无需发送短信提示
							Store store = this.storeService.getObjById(CommUtil
									.null2Long(child_order.getStore_id()));
							//发送信息
							if(boo){
								sendEmailOrSms(request,child_order,store,"child");
							}
						}
					}
					// 如果是团购订单，则需呀执行团购订单相关流程及发送团购码
					if (order.getOrder_cat() == 2) {
						Calendar ca = Calendar.getInstance();
						ca.add(ca.DATE, this.configService.getSysConfig()
								.getAuto_order_return());
						SimpleDateFormat bartDateFormat = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss");
						String latertime = bartDateFormat.format(ca.getTime());
						order.setReturn_shipTime(CommUtil.formatDate(latertime));
						Map map = this.orderFormTools.queryGroupInfo(order
								.getGroup_info());
						int count = CommUtil.null2Int(map.get("goods_count")
								.toString());
						String goods_id = map.get("goods_id").toString();
						GroupLifeGoods goods = this.groupLifeGoodsService
								.getObjById(CommUtil.null2Long(goods_id));
						goods.setGroup_count(goods.getGroup_count()
								- CommUtil.null2Int(count));
						this.groupLifeGoodsService.update(goods);
						int i = 0;
						List<String> code_list = new ArrayList();// 存放团购消费码
						String codes = "";
						while (i < count) {
							GroupInfo info = new GroupInfo();
							info.setAddTime(new Date());
							info.setLifeGoods(goods);
							info.setPayment(payments.get(0));
							info.setUser_id(user.getId());
							info.setUser_name(user.getUserName());
							info.setOrder_id(order.getId());
							info.setGroup_sn(user.getId()
									+ CommUtil.formatTime("yyyyMMddHHmmss" + i,
											new Date()));
							Calendar ca2 = Calendar.getInstance();
							ca2.add(ca2.DATE, this.configService.getSysConfig()
									.getGrouplife_order_return());
							SimpleDateFormat bartDateFormat2 = new SimpleDateFormat(
									"yyyy-MM-dd HH:mm:ss");
							String latertime2 = bartDateFormat2.format(ca2
									.getTime());
							info.setRefund_Time(CommUtil.formatDate(latertime2));
							this.groupInfoService.save(info);
							codes = codes + info.getGroup_sn() + " ";
							code_list.add(info.getGroup_sn());
							i++;
						}
						if (order.getOrder_form() == 0) {
							Store store = this.storeService.getObjById(CommUtil
									.null2Long(order.getStore_id()));
							PayoffLog plog = new PayoffLog();
							plog.setPl_sn("pl"
									+ CommUtil.formatTime("yyyyMMddHHmmss",
											new Date())
									+ store.getUser().getId());
							plog.setPl_info("团购码生成成功");
							plog.setAddTime(new Date());
							plog.setSeller(store.getUser());
							plog.setO_id(CommUtil.null2String(order.getId()));
							plog.setOrder_id(order.getOrder_id().toString());
							plog.setCommission_amount(BigDecimal
									.valueOf(CommUtil.null2Double("0.00")));// 该订单总佣金费用
							// 将订单中group_info（{}）转换为List<Map>([{}])
							List<Map> Map_list = new ArrayList<Map>();
							Map group_map = this.orderFormTools
									.queryGroupInfo(order.getGroup_info());
							Map_list.add(group_map);
							plog.setGoods_info(Json.toJson(Map_list,
									JsonFormat.compact()));
							plog.setOrder_total_price(order.getTotalPrice());// 该订单总商品金额
							plog.setTotal_amount(order.getTotalPrice());// 该订单应结算金额：结算金额=订单总商品金额-总佣金费用
							this.payoffLogService.save(plog);
							store.setStore_sale_amount(BigDecimal
									.valueOf(CommUtil.add(
											order.getTotalPrice(),
											store.getStore_sale_amount())));// 店铺本次结算总销售金额
							// 团购消费码，没有佣金，店铺总佣金不变
							store.setStore_payoff_amount(BigDecimal
									.valueOf(CommUtil.add(
											order.getTotalPrice(),
											store.getStore_payoff_amount())));// 店铺本次结算总佣金
							this.storeService.update(store);
						}
						// 增加系统总销售金额、消费码没有佣金，系统总佣金不变
						SysConfig sc = this.configService.getSysConfig();
						sc.setPayoff_all_sale(BigDecimal.valueOf(CommUtil.add(
								order.getTotalPrice(), sc.getPayoff_all_sale())));
						this.configService.update(sc);
						// 更新lucene索引
						elasticsearchUtil.indexUpdate(IndexName.GOODS, IndexType.GROUPLIFEGOODS, 
								goods.getId().toString(), IndexVoTools.groupLifeGoodsToIndexVo(goods));
//						String goods_lucene_path = ConfigContants.LUCENE_DIRECTORY
//								+ File.separator
//								+ "luence" + File.separator + "grouplifegoods";
//						File file = new File(goods_lucene_path);
//						if (!file.exists()) {
//							CommUtil.createFolder(goods_lucene_path);
//						}
//						LuceneUtil lucene = LuceneUtil.instance();
//						lucene.setIndex_path(goods_lucene_path);
//						lucene.update(CommUtil.null2String(goods.getId()),
//								luceneVoTools.updateLifeGoodsIndex(goods));
						String msg_content = "恭喜您成功购买团购"
								+ map.get("goods_name") + ",团购消费码分别为：" + codes
								+ "您可以到用户中心-我的生活购中查看消费码的使用情况";
						// 发送系统站内信给买家
						Message tobuyer_msg = new Message();
						tobuyer_msg.setAddTime(new Date());
						tobuyer_msg.setStatus(0);
						tobuyer_msg.setType(0);
						tobuyer_msg.setContent(msg_content);
						tobuyer_msg.setFromUser(this.userService
								.getObjByProperty("userName", "admin"));
						tobuyer_msg.setToUser(user);
						this.messageService.save(tobuyer_msg);
						// 付款成功，发送短信团购消费码
						if (this.configService.getSysConfig().isSmsEnbale()) {
							this.send_groupInfo_sms(request, order,
									user.getMobile(),
									"sms_tobuyer_online_ok_send_groupinfo",
									code_list, user.getId().toString(), goods
											.getUser().getId().toString());
						}
					}
					user.setAvailableBalance(BigDecimal.valueOf(CommUtil
							.subtract(user.getAvailableBalance(),
									order_total_price)));
					this.userService.update(user);
					PredepositLog log = new PredepositLog();
					log.setAddTime(new Date());
					log.setPd_log_user(user);
					log.setPd_op_type("消费");
					log.setPd_log_amount(BigDecimal.valueOf(-CommUtil
							.null2Double(order.getTotalPrice())));
					log.setPd_log_info(order.getOrder_id() + "订单购物减少可用预存款");
					log.setPd_type("可用预存款");
					this.predepositLogService.save(log);
					// 执行库存减少,如果是团购商品，团购库存同步减少
					if (order.getOrder_cat() != 2) {
						List<Goods> goods_list = this.orderFormTools
								.queryOfGoods(CommUtil.null2String(order
										.getId()));
						for (Goods goods : goods_list) {
							int goods_count = this.orderFormTools
									.queryOfGoodsCount(
											CommUtil.null2String(order.getId()),
											CommUtil.null2String(goods.getId()));
							if (goods.getGroup() != null
									&& goods.getGroup_buy() == 2) {
								for (GroupGoods gg : goods
										.getGroup_goods_list()) {
									if (gg.getGroup().getId()
											.equals(goods.getGroup().getId())) {

										gg.setGg_count(gg.getGg_count()
												- goods_count);
										gg.setGg_def_count(gg.getGg_def_count()
												+ goods_count);
										gg.setGg_selled_count(gg
												.getGg_selled_count()
												+ goods_count);
										this.groupGoodsService.update(gg);
									}
								}
							}
							List<String> gsps = new ArrayList<String>();
							List<GoodsSpecProperty> temp_gsp_list = this.orderFormTools
									.queryOfGoodsGsps(
											CommUtil.null2String(order.getId()),
											CommUtil.null2String(goods.getId()));
							for (GoodsSpecProperty gsp : temp_gsp_list) {
								gsps.add(gsp.getId().toString());
							}
							String[] gsp_list = new String[gsps.size()];
							gsps.toArray(gsp_list);
							goods.setGoods_salenum(goods.getGoods_salenum()
									+ goods_count);
							if (goods.getInventory_type().equals("all")) {
								goods.setGoods_inventory(goods
										.getGoods_inventory() - goods_count);
							} else {
								List<HashMap> list = Json.fromJson(
										ArrayList.class,
										goods.getGoods_inventory_detail());
								for (Map temp : list) {
									String[] temp_ids = CommUtil.null2String(
											temp.get("id")).split("_");
									Arrays.sort(temp_ids);
									Arrays.sort(gsp_list);
									if (Arrays.equals(temp_ids, gsp_list)) {
										temp.put(
												"count",
												CommUtil.null2Int(temp
														.get("count"))
														- goods_count);
									}
								}
								goods.setGoods_inventory_detail(Json.toJson(
										list, JsonFormat.compact()));
							}
							//2015-03-18 liuneng 解决当商品不是团购商品报空指针异常
							if(goods.getGroup()!=null){
								for (GroupGoods gg : goods.getGroup_goods_list()) {
									if (gg.getGroup().getId()
											.equals(goods.getGroup().getId())
											&& gg.getGg_count() == 0) {
										goods.setGroup_buy(3);// 标识商品的状态为团购数量已经结束
									}
								}
							}

							this.goodsService.update(goods);
							// 更新lucene索引
							if (goods.getGroup_buy() == 2) {
								elasticsearchUtil.indexUpdate(IndexName.GOODS, IndexType.GOODS, 
										CommUtil.null2String(goods.getId()), IndexVoTools.groupGoodsToIndexVo(goods.getGroup_goods_list()
												.get(goods.getGroup_goods_list().size() - 1)));
//								String goods_lucene_path = ConfigContants.LUCENE_DIRECTORY
//										+ File.separator
//										+ "luence"
//										+ File.separator + "goods";
//								File file = new File(goods_lucene_path);
//								if (!file.exists()) {
//									CommUtil.createFolder(goods_lucene_path);
//								}
//								LuceneUtil lucene = LuceneUtil.instance();
//								lucene.setIndex_path(goods_lucene_path);
//								lucene.update(CommUtil.null2String(goods
//										.getId()), luceneVoTools
//										.updateGroupGoodsIndex(goods
//												.getGroup_goods_list()
//												.get(goods
//														.getGroup_goods_list()
//														.size() - 1)));
							} else {
								elasticsearchUtil.indexUpdate(IndexName.GOODS, IndexType.GOODS, 
										CommUtil.null2String(goods.getId()), IndexVoTools.goodsToIndexVo(goods));
//								String goods_lucene_path = ConfigContants.LUCENE_DIRECTORY
//										+ File.separator
//										+ "luence"
//										+ File.separator + "goods";
//								File file = new File(goods_lucene_path);
//								if (!file.exists()) {
//									CommUtil.createFolder(goods_lucene_path);
//								}
//								LuceneUtil lucene = LuceneUtil.instance();
//								lucene.setIndex_path(goods_lucene_path);
//								lucene.update(
//										CommUtil.null2String(goods.getId()),
//										luceneVoTools.updateGoodsIndex(goods));
							}
						}
					}
				}
				// 记录支付日志
				OrderFormLog ofl = new OrderFormLog();
				ofl.setAddTime(new Date());
				ofl.setLog_info("预付款支付");
				ofl.setLog_user(SecurityUserHolder.getCurrentUser());
				ofl.setOf(order);
				this.orderFormLogService.save(ofl);
				mv.addObject("op_title", "预付款支付成功");
				if (order.getOrder_cat() == 2) {
					mv.addObject("url", CommUtil.getURL(request)
							+ "/buyer/index.htm");
				} else {
					mv.addObject("url", CommUtil.getURL(request)
							+ "/buyer/order.htm");
				}
			} else {
				mv = new JModelAndView("error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "可用余额不足，支付失败");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/buyer/order.htm");
			}
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "参数错误，支付失败");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "订单支付结果", value = "/order_finish.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping("/order_finish.htm")
	public ModelAndView order_finish(HttpServletRequest request,
			HttpServletResponse response, String order_id) {
		ModelAndView mv = new JModelAndView("order_finish.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		OrderForm obj = this.orderFormService.getObjById(CommUtil
				.null2Long(order_id));
		if(CommUtil.isActivityDate()){
			//判断是否有子订单
			if (obj.getOrder_main() == 1
					&& !CommUtil.null2String(
							obj.getChild_order_detail()).equals("")
					&& obj.getOrder_cat() != 2) {
				List<Map> maps = this.orderFormTools
						.queryGoodsInfo(obj.getChild_order_detail());
				for (Map child_map : maps) {
					OrderForm child_order = this.orderFormService
							.getObjById(CommUtil.null2Long(child_map
									.get("order_id")));
					//判断订单的店铺是否为活动店铺且订单状态必须是已付款状态
					if(ActFileTools.STORE_IDS_LIST.contains(child_order.getStore_id()) && obj.getOrder_status() >=20){
						Map params = new HashMap();
						params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
						params.put("remark", child_order.getOrder_id());
						List<CouponInfo> couponList = this.couponInfoService.query(""
								+ "select obj from CouponInfo obj where obj.user.id=:user_id and obj.remark=:remark", params, -1, -1);
						if(couponList.size() <1){
							mv.addObject("orderNo", child_order.getOrder_id());
						}
					}
				}
			}
			//判断订单的店铺是否为活动店铺且订单状态必须是已付款状态
			if(ActFileTools.STORE_IDS_LIST.contains(obj.getStore_id()) && obj.getOrder_status() >=20){
				Map params = new HashMap();
				params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
				params.put("remark", obj.getOrder_id());
				List<CouponInfo> couponList = this.couponInfoService.query(""
						+ "select obj from CouponInfo obj where obj.user.id=:user_id and obj.remark=:remark", params, -1, -1);
				if(couponList.size() <1){
					mv.addObject("orderNo", obj.getOrder_id());
				}
			}
		}
		mv.addObject("orderFormTools", orderFormTools);
		mv.addObject("obj", obj);
		return mv;
	}

	@SecurityMapping(title = "地址新增", value = "/cart_address.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping("/cart_address.htm")
	public ModelAndView cart_address(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("cart_address.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		List<Area> areas = this.areaService.query(
				"select obj from Area obj where obj.parent.id is null", null,
				-1, -1);
		Map params = new HashMap();
		params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
		List<Address> addrs = this.addressService
				.query("select obj from Address obj where obj.user.id=:user_id order by obj.addTime desc",
						params, -1, -1);
		if (id != null && !id.equals("")) {
			Address obj = this.addressService
					.getObjById(CommUtil.null2Long(id));
			if (obj != null) {
				if (SecurityUserHolder.getCurrentUser().getId()
						.equals(obj.getUser().getId())) {
					mv.addObject("obj", obj);
				} else {
					mv.addObject("error", true);
				}
			} else {
				mv.addObject("error", true);
			}
		}
		mv.addObject("addrs_size", addrs.size());
		mv.addObject("areas", areas);
		return mv;
	}

	@SecurityMapping(title = "地址新增保存", value = "/cart_address_save.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping("/cart_address_save.htm")
	@Transactional
	public String cart_address_save(HttpServletRequest request,
			HttpServletResponse response, String id, String area_id) {
		WebForm wf = new WebForm();
		Address address = null;
		if (id.equals("")) {
			address = wf.toPo(request, Address.class);
			address.setAddTime(new Date());
		} else {
			Address obj = this.addressService.getObjById(Long.parseLong(id));
			address = (Address) wf.toPo(request, obj);
		}
		address.setUser(SecurityUserHolder.getCurrentUser());
		Area area = this.areaService.getObjById(CommUtil.null2Long(area_id));
		address.setArea(area);
		if (id.equals("")) {
			this.addressService.save(address);
		} else
			this.addressService.update(address);
		return "redirect:goods_cart2.htm";
	}

	@SecurityMapping(title = "订单地址删除", value = "/cart_address_dele.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping("/cart_address_dele.htm")
	@Transactional
	public void cart_address_dele(HttpServletRequest request,
			HttpServletResponse response, String id) {
		boolean ret = false;
		if (id != null && !id.equals("")) {
			Address addr = this.addressService.getObjById(CommUtil
					.null2Long(id));
			if (addr != null) {
				if (SecurityUserHolder.getCurrentUser().getId()
						.equals(addr.getUser().getId())) {
					ret = this.addressService.delete(addr.getId());
				}
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

	@SecurityMapping(title = "地址切换", value = "/order_address.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping("/order_address.htm")
	public void order_address(HttpServletRequest request,String gcIds,
			HttpServletResponse response, String addr_id, String store_id) {
		List<GoodsCart> carts = this.cart_calc(request);
		List<GoodsCart> gc_list = new ArrayList<GoodsCart>();
		String[] ids = gcIds.split(",");
		for (GoodsCart gc : carts) {
			if (store_id != "self" && !store_id.equals("self")) {
				for(String id : ids){
					if (gc.getGoods().getGoods_type() == 1 && gc.getId().equals(CommUtil.null2Long(id))) {
						gc_list.add(gc);
					}
				}
			} else {
				if (gc.getGoods().getGoods_type() == 0) {
					gc_list.add(gc);
				}
			}
		}
		Address addr = this.addressService.getObjById(CommUtil
				.null2Long(addr_id));
		List<SysMap> sms = this.transportTools.query_cart_trans(gc_list,
				CommUtil.null2String(addr.getArea().getId()));
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(sms, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void send_email(HttpServletRequest request, OrderForm order,
			String email, String mark) throws Exception {
		Template template = this.templateService.getObjByProperty("mark", mark);
		if (template != null && template.isOpen()) {
			String subject = template.getTitle();
			String path =  TytsmsStringUtils.generatorFilesFolderServerPath(request) + ConfigContants.GENERATOR_FILES_MIDDLE_NAME + File.separator;
			if (!CommUtil.fileExist(path)) {
				CommUtil.createFolder(path);
			}
			PrintWriter pwrite = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(path + mark+".vm", false), "UTF-8"));
			pwrite.print(template.getContent());
			pwrite.flush();
			pwrite.close();
		
			VelocityEngine velocityEngine = new VelocityEngine();
            Properties properties = new Properties();
            properties.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
            properties.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
            properties.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH,path);
            System.out.println(properties.get(Velocity.FILE_RESOURCE_LOADER_PATH));
            velocityEngine.init(properties);  
            org.apache.velocity.Template blank = velocityEngine.getTemplate(mark+".vm");
			// 生成模板
			VelocityContext context = new VelocityContext();
			User buyer = this.userService.getObjById(CommUtil.null2Long(order
					.getUser_id()));
			context.put("buyer", buyer);
			if (order.getOrder_form() == 0) {
				Store store = this.storeService.getObjById(CommUtil
						.null2Long(order.getStore_id()));
				context.put("seller", store.getUser());
			}
			context.put("config", this.configService.getSysConfig());
			context.put("send_time", CommUtil.formatLongDate(new Date()));
			context.put("webPath", CommUtil.getURL(request));
			context.put("order", order);
			StringWriter writer = new StringWriter();
			blank.merge(context, writer);
			String content = writer.toString();
			this.msgTools.sendEmail(email, subject, content);
		}
	}

	private void send_sms(HttpServletRequest request, OrderForm order,
			String mobile, String mark){
		try{
			Template template = this.templateService.getObjByProperty("mark", mark);
			if (template != null && template.isOpen()) {
				String path =  TytsmsStringUtils.generatorFilesFolderServerPath(request)+ ConfigContants.GENERATOR_FILES_MIDDLE_NAME + File.separator;
				if (!CommUtil.fileExist(path)) {
					CommUtil.createFolder(path);
				}
				PrintWriter pwrite = new PrintWriter(new OutputStreamWriter(
						new FileOutputStream(path + mark+".vm", false), "UTF-8"));
				pwrite.print(CommUtil.filterHTML(template.getContent()));
				pwrite.flush();
				pwrite.close();
				// 生成模板
				VelocityEngine velocityEngine = new VelocityEngine();
	            Properties properties = new Properties();
	            properties.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
	            properties.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
	            properties.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH,path);
	            System.out.println(properties.get(Velocity.FILE_RESOURCE_LOADER_PATH));
	            velocityEngine.init(properties);  
	            org.apache.velocity.Template blank = velocityEngine.getTemplate(mark+".vm");
	            
				/*Properties p = new Properties();
				p.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH,TytsmsStringUtils.generatorFilesFolderServerPath(request) + ConfigContants.GENERATOR_FILES_MIDDLE_NAME + File.separator);
				p.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
				p.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
				Velocity.init(p);
				org.apache.velocity.Template blank = Velocity.getTemplate(mark+".vm","UTF-8");*/
				VelocityContext context = new VelocityContext();
				User buyer = this.userService.getObjById(CommUtil.null2Long(order
						.getUser_id()));
				context.put("buyer", buyer);
				if (order.getOrder_form() == 0) {
					Store store = this.storeService.getObjById(CommUtil
							.null2Long(order.getStore_id()));
					context.put("seller", store.getUser());
				}
				context.put("config", this.configService.getSysConfig());
				context.put("send_time", CommUtil.formatLongDate(new Date()));
				context.put("webPath", CommUtil.getURL(request));
				context.put("order", order);
				StringWriter writer = new StringWriter();
				blank.merge(context, writer);
				String content = writer.toString();
				this.msgTools.sendSMS(mobile, content);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private static String getHttpContent(String url, String charSet,
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

	private void send_groupInfo_sms(HttpServletRequest request,
			OrderForm order, String mobile, String mark, List<String> codes,
			String buyer_id, String seller_id){
		try{
			Template template = this.templateService.getObjByProperty("mark", mark);
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < codes.size(); i++) {
				sb.append(codes.get(i) + ",");
			}
			String code = sb.toString();
			if (template != null && template.isOpen()) {
				String path =  TytsmsStringUtils.generatorFilesFolderServerPath(request)+ ConfigContants.GENERATOR_FILES_MIDDLE_NAME+File.separator;
				PrintWriter pwrite = new PrintWriter(new OutputStreamWriter(
						new FileOutputStream(path + mark+".vm", false), "UTF-8"));
				pwrite.print(template.getContent());
				pwrite.flush();
				pwrite.close();
				// 生成模板
				Properties p = new Properties();
				p.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, TytsmsStringUtils.generatorFilesFolderServerPath(request) + ConfigContants.GENERATOR_FILES_MIDDLE_NAME + File.separator);
				p.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
				p.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
				Velocity.init(p);
				org.apache.velocity.Template blank = Velocity.getTemplate(mark+".vm",
						"UTF-8");
				VelocityContext context = new VelocityContext();
				context.put("buyer",
						this.userService.getObjById(CommUtil.null2Long(buyer_id)));
				context.put("seller",
						this.userService.getObjById(CommUtil.null2Long(seller_id)));
				context.put("config", this.configService.getSysConfig());
				context.put("send_time", CommUtil.formatLongDate(new Date()));
				context.put("webPath", CommUtil.getURL(request));
				context.put("order", order);
				Map map = Json.fromJson(Map.class, order.getGroup_info());
				context.put("group_info", map.get("goods_name"));
				context.put("code", code);
				StringWriter writer = new StringWriter();
				blank.merge(context, writer);
				// System.out.println(writer.toString());
				String content = writer.toString();
				this.msgTools.sendSMS(mobile, content);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
