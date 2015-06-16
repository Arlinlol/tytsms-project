package com.iskyshop.pay.tools;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.jdom.JDOMException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.allinpay.ets.client.RequestOrder;
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.Md5Encrypt;
import com.iskyshop.foundation.domain.GoldRecord;
import com.iskyshop.foundation.domain.IntegralGoodsOrder;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.Payment;
import com.iskyshop.foundation.domain.Predeposit;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.service.IGoldRecordService;
import com.iskyshop.foundation.service.IIntegralGoodsOrderService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IPaymentService;
import com.iskyshop.foundation.service.IPredepositService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.manage.admin.tools.OrderFormTools;
import com.iskyshop.pay.alipay.config.AlipayConfig;
import com.iskyshop.pay.alipay.services.AlipayService;
import com.iskyshop.pay.allinpay.AllinpayTools;
import com.iskyshop.pay.bill.config.BillConfig;
import com.iskyshop.pay.bill.services.BillService;
import com.iskyshop.pay.bill.util.BillCore;
import com.iskyshop.pay.bill.util.MD5Util;
import com.iskyshop.pay.chinabank.util.ChinaBankSubmit;
import com.iskyshop.pay.paypal.PaypalTools;
import com.iskyshop.pay.wechatpay.config.WeChatConfig;
import com.iskyshop.pay.wechatpay.util.HttpTool;
import com.iskyshop.pay.wechatpay.util.WXJSPay;
import com.iskyshop.pay.wechatpay.util.WechatpayTools;

/**
 * 
 * <p>
 * Title: PayTools.java
 * </p>
 * 
 * <p>
 * Description:在线支付工具类，用来生成主流常见支付平台的在线支付信息，并提交到支付平台;V1.3版本增加Paypal在线支付，
 * 及支付宝手机网页在线支付
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
 * @date 2014-4-28
 * 
 * @version iskyshop_b2b2c 1.0
 */
@Component
public class PayTools {
	@Autowired
	private IPaymentService paymentService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private OrderFormTools orderFormtools;
	@Autowired
	private IPredepositService predepositService;
	@Autowired
	private IGoldRecordService goldRecordService;
	@Autowired
	private IIntegralGoodsOrderService integralGoodsOrderService;
	@Autowired
	private ISysConfigService configService;

