package com.iskyshop.manage.admin.action;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
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
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.Advert;
import com.iskyshop.foundation.domain.AdvertPosition;
import com.iskyshop.foundation.domain.GoldLog;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.AdvertPositionQueryObject;
import com.iskyshop.foundation.domain.query.AdvertQueryObject;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IAdvertPositionService;
import com.iskyshop.foundation.service.IAdvertService;
import com.iskyshop.foundation.service.IGoldLogService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.pay.wechatpay.util.ConfigContants;
import com.iskyshop.pay.wechatpay.util.TytsmsStringUtils;

/**
 * 
* <p>Title: AdvertManageAction.java</p>

* <p>Description: 商城广告管理器1.0版</p>

* <p>Copyright: Copyright (c) 2014</p>

* <p>Company: 沈阳网之商科技有限公司 www.iskyshop.com</p>

* @author erikzhang

* @date 2014年5月27日

* @version 1.0
 */
@Controller
public class AdvertManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IAdvertService advertService;
	@Autowired
	private IAdvertPositionService advertPositionService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IGoldLogService goldLogService;

	/**
	 * Advert列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "广告列表", value = "/admin/advert_list.htm*", rtype = "admin", rname = "广告管理", rcode = "advert_admin", rgroup = "运营")
	@RequestMapping("/admin/advert_list.htm")
	public ModelAndView advert_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String ad_title) {
		ModelAndView mv = new JModelAndView("admin/blue/advert_list.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		AdvertQueryObject qo = new AdvertQueryObject(currentPage, mv, orderBy,
				orderType);
		if (!CommUtil.null2String(ad_title).equals("")) {
			qo.addQuery("obj.ad_title", new SysMap("ad_title", "%"
					+ ad_title.trim() + "%"), "like");
		}
		IPageList pList = this.advertService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("ad_title", ad_title);
		mv.addObject("currentPage",currentPage);
		return mv;
	}

	/**
	 * 待审批广告
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param ad_title
	 * @return
	 */
	@SecurityMapping(title = "待审批广告列表", value = "/admin/advert_list_audit.htm*", rtype = "admin", rname = "广告管理", rcode = "advert_admin", rgroup = "运营")
	@RequestMapping("/admin/advert_list_audit.htm")
	public ModelAndView advert_list_audit(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String ad_title) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/advert_list_audit.html", configService
						.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		AdvertQueryObject qo = new AdvertQueryObject(currentPage, mv, orderBy,
				orderType);
		if (!CommUtil.null2String(ad_title).equals("")) {
			qo.addQuery("obj.ad_title", new SysMap("ad_title", "%"
					+ ad_title.trim() + "%"), "like");
		}
		qo.addQuery("obj.ad_status", new SysMap("ad_status", 0), "=");
		IPageList pList = this.advertService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("ad_title", ad_title);
		return mv;
	}

	/**
	 * advert添加管理
	 * 
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "广告增加", value = "/admin/advert_add.htm*", rtype = "admin", rname = "广告管理", rcode = "advert_admin", rgroup = "运营")
	@RequestMapping("/admin/advert_add.htm")
	public ModelAndView advert_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/advert_add.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		List<AdvertPosition> aps = this.advertPositionService.query(
				"select obj from AdvertPosition obj", null, -1, -1);
		mv.addObject("aps", aps);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	/**
	 * advert编辑管理
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "广告编辑", value = "/admin/advert_edit.htm*", rtype = "admin", rname = "广告管理", rcode = "advert_admin", rgroup = "运营")
	@RequestMapping("/admin/advert_edit.htm")
	public ModelAndView advert_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/advert_add.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			Advert advert = this.advertService.getObjById(Long.parseLong(id));
			List<AdvertPosition> aps = this.advertPositionService.query(
					"select obj from AdvertPosition obj", null, -1, -1);
			mv.addObject("aps", aps);
			mv.addObject("obj", advert);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", true);
		}
		return mv;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "广告查看", value = "/admin/advert_view.htm*", rtype = "admin", rname = "广告管理", rcode = "advert_admin", rgroup = "运营")
	@RequestMapping("/admin/advert_view.htm")
	public ModelAndView advert_view(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/advert_view.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			Advert advert = this.advertService.getObjById(Long.parseLong(id));
			mv.addObject("obj", advert);
			mv.addObject("currentPage", currentPage);
		}
		return mv;
	}

	@SecurityMapping(title = "广告审核", value = "/admin/advert_audit.htm*", rtype = "admin", rname = "广告管理", rcode = "advert_admin", rgroup = "运营")
	@RequestMapping("/admin/advert_audit.htm")
	public ModelAndView advert_audit(HttpServletRequest request,
			HttpServletResponse response, String id, String ad_status,
			String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		Advert obj = this.advertService.getObjById(CommUtil.null2Long(id));
		obj.setAd_status(CommUtil.null2Int(ad_status));
		this.advertService.update(obj);
		if (obj.getAd_status() == 1 && obj.getAd_ap().getAp_show_type() == 0) {
			AdvertPosition ap = obj.getAd_ap();
			ap.setAp_use_status(1);
			this.advertPositionService.update(ap);
		}
		if (obj.getAd_status() == -1) {
			User user = obj.getAd_user();
			user.setGold(user.getGold() + obj.getAd_gold());
			this.userService.update(user);
			GoldLog log = new GoldLog();
			log.setAddTime(new Date());
			log.setGl_content("广告审核失败，恢复金币");
			log.setGl_count(obj.getAd_gold());
			log.setGl_user(obj.getAd_user());
			log.setGl_type(0);
			this.goldLogService.save(log);
		}
		mv.addObject("op_title", "广告审核成功");
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/admin/advert_list_audit.htm?currentPage=" + currentPage);
		return mv;
	}

	/**
	 * advert保存管理
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "广告保存", value = "/admin/advert_save.htm*", rtype = "admin", rname = "广告管理", rcode = "advert_admin", rgroup = "运营")
	@RequestMapping("/admin/advert_save.htm")
	@Transactional
	public ModelAndView advert_save(HttpServletRequest request,
			HttpServletResponse response, String id, String ad_ap_id,
			String currentPage, String ad_begin_time, String ad_end_time) {
		WebForm wf = new WebForm();
		Advert advert = null;
		if (id.equals("")) {
			advert = wf.toPo(request, Advert.class);
			advert.setAddTime(new Date());
			advert.setAd_user(SecurityUserHolder.getCurrentUser());
		} else {
			Advert obj = this.advertService.getObjById(Long.parseLong(id));
			advert = (Advert) wf.toPo(request, obj);
		}
		AdvertPosition ap = this.advertPositionService.getObjById(CommUtil
				.null2Long(ad_ap_id));
		advert.setAd_ap(ap);
		advert.setAd_begin_time(CommUtil.formatDate(ad_begin_time));
		advert.setAd_end_time(CommUtil.formatDate(ad_end_time));
		String uploadFilePath = ConfigContants.UPLOAD_IMAGE_MIDDLE_NAME;
		String saveFilePathName = TytsmsStringUtils.generatorImagesFolderServerPath(request)
				+ uploadFilePath + File.separator + "advert";
		Map map = new HashMap();
		String fileName = "";
		if (advert.getAd_acc() != null) {
			fileName = advert.getAd_acc().getName();
		}
		try {
			map = CommUtil.saveFileToServer(configService,request, "acc", saveFilePathName,
					fileName, null);
			Accessory acc = null;
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					acc = new Accessory();
					acc.setName(CommUtil.null2String(map.get("fileName")));
					acc.setExt(CommUtil.null2String(map.get("mime")));
					acc.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
					acc.setPath(uploadFilePath + "/advert");
					acc.setWidth(CommUtil.null2Int(map.get("width")));
					acc.setHeight(CommUtil.null2Int(map.get("height")));
					acc.setAddTime(new Date());
					this.accessoryService.save(acc);
					advert.setAd_acc(acc);
				}
			} else {
				if (map.get("fileName") != "") {
					acc = advert.getAd_acc();
					acc.setName(CommUtil.null2String(map.get("fileName")));
					acc.setExt(CommUtil.null2String(map.get("mime")));
					acc.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
					acc.setPath(uploadFilePath + "/advert");
					acc.setWidth(CommUtil.null2Int(map.get("width")));
					acc.setHeight(CommUtil.null2Int(map.get("height")));
					acc.setAddTime(new Date());
					this.accessoryService.update(acc);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (id.equals("")) {
			this.advertService.save(advert);
		} else
			this.advertService.update(advert);
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/admin/advert_list.htm?currentPage=" + currentPage);
		mv.addObject("op_title", "保存广告成功");
		mv.addObject("add_url", CommUtil.getURL(request)
				+ "/admin/advert_add.htm?currentPage=" + currentPage);
		return mv;
	}

	@SecurityMapping(title = "广告删除", value = "/admin/advert_del.htm*", rtype = "admin", rname = "广告管理", rcode = "advert_admin", rgroup = "运营")
	@RequestMapping("/admin/advert_del.htm")
	@Transactional
	public String advert_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				Advert advert = this.advertService.getObjById(Long
						.parseLong(id));
				if (advert.getAd_status() != 1) {
					CommUtil.del_acc(request, advert.getAd_acc());
					this.advertService.delete(Long.parseLong(id));
				}
			}
		}
		return "redirect:advert_list.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "广告位添加", value = "/admin/adv_pos_add.htm*", rtype = "admin", rname = "广告管理", rcode = "advert_admin", rgroup = "运营")
	@RequestMapping("/admin/adv_pos_add.htm")
	public ModelAndView adv_pos_add(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/adv_pos_add.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		List<Advert> advs = this.advertService.query(
				"select obj from Advert obj", null, -1, -1);
		mv.addObject("advs", advs);
		return mv;
	}

	@SecurityMapping(title = "广告位保存", value = "/admin/adv_pos_save.htm*", rtype = "admin", rname = "广告管理", rcode = "advert_admin", rgroup = "运营")
	@RequestMapping("/admin/adv_pos_save.htm")
	public ModelAndView adv_pos_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String cmd, String list_url, String add_url) {
		WebForm wf = new WebForm();
		AdvertPosition ap = null;
		if (id.equals("")) {
			ap = wf.toPo(request, AdvertPosition.class);
			ap.setAddTime(new Date());
		} else {
			AdvertPosition obj = this.advertPositionService.getObjById(Long
					.parseLong(id));
			ap = (AdvertPosition) wf.toPo(request, obj);
		}
		String uploadFilePath = ConfigContants.UPLOAD_IMAGE_MIDDLE_NAME;
		String saveFilePathName = TytsmsStringUtils.generatorImagesFolderServerPath(request)
				+ uploadFilePath + File.separator + "advert";
		Map map = new HashMap();
		String fileName = "";
		if (ap.getAp_acc() != null) {
			fileName = ap.getAp_acc().getName();
		}
		try {
			map = CommUtil.saveFileToServer(configService,request, "acc", saveFilePathName,
					fileName, null);
			Accessory acc = null;
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					acc = new Accessory();
					acc.setName(CommUtil.null2String(map.get("fileName")));
					acc.setExt(CommUtil.null2String(map.get("mime")));
					acc.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
					acc.setPath(uploadFilePath + "/advert");
					acc.setWidth(CommUtil.null2Int(map.get("width")));
					acc.setHeight(CommUtil.null2Int(map.get("height")));
					acc.setAddTime(new Date());
					this.accessoryService.save(acc);
					ap.setAp_acc(acc);
				}
			} else {
				if (map.get("fileName") != "") {
					acc = ap.getAp_acc();
					acc.setName(CommUtil.null2String(map.get("fileName")));
					acc.setExt(CommUtil.null2String(map.get("mime")));
					acc.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
					acc.setPath(uploadFilePath + "/advert");
					acc.setWidth(CommUtil.null2Int(map.get("width")));
					acc.setHeight(CommUtil.null2Int(map.get("height")));
					acc.setAddTime(new Date());
					this.accessoryService.update(acc);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (id.equals("")) {
			this.advertPositionService.save(ap);
		} else
			this.advertPositionService.update(ap);
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "保存广告位成功");
		if (add_url != null) {
			mv.addObject("add_url", add_url + "?currentPage=" + currentPage);
		}
		return mv;
	}

	@SecurityMapping(title = "广告位列表", value = "/admin/adv_pos_list.htm*", rtype = "admin", rname = "广告管理", rcode = "advert_admin", rgroup = "运营")
	@RequestMapping("/admin/adv_pos_list.htm")
	public ModelAndView adv_pos_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String ap_title) {
		ModelAndView mv = new JModelAndView("admin/blue/adv_pos_list.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		AdvertPositionQueryObject qo = new AdvertPositionQueryObject(
				currentPage, mv, orderBy, orderType);
		if (!CommUtil.null2String(ap_title).equals("")) {
			qo.addQuery("obj.ap_title", new SysMap("ap_title", "%" + ap_title
					+ "%"), "like");
		}
		IPageList pList = this.advertPositionService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("ap_title", ap_title);
		return mv;
	}

	@SecurityMapping(title = "广告位编辑", value = "/admin/adv_pos_edit.htm*", rtype = "admin", rname = "广告管理", rcode = "advert_admin", rgroup = "运营")
	@RequestMapping("/admin/adv_pos_edit.htm")
	public ModelAndView adv_pos_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/adv_pos_add.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			AdvertPosition obj = this.advertPositionService.getObjById(Long
					.parseLong(id));
			mv.addObject("obj", obj);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", true);
		}
		return mv;
	}

	@SecurityMapping(title = "广告位删除", value = "/admin/adv_pos_del.htm*", rtype = "admin", rname = "广告管理", rcode = "advert_admin", rgroup = "运营")
	@RequestMapping("/admin/adv_pos_del.htm")
	public String adv_pos_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				AdvertPosition ap = this.advertPositionService.getObjById(Long
						.parseLong(id));
				if (ap.getAp_sys_type() != 0) {
					CommUtil.del_acc(request, ap.getAp_acc());
					this.advertPositionService.delete(Long.parseLong(id));
				}
			}
		}
		return "redirect:adv_pos_list.htm?currentPage=" + currentPage;
	}

}