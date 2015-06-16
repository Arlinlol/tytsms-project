package com.iskyshop.view.mobile.action;

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

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsSpecProperty;
import com.iskyshop.foundation.domain.GroupGoods;
import com.iskyshop.foundation.domain.GroupInfo;
import com.iskyshop.foundation.domain.GroupLifeGoods;
import com.iskyshop.foundation.domain.Message;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.OrderFormLog;
import com.iskyshop.foundation.domain.Payment;
import com.iskyshop.foundation.domain.PayoffLog;
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
import com.iskyshop.pay.wechatpay.util.ConfigContants;
import com.iskyshop.pay.wechatpay.util.TytsmsStringUtils;
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
public class MobilePayViewAction {

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
	@RequestMapping("/mobile/alipay_return.htm")
	public void mobile_alipay_return(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Map json_map = new HashMap();
		int code = 100;// 100回调成功，-100回调失败
		// 获取支付宝POST过来反馈信息
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
			// 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
			// valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
			params.put(name, valueStr);
		}
		// 商户订单号
		String out_trade_no = new String(request.getParameter("out_trade_no")
				.getBytes("ISO-8859-1"), "UTF-8");
		// 商户快捷支付秘钥，
		String private_key = new String(request.getParameter("private")
				.getBytes("ISO-8859-1"), "UTF-8");
		String order_nos[] = out_trade_no.split("-");
		String order_no = order_nos[2];
		OrderForm main_order = this.orderFormService.getObjById(CommUtil
				.null2Long(order_no));
		Payment payment = this.paymentService.getObjById(main_order
				.getPayment().getId());
		if (private_key.equals(payment.getApp_private_key())) {// 验证成功
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
						Store store = this.storeService.getObjById(CommUtil
								.null2Long(main_order.getStore_id()));
						this.send_order_email(request, main_order, store
								.getUser().getEmail(),
								"email_toseller_online_pay_ok_notify");
					}
				}
				// 主订单付款成功，发送短信提示
				if (this.configService.getSysConfig().isSmsEnbale()) {
					User buyer = this.userService.getObjById(CommUtil
							.null2Long(main_order.getUser_id()));
					this.send_order_sms(request, main_order, buyer.getMobile(),
							"sms_tobuyer_online_pay_ok_notify");
					if (main_order.getOrder_form() == 0) {
						Store store = this.storeService.getObjById(CommUtil
								.null2Long(main_order.getStore_id()));
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
				if (main_order.getOrder_form() == 0) {// 如订单为商家订单，增加商家的可用预存款
					Store store = this.storeService.getObjById(CommUtil
							.null2Long(main_order.getStore_id()));
					User seller = this.userService.getObjById(store.getUser()
							.getId());
					// 需要增加预存款的金额=订单总价-配送金额
					BigDecimal addMoney = BigDecimal.valueOf(CommUtil.subtract(
							main_order.getTotalPrice(),
							main_order.getShip_price()));
					seller.setAvailableBalance(BigDecimal.valueOf(CommUtil.add(
							seller.getAvailableBalance(), addMoney)));
					this.userService.update(seller);
					PredepositLog log = new PredepositLog();
					log.setAddTime(new Date());
					log.setPd_log_amount(addMoney);
					log.setPd_log_user(seller);
					log.setPd_op_type("平台支付");
					log.setPd_type("可用预存款");
					log.setPd_log_info("订单：" + main_order.getOrder_id()
							+ "通过平台支付交易完成，预存款增加");
					this.predepositLogService.save(log);
				}
				// 子订单操作
				if (main_order.getOrder_main() == 1
						&& !CommUtil.null2String(
								main_order.getChild_order_detail()).equals("")) {// 同步完成子订单付款状态调整
					List<Map> maps = this.orderFormTools
							.queryGoodsInfo(main_order.getChild_order_detail());
					for (Map child_map : maps) {
						OrderForm child_order = this.orderFormService
								.getObjById(CommUtil.null2Long(child_map
										.get("order_id")));
						child_order.setOrder_status(20);
						this.orderFormService.update(child_order);
						// 付款成功，发送邮件提示
						if (this.configService.getSysConfig().isEmailEnable()) {
							// 向加盟商家发送付款成功短信提示，自营商品无需发送短信提示
							User buyer = this.userService.getObjById(CommUtil
									.null2Long(child_order.getUser_id()));
							this.send_order_email(request, child_order,
									buyer.getEmail(),
									"email_tobuyer_online_pay_ok_notify");
							if (child_order.getOrder_form() == 0) {
								Store store = this.storeService
										.getObjById(CommUtil
												.null2Long(child_order
														.getStore_id()));
								this.send_order_email(request, child_order,
										store.getUser().getEmail(),
										"email_toseller_online_pay_ok_notify");
							}
						}
						// 付款成功，发送短信提示
						if (this.configService.getSysConfig().isSmsEnbale()) {
							User buyer = this.userService.getObjById(CommUtil
									.null2Long(child_order.getUser_id()));
							this.send_order_sms(request, child_order,
									buyer.getMobile(),
									"sms_tobuyer_online_pay_ok_notify");
							if (child_order.getOrder_form() == 0) {
								Store store = this.storeService
										.getObjById(CommUtil
												.null2Long(child_order
														.getStore_id()));
								this.send_order_sms(request, child_order, store
										.getUser().getMobile(),
										"sms_toseller_online_pay_ok_notify");
							}
						}
						// 子订单付款成功，订单状态更新，同时更新商品库存，如果是团购商品，则更新团购库存
						this.update_goods_inventory(child_order);
						OrderFormLog ofl = new OrderFormLog();
						ofl.setAddTime(new Date());
						ofl.setLog_info("支付宝在线支付");
						User buyer = this.userService.getObjById(CommUtil
								.null2Long(child_order.getUser_id()));
						ofl.setLog_user(buyer);
						ofl.setOf(child_order);
						this.orderFormLogService.save(ofl);
						if (child_order.getOrder_form() == 0) {// 如订单为商家订单，增加商家的可用预存款
							Store store = this.storeService.getObjById(CommUtil
									.null2Long(child_order.getStore_id()));
							User seller = this.userService.getObjById(store
									.getUser().getId());
							// 需要增加预存款的金额=订单总价-配送金额
							BigDecimal addMoney = BigDecimal.valueOf(CommUtil
									.subtract(child_order.getTotalPrice(),
											child_order.getShip_price()));
							seller.setAvailableBalance(BigDecimal
									.valueOf(CommUtil.add(
											seller.getAvailableBalance(),
											addMoney)));
							this.userService.update(seller);
							PredepositLog log = new PredepositLog();
							log.setAddTime(new Date());
							log.setPd_log_amount(addMoney);
							log.setPd_log_user(seller);
							log.setPd_op_type("平台支付");
							log.setPd_type("可用预存款");
							log.setPd_log_info("订单："
									+ child_order.getOrder_id()
									+ "通过平台支付交易完成，预存款增加");
							this.predepositLogService.save(log);
						}
					}
				}
				code = 100;
			} else {// 已经支付
				code = -100;// 订单已经支付
			}
		} else {
			code = -100;// 订单已经支付
		}
		json_map.put("code", code);
		String json = Json.toJson(json_map, JsonFormat.compact());
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(json);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 手机端网页支付宝异步回调地址
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/mobile/alipay_notify.htm")
	public void mobile_alipay_notify(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// 获取支付宝POST过来反馈信息
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
			// 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
			// valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
			params.put(name, valueStr);
		}
		// 商户订单号
		String out_trade_no = new String(request.getParameter("out_trade_no")
				.getBytes("ISO-8859-1"), "UTF-8");
		String trade_no = new String(request.getParameter("trade_no").getBytes(
				"ISO-8859-1"), "UTF-8");
		// 交易状态
		String trade_status = new String(request.getParameter("trade_status")
				.getBytes("ISO-8859-1"), "UTF-8");
		String order_nos[] = out_trade_no.split("-");
		String order_no = order_nos[2];
		OrderForm main_order = this.orderFormService.getObjById(CommUtil
				.null2Long(order_no));
		Payment payment = this.paymentService.getObjById(main_order
				.getPayment().getId());
		if (trade_status.equals("TRADE_FINISHED")
				|| trade_status.equals("TRADE_SUCCESS")) {// 验证成功
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
						Store store = this.storeService.getObjById(CommUtil
								.null2Long(main_order.getStore_id()));
						this.send_order_email(request, main_order, store
								.getUser().getEmail(),
								"email_toseller_online_pay_ok_notify");
					}
				}
				// 主订单付款成功，发送短信提示
				if (this.configService.getSysConfig().isSmsEnbale()) {
					User buyer = this.userService.getObjById(CommUtil
							.null2Long(main_order.getUser_id()));
					this.send_order_sms(request, main_order, buyer.getMobile(),
							"sms_tobuyer_online_pay_ok_notify");
					if (main_order.getOrder_form() == 0) {
						Store store = this.storeService.getObjById(CommUtil
								.null2Long(main_order.getStore_id()));
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
				if (main_order.getOrder_form() == 0) {// 如订单为商家订单，增加商家的可用预存款
					Store store = this.storeService.getObjById(CommUtil
							.null2Long(main_order.getStore_id()));
					User seller = this.userService.getObjById(store.getUser()
							.getId());
					// 需要增加预存款的金额=订单总价-配送金额
					BigDecimal addMoney = BigDecimal.valueOf(CommUtil.subtract(
							main_order.getTotalPrice(),
							main_order.getShip_price()));
					seller.setAvailableBalance(BigDecimal.valueOf(CommUtil.add(
							seller.getAvailableBalance(), addMoney)));
					this.userService.update(seller);
					PredepositLog log = new PredepositLog();
					log.setAddTime(new Date());
					log.setPd_log_amount(addMoney);
					log.setPd_log_user(seller);
					log.setPd_op_type("平台支付");
					log.setPd_type("可用预存款");
					log.setPd_log_info("订单：" + main_order.getOrder_id()
							+ "通过平台支付交易完成，预存款增加");
					this.predepositLogService.save(log);
				}
				// 子订单操作
				if (main_order.getOrder_main() == 1
						&& !CommUtil.null2String(
								main_order.getChild_order_detail()).equals("")) {// 同步完成子订单付款状态调整
					List<Map> maps = this.orderFormTools
							.queryGoodsInfo(main_order.getChild_order_detail());
					for (Map child_map : maps) {
						OrderForm child_order = this.orderFormService
								.getObjById(CommUtil.null2Long(child_map
										.get("order_id")));
						child_order.setOrder_status(20);
						this.orderFormService.update(child_order);
						// 付款成功，发送邮件提示
						if (this.configService.getSysConfig().isEmailEnable()) {
							// 向加盟商家发送付款成功短信提示，自营商品无需发送短信提示
							User buyer = this.userService.getObjById(CommUtil
									.null2Long(child_order.getUser_id()));
							this.send_order_email(request, child_order,
									buyer.getEmail(),
									"email_tobuyer_online_pay_ok_notify");
							if (child_order.getOrder_form() == 0) {
								Store store = this.storeService
										.getObjById(CommUtil
												.null2Long(child_order
														.getStore_id()));
								this.send_order_email(request, child_order,
										store.getUser().getEmail(),
										"email_toseller_online_pay_ok_notify");
							}
						}
						// 付款成功，发送短信提示
						if (this.configService.getSysConfig().isSmsEnbale()) {
							User buyer = this.userService.getObjById(CommUtil
									.null2Long(child_order.getUser_id()));
							this.send_order_sms(request, child_order,
									buyer.getMobile(),
									"sms_tobuyer_online_pay_ok_notify");
							if (child_order.getOrder_form() == 0) {
								Store store = this.storeService
										.getObjById(CommUtil
												.null2Long(child_order
														.getStore_id()));
								this.send_order_sms(request, child_order, store
										.getUser().getMobile(),
										"sms_toseller_online_pay_ok_notify");
							}
						}
						// 子订单付款成功，订单状态更新，同时更新商品库存，如果是团购商品，则更新团购库存
						this.update_goods_inventory(child_order);
						OrderFormLog ofl = new OrderFormLog();
						ofl.setAddTime(new Date());
						ofl.setLog_info("支付宝在线支付");
						User buyer = this.userService.getObjById(CommUtil
								.null2Long(child_order.getUser_id()));
						ofl.setLog_user(buyer);
						ofl.setOf(child_order);
						this.orderFormLogService.save(ofl);
						if (child_order.getOrder_form() == 0) {// 如订单为商家订单，增加商家的可用预存款
							Store store = this.storeService.getObjById(CommUtil
									.null2Long(child_order.getStore_id()));
							User seller = this.userService.getObjById(store
									.getUser().getId());
							// 需要增加预存款的金额=订单总价-配送金额
							BigDecimal addMoney = BigDecimal.valueOf(CommUtil
									.subtract(child_order.getTotalPrice(),
											child_order.getShip_price()));
							seller.setAvailableBalance(BigDecimal
									.valueOf(CommUtil.add(
											seller.getAvailableBalance(),
											addMoney)));
							this.userService.update(seller);
							PredepositLog log = new PredepositLog();
							log.setAddTime(new Date());
							log.setPd_log_amount(addMoney);
							log.setPd_log_user(seller);
							log.setPd_op_type("平台支付");
							log.setPd_type("可用预存款");
							log.setPd_log_info("订单："
									+ child_order.getOrder_id()
									+ "通过平台支付交易完成，预存款增加");
							this.predepositLogService.save(log);
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
			} else {// 已经支付
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
		} else {// 验证失败
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
			for (GroupGoods gg : goods.getGroup_goods_list()) {
				if (gg.getGroup().getId().equals(goods.getGroup().getId())
						&& gg.getGg_count() == 0) {
					goods.setGroup_buy(3);// 标识商品的状态为团购数量已经结束
				}
			}
			this.goodsService.update(goods);
			// 更新lucene索引
			elasticsearchUtil.indexUpdate(IndexName.GOODS, IndexType.GOODS, 
					CommUtil.null2String(goods.getId()),IndexVoTools.goodsToIndexVo(goods));
			
//			String goods_lucene_path = ConfigContants.LUCENE_DIRECTORYo
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
			Properties p = new Properties();
			p.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH,
					TytsmsStringUtils.generatorFilesFolderServerPath(request)+ ConfigContants.GENERATOR_FILES_MIDDLE_NAME + File.separator);
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
				String path = TytsmsStringUtils.generatorFilesFolderServerPath(request)+ConfigContants.GENERATOR_FILES_MIDDLE_NAME+File.separator;
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
			String buyer_id, String seller_id) {
		try{
			Template template = this.templateService.getObjByProperty("mark", mark);
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < codes.size(); i++) {
				sb.append(codes.get(i) + ",");
			}
			String code = sb.toString();
			if (template != null && template.isOpen()) {
				String path = TytsmsStringUtils.generatorFilesFolderServerPath(request)+ConfigContants.GENERATOR_FILES_MIDDLE_NAME+File.separator;
				PrintWriter pwrite = new PrintWriter(new OutputStreamWriter(
						new FileOutputStream(path + mark+".vm", false), "UTF-8"));
				pwrite.print(template.getContent());
				pwrite.flush();
				pwrite.close();
				// 生成模板
				Properties p = new Properties();
				p.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH,TytsmsStringUtils.generatorFilesFolderServerPath(request)+ ConfigContants.GENERATOR_FILES_MIDDLE_NAME + File.separator);
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
		

}
