package com.iskyshop.manage.admin.action;

import java.text.ParseException;
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
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.Group;
import com.iskyshop.foundation.domain.GroupGoods;
import com.iskyshop.foundation.domain.GroupLifeGoods;
import com.iskyshop.foundation.domain.query.GroupGoodsQueryObject;
import com.iskyshop.foundation.domain.query.GroupLifeGoodsQueryObject;
import com.iskyshop.foundation.domain.query.GroupQueryObject;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGroupGoodsService;
import com.iskyshop.foundation.service.IGroupLifeGoodsService;
import com.iskyshop.foundation.service.IGroupService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;

/**
 * 
 * <p>
 * Title: GroupManageAction.java
 * </p>
 * 
 * <p>
 * Description: 团购活动平台管理控制器，用来添加（编辑、删除）团购信息、审核参团商品
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
 * @date 2014年5月27日
 * 
 * @version 1.0
 */
@Controller
public class GroupManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGroupService groupService;
	@Autowired
	private IGroupGoodsService groupGoodsService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGroupLifeGoodsService groupLifeGoodsService;

	/**
	 * Group列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "团购列表", value = "/admin/group_list.htm*", rtype = "admin", rname = "团购管理", rcode = "group_admin", rgroup = "运营")
	@RequestMapping("/admin/group_list.htm")
	public ModelAndView group_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView("admin/blue/group_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		GroupQueryObject qo = new GroupQueryObject(currentPage, mv, orderBy,
				orderType);
		WebForm wf = new WebForm();
		wf.toQueryPo(request, qo, Group.class, mv);
		IPageList pList = this.groupService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url + "/admin/group_list.htm", "",
				params, pList, mv);
		return mv;
	}

	/**
	 * group添加管理
	 * 
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "团购增加", value = "/admin/group_add.htm*", rtype = "admin", rname = "团购管理", rcode = "group_admin", rgroup = "运营")
	@RequestMapping("/admin/group_add.htm")
	public ModelAndView group_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/group_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Group goods_last = null;
		Group life_last = null;
		Map params = new HashMap();
		params.put("group_type", 0);
		params.put("status", 0);
		List<Group> goods_group_lasts = this.groupService
				.query("select obj from Group obj where obj.group_type=:group_type and obj.status=:status order by obj.endTime desc",
						params, 0, 1);
		params.clear();
		params.put("group_type", 1);
		params.put("status", 0);
		List<Group> life_group_lasts = this.groupService
				.query("select obj from Group obj where obj.group_type=:group_type and obj.status=:status order by obj.endTime desc",
						params, 0, 1);
		if (goods_group_lasts.size() > 0) {
			goods_last = goods_group_lasts.get(0);
			mv.addObject("goods_last_time",
					CommUtil.formatShortDate(goods_last.getEndTime()));
		}
		if (life_group_lasts.size() > 0) {
			life_last = life_group_lasts.get(0);
			mv.addObject("life_last_time",
					CommUtil.formatShortDate(life_last.getEndTime()));
		}

		mv.addObject("currentPage", currentPage);
		return mv;
	}

	/**
	 * group保存管理
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "团购保存", value = "/admin/group_save.htm*", rtype = "admin", rname = "团购管理", rcode = "group_admin", rgroup = "运营")
	@RequestMapping("/admin/group_save.htm")
	public ModelAndView group_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String cmd, String begin_hour, String end_hour, String join_hour) {
		WebForm wf = new WebForm();
		Group group = null;
		if (id.equals("")) {
			group = wf.toPo(request, Group.class);
			group.setAddTime(new Date());
		} else {
			Group obj = this.groupService.getObjById(Long.parseLong(id));
			group = (Group) wf.toPo(request, obj);
		}
		Date beginTime = group.getBeginTime();
		beginTime.setHours(CommUtil.null2Int(begin_hour));
		group.setBeginTime(beginTime);
		Date endTime = group.getEndTime();
		endTime.setHours(CommUtil.null2Int(end_hour));
		group.setEndTime(endTime);
		Date joinEndTime = group.getJoinEndTime();
		joinEndTime.setHours(CommUtil.null2Int(join_hour));
		group.setJoinEndTime(joinEndTime);
		if (beginTime.after(new Date())) {
			group.setStatus(1);
		}
		if (id.equals("")) {
			this.groupService.save(group);
		} else
			this.groupService.update(group);
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/admin/group_list.htm");
		mv.addObject("op_title", "保存团购成功");
		mv.addObject("add_url", CommUtil.getURL(request)
				+ "/admin/group_add.htm" + "?currentPage=" + currentPage);
		return mv;
	}

	@SecurityMapping(title = "团购删除", value = "/admin/group_del.htm*", rtype = "admin", rname = "团购管理", rcode = "group_admin", rgroup = "运营")
	@RequestMapping("/admin/group_del.htm")
	public String group_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				Group group = this.groupService.getObjById(CommUtil
						.null2Long(id));
				for (Goods goods : group.getGoods_list()) {
					goods.setGroup_buy(0);
					goods.setGroup(null);
					this.goodsService.update(goods);
				}
				for (GroupGoods gg : group.getGg_list()) {
					this.groupGoodsService.delete(gg.getId());
				}
				this.groupService.delete(CommUtil.null2Long(id));
			}
		}
		return "redirect:group_list.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "团购关闭", value = "/admin/group_close.htm*", rtype = "admin", rname = "团购管理", rcode = "group_admin", rgroup = "运营")
	@RequestMapping("/admin/group_close.htm")
	public String group_close(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				Group group = this.groupService.getObjById(Long.parseLong(id));
				group.setStatus(-1);
				this.groupService.update(group);
				for (GroupGoods gg : group.getGg_list()) {
					gg.setGg_status(-1);
					this.groupGoodsService.update(gg);
				}
				for (Goods goods : group.getGoods_list()) {
					if (goods.getGroup().getId().equals(group.getId())) {
						goods.setGroup(null);
						goods.setGroup_buy(0);
						this.goodsService.update(goods);
					}
				}
			}
		}
		return "redirect:group_list.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "团购申请列表", value = "/admin/group_goods_list.htm*", rtype = "seller", rname = "团购管理", rcode = "group_admin", rgroup = "运营")
	@RequestMapping("/admin/group_goods_list.htm")
	public ModelAndView group_goods_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String group_id,
			String gg_status, String type) {
		ModelAndView mv = new JModelAndView("admin/blue/group_goods_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if ("goods".equals(type) || "".equals(type) || type == null) {
			GroupGoodsQueryObject qo = new GroupGoodsQueryObject(currentPage,
					mv, "addTime", "desc");
			qo.addQuery("obj.group.id",
					new SysMap("group_id", CommUtil.null2Long(group_id)), "=");
			if (gg_status == null || gg_status.equals("")) {
				qo.addQuery("obj.gg_status", new SysMap("gg_status", 0), "=");
			} else {
				qo.addQuery("obj.gg_status",
						new SysMap("gg_status", CommUtil.null2Int(gg_status)),
						"=");
			}
			IPageList pList = this.groupGoodsService.list(qo);
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
			mv.addObject("group_id", group_id);
			mv.addObject("gg_status", CommUtil.null2Int(gg_status));
		}
		if ("life".equals(type)) {
			mv = new JModelAndView("admin/blue/group_lifegoods_list.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			String url = this.configService.getSysConfig().getAddress();
			GroupLifeGoodsQueryObject qo = new GroupLifeGoodsQueryObject(
					currentPage, mv, "addTime", "desc");
			qo.addQuery("obj.group.id",
					new SysMap("group_id", CommUtil.null2Long(group_id)), "=");
			if (gg_status == null || gg_status.equals("")) {
				qo.addQuery("obj.group_status", new SysMap("group_status", 0),
						"=");
			} else {
				qo.addQuery("obj.group_status", new SysMap("group_status",
						CommUtil.null2Int(gg_status)), "=");
			}
			WebForm wf = new WebForm();
			wf.toQueryPo(request, qo, GroupLifeGoods.class, mv);
			IPageList pList = this.groupLifeGoodsService.list(qo);
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
			mv.addObject("group_id", group_id);
			mv.addObject("gg_status", CommUtil.null2Int(gg_status));
		}
		mv.addObject("type", type);
		return mv;
	}

	@SecurityMapping(title = "团购商品审核通过", value = "/admin/group_goods_audit.htm*", rtype = "admin", rname = "团购管理", rcode = "group_admin", rgroup = "运营")
	@RequestMapping("/admin/group_goods_audit.htm")
	public String group_goods_audit(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String group_id,
			String gg_status, String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				GroupGoods gg = this.groupGoodsService.getObjById(CommUtil
						.null2Long(id));
				gg.setGg_status(1);
				gg.setGg_audit_time(new Date());
				this.groupGoodsService.update(gg);
				Goods goods = gg.getGg_goods();
				goods.setGroup_buy(2);
				goods.setGroup(gg.getGroup());
				goods.setGroup(this.groupService.getObjById(CommUtil
						.null2Long(group_id)));
				goods.setGoods_current_price(gg.getGg_price());
				this.goodsService.update(goods);
			}
		}
		return "redirect:group_goods_list.htm?group_id=" + group_id
				+ "&gg_status=" + gg_status + "&currentPage=" + currentPage;
	}

	@SecurityMapping(title = "团购商品审核拒绝", value = "/admin/group_goods_refuse.htm*", rtype = "admin", rname = "团购管理", rcode = "group_admin", rgroup = "运营")
	@RequestMapping("/admin/group_goods_refuse.htm")
	public String group_goods_refuse(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String group_id,
			String gg_status, String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				GroupGoods gg = this.groupGoodsService.getObjById(CommUtil
						.null2Long(id));
				Goods goods = gg.getGg_goods();
				goods.setGroup_buy(0);
				goods.setGroup(null);
				goods.setGoods_current_price(goods.getStore_price());
				this.goodsService.update(goods);
				gg.setGg_status(-1);
				this.groupGoodsService.update(gg);
			}
		}
		return "redirect:group_goods_list.htm?group_id=" + group_id
				+ "&gg_status=" + gg_status + "&currentPage=" + currentPage;
	}

	@SecurityMapping(title = "团购商品审核推荐", value = "/admin/group_goods_recommend.htm*", rtype = "admin", rname = "团购管理", rcode = "group_admin", rgroup = "运营")
	@RequestMapping("/admin/group_goods_recommend.htm")
	public String group_goods_recommend(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String group_id,
			String gg_status, String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				GroupGoods gg = this.groupGoodsService.getObjById(CommUtil
						.null2Long(id));
				if (gg.isGg_recommend()) {
					gg.setGg_recommend(false);
				} else {
					gg.setGg_recommend(true);
				}
				gg.setGg_recommend_time(new Date());
				this.groupGoodsService.update(gg);
			}
		}
		return "redirect:group_goods_list.htm?group_id=" + group_id
				+ "&gg_status=" + gg_status + "&currentPage=" + currentPage;
	}

	@SecurityMapping(title = "团购商品审核推荐", value = "/admin/group_lifegoods_recommend.htm*", rtype = "admin", rname = "团购管理", rcode = "group_admin", rgroup = "运营")
	@RequestMapping("/admin/group_lifegoods_recommend.htm")
	public String group_lifegoods_recommend(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String group_id,
			String gg_status, String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				GroupLifeGoods gg = this.groupLifeGoodsService
						.getObjById(CommUtil.null2Long(id));
				if (gg.isGroup_recommend()) {
					gg.setGroup_recommend(false);
				} else {
					gg.setGroup_recommend(true);
				}
				this.groupLifeGoodsService.update(gg);
			}
		}
		return "redirect:group_goods_list.htm?group_id=" + group_id
				+ "&gg_status=" + gg_status + "&currentPage=" + currentPage
				+ "&type=life";
	}

	@SecurityMapping(title = "团购商品审核通过", value = "/admin/group_lifegoods_audit.htm*", rtype = "admin", rname = "团购管理", rcode = "group_admin", rgroup = "运营")
	@RequestMapping("/admin/group_lifegoods_audit.htm")
	public String group_lifegoods_audit(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String group_id,
			String gg_status, String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				GroupLifeGoods gg = this.groupLifeGoodsService
						.getObjById(CommUtil.null2Long(id));
				gg.setGroup_status(1);
				this.groupLifeGoodsService.update(gg);
			}
		}
		return "redirect:group_goods_list.htm?group_id=" + group_id
				+ "&gg_status=" + gg_status + "&currentPage=" + currentPage
				+ "&type=life";
	}

	@SecurityMapping(title = "团购商品审核拒绝", value = "/admin/group_lifegoods_refuse.htm*", rtype = "admin", rname = "团购管理", rcode = "group_admin", rgroup = "运营")
	@RequestMapping("/admin/group_lifegoods_refuse.htm")
	public String group_lifegoods_refuse(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String group_id,
			String gg_status, String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				GroupLifeGoods gg = this.groupLifeGoodsService
						.getObjById(CommUtil.null2Long(id));
				gg.setGroup_status(-1);
				this.groupLifeGoodsService.update(gg);
			}
		}
		return "redirect:group_goods_list.htm?group_id=" + group_id
				+ "&gg_status=" + gg_status + "&currentPage=" + currentPage
				+ "&type=life";
	}
}