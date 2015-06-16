package com.iskyshop.view.web.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.ip.IPSeeker;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.ActivityGoods;
import com.iskyshop.foundation.domain.Area;
import com.iskyshop.foundation.domain.Consult;
import com.iskyshop.foundation.domain.FootPoint;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsBrand;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.GoodsSpecProperty;
import com.iskyshop.foundation.domain.GoodsTypeProperty;
import com.iskyshop.foundation.domain.Group;
import com.iskyshop.foundation.domain.GroupGoods;
import com.iskyshop.foundation.domain.Plate;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.StoreNavigation;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.UserGoodsClass;
import com.iskyshop.foundation.domain.query.ConsultQueryObject;
import com.iskyshop.foundation.domain.query.EvaluateQueryObject;
import com.iskyshop.foundation.domain.query.GoodsQueryObject;
import com.iskyshop.foundation.service.IAreaService;
import com.iskyshop.foundation.service.IConsultService;
import com.iskyshop.foundation.service.IEvaluateService;
import com.iskyshop.foundation.service.IFootPointService;
import com.iskyshop.foundation.service.IGoodsBrandService;
import com.iskyshop.foundation.service.IGoodsCartService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGoodsSpecPropertyService;
import com.iskyshop.foundation.service.IGoodsTypePropertyService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IPlateService;
import com.iskyshop.foundation.service.IStoreNavigationService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserGoodsClassService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.UserTools;
import com.iskyshop.manage.seller.Tools.TransportTools;
import com.iskyshop.view.web.tools.ActivityViewTools;
import com.iskyshop.view.web.tools.AreaViewTools;
import com.iskyshop.view.web.tools.GoodsViewTools;
import com.iskyshop.view.web.tools.IntegralViewTools;
import com.iskyshop.view.web.tools.StoreViewTools;

/**
 * 
 * <p>
 * Title: GoodsViewAction.java
 * </p>
 * 
 * <p>
 * Description: 商品前台控制器,用来显示商品列表、商品详情、商品其他信息
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012-2014
 * </p>
 * 
 * <p>
 * Company: 沈阳网之商科技有限公司 www.iskyshop.com
 * </p>
 * 
 * @author erikzhang
 * 
 * @date 2014-4-28
 * 
 * @version iskyshop_b2b2c 1.0
 */
