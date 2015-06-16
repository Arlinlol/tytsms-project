package com.iskyshop.manage.seller.action;

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
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.GoodsQueryObject;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;

@Controller
public class ZtcSellerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IUserService userService;

	@SecurityMapping(title = "直通车申请", value = "/seller/ztc_apply.htm*", rtype = "seller", rname = "竞价直通车", rcode = "ztc_seller", rgroup = "促销推广")
	@RequestMapping("/seller/ztc_apply.htm")
	public ModelAndView ztc_apply(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/ztc_apply.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (!this.configService.getSysConfig().isZtc_status()) {
			mv = new JModelAndView(
					"user/default/sellercenter/seller_error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未开启直通车");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
		}
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		mv.addObject("user", user);
		return mv;
	}

	@SecurityMapping(title = "直通车加载商品", value = "/seller/ztc_goods.htm*", rtype = "seller", rname = "竞价直通车", rcode = "ztc_seller", rgroup = "促销推广")
	@RequestMapping("/seller/ztc_goods.htm")
	public ModelAndView ztc_goods(HttpServletRequest request,
			HttpServletResponse response, String goods_name, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/ztc_goods.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		GoodsQueryObject qo = new GoodsQueryObject();
		qo.setCurrentPage(CommUtil.null2Int(currentPage));
		if (!CommUtil.null2String(goods_name).equals("")) {
			qo.addQuery("obj.goods_name", new SysMap("goods_name", "%"
					+ CommUtil.null2String(goods_name) + "%"), "like");
		}
		qo.addQuery("obj.goods_store.id",
				new SysMap("store_id", store.getId()), "=");
		qo.addQuery("obj.goods_status", new SysMap("goods_status", 0), "=");
		qo.addQuery("obj.ztc_status", new SysMap("ztc_status", 0), "=");
		qo.addQuery("obj.ztc_status", new SysMap("ztc_status1", -1), "=","or");
		qo.setPageSize(15);
		IPageList pList = this.goodsService.list(qo);
		CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request)
				+ "/seller/ztc_goods.htm", "", "&goods_name=" + goods_name,
				pList, mv);
		return mv;
	}

	@SecurityMapping(title = "直通车申请保存", value = "/seller/ztc_apply_save.htm*", rtype = "seller", rname = "竞价直通车", rcode = "ztc_seller", rgroup = "促销推广")
	@RequestMapping("/seller/ztc_apply_save.htm")
	@Transactional
	public String ztc_apply_save(HttpServletRequest request,
			HttpServletResponse response, String goods_id, String ztc_price,
			String ztc_begin_time, String ztc_gold, String ztc_session) {
		String url = "redirect:/seller/ztc_apply.htm";
		if (!this.configService.getSysConfig().isZtc_status()) {
			request.getSession(false).setAttribute("url",
					CommUtil.getURL(request) + "/seller/ztc_apply.htm");
			request.getSession(false).setAttribute("op_title", "系统未开启直通车");
			url = "redirect:/seller/error.htm";
		} else {
			Goods goods = this.goodsService.getObjById(CommUtil
					.null2Long(goods_id));
			goods.setZtc_status(1);
			goods.setZtc_pay_status(1);
			goods.setZtc_begin_time(CommUtil.formatDate(ztc_begin_time));
			goods.setZtc_gold(CommUtil.null2Int(ztc_gold));
			goods.setZtc_price(CommUtil.null2Int(ztc_price));
			goods.setZtc_apply_time(new Date());
			this.goodsService.update(goods);
			request.getSession(false).setAttribute("url",
					CommUtil.getURL(request) + "/seller/ztc_list.htm");
			request.getSession(false).setAttribute("op_title", "直通车申请成功,等待审核");
			url = "redirect:/seller/success.htm";
		}
		return url;
	}

	@SecurityMapping(title = "直通车申请列表", value = "/seller/ztc_apply_list.htm*", rtype = "seller", rname = "竞价直通车", rcode = "ztc_seller", rgroup = "促销推广")
	@RequestMapping("/seller/ztc_apply_list.htm")
	public ModelAndView ztc_apply_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String goods_name) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/ztc_apply_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (!this.configService.getSysConfig().isZtc_status()) {
			mv = new JModelAndView(
					"user/default/sellercenter/seller_error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未开启直通车");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
		} else {
			GoodsQueryObject qo = new GoodsQueryObject(currentPage, mv,
					"ztc_begin_time", "desc");
			qo.addQuery("obj.goods_store.user.id", new SysMap("user_id",
					SecurityUserHolder.getCurrentUser().getId()), "=");
			qo.addQuery("obj.ztc_status", new SysMap("ztc_status", 1), "=");
			if (!CommUtil.null2String(goods_name).equals("")) {
				qo.addQuery("obj.goods_name", new SysMap("goods_name", "%"
						+ goods_name.trim() + "%"), "like");
			}
			IPageList pList = this.goodsService.list(qo);
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
			mv.addObject("goods_name", goods_name);
		}
		return mv;
	}

	@SecurityMapping(title = "直通车商品列表", value = "/seller/ztc_list.htm*", rtype = "seller", rname = "竞价直通车", rcode = "ztc_seller", rgroup = "促销推广")
	@RequestMapping("/seller/ztc_list.htm")
	public ModelAndView ztc_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String goods_name) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/ztc_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (!this.configService.getSysConfig().isZtc_status()) {
			mv = new JModelAndView(
					"user/default/sellercenter/seller_error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未开启直通车");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
		} else {
			GoodsQueryObject qo = new GoodsQueryObject(currentPage, mv,
					"ztc_apply_time", "desc");
			qo.addQuery("obj.goods_store.user.id", new SysMap("user_id",
					SecurityUserHolder.getCurrentUser().getId()), "=");
			qo.addQuery("obj.ztc_status", new SysMap("ztc_status", 2), ">=");
			if (!CommUtil.null2String(goods_name).equals("")) {
				qo.addQuery("obj.goods_name", new SysMap("goods_name", "%"
						+ goods_name.trim() + "%"), "like");
			}
			IPageList pList = this.goodsService.list(qo);
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		}
		return mv;
	}

	@SecurityMapping(title = "直通车申请查看", value = "/seller/ztc_apply_view.htm*", rtype = "seller", rname = "竞价直通车", rcode = "ztc_seller", rgroup = "促销推广")
	@RequestMapping("/seller/ztc_apply_view.htm")
	public ModelAndView ztc_apply_view(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/ztc_apply_view.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (!this.configService.getSysConfig().isZtc_status()) {
			mv = new JModelAndView(
					"user/default/sellercenter/seller_error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未开启直通车");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
		} else {
			Goods obj = this.goodsService.getObjById(CommUtil.null2Long(id));
			if (obj.getGoods_store().getUser().getId()
					.equals(SecurityUserHolder.getCurrentUser().getId())) {
				mv.addObject("obj", obj);
			} else {
				mv = new JModelAndView(
						"user/default/sellercenter/seller_error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				mv.addObject("op_title", "参数错误，不存在该直通车信息");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/seller/ztc_list.htm");
			}
		}
		return mv;
	}
}
