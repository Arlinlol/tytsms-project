package com.iskyshop.manage.admin.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.virtual.OrderStat;
import com.iskyshop.foundation.domain.virtual.UserStat;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;

/**
 * 
 * <p>
 * Title: StatManageAction.java
 * </p>
 * 
 * <p>
 * Description:系统统计管理控制器，目前统计系统用户、系统订单、系统访问量
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
 * @date 2014-5-30
 * 
 * @version iskyshop_b2b2c 1.0
 */
@Controller
public class StatManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IOrderFormService orderFormService;

	@SecurityMapping(title = "用户统计", value = "/admin/stat_user.htm*", rtype = "admin", rname = "用户统计", rcode = "stat_user", rgroup = "交易")
	@RequestMapping("/admin/stat_user.htm")
	public ModelAndView stat_user(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/stat_user.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);

		return mv;
	}

	@SecurityMapping(title = "用户统计结果", value = "/admin/stat_user_done.htm*", rtype = "admin", rname = "用户统计", rcode = "stat_user", rgroup = "交易")
	@RequestMapping("/admin/stat_user_done.htm")
	public ModelAndView stat_user_done(HttpServletRequest request,
			HttpServletResponse response, String beginTime, String endTime) {
		ModelAndView mv = new JModelAndView("admin/blue/stat_user_result.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Date begin = CommUtil.formatDate(beginTime);
		Date end = CommUtil.formatDate(endTime);
		if (begin != null && end != null) {
			Map map = CommUtil.cal_time_space(begin, end);
			int day = CommUtil.null2Int(map.get("day"));
			if (day > 0 && day <= 90) {
				List<UserStat> us_list = new ArrayList<UserStat>();
				// 统计开始的第一天
				List times = new ArrayList();
				List user_counts = new ArrayList();
				List user_increase_counts = new ArrayList();
				List user_active_counts = new ArrayList();
				List user_order_counts = new ArrayList();
				List user_day_order_counts = new ArrayList();

				times.add(CommUtil.formatTime("MM-dd", begin));
				UserStat us = new UserStat();
				us.setStat_time(begin);
				Map params = new HashMap();
				params.put("addTime", begin);
				Calendar cal1 = Calendar.getInstance();
				cal1.setTime(begin);
				cal1.add(Calendar.DAY_OF_YEAR, 1);
				params.put("addTime1", cal1.getTime());
				List<User> users = this.userService
						.query("select obj from User obj where obj.addTime>=:addTime and obj.addTime<:addTime1",
								params, -1, -1);
				us.setUser_increase_count(users.size());
				params.clear();
				params.put("addTime1", cal1.getTime());
				users = this.userService.query(
						"select obj from User obj where obj.addTime<:addTime1",
						params, -1, -1);
				us.setUser_count(users.size());
				params.clear();
				params.put("addTime", begin);
				params.put("addTime1", cal1.getTime());
				users = this.userService
						.query("select obj from User obj where obj.loginDate>=:addTime and obj.loginDate<:addTime1",
								params, -1, -1);
				us.setUser_active_count(users.size());
				List<String> user_ids = this.orderFormService
						.queryFromOrderForm(
								"select distinct(obj.user_id) from OrderForm obj where obj.payTime>=:addTime and obj.payTime<:addTime1",
								params, -1, -1);
				us.setUser_order_count(user_ids.size());
				user_counts.add(us.getUser_count());
				user_increase_counts.add(us.getUser_increase_count());
				user_active_counts.add(us.getUser_active_count());
				user_order_counts.add(us.getUser_order_count());
				user_day_order_counts.add(us.getUser_day_order_count());
				for (String user_id : user_ids) {
					User user = this.userService.getObjById(CommUtil
							.null2Long(user_id));
					if (user.getAddTime().after(begin)
							&& user.getAddTime().before(cal1.getTime())) {
						us.setUser_day_order_count(us.getUser_day_order_count() + 1);
					}
				}
				us_list.add(us);
				Calendar cal = Calendar.getInstance();
				cal.setTime(begin);
				for (int i = 0; i <= day - 1; i++) {// 统计区间所有时间段
					cal.add(Calendar.DAY_OF_YEAR, 1);
					times.add(CommUtil.formatTime("MM-dd", cal.getTime()));
					us = new UserStat();
					us.setStat_time(cal.getTime());
					params.clear();
					params.put("addTime", cal.getTime());
					cal1 = Calendar.getInstance();
					cal1.setTime(cal.getTime());
					cal1.add(Calendar.DAY_OF_YEAR, 1);
					params.put("addTime1", cal1.getTime());
					users = this.userService
							.query("select obj from User obj where obj.addTime>=:addTime and obj.addTime<:addTime1",
									params, -1, -1);
					us.setUser_increase_count(users.size());
					params.clear();
					params.put("addTime1", cal1.getTime());
					users = this.userService
							.query("select obj from User obj where obj.addTime<:addTime1",
									params, -1, -1);
					us.setUser_count(users.size());
					params.clear();
					params.put("addTime", cal.getTime());
					params.put("addTime1", cal1.getTime());
					users = this.userService
							.query("select obj from User obj where obj.loginDate>=:addTime and obj.loginDate<:addTime1",
									params, -1, -1);
					us.setUser_active_count(users.size());
					user_ids = this.orderFormService
							.queryFromOrderForm(
									"select distinct(obj.user_id) from OrderForm obj where obj.payTime>=:addTime and obj.payTime<:addTime1",
									params, -1, -1);
					us.setUser_order_count(user_ids.size());
					for (String user_id : user_ids) {
						User user = this.userService.getObjById(CommUtil
								.null2Long(user_id));
						if (user.getAddTime().after(cal.getTime())
								&& user.getAddTime().before(cal1.getTime())) {
							us.setUser_day_order_count(us
									.getUser_day_order_count() + 1);
						}
					}
					us_list.add(us);
					user_counts.add(us.getUser_count());
					user_increase_counts.add(us.getUser_increase_count());
					user_active_counts.add(us.getUser_active_count());
					user_order_counts.add(us.getUser_order_count());
					user_day_order_counts.add(us.getUser_day_order_count());
				}
				mv.addObject("uss", us_list);
				mv.addObject("stat_title", "商城用户统计图");
				mv.addObject("begin", begin);
				mv.addObject("end", end);
				// System.out.println(Json.toJson(times, JsonFormat.compact()));
				mv.addObject("times", Json.toJson(times, JsonFormat.compact()));
				mv.addObject("user_counts",
						Json.toJson(user_counts, JsonFormat.compact()));
				mv.addObject("user_increase_counts",
						Json.toJson(user_increase_counts, JsonFormat.compact()));
				mv.addObject("user_active_counts",
						Json.toJson(user_active_counts, JsonFormat.compact()));
				mv.addObject("user_order_counts",
						Json.toJson(user_order_counts, JsonFormat.compact()));
				mv.addObject("user_day_order_counts", Json.toJson(
						user_day_order_counts, JsonFormat.compact()));
			} else if (day < 0) {
				mv = new JModelAndView("admin/blue/error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				mv.addObject("op_title", "结束日期必须迟于开始日期");
				mv.addObject("list_url", CommUtil.getURL(request)
						+ "/admin/stat_user.htm");
			} else if (day > 90) {
				mv = new JModelAndView("admin/blue/error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				mv.addObject("op_title", "统计日期间隔不能超过90天");
				mv.addObject("list_url", CommUtil.getURL(request)
						+ "/admin/stat_user.htm");
			}
		} else {
			mv = new JModelAndView("admin/blue/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "统计日期不能为空");
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/admin/stat_user.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "订单统计", value = "/admin/stat_order.htm*", rtype = "admin", rname = "订单统计", rcode = "stat_order", rgroup = "交易")
	@RequestMapping("/admin/stat_order.htm")
	public ModelAndView stat_order(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/stat_order.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);

		return mv;
	}

	@SecurityMapping(title = "订单统计", value = "/admin/stat_order_done.htm*", rtype = "admin", rname = "订单统计", rcode = "stat_order", rgroup = "交易")
	@RequestMapping("/admin/stat_order_done.htm")
	public ModelAndView stat_order_done(HttpServletRequest request,
			HttpServletResponse response, String beginTime, String endTime) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/stat_order_result.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Date begin = CommUtil.formatDate(beginTime);
		Date end = CommUtil.formatDate(endTime);
		if (begin != null && end != null) {
			Map map = CommUtil.cal_time_space(begin, end);
			int day = CommUtil.null2Int(map.get("day"));
			if (day > 0 && day <= 90) {
				List<OrderStat> oss = new ArrayList<OrderStat>();
				OrderStat os = new OrderStat();
				List times = new ArrayList();
				List order_counts = new ArrayList();
				List order_pay_counts = new ArrayList();
				List order_ship_counts = new ArrayList();
				List order_amounts = new ArrayList();
				times.add(CommUtil.formatTime("MM-dd", begin));
				os.setStat_time(begin);
				Map params = new HashMap();
				Calendar cal1 = Calendar.getInstance();
				params.put("addTime", begin);
				cal1 = Calendar.getInstance();
				cal1.setTime(begin);
				cal1.add(Calendar.DAY_OF_YEAR, 1);
				params.put("addTime1", cal1.getTime());
				List<OrderForm> ofs = this.orderFormService
						.query("select obj from OrderForm obj where obj.addTime>=:addTime and obj.addTime<:addTime1",
								params, -1, -1);
				os.setOrder_count(ofs.size());
				ofs = this.orderFormService
						.query("select obj from OrderForm obj where obj.payTime>=:addTime and obj.payTime<:addTime1",
								params, -1, -1);
				os.setOrder_pay_count(ofs.size());
				ofs = this.orderFormService
						.query("select obj from OrderForm obj where obj.shipTime>=:addTime and obj.shipTime<:addTime1",
								params, -1, -1);
				os.setOrder_ship_count(ofs.size());
				List amounts = this.orderFormService
						.queryFromOrderForm(
								"select sum(obj.totalPrice) from OrderForm obj where obj.addTime>=:addTime and obj.addTime<:addTime1 ",
								params, -1, -1);
				System.out.println(CommUtil.null2Float(amounts.get(0)));
				os.setOrder_amount(CommUtil.null2Float(amounts.get(0)));
				order_counts.add(os.getOrder_count());
				order_pay_counts.add(os.getOrder_pay_count());
				order_ship_counts.add(os.getOrder_ship_count());
				order_amounts.add(os.getOrder_amount());
				oss.add(os);
				Calendar cal = Calendar.getInstance();
				cal.setTime(begin);
				for (int i = 0; i <= day - 1; i++) {// 统计区间所有时间段
					cal.add(Calendar.DAY_OF_YEAR, 1);
					times.add(CommUtil.formatTime("MM-dd", cal.getTime()));
					os = new OrderStat();
					//
					params.clear();
					params.put("addTime", cal.getTime());
					cal1 = Calendar.getInstance();
					cal1.setTime(cal.getTime());
					cal1.add(Calendar.DAY_OF_YEAR, 1);
					params.put("addTime1", cal1.getTime());

					ofs = this.orderFormService
							.query("select obj from OrderForm obj where obj.addTime>=:addTime and obj.addTime<:addTime1",
									params, -1, -1);
					os.setOrder_count(ofs.size());
					ofs = this.orderFormService
							.query("select obj from OrderForm obj where obj.payTime>=:addTime and obj.payTime<:addTime1",
									params, -1, -1);
					os.setOrder_pay_count(ofs.size());
					ofs = this.orderFormService
							.query("select obj from OrderForm obj where obj.shipTime>=:addTime and obj.shipTime<:addTime1",
									params, -1, -1);
					os.setOrder_ship_count(ofs.size());
					amounts = this.orderFormService
							.queryFromOrderForm(
									"select sum(obj.totalPrice) from OrderForm obj where obj.addTime>=:addTime and obj.addTime<:addTime1 ",
									params, -1, -1);
					System.out.println(CommUtil.null2Float(amounts.get(0)));
					os.setOrder_amount(CommUtil.null2Float(amounts.get(0)));
					//
					os.setStat_time(cal.getTime());
					order_counts.add(os.getOrder_count());
					order_pay_counts.add(os.getOrder_pay_count());
					order_ship_counts.add(os.getOrder_ship_count());
					order_amounts.add(os.getOrder_amount());
					oss.add(os);
				}
				mv.addObject("stat_title", "商城订单统计图");
				mv.addObject("begin", begin);
				mv.addObject("end", end);
				mv.addObject("times", Json.toJson(times, JsonFormat.compact()));
				mv.addObject("order_counts",
						Json.toJson(order_counts, JsonFormat.compact()));
				mv.addObject("order_ship_counts",
						Json.toJson(order_ship_counts, JsonFormat.compact()));
				mv.addObject("order_pay_counts",
						Json.toJson(order_pay_counts, JsonFormat.compact()));
				mv.addObject("order_amounts",
						Json.toJson(order_amounts, JsonFormat.compact()));
				mv.addObject("oss", oss);

			} else if (day < 0) {
				mv = new JModelAndView("admin/blue/error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				mv.addObject("op_title", "结束日期必须迟于开始日期");
				mv.addObject("list_url", CommUtil.getURL(request)
						+ "/admin/stat_user.htm");
			} else if (day > 90) {
				mv = new JModelAndView("admin/blue/error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				mv.addObject("op_title", "统计日期间隔不能超过90天");
				mv.addObject("list_url", CommUtil.getURL(request)
						+ "/admin/stat_user.htm");
			}
		} else {
			mv = new JModelAndView("admin/blue/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "统计日期不能为空");
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/admin/stat_user.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "访问统计", value = "/admin/stat_visit.htm*", rtype = "admin", rname = "订单统计", rcode = "stat_order", rgroup = "交易")
	@RequestMapping("/admin/stat_visit.htm")
	public ModelAndView stat_visit(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/stat_visit.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);

		return mv;
	}
}
