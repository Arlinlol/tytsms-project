package com.iskyshop.manage.seller.action;

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

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import com.iskyshop.foundation.domain.Area;
import com.iskyshop.foundation.domain.ExpressCompany;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.OrderFormLog;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.OrderFormQueryObject;
import com.iskyshop.foundation.domain.virtual.TransInfo;
import com.iskyshop.foundation.service.IAreaService;
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
import com.iskyshop.foundation.service.IPayoffLogService;
import com.iskyshop.foundation.service.IRefundLogService;
import com.iskyshop.foundation.service.IStoreService;
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
import com.iskyshop.pay.wechatpay.util.TytsmsStringUtils;
import com.iskyshop.view.web.tools.StoreViewTools;
import com.taiyitao.logistics.httpclient.HttpclientLogisticsUtil;

/**
 * 
 * <p>
 * Title: OrderSellerAction.java
 * </p>
 * 
 * <p>
 * Description:卖家订单控制器，卖家中心订单管理所有控制器都在这里
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
 * @date 2014-5-20
 * 
 * @version iskyshop_b2b2c 1.0
 */
@Controller
public class OrderSellerAction {

	private static Log log = LogFactory.getLog(OrderSellerAction.class);
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
	private IExpressCompanyService expressCompayService;
	@Autowired
	private StoreViewTools storeViewTools;
	@Autowired
	private MsgTools msgTools;
	@Autowired
	private OrderFormTools orderFormTools;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private PaymentTools paymentTools;
	@Autowired
	private IPayoffLogService payoffservice;
	@Autowired
	private SendMsgAndEmTools sendMsgAndEmTools;
	@Autowired
	private IAreaService areaService;

	@SecurityMapping(title = "卖家订单列表", value = "/seller/order.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping("/seller/order.htm")
	public ModelAndView order(HttpServletRequest request,
			HttpServletResponse response, String currentPage,
			String order_status, String order_id, String beginTime,
			String endTime, String buyer_userName) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/seller_order.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderFormQueryObject ofqo = new OrderFormQueryObject(currentPage, mv,
				"addTime", "desc");
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		ofqo.addQuery("obj.store_id", new SysMap("store_id", user.getStore()
				.getId().toString()), "=");
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
				ofqo.addQuery("obj.order_status",
						new SysMap("order_status", 20), "=");
			}
			if (order_status.equals("order_shipping")) {// 已经发货
				ofqo.addQuery("obj.order_status",
						new SysMap("order_status", 30), "=");
			}
			if (order_status.equals("order_evaluate")) {// 等待评价
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
		mv.addObject("orderFormTools", orderFormTools);
		mv.addObject("store", user.getStore());
		return mv;
	}