@Controller
public class GoodsViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private IUserGoodsClassService userGoodsClassService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IEvaluateService evaluateService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IGoodsCartService goodsCartService;
	@Autowired
	private IConsultService consultService;
	@Autowired
	private IGoodsBrandService brandService;
	@Autowired
	private IGoodsSpecPropertyService goodsSpecPropertyService;
	@Autowired
	private IGoodsTypePropertyService goodsTypePropertyService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private AreaViewTools areaViewTools;
	@Autowired
	private GoodsViewTools goodsViewTools;
	@Autowired
	private StoreViewTools storeViewTools;
	@Autowired
	private UserTools userTools;
	@Autowired
	private TransportTools transportTools;
	@Autowired
	private IUserService userService;
	@Autowired
	private IStoreNavigationService storenavigationService;
	@Autowired
	private IntegralViewTools integralViewTools;
	@Autowired
	private ActivityViewTools activityViewTools;
	@Autowired
	private IPlateService plateService;
	@Autowired
	private IFootPointService footPointService;

	/**
	 * 根据单个店铺分类查看对应的商品
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@RequestMapping("/goods_list.htm")
	public ModelAndView goods_list(HttpServletRequest request,
			HttpServletResponse response, String ugc_id, String store_id,
			String orderBy, String orderType, String currentPage) {
		UserGoodsClass ugc = this.userGoodsClassService.getObjById(CommUtil
				.null2Long(ugc_id));
		String template = "default";
		Store store = this.storeService
				.getObjById(CommUtil.null2Long(store_id));
		if (store != null) {
			if (store.getTemplate() != null && !store.getTemplate().equals("")) {
				template = store.getTemplate();
			}
			ModelAndView mv = new JModelAndView(template + "/goods_list.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			GoodsQueryObject gqo = new GoodsQueryObject(currentPage, mv, "", "");
			gqo.addQuery("obj.goods_store.id", new SysMap("goods_store_id",
					store.getId()), "=");
			if (ugc != null) {
				Set<Long> ids = this.genericUserGcIds(ugc);
				List<UserGoodsClass> ugc_list = new ArrayList<UserGoodsClass>();
				for (Long g_id : ids) {
					UserGoodsClass temp_ugc = this.userGoodsClassService
							.getObjById(g_id);
					ugc_list.add(temp_ugc);
				}
				Map paras = new HashMap();
				paras.put("ugc", ugc);
				gqo.addQuery("(:ugc member of obj.goods_ugcs", paras);
				// gqo.addQuery("ugc", ugc, "obj.goods_ugcs", "member of");
				for (int i = 0; i < ugc_list.size(); i++) {
					// gqo.addQuery("ugc" + i, ugc_list.get(i),
					// "obj.goods_ugcs","member of", "or");
					paras.clear();
					if (i == ugc_list.size() - 1) {
						paras.put("ugc" + i, ugc_list.get(i));
						gqo.addQuery(" or :ugc" + i
								+ " member of obj.goods_ugcs)", paras);
					} else {
						paras.put("ugc" + i, ugc_list.get(i));
						gqo.addQuery(" or :ugc" + i
								+ " member of obj.goods_ugcs", paras);
					}
				}
			} else {
				ugc = new UserGoodsClass();
				ugc.setClassName("全部商品");
				mv.addObject("ugc", ugc);
			}

			if (orderBy != null && !orderBy.equals("")) {
				gqo.setOrderBy(orderBy);
				mv.addObject("orderBy", orderBy);
				gqo.setOrderType(orderType);
				mv.addObject("orderType", orderType);
			}
			gqo.addQuery("obj.goods_status", new SysMap("goods_status", 0), "=");
			gqo.setPageSize(18);
			System.out.println(gqo.getQuery());
			IPageList pList = this.goodsService.list(gqo);
			String url = this.configService.getSysConfig().getAddress();
			CommUtil.saveIPageList2ModelAndView(url + "/goods_list.htm", "",
					"", pList, mv);
			mv.addObject("ugc", ugc);
			mv.addObject("ugc_id", ugc_id);
			mv.addObject("store", store);
			mv.addObject("goodsViewTools", goodsViewTools);
			mv.addObject("storeViewTools", storeViewTools);
			mv.addObject("areaViewTools", areaViewTools);
			Map params = new HashMap();
			params.put("user_id", store.getUser().getId());
			params.put("display", true);
			List<UserGoodsClass> ugcs = this.userGoodsClassService
					.query("select obj from UserGoodsClass obj where obj.user.id=:user_id and obj.display=:display and obj.parent.id is null order by obj.sequence asc",
							params, -1, -1);
			mv.addObject("ugcs", ugcs);// 店内分类
			params.clear();
			params.put("store_id", store.getId());
			params.put("goods_status", 0);
			List<Goods> hotgoods = this.goodsService
					.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status order by obj.goods_salenum desc",
							params, 0, 5);
			mv.addObject("hotgoods", hotgoods);// 热销排行
			params.clear();
			params.put("store_id", store.getId());
			params.put("display", true);
			List<StoreNavigation> navs = this.storenavigationService
					.query("select obj from StoreNavigation obj where obj.store.id=:store_id and obj.display =:display order by obj.sequence asc ",
							params, -1, -1);
			mv.addObject("navs", navs);// 导航栏
			this.generic_evaluate(store, mv);// 店铺评分信息
			mv.addObject("userTools", userTools);
			return mv;
		} else {
			ModelAndView mv = new JModelAndView("error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "请求参数错误");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
			return mv;
		}
	}

	private Set<Long> genericUserGcIds(UserGoodsClass ugc) {
		Set<Long> ids = new HashSet<Long>();
		ids.add(ugc.getId());
		for (UserGoodsClass child : ugc.getChilds()) {
			Set<Long> cids = genericUserGcIds(child);
			for (Long cid : cids) {
				ids.add(cid);
			}
			ids.add(child.getId());
		}
		return ids;
	}

	/**
	 * 查看店铺商品详细信息
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping("/goods.htm")
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
				//登录用户记录浏览足迹信息-begin
				Map footParams = new HashMap();
				footParams.put("fp_date", CommUtil.formatDate(CommUtil
						.formatShortDate(new Date())));
				footParams.put("fp_user_id", current_user.getId());
				List<FootPoint> fps = this.footPointService
						.query("select obj from FootPoint obj where obj.fp_date=:fp_date and obj.fp_user_id=:fp_user_id",
								footParams, -1, -1);
				if (fps.size() == 0) {
					FootPoint fp = new FootPoint();
					fp.setAddTime(new Date());
					fp.setFp_date(new Date());
					fp.setFp_user_id(current_user.getId());
					fp.setFp_user_name(current_user.getUsername());
					fp.setFp_goods_count(1);
					Map map = new HashMap();
					map.put("goods_id", obj.getId());
					map.put("goods_name", obj.getGoods_name());
					map.put("goods_sale", obj.getGoods_salenum());
					map.put("goods_time", CommUtil.formatLongDate(new Date()));
					map.put("goods_img_path", obj.getGoods_main_photo() != null
							? CommUtil.getURL(request) + "/"
									+ obj.getGoods_main_photo().getPath() + "/"
									+ obj.getGoods_main_photo().getName()
							: CommUtil.getURL(request)
									+ "/"
									+ this.configService.getSysConfig()
											.getGoodsImage().getPath()
									+ "/"
									+ this.configService.getSysConfig()
											.getGoodsImage().getName());
					map.put("goods_price", obj.getGoods_current_price());
					map.put("goods_class_id",
							CommUtil.null2Long(obj.getGc().getId()));
					map.put("goods_class_name",
							CommUtil.null2String(obj.getGc().getClassName()));
					List<Map> list = new ArrayList<Map>();
					list.add(map);
					fp.setFp_goods_content(Json.toJson(list,
							JsonFormat.compact()));
					this.footPointService.save(fp);
			} else {
				FootPoint fp = fps.get(0);
				List<Map> list = Json.fromJson(List.class,
						fp.getFp_goods_content());
				boolean add = true;
				for (Map map : list) {// 排除重复的商品足迹
					if (CommUtil.null2Long(map.get("goods_id")).equals(
							obj.getId())) {
						add = false;
					}
				}
				if (add) {
					Map map = new HashMap();
					map.put("goods_id", obj.getId());
					map.put("goods_name", obj.getGoods_name());
					map.put("goods_sale", obj.getGoods_salenum());
					map.put("goods_time",
							CommUtil.formatLongDate(new Date()));
					map.put("goods_img_path",
							obj.getGoods_main_photo() != null
									? CommUtil.getURL(request)
											+ "/"
											+ obj.getGoods_main_photo()
													.getPath()
											+ "/"
											+ obj.getGoods_main_photo()
													.getName() : CommUtil
											.getURL(request)
											+ "/"
											+ this.configService
													.getSysConfig()
													.getGoodsImage()
													.getPath()
											+ "/"
											+ this.configService
													.getSysConfig()
													.getGoodsImage()
													.getName());
					map.put("goods_price", obj.getGoods_current_price());
					map.put("goods_class_id",
							CommUtil.null2Long(obj.getGc().getId()));
					map.put("goods_class_name", CommUtil.null2String(obj
							.getGc().getClassName()));
					list.add(0, map);// 后浏览的总是插入最前面
					fp.setFp_goods_count(list.size());
					fp.setFp_goods_content(Json.toJson(list,
							JsonFormat.compact()));
					this.footPointService.update(fp);
				}
			}
		}
	    //登录用户记录浏览足迹信息-end
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
				Map params = new HashMap();
				params.put("level", 1);
				List<Area> areas = this.areaService
						.query("select obj from Area obj where obj.level=:level order by obj.sequence asc",
								params, -1, -1);
				mv.addObject("areas", areas);
				// 相关分类
				params.clear();
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
					Map params = new HashMap();
					params.put("level", 1);
					List<Area> areas = this.areaService
							.query("select obj from Area obj where obj.level=:level order by obj.sequence asc",
									params, -1, -1);
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
			Long plateIdBottom = obj.getPlateid_bottom();
			Long plateIdTop = obj.getPlateid_top();
			Plate topPlate = plateService.getObjById(plateIdTop);
			Plate bottomPlate = plateService.getObjById(plateIdBottom);
			mv.addObject("topPlate", topPlate);
			mv.addObject("bottomPlate", bottomPlate);
			mv.addObject("adver", " <img src="+CommUtil.getURL(request)+"/upload/goods/adver.jpg>");
	        String progress ="<img src="+CommUtil.getURL(request)+"/upload/goods/progress_01.jpg align='middle' ><br>";
	        progress +="<img src="+CommUtil.getURL(request)+"/upload/goods/progress_02.jpg border='0' align='middle' usemap='#Map'><br>";
	        progress +="<map name='Map' id='Map'>";
	        progress +="<area shape='rect' coords='621,439,938,479' href='"+CommUtil.getURL(request)+"/articlelist_help_7.htm' target='_blank'/>";
	        progress +="<area shape='rect' coords='378,445,574,474' href='tencent://message/?Menu=yes&amp;uin=1273685323&amp;Service=300&amp;sigT=45a1e5847943b64c6ff3990f8a9e644d2b31356cb0b4ac6b24663a3c8dd0f8aa12a595b1714f9d45' target='_blank' /></map>";
	        progress +="<img src="+CommUtil.getURL(request)+"/upload/goods/progress_03.jpg border='0' align='middle' usemap='#Map2' /><map name='Map2' id='Map2'>";
	        progress +=" <area shape='rect' coords='382,89,584,131' href='"+CommUtil.getURL(request)+"/articlelist_help_4.htm' target='_blank'/></map>";
	        mv.addObject("progress",progress);
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
	 * 根据商城分类查看商品列表
	 * 
	 * @param request
	 * @param response
	 * @param gc_id
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@RequestMapping("/store_goods_list.htm")
	public ModelAndView store_goods_list(HttpServletRequest request,
			HttpServletResponse response, String gc_id, String currentPage,
			String orderBy, String orderType, String brand_ids, String gs_ids,
			String properties, String all_property_status,
			String detail_property_status, String goods_type,
			String goods_inventory) {
		ModelAndView mv = new JModelAndView("store_goods_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		GoodsClass gc = this.goodsClassService.getObjById(CommUtil
				.null2Long(gc_id));
		mv.addObject("gc", gc);
		Set gc_list = new TreeSet();
		if (gc != null) {
			if (gc.getLevel() == 0) {
				gc_list = gc.getChilds();
			} else if (gc.getLevel() == 1) {
				gc_list = gc.getParent().getChilds();
			} else if (gc.getLevel() == 2) {
				gc_list = gc.getParent().getParent().getChilds();
			}
		}
		mv.addObject("gc_list", gc_list);
		if (orderBy == null || orderBy.equals("")) {
			orderBy = "addTime";
		}
		if (orderType == null || orderType.equals("")) {
			orderType = "desc";
		}
		GoodsQueryObject gqo = new GoodsQueryObject(currentPage, mv, orderBy,
				orderType);
		Set<Long> ids = this.genericIds(gc);
		if (ids != null && ids.size() > 0) {
			Map paras = new HashMap();
			paras.put("ids", ids);
			gqo.addQuery("obj.gc.id in (:ids)", paras);
		}
		gqo.setPageSize(24);// 设定分页查询，每页24件商品
		gqo.addQuery("obj.goods_status", new SysMap("goods_status", 0), "=");
		List<Map> goods_property = new ArrayList<Map>();
		if (!CommUtil.null2String(brand_ids).equals("")) {
			if (brand_ids.indexOf(",") < 0) {
				brand_ids = brand_ids + ",";
			}
			String[] brand_id_list = brand_ids.split(",");
			if (brand_id_list.length == 1) {
				String brand_id = brand_id_list[0];
				gqo.addQuery("obj.goods_brand.id", new SysMap("brand_id",
						CommUtil.null2Long(brand_id)), "=", "and");
				Map map = new HashMap();
				GoodsBrand brand = this.brandService.getObjById(CommUtil
						.null2Long(brand_id));
				map.put("name", "品牌");
				map.put("value", brand.getName());
				map.put("type", "brand");
				map.put("id", brand.getId());
				goods_property.add(map);
			} else {
				for (int i = 0; i < brand_id_list.length; i++) {
					String brand_id = brand_id_list[i];
					if (i == 0) {
						gqo.addQuery(
								"and (obj.goods_brand.id="
										+ CommUtil.null2Long(brand_id), null);
						Map map = new HashMap();
						GoodsBrand brand = this.brandService
								.getObjById(CommUtil.null2Long(brand_id));
						map.put("name", "品牌");
						map.put("value", brand.getName());
						map.put("type", "brand");
						map.put("id", brand.getId());
						goods_property.add(map);
					} else if (i == brand_id_list.length - 1) {
						gqo.addQuery(
								"or obj.goods_brand.id="
										+ CommUtil.null2Long(brand_id) + ")",
								null);
						Map map = new HashMap();
						GoodsBrand brand = this.brandService
								.getObjById(CommUtil.null2Long(brand_id));
						map.put("name", "品牌");
						map.put("value", brand.getName());
						map.put("type", "brand");
						map.put("id", brand.getId());
						goods_property.add(map);
					} else {
						gqo.addQuery(
								"or obj.goods_brand.id="
										+ CommUtil.null2Long(brand_id), null);
						Map map = new HashMap();
						GoodsBrand brand = this.brandService
								.getObjById(CommUtil.null2Long(brand_id));
						map.put("name", "品牌");
						map.put("value", brand.getName());
						map.put("type", "brand");
						map.put("id", brand.getId());
						goods_property.add(map);
					}
				}
			}
			if (brand_ids != null && !brand_ids.equals("")) {
				mv.addObject("brand_ids", brand_ids);
			}
		}
		if (!CommUtil.null2String(gs_ids).equals("")) {
			List<List<GoodsSpecProperty>> gsp_lists = this.generic_gsp(gs_ids);
			for (int j = 0; j < gsp_lists.size(); j++) {
				List<GoodsSpecProperty> gsp_list = gsp_lists.get(j);
				if (gsp_list.size() == 1) {
					GoodsSpecProperty gsp = gsp_list.get(0);
					gqo.addQuery("gsp" + j, gsp, "obj.goods_specs",
							"member of", "and");
					Map map = new HashMap();
					map.put("name", gsp.getSpec().getName());
					map.put("value", gsp.getValue());
					map.put("type", "gs");
					map.put("id", gsp.getId());
					goods_property.add(map);
				} else {
					for (int i = 0; i < gsp_list.size(); i++) {
						if (i == 0) {
							GoodsSpecProperty gsp = gsp_list.get(i);
							gqo.addQuery("gsp" + j + i, gsp, "obj.goods_specs",
									"member of", "and(");
							Map map = new HashMap();
							map.put("name", gsp.getSpec().getName());
							map.put("value", gsp.getValue());
							map.put("type", "gs");
							map.put("id", gsp.getId());
							goods_property.add(map);
						} else if (i == gsp_list.size() - 1) {
							GoodsSpecProperty gsp = gsp_list.get(i);
							gqo.addQuery("gsp" + j + i, gsp,
									"obj.goods_specs)", "member of", "or");
							Map map = new HashMap();
							map.put("name", gsp.getSpec().getName());
							map.put("value", gsp.getValue());
							map.put("type", "gs");
							map.put("id", gsp.getId());
							goods_property.add(map);
						} else {
							GoodsSpecProperty gsp = gsp_list.get(i);
							gqo.addQuery("gsp" + j + i, gsp, "obj.goods_specs",
									"member of", "or");
							Map map = new HashMap();
							map.put("name", gsp.getSpec().getName());
							map.put("value", gsp.getValue());
							map.put("type", "gs");
							map.put("id", gsp.getId());
							goods_property.add(map);
						}
					}
				}
			}
			mv.addObject("gs_ids", gs_ids);
		}
		if (!CommUtil.null2String(properties).equals("")) {
			String[] properties_list = properties.substring(1).split("\\|");
			for (int i = 0; i < properties_list.length; i++) {
				String property_info = properties_list[i];
				String[] property_info_list = property_info.split(",");
				GoodsTypeProperty gtp = this.goodsTypePropertyService
						.getObjById(CommUtil.null2Long(property_info_list[0]));
				Map p_map = new HashMap();
				p_map.put("gtp_name" + i, "%" + gtp.getName().trim() + "%");
				p_map.put("gtp_value" + i, "%" + property_info_list[1].trim()
						+ "%");
				gqo.addQuery("and (obj.goods_property like :gtp_name" + i
						+ " and obj.goods_property like :gtp_value" + i + ")",
						p_map);
				Map map = new HashMap();
				map.put("name", gtp.getName());
				map.put("value", property_info_list[1]);
				map.put("type", "properties");
				map.put("id", gtp.getId());
				goods_property.add(map);
			}
			mv.addObject("properties", properties);
			// 处理筛选类型互斥,|1,超短裙（小于75cm）|2,纯色
			List<GoodsTypeProperty> filter_properties = new ArrayList<GoodsTypeProperty>();
			List<String> hc_property_list = new ArrayList<String>();// 已经互斥处理过的属性值，在循环中不再处理
			if (gc.getGoodsType() != null) {
				for (GoodsTypeProperty gtp : gc.getGoodsType().getProperties()) {
					// System.out.println(gtp.getName() + "," + gtp.getValue());
					boolean flag = true;
					GoodsTypeProperty gtp1 = new GoodsTypeProperty();
					gtp1.setDisplay(gtp.isDisplay());
					gtp1.setGoodsType(gtp.getGoodsType());
					gtp1.setHc_value(gtp.getHc_value());
					gtp1.setId(gtp.getId());
					gtp1.setName(gtp.getName());
					gtp1.setSequence(gtp.getSequence());
					gtp1.setValue(gtp.getValue());
					System.out.println("原始：" + gtp1.getValue());
					for (String hc_property : hc_property_list) {
						String[] hc_list = hc_property.split(":");
						if (hc_list[0].equals(gtp.getName())) {
							String[] hc_temp_list = hc_list[1].split(",");
							String[] defalut_list_value = gtp1.getValue()
									.split(",");
							ArrayList<String> defalut_list = new ArrayList<String>(
									Arrays.asList(defalut_list_value));
							for (String hc_temp : hc_temp_list) {
								defalut_list.remove(hc_temp);
							}
							String value = "";
							for (int i = defalut_list.size() - 1; i >= 0; i--) {
								value = defalut_list.get(i) + "," + value;
							}
							System.out.println(value);
							gtp1.setValue(value.substring(0, value.length() - 1));
							flag = false;
							break;
						}

					}
					if (flag) {
						if (!CommUtil.null2String(gtp.getHc_value()).equals("")) {// 取消互斥类型
							// System.out.println(gtp.getHc_value());
							String[] list1 = gtp.getHc_value().split("#");
							for (int i = 0; i < properties_list.length; i++) {
								String property_info = properties_list[i];
								String[] property_info_list = CommUtil
										.null2String(property_info).split(",");
								if (property_info_list[1].equals(list1[0])) {// 存在该互斥，则需要进行处理
									hc_property_list.add(list1[1]);
								}
							}

						}
						filter_properties.add(gtp);
					} else {
						filter_properties.add(gtp1);
					}
				}
				mv.addObject("filter_properties", filter_properties);
			}
		} else {
			// 处理筛选类型互斥
			mv.addObject("filter_properties", gc.getGoodsType() != null ? gc
					.getGoodsType().getProperties() : "");
		}
		if (CommUtil.null2Int(goods_inventory) == 0) {// 查询库存大于0
			gqo.addQuery("obj.goods_inventory",
					new SysMap("goods_inventory", 0), ">");
		}
		if (!CommUtil.null2String(goods_type).equals("")
				&& CommUtil.null2Int(goods_type) != -1) {// 查询自营或者第三方经销商商品
			gqo.addQuery("obj.goods_type",
					new SysMap("goods_type", CommUtil.null2Int(goods_type)),
					"=");
		}
		IPageList pList = this.goodsService.list(gqo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("gc", gc);
		mv.addObject("orderBy", orderBy);
		mv.addObject("goods_property", goods_property);
		mv.addObject("allCount", pList.getRowCount());
		// 如果系统开启直通车，商品列表页顶部推荐热卖商品及左侧推广商品均显示直通车商品
		if (this.configService.getSysConfig().isZtc_status()) {
			// 页面左侧10条数据，从第3位开始查询
			List<Goods> left_ztc_goods = null;
			Map ztc_map = new HashMap();
			ztc_map.put("ztc_status", 3);
			ztc_map.put("now_date", new Date());
			ztc_map.put("ztc_gold", 0);
			if (this.configService.getSysConfig().getZtc_goods_view() == 0) {
				// 获取所有商品数量，查询出的对象为只有id的对象，减少查询压力,查询所有直通车商品，随机显示出指定数量
				List<Goods> all_left_ztc_goods = this.goodsService
						.query("select obj.id from Goods obj where obj.ztc_status =:ztc_status "
								+ "and obj.ztc_begin_time <=:now_date and obj.ztc_gold>:ztc_gold "
								+ "order by obj.ztc_dredge_price desc",
								ztc_map, -1, -1);
				left_ztc_goods = this.goodsService
						.query("select obj from Goods obj where obj.ztc_status =:ztc_status "
								+ "and obj.ztc_begin_time <=:now_date and obj.ztc_gold>:ztc_gold "
								+ "order by obj.ztc_dredge_price desc",
								ztc_map, 3, all_left_ztc_goods.size());
				left_ztc_goods = this.goodsViewTools.randomZtcGoods2(
						left_ztc_goods, 10);
			}
			if (this.configService.getSysConfig().getZtc_goods_view() == 1) {
				ztc_map.put("gc_ids", ids);
				// 获取所有商品数量，查询出的对象为只有id的对象，减少查询压力,查询所有直通车商品，随机显示出指定数量
				List<Goods> all_left_ztc_goods = this.goodsService
						.query("select obj.id from Goods obj where obj.ztc_status =:ztc_status "
								+ "and obj.ztc_begin_time <=:now_date and obj.ztc_gold>:ztc_gold and obj.gc.id in (:gc_ids) "
								+ "order by obj.ztc_dredge_price desc",
								ztc_map, -1, -1);
				left_ztc_goods = this.goodsService
						.query("select obj from Goods obj where obj.ztc_status =:ztc_status "
								+ "and obj.ztc_begin_time <=:now_date and obj.ztc_gold>:ztc_gold and obj.gc.id in (:gc_ids) "
								+ "order by obj.ztc_dredge_price desc",
								ztc_map, 3, all_left_ztc_goods.size());
				left_ztc_goods = this.goodsViewTools.randomZtcGoods2(
						left_ztc_goods, 10);
			}
			mv.addObject("left_ztc_goods", left_ztc_goods);
			// 页面顶部,直通车前3个商品
			List<Goods> top_ztc_goods = null;
			Map ztc_map2 = new HashMap();
			ztc_map2.put("ztc_status", 3);
			ztc_map2.put("now_date", new Date());
			ztc_map2.put("ztc_gold", 0);
			if (this.configService.getSysConfig().getZtc_goods_view() == 0) {
				top_ztc_goods = this.goodsService
						.query("select obj from Goods obj where obj.ztc_status =:ztc_status "
								+ "and obj.ztc_begin_time <=:now_date and obj.ztc_gold>:ztc_gold "
								+ "order by obj.ztc_dredge_price desc",
								ztc_map2, 0, 3);
			}
			if (this.configService.getSysConfig().getZtc_goods_view() == 1) {
				ztc_map2.put("gc_ids", ids);
				top_ztc_goods = this.goodsService
						.query("select obj from Goods obj where obj.ztc_status =:ztc_status "
								+ "and obj.ztc_begin_time <=:now_date and obj.ztc_gold>:ztc_gold and obj.gc.id in (:gc_ids) "
								+ "order by obj.ztc_dredge_price desc",
								ztc_map2, 0, 3);
			}
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
					left_ztc_goods, 10);
			mv.addObject("left_ztc_goods", left_ztc_goods);
		}
		if (detail_property_status != null
				&& !detail_property_status.equals("")) {
			mv.addObject("detail_property_status", detail_property_status);
			String temp_str[] = detail_property_status.split(",");
			Map pro_map = new HashMap();
			List pro_list = new ArrayList();
			for (String property_status : temp_str) {
				if (property_status != null && !property_status.equals("")) {
					String mark[] = property_status.split("_");
					pro_map.put(mark[0], mark[1]);
					pro_list.add(mark[0]);
				}
			}
			mv.addObject("pro_list", pro_list);
			mv.addObject("pro_map", pro_map);
		}
		mv.addObject("all_property_status", all_property_status);
		// 计算当期访问用户的IP地址，并计算对应的运费信息
		String current_ip = CommUtil.getIpAddr(request);// 获得本机IP
		if (CommUtil.isIp(current_ip)) {
			IPSeeker ip = new IPSeeker(null, null);
			String current_city = ip.getIPLocation(current_ip).getCountry();
			mv.addObject("current_city", current_city);
		} else {
			mv.addObject("current_city", "未知地区");
		}

		// 查询运费地区
		List<Area> areas = this.areaService
				.query("select obj from Area obj where obj.parent.id is null order by obj.sequence asc",
						null, -1, -1);
		mv.addObject("areas", areas);
		mv.addObject("goods_inventory", CommUtil.null2Int(goods_inventory));
		mv.addObject(
				"goods_type",
				CommUtil.null2String(goods_type).equals("") ? -1 : CommUtil
						.null2Int(goods_type));
		mv.addObject("userTools", userTools);
		return mv;
	}

	private Set<Long> getArrayAreaChildIds(List<Area> areas) {
		Set<Long> ids = new HashSet<Long>();
		for (Area area : areas) {
			ids.add(area.getId());
			for (Area are : area.getChilds()) {
				Set<Long> cids = getAreaChildIds(are);
				for (Long cid : cids) {
					ids.add(cid);
				}
			}
		}
		return ids;
	}

	/**
	 * 底部根据流程猜你喜欢商品列表，
	 * 使用自定义标签$!httpInclude.include("/ztc_goods_left.htm?size=8") 完成页面引用
	 */
	@RequestMapping("/goods_list_bottom.htm")
	public ModelAndView goods_list_bottom(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("goods_list_bottom.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		// 猜您喜欢 根据cookie商品的分类 销量查询 如果没有cookie则按销量查询
		List<Goods> your_like_goods = this.goodsService.queryActionGoods();
		if(your_like_goods ==null ||your_like_goods.size()==0){
			Long your_like_GoodsClass = null;
			Cookie[] cookies = request.getCookies();
			if (cookies != null) {
				for (Cookie cookie : cookies) {
					if (cookie.getName().equals("goodscookie")) {
						String[] like_gcid = cookie.getValue().split(",", 2);
						Goods goods = this.goodsService.getObjById(CommUtil
								.null2Long(like_gcid[0]));
						if (goods == null)
							break;
						your_like_GoodsClass = goods.getGc().getId();
						your_like_goods = this.goodsService.query(
								"select obj from Goods obj where obj.goods_status=0 and obj.gc.id = "
										+ your_like_GoodsClass
										+ " and obj.id is not " + goods.getId()
										+ " order by obj.goods_salenum desc", null,
								0, 20);
						int gcs_size = your_like_goods.size();
						if (gcs_size < 20) {
							List<Goods> like_goods = this.goodsService.query(
									"select obj from Goods obj where obj.goods_status=0 and obj.id is not "
											+ goods.getId()
											+ " order by obj.goods_salenum desc",
									null, 0, 20 - gcs_size);
							for (int i = 0; i < like_goods.size(); i++) {
								// 去除重复商品
								int k = 0;
								for (int j = 0; j < your_like_goods.size(); j++) {
									if (like_goods.get(i).getId()
											.equals(your_like_goods.get(j).getId())) {
										k++;
									}
								}
								if (k == 0) {
									your_like_goods.add(like_goods.get(i));
								}
							}
						}
						break;
					} else {
						your_like_goods = this.goodsService
								.query("select obj from Goods obj where obj.goods_status=0 order by obj.goods_salenum desc",
										null, 0, 20);
					}
				}
			} else {
				your_like_goods = this.goodsService
						.query("select obj from Goods obj where obj.goods_status=0 order by obj.goods_salenum desc",
								null, 0, 20);
			}
		}
		mv.addObject("your_like_goods", your_like_goods);

		User user = null;
		if (SecurityUserHolder.getCurrentUser() != null) {
			user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
			mv.addObject("user", user);
		}
		List<Goods> goods_last = new ArrayList<Goods>();
		Cookie[] cookies_last = request.getCookies();
		if (cookies_last != null) {
			for (Cookie co : cookies_last) {
				if (co.getName().equals("goodscookie")) {
					String[] goods_id = co.getValue().split(",");
					int j = 4;
					if (j > goods_id.length) {
						j = goods_id.length;
					}
					for (int i = 0; i < j; i++) {
						Goods goods = new Goods();
						goods = this.goodsService.getObjById(CommUtil
								.null2Long(goods_id[i]));
						if (goods != null)
							goods_last.add(goods);
					}
				}
			}
		}
		mv.addObject("goods_last", goods_last);
		return mv;
	}

	private Set<Long> getAreaChildIds(Area area) {
		Set<Long> ids = new HashSet<Long>();
		ids.add(area.getId());
		for (Area are : area.getChilds()) {
			Set<Long> cids = getAreaChildIds(are);
			for (Long cid : cids) {
				ids.add(cid);
			}
		}
		return ids;
	}

	private List<List<GoodsSpecProperty>> generic_gsp(String gs_ids) {
		List<List<GoodsSpecProperty>> list = new ArrayList<List<GoodsSpecProperty>>();
		String[] gs_id_list = gs_ids.substring(1).split("\\|");
		for (String gd_id_info : gs_id_list) {
			String[] gs_info_list = gd_id_info.split(",");
			GoodsSpecProperty gsp = this.goodsSpecPropertyService
					.getObjById(CommUtil.null2Long(gs_info_list[0]));
			boolean create = true;
			for (List<GoodsSpecProperty> gsp_list : list) {
				for (GoodsSpecProperty gsp_temp : gsp_list) {
					if (gsp_temp.getSpec().getId()
							.equals(gsp.getSpec().getId())) {
						gsp_list.add(gsp);
						create = false;
						break;
					}
				}
			}
			if (create) {
				List<GoodsSpecProperty> gsps = new ArrayList<GoodsSpecProperty>();
				gsps.add(gsp);
				list.add(gsps);
			}
		}
		return list;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param goods_id
	 * @param currentPage
	 * @return
	 */
	@RequestMapping("/goods_evaluation.htm")
	public ModelAndView goods_evaluation(HttpServletRequest request,
			HttpServletResponse response, String id, String goods_id,
			String currentPage) {
		String template = "default";
		Store store = this.storeService.getObjById(CommUtil.null2Long(id));
		if (store != null) {
			template = store.getTemplate();
		}
		ModelAndView mv = new JModelAndView(
				template + "/goods_evaluation.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		EvaluateQueryObject qo = new EvaluateQueryObject(currentPage, mv,
				"addTime", "desc");
		qo.addQuery("obj.evaluate_goods.id",
				new SysMap("goods_id", CommUtil.null2Long(goods_id)), "=");
		qo.addQuery("obj.evaluate_type", new SysMap("evaluate_type", "goods"),
				"=");
		qo.addQuery("obj.evaluate_status", new SysMap("evaluate_status", 0),
				"=");
		qo.setPageSize(8);
		IPageList pList = this.evaluateService.list(qo);
		CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request)
				+ "/goods_evaluation.htm", "", "", pList, mv);
		mv.addObject("storeViewTools", storeViewTools);
		mv.addObject("store", store);
		Goods goods = this.goodsService
				.getObjById(CommUtil.null2Long(goods_id));
		mv.addObject("goods", goods);
		return mv;
	}

	@RequestMapping("/goods_detail.htm")
	public ModelAndView goods_detail(HttpServletRequest request,
			HttpServletResponse response, String goods_id) {
		ModelAndView mv = new JModelAndView("default/goods_detail.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Goods goods = this.goodsService
				.getObjById(CommUtil.null2Long(goods_id));
		mv.addObject("obj", goods);
		Long plateIdBottom = goods.getPlateid_bottom();
		Long plateIdTop = goods.getPlateid_top();
		Plate topPlate = plateService.getObjById(plateIdTop);
		Plate bottomPlate = plateService.getObjById(plateIdBottom);
		mv.addObject("topPlate", topPlate);
		mv.addObject("bottomPlate", bottomPlate);
		mv.addObject("adver", " <img src="+CommUtil.getURL(request)+"/upload/goods/adver.jpg>");
        String progress ="<img src="+CommUtil.getURL(request)+"/upload/goods/progress_01.jpg align='middle' ><br>";
        progress +="<img src="+CommUtil.getURL(request)+"/upload/goods/progress_02.jpg border='0' align='middle' usemap='#Map'><br>";
        progress +="<map name='Map' id='Map'>";
        progress +="<area shape='rect' coords='621,439,938,479' href='"+CommUtil.getURL(request)+"/articlelist_help_7.htm' target='_blank'/>";
        progress +="<area shape='rect' coords='378,445,574,474' href='tencent://message/?Menu=yes&amp;uin=1273685323&amp;Service=300&amp;sigT=45a1e5847943b64c6ff3990f8a9e644d2b31356cb0b4ac6b24663a3c8dd0f8aa12a595b1714f9d45' target='_blank' /></map>";
        progress +="<img src="+CommUtil.getURL(request)+"/upload/goods/progress_03.jpg border='0' align='middle' usemap='#Map2' /><map name='Map2' id='Map2'>";
        progress +=" <area shape='rect' coords='382,89,584,131' href='"+CommUtil.getURL(request)+"/articlelist_help_4.htm' target='_blank'/></map>";
        mv.addObject("progress",progress);
    	  return mv;
	}

	@RequestMapping("/goods_order.htm")
	public ModelAndView goods_order(HttpServletRequest request,
			HttpServletResponse response, String id, String goods_id,
			String currentPage) {
		String template = "default";
		Store store = this.storeService.getObjById(CommUtil.null2Long(id));
		if (store != null) {
			template = store.getTemplate();
		}
		ModelAndView mv = new JModelAndView(template + "/goods_order.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		EvaluateQueryObject qo = new EvaluateQueryObject(currentPage, mv,
				"addTime", "desc");
		qo.addQuery("obj.evaluate_goods.id",
				new SysMap("goods_id", CommUtil.null2Long(goods_id)), "=");
		qo.setPageSize(8);
		IPageList pList = this.evaluateService.list(qo);
		CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request)
				+ "/goods_order.htm", "", "", pList, mv);
		
		System.out.println("==============size:" + pList.getResult());

		return mv;
	}

	@RequestMapping("/goods_consult.htm")
	public ModelAndView goods_consult(HttpServletRequest request,
			HttpServletResponse response, String id, String goods_id,
			String currentPage) {
		ModelAndView mv = new JModelAndView("default/goods_consult.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		ConsultQueryObject qo = new ConsultQueryObject(currentPage, mv,
				"addTime", "desc");
		qo.addQuery("obj.goods.id",
				new SysMap("goods_id", CommUtil.null2Long(goods_id)), "=");
		IPageList pList = this.consultService.list(qo);
		CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request)
				+ "/goods_consult.htm", "", "", pList, mv);
		mv.addObject("storeViewTools", storeViewTools);
		mv.addObject("goods_id", goods_id);
		return mv;
	}

	@RequestMapping("/goods_consult_save.htm")
	@Transactional
	public ModelAndView goods_consult_save(HttpServletRequest request,
			HttpServletResponse response, String goods_id,
			String consult_content, String consult_email, String Anonymous,
			String consult_code) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String verify_code = CommUtil.null2String(request.getSession(false)
				.getAttribute("consult_code"));
		boolean visit_consult = true;
		String msg = "咨询发布成功";
		if (!this.configService.getSysConfig().isVisitorConsult()) {
			if (SecurityUserHolder.getCurrentUser() == null) {
				visit_consult = false;
				msg = "不允许游客发布";
			}
			if (CommUtil.null2Boolean(Anonymous)) {
				visit_consult = false;
				msg = "不允许游客发布";
			}
		}
		if (this.configService.getSysConfig().isSecurityCodeConsult()) {
			if (!CommUtil.null2String(consult_code).equals(verify_code)) {
				visit_consult = false;
				msg = "验证码错误";
			}
		}
		if (visit_consult) {
			Consult obj = new Consult();
			obj.setAddTime(new Date());
			obj.setConsult_content(consult_content);
			obj.setConsult_email(consult_email);
			if (!CommUtil.null2Boolean(Anonymous)) {
				obj.setConsult_user(SecurityUserHolder.getCurrentUser());
			}
			obj.setGoods(this.goodsService.getObjById(CommUtil
					.null2Long(goods_id)));
			this.consultService.save(obj);
			request.getSession(false).removeAttribute("consult_code");
			mv.addObject("op_title", msg);
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", msg);
		}
		mv.addObject("url", CommUtil.getURL(request) + "/goods_" + goods_id
				+ ".htm");
		return mv;
	}

	@RequestMapping("/load_goods_gsp.htm")
	public void load_goods_gsp(HttpServletRequest request,
			HttpServletResponse response, String gsp, String id) {
		Goods goods = this.goodsService.getObjById(CommUtil.null2Long(id));
		Map map = new HashMap();
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
			price = CommUtil.null2Double(goods.getStore_price());
			if (goods.getInventory_type().equals("spec")) {
				List<HashMap> list = Json.fromJson(ArrayList.class,
						goods.getGoods_inventory_detail());
				String[] gsp_ids = gsp.split(",");
				for (Map temp : list) {
					String[] temp_ids = CommUtil.null2String(temp.get("id"))
							.split("_");
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
		if (goods.getActivity_status() == 2
				&& SecurityUserHolder.getCurrentUser() != null) {// 如果是促销商品，并且用户已登录，根据规格配置价格计算相应配置的促销价格
			for (ActivityGoods ag : goods.getActivity_goods_list()) {
				if (ag.getAg_status() == 1 && ag.getAct().getAc_status() == 1) {
					temp_count++;
					if (temp_count > 0) {
						String level_name = integralViewTools
								.query_user_level_name(SecurityUserHolder
										.getCurrentUser().getId().toString());
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
		map.put("count", count);
		map.put("price", CommUtil.formatMoney(price));
		if (act_price != 0) {
			map.put("act_price", CommUtil.formatMoney(act_price));
		}
		// map.put("count", count);
		// map.put("price", price);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			System.out.println(Json.toJson(map, JsonFormat.compact()));
			writer.print(Json.toJson(map, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@RequestMapping("/trans_fee.htm")
	public void trans_fee(HttpServletRequest request,
			HttpServletResponse response, String city_name, String goods_id) {
		Map map = new HashMap();
		Goods goods = this.goodsService
				.getObjById(CommUtil.null2Long(goods_id));
		float mail_fee = 0;
		float express_fee = 0;
		float ems_fee = 0;
		if (goods.getTransport() != null) {
			mail_fee = this.transportTools.cal_goods_trans_fee(
					CommUtil.null2String(goods.getTransport().getId()), "mail",
					CommUtil.null2String(goods.getGoods_weight()),
					CommUtil.null2String(goods.getGoods_volume()), city_name);
			express_fee = this.transportTools.cal_goods_trans_fee(
					CommUtil.null2String(goods.getTransport().getId()),
					"express", CommUtil.null2String(goods.getGoods_weight()),
					CommUtil.null2String(goods.getGoods_volume()), city_name);
			ems_fee = this.transportTools.cal_goods_trans_fee(
					CommUtil.null2String(goods.getTransport().getId()), "ems",
					CommUtil.null2String(goods.getGoods_weight()),
					CommUtil.null2String(goods.getGoods_volume()), city_name);
		}
		map.put("mail_fee", mail_fee);
		map.put("express_fee", express_fee);
		map.put("ems_fee", ems_fee);
		map.put("current_city_info", CommUtil.substring(city_name, 5));
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

	@RequestMapping("/goods_share.htm")
	public ModelAndView goods_share(HttpServletRequest request,
			HttpServletResponse response, String goods_id) {
		ModelAndView mv = new JModelAndView("goods_share.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Goods goods = this.goodsService
				.getObjById(CommUtil.null2Long(goods_id));
		mv.addObject("obj", goods);
		return mv;
	}

	private Set<Long> genericIds(GoodsClass gc) {
		Set<Long> ids = new HashSet<Long>();
		if (gc != null) {
			ids.add(gc.getId());
			for (GoodsClass child : gc.getChilds()) {
				Set<Long> cids = genericIds(child);
				for (Long cid : cids) {
					ids.add(cid);
				}
				ids.add(child.getId());
			}
		}
		return ids;
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
}
