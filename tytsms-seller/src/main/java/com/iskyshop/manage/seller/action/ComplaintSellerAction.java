package com.iskyshop.manage.seller.action;

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
import com.iskyshop.foundation.domain.ComplaintSubject;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.ComplaintQueryObject;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IComplaintService;
import com.iskyshop.foundation.service.IComplaintSubjectService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.OrderFormTools;
import com.iskyshop.pay.wechatpay.util.ConfigContants;
import com.iskyshop.pay.wechatpay.util.TytsmsStringUtils;

/**
 * @info 卖家中心投诉管理，V1.3版开始将卖家投诉中心、买家投诉分开管理，更加合理
 * @since V1.3
 * @author 沈阳网之商科技有限公司 www.iskyshop.com erikzhang
 * 
 */
@Controller
public class ComplaintSellerAction {
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
	private OrderFormTools orderFormTools;

	@SecurityMapping(title = "卖家投诉发起", value = "/seller/complaint_handle.htm*", rtype = "seller", rname = "投诉管理", rcode = "complaint_seller", rgroup = "客户服务")
	@RequestMapping("/seller/complaint_handle.htm")
	public ModelAndView complaint_handle(HttpServletRequest request,
			HttpServletResponse response, String order_id) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/complaint_handle.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm of = this.orderFormService.getObjById(CommUtil
				.null2Long(order_id));
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -this.configService.getSysConfig()
				.getComplaint_time());
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		boolean result = true;
		if (of.getOrder_status() == 60) {
			if (of.getFinishTime().before(calendar.getTime())) {
				result = false;
			}
		}
		boolean result1 = true;
		if (of.getComplaints().size() > 0) {
			for (Complaint complaint : of.getComplaints()) {
				if (complaint.getFrom_user().getId().equals(user.getId())) {
					result1 = false;
				}
			}
		}
		if (result) {
			if (result1) {
				Complaint obj = new Complaint();
				obj.setFrom_user(user);
				obj.setStatus(0);
				obj.setType("seller");
				obj.setOf(of);
				User buyer = this.userService.getObjById(CommUtil.null2Long(of
						.getUser_id()));
				obj.setTo_user(buyer);
				mv.addObject("obj", obj);
				Map params = new HashMap();
				params.put("type", "seller");
				List<ComplaintSubject> css = this.complaintSubjectService
						.query("select obj from ComplaintSubject obj where obj.type=:type",
								params, -1, -1);
				mv.addObject("css", css);
			} else {
				mv = new JModelAndView(
						"user/default/sellercenter/seller_error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				mv.addObject("op_title", "该订单已经投诉，不允许重复投诉");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/seller/order.htm");
			}
		} else {
			mv = new JModelAndView(
					"user/default/sellercenter/seller_error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "该订单已经超过投诉有效期，不能投诉");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "卖家被投诉列表", value = "/seller/complaint.htm*", rtype = "seller", rname = "投诉管理", rcode = "complaint_seller", rgroup = "客户服务")
	@RequestMapping("/seller/complaint.htm")
	public ModelAndView complaint_seller(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String status) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/seller_complaint.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		ComplaintQueryObject qo = new ComplaintQueryObject(currentPage, mv,
				"addTime", "desc");
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		qo.addQuery("obj.to_user.id", new SysMap("user_id", user.getId()), "=");
		if (!CommUtil.null2String(status).equals("")) {
			qo.addQuery("obj.status",
					new SysMap("status", CommUtil.null2Int(status)), "=");
		} else {
			qo.addQuery("obj.status", new SysMap("status", 0), ">=");
		}
		IPageList pList = this.complaintService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("status", status);
		return mv;
	}

	@SecurityMapping(title = "卖家查看投诉详情", value = "/seller/complaint_view.htm*", rtype = "seller", rname = "投诉管理", rcode = "complaint_seller", rgroup = "客户服务")
	@RequestMapping("/seller/complaint_view.htm")
	public ModelAndView complaint_view(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/seller_complaint_view.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Complaint obj = this.complaintService
				.getObjById(CommUtil.null2Long(id));
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if (obj.getFrom_user().getId().equals(user.getId())
				|| obj.getTo_user().getId().equals(user.getId())) {
			mv.addObject("obj", obj);
			mv.addObject("orderFormTools", orderFormTools);
		} else {
			mv = new JModelAndView(
					"user/default/sellercenter/seller_error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "参数错误，不存在该投诉");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/seller/complaint.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "卖家查看投诉详情", value = "/seller/complaint_appeal.htm*", rtype = "seller", rname = "投诉管理", rcode = "complaint_seller", rgroup = "客户服务")
	@RequestMapping("/seller/complaint_appeal.htm")
	public String complaint_appeal(HttpServletRequest request,
			HttpServletResponse response, String id, String to_user_content) {
		Complaint obj = this.complaintService
				.getObjById(CommUtil.null2Long(id));
		obj.setStatus(2);
		obj.setTo_user_content(to_user_content);
		obj.setAppeal_time(new Date());
		String uploadFilePath =ConfigContants.UPLOAD_IMAGE_MIDDLE_NAME;
		String saveFilePathName =  TytsmsStringUtils.generatorImagesFolderServerPath(request)
				+ uploadFilePath + File.separator + "complaint";
		Map map = new HashMap();
		try {
			map = CommUtil.saveFileToServer(configService,request, "img1", saveFilePathName,
					null, null);
			if (map.get("fileName") != "") {
				Accessory to_acc1 = new Accessory();
				to_acc1.setName(CommUtil.null2String(map.get("fileName")));
				to_acc1.setExt(CommUtil.null2String(map.get("mime")));
				to_acc1.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
						.get("fileSize"))));
				to_acc1.setPath(uploadFilePath + "/complaint");
				to_acc1.setWidth(CommUtil.null2Int(map.get("width")));
				to_acc1.setHeight(CommUtil.null2Int(map.get("height")));
				to_acc1.setAddTime(new Date());
				this.accessoryService.save(to_acc1);
				obj.setTo_acc1(to_acc1);
			}
			map.clear();
			map = CommUtil.saveFileToServer(configService,request, "img2", saveFilePathName,
					null, null);
			if (map.get("fileName") != "") {
				Accessory to_acc2 = new Accessory();
				to_acc2.setName(CommUtil.null2String(map.get("fileName")));
				to_acc2.setExt(CommUtil.null2String(map.get("mime")));
				to_acc2.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
						.get("fileSize"))));
				to_acc2.setPath(uploadFilePath + "/complaint");
				to_acc2.setWidth(CommUtil.null2Int(map.get("width")));
				to_acc2.setHeight(CommUtil.null2Int(map.get("height")));
				to_acc2.setAddTime(new Date());
				this.accessoryService.save(to_acc2);
				obj.setTo_acc2(to_acc2);
			}
			map.clear();
			map = CommUtil.saveFileToServer(configService,request, "img3", saveFilePathName,
					null, null);
			if (map.get("fileName") != "") {
				Accessory to_acc3 = new Accessory();
				to_acc3.setName(CommUtil.null2String(map.get("fileName")));
				to_acc3.setExt(CommUtil.null2String(map.get("mime")));
				to_acc3.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
						.get("fileSize"))));
				to_acc3.setPath(uploadFilePath + "/complaint");
				to_acc3.setWidth(CommUtil.null2Int(map.get("width")));
				to_acc3.setHeight(CommUtil.null2Int(map.get("height")));
				to_acc3.setAddTime(new Date());
				this.accessoryService.save(to_acc3);
				obj.setTo_acc3(to_acc3);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.complaintService.update(obj);
		request.getSession(false).setAttribute("url",
				CommUtil.getURL(request) + "/seller/complaint.htm");
		request.getSession(false).setAttribute("op_title", "提交申诉成功");
		return "redirect:/seller/success.htm";
	}

	@SecurityMapping(title = "发布投诉对话", value = "/seller/complaint_talk.htm*", rtype = "seller", rname = "投诉管理", rcode = "complaint_seller", rgroup = "客户服务")
	@RequestMapping("/seller/complaint_talk.htm")
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

	@SecurityMapping(title = "申请仲裁成功", value = "/seller/complaint_arbitrate.htm*", rtype = "seller", rname = "投诉管理", rcode = "complaint_seller", rgroup = "客户服务")
	@RequestMapping("/seller/complaint_arbitrate.htm")
	public String complaint_arbitrate(HttpServletRequest request,
			HttpServletResponse response, String id, String to_user_content) {
		Complaint obj = this.complaintService
				.getObjById(CommUtil.null2Long(id));
		obj.setStatus(3);
		this.complaintService.update(obj);
		request.getSession(false).setAttribute("url",
				CommUtil.getURL(request) + "/seller/complaint.htm");
		request.getSession(false).setAttribute("op_title", "申请仲裁成功");
		return "redirect:/seller/success.htm";
	}

	@SecurityMapping(title = "申请仲裁成功", value = "/seller/complaint_img.htm*", rtype = "seller", rname = "投诉管理", rcode = "complaint_seller", rgroup = "客户服务")
	@RequestMapping("/seller/complaint_img.htm")
	public ModelAndView complaint_img(HttpServletRequest request,
			HttpServletResponse response, String id, String type) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/complaint_img.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Complaint obj = this.complaintService
				.getObjById(CommUtil.null2Long(id));
		mv.addObject("type", type);
		mv.addObject("obj", obj);
		return mv;
	}
}