	/**
	 * 根据支付类型生成支付宝在线表单 0为及时支付、1为担保支付、2为标准双接口,支持分润处理,由于支付宝限制，分润操作只支持即时到帐
	 * 
	 * @param url
	 *            系统url
	 * @param payment_id
	 *            支付方式id
	 * @param type
	 *            支付类型，分为goods支付商品，cash在线充值
	 * @param order_id
	 *            订单编号，根据type区分类型
	 * @return
	 */
	public String genericAlipay(String url, String payment_id, String type,
			String id) {
		String result = "";
		OrderForm of = null;
		Predeposit pd = null;
		GoldRecord gold = null;
		IntegralGoodsOrder ig_order = null;
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
		Payment payment = this.paymentService.getObjById(CommUtil
				.null2Long(payment_id));
		if (payment == null)
			payment = new Payment();
		int interfaceType = payment.getInterfaceType();
		AlipayConfig config = new AlipayConfig();
		Map params = new HashMap();
		params.put("mark", "alipay");
		List<Payment> payments = this.paymentService.query(
				"select obj from Payment obj where obj.mark=:mark", params, -1,
				-1);
		Payment shop_payment = new Payment();
		if (payments.size() > 0) {
			shop_payment = payments.get(0);
		}
		if (!CommUtil.null2String(payment.getSafeKey()).equals("")
				&& !CommUtil.null2String(payment.getPartner()).equals("")) {
			config.setKey(payment.getSafeKey());
			config.setPartner(payment.getPartner());
		} else {
			config.setKey(shop_payment.getSafeKey());
			config.setPartner(shop_payment.getPartner());
		}
		config.setSeller_email(payment.getSeller_email());
		config.setNotify_url(url + "/alipay_notify.htm");
		config.setReturn_url(url + "/alipay_return.htm");
		SysConfig sys_config = this.configService.getSysConfig();
		if (interfaceType == 0) {// 及时到账支付
			String out_trade_no = "";
			String trade_no = CommUtil.formatTime("yyyyMMddHHmmss", new Date());
			if (type.equals("goods") || type.equals("group")) {
				of.setTrade_no("order-" + trade_no + "-"
						+ of.getId().toString());
				boolean flag = this.orderFormService.update(of);// 更新订单流水号
				if (flag) {
					out_trade_no = "order-" + trade_no + "-"
							+ of.getId().toString();
				}
			}

			if (type.equals("cash")) {
				pd.setPd_no("pd-" + trade_no + "-" + pd.getId().toString());
				boolean flag = this.predepositService.update(pd);
				if (flag) {
					out_trade_no = "pd-" + trade_no + "-"
							+ pd.getId().toString();
				}
			}
			if (type.equals("gold")) {
				gold.setGold_sn("gold-" + trade_no + "-"
						+ gold.getId().toString());
				boolean flag = this.goldRecordService.update(gold);
				if (flag) {
					out_trade_no = "gold-" + trade_no + "-"
							+ gold.getId().toString();
				}
			}
			if (type.equals("integral")) {
				ig_order.setIgo_order_sn("igo-" + trade_no + "-"
						+ ig_order.getId().toString());
				boolean flag = this.integralGoodsOrderService.update(ig_order);
				if (flag) {
					out_trade_no = "igo-" + trade_no + "-"
							+ ig_order.getId().toString();
				}
			}
			// 订单名称，显示在支付宝收银台里的“商品名称”里，显示在支付宝的交易管理的“商品名称”的列表里。
			String subject = "";//
			if (type.equals("goods")) {
				subject = "泰易淘商品";
			}
			if (type.equals("cash")) {
				subject = pd.getPd_sn();
			}
			if (type.equals("gold")) {
				subject = gold.getGold_sn();
			}
			if (type.equals("integral")) {
				subject = ig_order.getIgo_order_sn();
			}
			if (type.equals("store_deposit")) {
				subject = "store_deposit";
			}
			if (type.equals("group")) {
				subject = of.getOrder_id();
			}
			// 订单描述、订单详细、订单备注，显示在支付宝收银台里的“商品描述”里
			String body = type;
			// 订单总金额，显示在支付宝收银台里的“应付总额”里
			String total_fee = "";//
			if (type.equals("goods")) {
				double total_price = this.orderFormtools
						.query_order_price(CommUtil.null2String(of.getId()));
				total_fee = CommUtil.null2Amount(total_price);
			}
			if (type.equals("cash")) {
				total_fee = CommUtil.null2Amount(pd.getPd_amount());
			}
			if (type.equals("gold")) {
				total_fee = CommUtil.null2Amount(gold.getGold_money());
			}
			if (type.equals("integral")) {
				total_fee = CommUtil.null2Amount(ig_order.getIgo_trans_fee());
			}
			if (type.equals("group")) {
				total_fee = CommUtil.null2Amount(of.getTotalPrice());
			}
			// 扩展功能参数——默认支付方式//
			// 默认支付方式，取值见“即时到帐接口”技术文档中的请求参数列表
			String paymethod = "";
			// 默认网银代号，代号列表见“即时到帐接口”技术文档“附录”→“银行列表”
			String defaultbank = "";
			// 扩展功能参数——防钓鱼//
			// 防钓鱼时间戳
			String anti_phishing_key = "";
			// 获取客户端的IP地址，建议：编写获取客户端IP地址的程序
			String exter_invoke_ip = "";
			// 注意：
			// 1.请慎重选择是否开启防钓鱼功能
			// 2.exter_invoke_ip、anti_phishing_key一旦被设置过，那么它们就会成为必填参数
			// 3.开启防钓鱼功能后，服务器、本机电脑必须支持远程XML解析，请配置好该环境。
			// 4.建议使用POST方式请求数据
			// 示例：
			// anti_phishing_key = AlipayService.query_timestamp(); //获取防钓鱼时间戳函数
			// exter_invoke_ip = "202.1.1.1";

			// 扩展功能参数——其他///

			// 自定义参数，可存放任何内容（除=、&等特殊字符外），不会显示在页面上
			String extra_common_param = "泰易淘商城购买商品";
			// 默认买家支付宝账号
			String buyer_email = "";
			// 商品展示地址，要用http:// 格式的完整路径，不允许加?id=123这类自定义参数
			String show_url = "";
			// 扩展功能参数——分润(若要使用，请按照注释要求的格式赋值)//s
			// 提成类型，该值为固定值：10，不需要修改
			String royalty_type = "10";
			// 减去支付宝手续费
			// 提成信息集
			String royalty_parameters = "";
			// 注意：
			// 与需要结合商户网站自身情况动态获取每笔交易的各分润收款账号、各分润金额、各分润说明。最多只能设置10条
			// 各分润金额的总和须小于等于total_fee
			// 提成信息集格式为：收款方Email_1^金额1^备注1|收款方Email_2^金额2^备注2
			// 把请求参数打包成数组
			Map<String, String> sParaTemp = new HashMap<String, String>();
			sParaTemp.put("payment_type", "1");
			sParaTemp.put("out_trade_no", out_trade_no);
			sParaTemp.put("subject", subject);
			sParaTemp.put("body", body);
			sParaTemp.put("total_fee", total_fee);
			sParaTemp.put("show_url", show_url);
			sParaTemp.put("paymethod", paymethod);
			sParaTemp.put("defaultbank", defaultbank);
			sParaTemp.put("anti_phishing_key", anti_phishing_key);
			sParaTemp.put("exter_invoke_ip", exter_invoke_ip);
			sParaTemp.put("extra_common_param", extra_common_param);
			sParaTemp.put("buyer_email", buyer_email);
			// 构造函数，生成请求URL
			result = AlipayService.create_direct_pay_by_user(config, sParaTemp);
		}
		if (interfaceType == 1) {// 担保支付接口
			// 请与贵网站订单系统中的唯一订单号匹配
			String out_trade_no = "";
			String trade_no = CommUtil.formatTime("yyyyMMddHHmmss", new Date());
			if (type.equals("goods")) {
				of.setTrade_no("order-" + trade_no + "-"
						+ of.getId().toString());
				boolean flag = this.orderFormService.update(of);// 更新订单流水号
				if (flag) {
					out_trade_no = "order-" + trade_no + "-"
							+ of.getId().toString();
				}
			}

			if (type.equals("cash")) {
				pd.setPd_no("pd-" + trade_no + "-" + pd.getId().toString());
				boolean flag = this.predepositService.update(pd);
				if (flag) {
					out_trade_no = "pd-" + trade_no + "-"
							+ pd.getId().toString();
				}
			}
			if (type.equals("gold")) {
				gold.setGold_sn("gold-" + trade_no + "-"
						+ gold.getId().toString());
				boolean flag = this.goldRecordService.update(gold);
				if (flag) {
					out_trade_no = "gold-" + trade_no + "-"
							+ gold.getId().toString();
				}
			}
			if (type.equals("integral")) {
				ig_order.setIgo_order_sn("igo-" + trade_no + "-"
						+ ig_order.getId().toString());
				boolean flag = this.integralGoodsOrderService.update(ig_order);
				if (flag) {
					out_trade_no = "igo-" + trade_no + "-"
							+ ig_order.getId().toString();
				}
			}

			if (type.equals("group")) {
				of.setTrade_no("order-" + trade_no + "-"
						+ of.getId().toString());
				boolean flag = this.orderFormService.update(of);// 更新订单流水号
				if (flag) {
					out_trade_no = "order-" + trade_no + "-"
							+ of.getId().toString();
				}
			}
			// 订单名称，显示在支付宝收银台里的“商品名称”里，显示在支付宝的交易管理的“商品名称”的列表里。
			String subject = "";//
			if (type.equals("goods")) {
				subject = of.getOrder_id();
			}
			if (type.equals("cash")) {
				subject = pd.getPd_sn();
			}
			if (type.equals("gold")) {
				subject = gold.getGold_sn();
			}
			if (type.equals("integral")) {
				subject = ig_order.getIgo_order_sn();
			}
			if (type.equals("store_deposit")) {
				subject = "store_deposit";
			}
			if (type.equals("group")) {
				subject = of.getOrder_id();
			}
			// 订单描述、订单详细、订单备注，显示在支付宝收银台里的“商品描述”里
			String body = type;
			// 订单总金额，显示在支付宝收银台里的“应付总额”里
			String total_fee = "";//
			if (type.equals("goods")) {
				total_fee = CommUtil.null2Amount(of.getTotalPrice());
			}
			if (type.equals("cash")) {
				total_fee = CommUtil.null2Amount(pd.getPd_amount());
			}
			if (type.equals("gold")) {
				total_fee = CommUtil.null2Amount(gold.getGold_money());
			}
			if (type.equals("integral")) {
				total_fee = CommUtil.null2Amount(ig_order.getIgo_trans_fee());
			}
			if (type.equals("group")) {
				total_fee = CommUtil.null2Amount(of.getTotalPrice());
			}
			// 订单总金额，显示在支付宝收银台里的“应付总额”里
			String price = String.valueOf(total_fee);
			// 物流费用，即运费。
			String logistics_fee = "0.00";
			// 物流类型，三个值可选：EXPRESS（快递）、POST（平邮）、EMS（EMS）
			String logistics_type = "EXPRESS";
			// 物流支付方式，两个值可选：SELLER_PAY（卖家承担运费）、BUYER_PAY（买家承担运费）
			String logistics_payment = "SELLER_PAY";
			// 商品数量，建议默认为1，不改变值，把一次交易看成是一次下订单而非购买一件商品。
			String quantity = "1";
			// 扩展参数//
			// 自定义参数，可存放任何内容（除=、&等特殊字符外），不会显示在页面上
			String extra_common_param = "";
			// 买家收货信息（推荐作为必填）
			// 该功能作用在于买家已经在商户网站的下单流程中填过一次收货信息，而不需要买家在支付宝的付款流程中再次填写收货信息。
			// 若要使用该功能，请至少保证receive_name、receive_address有值
			String receive_name = "";
			String receive_address = "";
			String receive_zip = "";
			String receive_phone = ""; // 收货人电话号码
			String receive_mobile = "";
			// 网站商品的展示地址，不允许加?id=123这类自定义参数
			String show_url = "";
			// 把请求参数打包成数组
			Map<String, String> sParaTemp = new HashMap<String, String>();
			sParaTemp.put("payment_type", "1");
			sParaTemp.put("show_url", show_url);
			sParaTemp.put("out_trade_no", out_trade_no);
			sParaTemp.put("subject", subject);
			sParaTemp.put("body", body);
			sParaTemp.put("price", price);
			sParaTemp.put("logistics_fee", logistics_fee);
			sParaTemp.put("logistics_type", logistics_type);
			sParaTemp.put("logistics_payment", logistics_payment);
			sParaTemp.put("quantity", quantity);
			sParaTemp.put("extra_common_param", extra_common_param);
			sParaTemp.put("receive_name", receive_name);
			sParaTemp.put("receive_address", receive_address);
			sParaTemp.put("receive_zip", receive_zip);
			sParaTemp.put("receive_phone", receive_phone);
			sParaTemp.put("receive_mobile", receive_mobile);

			// 构造函数，生成请求URL
			result = AlipayService.create_partner_trade_by_buyer(config,
					sParaTemp);
		}
		if (interfaceType == 2) {// 标准双接口
			// 请与贵网站订单系统中的唯一订单号匹配
			String out_trade_no = "";
			String trade_no = CommUtil.formatTime("yyyyMMddHHmmss", new Date());
			if (type.equals("goods")) {
				of.setTrade_no("order-" + trade_no + "-"
						+ of.getId().toString());
				boolean flag = this.orderFormService.update(of);// 更新订单流水号
				if (flag) {
					out_trade_no = "order-" + trade_no + "-"
							+ of.getId().toString();
				}
			}

			if (type.equals("cash")) {
				pd.setPd_no("pd-" + trade_no + "-" + pd.getId().toString());
				boolean flag = this.predepositService.update(pd);
				if (flag) {
					out_trade_no = "pd-" + trade_no + "-"
							+ pd.getId().toString();
				}
			}
			if (type.equals("gold")) {
				gold.setGold_sn("gold-" + trade_no + "-"
						+ gold.getId().toString());
				boolean flag = this.goldRecordService.update(gold);
				if (flag) {
					out_trade_no = "gold-" + trade_no + "-"
							+ gold.getId().toString();
				}
			}
			if (type.equals("integral")) {
				ig_order.setIgo_order_sn("igo-" + trade_no + "-"
						+ ig_order.getId().toString());
				boolean flag = this.integralGoodsOrderService.update(ig_order);
				if (flag) {
					out_trade_no = "igo-" + trade_no + "-"
							+ ig_order.getId().toString();
				}
			}
			if (type.equals("group")) {
				of.setTrade_no("order-" + trade_no + "-"
						+ of.getId().toString());
				boolean flag = this.orderFormService.update(of);// 更新订单流水号
				if (flag) {
					out_trade_no = "order-" + trade_no + "-"
							+ of.getId().toString();
				}
			}
			// 订单名称，显示在支付宝收银台里的“商品名称”里，显示在支付宝的交易管理的“商品名称”的列表里。
			String subject = "";//
			if (type.equals("goods")) {
				subject = of.getOrder_id();
			}
			if (type.equals("cash")) {
				subject = pd.getPd_sn();
			}
			if (type.equals("gold")) {
				subject = gold.getGold_sn();
			}
			if (type.equals("integral")) {
				subject = ig_order.getIgo_order_sn();
			}
			if (type.equals("store_deposit")) {
				subject = "store_deposit";
			}
			if (type.equals("group")) {
				subject = of.getOrder_id();
			}
			// 订单描述、订单详细、订单备注，显示在支付宝收银台里的“商品描述”里
			String body = type;
			// 订单总金额，显示在支付宝收银台里的“应付总额”里
			String total_fee = "";//
			if (type.equals("goods")) {
				total_fee = CommUtil.null2Amount(of.getTotalPrice());
			}
			if (type.equals("cash")) {
				total_fee = CommUtil.null2Amount(pd.getPd_amount());
			}
			if (type.equals("gold")) {
				total_fee = CommUtil.null2Amount(gold.getGold_money());
			}
			if (type.equals("integral")) {
				total_fee = CommUtil.null2Amount(ig_order.getIgo_trans_fee());
			}
			if (type.equals("group")) {
				total_fee = CommUtil.null2Amount(of.getTotalPrice());
			}
			// 订单总金额，显示在支付宝收银台里的“应付总额”里
			String price = String.valueOf(total_fee);

			// 物流费用，即运费。
			String logistics_fee = "0.00";
			// 物流类型，三个值可选：EXPRESS（快递）、POST（平邮）、EMS（EMS）
			String logistics_type = "EXPRESS";
			// 物流支付方式，两个值可选：SELLER_PAY（卖家承担运费）、BUYER_PAY（买家承担运费）
			String logistics_payment = "SELLER_PAY";

			// 商品数量，建议默认为1，不改变值，把一次交易看成是一次下订单而非购买一件商品。
			String quantity = "1";
			// 买家收货信息（推荐作为必填）
			String extra_common_param = "";
			// 该功能作用在于买家已经在商户网站的下单流程中填过一次收货信息，而不需要买家在支付宝的付款流程中再次填写收货信息。
			// 若要使用该功能，请至少保证receive_name、receive_address有值
			String receive_name = "";
			String receive_address = "";
			String receive_zip = "";
			String receive_phone = ""; // 收货人电话号码，如：0571-81234567
			String receive_mobile = "";
			// 网站商品的展示地址，不允许加?id=123这类自定义参数
			String show_url = "";
			// 把请求参数打包成数组
			Map<String, String> sParaTemp = new HashMap<String, String>();
			sParaTemp.put("payment_type", "1");
			sParaTemp.put("show_url", show_url);
			sParaTemp.put("out_trade_no", out_trade_no);
			sParaTemp.put("subject", subject);
			sParaTemp.put("body", body);
			sParaTemp.put("price", price);
			sParaTemp.put("logistics_fee", logistics_fee);
			sParaTemp.put("logistics_type", logistics_type);
			sParaTemp.put("logistics_payment", logistics_payment);
			sParaTemp.put("quantity", quantity);
			sParaTemp.put("extra_common_param", extra_common_param);
			sParaTemp.put("receive_name", receive_name);
			sParaTemp.put("receive_address", receive_address);
			sParaTemp.put("receive_zip", receive_zip);
			sParaTemp.put("receive_phone", receive_phone);
			sParaTemp.put("receive_mobile", receive_mobile);
			// 构造函数，生成请求URL
			result = AlipayService.trade_create_by_buyer(config, sParaTemp);
		}
		return result;
	}

