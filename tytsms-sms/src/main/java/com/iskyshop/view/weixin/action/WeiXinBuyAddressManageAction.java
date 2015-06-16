package com.iskyshop.view.weixin.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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
import com.iskyshop.foundation.domain.Address;
import com.iskyshop.foundation.domain.Area;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IAddressService;
import com.iskyshop.foundation.service.IAreaService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;

/**
 * 
 * <p>
 * Title: MobileBuyAddressManageAction.java
 * </p>
 * 
 * <p>
 * Description: 手机端用户中心用户收货地址管理控制器
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
 * @date 2014-8-14
 * 
 * @version 1.0
 */
@Controller
public class WeiXinBuyAddressManageAction  extends WeiXinBaseAction{

	@Autowired
	private IAddressService addressService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private IUserService userService;
	
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;

	/**
	 * 手机端买家收货地址列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/weiXin/buyer_address.htm")
	public ModelAndView buyer_address(HttpServletRequest request,
			HttpServletResponse response,  String token) {
		
		ModelAndView mv = new JModelAndView("weiXin/view/buyer/address.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
		String user_id = "";
		Map userMap = checkLogin(request, user_id);
	   	if(userMap.get("user_id") != null){
	   		user_id = userMap.get("user_id").toString();
	   	}
	   	User user = null ;
		if(userMap.get("user") != null){
	   		user =  (User) userMap.get("user");
	   	}
		
		Map json_map = new HashMap();
		boolean verify = CommUtil.null2Boolean(request.getHeader("verify"));
		verify = true ;
		List map_list = new ArrayList();
		if (verify && user_id != null && !user_id.equals("")) {
			user = this.userService
					.getObjById(CommUtil.null2Long(user_id));
			if (user != null) {
					Map params = new HashMap();
					params.put("user_id", CommUtil.null2Long(user_id));
					List<Address> addrs = this.addressService
							.query("select obj from Address obj where obj.user.id=:user_id order by obj.addTime desc",
									params, -1, -1);
					int default_mark = 0;
					for (Address addr : addrs) {
						if (addr.getDefault_val() == 1) {
							default_mark++;
						}
						Map map = new HashMap();
						map.put("addr_id", addr.getId());
						if (addr.getDefault_val()==1) {
							map.put("default", true);
						}
						map.put("trueName", addr.getTrueName());
						map.put("areaInfo", addr.getArea_info());
						map.put("zip", addr.getZip());
						map.put("mobile", addr.getMobile());
						map.put("telephone", addr.getTelephone());
						

						map.put("area", Area.getAreaInfo(addr.getArea(),","));
						
						map_list.add(map);
					}
					if (default_mark == 0 && addrs.size() > 0) {
						addrs.get(0).setDefault_val(1);
						this.addressService.update(addrs.get(0));
					}
			} else {
				verify = false;
			}
		} else {
			verify = false;
		}
		json_map.put("user_id", user_id);
		json_map.put("address_list", map_list);
		json_map.put("verify", verify);
		mv.addObject("json_map", json_map);
		System.out.println("json_map:" + json_map);
		return mv;
	}
	
	
	/**
	 * 手机端买家收货地址列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/weiXin/buyer_address_add.htm")
	public ModelAndView buyer_address_add(HttpServletRequest request,
			HttpServletResponse response, String token) {
		
		ModelAndView mv = new JModelAndView("weiXin/view/buyer/address_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
		return mv;
	}
	
	/**
	 * 手机端买家收货地址列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/weiXin/buyer_address_edit.htm")
	public ModelAndView buyer_address_edit(HttpServletRequest request,
			HttpServletResponse response,String user_id , String addr_id, String token) {
		
		ModelAndView mv = new JModelAndView("weiXin/view/buyer/address_edit.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Address address = new Address();
		if (addr_id != null && !addr_id.equals("")) {
			address = this.addressService.getObjById(CommUtil
					.null2Long(addr_id));
		} 
		List<Area> areas = this.areaService.query(
				"select obj from Area obj where obj.parent.id is null", null,
				-1, -1);
		mv.addObject("areas", areas);
		mv.addObject("user_id", user_id);
		System.out.println("address:" + address);
		mv.addObject("address", address);
		return mv;
	}
	

	/**
	 * 手机端买家收货地址新增
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/weiXin/buyer_address_save.htm")
	public void buyer_address_save(HttpServletRequest request,
			HttpServletResponse response, String token,
			String trueName, String zip, String telephone, String mobile,
			String area_id, String area_info, String addr_id,String referrerUrl) {
		
		String user_id = "";
		Map userMap = checkLogin(request, user_id);
	   	if(userMap.get("user_id") != null){
	   		user_id = userMap.get("user_id").toString();
	   	}
	   	User user = null ;
		if(userMap.get("user") != null){
	   		user =  (User) userMap.get("user");
	   	}
		
		Map json_map = new HashMap();
		boolean verify = CommUtil.null2Boolean(request.getHeader("verify"));
		verify = true;
		List map_list = new ArrayList();
		if (verify && user_id != null && !user_id.equals("")&& area_id != null && !area_id.equals("")) {
			user = this.userService
					.getObjById(CommUtil.null2Long(user_id));
			if (user != null) {
					Area area = this.areaService.getObjById(CommUtil
							.null2Long(area_id));
					Address address = new Address();
					boolean edit = false;
					if (addr_id != null && !addr_id.equals("")) {
						address = this.addressService.getObjById(CommUtil
								.null2Long(addr_id));
						edit = true;
					} else {
						edit = false;
					}
					address.setAddTime(new Date());
					address.setTrueName(trueName);
					address.setArea(area);
					address.setArea_info(area_info);
					address.setMobile(mobile);
					address.setTelephone(telephone);
					address.setUser(user);
					address.setZip(zip);
					this.addressService.save(address);
					Map map = new HashMap();
					map.put("addr_id", address.getId());
					map.put("trueName", address.getTrueName());
					map.put("areaInfo", address.getArea_info());
					map.put("zip", address.getZip());
					map.put("mobile", address.getMobile());
					map.put("telephone", address.getTelephone());
//					map.put("area",address.getArea().getParent().getParent().getParent()
//							.getAreaName()+ address.getArea().getParent().getParent()
//							.getAreaName()
//							+ address.getArea().getParent().getAreaName()
//							+ address.getArea().getAreaName());
					map.put("area", Area.getAreaInfo(address.getArea(),""));
					
					map_list.add(map);
					json_map.put("edit", edit);
				} else {
					verify = false;
				}
		} else {
			verify = false;
		}
		
		try {
			response.sendRedirect(referrerUrl);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
//		json_map.put("verify", verify);
//		json_map.put("user_id", user_id);
//		String json = Json.toJson(json_map, JsonFormat.compact());
//		response.setContentType("text/plain");
//		response.setHeader("Cache-Control", "no-cache");
//		response.setCharacterEncoding("UTF-8");
//		PrintWriter writer;
//		try {
//			writer = response.getWriter();
//			writer.print(json);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

	}

	/**
	 * 手机端买家收货地址设置默认地址，设置默认地址后，手机端下单收货地址栏默认显示默认收货地址
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/weiXin/buyer_address_default_set.htm")
	public void buyer_address_default_set(HttpServletRequest request,
			HttpServletResponse response, String token,
			String addr_id) {
		Map json_map = new HashMap();
		boolean verify = CommUtil.null2Boolean(request.getHeader("verify"));
		List map_list = new ArrayList();
		
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
					params.put("uid", CommUtil.null2Long(user_id));
					List<Address> objs = this.addressService
							.query("select obj from Address obj where obj.user.id=:uid order by addTime desc",
									params, -1, -1);
					int default_mark = 0;
					for (Address obj : objs) {
						if (obj.getId() == CommUtil.null2Long(addr_id)) {
							obj.setDefault_val(1);
							default_mark++;
						} else {
							obj.setDefault_val(0);
						}
						this.addressService.update(obj);
					}
					if (default_mark > 0) {
						verify = true;
					} else {
						verify = false;
					}
				} else {
					verify = false;
				}
			} else {
				verify = false;
			}
		} else {
			verify = false;
		}
		json_map.put("verify", verify);
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
	 * 手机端买家默认收货地址查询
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/weiXin/buyer_address_default.htm")
	public void buyer_address_default(HttpServletRequest request,
			HttpServletResponse response, String token) {
		Map json_map = new HashMap();
		boolean verify = CommUtil.null2Boolean(request.getHeader("verify"));
		List map_list = new ArrayList();
		
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
					params.put("uid", CommUtil.null2Long(user_id));
					params.put("default_val", 1);
					List<Address> objs = this.addressService
							.query("select obj from Address obj where obj.user.id=:uid and obj.default_val=:default_val order by addTime desc",
									params, -1, -1);
					if (objs.size() == 1) {
						json_map.put("addr_id", objs.get(0).getId());
						json_map.put("trueName", objs.get(0).getTrueName());
						json_map.put("areaInfo", objs.get(0).getArea_info());
						json_map.put("zip", objs.get(0).getZip());
						json_map.put("mobile", objs.get(0).getMobile());
						json_map.put("telephone", objs.get(0).getTelephone());
						
						json_map.put("area", Area.getAreaInfo(objs.get(0).getArea(),","));
						
					}
				} else {
					verify = false;
				}
			} else {
				verify = false;
			}
		} else {
			verify = false;
		}
		json_map.put("verify", verify);
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
	 * 手机端买家收货地址删除
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/weiXin/buyer_address_delete.htm")
	public void buyer_address_delete(HttpServletRequest request,
			HttpServletResponse response,  String token,
			String addr_id) {
		Map json_map = new HashMap();
		boolean verify = CommUtil.null2Boolean(request.getHeader("verify"));
		verify = true ;
		List map_list = new ArrayList();
		
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
				verify = this.addressService.delete(CommUtil
							.null2Long(addr_id));
			} else {
				verify = false;
			}
		} else {
			verify = false;
		}
		json_map.put("verify", verify);
		json_map.put("user_id", user_id);
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
