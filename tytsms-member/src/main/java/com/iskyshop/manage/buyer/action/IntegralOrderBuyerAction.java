package com.iskyshop.manage.buyer.action;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.ExpressCompany;
import com.iskyshop.foundation.domain.IntegralGoods;
import com.iskyshop.foundation.domain.IntegralGoodsCart;
import com.iskyshop.foundation.domain.IntegralGoodsOrder;
import com.iskyshop.foundation.domain.IntegralLog;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.IntegralGoodsOrderQueryObject;
import com.iskyshop.foundation.domain.virtual.TransInfo;
import com.iskyshop.foundation.service.IExpressCompanyService;
import com.iskyshop.foundation.service.IIntegralGoodsOrderService;
import com.iskyshop.foundation.service.IIntegralGoodsService;
import com.iskyshop.foundation.service.IIntegralLogService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.AreaManageTools;
import com.iskyshop.manage.admin.tools.OrderFormTools;
import com.iskyshop.view.web.tools.IntegralViewTools;

/**
 * 
 * 
* <p>Title:IntegralOrderBuyerAction.java</p>

* <p>Description: 积分商城买家控制器</p>

* <p>Copyright: Copyright (c) 2014</p>

* <p>Company: 沈阳网之商科技有限公司 www.iskyshop.com</p>

* @author jy

* @date 2014年5月19日

* @version 1.0
 */
