package com.iskyshop.manage.admin.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GroupInfo;
import com.iskyshop.foundation.domain.Message;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.PayoffLog;
import com.iskyshop.foundation.domain.Predeposit;
import com.iskyshop.foundation.domain.PredepositLog;
import com.iskyshop.foundation.domain.RefundLog;
import com.iskyshop.foundation.domain.ReturnGoodsLog;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.PredepositQueryObject;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGroupInfoService;
import com.iskyshop.foundation.service.IMessageService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IPayoffLogService;
import com.iskyshop.foundation.service.IPredepositLogService;
import com.iskyshop.foundation.service.IPredepositService;
import com.iskyshop.foundation.service.IRefundLogService;
import com.iskyshop.foundation.service.IReturnGoodsLogService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.OrderFormTools;

/**
 * 
 * <p>
 * Title: PredepositManageAction.java
 * </p>
 * 
 * <p>
 * Description: 平台预存款管理控制器，用来显示预存款信息、审核预存款、修改预存款等所有系统预存款操作
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
 * @date 2014-5-26
 * 
 * @version iskyshop_b2b2c 1.0
 */
@Controller
public class PredepositManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IPredepositService predepositService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IPredepositLogService predepositLogService;
	@Autowired
	private IReturnGoodsLogService returnGoodsLogService;
	@Autowired
	private IRefundLogService refundLogService;
	@Autowired
	private IPayoffLogService payoffLogService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private OrderFormTools orderFormTools;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGroupInfoService groupinfoService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IMessageService messageService;

	/**
	 * Predeposit列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "预存款列表", value = "/admin/predeposit_list.htm*", rtype = "admin", rname = "预存款管理", rcode = "predeposit", rgroup = "会员")
	@RequestMapping("/admin/predeposit_list.htm")
	public ModelAndView predeposit_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String pd_payment, String pd_pay_status,
			String pd_status, String pd_userName, String beginTime,
			String endTime, String pd_remittance_user, String pd_remittance_bank) {
		ModelAndView mv = new JModelAndView("admin/blue/predeposit_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (this.configService.getSysConfig().isDeposit()) {
			String url = this.configService.getSysConfig().getAddress();
			if (url == null || url.equals("")) {
				url = CommUtil.getURL(request);
			}
			PredepositQueryObject qo = new PredepositQueryObject(currentPage,
					mv, orderBy, orderType);
			if (!CommUtil.null2String(pd_payment).equals("")) {
				qo.addQuery("obj.pd_payment", new SysMap("pd_payment",
						pd_payment), "=");
			}
			if (!CommUtil.null2String(pd_pay_status).equals("")) {
				qo.addQuery("obj.pd_pay_status", new SysMap("pd_pay_status",
						CommUtil.null2Int(pd_pay_status)), "=");
			}
			if (!CommUtil.null2String(pd_status).equals("")) {
				qo.addQuery("obj.pd_status",
						new SysMap("pd_status", CommUtil.null2Int(pd_status)),
						"=");
			}
			if (!CommUtil.null2String(pd_remittance_user).equals("")) {
				qo.addQuery("obj.pd_remittance_user", new SysMap(
						"pd_remittance_user", pd_remittance_user), "=");
			}
			if (!CommUtil.null2String(pd_remittance_bank).equals("")) {
				qo.addQuery("obj.pd_remittance_bank", new SysMap(
						"pd_remittance_bank", pd_remittance_bank), "=");
			}
			if (!CommUtil.null2String(pd_userName).equals("")) {
				qo.addQuery("obj.pd_user.userName", new SysMap("pd_userName",
						pd_userName), "=");
			}
			if (!CommUtil.null2String(beginTime).equals("")) {
				qo.addQuery(
						"obj.addTime",
						new SysMap("beginTime", CommUtil.formatDate(beginTime)),
						">=");
			}
			if (!CommUtil.null2String(endTime).equals("")) {
				qo.addQuery("obj.addTime",
						new SysMap("endTime", CommUtil.formatDate(endTime)),
						"<=");
			}
			IPageList pList = this.predepositService.list(qo);
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
			mv.addObject("pd_payment", pd_payment);
			mv.addObject("pd_pay_status", pd_pay_status);
			mv.addObject("pd_status", pd_status);
			mv.addObject("pd_userName", pd_userName);
			mv.addObject("beginTime", beginTime);
			mv.addObject("endTime", endTime);
			mv.addObject("pd_remittance_user", pd_remittance_user);
			mv.addObject("pd_remittance_bank", pd_remittance_bank);
		} else {
			mv = new JModelAndView("admin/blue/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未开启预存款");
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/admin/operation_base_set.htm");
		}
		return mv;
	}

	/**
	 * predeposit添加管理
	 * 
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "预存款列表", value = "/admin/predeposit_list.htm*", rtype = "admin", rname = "预存款管理", rcode = "predeposit", rgroup = "会员")
	@RequestMapping("/admin/predeposit_view.htm")
	public ModelAndView predeposit_view(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("admin/blue/predeposit_view.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (this.configService.getSysConfig().isDeposit()) {
			Predeposit obj = this.predepositService.getObjById(CommUtil
					.null2Long(id));
			mv.addObject("obj", obj);
		} else {
			mv = new JModelAndView("admin/blue/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未开启预存款");
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/admin/operation_base_set.htm");
		}
		return mv;
	}

	/**
	 * predeposit编辑管理
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "预存款编辑", value = "/admin/predeposit_edit.htm*", rtype = "admin", rname = "预存款管理", rcode = "predeposit", rgroup = "会员")
	@RequestMapping("/admin/predeposit_edit.htm")
	public ModelAndView predeposit_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/predeposit_edit.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (this.configService.getSysConfig().isDeposit()) {
			if (id != null && !id.equals("")) {
				Predeposit predeposit = this.predepositService.getObjById(Long
						.parseLong(id));
				mv.addObject("obj", predeposit);
				mv.addObject("currentPage", currentPage);
				mv.addObject("edit", true);
			}
		} else {
			mv = new JModelAndView("admin/blue/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未开启预存款");
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/admin/operation_base_set.htm");
		}
		return mv;
	}

	/**
	 * predeposit保存管理
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "预存款保存", value = "/admin/predeposit_save.htm*", rtype = "admin", rname = "预存款管理", rcode = "predeposit", rgroup = "会员")
	@RequestMapping("/admin/predeposit_save.htm")
	public ModelAndView predeposit_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String cmd, String list_url) {
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (this.configService.getSysConfig().isDeposit()) {
			WebForm wf = new WebForm();
			Predeposit obj = this.predepositService.getObjById(Long
					.parseLong(id));
			Predeposit predeposit = (Predeposit) wf.toPo(request, obj);
			predeposit.setPd_admin(SecurityUserHolder.getCurrentUser());
			this.predepositService.update(predeposit);
			if (predeposit.getPd_status() == 1) {
				User pd_user = predeposit.getPd_user();
				pd_user.setAvailableBalance(BigDecimal.valueOf(CommUtil.add(
						pd_user.getAvailableBalance(),
						predeposit.getPd_amount())));
				this.userService.update(pd_user);
			}
			// 保存充值日志
			PredepositLog log = obj.getLog();
			log.setPd_log_admin(SecurityUserHolder.getCurrentUser());
			this.predepositLogService.update(log);

			mv.addObject("list_url", list_url);
			mv.addObject("op_title", "审核预存款成功");
		} else {
			mv = new JModelAndView("admin/blue/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未开启预存款");
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/admin/operation_base_set.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "预存款手动修改", value = "/admin/predeposit_modify.htm*", rtype = "admin", rname = "预存款管理", rcode = "predeposit", rgroup = "会员")
	@RequestMapping("/admin/predeposit_modify.htm")
	public ModelAndView predeposit_modify(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/predeposit_modify.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (!this.configService.getSysConfig().isDeposit()) {
			mv = new JModelAndView("admin/blue/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未开启预存款");
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/admin/operation_base_set.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "加载用户预存款信息", value = "/admin/predeposit_user.htm*", rtype = "admin", rname = "预存款管理", rcode = "predeposit", rgroup = "会员")
	@RequestMapping("/admin/predeposit_user.htm")
	public void predeposit_user(HttpServletRequest request,
			HttpServletResponse response, String userName) {
		User user = this.userService.getObjByProperty("userName", userName);
		Map map = new HashMap();
		if (user != null) {
			map.put("freezeBlance",
					CommUtil.null2Double(user.getFreezeBlance()));
			map.put("availableBalance",
					CommUtil.null2Double(user.getAvailableBalance()));
			map.put("id", user.getId());
			map.put("status", "success");
		} else {
			map.put("status", "error");
		}
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

	@SecurityMapping(title = "预存款手动修改保存", value = "/admin/predeposit_modify_save.htm*", rtype = "admin", rname = "预存款管理", rcode = "predeposit", rgroup = "会员")
	@RequestMapping("/admin/predeposit_modify_save.htm")
	@Transactional
	public ModelAndView predeposit_modify_save(HttpServletRequest request,
			HttpServletResponse response, String user_id, String amount,
			String type, String info, String list_url, String refund_user_id,
			String obj_id, String gi_id) {
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (this.configService.getSysConfig().isDeposit()) {
			User user = null;
			if (user_id != null && !user_id.equals("")) {
				user = this.userService.getObjById(CommUtil.null2Long(user_id));
			} else {
				user = this.userService.getObjById(CommUtil
						.null2Long(refund_user_id));
			}

			if (type.equals("free")) {
				user.setFreezeBlance(BigDecimal.valueOf(CommUtil.add(
						user.getFreezeBlance(), amount)));
			} else {
				user.setAvailableBalance(BigDecimal.valueOf(CommUtil.add(
						user.getAvailableBalance(), amount)));
			}
			this.userService.update(user);
			// 保存充值日志
			PredepositLog log = new PredepositLog();
			log.setPd_log_admin(SecurityUserHolder.getCurrentUser());
			log.setAddTime(new Date());
			log.setPd_log_amount(BigDecimal.valueOf(CommUtil
					.null2Double(amount)));
			log.setPd_log_info(info);
			log.setPd_log_user(user);
			log.setPd_op_type("手动修改");
			if (type.equals("free")) {
				log.setPd_type("冻结预存款");
			} else {
				if (refund_user_id != null && !refund_user_id.equals("")) {
					log.setPd_op_type("人工退款");
					log.setPd_type("可用预存款");
				} else {
					log.setPd_type("可用预存款");
				}
			}
			this.predepositLogService.save(log);
			mv.addObject("list_url", list_url);
			mv.addObject("op_title", "审核预存款成功");
			if (obj_id != null && !obj_id.equals("")) {// 商品退款
				ReturnGoodsLog rgl = this.returnGoodsLogService
						.getObjById(CommUtil.null2Long(obj_id));
				rgl.setRefund_status(1);
				rgl.setGoods_return_status("10");
				this.returnGoodsLogService.update(rgl);
				RefundLog r_log = new RefundLog();
				r_log.setAddTime(new Date());
				r_log.setRefund_id(CommUtil.formatTime("yyyyMMddHHmmss",
						new Date()) + user.getId());
				r_log.setReturnLog_id(rgl.getId());
				r_log.setReturnService_id(rgl.getReturn_service_id());
				r_log.setRefund(BigDecimal.valueOf(CommUtil.null2Double(amount)));
				r_log.setRefund_log(info);
				r_log.setRefund_type("预存款");
				r_log.setRefund_user(SecurityUserHolder.getCurrentUser());
				r_log.setReturnLog_userName(rgl.getUser_name());
				r_log.setReturnLog_userId(rgl.getUser_id());
				this.refundLogService.save(r_log);
				OrderForm of = this.orderFormService.getObjById(rgl
						.getReturn_order_id());
				Goods goods = this.goodsService.getObjById(rgl.getGoods_id());
				// 如果为自营商品时不添加结算日志，只有第三方经销商的商品才有结算日志
				if (goods.getGoods_type() == 1) {
					Store store = this.goodsService.getObjById(
							rgl.getGoods_id()).getGoods_store();
					PayoffLog pol = new PayoffLog();
					pol.setPl_sn("pl"
							+ CommUtil.formatTime("yyyyMMddHHmmss", new Date())
							+ store.getUser().getId());
					pol.setAddTime(new Date());
					pol.setGoods_info(of.getReturn_goods_info());
					pol.setRefund_user_id(rgl.getUser_id());
					pol.setSeller(goods.getGoods_store().getUser());
					pol.setRefund_userName(rgl.getUser_name());
					pol.setReturn_service_id(rgl.getReturn_service_id());
					pol.setPayoff_type(-1);
					pol.setPl_info("退款完成");
					BigDecimal price = BigDecimal.valueOf(CommUtil
							.null2Double(amount)); // 商品的原价
					BigDecimal mission = BigDecimal.valueOf(CommUtil.subtract(
							1, rgl.getGoods_commission_rate()));// 商品的佣金比例
					BigDecimal final_money = BigDecimal.valueOf(CommUtil
							.subtract(0, CommUtil.mul(price, mission)));
					pol.setTotal_amount(final_money);
					List<Map> list = new ArrayList<Map>();
					Map json = new HashMap();
					json.put("goods_id", rgl.getGoods_id());
					json.put("goods_name", rgl.getGoods_name());
					json.put("goods_price", rgl.getGoods_price());
					json.put("goods_mainphoto_path",
							rgl.getGoods_mainphoto_path());
					json.put("goods_commission_rate",
							rgl.getGoods_commission_rate());
					json.put("goods_count", rgl.getGoods_count());
					json.put("goods_all_price", rgl.getGoods_all_price());
					json.put(
							"goods_commission_price",
							CommUtil.mul(rgl.getGoods_all_price(),
									rgl.getGoods_commission_rate()));
					json.put("goods_payoff_price", final_money);
					list.add(json);
					pol.setReturn_goods_info(Json.toJson(list,
							JsonFormat.compact()));
					pol.setO_id(CommUtil.null2String(rgl.getReturn_order_id()));
					pol.setOrder_id(of.getOrder_id());
					pol.setCommission_amount(BigDecimal.valueOf(0));
					pol.setOrder_total_price(final_money);
					this.payoffLogService.save(pol);
					store.setStore_sale_amount(BigDecimal.valueOf(CommUtil
							.subtract(store.getStore_sale_amount(), amount)));// 减少店铺本次结算总销售金额
					store.setStore_payoff_amount(BigDecimal.valueOf(CommUtil
							.subtract(store.getStore_payoff_amount(),
									CommUtil.mul(price, mission))));// 减少店铺本次结算总金额
					this.storeService.update(store);
					// 减少系统总销售金额、总结算金额
					SysConfig sc = this.configService.getSysConfig();
					sc.setPayoff_all_sale(BigDecimal.valueOf(CommUtil.subtract(
							sc.getPayoff_all_sale(), amount)));
					sc.setPayoff_all_amount(BigDecimal.valueOf(CommUtil
							.subtract(sc.getPayoff_all_amount(),
									CommUtil.mul(price, mission))));
					sc.setPayoff_all_amount_reality(BigDecimal.valueOf(CommUtil
							.add(pol.getReality_amount(),
									sc.getPayoff_all_amount_reality())));// 增加系统实际总结算
					this.configService.update(sc);
				}
				String msg_content = "成功为订单号：" + of.getOrder_id() + "退款"
						+ amount + "元，请到收支明细中查看。";
				// 发送系统站内信
				Message msg = new Message();
				msg.setAddTime(new Date());
				msg.setStatus(0);
				msg.setType(0);
				msg.setContent(msg_content);
				msg.setFromUser(SecurityUserHolder.getCurrentUser());
				msg.setToUser(user);
				this.messageService.save(msg);
			}
			if (gi_id != null && !gi_id.equals("")) {// 消费码退款
				GroupInfo gi = this.groupinfoService.getObjById(CommUtil
						.null2Long(gi_id));
				gi.setStatus(7);// 退款完成
				this.groupinfoService.update(gi);
				OrderForm of = this.orderFormService.getObjById(gi
						.getOrder_id());
				if (of.getOrder_form() == 0) {// 商家订单生成退款结算账单
					Store store = this.storeService.getObjById(CommUtil
							.null2Long(of.getStore_id()));
					PayoffLog pol = new PayoffLog();
					pol.setPl_sn("pl"
							+ CommUtil.formatTime("yyyyMMddHHmmss", new Date())
							+ store.getUser().getId());
					pol.setAddTime(new Date());
					pol.setGoods_info(of.getReturn_goods_info());
					pol.setRefund_user_id(gi.getUser_id());
					pol.setSeller(store.getUser());
					pol.setRefund_userName(gi.getUser_name());
					pol.setPayoff_type(-1);
					pol.setPl_info("退款完成");
					BigDecimal price = BigDecimal.valueOf(CommUtil
							.null2Double(amount)); // 商品的原价
					BigDecimal final_money = BigDecimal.valueOf(CommUtil
							.subtract(0, price));
					pol.setTotal_amount(final_money);
					// 将订单中group_info（{}）转换为List<Map>([{}])
					List<Map> Map_list = new ArrayList<Map>();
					Map group_map = this.orderFormTools.queryGroupInfo(of
							.getGroup_info());
					Map_list.add(group_map);
					pol.setReturn_goods_info(Json.toJson(Map_list,
							JsonFormat.compact()));
					pol.setO_id(of.getId().toString());
					pol.setOrder_id(of.getOrder_id());
					pol.setCommission_amount(BigDecimal.valueOf(0));
					pol.setOrder_total_price(final_money);
					this.payoffLogService.save(pol);
					store.setStore_sale_amount(BigDecimal.valueOf(CommUtil
							.subtract(store.getStore_sale_amount(), amount)));// 减少店铺本次结算总销售金额
					store.setStore_payoff_amount(BigDecimal.valueOf(CommUtil
							.subtract(store.getStore_payoff_amount(), price)));// 减少店铺本次结算总金额
					this.storeService.update(store);
					// 减少系统总销售金额、总结算金额
					SysConfig sc = this.configService.getSysConfig();
					sc.setPayoff_all_sale(BigDecimal.valueOf(CommUtil.subtract(
							sc.getPayoff_all_sale(), amount)));
					sc.setPayoff_all_amount(BigDecimal.valueOf(CommUtil
							.subtract(sc.getPayoff_all_amount(),
									CommUtil.mul(amount, 0))));
					sc.setPayoff_all_amount_reality(BigDecimal.valueOf(CommUtil
							.add(pol.getReality_amount(),
									sc.getPayoff_all_amount_reality())));// 增加系统实际总结算
					this.configService.update(sc);
				}
				String msg_content = "您的团购商品：" + gi.getLifeGoods().getGg_name()
						+ "消费码已经成功退款，退款金额为："
						+ gi.getLifeGoods().getGroup_price() + "，退款消费码:"
						+ gi.getGroup_sn();
				// 发送系统站内信
				Message msg = new Message();
				msg.setAddTime(new Date());
				msg.setStatus(0);
				msg.setType(0);
				msg.setContent(msg_content);
				msg.setFromUser(SecurityUserHolder.getCurrentUser());
				msg.setToUser(user);
				this.messageService.save(msg);
			}
		} else {
			mv = new JModelAndView("admin/blue/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未开启预存款");
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/admin/operation_base_set.htm");
		}
		return mv;
	}
}