	/**
	 * 生成快钱在线表单
	 * 
	 * @param url
	 *            系统url
	 * @param payment_id
	 *            支付方式id
	 * @param type
	 *            支付类型，分为goods支付商品，cash在线充值
	 * @param order_id
	 *            订单编号，根据type区分类型
	 */
	public String generic99Bill(String url, String payment_id, String type,
			String id) throws UnsupportedEncodingException {
		String result = "";
		OrderForm of = null;
		Predeposit pd = null;
		GoldRecord gold = null;
		IntegralGoodsOrder ig_order = null;
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
		Payment payment = this.paymentService.getObjById(CommUtil
				.null2Long(payment_id));
		if (payment == null)
			payment = new Payment();
		BillConfig config = new BillConfig(payment.getMerchantAcctId(),
				payment.getRmbKey(), payment.getPid());
		// 人民币网关账户号
		// /请登录快钱系统获取用户编号，用户编号后加01即为人民币网关账户号。
		String merchantAcctId = config.getMerchantAcctId();
		String key = config.getKey();
		String inputCharset = "1";// 字符编码 1为UTF-8 2为GBK 3为GB2312
		String bgUrl = url + "/bill_notify_return.htm"; // 服务器接受支付结果的后台地址
		String pageUrl = url + "/bill_return.htm";// 服务器接受支付结果的后台地址
		String version = "v2.0";// 网关版本
		String language = "1";// 网关页面显示语言种类,1为中文
		String signType = "1";// 签名类型,1代表MD5加密签名方式
		// 支付人姓名
		// /可为中文或英文字符
		String payerName = SecurityUserHolder.getCurrentUser().getUserName();
		// 支付人联系方式类型.固定选择值
		// /只能选择1
		// /1代表Email
		String payerContactType = "1";
		// 支付人联系方式
		// /只能选择Email或手机号
		String payerContact = "";
		// 商户订单号
		// /由字母、数字、或[-][_]组成
		String orderId = "";
		String trade_no = CommUtil.formatTime("yyyyMMddHHmmss", new Date());
		if (type.equals("goods")) {
			of.setTrade_no("order-" + trade_no + "-" + of.getId().toString());
			boolean flag = this.orderFormService.update(of);// 更新订单流水号
			if (flag) {
				orderId = "order-" + trade_no + "-" + of.getId().toString();
				System.out.println(orderId);
			}
		}

		if (type.equals("cash")) {
			pd.setPd_no("pd-" + trade_no + "-" + pd.getId().toString());
			boolean flag = this.predepositService.update(pd);
			if (flag) {
				orderId = "pd-" + trade_no + "-" + pd.getId().toString();
			}
		}
		if (type.equals("gold")) {
			gold.setGold_sn("gold-" + trade_no + "-" + gold.getId().toString());
			boolean flag = this.goldRecordService.update(gold);
			if (flag) {
				orderId = "gold-" + trade_no + "-" + gold.getId().toString();
			}
		}
		if (type.equals("integral")) {
			ig_order.setIgo_order_sn("igo-" + trade_no + "-"
					+ ig_order.getId().toString());
			boolean flag = this.integralGoodsOrderService.update(ig_order);
			if (flag) {
				orderId = "igo-" + trade_no + "-" + ig_order.getId().toString();
			}
		}
		if (type.equals("group")) {
			of.setTrade_no("order-" + trade_no + "-" + of.getId().toString());
			boolean flag = this.orderFormService.update(of);// 更新订单流水号
			if (flag) {
				orderId = "order-" + trade_no + "-" + of.getId().toString();
			}
		}
		// 订单金额
		// /以分为单位，必须是整型数字
		// /比方2，代表0.02元
		String orderAmount = "";
		if (type.equals("goods")) {
			double total_price = this.orderFormtools.query_order_price(CommUtil
					.null2String(of.getId()));
			orderAmount = String.valueOf((int) Math.round(CommUtil
					.null2Double(total_price) * 100));
		}
		if (type.equals("cash")) {
			orderAmount = String.valueOf((int) Math.round(CommUtil
					.null2Double(pd.getPd_amount()) * 100));
		}
		if (type.equals("gold")) {
			orderAmount = String.valueOf((int) Math.round(CommUtil
					.null2Double(gold.getGold_money()) * 100));
		}
		if (type.equals("integral")) {
			orderAmount = String.valueOf((int) Math.round(CommUtil
					.null2Double(ig_order.getIgo_trans_fee()) * 100));
		}
		if (type.equals("group")) {
			orderAmount = String.valueOf((int) Math.round(CommUtil
					.null2Double(of.getTotalPrice()) * 100));
		}
		// 订单提交时间
		// /14位数字。年[4位]月[2位]日[2位]时[2位]分[2位]秒[2位]
		// /如；20080101010101
		String orderTime = new java.text.SimpleDateFormat("yyyyMMddHHmmss")
				.format(new java.util.Date());
		// 商品名称
		// /可为中文或英文字符
		String productName = "";
		if (type.equals("goods")) {
			productName = of.getOrder_id();
		}
		if (type.equals("cash")) {
			productName = pd.getPd_sn();
		}
		if (type.equals("gold")) {
			productName = gold.getGold_sn();
		}
		if (type.equals("integral")) {
			productName = ig_order.getIgo_order_sn();
		}
		if (type.equals("store_deposit")) {
			productName = "store_deposit";
		}
		if (type.equals("group")) {
			productName = of.getOrder_id();
		}
		// 商品数量
		// /可为空，非空时必须为数字
		String productNum = "1";

		// 商品代码
		// /可为字符或者数字
		String productId = "";

		// 商品描述
		String productDesc = "";

		// 扩展字段1
		// /在支付结束后原样返回给商户
		String ext1 = "";
		if (type.equals("goods")) {
			ext1 = of.getId().toString();
		}
		if (type.equals("cash")) {
			ext1 = pd.getId().toString();
		}
		if (type.equals("gold")) {
			ext1 = gold.getId().toString();
		}
		if (type.equals("integral")) {
			ext1 = ig_order.getId().toString();
		}
		if (type.equals("group")) {
			ext1 = of.getId().toString();
		}
		// 扩展字段2
		// /在支付结束后原样返回给商户
		String ext2 = type;

		// 支付方式.固定选择值
		// /只能选择00、10、11、12、13、14
		// /00：组合支付（网关支付页面显示快钱支持的各种支付方式，推荐使用）10：银行卡支付（网关支付页面只显示银行卡支付）.11：电话银行支付（网关支付页面只显示电话支付）.12：快钱账户支付（网关支付页面只显示快钱账户支付）.13：线下支付（网关支付页面只显示线下支付方式）
		String payType = "00";

		// 同一订单禁止重复提交标志
		// /固定选择值： 1、0
		// /1代表同一订单号只允许提交1次；0表示同一订单号在没有支付成功的前提下可重复提交多次。默认为0建议实物购物车结算类商户采用0；虚拟产品类商户采用1
		String redoFlag = "0";

		// 快钱的合作伙伴的账户号
		// /如未和快钱签订代理合作协议，不需要填写本参数
		String pid = "";
		if (config.getPid() != null)
			pid = config.getPid();
		// 生成加密签名串
		// /请务必按照如下顺序和规则组成加密串！
		String signMsgVal = "";
		signMsgVal = BillCore.appendParam(signMsgVal, "inputCharset",
				inputCharset);
		signMsgVal = BillCore.appendParam(signMsgVal, "pageUrl", pageUrl);
		signMsgVal = BillCore.appendParam(signMsgVal, "bgUrl", bgUrl);
		signMsgVal = BillCore.appendParam(signMsgVal, "version", version);
		signMsgVal = BillCore.appendParam(signMsgVal, "language", language);
		signMsgVal = BillCore.appendParam(signMsgVal, "signType", signType);
		signMsgVal = BillCore.appendParam(signMsgVal, "merchantAcctId",
				merchantAcctId);
		signMsgVal = BillCore.appendParam(signMsgVal, "payerName", payerName);
		signMsgVal = BillCore.appendParam(signMsgVal, "payerContactType",
				payerContactType);
		signMsgVal = BillCore.appendParam(signMsgVal, "payerContact",
				payerContact);
		signMsgVal = BillCore.appendParam(signMsgVal, "orderId", orderId);
		signMsgVal = BillCore.appendParam(signMsgVal, "orderAmount",
				orderAmount);
		signMsgVal = BillCore.appendParam(signMsgVal, "orderTime", orderTime);
		signMsgVal = BillCore.appendParam(signMsgVal, "productName",
				productName);
		signMsgVal = BillCore.appendParam(signMsgVal, "productNum", productNum);
		signMsgVal = BillCore.appendParam(signMsgVal, "productId", productId);
		signMsgVal = BillCore.appendParam(signMsgVal, "productDesc",
				productDesc);
		signMsgVal = BillCore.appendParam(signMsgVal, "ext1", ext1);
		signMsgVal = BillCore.appendParam(signMsgVal, "ext2", ext2);
		signMsgVal = BillCore.appendParam(signMsgVal, "payType", payType);
		signMsgVal = BillCore.appendParam(signMsgVal, "redoFlag", redoFlag);
		signMsgVal = BillCore.appendParam(signMsgVal, "pid", pid);
		signMsgVal = BillCore.appendParam(signMsgVal, "key", key);
		// 生成加密签名串
		String signMsg = MD5Util.md5Hex(signMsgVal.getBytes("UTF-8"))
				.toUpperCase();

		// 把请求参数打包成数组
		Map<String, String> sParaTemp = new HashMap<String, String>();
		sParaTemp.put("inputCharset", inputCharset);
		sParaTemp.put("pageUrl", pageUrl);
		sParaTemp.put("bgUrl", bgUrl);
		sParaTemp.put("version", version);
		sParaTemp.put("language", language);
		sParaTemp.put("signType", signType);
		sParaTemp.put("signMsg", signMsg);
		sParaTemp.put("merchantAcctId", merchantAcctId);
		sParaTemp.put("payerName", payerName);
		sParaTemp.put("payerContactType", payerContactType);
		sParaTemp.put("payerContact", payerContact);
		sParaTemp.put("orderId", orderId);
		sParaTemp.put("orderAmount", orderAmount);
		sParaTemp.put("orderTime", orderTime);
		sParaTemp.put("productName", productName);
		sParaTemp.put("productNum", productNum);
		sParaTemp.put("productId", productId);
		sParaTemp.put("productDesc", productDesc);
		sParaTemp.put("ext1", ext1);
		sParaTemp.put("ext2", ext2);
		sParaTemp.put("payType", payType);
		sParaTemp.put("redoFlag", redoFlag);
		sParaTemp.put("pid", pid);
		result = BillService.buildForm(config, sParaTemp, "post", "确定");
		return result;
	}

