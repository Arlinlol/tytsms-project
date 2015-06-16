package com.iskyshop.manage.buyer.action;

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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import com.iskyshop.core.tools.DateUtils;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.Area;
import com.iskyshop.foundation.domain.Evaluate;
import com.iskyshop.foundation.domain.ExpressCompany;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsSpecProperty;
import com.iskyshop.foundation.domain.GroupGoods;
import com.iskyshop.foundation.domain.GroupInfo;
import com.iskyshop.foundation.domain.IntegralLog;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.OrderFormLog;
import com.iskyshop.foundation.domain.PayoffLog;
import com.iskyshop.foundation.domain.ReturnGoodsLog;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.StorePoint;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.GroupInfoQueryObject;
import com.iskyshop.foundation.domain.query.OrderFormQueryObject;
import com.iskyshop.foundation.domain.query.ReturnGoodsLogQueryObject;
import com.iskyshop.foundation.domain.virtual.TransInfo;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IAreaService;
import com.iskyshop.foundation.service.IEvaluateService;
import com.iskyshop.foundation.service.IExpressCompanyService;
import com.iskyshop.foundation.service.IGoodsCartService;
import com.iskyshop.foundation.service.IGoodsReturnService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGroupGoodsService;
import com.iskyshop.foundation.service.IGroupInfoService;
import com.iskyshop.foundation.service.IIntegralLogService;
import com.iskyshop.foundation.service.IOrderFormLogService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IPaymentService;
import com.iskyshop.foundation.service.IPayoffLogService;
import com.iskyshop.foundation.service.IPredepositLogService;
import com.iskyshop.foundation.service.IReturnGoodsLogService;
import com.iskyshop.foundation.service.IStorePointService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.ITemplateService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.MsgTools;
import com.iskyshop.manage.admin.tools.OrderFormTools;
import com.iskyshop.manage.admin.tools.SendMsgAndEmTools;
import com.iskyshop.manage.buyer.Tools.ShipTools;
import com.iskyshop.pay.wechatpay.util.ConfigContants;
import com.iskyshop.pay.wechatpay.util.TytsmsStringUtils;
import com.taiyitao.elasticsearch.ElasticsearchUtil;
import com.taiyitao.elasticsearch.IndexVo.IndexName;
import com.taiyitao.elasticsearch.IndexVo.IndexType;
import com.taiyitao.elasticsearch.IndexVoTools;
import com.taiyitao.logistics.httpclient.HttpclientLogisticsUtil;

/**
 * 
 * <p>
 * Title: OrderBuyerAction.java
 * </p>
 * 
 * <p>
 * Description: 买家订单控制类，用来操作取消订单、查看订单、订单付款、物流查询、订单评价等操作；
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
 * @date 2014-5-19
 * 
 * @version iskyshop_b2b2c 1.0
 */
@Controller
public class OrderBuyerAction {
	private static Log log = LogFactory.getLog(OrderBuyerAction.class);
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IOrderFormLogService orderFormLogService;
	@Autowired
	private IEvaluateService evaluateService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private ITemplateService templateService;
	@Autowired
	private IStorePointService storePointService;
	@Autowired
	private IPredepositLogService predepositLogService;
	@Autowired
	private IPaymentService paymentService;
	@Autowired
	private IGoodsCartService goodsCartService;
	@Autowired
	private IGroupInfoService groupinfoService;
	@Autowired
	private IGoodsReturnService goodsReturnService;
	@Autowired
	private IExpressCompanyService expressCompayService;
	@Autowired
	private IGroupGoodsService ggService;
	@Autowired
	private OrderFormTools orderFormTools;
	@Autowired
	private IPayoffLogService payoffLogservice;
	@Autowired
	private MsgTools msgTools;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IReturnGoodsLogService returnGoodsLogService;
	@Autowired
	private ShipTools shipTools;
	@Autowired
	private IGroupGoodsService groupGoodsService;
	@Autowired
	private SendMsgAndEmTools sendMsgAndEmTools;
	@Autowired
	private IIntegralLogService integralLogService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private ElasticsearchUtil elasticsearchUtil;
	

