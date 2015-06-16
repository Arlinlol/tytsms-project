package com.iskyshop.view.mobile.action;

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
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.tools.CommUtil;
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
import com.iskyshop.manage.buyer.Tools.CartTools;
import com.iskyshop.manage.seller.Tools.TransportTools;
import com.iskyshop.pay.tools.PayTools;
import com.iskyshop.pay.wechatpay.util.ConfigContants;
import com.iskyshop.pay.wechatpay.util.TytsmsStringUtils;
import com.taiyitao.elasticsearch.ElasticsearchUtil;
import com.taiyitao.elasticsearch.IndexVo.IndexName;
import com.taiyitao.elasticsearch.IndexVo.IndexType;
import com.taiyitao.elasticsearch.IndexVoTools;

/**
 * 
 * <p>
 * Title: MobileCartViewAction.java
 * </p>
 * 
 * <p>
 * Description: 手机端购物控制器,包括购物车所有操作及订单相关操作
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
 * @date 2014-7-28
 * 
 * @version 1.0
 */
@Controller
public class MobileCartViewAction {
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
	private MsgTools msgTools;
	@Autowired
	private CartTools cartTools;
	@Autowired
	private ITemplateService templateService;
	@Autowired
	private IPaymentService paymentService;
	@Autowired
	private IGroupLifeGoodsService groupLifeGoodsService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IGoodsCartService goodsCartService;
	@Autowired
	private IOrderFormLogService orderFormLogService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IPredepositLogService predepositLogService;
	@Autowired
	private IGroupGoodsService groupGoodsService;
	@Autowired
	private ICouponInfoService couponInfoService;
	@Autowired
	private PaymentTools paymentTools;
	@Autowired
	private PayTools payTools;
	@Autowired
	private OrderFormTools orderTools;
	@Autowired
	private TransportTools transportTools;
	@Autowired
	private OrderFormTools orderFormTools;
	@Autowired
	private IGroupInfoService groupInfoService;
	@Autowired
	private IMessageService messageService;
	@Autowired
	private IPayoffLogService payoffLogService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private ElasticsearchUtil elasticsearchUtil;

