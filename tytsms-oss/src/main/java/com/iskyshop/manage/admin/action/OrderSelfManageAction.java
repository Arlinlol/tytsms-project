package com.iskyshop.manage.admin.action;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.ExpressCompany;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.OrderFormLog;
import com.iskyshop.foundation.domain.Payment;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.OrderFormQueryObject;
import com.iskyshop.foundation.domain.virtual.TransInfo;
import com.iskyshop.foundation.service.IEvaluateService;
import com.iskyshop.foundation.service.IExpressCompanyService;
import com.iskyshop.foundation.service.IGoodsCartService;
import com.iskyshop.foundation.service.IGoodsReturnService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGroupGoodsService;
import com.iskyshop.foundation.service.IIntegralLogService;
import com.iskyshop.foundation.service.IOrderFormLogService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IPaymentService;
import com.iskyshop.foundation.service.IRefundLogService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.ITemplateService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.MsgTools;
import com.iskyshop.manage.admin.tools.OrderFormTools;
import com.iskyshop.manage.admin.tools.PaymentTools;
import com.iskyshop.manage.admin.tools.SendMsgAndEmTools;
import com.iskyshop.pay.alipay.config.AlipayConfig;
import com.iskyshop.pay.alipay.util.AlipaySubmit;
import com.iskyshop.pay.wechatpay.util.ConfigContants;
import com.iskyshop.pay.wechatpay.util.TytsmsStringUtils;
import com.iskyshop.view.web.tools.StoreViewTools;

/**
 * 
 * 
 * <p>
 * Title: OrderSelfManageAction.java
 * </p>
 * 
 * <p>
 * Description: 自营商品订单管理器，显示所有自营商品订单，添加权限的管理员都可进行管理。
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
 * @author jinxinzhe
 * 
 * @date 2014年4月24日
 * 
 * @version 1.0
 */
