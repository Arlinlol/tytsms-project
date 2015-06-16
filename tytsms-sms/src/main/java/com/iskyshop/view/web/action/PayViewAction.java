package com.iskyshop.view.web.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.allinpay.ets.client.PaymentResult;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.Md5Encrypt;
import com.iskyshop.foundation.domain.GoldLog;
import com.iskyshop.foundation.domain.GoldRecord;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsSpecProperty;
import com.iskyshop.foundation.domain.GroupGoods;
import com.iskyshop.foundation.domain.GroupInfo;
import com.iskyshop.foundation.domain.GroupLifeGoods;
import com.iskyshop.foundation.domain.IntegralGoods;
import com.iskyshop.foundation.domain.IntegralGoodsCart;
import com.iskyshop.foundation.domain.IntegralGoodsOrder;
import com.iskyshop.foundation.domain.Message;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.OrderFormLog;
import com.iskyshop.foundation.domain.Payment;
import com.iskyshop.foundation.domain.PayoffLog;
import com.iskyshop.foundation.domain.Predeposit;
import com.iskyshop.foundation.domain.PredepositLog;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.Template;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IGoldLogService;
import com.iskyshop.foundation.service.IGoldRecordService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGroupGoodsService;
import com.iskyshop.foundation.service.IGroupInfoService;
import com.iskyshop.foundation.service.IGroupLifeGoodsService;
import com.iskyshop.foundation.service.IIntegralGoodsOrderService;
import com.iskyshop.foundation.service.IIntegralGoodsService;
import com.iskyshop.foundation.service.IMessageService;
import com.iskyshop.foundation.service.IOrderFormLogService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IPaymentService;
import com.iskyshop.foundation.service.IPayoffLogService;
import com.iskyshop.foundation.service.IPredepositLogService;
import com.iskyshop.foundation.service.IPredepositService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.ITemplateService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.MsgTools;
import com.iskyshop.manage.admin.tools.OrderFormTools;
import com.iskyshop.pay.alipay.config.AlipayConfig;
import com.iskyshop.pay.alipay.util.AlipayNotify;
import com.iskyshop.pay.bill.util.MD5Util;
import com.iskyshop.pay.tenpay.RequestHandler;
import com.iskyshop.pay.tenpay.ResponseHandler;
import com.iskyshop.pay.tenpay.util.TenpayUtil;
import com.iskyshop.pay.wechatpay.util.ConfigContants;
import com.iskyshop.pay.wechatpay.util.TytsmsStringUtils;
import com.iskyshop.pay.wechatpay.util.XMLUtil;
import com.taiyitao.elasticsearch.ElasticsearchUtil;
import com.taiyitao.elasticsearch.IndexVo.IndexName;
import com.taiyitao.elasticsearch.IndexVo.IndexType;
import com.taiyitao.elasticsearch.IndexVoTools;

/**
 * 
 * <p>
 * Title: PayViewAction.java
 * </p>
 * 
 * <p>
 * Description:在线支付回调控制器,处理系统支持的所有支付方式回调业务处理，包括支付宝、财付通、快钱、paypal、网银在线
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
 * @date 2014-5-25
 * 
 * @version iskyshop_b2b2c 1.0
 */
@Controller
public class PayViewAction {
	private static final Logger log = LoggerFactory.getLogger(PayViewAction.class);
	
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IOrderFormLogService orderFormLogService;
	@Autowired
	private IPredepositService predepositService;
	@Autowired
	private IPredepositLogService predepositLogService;
	@Autowired
	private IGoldRecordService goldRecordService;
	@Autowired
	private IGoldLogService goldLogService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IPaymentService paymentService;
	@Autowired
	private IIntegralGoodsOrderService integralGoodsOrderService;
	@Autowired
	private IIntegralGoodsService integralGoodsService;
	@Autowired
	private IGroupGoodsService groupGoodsService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private ITemplateService templateService;
	@Autowired
	private MsgTools msgTools;
	@Autowired
	private OrderFormTools orderFormTools;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IGroupLifeGoodsService groupLifeGoodsService;
	@Autowired
	private IGroupInfoService groupInfoService;
	@Autowired
	private IMessageService messageService;
	@Autowired
	private IPayoffLogService payoffservice;
	@Autowired
	private ElasticsearchUtil elasticsearchUtil;