	/**
	 * 合并购物车信息
	 * 
	 * @return
	 */
	private List<GoodsCart> cart_calc(String user_id, String token,
			String cart_mobile_ids) {
		List<GoodsCart> carts_list = new ArrayList<GoodsCart>();// 用户整体购物车
		List<GoodsCart> carts_mobile = new ArrayList<GoodsCart>();// 未提交的用户mobile购物车
		List<GoodsCart> carts_user = new ArrayList<GoodsCart>();// 未提交的用户user购物车
		Map cart_map = new HashMap();
		Set mark_ids = new TreeSet();
		User user = null;
		if (user_id != null && !user_id.equals("")) {
			user = this.userService.getObjById(CommUtil.null2Long(user_id));
			if (!user.getApp_login_token().equals(token.toLowerCase())) {
				user = null;
			}
		}
		if (cart_mobile_ids == null || cart_mobile_ids.equals("")) {
			cart_mobile_ids = "0";
		}
		String mobile_ids[] = cart_mobile_ids.split(",");
		for (String mobile_id : mobile_ids) {
			if (!mobile_id.equals("")) {
				mark_ids.add(mobile_id);
			}
		}
		if (user != null) {
			cart_map.clear();
			cart_map.put("mark_ids", mark_ids);
			cart_map.put("cart_status", 0);
			carts_mobile = this.goodsCartService
					.query("select obj from GoodsCart obj where obj.cart_mobile_id in (:mark_ids) and obj.cart_status=:cart_status ",
							cart_map, -1, -1);
			// 如果用户拥有自己的店铺，删除carts_mobile购物车中自己店铺中的商品信息
			if (user.getStore() != null) {
				for (GoodsCart gc : carts_mobile) {
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
			cart_map.put("mark_ids", mark_ids);
			cart_map.put("cart_status", 0);
			carts_mobile = this.goodsCartService
					.query("select obj from GoodsCart obj where obj.cart_mobile_id in (:mark_ids) and obj.cart_status=:cart_status ",
							cart_map, -1, -1);
		}
		// 将mobile购物车与用户user购物车合并，去重
		if (user != null) {
			for (GoodsCart ugc : carts_user) {
				carts_list.add(ugc);
			}
			for (GoodsCart mobile : carts_mobile) {
				boolean add = true;
				for (GoodsCart gc2 : carts_user) {
					if (mobile.getGoods().getId()
							.equals(gc2.getGoods().getId())) {
						if (mobile.getSpec_info().equals(gc2.getSpec_info())) {
							add = false;
							this.goodsCartService.delete(mobile.getId());
						}
					}
				}
				if (add) {// 将carts_mobile转变为user_cart
					mobile.setCart_mobile_id(null);
					mobile.setUser(user);
					this.goodsCartService.update(mobile);
					carts_list.add(mobile);
				}
			}
		} else {
			for (GoodsCart mobile : carts_mobile) {
				carts_list.add(mobile);
			}
		}
		
		carts_list = this.goodsCartService.cart_calc(carts_list); 
		
		return carts_list;
	}

	
	/**
	 * 手机端添加购物车
	 * 
	 * @param request
	 * @param response
	 * @param user_id
	 * @param token
	 * @param cart_mobile_ids
	 *            ：手机端用户购物车标志，用户未登录手机端时记录购物车与该手机用户的标识
	 * @param id
	 * @param count
	 * @param price
	 * @param gsp
	 * @param buy_type
	 */
	@RequestMapping("/mobile/add_goods_cart.htm")
	public void add_goods_cart(HttpServletRequest request,
			HttpServletResponse response, String user_id, String token,
			String cart_mobile_ids, String goods_id, String count,
			String price, String gsp) {
		List<GoodsCart> carts_list = new ArrayList<GoodsCart>();// 用户整体购物车
		List<GoodsCart> carts_mobile = new ArrayList<GoodsCart>();// 未提交的用户mobile购物车
		List<GoodsCart> carts_user = new ArrayList<GoodsCart>();// 未提交的用户user购物车
		Map cart_map = new HashMap();
		Set mark_ids = new TreeSet();
		Map json_map = new HashMap();
		String cart_mobile_id = null;
		int code = 100;// 100成功，-100用户信息错误，
		User user = null;
		if (user_id != null && !user_id.equals("")) {
			user = this.userService.getObjById(CommUtil.null2Long(user_id));
			if (!user.getApp_login_token().equals(token.toLowerCase())) {
				user = null;
				code = -100;
			}
		}
		if (code == 100) {

			if (cart_mobile_ids.equals("")) {
				mark_ids.add("0");
			} else {
				String mobile_ids[] = cart_mobile_ids.split(",");
				for (String mobile_id : mobile_ids) {
					if (!mobile_id.equals("")) {
						mark_ids.add(mobile_id);
					}
				}
			}

			if (user != null) {
				cart_map.clear();
				cart_map.put("mark_ids", mark_ids);
				cart_map.put("cart_status", 0);
				carts_mobile = this.goodsCartService
						.query("select obj from GoodsCart obj where obj.cart_mobile_id in (:mark_ids) and obj.cart_status=:cart_status ",
								cart_map, -1, -1);
				// 如果用户拥有自己的店铺，删除carts_mobile购物车中自己店铺中的商品信息
				if (user.getStore() != null) {
					for (GoodsCart gc : carts_mobile) {
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
				cart_map.put("mark_ids", mark_ids);
				cart_map.put("cart_status", 0);
				carts_mobile = this.goodsCartService
						.query("select obj from GoodsCart obj where obj.cart_mobile_id in (:mark_ids) and obj.cart_status=:cart_status ",
								cart_map, -1, -1);
			}
			// 将mobile购物车与用户user购物车合并，去重
			if (user != null) {
				for (GoodsCart ugc : carts_user) {
					carts_list.add(ugc);
				}
				for (GoodsCart mobile : carts_mobile) {
					boolean add = true;
					for (GoodsCart gc2 : carts_user) {
						if (mobile.getGoods().getId()
								.equals(gc2.getGoods().getId())) {
							if (mobile.getSpec_info()
									.equals(gc2.getSpec_info())) {
								add = false;
								this.goodsCartService.delete(mobile.getId());
							}
						}
					}
					if (add) {// 将carts_mobile转变为user_cart
						mobile.setCart_mobile_id(null);
						mobile.setUser(user);
						this.goodsCartService.update(mobile);
						carts_list.add(mobile);
					}
				}
			} else {
				for (GoodsCart mobile : carts_mobile) {
					carts_list.add(mobile);
				}
			}
			// 新添加购物车,排除没有重复商品后添加到carts_list中
			Goods goods = this.goodsService.getObjById(CommUtil
					.null2Long(goods_id));
			GoodsCart obj = new GoodsCart();
			obj.setAddTime(new Date());
			String[] gsp_ids = CommUtil.null2String(gsp).split(",");
			Arrays.sort(gsp_ids);
			boolean add = true;
			for (GoodsCart gc : carts_list) {
				if (gsp_ids != null && gsp_ids.length > 0
						&& gc.getGsps() != null && gc.getGsps().size() > 0) {
					String[] gsp_ids1 = new String[gc.getGsps().size()];
					for (int i = 0; i < gc.getGsps().size(); i++) {
						gsp_ids1[i] = gc.getGsps().get(i) != null ? gc
								.getGsps().get(i).getId().toString() : "";
					}
					Arrays.sort(gsp_ids1);
					if (gc.getGoods().getId().toString().equals(goods_id)
							&& Arrays.equals(gsp_ids, gsp_ids1)) {
						add = false;
					}
				} else {
					if (gc.getGoods().getId().toString().equals(goods_id)) {
						add = false;
					}
				}
			}

			if (add) {// 排除购物车中没有重复商品后添加该商品到购物车
				obj.setAddTime(new Date());
				obj.setCount(CommUtil.null2Int(count));
				obj.setPrice(BigDecimal.valueOf(CommUtil.null2Double(price)));
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
					cart_mobile_id = CommUtil.randomString(12) + "_mobile_"
							+ CommUtil.formatLongDate(new Date());
					obj.setCart_mobile_id(cart_mobile_id);// 设置手机端未登录用户会话Id
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
										.getGoods_current_price())
								* gc.getCount();
					}
					if (CommUtil.null2String(gc.getCart_type())
							.equals("combin")) { // 如果是组合销售购买，则设置组合价格
						cart_total_price = cart_total_price
								+ CommUtil.null2Double(gc.getGoods()
										.getCombin_price()) * gc.getCount();
					}
				}
			}
		}
		json_map.put("code", code);
		if (cart_mobile_id != null) {
			json_map.put("cart_mobile_id", cart_mobile_id);
		}
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
	 * 手机端删除购物车
	 * 
	 * @param request
	 * @param response
	 * @param cart_id
	 * @param user_id
	 * @param token
	 */
	@RequestMapping("/mobile/remove_goods_cart.htm")
	public void remove_goods_cart(HttpServletRequest request,
			HttpServletResponse response, String cart_ids, String user_id,
			String token, String cart_mobile_ids) {
		Double total_price = 0.00;
		String temp_mobile_id = "";
		String code = "100";// 100表示删除成功，200表示删除失败
		List<GoodsCart> carts = new ArrayList<GoodsCart>();
		if (cart_ids != null && !cart_ids.equals("")) {
			String[] ids = cart_ids.split(",");
			for (String id : ids) {
				if (!id.equals("")) {
					GoodsCart gc = this.goodsCartService.getObjById(CommUtil
							.null2Long(id));
					temp_mobile_id = gc.getCart_mobile_id() + ","
							+ temp_mobile_id;
					gc.getGsps().clear();
					this.goodsCartService.delete(CommUtil.null2Long(id));
				}
			}
		}
		total_price = this.getcartsPrice(carts);
		Map map = new HashMap();
		map.put("total_price", BigDecimal.valueOf(total_price));
		map.put("code", code);
		map.put("count", carts.size());
		map.put("dele_cart_mobile_ids", temp_mobile_id);
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
	 * 手机端购物车计算选中购物车的总价钱
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/mobile/compute_cart.htm")
	public void compute_cart(HttpServletRequest request,
			HttpServletResponse response, String cart_ids) {
		Map json_map = new HashMap();
		List<GoodsCart> gcs = new ArrayList<GoodsCart>();
		if (cart_ids != null && !cart_ids.equals("")) {
			String ids[] = cart_ids.split(",");
			for (String id : ids) {
				GoodsCart gc = this.goodsCartService.getObjById(CommUtil
						.null2Long(id));
				if (gc != null) {
					gcs.add(gc);
				}
			}
		}
		double select_cart_price = this.getcartsPrice(gcs);
		json_map.put("select_cart_price", select_cart_price);
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
	 * 购物车商品数量调整
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param store_id
	 */
	@RequestMapping("/mobile/cart_count_adjust.htm")
	public void cart_count_adjust(HttpServletRequest request,
			HttpServletResponse response, String cart_id, String count,
			String user_id, String token, String cart_mobile_ids) {
		List<GoodsCart> carts = this.cart_calc(user_id, token, cart_mobile_ids);
		String code = "100";// 100表示修改成功，200表示库存不足,300表示团购库存不足
		double gc_price = 0.00;// 单位GoodsCart总价钱
		double total_price = 0.00;// 购物车总价钱
		String cart_type = "";// 判断是否为组合销售
		int max_inventory = 0;// 超出商品库存时的最大库存
		Goods goods = null;
		int temp_count = CommUtil.null2Int(count);
		Map map = new HashMap();
		if (cart_id != null && !cart_id.equals("")) {
			GoodsCart gc = this.goodsCartService.getObjById(CommUtil
					.null2Long(cart_id));
			if (gc.getId().toString().equals(cart_id)) {
				cart_type = CommUtil.null2String(gc.getCart_type());
				goods = gc.getGoods();
				if (cart_type.equals("")) {// 普通商品的处理
					if (goods.getGroup_buy() == 2) {
						GroupGoods gg = new GroupGoods();
						for (GroupGoods gg1 : goods.getGroup_goods_list()) {
							if (gg1.getGg_goods().getId().equals(goods.getId())) {
								gg = gg1;
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
							max_inventory = gg.getGg_count();
							temp_count = max_inventory;
							gc.setCount(CommUtil.null2Int(temp_count));
							this.goodsCartService.update(gc);
						}
					} else {
						if (goods.getGoods_inventory() >= CommUtil
								.null2Int(count)) {
							if (gc.getId().toString().equals(cart_id)) {
								gc.setCount(CommUtil.null2Int(count));
								this.goodsCartService.update(gc);
								gc_price = CommUtil.mul(gc.getPrice(), count);
							}
						} else {
							max_inventory = goods.getGoods_inventory();
							code = "200";
							temp_count = max_inventory;
							gc.setCount(temp_count);
							this.goodsCartService.update(gc);
							gc_price = CommUtil.mul(gc.getPrice(), count);
						}
					}
				}
			}
		}
		if (max_inventory != 0) {
			map.put("max_inventory", max_inventory);
		}
		total_price = this.getcartsPrice(carts);
		map.put("gc_price", gc_price);
		map.put("total_price", total_price);
		map.put("code", code);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		System.out.println(Json.toJson(map, JsonFormat.compact()));
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(map, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 手机端轻松购，直接提交商品到订单页
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/mobile/goods_cart0.htm")
	public void goods_cart0(HttpServletRequest request,
			HttpServletResponse response, String user_id, String token,
			String goods_id, String count, String price, String gsp) {
		List<GoodsCart> carts_list = new ArrayList<GoodsCart>();// 用户整体购物车
		List<GoodsCart> carts_mobile = new ArrayList<GoodsCart>();// 未提交的用户mobile购物车
		List<GoodsCart> carts_user = new ArrayList<GoodsCart>();// 未提交的用户user购物车
		Map cart_map = new HashMap();
		Set mark_ids = new TreeSet();
		Map json_map = new HashMap();
		String cart_id = null;
		int code = 100;// 100成功，-100用户信息错误，
		User user = null;
		if (user_id != null && !user_id.equals("")) {
			user = this.userService.getObjById(CommUtil.null2Long(user_id));
			if (user != null
					&& !user.getApp_login_token().equals(token.toLowerCase())) {
				user = null;
				code = -100;
			}
		}
		if (code == 100) {
			// 新添加购物车,排除没有重复商品后添加到carts_list中
			Goods goods = this.goodsService.getObjById(CommUtil
					.null2Long(goods_id));
			GoodsCart obj = new GoodsCart();
			obj.setAddTime(new Date());
			String[] gsp_ids = CommUtil.null2String(gsp).split(",");
			Arrays.sort(gsp_ids);
			boolean add = true;
			for (GoodsCart gc : carts_list) {
				if (gsp_ids != null && gsp_ids.length > 0
						&& gc.getGsps() != null && gc.getGsps().size() > 0) {
					String[] gsp_ids1 = new String[gc.getGsps().size()];
					for (int i = 0; i < gc.getGsps().size(); i++) {
						gsp_ids1[i] = gc.getGsps().get(i) != null ? gc
								.getGsps().get(i).getId().toString() : "";
					}
					Arrays.sort(gsp_ids1);
					if (gc.getGoods().getId().toString().equals(goods_id)
							&& Arrays.equals(gsp_ids, gsp_ids1)) {
						add = false;
					}
				} else {
					if (gc.getGoods().getId().toString().equals(goods_id)) {
						add = false;
					}
				}
			}
			if (add) {// 排除购物车中没有重复商品后添加该商品到购物车
				obj.setAddTime(new Date());
				obj.setCount(CommUtil.null2Int(count));
				obj.setPrice(BigDecimal.valueOf(CommUtil.null2Double(price)));
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
				obj.setUser(user);
				obj.setSpec_info(spec_info);
				this.goodsCartService.save(obj);
				cart_id = CommUtil.null2String(obj.getId());
				double cart_total_price = 0;
				for (GoodsCart gc : carts_list) {
					if (CommUtil.null2String(gc.getCart_type()).equals("")) {
						cart_total_price = cart_total_price
								+ CommUtil.null2Double(gc.getGoods()
										.getGoods_current_price())
								* gc.getCount();
					}
					if (CommUtil.null2String(gc.getCart_type())
							.equals("combin")) { // 如果是组合销售购买，则设置组合价格
						cart_total_price = cart_total_price
								+ CommUtil.null2Double(gc.getGoods()
										.getCombin_price()) * gc.getCount();
					}
				}
			}
		}
		if (code == 100 && cart_id != null) {// 添加购物车完成，
			List<GoodsCart> carts = this.getGoodscartByids(cart_id);
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
			String store_ids = "";
			for (Object sl : store_list) {
				if (store_ids.indexOf(CommUtil.null2String(sl)) <= 0) {
					store_ids = CommUtil.null2String(sl) + "," + store_ids;
				}
			}
			double order_goods_price = this.getcartsPrice(carts);
			json_map.put("order_goods_price", order_goods_price);// 订单中商品总体价格
			json_map.put("store_ids", store_ids);
		}
		json_map.put("cart_ids", cart_id);
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
	 * 手机端购物车列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/mobile/goods_cart1.htm")
	public void goods_cart1(HttpServletRequest request,
			HttpServletResponse response, String user_id, String token,
			String cart_mobile_ids) {
		Map json_map = new HashMap();
		List cart_list = new ArrayList();
		List<GoodsCart> carts = this.cart_calc(user_id, token, cart_mobile_ids);
		for (GoodsCart gc : carts) {
			Map map = new HashMap();
			map.put("goods_name", gc.getGoods().getGoods_name());
			String status = "goods";
			if (gc.getGoods().getGroup_buy() == 2) {
				status = "group";
			}
			if (gc.getGoods().getActivity_status() == 2) {
				status = "activity";
			}
			map.put("goods_status", status);// 商品状态
			map.put("cart_id", gc.getId());// 商品对应购物车id
			map.put("goods_id", gc.getGoods().getId());// 商品id
			map.put("goods_price", gc.getPrice());// 商品价格
			map.put("goods_count", gc.getCount());// 商品数量
			map.put("goods_spec", gc.getSpec_info());// 商品规格
			String goods_main_photo = CommUtil.getURL(request)// 系统默认商品图片
					+ "/"
					+ this.configService.getSysConfig().getGoodsImage()
							.getPath()
					+ "/"
					+ this.configService.getSysConfig().getGoodsImage()
							.getName();
			if (gc.getGoods().getGoods_main_photo() != null) {// 商品主图片
				goods_main_photo = CommUtil.getURL(request) + "/"
						+ gc.getGoods().getGoods_main_photo().getPath() + "/"
						+ gc.getGoods().getGoods_main_photo().getName()
						+ "_small."
						+ gc.getGoods().getGoods_main_photo().getExt();
			}
			map.put("goods_main_photo", goods_main_photo);
			double cart_price = this.getcartsPrice(carts);
			map.put("cart_price", cart_price);// 购物车总商品价格
			cart_list.add(map);
		}
		json_map.put("cart_list", cart_list);
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
	 * 手机端提交购物车进入订单页面
	 * 
	 * @param request
	 * @param response
	 * @param cart_ids
	 *            :提交的购物车id
	 * @return
	 */
	@RequestMapping("/mobile/goods_cart2.htm")
	public void goods_cart2(HttpServletRequest request,
			HttpServletResponse response, String user_id, String token,
			String cart_ids) {
		Map json_map = new HashMap();
		boolean verify = CommUtil.null2Boolean(request.getHeader("verify"));
		if (verify && user_id != null && !user_id.equals("") && token != null
				&& !token.equals("")) {
			User user = this.userService
					.getObjById(CommUtil.null2Long(user_id));
			if (user != null) {
				if (user.getApp_login_token().equals(token.toLowerCase())) {
					List<GoodsCart> carts = this.getGoodscartByids(cart_ids);
					List<Object> store_list = new ArrayList<Object>();
					for (GoodsCart gc : carts) {
						if (gc.getGoods().getGoods_type() == 1) {
							store_list.add(gc.getGoods().getGoods_store()
									.getId());
						} else {
							store_list.add("self");
						}
					}
					HashSet hs = new HashSet(store_list);
					store_list.removeAll(store_list);
					store_list.addAll(hs);
					String store_ids = "";
					for (Object sl : store_list) {
						if (store_ids.indexOf(CommUtil.null2String(sl)) <= 0) {
							store_ids = CommUtil.null2String(sl) + ","
									+ store_ids;
						}
					}
					double order_goods_price = this.getcartsPrice(carts);
					json_map.put("order_goods_price", order_goods_price);// 订单中商品总体价格
					json_map.put("store_ids", store_ids);
				}
			}
		}
		json_map.put("cart_ids", cart_ids);
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
	 * 手机端提交订单页商品清单
	 * 
	 * @param request
	 * @param response
	 * @param cart_ids
	 *            :提交的购物车id
	 * @return
	 */
	@RequestMapping("/mobile/goods_cart2_goodsInfo.htm")
	public void goods_cart2_goodsInfo(HttpServletRequest request,
			HttpServletResponse response, String cart_ids) {
		Map json_map = new HashMap();
		List<GoodsCart> carts = this.getGoodscartByids(cart_ids);
		List goods_list = new ArrayList();
		String url = CommUtil.getURL(request);
		for (GoodsCart gc : carts) {
			Map map = new HashMap();
			map.put("goods_id", gc.getGoods().getId());// 商品id
			map.put("goods_id", gc.getGoods().getGoods_name());// 商品名称
			map.put("goods_price", gc.getPrice());// 商品价格
			map.put("goods_count", gc.getCount());// 商品数量
			map.put("goods_spec", gc.getSpec_info());// 商品规格
			String goods_main_photo = url// 系统默认商品图片
					+ "/"
					+ this.configService.getSysConfig().getGoodsImage()
							.getPath()
					+ "/"
					+ this.configService.getSysConfig().getGoodsImage()
							.getName();
			if (gc.getGoods().getGoods_main_photo() != null) {// 商品主图片
				goods_main_photo = url + "/"
						+ gc.getGoods().getGoods_main_photo().getPath() + "/"
						+ gc.getGoods().getGoods_main_photo().getName()
						+ "_small."
						+ gc.getGoods().getGoods_main_photo().getExt();
			}
			map.put("goods_main_photo", goods_main_photo);
			goods_list.add(map);
		}
		json_map.put("goods_list", goods_list);
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
	 * 确认订单时查询支付方式，分为在线支付、预存款支付以及货到付款，选择在线支付时确认订单后进入选择在线支付方式页面，
	 * 选择货到付款后确认订单后直接由商家发货。
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping("/mobile/goods_cart2_payment.htm")
	public void goods_cart2_payment(HttpServletRequest request,
			HttpServletResponse response) {
		Map json_map = new HashMap();
		Map params = new HashMap();
		List payment_list = new ArrayList();
		Set marks = new TreeSet();
		// 在线支付
		marks.add("alipay_app");
		params.put("marks", marks);
		params.put("install", true);
		List<Payment> online = this.paymentService
				.query("select obj from Payment obj where obj.mark in(:marks) and obj.install=:install",
						params, -1, -1);
		// 预存款支付
		marks.clear();
		marks.add("balance");
		params.put("marks", marks);
		params.put("install", true);
		List<Payment> balance = this.paymentService
				.query("select obj from Payment obj where obj.mark in(:marks) and obj.install=:install",
						params, -1, -1);
		// 货到付款
		marks.clear();
		marks.add("payafter");
		params.put("marks", marks);
		params.put("install", true);
		List<Payment> payafter = this.paymentService
				.query("select obj from Payment obj where obj.mark in(:marks) and obj.install=:install",
						params, -1, -1);
		if (online.size() == 1) {
			Map map = new HashMap();
			map.put("pay_mark", "online");
			map.put("pay_name", "支付宝");
			payment_list.add(map);
		}
		if (balance.size() == 1) {
			Map map = new HashMap();
			map.put("pay_mark", "balance");
			map.put("pay_name", "预存款支付");
			payment_list.add(map);
		}
		if (payafter.size() == 1) {
			Map map = new HashMap();
			map.put("pay_mark", "payafter");
			map.put("pay_name", "货到付款");
			payment_list.add(map);
		}
		json_map.put("payment_list", payment_list);
		String json = Json.toJson(json_map, JsonFormat.compact());
		System.out.println(json);
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
	 * 根据购物车获得相应的物流运费模板
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping("/mobile/goods_cart2_cartsTrans.htm")
	public void goods_cart2_cartsTrans(HttpServletRequest request,
			HttpServletResponse response, String cart_ids, String addr_id,
			String store_ids) {
		Map json_map = new HashMap();
		List<GoodsCart> carts = this.getGoodscartByids(cart_ids);
		Address addr = this.addressService.getObjById(CommUtil
				.null2Long(addr_id));
		if (store_ids != null && !store_ids.equals("")) {
			String ids[] = store_ids.split(",");
			List trans_list = new ArrayList();
			for (String id : ids) {
				int goods_count = 0;
				if (!id.equals("")) {
					List<GoodsCart> temp_gc_list = new ArrayList();
					for (GoodsCart gc : carts) {
						if (id.equals("self")
								&& gc.getGoods().getGoods_type() == 0) {// 平台自营
							temp_gc_list.add(gc);
						} else {
							if (gc.getGoods().getGoods_type() == 1
									&& gc.getGoods().getGoods_store().getId()
											.toString().equals(id)) {
								temp_gc_list.add(gc);
							}
						}
					}
					goods_count = temp_gc_list.size();
					List goods_list = new ArrayList();
					String url = CommUtil.getURL(request);
					for (GoodsCart gc : temp_gc_list) {
						String goods_main_photo = url// 系统默认商品图片
								+ "/"
								+ this.configService.getSysConfig()
										.getGoodsImage().getPath()
								+ "/"
								+ this.configService.getSysConfig()
										.getGoodsImage().getName();
						if (gc.getGoods().getGoods_main_photo() != null) {// 商品主图片
							goods_main_photo = url
									+ "/"
									+ gc.getGoods().getGoods_main_photo()
											.getPath()
									+ "/"
									+ gc.getGoods().getGoods_main_photo()
											.getName()
									+ "_small."
									+ gc.getGoods().getGoods_main_photo()
											.getExt();
						}
						goods_list.add(goods_main_photo);
					}
					List<SysMap> sms = this.transportTools.query_cart_trans(
							temp_gc_list, addr.getArea().getId().toString());
					List transInfo_list = new ArrayList();
					Map transInfo_map = new HashMap();
					for (SysMap s : sms) {
						Map map = new HashMap();
						map.put("key", s.getKey());
						map.put("value", s.getValue());
						transInfo_list.add(map);
					}
					transInfo_map.put("transInfo_list", transInfo_list);
					String store_name = "平台运营商";
					if (!id.equals("self")) {
						Store store = this.storeService.getObjById(CommUtil
								.null2Long(id));
						store_name = store.getStore_name();
					}
					transInfo_map.put("goods_list", goods_list);
					transInfo_map.put("goods_count", goods_count);
					transInfo_map.put("store_name", store_name);
					transInfo_map.put("store_id", id);
					trans_list.add(transInfo_map);
				}
			}
			json_map.put("trans_list", trans_list);
		}
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
	 * 根据购物车获得相应的物流运费模板
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping("/mobile/goods_cart2_coupon.htm")
	public void goods_cart2_coupon(HttpServletRequest request,
			HttpServletResponse response, String user_id, String token,
			String store_ids, String order_goods_price) {
		Map json_map = new HashMap();
		boolean verify = CommUtil.null2Boolean(request.getHeader("verify"));
		List map_list = new ArrayList();
		if (verify && user_id != null && !user_id.equals("") && token != null
				&& !token.equals("")) {
			User user = this.userService
					.getObjById(CommUtil.null2Long(user_id));
			if (user != null) {
				if (user.getApp_login_token().equals(token.toLowerCase())) {
					List<CouponInfo> Coupon_list = new ArrayList<CouponInfo>();
					List json_list = new ArrayList();
					String ids[] = store_ids.split(",");
					for (String id : ids) {
						if (!id.equals("")) {
							List<CouponInfo> list_temp = this.cartTools
									.mobile_query_coupon(id, order_goods_price,
											user_id);
							Coupon_list.addAll(list_temp);
						}
					}
					for (CouponInfo info : Coupon_list) {
						Map map = new HashMap();
						map.put("coupon_id", info.getId());
						map.put("coupon_name", info.getCoupon()
								.getCoupon_name());
						map.put("coupon_amount", info.getCoupon()
								.getCoupon_amount());
						map.put("coupon_info", "优惠"
								+ info.getCoupon().getCoupon_amount() + "元");
						json_list.add(map);
					}
					json_map.put("coupon_list", json_list);
				} else {
					verify = false;
				}
			} else {
				verify = false;
			}
		} else {
			verify = false;
		}
		json_map.put("verify", verify);
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
	 * 手机端购物车提交订单请求,提交订单如果有多个商家商品，发票信息全部一致、提交订单之前将本订单中所有可用优惠券查出来，提交订单时只能选择一张优惠券，
	 * 
	 * @param request
	 * @param response
	 * @param user_id
	 * @param token
	 * @param store_id
	 *            ：店铺id，以逗号间隔
	 * @param addr_id
	 * @param cart_ids
	 * @param order_type
	 *            ：为订单类型标识，android端生成的订单为android，ios端生成订单为ios，
	 * @param payType
	 *            ：支付方式，分为online（在线支付）,balance（预存款支付）,payafter（货到付款）
	 * @param invoiceType
	 *            ：发票类型
	 * @param invoice
	 * @param coupon_id
	 *            ：优惠券id，手机端每次下单只能选择一张优惠券使用
	 * @throws Exception
	 */
	@RequestMapping("/mobile/goods_cart3.htm")
	public void goods_cart3(HttpServletRequest request,
			HttpServletResponse response, String user_id, String token,
			String store_id, String addr_id, String cart_ids,
			String order_type, String payType, String invoiceType,
			String invoice, String coupon_id) throws Exception {
		Map json_map1 = new HashMap();
		int code = 100;
		boolean verify = CommUtil.null2Boolean(request.getHeader("verify"));
		List map_list1 = new ArrayList();
		Long main_order_id = null;
		String order_num = null;// 订单编号
		if (verify && user_id != null && !user_id.equals("") && token != null
				&& !token.equals("")) {
			User user = this.userService
					.getObjById(CommUtil.null2Long(user_id));
			if (user != null) {
				if (user.getApp_login_token().equals(token.toLowerCase())) {
					List<GoodsCart> carts = this.getGoodscartByids(cart_ids);
					Address addr = this.addressService.getObjById(CommUtil
							.null2Long(addr_id));
					if (carts.size() > 0 && addr != null) {
						// 验证购物车中是否存在库存为0的商品
						boolean inventory_very = true;
						for (GoodsCart gc : carts) {
							if (gc.getCount() == 0) {
								inventory_very = false;
							}
							if (gc.getGoods().getGoods_inventory() == 0
									|| gc.getGoods().getGoods_inventory() < gc
											.getCount()) {
								inventory_very = false;
							}
						}
						if (inventory_very) {
							double all_of_price = 0;
							String store_ids[] = store_id.split(",");
							List<Map> child_order_maps = new ArrayList<Map>();
							String order_suffix = CommUtil.formatTime(
									"yyyyMMddHHmmss", new Date());
							for (int i = 0; i < store_ids.length; i++) {// 根据店铺id，保存多个子订单
								String sid = store_ids[i];
								Store store = null;
								List<GoodsCart> gc_list = new ArrayList<GoodsCart>();
								List<Map> map_list = new ArrayList<Map>();
								if (sid != "self" && !sid.equals("self")) {
									store = this.storeService
											.getObjById(CommUtil.null2Long(sid));
								}
								for (GoodsCart gc : carts) {
									String goods_gsp_ids = "/";
									for (GoodsSpecProperty gsp : gc.getGsps()) {
										goods_gsp_ids = gsp.getId() + "/"
												+ goods_gsp_ids;
									}
									if (gc.getGoods().getGoods_type() == 1) {
										if (gc.getGoods()
												.getGoods_store()
												.getId()
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
											json_map.put("goods_id", gc
													.getGoods().getId());
											json_map.put("goods_name", gc
													.getGoods().getGoods_name());
											json_map.put(
													"goods_choice_type",
													gc.getGoods()
															.getGoods_choice_type());
											json_map.put("goods_type",
													goods_type);
											json_map.put("goods_count",
													gc.getCount());
											json_map.put("goods_price",
													gc.getPrice());// 商品单价
											json_map.put("goods_all_price",
													CommUtil.mul(gc.getPrice(),
															gc.getCount()));// 商品总价
											json_map.put(
													"goods_commission_price",
													this.getGoodscartCommission(gc));// 设置该商品总佣金
											json_map.put(
													"goods_commission_rate",
													gc.getGoods()
															.getGc()
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
											if (this.configService
													.getSysConfig()
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
									} else {
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
											json_map.put("goods_id", gc
													.getGoods().getId());
											json_map.put("goods_name", gc
													.getGoods().getGoods_name());
											json_map.put(
													"goods_choice_type",
													gc.getGoods()
															.getGoods_choice_type());
											json_map.put("goods_type",
													goods_type);
											json_map.put("goods_count",
													gc.getCount());
											json_map.put("goods_price",
													gc.getPrice());// 商品单价
											json_map.put("goods_all_price",
													CommUtil.mul(gc.getPrice(),
															gc.getCount()));// 商品总价
											json_map.put("goods_gsp_val",
													gc.getSpec_info());
											json_map.put("goods_gsp_ids",
													goods_gsp_ids);
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
											json_map.put("goods_domainPath",
													CommUtil.getURL(request)
															+ "/goods_"
															+ gc.getGoods()
																	.getId()
															+ ".htm");// 商品二级域名路径
											map_list.add(json_map);
											gc_list.add(gc);
										}
									}
								}
								double goods_amount = this
										.getcartsPrice(gc_list);// 订单中商品价格
								List<SysMap> sms = this.transportTools
										.query_cart_trans(gc_list, CommUtil
												.null2String(addr.getArea()
														.getId()));
								String transport = request
										.getParameter("trans_" + sid);
								double ship_price = CommUtil
										.null2Double(request
												.getParameter("ship_price_"
														+ sid));
								double totalPrice = CommUtil.add(goods_amount,
										ship_price);// 订单总价
								all_of_price = all_of_price + totalPrice;// 总订单价格
								double commission_amount = this
										.getOrderCommission(gc_list);// 订单总体佣金
								OrderForm of = new OrderForm();
								of.setAddTime(new Date());
								String order_store_id = "0";
								of.setOrder_type(order_type);// 设置订单类型，android端为"android",苹果端为"ios"
								if (sid != "self" && !sid.equals("self")) {
									order_store_id = CommUtil.null2String(store
											.getId());
								}
								of.setOrder_id(user.getId() + order_suffix
										+ order_store_id);
								// 设置收货地址信息
								of.setReceiver_Name(addr.getTrueName());
								of.setReceiver_area(Area.getAreaInfo(addr.getArea(),""));
//								of.setReceiver_area(addr.getArea().getParent()
//										.getParent().getAreaName()
//										+ addr.getArea().getParent()
//												.getAreaName()
//										+ addr.getArea().getAreaName());
								of.setReceiver_area_info(addr.getArea_info());
								of.setReceiver_mobile(addr.getMobile());
								of.setReceiver_telephone(addr.getTelephone());
								of.setReceiver_zip(addr.getZip());
								of.setTransport(transport);
								of.setOrder_status(10);
								User buyer = this.userService.getObjById(user
										.getId());
								of.setUser_id(buyer.getId().toString());
								of.setUser_name(buyer.getUserName());
								of.setGoods_info(Json.toJson(map_list,
										JsonFormat.compact()));// 设置商品信息json数据
								of.setInvoiceType(CommUtil
										.null2Int(invoiceType));
								of.setInvoice(invoice);
								of.setShip_price(BigDecimal.valueOf(ship_price));
								of.setGoods_amount(BigDecimal
										.valueOf(goods_amount));
								of.setTotalPrice(BigDecimal.valueOf(totalPrice));
								of.setPayType(payType);// 设置支付类型
								of.setPayment(this.get_payment(payType));
								if (sid.equals("self") || sid == "self") {
									of.setOrder_form(1);// 平台自营商品订单
								} else {
									of.setCommission_amount(BigDecimal
											.valueOf(commission_amount));// 该订单总体佣金费用
									of.setOrder_form(0);// 商家商品订单
									of.setStore_id(store.getId().toString());
									of.setStore_name(store.getStore_name());
								}
								if (coupon_id != null && !coupon_id.equals("")) {
									CouponInfo ci = this.couponInfoService
											.getObjById(CommUtil
													.null2Long(coupon_id));
									boolean coupon_verify = false;
									if (of.getOrder_form() == 1) {// 平台自营订单
										if (ci.getCoupon().getCoupon_type() == 0) {
											coupon_verify = true;
										} else {
											coupon_verify = false;
										}
									}
									if (of.getOrder_form() == 0) {// 商家订单
										if (ci.getCoupon().getCoupon_type() == 0) {
											coupon_verify = false;
										} else {
											if (ci.getCoupon().getStore()
													.getId().toString()
													.equals(of.getStore_id())) {
												coupon_verify = true;
											}
										}
									}
									if (ci != null && coupon_verify) {
										if (user.getId().equals(
												ci.getUser().getId())) {
											ci.setStatus(1);
											this.couponInfoService.update(ci);
											Map coupon_map = new HashMap();
											coupon_map.put("couponinfo_id",
													ci.getId());
											coupon_map.put("couponinfo_sn",
													ci.getCoupon_sn());
											coupon_map.put("coupon_amount", ci
													.getCoupon()
													.getCoupon_amount());
											double rate = CommUtil.div(ci
													.getCoupon()
													.getCoupon_amount(),
													goods_amount);
											coupon_map.put("coupon_goods_rate",
													rate);
											of.setCoupon_info(Json.toJson(
													coupon_map,
													JsonFormat.compact()));
											of.setTotalPrice(BigDecimal.valueOf(CommUtil.subtract(
													of.getTotalPrice(), ci
															.getCoupon()
															.getCoupon_amount())));
										}
									}
								}

								if (i == store_ids.length - 1) {
									of.setOrder_main(1);// 同时购买多个商家商品，最后一个订单为主订单，其他的作为子订单，以json信息保存，用在买家中心统一显示大订单，统一付款
									if (child_order_maps.size() > 0) {
										of.setChild_order_detail(Json.toJson(
												child_order_maps,
												JsonFormat.compact()));
									}
								}
								boolean flag = this.orderFormService.save(of);
								if (of.getOrder_main() == 1) {
									main_order_id = of.getId();// 主订单id
									order_num = of.getOrder_id();
								}
								if (flag) {
									// 如果是多个店铺的订单同时提交，则记录子订单信息到主订单中，用在买家中心统一显示及统一付款
									if (store_ids.length > 1) {
										Map order_map = new HashMap();
										order_map.put("order_id", of.getId());
										order_map.put("order_goods_info",
												of.getGoods_info());
										child_order_maps.add(order_map);
									}
									for (GoodsCart gc : gc_list) {// 删除已经提交订单的购物车信息
										this.goodsCartService
												.delete(gc.getId());
									}
									OrderFormLog ofl = new OrderFormLog();
									ofl.setAddTime(new Date());
									ofl.setOf(of);
									ofl.setLog_info("提交订单");
									ofl.setLog_user(user);
									this.orderFormLogService.save(ofl);
									if (this.configService.getSysConfig()
											.isEmailEnable()) {// 如果系统启动了邮件功能，则发送邮件提示
										this.send_email(request, of,
												buyer.getEmail(),
												"email_tobuyer_order_submit_ok_notify");
									}
									if (this.configService.getSysConfig()
											.isSmsEnbale()) {// 如果系统启用了短信功能，则发送短信提示
										this.send_sms(request, of,
												buyer.getMobile(),
												"sms_tobuyer_order_submit_ok_notify");
									}
								}
							}
						} else {
							verify = false;
							code = -300;
						}
					}
				} else {
					verify = false;
				}
			} else {
				verify = false;
			}
		} else {
			verify = false;
		}
		json_map1.put("code", code);
		json_map1.put("order_id", main_order_id);
		json_map1.put("order_num", order_num);
		json_map1.put("payType", payType);
		json_map1.put("verify", verify);
		String json = Json.toJson(json_map1, JsonFormat.compact());
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
	 * 订单货到付款支付
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping("/mobile/pay_payafter.htm")
	public void pay_payafter(HttpServletRequest request,
			HttpServletResponse response, String order_id, String pay_msg,
			String user_id, String token) throws Exception {
		Map json_map = new HashMap();
		int code = 100;// 100成功，-100用户信息错误,-200订单信息错误，-300订单支付方式信息错误,-400系统未开启该支付功能，订单不可支付，
		boolean user_verify = false;// 用户信息验证
		boolean order_verify = false;// 订单信息验证
		boolean pay_verify = false;// 订单支付方式信息验证
		OrderForm order = null;
		User user = null;
		boolean verify = CommUtil.null2Boolean(request.getHeader("verify"));
		List map_list1 = new ArrayList();
		Long main_order_id = null;
		if (verify && user_id != null && !user_id.equals("") && token != null
				&& !token.equals("")) {
			user = this.userService.getObjById(CommUtil.null2Long(user_id));
			if (user != null) {
				if (user.getApp_login_token().equals(token.toLowerCase())) {
					user_verify = true;
				} else {
					code = -100;
				}
			} else {
				code = -100;
			}
		} else {
			code = -100;
		}
		if (user_verify) {// 用户信息验证通过
			order = this.orderFormService.getObjById(CommUtil
					.null2Long(order_id));
			if (order != null && order.getUser_id().equals(user_id)) {
				order_verify = true;
			} else {
				code = -200;
			}
		}
		if (order_verify) {// 订单信息验证通过
			if (order.getPayType().equals("payafter")) {
				pay_verify = true;
			} else {
				code = -300;
			}
		}
		if (pay_verify) {// 订单支付方式信息验证通过
			order.setPay_msg(pay_msg);
			Map params = new HashMap();
			params.put("mark", "payafter");
			List<Payment> payments = this.paymentService.query(
					"select obj from Payment obj where obj.mark=:mark", params,
					-1, -1);
			if (payments.size() > 0) {
				order.setPayment(payments.get(0));
				order.setPayTime(new Date());
				order.setOrder_status(16);// 订单货到付款
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
							this.send_email(request, child_order, store
									.getUser().getEmail(),
									"email_toseller_payafter_pay_ok_notify");
						}
					}
				}
			} else {
				code = -400;
			}
			// 记录支付日志
			OrderFormLog ofl = new OrderFormLog();
			ofl.setAddTime(new Date());
			ofl.setLog_info("提交货到付款申请");
			ofl.setLog_user(user);
			ofl.setOf(order);
			this.orderFormLogService.save(ofl);
		}
		json_map.put("code", code);
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
	 * 订单预存款支付
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping("/mobile/pay_balance.htm")
	public void pay_balance(HttpServletRequest request,
			HttpServletResponse response, String order_id, String pay_msg,
			String user_id, String token) throws Exception {
		Map json_map = new HashMap();
		int code = 100;// 100成功，-100用户信息错误,-200订单信息错误，-300订单支付方式信息错误,-400预存款余额不足，-500订单重复支付
		boolean user_verify = false;// 用户信息验证
		boolean order_verify = false;// 订单信息验证
		boolean pay_verify = false;// 订单支付方式信息验证
		OrderForm order = null;
		User user = null;
		boolean verify = CommUtil.null2Boolean(request.getHeader("verify"));
		List map_list1 = new ArrayList();
		Long main_order_id = null;
		if (verify && user_id != null && !user_id.equals("") && token != null
				&& !token.equals("")) {
			user = this.userService.getObjById(CommUtil.null2Long(user_id));
			if (user != null) {
				if (user.getApp_login_token().equals(token.toLowerCase())) {
					user_verify = true;
				} else {
					code = -100;
				}
			} else {
				code = -100;
			}
		} else {
			code = -100;
		}
		if (user_verify) {// 用户信息验证通过
			order = this.orderFormService.getObjById(CommUtil
					.null2Long(order_id));
			if (order != null && order.getUser_id().equals(user_id)) {
				order_verify = true;
			} else {
				code = -200;
			}
		}
		if (order_verify) {// 订单信息验证通过
			if (order.getPayType().equals("balance")) {
				pay_verify = true;
			} else {
				code = -300;
			}
		}
		if (pay_verify) {// 订单支付方式信息验证通过
			if (order != null && order.getOrder_status() < 20) {// 订单不为空且订单状态为未付款才可以正常使用预存款付款
				Map params = new HashMap();
				params.put("mark", "balance");
				List<Payment> payments = this.paymentService.query(
						"select obj from Payment obj where obj.mark=:mark",
						params, -1, -1);
				double order_total_price = CommUtil.null2Double(order
						.getTotalPrice());
				if (!CommUtil.null2String(order.getChild_order_detail())
						.equals("") && order.getOrder_cat() != 2) {
					List<Map> maps = this.orderFormTools.queryGoodsInfo(order
							.getChild_order_detail());
					for (Map map : maps) {
						OrderForm child_order = this.orderFormService
								.getObjById(CommUtil.null2Long(map
										.get("order_id")));
						order_total_price = order_total_price
								+ CommUtil.null2Double(child_order
										.getTotalPrice());
					}
				}
				if (CommUtil.null2Double(user.getAvailableBalance()) >= order_total_price) {
					order.setPay_msg(pay_msg);
					order.setOrder_status(20);
					if (payments.size() > 0) {
						order.setPayment(payments.get(0));
						order.setPayTime(new Date());
					}
					boolean ret = this.orderFormService.update(order);
					if (ret) {
						// 预存款付款成功后，执行子订单状态改变及发送提醒信息
						if (order.getOrder_main() == 1
								&& !CommUtil.null2String(
										order.getChild_order_detail()).equals(
										"") && order.getOrder_cat() != 2) {
							List<Map> maps = this.orderFormTools
									.queryGoodsInfo(order
											.getChild_order_detail());
							for (Map child_map : maps) {
								OrderForm child_order = this.orderFormService
										.getObjById(CommUtil
												.null2Long(child_map
														.get("order_id")));
								child_order.setOrder_status(20);
								if (payments.size() > 0) {
									child_order.setPayment(payments.get(0));
									child_order.setPayTime(new Date());
								}
								this.orderFormService.update(child_order);
								// 向加盟商家发送付款成功短信提示，自营商品无需发送短信提示
								Store store = this.storeService
										.getObjById(CommUtil
												.null2Long(child_order
														.getStore_id()));
								if (this.configService.getSysConfig()
										.isEmailEnable()) {
									if (child_order.getOrder_form() == 0) {
										this.send_email(request, child_order,
												store.getUser().getEmail(),
												"email_toseller_balance_pay_ok_notify");
									}
									this.send_email(request, child_order, store
											.getUser().getEmail(),
											"email_tobuyer_balance_pay_ok_notify");
								}
								if (this.configService.getSysConfig()
										.isSmsEnbale()
										&& child_order.getOrder_form() == 0) {
									if (child_order.getOrder_form() == 0) {
										this.send_sms(request, child_order,
												store.getUser().getMobile(),
												"sms_toseller_balance_pay_ok_notify");
									}
									User buyer = this.userService
											.getObjById(CommUtil
													.null2Long(child_order
															.getUser_id()));
									this.send_sms(request, child_order,
											buyer.getMobile(),
											"sms_tobuyer_balance_pay_ok_notify");
								}
							}
						}
						// 如果是团购订单，则需要执行团购订单相关流程及发送团购码
						if (order.getOrder_cat() == 2) {
							Calendar ca = Calendar.getInstance();
							ca.add(ca.DATE, this.configService.getSysConfig()
									.getAuto_order_return());
							SimpleDateFormat bartDateFormat = new SimpleDateFormat(
									"yyyy-MM-dd HH:mm:ss");
							String latertime = bartDateFormat.format(ca
									.getTime());
							order.setReturn_shipTime(CommUtil
									.formatDate(latertime));
							Map map = this.orderFormTools.queryGroupInfo(order
									.getGroup_info());
							int count = CommUtil.null2Int(map
									.get("goods_count").toString());
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
										+ CommUtil.formatTime("yyyyMMddHHmmss"
												+ i, new Date()));
								Calendar ca2 = Calendar.getInstance();
								ca2.add(ca2.DATE, this.configService
										.getSysConfig()
										.getGrouplife_order_return());
								SimpleDateFormat bartDateFormat2 = new SimpleDateFormat(
										"yyyy-MM-dd HH:mm:ss");
								String latertime2 = bartDateFormat2.format(ca2
										.getTime());
								info.setRefund_Time(CommUtil
										.formatDate(latertime2));
								this.groupInfoService.save(info);
								codes = codes + info.getGroup_sn() + " ";
								code_list.add(info.getGroup_sn());
								i++;
							}
							if (order.getOrder_form() == 0) {
								Store store = this.storeService
										.getObjById(CommUtil.null2Long(order
												.getStore_id()));
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
							sc.setPayoff_all_sale(BigDecimal.valueOf(CommUtil
									.add(order.getTotalPrice(),
											sc.getPayoff_all_sale())));
							this.configService.update(sc);
							// 更新lucene索引
							elasticsearchUtil.indexUpdate(IndexName.GOODS, IndexType.GROUPLIFEGOODS,
									goods.getId().toString(), IndexVoTools.groupLifeGoodsToIndexVo(goods));
//							String goods_lucene_path = ConfigContants.LUCENE_DIRECTORY
//									+ File.separator
//									+ "luence"
//									+ File.separator
//									+ "grouplifegoods";
//							File file = new File(goods_lucene_path);
//							if (!file.exists()) {
//								CommUtil.createFolder(goods_lucene_path);
//							}
//							LuceneUtil lucene = LuceneUtil.instance();
//							lucene.setIndex_path(goods_lucene_path);
//							lucene.update(CommUtil.null2String(goods.getId()),
//									luceneVoTools.updateLifeGoodsIndex(goods));
							String msg_content = "恭喜您成功购买团购"
									+ map.get("goods_name") + ",团购消费码分别为："
									+ codes + "您可以到用户中心-我的生活购中查看消费码的使用情况";
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
										code_list, user.getId().toString(),
										goods.getUser().getId().toString());
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
										.queryOfGoodsCount(CommUtil
												.null2String(order.getId()),
												CommUtil.null2String(goods
														.getId()));
								if (goods.getGroup() != null
										&& goods.getGroup_buy() == 2) {
									for (GroupGoods gg : goods
											.getGroup_goods_list()) {
										if (gg.getGroup()
												.getId()
												.equals(goods.getGroup()
														.getId())) {

											gg.setGg_count(gg.getGg_count()
													- goods_count);
											gg.setGg_def_count(gg
													.getGg_def_count()
													+ goods_count);
											this.groupGoodsService.update(gg);
										}
									}
								}
								List<String> gsps = new ArrayList<String>();
								List<GoodsSpecProperty> temp_gsp_list = this.orderFormTools
										.queryOfGoodsGsps(CommUtil
												.null2String(order.getId()),
												CommUtil.null2String(goods
														.getId()));
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
										String[] temp_ids = CommUtil
												.null2String(temp.get("id"))
												.split("_");
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
									goods.setGoods_inventory_detail(Json
											.toJson(list, JsonFormat.compact()));
								}
								for (GroupGoods gg : goods
										.getGroup_goods_list()) {
									if (gg.getGroup().getId()
											.equals(goods.getGroup().getId())
											&& gg.getGg_count() == 0) {
										goods.setGroup_buy(3);// 标识商品的状态为团购数量已经结束
									}
								}
								this.goodsService.update(goods);
								// 更新lucene索引
								if (goods.getGroup_buy() == 2) {
									elasticsearchUtil.indexUpdate(IndexName.GOODS, IndexType.GOODS,CommUtil.null2String(goods.getId()), 
											IndexVoTools.groupGoodsToIndexVo(goods.getGroup_goods_list().get(goods.getGroup_goods_list().size() - 1)));
//									String goods_lucene_path = ConfigContants.LUCENE_DIRECTORY
//											+ File.separator
//											+ "luence"
//											+ File.separator + "goods";
//									File file = new File(goods_lucene_path);
//									if (!file.exists()) {
//										CommUtil.createFolder(goods_lucene_path);
//									}
//									LuceneUtil lucene = LuceneUtil.instance();
//									lucene.setIndex_path(goods_lucene_path);
//									lucene.update(
//											CommUtil.null2String(goods.getId()),
//											luceneVoTools
//													.updateGroupGoodsIndex(goods
//															.getGroup_goods_list()
//															.get(goods
//																	.getGroup_goods_list()
//																	.size() - 1)));
								} else {
									elasticsearchUtil.indexUpdate(IndexName.GOODS, IndexType.GOODS, 
											CommUtil.null2String(goods.getId()), IndexVoTools.goodsToIndexVo(goods));
//									String goods_lucene_path = ConfigContants.LUCENE_DIRECTORY
//											+ File.separator
//											+ "luence"
//											+ File.separator + "goods";
//									File file = new File(goods_lucene_path);
//									if (!file.exists()) {
//										CommUtil.createFolder(goods_lucene_path);
//									}
//									LuceneUtil lucene = LuceneUtil.instance();
//									lucene.setIndex_path(goods_lucene_path);
//									lucene.update(CommUtil.null2String(goods
//											.getId()), luceneVoTools
//											.updateGoodsIndex(goods));
								}
							}
						}
					}
					// 记录支付日志
					OrderFormLog ofl = new OrderFormLog();
					ofl.setAddTime(new Date());
					ofl.setLog_info("预付款支付");
					ofl.setLog_user(user);
					ofl.setOf(order);
					this.orderFormLogService.save(ofl);
				} else {
					code = -400;// 预存款余额不足
				}
			} else {
				code = -500;// 订单已经支付
			}
		} else {
			code = -300;// 订单支付方式信息错误
		}
		json_map.put("code", code);
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
	 * 订单订单预存款支付时登录密码验证
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping("/mobile/pay_balance_verify.htm")
	public void pay_balance_verify(HttpServletRequest request,
			HttpServletResponse response, String password, String user_id,
			String token) {
		boolean verify = CommUtil.null2Boolean(request.getHeader("verify"));
		Map json_map = new HashMap();
		List map_list1 = new ArrayList();
		if (verify && user_id != null && !user_id.equals("") && token != null
				&& !token.equals("")) {
			User user = this.userService
					.getObjById(CommUtil.null2Long(user_id));
			if (user != null) {
				if (user.getApp_login_token().equals(token.toLowerCase())) {
					String temp_password = this.encodeStr(password);
					System.out.println(password + "加密后：" + temp_password);
					if (user.getMobile_pay_password().equals(temp_password)) {
						verify = true;
					} else {
						verify = false;
					}
				} else {
					verify = false;
				}
			} else {
				verify = false;
			}
		} else {
			verify = false;
		}
		json_map.put("verify", verify);
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
	 * 手机端订单支付查询所有支付方式
	 * 
	 * @param request
	 * @param response
	 * @param user_id
	 * @param token
	 * @param order_id
	 */
	@RequestMapping("/mobile/query_payment.htm")
	public void query_payment(HttpServletRequest request,
			HttpServletResponse response, String user_id, String token,
			String order_id) {
		boolean verify = CommUtil.null2Boolean(request.getHeader("verify"));
		Map json_map = new HashMap();
		if (verify && user_id != null && !user_id.equals("") && token != null
				&& !token.equals("")) {
			User user = this.userService
					.getObjById(CommUtil.null2Long(user_id));
			if (user != null) {
				if (user.getApp_login_token().equals(token.toLowerCase())) {
					String str_payment = this.query_payment();
					json_map.put("str_payment", str_payment);
				} else {
					verify = false;
				}
			} else {
				verify = false;
			}
		} else {
			verify = false;
		}
		json_map.put("ret", CommUtil.null2String(verify));
		String json = Json.toJson(json_map, JsonFormat.compact());
		System.out.println(json);
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
	 * 手机端订单支付查询在线支付方式（APP支付宝）
	 * 
	 * @param request
	 * @param response
	 * @param user_id
	 * @param token
	 * @param order_id
	 */
	@RequestMapping("/mobile/query_payment_online.htm")
	public void query_payment_online(HttpServletRequest request,
			HttpServletResponse response) {
		boolean verify = CommUtil.null2Boolean(request.getHeader("verify"));
		Map json_map = new HashMap();
		Payment online = get_payment("online");
		json_map.put("seller", online.getSeller_email());
		json_map.put("partner", online.getPartner());
		json_map.put("private", online.getApp_private_key());
		json_map.put("public", online.getApp_public_key());
		json_map.put("safekey", online.getSafeKey());
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
	 * 订单查询时保存支付方式
	 * 
	 * @param request
	 * @param response
	 * @param user_id
	 * @param token
	 * @param order_id
	 */
	@RequestMapping("/mobile/buyer_order_payment_save.htm")
	public void order_payment_save(HttpServletRequest request,
			HttpServletResponse response, String user_id, String token,
			String order_id, String payType) {
		boolean verify = CommUtil.null2Boolean(request.getHeader("verify"));
		Map json_map = new HashMap();
		if (verify && user_id != null && !user_id.equals("") && token != null
				&& !token.equals("")) {
			User user = this.userService
					.getObjById(CommUtil.null2Long(user_id));
			if (user != null) {
				if (user.getApp_login_token().equals(token.toLowerCase())) {
					OrderForm obj = this.orderFormService.getObjById(CommUtil
							.null2Long(order_id));
					if (payType.equals("支付宝")) {
						payType = "online";
					}
					if (payType.equals("预存款支付")) {
						payType = "balance";
					}
					if (payType.equals("货到付款")) {
						payType = "payafter";
					}
					obj.setPayType(payType);
					obj.setPayment(this.get_payment(payType));
					this.orderFormService.update(obj);
				} else {
					verify = false;
				}
			} else {
				verify = false;
			}
		} else {
			verify = false;
		}
		json_map.put("ret", CommUtil.null2String(verify));
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
	 * 订单支付完成
	 * 
	 * @param request
	 * @param response
	 * @param payType
	 * @param order_id
	 * @return
	 */
	@RequestMapping("/mobile/pay_finish.htm")
	public ModelAndView pay_finish(HttpServletRequest request,
			HttpServletResponse response, String order_id, String type) {
		ModelAndView mv = new JModelAndView("mobile/view/pay_finish.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		OrderForm order = this.orderFormService.getObjById(CommUtil
				.null2Long(order_id));
		if (order != null) {
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
			mv.addObject("type", type);
			mv.addObject("obj", order);
			mv.addObject("order_total_price", order_total_price);
		}
		return mv;
	}

	/**
	 * 获得购物车总体商品价格
	 * 
	 * @param request
	 * @param response
	 */
	private double getcartsPrice(List<GoodsCart> carts) {
		double all_price = 0.0;
		for (GoodsCart gc : carts) {
			all_price = CommUtil.add(all_price,
					CommUtil.mul(gc.getCount(), gc.getPrice()));
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
	 * 根据cart_ids获取购物车
	 * 
	 * @param request
	 * @param response
	 */
	private List<GoodsCart> getGoodscartByids(String cart_ids) {
		String ids[] = cart_ids.split(",");
		List<GoodsCart> carts = new ArrayList<GoodsCart>();
		for (String cart_id : ids) {
			if (!cart_id.equals("")) {
				GoodsCart gc = this.goodsCartService.getObjById(CommUtil
						.null2Long(cart_id));
				carts.add(gc);
			}
		}
		return carts;
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

	private void send_email(HttpServletRequest request, OrderForm order,
			String email, String mark) {
		try{
			Template template = this.templateService.getObjByProperty("mark", mark);
			if (template != null && template.isOpen()) {
				String subject = template.getTitle();
				String path = TytsmsStringUtils.generatorFilesFolderServerPath(request)+ ConfigContants.GENERATOR_FILES_MIDDLE_NAME + File.separator;
				if (!CommUtil.fileExist(path)) {
					CommUtil.createFolder(path);
				}
				PrintWriter pwrite = new PrintWriter(new OutputStreamWriter(
						new FileOutputStream(path + mark+".vm", false), "UTF-8"));
				pwrite.print(template.getContent());
				pwrite.flush();
				pwrite.close();
				// 生成模板
				Properties p = new Properties();
				p.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH,
						TytsmsStringUtils.generatorFilesFolderServerPath(request) + ConfigContants.GENERATOR_FILES_MIDDLE_NAME + File.separator);
				p.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
				p.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
				Velocity.init(p);
				org.apache.velocity.Template blank = Velocity.getTemplate(mark+".vm",
						"UTF-8");
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
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	private void send_sms(HttpServletRequest request, OrderForm order,
			String mobile, String mark) {
		try{
			Template template = this.templateService.getObjByProperty("mark", mark);
			if (template != null && template.isOpen()) {
				String path =TytsmsStringUtils.generatorFilesFolderServerPath(request) + ConfigContants.GENERATOR_FILES_MIDDLE_NAME + File.separator;
				if (!CommUtil.fileExist(path)) {
					CommUtil.createFolder(path);
				}
				PrintWriter pwrite = new PrintWriter(new OutputStreamWriter(
						new FileOutputStream(path + mark+".vm", false), "UTF-8"));
				pwrite.print(CommUtil.filterHTML(template.getContent()));
				pwrite.flush();
				pwrite.close();
				// 生成模板
				Properties p = new Properties();
				p.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH,
						TytsmsStringUtils.generatorFilesFolderServerPath(request) + ConfigContants.GENERATOR_FILES_MIDDLE_NAME + File.separator);
				p.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
				p.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
				Velocity.init(p);
				org.apache.velocity.Template blank = Velocity.getTemplate(mark+".vm",
						"UTF-8");
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
				String path = TytsmsStringUtils.generatorFilesFolderServerPath(request)+ ConfigContants.GENERATOR_FILES_MIDDLE_NAME+File.separator;
				PrintWriter pwrite = new PrintWriter(new OutputStreamWriter(
						new FileOutputStream(path + mark+".vm", false), "UTF-8"));
				pwrite.print(template.getContent());
				pwrite.flush();
				pwrite.close();
				// 生成模板
				Properties p = new Properties();
				p.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH,
						TytsmsStringUtils.generatorFilesFolderServerPath(request)+ ConfigContants.GENERATOR_FILES_MIDDLE_NAME + File.separator);
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

	/**
	 * 加密
	 * 
	 * @param pwd
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public String encodeStr(String str) {
		byte[] enbytes = Base64.encodeBase64Chunked(str.getBytes());
		return new String(enbytes);
	}

	private String query_payment() {
		Map params = new HashMap();
		Set marks = new TreeSet();
		String payment = "none";
		// 在线支付
		marks.add("alipay_app");
		params.put("marks", marks);
		params.put("install", true);
		List<Payment> online = this.paymentService
				.query("select obj from Payment obj where obj.mark in(:marks) and obj.install=:install",
						params, -1, -1);
		// 预存款支付
		marks.clear();
		marks.add("balance");
		params.put("marks", marks);
		params.put("install", true);
		List<Payment> balance = this.paymentService
				.query("select obj from Payment obj where obj.mark in(:marks) and obj.install=:install",
						params, -1, -1);
		// 货到付款
		marks.clear();
		marks.add("payafter");
		params.put("marks", marks);
		params.put("install", true);
		List<Payment> payafter = this.paymentService
				.query("select obj from Payment obj where obj.mark in(:marks) and obj.install=:install",
						params, -1, -1);
		if (online.size() == 1) {
			payment = "online";
		}
		if (balance.size() == 1) {
			payment = payment + "," + balance.get(0).getMark();
		}
		if (payafter.size() == 1) {
			payment = payment + "," + payafter.get(0).getMark();
		}
		return payment;
	}

	private Payment get_payment(String mark) {
		Map params = new HashMap();
		Set marks = new TreeSet();
		Payment payment = null;
		// 在线支付
		if (mark.equals("online")) {
			mark = "alipay_app";
		}
		marks.add(mark);
		params.put("marks", marks);
		params.put("install", true);
		List<Payment> payments = this.paymentService
				.query("select obj from Payment obj where obj.mark in(:marks) and obj.install=:install",
						params, -1, -1);
		if (payments.size() > 0) {
			payment = payments.get(0);
		}
		return payment;
	}

}
