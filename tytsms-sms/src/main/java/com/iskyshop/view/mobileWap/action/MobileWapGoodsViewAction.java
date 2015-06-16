package com.iskyshop.view.mobileWap.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.search.Sort;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.ip.IPSeeker;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.ActivityGoods;
import com.iskyshop.foundation.domain.Area;
import com.iskyshop.foundation.domain.Consult;
import com.iskyshop.foundation.domain.Evaluate;
import com.iskyshop.foundation.domain.Favorite;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsBrand;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.GoodsSpecProperty;
import com.iskyshop.foundation.domain.GoodsSpecification;
import com.iskyshop.foundation.domain.Group;
import com.iskyshop.foundation.domain.GroupGoods;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IActivityGoodsService;
import com.iskyshop.foundation.service.IActivityService;
import com.iskyshop.foundation.service.IAreaService;
import com.iskyshop.foundation.service.IConsultService;
import com.iskyshop.foundation.service.IEvaluateService;
import com.iskyshop.foundation.service.IFavoriteService;
import com.iskyshop.foundation.service.IGoodsBrandService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGroupGoodsService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.UserTools;
import com.iskyshop.manage.seller.Tools.TransportTools;
import com.iskyshop.view.web.tools.ActivityViewTools;
import com.iskyshop.view.web.tools.GoodsViewTools;
import com.iskyshop.view.web.tools.IntegralViewTools;
import com.taiyitao.elasticsearch.ElasticsearchUtil;
import com.taiyitao.elasticsearch.IndexVo.IndexName;
import com.taiyitao.elasticsearch.IndexVo.IndexType;
import com.taiyitao.elasticsearch.IndexVoTools;
import com.taiyitao.elasticsearch.SearchResult;

/**
 * 
 * <p>
 * Title: MobileGoodsViewAction.java
 * </p>
 * 
 * <p>
 * Description:手机客户端商城前台商品请求
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
 * @date 2014-7-14
 * 
 * @version 1.0
 */
@Controller
public class MobileWapGoodsViewAction  extends MobileWapBaseAction{
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGoodsBrandService brandService;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private IGoodsBrandService goodsBrandService;
	@Autowired
	private IGroupGoodsService groupgoodsService;
	@Autowired
	private IActivityGoodsService activityGoodsService;
	@Autowired
	private IActivityService activityService;
	@Autowired
	private IConsultService consultService;
	@Autowired
	private GoodsViewTools goodsviewTools;
	@Autowired
	private IFavoriteService favoriteService;
	@Autowired
	private IEvaluateService evaluateService;
	@Autowired
	private IUserService userService;
	@Autowired
	private TransportTools transportTools;
	@Autowired
	private IntegralViewTools integralViewTools;
	
	@Autowired
	private GoodsViewTools goodsViewTools;
	
	@Autowired
	private IAreaService areaService;
	
	@Autowired
	private UserTools userTools;
	@Autowired
	private ActivityViewTools activityViewTools;
	@Autowired
	private ElasticsearchUtil elasticsearchUtil;
	