@Controller
public class IntegralOrderBuyerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IIntegralGoodsService integralGoodsService;
	@Autowired
	private IIntegralGoodsOrderService integralGoodsOrderService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IIntegralLogService integralLogService;
	@Autowired
	private IntegralViewTools integralViewTools;
	@Autowired 
	private AreaManageTools areaManageTools;
	@Autowired
	private IExpressCompanyService expressCompanyService;
	@Autowired
	private OrderFormTools orderFormTools;

	@SecurityMapping(title = "买家订单列表", value = "/buyer/integral_order_list.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/integral_order_list.htm")
	public ModelAndView integral_order_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/integral_order_list.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		if (this.configService.getSysConfig().isIntegralStore()) {
			IntegralGoodsOrderQueryObject qo = new IntegralGoodsOrderQueryObject(
					currentPage, mv, "addTime", "desc");
			qo.addQuery("obj.igo_user.id", new SysMap("user_id",
					SecurityUserHolder.getCurrentUser().getId()), "=");
			IPageList pList = this.integralGoodsOrderService.list(qo);
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "系统未开启积分商城");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/index.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "取消订单", value = "/buyer/integral_order_cancel.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/integral_order_cancel.htm")
	public ModelAndView integral_order_cancel(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView("success.html", configService
				.getSysConfig(), this.userConfigService.getUserConfig(), 1,
				request, response);
		IntegralGoodsOrder obj = this.integralGoodsOrderService
				.getObjById(CommUtil.null2Long(id));
		if (obj != null
				&& obj.getIgo_user().getId().equals(
						SecurityUserHolder.getCurrentUser().getId())) {
			obj.setIgo_status(-1);
			this.integralGoodsOrderService.update(obj);
			for (IntegralGoodsCart igc : obj.getIgo_gcs()) {
				IntegralGoods goods = igc.getGoods();
				goods.setIg_goods_count(goods.getIg_goods_count()
						+ igc.getCount());
				this.integralGoodsService.update(goods);
			}
			User user = obj.getIgo_user();
			user.setIntegral(user.getIntegral() + obj.getIgo_total_integral());
			this.userService.update(user);
			IntegralLog log = new IntegralLog();
			log.setAddTime(new Date());
			log.setContent("取消" + obj.getIgo_order_sn() + "积分兑换，返还积分");
			log.setIntegral(obj.getIgo_total_integral());
			log.setIntegral_user(obj.getIgo_user());
			log.setType("integral_order");
			this.integralLogService.save(log);
			mv.addObject("op_title", "积分兑换取消成功");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/buyer/integral_order_list.htm");
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "参数错误，无该订单");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/buyer/integral_order_list.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "积分订单详情", value = "/buyer/integral_order_view.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/integral_order_view.htm")
	public ModelAndView integral_order_view(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/integral_order_view.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		IntegralGoodsOrder obj = this.integralGoodsOrderService
				.getObjById(CommUtil.null2Long(id));
		if (obj != null
				&& obj.getIgo_user().getId().equals(
						SecurityUserHolder.getCurrentUser().getId())) {
			mv.addObject("obj", obj);
			mv.addObject("currentPage", currentPage);
			mv.addObject("integralViewTools",integralViewTools);
			mv.addObject("areaManageTools",areaManageTools);
			mv.addObject("orderFormTools", orderFormTools);
			boolean query_ship = false;// 是否查询物流
			if (!CommUtil.null2String(obj.getIgo_ship_code()).equals("")) {
				query_ship = true;
			}
			mv.addObject("query_ship", query_ship);
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "参数错误，无该订单");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/buyer/integral_order_list.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "确认收货", value = "/buyer/integral_order_cofirm.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/integral_order_cofirm.htm")
	public ModelAndView integral_order_cofirm(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/integral_order_cofirm.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		IntegralGoodsOrder obj = this.integralGoodsOrderService
				.getObjById(CommUtil.null2Long(id));
		if (obj != null
				&& obj.getIgo_user().getId().equals(
						SecurityUserHolder.getCurrentUser().getId())) {
			mv.addObject("obj", obj);
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "参数错误，无该订单");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/buyer/integral_order_list.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "确认收货保存", value = "/buyer/integral_order_cofirm_save.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/integral_order_cofirm_save.htm")
	public ModelAndView integral_order_cofirm_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView("success.html", configService
				.getSysConfig(), this.userConfigService.getUserConfig(), 1,
				request, response);
		IntegralGoodsOrder obj = this.integralGoodsOrderService
				.getObjById(CommUtil.null2Long(id));
		if (obj != null
				&& obj.getIgo_user().getId().equals(
						SecurityUserHolder.getCurrentUser().getId())) {
			obj.setIgo_status(40);
			this.integralGoodsOrderService.update(obj);
			mv.addObject("op_title", "确认收货成功");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/buyer/integral_order_list.htm");
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "参数错误，无该订单");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/buyer/integral_order_list.htm?currentPage="
					+ currentPage);
		}
		return mv;
	}
	
	@SecurityMapping(title = "物流ajax", value = "/buyer/integral_ship_ajax.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/integral_ship_ajax.htm")
	public ModelAndView integral_ship_ajax(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String order_id) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/integral_ship_ajax.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		IntegralGoodsOrder order = this.integralGoodsOrderService.getObjById(CommUtil
				.null2Long(order_id));
		TransInfo transInfo = this.query_ship_getData(CommUtil
				.null2String(order_id));
		if (transInfo != null) {
			transInfo.setExpress_company_name(this.orderFormTools.queryExInfo(
					order.getIgo_express_info(), "express_company_name"));
			transInfo.setExpress_ship_code(order.getIgo_ship_code());
		}
		mv.addObject("transInfo", transInfo);
		return mv;
	}
	
	private TransInfo query_ship_getData(String id) {
		TransInfo info = null;
		IntegralGoodsOrder obj = this.integralGoodsOrderService
				.getObjById(CommUtil.null2Long(id));
		if (obj != null && !CommUtil.null2String(obj.getIgo_ship_code()).equals("")) {
			info = new TransInfo();
			try {
				ExpressCompany ec = this.queryExpressCompany(obj
						.getIgo_express_info());
				String query_url = "http://api.kuaidi100.com/api?id="
						+ this.configService.getSysConfig().getKuaidi_id()
						+ "&com=" + (ec != null ? ec.getCompany_mark() : "")
						+ "&nu=" + obj.getIgo_ship_code()
						+ "&show=0&muti=1&order=asc";
				URL url = new URL(query_url);
				URLConnection con = url.openConnection();
				con.setAllowUserInteraction(false);
				InputStream urlStream = url.openStream();
				String type = con.guessContentTypeFromStream(urlStream);
				String charSet = null;
				if (type == null)
					type = con.getContentType();
				if (type == null || type.trim().length() == 0
						|| type.trim().indexOf("text/html") < 0)
					return info;
				if (type.indexOf("charset=") > 0)
					charSet = type.substring(type.indexOf("charset=") + 8);
				byte b[] = new byte[10000];
				int numRead = urlStream.read(b);
				String content = new String(b, 0, numRead, charSet);
				while (numRead != -1) {
					numRead = urlStream.read(b);
					if (numRead != -1) {
						// String newContent = new String(b, 0, numRead);
						String newContent = new String(b, 0, numRead, charSet);
						content += newContent;
					}
				}
				info = Json.fromJson(TransInfo.class, content);
				urlStream.close();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return info;
	}

	private ExpressCompany queryExpressCompany(String json) {
		ExpressCompany ec = null;
		if (json != null && !json.equals("")) {
			HashMap map = Json.fromJson(HashMap.class, json);
			ec = this.expressCompanyService.getObjById(CommUtil.null2Long(map
					.get("express_company_id")));
		}
		return ec;
	}
	
}