	/**
	 * 生成网银在线表单
	 * 
	 * @param url
	 *            系统url
	 * @param payment_id
	 *            支付方式id
	 * @param type
	 *            支付类型，分为goods支付商品，cash在线充值
	 * @param id
	 *            订单编号，根据type区分类型
	 * 
	 */
	public String genericChinaBank(String url, String payment_id, String type,
			String id) {
		OrderForm of = null;
		Predeposit pd = null;
		GoldRecord gold = null;
		IntegralGoodsOrder ig_order = null;
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
		Payment payment = this.paymentService.getObjById(CommUtil
				.null2Long(payment_id));
		if (payment == null)
			payment = new Payment();
		List<SysMap> list = new ArrayList<SysMap>();
		String v_mid = payment.getChinabank_account();// 网银商户号
		list.add(new SysMap("v_mid", v_mid));
		String key = payment.getChinabank_key();// 网银私钥
		list.add(new SysMap("key", key));
		String v_url = url + "/chinabank_return.htm";// 网银付款回调地址
		list.add(new SysMap("v_url", v_url));
		String v_oid = "";
		String trade_no = CommUtil.formatTime("yyyyMMddHHmmss", new Date());
		if (type.equals("goods")) {
			of.setTrade_no("order-" + trade_no + "-" + of.getId().toString());
			boolean flag = this.orderFormService.update(of);// 更新订单流水号
			if (flag) {
				v_oid = "order-" + trade_no + "-" + of.getId().toString();
			}
		}

		if (type.equals("cash")) {
			pd.setPd_no("pd-" + trade_no + "-" + pd.getId().toString());
			boolean flag = this.predepositService.update(pd);
			if (flag) {
				v_oid = "pd-" + trade_no + "-" + pd.getId().toString();
			}
		}
		if (type.equals("gold")) {
			gold.setGold_sn("gold-" + trade_no + "-" + gold.getId().toString());
			boolean flag = this.goldRecordService.update(gold);
			if (flag) {
				v_oid = "gold-" + trade_no + "-" + gold.getId().toString();
			}
		}
		if (type.equals("integral")) {
			ig_order.setIgo_order_sn("igo-" + trade_no + "-"
					+ ig_order.getId().toString());
			boolean flag = this.integralGoodsOrderService.update(ig_order);
			if (flag) {
				v_oid = "igo-" + trade_no + "-" + ig_order.getId().toString();
			}
		}
		if (type.equals("group")) {
			of.setTrade_no("order-" + trade_no + "-" + of.getId().toString());
			boolean flag = this.orderFormService.update(of);// 更新订单流水号
			if (flag) {
				v_oid = "order-" + trade_no + "-" + of.getId().toString();
			}
		}
		list.add(new SysMap("v_oid", v_oid));
		String v_amount = "";
		if (type.equals("goods")) {
			double total_price = this.orderFormtools.query_order_price(CommUtil
					.null2String(of.getId()));
			v_amount = CommUtil.null2String(total_price);// 订单总价格
		}
		if (type.equals("cash")) {
			v_amount = CommUtil.null2String(pd.getPd_amount());// 订单总价格
		}
		if (type.equals("gold")) {
			v_amount = CommUtil.null2String(gold.getGold_money());// 订单总价格
		}
		if (type.equals("integral")) {
			v_amount = CommUtil.null2String(ig_order.getIgo_trans_fee());// 订单总价格
		}
		if (type.equals("group")) {
			v_amount = CommUtil.null2String(of.getTotalPrice());// 订单总价格
		}
		list.add(new SysMap("v_amount", v_amount));
		String v_moneytype = "CNY";// 支付币种，CNY表示人民币
		list.add(new SysMap("v_moneytype", v_moneytype));
		String temp = v_amount + v_moneytype + v_oid + v_mid + v_url + key; // 拼凑加密串
		String v_md5info = Md5Encrypt.md5(temp).toUpperCase();// 使用MD5加密字符串
		list.add(new SysMap("v_md5info", v_md5info));
		// 以下为可选项
		String v_rcvname = "";// 收货人
		String v_rcvaddr = "";// 收货地址
		String v_rcvtel = "";// 收货人电话
		String v_rcvpost = "";// 收货人邮编
		String v_rcvemail = "";// 收货人邮件
		String v_rcvmobile = "";// 收货人手机号码
		String remark1 = "";// 备注1
		if (type.equals("goods")) {
			remark1 = of.getId().toString();
		}
		if (type.equals("cash")) {
			remark1 = pd.getId().toString();
		}
		if (type.equals("gold")) {
			remark1 = gold.getId().toString();
		}
		if (type.equals("integral")) {
			remark1 = ig_order.getId().toString();
		}
		if (type.equals("group")) {
			remark1 = of.getId().toString();
		}
		list.add(new SysMap("remark1", remark1));
		String remark2 = type;// 备注2
		list.add(new SysMap("remark2", remark2));
		String ret = ChinaBankSubmit.buildForm(list);
		return ret;
	}

