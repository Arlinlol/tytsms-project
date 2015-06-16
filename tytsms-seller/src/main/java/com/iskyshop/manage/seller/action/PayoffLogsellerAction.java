package com.iskyshop.manage.seller.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
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
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.PayoffLog;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.PayoffLogQueryObject;
import com.iskyshop.foundation.service.IGoodsCartService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGroupInfoService;
import com.iskyshop.foundation.service.IGroupLifeGoodsService;
import com.iskyshop.foundation.service.IMessageService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IPayoffLogService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.ITemplateService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.foundation.service.IZTCGoldLogService;
import com.iskyshop.manage.admin.tools.OrderFormTools;
import com.iskyshop.manage.admin.tools.PayoffLogTools;

/**
 * 
 * <p>
 * Title: PayoffLogManageAction.java
 * </p>
 * 
 * <p>
 * Description: 系统结算管理类,商家可以和平台进行结算
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
 * @date 2014年5月5日
 * 
 * @version 1.0
 */
@Controller
public class PayoffLogsellerAction {

	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IPayoffLogService payoffLogService;
	@Autowired
	private IUserService userService;
	@Autowired
	private PayoffLogTools payofflogTools;
	@Autowired
	private OrderFormTools orderFormTools;
	@Autowired
	private IOrderFormService orderFormServer;
	@Autowired
	private IGoodsCartService goodsCartService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IZTCGoldLogService ztcGoldLogService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private ITemplateService templateService;
	@Autowired
	private IMessageService messageService;
	@Autowired
	private IGroupLifeGoodsService groupLifeGoodsService;
	@Autowired
	private IGroupInfoService groupInfoService;
	@Autowired
	private IOrderFormService orderformService;

	/**
	 * 结算列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "结算列表页", value = "/seller/payofflog_list.htm*", rtype = "seller", display = false, rname = "结算管理", rcode = "payoff_seller", rgroup = "结算管理")
	@RequestMapping("/seller/payofflog_list.htm")
	public ModelAndView payofflog_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String beginTime, String endTime, String pl_sn,
			String order_id, String status) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/payofflog_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		PayoffLogQueryObject qo = new PayoffLogQueryObject(currentPage, mv,
				orderBy, orderType);
		if (pl_sn != null && !pl_sn.equals("")) {
			qo.addQuery("obj.pl_sn", new SysMap("obj_pl_sn", pl_sn), "=");
			mv.addObject("pl_sn", pl_sn);
		}
		if (order_id != null && !order_id.equals("")) {
			qo.addQuery("obj.order_id", new SysMap("obj_order_id", order_id),
					"=");
			mv.addObject("order_id", order_id);
		}
		if (beginTime != null && !beginTime.equals("")) {
			qo.addQuery("obj.addTime",
					new SysMap("beginTime", CommUtil.formatDate(beginTime)),
					">=");
			mv.addObject("beginTime", beginTime);
		}
		if (endTime != null && !endTime.equals("")) {
			qo.addQuery("obj.addTime",
					new SysMap("endTime", CommUtil.formatDate(endTime)), "<=");
			mv.addObject("endTime", endTime);
		}
		int st = 0;
		if (status != null && !status.equals("")) {
			if (status.equals("not")) {
				st = 0;
			}
			if (status.equals("underway")) {
				st = 3;
			}
			if (status.equals("already")) {
				st = 6;
			}
		} else {
			status = "not";
		}
		qo.addQuery("obj.status", new SysMap("status", st), "=");
		mv.addObject("status", status);
		qo.setPageSize(20);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		qo.addQuery("obj.seller.id", new SysMap("seller_id", user.getId()), "=");
		IPageList pList = this.payoffLogService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", null, pList, mv);
		//
		Calendar a = Calendar.getInstance();
		a.set(Calendar.DATE, 1);
		a.roll(Calendar.DATE, -1);
		int maxDate = a.get(Calendar.DATE);// 当月总天数
		List list = new ArrayList();
		for (int i = 1; i <= maxDate; i++) {
			list.add(i);
		}
		SysConfig obj = configService.getSysConfig();
		String select = getSelectedDate(obj.getPayoff_count());
		String[] str = select.split(",");
		String ms = "";
		for (int i = 0; i < str.length; i++) {
			if (i + 1 == str.length) {
				ms = ms + str[i] + "日";
			} else {
				ms = ms + str[i] + "日、";
			}
		}
		mv.addObject(
				"payoff_mag_default",
				"今天是"
						+ DateFormat.getDateInstance(DateFormat.FULL,Locale.CHINESE).format(
								new Date()) + "，本月的结算日期为" + ms + "，请于结算日申请结算。");
		return mv;
	}

	private String getSelectedDate(int payoff_count) {
		Calendar a = Calendar.getInstance();
		a.set(Calendar.DATE, 1);
		a.roll(Calendar.DATE, -1);
		int allDate = a.get(Calendar.DATE);// 当月总天数
		String selected = "";
		if (payoff_count == 1) {
			selected = CommUtil.null2String(allDate);
		} else if (payoff_count == 2) {
			if (allDate == 31) {
				selected = "15,31";
			}
			if (allDate == 30) {
				selected = "15,30";
			}
			if (allDate == 29) {
				selected = "14,29";
			}
			if (allDate == 28) {
				selected = "14,28";
			}
		} else if (payoff_count == 3) {
			if (allDate == 31) {
				selected = "10,20,31";
			}
			if (allDate == 30) {
				selected = "10,20,30";
			}
			if (allDate == 29) {
				selected = "10,20,29";
			}
			if (allDate == 28) {
				selected = "10,20,28";
			}
		} else if (payoff_count == 4) {
			if (allDate == 31) {
				selected = "7,14,21,31";
			}
			if (allDate == 30) {
				selected = "7,14,21,30";
			}
			if (allDate == 29) {
				selected = "7,14,21,29";
			}
			if (allDate == 28) {
				selected = "7,14,21,28";
			}
		}
		return selected;
	}

	/**
	 * 验证今天是否为结算日，是返回true，不是返回false
	 * 
	 * @return
	 */
	private boolean validatePayoffDate() {
		boolean payoff = false;
		Date Payoff_data = this.configService.getSysConfig().getPayoff_date();
		System.out.println(Payoff_data);
		Date now = new Date();
		now.setHours(0);
		now.setMinutes(0);
		now.setSeconds(0);
		Date next = new Date();
		next.setDate(next.getDate() + 1);
		next.setHours(0);
		next.setMinutes(0);
		next.setSeconds(0);
		if (Payoff_data.after(now) && Payoff_data.before(next)) {
			payoff = true;
		}
		return payoff;
	}

