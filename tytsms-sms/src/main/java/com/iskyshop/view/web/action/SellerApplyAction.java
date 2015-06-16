package com.iskyshop.view.web.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.HtmlUtils;

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.Area;
import com.iskyshop.foundation.domain.Document;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.StoreGrade;
import com.iskyshop.foundation.domain.StorePoint;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IAreaService;
import com.iskyshop.foundation.service.IDocumentService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IStoreGradeService;
import com.iskyshop.foundation.service.IStorePointService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.StoreTools;
import com.iskyshop.pay.wechatpay.util.ConfigContants;
import com.iskyshop.pay.wechatpay.util.TytsmsStringUtils;

/**
 * 
 * 
 * <p>
 * Title:SellerApplyAction.java
 * </p>
 * 
 * <p>
 * Description:商家入驻流程控制器，商城所有注册用户的都可以申请成为商家，需要完成相关申请流程，运营商审批通过后即可开通在线店铺入驻
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
 * @author wyj、jy、erikzhang
 * 
 * @date 2014年4月25日
 * 
 * @version iskyshop_b2b2c 1.0
 */
@Controller
public class SellerApplyAction {
	private static final boolean GoodsClass = false;
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private ISysConfigService sysConfigService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IStoreGradeService storegradeService;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private IDocumentService documentService;
	@Autowired
	private IStorePointService storePointService;
	@Autowired
	private StoreTools storeTools;