	@SecurityMapping(title = "买家订单列表", value = "/buyer/order.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/order.htm")
	public ModelAndView order(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String order_id,
			String beginTime, String endTime, String order_status) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/buyer_order.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderFormQueryObject ofqo = new OrderFormQueryObject(currentPage, mv,
				"addTime", "desc");
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		ofqo.addQuery("obj.user_id", new SysMap("user_id", SecurityUserHolder
				.getCurrentUser().getId().toString()), "=");
		ofqo.addQuery("obj.order_main", new SysMap("order_main", 1), "=");// 只显示主订单,通过主订单完成子订单的加载
		ofqo.addQuery("obj.order_cat", new SysMap("order_cat", 2), "!=");
		if (!CommUtil.null2String(order_id).equals("")) {
			ofqo.addQuery("obj.order_id", new SysMap("order_id", "%" + order_id
					+ "%"), "like");
			mv.addObject("order_id", order_id);
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
		if (!CommUtil.null2String(order_status).equals("")) {
			if (order_status.equals("order_submit")) {// 已经提交
				ofqo.addQuery("obj.order_status",
						new SysMap("order_status", 10), "=");
			}
			if (order_status.equals("order_pay")) {// 已经付款
				ofqo.addQuery("obj.order_status",
						new SysMap("order_status", 20), "=");
			}
			if (order_status.equals("order_shipping")) {// 已经发货
				ofqo.addQuery("obj.order_status",
						new SysMap("order_status", 30), "=");
			}
			if (order_status.equals("order_receive")) {// 已经收货
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
		mv.addObject("orderFormTools", orderFormTools);
		mv.addObject("order_status", order_status);
		IPageList pList = this.orderFormService.list(ofqo);
		List<OrderForm> orderForms = pList.getResult();
		
		//商品修改主图后，订单主图无法显示
		orderFormService.changPhotoByJson(orderForms);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		
		List<Object> result =dataProcess(pList.getResult());
		mv.addObject("data",result);
		// 查询订单信息
		int[] status = new int[] { 10, 30, 50 }; // 已提交 已发货 已完成
		String[] string_status = new String[] { "order_submit",
				"order_shipping", "order_finish" };
		Map orders_status = new LinkedHashMap();
		BigDecimal totleAmount = new BigDecimal(0);	
		for (int i = 0; i < status.length; i++) {
			int size = this.orderFormService.query(
					"select obj.id,obj.totalPrice from OrderForm obj where obj.user_id="
							+ user.getId().toString()
							+ " and obj.order_status =" + status[i] + "", null,
					-1, -1).size();
			mv.addObject("order_size_" + status[i], size);
			orders_status.put(string_status[i],size);
		}
		//统计销售总额
		List list = this.orderFormService.query(
				"select sum(obj.totalPrice) from OrderForm obj where obj.user_id="
						+ user.getId().toString()
						+ " and obj.order_status >=40", null,-1, -1);
		BigDecimal amountTotle = BigDecimal.ZERO;
		if(list!=null && list.size()>0 && list.get(0)!=null){
			 amountTotle = (BigDecimal)list.get(0);
		}
		mv.addObject("amountTotle",amountTotle);
		mv.addObject("orders_status", orders_status);
		mv.addObject("orderFormTools", this.orderFormTools);
		// 猜您喜欢 根据cookie商品的分类 销量查询 如果没有cookie则按销量查询
		List<Goods> your_like_goods = new ArrayList<Goods>();
		Long your_like_GoodsClass = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("goodscookie")) {
					String[] like_gcid = cookie.getValue().split(",", 2);
					Goods goods = this.goodsService.getObjById(CommUtil
							.null2Long(like_gcid[0]));
					if (goods == null)
						break;
					your_like_GoodsClass = goods.getGc().getId();
					your_like_goods = this.goodsService.query(
							"select obj from Goods obj where obj.goods_status=0 and obj.gc.id = "
									+ your_like_GoodsClass
									+ " and obj.id is not " + goods.getId()
									+ " order by obj.goods_salenum desc", null,
							0, 20);
					int gcs_size = your_like_goods.size();
					if (gcs_size < 20) {
						List<Goods> like_goods = this.goodsService.query(
								"select obj from Goods obj where obj.goods_status=0 and obj.id is not "
										+ goods.getId()
										+ " order by obj.goods_salenum desc",
								null, 0, 20 - gcs_size);
						for (int i = 0; i < like_goods.size(); i++) {
							// 去除重复商品
							int k = 0;
							for (int j = 0; j < your_like_goods.size(); j++) {
								if (like_goods.get(i).getId()
										.equals(your_like_goods.get(j).getId())) {
									k++;
								}
							}
							if (k == 0) {
								your_like_goods.add(like_goods.get(i));
							}
						}
					}
					break;
				} else {
					your_like_goods = this.goodsService
							.query("select obj from Goods obj where obj.goods_status=0 order by obj.goods_salenum desc",
									null, 0, 20);
				}
			}
		} else {
			your_like_goods = this.goodsService
					.query("select obj from Goods obj where obj.goods_status=0 order by obj.goods_salenum desc",
							null, 0, 20);
		}
		mv.addObject("your_like_goods", your_like_goods);
		return mv;
	}
	
	
	private List<Object> dataProcess(List<OrderForm> resultList){
		List<Object> result = new ArrayList<Object>();
		if(resultList!=null){
			for(OrderForm form:resultList){
				BigDecimal totleAmount = BigDecimal.ZERO;
				BigDecimal totleShipPrice = BigDecimal.ZERO;
				List<Object> list = new ArrayList<Object>();
				//主订单信息
				Map<String,Object> map = new HashMap<String, Object>();
				map.put("id", form.getId());
				map.put("order_id", form.getOrder_id());
				map.put("addTime", form.getAddTime());
				map.put("order_status", form.getOrder_status());
				map.put("ship_price", form.getShip_price());
				map.put("order_cat", form.getOrder_cat());
				if(form.getPayment()!=null){
					map.put("mark", form.getPayment().getMark());
				}
				
				//店铺订单信息
				Map<String,Object> mainstorinfo = new HashMap<String, Object>();
				mainstorinfo.put("receiver_Name", form.getReceiver_Name());
				mainstorinfo.put("id", form.getId());
				mainstorinfo.put("totalPrice", form.getTotalPrice());
				mainstorinfo.put("goods_amount", form.getGoods_amount());
				mainstorinfo.put("shipCode", form.getShipCode());
				mainstorinfo.put("order_id", form.getOrder_id());
				mainstorinfo.put("order_cat", form.getOrder_cat());
				mainstorinfo.put("ship_price", form.getShip_price());
				mainstorinfo.put("order_status", form.getOrder_status());
				
				totleAmount = totleAmount.add(form.getTotalPrice());
				totleShipPrice = totleShipPrice.add(form.getShip_price());
				
				//遍历主订单商品信息
				List<Map> maps = this.orderFormTools.queryGoodsInfo(form.getGoods_info());
				Map<String,Object> temmap = new HashMap<String, Object>();
				List<Object> temList = new ArrayList<Object>();
				for(Map tem :maps){
					temmap = new HashMap<String, Object>();
					temmap.put("goods_mainphoto_path", tem.get("goods_mainphoto_path"));
					temmap.put("goods_domainPath", tem.get("goods_domainPath"));
					temmap.put("goods_name", tem.get("goods_name"));
					temList.add(temmap);
				}
				mainstorinfo.put("goods_info", temList);
				list.add(mainstorinfo);
				
				//遍历子订单
				List<Map> maps2 = this.orderFormTools.queryGoodsInfo(form.getChild_order_detail());
				for(Map tem:maps2){
					OrderForm orderForm = this.orderFormService.getObjById(Long.parseLong((tem.get("order_id")).toString()));
					Map<String,Object> childstorInfo = new HashMap<String, Object>();
					childstorInfo.put("receiver_Name", form.getReceiver_Name());
					childstorInfo.put("totalPrice", orderForm.getTotalPrice());
					childstorInfo.put("id", orderForm.getId());
					childstorInfo.put("shipCode", orderForm.getShipCode());
					childstorInfo.put("goods_amount", orderForm.getGoods_amount());
					childstorInfo.put("order_id", orderForm.getOrder_id());
					childstorInfo.put("order_cat", orderForm.getOrder_cat());
					childstorInfo.put("ship_price", orderForm.getShip_price());
					childstorInfo.put("order_status", orderForm.getOrder_status());
					//子订单上商品信息
					List<Map> maps_child_order_info = this.orderFormTools.queryGoodsInfo(orderForm.getGoods_info());
					Map<String,Object> temmap2 =null;
					List<Object> temList2 = new ArrayList<Object>();
					for(Map tem3 :maps_child_order_info){//自定单商品信息
						temmap2 = new HashMap<String, Object>();
						temmap2.put("goods_mainphoto_path", tem3.get("goods_mainphoto_path"));
						temmap2.put("goods_domainPath", tem3.get("goods_domainPath"));
						temmap2.put("goods_name", tem3.get("goods_name"));
						temList2.add(temmap2);
					}
					childstorInfo.put("goods_info", temList2);
					list.add(childstorInfo);
					totleAmount = totleAmount.add(orderForm.getTotalPrice());
					totleShipPrice = totleShipPrice.add(orderForm.getShip_price());
				}
				map.put("lists", list);
				map.put("totleAmount", totleAmount);
				map.put("totleShipPrice", totleShipPrice);
				result.add(map);
			}
		}
		return result;
	}
	

	@SecurityMapping(title = "订单取消", value = "/buyer/order_cancel.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/order_cancel.htm")
	public ModelAndView order_cancel(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/buyer_order_cancel.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		if (obj != null
				&& obj.getUser_id().equals(
						SecurityUserHolder.getCurrentUser().getId().toString())
				&& obj.getOrder_status() < 20) {
			mv.addObject("obj", obj);
			mv.addObject("currentPage", currentPage);
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "请求参数错误");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "订单取消确定", value = "/buyer/order_cancel_save.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/order_cancel_save.htm")
	public String order_cancel_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String state_info, String other_state_info) throws Exception {
		List<OrderForm> objs = new ArrayList<OrderForm>();
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		objs.add(obj);
		boolean all_verify = true;
		if (obj != null
				&& obj.getUser_id().equals(
						SecurityUserHolder.getCurrentUser().getId().toString())) {
			if (obj.getOrder_main() == 1 && obj.getChild_order_detail() != null) {
				List<Map> maps = (List<Map>) Json.fromJson(CommUtil
						.null2String(obj.getChild_order_detail()));
				if (maps != null) {
					for (Map map : maps) {
						// System.out.println(map.get("order_id"));
						OrderForm child_order = this.orderFormService
								.getObjById(CommUtil.null2Long(map
										.get("order_id")));
						objs.add(child_order);
					}
				}
			}

			for (OrderForm of : objs) {
				if (of.getOrder_status() >= 20) {
					all_verify = false;
				}
			}
		}
		if (all_verify) {
			if (obj != null
					&& obj.getUser_id().equals(
							SecurityUserHolder.getCurrentUser().getId()
									.toString())) {
				if (obj.getOrder_main() == 1
						&& obj.getChild_order_detail() != null) {
					List<Map> maps = (List<Map>) Json.fromJson(CommUtil
							.null2String(obj.getChild_order_detail()));
					if (maps != null) {
						for (Map map : maps) {
							// System.out.println(map.get("order_id"));
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
				ofl.setLog_user(SecurityUserHolder.getCurrentUser());
				ofl.setOf(obj);
				if (state_info.equals("other")) {
					ofl.setState_info(other_state_info);
				} else {
					ofl.setState_info(state_info);
				}
				this.orderFormLogService.save(ofl);
				Store store = this.storeService.getObjById(CommUtil
						.null2Long(obj.getStore_id()));
				Map map = new HashMap();
				if (store != null) {
					map.put("seller_id", store.getUser().getId().toString());
				}
				map.put("order_id", obj.getId().toString());
				String json = Json.toJson(map);
				if (this.configService.getSysConfig().isEmailEnable()) {
					if (obj.getOrder_form() == 0) {
						this.sendMsgAndEmTools.sendEmail(CommUtil
								.getURL(request),TytsmsStringUtils.generatorFilesFolderServerPath(request) ,
								"email_toseller_order_cancel_notify", store
										.getUser().getEmail(), json);
					}
				}
				if (this.configService.getSysConfig().isSmsEnbale()) {
					if (obj.getOrder_form() == 0) {
						String path = TytsmsStringUtils.generatorFilesFolderServerPath(request);
						String url = CommUtil.getURL(request);
						this.sendMsgAndEmTools.sendMsg(url, path,
								"sms_toseller_order_cancel_notify", store
										.getUser().getMobile(), json);
					}
				}
			}
		}

		return "redirect:order.htm?currentPage=" + currentPage;
	}

	/**
	 * 买家打开收货确认对话框
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "收货确认", value = "/buyer/order_cofirm.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/order_cofirm.htm")
	public ModelAndView order_cofirm(HttpServletRequest request,
			HttpServletResponse response, String id,String oid, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/buyer_order_cofirm.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		
		
		if (obj != null
				&& obj.getUser_id().equals(
						CommUtil.null2String(SecurityUserHolder
								.getCurrentUser().getId()))) {
			mv.addObject("obj", obj);
			mv.addObject("oid", oid);
			mv.addObject(
					"child_order",
					!CommUtil.null2String(obj.getChild_order_detail()).equals(
							""));
			mv.addObject("currentPage", currentPage);
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您没有编号为" + id + "的订单！");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
		}
		return mv;
	}
	
	/*
	 * 2015-03-16
	 * liuneng
	 * 发送邮件和短信
	 */
	public void sendEmailOrSms(HttpServletRequest request,Store store,String json,OrderForm obj)throws Exception{
		if (this.configService.getSysConfig()
				.isEmailEnable()) {
			if (obj.getOrder_form() == 0) {
				this.sendMsgAndEmTools.sendEmail(
								CommUtil.getURL(request),
								TytsmsStringUtils.generatorFilesFolderServerPath(request),
								"email_toseller_order_receive_ok_notify",
								store.getUser().getEmail(),
								json);
			}
		}
		if (this.configService.getSysConfig().isSmsEnbale()) {
			if (obj.getOrder_form() == 0) {
				String url = CommUtil.getURL(request);
				String path = TytsmsStringUtils.generatorFilesFolderServerPath(request);
				this.sendMsgAndEmTools.sendMsg(url,path,"sms_toseller_order_receive_ok_notify",store.getUser().getMobile(),json);
			}
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
	@SecurityMapping(title = "收货确认保存", value = "/buyer/order_cofirm_save.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/order_cofirm_save.htm")
	public String order_cofirm_save(HttpServletRequest request,
			HttpServletResponse response, String id,String oid, String currentPage)
			throws Exception {
		OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
		OrderForm mainObj = this.orderFormService.getObjById(CommUtil.null2Long(oid));
		if (obj != null&& obj.getUser_id().equals(CommUtil.null2String(SecurityUserHolder.getCurrentUser().getId()))) {
			obj.setOrder_status(40);
			Calendar ca = Calendar.getInstance();
			ca.add(ca.DATE, this.configService.getSysConfig().getAuto_order_return());
			SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String latertime = bartDateFormat.format(ca.getTime());
			obj.setReturn_shipTime(CommUtil.formatDate(latertime));
			
			obj.setConfirmTime(new Date());// 设置确认收货时间
			boolean ret = this.orderFormService.update(obj);
			if (obj.getPayment().getMark().equals("payafter")) {// 如果买家支付方式为货到付款，买家确认收货时更新商品库存
				this.update_goods_inventory(obj);// 更新商品库存
			}
			Store mainStore = this.storeService.getObjById(CommUtil.null2Long(obj.getStore_id()));
			Map json = new HashMap();
			if(obj.getOrder_form()==0){
				json.put("seller_id", mainStore.getUser().getId().toString());
			}
			json.put("order_id", obj.getId().toString());
			String mainJson = Json.toJson(json);
			if (ret) {// 订单状态更新成功，更新相关信息
				sendEmailOrSms(request,mainStore,mainJson,obj);
				
				/*	if (obj.getOrder_main() == 1&& !CommUtil.null2String(obj.getChild_order_detail()).equals("")) {// 更新子订单状态信息
					List<Map> maps = this.orderFormTools.queryGoodsInfo(obj.getChild_order_detail());
					for (Map map : maps) {
						OrderForm child_order = this.orderFormService.getObjById(CommUtil.null2Long(map.get("order_id")));
						child_order.setOrder_status(40);
						child_order.setReturn_shipTime(CommUtil.formatDate(latertime));
						child_order.setConfirmTime(new Date());// 设置确认收货时间
						this.orderFormService.update(child_order);
						if (obj.getPayment().getMark().equals("payafter")) {// 如果买家支付方式为货到付款，买家确认收货，子订单商品销量增加
							List<Map> goods_map = this.orderFormTools.queryGoodsInfo(child_order.getGoods_info());
							for (Map child_map : goods_map) {
								Goods goods = this.goodsService.getObjById(CommUtil.null2Long(child_map.get("goods_id")));
								goods.setGoods_salenum(goods.getGoods_salenum()+ CommUtil.null2Int(child_map.get("goods_count")));// 增加商品销量
								goods.setGoods_inventory(goods.getGoods_inventory()- CommUtil.null2Int(child_map.get("goods_count")));// 库存减少
								if (goods.getGroup_buy() == 2) {// 如果为团购商品，增加团购销量,减少团购库存
									for (GroupGoods gg : goods.getGroup_goods_list()) {
										if (gg.getGroup().getId().equals(goods.getGroup().getId())) {
											gg.setGg_selled_count(CommUtil.null2Int(gg.getGg_selled_count()+ CommUtil.null2Int(map.get("goods_count"))));// 增加团购销量
											if (gg.getGg_count()- CommUtil.null2Int(map.get("goods_count")) > 0) {// 减少团购库存
												gg.setGg_count(gg.getGg_count()- CommUtil.null2Int(map.get("goods_count")));
											} else {
												gg.setGg_count(0);
											}
											this.ggService.update(gg);
										}
									}
								}
								this.goodsService.update(goods);
							}
						}
						
						// 向子订单商家发送提醒信息，同时生成结算日志，如果子订单为平台自营，则不发送短信和邮件,
						if (child_order.getOrder_form() == 0) {
							Store store = this.storeService.getObjById(CommUtil.null2Long(child_order.getStore_id()));
							Map json_map = new HashMap();
							json_map.put("seller_id", store.getUser().getId().toString());
							json_map.put("order_id", child_order.getId().toString());
							String json = Json.toJson(json_map);
							sendEmailOrSms(request,store,json,obj);
							// 订单生成商家结算日志
							PayoffLog plog = new PayoffLog();
							plog.setPl_sn("pl"+ CommUtil.formatTime("yyyyMMddHHmmss",new Date())+ store.getUser().getId());
							plog.setPl_info("确认收货");
							plog.setAddTime(new Date());
							plog.setSeller(store.getUser());
							plog.setO_id(CommUtil.null2String(child_order.getId()));
							plog.setOrder_id(child_order.getOrder_id().toString());
							plog.setCommission_amount(child_order.getCommission_amount());// 该订单总佣金费用
							plog.setGoods_info(child_order.getGoods_info());
							plog.setOrder_total_price(child_order.getGoods_amount());// 该订单总商品金额
							plog.setTotal_amount(BigDecimal.valueOf(CommUtil.subtract(child_order.getGoods_amount(),child_order.getCommission_amount())));// 该订单应结算金额：结算金额=订单总商品金额-总佣金费用
							this.payoffLogservice.save(plog);
							store.setStore_sale_amount(BigDecimal.valueOf(CommUtil.add(child_order.getGoods_amount(),store.getStore_sale_amount())));// 店铺本次结算总销售金额
							store.setStore_commission_amount(BigDecimal.valueOf(CommUtil.add(child_order.getCommission_amount(),store.getStore_commission_amount())));// 店铺本次结算总佣金
							store.setStore_payoff_amount(BigDecimal.valueOf(CommUtil.add(plog.getTotal_amount(),store.getStore_payoff_amount())));// 店铺本次结算总佣金
							this.storeService.update(store);
							// 增加系统总销售金额、总佣金
							SysConfig sc = this.configService.getSysConfig();
							sc.setPayoff_all_sale(BigDecimal.valueOf(CommUtil.add(child_order.getGoods_amount(),sc.getPayoff_all_sale())));
							sc.setPayoff_all_commission(BigDecimal.valueOf(CommUtil.add(child_order.getCommission_amount(),sc.getPayoff_all_commission())));
							this.configService.update(sc);
						}
						
					}
				}*/
				//生成订单日志
				OrderFormLog ofl = new OrderFormLog();
				ofl.setAddTime(new Date());
				ofl.setLog_info("确认收货");
				ofl.setLog_user(SecurityUserHolder.getCurrentUser());
				ofl.setOf(obj);
				this.orderFormLogService.save(ofl);
				// 单生成商家结算日志
				if (obj.getOrder_form() == 0) {
					Store store = this.storeService.getObjById(CommUtil.null2Long(obj.getStore_id()));
					PayoffLog plog = new PayoffLog();
					plog.setPl_sn("pl"+ CommUtil.formatTime("yyyyMMddHHmmss", new Date())+ store.getUser().getId());
					plog.setPl_info("确认收货");
					plog.setAddTime(new Date());
					plog.setSeller(store.getUser());
					plog.setO_id(CommUtil.null2String(obj.getId()));
					plog.setOrder_id(obj.getOrder_id().toString());
					plog.setCommission_amount(obj.getCommission_amount());// 该订单总佣金费用
					plog.setGoods_info(obj.getGoods_info());
					plog.setOrder_total_price(obj.getGoods_amount());// 该订单总商品金额
					plog.setTotal_amount(BigDecimal.valueOf(CommUtil.subtract(obj.getGoods_amount(), obj.getCommission_amount())));// 该订单应结算金额：结算金额=订单总商品金额-总佣金费用
					this.payoffLogservice.save(plog);
					store.setStore_sale_amount(BigDecimal.valueOf(CommUtil.add(obj.getGoods_amount(), store.getStore_sale_amount())));// 店铺本次结算总销售金额
					store.setStore_commission_amount(BigDecimal.valueOf(CommUtil.add(obj.getCommission_amount(),store.getStore_commission_amount())));// 店铺本次结算总佣金
					store.setStore_payoff_amount(BigDecimal.valueOf(CommUtil.add(plog.getTotal_amount(),store.getStore_payoff_amount())));// 店铺本次结算总佣金
					this.storeService.update(store);
					// 增加系统总销售金额、总佣金
					SysConfig sc = this.configService.getSysConfig();
					sc.setPayoff_all_sale(BigDecimal.valueOf(CommUtil.add(obj.getGoods_amount(), sc.getPayoff_all_sale())));
					sc.setPayoff_all_commission(BigDecimal.valueOf(CommUtil.add(obj.getCommission_amount(),sc.getPayoff_all_commission())));
					this.configService.update(sc);
				}
			}
			
			
		}
		String url = "redirect:order.htm?currentPage=" + currentPage;
		return url;
	}

	@SecurityMapping(title = "买家评价", value = "/buyer/order_evaluate.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/order_evaluate.htm")
	public ModelAndView order_evaluate(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/buyer_order_evaluate.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		if (obj != null
				&& obj.getUser_id().equals(
						SecurityUserHolder.getCurrentUser().getId().toString())) {
			mv.addObject("obj", obj);
			mv.addObject("orderFormTools", orderFormTools);
			String evaluate_session = CommUtil.randomString(32);
			request.getSession(false).setAttribute("evaluate_session",
					evaluate_session);
			mv.addObject("evaluate_session", evaluate_session);
			if (obj.getOrder_status() >= 50) {
				mv = new JModelAndView("success.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "订单已经评价！");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/buyer/order.htm");
			}
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您没有编号为" + id + "的订单！");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
		}
		mv.addObject("orderFormTools", orderFormTools);
		return mv;
	}

	@SecurityMapping(title = "买家评价保存", value = "/buyer/order_evaluate_save.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/order_evaluate_save.htm")
	@Transactional
	public ModelAndView order_evaluate_save(HttpServletRequest request,
			HttpServletResponse response, String id, String evaluate_session)
			throws Exception {
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		String evaluate_session1 = (String) request.getSession(false)
				.getAttribute("evaluate_session");
		if (evaluate_session1.equals(evaluate_session)) {
			request.getSession(false).removeAttribute("evaluate_session");
			if (obj != null
					&& obj.getUser_id().equals(
							SecurityUserHolder.getCurrentUser().getId()
									.toString())) {
				if (obj.getOrder_status() == 40) {
					obj.setOrder_status(50);
					this.orderFormService.update(obj);
					OrderFormLog ofl = new OrderFormLog();
					ofl.setAddTime(new Date());
					ofl.setLog_info("评价订单");
					ofl.setLog_user(SecurityUserHolder.getCurrentUser());
					ofl.setOf(obj);
					this.orderFormLogService.save(ofl);
					obj.setFinishTime(new Date());
					this.orderFormService.update(obj);
					List<Map> json = this.orderFormTools.queryGoodsInfo(obj
							.getGoods_info());
					for (Map map : json) {
						map.put("orderForm", obj.getId());
					}
					List<Map> child_order = this.orderFormTools
							.queryGoodsInfo(obj.getChild_order_detail());
					List<Map> child_goods = new ArrayList<Map>();
					for (Map c : child_order) {
						List<Map> maps = this.orderFormTools.queryGoodsInfo(c
								.get("order_goods_info").toString());
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
						eva.setGoods_price(map.get("goods_price").toString());
						eva.setGoods_spec(map.get("goods_gsp_val").toString());
						eva.setEvaluate_info(request
								.getParameter("evaluate_info_" + goods.getId()));
						eva.setEvaluate_buyer_val(CommUtil.null2Int(request
								.getParameter("evaluate_buyer_val"
										+ goods.getId())));
						eva.setDescription_evaluate(BigDecimal.valueOf(CommUtil
								.null2Double(request
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
						eva.setEvaluate_user(SecurityUserHolder
								.getCurrentUser());
						eva.setOf(this.orderFormService.getObjById(CommUtil
								.null2Long(map.get("orderForm"))));
						this.evaluateService.save(eva);
						Map params = new HashMap();
						if (goods.getGoods_type() == 1) {
							Store store = this.storeService.getObjById(CommUtil
									.null2Long(goods.getGoods_store().getId()));
							System.out.println(store.getId());
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
							store_evaluate1 = CommUtil
									.null2Double(df
											.format(store_evaluate1_total
													/ evas.size()));
							description_evaluate = CommUtil.null2Double(df
									.format(description_evaluate_total
											/ evas.size()));
							service_evaluate = CommUtil.null2Double(df
									.format(service_evaluate_total
											/ evas.size()));
							ship_evaluate = CommUtil.null2Double(df
									.format(ship_evaluate_total / evas.size()));
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
							params.put("user_id", SecurityUserHolder
									.getCurrentUser().getId().toString());
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
							user_evaluate1 = CommUtil
									.null2Double(df.format(user_evaluate1_total
											/ evas.size()));
							description_evaluate = CommUtil.null2Double(df
									.format(description_evaluate_total
											/ evas.size()));
							service_evaluate = CommUtil.null2Double(df
									.format(service_evaluate_total
											/ evas.size()));
							ship_evaluate = CommUtil.null2Double(df
									.format(ship_evaluate_total / evas.size()));
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

						this.goodsService.update(goods);
						User user = this.userService.getObjById(CommUtil
								.null2Long(obj.getUser_id()));						

						// 增加评价积分
						user.setIntegral(user.getIntegral()
								+ this.configService.getSysConfig()
										.getIndentComment());
						// 增加用户消费金额
						user.setUser_goods_fee(BigDecimal.valueOf(CommUtil.add(
								user.getUser_goods_fee(), obj.getTotalPrice())));
						this.userService.update(user);
						// 记录积分明细
						if (this.configService.getSysConfig().isIntegral()) {
							IntegralLog log = new IntegralLog();
							log.setAddTime(new Date());
							log.setContent("订单评价增加"
									+ this.configService.getSysConfig()
											.getIndentComment() + "分");
							log.setIntegral(this.configService.getSysConfig()
									.getIndentComment());
							log.setIntegral_user(user);
							log.setType("order");
							this.integralLogService.save(log);
						}
					}
				}
				if (this.configService.getSysConfig().isEmailEnable()) {
					if (obj.getOrder_form() == 0) {
						Store store = this.storeService.getObjById(CommUtil
								.null2Long(obj.getStore_id()));
						Map map = new HashMap();
						map.put("seller_id", store.getUser().getId().toString());
						map.put("order_id", obj.getId().toString());
						String json = Json.toJson(map);
						this.sendMsgAndEmTools.sendEmail(CommUtil
								.getURL(request), TytsmsStringUtils.generatorFilesFolderServerPath(request),
								"email_toseller_evaluate_ok_notify", store
										.getUser().getEmail(), json);
					}
				}
			}
			ModelAndView mv = new JModelAndView("success.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "订单评价成功！");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
			return mv;
		} else {
			ModelAndView mv = new JModelAndView("error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "禁止重复评价!");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
			return mv;
		}

	}

	@SecurityMapping(title = "删除订单信息", value = "/buyer/order_delete.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/order_delete.htm")
	public String order_delete(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage)
			throws Exception {
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		if (obj != null
				&& obj.getUser_id().equals(
						SecurityUserHolder.getCurrentUser().getId().toString())) {
			if (obj.getOrder_status() == 0) {
				if (obj.getOrder_main() == 1 && obj.getOrder_cat() == 0) {
					if (obj.getChild_order_detail() != null
							&& !obj.getChild_order_detail().equals("")) {
						List<Map> maps = (List<Map>) Json.fromJson(obj
								.getChild_order_detail());
						for (Map map : maps) {
							// System.out.println(map.get("order_id"));
							OrderForm child_order = this.orderFormService
									.getObjById(CommUtil.null2Long(map
											.get("order_id")));
							child_order.setOrder_status(0);
							this.orderFormService.delete(child_order.getId());
						}
					}
				}
				this.orderFormService.delete(obj.getId());
			}
		}
		return "redirect:order.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "买家订单详情", value = "/buyer/order_view.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/order_view.htm")
	public ModelAndView order_view(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/order_view.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		if (obj != null
				&& obj.getUser_id().equals(
						SecurityUserHolder.getCurrentUser().getId().toString())) {
			if (obj.getOrder_cat() == 1) {// 手机充值订单
				mv = new JModelAndView(
						"user/default/usercenter/recharge_order_view.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
			}
			boolean query_ship = false;// 是否查询物流
			if (!CommUtil.null2String(obj.getShipCode()).equals("")) {
				query_ship = true;
			}
			if (obj.getOrder_main() == 1
					&& !CommUtil.null2String(obj.getChild_order_detail())
							.equals("")) {// 子订单中有商家已经发货，也需要显示
				List<Map> maps = this.orderFormTools.queryGoodsInfo(obj
						.getChild_order_detail());
				for (Map child_map : maps) {
					OrderForm child_order = this.orderFormService
							.getObjById(CommUtil.null2Long(child_map
									.get("order_id")));
					if (!CommUtil.null2String(child_order.getShipCode())
							.equals("")) {
						query_ship = true;
					}
				}
			}
			mv.addObject("obj", obj);
			mv.addObject("shipTools", shipTools);
			mv.addObject("orderFormTools", orderFormTools);
			mv.addObject("query_ship", query_ship);
			Map params = new HashMap();
			params.put("of_id", obj.getId());
			List<OrderFormLog> ofls = this.orderFormLogService.query(
					"select obj from OrderFormLog obj where obj.of.id=:of_id",
					params, -1, -1);
			mv.addObject("ofls", ofls);
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "订单编号错误");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
		}
		mv.addObject("orderFormTools", orderFormTools);
		mv.addObject("express_company_name", this.orderFormTools.queryExInfo(
				obj.getExpress_info(), "express_company_name"));
		return mv;
	}

	@SecurityMapping(title = "买家物流详情", value = "/buyer/ship_view.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/ship_view.htm")
	public ModelAndView order_ship_view(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/order_ship_view.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm order = this.orderFormService.getObjById(CommUtil
				.null2Long(id));
		if (order != null && !order.equals("")) {
			if (order.getUser_id().equals(
					SecurityUserHolder.getCurrentUser().getId().toString())) {
				//bigen cty 修改时间：2015-03-19
				List<TransInfo> transInfo_list = new ArrayList<TransInfo>();
				HttpclientLogisticsUtil logistics = new HttpclientLogisticsUtil();
				Map jsonMap = new HashMap();
				jsonMap.put("tytordertyte", order.getShipper_type()); //订单类型
				jsonMap.put("pickexpressno", order.getShipCode()); //物流单号
				jsonMap.put("pickexpresscom", this.orderFormTools.queryExInfo(order.getExpress_info(),"express_company_mark")); //快递代码
				JSONObject json = new JSONObject().fromObject(jsonMap);
				TransInfo transInfo = logistics.searchLogisticsInfo(json.toString());
				transInfo.setExpress_company_name(this.orderFormTools
						.queryExInfo(order.getExpress_info(),
								"express_company_name"));
				transInfo.setExpress_ship_code(order.getShipCode());
				if(transInfo.getData().size() >0){
					transInfo.setStatus("1");
				}else{
					transInfo.setStatus("2");
					transInfo.setMessage("系统繁忙，请稍后在试!");
				}
				transInfo_list.add(transInfo);
				// 查询子订单的物流跟踪信息
				if(order.getOrder_main() != 1 && !!CommUtil.null2String(order.getChild_order_detail()).equals("")){
					List<Map> maps = this.orderFormTools.queryGoodsInfo(order
							.getChild_order_detail());
					for (Map child_map : maps) {
						OrderForm child_order = this.orderFormService
								.getObjById(CommUtil.null2Long(child_map
										.get("order_id")));
						if (child_order.getExpress_info() != null) {
							Map jMap = new HashMap();
							jMap.put("tytordertyte", child_order.getShipper_type()); //订单类型
							jMap.put("pickexpressno", child_order.getShipCode()); //物流单号
							jMap.put("pickexpresscom", this.orderFormTools.queryExInfo(child_order.getExpress_info(),"express_company_mark")); //快递代码
							JSONObject jsons = new JSONObject().fromObject(jMap);
							TransInfo transInfo1 = logistics.searchLogisticsInfo(jsons.toString());
							transInfo1.setExpress_company_name(this.orderFormTools.queryExInfo(child_order.getExpress_info(),"express_company_name"));
							transInfo1.setExpress_ship_code(child_order.getShipCode());
							if(transInfo.getData().size() >0){
								transInfo.setStatus("1");
							}else{
								transInfo.setStatus("2");
								transInfo.setMessage("系统繁忙，请稍后在试!");
							}
							transInfo_list.add(transInfo1);
						}
					}
				}
				//end cty 修改时间：2015-03-19
//				List<TransInfo> transInfo_list = new ArrayList<TransInfo>();
//				TransInfo transInfo = this.query_ship_getData(id);
//				if (order.getOrder_main() == 1
//						&& !CommUtil.null2String(order.getChild_order_detail())
//								.equals("")) {// 查询子订单的物流跟踪信息
//					List<Map> maps = this.orderFormTools.queryGoodsInfo(order
//							.getChild_order_detail());
//					for (Map child_map : maps) {
//						OrderForm child_order = this.orderFormService
//								.getObjById(CommUtil.null2Long(child_map
//										.get("order_id")));
//						if (child_order.getExpress_info() != null) {
//							TransInfo transInfo1 = this
//									.query_ship_getData(CommUtil
//											.null2String(child_order.getId()));
//							transInfo1
//									.setExpress_company_name(this.orderFormTools
//											.queryExInfo(child_order
//													.getExpress_info(),
//													"express_company_name"));
//							transInfo1.setExpress_ship_code(child_order
//									.getShipCode());
//							transInfo_list.add(transInfo1);
//						}
//					}
//
//				}
				mv.addObject("transInfo_list", transInfo_list);
				mv.addObject("obj", order);
			} else {
				mv = new JModelAndView("error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "您查询的物流不存在！");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/buyer/order.htm");
			}
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您查询的物流不存在！");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "物流跟踪查询", value = "/buyer/query_ship.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/query_ship.htm")
	public ModelAndView query_ship(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/query_ship_data.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		TransInfo info = this.query_ship_getData(id);
		mv.addObject("transInfo", info);
		return mv;
	}

	@SecurityMapping(title = "虚拟商品信息", value = "/buyer/order_seller_intro.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/order_seller_intro.htm")
	public ModelAndView order_seller_intro(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/order_seller_intro.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		if (obj != null
				&& obj.getUser_id().equals(
						SecurityUserHolder.getCurrentUser().getId().toString())) {
			mv.addObject("obj", obj);
		}
		return mv;
	}
	/**
	 * 
	 * @param request
	 * @param response
	 * @param id --商品id
	 * @param oid-订单id
	 * @param order_id--订单order_id
	 * @param view
	 * @param goods_gsp_ids
	 * @return
	 */

	@SecurityMapping(title = "买家退货申请", value = "/buyer/order_return_apply.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/order_return_apply.htm")
	public ModelAndView order_return_apply(HttpServletRequest request,
			HttpServletResponse response, String id, String oid,
			String order_id, String view, String goods_gsp_ids,String original_id) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/order_return_apply.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(oid));
		if (obj.getUser_id().equals(
				SecurityUserHolder.getCurrentUser().getId().toString())) {
			List<Map> maps = this.orderFormTools.queryGoodsInfo(obj
					.getGoods_info());
			Goods goods = this.goodsService.getObjById(CommUtil.null2Long(id));
			for (Map m : maps) {
				if (CommUtil.null2String(m.get("goods_id")).equals(id)) {
					mv.addObject("return_count", m.get("return_goods_count"));
					mv.addObject("oid", oid);
					mv.addObject("goods", goods);
					if (CommUtil.null2String(m.get("goods_return_status")).equals("5")) {
						mv.addObject("view", true);
						List<Map> return_maps = this.orderFormTools
								.queryGoodsInfo(obj.getReturn_goods_info());
						for (Map map : return_maps) {
							if (CommUtil
									.null2String(map.get("return_goods_id"))
									.equals(id)) {
								mv.addObject("return_content",
										map.get("return_goods_content"));
							}
						}
					}
				}
			}
			HttpSession session = request.getSession();
			AtomicInteger img_num = (AtomicInteger)session.getAttribute("img_num");
        	String img_html = "";
			if(img_num != null){
				for (int i = 1; i < img_num.intValue()+1; i++) {
					img_html += "<li><a target='_blank' id='refund_href"+i+"' href='"+CommUtil.getURL(request) + "/"+session.getAttribute("refund_img_"+i)+"'> <img id='refund_img_"+i+"' width='55' height='55' src='"+CommUtil.getURL(request) + "/"+session.getAttribute("refund_img_"+i)+"' /></a> <b onclick='delete_return_img("+i+")'>X</b></li>";
				}
            }
			mv.addObject("img_html", img_html);
		}
		mv.addObject("goods_gsp_ids", goods_gsp_ids);
		mv.addObject("original_id", original_id);
		
		return mv;
	}
	
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param id --$!oid
	 * @param currentPage
	 * @param return_goods_content
	 * @param goods_id--订单order_id
	 * @param return_goods_count
	 * @param goods_gsp_ids
	 * @return
	 * @throws Exception
	 */
	

	@Transactional
	@SecurityMapping(title = "买家退货申请保存", value = "/buyer/order_return_apply_save.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/order_return_apply_save.htm")
	public String order_return_apply_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String return_goods_content, String goods_id,String original_id,
			String return_goods_count, String goods_gsp_ids) throws Exception {
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		List<Goods> goods_list = this.orderFormTools.queryOfGoods(obj.getId()
				.toString());
		Goods goods = null;
		for (Goods g : goods_list) {
			if (g.getId().toString().equals(goods_id)) {
				goods = g;
			}
		}
		if (obj != null
				&& obj.getUser_id().equals(
						SecurityUserHolder.getCurrentUser().getId().toString())
				&& goods != null) {
			List<Map> list = new ArrayList<Map>();
			Map json = new HashMap();
			json.put("return_goods_id", goods.getId());
			json.put("return_goods_content",
					CommUtil.filterHTML(return_goods_content));
			json.put("return_goods_count", return_goods_count);
			json.put("return_goods_price", goods.getStore_price());
			json.put("return_goods_commission_rate", goods.getGc()
					.getCommission_rate());
			json.put("return_order_id", id);
			list.add(json);
			obj.setReturn_goods_info(Json.toJson(list, JsonFormat.compact()));
			List<Map> maps = this.orderFormTools.queryGoodsInfo(obj
					.getGoods_info());
			List<Map> new_maps = new ArrayList<Map>();
			Map gls = new HashMap();
			for (Map m : maps) {
				String tmp_goods_gsp_ids = m.get("goods_gsp_ids").toString();
				if (m.get("goods_id").toString().equals(goods_id)&& goods_gsp_ids.equals(tmp_goods_gsp_ids)) {
					m.put("goods_return_status", 5);
					gls.putAll(m);
				}
				new_maps.add(m);
			}
			
			obj.setGoods_info(Json.toJson(new_maps));
			this.orderFormService.update(obj);
			User user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
			ReturnGoodsLog rlog = new ReturnGoodsLog();
			rlog.setReturn_service_id("re" + user.getId()
					+ CommUtil.formatTime("yyyyMMddHHmmss", new Date()));
			rlog.setUser_name(user.getUserName());
			rlog.setUser_id(user.getId());
			rlog.setReturn_content(CommUtil.filterHTML(return_goods_content));
			rlog.setGoods_all_price(gls.get("goods_all_price").toString());
			rlog.setGoods_count(gls.get("goods_count").toString());
			rlog.setGoods_id(CommUtil.null2Long(gls.get("goods_id").toString()));
			rlog.setGoods_mainphoto_path(gls.get("goods_mainphoto_path")
					.toString());
			rlog.setGoods_commission_rate(BigDecimal.valueOf(CommUtil
					.null2Double(gls.get("goods_commission_rate"))));
			rlog.setGoods_name(gls.get("goods_name").toString());
			rlog.setGoods_price(gls.get("goods_price").toString());
			rlog.setGoods_return_status("5");
			rlog.setAddTime(new Date());
			rlog.setReturn_order_id(CommUtil.null2Long(id));
			rlog.setGoods_type(goods.getGoods_type());
			if (goods.getGoods_store() != null) {
				rlog.setStore_id(goods.getGoods_store().getId());
			}
			String refund_img ="";
			HttpSession session = request.getSession();
			AtomicInteger num_img = (AtomicInteger)session.getAttribute("img_num");
            if(num_img != null){
            	for (int i = num_img.intValue(); i>0; i--) {
            		refund_img += session.getAttribute("refund_img_"+i)+",";
				}
            }
            rlog.setRefund_img(refund_img);
			this.returnGoodsLogService.save(rlog);
			if (this.configService.getSysConfig().isEmailEnable()) {
				if (obj.getOrder_form() == 0) {
					User seller = this.userService.getObjById(this.storeService
							.getObjById(CommUtil.null2Long(obj.getStore_id()))
							.getUser().getId());
					Map map = new HashMap();
					map.put("buyer_id", user.getId().toString());
					map.put("seller_id", seller.getId().toString());
					String map_json = Json.toJson(map);
					this.sendMsgAndEmTools.sendEmail(CommUtil.getURL(request),
							TytsmsStringUtils.generatorFilesFolderServerPath(request),
							"email_toseller_order_return_apply_notify",
							seller.getEmail(), map_json);
				}
			}
			if (this.configService.getSysConfig().isSmsEnbale()) {
				if (obj.getOrder_form() == 0) {
					User seller = this.userService.getObjById(this.storeService
							.getObjById(CommUtil.null2Long(obj.getStore_id()))
							.getUser().getId());
					Map map = new HashMap();
					map.put("buyer_id", user.getId().toString());
					map.put("seller_id", seller.getId().toString());
					String map_json = Json.toJson(map);
					String path = TytsmsStringUtils.generatorImagesFolderServerPath(request);
					String url = CommUtil.getURL(request);
					this.sendMsgAndEmTools.sendMsg(url, path,
							"sms_toseller_order_return_apply_notify",
							seller.getMobile(), map_json);
				}
			}
		}
		return "redirect:order_return_list.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "买家退货申请取消", value = "/buyer/order_return_apply_cancel.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/order_return_apply_cancel.htm")
	public String order_return_apply_cancel(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String return_goods_content, String goods_id,
			String return_goods_count, String goods_gsp_ids) throws Exception {
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		List<Goods> goods_list = this.orderFormTools.queryOfGoods(obj.getId()
				.toString());
		Goods goods = null;
		for (Goods g : goods_list) {
			if (g.getId().toString().equals(goods_id)) {
				goods = g;
			}
		}
		if (obj != null
				&& obj.getUser_id().equals(
						SecurityUserHolder.getCurrentUser().getId().toString())
				&& goods != null) {
			obj.setReturn_goods_info("");
			List<Map> maps = this.orderFormTools.queryGoodsInfo(obj
					.getGoods_info());
			List<Map> new_maps = new ArrayList<Map>();
			Map gls = new HashMap();
			for (Map m : maps) {
				if (m.get("goods_id").toString().equals(goods_id)
						&& goods_gsp_ids.equals(m.get("goods_gsp_ids")
								.toString())) {
					m.put("goods_return_status", "");
					gls.putAll(m);
				}
				new_maps.add(m);
			}
			obj.setGoods_info(Json.toJson(new_maps));
			this.orderFormService.update(obj);
			User user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
			Map map = new HashMap();
			map.put("goods_id",
					CommUtil.null2Long(gls.get("goods_id").toString()));
			map.put("return_order_id", CommUtil.null2Long(id));
			map.put("uid", SecurityUserHolder.getCurrentUser().getId());
			List<ReturnGoodsLog> objs = this.returnGoodsLogService
					.query("select obj from ReturnGoodsLog obj where obj.goods_id=:goods_id and obj.return_order_id=:return_order_id and obj.user_id=:uid",
							map, -1, -1);
			for (ReturnGoodsLog rl : objs) {
				this.returnGoodsLogService.delete(rl.getId());
			}
		}
		return "redirect:order_return_list.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "买家退货物流信息", value = "/buyer/order_return_ship.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/order_return_ship.htm")
	public ModelAndView order_return_ship(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/order_return_ship.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		if (obj != null
				&& obj.getUser_id().equals(
						SecurityUserHolder.getCurrentUser().getId().toString())) {
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
			Map params = new HashMap();
			params.put("status", 0);
			List<ExpressCompany> expressCompanys = this.expressCompayService
					.query("select obj from ExpressCompany obj where obj.company_status=:status order by company_sequence asc",
							params, -1, -1);
			mv.addObject("expressCompanys", expressCompanys);
			mv.addObject("physicalGoods", physicalGoods);
			mv.addObject("deliveryGoods", deliveryGoods);
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您没有编号为" + id + "的订单！");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
		}
		return mv;
	}

	@Transactional
	@SecurityMapping(title = "买家退货物流信息保存", value = "/buyer/order_return_ship_save.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/order_return_ship_save.htm")
	public ModelAndView order_return_ship_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String express_id, String express_code,String order_type,String area_id) {
		ModelAndView mv = new JModelAndView("success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		ReturnGoodsLog obj = this.returnGoodsLogService.getObjById(CommUtil
				.null2Long(id));
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		if (obj != null && obj.getUser_id().equals(user.getId())) {
			ExpressCompany ec = this.expressCompayService.getObjById(CommUtil
					.null2Long(express_id));
			Map json_map = new HashMap();
			json_map.put("express_company_id", ec.getId());
			json_map.put("express_company_name", ec.getCompany_name());
			json_map.put("express_company_mark", ec.getCompany_mark());
			json_map.put("express_company_type", ec.getCompany_type());
			String express_json = Json.toJson(json_map);
			obj.setReturn_express_info(express_json);
			obj.setExpress_code(express_code);
			obj.setFefund_type(CommUtil.null2Long(order_type));
			obj.setGoods_return_status("7");
			this.returnGoodsLogService.update(obj);
			mv.addObject("op_title", "保存退货物流成功！");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/buyer/order_return_listlog.htm");

			OrderForm return_of = this.orderFormService.getObjById(obj
					.getReturn_order_id());
			List<Map> maps = this.orderFormTools.queryGoodsInfo(return_of
					.getGoods_info());
			List<Map> new_maps = new ArrayList<Map>();
			Map gls = new HashMap();
			for (Map m : maps) {
				if (m.get("goods_id").toString()
						.equals(CommUtil.null2String(obj.getGoods_id()))) {
					m.put("goods_return_status", 7);
					gls.putAll(m);
				}
				new_maps.add(m);
			}
			return_of.setGoods_info(Json.toJson(new_maps));
			this.orderFormService.update(return_of);
			//bigen cty 修改时间：2015-03-19
			try {
				Store store = this.storeService.getObjById(obj.getStore_id());
				Map jsonMap = new HashMap();
				jsonMap.put("tytorderid", return_of.getId()); //订单ID
				jsonMap.put("tytorderno", return_of.getOrder_id()); //订单号
				jsonMap.put("tytordertyte", order_type); //订单类型
				jsonMap.put("pickexpressno", express_code); //物流单号
				jsonMap.put("pickexpresscom", ec.getCompany_mark()); //快递公司代码
				jsonMap.put("pickexpresscorp", ec.getCompany_name()); //快递公司名称
				jsonMap.put("shipperperson", user.getUserName()); //发货人
				if(!"".equals(user.getTelephone()) && !"".equals(user.getMobile())){//发货人电话
					if(user.getTelephone().equals(user.getMobile())){
						jsonMap.put("shippertell", user.getMobile());
					}else{
						jsonMap.put("shippertell", user.getTelephone()+","+user.getMobile()); 
					}
				}else{
					if(!"".equals(user.getMobile())){
						jsonMap.put("shippertell", user.getMobile());
					}else{
						jsonMap.put("shippertell", user.getTelephone());
					}
				}
				if(user.getAddrs() != null){
					Map<String,Object> areaMap = null;
					String areaInfo = "";
					for (int i = 0; i < user.getAddrs().size(); i++) {
						areaMap = CommUtil.getAreaInfo(user.getAddrs().get(i).getArea(),1);
						if(areaMap.size()>0 && area_id.equals(areaMap.get("areaId"))){
							jsonMap.put("shipperarea", areaMap.get("areaName")); //发货人地址
							jsonMap.put("shipperaddr", user.getAddrs().get(i).getArea_info()); //发货人详细地址
							areaMap = new HashMap();
							break;
						}
						areaInfo = user.getAddrs().get(i).getArea_info();
					}
					if(areaMap.size() >0){
						jsonMap.put("shipperarea", areaMap.get("areaName")); //发货人地址
						jsonMap.put("shipperaddr", areaInfo); //发货人详细地址
					}
				}else{
					jsonMap.put("shipperarea", null); //发货人地址
					jsonMap.put("shipperaddr", null); //发货人详细地址
				}
				jsonMap.put("consigneeperson", store.getStore_ower()); //收货人
				jsonMap.put("consigneetell", store.getStore_telephone()); //收货人电话
				if(store.getArea() != null){//收货人地址
					Map<String,Object> areaMap = CommUtil.getAreaInfo(store.getArea(),1);
					if(areaMap.size()>0){
						jsonMap.put("consigneearea", areaMap.get("areaName")); //收货人地址
					}
				}else{
					jsonMap.put("consigneearea", null); //收货人地址
				}
				jsonMap.put("consigneeaddr", store.getStore_address()); //收货人详情地址
				Area area = this.areaService.getObjById(CommUtil.null2Long(area_id));
				jsonMap.put("from", area.getAreaName()); //出发地
				Map<String,Object> areaMap = CommUtil.getAreaInfo(store.getArea(),0);
				jsonMap.put("to", areaMap.get("areaName"));   //目的地
				jsonMap.put("shipper", "个人"); //发货单位
				jsonMap.put("consignee", store.getLicense_c_name()); //收货单位
				jsonMap.put("consigneesepdemand", ""); //收货特别要求
				List<Map> map_list = new ArrayList<Map>();
				Map jmap = new HashMap();
				Goods goods = this.goodsService.getObjById(obj.getGoods_id());
				jmap.put("gicargoname", obj.getGoods_name()); //货名
				jmap.put("unitpiece", obj.getGoods_count());  //件数 
				jmap.put("unitweight", goods.getGoods_weight()); //重量
				jmap.put("unitprice", obj.getGoods_price());  //订单单价
				jmap.put("cargovalue", null); //保险费值
				map_list.add(jmap);
				jsonMap.put("goodsinfo", Json.toJson(map_list)); //商品信息
				JSONObject jsonTip = new JSONObject().fromObject(jsonMap);
				//调用接口 数据推送到TPI中
				HttpclientLogisticsUtil logistics = new HttpclientLogisticsUtil();
			    boolean isResult = logistics.pushLogisticsInfo(jsonTip.toString());
			    if(isResult){
					log.info("买家退货推送Tpi信息===订单号"+return_of.getOrder_id()+"===订单推送信息成功");
			    }else{
					log.info("买家退货推送Tpi信息===订单号"+return_of.getOrder_id()+"===订单推送信息失败");
			    }
			} catch (Exception e) {
				log.info(e);
			}
			//end cty 修改时间：2015-03-19
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您没有为" + obj.getReturn_service_id()
					+ "的退货服务号！");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/buyer/order_return_listlog.htm");

		}
		return mv;
	}

	@SecurityMapping(title = "买家退货申请列表", value = "/buyer/order_return_list.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/order_return_list.htm")
	public ModelAndView order_return_list(HttpServletRequest request,
			HttpServletResponse response, String id, String view,
			String currentPage, String order_id, String beginTime,
			String endTime) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/order_return_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderFormQueryObject ofqo = new OrderFormQueryObject(currentPage, mv,
				"addTime", "desc");
		ofqo.addQuery("obj.user_id", new SysMap("user_id", SecurityUserHolder
				.getCurrentUser().getId().toString()), "=");
		ofqo.addQuery("obj.order_main", new SysMap("order_main", 1), "=");
		if (order_id!=null && !CommUtil.null2String(order_id).equals("")) {
			ofqo.addQuery("obj.order_id", new SysMap("order_id", "%" + order_id
					+ "%"), "like");
			mv.addObject("order_id", order_id);
		}
		ofqo.addQuery("obj.order_cat", new SysMap("order_cat", 2), "!=");
		//ofqo.addQuery("obj.order_status", new SysMap("order_status", 40), ">=");
		//ofqo.addQuery("obj.return_shipTime", new SysMap("return_shipTime",new Date()), ">=");
		IPageList pList = this.orderFormService.list(ofqo);
		
		List<OrderForm> resultList = pList.getResult();
			List<OrderForm> listForm = new ArrayList<OrderForm>();
		if(pList.getResult()!=null){
			for(OrderForm form:resultList){
				boolean flag = checkChildConfirm(form);//确定是否有主订单没有确认收货，子订单已经确认收货情况
				if(form.getOrder_status()>=40 || flag){
					listForm.add(form);
				}
			}
		}
		
		List<Object> result = new ArrayList<Object>();
		if(resultList!=null){
			for(OrderForm form:listForm){
				Map<String,Object> map = new HashMap<String, Object>();
				map.put("id", form.getId());
				map.put("order_id", form.getOrder_id());
				map.put("addTime", form.getAddTime());
				Map<String,Object> temmap =null;
				List<Object> temList = new ArrayList<Object>();
				//遍历主订单商品信息
				List<Map> maps = this.orderFormTools.queryGoodsInfo(form.getGoods_info());
				for(Map tem :maps){
					temmap = new HashMap<String, Object>();
					temmap.put("original_id", form.getId());
					temmap.put("order_status", form.getOrder_status());
					temmap.put("goods_id", tem.get("goods_id"));
					temmap.put("order_id", form.getOrder_id());
					temmap.put("id", (Long) form.getId());
					temmap.put("goods_mainphoto_path", tem.get("goods_mainphoto_path"));
					temmap.put("goods_choice_type", tem.get("goods_choice_type"));
					temmap.put("goods_gsp_ids", tem.get("goods_gsp_ids"));
					temmap.put("goods_return_status", tem.get("goods_return_status")!=null?tem.get("goods_return_status"):"");
					temList.add(temmap);
				}
				//遍历子订单
				List<Map> maps2 = this.orderFormTools.queryGoodsInfo(form.getChild_order_detail());
				Map<String,Object> temmap2 =null;
				List<Object> temList2 = new ArrayList<Object>();
				for(Map tem:maps2){
					OrderForm orderForm = this.orderFormService.getObjById(Long.parseLong((tem.get("order_id")).toString()));
					//List<Map> maps_child_order_info = this.orderFormTools.queryGoodsInfo(tem.get("order_goods_info").toString());
					List<Map> maps_child_order_info = this.orderFormTools.queryGoodsInfo(orderForm.getGoods_info());
					for(Map tem3 :maps_child_order_info){
						temmap2 = new HashMap<String, Object>();
						temmap2.put("id",tem.get("order_id"));
						temmap2.put("original_id", form.getId());
						temmap2.put("order_status", orderForm.getOrder_status());
						temmap2.put("goods_id", tem3.get("goods_id"));
						temmap2.put("order_id", orderForm.getOrder_id());
						temmap2.put("goods_mainphoto_path", tem3.get("goods_mainphoto_path"));
						temmap2.put("goods_return_status", tem3.get("goods_return_status")!=null?tem3.get("goods_return_status"):"");
						temmap2.put("goods_choice_type", tem3.get("goods_choice_type"));
						temmap2.put("goods_gsp_ids", tem3.get("goods_gsp_ids"));
						temList2.add(temmap2);
					}
				}
				temList.addAll(temList2);
				map.put("info", temList);
				result.add(map);
			}
		}
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("data", result);
		mv.addObject("orderFormTools", orderFormTools);
		return mv;
	}

	/**
	 * 确认主订单没有收货，子订单已经确认收货
	 * @param form
	 * @return
	 */
	private boolean checkChildConfirm(OrderForm form) {
		boolean flag = false;
		if(form!=null){
			List<Map> maps = this.orderFormTools.queryGoodsInfo(form.getChild_order_detail());
			for(Map map:maps){
				OrderForm orderForm = this.orderFormService.getObjById(Long.parseLong((map.get("order_id")).toString()));
				if(orderForm.getOrder_status()>=40){
					flag = true;
					break;
				}
			}
		}
		return flag;
	}

	@SecurityMapping(title = "生活购退款列表", value = "/buyer/group_life_return.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/group_life_return.htm")
	public ModelAndView group_life_return(HttpServletRequest request,
			HttpServletResponse response, String id, String view,
			String currentPage, String order_id, String beginTime,
			String endTime) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/group_life_return.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		GroupInfoQueryObject giqo = new GroupInfoQueryObject(currentPage, mv,
				"addTime", "desc");
		giqo.addQuery("obj.user_id", new SysMap("user_id", SecurityUserHolder
				.getCurrentUser().getId()), "=");// 当前用户
		giqo.addQuery("obj.status", new SysMap("status", 1), "!=");
		giqo.addQuery("obj.status", new SysMap("status", -1), "!=");
		giqo.addQuery("obj.refund_Time", new SysMap("refund_Time", new Date()),
				">=");
		IPageList pList = this.groupinfoService.list(giqo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}

	@SecurityMapping(title = "生活购退款申请", value = "/buyer/group_life_return_apply.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/group_life_return_apply.htm")
	public ModelAndView group_life_return_apply(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/group_life_return_apply.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		GroupInfo obj = this.groupinfoService
				.getObjById(CommUtil.null2Long(id));
		if (obj.getUser_id()
				.equals(SecurityUserHolder.getCurrentUser().getId())) {
			mv.addObject("obj", obj);
		}
		return mv;
	}

	@SecurityMapping(title = "生活购退款申请取消", value = "/buyer/grouplife_return_apply_cancel.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/grouplife_return_apply_cancel.htm")
	public String grouplife_return_apply_cancel(HttpServletRequest request,
			HttpServletResponse response, String id) {
		GroupInfo obj = this.groupinfoService
				.getObjById(CommUtil.null2Long(id));
		if (obj.getUser_id()
				.equals(SecurityUserHolder.getCurrentUser().getId())) {
			obj.setStatus(0);
			this.groupinfoService.update(obj);
		}
		return "redirect:group_life_return.htm";
	}

	@SecurityMapping(title = "生活购退款申请保存", value = "/buyer/grouplife_return_apply_save.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/grouplife_return_apply_save.htm")
	public String grouplife_return_apply_save(HttpServletRequest request,
			HttpServletResponse response, String id,
			String return_group_content, String reasion) throws Exception {
		GroupInfo obj = this.groupinfoService
				.getObjById(CommUtil.null2Long(id));
		if (obj != null) {
			if (obj.getUser_id().equals(
					SecurityUserHolder.getCurrentUser().getId())) {// 订单为生活购订单
				String mark = "";
				if (reasion != null && !reasion.equals("")) {
					String rs_ids[] = reasion.split(",");
					for (String rid : rs_ids) {
						if (!rid.equals("")) {
							if (rid.equals("1")) {
								mark = "买错了/重新买";
							} else if (rid.equals("2")) {
								mark = "计划有变，没时间消费";
							} else if (rid.equals("3")) {
								mark = "预约不上";
							} else if (rid.equals("4")) {
								mark = "去过了，不太满意";
							} else if (rid.equals("5")) {
								mark = "其他";
							}
						}
						obj.setStatus(3);// 申请退款中
						obj.setRefund_reasion(mark + "[" + return_group_content
								+ "]");// 退款说明
						this.groupinfoService.update(obj);
						OrderForm order = this.orderFormService.getObjById(obj
								.getOrder_id());
						if (this.configService.getSysConfig().isEmailEnable()) {
							if (order.getOrder_form() == 0) {
								User seller = this.userService
										.getObjById(this.storeService
												.getObjById(
														CommUtil.null2Long(order
																.getStore_id()))
												.getUser().getId());
								Map map = new HashMap();
								map.put("buyer_id", SecurityUserHolder
										.getCurrentUser().getId().toString());
								map.put("seller_id", seller.getId().toString());
								String map_json = Json.toJson(map);
								this.sendMsgAndEmTools
										.sendEmail(
												CommUtil.getURL(request),
												TytsmsStringUtils.generatorFilesFolderServerPath(request) ,
												"email_toseller_order_refund_apply_notify",
												seller.getEmail(), map_json);
							}
						}
						if (this.configService.getSysConfig().isSmsEnbale()) {
							if (order.getOrder_form() == 0) {
								User seller = this.userService
										.getObjById(this.storeService
												.getObjById(
														CommUtil.null2Long(order
																.getStore_id()))
												.getUser().getId());
								Map map = new HashMap();
								map.put("buyer_id", SecurityUserHolder
										.getCurrentUser().getId().toString());
								map.put("seller_id", seller.getId().toString());
								String map_json = Json.toJson(map);
								String url = CommUtil.getURL(request);
								String path = TytsmsStringUtils.generatorFilesFolderServerPath(request);
								this.sendMsgAndEmTools
										.sendMsg(
												url,
												path,
												"sms_toseller_order_refund_apply_notify",
												seller.getMobile(), map_json);
							}
						}
					}
				}
			}
		}
		return "redirect:group_life_return.htm";
	}

	@SecurityMapping(title = "买家退货申请列表记录", value = "/buyer/order_return_listlog.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/order_return_listlog.htm")
	public ModelAndView order_return_listlog(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/order_return_listlog.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		ReturnGoodsLogQueryObject qo = new ReturnGoodsLogQueryObject(
				currentPage, mv, "addTime", "desc");
		WebForm wf = new WebForm();
		wf.toQueryPo(request, qo, ReturnGoodsLog.class, mv);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		qo.addQuery("obj.user_id", new SysMap("user_id", user.getId()), "=");
		qo.addQuery("obj.goods_return_status", new SysMap(
				"goods_return_status", "1"), "is not");
		IPageList pList = this.returnGoodsLogService.list(qo);
		
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("orderFormTools", orderFormTools);
		return mv;
	}

	
	/*private void checkTimeOut() {
		int auto_order_return = this.configService.getSysConfig().getAuto_order_return();
		Map<String,Object> params  = new HashMap<String, Object>();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, -auto_order_return);
		params.put("return_shipTime", cal.getTime());
		params.put("order_status", 40);
		List<OrderForm> confirm_return_ofs = this.orderFormService.query("select obj from OrderForm obj where obj.return_shipTime<=:return_shipTime and obj.order_status>=:order_status",
						params, -1, -1);
		for (OrderForm order : confirm_return_ofs) {
			List<Map> maps = this.orderFormTools.queryGoodsInfo(order.getGoods_info());
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
		}
	}*/
	

	@SecurityMapping(title = "买家退货填写物流", value = "/buyer/order_returnlog_view.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/order_returnlog_view.htm")
	public ModelAndView order_returnlog_view(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/order_returnlog_view.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		ReturnGoodsLog obj = this.returnGoodsLogService.getObjById(CommUtil
				.null2Long(id));
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		if (obj.getUser_id().equals(user.getId())) {
			if (obj.getGoods_return_status().equals("6")
					|| obj.getGoods_return_status().equals("7")) {
				Map params = new HashMap();
				params.put("status", 0);
				List<ExpressCompany> expressCompanys = this.expressCompayService
						.query("select obj from ExpressCompany obj where obj.company_status=:status order by company_sequence asc",
								params, -1, -1);
				mv.addObject("expressCompanys", expressCompanys);
				mv.addObject("obj", obj);
				mv.addObject("user", user);
				OrderForm of = this.orderFormService.getObjById(obj
						.getReturn_order_id());
				long dayMatch=0l;
				if(of.getReturn_shipTime()!=null&&!of.getReturn_shipTime().equals("")){
					dayMatch=DateUtils.getDays(of.getReturn_shipTime().toString(),DateUtils.getCurSqlDate().toString());
				}
				mv.addObject("dayMatch",dayMatch);
				mv.addObject("of", of);
				Goods goods = this.goodsService.getObjById(obj.getGoods_id());
				if (goods.getGoods_type() == 1) {
					mv.addObject("name", goods.getGoods_store().getStore_name());
					mv.addObject("store_id", goods.getGoods_store().getId());
				} else {
					mv.addObject("name", "平台商");
				}
				//bigen cty 修改时间2015-3-17增加内容
				Store store = this.storeService.getObjById(obj.getStore_id());
				if(store != null){ //收货人
					Map<String,Object> areaMap = CommUtil.getAreaInfo(store.getArea(),1);
					if(areaMap.size()>0){
						mv.addObject("consigneeName", areaMap.get("areaName")); 
						mv.addObject("consigneeid",new Long(areaMap.get("areaId").toString()));
					}
				}
                if(user.getAddrs() != null){//发货人
                	Map<String,Object> areaMap = null;
    				for (int i = 0; i < user.getAddrs().size(); i++) {
    					areaMap = CommUtil.getAreaInfo(user.getAddrs().get(i).getArea(),1);
    					if(of.getReceiver_area().equals(areaMap.get("areaName"))){
    						mv.addObject("areaId",new Long(areaMap.get("areaId").toString()));
    						areaMap = new HashMap();
    						break;
    					}
    				}
    				if(areaMap.size() > 0){
    					mv.addObject("areaId",new Long(areaMap.get("areaId").toString()));
    				}
                }
				List<Area> areas = this.areaService.query(
						"select obj from Area obj where obj.parent.id is null", null,
						-1, -1);
				mv.addObject("areas", areas);
				//end
				if (obj.getGoods_return_status().equals("7")) {
					//bigen cty 修改时间2015-3-17增加物流查询
                    OrderForm order = this.orderFormService.getObjById(obj.getReturn_order_id());
					HttpclientLogisticsUtil logistics = new HttpclientLogisticsUtil();
					Map jsonMap = new HashMap();
					jsonMap.put("tytordertyte", obj.getFefund_type()); //订单类型
					jsonMap.put("pickexpressno", obj.getExpress_code()); //物流单号
					jsonMap.put("pickexpresscom", this.orderFormTools.queryExInfo(obj.getReturn_express_info(),"express_company_mark")); //快递代码
					JSONObject json = new JSONObject().fromObject(jsonMap);
					TransInfo transInfo = logistics.searchLogisticsInfo(json.toString());
					transInfo.setExpress_company_name(this.orderFormTools
							.queryExInfo(obj.getReturn_express_info(),
									"express_company_name"));
					transInfo.setExpress_ship_code(obj.getExpress_code());
					if(transInfo.getData().size() >0){
						transInfo.setStatus("1");
					}else{
						transInfo.setStatus("2");
						transInfo.setMessage("系统繁忙，请稍后在试!");
					}
					mv.addObject("transInfo", transInfo);
					//end cty 修改时间2015-3-17增加物流查询
//					TransInfo transInfo = this
//							.query_Returnship_getData(CommUtil.null2String(obj
//									.getId()));
//					mv.addObject("transInfo", transInfo);
//					Map map = Json.fromJson(HashMap.class,
//							obj.getReturn_express_info());
//					mv.addObject("express_company_name",
//							map.get("express_company_name"));
				}

			} else {
				mv = new JModelAndView("error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "当前状态无法对退货服务单进行操作");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/buyer/order_return_listlog.htm");
			}
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您没有为" + obj.getReturn_service_id()
					+ "的退货服务号！");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/buyer/order_return_listlog.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "买家退货申请列表记录", value = "/buyer/order_cancel_listlog.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/order_cancel_listlog.htm")
	public ModelAndView order_cancel_listlog(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/order_cancel_listlog.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderFormQueryObject ofqo = new OrderFormQueryObject(currentPage, mv,
				"addTime", "desc");
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		ofqo.addQuery("obj.user_id", new SysMap("user_id", SecurityUserHolder
				.getCurrentUser().getId().toString()), "=");
		ofqo.addQuery("obj.order_main", new SysMap("order_main", 1), "=");
		ofqo.addQuery("obj.order_status", new SysMap("order_status", 0), "=");
		IPageList pList = this.orderFormService.list(ofqo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("orderFormTools", orderFormTools);
		return mv;
	}
	
	
	@SecurityMapping(title = "检测退货时间ajax", value = "/buyer/order_cancel_listlog.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/ajax_check_overtime.htm")
	public void ajax_check_overtime(HttpServletRequest request,
			HttpServletResponse response, String id) {
		boolean flag = false;
		int auto_order_return = this.configService.getSysConfig()
				.getAuto_order_return();
		Calendar cal = Calendar.getInstance();
		cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, -auto_order_return);
		Date return_shipTime = cal.getTime();
		
		ReturnGoodsLog obj = this.returnGoodsLogService.getObjById(CommUtil.null2Long(id));
		OrderForm of = this.orderFormService.getObjById(obj.getReturn_order_id());
		if(return_shipTime.after(of.getReturn_shipTime())){
			flag = true;
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(flag);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	

	@SecurityMapping(title = "物流ajax", value = "/buyer/ship_ajax.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/ship_ajax.htm")
	public ModelAndView ship_ajax(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String order_id) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/ship_ajax.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm order = this.orderFormService.getObjById(CommUtil
				.null2Long(order_id));
		//bigen cty 修改时间：2015-03-19
		List<TransInfo> transInfo_list = new ArrayList<TransInfo>();
		HttpclientLogisticsUtil logistics = new HttpclientLogisticsUtil();
		Map jsonMap = new HashMap();
		jsonMap.put("tytordertyte", order.getShipper_type()); //订单类型
		jsonMap.put("pickexpressno", order.getShipCode()); //物流单号
		jsonMap.put("pickexpresscom", this.orderFormTools.queryExInfo(order.getExpress_info(),"express_company_mark")); //快递代码
		JSONObject json = new JSONObject().fromObject(jsonMap);
		TransInfo transInfo = logistics.searchLogisticsInfo(json.toString());
		transInfo.setExpress_company_name(this.orderFormTools
				.queryExInfo(order.getExpress_info(),
						"express_company_name"));
		transInfo.setExpress_ship_code(order.getShipCode());
		if(transInfo.getData().size() >0){
			transInfo.setStatus("1");
		}else{
			transInfo.setStatus("2");
			transInfo.setMessage("系统繁忙，请稍后在试!");
		}
		transInfo_list.add(transInfo);
		//end cty 修改时间：2015-03-19
//		TransInfo transInfo = this.query_ship_getData(CommUtil
//				.null2String(order_id));
//		if (transInfo != null) {
//			transInfo.setExpress_company_name(this.orderFormTools.queryExInfo(
//					order.getExpress_info(), "express_company_name"));
//			transInfo.setExpress_ship_code(order.getShipCode());
//			transInfo_list.add(transInfo);
//		}
		if (order.getOrder_main() != 1
				&& !CommUtil.null2String(order.getChild_order_detail()).equals(
						"")) {// 查询子订单的物流跟踪信息
			List<Map> maps = this.orderFormTools.queryGoodsInfo(order
					.getChild_order_detail());
			for (Map child_map : maps) {
				OrderForm child_order = this.orderFormService
						.getObjById(CommUtil.null2Long(child_map
								.get("order_id")));
				TransInfo transInfo1 = this.query_ship_getData(CommUtil
						.null2String(child_order.getId()));
				if (transInfo1 != null) {
					transInfo1.setExpress_company_name(this.orderFormTools
							.queryExInfo(child_order.getExpress_info(),
									"express_company_name"));
					transInfo1.setExpress_ship_code(child_order.getShipCode());
					transInfo_list.add(transInfo1);
				}
			}

		}
		mv.addObject("transInfo_list", transInfo_list);
		return mv;
	}

	@SecurityMapping(title = "买家已经购买商品列表", value = "/buyer/buy_goods_finish.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/buy_goods_finish.htm")
	public ModelAndView buy_goods_finish(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/buy_goods_finish.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		Calendar cal = Calendar.getInstance();
		cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, -7);
		Map params = new HashMap();
		params.put("user_id", user.getId().toString());
		params.put("status", 50);
		params.put("addTime", cal.getTime());
		List<OrderForm> orderForms = this.orderFormService
				.query("select obj from OrderForm obj where obj.addTime>=:addTime and obj.user_id=:user_id and obj.order_status>=:status order by obj.addTime desc",
						params, -1, -1);
		List<Goods> gds = new ArrayList<Goods>();
		for (OrderForm of : orderForms) {
			List<Goods> goods_list = this.orderFormTools.queryOfGoods(CommUtil
					.null2String(of.getId()));
			gds.addAll(goods_list);
		}
		List<Goods> page_goods = new ArrayList<Goods>();
		if (CommUtil.null2Int(currentPage) <= 0 || "".equals(currentPage)) {
			currentPage = "1";
		}
		for (int i = CommUtil.null2Int(currentPage); i <= i * 5; i++) {
			if (i - 1 < gds.size()) {
				page_goods.add(gds.get(i - 1));
			}
		}
		mv.addObject("page_goods", page_goods);
		mv.addObject("page", page_goods.size());
		mv.addObject("currentPage", CommUtil.null2Int(currentPage));
		return mv;
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
						System.out.println(gg.getGg_selled_count());
						System.out.println(goods_count);
						gg.setGg_selled_count(gg.getGg_selled_count()
								+ goods_count);
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

	private TransInfo query_Returnship_getData(String id) {
		TransInfo info = new TransInfo();
		ReturnGoodsLog obj = this.returnGoodsLogService.getObjById(CommUtil
				.null2Long(id));
		try {
			ExpressCompany ec = this.queryExpressCompany(obj
					.getReturn_express_info());
			String query_url = "http://api.kuaidi100.com/api?id="
					+ this.configService.getSysConfig().getKuaidi_id()
					+ "&com=" + (ec != null ? ec.getCompany_mark() : "")
					+ "&nu=" + obj.getExpress_code()
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
		return info;
	}

	private TransInfo query_ship_getData(String id) {
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
	
	@Transactional
	@SecurityMapping(title = "退货图片", value = "/buyer/ajax_img_refund.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/ajax_img_refund.htm")
	public void ajax_img_upload(HttpServletRequest request,
			HttpServletResponse response) throws FileUploadException {

		HttpSession session = request.getSession();
		int count = 1;
		AtomicInteger img_num = (AtomicInteger) session.getAttribute("img_num");
		response.setContentType("text/plain;charset=UTF-8");
		PrintWriter writer;
		try {
			if(img_num == null){
				count = 1;
			}else{
				count += img_num.intValue();
			}	
			if(count >5){
				String res = "{\"num\":"+6+"}";
				writer = response.getWriter();
				writer.print(res);
			}else{
			    String uploadFilePath = ConfigContants.UPLOAD_IMAGE_MIDDLE_NAME+"/"+"refund";
				String saveFilePathName = TytsmsStringUtils.generatorImagesFolderServerPath(request)
						+ uploadFilePath;
				File file = new File(saveFilePathName);
				if(!file.exists()){
					file.mkdirs();
				}
				Map map = new HashMap();
				map = CommUtil.saveFileToServer(configService,request, "refund_img", saveFilePathName, "", null);
				if(map.size() > 0){
					String reg = ".+("+configService.getSysConfig().getImageSuffix()+")$";
					String imgp = (String) map.get("fileName");
					Pattern pattern = Pattern.compile(reg);
					Matcher matcher = pattern.matcher(imgp.toLowerCase());
					if (matcher.find()) {
						//CommUtil.del_acc(request, old_photo);
						if (map.get("fileName") != "") {
		 					String refund_img = uploadFilePath+"/"+map.get("fileName");
							session.setAttribute("refund_img_"+count, refund_img);
							session.setAttribute("img_num", new AtomicInteger(count));
							String files = CommUtil.getURL(request) + "/"+refund_img;
							String img_html = "";
							for (int i = 1; i < count+1; i++) {
								img_html += "<li><a target='_blank' id='refund_href"+i+"' href='"+CommUtil.getURL(request) + "/"+session.getAttribute("refund_img_"+i)+"'> <img id='refund_img_"+i+"' width='55' height='55' src='"+CommUtil.getURL(request) + "/"+session.getAttribute("refund_img_"+i)+"' /></a> <b onclick='delete_return_img("+i+")'>X</b></li>";
							}
							String res = "{\"num\":"+count+",\"img_html\":\""+img_html+"\"}";
							writer = response.getWriter();
							writer.print(res);
						}else {
							String error = "error";
							String res = "{\"error\":\""+error+"\"}";
							writer = response.getWriter();
							writer.print(res);
						}
					}
				}else{
					String error = "error";
					String res = "{\"error\":\""+error+"\"}";
					writer = response.getWriter();
					writer.print(res);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SecurityMapping(title = "删除退货图片", value = "/buyer/refund_image_del.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/refund_image_del.htm")
	public void refund_image_del(HttpServletRequest request,
			HttpServletResponse response,String image_id) {
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			HttpSession session = request.getSession();
			AtomicInteger img_num = (AtomicInteger)session.getAttribute("img_num");
			if(img_num != null){
				String refund_img = (String)session.getAttribute("refund_img_"+image_id);
			    CommUtil.deleteFile(CommUtil.getURL(request) + "/"+refund_img);
			    session.removeAttribute(refund_img);
			    session.removeAttribute(image_id);
			    int id = Integer.valueOf(image_id);
			    int j = 0;
				for (int i = 1; i < img_num.intValue(); i++) {
					j++;
					if(i == id){
						i--;
						id--;
						continue;
					}
					session.setAttribute("refund_img_"+i, session.getAttribute("refund_img_"+j));
				}
				session.setAttribute("img_num", new AtomicInteger(img_num.intValue()-1));
				AtomicInteger count = (AtomicInteger)session.getAttribute("img_num");
				String img_html = "";
				for (int i = 1; i < count.intValue()+1; i++) {
					img_html += "<li><a target='_blank' id='refund_href"+i+"' href='"+CommUtil.getURL(request) + "/"+session.getAttribute("refund_img_"+i)+"'> <img id='refund_img_"+i+"' width='55' height='55' src='"+CommUtil.getURL(request) + "/"+session.getAttribute("refund_img_"+i)+"' /></a> <b onclick='delete_return_img("+i+")'>X</b></li>";
				}
				Map map = new HashMap();
				map.put("num", count);
				map.put("result", true);
				map.put("img_html", img_html);
				writer = response.getWriter();
				writer.print(Json.toJson(map));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@SecurityMapping(title = "买家退货申请记录详情", value = "/buyer/returnlog_details.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/returnlog_details.htm")
	public ModelAndView returnlog_details(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/returnlog_details.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		ReturnGoodsLog obj = this.returnGoodsLogService.getObjById(CommUtil
				.null2Long(id));
		if(obj.getGoods_return_status().equals("7") || obj.getGoods_return_status().equals("10")){
			mv.addObject("express_company_name", this.orderFormTools.queryExInfo(obj.getReturn_express_info(),"express_company_name"));
		}
		if(obj.getRefund_img() !=null){
			String[] refund_img = obj.getRefund_img().split(",");
			mv.addObject("refund_img", refund_img);
		}
		mv.addObject("obj",obj);
		return mv;
	}
}