	/**
	 * 支付宝在线支付成功回调控制
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/alipay_return.htm")
	@Transactional
	public ModelAndView alipay_return(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mv = new JModelAndView("order_finish_pay.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String trade_no = request.getParameter("trade_no"); // 支付宝交易号
		String[] order_nos = request.getParameter("out_trade_no").split("-"); // 获取订单号
		String total_fee = request.getParameter("total_fee"); // 获取总金额
		String subject = request.getParameter("subject");
//		String subject =new
//		 String(request.getParameter("subject").getBytes("ISO-8859-1"),
//		 "UTF-8");//
		// 商品名称、订单名称
		String order_no = order_nos[2];
		String type = CommUtil.null2String(request.getParameter("body")).trim();
		String trade_status = request.getParameter("trade_status"); // 交易状态
		OrderForm main_order = null;
		Predeposit obj = null;
		GoldRecord gold = null;
		IntegralGoodsOrder ig_order = null;
		if (type.equals("goods")) {
			main_order = this.orderFormService.getObjById(CommUtil
					.null2Long(order_no));
		}
		if (type.equals("cash")) {
			obj = this.predepositService.getObjById(CommUtil
					.null2Long(order_no));
		}
		if (type.equals("gold")) {
			gold = this.goldRecordService.getObjById(CommUtil
					.null2Long(order_no));
		}
		if (type.equals("integral")) {
			ig_order = this.integralGoodsOrderService.getObjById(CommUtil
					.null2Long(order_no));
		}
		if (type.equals("group")) {
			main_order = this.orderFormService.getObjById(CommUtil
					.null2Long(order_no));
		}
		// 获取支付宝GET过来反馈信息
		Map<String, String> params = new HashMap<String, String>();
		Map requestParams = request.getParameterMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
						: valueStr + values[i] + ",";
			}
			// 如果没有配置Tomcat的get编码为UTF-8，需要下面一行代码转码，否则会出现乱码，导致回调失败
//			 valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
			params.put(name, valueStr);
		}
		AlipayConfig config = new AlipayConfig();
		if (type.equals("goods") || type.equals("group")) {
			config.setKey(main_order.getPayment().getSafeKey());
			config.setPartner(main_order.getPayment().getPartner());
			config.setSeller_email(main_order.getPayment().getSeller_email());
		}
		if (type.equals("cash") || type.equals("gold")
				|| type.equals("integral")) {
			Map q_params = new HashMap();
			q_params.put("install", true);
			if (type.equals("cash")) {
				q_params.put("mark", obj.getPd_payment());
			}
			if (type.equals("gold")) {
				q_params.put("mark", gold.getGold_payment());
			}
			if (type.equals("integral")) {
				q_params.put("mark", ig_order.getIgo_payment());
			}
			List<Payment> payments = this.paymentService
					.query("select obj from Payment obj where obj.install=:install and obj.mark=:mark",
							q_params, -1, -1);
			config.setKey(payments.get(0).getSafeKey());
			config.setPartner(payments.get(0).getPartner());
			config.setSeller_email(payments.get(0).getSeller_email());
		}
		config.setNotify_url(CommUtil.getURL(request) + "/alipay_notify.htm");
		config.setReturn_url(CommUtil.getURL(request) + "/alipay_return.htm");
		boolean verify_result = AlipayNotify.verify(config, params);
		if (verify_result) {// 验证成功
			if (type.equals("goods")) {
				if (trade_status.equals("WAIT_SELLER_SEND_GOODS")
						|| trade_status.equals("TRADE_FINISHED")
						|| trade_status.equals("TRADE_SUCCESS")) {
					if (main_order.getOrder_status() < 20) {// 异步没有出来订单，则同步处理订单
						main_order.setOrder_status(20);
						main_order.setPayTime(new Date());
						this.orderFormService.update(main_order);
						// 主订单付款成功，发送邮件提示
						if (this.configService.getSysConfig().isEmailEnable()) {
							// 向加盟商家发送付款成功短信提示，自营商品无需发送短信提示
							User buyer = this.userService.getObjById(CommUtil
									.null2Long(main_order.getUser_id()));
							this.send_order_email(request, main_order,
									buyer.getEmail(),
									"email_tobuyer_online_pay_ok_notify");
							if (main_order.getOrder_form() == 0) {
								Store store = this.storeService
										.getObjById(CommUtil
												.null2Long(main_order
														.getStore_id()));
								this.send_order_email(request, main_order,
										store.getUser().getEmail(),
										"email_toseller_online_pay_ok_notify");
							}
						}
						// 主订单付款成功，发送短信提示
						if (this.configService.getSysConfig().isSmsEnbale()) {
							User buyer = this.userService.getObjById(CommUtil
									.null2Long(main_order.getUser_id()));
							this.send_order_sms(request, main_order,
									buyer.getMobile(),
									"sms_tobuyer_online_pay_ok_notify");
							if (main_order.getOrder_form() == 0) {
								Store store = this.storeService
										.getObjById(CommUtil
												.null2Long(main_order
														.getStore_id()));
								this.send_order_sms(request, main_order, store
										.getUser().getMobile(),
										"sms_toseller_online_pay_ok_notify");
							}
						}
						// 主订单付款成功，订单状态更新，同时更新商品库存，如果是团购商品，则更新团购库存
						this.update_goods_inventory(main_order);
						OrderFormLog main_ofl = new OrderFormLog();
						main_ofl.setAddTime(new Date());
						main_ofl.setLog_info("支付宝在线支付");
						User main_buyer = this.userService.getObjById(CommUtil
								.null2Long(main_order.getUser_id()));
						main_ofl.setLog_user(main_buyer);
						main_ofl.setOf(main_order);
						this.orderFormLogService.save(main_ofl);
						// 子订单操作
						if (main_order.getOrder_main() == 1
								&& !CommUtil.null2String(
										main_order.getChild_order_detail())
										.equals("")) {// 同步完成子订单付款状态调整
							List<Map> maps = this.orderFormTools
									.queryGoodsInfo(main_order
											.getChild_order_detail());
							for (Map child_map : maps) {
								OrderForm child_order = this.orderFormService
										.getObjById(CommUtil
												.null2Long(child_map
														.get("order_id")));
								child_order.setOrder_status(20);
								this.orderFormService.update(child_order);
								// 付款成功，发送邮件提示
								if (this.configService.getSysConfig()
										.isEmailEnable()) {
									// 向加盟商家发送付款成功短信提示，自营商品无需发送短信提示
									User buyer = this.userService
											.getObjById(CommUtil
													.null2Long(child_order
															.getUser_id()));
									this.send_order_email(request, child_order,
											buyer.getEmail(),
											"email_tobuyer_online_pay_ok_notify");
									if (child_order.getOrder_form() == 0) {
										Store store = this.storeService
												.getObjById(CommUtil
														.null2Long(child_order
																.getStore_id()));
										this.send_order_email(request,
												child_order, store.getUser()
														.getEmail(),
												"email_toseller_online_pay_ok_notify");
									}
								}
								// 付款成功，发送短信提示
								if (this.configService.getSysConfig()
										.isSmsEnbale()) {
									User buyer = this.userService
											.getObjById(CommUtil
													.null2Long(child_order
															.getUser_id()));
									this.send_order_sms(request, child_order,
											buyer.getMobile(),
											"sms_tobuyer_online_pay_ok_notify");
									if (child_order.getOrder_form() == 0) {
										Store store = this.storeService
												.getObjById(CommUtil
														.null2Long(child_order
																.getStore_id()));
										this.send_order_sms(request,
												child_order, store.getUser()
														.getMobile(),
												"sms_toseller_online_pay_ok_notify");
									}
								}
								// 子订单付款成功，订单状态更新，同时更新商品库存，如果是团购商品，则更新团购库存
								this.update_goods_inventory(child_order);
								OrderFormLog ofl = new OrderFormLog();
								ofl.setAddTime(new Date());
								ofl.setLog_info("支付宝在线支付");
								User buyer = this.userService
										.getObjById(CommUtil
												.null2Long(child_order
														.getUser_id()));
								ofl.setLog_user(buyer);
								ofl.setOf(child_order);
								this.orderFormLogService.save(ofl);
							}
						}
					}
					mv.addObject("obj", main_order);
				}
			}
			if (type.equals("group")) {
				if (trade_status.equals("WAIT_SELLER_SEND_GOODS")
						|| trade_status.equals("TRADE_FINISHED")
						|| trade_status.equals("TRADE_SUCCESS")) {
					if (main_order.getOrder_status() != 20) {// 异步没有出来订单，则同步处理订单
						this.generate_groupInfos(request, main_order, "alipay",
								"支付宝在线支付", trade_no);
					}
					mv.addObject("obj", main_order);
				}
			}
			if (type.equals("cash")) {
				if (trade_status.equals("WAIT_SELLER_SEND_GOODS")
						|| trade_status.equals("TRADE_FINISHED")
						|| trade_status.equals("TRADE_SUCCESS")) {
					if (obj.getPd_pay_status() != 2) {// 异步没有处理该充值业务，则同步处理一下
						obj.setPd_status(1);
						obj.setPd_pay_status(2);
						this.predepositService.update(obj);
						User user = this.userService.getObjById(obj
								.getPd_user().getId());
						user.setAvailableBalance(BigDecimal.valueOf(CommUtil
								.add(user.getAvailableBalance(),
										obj.getPd_amount())));
						this.userService.update(user);
						PredepositLog log = new PredepositLog();
						log.setAddTime(new Date());
						log.setPd_log_amount(obj.getPd_amount());
						log.setPd_log_user(obj.getPd_user());
						log.setPd_op_type("充值");
						log.setPd_type("可用预存款");
						log.setPd_log_info("支付宝在线支付");
						this.predepositLogService.save(log);
					}
					mv = new JModelAndView("success.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "充值" + obj.getPd_amount() + "成功");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/buyer/predeposit_list.htm");
				}
			}
			if (type.equals("gold")) {
				if (trade_status.equals("WAIT_SELLER_SEND_GOODS")
						|| trade_status.equals("TRADE_FINISHED")
						|| trade_status.equals("TRADE_SUCCESS")) {
					if (gold.getGold_pay_status() != 2) {
						gold.setGold_status(1);
						gold.setGold_pay_status(2);
						this.goldRecordService.update(gold);
						User user = this.userService.getObjById(gold
								.getGold_user().getId());
						user.setGold(user.getGold() + gold.getGold_count());
						this.userService.update(user);
						GoldLog log = new GoldLog();
						log.setAddTime(new Date());
						log.setGl_payment(gold.getGold_payment());
						log.setGl_content("支付宝在线支付");
						log.setGl_money(gold.getGold_money());
						log.setGl_count(gold.getGold_count());
						log.setGl_type(0);
						log.setGl_user(gold.getGold_user());
						log.setGr(gold);
						this.goldLogService.save(log);
					}
					mv = new JModelAndView("success.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "兑换" + gold.getGold_count()
							+ "金币成功");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/seller/gold_record_list.htm");
				}
			}
			if (type.equals("integral")) {
				if (trade_status.equals("WAIT_SELLER_SEND_GOODS")
						|| trade_status.equals("TRADE_FINISHED")
						|| trade_status.equals("TRADE_SUCCESS")) {
					if (ig_order.getIgo_status() < 20) {
						ig_order.setIgo_status(20);
						ig_order.setIgo_pay_time(new Date());
						ig_order.setIgo_payment("alipay");
						this.integralGoodsOrderService.update(ig_order);
						for (IntegralGoodsCart igc : ig_order.getIgo_gcs()) {
							IntegralGoods goods = igc.getGoods();
							goods.setIg_goods_count(goods.getIg_goods_count()
									- igc.getCount());
							goods.setIg_exchange_count(goods
									.getIg_exchange_count() + igc.getCount());
							this.integralGoodsService.update(goods);
						}
					}
					mv = new JModelAndView("integral_order_finish.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("obj", ig_order);
				}
			}
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "支付回调失败！");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		}
		return mv;
	}

	/**
	 * 支付宝异步通知
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/alipay_notify.htm")
	@Transactional
	public void alipay_notify(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		//
		String trade_no = request.getParameter("trade_no"); // 支付宝交易号
		String[] order_nos = request.getParameter("out_trade_no").split("-"); // 获取订单号
		String total_fee = request.getParameter("total_fee"); // 获取总金额
		String subject = request.getParameter("subject");// new
//		 String subject =new String(request.getParameter("subject").getBytes("ISO-8859-1"),
//		 "UTF-8");//
		// 商品名称、订单名称
		String order_no = order_nos[2];
		String type = CommUtil.null2String(request.getParameter("body")).trim();
		String trade_status = request.getParameter("trade_status"); // 交易状态
		OrderForm main_order = null;
		Predeposit obj = null;
		GoldRecord gold = null;
		IntegralGoodsOrder ig_order = null;
		if (type.equals("goods") || type.equals("group")) {
			main_order = this.orderFormService.getObjById(CommUtil
					.null2Long(order_no));
		}
		if (type.equals("cash")) {
			obj = this.predepositService.getObjById(CommUtil
					.null2Long(order_no));
		}
		if (type.equals("gold")) {
			gold = this.goldRecordService.getObjById(CommUtil
					.null2Long(order_no));
		}
		if (type.equals("integral")) {
			ig_order = this.integralGoodsOrderService.getObjById(CommUtil
					.null2Long(order_no));
		}
		// 获取支付宝GET过来反馈信息
		Map<String, String> params = new HashMap<String, String>();
		Map requestParams = request.getParameterMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
						: valueStr + values[i] + ",";
			}
			// 如果没有配置Tomcat的get编码为UTF-8，需要下面一行代码转码，否则会出现乱码，导致回调失败
//			 valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
			params.put(name, valueStr);
		}
		AlipayConfig config = new AlipayConfig();
		if (type.equals("goods") || type.equals("group")) {
			config.setKey(main_order.getPayment().getSafeKey());
			config.setPartner(main_order.getPayment().getPartner());
			config.setSeller_email(main_order.getPayment().getSeller_email());
		}
		if (type.equals("cash") || type.equals("gold")
				|| type.equals("integral")) {
			Map q_params = new HashMap();
			q_params.put("install", true);
			if (type.equals("cash")) {
				q_params.put("mark", obj.getPd_payment());
			}
			if (type.equals("gold")) {
				q_params.put("mark", gold.getGold_payment());
			}
			if (type.equals("integral")) {
				q_params.put("mark", ig_order.getIgo_payment());
			}
			List<Payment> payments = this.paymentService
					.query("select obj from Payment obj where obj.install=:install and obj.mark=:mark",
							q_params, -1, -1);
			config.setKey(payments.get(0).getSafeKey());
			config.setPartner(payments.get(0).getPartner());
			config.setSeller_email(payments.get(0).getSeller_email());
		}
		config.setNotify_url(CommUtil.getURL(request) + "/alipay_notify.htm");
		config.setReturn_url(CommUtil.getURL(request) + "/alipay_return.htm");
		boolean verify_result = AlipayNotify.verify(config, params);
		if (verify_result) {// 验证成功
			if (type.equals("goods")) {
				if (trade_status.equals("WAIT_SELLER_SEND_GOODS")
						|| trade_status.equals("TRADE_FINISHED")
						|| trade_status.equals("TRADE_SUCCESS")) {
					if (main_order.getOrder_status() < 20) {// 异步没有出来订单，则同步处理订单
						main_order.setOrder_status(20);
						main_order.setPayTime(new Date());
						this.orderFormService.update(main_order);
						// 主订单付款成功，发送邮件提示
						if (this.configService.getSysConfig().isEmailEnable()) {
							// 向加盟商家发送付款成功短信提示，自营商品无需发送短信提示
							User buyer = this.userService.getObjById(CommUtil
									.null2Long(main_order.getUser_id()));
							this.send_order_email(request, main_order,
									buyer.getEmail(),
									"email_tobuyer_online_pay_ok_notify");
							if (main_order.getOrder_form() == 0) {
								Store store = this.storeService
										.getObjById(CommUtil
												.null2Long(main_order
														.getStore_id()));
								this.send_order_email(request, main_order,
										store.getUser().getEmail(),
										"email_toseller_online_pay_ok_notify");
							}
						}
						// 主订单付款成功，发送短信提示
						if (this.configService.getSysConfig().isSmsEnbale()) {
							User buyer = this.userService.getObjById(CommUtil
									.null2Long(main_order.getUser_id()));
							this.send_order_sms(request, main_order,
									buyer.getMobile(),
									"sms_tobuyer_online_pay_ok_notify");
							if (main_order.getOrder_form() == 0) {
								Store store = this.storeService
										.getObjById(CommUtil
												.null2Long(main_order
														.getStore_id()));
								this.send_order_sms(request, main_order, store
										.getUser().getMobile(),
										"sms_toseller_online_pay_ok_notify");
							}
						}
						// 主订单付款成功，订单状态更新，同时更新商品库存，如果是团购商品，则更新团购库存
						this.update_goods_inventory(main_order);
						OrderFormLog main_ofl = new OrderFormLog();
						main_ofl.setAddTime(new Date());
						main_ofl.setLog_info("支付宝在线支付");
						User main_buyer = this.userService.getObjById(CommUtil
								.null2Long(main_order.getUser_id()));
						main_ofl.setLog_user(main_buyer);
						main_ofl.setOf(main_order);
						this.orderFormLogService.save(main_ofl);
						// 子订单操作
						if (main_order.getOrder_main() == 1
								&& !CommUtil.null2String(
										main_order.getChild_order_detail())
										.equals("")) {// 同步完成子订单付款状态调整
							List<Map> maps = this.orderFormTools
									.queryGoodsInfo(main_order
											.getChild_order_detail());
							for (Map child_map : maps) {
								OrderForm child_order = this.orderFormService
										.getObjById(CommUtil
												.null2Long(child_map
														.get("order_id")));
								child_order.setOrder_status(20);
								this.orderFormService.update(child_order);
								// 付款成功，发送邮件提示
								if (this.configService.getSysConfig()
										.isEmailEnable()) {
									// 向加盟商家发送付款成功短信提示，自营商品无需发送短信提示
									User buyer = this.userService
											.getObjById(CommUtil
													.null2Long(child_order
															.getUser_id()));
									this.send_order_email(request, child_order,
											buyer.getEmail(),
											"email_tobuyer_online_pay_ok_notify");
									if (child_order.getOrder_form() == 0) {
										Store store = this.storeService
												.getObjById(CommUtil
														.null2Long(child_order
																.getStore_id()));
										this.send_order_email(request,
												child_order, store.getUser()
														.getEmail(),
												"email_toseller_online_pay_ok_notify");
									}
								}
//								// 付款成功，发送短信提示
//								if (this.configService.getSysConfig()
//										.isSmsEnbale()) {
//									User buyer = this.userService
//											.getObjById(CommUtil
//													.null2Long(child_order
//															.getUser_id()));
//									this.send_order_sms(request, child_order,
//											buyer.getMobile(),
//											"sms_tobuyer_online_pay_ok_notify");
//									if (child_order.getOrder_form() == 0) {
//										Store store = this.storeService
//												.getObjById(CommUtil
//														.null2Long(child_order
//																.getStore_id()));
//										this.send_order_sms(request,
//												child_order, store.getUser()
//														.getMobile(),
//												"sms_toseller_online_pay_ok_notify");
//									}
//								}
								// 子订单付款成功，订单状态更新，同时更新商品库存，如果是团购商品，则更新团购库存
								this.update_goods_inventory(child_order);
								OrderFormLog ofl = new OrderFormLog();
								ofl.setAddTime(new Date());
								ofl.setLog_info("支付宝在线支付");
								User buyer = this.userService
										.getObjById(CommUtil
												.null2Long(child_order
														.getUser_id()));
								ofl.setLog_user(buyer);
								ofl.setOf(child_order);
								this.orderFormLogService.save(ofl);
							}
						}
					}
				}
			}
			if (type.equals("group")) {
				if (trade_status.equals("WAIT_SELLER_SEND_GOODS")
						|| trade_status.equals("TRADE_FINISHED")
						|| trade_status.equals("TRADE_SUCCESS")) {
					if (main_order.getOrder_status() != 20) {// 异步没有出来订单，则同步处理订单
						this.generate_groupInfos(request, main_order, "alipay",
								"支付宝在线支付", trade_no);
					}
				}
			}
			if (type.equals("cash")) {
				if (trade_status.equals("WAIT_SELLER_SEND_GOODS")
						|| trade_status.equals("TRADE_FINISHED")
						|| trade_status.equals("TRADE_SUCCESS")) {
					if (obj.getPd_pay_status() < 2) {
						obj.setPd_status(1);
						obj.setPd_pay_status(2);
						this.predepositService.update(obj);
						User user = this.userService.getObjById(obj
								.getPd_user().getId());
						user.setAvailableBalance(BigDecimal.valueOf(CommUtil
								.add(user.getAvailableBalance(),
										obj.getPd_amount())));
						this.userService.update(user);
						PredepositLog log = new PredepositLog();
						log.setAddTime(new Date());
						log.setPd_log_amount(obj.getPd_amount());
						log.setPd_log_user(obj.getPd_user());
						log.setPd_op_type("充值");
						log.setPd_type("可用预存款");
						log.setPd_log_info("支付宝在线支付");
						this.predepositLogService.save(log);
					}
				}
			}
			if (type.equals("gold")) {
				if (trade_status.equals("WAIT_SELLER_SEND_GOODS")
						|| trade_status.equals("TRADE_FINISHED")
						|| trade_status.equals("TRADE_SUCCESS")) {
					if (gold.getGold_pay_status() < 2) {
						gold.setGold_status(1);
						gold.setGold_pay_status(2);
						this.goldRecordService.update(gold);
						User user = this.userService.getObjById(gold
								.getGold_user().getId());
						user.setGold(user.getGold() + gold.getGold_count());
						this.userService.update(user);
						GoldLog log = new GoldLog();
						log.setAddTime(new Date());
						log.setGl_payment(gold.getGold_payment());
						log.setGl_content("支付宝在线支付");
						log.setGl_money(gold.getGold_money());
						log.setGl_count(gold.getGold_count());
						log.setGl_type(0);
						log.setGl_user(gold.getGold_user());
						log.setGr(gold);
						this.goldLogService.save(log);
					}
				}
			}
			if (type.equals("integral")) {
				if (trade_status.equals("WAIT_SELLER_SEND_GOODS")
						|| trade_status.equals("TRADE_FINISHED")
						|| trade_status.equals("TRADE_SUCCESS")) {
					if (ig_order.getIgo_status() < 20) {
						ig_order.setIgo_status(20);
						ig_order.setIgo_pay_time(new Date());
						ig_order.setIgo_payment("alipay");
						this.integralGoodsOrderService.update(ig_order);
						for (IntegralGoodsCart igc : ig_order.getIgo_gcs()) {
							IntegralGoods goods = igc.getGoods();
							goods.setIg_goods_count(goods.getIg_goods_count()
									- igc.getCount());
							goods.setIg_exchange_count(goods
									.getIg_exchange_count() + igc.getCount());
							this.integralGoodsService.update(goods);
						}
					}
				}
			}
			response.setContentType("text/plain");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			PrintWriter writer;
			try {
				writer = response.getWriter();
				writer.print("success");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			response.setContentType("text/plain");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			PrintWriter writer;
			try {
				writer = response.getWriter();
				writer.print("fail");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	/**
	 * 快钱在线支付回调处理控制
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/bill_return.htm")
	@Transactional
	public ModelAndView bill_return(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mv = new JModelAndView("order_finish_pay.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		// 获取扩展字段1
		String ext1 = (String) request.getParameter("ext1").trim();
		String ext2 = CommUtil.null2String(request.getParameter("ext2").trim());
		OrderForm order = null;
		Predeposit obj = null;
		GoldRecord gold = null;
		IntegralGoodsOrder ig_order = null;
		if (ext2.equals("goods") || ext2.equals("group")) {
			order = this.orderFormService.getObjById(CommUtil.null2Long(ext1));
		}
		if (ext2.equals("cash")) {
			obj = this.predepositService.getObjById(CommUtil.null2Long(ext1));
		}
		if (ext2.equals("gold")) {
			gold = this.goldRecordService.getObjById(CommUtil.null2Long(ext1));
		}
		if (ext2.equals("integral")) {
			ig_order = this.integralGoodsOrderService.getObjById(CommUtil
					.null2Long(ext1));
		}
		// 获取人民币网关账户号
		String merchantAcctId = (String) request.getParameter("merchantAcctId")
				.trim();
		// 设置人民币网关密钥
		// /区分大小写
		String key = "";
		if (ext2.equals("goods") || ext2.equals("group")) {
			key = order.getPayment().getRmbKey();
		}
		if (ext2.equals("cash") || ext2.equals("gold")
				|| ext2.equals("integral")) {
			Map q_params = new HashMap();
			q_params.put("install", true);
			if (ext2.equals("cash")) {
				q_params.put("mark", obj.getPd_payment());
			}
			if (ext2.equals("gold")) {
				q_params.put("mark", gold.getGold_payment());
			}
			if (ext2.equals("integral")) {
				q_params.put("mark", ig_order.getIgo_payment());
			}
			List<Payment> payments = this.paymentService
					.query("select obj from Payment obj where obj.install=:install and obj.mark=:mark",
							q_params, -1, -1);
			key = payments.get(0).getRmbKey();
		}
		// /快钱会根据版本号来调用对应的接口处理程序。
		// /本代码版本号固定为v2.0
		String version = (String) request.getParameter("version").trim();
		// 获取语言种类.固定选择值。
		// /只能选择1、2、3
		// /1代表中文；2代表英文
		// /默认值为1
		String language = (String) request.getParameter("language").trim();
		// 签名类型.固定值
		// /1代表MD5签名
		// /当前版本固定为1
		String signType = (String) request.getParameter("signType").trim();

		// 获取支付方式
		// /值为：10、11、12、13、14
		// /00：组合支付（网关支付页面显示快钱支持的各种支付方式，推荐使用）10：银行卡支付（网关支付页面只显示银行卡支付）.11：电话银行支付（网关支付页面只显示电话支付）.12：快钱账户支付（网关支付页面只显示快钱账户支付）.13：线下支付（网关支付页面只显示线下支付方式）.14：B2B支付（网关支付页面只显示B2B支付，但需要向快钱申请开通才能使用）
		String payType = (String) request.getParameter("payType").trim();

		// 获取银行代码
		// /参见银行代码列表
		String bankId = (String) request.getParameter("bankId").trim();
		// 获取商户订单号
		String orderId = (String) request.getParameter("orderId").trim();
		// 获取订单提交时间
		// /获取商户提交订单时的时间.14位数字。年[4位]月[2位]日[2位]时[2位]分[2位]秒[2位]
		// /如：20080101010101
		String orderTime = (String) request.getParameter("orderTime").trim();
		// 获取原始订单金额
		// /订单提交到快钱时的金额，单位为分。
		// /比方2 ，代表0.02元
		String orderAmount = (String) request.getParameter("orderAmount")
				.trim();
		// 获取快钱交易号
		// /获取该交易在快钱的交易号
		String dealId = (String) request.getParameter("dealId").trim();
		// 获取银行交易号
		// /如果使用银行卡支付时，在银行的交易号。如不是通过银行支付，则为空
		String bankDealId = (String) request.getParameter("bankDealId").trim();
		// 获取在快钱交易时间
		// /14位数字。年[4位]月[2位]日[2位]时[2位]分[2位]秒[2位]
		// /如；20080101010101
		String dealTime = (String) request.getParameter("dealTime").trim();
		// 获取实际支付金额
		// /单位为分
		// /比方 2 ，代表0.02元
		String payAmount = (String) request.getParameter("payAmount").trim();
		// 获取交易手续费
		// /单位为分
		// /比方 2 ，代表0.02元
		String fee = (String) request.getParameter("fee").trim();
		// 获取扩展字段2
		// /10代表 成功11代表 失败
		String payResult = (String) request.getParameter("payResult").trim();
		// 获取错误代码
		String errCode = (String) request.getParameter("errCode").trim();
		// 获取加密签名串
		String signMsg = (String) request.getParameter("signMsg").trim();
		// 生成加密串。必须保持如下顺序。
		String merchantSignMsgVal = "";
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "merchantAcctId",
				merchantAcctId);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "version", version);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "language",
				language);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "signType",
				signType);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "payType", payType);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "bankId", bankId);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "orderId", orderId);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "orderTime",
				orderTime);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "orderAmount",
				orderAmount);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "dealId", dealId);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "bankDealId",
				bankDealId);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "dealTime",
				dealTime);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "payAmount",
				payAmount);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "fee", fee);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "ext1", ext1);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "ext2", ext2);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "payResult",
				payResult);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "errCode", errCode);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "key", key);

		String merchantSignMsg = MD5Util.md5Hex(
				merchantSignMsgVal.getBytes("utf-8")).toUpperCase();
		// 商家进行数据处理，并跳转会商家显示支付结果的页面
		// /首先进行签名字符串验证
		if (signMsg.toUpperCase().equals(merchantSignMsg.toUpperCase())) {
			// /接着进行支付结果判断
			switch (Integer.parseInt(payResult)) {
			case 10:
				// 特别注意：只有signMsg.toUpperCase().equals(merchantSignMsg.toUpperCase())，且payResult=10，才表示支付成功！同时将订单金额与提交订单前的订单金额进行对比校验。
				if (ext2.equals("goods")) {
					if (order.getOrder_status() < 20) {
						order.setOrder_status(20);
						order.setPayTime(new Date());
						this.orderFormService.update(order);
						if (order.getOrder_main() == 1
								&& !CommUtil.null2String(
										order.getChild_order_detail()).equals(
										"")) {// 同步完成子订单付款状态调整
							List<Map> maps = this.orderFormTools
									.queryGoodsInfo(order
											.getChild_order_detail());
							for (Map child_map : maps) {
								OrderForm child_order = this.orderFormService
										.getObjById(CommUtil
												.null2Long(child_map
														.get("order_id")));
								child_order.setOrder_status(20);
								this.orderFormService.update(child_order);
								// 向加盟商家发送付款成功短信提示，自营商品无需发送短信提示
								Store store = this.storeService
										.getObjById(CommUtil
												.null2Long(child_order
														.getStore_id()));
								User buyer = this.userService
										.getObjById(CommUtil.null2Long(order
												.getUser_id()));
								// 付款成功，发送邮件提示
								if (this.configService.getSysConfig()
										.isEmailEnable()) {
									this.send_order_email(request, child_order,
											buyer.getEmail(),
											"email_tobuyer_online_pay_ok_notify");
									if (child_order.getOrder_form() == 0) {
										this.send_order_email(request,
												child_order, store.getUser()
														.getEmail(),
												"email_toseller_online_pay_ok_notify");
									}
								}
								// 付款成功，发送短信提示
								if (this.configService.getSysConfig()
										.isSmsEnbale()) {
									this.send_order_sms(request, child_order,
											buyer.getMobile(),
											"sms_tobuyer_online_pay_ok_notify");
									if (child_order.getOrder_form() == 0) {
										this.send_order_sms(request,
												child_order, store.getUser()
														.getMobile(),
												"sms_toseller_online_pay_ok_notify");
									}
								}
							}
						}
						this.update_goods_inventory(order);// 更新商品库存
						OrderFormLog ofl = new OrderFormLog();
						ofl.setAddTime(new Date());
						ofl.setLog_info("快钱在线支付");
						ofl.setLog_user(SecurityUserHolder.getCurrentUser());
						ofl.setOf(order);
						this.orderFormLogService.save(ofl);
					}
					mv.addObject("obj", order);
				}
				if (ext2.equals("group")) {
					this.generate_groupInfos(request, order, "bill", "快钱在线支付",
							"");
					mv.addObject("obj", order);
				}
				if (ext2.equals("cash")) {
					if (obj.getPd_pay_status() < 2) {
						obj.setPd_status(1);
						obj.setPd_pay_status(2);
						this.predepositService.update(obj);
						User user = this.userService.getObjById(obj
								.getPd_user().getId());
						user.setAvailableBalance(BigDecimal.valueOf(CommUtil
								.add(user.getAvailableBalance(),
										obj.getPd_amount())));
						this.userService.update(user);
						PredepositLog log = new PredepositLog();
						log.setAddTime(new Date());
						log.setPd_log_amount(obj.getPd_amount());
						log.setPd_log_user(obj.getPd_user());
						log.setPd_op_type("充值");
						log.setPd_type("可用预存款");
						log.setPd_log_info("快钱在线支付");
						this.predepositLogService.save(log);
					}
					mv = new JModelAndView("success.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "充值" + obj.getPd_amount() + "成功");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/buyer/predeposit_list.htm");
				}
				if (ext2.equals("gold")) {
					if (gold.getGold_pay_status() < 2) {
						gold.setGold_status(1);
						gold.setGold_pay_status(2);
						this.goldRecordService.update(gold);
						User user = this.userService.getObjById(gold
								.getGold_user().getId());
						user.setGold(user.getGold() + gold.getGold_count());
						this.userService.update(user);
						GoldLog log = new GoldLog();
						log.setAddTime(new Date());
						log.setGl_payment(gold.getGold_payment());
						log.setGl_content("快钱在线支付");
						log.setGl_money(gold.getGold_money());
						log.setGl_count(gold.getGold_count());
						log.setGl_type(0);
						log.setGl_user(gold.getGold_user());
						log.setGr(gold);
						this.goldLogService.save(log);
					}
					mv = new JModelAndView("success.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "兑换" + gold.getGold_count()
							+ "金币成功");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/seller/gold_record_list.htm");
				}
				if (ext2.equals("integral")) {
					if (ig_order.getIgo_status() < 20) {
						ig_order.setIgo_status(20);
						ig_order.setIgo_pay_time(new Date());
						ig_order.setIgo_payment("bill");
						this.integralGoodsOrderService.update(ig_order);
						for (IntegralGoodsCart igc : ig_order.getIgo_gcs()) {
							IntegralGoods goods = igc.getGoods();
							goods.setIg_goods_count(goods.getIg_goods_count()
									- igc.getCount());
							goods.setIg_exchange_count(goods
									.getIg_exchange_count() + igc.getCount());
							this.integralGoodsService.update(goods);
						}
					}
					mv = new JModelAndView("integral_order_finish.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("obj", ig_order);
				}
				break;
			default:
				mv = new JModelAndView("error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "快钱支付失败！");
				mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
				break;

			}

		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "快钱支付失败！");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		}
		return mv;
	}

	@RequestMapping("/bill_notify_return.htm")
	@Transactional
	public void bill_notify_return(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		int rtnOK = 0;
		// 获取扩展字段1
		String ext1 = (String) request.getParameter("ext1").trim();
		String ext2 = CommUtil.null2String(request.getParameter("ext2").trim());
		OrderForm order = null;
		Predeposit obj = null;
		GoldRecord gold = null;
		IntegralGoodsOrder ig_order = null;
		if (ext2.equals("goods") || ext2.equals("group")) {
			order = this.orderFormService.getObjById(CommUtil.null2Long(ext1));
		}
		if (ext2.equals("cash")) {
			obj = this.predepositService.getObjById(CommUtil.null2Long(ext1));
		}
		if (ext2.equals("gold")) {
			gold = this.goldRecordService.getObjById(CommUtil.null2Long(ext1));
		}
		if (ext2.equals("integral")) {
			ig_order = this.integralGoodsOrderService.getObjById(CommUtil
					.null2Long(ext1));
		}
		// 获取人民币网关账户号
		String merchantAcctId = (String) request.getParameter("merchantAcctId")
				.trim();
		// 设置人民币网关密钥
		// /区分大小写
		String key = "";
		if (ext2.equals("goods") || ext2.equals("group")) {
			key = order.getPayment().getRmbKey();
		}
		if (ext2.equals("cash") || ext2.equals("gold")
				|| ext2.equals("integral")) {
			Map q_params = new HashMap();
			q_params.put("install", true);
			if (ext2.equals("cash")) {
				q_params.put("mark", obj.getPd_payment());
			}
			if (ext2.equals("gold")) {
				q_params.put("mark", gold.getGold_payment());
			}
			if (ext2.equals("integral")) {
				q_params.put("mark", ig_order.getIgo_payment());
			}
			List<Payment> payments = this.paymentService
					.query("select obj from Payment obj where obj.install=:install and obj.mark=:mark",
							q_params, -1, -1);
			key = payments.get(0).getRmbKey();
		}
		// /快钱会根据版本号来调用对应的接口处理程序。
		// /本代码版本号固定为v2.0
		String version = (String) request.getParameter("version").trim();
		// 获取语言种类.固定选择值。
		// /只能选择1、2、3
		// /1代表中文；2代表英文
		// /默认值为1
		String language = (String) request.getParameter("language").trim();
		// 签名类型.固定值
		// /1代表MD5签名
		// /当前版本固定为1
		String signType = (String) request.getParameter("signType").trim();

		// 获取支付方式
		// /值为：10、11、12、13、14
		// /00：组合支付（网关支付页面显示快钱支持的各种支付方式，推荐使用）10：银行卡支付（网关支付页面只显示银行卡支付）.11：电话银行支付（网关支付页面只显示电话支付）.12：快钱账户支付（网关支付页面只显示快钱账户支付）.13：线下支付（网关支付页面只显示线下支付方式）.14：B2B支付（网关支付页面只显示B2B支付，但需要向快钱申请开通才能使用）
		String payType = (String) request.getParameter("payType").trim();

		// 获取银行代码
		// /参见银行代码列表
		String bankId = (String) request.getParameter("bankId").trim();
		// 获取商户订单号
		String orderId = (String) request.getParameter("orderId").trim();
		// 获取订单提交时间
		// /获取商户提交订单时的时间.14位数字。年[4位]月[2位]日[2位]时[2位]分[2位]秒[2位]
		// /如：20080101010101
		String orderTime = (String) request.getParameter("orderTime").trim();
		// 获取原始订单金额
		// /订单提交到快钱时的金额，单位为分。
		// /比方2 ，代表0.02元
		String orderAmount = (String) request.getParameter("orderAmount")
				.trim();
		// 获取快钱交易号
		// /获取该交易在快钱的交易号
		String dealId = (String) request.getParameter("dealId").trim();
		// 获取银行交易号
		// /如果使用银行卡支付时，在银行的交易号。如不是通过银行支付，则为空
		String bankDealId = (String) request.getParameter("bankDealId").trim();
		// 获取在快钱交易时间
		// /14位数字。年[4位]月[2位]日[2位]时[2位]分[2位]秒[2位]
		// /如；20080101010101
		String dealTime = (String) request.getParameter("dealTime").trim();
		// 获取实际支付金额
		// /单位为分
		// /比方 2 ，代表0.02元
		String payAmount = (String) request.getParameter("payAmount").trim();
		// 获取交易手续费
		// /单位为分
		// /比方 2 ，代表0.02元
		String fee = (String) request.getParameter("fee").trim();
		// 获取扩展字段2
		// /10代表 成功11代表 失败
		String payResult = (String) request.getParameter("payResult").trim();
		// 获取错误代码
		String errCode = (String) request.getParameter("errCode").trim();
		// 获取加密签名串
		String signMsg = (String) request.getParameter("signMsg").trim();
		// 生成加密串。必须保持如下顺序。
		String merchantSignMsgVal = "";
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "merchantAcctId",
				merchantAcctId);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "version", version);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "language",
				language);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "signType",
				signType);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "payType", payType);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "bankId", bankId);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "orderId", orderId);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "orderTime",
				orderTime);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "orderAmount",
				orderAmount);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "dealId", dealId);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "bankDealId",
				bankDealId);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "dealTime",
				dealTime);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "payAmount",
				payAmount);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "fee", fee);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "ext1", ext1);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "ext2", ext2);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "payResult",
				payResult);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "errCode", errCode);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "key", key);

		String merchantSignMsg = MD5Util.md5Hex(
				merchantSignMsgVal.getBytes("utf-8")).toUpperCase();
		// 商家进行数据处理，并跳转会商家显示支付结果的页面
		// /首先进行签名字符串验证
		if (signMsg.toUpperCase().equals(merchantSignMsg.toUpperCase())) {
			// /接着进行支付结果判断
			switch (Integer.parseInt(payResult)) {
			case 10:
				// 特别注意：只有signMsg.toUpperCase().equals(merchantSignMsg.toUpperCase())，且payResult=10，才表示支付成功！同时将订单金额与提交订单前的订单金额进行对比校验。
				if (ext2.equals("goods")) {
					if (order.getOrder_status() < 20) {
						order.setOrder_status(20);
						order.setPayTime(new Date());
						this.orderFormService.update(order);
						if (order.getOrder_main() == 1
								&& !CommUtil.null2String(
										order.getChild_order_detail()).equals(
										"")) {// 同步完成子订单付款状态调整
							List<Map> maps = this.orderFormTools
									.queryGoodsInfo(order
											.getChild_order_detail());
							for (Map child_map : maps) {
								OrderForm child_order = this.orderFormService
										.getObjById(CommUtil
												.null2Long(child_map
														.get("order_id")));
								child_order.setOrder_status(20);
								this.orderFormService.update(child_order);
								// 向加盟商家发送付款成功短信提示，自营商品无需发送短信提示
								Store store = this.storeService
										.getObjById(CommUtil
												.null2Long(child_order
														.getStore_id()));
								User buyer = this.userService
										.getObjById(CommUtil.null2Long(order
												.getUser_id()));
								// 付款成功，发送邮件提示
								if (this.configService.getSysConfig()
										.isEmailEnable()) {
									this.send_order_email(request, child_order,
											buyer.getEmail(),
											"email_tobuyer_online_pay_ok_notify");
									if (child_order.getOrder_form() == 0) {
										this.send_order_email(request,
												child_order, store.getUser()
														.getEmail(),
												"email_toseller_online_pay_ok_notify");
									}
								}
								// 付款成功，发送短信提示
								if (this.configService.getSysConfig()
										.isSmsEnbale()) {
									this.send_order_sms(request, child_order,
											buyer.getMobile(),
											"sms_tobuyer_online_pay_ok_notify");
									if (child_order.getOrder_form() == 0) {
										this.send_order_sms(request,
												child_order, store.getUser()
														.getMobile(),
												"sms_toseller_online_pay_ok_notify");
									}
								}
							}
						}
						this.update_goods_inventory(order);// 更新商品库存
						OrderFormLog ofl = new OrderFormLog();
						ofl.setAddTime(new Date());
						ofl.setLog_info("快钱在线支付");
						ofl.setLog_user(SecurityUserHolder.getCurrentUser());
						ofl.setOf(order);
						this.orderFormLogService.save(ofl);
					}
					rtnOK = 1;
				}
				if (ext2.equals("group")) {
					this.generate_groupInfos(request, order, "bill", "快钱在线支付",
							"");
					rtnOK = 1;
				}
				if (ext2.equals("cash")) {
					if (obj.getPd_pay_status() < 2) {
						obj.setPd_status(1);
						obj.setPd_pay_status(2);
						this.predepositService.update(obj);
						User user = this.userService.getObjById(obj
								.getPd_user().getId());
						user.setAvailableBalance(BigDecimal.valueOf(CommUtil
								.add(user.getAvailableBalance(),
										obj.getPd_amount())));
						this.userService.update(user);
						PredepositLog log = new PredepositLog();
						log.setAddTime(new Date());
						log.setPd_log_amount(obj.getPd_amount());
						log.setPd_log_user(obj.getPd_user());
						log.setPd_op_type("充值");
						log.setPd_type("可用预存款");
						log.setPd_log_info("快钱在线支付");
						this.predepositLogService.save(log);
					}
					rtnOK = 1;
				}
				if (ext2.equals("gold")) {
					if (gold.getGold_pay_status() < 2) {
						gold.setGold_status(1);
						gold.setGold_pay_status(2);
						this.goldRecordService.update(gold);
						User user = this.userService.getObjById(gold
								.getGold_user().getId());
						user.setGold(user.getGold() + gold.getGold_count());
						this.userService.update(user);
						GoldLog log = new GoldLog();
						log.setAddTime(new Date());
						log.setGl_payment(gold.getGold_payment());
						log.setGl_content("快钱在线支付");
						log.setGl_money(gold.getGold_money());
						log.setGl_count(gold.getGold_count());
						log.setGl_type(0);
						log.setGl_user(gold.getGold_user());
						log.setGr(gold);
						this.goldLogService.save(log);
					}
					rtnOK = 1;
				}
				if (ext2.equals("integral")) {
					if (ig_order.getIgo_status() < 20) {
						ig_order.setIgo_status(20);
						ig_order.setIgo_pay_time(new Date());
						ig_order.setIgo_payment("bill");
						this.integralGoodsOrderService.update(ig_order);
						for (IntegralGoodsCart igc : ig_order.getIgo_gcs()) {
							IntegralGoods goods = igc.getGoods();
							goods.setIg_goods_count(goods.getIg_goods_count()
									- igc.getCount());
							goods.setIg_exchange_count(goods
									.getIg_exchange_count() + igc.getCount());
							this.integralGoodsService.update(goods);
						}
					}
					rtnOK = 1;
				}
				break;
			default:
				break;

			}

		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(rtnOK);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String appendParam(String returnStr, String paramId,
			String paramValue) {
		if (!returnStr.equals("")) {
			if (!paramValue.equals("")) {
				returnStr = returnStr + "&" + paramId + "=" + paramValue;
			}
		} else {
			if (!paramValue.equals("")) {
				returnStr = paramId + "=" + paramValue;
			}
		}
		return returnStr;
	}

	/**
	 * 财付通支付
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/tenpay.htm")
	@Transactional
	public void tenpay(HttpServletRequest request,
			HttpServletResponse response, String id, String type,
			String payment_id) throws IOException {
		OrderForm of = null;
		Predeposit obj = null;
		GoldRecord gold = null;
		IntegralGoodsOrder ig_order = null;
		if (type.equals("goods") || type.equals("group")) {
			of = this.orderFormService.getObjById(CommUtil.null2Long(id));
		}
		if (type.equals("cash")) {
			obj = this.predepositService.getObjById(CommUtil.null2Long(id));
		}
		if (type.equals("gold")) {
			gold = this.goldRecordService.getObjById(CommUtil.null2Long(id));
		}
		if (type.equals("integral")) {
			ig_order = this.integralGoodsOrderService.getObjById(CommUtil
					.null2Long(id));
		}
		// 获取提交的商品价格
		String order_price = "";
		if (type.equals("goods") || type.equals("gruop")) {
			order_price = CommUtil.null2String(of.getTotalPrice());
		}
		if (type.equals("cash")) {
			order_price = CommUtil.null2String(obj.getPd_amount());
		}
		if (type.equals("gold")) {
			order_price = CommUtil.null2String(gold.getGold_money());
		}
		if (type.equals("integral")) {
			order_price = CommUtil.null2String(ig_order.getIgo_trans_fee());
		}
		/* 商品价格（包含运费），以分为单位 */
		double total_fee = CommUtil.null2Double(order_price) * 100;
		int fee = (int) total_fee;
		// 获取提交的商品名称
		String product_name = "";
		if (type.equals("goods") || type.equals("group")) {
			product_name = of.getOrder_id();
		}
		if (type.equals("cash")) {
			product_name = obj.getPd_sn();
		}
		if (type.equals("gold")) {
			product_name = gold.getGold_sn();
		}
		if (type.equals("integral")) {
			product_name = ig_order.getIgo_order_sn();
		}
		// 获取提交的备注信息
		String remarkexplain = "";
		if (type.equals("goods") || type.equals("group")) {
			remarkexplain = of.getMsg();
		}
		if (type.equals("cash")) {
			remarkexplain = obj.getPd_remittance_info();
		}
		if (type.equals("gold")) {
			remarkexplain = gold.getGold_exchange_info();
		}
		if (type.equals("integral")) {
			remarkexplain = ig_order.getIgo_msg();
		}
		String attach = "";
		if (type.equals("goods") || type.equals("group")) {
			attach = type + "," + of.getId().toString();
		}
		if (type.equals("cash")) {
			attach = type + "," + obj.getId().toString();
		}
		if (type.equals("gold")) {
			attach = type + "," + gold.getId().toString();
		}
		if (type.equals("integral")) {
			attach = type + "," + ig_order.getId().toString();
		}
		String desc = "商品：" + product_name;
		// 获取提交的订单号
		String out_trade_no = "";
		if (type.equals("goods") || type.equals("group")) {
			out_trade_no = of.getOrder_id();
		}
		if (type.endsWith("cash")) {
			out_trade_no = obj.getPd_sn();
		}
		if (type.endsWith("gold")) {
			out_trade_no = gold.getGold_sn();
		}
		if (type.equals("integral")) {
			out_trade_no = ig_order.getIgo_order_sn();
		}
		// 支付方式
		Payment payment = this.paymentService.getObjById(CommUtil
				.null2Long(payment_id));
		if (payment == null) {
			payment = new Payment();
		}
		String trade_mode = CommUtil.null2String(payment.getTrade_mode());
		String currTime = TenpayUtil.getCurrTime();
		// 创建支付请求对象
		RequestHandler reqHandler = new RequestHandler(request, response);
		reqHandler.init();
		// 设置密钥
		reqHandler.setKey(payment.getTenpay_key());
		// 设置支付网关
		reqHandler.setGateUrl("https://gw.tenpay.com/gateway/pay.htm");
		// -----------------------------
		// 设置支付参数
		// -----------------------------
		reqHandler.setParameter("partner", payment.getTenpay_partner()); // 商户号
		reqHandler.setParameter("out_trade_no", out_trade_no); // 商家订单号
		reqHandler.setParameter("total_fee", String.valueOf(fee)); // 商品金额,以分为单位
		reqHandler.setParameter("return_url", CommUtil.getURL(request)
				+ "/tenpay_return.htm"); // 交易完成后跳转的URL
		reqHandler.setParameter("notify_url", CommUtil.getURL(request)
				+ "/tenpay_notify.htm"); // 接收财付通通知的URL
		reqHandler.setParameter("body", desc); // 商品描述
		reqHandler.setParameter("bank_type", "DEFAULT"); // 银行类型(中介担保时此参数无效)
		reqHandler.setParameter("spbill_create_ip", request.getRemoteAddr()); // 用户的公网ip，不是商户服务器IP
		reqHandler.setParameter("fee_type", "1"); // 币种，1人民币
		reqHandler.setParameter("subject", desc); // 商品名称(中介交易时必填)
		// 系统可选参数
		reqHandler.setParameter("sign_type", "MD5"); // 签名类型,默认：MD5
		reqHandler.setParameter("service_version", "1.0"); // 版本号，默认为1.0
		reqHandler.setParameter("input_charset", "UTF-8"); // 字符编码
		reqHandler.setParameter("sign_key_index", "1"); // 密钥序号