@Controller
public class OrderSelfManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IOrderFormLogService orderFormLogService;
	@Autowired
	private IRefundLogService refundLogService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGoodsReturnService goodsReturnService;
	@Autowired
	private IGoodsCartService goodsCartService;
	@Autowired
	private IEvaluateService evaluateService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IIntegralLogService integralLogService;
	@Autowired
	private IGroupGoodsService groupGoodsService;
	@Autowired
	private ITemplateService templateService;
	@Autowired
	private IPaymentService paymentService;
	@Autowired
	private IExpressCompanyService expressCompanyService;
	@Autowired
	private StoreViewTools storeViewTools;
	@Autowired
	private MsgTools msgTools;
	@Autowired
	private PaymentTools paymentTools;
	@Autowired
	private OrderFormTools orderFormTools;
	@Autowired
	private SendMsgAndEmTools sendMsgAndEmTools;

	@SecurityMapping(title = "自营订单列表", value = "/admin/self_order.htm*", rtype = "admin", rname = "自营订单管理", rcode = "order_self", rgroup = "自营")
	@RequestMapping("/admin/self_order.htm")
	public ModelAndView order(HttpServletRequest request,
			HttpServletResponse response, String currentPage,
			String order_status, String order_id, String beginTime,
			String endTime, String buyer_userName) {
		ModelAndView mv = new JModelAndView("admin/blue/self_order.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderFormQueryObject ofqo = new OrderFormQueryObject(currentPage, mv,
				"addTime", "desc");
		ofqo.addQuery("obj.order_form", new SysMap("order_form", 1), "=");
		ofqo.addQuery("obj.order_cat", new SysMap("order_cat", 2), "!=");
		if (!CommUtil.null2String(order_status).equals("")) {
			if (order_status.equals("order_submit")) {// 已经提交
				Map map = new HashMap();
				map.put("order_status1", 10);
				map.put("order_status2", 16);
				ofqo.addQuery(
						"(obj.order_status=:order_status1 or obj.order_status=:order_status2)",
						map);
			}
			if (order_status.equals("order_pay")) {// 已经付款
				Map map = new HashMap();
				map.put("order_status1", 20);
				map.put("order_status2", 16);
				ofqo.addQuery(
						"(obj.order_status=:order_status1 or obj.order_status=:order_status2)",
						map);
			}
			if (order_status.equals("order_shipping")) {// 已经发货
				ofqo.addQuery("obj.order_status",
						new SysMap("order_status", 30), "=");
			}
			if (order_status.equals("order_evaluate")) {// 买家已评价
				ofqo.addQuery("obj.order_status",
						new SysMap("order_status", 40), "=");
			}
			if (order_status.equals("order_finish")) {// 已经完成
				ofqo.addQuery("obj.order_status",
						new SysMap("order_status", 50), "=");
			}
			if (order_status.equals("order_cancel")) {// 已经取消
				ofqo.addQuery("obj.order_status",
						new SysMap("order_status", 0), "=");
			}
		}
		if (!CommUtil.null2String(order_id).equals("")) {
			ofqo.addQuery("obj.order_id", new SysMap("order_id", "%" + order_id
					+ "%"), "like");
		}
		if (!CommUtil.null2String(beginTime).equals("")) {
			ofqo.addQuery("obj.addTime",
					new SysMap("beginTime", CommUtil.formatDate(beginTime)),
					">=");
			mv.addObject("beginTime", beginTime);
		}
		if (!CommUtil.null2String(endTime).equals("")) {
			String ends = endTime + " 23:59:59";
			ofqo.addQuery(
					"obj.addTime",
					new SysMap("endTime", CommUtil.formatDate(ends,
							"yyyy-MM-dd hh:mm:ss")), "<=");
			mv.addObject("endTime", endTime);
		}
		if (!CommUtil.null2String(buyer_userName).equals("")) {
			ofqo.addQuery("obj.user_name", new SysMap("user_name",
					buyer_userName), "=");
		}
		IPageList pList = this.orderFormService.list(ofqo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("order_id", order_id);
		mv.addObject("order_status", order_status == null ? "all"
				: order_status);
		mv.addObject("beginTime", beginTime);
		mv.addObject("endTime", endTime);
		mv.addObject("buyer_userName", buyer_userName);
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/admin/self_order.htm");
		mv.addObject("orderFormTools", orderFormTools);
		return mv;
	}

	@SecurityMapping(title = "调整订单费用", value = "/admin/order_fee.htm*", rtype = "admin", rname = "自营订单管理", rcode = "order_self", rgroup = "自营")
	@RequestMapping("/admin/order_fee.htm")
	public ModelAndView order_fee(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/admin_order_fee.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		if (1 == obj.getOrder_form()) {
			mv.addObject("obj", obj);
			mv.addObject("currentPage", currentPage);
		} else {
			mv = new JModelAndView("admin/blue/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "您没有编号为" + id + "的订单！");
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/admin/self_order.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "调整订单费用保存", value = "/admin/order_fee_save.htm*", rtype = "admin", rname = "自营订单管理", rcode = "order_self", rgroup = "自营")
	@RequestMapping("/admin/order_fee_save.htm")
	public String order_fee_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String goods_amount, String ship_price, String totalPrice)
			throws Exception {
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		if (1 == obj.getOrder_form()) {
			obj.setGoods_amount(BigDecimal.valueOf(CommUtil
					.null2Double(goods_amount)));
			obj.setShip_price(BigDecimal.valueOf(CommUtil
					.null2Double(ship_price)));
			obj.setTotalPrice(BigDecimal.valueOf(CommUtil
					.null2Double(totalPrice)));
			obj.setOperation_price_count(obj.getOperation_price_count() + 1);
			this.orderFormService.update(obj);
			OrderFormLog ofl = new OrderFormLog();
			ofl.setAddTime(new Date());
			ofl.setLog_info("调整订单费用");
			ofl.setState_info("调整订单总金额为:" + totalPrice + ",调整运费金额为:"
					+ ship_price);
			ofl.setLog_user(SecurityUserHolder.getCurrentUser());
			ofl.setOf(obj);
			this.orderFormLogService.save(ofl);
			User buyer = this.userService.getObjById(CommUtil.null2Long(obj
					.getUser_id()));
			Map map = new HashMap();
			map.put("buyer_id", buyer.getId().toString());
			map.put("self_goods", this.configService.getSysConfig().getTitle());
			map.put("order_id", obj.getId());
			String json = Json.toJson(map);
			if (this.configService.getSysConfig().isEmailEnable()) {
				this.sendMsgAndEmTools.sendEmail(CommUtil.getURL(request),
						TytsmsStringUtils.generatorFilesFolderServerPath(request),
						"email_tobuyer_selforder_update_fee_notify",
						buyer.getEmail(), json);
			}
			if (this.configService.getSysConfig().isSmsEnbale()) {
				this.sendMsgAndEmTools.sendMsg(CommUtil.getURL(request),
						TytsmsStringUtils.generatorFilesFolderServerPath(request),
						"sms_tobuyer_selforder_fee_notify", buyer.getMobile(),
						json);
			}
		}
		return "redirect:self_order.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "买家个人信息", value = "/admin/order_query_userinfor.htm*", rtype = "admin", rname = "自营订单管理", rcode = "order_self", rgroup = "自营")
	@RequestMapping("/admin/order_query_userinfor.htm")
	public ModelAndView order_query_userinfor(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/order_query_userinfor.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		mv.addObject("obj", obj);
		return mv;
	}

	@SecurityMapping(title = "取消订单", value = "/admin/order_cancel.htm*", rtype = "admin", rname = "自营订单管理", rcode = "order_self", rgroup = "自营")
	@RequestMapping("/admin/order_cancel.htm")
	public ModelAndView order_cancel(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/admin_order_cancel.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		if (1 == obj.getOrder_form()) {
			mv.addObject("obj", obj);
			mv.addObject("currentPage", currentPage);
		} else {
			mv = new JModelAndView("admin/blue/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "您没有编号为" + id + "的订单！");
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/admin/self_order.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "取消订单保存", value = "/admin/order_cancel_save.htm*", rtype = "admin", rname = "自营订单管理", rcode = "order_self", rgroup = "自营")
	@RequestMapping("/admin/order_cancel_save.htm")
	@Transactional
	public String order_cancel_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String state_info, String other_state_info) throws Exception {
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		if (1 == obj.getOrder_form()) {
			obj.setOrder_status(0);
			this.orderFormService.update(obj);
			OrderFormLog ofl = new OrderFormLog();
			ofl.setAddTime(new Date());
			ofl.setLog_info("取消订单");
			ofl.setLog_user(SecurityUserHolder.getCurrentUser());
			ofl.setOf(obj);
			if (state_info.equals("other")) {
				ofl.setState_info(other_state_info);
			} else {
				ofl.setState_info(state_info);
			}
			this.orderFormLogService.save(ofl);
			User buyer = this.userService.getObjById(CommUtil.null2Long(obj
					.getUser_id()));
			Map map = new HashMap();
			map.put("buyer_id", buyer.getId().toString());
			map.put("self_goods", this.configService.getSysConfig().getTitle());
			map.put("order_id", obj.getId());
			String json = Json.toJson(map);
			if (this.configService.getSysConfig().isEmailEnable()) {
				this.sendMsgAndEmTools.sendEmail(CommUtil.getURL(request),
						TytsmsStringUtils.generatorFilesFolderServerPath(request),
						"email_tobuyer_selforder_cancel_notify",
						buyer.getEmail(), json);
			}
			if (this.configService.getSysConfig().isSmsEnbale()) {
				this.sendMsgAndEmTools.sendMsg(CommUtil.getURL(request),
						TytsmsStringUtils.generatorFilesFolderServerPath(request) ,
						"sms_tobuyer_selforder_cancel_notify",
						buyer.getMobile(), json);
			}
		}
		return "redirect:self_order.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "确认发货", value = "/admin/order_shipping.htm*", rtype = "admin", rname = "自营订单管理", rcode = "order_self", rgroup = "自营")
	@RequestMapping("/admin/order_shipping.htm")
	public ModelAndView order_shipping(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/admin_order_shipping.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		if (1 == obj.getOrder_form()) {
			mv.addObject("obj", obj);
			mv.addObject("currentPage", currentPage);
			List<Goods> list_goods = this.orderFormTools.queryOfGoods(id);
			List<Goods> deliveryGoods = new ArrayList<Goods>();
			boolean physicalGoods = false;
			for (Goods g : list_goods) {
				if (g.getGoods_choice_type() == 1) {
					deliveryGoods.add(g);
				} else {
					physicalGoods = true;
				}
			}
			Map params = new HashMap();
			params.put("status", 0);
			List<ExpressCompany> expressCompanys = this.expressCompanyService
					.query("select obj from ExpressCompany obj where obj.company_status=:status order by company_sequence asc",
							params, -1, -1);
			mv.addObject("expressCompanys", expressCompanys);
			mv.addObject("physicalGoods", physicalGoods);
			mv.addObject("deliveryGoods", deliveryGoods);
		} else {
			mv = new JModelAndView("admin/blue/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "您没有编号为" + id + "的订单！");
			mv.addObject("url_list", CommUtil.getURL(request)
					+ "/admin/self_order.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "确认发货保存", value = "/admin/order_shipping_save.htm*", rtype = "admin", rname = "自营订单管理", rcode = "order_self", rgroup = "自营")
	@RequestMapping("/admin/order_shipping_save.htm")
	@Transactional
	public String order_shipping_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String shipCode, String state_info, String order_seller_intro,
			String ec_id) throws Exception {
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		ExpressCompany ec = this.expressCompanyService.getObjById(CommUtil
				.null2Long(ec_id));
		if (1 == obj.getOrder_form()) {
			obj.setEva_user_id(SecurityUserHolder.getCurrentUser().getId());
			obj.setOrder_status(30);
			obj.setShipCode(shipCode);
			obj.setShipTime(new Date());
			Map json_map = new HashMap();
			json_map.put("express_company_id", ec.getId());
			json_map.put("express_company_name", ec.getCompany_name());
			json_map.put("express_company_mark", ec.getCompany_mark());
			json_map.put("express_company_type", ec.getCompany_type());
			String express_json = Json.toJson(json_map);
			obj.setExpress_info(express_json);
			obj.setOrder_seller_intro(order_seller_intro);
			this.orderFormService.update(obj);
			OrderFormLog ofl = new OrderFormLog();
			ofl.setAddTime(new Date());
			ofl.setLog_info("确认发货");
			ofl.setState_info(state_info);
			ofl.setLog_user(SecurityUserHolder.getCurrentUser());
			ofl.setOf(obj);
			this.orderFormLogService.save(ofl);
			User buyer = this.userService.getObjById(CommUtil.null2Long(obj
					.getUser_id()));
			Map map = new HashMap();
			map.put("buyer_id", buyer.getId().toString());
			map.put("self_goods", this.configService.getSysConfig().getTitle());
			map.put("order_id", obj.getId());
			String json = Json.toJson(map);
			if (this.configService.getSysConfig().isEmailEnable()) {
				this.sendMsgAndEmTools.sendEmail(CommUtil.getURL(request),
						TytsmsStringUtils.generatorFilesFolderServerPath(request),
						"email_tobuyer_selforder_ship_notify",
						buyer.getEmail(), json);
			}
			if (this.configService.getSysConfig().isSmsEnbale()) {
				this.sendMsgAndEmTools.sendMsg(CommUtil.getURL(request),
						TytsmsStringUtils.generatorFilesFolderServerPath(request),
						"sms_tobuyer_selforder_ship_notify", buyer.getMobile(),
						json);
			}
			// 异步通知支付宝,该方法用在支付宝担保支付，目的用来修改支付宝订单状态为“已发货”
			if (obj.getPayment().getMark().equals("alipay")
					&& obj.getPayment().getInterfaceType() == 1) {
				// 把请求参数打包成数组
				boolean synch = false;
				String safe_key = "";
				String partner = "";
				if (!CommUtil.null2String(obj.getPayment().getSafeKey())
						.equals("")
						&& !CommUtil.null2String(obj.getPayment().getPartner())
								.equals("")) {
					safe_key = obj.getPayment().getSafeKey();
					partner = obj.getPayment().getPartner();
					synch = true;
				}
				if (synch) {
					AlipayConfig config = new AlipayConfig();
					config.setKey(safe_key);
					config.setPartner(partner);
					Map<String, String> sParaTemp = new HashMap<String, String>();
					sParaTemp.put("service", "send_goods_confirm_by_platform");
					sParaTemp.put("partner", config.getPartner());
					sParaTemp.put("_input_charset", config.getInput_charset());
					sParaTemp.put("trade_no", obj.getOut_order_id());
					sParaTemp.put("logistics_name", ec.getCompany_name());
					sParaTemp.put("invoice_no", shipCode);
					sParaTemp.put("transport_type", ec.getCompany_type());
					// 建立请求
					String sHtmlText = AlipaySubmit.buildRequest(config, "web",
							sParaTemp, "", "");
					// System.out.println(sHtmlText);
				}
			}
		}
		return "redirect:self_order.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "修改物流", value = "/admin/order_shipping_code.htm*", rtype = "admin", rname = "自营订单管理", rcode = "order_self", rgroup = "自营")
	@RequestMapping("/admin/order_shipping_code.htm")
	public ModelAndView order_shipping_code(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/admin_order_shipping_code.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		if (1 == obj.getOrder_form()) {
			mv.addObject("obj", obj);
			mv.addObject("currentPage", currentPage);
		} else {
			mv = new JModelAndView("admin/blue/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "您没有编号为" + id + "的订单！");
			mv.addObject("url_list", CommUtil.getURL(request)
					+ "/admin/self_order.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "修改物流保存", value = "/admin/order_shipping_code_save.htm*", rtype = "admin", rname = "自营订单管理", rcode = "order_self", rgroup = "自营")
	@RequestMapping("/admin/order_shipping_code_save.htm")
	public String order_shipping_code_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String shipCode, String state_info) {
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		if (1 == obj.getOrder_form()) {
			obj.setShipCode(shipCode);
			this.orderFormService.update(obj);
			OrderFormLog ofl = new OrderFormLog();
			ofl.setAddTime(new Date());
			ofl.setLog_info("修改物流信息");
			ofl.setState_info(state_info);
			ofl.setLog_user(SecurityUserHolder.getCurrentUser());
			ofl.setOf(obj);
			this.orderFormLogService.save(ofl);
		}
		return "redirect:self_order.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "买家退货申请详情", value = "/admin/admin_order_return_apply_view.htm*", rtype = "admin", rname = "自营订单管理", rcode = "order_self", rgroup = "自营")
	@RequestMapping("/admin/admin_order_return_apply_view.htm")
	public ModelAndView admin_order_return_apply_view(
			HttpServletRequest request, HttpServletResponse response,
			String id, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/admin_order_return_apply_view.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		if (1 == obj.getOrder_form()) {
			mv.addObject("obj", obj);
		} else {
			mv = new JModelAndView("admin/blue/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "您没有编号为" + id + "的订单！");
			mv.addObject("url_list", CommUtil.getURL(request)
					+ "/admin/self_order.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "打印订单", value = "/admin/order_print.htm*", rtype = "admin", rname = "自营订单管理", rcode = "order_self", rgroup = "自营")
	@RequestMapping("/admin/order_print.htm")
	public ModelAndView order_print(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("admin/blue/order_print.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			OrderForm orderform = this.orderFormService.getObjById(CommUtil
					.null2Long(id));
			mv.addObject("obj", orderform);
		}
		mv.addObject("orderFormTools", orderFormTools);
		return mv;
	}

	@SecurityMapping(title = "物流详情", value = "/admin/ship_view.htm*", rtype = "admin", rname = "自营订单管理", rcode = "order_self", rgroup = "自营")
	@RequestMapping("/admin/ship_view.htm")
	public ModelAndView order_ship_view(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("admin/blue/order_ship_view.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		if (obj != null && !obj.equals("")) {
			if (1 == obj.getOrder_form()) {
				mv.addObject("obj", obj);
				TransInfo transInfo = this.query_ship_getData(CommUtil
						.null2String(obj.getId()));
				mv.addObject("transInfo", transInfo);
				mv.addObject("express_company_name", this.orderFormTools
						.queryExInfo(obj.getExpress_info(),
								"express_company_name"));
			} else {
				mv = new JModelAndView("error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "您查询的物流不存在！");
				mv.addObject("url_list", CommUtil.getURL(request)
						+ "/admin/self_order.htm");
			}
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您查询的物流不存在！");
			mv.addObject("url_list", CommUtil.getURL(request)
					+ "/admin/self_order.htm");
		}
		return mv;
	}

	private TransInfo query_ship_getData(String id) {
		TransInfo info = new TransInfo();
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		try {
			ExpressCompany ec = this.queryExpressCompany(obj.getExpress_info());
			URL url = new URL("http://api.kuaidi100.com/api?id="
					+ this.configService.getSysConfig().getKuaidi_id()
					+ "&com=" + (ec != null ? ec.getCompany_mark() : "")
					+ "&nu=" + obj.getShipCode() + "&show=0&muti=1&order=asc");
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