	/**
	 * 生成Paypal支付表单并自动提交
	 * 
	 * @param url
	 * @param payment_id
	 * @param type
	 * @param id
	 * @return
	 */
	public String genericPaypal(String url, String payment_id, String type,
			String id) {
		OrderForm of = null;
		Predeposit pd = null;
		GoldRecord gold = null;
		IntegralGoodsOrder ig_order = null;
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
		Payment payment = this.paymentService.getObjById(CommUtil
				.null2Long(payment_id));
		if (payment == null)
			payment = new Payment();
		List<SysMap> sms = new ArrayList<SysMap>();
		String business = payment.getPaypal_userId();// Paypal商户号
		sms.add(new SysMap("business", business));
		String return_url = url + "/paypal_return.htm";// paypal付款回调地址
		String notify_url = url + "/paypal_return.htm";// paypal notify地址
		sms.add(new SysMap("return", return_url));
		String item_name = "";
		String trade_no = CommUtil.formatTime("yyyyMMddHHmmss", new Date());
		if (type.equals("goods")) {
			of.setTrade_no("order-" + trade_no + "-" + of.getId().toString());
			boolean flag = this.orderFormService.update(of);// 更新订单流水号
			if (flag) {
				item_name = "order-" + trade_no + "-" + of.getId().toString();
			}
		}

		if (type.equals("cash")) {
			pd.setPd_no("pd-" + trade_no + "-" + pd.getId().toString());
			boolean flag = this.predepositService.update(pd);
			if (flag) {
				item_name = "pd-" + trade_no + "-" + pd.getId().toString();
			}
		}
		if (type.equals("gold")) {
			gold.setGold_sn("gold-" + trade_no + "-" + gold.getId().toString());
			boolean flag = this.goldRecordService.update(gold);
			if (flag) {
				item_name = "gold-" + trade_no + "-" + gold.getId().toString();
			}
		}
		if (type.equals("integral")) {
			ig_order.setIgo_order_sn("igo-" + trade_no + "-"
					+ ig_order.getId().toString());
			boolean flag = this.integralGoodsOrderService.update(ig_order);
			if (flag) {
				item_name = "igo-" + trade_no + "-"
						+ ig_order.getId().toString();
			}
		}
		if (type.equals("group")) {
			of.setTrade_no("order-" + trade_no + "-" + of.getId().toString());
			boolean flag = this.orderFormService.update(of);// 更新订单流水号
			if (flag) {
				item_name = "order-" + trade_no + "-" + of.getId().toString();
			}
		}
		sms.add(new SysMap("item_name", item_name));
		String amount = "";
		String item_number = "";
		if (type.equals("goods")) {
			double total_price = this.orderFormtools.query_order_price(CommUtil
					.null2String(of.getId()));
			amount = CommUtil.null2String(total_price);// 订单总价格
			item_number = of.getOrder_id();
		}
		if (type.equals("cash")) {
			amount = CommUtil.null2String(pd.getPd_amount());// 订单总价格
			item_number = pd.getPd_sn();
		}
		if (type.equals("gold")) {
			amount = CommUtil.null2String(gold.getGold_money());// 订单总价格
			item_number = gold.getGold_sn();
		}
		if (type.equals("integral")) {
			amount = CommUtil.null2String(ig_order.getIgo_trans_fee());// 订单总价格
			item_number = ig_order.getIgo_order_sn();
		}
		if (type.equals("group")) {
			amount = CommUtil.null2String(of.getTotalPrice());// 订单总价格
			item_number = of.getOrder_id();
		}
		sms.add(new SysMap("amount", amount));
		sms.add(new SysMap("notify_url", notify_url));
		sms.add(new SysMap("cmd", "_xclick"));
		sms.add(new SysMap("currency_code", payment.getCurrency_code()));
		sms.add(new SysMap("item_number", item_number));
		// sms.add(new SysMap("no_shipping", "1"));
		// sms.add(new SysMap("no_note", "1"));
		// 以下为可选项
		String custom = "";// 备注1
		if (type.equals("goods")) {
			custom = of.getId().toString();
		}
		if (type.equals("cash")) {
			custom = pd.getId().toString();
		}
		if (type.equals("gold")) {
			custom = gold.getId().toString();
		}
		if (type.equals("integral")) {
			custom = ig_order.getId().toString();
		}
		if (type.equals("group")) {
			custom = of.getId().toString();
		}
		custom = custom + "," + type;
		sms.add(new SysMap("custom", custom));
		String ret = PaypalTools.buildForm(sms);
		return ret;
	}
	