		// 业务可选参数
		reqHandler.setParameter("attach", attach); // 附加数据，原样返回
		reqHandler.setParameter("product_fee", ""); // 商品费用，必须保证transport_fee +
		reqHandler.setParameter("transport_fee", "0"); // 物流费用，必须保证transport_fee
		reqHandler.setParameter("time_start", currTime); // 订单生成时间，格式为yyyymmddhhmmss
		reqHandler.setParameter("time_expire", ""); // 订单失效时间，格式为yyyymmddhhmmss
		reqHandler.setParameter("buyer_id", ""); // 买方财付通账号
		reqHandler.setParameter("goods_tag", ""); // 商品标记
		reqHandler.setParameter("trade_mode", trade_mode); // 交易模式，1即时到账(默认)，2中介担保，3后台选择（买家进支付中心列表选择）
		reqHandler.setParameter("transport_desc", ""); // 物流说明
		reqHandler.setParameter("trans_type", "1"); // 交易类型，1实物交易，2虚拟交易
		reqHandler.setParameter("agentid", ""); // 平台ID
		reqHandler.setParameter("agent_type", ""); // 代理模式，0无代理(默认)，1表示卡易售模式，2表示网店模式
		reqHandler.setParameter("seller_id", ""); // 卖家商户号，为空则等同于partner
		// 请求的url
		String requestUrl = reqHandler.getRequestURL();
		response.sendRedirect(requestUrl);
	}

	/**
	 * 财付通在线支付回调控制
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/tenpay_return.htm")
	@Transactional
	public ModelAndView tenpay_return(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mv = new JModelAndView("order_finish_pay.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		ResponseHandler resHandler = new ResponseHandler(request, response);
		String[] attachs = request.getParameter("attach").split(",");
		// 商户订单号
		String out_trade_no = resHandler.getParameter("out_trade_no");
		OrderForm order = null;
		Predeposit obj = null;
		GoldRecord gold = null;
		IntegralGoodsOrder ig_order = null;
		if (attachs[0].equals("integral")) {
			ig_order = this.integralGoodsOrderService.getObjById(CommUtil
					.null2Long(attachs[1]));
			Map q_params = new HashMap();
			q_params.put("install", true);
			q_params.put("mark", ig_order.getIgo_payment());
			List<Payment> payments = this.paymentService
					.query("select obj from Payment obj where obj.install=:install and obj.mark=:mark",
							q_params, -1, -1);
			resHandler.setKey(payments.get(0).getTenpay_key());
		}
		if (attachs[0].equals("cash")) {
			obj = this.predepositService.getObjById(CommUtil
					.null2Long(attachs[1]));
			Map q_params = new HashMap();
			q_params.put("install", true);
			q_params.put("mark", obj.getPd_payment());
			List<Payment> payments = this.paymentService
					.query("select obj from Payment obj where obj.install=:install and obj.mark=:mark",
							q_params, -1, -1);
			resHandler.setKey(payments.get(0).getTenpay_key());
		}
		if (attachs[0].equals("gold")) {
			gold = this.goldRecordService.getObjById(CommUtil
					.null2Long(attachs[1]));
			Map q_params = new HashMap();
			q_params.put("install", true);
			q_params.put("mark", gold.getGold_payment());
			List<Payment> payments = this.paymentService
					.query("select obj from Payment obj where obj.install=:install and obj.mark=:mark",
							q_params, -1, -1);
			resHandler.setKey(payments.get(0).getTenpay_key());
		}
		if (attachs[0].equals("goods") || attachs[0].equals("group")) {
			order = this.orderFormService.getObjById(CommUtil
					.null2Long(attachs[1]));
			resHandler.setKey(order.getPayment().getTenpay_key());
		}
		// System.out.println("前台回调返回参数:" + resHandler.getAllParameters());
		// 判断签名
		if (resHandler.isTenpaySign()) {
			// 通知id
			String notify_id = resHandler.getParameter("notify_id");
			// 财付通订单号
			String transaction_id = resHandler.getParameter("transaction_id");
			// 金额,以分为单位
			String total_fee = resHandler.getParameter("total_fee");
			// 如果有使用折扣券，discount有值，total_fee+discount=原请求的total_fee
			String discount = resHandler.getParameter("discount");
			// 支付结果
			String trade_state = resHandler.getParameter("trade_state");
			// 交易模式，1即时到账，2中介担保
			String trade_mode = resHandler.getParameter("trade_mode");
			if ("1".equals(trade_mode)) { // 即时到账
				if ("0".equals(trade_state)) {
					// 即时到账处理业务完毕
					if (attachs[0].equals("cash")) {
						obj.setPd_status(1);
						obj.setPd_pay_status(2);
						this.predepositService.update(obj);
						User user = this.userService.getObjById(obj
								.getPd_user().getId());
						user.setAvailableBalance(BigDecimal.valueOf(CommUtil
								.add(user.getAvailableBalance(),
										obj.getPd_amount())));
						this.userService.update(user);
						mv = new JModelAndView("success.html",
								configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1,
								request, response);
						mv.addObject("op_title", "充值" + obj.getPd_amount()
								+ "成功");
						mv.addObject("url", CommUtil.getURL(request)
								+ "/buyer/predeposit_list.htm");
					}
					if (attachs[0].equals("goods")) {
						order.setOrder_status(20);
						order.setPayTime(new Date());
						this.orderFormService.update(order);
						if (order.getOrder_main() == 1
								&& !CommUtil.null2String(
										order.getChild_order_detail()).equals(
										"")) {// 同步完成子订单付款状态调整
							List<Map> maps = this.orderFormTools
									.queryGoodsInfo(order
											.getChild_order_detail());
							for (Map child_map : maps) {
								OrderForm child_order = this.orderFormService
										.getObjById(CommUtil
												.null2Long(child_map
														.get("order_id")));
								child_order.setOrder_status(20);
								this.orderFormService.update(child_order);
								// 向加盟商家发送付款成功短信提示，自营商品无需发送短信提示
								Store store = this.storeService
										.getObjById(CommUtil
												.null2Long(child_order
														.getStore_id()));
								User buyer = this.userService
										.getObjById(CommUtil.null2Long(order
												.getUser_id()));
								// 付款成功，发送邮件提示
								if (this.configService.getSysConfig()
										.isEmailEnable()) {
									this.send_order_email(request, child_order,
											buyer.getEmail(),
											"email_tobuyer_online_pay_ok_notify");
									if (child_order.getOrder_form() == 0) {
										this.send_order_email(request,
												child_order, store.getUser()
														.getEmail(),
												"email_toseller_online_pay_ok_notify");
									}
								}
								// 付款成功，发送短信提示
								if (this.configService.getSysConfig()
										.isSmsEnbale()) {
									this.send_order_sms(request, child_order,
											buyer.getMobile(),
											"sms_tobuyer_online_pay_ok_notify");
									if (child_order.getOrder_form() == 0) {
										this.send_order_sms(request,
												child_order, store.getUser()
														.getMobile(),
												"sms_toseller_online_pay_ok_notify");
									}
								}
							}
						}
						this.update_goods_inventory(order);// 更新商品库存
						OrderFormLog ofl = new OrderFormLog();
						ofl.setAddTime(new Date());
						ofl.setLog_info("财付通及时到账支付");
						ofl.setLog_user(SecurityUserHolder.getCurrentUser());
						ofl.setOf(order);
						this.orderFormLogService.save(ofl);
						mv.addObject("obj", order);
					}
					if (attachs[0].equals("group")) {
						this.generate_groupInfos(request, order, "tenpay",
								"财付通即时到帐支付", "");
						mv.addObject("obj", order);
					}
					if (attachs[0].equals("gold")) {
						gold.setGold_status(1);
						gold.setGold_pay_status(2);
						this.goldRecordService.update(gold);
						User user = this.userService.getObjById(gold
								.getGold_user().getId());
						user.setGold(user.getGold() + gold.getGold_count());
						this.userService.update(user);
						GoldLog log = new GoldLog();
						log.setAddTime(new Date());
						log.setGl_payment(gold.getGold_payment());
						log.setGl_content("财付通及时到账支付");
						log.setGl_money(gold.getGold_money());
						log.setGl_count(gold.getGold_count());
						log.setGl_type(0);
						log.setGl_user(gold.getGold_user());
						log.setGr(gold);
						this.goldLogService.save(log);
						mv = new JModelAndView("success.html",
								configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1,
								request, response);
						mv.addObject("op_title", "兑换" + gold.getGold_count()
								+ "金币成功");
						mv.addObject("url", CommUtil.getURL(request)
								+ "/seller/gold_record_list.htm");
					}
					if (attachs[0].equals("integral")) {
						ig_order.setIgo_status(20);
						ig_order.setIgo_pay_time(new Date());
						ig_order.setIgo_payment("bill");
						this.integralGoodsOrderService.update(ig_order);
						for (IntegralGoodsCart igc : ig_order.getIgo_gcs()) {
							IntegralGoods goods = igc.getGoods();
							goods.setIg_goods_count(goods.getIg_goods_count()
									- igc.getCount());
							goods.setIg_exchange_count(goods
									.getIg_exchange_count() + igc.getCount());
							this.integralGoodsService.update(goods);
						}
						mv = new JModelAndView("integral_order_finish.html",
								configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1,
								request, response);
						mv.addObject("obj", ig_order);
					}
				} else {
					mv = new JModelAndView("error.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "财付通支付失败！");
					mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
				}
			} else if ("2".equals(trade_mode)) { // 中介担保
				if ("0".equals(trade_state)) {
					// 中介担保处理业务完毕
					if (attachs[0].equals("cash")) {
						obj.setPd_status(1);
						obj.setPd_pay_status(2);
						this.predepositService.update(obj);
						User user = this.userService.getObjById(obj
								.getPd_user().getId());
						user.setAvailableBalance(BigDecimal.valueOf(CommUtil
								.add(user.getAvailableBalance(),
										obj.getPd_amount())));
						this.userService.update(user);
						PredepositLog log = new PredepositLog();
						log.setAddTime(new Date());
						log.setPd_log_amount(obj.getPd_amount());
						log.setPd_log_user(obj.getPd_user());
						log.setPd_op_type("充值");
						log.setPd_type("可用预存款");
						log.setPd_log_info("财付通中介担保付款");
						this.predepositLogService.save(log);
						mv = new JModelAndView("success.html",
								configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1,
								request, response);
						mv.addObject("op_title", "充值" + obj.getPd_amount()
								+ "成功");
						mv.addObject("url", CommUtil.getURL(request)
								+ "/buyer/predeposit_list.htm");
					}
					if (attachs[0].equals("goods")) {
						order.setOrder_status(20);
						order.setPayTime(new Date());
						this.orderFormService.update(order);
						if (order.getOrder_main() == 1
								&& !CommUtil.null2String(
										order.getChild_order_detail()).equals(
										"")) {// 同步完成子订单付款状态调整
							List<Map> maps = this.orderFormTools
									.queryGoodsInfo(order
											.getChild_order_detail());
							for (Map child_map : maps) {
								OrderForm child_order = this.orderFormService
										.getObjById(CommUtil
												.null2Long(child_map
														.get("order_id")));
								child_order.setOrder_status(20);
								this.orderFormService.update(child_order);
								// 向加盟商家发送付款成功短信提示，自营商品无需发送短信提示
								Store store = this.storeService
										.getObjById(CommUtil
												.null2Long(child_order
														.getStore_id()));
								User buyer = this.userService
										.getObjById(CommUtil.null2Long(order
												.getUser_id()));
								// 付款成功，发送邮件提示
								if (this.configService.getSysConfig()
										.isEmailEnable()) {
									this.send_order_email(request, child_order,
											buyer.getEmail(),
											"email_tobuyer_online_pay_ok_notify");
									if (child_order.getOrder_form() == 0) {
										this.send_order_email(request,
												child_order, store.getUser()
														.getEmail(),
												"email_toseller_online_pay_ok_notify");
									}
								}
								// 付款成功，发送短信提示
								if (this.configService.getSysConfig()
										.isSmsEnbale()) {
									this.send_order_sms(request, child_order,
											buyer.getMobile(),
											"sms_tobuyer_online_pay_ok_notify");
									if (child_order.getOrder_form() == 0) {
										this.send_order_sms(request,
												child_order, store.getUser()
														.getMobile(),
												"sms_toseller_online_pay_ok_notify");
									}
								}
							}
						}
						this.update_goods_inventory(order);// 更新商品库存
						OrderFormLog ofl = new OrderFormLog();
						ofl.setAddTime(new Date());
						ofl.setLog_info("财付通中介担保付款成功");
						ofl.setLog_user(SecurityUserHolder.getCurrentUser());
						ofl.setOf(order);
						this.orderFormLogService.save(ofl);
						mv.addObject("obj", order);
					}
					if (attachs[0].equals("gold")) {
						gold.setGold_status(1);
						gold.setGold_pay_status(2);
						this.goldRecordService.update(gold);
						User user = this.userService.getObjById(gold
								.getGold_user().getId());
						user.setGold(user.getGold() + gold.getGold_count());
						this.userService.update(user);
						GoldLog log = new GoldLog();
						log.setAddTime(new Date());
						log.setGl_payment(gold.getGold_payment());
						log.setGl_content("财付通中介担保付款成功");
						log.setGl_money(gold.getGold_money());
						log.setGl_count(gold.getGold_count());
						log.setGl_type(0);
						log.setGl_user(gold.getGold_user());
						log.setGr(gold);
						this.goldLogService.save(log);
						mv = new JModelAndView("success.html",
								configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1,
								request, response);
						mv.addObject("op_title", "兑换" + gold.getGold_count()
								+ "金币成功");
						mv.addObject("url", CommUtil.getURL(request)
								+ "/seller/gold_record_list.htm");
					}
					if (attachs[0].equals("integral")) {
						ig_order.setIgo_status(20);
						ig_order.setIgo_pay_time(new Date());
						ig_order.setIgo_payment("bill");
						this.integralGoodsOrderService.update(ig_order);
						for (IntegralGoodsCart igc : ig_order.getIgo_gcs()) {
							IntegralGoods goods = igc.getGoods();
							goods.setIg_goods_count(goods.getIg_goods_count()
									- igc.getCount());
							goods.setIg_exchange_count(goods
									.getIg_exchange_count() + igc.getCount());
							this.integralGoodsService.update(goods);
						}
						mv = new JModelAndView("integral_order_finish.html",
								configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1,
								request, response);
						mv.addObject("obj", ig_order);
					}
				} else {
					mv = new JModelAndView("error.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "财付通支付失败！");
					mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
				}
			}
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "财付通认证签名失败！");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		}
		return mv;
	}

	/**
	 * 网银在线回调函数
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/chinabank_return.htm")
	@Transactional
	public ModelAndView chinabank_return(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mv = new JModelAndView("order_finish_pay.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String remark1 = request.getParameter("remark1"); // 备注1
		String remark2 = CommUtil.null2String(request.getParameter("remark2"));
		OrderForm order = null;
		Predeposit obj = null;
		GoldRecord gold = null;
		IntegralGoodsOrder ig_order = null;
		if (remark2.equals("goods") || remark2.equals("group")) {
			order = this.orderFormService.getObjById(CommUtil.null2Long(remark1
					.trim()));
		}
		if (remark2.equals("cash")) {
			obj = this.predepositService
					.getObjById(CommUtil.null2Long(remark1));
		}
		if (remark2.equals("gold")) {
			gold = this.goldRecordService.getObjById(CommUtil
					.null2Long(remark1));
		}
		if (remark2.equals("integral")) {
			ig_order = this.integralGoodsOrderService.getObjById(CommUtil
					.null2Long(remark1));
		}
		String key = "";
		if (remark2.equals("goods") || remark2.equals("group")) {
			key = order.getPayment().getChinabank_key();
		}
		if (remark2.equals("cash") || remark2.equals("gold")
				|| remark2.equals("integral")) {
			Map q_params = new HashMap();
			q_params.put("install", true);
			if (remark2.equals("cash")) {
				q_params.put("mark", obj.getPd_payment());
			}
			if (remark2.equals("gold")) {
				q_params.put("mark", gold.getGold_payment());
			}
			if (remark2.equals("integral")) {
				q_params.put("mark", ig_order.getIgo_payment());
			}
			List<Payment> payments = this.paymentService
					.query("select obj from Payment obj where obj.install=:install and obj.mark=:mark",
							q_params, -1, -1);
			key = payments.get(0).getChinabank_key();
		}
		String v_oid = request.getParameter("v_oid"); // 订单号
		String v_pmode = request.getParameter("v_pmode");// new
		// String(request.getParameter("v_pmode").getBytes("ISO-8859-1"),
		// "UTF-8"); //
		// 支付方式中文说明，如"中行长城信用卡"
		String v_pstatus = request.getParameter("v_pstatus"); // 支付结果，20支付完成；30支付失败；
		String v_pstring = request.getParameter("v_pstring");// new
		// String(request.getParameter("v_pstring").getBytes("ISO-8859-1"),
		// "UTF-8"); //
		// 对支付结果的说明，成功时（v_pstatus=20）为"支付成功"，支付失败时（v_pstatus=30）为"支付失败"
		String v_amount = request.getParameter("v_amount"); // 订单实际支付金额
		String v_moneytype = request.getParameter("v_moneytype"); // 币种
		String v_md5str = request.getParameter("v_md5str"); // MD5校验码
		String text = v_oid + v_pstatus + v_amount + v_moneytype + key; // 拼凑加密串
		String v_md5text = Md5Encrypt.md5(text).toUpperCase();
		if (v_md5str.equals(v_md5text)) {
			if ("20".equals(v_pstatus)) {
				// 支付成功，商户 根据自己业务做相应逻辑处理
				// 此处加入商户系统的逻辑处理（例如判断金额，判断支付状态(20成功,30失败)，更新订单状态等等）......
				if (remark2.equals("goods")) {
					order.setOrder_status(20);
					order.setPayTime(new Date());
					this.orderFormService.update(order);
					if (order.getOrder_main() == 1
							&& !CommUtil.null2String(
									order.getChild_order_detail()).equals("")) {// 同步完成子订单付款状态调整
						List<Map> maps = this.orderFormTools
								.queryGoodsInfo(order.getChild_order_detail());
						for (Map child_map : maps) {
							OrderForm child_order = this.orderFormService
									.getObjById(CommUtil.null2Long(child_map
											.get("order_id")));
							child_order.setOrder_status(20);
							this.orderFormService.update(child_order);
							// 向加盟商家发送付款成功短信提示，自营商品无需发送短信提示
							Store store = this.storeService.getObjById(CommUtil
									.null2Long(child_order.getStore_id()));
							User buyer = this.userService.getObjById(CommUtil
									.null2Long(order.getUser_id()));
							// 付款成功，发送邮件提示
							if (this.configService.getSysConfig()
									.isEmailEnable()) {
								this.send_order_email(request, child_order,
										buyer.getEmail(),
										"email_tobuyer_online_pay_ok_notify");
								if (child_order.getOrder_form() == 0) {
									this.send_order_email(request, child_order,
											store.getUser().getEmail(),
											"email_toseller_online_pay_ok_notify");
								}
							}
							// 付款成功，发送短信提示
							if (this.configService.getSysConfig().isSmsEnbale()) {
								this.send_order_sms(request, child_order,
										buyer.getMobile(),
										"sms_tobuyer_online_pay_ok_notify");
								if (child_order.getOrder_form() == 0) {
									this.send_order_sms(request, child_order,
											store.getUser().getMobile(),
											"sms_toseller_online_pay_ok_notify");
								}
							}
						}
					}
					this.update_goods_inventory(order);// 更新商品库存
					OrderFormLog ofl = new OrderFormLog();
					ofl.setAddTime(new Date());
					ofl.setLog_info("网银在线支付");
					ofl.setLog_user(SecurityUserHolder.getCurrentUser());
					ofl.setOf(order);
					this.orderFormLogService.save(ofl);
					mv.addObject("obj", order);
					Store store = this.storeService.getObjById(CommUtil
							.null2Long(order.getStore_id()));
					User user = this.userService.getObjById(store.getUser()
							.getId());
					// 付款成功，发送邮件提示
					User buyer = this.userService.getObjById(CommUtil
							.null2Long(order.getUser_id()));
					if (this.configService.getSysConfig().isEmailEnable()) {
						this.send_order_email(request, order, buyer.getEmail(),
								"email_tobuyer_online_pay_ok_notify");
						if (order.getOrder_form() == 0) {
							this.send_order_email(request, order, store
									.getUser().getEmail(),
									"email_toseller_online_pay_ok_notify");
						}
					}
					// 付款成功，发送短信提示
					if (this.configService.getSysConfig().isSmsEnbale()) {
						this.send_order_sms(request, order, buyer.getMobile(),
								"sms_tobuyer_online_pay_ok_notify");
						if (order.getOrder_form() == 0) {
							this.send_order_sms(request, order, store.getUser()
									.getMobile(),
									"sms_toseller_online_pay_ok_notify");
						}
					}
				}
				if (remark2.equals("group")) {
					this.generate_groupInfos(request, order, "chinabank",
							"网银在线支付", "");
					mv.addObject("obj", order);
				}
				if (remark2.endsWith("cash")) {
					obj.setPd_status(1);
					obj.setPd_pay_status(2);
					this.predepositService.update(obj);
					User user = this.userService.getObjById(obj.getPd_user()
							.getId());
					user.setAvailableBalance(BigDecimal.valueOf(CommUtil.add(
							user.getAvailableBalance(), obj.getPd_amount())));
					this.userService.update(user);
					PredepositLog log = new PredepositLog();
					log.setAddTime(new Date());
					log.setPd_log_amount(obj.getPd_amount());
					log.setPd_log_user(obj.getPd_user());
					log.setPd_op_type("充值");
					log.setPd_type("可用预存款");
					log.setPd_log_info("网银在线支付");
					this.predepositLogService.save(log);
					mv = new JModelAndView("success.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "充值" + obj.getPd_amount() + "成功");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/buyer/predeposit_list.htm");
				}
				if (remark2.equals("gold")) {
					gold.setGold_status(1);
					gold.setGold_pay_status(2);
					this.goldRecordService.update(gold);
					User user = this.userService.getObjById(gold.getGold_user()
							.getId());
					user.setGold(user.getGold() + gold.getGold_count());
					this.userService.update(user);
					GoldLog log = new GoldLog();
					log.setAddTime(new Date());
					log.setGl_payment(gold.getGold_payment());
					log.setGl_content("网银在线支付");
					log.setGl_money(gold.getGold_money());
					log.setGl_count(gold.getGold_count());
					log.setGl_type(0);
					log.setGl_user(gold.getGold_user());
					log.setGr(gold);
					this.goldLogService.save(log);
					mv = new JModelAndView("success.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "兑换" + gold.getGold_count()
							+ "金币成功");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/seller/gold_record_list.htm");
				}
				if (remark2.equals("gold")) {
					ig_order.setIgo_status(20);
					ig_order.setIgo_pay_time(new Date());
					ig_order.setIgo_payment("bill");
					this.integralGoodsOrderService.update(ig_order);
					for (IntegralGoodsCart igc : ig_order.getIgo_gcs()) {
						IntegralGoods goods = igc.getGoods();
						goods.setIg_goods_count(goods.getIg_goods_count()
								- igc.getCount());
						goods.setIg_exchange_count(goods.getIg_exchange_count()
								+ igc.getCount());
						this.integralGoodsService.update(goods);
					}
					mv = new JModelAndView("integral_order_finish.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("obj", ig_order);
				}
			}
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "网银在线支付失败！");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");

		}
		return mv;
	}

	/**
	 * paypal回调方法,paypal支付成功了后，调用该方法进行后续处理
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/paypal_return.htm")
	@Transactional
	public ModelAndView paypal_return(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mv = new JModelAndView("order_finish_pay.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Enumeration en = request.getParameterNames();
		String str = "cmd=_notify-validate";
		while (en.hasMoreElements()) {
			String paramName = (String) en.nextElement();
			String paramValue = request.getParameter(paramName);
			str = str + "&" + paramName + "=" + URLEncoder.encode(paramValue);
		}
		String[] customs = CommUtil.null2String(request.getParameter("custom"))
				.split(",");
		String remark1 = customs[0];
		String remark2 = customs[1];
		String item_name = request.getParameter("item_name");
		String txnId = request.getParameter("txn_id");
		OrderForm order = null;
		Predeposit obj = null;
		GoldRecord gold = null;
		IntegralGoodsOrder ig_order = null;
		if (remark2.equals("goods") || remark2.equals("group")) {
			order = this.orderFormService.getObjById(CommUtil.null2Long(remark1
					.trim()));
		}
		if (remark2.equals("cash")) {
			obj = this.predepositService
					.getObjById(CommUtil.null2Long(remark1));
		}
		if (remark2.equals("gold")) {
			gold = this.goldRecordService.getObjById(CommUtil
					.null2Long(remark1));
		}
		if (remark2.equals("integral")) {
			ig_order = this.integralGoodsOrderService.getObjById(CommUtil
					.null2Long(remark1));
		}
		String txn_id = request.getParameter("txn_id");
		//
		// 建议在此将接受到的信息str记录到日志文件中以确认是否收到IPN信息
		// 将信息POST回给PayPal进行验证
		// 设置HTTP的头信息
		// 在Sandbox情况下，设置：
		// URL u = new URL("http://www.sanbox.paypal.com/cgi-bin/webscr");
		// URLConnection uc = u.openConnection();
		// uc.setDoOutput(true);
		// uc.setRequestProperty("Content-Type",
		// "application/x-www-form-urlencoded");
		// PrintWriter pw = new PrintWriter(uc.getOutputStream());
		// pw.println(str);
		// pw.close();
		// 接受PayPal对IPN回发的回复信息
		// BufferedReader in = new BufferedReader(new InputStreamReader(uc
		// .getOutputStream()));
		// String res = in.readLine();
		// in.close();
		// System.out.println("接受PayPal对IPN回发的回复信息：" + res);

		// 将POST信息分配给本地变量，可以根据您的需要添加
		// 该付款明细所有变量可参考：
		String itemName = request.getParameter("item_name");
		String paymentStatus = request.getParameter("payment_status");
		String paymentAmount = request.getParameter("mc_gross");
		String paymentCurrency = request.getParameter("mc_currency");
		String receiverEmail = request.getParameter("receiver_email");
		String payerEmail = request.getParameter("payer_email");
		// System.out.println("付款明细信息：订单编号:" + itemName + ",支付状态：" +
		// paymentStatus+ ",金额：" + paymentAmount + ",货币种类：" + paymentCurrency+
		// ",paypal支付流水号:" + txnId + ",paypal接收方账号：" + receiverEmail+
		// ",paypal支付方账号" + payerEmail);
		if (paymentStatus.equals("Completed")
				|| paymentStatus.equals("Pending")) {
			if (remark2.equals("goods")) {
				order.setOrder_status(20);
				order.setPayTime(new Date());
				this.orderFormService.update(order);
				if (order.getOrder_main() == 1
						&& !CommUtil.null2String(order.getChild_order_detail())
								.equals("")) {// 同步完成子订单付款状态调整
					List<Map> maps = this.orderFormTools.queryGoodsInfo(order
							.getChild_order_detail());
					for (Map child_map : maps) {
						OrderForm child_order = this.orderFormService
								.getObjById(CommUtil.null2Long(child_map
										.get("order_id")));
						child_order.setOrder_status(20);
						this.orderFormService.update(child_order);
						// 向加盟商家发送付款成功短信提示，自营商品无需发送短信提示
						Store store = this.storeService.getObjById(CommUtil
								.null2Long(child_order.getStore_id()));
						User buyer = this.userService.getObjById(CommUtil
								.null2Long(order.getUser_id()));
						// 付款成功，发送邮件提示
						if (this.configService.getSysConfig().isEmailEnable()) {
							this.send_order_email(request, child_order,
									buyer.getEmail(),
									"email_tobuyer_online_pay_ok_notify");
							if (child_order.getOrder_form() == 0) {
								this.send_order_email(request, child_order,
										store.getUser().getEmail(),
										"email_toseller_online_pay_ok_notify");
							}
						}
						// 付款成功，发送短信提示
						if (this.configService.getSysConfig().isSmsEnbale()) {
							this.send_order_sms(request, child_order,
									buyer.getMobile(),
									"sms_tobuyer_online_pay_ok_notify");
							if (child_order.getOrder_form() == 0) {
								this.send_order_sms(request, child_order, store
										.getUser().getMobile(),
										"sms_toseller_online_pay_ok_notify");
							}
						}
					}
				}
				this.update_goods_inventory(order);// 更新商品库存
				OrderFormLog ofl = new OrderFormLog();
				ofl.setAddTime(new Date());
				ofl.setLog_info("Paypal在线支付");
				ofl.setLog_user(SecurityUserHolder.getCurrentUser());
				ofl.setOf(order);
				this.orderFormLogService.save(ofl);
				mv.addObject("obj", order);
				Store store = this.storeService.getObjById(CommUtil
						.null2Long(order.getStore_id()));
				User user = this.userService
						.getObjById(store.getUser().getId());
				// 付款成功，发送邮件提示
				User buyer = this.userService.getObjById(CommUtil
						.null2Long(order.getUser_id()));
				if (this.configService.getSysConfig().isEmailEnable()) {
					this.send_order_email(request, order, buyer.getEmail(),
							"email_tobuyer_online_pay_ok_notify");
					if (order.getOrder_form() == 0) {
						this.send_order_email(request, order, store.getUser()
								.getEmail(),
								"email_toseller_online_pay_ok_notify");
					}
				}
				// 付款成功，发送短信提示
				if (this.configService.getSysConfig().isSmsEnbale()) {
					this.send_order_sms(request, order, buyer.getMobile(),
							"sms_tobuyer_online_pay_ok_notify");
					if (order.getOrder_form() == 0) {
						this.send_order_sms(request, order, store.getUser()
								.getMobile(),
								"sms_toseller_online_pay_ok_notify");
					}
				}
			}
			if (remark2.equals("group")) {
				this.generate_groupInfos(request, order, "paypal",
						"paypal在线支付", "");
				mv.addObject("obj", order);
			}
			if (remark2.endsWith("cash")) {
				obj.setPd_status(1);
				obj.setPd_pay_status(2);
				this.predepositService.update(obj);
				User user = this.userService.getObjById(obj.getPd_user()
						.getId());
				user.setAvailableBalance(BigDecimal.valueOf(CommUtil.add(
						user.getAvailableBalance(), obj.getPd_amount())));
				this.userService.update(user);
				PredepositLog log = new PredepositLog();
				log.setAddTime(new Date());
				log.setPd_log_amount(obj.getPd_amount());
				log.setPd_log_user(obj.getPd_user());
				log.setPd_op_type("充值");
				log.setPd_type("可用预存款");
				log.setPd_log_info("Paypal在线支付");
				this.predepositLogService.save(log);
				mv = new JModelAndView("success.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "成功充值：" + obj.getPd_amount());
				mv.addObject("url", CommUtil.getURL(request)
						+ "/buyer/predeposit_list.htm");
			}
			if (remark2.equals("gold")) {
				gold.setGold_status(1);
				gold.setGold_pay_status(2);
				this.goldRecordService.update(gold);
				User user = this.userService.getObjById(gold.getGold_user()
						.getId());
				user.setGold(user.getGold() + gold.getGold_count());
				this.userService.update(user);
				GoldLog log = new GoldLog();
				log.setAddTime(new Date());
				log.setGl_payment(gold.getGold_payment());
				log.setGl_content("Paypal");
				log.setGl_money(gold.getGold_money());
				log.setGl_count(gold.getGold_count());
				log.setGl_type(0);
				log.setGl_user(gold.getGold_user());
				log.setGr(gold);
				this.goldLogService.save(log);
				mv = new JModelAndView("success.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "成功充值金币:" + gold.getGold_count());
				mv.addObject("url", CommUtil.getURL(request)
						+ "/seller/gold_record_list.htm");
			}
			if (remark2.equals("gold")) {
				ig_order.setIgo_status(20);
				ig_order.setIgo_pay_time(new Date());
				ig_order.setIgo_payment("paypal");
				this.integralGoodsOrderService.update(ig_order);
				for (IntegralGoodsCart igc : ig_order.getIgo_gcs()) {
					IntegralGoods goods = igc.getGoods();
					goods.setIg_goods_count(goods.getIg_goods_count()
							- igc.getCount());
					goods.setIg_exchange_count(goods.getIg_exchange_count()
							+ igc.getCount());
					this.integralGoodsService.update(goods);
				}
				mv = new JModelAndView("integral_order_finish.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("obj", ig_order);
			}
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "Paypal支付失败");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		}
		return mv;
	}
	
	/**
	 * 通联支付在线支付成功回调控制
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/allinpay_return.htm")
	@Transactional
	public ModelAndView allinpay_return(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mv = new JModelAndView("order_finish_pay.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		log.info("通联支付已经回写！");
		String merchantId=request.getParameter("merchantId");//商户号
		String version=request.getParameter("version");//网关返回支付结果接口版本
		String language=request.getParameter("language");//网页显示语言种类
		String signType=request.getParameter("signType");//签名类型
		String payType=request.getParameter("payType");//支付方式
		String issuerId=request.getParameter("issuerId");//发卡方机构代码
		String paymentOrderId=request.getParameter("paymentOrderId");//通联订单号
		String orderNo=request.getParameter("orderNo");//商户订单号
		String orderDatetime=request.getParameter("orderDatetime");//商户订单提交时间
		String orderAmount=request.getParameter("orderAmount");//商户订单金额
		String payDatetime=request.getParameter("payDatetime");//支付完成时间
		String payAmount=request.getParameter("payAmount");//订单实际支付金额
		String ext1=request.getParameter("ext1");//
		String ext2=request.getParameter("ext2").trim();//
		String payResult=request.getParameter("payResult");//处理结果
		String errorCode=request.getParameter("errorCode");//错误代码
		String returnDatetime=request.getParameter("returnDatetime");//结果返回时间
		String signMsg=request.getParameter("signMsg");
	
		//验签是商户为了验证接收到的报文数据确实是支付网关发送的。
		//构造订单结果对象，验证签名。
		PaymentResult paymentResult = new PaymentResult();
		paymentResult.setMerchantId(merchantId);
		paymentResult.setVersion(version);
		paymentResult.setLanguage(language);
		paymentResult.setSignType(signType);
		paymentResult.setPayType(payType);
		paymentResult.setIssuerId(issuerId);
		paymentResult.setPaymentOrderId(paymentOrderId);
		paymentResult.setOrderNo(orderNo);
		paymentResult.setOrderDatetime(orderDatetime);
		paymentResult.setOrderAmount(orderAmount);
		paymentResult.setPayDatetime(payDatetime);
		paymentResult.setPayAmount(payAmount);
		paymentResult.setExt1(ext1);
		paymentResult.setExt2(ext2);
		paymentResult.setPayResult(payResult);
		paymentResult.setErrorCode(errorCode);
		paymentResult.setReturnDatetime(returnDatetime);
		log.info("回写订单号："+paymentOrderId);
		//signMsg为服务器端返回的签名值。
		paymentResult.setSignMsg(signMsg); 
		
		Map params = new HashMap();
		params.put("mark", "allinpay");
		List<Payment> payments = this.paymentService.query(
				"select obj from Payment obj where obj.mark=:mark", params, -1,
				-1);
		Payment shop_payment = new Payment();
		if (payments.size() > 0) {
			shop_payment = payments.get(0);
		}
		//signType为"1"时，必须设置证书路径。
		if("1".equals(signType)&&shop_payment!=null)
		paymentResult.setCertPath(shop_payment.getPath()); 
		
		//验证签名：返回true代表验签成功；否则验签失败。
		boolean verifyResult = paymentResult.verify();
		
		//验签成功，还需要判断订单状态，为"1"表示支付成功。
		boolean paySuccess = verifyResult && payResult.equals("1");
		OrderForm main_order = null;
		Predeposit obj = null;
		GoldRecord gold = null;
		IntegralGoodsOrder ig_order = null;
		if (ext2.equals("goods")) {
			main_order = this.orderFormService.getObjById(CommUtil
					.null2Long(ext1));
		}
		if (ext2.equals("cash")) {
			obj = this.predepositService.getObjById(CommUtil
					.null2Long(ext1));
		}
		if (ext2.equals("gold")) {
			gold = this.goldRecordService.getObjById(CommUtil
					.null2Long(ext1));
		}
		if (ext2.equals("integral")) {
			ig_order = this.integralGoodsOrderService.getObjById(CommUtil
					.null2Long(ext1));
		}
		if (ext2.equals("group")) {
			main_order = this.orderFormService.getObjById(CommUtil
					.null2Long(ext1));
		}
		if (paySuccess) {// 验证成功
			if (ext2.equals("goods")) {
					if (main_order.getOrder_status() < 20) {// 异步没有出来订单，则同步处理订单
						main_order.setOrder_status(20);
						main_order.setPayTime(new Date());
						this.orderFormService.update(main_order);
						// 主订单付款成功，发送邮件提示
						if (this.configService.getSysConfig().isEmailEnable()) {
							// 向加盟商家发送付款成功短信提示，自营商品无需发送短信提示
							User buyer = this.userService.getObjById(CommUtil
									.null2Long(main_order.getUser_id()));
							this.send_order_email(request, main_order,
									buyer.getEmail(),
									"email_tobuyer_online_pay_ok_notify");
							if (main_order.getOrder_form() == 0) {
								Store store = this.storeService
										.getObjById(CommUtil
												.null2Long(main_order
														.getStore_id()));
								this.send_order_email(request, main_order,
										store.getUser().getEmail(),
										"email_toseller_online_pay_ok_notify");
							}
						}
						// 主订单付款成功，发送短信提示
						if (this.configService.getSysConfig().isSmsEnbale()) {
							User buyer = this.userService.getObjById(CommUtil
									.null2Long(main_order.getUser_id()));
							this.send_order_sms(request, main_order,
									buyer.getMobile(),
									"sms_tobuyer_online_pay_ok_notify");
							if (main_order.getOrder_form() == 0) {
								Store store = this.storeService
										.getObjById(CommUtil
												.null2Long(main_order
														.getStore_id()));
								this.send_order_sms(request, main_order, store
										.getUser().getMobile(),
										"sms_toseller_online_pay_ok_notify");
							}
						}
						// 主订单付款成功，订单状态更新，同时更新商品库存，如果是团购商品，则更新团购库存
						this.update_goods_inventory(main_order);
						OrderFormLog main_ofl = new OrderFormLog();
						main_ofl.setAddTime(new Date());
						main_ofl.setLog_info("通联在线支付");
						User main_buyer = this.userService.getObjById(CommUtil
								.null2Long(main_order.getUser_id()));
						main_ofl.setLog_user(main_buyer);
						main_ofl.setOf(main_order);
						this.orderFormLogService.save(main_ofl);
						// 子订单操作
						if (main_order.getOrder_main() == 1
								&& !CommUtil.null2String(
										main_order.getChild_order_detail())
										.equals("")) {// 同步完成子订单付款状态调整
							List<Map> maps = this.orderFormTools
									.queryGoodsInfo(main_order
											.getChild_order_detail());
							for (Map child_map : maps) {
								OrderForm child_order = this.orderFormService
										.getObjById(CommUtil
												.null2Long(child_map
														.get("order_id")));
								child_order.setOrder_status(20);
								this.orderFormService.update(child_order);
								// 付款成功，发送邮件提示
								if (this.configService.getSysConfig()
										.isEmailEnable()) {
									// 向加盟商家发送付款成功短信提示，自营商品无需发送短信提示
									User buyer = this.userService
											.getObjById(CommUtil
													.null2Long(child_order
															.getUser_id()));
									this.send_order_email(request, child_order,
											buyer.getEmail(),
											"email_tobuyer_online_pay_ok_notify");
									if (child_order.getOrder_form() == 0) {
										Store store = this.storeService
												.getObjById(CommUtil
														.null2Long(child_order
																.getStore_id()));
										this.send_order_email(request,
												child_order, store.getUser()
														.getEmail(),
												"email_toseller_online_pay_ok_notify");
									}
								}
								// 付款成功，发送短信提示
								if (this.configService.getSysConfig()
										.isSmsEnbale()) {
									User buyer = this.userService
											.getObjById(CommUtil
													.null2Long(child_order
															.getUser_id()));
									this.send_order_sms(request, child_order,
											buyer.getMobile(),
											"sms_tobuyer_online_pay_ok_notify");
									if (child_order.getOrder_form() == 0) {
										Store store = this.storeService
												.getObjById(CommUtil
														.null2Long(child_order
																.getStore_id()));
										this.send_order_sms(request,
												child_order, store.getUser()
														.getMobile(),
												"sms_toseller_online_pay_ok_notify");
									}
								}
								// 子订单付款成功，订单状态更新，同时更新商品库存，如果是团购商品，则更新团购库存
								this.update_goods_inventory(child_order);
								OrderFormLog ofl = new OrderFormLog();
								ofl.setAddTime(new Date());
								ofl.setLog_info("通联在线支付");
								User buyer = this.userService
										.getObjById(CommUtil
												.null2Long(child_order
														.getUser_id()));
								ofl.setLog_user(buyer);
								ofl.setOf(child_order);
								this.orderFormLogService.save(ofl);
							}
						}
					}
					mv.addObject("obj", main_order);
			}
			if (ext2.equals("group")) {
					if (main_order.getOrder_status() != 20) {// 异步没有出来订单，则同步处理订单
						this.generate_groupInfos(request, main_order, "allinpay",
								"通联在线支付", paymentOrderId);
					}
					mv.addObject("obj", main_order);
			}
			if (ext2.equals("cash")) {
					if (obj.getPd_pay_status() != 2) {// 异步没有处理该充值业务，则同步处理一下
						obj.setPd_status(1);
						obj.setPd_pay_status(2);
						this.predepositService.update(obj);
						User user = this.userService.getObjById(obj
								.getPd_user().getId());
						user.setAvailableBalance(BigDecimal.valueOf(CommUtil
								.add(user.getAvailableBalance(),
										obj.getPd_amount())));
						this.userService.update(user);
						PredepositLog log = new PredepositLog();
						log.setAddTime(new Date());
						log.setPd_log_amount(obj.getPd_amount());
						log.setPd_log_user(obj.getPd_user());
						log.setPd_op_type("充值");
						log.setPd_type("可用预存款");
						log.setPd_log_info("通联在线支付");
						this.predepositLogService.save(log);
					}
					mv = new JModelAndView("success.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "充值" + obj.getPd_amount() + "成功");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/buyer/predeposit_list.htm");
			}
			if (ext2.equals("gold")) {
					if (gold.getGold_pay_status() != 2) {
						gold.setGold_status(1);
						gold.setGold_pay_status(2);
						this.goldRecordService.update(gold);
						User user = this.userService.getObjById(gold
								.getGold_user().getId());
						user.setGold(user.getGold() + gold.getGold_count());
						this.userService.update(user);
						GoldLog log = new GoldLog();
						log.setAddTime(new Date());
						log.setGl_payment(gold.getGold_payment());
						log.setGl_content("通联在线支付");
						log.setGl_money(gold.getGold_money());
						log.setGl_count(gold.getGold_count());
						log.setGl_type(0);
						log.setGl_user(gold.getGold_user());
						log.setGr(gold);
						this.goldLogService.save(log);
					}
					mv = new JModelAndView("success.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "兑换" + gold.getGold_count()
							+ "金币成功");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/seller/gold_record_list.htm");
			}
			if (ext2.equals("integral")) {
					if (ig_order.getIgo_status() < 20) {
						ig_order.setIgo_status(20);
						ig_order.setIgo_pay_time(new Date());
						ig_order.setIgo_payment("allinpay");
						this.integralGoodsOrderService.update(ig_order);
						for (IntegralGoodsCart igc : ig_order.getIgo_gcs()) {
							IntegralGoods goods = igc.getGoods();
							goods.setIg_goods_count(goods.getIg_goods_count()
									- igc.getCount());
							goods.setIg_exchange_count(goods
									.getIg_exchange_count() + igc.getCount());
							this.integralGoodsService.update(goods);
						}
					}
					mv = new JModelAndView("integral_order_finish.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("obj", ig_order);
				}
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "支付回调失败！");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		}
		return mv;
	}


	private void generate_groupInfos(HttpServletRequest request,
			OrderForm order, String mark, String pay_info, String trade_no)
			throws Exception {
		if (order.getOrder_status() < 20) {
			order.setOrder_status(20);
			order.setOut_order_id(trade_no);
			order.setPayTime(new Date());
			// 生活团购订单付款时增加退款时效
			if (order.getOrder_cat() == 2) {
				Calendar ca = Calendar.getInstance();
				ca.add(ca.DATE, this.configService.getSysConfig()
						.getGrouplife_order_return());
				SimpleDateFormat bartDateFormat = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				String latertime = bartDateFormat.format(ca.getTime());
				order.setReturn_shipTime(CommUtil.formatDate(latertime));
			}
			this.orderFormService.update(order);
			OrderFormLog ofl = new OrderFormLog();
			ofl.setAddTime(new Date());
			ofl.setLog_info(pay_info);
			User buyer = this.userService.getObjById(CommUtil.null2Long(order
					.getUser_id()));
			ofl.setLog_user(buyer);
			ofl.setOf(order);
			this.orderFormLogService.save(ofl);
			Store store = null;
			if (order.getStore_id() != null && !"".equals(order.getStore_id())) {
				store = this.storeService.getObjById(CommUtil.null2Long(order
						.getStore_id()));
			}

			if (order.getOrder_cat() == 2) {
				Map map = this.orderFormTools.queryGroupInfo(order
						.getGroup_info());
				int count = CommUtil
						.null2Int(map.get("goods_count").toString());
				String goods_id = map.get("goods_id").toString();
				GroupLifeGoods goods = this.groupLifeGoodsService
						.getObjById(CommUtil.null2Long(goods_id));
				goods.setGroup_count(goods.getGroup_count()
						- CommUtil.null2Int(count));
				goods.setSelled_count(goods.getSelled_count()
						+ CommUtil.null2Int(count));
				this.groupLifeGoodsService.update(goods);
				Map pay_params = new HashMap();
				pay_params.put("mark", mark);
				List<Payment> payments = this.paymentService.query(
						"select obj from Payment obj where obj.mark=:mark",
						pay_params, -1, -1);
				int i = 0;
				List<String> code_list = new ArrayList();// 存放团购消费码
				String codes = "";
				while (i < count) {
					GroupInfo info = new GroupInfo();
					info.setAddTime(new Date());
					info.setLifeGoods(goods);
					info.setPayment(payments.get(0));
					info.setOrder_id(order.getId());
					info.setUser_id(buyer.getId());
					info.setUser_name(buyer.getUserName());
					info.setGroup_sn(buyer.getId()
							+ CommUtil.formatTime("yyyyMMddHHmmss" + i,
									new Date()));
					Calendar ca2 = Calendar.getInstance();
					ca2.add(ca2.DATE, this.configService.getSysConfig()
							.getGrouplife_order_return());
					SimpleDateFormat bartDateFormat2 = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					String latertime2 = bartDateFormat2.format(ca2.getTime());
					info.setRefund_Time(CommUtil.formatDate(latertime2));
					this.groupInfoService.save(info);
					codes = codes + info.getGroup_sn() + " ";
					code_list.add(info.getGroup_sn());
					i++;
				}
				// 更新lucene索引
				elasticsearchUtil.indexUpdate(IndexName.GOODS, IndexType.GROUPLIFEGOODS,
						goods.getId().toString(), IndexVoTools.groupLifeGoodsToIndexVo(goods));
//				String goods_lucene_path = ConfigContants.LUCENE_DIRECTORY
//						+ File.separator + "luence" + File.separator
//						+ "grouplifegoods";
//				File file = new File(goods_lucene_path);
//				if (!file.exists()) {
//					CommUtil.createFolder(goods_lucene_path);
//				}
//				LuceneUtil lucene = LuceneUtil.instance();
//				lucene.setIndex_path(goods_lucene_path);
//				lucene.update(CommUtil.null2String(goods.getId()),
//						luceneVoTools.updateLifeGoodsIndex(goods));
				// 如果为运营商发布的团购则进行结算日志生成
				if (order.getOrder_form() == 0) {
					PayoffLog plog = new PayoffLog();
					plog.setPl_sn("pl"
							+ CommUtil.formatTime("yyyyMMddHHmmss", new Date())
							+ store.getUser().getId());
					plog.setPl_info("团购码生成成功");
					plog.setAddTime(new Date());
					plog.setSeller(store.getUser());
					plog.setO_id(CommUtil.null2String(order.getId()));
					plog.setOrder_id(order.getOrder_id().toString());
					plog.setCommission_amount(BigDecimal.valueOf(CommUtil
							.null2Double("0.00")));// 该订单总佣金费用
					plog.setGoods_info(order.getGroup_info());
					plog.setOrder_total_price(order.getTotalPrice());// 该订单总商品金额
					plog.setTotal_amount(order.getTotalPrice());// 该订单应结算金额：结算金额=订单总商品金额-总佣金费用
					this.payoffservice.save(plog);
					store.setStore_sale_amount(BigDecimal.valueOf(CommUtil.add(
							order.getGoods_amount(),
							store.getStore_sale_amount())));// 店铺本次结算总销售金额
					store.setStore_commission_amount(BigDecimal
							.valueOf(CommUtil.add(order.getCommission_amount(),
									store.getStore_commission_amount())));// 店铺本次结算总佣金
					store.setStore_payoff_amount(BigDecimal.valueOf(CommUtil
							.add(plog.getTotal_amount(),
									store.getStore_payoff_amount())));// 店铺本次结算总佣金
					this.storeService.update(store);
				}
				// 增加系统总销售金额、总佣金
				SysConfig sc = this.configService.getSysConfig();
				sc.setPayoff_all_sale(BigDecimal.valueOf(CommUtil.add(
						order.getGoods_amount(), sc.getPayoff_all_sale())));
				sc.setPayoff_all_commission(BigDecimal.valueOf(CommUtil.add(
						order.getCommission_amount(),
						sc.getPayoff_all_commission())));
				this.configService.update(sc);
				String msg_content = "恭喜您成功购买团购" + map.get("goods_name")
						+ ",团购消费码分别为：" + codes + "您可以到用户中心-我的生活购中查看消费码的使用情况";
				// 发送系统站内信给买家
				Message tobuyer_msg = new Message();
				tobuyer_msg.setAddTime(new Date());
				tobuyer_msg.setStatus(0);
				tobuyer_msg.setType(0);
				tobuyer_msg.setContent(msg_content);
				tobuyer_msg.setFromUser(this.userService.getObjByProperty(
						"userName", "admin"));
				tobuyer_msg.setToUser(buyer);
				this.messageService.save(tobuyer_msg);
				// 发送系统站内信给卖家
				Message toSeller_msg = new Message();
				toSeller_msg.setAddTime(new Date());
				toSeller_msg.setStatus(0);
				toSeller_msg.setType(0);
				toSeller_msg.setContent(buyer.getUsername());
				toSeller_msg.setFromUser(this.userService.getObjByProperty(
						"userName", "admin"));
				toSeller_msg.setToUser(goods.getUser());
				this.messageService.save(toSeller_msg);
				// 付款成功，发送短信团购消费码
				if (this.configService.getSysConfig().isSmsEnbale()) {
					this.send_groupInfo_sms(request, order, buyer.getMobile(),
							"sms_tobuyer_online_ok_send_groupinfo", code_list,
							buyer.getId().toString(), goods.getUser().getId()
									.toString());
				}
			}
			// 付款成功，发送邮件提示
			if (this.configService.getSysConfig().isEmailEnable()) {
				this.send_order_email(request, order, buyer.getEmail(),
						"email_tobuyer_online_pay_ok_notify");
				if (store != null) {
					this.send_order_email(request, order, store.getUser()
							.getEmail(), "email_toseller_online_pay_ok_notify");
				}
				if (store != null) {
					this.send_order_sms(request, order, store.getUser()
							.getMobile(), "sms_toseller_online_pay_ok_notify");
				}
			}
			// 付款成功，发送短信提示
			if (this.configService.getSysConfig().isSmsEnbale()) {
				this.send_order_sms(request, order, buyer.getMobile(),
						"sms_tobuyer_online_pay_ok_notify");
			}
		}
	}
	
	
	
	/**
	 * 微信支付回调方法
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/wechat_notify.htm")
	public void wechat_notify(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		log.debug("微信支付回调处理......");
		OrderForm main_order = null;
		Predeposit obj = null;
		GoldRecord gold = null;
		IntegralGoodsOrder ig_order = null;
		request.setCharacterEncoding("UTF-8");
		Map<String, String> requestMap =XMLUtil.doXMLParse(request);
		String return_code = requestMap.get("return_code");
		if(return_code!=null && "SUCCESS".equals(return_code)){
			String attach = requestMap.get("attach");
			String type = attach.split(",")[0];
			String id = attach.split(",")[1];
			String out_trade_no = requestMap.get("out_trade_no");
			//获取订单数据
			if (type.equals("goods") || type.equals("group")) {
				main_order = this.orderFormService.getObjById(CommUtil
						.null2Long(id));
			}
			if (type.equals("cash")) {
				obj = this.predepositService.getObjById(CommUtil
						.null2Long(id));
			}
			if (type.equals("gold")) {
				gold = this.goldRecordService.getObjById(CommUtil
						.null2Long(id));
			}
			if (type.equals("integral")) {
				ig_order = this.integralGoodsOrderService.getObjById(CommUtil
						.null2Long(id));
			}
			
			if("goods".equals(type) && main_order.getOrder_status()<20){
				//更新主订单状态
				Map pay_params = new HashMap();
				pay_params.put("mark", "wechatpay");
				List<Payment> payments = this.paymentService.query("select obj from Payment obj where obj.mark=:mark",pay_params, -1, -1);
				main_order.setOrder_status(20);
				main_order.setPayType("wechatpay");
				main_order.setPayTime(new Date());
				if(payments!=null && payments.size()>0){
					main_order.setPayment(payments.get(0));
				}
				boolean flag = this.orderFormService.update(main_order);
				// 主订单付款成功，发送邮件提示(向加盟商家发送付款成功短信提示，自营商品无需发送短信提示)
				if (this.configService.getSysConfig().isEmailEnable()) {
					User buyer = this.userService.getObjById(CommUtil.null2Long(main_order.getUser_id()));
					this.send_order_email(request, main_order,buyer.getEmail(),"email_tobuyer_online_pay_ok_notify");
					if (main_order.getOrder_form() == 0) {
						Store store = this.storeService.getObjById(CommUtil.null2Long(main_order.getStore_id()));
						this.send_order_email(request, main_order,store.getUser().getEmail(),"email_toseller_online_pay_ok_notify");
					}
				}
				// 主订单付款成功，发送短信提示
				if (this.configService.getSysConfig().isSmsEnbale()) {
					User buyer = this.userService.getObjById(CommUtil.null2Long(main_order.getUser_id()));
					this.send_order_sms(request, main_order,buyer.getMobile(),"sms_tobuyer_online_pay_ok_notify");
					if (main_order.getOrder_form() == 0) {
						Store store = this.storeService.getObjById(CommUtil.null2Long(main_order.getStore_id()));
						this.send_order_sms(request, main_order, store.getUser().getMobile(),"sms_toseller_online_pay_ok_notify");
					}
				}
				// 主订单付款成功，订单状态更新，同时更新商品库存，如果是团购商品，则更新团购库存
				this.update_goods_inventory(main_order);
				OrderFormLog main_ofl = new OrderFormLog();
				main_ofl.setAddTime(new Date());
				main_ofl.setLog_info("微信在线支付");
				User main_buyer = this.userService.getObjById(CommUtil.null2Long(main_order.getUser_id()));
				main_ofl.setLog_user(main_buyer);
				main_ofl.setOf(main_order);
				this.orderFormLogService.save(main_ofl);
				
				// 子订单操作
				boolean morder_flag = CommUtil.null2String(main_order.getChild_order_detail()).equals("");
				if (main_order.getOrder_main() == 1&& !morder_flag) {// 同步完成子订单付款状态调整
					List<Map> maps = this.orderFormTools.queryGoodsInfo(main_order.getChild_order_detail());
					for (Map child_map : maps) {
						OrderForm child_order = this.orderFormService.getObjById(CommUtil.null2Long(child_map.get("order_id")));
						child_order.setOrder_status(20);
						this.orderFormService.update(child_order);
						// 付款成功，发送邮件提示
						if (this.configService.getSysConfig().isEmailEnable()) {
							// 向加盟商家发送付款成功短信提示，自营商品无需发送短信提示
							User buyer = this.userService.getObjById(CommUtil.null2Long(child_order.getUser_id()));
							this.send_order_email(request, child_order,buyer.getEmail(),"email_tobuyer_online_pay_ok_notify");
							if (child_order.getOrder_form() == 0) {
								Store store = this.storeService.getObjById(CommUtil.null2Long(child_order.getStore_id()));
								this.send_order_email(request,child_order, store.getUser().getEmail(),"email_toseller_online_pay_ok_notify");
							}
						}
						// 付款成功，发送短信提示
						if (this.configService.getSysConfig().isSmsEnbale()) {
							User buyer = this.userService.getObjById(CommUtil.null2Long(child_order.getUser_id()));
							this.send_order_sms(request, child_order,buyer.getMobile(),"sms_tobuyer_online_pay_ok_notify");
							if (child_order.getOrder_form() == 0) {
								Store store = this.storeService.getObjById(CommUtil.null2Long(child_order.getStore_id()));
								this.send_order_sms(request,child_order, store.getUser().getMobile(),"sms_toseller_online_pay_ok_notify");
							}
						}
						// 子订单付款成功，订单状态更新，同时更新商品库存，如果是团购商品，则更新团购库存
						this.update_goods_inventory(child_order);
						OrderFormLog ofl = new OrderFormLog();
						ofl.setAddTime(new Date());
						ofl.setLog_info("微信在线支付");
						User buyer = this.userService.getObjById(CommUtil.null2Long(child_order.getUser_id()));
						ofl.setLog_user(buyer);
						ofl.setOf(child_order);
						this.orderFormLogService.save(ofl);
					}
				}
				
			}
			
			if (type.equals("group")) {
				if (main_order.getOrder_status() != 20) {// 异步没有出来订单，则同步处理订单
					this.generate_groupInfos(request, main_order, "wechatpay","微信线支付", out_trade_no);
				}
			}
			
			if (type.equals("cash")) {
				if (obj.getPd_pay_status() < 2) {
					obj.setPd_status(1);
					obj.setPd_pay_status(2);
					this.predepositService.update(obj);
					User user = this.userService.getObjById(obj
							.getPd_user().getId());
					user.setAvailableBalance(BigDecimal.valueOf(CommUtil
							.add(user.getAvailableBalance(),
									obj.getPd_amount())));
					this.userService.update(user);
					PredepositLog log = new PredepositLog();
					log.setAddTime(new Date());
					log.setPd_log_amount(obj.getPd_amount());
					log.setPd_log_user(obj.getPd_user());
					log.setPd_op_type("充值");
					log.setPd_type("可用预存款");
					log.setPd_log_info("微信在线支付");
					this.predepositLogService.save(log);
				}
				
			}
			if (type.equals("gold")) {
				if (gold.getGold_pay_status() < 2) {
					gold.setGold_status(1);
					gold.setGold_pay_status(2);
					this.goldRecordService.update(gold);
					User user = this.userService.getObjById(gold.getGold_user().getId());
					user.setGold(user.getGold() + gold.getGold_count());
					this.userService.update(user);
					GoldLog log = new GoldLog();
					log.setAddTime(new Date());
					log.setGl_payment(gold.getGold_payment());
					log.setGl_content("微信在线支付");
					log.setGl_money(gold.getGold_money());
					log.setGl_count(gold.getGold_count());
					log.setGl_type(0);
					log.setGl_user(gold.getGold_user());
					log.setGr(gold);
					this.goldLogService.save(log);
				}
			}
			if (type.equals("integral")) {
				if (ig_order.getIgo_status() < 20) {
					ig_order.setIgo_status(20);
					ig_order.setIgo_pay_time(new Date());
					ig_order.setIgo_payment("wechatpay");
					this.integralGoodsOrderService.update(ig_order);
					for (IntegralGoodsCart igc : ig_order.getIgo_gcs()) {
						IntegralGoods goods = igc.getGoods();
						goods.setIg_goods_count(goods.getIg_goods_count()- igc.getCount());
						goods.setIg_exchange_count(goods.getIg_exchange_count() + igc.getCount());
						this.integralGoodsService.update(goods);
					}
				}
			}
			
		}else{
			log.error("微信支付失败！");
		}
	}
	
	

	private void update_goods_inventory(OrderForm order) {
		// 付款成功，订单状态更新，同时更新商品库存，如果是团购商品，则更新团购库存

		List<Goods> goods_list = this.orderFormTools.queryOfGoods(CommUtil
				.null2String(order.getId()));
		for (Goods goods : goods_list) {
			int goods_count = this.orderFormTools.queryOfGoodsCount(
					CommUtil.null2String(order.getId()),
					CommUtil.null2String(goods.getId()));
			if (goods.getGroup() != null && goods.getGroup_buy() == 2) {
				for (GroupGoods gg : goods.getGroup_goods_list()) {
					if (gg.getGroup().getId().equals(goods.getGroup().getId())) {
						gg.setGg_def_count(gg.getGg_def_count() + goods_count);
						gg.setGg_count(gg.getGg_count() - goods_count);
						gg.setGg_selled_count(gg.getGg_selled_count()
								+ goods_count);
						this.groupGoodsService.update(gg);
						// 更新lucene索引
						elasticsearchUtil.indexUpdate(IndexName.GOODS, IndexType.GROUPGOODS, 
								gg.getId().toString(), IndexVoTools.groupGoodsToIndexVo(gg));
//						String goods_lucene_path = ConfigContants.LUCENE_DIRECTORY
//								+ File.separator
//								+ "luence" + File.separator + "groupgoods";
//						File file = new File(goods_lucene_path);
//						if (!file.exists()) {
//							CommUtil.createFolder(goods_lucene_path);
//						}
//						LuceneUtil lucene = LuceneUtil.instance();
//						lucene.setIndex_path(goods_lucene_path);
//						lucene.update(CommUtil.null2String(goods.getId()),
//								luceneVoTools.updateGroupGoodsIndex(gg));
					}
				}
			}
			List<String> gsps = new ArrayList<String>();
			List<GoodsSpecProperty> temp_gsp_list = this.orderFormTools
					.queryOfGoodsGsps(CommUtil.null2String(order.getId()),
							CommUtil.null2String(goods.getId()));
			for (GoodsSpecProperty gsp : temp_gsp_list) {
				gsps.add(gsp.getId().toString());
			}
			String[] gsp_list = new String[gsps.size()];
			gsps.toArray(gsp_list);
			goods.setGoods_salenum(goods.getGoods_salenum() + goods_count);
			String inventory_type = goods.getInventory_type() == null ? "all"
					: goods.getInventory_type();
			if (inventory_type.equals("all")) {
				goods.setGoods_inventory(goods.getGoods_inventory()
						- goods_count);
			} else {
				List<HashMap> list = Json
						.fromJson(ArrayList.class, CommUtil.null2String(goods
								.getGoods_inventory_detail()));
				for (Map temp : list) {
					String[] temp_ids = CommUtil.null2String(temp.get("id"))
							.split("_");
					Arrays.sort(temp_ids);
					Arrays.sort(gsp_list);
					if (Arrays.equals(temp_ids, gsp_list)) {
						temp.put("count", CommUtil.null2Int(temp.get("count"))
								- goods_count);
					}
				}
				goods.setGoods_inventory_detail(Json.toJson(list,
						JsonFormat.compact()));
			}
			if(goods.getGroup()!=null){
				for (GroupGoods gg : goods.getGroup_goods_list()) {
					if (gg.getGroup().getId().equals(goods.getGroup().getId())
							&& gg.getGg_count() == 0) {
						goods.setGroup_buy(3);// 标识商品的状态为团购数量已经结束
					}
				}
			}

			this.goodsService.update(goods);
			// 更新lucene索引
			elasticsearchUtil.indexUpdate(IndexName.GOODS, IndexType.GOODS, 
					CommUtil.null2String(goods.getId()), IndexVoTools.goodsToIndexVo(goods));
//			String goods_lucene_path = ConfigContants.LUCENE_DIRECTORY
//					+ File.separator + "luence" + File.separator + "goods";
//			File file = new File(goods_lucene_path);
//			if (!file.exists()) {
//				CommUtil.createFolder(goods_lucene_path);
//			}
//			LuceneUtil lucene = LuceneUtil.instance();
//			lucene.setIndex_path(goods_lucene_path);
//			lucene.update(CommUtil.null2String(goods.getId()),
//					luceneVoTools.updateGoodsIndex(goods));
		}
	}

	private void send_order_email(HttpServletRequest request, OrderForm order,
			String email, String mark) throws Exception {
		Template template = this.templateService.getObjByProperty("mark", mark);
		if (template != null && template.isOpen()) {
			String subject = template.getTitle();
			String path = TytsmsStringUtils.generatorFilesFolderServerPath(request)+ ConfigContants.GENERATOR_FILES_MIDDLE_NAME+File.separator;
			PrintWriter pwrite = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(path + mark+".vm", false), "UTF-8"));
			pwrite.print(template.getContent());
			pwrite.flush();
			pwrite.close();
			// 生成模板
			VelocityEngine velocityEngine = new VelocityEngine();
			Properties p = new Properties();
			p.setProperty(VelocityEngine.FILE_RESOURCE_LOADER_PATH,
					TytsmsStringUtils.generatorFilesFolderServerPath(request) + ConfigContants.GENERATOR_FILES_MIDDLE_NAME + File.separator);
			p.setProperty(VelocityEngine.INPUT_ENCODING, "UTF-8");
			p.setProperty(VelocityEngine.OUTPUT_ENCODING, "UTF-8");
			Velocity.init(p);
			log.info("读取email.vm文件路径为："+TytsmsStringUtils.generatorFilesFolderServerPath(request) + ConfigContants.GENERATOR_FILES_MIDDLE_NAME + File.separator);
			try{
			org.apache.velocity.Template blank = velocityEngine.getTemplate(mark+".vm");
			VelocityContext context = new VelocityContext();
			User buyer = this.userService.getObjById(CommUtil.null2Long(order
					.getUser_id()));
			context.put("buyer", buyer);
			Store store = this.storeService.getObjById(CommUtil.null2Long(order
					.getStore_id()));
			if(order.getOrder_form()==0){
				context.put("seller", store.getUser());
			}
			context.put("config", this.configService.getSysConfig());
			context.put("send_time", CommUtil.formatLongDate(new Date()));
			context.put("webPath", CommUtil.getURL(request));
			context.put("order", order);
			StringWriter writer = new StringWriter();
			blank.merge(context, writer);
			// System.out.println(writer.toString());
			String content = writer.toString();
			this.msgTools.sendEmail(email, subject, content);
			}catch(Exception e){
				log.info("读取邮件模板："+e.toString());
			}
		}
	}

	private void send_order_sms(HttpServletRequest request, OrderForm order,
			String mobile, String mark){
		try{
			Template template = this.templateService.getObjByProperty("mark", mark);
			if (template != null && template.isOpen()) {
				String path = TytsmsStringUtils.generatorFilesFolderServerPath(request)+ ConfigContants.GENERATOR_FILES_MIDDLE_NAME+File.separator;
				PrintWriter pwrite = new PrintWriter(new OutputStreamWriter(
						new FileOutputStream(path + mark+".vm", false), "UTF-8"));
				pwrite.print(template.getContent());
				pwrite.flush();
				pwrite.close();
				// 生成模板
				VelocityEngine velocityEngine = new VelocityEngine();
				Properties p = new Properties();
				p.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH,
						TytsmsStringUtils.generatorFilesFolderServerPath(request) + ConfigContants.GENERATOR_FILES_MIDDLE_NAME + File.separator);
				p.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
				p.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
				Velocity.init(p);
				org.apache.velocity.Template blank = velocityEngine.getTemplate(mark+".vm",
						"UTF-8");
				VelocityContext context = new VelocityContext();
				User buyer = this.userService.getObjById(CommUtil.null2Long(order
						.getUser_id()));
				context.put("buyer", buyer);
				Store store = this.storeService.getObjById(CommUtil.null2Long(order
						.getStore_id()));
				if(order.getOrder_form()==0){
					context.put("seller", store.getUser());
				}
				context.put("config", this.configService.getSysConfig());
				context.put("send_time", CommUtil.formatLongDate(new Date()));
				context.put("webPath", CommUtil.getURL(request));
				context.put("order", order);
				StringWriter writer = new StringWriter();
				blank.merge(context, writer);
				// System.out.println(writer.toString());
				String content = writer.toString();
				this.msgTools.sendSMS(mobile, content);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private void send_groupInfo_sms(HttpServletRequest request,
			OrderForm order, String mobile, String mark, List<String> codes,
			String buyer_id, String seller_id){
		try{
			Template template = this.templateService.getObjByProperty("mark", mark);
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < codes.size(); i++) {
				sb.append(codes.get(i) + ",");
			}
			String code = sb.toString();
			if (template != null && template.isOpen()) {
				String path = TytsmsStringUtils.generatorFilesFolderServerPath(request)+ ConfigContants.GENERATOR_FILES_MIDDLE_NAME+File.separator;
				PrintWriter pwrite = new PrintWriter(new OutputStreamWriter(
						new FileOutputStream(path + mark+".vm", false), "UTF-8"));
				pwrite.print(template.getContent());
				pwrite.flush();
				pwrite.close();
				// 生成模板
				VelocityEngine velocityEngine = new VelocityEngine();
				Properties p = new Properties();
				p.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH,TytsmsStringUtils.generatorFilesFolderServerPath(request) + ConfigContants.GENERATOR_FILES_MIDDLE_NAME + File.separator);
				p.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
				p.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
				Velocity.init(p);
				org.apache.velocity.Template blank = velocityEngine.getTemplate(mark+".vm",
						"UTF-8");
				VelocityContext context = new VelocityContext();
				context.put("buyer",
						this.userService.getObjById(CommUtil.null2Long(buyer_id)));
				context.put("seller",
						this.userService.getObjById(CommUtil.null2Long(seller_id)));
				context.put("config", this.configService.getSysConfig());
				context.put("send_time", CommUtil.formatLongDate(new Date()));
				context.put("webPath", CommUtil.getURL(request));
				context.put("order", order);
				context.put("code", code);
				StringWriter writer = new StringWriter();
				blank.merge(context, writer);
				// System.out.println(writer.toString());
				String content = writer.toString();
				this.msgTools.sendSMS(mobile, content);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
