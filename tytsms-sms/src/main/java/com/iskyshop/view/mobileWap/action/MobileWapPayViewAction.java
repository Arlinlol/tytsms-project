package com.iskyshop.view.mobileWap.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.jdom.JDOMException;
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
import com.iskyshop.core.tools.CommUtil;
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
import com.iskyshop.pay.wechatpay.config.WeChatConfig;
import com.iskyshop.pay.wechatpay.util.ConfigContants;
import com.iskyshop.pay.wechatpay.util.HttpTool;
import com.iskyshop.pay.wechatpay.util.TytsmsStringUtils;
import com.iskyshop.pay.wechatpay.util.WXJSPay;
import com.iskyshop.pay.wechatpay.util.XMLUtil;
import com.taiyitao.elasticsearch.ElasticsearchUtil;
import com.taiyitao.elasticsearch.IndexVo.IndexName;
import com.taiyitao.elasticsearch.IndexVo.IndexType;
import com.taiyitao.elasticsearch.IndexVoTools;


/**
 * 
 * <p>
 * Title: MobilePayViewAction.java
 * </p>
 * 
 * <p>
 * Description: 手机端订单在线支付回调控制器
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
 * @date 2014-8-18
 * 
 * @version 1.0
 */
@Controller
public class MobileWapPayViewAction  extends MobileWapBaseAction{