	/**
	 * 生成通联支付表单并自动提交
	 * 
	 * @param url
	 * @param payment_id
	 * @param type
	 * @param id
	 * @return
	 */
	public String genericAllinpay(String url, String payment_id, String type,
			String id) {
		OrderForm of = null;
		Predeposit pd = null;
		GoldRecord gold = null;
		IntegralGoodsOrder ig_order = null;
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
		Payment payment = this.paymentService.getObjById(CommUtil
				.null2Long(payment_id));
		if (payment == null)
			payment = new Payment();
		List<SysMap> sms = new ArrayList<SysMap>();
		String merchantId = payment.getMerchantId();// 通联商户号
		String key = payment.getAllinpayKey();// 通联商户号
		String allinpayUrl = payment.getAllinpayUrl();// 通联商户号
		String pickupUrl = url + "/allinpay_return.htm";// 通联页面跳转同步通知页面路径
		String receiveUrl = url + "/allinpay_return.htm";// 通联服务器异步通知页面路径
//		String pickupUrl = "http://mingyewl.eicp.net:8083/tytsms-web" + "/allinpay_return.htm";// 通联页面跳转同步通知页面路径
//		String receiveUrl = "http://mingyewl.eicp.net:8083/tytsms-web" + "/allinpay_return.htm";// 通联服务器异步通知页面路径
		String item_name = "";
		String trade_no = CommUtil.formatTime("yyyyMMddHHmmss", new Date());
		if (type.equals("goods")) {
			of.setTrade_no("order-" + trade_no + "-" + of.getId().toString());
			boolean flag = this.orderFormService.update(of);// 更新订单流水号
			if (flag) {
				item_name = "order-" + trade_no + "-" + of.getId().toString();
			}
		}
		
		if (type.equals("cash")) {
			pd.setPd_no("pd-" + trade_no + "-" + pd.getId().toString());
			boolean flag = this.predepositService.update(pd);
			if (flag) {
				item_name = "pd-" + trade_no + "-" + pd.getId().toString();
			}
		}
		if (type.equals("gold")) {
			gold.setGold_sn("gold-" + trade_no + "-" + gold.getId().toString());
			boolean flag = this.goldRecordService.update(gold);
			if (flag) {
				item_name = "gold-" + trade_no + "-" + gold.getId().toString();
			}
		}
		if (type.equals("integral")) {
			ig_order.setIgo_order_sn("igo-" + trade_no + "-"
					+ ig_order.getId().toString());
			boolean flag = this.integralGoodsOrderService.update(ig_order);
			if (flag) {
				item_name = "igo-" + trade_no + "-"
						+ ig_order.getId().toString();
			}
		}
		if (type.equals("group")) {
			of.setTrade_no("order-" + trade_no + "-" + of.getId().toString());
			boolean flag = this.orderFormService.update(of);// 更新订单流水号
			if (flag) {
				item_name = "order-" + trade_no + "-" + of.getId().toString();
			}
		}
		String orderAmount = "";
		if (type.equals("goods")) {
			double total_price = this.orderFormtools.query_order_price(CommUtil
					.null2String(of.getId()));
			orderAmount = String.valueOf((int) Math.round(CommUtil
					.null2Double(total_price) * 100));
		}
		if (type.equals("cash")) {
			orderAmount = String.valueOf((int) Math.round(CommUtil
					.null2Double(pd.getPd_amount()) * 100));
		}
		if (type.equals("gold")) {
			orderAmount = String.valueOf((int) Math.round(CommUtil
					.null2Double(gold.getGold_money()) * 100));
		}
		if (type.equals("integral")) {
			orderAmount = String.valueOf((int) Math.round(CommUtil
					.null2Double(ig_order.getIgo_trans_fee()) * 100));
		}
		if (type.equals("group")) {
			orderAmount = String.valueOf((int) Math.round(CommUtil
					.null2Double(of.getTotalPrice()) * 100));
		}
		
		// 扩展字段1
		// /在支付结束后原样返回给商户
		String ext1 = "";
		if (type.equals("goods")) {
			ext1 = of.getId().toString();
		}
		if (type.equals("cash")) {
			ext1 = pd.getId().toString();
		}
		if (type.equals("gold")) {
			ext1 = gold.getId().toString();
		}
		if (type.equals("integral")) {
			ext1 = ig_order.getId().toString();
		}
		if (type.equals("group")) {
			ext1 = of.getId().toString();
		}
		// 扩展字段2
		// /在支付结束后原样返回给商户
		String ext2 = type;		
		
		//构造订单请求对象，生成signMsg。
		RequestOrder requestOrder = new RequestOrder();
		requestOrder.setInputCharset(1);
		sms.add(new SysMap("inputCharset", 1));
		requestOrder.setPickupUrl(pickupUrl);
		sms.add(new SysMap("pickupUrl", pickupUrl));
		requestOrder.setReceiveUrl(receiveUrl);
		sms.add(new SysMap("receiveUrl", receiveUrl));
		requestOrder.setVersion("v1.0");
		sms.add(new SysMap("version", "v1.0"));
		requestOrder.setLanguage(1);
		sms.add(new SysMap("language", 1));
		requestOrder.setSignType(1);
		sms.add(new SysMap("signType", 1));

		//发卡方代码
//		requestOrder.setIssuerId("");
		requestOrder.setMerchantId(merchantId);
		sms.add(new SysMap("merchantId", merchantId));
		sms.add(new SysMap("payerName", ""));
		sms.add(new SysMap("payerEmail", ""));
		sms.add(new SysMap("payerTelephone", ""));
		sms.add(new SysMap("payerIDCard", ""));
		sms.add(new SysMap("pid", ""));
//		requestOrder.setPayerName("");
//		requestOrder.setPayerEmail("");
//		requestOrder.setPayerTelephone("");
//		requestOrder.setPayerIDCard("");
//		requestOrder.setPid("");
		requestOrder.setOrderNo(trade_no);
		sms.add(new SysMap("orderNo", trade_no));
		//订单金额 要变成分为单位
		requestOrder.setOrderAmount(Long.parseLong(orderAmount));
		sms.add(new SysMap("orderAmount", orderAmount));
		requestOrder.setOrderCurrency("0");
		sms.add(new SysMap("orderCurrency", "0"));
		requestOrder.setOrderDatetime(CommUtil.formatTime("yyyyMMddHHmmss", new Date()));
		sms.add(new SysMap("orderDatetime", CommUtil.formatTime("yyyyMMddHHmmss", new Date())));
		requestOrder.setOrderExpireDatetime("60");
		sms.add(new SysMap("orderExpireDatetime", "60"));
		sms.add(new SysMap("productName", "泰易淘商城商品"));
		requestOrder.setProductName("泰易淘商城商品");
		sms.add(new SysMap("productPrice", ""));
		sms.add(new SysMap("productNum", ""));
		sms.add(new SysMap("productId", ""));
		sms.add(new SysMap("productDesc", ""));
		sms.add(new SysMap("ext1", ext1));
		sms.add(new SysMap("ext2", ext2));
//		requestOrder.setProductName(item_name);
//		requestOrder.setProductPrice(Long.parseLong(amount));
//		requestOrder.setProductNum(item_number);
//		requestOrder.setProductId(productId);
//		requestOrder.setProductDesc(productDesc);
		requestOrder.setExt1(ext1);
		requestOrder.setExt2(ext2);
//		requestOrder.setExtTL(extTL);//通联商户拓展业务字段，在v2.2.0版本之后才使用到的，用于开通分账等业务
//		requestOrder.setPan(pan);
//		requestOrder.setTradeNature(tradeNature);
//		requestOrder.setKey(key); //key为MD5密钥，密钥是在通联支付网关会员服务网站上设置。
		//支付方式
//		固定选择值：0、1、4、10、11、12、21、22
//		接入手机网关时，该值填固定填0
//		接入互联网关时，默认为间连模式，填0
//		若需接入外卡支付，只支持直连模式，即固定上送payType=23，issuerId=visa或mastercard
//		0代表未指定支付方式，即显示该商户开通的所有支付方式
//		1个人网银支付
//		4企业网银支付
//		10 wap支付
//		11信用卡支付
//		12快捷付
//		21认证支付
//		23外卡支付
//		非直连模式，设置为0；直连模式，值为非0的固定选择值
//		该字段在version=v1.0时参与签名，version =v2.0 时不参与签名
		requestOrder.setPayType(0);
		sms.add(new SysMap("payType", 0));
		sms.add(new SysMap("issuerId", ""));
		sms.add(new SysMap("pan", ""));
		sms.add(new SysMap("tradeNature", ""));
		requestOrder.setKey(key);
		String strSrcMsg = requestOrder.getSrc(); // 此方法用于debug，测试通过后可注释。
		String strSignMsg = requestOrder.doSign(); // 签名，设为signMsg字段值。		
		sms.add(new SysMap("signMsg", strSignMsg));
		
		String ret = AllinpayTools.buildForm(sms,allinpayUrl);
		return ret;
	}
	
	
	/**
	 * 
	 * 
	 * @param url
	 * @param payment_id
	 * @param type
	 * @param id
	 * @return
	 */
	public String genericWechatpay(String url, String payment_id, String type,
			String id,String spbill_create_ip,String realPath) {
		OrderForm of = null;
		Predeposit pd = null;
		GoldRecord gold = null;
		IntegralGoodsOrder ig_order = null;
		
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
			double total_price = this.orderFormtools.query_order_price(CommUtil
					.null2String(of.getId()));
			orderAmount = String.valueOf((int) Math.round(CommUtil
					.null2Double(total_price) * 100));
		}
		if (type.equals("cash")) {
			orderAmount = String.valueOf((int) Math.round(CommUtil
					.null2Double(pd.getPd_amount()) * 100));
		}
		if (type.equals("gold")) {
			orderAmount = String.valueOf((int) Math.round(CommUtil
					.null2Double(gold.getGold_money()) * 100));
		}
		if (type.equals("integral")) {
			orderAmount = String.valueOf((int) Math.round(CommUtil
					.null2Double(ig_order.getIgo_trans_fee()) * 100));
		}
		if (type.equals("group")) {
			orderAmount = String.valueOf((int) Math.round(CommUtil
					.null2Double(of.getTotalPrice()) * 100));
		}
		
