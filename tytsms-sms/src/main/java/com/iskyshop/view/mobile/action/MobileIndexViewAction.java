package com.iskyshop.view.mobile.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Activity;
import com.iskyshop.foundation.domain.ActivityGoods;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsBrand;
import com.iskyshop.foundation.domain.GroupGoods;
import com.iskyshop.foundation.service.IActivityGoodsService;
import com.iskyshop.foundation.service.IActivityService;
import com.iskyshop.foundation.service.IGoodsBrandService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGroupGoodsService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;

/**
 * 
 * <p>
 * Title: MobileIndexViewAction.java
 * </p>
 * 
 * <p>
 * Description:手机客户端商城前台请求控制器
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
public class MobileIndexViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGoodsBrandService brandService;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private IGroupGoodsService groupgoodsService;
	@Autowired
	private IGoodsBrandService goodsBrandService;
	@Autowired
	private IActivityGoodsService activityGoodsService;
	@Autowired
	private IActivityService activityService;


	/**
	 * 手机客户端商城首页，返回html页面
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/mobile/index.htm")
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response, String type) {
		ModelAndView mv = new JModelAndView("mobile/view/index.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map params = new HashMap();
		params.clear();
		params.put("mobile_hot", 1);
		params.put("goods_status", 0);
		List<Goods> goods_hots = this.goodsService
				.query("select obj from Goods obj where obj.mobile_hot=:mobile_hot and obj.goods_status =:goods_status order by mobile_hotTime desc",
						params, 0, 4);
		mv.addObject("goods_hots", goods_hots);
		params.clear();
		params.put("mobile_recommend", 1);
		params.put("goods_status", 0);
		List<Goods> top_recommends = this.goodsService
				.query("select obj from Goods obj where obj.mobile_recommend=:mobile_recommend and obj.goods_status =:goods_status order by mobile_recommendTime desc",
						params, 0, 4);
		mv.addObject("top_recommends", top_recommends);

		params.clear();
		params.put("mobile_recommend", 1);
		params.put("goods_status", 0);
		List<Goods> bottom_recommends = this.goodsService
				.query("select obj from Goods obj where obj.mobile_recommend=:mobile_recommend and obj.goods_status =:goods_status order by mobile_recommendTime desc",
						params, 4, 4);
		mv.addObject("bottom_recommends", bottom_recommends);

		params.clear();
		params.put("mobile_recommend", 1);
		params.put("audit", 1);
		List<GoodsBrand> gbs = this.brandService
				.query("select obj from GoodsBrand obj where obj.audit=:audit and obj.mobile_recommend=:mobile_recommend order by mobile_recommendTime desc",
						params, 0, 4);
		mv.addObject("gbs", gbs);
		params.clear();
		params.put("ac_begin_time", new Date());
		params.put("ac_end_time", new Date());
		params.put("ac_status", 1);
		List<Activity> activitys = this.activityService
				.query("select obj from Activity obj where obj.ac_status=:ac_status and obj.ac_begin_time<=:ac_begin_time and "
						+ "obj.ac_end_time>=:ac_end_time", params, 0, 1);
		if (activitys.size() > 0) {
			params.clear();
			params.put("ag_status", 1);
			params.put("goods_status", 0);
			params.put("mobile_recommend", 1);
			params.put("act_id", activitys.get(0).getId());
			List<ActivityGoods> activitygoods = this.activityGoodsService
					.query("select obj from ActivityGoods obj where obj.ag_status=:ag_status and obj.ag_goods.goods_status=:goods_status "
							+ "and obj.mobile_recommend=:mobile_recommend and obj.act.id=:act_id "
							+ " order by mobile_recommendTime desc", params, 0,
							4);
			mv.addObject("activitygoods", activitygoods);
		}
		params.clear();
		params.put("gg_status", 1);
		params.put("group_status", 0);
		params.put("mobile_recommend", 1);
		params.put("beginTime", new Date());
		params.put("endTime", new Date());
		params.put("group_beginTime", new Date());
		params.put("group_endTime", new Date());
		List<GroupGoods> groupgoods = this.groupgoodsService
				.query("select obj from GroupGoods obj where obj.gg_status=:gg_status and obj.group.status=:group_status "
						+ "and obj.mobile_recommend=:mobile_recommend and obj.group.beginTime<=:group_beginTime and "
						+ "obj.group.endTime>=:group_endTime and obj.beginTime<=:beginTime and obj.endTime>=:endTime order by mobile_recommendTime desc",
						params, 0, 4);
		mv.addObject("groupgoods", groupgoods);
		mv.addObject("type", type);
		return mv;
	}

	/**
	 * 手机客户端商城首页，返回html页面
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/mobile/close.htm")
	public ModelAndView close(HttpServletRequest request,
			HttpServletResponse response, String type) {
		ModelAndView mv = new JModelAndView("mobile/view/close.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		return mv;
	}

	/**
	 * 根据前端二维码扫描结果自动下载对应的手机客户端
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping("/app/download.htm")
	public void app_download(HttpServletRequest request,
			HttpServletResponse response) {
		// System.out.println(request.getHeader("User-Agent").toLowerCase());
		String user_agent = request.getHeader("User-Agent").toLowerCase();
		String url = CommUtil.getURL(request);
		// String ios_reg =
		// ".+?\\(iphone; cpu \\w+ os [1-9]\\d*_\\d+_\\d+ \\w+ mac os x\\).+";
		if (user_agent.indexOf("iphone") > 0) {
			url = this.configService.getSysConfig().getIos_download();
		}
		if (user_agent.indexOf("android") > 0) {
			url = this.configService.getSysConfig().getAndroid_download();
		}
		// System.out.println("自动下载app");
		// request.getRequestDispatcher(url).forward(request, response);
		try {
			response.sendRedirect(url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
