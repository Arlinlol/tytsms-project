package com.iskyshop.manage.admin.action;

import java.math.BigDecimal;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Activity;
import com.iskyshop.foundation.domain.ActivityGoods;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.query.ActivityGoodsQueryObject;
import com.iskyshop.foundation.domain.query.ActivityQueryObject;
import com.iskyshop.foundation.domain.query.GoodsQueryObject;
import com.iskyshop.foundation.service.IActivityGoodsService;
import com.iskyshop.foundation.service.IActivityService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;

/**
 * 
 * <p>
 * Title: ActivitySelfManageAction.java
 * </p>
 * 
 * <p>
 * Description: 自营活动管理类
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
 * @date 2014年5月21日
 * 
 * @version 1.0
 */

@Controller
public class ActivitySelfManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IActivityService activityService;
	@Autowired
	private IActivityGoodsService activityGoodsService;
	@Autowired
	private IGoodsService goodService;

	/**
	 * Activity列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "自营活动列表", value = "/admin/group_self.htm*", rtype = "admin", rname = "自营活动管理", rcode = "activity_self", rgroup = "自营")
	@RequestMapping("/admin/activity_self.htm")
	public ModelAndView activity_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView("admin/blue/activity_self.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		ActivityQueryObject qo = new ActivityQueryObject(currentPage, mv,
				orderBy, orderType);
		IPageList pList = this.activityService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}

	@SecurityMapping(title = "自营活动申请", value = "/admin/activity_self_apply.htm*", rtype = "admin", rname = "自营活动管理", rcode = "activity_self", rgroup = "自营")
	@RequestMapping("/admin/activity_self_apply.htm")
	public ModelAndView activity_apply(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/activity_self_apply.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Activity act = this.activityService.getObjById(CommUtil.null2Long(id));
		mv.addObject("act", act);
		return mv;
	}

	@SecurityMapping(title = "自营活动商品加载", value = "/admin/activity_self_goods_load.htm*", rtype = "admin", rname = "自营活动管理", rcode = "activity_self", rgroup = "自营")
	@RequestMapping("/admin/activity_self_goods_load.htm")
	public ModelAndView activity_self_goods_load(HttpServletRequest request,
			HttpServletResponse response, String goods_name, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/activity_self_goods_load.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		GoodsQueryObject qo = new GoodsQueryObject();
		qo.setCurrentPage(CommUtil.null2Int(currentPage));
		if (!CommUtil.null2String(goods_name).equals("")) {
			qo.addQuery("obj.goods_name", new SysMap("goods_name", "%"
					+ CommUtil.null2String(goods_name) + "%"), "like");
		}
		qo.addQuery("obj.goods_type", new SysMap("goods_type", 0), "=");
		qo.addQuery("obj.goods_status", new SysMap("goods_status", 0), "=");
		qo.addQuery("obj.group_buy", new SysMap("group_buy", 0), "=");
		qo.addQuery("obj.activity_status", new SysMap("activity_status", 0),
				"=");
		qo.setPageSize(15);
		IPageList pList = this.goodService.list(qo);
		CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request)
				+ "/admin/activity_self_goods_load.htm", "", "&goods_name="
				+ goods_name, pList, mv);
		return mv;
	}

	@SecurityMapping(title = "自营活动商品保存", value = "/admin/activity_self_apply_save.htm*", rtype = "admin", rname = "自营活动管理", rcode = "activity_self", rgroup = "自营")
	@RequestMapping("/admin/activity_self_apply_save.htm")
	@Transactional
	public ModelAndView activity_apply_save(HttpServletRequest request,
			HttpServletResponse response, String goods_ids, String act_id) {
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Activity act = this.activityService.getObjById(CommUtil
				.null2Long(act_id));
		String[] ids = goods_ids.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				ActivityGoods ag = new ActivityGoods();
				ag.setAddTime(new Date());
				Goods goods = this.goodService.getObjById(CommUtil
						.null2Long(id));
				// 活动状态
				// 活动状态，0为无活动，1为待审核，2为审核通过，3为活动商品已经卖完
				goods.setActivity_price(BigDecimal.valueOf(CommUtil.mul(
						goods.getStore_price(), act.getAc_rebate())));// 设置铜牌会员商品价格
				goods.setActivity_price1(BigDecimal.valueOf(CommUtil.mul(
						goods.getStore_price(), act.getAc_rebate1())));// 设置银牌会员商品价格
				goods.setActivity_price2(BigDecimal.valueOf(CommUtil.mul(
						goods.getStore_price(), act.getAc_rebate2())));// 设置金牌会员商品价格
				goods.setActivity_price3(BigDecimal.valueOf(CommUtil.mul(
						goods.getStore_price(), act.getAc_rebate3())));// 设置超级会员商品价格
				goods.setActivity_status(2);
				this.goodService.update(goods);
				ag.setAg_goods(goods);
				ag.setAg_status(1);
				ag.setAct(act);
				ag.setAg_type(1);// 自营活动商品
				this.activityGoodsService.save(ag);
			}
		}
		mv.addObject("op_title", "参加活动成功");
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/admin/activity_self.htm");
		return mv;
	}

	@SecurityMapping(title = "活动商品列表", value = "/admin/activity_self_goods_list.htm*", rtype = "admin", rname = "自营活动管理", rcode = "activity_self", rgroup = "自营")
	@RequestMapping("/admin/activity_self_goods_list.htm")
	public ModelAndView activity_goods_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String act_id) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/activity_self_goods_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		ActivityGoodsQueryObject qo = new ActivityGoodsQueryObject(currentPage,
				mv, orderBy, orderType);
		qo.addQuery("obj.ag_type", new SysMap("ag_type", CommUtil.null2Int(1)),
				"=");
		if (act_id != null && !act_id.equals("")) {
			qo.addQuery("obj.act.id",
					new SysMap("obj_act_id", CommUtil.null2Long(act_id)), "=");
		}
		IPageList pList = this.activityGoodsService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("act_id", act_id);
		return mv;
	}

}