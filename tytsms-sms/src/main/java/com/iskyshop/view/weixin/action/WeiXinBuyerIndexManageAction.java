package com.iskyshop.view.weixin.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.CouponInfo;
import com.iskyshop.foundation.domain.Favorite;
import com.iskyshop.foundation.domain.Message;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.ICouponInfoService;
import com.iskyshop.foundation.service.IFavoriteService;
import com.iskyshop.foundation.service.IMessageService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.OrderFormTools;
import com.iskyshop.view.web.tools.IntegralViewTools;

/**
 * 
 * <p>
 * Title: MobileBuyerIndexViewAction.java
 * </p>
 * 
 * <p>
 * Description: 手机端用户中心
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
 * @date 2014-7-25
 * 
 * @version 1.0
 */
@Controller
public class WeiXinBuyerIndexManageAction extends WeiXinBaseAction{
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserService userService;
	
	@Autowired
	private IUserConfigService userConfigService;

	@Autowired
	private IntegralViewTools integralViewTools;
	@Autowired
	private ICouponInfoService couponInfoService;
	@Autowired
	private OrderFormTools orderformTools;
	@Autowired
	private IFavoriteService favoriteService;
	@Autowired
	private IMessageService messageService;
	
	@Autowired
	private IOrderFormService orderFormService;

