package com.iskyshop.manage.buyer.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.Complaint;
import com.iskyshop.foundation.domain.ComplaintGoods;
import com.iskyshop.foundation.domain.ComplaintSubject;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.ComplaintQueryObject;
import com.iskyshop.foundation.domain.query.OrderFormQueryObject;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IComplaintService;
import com.iskyshop.foundation.service.IComplaintSubjectService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.OrderFormTools;
import com.iskyshop.pay.wechatpay.util.ConfigContants;
import com.iskyshop.pay.wechatpay.util.TytsmsStringUtils;

/**
 * 
 * <p>
 * Title: ComplaintBuyerAction.java
 * </p>
 * 
 * <p>
 * Description: 买家投诉管理器
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
 * @date 2014-6-24
 * 
 * @version iskyshop_b2b2c 1.0
 */
@Controller
public class ComplaintBuyerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IComplaintService complaintService;
	@Autowired
	private IComplaintSubjectService complaintSubjectService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private OrderFormTools orderFormTools;

	@SecurityMapping(title = "买家投诉列表", value = "/buyer/order_complaint_list.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/order_complaint_list.htm")
	public ModelAndView order_complaint_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String order_id) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/order_complaint_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderFormQueryObject ofqo = new OrderFormQueryObject(currentPage, mv,
				"addTime", "desc");
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		ofqo.addQuery("obj.user_id", new SysMap("user_id", SecurityUserHolder
				.getCurrentUser().getId().toString()), "=");
		ofqo.addQuery("obj.order_main", new SysMap("order_main", 1), "=");
		if (!CommUtil.null2String(order_id).equals("")) {
			ofqo.addQuery("obj.order_id", new SysMap("order_id", "%" + order_id
					+ "%"), "like");
			mv.addObject("order_id", order_id);
		}
		ofqo.addQuery("obj.order_status", new SysMap("order_status", 20), ">=");
		IPageList pList = this.orderFormService.list(ofqo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("orderFormTools", orderFormTools);
		return mv;
	}

	@SecurityMapping(title = "买家投诉列表", value = "/buyer/complaint.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/complaint.htm")
	public ModelAndView complaint(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String status) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/buyer_complaint.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		ComplaintQueryObject qo = new ComplaintQueryObject(currentPage, mv,
				"addTime", "desc");
		qo.addQuery("obj.from_user.id", new SysMap("user_id",
				SecurityUserHolder.getCurrentUser().getId()), "=");
		if (!CommUtil.null2String(status).equals("")) {
			qo.addQuery("obj.status",
					new SysMap("status", CommUtil.null2Int(status)), "=");
		}
		IPageList pList = this.complaintService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("status", status);
		return mv;
	}

	@SecurityMapping(title = "买家投诉发起", value = "/buyer/complaint_handle.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/complaint_handle.htm")
	public ModelAndView complaint_handle(HttpServletRequest request,
			HttpServletResponse response, String order_id, String goods_id) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/complaint_handle.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm of = this.orderFormService.getObjById(CommUtil
				.null2Long(order_id));
		if (!of.getUser_id().equals(
				SecurityUserHolder.getCurrentUser().getId().toString())) {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您没有该订单！");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
			return mv;
		}
		Goods goods = this.goodsService
				.getObjById(CommUtil.null2Long(goods_id));
		mv.addObject("of", of);
		mv.addObject("goods", goods);
		mv.addObject("goods_ids", goods_id);
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -this.configService.getSysConfig()
				.getComplaint_time());
		boolean result = true;
		if (of.getOrder_status() == 60) {
			if (of.getFinishTime().before(calendar.getTime())) {
				result = false;
			}
		}
		boolean result1 = true;
		List<ComplaintSubject> subs = this.complaintSubjectService.query(
				"select obj from ComplaintSubject obj where obj.type='buyer'", null, -1, -1);

		if (result) {
			if (result1) {
				if (subs.size() > 0) {
					Complaint obj = new Complaint();
					obj.setFrom_user(SecurityUserHolder.getCurrentUser());
					obj.setStatus(0);
					obj.setType("buyer");
					obj.setOf(of);
					Store store = this.storeService.getObjById(CommUtil
							.null2Long(of.getStore_id()));
					if (store != null) {
						obj.setTo_user(store.getUser());
					}
					mv.addObject("obj", obj);
					mv.addObject("subs", subs);
				}else{
					mv = new JModelAndView("error.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "系统未设置投诉主题不可投诉，请联系客服");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/buyer/index.htm");
				}
			} else {
				mv = new JModelAndView("error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "该订单已经投诉，不允许重复投诉");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/buyer/order.htm");
			}
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "该订单已经超过投诉有效期，不能投诉");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
		}
		mv.addObject("orderFormTools", orderFormTools);
		return mv;
	}

	@Transactional
	@SecurityMapping(title = "买家投诉列表", value = "/buyer/complaint_save.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/complaint_save.htm")
	public ModelAndView complaint_save(HttpServletRequest request,
			HttpServletResponse response, String order_id, String cs_id,
			String from_user_content, String goods_ids, String to_user_id,
			String type, String goods_gsp_ids) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm of = this.orderFormService.getObjById(CommUtil
				.null2Long(order_id));
		List<Map> maps = this.orderFormTools.queryGoodsInfo(of.getGoods_info());
		List<Map> new_maps = new ArrayList<Map>();
		Map gls = new HashMap();
		for (Map m : maps) {
			if (m.get("goods_id").toString().equals(goods_ids)
					&& goods_gsp_ids.equals(m.get("goods_gsp_ids").toString())) {
				m.put("goods_complaint_status", 1);
				gls.putAll(m);
			}
			new_maps.add(m);
		}
		of.setGoods_info(Json.toJson(new_maps));
		this.orderFormService.update(of);
		Complaint obj = new Complaint();
		obj.setAddTime(new Date());
		ComplaintSubject cs = this.complaintSubjectService.getObjById(CommUtil
				.null2Long(cs_id));
		obj.setCs(cs);
		obj.setFrom_user_content(from_user_content);
		obj.setFrom_user(SecurityUserHolder.getCurrentUser());
		obj.setTo_user(this.userService.getObjById(CommUtil
				.null2Long(to_user_id)));
		obj.setType(type);
		obj.setOf(of);
		Goods goods = this.goodsService.getObjById(CommUtil
				.null2Long(goods_ids));
		ComplaintGoods cg = new ComplaintGoods();
		cg.setAddTime(new Date());
		cg.setComplaint(obj);
		cg.setGoods(goods);
		cg.setContent(CommUtil.null2String(request.getParameter("content_"
				+ goods_ids)));
		obj.getCgs().add(cg);
		String uploadFilePath = ConfigContants.UPLOAD_IMAGE_MIDDLE_NAME;
		String saveFilePathName = TytsmsStringUtils.generatorImagesFolderServerPath(request)
				+ uploadFilePath + File.separator + "complaint";
		Map map = new HashMap();
		try {
			map = CommUtil.saveFileToServer(configService,request, "img1", saveFilePathName,
					null, null);
			if (map.get("fileName") != "") {
				Accessory from_acc1 = new Accessory();
				from_acc1.setName(CommUtil.null2String(map.get("fileName")));
				from_acc1.setExt(CommUtil.null2String(map.get("mime")));
				from_acc1.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
						.get("fileSize"))));
				from_acc1.setPath(uploadFilePath + "/complaint");
				from_acc1.setWidth(CommUtil.null2Int(map.get("width")));
				from_acc1.setHeight(CommUtil.null2Int(map.get("height")));
				from_acc1.setAddTime(new Date());
				this.accessoryService.save(from_acc1);
				obj.setFrom_acc1(from_acc1);
			}
			map.clear();
			map = CommUtil.saveFileToServer(configService,request, "img2", saveFilePathName,
					null, null);
			if (map.get("fileName") != "") {
				Accessory from_acc2 = new Accessory();
				from_acc2.setName(CommUtil.null2String(map.get("fileName")));
				from_acc2.setExt(CommUtil.null2String(map.get("mime")));
				from_acc2.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
						.get("fileSize"))));
				from_acc2.setPath(uploadFilePath + "/complaint");
				from_acc2.setWidth(CommUtil.null2Int(map.get("width")));
				from_acc2.setHeight(CommUtil.null2Int(map.get("height")));
				from_acc2.setAddTime(new Date());
				this.accessoryService.save(from_acc2);
				obj.setFrom_acc2(from_acc2);
			}
			map.clear();
			map = CommUtil.saveFileToServer(configService,request, "img3", saveFilePathName,
					null, null);
			if (map.get("fileName") != "") {
				Accessory from_acc3 = new Accessory();
				from_acc3.setName(CommUtil.null2String(map.get("fileName")));
				from_acc3.setExt(CommUtil.null2String(map.get("mime")));
				from_acc3.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
						.get("fileSize"))));
				from_acc3.setPath(uploadFilePath + "/complaint");
				from_acc3.setWidth(CommUtil.null2Int(map.get("width")));
				from_acc3.setHeight(CommUtil.null2Int(map.get("height")));
				from_acc3.setAddTime(new Date());
				this.accessoryService.save(from_acc3);
				obj.setFrom_acc3(from_acc3);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.complaintService.save(obj);
		mv.addObject("op_title", "投诉提交成功");
		mv.addObject("url", CommUtil.getURL(request) + "/buyer/complaint.htm");
		return mv;
	}

	@SecurityMapping(title = "买家查看投诉详情", value = "/buyer/complaint_view.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/complaint_view.htm")
	public ModelAndView complaint_view(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/complaint_view.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Complaint obj = this.complaintService
				.getObjById(CommUtil.null2Long(id));
		if (obj.getFrom_user().getId()
				.equals(SecurityUserHolder.getCurrentUser().getId())
				|| obj.getTo_user().getId()
						.equals(SecurityUserHolder.getCurrentUser().getId())) {
			mv.addObject("obj", obj);
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "参数错误，不存在该投诉");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/buyer/complaint.htm");
		}
		if (obj.getOf().getStore_id() != null) {
			Store store = this.storeService.getObjById(CommUtil.null2Long(obj
					.getOf().getStore_id()));
			mv.addObject("store", store);
		}
		if (obj.getOf().getStore_id() != null) {
			Store store = this.storeService.getObjById(CommUtil.null2Long(obj
					.getOf().getStore_id()));
			mv.addObject("store", store);
		}
		mv.addObject("orderFormTools", orderFormTools);
		return mv;
	}

	@SecurityMapping(title = "买家取消投诉", value = "/buyer/complaint_cancel.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/complaint_cancel.htm")
	public String complaint_cancel(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		this.complaintService.delete(CommUtil.null2Long(id));

		return "redirect:complaint.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "投诉图片", value = "/buyer/complaint_img.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/complaint_img.htm")
	public ModelAndView complaint_img(HttpServletRequest request,
			HttpServletResponse response, String id, String type) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/complaint_img.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Complaint obj = this.complaintService
				.getObjById(CommUtil.null2Long(id));
		mv.addObject("type", type);
		mv.addObject("obj", obj);
		return mv;
	}

	@SecurityMapping(title = "发布投诉对话", value = "/buyer/complaint_talk.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/complaint_talk.htm")
	public void complaint_talk(HttpServletRequest request,
			HttpServletResponse response, String id, String talk_content)
			throws IOException {
		Complaint obj = this.complaintService
				.getObjById(CommUtil.null2Long(id));
		if (!CommUtil.null2String(talk_content).equals("")) {
			String user_role = "";
			if (SecurityUserHolder.getCurrentUser().getId()
					.equals(obj.getFrom_user().getId())) {
				user_role = "投诉人";
			}
			if (SecurityUserHolder.getCurrentUser().getId()
					.equals(obj.getTo_user().getId())) {
				user_role = "申诉人";
			}
			String temp = user_role + "["
					+ SecurityUserHolder.getCurrentUser().getUsername() + "] "
					+ CommUtil.formatLongDate(new Date()) + "说: "
					+ talk_content;
			if (obj.getTalk_content() == null) {
				obj.setTalk_content(temp);
			} else {
				obj.setTalk_content(temp + "\n\r" + obj.getTalk_content());
			}
			this.complaintService.update(obj);
		}
		List<Map> maps = new ArrayList<Map>();
		for (String s : CommUtil.str2list(obj.getTalk_content())) {
			Map map = new HashMap();
			map.put("content", s);
			if (s.indexOf("管理员") == 0) {
				map.put("role", "admin");
			}
			if (s.indexOf("投诉") == 0) {
				map.put("role", "from_user");
			}
			if (s.indexOf("申诉") == 0) {
				map.put("role", "to_user");
			}
			maps.add(map);
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(maps, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SecurityMapping(title = "申诉提交仲裁", value = "/buyer/complaint_arbitrate.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/complaint_arbitrate.htm")
	public ModelAndView complaint_arbitrate(HttpServletRequest request,
			HttpServletResponse response, String id, String to_user_content) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Complaint obj = this.complaintService
				.getObjById(CommUtil.null2Long(id));
		obj.setStatus(3);
		this.complaintService.update(obj);
		mv.addObject("op_title", "申诉提交仲裁成功");
		mv.addObject("url", CommUtil.getURL(request)
				+ "/buyer/order_complaint_list.htm");
		return mv;
	}
}