	private static final Logger log = LoggerFactory.getLogger(MobileWapPayViewAction.class);
	
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
	 * 手机端网页支付宝同步回调地址，当手机端支付成功后调用该接口修改订单信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	/**
	 * 支付宝在线支付成功回调控制
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/mobileWap/alipay_return.htm")
	@Transactional
	public ModelAndView alipay_return(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mv = new JModelAndView("/mobileWap/view/order_finish.html",
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
		config.setNotify_url(CommUtil.getURL(request) + "/mobileWap/alipay_notify.htm");
		config.setReturn_url(CommUtil.getURL(request) + "/mobileWap/alipay_return.htm");
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
			mv.addObject("url", CommUtil.getURL(request) + "/mobileWap/index.htm");
		}
		return mv;
	}

	/**
	 * 手机端网页支付宝异步回调地址
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/mobileWap/alipay_notify.htm")
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
		config.setNotify_url(CommUtil.getURL(request) + "/mobileWap/alipay_notify.htm");
		config.setReturn_url(CommUtil.getURL(request) + "/mobileWap/alipay_return.htm");
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


	private void generate_groupInfos(HttpServletRequest request,
			OrderForm order, String mark, String pay_info, String trade_no)
			throws Exception {
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
			Map map = this.orderFormTools.queryGroupInfo(order.getGroup_info());
			int count = CommUtil.null2Int(map.get("goods_count").toString());
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
						+ CommUtil.formatTime("yyyyMMddHHmmss" + i, new Date()));
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
//			String goods_lucene_path = ConfigContants.LUCENE_DIRECTORY
//					+ File.separator + "luence" + File.separator
//					+ "grouplifegoods";
//			File file = new File(goods_lucene_path);
//			if (!file.exists()) {
//				CommUtil.createFolder(goods_lucene_path);
//			}
//			LuceneUtil lucene = LuceneUtil.instance();
//			lucene.setIndex_path(goods_lucene_path);
//			lucene.update(CommUtil.null2String(goods.getId()),
//					luceneVoTools.updateLifeGoodsIndex(goods));
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
						order.getGoods_amount(), store.getStore_sale_amount())));// 店铺本次结算总销售金额
				store.setStore_commission_amount(BigDecimal.valueOf(CommUtil
						.add(order.getCommission_amount(),
								store.getStore_commission_amount())));// 店铺本次结算总佣金
				store.setStore_payoff_amount(BigDecimal.valueOf(CommUtil.add(
						plog.getTotal_amount(), store.getStore_payoff_amount())));// 店铺本次结算总佣金
				this.storeService.update(store);
			}
			// 增加系统总销售金额、总佣金
			SysConfig sc = this.configService.getSysConfig();
			sc.setPayoff_all_sale(BigDecimal.valueOf(CommUtil.add(
					order.getGoods_amount(), sc.getPayoff_all_sale())));
			sc.setPayoff_all_commission(BigDecimal.valueOf(CommUtil.add(
					order.getCommission_amount(), sc.getPayoff_all_commission())));
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
				this.send_order_sms(request, order,
						store.getUser().getMobile(),
						"sms_toseller_online_pay_ok_notify");
			}
		}
		// 付款成功，发送短信提示
		if (this.configService.getSysConfig().isSmsEnbale()) {
			this.send_order_sms(request, order, buyer.getMobile(),
					"sms_tobuyer_online_pay_ok_notify");
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
						this.groupGoodsService.update(gg);
						// 更新lucene索引
						elasticsearchUtil.indexUpdate(IndexName.GOODS, IndexType.GROUPLIFEGOODS,
								goods.getId().toString(), IndexVoTools.groupGoodsToIndexVo(gg));
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
			for (GroupGoods gg : goods.getGroup_goods_list()) {
				if (gg.getGroup().getId().equals(goods.getGroup().getId())
						&& gg.getGg_count() == 0) {
					goods.setGroup_buy(3);// 标识商品的状态为团购数量已经结束
				}
			}
			this.goodsService.update(goods);
			// 更新lucene索引
			elasticsearchUtil.indexUpdate(IndexName.GOODS, IndexType.GROUPLIFEGOODS,
					goods.getId().toString(), IndexVoTools.goodsToIndexVo(goods));
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
	
	
	/**
	 * 微信支付		
	 * @param request
	 * @param response
	 * @param url 请求url
	 * @param payment_id 支付配置id
	 * @param type 支付类型
	 * @param id 订单编号
	 * @param code 微信授权code
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/mobileWap/wechatpay.htm")
	public ModelAndView wechatpay(HttpServletRequest request,
			HttpServletResponse response,String url, String payment_id, String type,
			String id,String code) throws Exception{
		OrderForm of = null;
		Predeposit pd = null;
		GoldRecord gold = null;
		IntegralGoodsOrder ig_order = null;
		ModelAndView mv = new JModelAndView("mobileWap/view/wechatpay.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		//订单获取
		if (type.equals("goods")) {
			of = this.orderFormService.getObjById(CommUtil.null2Long(id));
		}
		if (type.equals("cash")) {
			pd = this.predepositService.getObjById(CommUtil.null2Long(id));
		}
		if (type.equals("gold")) {
			gold = this.goldRecordService.getObjById(CommUtil.null2Long(id));
		}
		if (type.equals("integral")) {
			ig_order = this.integralGoodsOrderService.getObjById(CommUtil
					.null2Long(id));
		}
		if (type.equals("group")) {
			of = this.orderFormService.getObjById(CommUtil.null2Long(id));
		}
		//支付信息获取
		Payment payment = this.paymentService.getObjById(CommUtil.null2Long(payment_id));
		
		if (payment == null){
			payment = new Payment();
		}
		//商品订单号
		String out_trade_no = "";
		String body = ""; //商品描述信息
		String trade_no = CommUtil.formatTime("yyyyMMddHHmmss", new Date());
		if (type.equals("goods")) {
			of.setTrade_no("order-" + trade_no + "-" + of.getId().toString());
			boolean flag = this.orderFormService.update(of);// 更新订单流水号
			if (flag) {
				out_trade_no = "order-" + trade_no + "-" + of.getId().toString();
			}
			body = "商品支付";
		}
		
		if (type.equals("cash")) {
			pd.setPd_no("pd-" + trade_no + "-" + pd.getId().toString());
			boolean flag = this.predepositService.update(pd);
			if (flag) {
				out_trade_no = "pd-" + trade_no + "-" + pd.getId().toString();
			}
			body = "会员充值支付";
		}
		if (type.equals("gold")) {
			gold.setGold_sn("gold-" + trade_no + "-" + gold.getId().toString());
			boolean flag = this.goldRecordService.update(gold);
			if (flag) {
				out_trade_no = "gold-" + trade_no + "-" + gold.getId().toString();
			}
			body = "金币兑换支付";
		}
		if (type.equals("integral")) {
			ig_order.setIgo_order_sn("igo-" + trade_no + "-"
					+ ig_order.getId().toString());
			boolean flag = this.integralGoodsOrderService.update(ig_order);
			if (flag) {
				out_trade_no = "igo-" + trade_no + "-"+ ig_order.getId().toString();
			}
			body = "积分订单支付";
		}
		if (type.equals("group")) {
			of.setTrade_no("order-" + trade_no + "-" + of.getId().toString());
			boolean flag = this.orderFormService.update(of);// 更新订单流水号
			if (flag) {
				out_trade_no = "order-" + trade_no + "-" + of.getId().toString();
			}
			body = "团购商品支付";
		}
		//订单金额
		String orderAmount = "";
		if (type.equals("goods")) {
			double total_price = orderFormTools.query_order_price(CommUtil
					.null2String(of.getId()));
			orderAmount = String.valueOf((int) Math.floor(CommUtil
					.null2Double(total_price) * 100));
		}
		if (type.equals("cash")) {
			orderAmount = String.valueOf((int) Math.floor(CommUtil
					.null2Double(pd.getPd_amount()) * 100));
		}
		if (type.equals("gold")) {
			orderAmount = String.valueOf((int) Math.floor(CommUtil
					.null2Double(gold.getGold_money()) * 100));
		}
		if (type.equals("integral")) {
			orderAmount = String.valueOf((int) Math.floor(CommUtil
					.null2Double(ig_order.getIgo_trans_fee()) * 100));
		}
		if (type.equals("group")) {
			orderAmount = String.valueOf((int) Math.floor(CommUtil
					.null2Double(of.getTotalPrice()) * 100));
		}
		
		//获取openid
		String returnJSON=HttpTool.getToken(payment.getAppid(), payment.getAppSecret(), "authorization_code", code);
		JSONObject obj = JSONObject.fromObject(returnJSON);
		String openid=obj.get("openid").toString();
		//封装传递参数（必填字段信息）
		WeChatConfig wechatconfig = new WeChatConfig();
		wechatconfig.setAppid(payment.getAppid());
		wechatconfig.setMch_id(payment.getMch_id());
		wechatconfig.setBody(body);
		wechatconfig.setOut_trade_no(out_trade_no);
		wechatconfig.setTotal_fee(orderAmount);
	//	wechatconfig.setTotal_fee("1");
		wechatconfig.setSpbill_create_ip(request.getRemoteAddr());
		wechatconfig.setNotify_url(url+"/mobileWap/wechat_notify.htm");
		wechatconfig.setTrade_type("JSAPI");
		wechatconfig.setOpenid(openid);
		//封装传递参数（附加数据）
		wechatconfig.setAttach(type+","+id);
		
		Map<String, String> wxPayParamMap = null;
		try {
			wxPayParamMap = WXJSPay.jsApiPay(wechatconfig,payment);
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		mv.addObject("appId", wxPayParamMap.get("appId"));
		mv.addObject("timeStamp", wxPayParamMap.get("timeStamp"));
		mv.addObject("nonceStr", wxPayParamMap.get("nonceStr"));
		mv.addObject("package", wxPayParamMap.get("package"));
		mv.addObject("signType", wxPayParamMap.get("signType"));
		mv.addObject("paySign", wxPayParamMap.get("paySign"));
		return mv;
	}
	
	
	/**
	 * 微信支付回调方法
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/mobileWap/wechat_notify.htm")
	public ModelAndView wechat_notify(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		OrderForm main_order = null;
		Predeposit obj = null;
		GoldRecord gold = null;
		IntegralGoodsOrder ig_order = null;
		ModelAndView mv = new JModelAndView("/mobileWap/view/order_finish.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		request.setCharacterEncoding("UTF-8");
		Map<String, String> requestMap =XMLUtil.doXMLParse(request);
		String return_code = requestMap.get("return_code");
		if("SUCCESS".equals(return_code)){
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
			//
			if("goods".equals(type) && main_order.getOrder_status()<20){
				//更新主订单状态
				main_order.setOrder_status(20);
				main_order.setPayType("wechatpay");
				
				Map pay_params = new HashMap();
				pay_params.put("mark", "wechatpay");
				List<Payment> payments = this.paymentService.query(
						"select obj from Payment obj where obj.mark=:mark",
						pay_params, -1, -1);
				
				main_order.setPayTime(new Date());
				main_order.setPayment(payments.get(0));
				this.orderFormService.update(main_order);
				// 主订单付款成功，发送邮件提示(向加盟商家发送付款成功短信提示，自营商品无需发送短信提示)
				if (this.configService.getSysConfig().isEmailEnable()) {
					
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
				main_ofl.setLog_info("微信在线支付");
				User main_buyer = this.userService.getObjById(CommUtil
						.null2Long(main_order.getUser_id()));
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
						child_order.setPayment(payments.get(0));
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
					mv.addObject("obj", main_order);
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
					mv = new JModelAndView("success.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "成功充值：" + obj.getPd_amount());
					mv.addObject("url", CommUtil.getURL(request)
							+ "/buyer/predeposit_list.htm");
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
					mv = new JModelAndView("success.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "成功充值金币:" + gold.getGold_count());
					mv.addObject("url", CommUtil.getURL(request)
							+ "/seller/gold_record_list.htm");
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
					mv = new JModelAndView("integral_order_finish.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("obj", ig_order);
				}
			}
			
		}else{
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "微信支付失败");
			mv.addObject("url", CommUtil.getURL(request) + "/mobileWap/index.htm");
		}
		return mv;
	}



	private void send_order_email(HttpServletRequest request, OrderForm order,
			String email, String mark) throws Exception {
		Template template = this.templateService.getObjByProperty("mark", mark);
		if (template != null && template.isOpen()) {
			String subject = template.getTitle();
			String path =  TytsmsStringUtils.generatorFilesFolderServerPath(request)+ ConfigContants.GENERATOR_FILES_MIDDLE_NAME+File.separator;
			PrintWriter pwrite = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(path + mark+".vm", false), "UTF-8"));
			pwrite.print(template.getContent());
			pwrite.flush();
			pwrite.close();
			// 生成模板
			Properties p = new Properties();
			p.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, TytsmsStringUtils.generatorFilesFolderServerPath(request) + ConfigContants.GENERATOR_FILES_MIDDLE_NAME + File.separator);
			p.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
			p.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
			Velocity.init(p);
			org.apache.velocity.Template blank = Velocity.getTemplate(mark+".vm",
					"UTF-8");
			VelocityContext context = new VelocityContext();
			User buyer = this.userService.getObjById(CommUtil.null2Long(order
					.getUser_id()));
			context.put("buyer", buyer);
			Store store = this.storeService.getObjById(CommUtil.null2Long(order
					.getStore_id()));
			context.put("seller", store.getUser());
			context.put("config", this.configService.getSysConfig());
			context.put("send_time", CommUtil.formatLongDate(new Date()));
			context.put("webPath", CommUtil.getURL(request));
			context.put("order", order);
			StringWriter writer = new StringWriter();
			blank.merge(context, writer);
			// System.out.println(writer.toString());
			String content = writer.toString();
			this.msgTools.sendEmail(email, subject, content);
		}
	}

	private void send_order_sms(HttpServletRequest request, OrderForm order,
			String mobile, String mark){
		try{
			Template template = this.templateService.getObjByProperty("mark", mark);
			if (template != null && template.isOpen()) {
				String path =  TytsmsStringUtils.generatorFilesFolderServerPath(request)+ ConfigContants.GENERATOR_FILES_MIDDLE_NAME+File.separator;
				PrintWriter pwrite = new PrintWriter(new OutputStreamWriter(
						new FileOutputStream(path + mark+".vm", false), "UTF-8"));
				pwrite.print(template.getContent());
				pwrite.flush();
				pwrite.close();
				// 生成模板
				Properties p = new Properties();
				p.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH,TytsmsStringUtils.generatorFilesFolderServerPath(request) + ConfigContants.GENERATOR_FILES_MIDDLE_NAME + File.separator);
				p.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
				p.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
				Velocity.init(p);
				org.apache.velocity.Template blank = Velocity.getTemplate(mark+".vm",
						"UTF-8");
				VelocityContext context = new VelocityContext();
				User buyer = this.userService.getObjById(CommUtil.null2Long(order
						.getUser_id()));
				context.put("buyer", buyer);
				Store store = this.storeService.getObjById(CommUtil.null2Long(order
						.getStore_id()));
				context.put("seller", store.getUser());
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
				String path =  TytsmsStringUtils.generatorFilesFolderServerPath(request)+ ConfigContants.GENERATOR_FILES_MIDDLE_NAME+File.separator;
				PrintWriter pwrite = new PrintWriter(new OutputStreamWriter(
						new FileOutputStream(path + mark+".vm", false), "UTF-8"));
				pwrite.print(template.getContent());
				pwrite.flush();
				pwrite.close();
				// 生成模板
				Properties p = new Properties();
				p.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH,
						 TytsmsStringUtils.generatorFilesFolderServerPath(request) + ConfigContants.GENERATOR_FILES_MIDDLE_NAME + File.separator);
				p.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
				p.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
				Velocity.init(p);
				org.apache.velocity.Template blank = Velocity.getTemplate(mark+".vm",
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

	
	
	/**
	 * 通联支付在线支付成功回调控制
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/mobileWap/allinpay_return.htm")
	@Transactional
	public ModelAndView allinpay_return(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mv = new JModelAndView("/mobileWap/view/order_finish.html",
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
		boolean verifyResult = false;
		try{
		   verifyResult = paymentResult.verify();
		}catch(Exception e){
			e.printStackTrace();
		}
		
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
			mv.addObject("url", CommUtil.getURL(request) + "/mobileWap/index.htm");
		}
		return mv;
	}
	
}