	/**
	 * 手机客户端商城首页商品详情请求
	 * 
	 * @param request
	 * @param response
	 * @param store_id
	 * @return
	 */
	@RequestMapping("/mobileWap/goods.htm")
	public ModelAndView goods(HttpServletRequest request, HttpServletResponse response,
			String id, String token) {
		
		String user_id = "";
		Map userMap = checkLogin(request, user_id);
	   	if(userMap.get("user_id") != null){
	   		user_id = userMap.get("user_id").toString();
	   	}
	   	User user = null ;
		if(userMap.get("user") != null){
	   		user =  (User) userMap.get("user");
	   	}
		
		ModelAndView mv = new JModelAndView("mobileWap/view/goods.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		boolean verify = CommUtil.null2Boolean(request.getHeader("verify"));
		verify = true;
		Map goods_map = new HashMap();
		String json = "";

		if (verify) {
			Goods obj = this.goodsService.getObjById(CommUtil.null2Long(id));
			if (obj != null) {
				if (obj.getGroup() != null && obj.getGroup_buy() == 2) {// 如果是团购商品，检查团购是否过期
					Group group = obj.getGroup();
					if (group.getEndTime().before(new Date())) {
						obj.setGroup(null);
						obj.setGroup_buy(0);
						obj.setGoods_current_price(obj.getStore_price());
					}
				}
				this.goodsService.update(obj);
				goods_map.put("id", obj.getId());
				goods_map.put("favorite", "false");
				if (verify && user_id != null && !user_id.equals("")) {
					user = this.userService.getObjById(CommUtil
							.null2Long(user_id));
					if (user != null) {
							Map params = new HashMap();
							params.put("user_id", CommUtil.null2Long(user_id));
							params.put("goods_id", obj.getId());
							List<Favorite> list = this.favoriteService
									.query("select obj from Favorite obj where obj.user.id=:user_id and obj.goods.id=:goods_id",
											params, -1, -1);
							if (list.size() > 0) {
								goods_map.put("favorite", "true");
							}
					}
				}
				goods_map.put("user_id", user_id);
				goods_map.put("goods_status", obj.getGoods_status());
				goods_map.put("goods_name", obj.getGoods_name());
				goods_map.put("goods_type", obj.getGoods_type());// 商品类型，0为自营商品，1为第三方经销商
				goods_map.put("goods_price",
						CommUtil.null2String(obj.getGoods_price()));// 商品原价
				goods_map.put("goods_current_price",
						CommUtil.null2String(obj.getGoods_current_price()));// 商品现价
				goods_map.put("goods_inventory", obj.getGoods_inventory());// 库存
				goods_map.put("inventory_type", obj.getInventory_type());// 库存方式分为all全局库存，spec按规格库存
				goods_map.put("goods_salenum", obj.getGoods_salenum());// 销量
				goods_map.put("goods_fee", obj.getGoods_fee());// 运费
				goods_map.put("goods_well_evaluate",
						CommUtil.mul(obj.getWell_evaluate(), 100) + "%");// 好评率
				goods_map.put("goods_middle_evaluate",
						CommUtil.mul(obj.getMiddle_evaluate(), 100) + "%");// 中评率
				goods_map.put("goods_bad_evaluate",
						CommUtil.mul(obj.getBad_evaluate(), 100) + "%");// 差评率
				goods_map.put("evaluate_count", obj.getEvaluates().size());// 总评论数
				goods_map.put("consult_count", obj.getConsults().size());// 总咨询数
				String status = "goods";
				if (obj.getGroup_buy() == 2) {
					status = "group";
				}
				if (obj.getActivity_status() == 2) {
					status = "activity";
				}
				goods_map.put("status", status);// 是否参加活动
				List list = new ArrayList();
				if (obj.getGoods_main_photo() != null) {
					list.add(CommUtil.getURL(request) + "/"
							+ obj.getGoods_main_photo().getPath() + "/"
							+ obj.getGoods_main_photo().getName() + "_middle."
							+ obj.getGoods_main_photo().getExt());// 添加主图片
				}
				for (Accessory acc : obj.getGoods_photos()) {// 添加附图
					list.add(CommUtil.getURL(request) + "/" + acc.getPath()
							+ "/" + acc.getName() + "_middle." + acc.getExt());
				}
				goods_map.put("goods_photos", list);
				goods_map.put("main_img", list.get(0));
				String current_ip = CommUtil.getIpAddr(request);// 获得本机IP
				String current_city = "未知地区";
				if (CommUtil.isIp(current_ip)) {
					IPSeeker ip = new IPSeeker(null, null);
					current_city = ip.getIPLocation(current_ip).getCountry();
				}
				goods_map.put("current_city", current_city);
				// 设置运费信息
				String trans_information = "商家承担";
				if (obj.getGoods_transfee() == 0) {
					if (obj.getTransport() != null
							&& !obj.getTransport().equals("")) {
						String main_info = "平邮(¥"
								+ this.transportTools.cal_goods_trans_fee(obj
										.getTransport().getId().toString(),
										"mail", CommUtil.null2String(obj
												.getGoods_weight()), CommUtil
												.null2String(obj
														.getGoods_volume()),
										current_city);
						String express_info = "快递(¥"
								+ this.transportTools.cal_goods_trans_fee(obj
										.getTransport().getId().toString(),
										"express", CommUtil.null2String(obj
												.getGoods_weight()), CommUtil
												.null2String(obj
														.getGoods_volume()),
										current_city);
						String ems_info = "EMS(¥"
								+ this.transportTools.cal_goods_trans_fee(obj
										.getTransport().getId().toString(),
										"ems", CommUtil.null2String(obj
												.getGoods_weight()), CommUtil
												.null2String(obj
														.getGoods_volume()),
										current_city);
						trans_information = main_info + ") | " + express_info
								+ ") | " + ems_info + ")";
					} else {
						trans_information = "平邮(¥"
								+ CommUtil.null2Float(obj.getMail_trans_fee())
								+ ") | "
								+ "快递(¥"
								+ CommUtil.null2Float(obj
										.getExpress_trans_fee()) + ") | "
								+ "EMS(¥"
								+ CommUtil.null2Float(obj.getEms_trans_fee())
								+ ")";
					}
				}
				goods_map.put("trans_information", trans_information);
				goods_map.put("ret", CommUtil.null2String(verify));
				
				
				
					if (obj.getGoods_type() == 0) {// 平台自营商品
						
						obj.setGoods_click(obj.getGoods_click() + 1);
						if (this.configService.getSysConfig().isZtc_status()
								&& obj.getZtc_status() == 2) {
							obj.setZtc_click_num(obj.getZtc_click_num() + 1);
						}
						if (obj.getGroup() != null && obj.getGroup_buy() == 2) {// 如果是团购商品，检查团购是否过期
							Group group = obj.getGroup();
							if (group.getEndTime().before(new Date())) {
								obj.setGroup(null);
								obj.setGroup_buy(0);
								obj.setGoods_current_price(obj.getStore_price());
							}
						}
						this.goodsService.update(obj);
						
						// 查询运费地区
						List<Area> areas = this.areaService.queryAllAreas();
						mv.addObject("areas", areas);
						// 相关分类
						Map params = new HashMap();
						params.put("parent_id", obj.getGc().getParent().getId());
						params.put("display", true);
						List<GoodsClass> about_gcs = this.goodsClassService
								.query("select obj from GoodsClass obj where obj.parent.id=:parent_id and obj.display=:display order by sequence asc",
										params, -1, -1);
						mv.addObject("about_gcs", about_gcs);
						
					} else {// 商家商品
						String template = "default";
						if (obj.getGoods_store().getTemplate() != null
								&& !obj.getGoods_store().getTemplate().equals("")) {
							template = obj.getGoods_store().getTemplate();
						}
						
						obj.setGoods_click(obj.getGoods_click() + 1);
						if (this.configService.getSysConfig().isZtc_status()
								&& obj.getZtc_status() == 2) {
							obj.setZtc_click_num(obj.getZtc_click_num() + 1);
						}
						if (obj.getGroup() != null && obj.getGroup_buy() == 2) {// 如果是团购商品，检查团购是否过期
							Group group = obj.getGroup();
							if (group.getEndTime().before(new Date())) {
								obj.setGroup(null);
								obj.setGroup_buy(0);
								obj.setGoods_current_price(obj.getStore_price());
							}
						}
						this.goodsService.update(obj);
						if (obj.getGoods_store().getStore_status() == 15) {// 店铺为开通状态
							mv.addObject("obj", obj);
							mv.addObject("store", obj.getGoods_store());
							mv.addObject("goodsViewTools", goodsViewTools);
							mv.addObject("transportTools", transportTools);


							// 查询运费地区
							List<Area> areas = this.areaService.queryAllAreas();
							mv.addObject("areas", areas);
							this.generic_evaluate(obj.getGoods_store(), mv);

							// 相关分类
							Map params2 = new HashMap();
							params2.put("parent_id", obj.getGc().getParent().getId());
							params2.put("display", true);
							List<GoodsClass> about_gcs = this.goodsClassService
									.query("select obj from GoodsClass obj where obj.parent.id=:parent_id and obj.display=:display order by sequence asc",
											params2, -1, -1);
							mv.addObject("about_gcs", about_gcs);
							
						}

					}
				
				json = Json.toJson(goods_map, JsonFormat.compact());
				System.out.println("=========商品详情json=======" + json);
			}
		}
		
		Goods obj = this.goodsService.getObjById(CommUtil.null2Long(id));
		if (!obj.getGoods_property().equals("[]")) {
			List propertities = Json.fromJson(ArrayList.class,
					obj.getGoods_property());
			mv.addObject("propertities", propertities);
		}
		mv.addObject("obj", obj);
		
		mv.addObject("goods_map", goods_map);
		mv.addObject("user_id", user_id);

		return mv;
	}

	
	
	/**
	 * 查看店铺商品详细信息
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping("/mobileWap/storegoods.htm")
	public ModelAndView goods(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = null;
		Goods obj = this.goodsService.getObjById(CommUtil.null2Long(id));
		if (this.configService.getSysConfig().isSecond_domain_open()) {// 如果系统开启了二级域名，则判断该商品是不是对应的二级域名下的，如果不是则返回错误页面
			String serverName = request.getServerName().toLowerCase();
			String secondDomain = CommUtil.null2String(serverName.substring(0,
					serverName.indexOf(".")));
			if (serverName.indexOf(".") == serverName.lastIndexOf(".")) {
				secondDomain = "www";
			}
			// System.out.println("已经开启二级域名，二级域名为：" + secondDomain);
			if (obj != null && !secondDomain.equals("")) {
				if (obj.getGoods_type() == 0) {// 自营商品禁止使用二级域名访问
					if (!secondDomain.equals("www")) {
						mv = new JModelAndView("error.html",
								configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1,
								request, response);
						mv.addObject("op_title", "参数错误，商品查看失败");
						mv.addObject("url", CommUtil.getURL(request)
								+ "/index.htm");
						return mv;
					}
					// System.out.println("已经开启二级域名，自营商品禁止二级域名访问");
				} else {
					if (!obj.getGoods_store().getStore_second_domain()
							.equals(secondDomain)) {
						mv = new JModelAndView("error.html",
								configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1,
								request, response);
						mv.addObject("op_title", "参数错误，商品查看失败");
						mv.addObject("url", CommUtil.getURL(request)
								+ "/index.htm");
						// System.out.println("已经开启二级域名，非本店商品，二级域名错误");
						return mv;
					}
				}
			} else {
				mv = new JModelAndView("error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "参数错误，商品查看失败");
				mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
				return mv;
			}
		}
		// System.out.println("未开启二级域名");
		// 利用cookie添加浏览过的商品
		Cookie[] cookies = request.getCookies();
		Cookie goodscookie = null;
		int k = 0;
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("goodscookie")) {
					String goods_ids = cookie.getValue();
					int m = 6;
					int n = goods_ids.split(",").length;
					if (m > n) {
						m = n + 1;
					}
					String[] new_goods_ids = goods_ids.split(",", m);
					for (int i = 0; i < new_goods_ids.length; i++) {
						if ("".equals(new_goods_ids[i])) {
							for (int j = i + 1; j < new_goods_ids.length; j++) {
								new_goods_ids[i] = new_goods_ids[j];
							}
						}
					}
					String[] new_ids = new String[6];
					for (int i = 0; i < m - 1; i++) {
						if (id.equals(new_goods_ids[i])) {
							k++;
						}
					}
					if (k == 0) {
						new_ids[0] = id;
						for (int j = 1; j < m; j++) {
							new_ids[j] = new_goods_ids[j - 1];
						}
						goods_ids = id + ",";
						if (m == 2) {
							for (int i = 1; i <= m - 1; i++) {
								goods_ids = goods_ids + new_ids[i] + ",";
							}
						} else {
							for (int i = 1; i < m; i++) {
								goods_ids = goods_ids + new_ids[i] + ",";
							}
						}
						goodscookie = new Cookie("goodscookie", goods_ids);
					} else {
						new_ids = new_goods_ids;
						goods_ids = "";
						for (int i = 0; i < m - 1; i++) {
							goods_ids += new_ids[i] + ",";
						}
						goodscookie = new Cookie("goodscookie", goods_ids);
					}
					goodscookie.setMaxAge(60 * 60 * 24 * 7);
					goodscookie.setDomain(CommUtil.generic_domain(request));
					response.addCookie(goodscookie);
					break;
				} else {
					goodscookie = new Cookie("goodscookie", id + ",");
					goodscookie.setMaxAge(60 * 60 * 24 * 7);
					goodscookie.setDomain(CommUtil.generic_domain(request));
					response.addCookie(goodscookie);
				}
			}
		} else {
			goodscookie = new Cookie("goodscookie", id + ",");
			goodscookie.setMaxAge(60 * 60 * 24 * 7);
			goodscookie.setDomain(CommUtil.generic_domain(request));
			response.addCookie(goodscookie);
		}
		User current_user = SecurityUserHolder.getCurrentUser();
		boolean admin_view = false;
		if (current_user != null) {
			current_user = this.userService.getObjById(current_user.getId());
			if (current_user.getUserRole().equals("ADMIN")) {
				admin_view = true;
			}
		}
		if (obj != null && obj.getGoods_status() == 0 || admin_view) {
			if (obj.getGoods_type() == 0) {// 平台自营商品
				mv = new JModelAndView("default/store_goods.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				obj.setGoods_click(obj.getGoods_click() + 1);
				if (this.configService.getSysConfig().isZtc_status()
						&& obj.getZtc_status() == 2) {
					obj.setZtc_click_num(obj.getZtc_click_num() + 1);
				}
				if (obj.getGroup() != null && obj.getGroup_buy() == 2) {// 如果是团购商品，检查团购是否过期
					Group group = obj.getGroup();
					if (group.getEndTime().before(new Date())) {
						obj.setGroup(null);
						obj.setGroup_buy(0);
						obj.setGoods_current_price(obj.getStore_price());
					}
				}
				this.goodsService.update(obj);
				mv.addObject("obj", obj);
				mv.addObject("goodsViewTools", goodsViewTools);
				mv.addObject("transportTools", transportTools);
				// 计算当期访问用户的IP地址，并计算对应的运费信息
				String current_ip = CommUtil.getIpAddr(request);// 获得本机IP
				if (CommUtil.isIp(current_ip)) {
					IPSeeker ip = new IPSeeker(null, null);
					String current_city = ip.getIPLocation(current_ip)
							.getCountry();
					mv.addObject("current_city", current_city);
				} else {
					mv.addObject("current_city", "未知地区");
				}
				// 查询运费地区
				List<Area> areas = this.areaService
						.query("select obj from Area obj where obj.parent.id is null order by obj.sequence asc",
								null, -1, -1);
				mv.addObject("areas", areas);
				// 相关分类
				Map params = new HashMap();
				params.put("parent_id", obj.getGc().getParent().getId());
				params.put("display", true);
				List<GoodsClass> about_gcs = this.goodsClassService
						.query("select obj from GoodsClass obj where obj.parent.id=:parent_id and obj.display=:display order by sequence asc",
								params, -1, -1);
				mv.addObject("about_gcs", about_gcs);
				mv.addObject("userTools", userTools);
				mv.addObject("activityViewTools", activityViewTools);
			} else {// 商家商品
				String template = "default";
				if (obj.getGoods_store().getTemplate() != null
						&& !obj.getGoods_store().getTemplate().equals("")) {
					template = obj.getGoods_store().getTemplate();
				}
				mv = new JModelAndView(template + "/store_goods.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				obj.setGoods_click(obj.getGoods_click() + 1);
				if (this.configService.getSysConfig().isZtc_status()
						&& obj.getZtc_status() == 2) {
					obj.setZtc_click_num(obj.getZtc_click_num() + 1);
				}
				if (obj.getGroup() != null && obj.getGroup_buy() == 2) {// 如果是团购商品，检查团购是否过期
					Group group = obj.getGroup();
					if (group.getEndTime().before(new Date())) {
						obj.setGroup(null);
						obj.setGroup_buy(0);
						obj.setGoods_current_price(obj.getStore_price());
					}
				}
				this.goodsService.update(obj);
				if (obj.getGoods_store().getStore_status() == 15) {// 店铺为开通状态
					mv.addObject("obj", obj);
					mv.addObject("store", obj.getGoods_store());
					mv.addObject("goodsViewTools", goodsViewTools);
					mv.addObject("transportTools", transportTools);

					// 计算当期访问用户的IP地址，并计算对应的运费信息
					String current_ip = CommUtil.getIpAddr(request);// 获得本机IP
					if (CommUtil.isIp(current_ip)) {
						IPSeeker ip = new IPSeeker(null, null);
						String current_city = ip.getIPLocation(current_ip)
								.getCountry();
						mv.addObject("current_city", current_city);
					} else {
						mv.addObject("current_city", "未知地区");
					}

					// 查询运费地区
					List<Area> areas = this.areaService
							.query("select obj from Area obj where obj.parent.id is null order by obj.sequence asc",
									null, -1, -1);
					mv.addObject("areas", areas);
					this.generic_evaluate(obj.getGoods_store(), mv);

					// 相关分类
					Map params2 = new HashMap();
					params2.put("parent_id", obj.getGc().getParent().getId());
					params2.put("display", true);
					List<GoodsClass> about_gcs = this.goodsClassService
							.query("select obj from GoodsClass obj where obj.parent.id=:parent_id and obj.display=:display order by sequence asc",
									params2, -1, -1);
					mv.addObject("about_gcs", about_gcs);
					mv.addObject("userTools", userTools);
					mv.addObject("activityViewTools", activityViewTools);
				} else {
					mv = new JModelAndView("error.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "店铺未开通或者其他参数错误，拒绝访问");
					mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
				}

			}

		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "参数错误，商品查看失败");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		}
		return mv;
	}
	
	
	
	/**
	 * 加载店铺评分信息
	 * 
	 * @param store
	 * @param mv
	 */
	private void generic_evaluate(Store store, ModelAndView mv) {
		double description_result = 0;
		double service_result = 0;
		double ship_result = 0;
		GoodsClass gc = this.goodsClassService
				.getObjById(store.getGc_main_id());
		if (store != null && gc != null && store.getPoint() != null) {
			float description_evaluate = CommUtil.null2Float(gc
					.getDescription_evaluate());
			float service_evaluate = CommUtil.null2Float(gc
					.getService_evaluate());
			float ship_evaluate = CommUtil.null2Float(gc.getShip_evaluate());

			float store_description_evaluate = CommUtil.null2Float(store
					.getPoint().getDescription_evaluate());
			float store_service_evaluate = CommUtil.null2Float(store.getPoint()
					.getService_evaluate());
			float store_ship_evaluate = CommUtil.null2Float(store.getPoint()
					.getShip_evaluate());
			// 计算和同行比较结果
			description_result = CommUtil.div(store_description_evaluate
					- description_evaluate, description_evaluate);
			service_result = CommUtil.div(store_service_evaluate
					- service_evaluate, service_evaluate);
			ship_result = CommUtil.div(store_ship_evaluate - ship_evaluate,
					ship_evaluate);
		}
		if (description_result > 0) {
			mv.addObject("description_css", "red");
			mv.addObject("description_css1", "bg_red");
			mv.addObject("description_type", "高于");
			mv.addObject(
					"description_result",
					CommUtil.null2String(CommUtil.mul(description_result, 100) > 100 ? 100
							: CommUtil.mul(description_result, 100))
							+ "%");
		}
		if (description_result == 0) {
			mv.addObject("description_css", "orange");
			mv.addObject("description_css1", "bg_orange");
			mv.addObject("description_type", "持平");
			mv.addObject("description_result", "-----");
		}
		if (description_result < 0) {
			mv.addObject("description_css", "green");
			mv.addObject("description_css1", "bg_green");
			mv.addObject("description_type", "低于");
			mv.addObject(
					"description_result",
					CommUtil.null2String(CommUtil.mul(-description_result, 100))
							+ "%");
		}
		if (service_result > 0) {
			mv.addObject("service_css", "red");
			mv.addObject("service_css1", "bg_red");
			mv.addObject("service_type", "高于");
			mv.addObject(
					"service_result",
					CommUtil.null2String(CommUtil.mul(service_result, 100) > 100 ? 100
							: CommUtil.mul(service_result, 100))
							+ "%");
		}
		if (service_result == 0) {
			mv.addObject("service_css", "orange");
			mv.addObject("service_css1", "bg_orange");
			mv.addObject("service_type", "持平");
			mv.addObject("service_result", "-----");
		}
		if (service_result < 0) {
			mv.addObject("service_css", "green");
			mv.addObject("service_css1", "bg_green");
			mv.addObject("service_type", "低于");
			mv.addObject("service_result",
					CommUtil.null2String(CommUtil.mul(-service_result, 100))
							+ "%");
		}
		if (ship_result > 0) {
			mv.addObject("ship_css", "red");
			mv.addObject("ship_css1", "bg_red");
			mv.addObject("ship_type", "高于");
			mv.addObject(
					"ship_result",
					CommUtil.null2String(CommUtil.mul(ship_result, 100) > 100 ? 100
							: CommUtil.mul(ship_result, 100))
							+ "%");
		}
		if (ship_result == 0) {
			mv.addObject("ship_css", "orange");
			mv.addObject("ship_css1", "bg_orange");
			mv.addObject("ship_type", "持平");
			mv.addObject("ship_result", "-----");
		}
		if (ship_result < 0) {
			mv.addObject("ship_css", "green");
			mv.addObject("ship_css1", "bg_green");
			mv.addObject("ship_type", "低于");
			mv.addObject("ship_result",
					CommUtil.null2String(CommUtil.mul(-ship_result, 100)) + "%");
		}
	}
	
	/**
	 * 根据商品id和地区名称查询相应商品运费
	 * 
	 * @param request
	 * @param response
	 * @param current_city
	 * @param goods_id
	 */
	@RequestMapping("/mobileWap/goods_trans_fee.htm")
	public void goods_trans_fee(HttpServletRequest request,
			HttpServletResponse response, String current_city, String goods_id) {
		boolean verify = CommUtil.null2Boolean(request.getHeader("verify"));
		Map json_map = new HashMap();
		if (verify) {
			Goods obj = this.goodsService.getObjById(CommUtil
					.null2Long(goods_id));
			// 设置运费信息
			String trans_information = "商家承担";
			if (obj.getGoods_transfee() == 0) {
				if (obj.getTransport() != null
						&& !obj.getTransport().equals("")) {
					String main_info = "平邮(¥"
							+ this.transportTools
									.cal_goods_trans_fee(obj.getTransport()
											.getId().toString(), "mail",
											CommUtil.null2String(obj
													.getGoods_weight()),
											CommUtil.null2String(obj
													.getGoods_volume()),
											current_city);
					String express_info = "快递(¥"
							+ this.transportTools
									.cal_goods_trans_fee(obj.getTransport()
											.getId().toString(), "express",
											CommUtil.null2String(obj
													.getGoods_weight()),
											CommUtil.null2String(obj
													.getGoods_volume()),
											current_city);
					String ems_info = "EMS(¥"
							+ this.transportTools
									.cal_goods_trans_fee(obj.getTransport()
											.getId().toString(), "ems",
											CommUtil.null2String(obj
													.getGoods_weight()),
											CommUtil.null2String(obj
													.getGoods_volume()),
											current_city);
					trans_information = main_info + ") | " + express_info
							+ ") | " + ems_info + ")";
				} else {
					trans_information = "平邮(¥"
							+ CommUtil.null2Float(obj.getMail_trans_fee())
							+ ") | " + "快递(¥"
							+ CommUtil.null2Float(obj.getExpress_trans_fee())
							+ ") | " + "EMS(¥"
							+ CommUtil.null2Float(obj.getEms_trans_fee()) + ")";
				}
			}
			json_map.put("trans_information", trans_information);
		} else {
			verify = false;
		}
		json_map.put("ret", CommUtil.null2String(verify));
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			// System.out.println(Json.toJson(map, JsonFormat.compact()));
			writer.print(Json.toJson(json_map, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 点击商品规格加载商品规格信息
	 * 
	 * @param request
	 * @param response
	 * @param gsp
	 * @param id
	 */
	@RequestMapping("/mobileWap/load_goods_gsp.htm")
	public void load_goods_gsp(HttpServletRequest request,
			HttpServletResponse response, String gsp, String id,
			String token) {
		
		String user_id = "";
		Map userMap = checkLogin(request, user_id);
	   	if(userMap.get("user_id") != null){
	   		user_id = userMap.get("user_id").toString();
	   	}
	   	User user = null ;
		if(userMap.get("user") != null){
	   		user =  (User) userMap.get("user");
	   	}
		
		boolean verify = CommUtil.null2Boolean(request.getHeader("verify"));
		Map map = new HashMap();
		if (verify) {
			Goods goods = this.goodsService.getObjById(CommUtil.null2Long(id));
			int count = 0;
			double price = 0;
			double act_price = 0;
			if (goods.getGroup() != null && goods.getGroup_buy() == 2) {// 团购商品统一按照团购价格处理
				for (GroupGoods gg : goods.getGroup_goods_list()) {
					if (gg.getGroup().getId().equals(goods.getGroup().getId())) {
						count = gg.getGg_group_count() - gg.getGg_def_count();
						price = CommUtil.null2Double(gg.getGg_price());
					}
				}
			} else {
				count = goods.getGoods_inventory();
				price = CommUtil.null2Double(goods.getGoods_current_price());
				if (goods.getInventory_type().equals("spec")) {
					List<HashMap> list = Json.fromJson(ArrayList.class,
							goods.getGoods_inventory_detail());
					if (gsp != null) {
						String[] gsp_ids = gsp.split(",");
						for (Map temp : list) {
							String[] temp_ids = CommUtil.null2String(
									temp.get("id")).split("_");
							Arrays.sort(gsp_ids);
							Arrays.sort(temp_ids);
							if (Arrays.equals(gsp_ids, temp_ids)) {
								count = CommUtil.null2Int(temp.get("count"));
								price = CommUtil.null2Double(temp.get("price"));
							}
						}
					}
				}
				int temp_count = 0;
				BigDecimal ac_rebate = null;
				if (user_id != null && token != null) {
					if (goods.getActivity_status() == 2) {// 如果是促销商品，根据规格配置价格计算相应配置的促销价格
						for (ActivityGoods ag : goods.getActivity_goods_list()) {
							if (ag.getAg_status() == 1
									&& ag.getAct().getAc_status() == 1) {
								temp_count++;
								if (temp_count > 0) {
									String level_name = integralViewTools
											.query_user_level_name(user_id);
									if (level_name.equals("铜牌会员")) {
										ac_rebate = ag.getAct().getAc_rebate();
									}
									if (level_name.equals("银牌会员")) {
										ac_rebate = ag.getAct().getAc_rebate1();
									}
									if (level_name.equals("金牌会员")) {
										ac_rebate = ag.getAct().getAc_rebate2();
									}
									if (level_name.equals("超级会员")) {
										ac_rebate = ag.getAct().getAc_rebate3();
									}
									act_price = CommUtil.mul(price, ac_rebate);
									break;
								}
							}

						}
					}
				}

			}
			map.put("count", count);
			map.put("price", price);
			map.put("act_price", act_price);
		}
		map.put("ret", CommUtil.null2String(verify));
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
	 * 手机客户端商城商品规格
	 * 
	 * @param request
	 * @param response
	 * @param store_id
	 * @return
	 */
	@RequestMapping("/mobileWap/goods_specs.htm")
	public void goods_specs(HttpServletRequest request,
			HttpServletResponse response, String id) {
		boolean verify = CommUtil.null2Boolean(request.getHeader("verify"));
		Map map = new HashMap();
		List list = new ArrayList();
		String url = CommUtil.getURL(request);
		if (verify) {
			Goods obj = this.goodsService.getObjById(CommUtil.null2Long(id));
			List<GoodsSpecification> specs = goodsviewTools.generic_spec(id);
			for (GoodsSpecification spec : specs) {
				Map spec_map = new HashMap();
				spec_map.put("spec_type", spec.getType());
				spec_map.put("spec_key", spec.getName());
				List spec_list = new ArrayList();
				for (GoodsSpecProperty spro : obj.getGoods_specs()) {
					if (spro.getSpec().getId().equals(spec.getId())) {
						Map map_par = new HashMap();
						map_par.put("id", spro.getId());
						map_par.put("val", spro.getValue());
						spec_list.add(map_par);
					}
				}
				spec_map.put("spec_values", spec_list);
				list.add(spec_map);
			}
			map.put("spec_list", list);
		}
		map.put("ret", CommUtil.null2String(verify));
		String json = Json.toJson(map, JsonFormat.compact());
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
	 * gb_id：商品品牌id，gc_id：商品分类id,beginCount：查询起始位置，selectCount:查询个数
	 * 
	 * @param request
	 * @param response
	 * @param goods_current_price
	 *            ,goods_salenum,goods_seller_time,goods_click
	 * @param orderType
	 * @param gb_id
	 * @param gc_id
	 */
	@RequestMapping("/mobileWap/goods_list.htm")
	public ModelAndView goods_list(HttpServletRequest request,
			HttpServletResponse response, String orderBy, String orderType,
			String beginCount, String selectCount, String gc_id, String gb_id,
			String keyword, String queryType) {
		ModelAndView mv = new JModelAndView("mobileWap/view/search_goods_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		boolean load = true;
//		if(orderBy !=null && "goods_current_price".equals(orderBy)){
//			load = false ;
//		}else {
			if(beginCount ==null || "".equals(beginCount)){
				beginCount = "0";
			}
			if(selectCount ==null || "".equals(selectCount)){
				selectCount = "10";
			}
//		}
		boolean verify = CommUtil.null2Boolean(request.getHeader("verify"));
		verify = true;
		Map json_map = new HashMap();
		if (orderBy == null || orderBy.equals("")) {
			orderBy = "goods_salenum";
		}
		if (orderType == null || orderType.equals("")) {
			orderType = "desc";
		}
		if (verify && orderBy != null && !orderBy.equals("")) {
			List<Goods> goods_list = null;
			Map params = new HashMap();
			params.put("goods_status", 0);
			String query = "select obj from Goods obj where obj.goods_status=:goods_status order by  ";
			if (gc_id != null && !gc_id.equals("")) {
				params.put("gc_id", CommUtil.null2Long(gc_id));
				query = "select obj from Goods obj where obj.goods_status=:goods_status and obj.gc.id=:gc_id order by  ";
			}
			if (gb_id != null && !gb_id.equals("")) {
				params.put("gb_id", CommUtil.null2Long(gb_id));
				query = "select obj from Goods obj where obj.goods_status=:goods_status and obj.goods_brand.id=:gb_id order by ";
			}
			if (keyword != null && !keyword.equals("")) {
				params.put("keyword", "%" + keyword + "%");
				query = "select obj from Goods obj where obj.goods_status=:goods_status and obj.goods_name like:keyword order by  ";
			}
			if (queryType != null && queryType.equals("activity")) {
				params.put("activity_status", 2);
				query = "select obj from Goods obj where obj.goods_status=:goods_status and obj.activity_status=:activity_status order by  ";
			}
			if (queryType != null && queryType.equals("group")) {
				params.put("group_buy", 2);
				query = "select obj from Goods obj where obj.goods_status=:goods_status and obj.group_buy=:group_buy order by  ";
			}
//			按照品牌查询
			if (queryType != null && queryType.equals("goods_brand")) {
				params.put("goods_brand", CommUtil.null2Long(gc_id));
				params.remove("gc_id");
				params.remove("keyword");
//				GoodsBrand goodsBrand = this.goodsBrandService.getObjById(CommUtil.null2Long(gc_id));
//				keyword = goodsBrand.getName();
				query = "select obj from Goods obj where obj.goods_status=:goods_status and obj.goods_brand.id=:goods_brand order by  ";
			}
//			按照分类查询
			if (queryType != null && queryType.equals("goodsClass")) {
				params.put("gc_id", CommUtil.null2Long(gc_id));
				params.remove("keyword");
//				GoodsClass goodsClass = this.goodsClassService.getObjById(CommUtil.null2Long(gc_id));
//				keyword = goodsClass.getClassName();
				query = "select obj from Goods obj where obj.goods_status=:goods_status and (obj.gc.id=:gc_id or obj.gc.parent.id=:gc_id or obj.gc.parent.parent.id=:gc_id  )  order by  ";
			}
			if (gc_id == null && gb_id == null && keyword == null
					&& queryType == null) {
				params = null;
			}
			goods_list = this.goodsService.query(query + orderBy + " "
					+ orderType, params, CommUtil.null2Int(beginCount),
					CommUtil.null2Int(selectCount));
			List map_list = new ArrayList();
			for (Goods obj : goods_list) {
				Map goods_map = new HashMap();
				goods_map.put("id", obj.getId());
				goods_map.put("goods_name", obj.getGoods_name());
				goods_map.put("goods_current_price",
						CommUtil.null2String(obj.getGoods_current_price()));// 商品现价
				goods_map.put("goods_salenum", obj.getGoods_salenum());// 销量
				String goods_main_photo = CommUtil.getURL(request)// 系统默认商品图片
						+ "/"
						+ this.configService.getSysConfig().getGoodsImage()
								.getPath()
						+ "/"
						+ this.configService.getSysConfig().getGoodsImage()
								.getName();
				if (obj.getGoods_main_photo() != null) {// 商品主图片
					goods_main_photo = CommUtil.getURL(request) + "/"
							+ obj.getGoods_main_photo().getPath() + "/"
							+ obj.getGoods_main_photo().getName() + "_small."
							+ obj.getGoods_main_photo().getExt();
				}
				goods_map.put("goods_main_photo", goods_main_photo);
				String status = "goods";
				if (obj.getGroup_buy() == 2) {
					status = "group";
				}
				if (obj.getActivity_status() == 2) {
					status = "activity";
				}
				goods_map.put("status", status);
				map_list.add(goods_map);
			}
			json_map.put("goods_list", map_list);
		}
		json_map.put("code", CommUtil.null2String(verify));
		json_map.put("keyword", keyword);
		json_map.put("orderType", orderType);
		json_map.put("orderBy", orderBy);
		json_map.put("queryType", queryType);
		json_map.put("gc_id", gc_id);
		json_map.put("beginCount", beginCount);
		json_map.put("selectCount", selectCount);
		json_map.put("load", load);
		mv.addObject("json_map", json_map);
		System.out.println("json_map:====" + json_map);
		return mv;
	}

	
	

	/**
	 * gb_id：商品品牌id，gc_id：商品分类id,beginCount：查询起始位置，selectCount:查询个数
	 * 
	 * @param request
	 * @param response
	 * @param goods_current_price
	 *            ,goods_salenum,goods_seller_time,goods_click
	 * @param orderType
	 * @param gb_id
	 * @param gc_id
	 */
	@RequestMapping("/mobileWap/goods_list_load.htm")
	public void goods_list_load(HttpServletRequest request,
			HttpServletResponse response, String orderBy, String orderType,
			String beginCount, String selectCount, String gc_id, String gb_id,
			String keyword, String queryType) {
		
		if(beginCount ==null || "".equals(beginCount)){
			beginCount = "0";
		}
		if(selectCount ==null || "".equals(selectCount)){
			selectCount = "10";
		}
		boolean verify = CommUtil.null2Boolean(request.getHeader("verify"));
		verify = true;
		Map json_map = new HashMap();
		if (orderBy == null || orderBy.equals("")) {
			orderBy = "goods_salenum";
		}
		if (orderType == null || orderType.equals("")) {
			orderType = "desc";
		}
		if (verify && orderBy != null && !orderBy.equals("")) {
			List<Goods> goods_list = null;
			Map params = new HashMap();
			params.put("goods_status", 0);
			String query = "select obj from Goods obj where obj.goods_status=:goods_status order by  ";
			if (gc_id != null && !gc_id.equals("")) {
				params.put("gc_id", CommUtil.null2Long(gc_id));
				query = "select obj from Goods obj where obj.goods_status=:goods_status and obj.gc.id=:gc_id order by  ";
			}
			if (gb_id != null && !gb_id.equals("")) {
				params.put("gb_id", CommUtil.null2Long(gb_id));
				query = "select obj from Goods obj where obj.goods_status=:goods_status and obj.goods_brand.id=:gb_id order by ";
			}
			if (keyword != null && !keyword.equals("")) {
				params.put("keyword", "%" + keyword + "%");
				query = "select obj from Goods obj where obj.goods_status=:goods_status and obj.goods_name like:keyword order by  ";
			}
			if (queryType != null && queryType.equals("activity")) {
				params.put("activity_status", 2);
				query = "select obj from Goods obj where obj.goods_status=:goods_status and obj.activity_status=:activity_status order by  ";
			}
			if (queryType != null && queryType.equals("group")) {
				params.put("group_buy", 2);
				query = "select obj from Goods obj where obj.goods_status=:goods_status and obj.group_buy=:group_buy order by  ";
			}
			if (gc_id == null && gb_id == null && keyword == null
					&& queryType == null) {
				params = null;
			}
			goods_list = this.goodsService.query(query + orderBy + " "
					+ orderType, params, CommUtil.null2Int(beginCount),
					CommUtil.null2Int(selectCount));
			List map_list = new ArrayList();
			for (Goods obj : goods_list) {
				Map goods_map = new HashMap();
				goods_map.put("id", obj.getId());
				goods_map.put("goods_name", obj.getGoods_name());
				goods_map.put("goods_current_price",
						CommUtil.null2String(obj.getGoods_current_price()));// 商品现价
				goods_map.put("goods_salenum", obj.getGoods_salenum());// 销量
				String goods_main_photo = CommUtil.getURL(request)// 系统默认商品图片
						+ "/"
						+ this.configService.getSysConfig().getGoodsImage()
								.getPath()
						+ "/"
						+ this.configService.getSysConfig().getGoodsImage()
								.getName();
				if (obj.getGoods_main_photo() != null) {// 商品主图片
					goods_main_photo = CommUtil.getURL(request) + "/"
							+ obj.getGoods_main_photo().getPath() + "/"
							+ obj.getGoods_main_photo().getName() + "_small."
							+ obj.getGoods_main_photo().getExt();
				}
				goods_map.put("goods_main_photo", goods_main_photo);
				String status = "goods";
				if (obj.getGroup_buy() == 2) {
					status = "group";
				}
				if (obj.getActivity_status() == 2) {
					status = "activity";
				}
				goods_map.put("status", status);
				map_list.add(goods_map);
			}
			json_map.put("goods_list", map_list);
		}
		json_map.put("code", CommUtil.null2String(verify));
		json_map.put("keyword", keyword);
		json_map.put("orderType", orderType);
		json_map.put("orderBy", orderBy);
		json_map.put("beginCount", beginCount);
		json_map.put("selectCount", selectCount);
		System.out.println("json_map_load:====" + json_map);
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
	 * 手机客户端商品详细介绍
	 * 
	 * @param request
	 * @param response
	 * @param store_id
	 * @return
	 */
	@RequestMapping("/mobileWap/goods_introduce.htm")
	public ModelAndView goods_introduce(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("mobileWap/view/goods_introduce.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Goods obj = this.goodsService.getObjById(CommUtil.null2Long(id));
		if (!obj.getGoods_property().equals("[]")) {
			List propertities = Json.fromJson(ArrayList.class,
					obj.getGoods_property());
			mv.addObject("propertities", propertities);
		}
		mv.addObject("obj", obj);
		return mv;
	}

	/**
	 * 手机客户端商城商品评价,type:类型，1:好评，0中评，-1差评
	 * 
	 * @param request
	 * @param response
	 * @param store_id
	 * @return
	 */
	@RequestMapping("/mobileWap/goods_evaluate.htm")
	public void goods_evaluate(HttpServletRequest request,
			HttpServletResponse response, String id, String type,
			String beginCount, String selectCount) {
		boolean verify = CommUtil.null2Boolean(request.getHeader("verify"));
		Map json_map = new HashMap();
		List eva_list = new ArrayList();
		if (true) {
			Map params = new HashMap();
			String query;
			Goods goods = this.goodsService.getObjById(CommUtil.null2Long(id));
			if (type != null && !type.equals("")) {// 查询好评、中评、差评
				params.clear();
				params.put("gid", CommUtil.null2Long(id));
				params.put("evaluate_status", 0);
				params.put("evaluate_buyer_val", CommUtil.null2Int(type));
				query = "select obj from Evaluate obj where obj.evaluate_goods.id=:gid and obj.evaluate_status=:evaluate_status and obj.evaluate_buyer_val=:evaluate_buyer_val order by addTime desc";
			} else {// 查询所有评价
				params.clear();
				params.put("gid", CommUtil.null2Long(id));
				params.put("evaluate_status", 0);
				query = "select obj from Evaluate obj where obj.evaluate_goods.id=:gid and obj.evaluate_status=:evaluate_status order by addTime desc";
			}
			List<Evaluate> evas = this.evaluateService.query(query, params,
					CommUtil.null2Int(beginCount),
					CommUtil.null2Int(selectCount));
			for (Evaluate eva : evas) {
				Map map = new HashMap();
				map.put("user", eva.getEvaluate_user().getUserName());
				map.put("content", eva.getEvaluate_info());
				map.put("addTime", CommUtil.formatShortDate(eva.getAddTime()));
				eva_list.add(map);
			}
			json_map.put("eva_list", eva_list);
			int well_count = 0;
			int middle_count = 0;
			int bad_count = 0;
			Map params2 = new HashMap();
			int evs[] = { -1, 0, 1 };
			for (int ev : evs) {
				params2.clear();
				params2.put("gid", CommUtil.null2Long(id));
				params2.put("evaluate_status", 0);
				params2.put("evaluate_buyer_val", ev);
				List<Evaluate> all_evas = this.evaluateService
						.query("select obj.id from Evaluate obj where obj.evaluate_goods.id=:gid and obj.evaluate_status=:evaluate_status and obj.evaluate_buyer_val=:evaluate_buyer_val order by addTime desc",
								params2, -1, -1);
				if (ev == -1) {
					bad_count = all_evas.size();
					json_map.put("bad", CommUtil.null2String(bad_count) + "-"
							+ CommUtil.mul(goods.getBad_evaluate(), 100) + "%");
				}
				if (ev == 0) {
					middle_count = all_evas.size();
					json_map.put(
							"middle",
							CommUtil.null2String(middle_count)
									+ "-"
									+ CommUtil.mul(goods.getMiddle_evaluate(),
											100) + "%");
				}
				if (ev == 1) {
					well_count = all_evas.size();
					json_map.put("well", CommUtil.null2String(well_count) + "-"
							+ CommUtil.mul(goods.getWell_evaluate(), 100) + "%");
				}
			}
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
	 * 手机客户端商城商品资讯信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/mobileWap/goods_consult.htm")
	public void goods_consult(HttpServletRequest request,
			HttpServletResponse response, String id, String beginCount,
			String selectCount) {
		boolean verify = CommUtil.null2Boolean(request.getHeader("verify"));
		Map json_map = new HashMap();
		List consult_list = new ArrayList();
		if (verify) {
			Map params = new HashMap();
			params.put("gid", CommUtil.null2Long(id));
			List<Consult> consults = this.consultService
					.query("select obj from Consult obj where obj.goods.id=:gid order by addTime desc",
							params, CommUtil.null2Int(beginCount),
							CommUtil.null2Int(selectCount));
			for (Consult obj : consults) {
				Map map = new HashMap();
				map.put("addTime", CommUtil.formatShortDate(obj.getAddTime()));// 咨询时间
				if (obj.getConsult_user() != null) {
					map.put("consult_user", obj.getConsult_user().getUserName());// 咨询用户名称
				} else {
					map.put("consult_user", "匿名");// 咨询用户名称
				}
				map.put("content", obj.getConsult_content());// 咨询内容
				map.put("reply", obj.isReply());// 是否回复
				if (obj.isReply()) {
					map.put("reply_content", obj.getConsult_reply());// 回复内容
					map.put("reply_user", obj.getReply_user().getUserName());// 回复人姓名
					map.put("reply_time",
							CommUtil.formatShortDate(obj.getReply_time()));// 回复时间
				}
				consult_list.add(map);
			}
			json_map.put("consult_list", consult_list);
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
	 * 手机客户端发布商品咨询信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/mobileWap/goods_consult_save.htm")
	public void goods_consult_save(HttpServletRequest request,
			HttpServletResponse response, String goods_id, 
			String token, String content) {
		
		String user_id = "";
		Map userMap = checkLogin(request, user_id);
	   	if(userMap.get("user_id") != null){
	   		user_id = userMap.get("user_id").toString();
	   	}
	   	User user = null ;
		if(userMap.get("user") != null){
	   		user =  (User) userMap.get("user");
	   	}
		
		boolean verify = CommUtil.null2Boolean(request.getHeader("verify"));
		Map json_map = new HashMap();
		if (verify) {
			if (user_id != null && !user_id.equals("") && token != null
					&& !token.equals("")) {
				user = this.userService.getObjById(CommUtil
						.null2Long(user_id));
				Goods goods = this.goodsService.getObjById(CommUtil
						.null2Long(goods_id));
				if (user != null) {
					Consult obj = new Consult();
					obj.setGoods(goods);
					obj.setAddTime(new Date());
					obj.setConsult_content(content);
					obj.setConsult_user(user);
					this.consultService.save(obj);
				} else {
					verify = false;
				}
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
	 * 手机客户端收藏商品请求
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/mobileWap/goods_favorite_save.htm")
	public void goods_favorite_save(HttpServletRequest request,
			HttpServletResponse response, String goods_id) {
		boolean verify = CommUtil.null2Boolean(request.getHeader("verify"));
		verify = true;
		int ret = 100;// 操作成功
		Map json_map = new HashMap();
		
		String user_id = "" ;
		Map userMap = checkLogin(request, user_id);
		User user = null ;
	   	if(userMap.get("user_id") != null){
	   		user_id = userMap.get("user_id").toString();
	   	}
	   	if(userMap.get("user") != null){
	   		user =  (User) userMap.get("user");
	   	}
		
		if (verify && user_id != null && !user_id.equals("")) {
			user = this.userService
					.getObjById(CommUtil.null2Long(user_id));
			if (user != null) {
					Goods goods = this.goodsService.getObjById(CommUtil
							.null2Long(goods_id));
					if (goods != null) {
						Map params = new HashMap();
						params.put("user_id", CommUtil.null2Long(user_id));
						params.put("goods_id", goods.getId());
						List<Favorite> list = this.favoriteService
								.query("select obj from Favorite obj where obj.user.id=:user_id and obj.goods.id=:goods_id",
										params, -1, -1);
						if (list.size() == 0) {
							Favorite obj = new Favorite();
							obj.setAddTime(new Date());
							obj.setType(0);
							obj.setUser(user);
							obj.setGoods(goods);
							this.favoriteService.save(obj);
							goods.setGoods_collect(goods.getGoods_collect() + 1);
							this.goodsService.update(goods);
							// 更新lucene索引
							elasticsearchUtil.indexUpdate(IndexName.GOODS, IndexType.GOODS, 
									goods.getId().toString(), IndexVoTools.goodsToIndexVo(goods));
//							String goods_lucene_path = ConfigContants.LUCENE_DIRECTORY
//									+ File.separator
//									+ "luence" + File.separator + "goods";
//							File file = new File(goods_lucene_path);
//							if (!file.exists()) {
//								CommUtil.createFolder(goods_lucene_path);
//							}
//							LuceneUtil lucene = LuceneUtil.instance();
//							lucene.setIndex_path(goods_lucene_path);
//							lucene.update(CommUtil.null2String(goods.getId()),
//									luceneVoTools.updateGoodsIndex(goods));
						} else {
							ret = -300;// 已经关注过该商品
						}
					} else {
						ret = -350;// 商品信息错误
					}

				} else {
					ret = -400;// 用户信息错误
				}
		} else {
			ret = -500;// 请求错误
		}
		json_map.put("code", ret);
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
	 * 手机客户端商城首页商品详情底部“你可能喜欢的商品”列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/mobileWap/like_goods_list.htm")
	public void like_goods_list(HttpServletRequest request,
			HttpServletResponse response, String id) {
		boolean verify = CommUtil.null2Boolean(request.getHeader("verify"));
		Map goods_map = new HashMap();
		List goods_list = new ArrayList();
		if (verify) {
			Goods obj = this.goodsService.getObjById(CommUtil.null2Long(id));
			Map params = new HashMap();
			params.put("gid", CommUtil.null2Long(id));
			params.put("gc_id", obj.getGc().getId());
			params.put("goods_status", 0);
			List<Goods> lists_goods = this.goodsService
					.query("select obj from Goods obj where obj.id!=:gid and obj.gc.id=:gc_id and obj.goods_status=:goods_status",
							params, 0, 30);
			String url = CommUtil.getURL(request);
			for (Goods goods : lists_goods) {
				Map map = new HashMap();
				map.put("id", goods.getId());
				map.put("name", goods.getGoods_name());
				String goods_main_photo = url// 系统默认商品图片
						+ "/"
						+ this.configService.getSysConfig().getGoodsImage()
								.getPath()
						+ "/"
						+ this.configService.getSysConfig().getGoodsImage()
								.getName();
				if (goods.getGoods_main_photo() != null) {// 商品主图片
					goods_main_photo = url + "/"
							+ goods.getGoods_main_photo().getPath() + "/"
							+ goods.getGoods_main_photo().getName() + "_small."
							+ goods.getGoods_main_photo().getExt();
				}
				map.put("goods_main_photo", goods_main_photo);
				goods_list.add(map);
			}
			goods_map.put("goods_list", goods_list);
		}
		goods_map.put("ret", CommUtil.null2String(verify));
		String json = Json.toJson(goods_map, JsonFormat.compact());
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
	 * 通过会员id查询会员的等级，然后返回该会员等级对应的活动商品价格
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/mobileWap/query_goodsActivity_price.htm")
	public void query_user_level(HttpServletRequest request,
			HttpServletResponse response, String token,
			String goods_id) {
		
		
		String user_id = "";
		Map userMap = checkLogin(request, user_id);
	   	if(userMap.get("user_id") != null){
	   		user_id = userMap.get("user_id").toString();
	   	}
	   	User user = null ;
		if(userMap.get("user") != null){
	   		user =  (User) userMap.get("user");
	   	}
		
		boolean verify = CommUtil.null2Boolean(request.getHeader("verify"));
		Map json_map = new HashMap();
		if (verify && user_id != null && !user_id.equals("") && token != null
				&& !token.equals("")) {
			user = this.userService
					.getObjById(CommUtil.null2Long(user_id));
			String level_name = "";
			if (user != null) {
				if (user.getApp_login_token().equals(token.toLowerCase())) {
					Goods goods = this.goodsService.getObjById(CommUtil
							.null2Long(goods_id));
					int temp_count = 0;
					BigDecimal ac_rebate = null;
					if (user_id != null && token != null) {
						if (goods.getActivity_status() == 2) {// 如果是促销商品，根据规格配置价格计算相应配置的促销价格
							for (ActivityGoods ag : goods
									.getActivity_goods_list()) {
								if (ag.getAg_status() == 1
										&& ag.getAct().getAc_status() == 1) {
									temp_count++;
									if (temp_count > 0) {
										level_name = integralViewTools
												.query_user_level_name(user_id);
										if (level_name.equals("铜牌会员")) {
											ac_rebate = ag.getAct()
													.getAc_rebate();
										}
										if (level_name.equals("银牌会员")) {
											ac_rebate = ag.getAct()
													.getAc_rebate1();
										}
										if (level_name.equals("金牌会员")) {
											ac_rebate = ag.getAct()
													.getAc_rebate2();
										}
										if (level_name.equals("超级会员")) {
											ac_rebate = ag.getAct()
													.getAc_rebate3();
										}
										break;
									}
								}

							}
						}
					}
					json_map.put("act_rate", ac_rebate);// 享有折扣率
					json_map.put("level_name", level_name);// 会员等级名称
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

	
	
	@RequestMapping("/mobileWap/search.htm")
	public ModelAndView search(HttpServletRequest request,
			HttpServletResponse response,  String keyword) {
		ModelAndView mv = new JModelAndView("mobileWap/view/search_goods_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
//		String path = ConfigContants.LUCENE_DIRECTORY + File.separator
//				+ "luence" + File.separator + "goods";
//		LuceneUtil lucene = LuceneUtil.instance();
//		lucene.setIndex_path(path);
//		lucene.setGc_size(this.goodsClassService.query("select obj from GoodsClass obj",null,-1, -1).size());
		boolean order_type = true;
		String order_by = "";
		Sort sort = null;
		String query_gc = "";
		
//		LuceneResult pList = lucene.search(keyword,
//				CommUtil.null2Int(0), 0, 0, null, sort,
//				null, null, "", "", "",query_gc);
//		CommUtil.saveLucene2ModelAndView(pList, mv);
		
		SearchResult pList = elasticsearchUtil.search(keyword, 
				CommUtil.null2Int(0), -1, -1, "", "", 
				"", "", "", "", "", query_gc);
		CommUtil.saveSearchResult2ModelAndView(pList, mv);
		
		//对关键字命中的商品进行分类提取
//		List<String> list_gcs = lucene.LoadData_goods_class(keyword);
		List<String> list_gcs = elasticsearchUtil.LoadData_goods_class(keyword);
		//对商品分类数据进行分析加载
		
		mv.addObject("list_gc", list_gcs);
		mv.addObject("allCount", pList.getRows());
		mv.addObject("keyword", keyword);
		mv.addObject("goodsViewTools", goodsViewTools);

		// 如果系统开启直通车，商品列表页顶部推荐热卖商品及左侧推广商品均显示直通车商品
		if (this.configService.getSysConfig().isZtc_status()) {
			// 页面左侧8条数据，从第3位开始查询
			List<Goods> left_ztc_goods = null;
			Map ztc_map = new HashMap();
			ztc_map.put("ztc_status", 3);
			ztc_map.put("now_date", new Date());
			ztc_map.put("ztc_gold", 0);
			// 获取所有商品数量，查询出的对象为只有id的对象，减少查询压力,查询所有直通车商品，随机显示出指定数量
			List<Goods> all_left_ztc_goods = this.goodsService
					.query("select obj.id from Goods obj where obj.ztc_status =:ztc_status "
							+ "and obj.ztc_begin_time <=:now_date and obj.ztc_gold>:ztc_gold "
							+ "order by obj.ztc_dredge_price desc", ztc_map,
							-1, -1);
			left_ztc_goods = this.goodsService
					.query("select obj from Goods obj where obj.ztc_status =:ztc_status "
							+ "and obj.ztc_begin_time <=:now_date and obj.ztc_gold>:ztc_gold "
							+ "order by obj.ztc_dredge_price desc", ztc_map, 3,
							all_left_ztc_goods.size());
			left_ztc_goods = this.goodsViewTools.randomZtcGoods2(
					left_ztc_goods, 8);
			mv.addObject("left_ztc_goods", left_ztc_goods);
			// 页面顶部,直通车前3个商品
			List<Goods> top_ztc_goods = null;
			Map ztc_map2 = new HashMap();
			ztc_map2.put("ztc_status", 3);
			ztc_map2.put("now_date", new Date());
			ztc_map2.put("ztc_gold", 0);
			top_ztc_goods = this.goodsService
					.query("select obj from Goods obj where obj.ztc_status =:ztc_status "
							+ "and obj.ztc_begin_time <=:now_date and obj.ztc_gold>:ztc_gold "
							+ "order by obj.ztc_dredge_price desc", ztc_map2,
							0, 3);
			mv.addObject("top_ztc_goods", top_ztc_goods);
		} else {
			Map params = new HashMap();
			params.put("store_recommend", true);
			params.put("goods_status", 0);
			List<Goods> top_ztc_goods = this.goodsService
					.query("select obj from Goods obj where obj.store_recommend=:store_recommend and obj.goods_status=:goods_status order by obj.goods_salenum desc",
							params, 0, 3);
			mv.addObject("top_ztc_goods", top_ztc_goods);
			params.clear();
			params.put("store_recommend", true);
			params.put("goods_status", 0);
			// 获取所有商品数量，查询出的对象为只有id的对象，减少查询压力,查询所有直通车商品，随机显示出指定数量
			List<Goods> all_goods = this.goodsService
					.query("select obj.id from Goods obj where obj.store_recommend=:store_recommend and obj.goods_status=:goods_status order by obj.goods_salenum desc",
							params, -1, -1);
			List<Goods> left_ztc_goods = this.goodsService
					.query("select obj from Goods obj where obj.store_recommend=:store_recommend and obj.goods_status=:goods_status order by obj.goods_salenum desc",
							params, 3, all_goods.size());
			left_ztc_goods = this.goodsViewTools.randomZtcGoods2(
					left_ztc_goods, 8);
			mv.addObject("left_ztc_goods", left_ztc_goods);
		}
		mv.addObject("userTools", userTools);
		return mv;
	}
	
}