	@SecurityMapping(title = "商家入驻申请", value = "/seller_apply.htm*", rtype = "buyer", rname = "商家入驻", rcode = "seller_apply", rgroup = "商家入驻")
	@RequestMapping("/seller_apply.htm")
	public ModelAndView seller_apply(HttpServletRequest request,
			HttpServletResponse response, String op, String step) {
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		boolean apply_flag = true;
		JModelAndView mv = null;
		if(user.getStore() != null && user.getStore().getStore_status() == 30){ //快速入驻功能
			return seller_store_query(request,response);
		}
		if (user.getStore_apply_step() > 7) {// 申请分为7步，走完第7步流程进入审核状态，就不允许继续修改资料了
			return seller_store_query(request, response);
		} else {
			if (!this.configService.getSysConfig().isStore_allow()) {
				mv = new JModelAndView("error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "商城尚未开启商家入驻功能");
				mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
				return mv;
			}
			if (CommUtil.null2Int(step) > 7 || CommUtil.null2Int(step) < 0) {
				step = CommUtil.null2String(user.getStore_apply_step());
			}
			if (step != null && !CommUtil.null2String(step).equals("")) {// 如果step参数不为空，更新申请步骤
				if (user.getStore_apply_step() >= 0
						&& (CommUtil.null2Int(step)
								- user.getStore_apply_step() <= 1)) {// 如果用户已经进入申请流程，必须按照逐步流程走，杜绝恶意数据参数发生
					user.setStore_apply_step(CommUtil.null2Int(step));
					this.userService.update(user);
				}
			}
			if (CommUtil.null2String(op).equals("back")
					&& (user.getStore_apply_step() - CommUtil.null2Int(step) >= 1)) {// 如果执行的返回操作
				if (CommUtil.null2Int(step) == 0) {
					return seller_apply_step0(request, response);
				}
				if (CommUtil.null2Int(step) == 1) {
					return seller_apply_step1(request, response);
				}
				if (CommUtil.null2Int(step) == 2) {
					return seller_apply_step2(request, response);
				}
				if (CommUtil.null2Int(step) == 3) {
					return seller_apply_step3(request, response);
				}
				if (CommUtil.null2Int(step) == 4) {
					return seller_apply_step4(request, response);
				}
				if (CommUtil.null2Int(step) == 5) {
					return seller_apply_step5(request, response);
				}
				if (CommUtil.null2Int(step) == 6) {
					return seller_apply_step6(request, response);
				}
				if (CommUtil.null2Int(step) == 7) {
					return seller_apply_step7(request, response);
				}
			} else {
				if (user.getStore_apply_step() == 0) {
					return seller_apply_step0(request, response);
				}
				if (user.getStore_apply_step() == 1) {
					return seller_apply_step1(request, response);
				}
				if (user.getStore_apply_step() == 2) {
					return seller_apply_step2(request, response);
				}
				if (user.getStore_apply_step() == 3) {
					return seller_apply_step3(request, response);
				}
				if (user.getStore_apply_step() == 4) {
					return seller_apply_step4(request, response);
				}
				if (user.getStore_apply_step() == 5) {
					return seller_apply_step5(request, response);
				}
				if (user.getStore_apply_step() == 6) {
					return seller_apply_step6(request, response);
				}
				if (user.getStore_apply_step() == 7) {
					return seller_apply_step7(request, response);
				}
			}
		}
		return mv;
	}

	/**
	 * 商家协议确定
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	private ModelAndView seller_apply_step0(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"seller_apply/seller_apply_step0.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Document doc = this.documentService.getObjByProperty("mark","apply_agreement");
		doc.setContent(HtmlUtils.htmlUnescape(doc.getContent()));//反转义
		mv.addObject("doc", doc);
		return mv;
	}

	/**
	 * 商家入驻须知
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	private ModelAndView seller_apply_step1(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"seller_apply/seller_apply_step1.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Document doc = this.documentService.getObjByProperty("mark","apply_notice");
		doc.setContent(HtmlUtils.htmlUnescape(doc.getContent()));//反转义
		mv.addObject("doc", doc);
		return mv;
	}

	/**
	 * 商家申请页面 商家由买家注册后申请
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	private ModelAndView seller_apply_step2(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"seller_apply/seller_apply_step2.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		Store store = user.getStore();
		if (store == null) {
			store = new Store();
			store.setAddTime(new Date());
			this.storeService.save(store);
			StorePoint sp = new StorePoint();
			sp.setAddTime(new Date());
			sp.setStore(store);
			sp.setDescription_evaluate(BigDecimal.valueOf(0));
			sp.setService_evaluate(BigDecimal.valueOf(0));
			sp.setShip_evaluate(BigDecimal.valueOf(0));
			this.storePointService.save(sp);
			user.setStore(store);
			this.userService.update(user);
		}
		mv.addObject("user", user);
		mv.addObject("store", store);
		return mv;
	}
	

	/**
	 * 快速入驻信息保存
	 * 
	 * @param request
	 * @param response
	 * @param trueName
	 * @param telephone
	 * @param email
	 * @return
	 */
	@SecurityMapping(title = "快速入驻信息保存", value = "/seller_apply_quickly_save.htm*", rtype = "buyer", rname = "商家入驻", rcode = "seller_apply", rgroup = "商家入驻")
	@RequestMapping("/seller_apply_quickly_save.htm")
	@Transactional
	public String seller_apply_quickly_save(HttpServletRequest request,
			HttpServletResponse response) {
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		if(user.getStore().getStore_status() == 0){
			this.userService.update(user);
			WebForm wf = new WebForm();
			Store store = null;
			Store obj = user.getStore();
			store = (Store) wf.toPo(request, obj);
			store.setStore_status(30);
			this.storeService.update(store);
		}
		return "redirect:seller_store_query.htm";
	}


	/**
	 * 商家申请联系方式保存
	 * 
	 * @param request
	 * @param response
	 * @param trueName
	 * @param telephone
	 * @param email
	 * @return
	 */
	@SecurityMapping(title = "入驻联系方式保存", value = "/seller_apply_step2_save.htm*", rtype = "buyer", rname = "商家入驻", rcode = "seller_apply", rgroup = "商家入驻")
	@RequestMapping("/seller_apply_step2_save.htm")
	@Transactional
	public String seller_apply_step2_save(HttpServletRequest request,
			HttpServletResponse response, String trueName, String telephone,
			String email) {
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		if (!CommUtil.null2String(trueName).equals("")
				&& !CommUtil.null2String(telephone).equals("")
				&& !CommUtil.null2String(email).equals("")) {
			user.setTrueName(trueName);
			user.setTelephone(telephone);
			user.setEmail(email);
			this.userService.update(user);
		}
		return "redirect:seller_apply.htm?step=3";
	}

	/**
	 * 完善公司信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	private ModelAndView seller_apply_step3(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"seller_apply/seller_apply_step3.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		if (user.getStore().getStore_status() == 6) {
			Store store = user.getStore();
			store.setStore_status(0);
			this.storeService.update(store);
		}
		List<Area> areas = this.areaService.query(
				"select obj from Area obj where obj.parent.id is null", null,
				-1, -1);
		mv.addObject("areas", areas);
		mv.addObject("store", user.getStore());
		return mv;
	}

	/**
	 * 商家申请公司信息保存
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "入驻公司信息保存", value = "/seller_apply_step3_save.htm*", rtype = "buyer", rname = "商家入驻", rcode = "seller_apply", rgroup = "商家入驻")
	@RequestMapping("/seller_apply_step3_save.htm")
	@Transactional
	public String seller_apply_step3_save(HttpServletRequest request,
			HttpServletResponse response, String lid2, String cid2, String step) {
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user.setStore_apply_step(CommUtil.null2Int(step));
		this.userService.update(user);
		WebForm wf = new WebForm();
		Store store = null;
		Store obj = user.getStore();
		store = (Store) wf.toPo(request, obj);
		store.setLicense_area(this.areaService.getObjById(CommUtil
				.null2Long(lid2)));
		store.setLicense_c_area(this.areaService.getObjById(CommUtil
				.null2Long(cid2)));
		this.storeService.update(store);
		return "redirect:seller_apply.htm";
	}

	/**
	 * 完善税务以及银行信息
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	private ModelAndView seller_apply_step4(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"seller_apply/seller_apply_step4.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		List<Area> areas = this.areaService.query(
				"select obj from Area obj where obj.parent.id is null", null,
				-1, -1);
		mv.addObject("areas", areas);
		mv.addObject("store", user.getStore());
		return mv;
	}

	/**
	 * 保存税务以及银行信息
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "入驻财务信息保存", value = "/seller_apply_step4_save.htm*", rtype = "buyer", rname = "商家入驻", rcode = "seller_apply", rgroup = "商家入驻")
	@RequestMapping("/seller_apply_step4_save.htm")
	@Transactional
	public String seller_apply_step4_save(HttpServletRequest request,
			HttpServletResponse response, String bid2, String step) {
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user.setStore_apply_step(CommUtil.null2Int(step));
		this.userService.update(user);
		WebForm wf = new WebForm();
		Store store = null;
		Store obj = user.getStore();
		store = (Store) wf.toPo(request, obj);
		store.setBank_area(this.areaService.getObjById(CommUtil.null2Long(bid2)));
		this.storeService.update(store);
		return "redirect:seller_apply.htm";
	}

	/**
	 * 完善店铺信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	private ModelAndView seller_apply_step5(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"seller_apply/seller_apply_step5.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		if (user.getStore().getStore_status() == 11) {
			Store store = user.getStore();
			store.setStore_status(0);
			this.storeService.update(store);
		}
		List<Area> areas = this.areaService.query(
				"select obj from Area obj where obj.parent.id is null", null,
				-1, -1);
		List<StoreGrade> storeGrades = this.storegradeService.query(
				"select obj from StoreGrade obj", null, -1, -1);
		List<GoodsClass> goodsClass = this.goodsClassService.query(
				"select obj from GoodsClass obj where obj.parent.id is null",
				null, -1, -1);
		mv.addObject("goodsClass", goodsClass);
		mv.addObject("storeGrades", storeGrades);
		mv.addObject("areas", areas);
		mv.addObject("store", user.getStore());
		return mv;
	}

	/**
	 * 保存店铺信息
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "入驻店铺信息保存", value = "/seller_apply_step5_save.htm*", rtype = "buyer", rname = "商家入驻", rcode = "seller_apply", rgroup = "商家入驻")
	@RequestMapping("/seller_apply_step5_save.htm")
	@Transactional
	public String seller_apply_step5_save(HttpServletRequest request,
			HttpServletResponse response, String aid2, String step,
			String storeGrades, String gc_main_id, String validity) {
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user.setStore_apply_step(CommUtil.null2Int(step));
		this.userService.update(user);
		Store obj = user.getStore();
		WebForm wf = new WebForm();
		Store store = null;
		store = (Store) wf.toPo(request, obj);
		if (aid2 != null && !"".equals(aid2)) {
			store.setArea(this.areaService.getObjById(CommUtil.null2Long(aid2)));
		}
		store.setGrade(this.storegradeService.getObjById(CommUtil
				.null2Long(storeGrades)));
		store.setGc_main_id(CommUtil.null2Long(gc_main_id));
		store.setGc_detail_info(null);
		store.setValidity(CommUtil.formatDate(validity));
		this.storeService.update(store);
		return "redirect:seller_apply.htm";
	}

	/**
	 * 店铺经营类目
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	private ModelAndView seller_apply_step6(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"seller_apply/seller_apply_step6.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		Store user_store = user.getStore();
		if (user_store.getGrade().getMain_limit() == 1) {// 查出该主营类目下的二级类目
			GoodsClass gc = this.goodsClassService.getObjById(user_store
					.getGc_main_id());
			mv.addObject("gc", gc);
		} else {// 不限制
			List<GoodsClass> goodsClass = this.goodsClassService
					.query("select obj from GoodsClass obj where obj.parent.id is null",
							null, -1, -1);
			mv.addObject("goodsClass", goodsClass);
			mv.addObject("gc", goodsClass.get(0));
		}
		mv.addObject("store", user_store);
		Set<GoodsClass> all_details_gcs = this.storeTools
				.query_store_DetailGc(user_store.getGc_detail_info());
		mv.addObject("details_gcs", all_details_gcs);
		return mv;
	}

	/**
	 * 保存店铺类型及类目信息
	 * 
	 * @param request
	 * @param response
	 * @param goodsClass_main
	 * @param sg
	 * @return
	 */
	@SecurityMapping(title = "入驻店铺类型保存", value = "/seller_apply_step6_save.htm*", rtype = "buyer", rname = "商家入驻", rcode = "seller_apply", rgroup = "商家入驻")
	@RequestMapping("/seller_apply_step6_save.htm")
	@Transactional
	public String seller_apply_step6_save(HttpServletRequest request,
			HttpServletResponse response, String goodsClass_main, String step) {
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user.setStore_apply_step(CommUtil.null2Int(step));
		this.userService.update(user);
		Store store = user.getStore();
		this.storeService.update(store);
		return "redirect:seller_apply.htm";
	}

	/**
	 * 确认入驻协议
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	private ModelAndView seller_apply_step7(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"seller_apply/seller_apply_step7.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("doc", this.documentService.getObjByProperty("mark",
				"apply_agreement"));
		return mv;
	}

	/**
	 * 商家入驻佣金信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "入驻佣金", value = "/seller_apply_money.htm*", rtype = "buyer", rname = "商家入驻", rcode = "seller_apply", rgroup = "商家入驻")
	@RequestMapping("/seller_apply_money.htm")
	public ModelAndView seller_apply_money(HttpServletRequest request,HttpServletResponse response) {
		SysConfig sysConfig = configService.getSysConfig();
		ModelAndView mv  = new JModelAndView(
				"seller_apply/seller_apply_money.html",
				sysConfig,
				this.userConfigService.getUserConfig(), 1, request, response);
		List<GoodsClass> goodsClass = this.goodsClassService.query(
				"select obj from GoodsClass obj where obj.parent.id is null",
				null, -1, -1);
		mv.addObject("goodsClass", goodsClass);
		
		
		return mv;
	}

	/**
	 * 确认提交申请
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "入驻申请提交", value = "/seller_store_wait.htm*", rtype = "buyer", rname = "商家入驻", rcode = "seller_apply", rgroup = "商家入驻")
	@RequestMapping("/seller_store_wait.htm")
	public ModelAndView seller_store_wait(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"seller_apply/seller_apply_wait.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		if (user.getStore().getStore_status() > 0) {// 已经提交过申请了，进入查询请求
			return this.seller_store_query(request, response);
		}
		Store store = user.getStore();
		store.setStore_status(5);// 将店铺状态设置为“公司等待信息审核”
		user.setStore_apply_step(8);// 将用户的申请进度设置为“已提交入驻申请”
		this.userService.update(user);
		this.storeService.update(store);
		return mv;
	}

	/**
	 * 入驻进度查询
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "入驻进度查询", value = "/seller_store_query.htm*", rtype = "buyer", rname = "商家入驻", rcode = "seller_apply", rgroup = "商家入驻")
	@RequestMapping("/seller_store_query.htm")
	public ModelAndView seller_store_query(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = null;
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		Store store = user.getStore();
		if (store.getStore_status() == 5 || store.getStore_status() == 6) {
			mv = new JModelAndView("seller_apply/seller_apply_wait1.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
		}
		if (store.getStore_status() == 10 || store.getStore_status() == 11) {
			mv = new JModelAndView("seller_apply/seller_apply_wait2.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
		}
		if (store.getStore_status() == 15 || store.getStore_status() == 20
				|| store.getStore_status() == 25) {
			mv = new JModelAndView("seller_apply/seller_apply_wait3.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
		}
		if (store.getStore_status() == 30) { //快速入驻
			mv = new JModelAndView("seller_apply/seller_apply_wait4.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
		}
		mv.addObject("store", store);
		return mv;
	}

	/**
	 * 审核拒绝，重新提交
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "重新申请入驻", value = "/seller_store_rewrite.htm*", rtype = "buyer", rname = "商家入驻", rcode = "seller_apply", rgroup = "商家入驻")
	@RequestMapping("/seller_store_rewrite.htm")
	public ModelAndView seller_store_rewrite(HttpServletRequest request,
			HttpServletResponse response, String step) {
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		if (step.equals("3")) {// 重新填写公司信息
			user.setStore_apply_step(3);
		}
		if (step.equals("5")) {// 重新填写店铺信息
			user.setStore_apply_step(5);
		}
		this.userService.update(user);
		return seller_apply(request, response, null, step);
	}

	/**
	 * 商家入驻照片保存上传
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping("/seller_apply_image_save.htm")
	@Transactional
	public void seller_apply_image_save(HttpServletRequest request,
			HttpServletResponse response, String mark, String uid) {
		// 根据用户的id操作对应的店铺
		User user = this.userService.getObjById(CommUtil.null2Long(uid));
		Store store = user.getStore();
		// 上传
		String uploadFilePath =ConfigContants.UPLOAD_IMAGE_MIDDLE_NAME;
		String saveFilePathName =  TytsmsStringUtils.generatorImagesFolderServerPath(request)
				+ uploadFilePath
				+ File.separator
				+ "store"
				+ File.separator
				+ store.getId();
		if (!CommUtil.fileExist(saveFilePathName)) {
			CommUtil.createFolder(saveFilePathName);
		}
		Map map = new HashMap();
		Map json = new HashMap();
		if ("idCard".equals(mark)) {// 保存身份证电子版
			if (store.getLicense_legal_idCard_image() != null) {// 如果存在，将其删除
				Long aid = store.getLicense_legal_idCard_image().getId();
				store.setLicense_legal_idCard_image(null);
				this.storeService.update(store);
				Accessory img = this.accessoryService.getObjById(CommUtil
						.null2Long(CommUtil.null2Long(aid)));
				boolean ret = this.accessoryService.delete(img.getId());
				if (ret) {
					CommUtil.del_acc(request, img);
				}
			}
			try {
				map = CommUtil.saveFileToServer(configService,request, "Filedata",
						saveFilePathName, "", null);
				if (map.get("fileName") != "") {
					Accessory photo = new Accessory();
					photo.setName((String) map.get("fileName"));
					photo.setExt("." + (String) map.get("mime"));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					photo.setPath(uploadFilePath + "/store/" + store.getId());
					photo.setAddTime(new Date());
					this.accessoryService.save(photo);
					store.setLicense_legal_idCard_image(photo);
					this.storeService.update(store);
					json.put("url", photo.getPath() + "/" + photo.getName());
					json.put("id", photo.getId());
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if ("license".equals(mark)) {// 保存营业执照电子版
			if (store.getLicense_image() != null) {// 如果存在，将其删除
				Long aid = store.getLicense_image().getId();
				store.setLicense_image(null);
				this.storeService.update(store);
				Accessory img = this.accessoryService.getObjById(CommUtil
						.null2Long(CommUtil.null2Long(aid)));
				boolean ret = this.accessoryService.delete(img.getId());
				if (ret) {
					CommUtil.del_acc(request, img);
				}
			}
			try {
				map = CommUtil.saveFileToServer(configService,request, "Filedata",
						saveFilePathName, "", null);
				if (map.get("fileName") != "") {
					Accessory photo = new Accessory();
					photo.setName((String) map.get("fileName"));
					photo.setExt("." + (String) map.get("mime"));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					photo.setPath(uploadFilePath + "/store/" + store.getId());
					photo.setAddTime(new Date());
					this.accessoryService.save(photo);
					store.setLicense_image(photo);
					this.storeService.update(store);
					json.put("url", photo.getPath() + "/" + photo.getName());
					json.put("id", photo.getId());
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if ("organization".equals(mark)) {// 保存组织机构代码证
			if (store.getOrganization_image() != null) {// 如果存在，将其删除
				Long aid = store.getOrganization_image().getId();
				store.setOrganization_image(null);
				this.storeService.update(store);
				Accessory img = this.accessoryService.getObjById(CommUtil
						.null2Long(CommUtil.null2Long(aid)));
				boolean ret = this.accessoryService.delete(img.getId());
				if (ret) {
					CommUtil.del_acc(request, img);
				}
			}
			try {
				map = CommUtil.saveFileToServer(configService,request, "Filedata",
						saveFilePathName, "", null);
				if (map.get("fileName") != "") {
					Accessory photo = new Accessory();
					photo.setName((String) map.get("fileName"));
					photo.setExt("." + (String) map.get("mime"));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					photo.setPath(uploadFilePath + "/store/" + store.getId());
					photo.setAddTime(new Date());
					this.accessoryService.save(photo);
					store.setOrganization_image(photo);
					this.storeService.update(store);
					json.put("url", photo.getPath() + "/" + photo.getName());
					json.put("id", photo.getId());
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if ("tax_reg".equals(mark)) {// 保存税务登记证电子版
			if (store.getTax_reg_card() != null) {// 如果存在，将其删除
				Long aid = store.getTax_reg_card().getId();
				store.setTax_reg_card(null);
				this.storeService.update(store);
				Accessory img = this.accessoryService.getObjById(CommUtil
						.null2Long(CommUtil.null2Long(aid)));
				boolean ret = this.accessoryService.delete(img.getId());
				if (ret) {
					CommUtil.del_acc(request, img);
				}
			}
			try {
				map = CommUtil.saveFileToServer(configService,request, "Filedata",
						saveFilePathName, "", null);
				if (map.get("fileName") != "") {
					Accessory photo = new Accessory();
					photo.setName((String) map.get("fileName"));
					photo.setExt("." + (String) map.get("mime"));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					photo.setPath(uploadFilePath + "/store/" + store.getId());
					photo.setAddTime(new Date());
					this.accessoryService.save(photo);
					store.setTax_reg_card(photo);
					this.storeService.update(store);
					json.put("url", photo.getPath() + "/" + photo.getName());
					json.put("id", photo.getId());
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if ("tax_general".equals(mark)) {// 保存纳税人资格证电子版
			if (store.getTax_general_card() != null) {// 如果存在，将其删除
				Long aid = store.getTax_general_card().getId();
				store.setTax_general_card(null);
				this.storeService.update(store);
				Accessory img = this.accessoryService.getObjById(CommUtil
						.null2Long(CommUtil.null2Long(aid)));
				boolean ret = this.accessoryService.delete(img.getId());
				if (ret) {
					CommUtil.del_acc(request, img);
				}
			}
			try {
				map = CommUtil.saveFileToServer(configService,request, "Filedata",
						saveFilePathName, "", null);
				if (map.get("fileName") != "") {
					Accessory photo = new Accessory();
					photo.setName((String) map.get("fileName"));
					photo.setExt("." + (String) map.get("mime"));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					photo.setPath(uploadFilePath + "/store/" + store.getId());
					photo.setAddTime(new Date());
					this.accessoryService.save(photo);
					store.setTax_general_card(photo);
					this.storeService.update(store);
					json.put("url", photo.getPath() + "/" + photo.getName());
					json.put("id", photo.getId());
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if ("bank_permit".equals(mark)) {// 保存银行开户许可证电子版
			if (store.getBank_permit_image() != null) {// 如果存在，将其删除
				Long aid = store.getBank_permit_image().getId();
				store.setBank_permit_image(null);
				this.storeService.update(store);
				Accessory img = this.accessoryService.getObjById(CommUtil
						.null2Long(CommUtil.null2Long(aid)));
				boolean ret = this.accessoryService.delete(img.getId());
				if (ret) {
					CommUtil.del_acc(request, img);
				}
			}
			try {
				map = CommUtil.saveFileToServer(configService,request, "Filedata",
						saveFilePathName, "", null);
				if (map.get("fileName") != "") {
					Accessory photo = new Accessory();
					photo.setName((String) map.get("fileName"));
					photo.setExt("." + (String) map.get("mime"));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					photo.setPath(uploadFilePath + "/store/" + store.getId());
					photo.setAddTime(new Date());
					this.accessoryService.save(photo);
					store.setBank_permit_image(photo);
					this.storeService.update(store);
					json.put("url", photo.getPath() + "/" + photo.getName());
					json.put("id", photo.getId());
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		String ret = Json.toJson(json, JsonFormat.compact());
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 商家入驻照片删除
	 * 
	 * @param request
	 * @param response
	 * @param id
	 */
	@SecurityMapping(title = "商家入驻照片删除", value = "/seller_apply_image_del.htm*", rtype = "buyer", rname = "商家入驻", rcode = "seller_apply", rgroup = "商家入驻")
	@RequestMapping("/seller_apply_image_del.htm")
	public void seller_apply_image_del(HttpServletRequest request,
			HttpServletResponse response, String mark, String uid) {
		User user = this.userService.getObjById(CommUtil.null2Long(uid));
		Store store = user.getStore();
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		Map json = new HashMap();
		Long aid = null;
		if ("idCard".equals(mark)) {// 删除身份证电子版
			aid = store.getLicense_legal_idCard_image().getId();
			store.setLicense_legal_idCard_image(null);
			this.storeService.update(store);
			Accessory img = this.accessoryService.getObjById(CommUtil
					.null2Long(CommUtil.null2Long(aid)));
			try {
				boolean ret = this.accessoryService.delete(img.getId());
				if (ret) {
					CommUtil.del_acc(request, img);
				}
				json.put("result", ret);
				writer = response.getWriter();
				writer.print(Json.toJson(json, JsonFormat.compact()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if ("license".equals(mark)) {// 删除营业执照电子版
			aid = store.getLicense_image().getId();
			store.setLicense_image(null);
			this.storeService.update(store);
			Accessory img = this.accessoryService.getObjById(CommUtil
					.null2Long(CommUtil.null2Long(aid)));
			try {
				boolean ret = this.accessoryService.delete(img.getId());
				if (ret) {
					CommUtil.del_acc(request, img);
				}
				json.put("result", ret);
				writer = response.getWriter();
				writer.print(Json.toJson(json, JsonFormat.compact()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if ("organization".equals(mark)) {// 删除组织机构代码证
			aid = store.getOrganization_image().getId();
			store.setOrganization_image(null);
			this.storeService.update(store);
			Accessory img = this.accessoryService.getObjById(CommUtil
					.null2Long(CommUtil.null2Long(aid)));
			try {
				boolean ret = this.accessoryService.delete(img.getId());
				if (ret) {
					CommUtil.del_acc(request, img);
				}
				json.put("result", ret);
				writer = response.getWriter();
				writer.print(Json.toJson(json, JsonFormat.compact()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if ("tax_reg".equals(mark)) {// 删除税务登记证电子版
			aid = store.getTax_reg_card().getId();
			store.setTax_reg_card(null);
			this.storeService.update(store);
			Accessory img = this.accessoryService.getObjById(CommUtil
					.null2Long(CommUtil.null2Long(aid)));
			try {
				boolean ret = this.accessoryService.delete(img.getId());
				if (ret) {
					CommUtil.del_acc(request, img);
				}
				json.put("result", ret);
				writer = response.getWriter();
				writer.print(Json.toJson(json, JsonFormat.compact()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if ("tax_general".equals(mark)) {// 删除纳税人资格证电子版
			aid = store.getTax_general_card().getId();
			store.setTax_general_card(null);
			this.storeService.update(store);
			Accessory img = this.accessoryService.getObjById(CommUtil
					.null2Long(CommUtil.null2Long(aid)));
			try {
				boolean ret = this.accessoryService.delete(img.getId());
				if (ret) {
					CommUtil.del_acc(request, img);
				}
				json.put("result", ret);
				writer = response.getWriter();
				writer.print(Json.toJson(json, JsonFormat.compact()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if ("bank_permit".equals(mark)) {// 删除银行开户许可证电子版
			aid = store.getBank_permit_image().getId();
			store.setBank_permit_image(null);
			this.storeService.update(store);
			Accessory img = this.accessoryService.getObjById(CommUtil
					.null2Long(CommUtil.null2Long(aid)));
			try {
				boolean ret = this.accessoryService.delete(img.getId());
				if (ret) {
					CommUtil.del_acc(request, img);
				}
				json.put("result", ret);
				writer = response.getWriter();
				writer.print(Json.toJson(json, JsonFormat.compact()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 所在地址下拉
	 * 
	 * @param request
	 * @param response
	 * @param value
	 */
	@SecurityMapping(title = "所在地址下拉", value = "/seller_license_area.htm*", rtype = "buyer", rname = "商家入驻", rcode = "seller_apply", rgroup = "商家入驻")
	@RequestMapping("/seller_license_area.htm")
	public ModelAndView seller_license_area_ajax(HttpServletRequest request,
			HttpServletResponse response, String value, String type) {
		ModelAndView mv = new JModelAndView(
				"seller_apply/seller_store_ajax_select.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map params = new HashMap();
		params.put("id", value);
		Area area = this.areaService.getObjById(CommUtil.null2Long(value));
		//cty 修改时间2015-3-12 增加内容 
		if (("l").equals(type)) {// 营业执照地址
			if (area.getLevel() == 0) {
				mv.addObject("level", "省份");
				mv.addObject("mark", "lid0");
				mv.addObject("area", area);
			}else if (area.getLevel() == 1) {
				mv.addObject("level", "城市");
				mv.addObject("mark", "lid1");
				mv.addObject("area", area);
			} else if (area.getLevel() == 2) {
				mv.addObject("level", "州县");
				mv.addObject("mark", "lid2");
				mv.addObject("area", area);
			}
		} else if (("c").equals(type)) {// 公司地址
			if (area.getLevel() == 0) {
				mv.addObject("level", "省份");
				mv.addObject("mark", "cid0");
				mv.addObject("area", area);
			}else if (area.getLevel() == 1) {
				mv.addObject("level", "城市");
				mv.addObject("mark", "cid1");
				mv.addObject("area", area);
			} else if (area.getLevel() == 2) {
				mv.addObject("level", "州县");
				mv.addObject("mark", "cid2");
				mv.addObject("area", area);
			}
		} else if (("b").equals(type)) {// 开户银行地址
			if (area.getLevel() == 0) {
				mv.addObject("level", "省份");
				mv.addObject("mark", "bid0");
				mv.addObject("area", area);
			}else if (area.getLevel() == 1) {
				mv.addObject("level", "城市");
				mv.addObject("mark", "bid1");
				mv.addObject("area", area);
			} else if (area.getLevel() == 2) {
				mv.addObject("level", "州县");
				mv.addObject("mark", "bid2");
				mv.addObject("area", area);
			}
		} else if (("a").equals(type)) {// 店铺地址
			if (area.getLevel() == 0) {
				mv.addObject("level", "省份");
				mv.addObject("mark", "aid0");
				mv.addObject("area", area);
			}else if (area.getLevel() == 1) {
				mv.addObject("level", "城市");
				mv.addObject("mark", "aid1");
				mv.addObject("area", area);
			} else if (area.getLevel() == 2) {
				mv.addObject("level", "州县");
				mv.addObject("mark", "aid2");
				mv.addObject("area", area);
			}
		}
		return mv;
	}

	/**
	 * 店铺经营类目
	 * 
	 * @param request
	 * @param response
	 * @param cid
	 * @return
	 */
	@SecurityMapping(title = "店铺经营类目", value = "/seller_goodsclass_ajax.htm*", rtype = "buyer", rname = "商家入驻", rcode = "seller_apply", rgroup = "商家入驻")
	@RequestMapping("/seller_goodsclass_ajax.htm")
	public ModelAndView seller_goodsclass_ajax(HttpServletRequest request,
			HttpServletResponse response, String cid, String uid) {
		ModelAndView mv = new JModelAndView(
				"seller_apply/seller_store_ajax_goodsClass.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = this.userService.getObjById(CommUtil.null2Long(uid));
		Store user_store = user.getStore();
		GoodsClass goodsClass = this.goodsClassService.getObjById(CommUtil
				.null2Long(cid));
		Set<GoodsClass> gcs = new TreeSet<GoodsClass>();
		Set<GoodsClass> gcs1=new TreeSet<GoodsClass>();
		for (GoodsClass gc : goodsClass.getChilds()) {
			gcs.add(gc);
			gcs1.add(gc);
		}
		// System.out.println("共有：" + gcs.size());
		Iterator<GoodsClass> it = gcs1.iterator();	
		while (it.hasNext()) {// 遍历集合
			GoodsClass gc = it.next();
			// System.out.println("------ " + gc.getClassName());
			for (GoodsClass ugc : this.storeTools
					.query_store_DetailGc(user_store.getGc_detail_info())) {
				if (ugc.getId() == gc.getId()) {
					gcs.remove(ugc);
					break;
				}
			}
		}
		mv.addObject("gcs", gcs);
		return mv;
	}

	/**
	 * 店铺经营详细类目保存
	 * 
	 * @param request
	 * @param response
	 * @param goodsClass
	 * @return
	 */
	@SecurityMapping(title = "店铺经营详细类目保存", value = "/seller_goodsclass_save.htm*", rtype = "buyer", rname = "商家入驻", rcode = "seller_apply", rgroup = "商家入驻")
	@RequestMapping("/seller_goodsclass_save.htm")
	@Transactional
	public ModelAndView seller_goodsclass_save(HttpServletRequest request,
			HttpServletResponse response, String gc_ids, String uid) {
		ModelAndView mv = new JModelAndView(
				"seller_apply/seller_store_gc_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = this.userService.getObjById(CommUtil.null2Long(uid));
		Store store = user.getStore();
		String[] str = gc_ids.split(",");
		List<Map> list = new ArrayList<Map>();
		Map map = new HashMap();
		List gc_list = new ArrayList();
		GoodsClass parent = null;
		for (int i = 1; i < str.length; i++) {
			GoodsClass gc = this.goodsClassService.getObjById(CommUtil
					.null2Long(str[i]));
			parent = gc.getParent();
		}
		Map map_temp = this.storeTools
				.query_MainGc_Map(CommUtil.null2String(parent.getId()),
						store.getGc_detail_info());
		if (map_temp != null) {// 同一个一级类目
			gc_list = (List) map_temp.get("gc_list");
			List<GoodsClass> gc_mains = this.storeTools
					.query_store_MainGc(store.getGc_detail_info());
			for (GoodsClass gc_main : gc_mains) {
				if (gc_main.getId() != parent.getId()) {
					Map map_main = this.storeTools.query_MainGc_Map(
							CommUtil.null2String(gc_main.getId()),
							store.getGc_detail_info());
					list.add(map_main);
				}
			}
		}
		if (map_temp == null) {// 不同的一级类目
			List<GoodsClass> gc_mains = this.storeTools
					.query_store_MainGc(store.getGc_detail_info());
			for (GoodsClass gc_main : gc_mains) {
				Map map_main = this.storeTools.query_MainGc_Map(
						CommUtil.null2String(gc_main.getId()),
						store.getGc_detail_info());
				list.add(map_main);
			}
		}
		for (int i = 1; i < str.length; i++) {
			GoodsClass gc = this.goodsClassService.getObjById(CommUtil
					.null2Long(str[i]));
			Set<GoodsClass> detail_info = this.storeTools
					.query_store_DetailGc(store.getGc_detail_info());
			if (detail_info.size() == 0) {
				gc_list.add(gc.getId());
			} else {
				int m = 0;
				for (GoodsClass gc2 : detail_info) {
					if (gc.getId() != gc2.getId()) {
						m++;
					}
				}
				if (m == detail_info.size()) {
					gc_list.add(gc.getId());
				}
			}
			parent = gc.getParent();
		}
		map.put("m_id", parent.getId());
		map.put("gc_list", gc_list);
		list.add(map);
		store.setGc_detail_info(Json.toJson(list, JsonFormat.compact()));
		this.storeService.update(store);
		Set<GoodsClass> all_details_gcs = this.storeTools
				.query_store_DetailGc(store.getGc_detail_info());
		mv.addObject("details_gcs", all_details_gcs);
		return mv;
	}

	/**
	 * 店铺经营详细类目删除
	 * 
	 * @param request
	 * @param response
	 * @param goodsClass
	 * @return
	 */
	@SecurityMapping(title = "店铺经营详细类目删除", value = "/seller_goodsclass_del.htm*", rtype = "buyer", rname = "商家入驻", rcode = "seller_apply", rgroup = "商家入驻")
	@RequestMapping("/seller_goodsclass_del.htm")
	public ModelAndView seller_goodsclass_del(HttpServletRequest request,
			HttpServletResponse response, String gc_id) {
		ModelAndView mv = new JModelAndView(
				"seller_apply/seller_store_gc_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		Store store = user.getStore();
		GoodsClass gc_detail = this.goodsClassService.getObjById(CommUtil
				.null2Long(gc_id));
		GoodsClass gc_main = this.goodsClassService.getObjById(gc_detail
				.getParent().getId());
		Map map_temp = this.storeTools.query_MainGc_Map(
				CommUtil.null2String(gc_main.getId()),
				store.getGc_detail_info());
		List<Integer> gc_list = (List) map_temp.get("gc_list");
		for (int i = 0; i < gc_list.size(); i++) {
			if (gc_list.get(i).longValue() == gc_detail.getId()) {// 删除该详细类目
				gc_list.remove(i);
				break;
			}
		}
		map_temp.put("m_id", gc_main.getId());
		map_temp.put("gc_list", gc_list);

		List<Map> list = new ArrayList<Map>();// 调整store中的数据
		Map map = new HashMap();
		List<GoodsClass> gc_mains = this.storeTools.query_store_MainGc(store
				.getGc_detail_info());
		for (GoodsClass gc : gc_mains) {
			if (gc.getId() == gc_main.getId()) {// 删除对应类目
				if (gc_list.size() > 0) {
					list.add(map_temp);
				}
			} else {
				Map map_main = this.storeTools.query_MainGc_Map(
						CommUtil.null2String(gc.getId()),
						store.getGc_detail_info());
				list.add(map_main);
			}
		}
		store.setGc_detail_info(Json.toJson(list, JsonFormat.compact()));
		this.storeService.update(store);
		mv.addObject("details_gcs",
				this.storeTools.query_store_DetailGc(store.getGc_detail_info()));
		mv.addObject("store", store);
		return mv;
	}
}
