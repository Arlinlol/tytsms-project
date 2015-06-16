package com.iskyshop.view.mobileWap.action;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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

import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Evaluate;
import com.iskyshop.foundation.domain.ExpressCompany;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsSpecProperty;
import com.iskyshop.foundation.domain.GroupGoods;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.OrderFormLog;
import com.iskyshop.foundation.domain.PayoffLog;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.StorePoint;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.virtual.TransContent;
import com.iskyshop.foundation.domain.virtual.TransInfo;
import com.iskyshop.foundation.service.ICouponInfoService;
import com.iskyshop.foundation.service.IEvaluateService;
import com.iskyshop.foundation.service.IExpressCompanyService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGroupGoodsService;
import com.iskyshop.foundation.service.IOrderFormLogService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IPaymentService;
import com.iskyshop.foundation.service.IPayoffLogService;
import com.iskyshop.foundation.service.IStorePointService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.OrderFormTools;
import com.iskyshop.manage.admin.tools.SendMsgAndEmTools;
import com.iskyshop.pay.wechatpay.util.ConfigContants;
import com.iskyshop.pay.wechatpay.util.TytsmsStringUtils;
import com.iskyshop.view.web.tools.IntegralViewTools;
import com.taiyitao.elasticsearch.ElasticsearchUtil;
import com.taiyitao.elasticsearch.IndexVo.IndexName;
import com.taiyitao.elasticsearch.IndexVo.IndexType;
import com.taiyitao.elasticsearch.IndexVoTools;

@Controller
public class MobileWapBuyOrderManageAction extends MobileWapBaseAction{
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IOrderFormLogService orderFormlogService;
	@Autowired
	private IntegralViewTools integralViewTools;
	@Autowired
	private IOrderFormLogService orderFormLogService;
	@Autowired
	private ICouponInfoService couponInfoService;
	@Autowired
	private OrderFormTools orderFormTools;
	@Autowired
	private SendMsgAndEmTools sendMsgAndEmTools;
	@Autowired
	private IStorePointService storePointService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGroupGoodsService ggService;
	@Autowired
	private IEvaluateService evaluateService;
	@Autowired
	private IPayoffLogService payoffLogservice;
	@Autowired
	private IExpressCompanyService expressCompayService;
	@Autowired
	private IGroupGoodsService groupGoodsService;
	@Autowired
	private IPaymentService paymentService;
	@Autowired
	private ElasticsearchUtil elasticsearchUtil;