	@SecurityMapping(title = "卖家订单详情", value = "/seller/order_view.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping("/seller/order_view.htm")
	public ModelAndView order_view(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/order_view.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		Store store = this.storeService.getObjById(CommUtil.null2Long(obj
				.getStore_id()));
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if (user.getStore().getId().equals(store.getId())) {
			mv.addObject("obj", obj);
			mv.addObject("orderFormTools", orderFormTools);
			mv.addObject("express_company_name", this.orderFormTools
					.queryExInfo(obj.getExpress_info(), "express_company_name"));
		} else {
			mv = new JModelAndView(
					"user/default/sellercenter/seller_error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "您店铺中没有编号为" + id + "的订单！");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "卖家取消订单", value = "/seller/order_cancel.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping("/seller/order_cancel.htm")
	public ModelAndView order_cancel(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/seller_order_cancel.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		Store store = this.storeService.getObjById(CommUtil.null2Long(obj
				.getStore_id()));
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if (user.getStore().getId().equals(store.getId())) {
			mv.addObject("obj", obj);
			mv.addObject("currentPage", currentPage);
		} else {
			mv = new JModelAndView(
					"user/default/sellercenter/seller_error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "您没有编号为" + id + "的订单！");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "卖家取消订单保存", value = "/seller/order_cancel_save.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping("/seller/order_cancel_save.htm")
	public String order_cancel_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String state_info, String other_state_info) throws Exception {
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		Store store = this.storeService.getObjById(CommUtil.null2Long(obj
				.getStore_id()));
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if (user.getStore().getId().equals(store.getId())) {
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
			map.put("seller_id", store.getUser().getId().toString());
			map.put("order_id", obj.getId());
			String json = Json.toJson(map);
			if (this.configService.getSysConfig().isEmailEnable()) {
				this.sendMsgAndEmTools.sendEmail(CommUtil.getURL(request),
						TytsmsStringUtils.generatorFilesFolderServerPath(request),
						"email_tobuyer_order_cancel_notify", buyer.getEmail(),
						json);
			}
			if (this.configService.getSysConfig().isSmsEnbale()) {
				this.sendMsgAndEmTools.sendMsg(CommUtil.getURL(request),
						TytsmsStringUtils.generatorFilesFolderServerPath(request),
						"sms_tobuyer_order_cancel_notify", buyer.getMobile(),
						json);
			}
		}
		return "redirect:order.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "卖家调整订单费用", value = "/seller/order_fee.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping("/seller/order_fee.htm")
	public ModelAndView order_fee(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/seller_order_fee.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		Store store = this.storeService.getObjById(CommUtil.null2Long(obj
				.getStore_id()));
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if (user.getStore().getId().equals(store.getId())) {
			mv.addObject("obj", obj);
			mv.addObject("currentPage", currentPage);
			if(obj.getCoupon_info() != null){
				JSONObject json = new JSONObject().fromObject(obj.getCoupon_info());
				mv.addObject("coupon_amount", json.getString("coupon_amount"));
			}
		} else {
			mv = new JModelAndView(
					"user/default/sellercenter/seller_error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "您没有编号为" + id + "的订单！");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "卖家调整订单费用保存", value = "/seller/order_fee_save.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping("/seller/order_fee_save.htm")
	public String order_fee_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String goods_amount, String ship_price, String totalPrice)
			throws Exception {
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		Store store = this.storeService.getObjById(CommUtil.null2Long(obj
				.getStore_id()));
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if (user.getStore().getId().equals(store.getId())) {
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
			map.put("seller_id", store.getUser().getId().toString());
			map.put("order_id", obj.getId());
			String json = Json.toJson(map);
			if (this.configService.getSysConfig().isEmailEnable()) {
				this.sendMsgAndEmTools.sendEmail(CommUtil.getURL(request),
						TytsmsStringUtils.generatorFilesFolderServerPath(request),
						"email_tobuyer_order_update_fee_notify",
						buyer.getEmail(), json);
			}
			if (this.configService.getSysConfig().isSmsEnbale()) {
				this.sendMsgAndEmTools
						.sendMsg(CommUtil.getURL(request),
								TytsmsStringUtils.generatorFilesFolderServerPath(request), "sms_tobuyer_order_fee_notify",
								buyer.getMobile(), json);
			}
		}
		return "redirect:order.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "卖家确认发货", value = "/seller/order_shipping.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping("/seller/order_shipping.htm")
	public ModelAndView order_shipping(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/seller_order_shipping.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		Store store = this.storeService.getObjById(CommUtil.null2Long(obj
				.getStore_id()));
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if (user.getStore().getId().equals(store.getId())) {
			mv.addObject("obj", obj);
			mv.addObject("currentPage", currentPage);
			// 当前订单中的虚拟商品
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
			//bigen cty 修改时间2015-3-12 增加内容
			User member_user = this.userService.getObjById(CommUtil.null2Long(obj.getUser_id()));
			if(member_user.getAddrs() != null){ //收货人
				Map<String,Object> areaMap = null;
				for (int i = 0; i < member_user.getAddrs().size(); i++) {
					areaMap = CommUtil.getAreaInfo(member_user.getAddrs().get(i).getArea(),1);
					if(obj.getReceiver_area().equals(areaMap.get("areaName"))){
						mv.addObject("consigneeName", areaMap.get("areaName"));
						mv.addObject("consigneeid", new Long(areaMap.get("areaId").toString()));
						areaMap = new HashMap();
						break;
					}
				}
				if(areaMap.size() > 0){
					mv.addObject("consigneeName", areaMap.get("areaName"));
					mv.addObject("consigneeid", new Long(areaMap.get("areaId").toString()));
				}
			}
			if(user.getStore() != null){//发货人
				Map<String,Object> areaMap = CommUtil.getAreaInfo(user.getStore().getArea(),1);
				if(areaMap.size()>0){
					mv.addObject("areaId", new Long(areaMap.get("areaId").toString()));
				}
			}
			List<Area> areas = this.areaService.query(
					"select obj from Area obj where obj.parent.id is null", null,
					-1, -1);
			mv.addObject("areas", areas);
			//end
			Map params = new HashMap();
			params.put("status", 0);
			List<ExpressCompany> expressCompanys = this.expressCompayService
					.query("select obj from ExpressCompany obj where obj.company_status=:status order by company_sequence asc",
							params, -1, -1);
			mv.addObject("expressCompanys", expressCompanys);
			mv.addObject("physicalGoods", physicalGoods);
			mv.addObject("deliveryGoods", deliveryGoods);
		} else {
			mv = new JModelAndView(
					"user/default/sellercenter/seller_error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "订单参数错误！");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "卖家确认发货保存", value = "/seller/order_shipping_save.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping("/seller/order_shipping_save.htm")
	@Transactional
	public String order_shipping_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String shipCode, String state_info, String order_seller_intro,
			String ec_id,String order_type,String area_id) throws Exception {
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		ExpressCompany ec = this.expressCompayService.getObjById(CommUtil
				.null2Long(ec_id));
		Store store = this.storeService.getObjById(CommUtil.null2Long(obj
				.getStore_id()));
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if (user.getStore().getId().equals(store.getId())) {
			obj.setOrder_status(30);
			obj.setShipCode(shipCode);
			obj.setShipper_type(CommUtil.null2Long(order_type));
			obj.setShipTime(new Date());
			Map json_map = new HashMap();
			json_map.put("express_company_id", ec.getId());
			json_map.put("express_company_name", ec.getCompany_name());
			json_map.put("express_company_mark", ec.getCompany_mark());
			json_map.put("express_company_type", ec.getCompany_type());
			obj.setExpress_info(Json.toJson(json_map));
			obj.setOrder_seller_intro(order_seller_intro);
			User buyer = this.userService.getObjById(CommUtil.null2Long(obj
					.getUser_id()));
			//bigen cty 修改时间：2015-03-19
			try {
				Map jsonMap = new HashMap();
				jsonMap.put("tytorderid", obj.getId()); //订单ID
				jsonMap.put("tytorderno", obj.getOrder_id()); //订单号
				jsonMap.put("tytordertyte", order_type); //订单类型
				jsonMap.put("pickexpressno", shipCode); //物流单号
				jsonMap.put("pickexpresscom", ec.getCompany_mark()); //快递公司代码
				jsonMap.put("pickexpresscorp", ec.getCompany_name()); //快递公司名称
				jsonMap.put("shipperperson", store.getStore_ower()); //发货人
				jsonMap.put("shippertell", store.getStore_telephone()); //发货人电话
				//发货人
				if(store != null){
					Map<String,Object> areaMap = CommUtil.getAreaInfo(store.getArea(),1);
					if(areaMap.size()>0){
						jsonMap.put("shipperarea",areaMap.get("areaName"));
					}
				}else{
					jsonMap.put("shipperarea", null); //发货人地址
				}
				jsonMap.put("shipperaddr", store.getStore_address()); //发货人详细地址
				jsonMap.put("consigneeperson", obj.getReceiver_Name()); //收货人
				if(!"".equals(obj.getReceiver_telephone()) && !"".equals(obj.getReceiver_mobile())){//收货人电话
					if(obj.getReceiver_telephone().equals(obj.getReceiver_mobile())){
						jsonMap.put("consigneetell", obj.getReceiver_mobile());
					}else{
						jsonMap.put("consigneetell", obj.getReceiver_telephone()+","+obj.getReceiver_mobile()); 
					}
				}else{
					if(!"".equals(obj.getReceiver_mobile())){
						jsonMap.put("consigneetell", obj.getReceiver_mobile());
					}else{
						jsonMap.put("consigneetell",obj.getReceiver_telephone());
					}
				}
				jsonMap.put("consigneearea", obj.getReceiver_area()); //收货人地址
				jsonMap.put("consigneeaddr", obj.getReceiver_area_info()); //收货人详情地址
				Area area = this.areaService.getObjById(CommUtil.null2Long(area_id));
				jsonMap.put("from", area.getAreaName()); //出发地
				if(buyer.getAddrs() != null){
					Map<String,Object> areaMap = CommUtil.getAreaInfo(buyer.getAddrs().get(0).getArea(),0);
					jsonMap.put("to", areaMap.get("areaName"));   //目的地
				}
				jsonMap.put("shipper", store.getLicense_c_name()); //发货单位
				jsonMap.put("consignee", obj.getReceiver_Name()); //收货单位
				jsonMap.put("consigneesepdemand", state_info); //收货特别要求
				JSONArray jarray = new JSONArray().fromObject(obj.getGoods_info());
				List<Map> map_list = new ArrayList<Map>();
				for (int i = 0; i < jarray.size(); i++) {
					Map jmap = new HashMap();
					JSONObject js = jarray.getJSONObject(i);
					Goods goods = this.goodsService.getObjById(new Long(js.get("goods_id").toString()));
					jmap.put("gicargoname", js.getString("goods_name")); //货名
					jmap.put("unitpiece", js.getString("goods_count"));  //件数 
					jmap.put("unitweight", goods.getGoods_weight()); //重量
					jmap.put("unitprice", js.getString("goods_price"));  //订单单价
					jmap.put("cargovalue", null); //保险费值
					map_list.add(jmap);
				}
				jsonMap.put("goodsinfo", Json.toJson(map_list)); //商品信息
				JSONObject jsonTpi = new JSONObject().fromObject(jsonMap);
				//调用接口 数据推送到TPI中
				HttpclientLogisticsUtil logistics = new HttpclientLogisticsUtil();
			    boolean isResult = logistics.pushLogisticsInfo(jsonTpi.toString());
				if(isResult){
	                obj.setPush_status(1L);
					log.info("商家发货推送Tpi信息===订单号"+obj.getOrder_id()+"===订单推送信息成功");
				}else{
					log.info("商家发货推送Tpi信息===订单号"+obj.getOrder_id()+"===订单推送信息失败");
	                obj.setPush_status(2L);
				}
			} catch (Exception e) {
				log.info(e);
			}
			//end cty 修改时间：2015-03-19
			this.orderFormService.update(obj);
			OrderFormLog ofl = new OrderFormLog();
			ofl.setAddTime(new Date());
			ofl.setLog_info("确认发货");
			ofl.setState_info(state_info);
			ofl.setLog_user(user);
			ofl.setOf(obj);
			this.orderFormLogService.save(ofl);
			Map map = new HashMap();
			map.put("buyer_id", buyer.getId().toString());
			map.put("seller_id", store.getUser().getId().toString());
			map.put("order_id", obj.getId());
			String json = Json.toJson(map);
			if (this.configService.getSysConfig().isEmailEnable()) {
				this.sendMsgAndEmTools.sendEmail(CommUtil.getURL(request),
						TytsmsStringUtils.generatorFilesFolderServerPath(request),
						"email_tobuyer_order_ship_notify", buyer.getEmail(),
						json);
			}
			if (this.configService.getSysConfig().isSmsEnbale()) {
				this.sendMsgAndEmTools.sendMsg(CommUtil.getURL(request),
						TytsmsStringUtils.generatorFilesFolderServerPath(request),
						"sms_tobuyer_order_ship_notify", buyer.getMobile(),
						json);
			}
		
			// 异步通知支付宝,该方法仅仅在支付宝担保支付情况下支持，目的是为了修改支付宝订单状态为“已经发货”
			if (obj!=null && obj.getPayment() !=null && obj.getPayment().getMark().equals("alipay")
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
		return "redirect:order.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "卖家修改物流", value = "/seller/order_shipping_code.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping("/seller/order_shipping_code.htm")
	public ModelAndView order_shipping_code(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/seller_order_shipping_code.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		Store store = this.storeService.getObjById(CommUtil.null2Long(obj
				.getStore_id()));
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Map params = new HashMap();
		params.put("status", 0);
		List<ExpressCompany> expressCompanys = this.expressCompayService
				.query("select obj from ExpressCompany obj where obj.company_status=:status order by company_sequence asc",
						params, -1, -1);
		mv.addObject("expressCompanys", expressCompanys);
		JSONObject json = new JSONObject().fromObject(obj.getExpress_info());
		mv.addObject("expressId", json.getLong("express_company_id"));
		if (user.getStore().getId().equals(store.getId())) {
			mv.addObject("obj", obj);
			mv.addObject("currentPage", currentPage);
		} else {
			mv = new JModelAndView(
					"user/default/sellercenter/seller_error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "您没有编号为" + id + "的订单！");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "卖家修改物流保存", value = "/seller/order_shipping_code_save.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping("/seller/order_shipping_code_save.htm")
	public String order_shipping_code_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String shipCode, String state_info,String ec_id) {
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		ExpressCompany ec = this.expressCompayService.getObjById(CommUtil
				.null2Long(ec_id));
		Store store = this.storeService.getObjById(CommUtil.null2Long(obj
				.getStore_id()));
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if (user.getStore().getId().equals(store.getId())) {
			obj.setShipCode(shipCode);
			Map json_map = new HashMap();
			json_map.put("express_company_id", ec.getId());
			json_map.put("express_company_name", ec.getCompany_name());
			json_map.put("express_company_mark", ec.getCompany_mark());
			json_map.put("express_company_type", ec.getCompany_type());
			obj.setExpress_info(Json.toJson(json_map));
			Map jsonMap = new HashMap();
			jsonMap.put("tytorderno", obj.getOrder_id()); //订单号
			jsonMap.put("tytordertyte", obj.getShipper_type()); //订单类型
			jsonMap.put("pickexpressno", shipCode); //物流单号
			jsonMap.put("pickexpresscom", ec.getCompany_mark()); //快递公司代码
			jsonMap.put("pickexpresscorp", ec.getCompany_name()); //快递公司名称
			JSONObject jsonTpi= new JSONObject().fromObject(jsonMap);
			HttpclientLogisticsUtil logistics = new HttpclientLogisticsUtil();
			boolean isResult = logistics.UpdateLogisticsInfo(jsonTpi.toString());
			if(isResult){
                obj.setPush_status(1L);
				log.info("商家修改推送Tpi信息===订单号"+obj.getOrder_id()+"===订单推送信息成功");
			}else{
				log.info("商家修改推送Tpi信息===订单号"+obj.getOrder_id()+"===订单推送信息失败");
                obj.setPush_status(2L);
			}
			this.orderFormService.update(obj);
			OrderFormLog ofl = new OrderFormLog();
			ofl.setAddTime(new Date());
			ofl.setLog_info("修改物流信息");
			ofl.setState_info(state_info);
			ofl.setLog_user(SecurityUserHolder.getCurrentUser());
			ofl.setOf(obj);
			this.orderFormLogService.save(ofl);
		}
		return "redirect:order.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "打印订单", value = "/seller/order_print.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping("/seller/order_print.htm")
	public ModelAndView order_print(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/order_print.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			OrderForm orderform = this.orderFormService.getObjById(CommUtil
					.null2Long(id));
			Store store = this.storeService.getObjById(CommUtil
					.null2Long(orderform.getStore_id()));
			mv.addObject("store", store);
			mv.addObject("obj", orderform);
			mv.addObject("orderFormTools", orderFormTools);
		}
		return mv;
	}

	@SecurityMapping(title = "卖家物流详情", value = "/seller/ship_view.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping("/seller/ship_view.htm")
	public ModelAndView order_ship_view(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/order_ship_view.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		Store store = this.storeService.getObjById(CommUtil.null2Long(obj
				.getStore_id()));
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if (user.getStore().getId().equals(store.getId())) {
			mv.addObject("obj", obj);
			//bigen cty 修改时间：2015-03-19
			List<TransInfo> transInfo_list = new ArrayList<TransInfo>();
			HttpclientLogisticsUtil logistics = new HttpclientLogisticsUtil();
			Map jsonMap = new HashMap();
			jsonMap.put("tytordertyte", obj.getShipper_type()); //订单类型
			jsonMap.put("pickexpressno", obj.getShipCode()); //物流单号
			jsonMap.put("pickexpresscom", this.orderFormTools.queryExInfo(obj.getExpress_info(),"express_company_mark")); //快递代码
			JSONObject json = new JSONObject().fromObject(jsonMap);
			TransInfo transInfo = logistics.searchLogisticsInfo(json.toString());
			transInfo.setExpress_company_name(this.orderFormTools
					.queryExInfo(obj.getExpress_info(),
							"express_company_name"));
			transInfo.setExpress_ship_code(obj.getShipCode());
			if(transInfo.getData().size() >0){
				transInfo.setStatus("1");
			}else{
				transInfo.setStatus("2");
				transInfo.setMessage("系统繁忙，请稍后在试!");
			}
			transInfo_list.add(transInfo);
			mv.addObject("transInfo_list", transInfo_list);
			//end cty 修改时间：2015-03-19
//			TransInfo transInfo = this.query_ship_getData(CommUtil
//					.null2String(obj.getId()));
//			mv.addObject("transInfo", transInfo);
		} else {
			mv = new JModelAndView(
					"user/default/sellercenter/seller_error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "您店铺中没有编号为" + id + "的订单！");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
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
			ec = this.expressCompayService.getObjById(CommUtil.null2Long(map
					.get("express_company_id")));
		}
		return ec;
	}
}
