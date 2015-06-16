package com.iskyshop.manage.admin.action;

import java.io.IOException;
import java.io.PrintWriter;
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

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.ActivityGoods;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.Group;
import com.iskyshop.foundation.domain.GroupGoods;
import com.iskyshop.foundation.domain.query.ActivityGoodsQueryObject;
import com.iskyshop.foundation.domain.query.GoodsQueryObject;
import com.iskyshop.foundation.domain.query.GroupGoodsQueryObject;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IActivityGoodsService;
import com.iskyshop.foundation.service.IActivityService;
import com.iskyshop.foundation.service.IGoodsBrandService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGroupGoodsService;
import com.iskyshop.foundation.service.IGroupService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;

/**
 * 
 * <p>
 * Title: AndroidManagAction.java
 * </p>
 * 
 * <p>
 * Description: 手机客户端管理类，用于设置安卓、苹果客户端商品显示情况
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
 * @date 2014-7-21
 * 
 * @version 1.0
 */
@Controller
public class MobileClientManageAction {

	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;

	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGoodsBrandService goodsBrandService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IActivityGoodsService activityGoodsService;
	@Autowired
	private IActivityService activityService;
	@Autowired
	private IGoodsClassService goodsclassService;
	@Autowired
	private IGroupService groupService;
	@Autowired
	private IGroupGoodsService groupgoodsService;
	@Autowired
	private IActivityGoodsService activitygoodsService;
	@Autowired
	private IUserService userService;

	@SecurityMapping(title = "手机客户端商品", value = "/admin/mobile_goods.htm*", rtype = "admin", rname = "手机商品", rcode = "admin_mobile_goods", rgroup = "运营")
	@RequestMapping("/admin/mobile_goods.htm")
	public ModelAndView mobile_goods(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String goods_name, String query_type) {
		ModelAndView mv = new JModelAndView("admin/blue/mobile_goods.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		GoodsQueryObject qo = new GoodsQueryObject(currentPage, mv, orderBy,
				orderType);
		WebForm wf = new WebForm();
		wf.toQueryPo(request, qo, Goods.class, mv);
		qo.addQuery("obj.goods_status", new SysMap("goods_status", 0), "=");
		if (goods_name != null && !goods_name.equals("")) {
			qo.addQuery("obj.goods_name", new SysMap("goods_name", "%"
					+ goods_name + "%"), "like");
		}
		if (query_type != null && !query_type.equals("")) {
			if (query_type.equals("0")) {
				qo.addQuery("obj.mobile_hot", new SysMap("mobile_hot", 1), "=");
			}
			if (query_type.equals("1")) {
				qo.addQuery("obj.mobile_recommend", new SysMap(
						"mobile_recommend", 1), "=");
			}
		}
		IPageList pList = this.goodsService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", null, pList, mv);
		mv.addObject("goods_name", goods_name);
		mv.addObject("query_type", query_type);
		return mv;
	}