	/**
	 * 可结算列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "可结算列表页", value = "/seller/payofflog_ok_list.htm*", rtype = "seller", display = false, rname = "结算管理", rcode = "payoff_seller", rgroup = "结算管理")
	@RequestMapping("/seller/payofflog_ok_list.htm")
	public ModelAndView payofflog_ok_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String beginTime, String endTime, String pl_sn,
			String order_id) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/payofflog_ok_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		boolean verify = this.validatePayoffDate();
		if (verify) {// 如果今天是结算日，更新可结算账单
			this.check_payoff_list();
		}
		PayoffLogQueryObject qo = new PayoffLogQueryObject(currentPage, mv,
				orderBy, orderType);
		if (pl_sn != null && !pl_sn.equals("")) {
			qo.addQuery("obj.pl_sn", new SysMap("obj_pl_sn", pl_sn), "=");
			mv.addObject("pl_sn", pl_sn);
		}
		if (order_id != null && !order_id.equals("")) {
			qo.addQuery("obj.order_id", new SysMap("obj_order_id", order_id),
					"=");
			mv.addObject("order_id", order_id);
		}
		if (beginTime != null && !beginTime.equals("")) {
			qo.addQuery("obj.addTime",
					new SysMap("beginTime", CommUtil.formatDate(beginTime)),
					">=");
			mv.addObject("beginTime", beginTime);
		}
		if (endTime != null && !endTime.equals("")) {
			qo.addQuery("obj.addTime",
					new SysMap("endTime", CommUtil.formatDate(endTime)), "<=");
			mv.addObject("endTime", endTime);
		}
		qo.addQuery("obj.status", new SysMap("status", 1), "=");
		qo.setPageSize(20);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		qo.addQuery("obj.seller.id", new SysMap("seller_id", user.getId()), "=");
		IPageList pList = this.payoffLogService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", null, pList, mv);
		boolean payoff = this.validatePayoffDate();
		mv.addObject("payoff", payoff);
		return mv;
	}

	private void check_payoff_list() {
		// 系统处理最近结算日期
		Map params = new HashMap();
		SysConfig sysConfig = this.configService.getSysConfig();
		params.clear();
		params.put("seller_id", SecurityUserHolder.getCurrentUser().getId());
		params.put("status", 0);
		params.put("PayoffTime", sysConfig.getPayoff_date());
		List<PayoffLog> payofflogs = this.payoffLogService
				.query("select obj from PayoffLog obj where obj.status=:status and obj.addTime<:PayoffTime and obj.seller.id=:seller_id order by addTime desc",
						params, -1, -1);// 本店结算日之前的所有未结算账单
		for (PayoffLog obj : payofflogs) {
			OrderForm of = this.orderformService.getObjById(CommUtil
					.null2Long(obj.getO_id()));
			Date Payoff_date = this.configService.getSysConfig()
					.getPayoff_date();
			Date now = new Date();
			now.setHours(0);
			now.setMinutes(0);
			now.setSeconds(0);
			Date next = new Date();
			next.setDate(next.getDate() + 1);
			next.setHours(0);
			next.setMinutes(0);
			next.setSeconds(0);
			if (of.getOrder_cat() == 2) {
				if (of.getOrder_status() == 20) {// 团购消费码订单
					obj.setStatus(1);// 设置当天可结算的账单
				}
			}
			if (of.getOrder_cat() == 0) {
				if (of.getOrder_status() == 50 || of.getOrder_status() == 65) {// 账单对应订单已经评价完成或者不可评价时
					obj.setStatus(1);// 设置当天可结算的账单
				}
				if (obj.getPayoff_type() == -1) {// 账单为退款账单，系统自动判定该退款账单为申请状态
					if (of.getOrder_status() == 50
							|| of.getOrder_status() == 65) {// 账单对应订单已经评价完成或者不可评价时
						obj.setStatus(3);
						obj.setApply_time(new Date());
					}
				}
			}
			this.payoffLogService.update(obj);
		}

	}

	/**
	 * 账单详情
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "账单详情", value = "/seller/payofflog_info.htm*", rtype = "seller", display = false, rname = "结算管理", rcode = "payoff_seller", rgroup = "结算管理")
	@RequestMapping("/seller/payofflog_info.htm")
	public ModelAndView payofflog_info(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String op) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/payofflog_info.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			PayoffLog payofflog = this.payoffLogService.getObjById(Long
					.parseLong(id));
			User user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
			user = user.getParent() == null ? user : user.getParent();
			if (user.getId().equals(payofflog.getSeller().getId())) {
				mv.addObject("payofflogTools", payofflogTools);
				mv.addObject("obj", payofflog);
				mv.addObject("currentPage", currentPage);
				mv.addObject("op", op);
			} else {
				mv.addObject("list_url", CommUtil.getURL(request)
						+ "/payofflog_list.htm");
				mv.addObject("op_title", "您没有该账单");
				mv = new JModelAndView(
						"user/default/sellercenter/seller_error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
			}
		} else {
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/payofflog_list.htm");
			mv.addObject("op_title", "账单不存在");
			mv = new JModelAndView(
					"user/default/sellercenter/seller_error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
		}
		return mv;
	}

	/**
	 * 结算账单
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "账单结算", value = "/seller/payofflog_edit.htm*", rtype = "seller", display = false, rname = "结算管理", rcode = "payoff_seller", rgroup = "结算管理")
	@RequestMapping("/seller/payofflog_edit.htm")
	public String payofflog_edit(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		for (String id : mulitId.split(",")) {
			if (id != null && !id.equals("")) {
				PayoffLog obj = this.payoffLogService.getObjById(CommUtil
						.null2Long(id));
				if (obj != null) {
					User user = this.userService.getObjById(SecurityUserHolder
							.getCurrentUser().getId());
					user = user.getParent() == null ? user : user.getParent();
					if (user.getId().equals(obj.getSeller().getId())
							&& obj.getStatus() == 1) {
						OrderForm of = this.orderFormServer.getObjById(CommUtil
								.null2Long(obj.getO_id()));
						if (of != null) {
							boolean payoff = this.validatePayoffDate();
							boolean goods = false;// 购物
							boolean group = false;// 团购
							if (of.getOrder_status() == 50 && payoff
									|| of.getOrder_status() == 65 && payoff) {
								goods = true;
							}
							if (of.getOrder_cat() == 2) {
								if (of.getOrder_status() == 20 && payoff) {// 团购消费码订单
									group = true;
								}
							}
							if (goods || group) {// 已经完成的订单，并且今天为结算日
								obj.setStatus(3);// 设置结算中
								obj.setApply_time(new Date());
								this.payoffLogService.update(obj);
							}
						}
					}
				}
			}
		}
		return "redirect:payofflog_ok_list.htm?currentPage" + currentPage;
	}

	/**
	 * 批量统计账单
	 * 
	 * @param request
	 * @param response
	 * @param mulitId
	 * @throws ClassNotFoundException
	 */
	@SecurityMapping(title = "批量统计", value = "/seller/payofflog_ajax.htm*", rtype = "seller", display = false, rname = "结算管理", rcode = "payoff_seller", rgroup = "结算管理")
	@RequestMapping("/seller/payofflog_ajax.htm")
	public void payofflog_ajax(HttpServletRequest request,
			HttpServletResponse response, String mulitId)
			throws ClassNotFoundException {
		String[] ids = mulitId.split(",");
		double order_total_price = 0.00;// 账单总订单金额
		double commission_amount = 0.00;// 账单总佣金
		double total_amount = 0.00;// 账单总结算金额
		boolean error = true;
		for (String id : ids) {
			if (!id.equals("")) {
				PayoffLog obj = this.payoffLogService.getObjById(Long
						.parseLong(id));
				if (obj != null) {
					User user = this.userService.getObjById(SecurityUserHolder
							.getCurrentUser().getId());
					user = user.getParent() == null ? user : user.getParent();
					if (user.getId().equals(obj.getSeller().getId())) {
						total_amount = CommUtil.add(total_amount,
								obj.getTotal_amount());
						commission_amount = CommUtil.add(commission_amount,
								obj.getCommission_amount());
						order_total_price = CommUtil.add(order_total_price,
								obj.getOrder_total_price());
					} else {
						error = false;
						break;
					}
				} else {
					error = false;
					break;
				}
			}
		}
		Map map = new HashMap();
		map.put("order_total_price", order_total_price);
		map.put("commission_amount", commission_amount);
		map.put("total_amount", total_amount);
		map.put("error", error);
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
}