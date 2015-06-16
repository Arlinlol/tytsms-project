package com.iskyshop.view.web.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.StoreNavigation;
import com.iskyshop.foundation.domain.UserGoodsClass;
import com.iskyshop.foundation.domain.query.EvaluateQueryObject;
import com.iskyshop.foundation.domain.query.GoodsQueryObject;
import com.iskyshop.foundation.service.IEvaluateService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IStoreNavigationService;
import com.iskyshop.foundation.service.IStorePartnerService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserGoodsClassService;
import com.iskyshop.manage.admin.tools.UserTools;
import com.iskyshop.view.web.tools.AreaViewTools;
import com.iskyshop.view.web.tools.GoodsViewTools;
import com.iskyshop.view.web.tools.StoreViewTools;

/**
 * 
 * 
 * <p>
 * Title:StoreViewAction.java
 * </p>
 * 
 * <p>
 * Description: 前端店铺控制器
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
 * @author erikzhang、jy
 * 
 * @date 2014年4月24日
 * 
 * @version 1.0
 */
@Controller
public class StoreViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IUserGoodsClassService userGoodsClassService;
	@Autowired
	private IStoreNavigationService storenavigationService;
	@Autowired
	private IStorePartnerService storepartnerService;
	@Autowired
	private IEvaluateService evaluateService;
	@Autowired
	private AreaViewTools areaViewTools;
	@Autowired
	private GoodsViewTools goodsViewTools;
	@Autowired
	private UserTools userTools;
	@Autowired
	private StoreViewTools storeViewTools;

	/**
	 * 店铺首页
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping("/store.htm")
	public ModelAndView store(HttpServletRequest request,
			HttpServletResponse response, String id) {
		String serverName = request.getServerName().toLowerCase();
		String secondDomain = "";
		if(this.configService.getSysConfig().isSecond_domain_open()){
			secondDomain=serverName.substring(0, serverName.indexOf("."));
		}
		Store store = null;
		if (this.configService.getSysConfig().isSecond_domain_open()
				&& serverName.indexOf(".") != serverName.lastIndexOf(".")
				&& !secondDomain.equals("www")) {
			store = this.storeService.getObjByProperty("store_second_domain",
					secondDomain);
		} else {
			store = this.storeService.getObjById(CommUtil.null2Long(id));
		}
		if (store == null) {
			ModelAndView mv = new JModelAndView("error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "不存在该店铺信息");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
			return mv;
		} else {
			String template = "default";
			if (store.getTemplate() != null && !store.getTemplate().equals("")) {
				template = store.getTemplate();
			}
			ModelAndView mv = new JModelAndView(template + "/store_index.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			if (store.getStore_status() == 15) {
				this.add_store_common_info(mv, store);// 店铺商品信息
				this.generic_evaluate(store, mv);// 店铺信用信息
				mv.addObject("userTools", userTools);
			} else {
				mv = new JModelAndView("error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "店铺已经关闭或者未开通店铺");
				mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
			}
			return mv;
		}
	}

	/**
	 * 店铺导航
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/store_nav.htm")
	public ModelAndView store_nav(HttpServletRequest request,
			HttpServletResponse response) {
		Long id = CommUtil.null2Long(request.getAttribute("id"));
		Store store = this.storeService.getObjById(id);
		String template = "default";
		if (store.getTemplate() != null && !store.getTemplate().equals("")) {
			template = store.getTemplate();
		}
		ModelAndView mv = new JModelAndView(template + "/store_nav.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (store.getStore_status() == 15) {
			Map params = new HashMap();
			params.put("store_id", store.getId());
			params.put("display", true);
			List<StoreNavigation> navs = this.storenavigationService
					.query("select obj from StoreNavigation obj where obj.store.id=:store_id and obj.display=:display order by obj.sequence asc",
							params, -1, -1);
			mv.addObject("navs", navs);
			mv.addObject("store", store);
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "店铺信息错误");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		}
		return mv;
	}

	@RequestMapping("/store_eva.htm")
	public ModelAndView store_eva(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String eva_val) {
		Store store = this.storeService.getObjById(Long.parseLong(id));
		String template = "default";
		if (store.getTemplate() != null && !store.getTemplate().equals("")) {
			template = store.getTemplate();
		}
		ModelAndView mv = new JModelAndView(template + "/store_eva.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (store.getStore_status() == 15) {
			EvaluateQueryObject qo = new EvaluateQueryObject(currentPage, mv,
					"addTime", "desc");
			qo.addQuery("obj.evaluate_goods.goods_store.id", new SysMap(
					"store_id", store.getId()), "=");
			if (!CommUtil.null2String(eva_val).equals("")) {
				qo.addQuery("obj.evaluate_buyer_val", new SysMap(
						"evaluate_buyer_val", CommUtil.null2Int(eva_val)), "=");
			}
			IPageList pList = this.evaluateService.list(qo);
			CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request)
					+ "/store_eva.htm", "",
					"&eva_val=" + CommUtil.null2String(eva_val), pList, mv);
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "店铺信息错误");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		}
		return mv;
	}

	/**
	 * 店铺导航详情页
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/store_url.htm")
	public ModelAndView store_url(HttpServletRequest request,
			HttpServletResponse response, String id) {
		StoreNavigation nav = this.storenavigationService.getObjById(CommUtil
				.null2Long(id));
		String template = "default";
		if (nav.getStore().getTemplate() != null
				&& !nav.getStore().getTemplate().equals("")) {
			template = nav.getStore().getTemplate();
		}
		ModelAndView mv = new JModelAndView(template + "/store_url.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("store", nav.getStore());
		mv.addObject("nav", nav);
		mv.addObject("nav_id", nav.getId());
		this.add_store_common_info(mv, nav.getStore());// 店铺商品信息
		this.generic_evaluate(nav.getStore(), mv);// 店铺信用信息
		mv.addObject("userTools", userTools);
		return mv;
	}

	/**
	 * 加载店铺相关信息
	 * 
	 * @param mv
	 * @param store
	 */
	private void add_store_common_info(ModelAndView mv, Store store) {
		Map params = new HashMap();
		params.put("user_id", store.getUser().getId());
		params.put("display", true);
		List<UserGoodsClass> ugcs = this.userGoodsClassService
				.query("select obj from UserGoodsClass obj where obj.user.id=:user_id and obj.display=:display and obj.parent.id is null order by obj.sequence asc",
						params, -1, -1);
		mv.addObject("ugcs", ugcs);// 加载店内分类

		params.clear();
		params.put("store_id", store.getId());
		params.put("goods_status", 0);
		List<Goods> hotgoods = this.goodsService
				.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status order by obj.goods_salenum desc",
						params, 0, 5);
		mv.addObject("hotgoods", hotgoods);// 加载热销商品

		params.clear();
		params.put("recommend", true);
		params.put("goods_store_id", store.getId());
		params.put("goods_status", 0);
		List<Goods> goods_recommend = this.goodsService
				.query("select obj from Goods obj where obj.goods_recommend=:recommend and obj.goods_store.id=:goods_store_id and obj.goods_status=:goods_status order by obj.addTime desc",
						params, 0, 6);
		mv.addObject("goods_recommend", goods_recommend);// 加载推荐商品

		params.clear();
		params.put("store_id", store.getId());
		params.put("goods_status", 0);
		List<Goods> goods_collect = this.goodsService
				.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status order by obj.goods_collect desc",
						params, 0, 6);
		mv.addObject("goods_collect", goods_collect);// 加载人气商品

		params.clear();
		params.put("goods_store_id", store.getId());
		params.put("goods_status", 0);
		List<Goods> goods_new = this.goodsService
				.query("select obj from Goods obj where obj.goods_store.id=:goods_store_id and obj.goods_status=:goods_status order by obj.addTime desc ",
						params, 0, 12);
		mv.addObject("objs", goods_new);// 加载最新商品

		params.clear();
		params.put("store_id", store.getId());
		params.put("display", true);
		List<StoreNavigation> navs = this.storenavigationService
				.query("select obj from StoreNavigation obj where obj.store.id=:store_id and obj.display =:display order by obj.sequence asc ",
						params, -1, -1);
		mv.addObject("navs", navs);// 导航栏

		mv.addObject("store", store);
		mv.addObject("goodsViewTools", goodsViewTools);
		mv.addObject("storeViewTools", storeViewTools);
		mv.addObject("areaViewTools", areaViewTools);
	}

	@RequestMapping("/store_goods_search.htm")
	public ModelAndView store_goods_search(HttpServletRequest request,
			HttpServletResponse response, String keyword, String store_id,
			String currentPage) {
		Store store = this.storeService.getObjById(Long.parseLong(store_id));
		String template = "default";
		if (store.getTemplate() != null && !store.getTemplate().equals("")) {
			template = store.getTemplate();
		}
		ModelAndView mv = new JModelAndView(template
				+ "/store_goods_search.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		GoodsQueryObject gqo = new GoodsQueryObject(currentPage, mv, null, null);
		gqo.addQuery("obj.goods_store.id",
				new SysMap("store_id", CommUtil.null2Long(store_id)), "=");
		gqo.addQuery("obj.goods_name", new SysMap("goods_name", "%" + keyword
				+ "%"), "like");
		gqo.addQuery("obj.goods_status", new SysMap("goods_status", 0), "=");
		gqo.setPageSize(20);
		IPageList pList = this.goodsService.list(gqo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("keyword", keyword);
		mv.addObject("store", store);
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
}