	@SecurityMapping(title = "手机商品AJAX更新", value = "/admin/mobile_goods_ajax.htm*", rtype = "admin", rname = "手机商品", rcode = "admin_mobile_goods", rgroup = "运营")
	@RequestMapping("/admin/mobile_goods_ajax.htm")
	public void mobile_goods_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String fieldName) {
		Goods obj = this.goodsService.getObjById(Long.parseLong(id));
		boolean val = false;
		if (fieldName.equals("mobile_recommend")) {
			if (obj.getMobile_recommend() == 1) {
				obj.setMobile_recommend(0);
				obj.setMobile_recommendTime(null);
				val = false;
			} else {
				obj.setMobile_recommend(1);
				obj.setMobile_recommendTime(new Date());
				val = true;
			}
		}
		if (fieldName.equals("mobile_hot")) {
			if (obj.getMobile_hot() == 1) {
				obj.setMobile_hot(0);
				obj.setMobile_hotTime(null);
				val = false;
			} else {
				obj.setMobile_hot(1);
				obj.setMobile_hotTime(new Date());
				val = true;
			}
		}
		this.goodsService.update(obj);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(val);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@SecurityMapping(title = "手机团购商品列表", value = "/admin/mobile_groupgoods.htm*", rtype = "admin", rname = "手机团购", rcode = "admin_mobile_group", rgroup = "运营")
	@RequestMapping("/admin/mobile_groupgoods.htm")
	public ModelAndView mobile_groupgoods(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String gg_name, String goods_name,
			String mobile_recommend) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/mobile_groupgoods.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map params = new HashMap();
		params.put("beginTime", new Date());
		params.put("endTime", new Date());
		params.put("status", 0);
		List<Group> groups = this.groupService
				.query("select obj from Group obj where obj.beginTime<=:beginTime and obj.endTime>=:endTime and obj.status=:status",
						params, -1, -1);
		if (groups.size() > 0) {
			GroupGoodsQueryObject qo = new GroupGoodsQueryObject(currentPage,
					mv, orderBy, orderType);
			WebForm wf = new WebForm();
			wf.toQueryPo(request, qo, GroupGoods.class, mv);
			qo.addQuery("obj.group.id", new SysMap("group_id", groups.get(0)
					.getId()), "=");
			qo.addQuery("obj.gg_status", new SysMap("obj_gg_status", 1), "=");
			if (goods_name != null && !goods_name.equals("")) {
				qo.addQuery(
						"obj.gg_goods.goods_name",
						new SysMap("obj_goods_name", "%"
								+ CommUtil.null2String(goods_name) + "%"),
						"like");
			}
			if (gg_name != null && !gg_name.equals("")) {
				qo.addQuery(
						"obj.gg_name",
						new SysMap("gg_name", "%"
								+ CommUtil.null2String(gg_name) + "%"), "like");
			}
			if (mobile_recommend != null && !mobile_recommend.equals("")) {
				qo.addQuery(
						"obj.mobile_recommend",
						new SysMap("mobile_recommend", CommUtil
								.null2Int(mobile_recommend)), "=");
			}
			IPageList pList = this.groupgoodsService.list(qo);
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
			mv.addObject("goods_name", goods_name);
			mv.addObject("gg_name", gg_name);
			mv.addObject("mobile_recommend", mobile_recommend);
		}
		return mv;
	}

	@SecurityMapping(title = "手机团购商品更新", value = "/admin/mobile_groupgoods_ajax.htm*", rtype = "admin", rname = "手机团购", rcode = "admin_mobile_group", rgroup = "运营")
	@RequestMapping("/admin/mobile_groupgoods_ajax.htm")
	public void mobile_groupgoods_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String fieldName,
			String value) throws ClassNotFoundException {
		GroupGoods obj = this.groupgoodsService.getObjById(Long.parseLong(id));
		boolean val = false;
		if (obj.getMobile_recommend() == 1) {
			obj.setMobile_recommend(0);
			obj.setMobile_recommendTime(null);
			val = false;
		} else {
			obj.setMobile_recommend(1);
			obj.setMobile_recommendTime(new Date());
			val = true;
		}
		this.groupgoodsService.update(obj);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(val);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@SecurityMapping(title = "手机活动商品列表", value = "/admin/mobile_activitygoods.htm*", rtype = "admin", rname = "手机团购", rcode = "admin_mobile_group", rgroup = "运营")
	@RequestMapping("/admin/mobile_activitygoods.htm")
	public ModelAndView mobile_activitygoods(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String goods_name) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/mobile_activitygoods.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		ActivityGoodsQueryObject qo = new ActivityGoodsQueryObject(currentPage,
				mv, orderBy, orderType);
		WebForm wf = new WebForm();
		wf.toQueryPo(request, qo, GroupGoods.class, mv);
		if (goods_name != null && !goods_name.equals("")) {
			qo.addQuery("obj.ag_goods.goods_name", new SysMap("goods_name", "%"
					+ goods_name + "%"), "like");
		}
		qo.addQuery("obj.act.ac_begin_time", new SysMap("ac_begin_time",
				new Date()), "<=");
		qo.addQuery("obj.act.ac_end_time",
				new SysMap("ac_end_time", new Date()), ">=");
		qo.addQuery("obj.act.ac_status", new SysMap("ac_status", 1), "=");
		qo.addQuery("obj.ag_status", new SysMap("ag_status", 1), "=");
		IPageList pList = this.activitygoodsService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("goods_name", goods_name);
		return mv;
	}

	@SecurityMapping(title = "手机活动商品更新", value = "/admin/mobile_activitygoods_ajax.htm*", rtype = "admin", rname = "手机团购", rcode = "admin_mobile_group", rgroup = "运营")
	@RequestMapping("/admin/mobile_activitygoods_ajax.htm")
	public void mobile_activitygoods_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String value)
			throws ClassNotFoundException {
		ActivityGoods obj = this.activityGoodsService.getObjById(Long
				.parseLong(id));
		boolean val = false;
		if (obj.getMobile_recommend() == 1) {
			obj.setMobile_recommend(0);
			obj.setMobile_recommendTime(null);
			val = false;
		} else {
			obj.setMobile_recommend(1);
			obj.setMobile_recommendTime(new Date());
			val = true;
		}
		this.activityGoodsService.update(obj);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(val);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
