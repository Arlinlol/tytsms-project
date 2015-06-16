package com.iskyshop.manage.admin.tools;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Complaint;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GroupInfo;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.PayoffLog;
import com.iskyshop.foundation.domain.Report;
import com.iskyshop.foundation.domain.ReturnGoodsLog;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IComplaintService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGroupInfoService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IPayoffLogService;
import com.iskyshop.foundation.service.IReportService;
import com.iskyshop.foundation.service.IReturnGoodsLogService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.IUserService;

@Component
public class StatTools {
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IReportService reportService;
	@Autowired
	private IPayoffLogService plService;
	@Autowired
	private IGroupInfoService groupinfoService;
	@Autowired
	private IComplaintService complaintService;
	@Autowired
	private IReturnGoodsLogService returngoodslogService;

	public int query_store(int count) {
		List<Store> stores = new ArrayList<Store>();
		Map params = new HashMap();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, count);
		params.put("time", cal.getTime());
		stores = this.storeService.query(
				"select obj from Store obj where obj.addTime>=:time", params,
				-1, -1);
		return stores.size();
	}

	public int query_user(int count) {
		List<User> users = new ArrayList<User>();
		Map params = new HashMap();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, count);
		params.put("time", cal.getTime());
		users = this.userService.query(
				"select obj from User obj where obj.addTime>=:time", params,
				-1, -1);
		return users.size();
	}

	public int query_live_user(int count) {
		List<User> users = new ArrayList<User>();
		Map params = new HashMap();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, count);
		params.put("lastLoginDate", cal.getTime());
		params.put("userRole", "ADMIN");
		users = this.userService
				.query("select obj from User obj where obj.lastLoginDate>=:lastLoginDate and obj.userRole!=:userRole",
						params, -1, -1);
		return users.size();
	}

	public int query_goods(int count) {
		List<Goods> goods = new ArrayList<Goods>();
		Map params = new HashMap();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, count);
		params.put("time", cal.getTime());
		goods = this.goodsService.query(
				"select obj from Goods obj where obj.addTime>=:time", params,
				-1, -1);
		return goods.size();
	}

	public int query_order(int count) {
		List<OrderForm> orders = new ArrayList<OrderForm>();
		Map params = new HashMap();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, count);
		params.put("time", cal.getTime());
		orders = this.orderFormService.query(
				"select obj from OrderForm obj where obj.addTime>=:time",
				params, -1, -1);
		return orders.size();
	}

	public int query_all_user() {
		Map params = new HashMap();
		params.put("userRole", "ADMIN");
		List<User> users = this.userService.query(
				"select obj from User obj where obj.userRole!=:userRole",
				params, -1, -1);
		return users.size();
	}

	public int query_all_goods() {
		List<Goods> goods = this.goodsService.query(
				"select obj from Goods obj", null, -1, -1);
		return goods.size();
	}

	public int query_all_store() {
		List<Store> stores = this.storeService.query(
				"select obj from Store obj", null, -1, -1);
		return stores.size();
	}

	public int query_audit_store() {
		Map params = new HashMap();
		params.put("store_status1", 5);
		params.put("store_status2", 10);
		List<Store> stores = this.storeService
				.query("select obj from Store obj where obj.store_status=:store_status1 or obj.store_status=:store_status2",
						params, -1, -1);
		return stores.size();
	}

	public double query_all_amount() {
		double price = 0;
		Map params = new HashMap();
		params.put("order_status", 60);
		List<OrderForm> ofs = this.orderFormService
				.query("select obj from OrderForm obj where obj.order_status=:order_status",
						params, -1, -1);
		for (OrderForm of : ofs) {
			price = CommUtil.null2Double(of.getTotalPrice()) + price;
		}
		return price;
	}

	public int query_complaint(int count) {
		List<Complaint> objs = new ArrayList<Complaint>();
		Map params = new HashMap();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, count);
		params.put("time", cal.getTime());
		params.put("status", 0);
		objs = this.complaintService
				.query("select obj from Complaint obj where obj.addTime>=:time and obj.status=:status",
						params, -1, -1);
		return objs.size();
	}

	public int query_report(int count) {
		List<Report> objs = new ArrayList<Report>();
		Map params = new HashMap();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, count);
		params.put("time", cal.getTime());
		params.put("status", 0);
		objs = this.reportService
				.query("select obj from Report obj where obj.addTime>=:time and obj.status=:status",
						params, -1, -1);
		return objs.size();
	}

	public int query_payoff() {
		Map params = new HashMap();
		params.put("status", 1);
		List<PayoffLog> objs = this.plService.query(
				"select obj from PayoffLog obj where obj.status=:status",
				params, -1, -1);
		return objs.size();
	}

	public double query_all_user_balance() {
		Map params = new HashMap();
		params.put("userRole", "ADMIN");
		List<User> users = this.userService
				.query("select obj.availableBalance from User obj where obj.userRole!=:userRole",
						params, -1, -1);
		double banlance = 0;
		for (int i = 0; i < users.size(); i++) {
			banlance = CommUtil.add(CommUtil.null2Double(users.get(i)),
					banlance);
		}
		return banlance;
	}

	public int query_refund() {
		Map params = new HashMap();
		params.put("refund_status", 0);
		params.put("goods_return_status", "10");
		List<PayoffLog> objs = this.plService
				.query("select obj from ReturnGoodsLog obj where obj.refund_status=:refund_status and obj.goods_return_status=:goods_return_status",
						params, -1, -1);
		return objs.size();
	}

	public int query_grouplife_refund() {
		Map params = new HashMap();
		params.put("status", 5);
		List<GroupInfo> objs = this.groupinfoService.query(
				"select obj from GroupInfo obj where obj.status=:status",
				params, -1, -1);
		return objs.size();
	}

	public int query_self_goods() {
		Map params = new HashMap();
		params.put("goods_type", 0);
		params.put("goods_status", 0);
		List<Goods> objs = this.goodsService
				.query("select obj from Goods obj where obj.goods_type=:goods_type and obj.goods_status=:goods_status",
						params, -1, -1);
		return objs.size();
	}

	public int query_self_storage_goods() {
		Map params = new HashMap();
		params.put("goods_type", 0);
		params.put("goods_status", 1);
		List<Goods> objs = this.goodsService
				.query("select obj from Goods obj where obj.goods_type=:goods_type and obj.goods_status=:goods_status",
						params, -1, -1);
		return objs.size();
	}

	public int query_self_out_goods() {
		Map params = new HashMap();
		params.put("goods_type", 0);
		params.put("goods_status", -1);
		List<Goods> objs = this.goodsService
				.query("select obj from Goods obj where obj.goods_type=:goods_type and obj.goods_status=:goods_status",
						params, -1, -1);
		return objs.size();
	}

	public int query_self_order_shipping() {
		List<OrderForm> orders = new ArrayList<OrderForm>();
		Map params = new HashMap();
		params.put("order_status1", 20);
		params.put("order_status2", 16);
		params.put("order_form", 1);
		params.put("order_cat", 2);
		orders = this.orderFormService
				.query("select obj from OrderForm obj where (obj.order_status=:order_status1 or obj.order_status=:order_status2) and obj.order_form=:order_form and obj.order_cat!=:order_cat",
						params, -1, -1);
		return orders.size();
	}

	public int query_self_order_pay() {
		List<OrderForm> orders = new ArrayList<OrderForm>();
		Map params = new HashMap();
		params.put("order_status1", 10);
		params.put("order_status2", 16);
		params.put("order_form", 1);
		params.put("order_cat", 2);
		orders = this.orderFormService
				.query("select obj from OrderForm obj where (obj.order_status=:order_status1 or obj.order_status=:order_status2) and obj.order_cat!=:order_cat and obj.order_form=:order_form",
						params, -1, -1);
		return orders.size();
	}

	public int query_self_order_evaluate() {
		List<OrderForm> orders = new ArrayList<OrderForm>();
		Map params = new HashMap();
		params.put("order_status", 40);
		params.put("order_form", 1);
		params.put("order_cat", 2);
		orders = this.orderFormService
				.query("select obj from OrderForm obj where obj.order_status=:order_status and obj.order_cat!=:order_cat and obj.order_form=:order_form",
						params, -1, -1);
		return orders.size();
	}

	public int query_self_all_order() {
		List<OrderForm> orders = new ArrayList<OrderForm>();
		Map params = new HashMap();
		params.put("order_form", 1);
		params.put("order_cat", 2);
		orders = this.orderFormService
				.query("select obj from OrderForm obj where obj.order_form=:order_form and obj.order_cat!=:order_cat",
						params, -1, -1);
		return orders.size();
	}

	public int query_self_return_apply() {
		List<ReturnGoodsLog> objs = new ArrayList<ReturnGoodsLog>();
		Map params = new HashMap();
		params.put("goods_return_status", "5");
		params.put("goods_type", 0);
		objs = this.returngoodslogService
				.query("select obj from ReturnGoodsLog obj where obj.goods_return_status=:goods_return_status and obj.goods_type=:goods_type",
						params, -1, -1);
		return objs.size();
	}

	public int query_self_groupinfo_return_apply() {
		List<GroupInfo> objs = new ArrayList<GroupInfo>();
		Map params = new HashMap();
		params.put("status", 3);
		params.put("goods_type", 1);
		objs = this.groupinfoService
				.query("select obj from GroupInfo obj where obj.status=:status and obj.lifeGoods.goods_type=:goods_type",
						params, -1, -1);
		return objs.size();
	}

}
