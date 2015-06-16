package com.iskyshop.manage.buyer.action;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.Payment;
import com.iskyshop.foundation.domain.Predeposit;
import com.iskyshop.foundation.domain.PredepositLog;
import com.iskyshop.foundation.domain.query.PredepositLogQueryObject;
import com.iskyshop.foundation.domain.query.PredepositQueryObject;
import com.iskyshop.foundation.service.IPaymentService;
import com.iskyshop.foundation.service.IPredepositLogService;
import com.iskyshop.foundation.service.IPredepositService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.pay.tools.PayTools;

/**
 * 
 * <p>
 * Title: PredepositBuyerAction.java
 * </p>
 * 
 * <p>
 * Description: 前端买家充值管理控制器，用来处理买家充值等
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
 * @date 2014-5-21
 * 
 * @version iskyshop_b2b2c 1.0
 */
@Controller
public class PredepositBuyerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IPaymentService paymentService;
	@Autowired
	private IPredepositService predepositService;
	@Autowired
	private IPredepositLogService predepositLogService;
	@Autowired
	private IUserService userService;
	@Autowired
	private PayTools payTools;

	@SecurityMapping(title = "会员充值", value = "/buyer/predeposit.htm*", rtype = "buyer", rname = "预存款管理", rcode = "predeposit_set", rgroup = "用户中心")
	@RequestMapping("/buyer/predeposit.htm")
	public ModelAndView predeposit(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/buyer_predeposit.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (this.configService.getSysConfig().isDeposit()) {
			Map params = new HashMap();
			params.put("install", true);
			params.put("mark", "alipay_wap");
			params.put("mark2", "balance");
			params.put("mark3", "payafter");
			params.put("mark4", "alipay_app");
			List<Payment> payments = this.paymentService
					.query("select obj from Payment obj where obj.install=:install and obj.mark !=:mark and obj.mark !=:mark2 and obj.mark !=:mark3 and obj.mark !=:mark4",
							params, -1, -1);
			mv.addObject("payments", payments);
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "系统未开启预存款");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/index.htm");
		}
		mv.addObject("user", this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId()));
		return mv;
	}

	@Transactional
	@SecurityMapping(title = "会员充值保存", value = "/buyer/predeposit_save.htm*", rtype = "buyer", rname = "预存款管理", rcode = "predeposit_set", rgroup = "用户中心")
	@RequestMapping("/buyer/predeposit_save.htm")
	public ModelAndView predeposit_save(HttpServletRequest request,
			HttpServletResponse response, String id, String pd_payment,
			String pd_amount, String pd_remittance_user,
			String pd_remittance_bank, String pd_remittance_time,
			String pd_remittance_info) {
		ModelAndView mv = new JModelAndView("line_pay.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (this.configService.getSysConfig().isDeposit()) {
			WebForm wf = new WebForm();
			Predeposit obj = null;
			if (CommUtil.null2String(id).equals("")) {
				obj = wf.toPo(request, Predeposit.class);
				obj.setAddTime(new Date());
				if (pd_payment.equals("outline")) {
					obj.setPd_pay_status(1);
				} else {
					obj.setPd_pay_status(0);
				}
				obj.setPd_sn("pd"
						+ CommUtil.formatTime("yyyyMMddHHmmss", new Date())
						+ SecurityUserHolder.getCurrentUser().getId());
				obj.setPd_user(SecurityUserHolder.getCurrentUser());
				this.predepositService.save(obj);
			} else {
				Predeposit pre = this.predepositService.getObjById(CommUtil
						.null2Long(id));
				obj = (Predeposit) wf.toPo(request, pre);
				this.predepositService.update(obj);
			}
			if (pd_payment.equals("outline")) {
				mv = new JModelAndView("success.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "线下支付提交成功，等待审核");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/buyer/predeposit_list.htm");
			} else {
				mv.addObject("payType", pd_payment);
				mv.addObject("type", "cash");
				mv.addObject("url", CommUtil.getURL(request));
				mv.addObject("payTools", payTools);
				mv.addObject("cash_id", obj.getId());
				Map params = new HashMap();
				params.put("install", true);
				params.put("mark", obj.getPd_payment());
				List<Payment> payments = this.paymentService
						.query("select obj from Payment obj where obj.install=:install and obj.mark=:mark",
								params, -1, -1);
				mv.addObject("payment_id", payments.size() > 0 ? payments
						.get(0).getId() : new Payment());
			}
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "系统未开启预存款");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/index.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "会员充值列表", value = "/buyer/predeposit_list.htm*", rtype = "buyer", rname = "预存款管理", rcode = "predeposit_set", rgroup = "用户中心")
	@RequestMapping("/buyer/predeposit_list.htm")
	public ModelAndView predeposit_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/predeposit_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (this.configService.getSysConfig().isDeposit()) {
			PredepositQueryObject qo = new PredepositQueryObject(currentPage,
					mv, "addTime", "desc");
			qo.addQuery("obj.pd_user.id", new SysMap("user_id",
					SecurityUserHolder.getCurrentUser().getId()), "=");
			IPageList pList = this.predepositService.list(qo);
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "系统未开启预存款");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/index.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "会员充值详情", value = "/buyer/predeposit_view.htm*", rtype = "buyer", rname = "预存款管理", rcode = "predeposit_set", rgroup = "用户中心")
	@RequestMapping("/buyer/predeposit_view.htm")
	public ModelAndView predeposit_view(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/predeposit_view.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (this.configService.getSysConfig().isDeposit()) {
			Predeposit obj = this.predepositService.getObjById(CommUtil
					.null2Long(id));
			if (obj.getPd_user().getId()
					.equals(SecurityUserHolder.getCurrentUser().getId())) {
				mv.addObject("obj", obj);
			} else {
				mv = new JModelAndView("error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "参数错误，您没有该充值信息！");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/buyer/predeposit_list.htm");
			}

		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "系统未开启预存款");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/index.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "会员充值支付", value = "/buyer/predeposit_pay.htm*", rtype = "buyer", rname = "预存款管理", rcode = "predeposit_set", rgroup = "用户中心")
	@RequestMapping("/buyer/predeposit_pay.htm")
	public ModelAndView predeposit_pay(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/predeposit_pay.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (this.configService.getSysConfig().isDeposit()) {
			Predeposit obj = this.predepositService.getObjById(CommUtil
					.null2Long(id));
			if (obj.getPd_user().getId()
					.equals(SecurityUserHolder.getCurrentUser().getId())) {
				mv.addObject("obj", obj);
			} else {
				mv = new JModelAndView("error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "参数错误，您没有该充值信息！");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/buyer/predeposit_list.htm");
			}
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "系统未开启预存款");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/index.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "会员收入明细", value = "/buyer/predeposit_log.htm*", rtype = "buyer", rname = "预存款管理", rcode = "predeposit_set", rgroup = "用户中心")
	@RequestMapping("/buyer/predeposit_log.htm")
	public ModelAndView predeposit_log(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/buyer_predeposit_log.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (this.configService.getSysConfig().isDeposit()) {
			PredepositLogQueryObject qo = new PredepositLogQueryObject(
					currentPage, mv, "addTime", "desc");
			qo.addQuery("obj.pd_log_user.id", new SysMap("user_id",
					SecurityUserHolder.getCurrentUser().getId()), "=");
			IPageList pList = this.predepositLogService.list(qo);
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
			mv.addObject("user", this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId()));
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "系统未开启预存款");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/index.htm");
		}
		return mv;
	}

}
