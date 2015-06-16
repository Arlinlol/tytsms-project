package com.iskyshop.manage.seller.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.Area;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.StoreSlide;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IAreaService;
import com.iskyshop.foundation.service.IRoleService;
import com.iskyshop.foundation.service.IStoreGradeService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.IStoreSlideService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.pay.wechatpay.util.ConfigContants;
import com.iskyshop.pay.wechatpay.util.TytsmsStringUtils;
import com.iskyshop.view.web.tools.AreaViewTools;
import com.iskyshop.view.web.tools.StoreViewTools;

/**
 * 
 * <p>
 * Title: StoreSellerAction.java
 * </p>
 * 
 * <p>
 * Description: 卖家中心店铺控制器
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
 * @date 2014-4-25
 * 
 * @version iskyshop_b2b2c 1.0
 */
@Controller
public class StoreSellerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IStoreGradeService storeGradeService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IRoleService roleService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IStoreSlideService storeSlideService;
	@Autowired
	private StoreViewTools storeTools;
	@Autowired
	private AreaViewTools areaViewTools;

	@SecurityMapping(title = "店铺设置", value = "/seller/store_set.htm*", rtype = "seller", rname = "店铺设置", rcode = "store_set_seller", rgroup = "我的店铺")
	@RequestMapping("/seller/store_set.htm")
	public ModelAndView store_set(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/store_set.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		mv.addObject("store", store);
		mv.addObject("areaViewTools", areaViewTools);
		List<Area> areas = this.areaService.query(
				"select obj from Area obj where obj.parent.id is null", null,
				-1, -1);
		mv.addObject("areas", areas);
		return mv;
	}

	@SecurityMapping(title = "店铺设置保存", value = "/seller/store_set_save.htm*", rtype = "seller", rname = "店铺设置", rcode = "store_set_seller", rgroup = "我的店铺")
	@RequestMapping("/seller/store_set_save.htm")
	public String store_set_save(HttpServletRequest request,
			HttpServletResponse response, String area_id,
			String store_second_domain, String mobile) {
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		WebForm wf = new WebForm();
		wf.toPo(request, store);
		// 图片上传开始logo
		String uploadFilePath = ConfigContants.UPLOAD_IMAGE_MIDDLE_NAME;
		String saveFilePathName =  TytsmsStringUtils.generatorImagesFolderServerPath(request)
				+ uploadFilePath + "/store_logo";
		Map map = new HashMap();
		try {
			String fileName = store.getStore_logo() == null ? "" : store
					.getStore_logo().getName();
			map = CommUtil.saveFileToServer(configService,request, "logo", saveFilePathName,
					fileName, null);
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory store_logo = new Accessory();
					store_logo
							.setName(CommUtil.null2String(map.get("fileName")));
					store_logo.setExt(CommUtil.null2String(map.get("mime")));
					store_logo.setSize(BigDecimal.valueOf(CommUtil
							.null2Double(map.get("fileSize"))));
					store_logo.setPath(uploadFilePath + "/store_logo");
					store_logo.setWidth(CommUtil.null2Int(map.get("width")));
					store_logo.setHeight(CommUtil.null2Int(map.get("height")));
					store_logo.setAddTime(new Date());
					this.accessoryService.save(store_logo);
					store.setStore_logo(store_logo);
				}
			} else {
				if (map.get("fileName") != "") {
					Accessory store_logo = store.getStore_logo();
					store_logo
							.setName(CommUtil.null2String(map.get("fileName")));
					store_logo.setExt(CommUtil.null2String(map.get("mime")));
					store_logo.setSize(BigDecimal.valueOf(CommUtil
							.null2Double(map.get("fileSize"))));
					store_logo.setPath(uploadFilePath + "/store_logo");
					store_logo.setWidth(CommUtil.null2Int(map.get("width")));
					store_logo.setHeight(CommUtil.null2Int(map.get("height")));
					this.accessoryService.update(store_logo);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//
		saveFilePathName =  TytsmsStringUtils.generatorImagesFolderServerPath(request)
				+ ConfigContants.UPLOAD_IMAGE_MIDDLE_NAME
				+ "/store_banner";
		try {
			String fileName = store.getStore_banner() == null ? "" : store
					.getStore_banner().getName();
			map = CommUtil.saveFileToServer(configService,request, "banner",
					saveFilePathName, fileName, null);
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory store_banner = new Accessory();
					store_banner.setName(CommUtil.null2String(map
							.get("fileName")));
					store_banner.setExt(CommUtil.null2String(map.get("mime")));
					store_banner.setSize(BigDecimal.valueOf(CommUtil
							.null2Double(map.get("fileSize"))));
					store_banner.setPath(uploadFilePath + "/store_banner");
					store_banner.setWidth(CommUtil.null2Int(map.get("width")));
					store_banner
							.setHeight(CommUtil.null2Int(map.get("height")));
					store_banner.setAddTime(new Date());
					this.accessoryService.save(store_banner);
					store.setStore_banner(store_banner);
				}
			} else {
				if (map.get("fileName") != "") {
					Accessory store_banner = store.getStore_banner();
					store_banner.setName(CommUtil.null2String(map
							.get("fileName")));
					store_banner.setExt(CommUtil.null2String(map.get("mime")));
					store_banner.setSize(BigDecimal.valueOf(CommUtil
							.null2Double(map.get("fileSize"))));
					store_banner.setPath(uploadFilePath + "/store_banner");
					store_banner.setWidth(CommUtil.null2Int(map.get("width")));
					store_banner
							.setHeight(CommUtil.null2Int(map.get("height")));
					this.accessoryService.update(store_banner);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Area area = this.areaService.getObjById(CommUtil.null2Long(area_id));
		store.setArea(area);
		if (this.configService.getSysConfig().isSecond_domain_open()
				&& this.configService.getSysConfig().getDomain_allow_count() > store
						.getDomain_modify_count()) {
			if (!CommUtil.null2String(store_second_domain).equals("")
					&& !store_second_domain.equals(store
							.getStore_second_domain())) {
				store.setStore_second_domain(store_second_domain);
				store.setDomain_modify_count(store.getDomain_modify_count() + 1);
			}
		}
		this.storeService.update(store);
		if (mobile != null && !mobile.equals("")) {
			user.setMobile(mobile);
			this.userService.update(user);
		}
		request.getSession(false).setAttribute("url",
				CommUtil.getURL(request) + "/seller/store_set.htm");
		request.getSession(false).setAttribute("op_title", "店铺设置成功");
		return "redirect:/seller/success.htm";
	}

	/**
	 * 店铺二级域名申请
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@SecurityMapping(title = "店铺二级域名申请", value = "/seller/store_sld.htm*", rtype = "seller", rname = "二级域名", rcode = "store_sld_seller", rgroup = "我的店铺")
	@RequestMapping("/seller/store_sld.htm")
	public ModelAndView store_sld(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/store_sld.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		SysConfig sysconfig = this.configService.getSysConfig();
		String serverName = request.getServerName();
		if (serverName.equals("localhost") || CommUtil.isIp(serverName)
				|| serverName.indexOf("www.") > 0) {
			mv = new JModelAndView(
					"user/default/sellercenter/seller_error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未绑定顶级域名，无法设定二级域名");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
		} else {
			if (sysconfig.isSecond_domain_open()) {
				User user = this.userService.getObjById(SecurityUserHolder
						.getCurrentUser().getId());
				user = user.getParent() == null ? user : user.getParent();
				Store store = user.getStore();
				String store_second_domain = "";
				if (!CommUtil.null2String(store.getStore_second_domain())
						.equals("")) {
					store_second_domain = store.getStore_second_domain() + "."
							+ CommUtil.generic_domain(request);
				} else {
					store_second_domain = user.getUsername() + "."
							+ CommUtil.generic_domain(request);
				}
				mv.addObject("store_second_domain",
						store_second_domain.split("\\.")[0]);
				mv.addObject("store", store);
				mv.addObject("serverName", serverName.substring(4));
			} else {
				mv = new JModelAndView(
						"user/default/sellercenter/seller_error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				mv.addObject("op_title", "系统未开启二级域名");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/seller/index.htm");
			}
		}

		return mv;
	}

	/**
	 * 二级域名申请保存成功
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "二级域名申请保存", value = "/seller/store_sld_save.htm*", rtype = "seller", rname = "二级域名", rcode = "store_sld_seller", rgroup = "我的店铺")
	@RequestMapping("/seller/store_sld_save.htm")
	public String store_sld_save(HttpServletRequest request,
			HttpServletResponse response, String store_second_domain) {
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		WebForm wf = new WebForm();
		wf.toPo(request, store);
		if (this.configService.getSysConfig().isSecond_domain_open()
				&& this.configService.getSysConfig().getDomain_allow_count() > store
						.getDomain_modify_count()) {
			if (!CommUtil.null2String(store_second_domain).equals("")
					&& !store_second_domain.equals(store
							.getStore_second_domain())) {
				store.setStore_second_domain(store_second_domain);
				store.setDomain_modify_count(store.getDomain_modify_count() + 1);
			}
		}
		this.storeService.update(store);
		request.getSession(false).setAttribute("url",
				CommUtil.getURL(request) + "/seller/store_sld.htm");
		request.getSession(false).setAttribute("op_title", "店铺二级域名设置成功");
		return "redirect:/seller/success.htm";
	}

	/**
	 * 店铺二级域名验证
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@SecurityMapping(title = "店铺二级域名验证", value = "/seller/store_sld_verify.htm*", rtype = "seller", rname = "二级域名", rcode = "store_sld_seller", rgroup = "我的店铺")
	@RequestMapping("/seller/store_sld_verify.htm")
	public void store_sld_verify(HttpServletRequest request,
			HttpServletResponse response, String sld_name, String store_id)
			throws IOException {
		SysConfig sysconfig = this.configService.getSysConfig();
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		boolean ret = true;
		for (String domain : CommUtil.str2list(sysconfig.getSys_domain())) {// 系统限定域名不可以绑定
			if (domain.equals(sld_name)) {
				ret = false;
				break;
			}
		}
		if (sysconfig.getDomain_allow_count() > 0
				&& store.getDomain_modify_count() >= sysconfig
						.getDomain_allow_count()) {// 超过系统允许修改的次数，不允许修改
			ret = false;
		}
		Map params = new HashMap();
		params.put("store_second_domain", CommUtil.null2String(sld_name));
		params.put("store_id", CommUtil.null2Long(store_id));
		List<Store> stores = this.storeService
				.query("select obj from Store obj where obj.store_second_domain=:store_second_domain and obj.id!=:store_id",
						params, -1, -1);
		if (stores.size() > 0) {
			ret = false;
		}
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

	@SecurityMapping(title = "店铺幻灯", value = "/seller/store_slide.htm*", rtype = "seller", rname = "店铺设置", rcode = "store_set_seller", rgroup = "我的店铺")
	@RequestMapping("/seller/store_slide.htm")
	public ModelAndView store_slide(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/store_slide.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		mv.addObject("store", store);
		return mv;
	}

	@SecurityMapping(title = "店铺幻灯保存", value = "/seller/store_slide_save.htm*", rtype = "seller", rname = "店铺设置", rcode = "store_set_seller", rgroup = "我的店铺")
	@RequestMapping("/seller/store_slide_save.htm")
	public ModelAndView store_slide_save(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/store_slide.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		String uploadFilePath = ConfigContants.UPLOAD_IMAGE_MIDDLE_NAME;
		String saveFilePathName =  TytsmsStringUtils.generatorImagesFolderServerPath(request)
				+ uploadFilePath + File.separator + "store_slide";
		for (int i = 1; i <= 5; i++) {
			Map map = new HashMap();
			String fileName = "";
			StoreSlide slide = null;
			if (store.getSlides().size() >= i) {
				fileName = store.getSlides().get(i - 1).getAcc().getName();
				slide = store.getSlides().get(i - 1);
			}
			try {
				map = CommUtil.saveFileToServer(configService,request, "acc" + i,
						saveFilePathName, fileName, null);
				Accessory acc = null;
				if (fileName.equals("")) {
					if (map.get("fileName") != "") {
						acc = new Accessory();
						acc.setName(CommUtil.null2String(map.get("fileName")));
						acc.setExt(CommUtil.null2String(map.get("mime")));
						acc.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
								.get("fileSize"))));
						acc.setPath(uploadFilePath + "/store_slide");
						acc.setWidth(CommUtil.null2Int(map.get("width")));
						acc.setHeight(CommUtil.null2Int(map.get("height")));
						acc.setAddTime(new Date());
						this.accessoryService.save(acc);
					}
				} else {
					if (map.get("fileName") != "") {
						acc = slide.getAcc();
						acc.setName(CommUtil.null2String(map.get("fileName")));
						acc.setExt(CommUtil.null2String(map.get("mime")));
						acc.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
								.get("fileSize"))));
						acc.setPath(uploadFilePath + "/store_slide");
						acc.setWidth(CommUtil.null2Int(map.get("width")));
						acc.setHeight(CommUtil.null2Int(map.get("height")));
						acc.setAddTime(new Date());
						this.accessoryService.update(acc);
					}
				}
				if (acc != null) {
					if (slide == null) {
						slide = new StoreSlide();
						slide.setAcc(acc);
						slide.setAddTime(new Date());
						slide.setStore(store);
					}
					slide.setUrl(request.getParameter("acc_url" + i));
					if (slide == null) {
						this.storeSlideService.save(slide);
					} else {
						this.storeSlideService.update(slide);
					}
				} else {
					if (slide != null) {
						slide.setUrl(request.getParameter("acc_url" + i));
						this.storeSlideService.update(slide);
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		mv.addObject("store", store);
		return mv;
	}

}