	/**
	 * 用户中心首页
	 * 
	 * @param request
	 * @param response
	 * @param id
	 */
	@RequestMapping("/weiXin/buyer/index.htm")
	public ModelAndView buyer_index(HttpServletRequest request,
			HttpServletResponse response) {
		HttpSession session = request.getSession();
		ModelAndView mv = new JModelAndView("weiXin/view/buyer/account.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
		boolean verify = CommUtil.null2Boolean(request.getHeader("verify"));
		verify = true;
		
		String user_id = "" ;
	
		Map map = checkLogin(request, user_id);
		User user = null ;
	   	if(map.get("user_id") != null){
	   		user_id = map.get("user_id").toString();
	   	}
	   	if(map.get("user") != null){
	   		user =  (User) map.get("user");
	   	}
				
		Map map_list = new HashMap();
		if (verify && user_id != null && !user_id.equals("")) {
			if(user == null){
				user = this.userService.getObjById(CommUtil.null2Long(user_id));
			}
			if (user != null) {
					String balance = CommUtil.null2String(user
							.getAvailableBalance());
					if (balance.equals("")) {
						balance = "0";
					}
					String level_name = integralViewTools
							.query_user_level_name(CommUtil.null2String(user
									.getId()));
					String photo_url = CommUtil.getURL(request)
							+ "/"
							+ this.configService.getSysConfig().getMemberIcon()
									.getPath()
							+ "/"
							+ this.configService.getSysConfig().getMemberIcon()
									.getName();
					if (user.getPhoto() != null) {
						photo_url = CommUtil.getURL(request) + "/"
								+ user.getPhoto().getPath() + "/"
								+ user.getPhoto().getName();
					}
					map_list.put("name", user.getUserName());
					map_list.put("photo_url", photo_url);
					map_list.put("level_name", level_name);
					map_list.put("balance", balance);
					map_list.put("user_id", user_id);
					Map params = new HashMap();
					params.put("user_id", CommUtil.null2Long(user_id));
					params.put("status", 0);
					List<CouponInfo> couponinfos = this.couponInfoService
							.query("select obj from CouponInfo obj where obj.user.id=:user_id and obj.status=:status",
									params, -1, -1);
					map_list.put("coupon",
							CommUtil.null2String(couponinfos.size()));
					map_list.put("integral",
							CommUtil.null2String(user.getIntegral()));
					
					// 查询订单信息
					int[] status = new int[] { 10, 20,30, 40 }; // 已提交 已发货 已完成
					for (int i = 0; i < status.length; i++) {
						int size = this.orderFormService.query(
								"select obj.id from OrderForm obj where obj.order_cat!=2 and obj.user_id="
										+ user.getId().toString()
										+ " and obj.order_status =" + status[i] + "", null,
								-1, -1).size();
						mv.addObject("order_size_" + status[i], size);
					}
					List<OrderForm> orderForms = this.orderFormService.query(
							"select obj from OrderForm obj where obj.order_cat!=2 and obj.user_id="
									+ user.getId().toString()
									+ " order by obj.addTime desc", null, 0, 3);
					mv.addObject("orderForms", orderForms);
					
					mv.addObject("user", user);
					
					
				} else {
					verify = false;
				}
		} else {
			verify = false;
		}
		map_list.put("ret", CommUtil.null2String(verify));
		String json = Json.toJson(map_list, JsonFormat.compact());
		mv.addObject("map_list" ,map_list);
		System.out.println("map_list:" + map_list);
		mv.addObject("user_id", user_id);
		return mv;
	}

	/**
	 * 手机端用户优惠券
	 * 
	 * @param request
	 * @param response
	 * @param id
	 */
	@RequestMapping("/weiXin/buyer/coupon.htm")
	public void buyer_coupon(HttpServletRequest request,
			HttpServletResponse response, String token,
			String beginCount, String selectCount) {
		boolean verify = CommUtil.null2Boolean(request.getHeader("verify"));
		Map json_map = new HashMap();
		List coupon_list = new ArrayList();
		
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
					Map params = new HashMap();
					params.put("user_id", CommUtil.null2Long(user_id));
					params.put("status", 0);
					List<CouponInfo> couponinfos = this.couponInfoService
							.query("select obj from CouponInfo obj where obj.user.id=:user_id and obj.status=:status",
									params, CommUtil.null2Int(beginCount),
									CommUtil.null2Int(selectCount));
					for (CouponInfo ci : couponinfos) {
						Map map = new HashMap();
						map.put("coupon_sn", ci.getCoupon_sn());
						map.put("coupon_addTime", ci.getAddTime());
						String status = "未使用";
						if (ci.getStatus() == 1) {
							status = "已使用";
						}
						if (ci.getStatus() == -1) {
							status = "已过期";
						}
						map.put("coupon_status", status);
						map.put("coupon_amount", ci.getCoupon()
								.getCoupon_amount());
						map.put("coupon_order_amount", ci.getCoupon()
								.getCoupon_order_amount());
						map.put("coupon_beginTime", ci.getCoupon()
								.getCoupon_begin_time());
						map.put("coupon_endTime", ci.getCoupon()
								.getCoupon_end_time());
						map.put("coupon_id", ci.getId());
						map.put("coupon_name", ci.getCoupon().getCoupon_name());
						map.put("coupon_info", "优惠"
								+ ci.getCoupon().getCoupon_amount() + "元");
						coupon_list.add(map);
					}
					json_map.put("coupon_list", coupon_list);
				} else {
					verify = false;
				}
			} else {
				verify = false;
			}
		} else {
			verify = false;
		}
		json_map.put("ret", CommUtil.null2String(verify));
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
	 * 手机端用户收藏的商品
	 * 
	 * @param request
	 * @param response
	 * @param id
	 */
	@RequestMapping("/weiXin/buyer/favorite.htm")
	public void buyer_favorite(HttpServletRequest request,
			HttpServletResponse response, String token,
			String beginCount, String selectCount) {
		boolean verify = CommUtil.null2Boolean(request.getHeader("verify"));
		Map json_map = new HashMap();
		List coupon_list = new ArrayList();
		
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
					Map params = new HashMap();
					params.put("user_id", CommUtil.null2Long(user_id));
					params.put("type", 0);
					List<Favorite> favs = this.favoriteService
							.query("select obj from Favorite obj where obj.user.id=:user_id and obj.type=:type",
									params, CommUtil.null2Int(beginCount),
									CommUtil.null2Int(selectCount));
					String url = CommUtil.getURL(request);
					for (Favorite fav : favs) {
						Map map = new HashMap();
						map.put("goods_id", fav.getGoods().getId());
						String goods_main_photo = url
								+ "/"
								+ this.configService.getSysConfig()
										.getGoodsImage().getPath()
								+ "/"
								+ this.configService.getSysConfig()
										.getGoodsImage().getName();
						if (fav.getGoods().getGoods_main_photo() != null) {// 商品主图片
							goods_main_photo = url
									+ "/"
									+ fav.getGoods().getGoods_main_photo()
											.getPath()
									+ "/"
									+ fav.getGoods().getGoods_main_photo()
											.getName()
									+ "_small."
									+ fav.getGoods().getGoods_main_photo()
											.getExt();
						}
						map.put("goods_photo", goods_main_photo);
						map.put("id", fav.getGoods().getId());
						map.put("name", fav.getGoods().getGoods_name());
						map.put("price", fav.getGoods()
								.getGoods_current_price());
						map.put("addTime", fav.getAddTime());
						coupon_list.add(map);
					}
					json_map.put("coupon_list", coupon_list);
				} else {
					verify = false;
				}
			} else {
				verify = false;
			}
		} else {
			verify = false;
		}
		json_map.put("ret", CommUtil.null2String(verify));
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
	 * 手机端用户的消息
	 * 
	 * @param request
	 * @param response
	 * @param id
	 */
	@RequestMapping("/weiXin/buyer/message.htm")
	public void buyer_message(HttpServletRequest request,
			HttpServletResponse response, String token,
			String beginCount, String selectCount) {
		
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
		Map json_map = new HashMap();
		List msg_list = new ArrayList();
		if (verify && user_id != null && !user_id.equals("") && token != null
				&& !token.equals("")) {
			user = this.userService
					.getObjById(CommUtil.null2Long(user_id));
			if (user != null) {
				if (user.getApp_login_token().equals(token.toLowerCase())) {
					Map params = new HashMap();
					params.put("user_id", CommUtil.null2Long(user_id));
					List<Message> msgs = this.messageService
							.query("select obj from Message obj where obj.toUser.id=:user_id order by addTime desc",
									params, CommUtil.null2Int(beginCount),
									CommUtil.null2Int(selectCount));
					for (Message obj : msgs) {
						Map map = new HashMap();
						map.put("title", obj.getTitle());
						map.put("content", obj.getContent());
						map.put("addTime", obj.getAddTime());
						map.put("fromUser", obj.getFromUser().getUserName());
						msg_list.add(map);
					}
					json_map.put("msg_list", msg_list);
				} else {
					verify = false;
				}
			} else {
				verify = false;
			}
		} else {
			verify = false;
		}
		json_map.put("ret", CommUtil.null2String(verify));
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

}