	/**
	 * 用户订单查询，order_cat：查询订单类型，0为购物订单，1为手机充值订单，
	 * 
	 * @param request
	 * @param response
	 * @param user_id
	 * @param token
	 * @param order_cat
	 * @param beginCount
	 * @param selectCount
	 */
	@RequestMapping("/mobileWap/buyer_order.htm")
	public ModelAndView buyer_order(HttpServletRequest request,
			HttpServletResponse response, String order_status ,String token,
			String beginCount, String selectCount, String goods_choice_type) {
		ModelAndView mv = new JModelAndView("mobileWap/view/buyer/order.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
//		ModelAndView mv = new JModelAndView("mobileWap/view/buyer/address.html",
//				configService.getSysConfig(),
//				this.userConfigService.getUserConfig(), 1, request, response);
//		
		boolean verify = CommUtil.null2Boolean(request.getHeader("verify"));
		verify = true;
		Map map = new HashMap();
		
		String user_id = "" ;
		Map userMap = checkLogin(request, user_id);
		User user = null ;
	   	if(userMap.get("user_id") != null){
	   		user_id = userMap.get("user_id").toString();
	   	}
	   	if(userMap.get("user") != null){
	   		user =  (User) userMap.get("user");
	   	}
		
		if (verify && user_id != null && !user_id.equals("")) {
			user = this.userService
					.getObjById(CommUtil.null2Long(user_id));
			if (user != null) {
					int order_cat = 0;// 购物订单
					Map params = new HashMap();
					params.put("user_id", user_id);
					params.put("order_cat", order_cat);// 购物订单
					params.put("order_main", 1);// 主订单
					if(order_status != null && !"".equals(order_status)){
						params.put("order_status", Integer.parseInt(order_status));// 订单状态
					}
					String sql = "select obj from OrderForm obj where obj.user_id=:user_id and obj.order_cat=:order_cat and obj.order_main=:order_main " ;
					if(order_status != null && !"".equals(order_status)){
						sql += " and obj.order_status=:order_status " ;
					}
					sql += " order by addTime desc " ;
					List<OrderForm> orders = this.orderFormService
							.query(sql,params, CommUtil.null2Int(beginCount),CommUtil.null2Int(selectCount));
					List list = new ArrayList();
					for (OrderForm of : orders) {
						Map order_map = new HashMap();
						order_map.put("order_id", of.getId());// 订单id
						order_map.put("order_num", of.getOrder_id());// 订单号
						order_map.put("addTime", of.getAddTime());// 下单时间
						order_map.put("order_total_price", this.orderFormTools
								.query_order_price(of.getId().toString()));// 订单总金额
						order_map.put("ship_price", of.getShip_price());// 物流费用
						order_map.put("order_status",of.getOrder_status());
							//	this.query_order_status(of.getId().toString()));// 订单状态,0为订单取消，10为已提交待付款，16为货到付款，20为已付款待发货，30为已发货待收货，40为已收货
						// 50买家评价完毕 ,65订单不可评价，到达设定时间，系统自动关闭订单相互评价功能
						if (of.getPayType() != null
								&& !of.getPayType().equals("") &&of.getOrder_status() > 10) {
							order_map.put("payType", of.getPayment().getName());// 支付方式
						} else if (of.getOrder_status() < 20){
							order_map.put("payType", "未支付");// 支付方式
						}
						List<Map> goods_infos = orderFormTools
								.queryGoodsInfo(of.getGoods_info());
						List list_detail = new ArrayList();
						String goods_mainphoto_path = "";
						for (Map map_temp : goods_infos) {
							list_detail.add(CommUtil.getURL(request) + "/"
									+ map_temp.get("goods_mainphoto_path"));
							goods_mainphoto_path = CommUtil.getURL(request) + "/" + map_temp.get("goods_mainphoto_path");
							map_temp.put("good_mainphoto_path", goods_mainphoto_path);
						}
						List<Map> goods_child_infos = new ArrayList<Map>();
						List<Map> child_goods_map  =  new ArrayList<Map>();
						List<OrderForm> child_orders_map  = new ArrayList<OrderForm>();
						if (of.getChild_order_detail() != null
								&& !of.getChild_order_detail().equals("")) {
							
							goods_child_infos = orderFormTools
									.queryGoodsInfo(of.getChild_order_detail());
							for (Map map_temp : goods_child_infos) {
								OrderForm order = this.orderFormService.getObjById(Long.parseLong(map_temp.get("order_id").toString()));
								
								List<Map> order_child_goods_map  = orderFormTools
										.queryGoodsInfo(CommUtil.null2String(map_temp
												.get("order_goods_info")));
								
								for (Map map_temp2 : order_child_goods_map) {
									String child_goods_mainphoto_path = CommUtil.getURL(request)
											+ "/"
											+ map_temp2
													.get("goods_mainphoto_path"); 
									map_temp2.put("good_mainphoto_path", child_goods_mainphoto_path);
									map_temp2.put("order_id", order.getId());// 订单id
									map_temp2.put("order_status",order.getOrder_status());
									child_goods_map.add(map_temp2);
								}
								
							}
						}
						order_map.put("photo_list", list_detail);
						order_map.put("goods_infos", goods_infos);
						order_map.put("goods_child_infos", child_goods_map);
						System.out.println("order_map:" + order_map);
						list.add(order_map);
					}
					
					map.put("order_list", list);
					map.put("order_list_size", list.size());
			} else {
				verify = false;
			}
			map.put("user_id", user.getId());
			mv.addObject("user_id", user_id);
		} else {
			verify = false;
		}
		map.put("ret", CommUtil.null2String(verify));
		
		//String json = Json.toJson(map, JsonFormat.compact());
		System.out.println("map:" + map);
		mv.addObject("map", map);
		return mv;
	}

	@RequestMapping("/mobileWap/buyer_order_view.htm")
	public ModelAndView buyer_order_view(HttpServletRequest request,
			HttpServletResponse response,  String token,
			String order_id) {
		ModelAndView mv = new JModelAndView("mobileWap/view/buyer/order_view.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
		
		int code = 100;// 100请求成功，-100，用户信息错误,-200订单信息错误,
		boolean verify = CommUtil.null2Boolean(request.getHeader("verify"));
		Map json_map = new HashMap();
		verify = true ;
		
		String user_id = "";
		Map userMap = checkLogin(request, user_id);
	   	if(userMap.get("user_id") != null){
	   		user_id = userMap.get("user_id").toString();
	   	}
	   	User user = null ;
		if(userMap.get("user") != null){
	   		user =  (User) userMap.get("user");
	   	}
		
		
		if (verify && user_id != null && !user_id.equals("") ) {
			user = this.userService
					.getObjById(CommUtil.null2Long(user_id));
			if (user != null) {
					OrderForm obj = this.orderFormService.getObjById(CommUtil
							.null2Long(order_id));
					if (obj != null) {
						if (obj.getUser_id().equals(user_id)) {
							json_map.put("order_id", obj.getId());// 订单id
							json_map.put("order_num", obj.getOrder_id());// 订单号
							json_map.put("addTime", obj.getAddTime());// 下单时间
							json_map.put("order_total_price",
									this.orderFormTools
											.query_order_price(order_id));// 订单总金额
							json_map.put("goods_price", obj.getGoods_amount());// 商品金额
							json_map.put("order_status", this
									.query_order_status(obj.getId().toString()));// 订单状态,0为订单取消，10为已提交待付款，16为货到付款，20为已付款待发货，30为已发货待收货，40为已收货
							// 50买家评价完毕 ,65订单不可评价，到达设定时间，系统自动关闭订单相互评价功能
							String payType = "";
							if (obj.getPayType() != null && obj.getPayment() != null) {
								payType = obj.getPayment().getName();
							} else if (obj.getOrder_status() < 20){
								payType =  "未支付";// 支付方式
							}
							json_map.put("payType", payType);// 支付方式
							Map map_train = new HashMap();
							List trans_list = new ArrayList();
							if (obj.getExpress_info() != null
									&& !obj.getExpress_info().equals("")) {
								Map express_map = this.orderFormTools
										.queryCouponInfo(obj.getExpress_info());
								map_train
										.put("express_company", express_map
												.get("express_company_name"));// 物流公司信息
							}
							map_train.put("shipTime", obj.getShipTime());// 发货时间
							map_train.put("train_order_id", obj.getId());// 物流对应订单id
							map_train.put("shipCode", obj.getShipCode());// 物流单号
							map_train.put("expressLog", this.orderFormTools.getbuyer_order_ship(obj));// 物流信息
							trans_list.add(map_train);
							if (obj.getChild_order_detail() != null
									&& !obj.getChild_order_detail().equals("")) {
								List<Map> temp_maps = this.orderFormTools
										.queryGoodsInfo(obj
												.getChild_order_detail());
								for (Map map : temp_maps) {
									OrderForm of = this.orderFormService
											.getObjById(CommUtil.null2Long(map
													.get("order_id")));
									Map map_train2 = new HashMap();
									if (of.getExpress_info() != null
											&& !of.getExpress_info().equals("")) {
										Map express_map = this.orderFormTools
												.queryCouponInfo(of
														.getExpress_info());
										map_train2
												.put("express_company",
														express_map
																.get("express_company_name"));// 物流公司信息
									}
									map_train2
											.put("shipTime", of.getShipTime());// 发货时间
									map_train2
											.put("train_order_id", of.getId());// 物流对应订单id
									map_train2
											.put("shipCode", of.getShipCode());// 物流单号
									map_train2.put("expressLog", this.orderFormTools.getbuyer_order_ship(obj));// 物流信息
									trans_list.add(map_train2);
								}
							}
							json_map.put("trans_list", trans_list);
							json_map.put("ship_price", this.orderFormTools
									.query_ship_price(order_id));// 运费
							json_map.put("payTime", obj.getPayTime());// 付款时间
							json_map.put("confirmTime", obj.getConfirmTime());// 确认收货时间
							json_map.put("finishTime", obj.getFinishTime());// 完成时间
							json_map.put("receiver_Name",
									obj.getReceiver_Name());// 收货人姓名
							json_map.put("receiver_area",
									obj.getReceiver_area());// 收货地址
							json_map.put("receiver_area_info",
									obj.getReceiver_area_info());// 地址详情
							json_map.put("receiver_zip", obj.getReceiver_zip());// 地址邮编
							json_map.put("receiver_telephone",
									obj.getReceiver_telephone());// 收货人电话
							json_map.put("receiver_mobile",
									obj.getReceiver_mobile());// 收货人手机
							if (obj.getCoupon_info() != null
									&& !obj.getCoupon_info().equals("")) {
								Map coupon_map = this.orderFormTools
										.queryCouponInfo(obj.getCoupon_info());
								json_map.put("coupon_price",
										coupon_map.get("coupon_amount"));// 优惠券价格
							} else {
								json_map.put("coupon_price", "0.0");// 优惠券价格
							}
							String invoiceType = "普通发票";
							if (obj.getInvoiceType() == 1) {
								invoiceType = "增值税发票";
							}
							json_map.put("invoiceType", invoiceType);
							json_map.put("invoice", obj.getInvoice());// 发票信息
							List goods_list = new ArrayList();
							List<Map> temp_maps = this.orderFormTools
									.queryGoodsInfo(obj.getGoods_info());
							String url = CommUtil.getURL(request);
							for (Map goods : temp_maps) {
								Map goods_map = new HashMap();
								goods_map
										.put("goods_id", goods.get("goods_id"));
								goods_map.put("goods_name",
										goods.get("goods_name"));
								goods_map.put("goods_type",
										goods.get("goods_type"));
								goods_map.put("goods_count",
										goods.get("goods_count"));
								goods_map.put("goods_price",
										goods.get("goods_price"));
								goods_map.put("goods_gsp_val",
										goods.get("goods_gsp_val"));
								goods_map.put("goods_mainphoto_path", url + "/"
										+ goods.get("goods_mainphoto_path"));
								goods_list.add(goods_map);
							}
							if (obj.getChild_order_detail() != null
									&& !obj.getChild_order_detail().equals("")) {
								List<Map> temp_maps2 = this.orderFormTools
										.queryGoodsInfo(obj
												.getChild_order_detail());
								for (Map goods : temp_maps2) {
									String child_order_id = CommUtil
											.null2String(goods.get("order_id"));
									OrderForm child_order = this.orderFormService
											.getObjById(CommUtil
													.null2Long(child_order_id));
									List<Map> temp_maps3 = this.orderFormTools
											.queryGoodsInfo(child_order
													.getGoods_info());
									for (Map goods3 : temp_maps3) {
										Map goods_map = new HashMap();
										goods_map.put("goods_id",
												goods3.get("goods_id"));
										goods_map.put("goods_name",
												goods3.get("goods_name"));
										goods_map.put("goods_type",
												goods3.get("goods_type"));
										goods_map.put("goods_count",
												goods3.get("goods_count"));
										goods_map.put("goods_price",
												goods3.get("goods_price"));
										goods_map.put("goods_gsp_val",
												goods3.get("goods_gsp_val"));
										goods_map
												.put("goods_mainphoto_path",
														url
																+ "/"
																+ goods3.get("goods_mainphoto_path"));
										goods_list.add(goods_map);
									}
								}
							}
							json_map.put("goods_list", goods_list);
						} else {
							code = -200;
						}
					} else {
						code = -200;
				}
			} else {
				code = -100;
			}
		} else {
			code = -100;
		}
		json_map.put("code", code);
		String json = Json.toJson(json_map, JsonFormat.compact());
		System.out.println("json_map:" + json_map);
		mv.addObject("json_map", json_map);
		mv.addObject("user_id", user_id);
		return mv;
	}

	@RequestMapping("/mobileWap/buyer_order_cancel.htm")
	public void buyer_order_cancel(HttpServletRequest request,
			HttpServletResponse response, String order_id,
			String token) throws Exception {
		Map json_map = new HashMap();
		int code = 100;// 100取消订单成功，-100用户信息不正确，-200订单信息不正确，
		boolean verify = CommUtil.null2Boolean(request.getHeader("verify"));
		verify = true ;
		
		
		String user_id = "";
		Map userMap = checkLogin(request, user_id);
	   	if(userMap.get("user_id") != null){
	   		user_id = userMap.get("user_id").toString();
	   	}
	   	User user = null ;
		if(userMap.get("user") != null){
	   		user =  (User) userMap.get("user");
	   	}
		
		
		if (verify && user_id != null && !user_id.equals("")) {
			user = this.userService
					.getObjById(CommUtil.null2Long(user_id));
			if (user != null) {
					OrderForm obj = this.orderFormService.getObjById(CommUtil
							.null2Long(order_id));
					if (obj != null
							&& obj.getUser_id().equals(user.getId().toString())) {
						if (obj.getOrder_main() == 1) {
							List<Map> maps = (List<Map>) Json.fromJson(CommUtil
									.null2String(obj.getChild_order_detail()));
							if (maps != null) {
								for (Map map : maps) {
									OrderForm child_order = this.orderFormService
											.getObjById(CommUtil.null2Long(map
													.get("order_id")));
									child_order.setOrder_status(0);
									this.orderFormService.update(child_order);
								}
							}
						}
						obj.setOrder_status(0);
						this.orderFormService.update(obj);
						OrderFormLog ofl = new OrderFormLog();
						ofl.setAddTime(new Date());
						ofl.setLog_info("取消订单");
						ofl.setLog_user(user);
						ofl.setOf(obj);
						ofl.setState_info("手机端取消订单");
						this.orderFormlogService.save(ofl);
						Store store = this.storeService.getObjById(CommUtil
								.null2Long(obj.getStore_id()));
						Map map = new HashMap();
						if (store != null) {
							map.put("seller_id", store.getUser().getId()
									.toString());
						}
						map.put("order_id", obj.getId().toString());
						String json = Json.toJson(map);
						if (this.configService.getSysConfig().isEmailEnable()) {
							if (obj.getOrder_form() == 0) {
								this.sendMsgAndEmTools.sendEmail(CommUtil
										.getURL(request), TytsmsStringUtils.generatorFilesFolderServerPath(request),
										"email_toseller_order_cancel_notify",
										store.getUser().getEmail(), json);
							}
						}
						if (this.configService.getSysConfig().isSmsEnbale()) {
							if (obj.getOrder_form() == 0) {
								String url = CommUtil.getURL(request);
								String path =TytsmsStringUtils.generatorFilesFolderServerPath(request);
								this.sendMsgAndEmTools.sendMsg(url, path,
										"sms_toseller_order_cancel_notify",
										store.getUser().getMobile(), json);
							}
						}
				} else {
					code = -100;
				}
			} else {
				code = -100;
			}
		} else {
			code = -100;
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
	 * 买家确认收货，确认收货后，订单状态值改变为40，如果是预存款支付，买家冻结预存款中同等订单账户金额自动转入商家预存款，如果开启预存款分润，
	 * 则按照分润比例，买家预存款分别进入商家及平台商的账户
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/mobileWap/buyer_order_cofirm.htm")
	public void buyer_order_cofirm(HttpServletRequest request,
			HttpServletResponse response, String order_id, 
			String token) throws Exception {
		Map json_map_large = new HashMap();
		int code = 100;// 100确认收货成功，-100用户信息错误
		boolean verify = CommUtil.null2Boolean(request.getHeader("verify"));
		verify = true ;
		
		String user_id = "";
		Map userMap = checkLogin(request, user_id);
	   	if(userMap.get("user_id") != null){
	   		user_id = userMap.get("user_id").toString();
	   	}
	   	User user = null ;
		if(userMap.get("user") != null){
	   		user =  (User) userMap.get("user");
	   	}
		
		
		if (verify && user_id != null && !user_id.equals("") ) {
			user = this.userService
					.getObjById(CommUtil.null2Long(user_id));
			if (user != null) {
					OrderForm obj = this.orderFormService.getObjById(CommUtil
							.null2Long(order_id));
					if (obj != null
							&& obj.getUser_id().equals(
									CommUtil.null2String(user.getId()))) {
						obj.setOrder_status(40);
						Calendar ca = Calendar.getInstance();
						ca.add(ca.DATE, this.configService.getSysConfig()
								.getAuto_order_return());
						SimpleDateFormat bartDateFormat = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss");
						String latertime = bartDateFormat.format(ca.getTime());
						obj.setReturn_shipTime(CommUtil.formatDate(latertime));
						obj.setConfirmTime(new Date());// 设置确认收货时间
						boolean ret = this.orderFormService.update(obj);
						if (obj.getPayment().getMark().equals("payafter")) {// 如果买家支付方式为货到付款，买家确认收货时更新商品库存
							this.update_goods_inventory(obj);// 更新商品库存
						}
						if (ret) {// 订单状态更新成功，更新相关信息
							if (obj.getOrder_main() == 1
									&& !CommUtil.null2String(
											obj.getChild_order_detail())
											.equals("")) {// 更新子订单状态信息
								List<Map> maps = this.orderFormTools
										.queryGoodsInfo(obj
												.getChild_order_detail());
								for (Map map : maps) {
									OrderForm child_order = this.orderFormService
											.getObjById(CommUtil.null2Long(map
													.get("order_id")));
									child_order.setOrder_status(40);
									child_order.setReturn_shipTime(CommUtil
											.formatDate(latertime));
									child_order.setConfirmTime(new Date());// 设置确认收货时间
									this.orderFormService.update(child_order);
									if (obj.getPayment().getMark()
											.equals("payafter")) {// 如果买家支付方式为货到付款，买家确认收货，子订单商品销量增加
										List<Map> goods_map = this.orderFormTools
												.queryGoodsInfo(child_order
														.getGoods_info());
										for (Map child_map : goods_map) {
											Goods goods = this.goodsService
													.getObjById(CommUtil
															.null2Long(child_map
																	.get("goods_id")));
											goods.setGoods_salenum(goods
													.getGoods_salenum()
													+ CommUtil.null2Int(child_map
															.get("goods_count")));// 增加商品销量
											goods.setGoods_inventory(goods
													.getGoods_inventory()
													- CommUtil.null2Int(child_map
															.get("goods_count")));// 库存减少
											if (goods.getGroup_buy() == 2) {// 如果为团购商品，增加团购销量,减少团购库存
												for (GroupGoods gg : goods
														.getGroup_goods_list()) {
													if (gg.getGroup()
															.getId()
															.equals(goods
																	.getGroup()
																	.getId())) {
														gg.setGg_selled_count(CommUtil.null2Int(gg
																.getGg_selled_count()
																+ CommUtil
																		.null2Int(map
																				.get("goods_count"))));// 增加团购销量
														if (gg.getGg_count()// 减少团购库存
																- CommUtil
																		.null2Int(map
																				.get("goods_count")) > 0) {
															gg.setGg_count(gg
																	.getGg_count()
																	- CommUtil
																			.null2Int(map
																					.get("goods_count")));
														} else {
															gg.setGg_count(0);
														}
														this.ggService
																.update(gg);
													}
												}
											}
											this.goodsService.update(goods);
										}
									}
									// 向子订单商家发送提醒信息，同时生成结算日志，如果子订单为平台自营，则不发送短信和邮件,
									if (child_order.getOrder_form() == 0) {
										Store store = this.storeService
												.getObjById(CommUtil
														.null2Long(child_order
																.getStore_id()));
										Map json_map = new HashMap();
										json_map.put("seller_id", store
												.getUser().getId().toString());
										json_map.put("childorder_id",
												child_order.getId().toString());
										String json = Json.toJson(json_map);
										if (this.configService.getSysConfig()
												.isEmailEnable()) {
											if (obj.getOrder_form() == 0) {
												this.sendMsgAndEmTools
														.sendEmail(
																CommUtil.getURL(request),
																TytsmsStringUtils.generatorFilesFolderServerPath(request),
																"email_toseller_order_receive_ok_notify",
																store.getUser()
																		.getEmail(),
																json);
											}
										}
										if (this.configService.getSysConfig()
												.isSmsEnbale()) {
											if (obj.getOrder_form() == 0) {
												String url = CommUtil
														.getURL(request);
												String path = TytsmsStringUtils.generatorFilesFolderServerPath(request);
												this.sendMsgAndEmTools
														.sendMsg(
																url,
																path,
																"sms_toseller_order_receive_ok_notify",
																store.getUser()
																		.getEmail(),
																json);
											}
										}
										// 订单生成商家结算日志
										PayoffLog plog = new PayoffLog();
										plog.setPl_sn("pl"
												+ CommUtil.formatTime(
														"yyyyMMddHHmmss",
														new Date())
												+ store.getUser().getId());
										plog.setPl_info("确认收货");
										plog.setAddTime(new Date());
										plog.setSeller(store.getUser());
										plog.setO_id(CommUtil
												.null2String(child_order
														.getId()));
										plog.setOrder_id(child_order
												.getOrder_id().toString());
										plog.setCommission_amount(child_order
												.getCommission_amount());// 该订单总佣金费用
										plog.setGoods_info(child_order
												.getGoods_info());
										plog.setOrder_total_price(child_order
												.getGoods_amount());// 该订单总商品金额
										plog.setTotal_amount(BigDecimal.valueOf(CommUtil.subtract(
												child_order.getGoods_amount(),
												child_order
														.getCommission_amount())));// 该订单应结算金额：结算金额=订单总商品金额-总佣金费用
										this.payoffLogservice.save(plog);
										store.setStore_sale_amount(BigDecimal.valueOf(CommUtil.add(
												child_order.getGoods_amount(),
												store.getStore_sale_amount())));// 店铺本次结算总销售金额
										store.setStore_commission_amount(BigDecimal.valueOf(CommUtil.add(
												child_order
														.getCommission_amount(),
												store.getStore_commission_amount())));// 店铺本次结算总佣金
										store.setStore_payoff_amount(BigDecimal.valueOf(CommUtil.add(
												plog.getTotal_amount(),
												store.getStore_payoff_amount())));// 店铺本次结算总佣金
										this.storeService.update(store);
										// 增加系统总销售金额、总佣金
										SysConfig sc = this.configService
												.getSysConfig();
										sc.setPayoff_all_sale(BigDecimal.valueOf(CommUtil
												.add(child_order
														.getGoods_amount(), sc
														.getPayoff_all_sale())));
										sc.setPayoff_all_commission(BigDecimal.valueOf(CommUtil.add(
												child_order
														.getCommission_amount(),
												sc.getPayoff_all_commission())));
										this.configService.update(sc);
									}
								}
							}
							OrderFormLog ofl = new OrderFormLog();
							ofl.setAddTime(new Date());
							ofl.setLog_info("确认收货");
							ofl.setLog_user(user);
							ofl.setOf(obj);
							this.orderFormlogService.save(ofl);
							// 主订单生成商家结算日志
							if (obj.getOrder_form() == 0) {
								Store store = this.storeService
										.getObjById(CommUtil.null2Long(obj
												.getStore_id()));
								PayoffLog plog = new PayoffLog();
								plog.setPl_sn("pl"
										+ CommUtil.formatTime("yyyyMMddHHmmss",
												new Date())
										+ store.getUser().getId());
								plog.setPl_info("确认收货");
								plog.setAddTime(new Date());
								plog.setSeller(store.getUser());
								plog.setO_id(CommUtil.null2String(obj.getId()));
								plog.setOrder_id(obj.getOrder_id().toString());
								plog.setCommission_amount(obj
										.getCommission_amount());// 该订单总佣金费用
								plog.setGoods_info(obj.getGoods_info());
								plog.setOrder_total_price(obj.getGoods_amount());// 该订单总商品金额
								plog.setTotal_amount(BigDecimal
										.valueOf(CommUtil.subtract(
												obj.getGoods_amount(),
												obj.getCommission_amount())));// 该订单应结算金额：结算金额=订单总商品金额-总佣金费用
								this.payoffLogservice.save(plog);
								store.setStore_sale_amount(BigDecimal
										.valueOf(CommUtil.add(
												obj.getGoods_amount(),
												store.getStore_sale_amount())));// 店铺本次结算总销售金额
								store.setStore_commission_amount(BigDecimal
										.valueOf(CommUtil.add(obj
												.getCommission_amount(), store
												.getStore_commission_amount())));// 店铺本次结算总佣金
								store.setStore_payoff_amount(BigDecimal
										.valueOf(CommUtil.add(
												plog.getTotal_amount(),
												store.getStore_payoff_amount())));// 店铺本次结算总佣金
								this.storeService.update(store);
								// 增加系统总销售金额、总佣金
								SysConfig sc = this.configService
										.getSysConfig();
								sc.setPayoff_all_sale(BigDecimal
										.valueOf(CommUtil.add(
												obj.getGoods_amount(),
												sc.getPayoff_all_sale())));
								sc.setPayoff_all_commission(BigDecimal
										.valueOf(CommUtil.add(
												obj.getCommission_amount(),
												sc.getPayoff_all_commission())));
								this.configService.update(sc);
							}
						}
					} else {
						code = -100;
					}
			} else {
				code = -100;
			}
		} else {
			code = -100;
		}
		json_map_large.put("code", code);
		String json = Json.toJson(json_map_large, JsonFormat.compact());
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

	@RequestMapping("/mobileWap/buyer_order_evaluate_query.htm")
	public void buyer_order_evaluate_query(HttpServletRequest request,
			HttpServletResponse response, String order_id,
			String token) {
		Map json_map = new HashMap();
		int code = 100;// -200用户信息错误，
		List goods_list = new ArrayList();
		boolean verify = CommUtil.null2Boolean(request.getHeader("verify"));
		
		String user_id = "";
		Map userMap = checkLogin(request, user_id);
	   	if(userMap.get("user_id") != null){
	   		user_id = userMap.get("user_id").toString();
	   	}
	   	User user = null ;
		if(userMap.get("user") != null){
	   		user =  (User) userMap.get("user");
	   	}
		
		if (verify && user_id != null && !user_id.equals("") && token != null
				&& !token.equals("")) {
			user = this.userService
					.getObjById(CommUtil.null2Long(user_id));
			if (user != null) {
				if (user.getApp_login_token().equals(token.toLowerCase())) {
					OrderForm obj = this.orderFormService.getObjById(CommUtil
							.null2Long(order_id));
					List<Map> temp_maps = this.orderFormTools
							.queryGoodsInfo(obj.getGoods_info());
					String url = CommUtil.getURL(request);
					for (Map goods : temp_maps) {
						Map goods_map = new HashMap();
						goods_map.put("goods_id", goods.get("goods_id"));
						goods_map.put("goods_name", goods.get("goods_name"));
						goods_map.put("goods_type", goods.get("goods_type"));
						goods_map.put("goods_count", goods.get("goods_count"));
						goods_map.put("goods_price", goods.get("goods_price"));
						goods_map.put("goods_gsp_val",
								goods.get("goods_gsp_val"));
						goods_map.put("goods_mainphoto_path",
								url + "/" + goods.get("goods_mainphoto_path"));
						goods_list.add(goods_map);
					}
					if (obj.getChild_order_detail() != null
							&& !obj.getChild_order_detail().equals("")) {
						List<Map> temp_maps2 = this.orderFormTools
								.queryGoodsInfo(obj.getChild_order_detail());
						for (Map goods : temp_maps2) {
							Map goods_map = new HashMap();
							String child_order_id = CommUtil.null2String(goods
									.get("order_id"));
							OrderForm child_order = this.orderFormService
									.getObjById(CommUtil
											.null2Long(child_order_id));
							List<Map> temp_maps3 = this.orderFormTools
									.queryGoodsInfo(child_order.getGoods_info());
							for (Map goods3 : temp_maps3) {
								goods_map.put("goods_id",
										goods3.get("goods_id"));
								goods_map.put("goods_name",
										goods3.get("goods_name"));
								goods_map.put("goods_type",
										goods3.get("goods_type"));
								goods_map.put("goods_count",
										goods3.get("goods_count"));
								goods_map.put("goods_price",
										goods3.get("goods_price"));
								goods_map.put("goods_gsp_val",
										goods3.get("goods_gsp_val"));
								goods_map.put("goods_mainphoto_path", url + "/"
										+ goods.get("goods_mainphoto_path"));
								goods_list.add(goods_map);
							}
						}
					}
					json_map.put("goods_list", goods_list);

				} else {
					code = -200;
				}
			} else {
				code = -200;
			}
		} else {
			code = -200;
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

	@RequestMapping("/mobileWap/buyer_order_evaluate.htm")
	public void buyer_order_evaluate(HttpServletRequest request,
			HttpServletResponse response, String order_id,
			String token) throws Exception {
		Map json_map_large = new HashMap();
		int code = 100;// -200用户信息错误，-300订单已经评价
		
		String user_id = "";
		Map userMap = checkLogin(request, user_id);
	   	if(userMap.get("user_id") != null){
	   		user_id = userMap.get("user_id").toString();
	   	}
	   	User user = null ;
		if(userMap.get("user") != null){
	   		user =  (User) userMap.get("user");
	   	}
		
		boolean verify = CommUtil.null2Boolean(request.getHeader("verify"));
		if (verify && user_id != null && !user_id.equals("") && token != null
				&& !token.equals("")) {
			user = this.userService
					.getObjById(CommUtil.null2Long(user_id));
			if (user != null) {
				if (user.getApp_login_token().equals(token.toLowerCase())) {

					OrderForm obj = this.orderFormService.getObjById(CommUtil
							.null2Long(order_id));
					if (obj.getOrder_status() == 40) {
						obj.setOrder_status(50);
						this.orderFormService.update(obj);
						OrderFormLog ofl = new OrderFormLog();
						ofl.setAddTime(new Date());
						ofl.setLog_info("评价订单");
						ofl.setLog_user(user);
						ofl.setOf(obj);
						this.orderFormLogService.save(ofl);
						List<Map> json = this.orderFormTools.queryGoodsInfo(obj
								.getGoods_info());
						for (Map map : json) {
							map.put("orderForm", obj.getId());
						}
						List<Map> child_order = this.orderFormTools
								.queryGoodsInfo(obj.getChild_order_detail());
						List<Map> child_goods = new ArrayList<Map>();
						for (Map c : child_order) {
							List<Map> maps = this.orderFormTools
									.queryGoodsInfo(c.get("order_goods_info")
											.toString());
							for (Map cmap : maps) {
								cmap.put("orderForm", c.get("order_id"));
							}
							child_goods.addAll(maps);
						}
						if (child_goods.size() > 0) {
							json.addAll(child_goods);
						}
						for (Map map : json) {
							Evaluate eva = new Evaluate();
							Goods goods = this.goodsService.getObjById(CommUtil
									.null2Long(map.get("goods_id")));
							eva.setAddTime(new Date());
							eva.setEvaluate_goods(goods);
							eva.setGoods_num(CommUtil.null2Int(map
									.get("goods_count")));
							eva.setGoods_price(map.get("goods_price")
									.toString());
							eva.setGoods_spec(map.get("goods_gsp_val")
									.toString());
							eva.setEvaluate_info(request
									.getParameter("evaluate_info_"
											+ goods.getId()));
							eva.setEvaluate_buyer_val(CommUtil.null2Int(request
									.getParameter("evaluate_buyer_val"
											+ goods.getId())));
							eva.setDescription_evaluate(BigDecimal.valueOf(CommUtil.null2Double(request
									.getParameter("description_evaluate"
											+ goods.getId()))));
							eva.setService_evaluate(BigDecimal.valueOf(CommUtil
									.null2Double(request
											.getParameter("service_evaluate"
													+ goods.getId()))));
							eva.setShip_evaluate(BigDecimal.valueOf(CommUtil
									.null2Double(request
											.getParameter("ship_evaluate"
													+ goods.getId()))));
							eva.setEvaluate_type("goods");
							eva.setEvaluate_user(user);
							eva.setOf(this.orderFormService.getObjById(CommUtil
									.null2Long(map.get("orderForm"))));
							this.evaluateService.save(eva);
							Map params = new HashMap();
							if (goods.getGoods_type() == 1) {
								Store store = this.storeService
										.getObjById(CommUtil.null2Long(goods
												.getGoods_store().getId()));
								params.put("store_id", store.getId().toString());
								List<Evaluate> evas = this.evaluateService
										.query("select obj from Evaluate obj where obj.of.store_id=:store_id",
												params, -1, -1);
								double store_evaluate1 = 0;
								double store_evaluate1_total = 0;
								double description_evaluate = 0;
								double description_evaluate_total = 0;
								double service_evaluate = 0;
								double service_evaluate_total = 0;
								double ship_evaluate = 0;
								double ship_evaluate_total = 0;
								DecimalFormat df = new DecimalFormat("0.0");
								for (Evaluate eva1 : evas) {
									store_evaluate1_total = store_evaluate1_total
											+ eva1.getEvaluate_buyer_val();
									description_evaluate_total = description_evaluate_total
											+ CommUtil.null2Double(eva1
													.getDescription_evaluate());
									service_evaluate_total = service_evaluate_total
											+ CommUtil.null2Double(eva1
													.getService_evaluate());
									ship_evaluate_total = ship_evaluate_total
											+ CommUtil.null2Double(eva1
													.getShip_evaluate());
								}
								store_evaluate1 = CommUtil.null2Double(df
										.format(store_evaluate1_total
												/ evas.size()));
								description_evaluate = CommUtil.null2Double(df
										.format(description_evaluate_total
												/ evas.size()));
								service_evaluate = CommUtil.null2Double(df
										.format(service_evaluate_total
												/ evas.size()));
								ship_evaluate = CommUtil.null2Double(df
										.format(ship_evaluate_total
												/ evas.size()));
								store.setStore_credit(store.getStore_credit()
										+ eva.getEvaluate_buyer_val());
								this.storeService.update(store);
								params.clear();
								params.put("store_id", store.getId());
								List<StorePoint> sps = this.storePointService
										.query("select obj from StorePoint obj where obj.store.id=:store_id",
												params, -1, -1);
								StorePoint point = null;
								if (sps.size() > 0) {
									point = sps.get(0);
								} else {
									point = new StorePoint();
								}
								point.setAddTime(new Date());
								point.setStore(store);
								point.setDescription_evaluate(BigDecimal
										.valueOf(description_evaluate));
								point.setService_evaluate(BigDecimal
										.valueOf(service_evaluate));
								point.setShip_evaluate(BigDecimal
										.valueOf(ship_evaluate));
								point.setStore_evaluate1(BigDecimal
										.valueOf(store_evaluate1));
								if (sps.size() > 0) {
									this.storePointService.update(point);
								} else {
									this.storePointService.save(point);
								}
							} else {
								User sp_user = this.userService.getObjById(obj
										.getEva_user_id());
								params.put("user_id", user_id);
								List<Evaluate> evas = this.evaluateService
										.query("select obj from Evaluate obj where obj.of.user_id=:user_id",
												params, -1, -1);
								double user_evaluate1 = 0;
								double user_evaluate1_total = 0;
								double description_evaluate = 0;
								double description_evaluate_total = 0;
								double service_evaluate = 0;
								double service_evaluate_total = 0;
								double ship_evaluate = 0;
								double ship_evaluate_total = 0;
								DecimalFormat df = new DecimalFormat("0.0");
								for (Evaluate eva1 : evas) {
									user_evaluate1_total = user_evaluate1_total
											+ eva1.getEvaluate_buyer_val();
									description_evaluate_total = description_evaluate_total
											+ CommUtil.null2Double(eva1
													.getDescription_evaluate());
									service_evaluate_total = service_evaluate_total
											+ CommUtil.null2Double(eva1
													.getService_evaluate());
									ship_evaluate_total = ship_evaluate_total
											+ CommUtil.null2Double(eva1
													.getShip_evaluate());
								}
								user_evaluate1 = CommUtil.null2Double(df
										.format(user_evaluate1_total
												/ evas.size()));
								description_evaluate = CommUtil.null2Double(df
										.format(description_evaluate_total
												/ evas.size()));
								service_evaluate = CommUtil.null2Double(df
										.format(service_evaluate_total
												/ evas.size()));
								ship_evaluate = CommUtil.null2Double(df
										.format(ship_evaluate_total
												/ evas.size()));
								params.clear();
								params.put("user_id", obj.getEva_user_id());
								List<StorePoint> sps = this.storePointService
										.query("select obj from StorePoint obj where obj.user.id=:user_id",
												params, -1, -1);
								StorePoint point = null;
								if (sps.size() > 0) {
									point = sps.get(0);
								} else {
									point = new StorePoint();
								}
								point.setAddTime(new Date());
								point.setUser(sp_user);
								point.setDescription_evaluate(BigDecimal
										.valueOf(description_evaluate));
								point.setService_evaluate(BigDecimal
										.valueOf(service_evaluate));
								point.setShip_evaluate(BigDecimal
										.valueOf(ship_evaluate));
								point.setStore_evaluate1(BigDecimal
										.valueOf(user_evaluate1));
								if (sps.size() > 0) {
									this.storePointService.update(point);
								} else {
									this.storePointService.save(point);
								}
							}
							// 增加用户积分和消费金额
							user.setIntegral(user.getIntegral()
									+ this.configService.getSysConfig()
											.getIndentComment());
							user.setUser_goods_fee(BigDecimal.valueOf(CommUtil
									.add(user.getUser_goods_fee(),
											obj.getTotalPrice())));
							this.userService.update(user);
						}
						if (this.configService.getSysConfig().isEmailEnable()) {
							if (obj.getOrder_form() == 0) {
								Store store = this.storeService
										.getObjById(CommUtil.null2Long(obj
												.getStore_id()));
								Map map = new HashMap();
								map.put("seller_id", store.getUser().getId()
										.toString());
								map.put("order_id", obj.getId().toString());
								String json2 = Json.toJson(map);
								this.sendMsgAndEmTools.sendEmail(CommUtil
										.getURL(request), TytsmsStringUtils.generatorFilesFolderServerPath(request),
										"email_toseller_evaluate_ok_notify",
										store.getUser().getEmail(), json2);
							}
						}
					} else {
						code = -300;
					}
				} else {
					code = -200;
				}
			} else {
				code = -200;
			}
		} else {
			code = -200;
		}
		json_map_large.put("code", code);
		String json = Json.toJson(json_map_large, JsonFormat.compact());
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

	@RequestMapping("/mobileWap/buyer_order_ship.htm")
	public void buyer_order_ship(HttpServletRequest request,
			HttpServletResponse response, String order_id) {
		Map json_map = new HashMap();
		List json_list = new ArrayList();
		int code = 100;// 100成功，-100，用户信息错误，-200，订单信息错误，-500参数错误
		OrderForm order = this.orderFormService.getObjById(CommUtil
				.null2Long(order_id));
		List<TransInfo> transInfo_list = new ArrayList<TransInfo>();
		TransInfo transInfo = this.getShipData(order_id);
		transInfo.setExpress_company_name(this.orderFormTools.queryExInfo(
				order.getExpress_info(), "express_company_name"));
		transInfo.setExpress_ship_code(order.getShipCode());
		transInfo_list.add(transInfo);
		for (TransInfo transinfo : transInfo_list) {
			Map map = new HashMap();
			map.put("message", transinfo.getMessage());
			map.put("status", transinfo.getStatus());
			List list = new ArrayList();
			for (TransContent con : transinfo.getData()) {
				Map map_con = new HashMap();
				map_con.put("content", con.getContext());
				map_con.put("time", con.getTime());
				list.add(map_con);
			}
			map.put("content", list);
			json_list.add(map);
		}
		json_map.put("code", code);
		json_map.put("json_list", json_list);
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

	private TransInfo getShipData(String id) {
		TransInfo info = null;
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		if (obj != null && !CommUtil.null2String(obj.getShipCode()).equals("")) {
			info = new TransInfo();
			try {
				ExpressCompany ec = this.queryExpressCompany(obj
						.getExpress_info());
				String query_url = "http://api.kuaidi100.com/api?id="
						+ this.configService.getSysConfig().getKuaidi_id()
						+ "&com=" + (ec != null ? ec.getCompany_mark() : "")
						+ "&nu=" + obj.getShipCode()
						+ "&show=0&muti=1&order=asc";
				URL url = new URL(query_url);
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

	/**
	 * 查询订单的状态，用在买家中心的订单列表中，多商家复合订单中只有全部商家都已经发货，卖家中心才会出现确认收货按钮
	 * 
	 * @param order_id
	 * @return
	 */
	public int query_order_status(String order_id) {
		int order_status = 0;
		OrderForm order = this.orderFormService.getObjById(CommUtil
				.null2Long(order_id));
		if (order != null) {
			order_status = order.getOrder_status();
			if (order.getOrder_main() == 1
					&& !CommUtil.null2String(order.getChild_order_detail())
							.equals("")) {
				List<Map> maps = this.orderFormTools.queryGoodsInfo(order
						.getChild_order_detail());
				for (Map child_map : maps) {
					OrderForm child_order = this.orderFormService
							.getObjById(CommUtil.null2Long(child_map
									.get("order_id")));
					if (child_order.getOrder_status() < 30) {
						order_status = child_order.getOrder_status();
					}
				}
			}
		}
		return order_status;
	}
}
