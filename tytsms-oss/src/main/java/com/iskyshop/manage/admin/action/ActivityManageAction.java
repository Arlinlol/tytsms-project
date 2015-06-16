package com.iskyshop.manage.admin.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.easyjf.beans.BeanUtils;
import com.easyjf.beans.BeanWrapper;
import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.Activity;
import com.iskyshop.foundation.domain.ActivityGoods;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.query.ActivityGoodsQueryObject;
import com.iskyshop.foundation.domain.query.ActivityQueryObject;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IActivityGoodsService;
import com.iskyshop.foundation.service.IActivityService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.pay.wechatpay.util.ConfigContants;
import com.iskyshop.pay.wechatpay.util.TytsmsStringUtils;

/**
 * 
 * <p>
 * Title: ActivityManageAction.java
 * </p>
 * 
 * <p>
 * Description: 商城活动管理类
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
 * @date 2014年5月21日
 * 
 * @version 1.0
 */

@Controller
public class ActivityManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IActivityService activityService;
	@Autowired
	private IActivityGoodsService activityGoodsService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IGoodsService goodService;

	/**
	 * Activity列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "活动列表", value = "/admin/activity_list.htm*", rtype = "admin", rname = "活动管理", rcode = "activity_admin", rgroup = "运营")
	@RequestMapping("/admin/activity_list.htm")
	public ModelAndView activity_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String q_ac_title, String ac_status,
			String beginTime, String endTime) {
		ModelAndView mv = new JModelAndView("admin/blue/activity_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		ActivityQueryObject qo = new ActivityQueryObject(currentPage, mv,
				orderBy, orderType);
		if (!CommUtil.null2String(q_ac_title).equals("")) {
			qo.addQuery("obj.ac_title", new SysMap("q_ac_title", "%"
					+ q_ac_title.trim() + "%"), "like");
		}
		if (!CommUtil.null2String(ac_status).equals("")) {
			qo.addQuery("obj.ac_status",
					new SysMap("ac_status", CommUtil.null2Int(ac_status)), "=");
		}
		if (!CommUtil.null2String(beginTime).equals("")) {
			qo.addQuery("obj.ac_begin_time",
					new SysMap("beginTime", CommUtil.formatDate(beginTime)),
					">=");
		}
		if (!CommUtil.null2String(endTime).equals("")) {
			qo.addQuery("obj.ac_end_time",
					new SysMap("endTime", CommUtil.formatDate(endTime)), "<=");
		}
		IPageList pList = this.activityService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("q_ac_title", q_ac_title);
		mv.addObject("ac_status", ac_status);
		mv.addObject("beginTime", beginTime);
		mv.addObject("endTime", endTime);
		return mv;
	}

	/**
	 * activity添加管理
	 * 
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "活动添加", value = "/admin/activity_add.htm*", rtype = "admin", rname = "活动管理", rcode = "activity_admin", rgroup = "运营")
	@RequestMapping("/admin/activity_add.htm")
	public ModelAndView activity_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/activity_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	/**
	 * activity编辑管理
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "活动编辑", value = "/admin/activity_edit.htm*", rtype = "admin", rname = "活动管理", rcode = "activity_admin", rgroup = "运营")
	@RequestMapping("/admin/activity_edit.htm")
	public ModelAndView activity_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/activity_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			Activity activity = this.activityService.getObjById(Long
					.parseLong(id));
			mv.addObject("obj", activity);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", true);
		}
		return mv;
	}

	/**
	 * activity保存管理
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "活动保存", value = "/admin/activity_save.htm*", rtype = "admin", rname = "活动管理", rcode = "activity_admin", rgroup = "运营")
	@RequestMapping("/admin/activity_save.htm")
	public ModelAndView activity_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String ac_begin_time, String ac_end_time) {
		WebForm wf = new WebForm();
		Activity activity = null;
		if (id.equals("")) {
			activity = wf.toPo(request, Activity.class);
			activity.setAddTime(new Date());
		} else {
			Activity obj = this.activityService.getObjById(Long.parseLong(id));
			activity = (Activity) wf.toPo(request, obj);
		}
		String uploadFilePath = ConfigContants.UPLOAD_IMAGE_MIDDLE_NAME;
		String saveFilePathName = TytsmsStringUtils.generatorImagesFolderServerPath(request)
				+ uploadFilePath + File.separator + "activity";
		Map map1 = new HashMap();// 活动主横幅图片
		try {
			String fileName = activity.getAc_acc() == null ? "" : activity
					.getAc_acc().getName();
			map1 = CommUtil.saveFileToServer(configService,request, "acc", saveFilePathName,
					fileName, null);
			if (fileName.equals("")) {
				if (map1.get("fileName") != "") {
					Accessory ac_acc = new Accessory();
					ac_acc.setName(CommUtil.null2String(map1.get("fileName")));
					ac_acc.setExt(CommUtil.null2String(map1.get("mime")));
					ac_acc.setSize(BigDecimal.valueOf(CommUtil.null2Double(map1
							.get("fileSize"))));
					ac_acc.setPath(uploadFilePath + "/activity");
					ac_acc.setWidth(CommUtil.null2Int(map1.get("width")));
					ac_acc.setHeight(CommUtil.null2Int(map1.get("height")));
					ac_acc.setAddTime(new Date());
					this.accessoryService.save(ac_acc);
					activity.setAc_acc(ac_acc);
				}
			} else {
				if (map1.get("fileName") != "") {
					Accessory ac_acc = activity.getAc_acc();
					ac_acc.setName(CommUtil.null2String(map1.get("fileName")));
					ac_acc.setExt(CommUtil.null2String(map1.get("mime")));
					ac_acc.setSize(BigDecimal.valueOf(CommUtil.null2Double(map1
							.get("fileSize"))));
					ac_acc.setPath(uploadFilePath + "/activity");
					ac_acc.setWidth(CommUtil.null2Int(map1.get("width")));
					ac_acc.setHeight(CommUtil.null2Int(map1.get("height")));
					ac_acc.setAddTime(new Date());
					this.accessoryService.update(ac_acc);
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Map map2 = new HashMap();// 活动主横幅图片旁小图
		try {
			String fileName = activity.getAc_acc2() == null ? "" : activity
					.getAc_acc2().getName();
			map2 = CommUtil.saveFileToServer(configService,request, "acc2", saveFilePathName,
					fileName, null);
			if (fileName.equals("")) {
				if (map2.get("fileName") != "") {
					Accessory ac_acc = new Accessory();
					ac_acc.setName(CommUtil.null2String(map2.get("fileName")));
					ac_acc.setExt(CommUtil.null2String(map2.get("mime")));
					ac_acc.setSize(BigDecimal.valueOf(CommUtil.null2Double(map2
							.get("fileSize"))));
					ac_acc.setPath(uploadFilePath + "/activity");
					ac_acc.setWidth(CommUtil.null2Int(map2.get("width")));
					ac_acc.setHeight(CommUtil.null2Int(map2.get("height")));
					ac_acc.setAddTime(new Date());
					this.accessoryService.save(ac_acc);
					activity.setAc_acc2(ac_acc);
				}
			} else {
				if (map2.get("fileName") != "") {
					Accessory ac_acc = activity.getAc_acc2();
					ac_acc.setName(CommUtil.null2String(map2.get("fileName")));
					ac_acc.setExt(CommUtil.null2String(map2.get("mime")));
					ac_acc.setSize(BigDecimal.valueOf(CommUtil.null2Double(map2
							.get("fileSize"))));
					ac_acc.setPath(uploadFilePath + "/activity");
					ac_acc.setWidth(CommUtil.null2Int(map2.get("width")));
					ac_acc.setHeight(CommUtil.null2Int(map2.get("height")));
					ac_acc.setAddTime(new Date());
					this.accessoryService.update(ac_acc);
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Map map3 = new HashMap();// 活动主横幅图片旁小图
		try {
			String fileName = activity.getAc_acc3() == null ? "" : activity
					.getAc_acc3().getName();
			map3 = CommUtil.saveFileToServer(configService,request, "acc3", saveFilePathName,
					fileName, null);
			if (fileName.equals("")) {
				if (map3.get("fileName") != "") {
					Accessory ac_acc = new Accessory();
					ac_acc.setName(CommUtil.null2String(map3.get("fileName")));
					ac_acc.setExt(CommUtil.null2String(map3.get("mime")));
					ac_acc.setSize(BigDecimal.valueOf(CommUtil.null2Double(map3
							.get("fileSize"))));
					ac_acc.setPath(uploadFilePath + "/activity");
					ac_acc.setWidth(CommUtil.null2Int(map3.get("width")));
					ac_acc.setHeight(CommUtil.null2Int(map3.get("height")));
					ac_acc.setAddTime(new Date());
					this.accessoryService.save(ac_acc);
					activity.setAc_acc3(ac_acc);
				}
			} else {
				if (map3.get("fileName") != "") {
					Accessory ac_acc = activity.getAc_acc3();
					ac_acc.setName(CommUtil.null2String(map3.get("fileName")));
					ac_acc.setExt(CommUtil.null2String(map3.get("mime")));
					ac_acc.setSize(BigDecimal.valueOf(CommUtil.null2Double(map3
							.get("fileSize"))));
					ac_acc.setPath(uploadFilePath + "/activity");
					ac_acc.setWidth(CommUtil.null2Int(map3.get("width")));
					ac_acc.setHeight(CommUtil.null2Int(map3.get("height")));
					ac_acc.setAddTime(new Date());
					this.accessoryService.update(ac_acc);
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		activity.setAc_begin_time(CommUtil.formatDate(ac_begin_time));
		activity.setAc_end_time(CommUtil.formatDate(ac_end_time));
		if (id.equals("")) {
			this.activityService.save(activity);
		} else
			this.activityService.update(activity);
		int status = activity.getAc_status();
		if(status==0){
			for(ActivityGoods ag : activity.getAgs()){
				if(ag.getAg_status()==1){
					ag.getAg_goods().setActivity_status(0);
					this.goodService.update(ag.getAg_goods());
				}	
			}
		}
		if(status==1){
			for(ActivityGoods ag : activity.getAgs()){
				if(ag.getAg_status()==1){
					ag.getAg_goods().setActivity_status(1);
					this.goodService.update(ag.getAg_goods());
				}	
			}
		}
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/admin/activity_list.htm");
		mv.addObject("op_title", "保存商城活动成功");
		mv.addObject("add_url", CommUtil.getURL(request)
				+ "/admin/activity_add.htm" + "?currentPage=" + currentPage);
		return mv;
	}

	@SecurityMapping(title = "活动删除", value = "/admin/activity_del.htm*", rtype = "admin", rname = "活动管理", rcode = "activity_admin", rgroup = "运营")
	@RequestMapping("/admin/activity_del.htm")
	public String activity_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				Activity activity = this.activityService.getObjById(Long
						.parseLong(id));
				for(ActivityGoods ag : activity.getAgs()){
					ag.getAg_goods().setActivity_status(0);
					this.activityGoodsService.delete(ag.getId());
					this.goodService.update(ag.getAg_goods());
				}
				CommUtil.del_acc(request, activity.getAc_acc());
				this.activityService.delete(Long.parseLong(id));
			}
		}
		return "redirect:activity_list.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "活动AJAX更新", value = "/admin/activity_ajax.htm*", rtype = "admin", rname = "活动管理", rcode = "activity_admin", rgroup = "运营")
	@RequestMapping("/admin/activity_ajax.htm")
	public void activity_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String fieldName,
			String value) throws ClassNotFoundException {
		Activity obj = this.activityService.getObjById(Long.parseLong(id));
		Field[] fields = Activity.class.getDeclaredFields();
		BeanWrapper wrapper = new BeanWrapper(obj);
		Object val = null;
		for (Field field : fields) {
			// System.out.println(field.getName());
			if (field.getName().equals(fieldName)) {
				Class clz = Class.forName("java.lang.String");
				if (field.getType().getName().equals("int")) {
					clz = Class.forName("java.lang.Integer");
				}
				if (field.getType().getName().equals("boolean")) {
					clz = Class.forName("java.lang.Boolean");
				}
				if (!value.equals("")) {
					val = BeanUtils.convertType(value, clz);
				} else {
					val = !CommUtil.null2Boolean(wrapper
							.getPropertyValue(fieldName));
				}
				wrapper.setPropertyValue(fieldName, val);
			}
		}
		this.activityService.update(obj);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(val.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@SecurityMapping(title = "活动商品列表", value = "/admin/activity_goods_list.htm*", rtype = "admin", rname = "活动管理", rcode = "activity_admin", rgroup = "运营")
	@RequestMapping("/admin/activity_goods_list.htm")
	public ModelAndView activity_goods_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String act_id, String goods_name, String ag_status) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/activity_goods_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		ActivityGoodsQueryObject qo = new ActivityGoodsQueryObject(currentPage,
				mv, orderBy, orderType);
		qo.addQuery("obj.act.id",
				new SysMap("act_id", CommUtil.null2Long(act_id)), "=");
		if (!CommUtil.null2String(ag_status).equals("")) {
			qo.addQuery("obj.ag_status",
					new SysMap("ag_status", CommUtil.null2Int(ag_status)), "=");
		}
		if (!CommUtil.null2String(goods_name).equals("")) {
			qo.addQuery("obj.ag_goods.goods_name", new SysMap("goods_name", "%"
					+ goods_name.trim() + "goods_name"), "=");
		}
		IPageList pList = this.activityGoodsService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("ag_status", ag_status);
		mv.addObject("goods_name", goods_name);
		mv.addObject("act_id", act_id);
		return mv;
	}

	@SecurityMapping(title = "活动通过", value = "/admin/activity_goods_audit.htm*", rtype = "admin", rname = "活动管理", rcode = "activity_admin", rgroup = "运营")
	@RequestMapping("/admin/activity_goods_audit.htm")
	@Transactional
	public String activity_goods_audit(HttpServletRequest request,
			HttpServletResponse response, String act_id, String mulitId,
			String currentPage) {
		Activity act = this.activityService.getObjById(CommUtil
				.null2Long(act_id));
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				ActivityGoods ac = this.activityGoodsService.getObjById(Long
						.parseLong(id));
				ac.setAg_status(1);
				this.activityGoodsService.update(ac);
				// 活动状态
				// 活动状态，0为无活动，1为待审核，2为审核通过，3为活动商品已经卖完
				Goods goods = ac.getAg_goods();
				goods.setActivity_price(BigDecimal.valueOf(CommUtil.mul(
						goods.getStore_price(), act.getAc_rebate())));// 设置铜牌会员商品价格
				goods.setActivity_price1(BigDecimal.valueOf(CommUtil.mul(
						goods.getStore_price(), act.getAc_rebate1())));// 设置银牌会员商品价格
				goods.setActivity_price2(BigDecimal.valueOf(CommUtil.mul(
						goods.getStore_price(), act.getAc_rebate2())));// 设置金牌会员商品价格
				goods.setActivity_price3(BigDecimal.valueOf(CommUtil.mul(
						goods.getStore_price(), act.getAc_rebate3())));// 设置超级会员商品价格
				goods.setActivity_status(2);
				this.goodService.update(goods);
			}
		}
		return "redirect:activity_goods_list.htm?act_id=" + act_id
				+ "&currentPage=" + currentPage;
	}

	@SecurityMapping(title = "活动拒绝", value = "/admin/activity_goods_refuse.htm*", rtype = "admin", rname = "活动管理", rcode = "activity_admin", rgroup = "运营")
	@RequestMapping("/admin/activity_goods_refuse.htm")
	@Transactional
	public String activity_goods_refuse(HttpServletRequest request,
			HttpServletResponse response, String act_id, String mulitId,
			String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				ActivityGoods ac = this.activityGoodsService.getObjById(Long
						.parseLong(id));
				ac.setAg_status(-1);
				this.activityGoodsService.update(ac);
				// 活动状态
				Goods goods = ac.getAg_goods();
				goods.setActivity_status(0);
				// 恢复原价
				goods.setGoods_current_price(goods.getStore_price());
				this.goodService.update(goods);
			}
		}
		return "redirect:activity_goods_list.htm?act_id=" + act_id
				+ "&currentPage=" + currentPage;
	}
}