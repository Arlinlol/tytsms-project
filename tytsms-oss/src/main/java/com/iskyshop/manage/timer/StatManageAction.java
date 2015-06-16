package com.iskyshop.manage.timer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Activity;
import com.iskyshop.foundation.domain.ActivityGoods;
import com.iskyshop.foundation.domain.Evaluate;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.GoodsSpecProperty;
import com.iskyshop.foundation.domain.Group;
import com.iskyshop.foundation.domain.GroupGoods;
import com.iskyshop.foundation.domain.IntegralLog;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.OrderFormLog;
import com.iskyshop.foundation.domain.PayoffLog;
import com.iskyshop.foundation.domain.ReturnGoodsLog;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.StorePoint;
import com.iskyshop.foundation.domain.StoreStat;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.Template;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.VerifyCode;
import com.iskyshop.foundation.service.IActivityGoodsService;
import com.iskyshop.foundation.service.IActivityService;
import com.iskyshop.foundation.service.IEvaluateService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGroupGoodsService;
import com.iskyshop.foundation.service.IGroupService;
import com.iskyshop.foundation.service.IIntegralLogService;
import com.iskyshop.foundation.service.IOrderFormLogService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IPaymentService;
import com.iskyshop.foundation.service.IPayoffLogService;
import com.iskyshop.foundation.service.IPredepositLogService;
import com.iskyshop.foundation.service.IReturnGoodsLogService;
import com.iskyshop.foundation.service.IStorePointService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.IStoreStatService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.ITemplateService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.foundation.service.IVerifyCodeService;
import com.iskyshop.manage.admin.tools.MsgTools;
import com.iskyshop.manage.admin.tools.OrderFormTools;
import com.iskyshop.manage.admin.tools.StatTools;
import com.iskyshop.pay.wechatpay.util.ConfigContants;
import com.taiyitao.elasticsearch.ElasticsearchUtil;
import com.taiyitao.elasticsearch.IndexVo.IndexName;
import com.taiyitao.elasticsearch.IndexVo.IndexType;
import com.taiyitao.elasticsearch.IndexVoTools;

/**
 * 
 * <p>
 * Title: StatManageAction.java
 * </p>
 * 
 * <p>
 * Description:系统定制器类，每间隔半小时执行一次，用在数据统计及团购开启关闭、自动确认订单生产结算日志等,
 * 其他按小时计算的定制器都可以在这里增加代码控制
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
 * @date 2014-5-13
 * 
 * @version iskyshop_b2b2c 1.0
 */
@Component(value = "shop_stat")
public class StatManageAction {
	@Autowired
	private IStoreStatService storeStatService;
	@Autowired
	private StatTools statTools;
	@Autowired
	private IVerifyCodeService verifycodeService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IEvaluateService evaluateService;
	@Autowired
	private IStorePointService storePointService;
	@Autowired
	private IGroupService groupService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IOrderFormLogService orderFormLogService;
	@Autowired
	private IPaymentService paymentService;
	@Autowired
	private IPredepositLogService predepositLogService;
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserService userService;
	@Autowired
	private ITemplateService templateService;
	@Autowired
	private IActivityService activityService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IActivityGoodsService activityGoodsService;
	@Autowired
	private IGroupGoodsService groupGoodsService;
	@Autowired
	private IPayoffLogService payoffLogService;
	@Autowired
	private MsgTools msgTools;
	@Autowired
	private IGroupGoodsService ggService;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private OrderFormTools orderFormTools;
	@Autowired
	private IReturnGoodsLogService returnGoodsLogService;
	@Autowired
	private ElasticsearchUtil elasticsearchUtil;
	@Autowired
	private IIntegralLogService integralLogService;
	