		//获取openid
//		String returnJSON=HttpTool.getToken(payment.getAppid(), payment.getAppSecret(), "authorization_code", code);
//		JSONObject obj = JSONObject.fromObject(returnJSON);
//		String openid=obj.get("openid").toString();
		//封装传递参数（必填字段信息）
		WeChatConfig wechatconfig = new WeChatConfig();
		wechatconfig.setAppid(payment.getAppid());
		wechatconfig.setMch_id(payment.getMch_id());
		wechatconfig.setBody(body);
		wechatconfig.setOut_trade_no(out_trade_no);
		wechatconfig.setProduct_id(out_trade_no);
		wechatconfig.setTotal_fee(orderAmount);
		wechatconfig.setSpbill_create_ip(spbill_create_ip);
		//wechatconfig.setNotify_url("http://www.taiyitao.com:8020/tytsms-web/wechat_notify.htm");
		wechatconfig.setNotify_url(url+"/wechat_notify.htm");
		wechatconfig.setTrade_type("NATIVE");
//		wechatconfig.setOpenid(openid);http://www.taiyitao.com/wechat_notify.htm
		//封装传递参数（附加数据）
		wechatconfig.setAttach(type+","+id);
		
//		Map<String, String> wxPayParamMap = null;
		String code_url="";
		try {
			code_url = WXJSPay.qcscan(wechatconfig,payment,realPath);
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return code_url;
	}
	

}