	public void execute() throws Exception {
		// System.out.println(System.getProperty("tytsms.root"));
		// 统计信息
		SysConfig sc = this.configService.getSysConfig();
		List<StoreStat> stats = this.storeStatService.query(
				"select obj from StoreStat obj", null, -1, -1);
		StoreStat stat = null;
		if (stats.size() > 0) {
			stat = stats.get(0);
		} else {
			stat = new StoreStat();
		}
		stat.setAddTime(new Date());
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, 30);
		stat.setNext_time(cal.getTime());
		stat.setWeek_complaint(this.statTools.query_complaint(-7));
		stat.setWeek_goods(this.statTools.query_goods(-7));
		stat.setWeek_order(this.statTools.query_order(-7));
		stat.setWeek_report(this.statTools.query_report(-7));
		stat.setWeek_store(this.statTools.query_store(-7));
		stat.setWeek_user(this.statTools.query_user(-7));
		stat.setWeek_live_user(this.statTools.query_live_user(-7));
		stat.setAll_goods(this.statTools.query_all_goods());
		stat.setAll_store(this.statTools.query_all_store());
		stat.setAll_user(this.statTools.query_all_user());
		stat.setStore_audit(this.statTools.query_audit_store());
		stat.setOrder_amount(BigDecimal.valueOf(this.statTools
				.query_all_amount()));
		stat.setNot_payoff_num(this.statTools.query_payoff());
		stat.setNot_refund(this.statTools.query_refund());
		stat.setNot_grouplife_refund(this.statTools.query_grouplife_refund());
		stat.setAll_sale_amount(CommUtil.null2Int(sc.getPayoff_all_sale()));
		stat.setAll_commission_amount(CommUtil.null2Int(sc
				.getPayoff_all_commission()));
		stat.setAll_payoff_amount(CommUtil.null2Int(sc.getPayoff_all_amount()));
		stat.setAll_payoff_amount_reality(CommUtil.null2Int(sc
				.getPayoff_all_amount_reality()));
		stat.setAll_user_balance(BigDecimal.valueOf(this.statTools
				.query_all_user_balance()));
		System.out.println(this.statTools.query_all_user_balance());
		stat.setSelf_goods(this.statTools.query_self_goods());
		stat.setSelf_storage_goods(this.statTools.query_self_storage_goods());
		stat.setSelf_out_goods(this.statTools.query_self_out_goods());
		stat.setSelf_order_shipping(this.statTools.query_self_order_shipping());
		stat.setSelf_order_pay(this.statTools.query_self_order_pay());
		stat.setSelf_order_evaluate(this.statTools.query_self_order_evaluate());
		stat.setSelf_all_order(this.statTools.query_self_all_order());
		stat.setSelf_return_apply(this.statTools.query_self_return_apply());
		stat.setSelf_grouplife_refund(this.statTools
				.query_self_groupinfo_return_apply());
		if (stats.size() > 0) {
			this.storeStatService.update(stat);
		} else
			this.storeStatService.save(stat);
		// 删除验证码信息
		cal.setTime(new Date());
		cal.add(Calendar.MINUTE, -30);
		Map params = new HashMap();
		params.put("time", cal.getTime());
		List<VerifyCode> mvcs = this.verifycodeService.query(
				"select obj from VerifyCode obj where obj.addTime<=:time",
				params, -1, -1);
		for (VerifyCode mvc : mvcs) {
			this.verifycodeService.delete(mvc.getId());
		}
		// 设置下次结算日期
		// 系统处理最近结算日期
		SysConfig sysConfig = this.configService.getSysConfig();
		int payoff_count = sysConfig.getPayoff_count();
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
		Date payoff_data = new Date();
		int now_date = payoff_data.getDate();
		String str[] = selected.split(",");
		for (String payoff_date : str) {
			if (CommUtil.null2Int(payoff_date) >= now_date) {
				payoff_data.setDate(CommUtil.null2Int(payoff_date));
				payoff_data.setHours(0);
				payoff_data.setMinutes(00);
				payoff_data.setSeconds(01);
				break;
			}
		}
		String ms = "";
		for (int i = 0; i < str.length; i++) {
			if (i + 1 == str.length) {
				ms = ms + str[i] + "日";
			} else {
				ms = ms + str[i] + "日、";
			}
		}
		ms = "今天是"
				+ DateFormat.getDateInstance(DateFormat.FULL)
						.format(new Date()) + "，本月的结算日期为" + ms + "，请于结算日申请结算。";
		sysConfig.setPayoff_mag_default(ms);
		sysConfig.setPayoff_date(payoff_data);// 设置下次结算日期
		this.configService.update(sysConfig);
		// 统计店铺的评分信息
		List<GoodsClass> gcs = this.goodsClassService.query(
				"select obj from GoodsClass obj where obj.parent.id is null",
				null, -1, -1);
		for (GoodsClass gc : gcs) {
			double description_evaluate = 0;
			double service_evaluate = 0;
			double ship_evaluate = 0;
			params.clear();
			params.put("gc_id", gc.getId());
			List<StorePoint> sp_list = this.storePointService
					.query("select obj from StorePoint obj where obj.store.gc_main_id=:gc_id",
							params, -1, -1);
			for (StorePoint sp : sp_list) {
				description_evaluate = CommUtil.add(description_evaluate,
						sp.getDescription_evaluate());
				service_evaluate = CommUtil.add(service_evaluate,
						sp.getService_evaluate());
				ship_evaluate = CommUtil.add(ship_evaluate,
						sp.getShip_evaluate());
			}
			gc.setDescription_evaluate(BigDecimal.valueOf(CommUtil.div(
					description_evaluate, sp_list.size())));
			gc.setService_evaluate(BigDecimal.valueOf(CommUtil.div(
					service_evaluate, sp_list.size())));
			gc.setShip_evaluate(BigDecimal.valueOf(CommUtil.div(ship_evaluate,
					sp_list.size())));
			this.goodsClassService.update(gc);
		}
		// 团购监控，团购添加时候可以控制到小时，每个半小时统计一次团购是否过期，是否开启
		List<Group> groups = this.groupService.query(
				"select obj from Group obj order by obj.addTime", null, -1, -1);
		for (Group group : groups) {
			if (group.getBeginTime().before(new Date())
					&& group.getEndTime().after(new Date())) {
				group.setStatus(0);
				this.groupService.update(group);
			}
			if (group.getEndTime().before(new Date())) {
				group.setStatus(-2);
				this.groupService.update(group);
				params.clear();
				params.put("group_id", group.getId());
				List<GroupGoods> gg_list = this.ggService
						.query("select obj from GroupGoods obj where obj.group.id=:group_id",
								params, -1, -1);
				for (GroupGoods gg : gg_list) {
					gg.setGg_status(-2);
					this.groupGoodsService.update(gg);
					Goods goods = gg.getGg_goods();
					goods.setGroup_buy(0);
					goods.setGroup(null);
					goods.setGoods_current_price(goods.getStore_price());
					this.goodsService.update(goods);
				}
			}
		}
		// 商城活动监控，自动关闭过期的商城活动,同时恢复对应的商品状态、价格
		params.clear();
		params.put("ac_end_time", new Date());
		params.put("ac_status", 1);
		List<Activity> acts = this.activityService
				.query("select obj from Activity obj where obj.ac_end_time<=:ac_end_time and obj.ac_status=:ac_status",
						params, -1, -1);
		for (Activity act : acts) {
			act.setAc_status(0);
			this.activityService.update(act);
			for (ActivityGoods ac : act.getAgs()) {
				ac.setAg_status(-2);// 到期关闭
				this.activityGoodsService.update(ac);
				Goods goods = ac.getAg_goods();
				goods.setActivity_status(0);
				goods.setGoods_current_price(goods.getStore_price());
				this.goodsService.update(goods);
			}
		}
		// 检测给予短信、邮件提醒即将确认自动收货的订单信息
		int auto_order_notice = this.configService.getSysConfig()
				.getAuto_order_notice();
		cal = Calendar.getInstance();
		params.clear();
		cal.add(Calendar.DAY_OF_YEAR, -auto_order_notice);
		params.put("shipTime", cal.getTime());
		params.put("auto_confirm_email", true);
		params.put("auto_confirm_sms", true);
		List<OrderForm> notice_ofs = this.orderFormService
				.query("select obj from OrderForm obj where obj.shipTime<=:shipTime and (obj.auto_confirm_email=:auto_confirm_email or obj.auto_confirm_sms=:auto_confirm_sms)",
						params, -1, -1);
		for (OrderForm of : notice_ofs) {
			if (!of.isAuto_confirm_email()) {// 订单为商家订单
				boolean email = this.send_email(of,
						"email_tobuyer_order_will_confirm_notify");
				if (email) {
					of.setAuto_confirm_email(true);
					this.orderFormService.update(of);
				}
			}
			if (!of.isAuto_confirm_sms()) {
				User buyer = this.userService.getObjById(CommUtil.null2Long(of
						.getUser_id()));
				boolean sms = this.send_sms(of, buyer.getMobile(),
						"sms_tobuyer_order_will_confirm_notify");
				if (sms) {
					of.setAuto_confirm_sms(true);
					this.orderFormService.update(of);
				}
			}
		}
		// 检测默认自动收货的订单信息
		int auto_order_confirm = this.configService.getSysConfig()
				.getAuto_order_confirm();
		cal = Calendar.getInstance();
		params.clear();
		cal.add(Calendar.DAY_OF_YEAR, -auto_order_confirm);
		params.put("shipTime", cal.getTime());
		params.put("order_status", 30);
		List<OrderForm> confirm_ofs = this.orderFormService
				.query("select obj from OrderForm obj where obj.shipTime<=:shipTime and obj.order_status=:order_status",
						params, -1, -1);
		for (OrderForm of : confirm_ofs) {
			of.setOrder_status(40);// 自动确认收货
			boolean ret = this.orderFormService.update(of);
			if (ret) {
				Store store = this.storeService.getObjById(CommUtil
						.null2Long(of.getStore_id()));
				OrderFormLog ofl = new OrderFormLog();
				ofl.setAddTime(new Date());
				ofl.setLog_info("确认收货");
				ofl.setLog_user(SecurityUserHolder.getCurrentUser());
				ofl.setOf(of);
				this.orderFormLogService.save(ofl);
				if (this.configService.getSysConfig().isEmailEnable()
						&& of.getOrder_form() == 0) {
					this.send_email(of,
							"email_toseller_order_receive_ok_notify");
				}
				if (this.configService.getSysConfig().isSmsEnbale()
						&& of.getOrder_form() == 0) {
					this.send_sms(of, store.getUser().getMobile(),
							"sms_toseller_order_receive_ok_notify");
				}
				if (of.getPayment().getMark().equals("payafter")) {// 如果买家支付方式为货到付款，买家确认收货时更新商品库存
					this.update_goods_inventory(of);// 更新商品库存
				}
				// 自动生成结算日志
				if (of.getOrder_form() == 0) {// 商家订单生成结算日志,这里查询的是所有订单信息，不需要区分主订单及从订单信息
					PayoffLog plog = new PayoffLog();
					plog.setPl_sn("pl"
							+ CommUtil.formatTime("yyyyMMddHHmmss", new Date())
							+ store.getUser().getId());
					plog.setPl_info("订单到期自动收货");
					plog.setAddTime(new Date());
					plog.setSeller(store.getUser());
					plog.setO_id(CommUtil.null2String(of.getId()));
					plog.setOrder_id(of.getOrder_id().toString());
					plog.setCommission_amount(of.getCommission_amount());// 该订单总佣金费用
					plog.setGoods_info(of.getGoods_info());
					plog.setOrder_total_price(of.getGoods_amount());// 该订单总商品金额
					plog.setTotal_amount(BigDecimal.valueOf(CommUtil.subtract(
							of.getGoods_amount(), of.getCommission_amount())));// 该订单应结算金额：结算金额=订单总商品金额-总佣金费用
					this.payoffLogService.save(plog);
					store.setStore_sale_amount(BigDecimal.valueOf(CommUtil.add(
							of.getGoods_amount(), store.getStore_sale_amount())));// 店铺本次结算总销售金额
					store.setStore_commission_amount(BigDecimal
							.valueOf(CommUtil.add(of.getCommission_amount(),
									store.getStore_commission_amount())));// 店铺本次结算总佣金
					store.setStore_payoff_amount(BigDecimal.valueOf(CommUtil
							.add(plog.getTotal_amount(),
									store.getStore_payoff_amount())));// 店铺本次结算总佣金
					this.storeService.update(store);
					// 增加系统总销售金额、总佣金
					sc.setPayoff_all_sale(BigDecimal.valueOf(CommUtil.add(
							of.getGoods_amount(), sc.getPayoff_all_sale())));
					sc.setPayoff_all_commission(BigDecimal.valueOf(CommUtil
							.add(of.getCommission_amount(),
									sc.getPayoff_all_commission())));
					this.configService.update(sc);
				}
			}
		}
		// 到达设定时间，系统自动关闭订单相互评价功能
		int auto_order_evaluate = this.configService.getSysConfig()
				.getAuto_order_evaluate();
		cal = Calendar.getInstance();
		params.clear();
		cal.add(Calendar.DAY_OF_YEAR, -auto_order_evaluate);
		params.put("auto_order_evaluate", cal.getTime());
		params.put("order_status_40", 40);
		List<OrderForm> confirm_evaluate_ofs = this.orderFormService
				.query("select obj from OrderForm obj where obj.confirmTime<=:auto_order_evaluate and obj.order_status=:order_status_40 order by addTime asc",
						params, -1, -1);
		for (OrderForm order : confirm_evaluate_ofs) {
			order.setOrder_status(65);
			this.orderFormService.update(order);

			User user = this.userService.getObjById(CommUtil.null2Long(order
					.getUser_id()));

			// 增加消费总金额
			user.setUser_goods_fee(BigDecimal.valueOf(CommUtil.add(
					user.getUser_goods_fee(), order.getTotalPrice())));
			this.userService.update(user);
		}
		// 申请退货后到达设定时间，未能输入退货物流单号和物流公司
		int auto_order_return = this.configService.getSysConfig()
				.getAuto_order_return();
		cal = Calendar.getInstance();
		params.clear();
		cal.add(Calendar.DAY_OF_YEAR, -auto_order_return);
		params.put("return_shipTime", cal.getTime());
		params.put("order_status", 40);
		List<OrderForm> confirm_return_ofs = this.orderFormService
				.query("select obj from OrderForm obj where obj.return_shipTime<=:return_shipTime and obj.order_status>=:order_status",
						params, -1, -1);
		for (OrderForm order : confirm_return_ofs) {
			List<Map> maps = this.orderFormTools.queryGoodsInfo(order
					.getGoods_info());
			List<Map> new_maps = new ArrayList<Map>();
			Map gls = new HashMap();
			for (Map m : maps) {
				m.put("goods_return_status", -1);
				gls.putAll(m);
				new_maps.add(m);
			}
			order.setGoods_info(Json.toJson(new_maps));
			this.orderFormService.update(order);
			Map rgl_params = new HashMap();
			rgl_params.put("goods_return_status", "-2");
			rgl_params.put("return_order_id", order.getId());
			List<ReturnGoodsLog> rgl = this.returnGoodsLogService
					.query("select obj from ReturnGoodsLog obj where obj.goods_return_status is not :goods_return_status and obj.return_order_id=:return_order_id",
							rgl_params, -1, -1);
			for (ReturnGoodsLog r : rgl) {
				r.setGoods_return_status("-2");
				this.returnGoodsLogService.update(r);
			}
			// 增加购物积分
			int user_integral = (int) CommUtil.div(order.getTotalPrice(),this.configService.getSysConfig().getConsumptionRatio());
			if(user_integral>configService.getSysConfig().getEveryIndentLimit()){
				user_integral = configService.getSysConfig().getEveryIndentLimit();
			}
			User orderUser = this.userService.getObjById(CommUtil.null2Long(order.getUser_id()));
			orderUser.setIntegral(orderUser.getIntegral()
					+ user_integral);
			// 记录积分明细
			if (this.configService.getSysConfig().isIntegral()) {
				IntegralLog log = new IntegralLog();
				log.setAddTime(new Date());
				log.setContent("购物增加"
						+ user_integral + "分");
				log.setIntegral(user_integral);
				log.setIntegral_user(orderUser);
				log.setType("order");
				this.integralLogService.save(log);
			}
		}
		// 统计所有商品的评分信息
		List<Goods> goods_list = this.evaluateService.query_goods(
				"select distinct obj.evaluate_goods from Evaluate obj ", null,
				-1, -1);
		for (Goods goods : goods_list) {
			// 统计所有商品的描述相符评分
			double description_evaluate = 0;
			params.clear();
			params.put("evaluate_goods_id", goods.getId());
			List<Evaluate> eva_list = this.evaluateService
					.query("select obj from Evaluate obj where obj.evaluate_goods.id=:evaluate_goods_id",
							params, -1, -1);
			for (Evaluate eva : eva_list) {
				description_evaluate = CommUtil.add(
						eva.getDescription_evaluate(), description_evaluate);
			}
			description_evaluate = CommUtil.div(description_evaluate,
					eva_list.size());
			goods.setDescription_evaluate(BigDecimal
					.valueOf(description_evaluate));
			if (eva_list.size() > 0) {// 商品有评价情况下
				// 统计所有商品的好评率
				double well_evaluate = 0;
				params.clear();
				params.put("evaluate_goods_id", goods.getId());
				params.put("evaluate_buyer_val", 1);
				List<Evaluate> well_list = this.evaluateService
						.query("select obj from Evaluate obj where obj.evaluate_goods.id=:evaluate_goods_id and obj.evaluate_buyer_val=:evaluate_buyer_val",
								params, -1, -1);
				well_evaluate = CommUtil.div(well_list.size(), eva_list.size());
				goods.setWell_evaluate(BigDecimal.valueOf(well_evaluate));
				// 统计所有商品的中评率
				double middle_evaluate = 0;
				params.clear();
				params.put("evaluate_goods_id", goods.getId());
				params.put("evaluate_buyer_val", 0);
				List<Evaluate> middle_list = this.evaluateService
						.query("select obj from Evaluate obj where obj.evaluate_goods.id=:evaluate_goods_id and obj.evaluate_buyer_val=:evaluate_buyer_val",
								params, -1, -1);
				middle_evaluate = CommUtil.div(middle_list.size(),
						eva_list.size());
				goods.setMiddle_evaluate(BigDecimal.valueOf(middle_evaluate));
				// 统计所有商品的差评率
				double bad_evaluate = 0;
				params.clear();
				params.put("evaluate_goods_id", goods.getId());
				params.put("evaluate_buyer_val", -1);
				List<Evaluate> bad_list = this.evaluateService
						.query("select obj from Evaluate obj where obj.evaluate_goods.id=:evaluate_goods_id and obj.evaluate_buyer_val=:evaluate_buyer_val",
								params, -1, -1);
				bad_evaluate = CommUtil.div(bad_list.size(), eva_list.size());
				goods.setBad_evaluate(BigDecimal.valueOf(bad_evaluate));
			}
			// 处理定时发布商品
			if (goods.getGoods_status() == 2) {
				if (goods.getGoods_seller_time().after(new Date())) {
					goods.setGoods_status(0);
					this.goodsService.update(goods);
					// 添加lucene索引
					elasticsearchUtil.index(IndexName.GOODS, IndexType.GOODS, IndexVoTools.goodsToIndexVo(goods));
//					String goods_lucene_path = ConfigContants.LUCENE_DIRECTORY
//							+ File.separator + "luence" + File.separator
//							+ "goods";
//					LuceneVo vo = this.luceneVoTools.updateGoodsIndex(goods);
//					LuceneUtil lucene = LuceneUtil.instance();
//					lucene.setIndex_path(goods_lucene_path);
//					lucene.writeIndex(vo);
				}
			}
			this.goodsService.update(goods);
		}
	}

	private boolean send_email(OrderForm order, String mark) throws Exception {
		Template template = this.templateService.getObjByProperty("mark", mark);
		if (template != null && template.isOpen()) {
			Store store = this.storeService.getObjById(CommUtil.null2Long(order
					.getStore_id()));
			String email = store.getUser().getEmail();
			String subject = template.getTitle();
			String path = (ConfigContants.IS_UPLOAD_SERVER_MODEL ? ConfigContants.GENERATOR_FILES_SERVER_PATH
					: System.getProperty("tytsms.root"))
					+ File.separator + ConfigContants.GENERATOR_FILES_MIDDLE_NAME
					+ File.separator;
			PrintWriter pwrite = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(path + mark+".vm", false), "UTF-8"));
			pwrite.print(template.getContent());
			pwrite.flush();
			pwrite.close();
			// 生成模板
			/*Properties p = new Properties();
			p.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH,
					(ConfigContants.IS_UPLOAD_SERVER_MODEL ? ConfigContants.GENERATOR_FILES_SERVER_PATH
							: System.getProperty("tytsms.root"))
							+ File.separator + ConfigContants.GENERATOR_FILES_MIDDLE_NAME + File.separator);
			p.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
			p.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
			Velocity.init(p);*/
			VelocityEngine velocityEngine = new VelocityEngine();
            Properties properties = new Properties();
            properties.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
            properties.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
            properties.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH,path);
            System.out.println(properties.get(Velocity.FILE_RESOURCE_LOADER_PATH));
            velocityEngine.init(properties);
            
			org.apache.velocity.Template blank = velocityEngine.getTemplate(mark+".vm");
			VelocityContext context = new VelocityContext();
			User buyer = this.userService.getObjById(CommUtil.null2Long(order
					.getUser_id()));
			context.put("buyer", buyer);
			context.put("seller", store.getUser());
			context.put("config", this.configService.getSysConfig());
			context.put("send_time", CommUtil.formatLongDate(new Date()));
			context.put("webPath", this.configService.getSysConfig()
					.getAddress());
			context.put("order", order);
			StringWriter writer = new StringWriter();
			blank.merge(context, writer);
			// System.out.println(writer.toString());
			String content = writer.toString();
			boolean ret = this.msgTools.sendEmail(email, subject, content);
			return ret;
		} else
			return false;
	}

	private boolean send_sms(OrderForm order, String mobile, String mark)
			throws Exception {
		Store store = this.storeService.getObjById(CommUtil.null2Long(order
				.getStore_id()));
		Template template = this.templateService.getObjByProperty("mark", mark);
		if (template != null && template.isOpen()) {
			String path = (ConfigContants.IS_UPLOAD_SERVER_MODEL ? ConfigContants.GENERATOR_FILES_SERVER_PATH
					: System.getProperty("tytsms.root"))
					+ File.separator + ConfigContants.GENERATOR_FILES_MIDDLE_NAME
					+ File.separator;
			PrintWriter pwrite = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(path + mark+".vm", false), "UTF-8"));
			pwrite.print(template.getContent());
			pwrite.flush();
			pwrite.close();
			// 生成模板
			Properties p = new Properties();
			p.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH,
					(ConfigContants.IS_UPLOAD_SERVER_MODEL ? ConfigContants.GENERATOR_FILES_SERVER_PATH
							: System.getProperty("tytsms.root"))
							+ File.separator+ ConfigContants.GENERATOR_FILES_MIDDLE_NAME + File.separator);
			p.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
			p.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
			Velocity.init(p);
			org.apache.velocity.Template blank = Velocity.getTemplate(mark+".vm",
					"UTF-8");
			VelocityContext context = new VelocityContext();
			User buyer = this.userService.getObjById(CommUtil.null2Long(order
					.getUser_id()));
			context.put("buyer", buyer);
			context.put("seller", store.getUser());
			context.put("config", this.configService.getSysConfig());
			context.put("send_time", CommUtil.formatLongDate(new Date()));
			context.put("webPath", this.configService.getSysConfig()
					.getAddress());
			context.put("order", order);
			StringWriter writer = new StringWriter();
			blank.merge(context, writer);
			// System.out.println(writer.toString());
			String content = writer.toString();
			boolean ret = this.msgTools.sendSMS(mobile, content);
			return ret;
		} else
			return false;
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
								CommUtil.null2String(goods.getId()), IndexVoTools.groupGoodsToIndexVo(gg));
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

}