package com.iskyshop.manage.admin.action;

import java.text.ParseException;
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

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.TransArea;
import com.iskyshop.foundation.domain.Transport;
import com.iskyshop.foundation.domain.query.TransportQueryObject;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.ITransAreaService;
import com.iskyshop.foundation.service.ITransportService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.seller.Tools.TransportTools;

/**
 * 
 * <p>
 * Title: TransportSelfManageAction.java
 * </p>
 * 
 * <p>
 * Description: 商城自营商品运费模板管理控制器，用来添加、复制、编辑管理运费模板信息
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
 * @author jingxinzhe
 * 
 * @date 2014-5-9
 * 
 * @version iskyshop_b2b2c 1.0
 */
@Controller
public class TransportSelfManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private ITransportService transportService;
	@Autowired
	private ITransAreaService transAreaService;
	@Autowired
	private IUserService userService;
	@Autowired
	private TransportTools transportTools;

	/**
	 * Transport列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "运费模板列表", value = "/admin/transport_list.htm*", rtype = "admin", rname = "自营物流", rcode = "transport_self", rgroup = "自营")
	@RequestMapping("/admin/transport_list.htm")
	public ModelAndView transport_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView("admin/blue/transport_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		TransportQueryObject qo = new TransportQueryObject(currentPage, mv,
				orderBy, orderType);
		qo.addQuery("obj.trans_user", new SysMap("obj_trans_user", 0), "=");
		qo.setPageSize(3);
		IPageList pList = this.transportService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", params, pList, mv);
		mv.addObject("transportTools", transportTools);
		return mv;
	}

	/**
	 * transport添加管理
	 * 
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "运费模板添加", value = "/admin/transport_add.htm*", rtype = "admin", rname = "自营物流", rcode = "transport_self", rgroup = "自营")
	@RequestMapping("/admin/transport_add.htm")
	public ModelAndView transport_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/transport_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	/**
	 * transport编辑管理
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "运费模板编辑", value = "/admin/transport_edit.htm*", rtype = "admin", rname = "自营物流", rcode = "transport_self", rgroup = "自营")
	@RequestMapping("/admin/transport_edit.htm")
	public ModelAndView transport_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/transport_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			Transport transport = this.transportService.getObjById(Long
					.parseLong(id));
			mv.addObject("obj", transport);
			mv.addObject("currentPage", currentPage);
		}
		mv.addObject("transportTools", transportTools);
		return mv;
	}

	@SecurityMapping(title = "运费模板复制", value = "/admin/transport_copy.htm*", rtype = "admin", rname = "自营物流", rcode = "transport_self", rgroup = "自营")
	@RequestMapping("/admin/transport_copy.htm")
	public ModelAndView transport_copy(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/transport_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			Transport transport = this.transportService.getObjById(Long
					.parseLong(id));
			Transport obj = new Transport();
			obj.setStore(transport.getStore());
			obj.setTrans_ems(transport.isTrans_ems());
			obj.setTrans_ems_info(transport.getTrans_ems_info());
			obj.setTrans_express(transport.isTrans_express());
			obj.setTrans_express_info(transport.getTrans_express_info());
			obj.setTrans_mail(transport.isTrans_mail());
			obj.setTrans_mail_info(transport.getTrans_mail_info());
			obj.setTrans_name(transport.getTrans_name());
			mv.addObject("obj", obj);
			mv.addObject("currentPage", currentPage);
		}
		mv.addObject("transportTools", transportTools);
		return mv;
	}

	/**
	 * transport保存管理
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "运费模板保存", value = "/admin/transport_save.htm*", rtype = "admin", rname = "自营物流", rcode = "transport_self", rgroup = "自营")
	@RequestMapping("/admin/transport_save.htm")
	public String transport_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String trans_mail, String trans_express, String trans_ems,
			String mail_city_count, String express_city_count,
			String ems_city_count) {
		WebForm wf = new WebForm();
		Transport transport = null;
		if (id.equals("")) {
			transport = wf.toPo(request, Transport.class);
			transport.setAddTime(new Date());
		} else {
			Transport obj = this.transportService
					.getObjById(Long.parseLong(id));
			transport = (Transport) wf.toPo(request, obj);
		}
		if (CommUtil.null2Boolean(trans_mail)) {
			List<Map> trans_mail_info = new ArrayList<Map>();
			Map map = new HashMap();
			map.put("city_id", "-1");
			map.put("city_name", "全国");
			map.put("trans_weight", CommUtil.null2Int(request
					.getParameter("mail_trans_weight")));
			map.put("trans_fee",
					CommUtil.null2Float(request.getParameter("mail_trans_fee")));
			map.put("trans_add_weight", CommUtil.null2Int(request
					.getParameter("mail_trans_add_weight")));
			map.put("trans_add_fee", CommUtil.null2Float(request
					.getParameter("mail_trans_add_fee")));
			trans_mail_info.add(map);
			for (int i = 1; i <= CommUtil.null2Int(mail_city_count); i++) {
				int trans_weight = CommUtil.null2Int(request
						.getParameter("mail_trans_weight" + i));
				String city_ids = CommUtil.null2String(request
						.getParameter("mail_city_ids" + i));
				if (!city_ids.equals("") && trans_weight > 0) {
					float trans_fee = CommUtil.null2Float(request
							.getParameter("mail_trans_fee" + i));
					int trans_add_weight = CommUtil.null2Int(request
							.getParameter("mail_trans_add_weight" + i));
					float trans_add_fee = CommUtil.null2Float(request
							.getParameter("mail_trans_add_fee" + i));
					String city_name = CommUtil.null2String(request
							.getParameter("mail_city_names" + i));
					Map map1 = new HashMap();
					map1.put("city_id", city_ids);
					map1.put("city_name", city_name);
					map1.put("trans_weight", trans_weight);
					map1.put("trans_fee", trans_fee);
					map1.put("trans_add_weight", trans_add_weight);
					map1.put("trans_add_fee", trans_add_fee);
					trans_mail_info.add(map1);
				}
			}
			transport.setTrans_mail_info(Json.toJson(trans_mail_info,
					JsonFormat.compact()));
		}
		if (CommUtil.null2Boolean(trans_express)) {
			List<Map> trans_express_info = new ArrayList<Map>();
			Map map = new HashMap();
			map.put("city_id", "-1");
			map.put("city_name", "全国");
			map.put("trans_weight", CommUtil.null2Int(request
					.getParameter("express_trans_weight")));
			map.put("trans_fee", CommUtil.null2Float(request
					.getParameter("express_trans_fee")));
			map.put("trans_add_weight", CommUtil.null2Int(request
					.getParameter("express_trans_add_weight")));
			map.put("trans_add_fee", CommUtil.null2Float(request
					.getParameter("express_trans_add_fee")));
			trans_express_info.add(map);
			for (int i = 1; i <= CommUtil.null2Int(express_city_count); i++) {
				int trans_weight = CommUtil.null2Int(request
						.getParameter("express_trans_weight" + i));
				String city_ids = CommUtil.null2String(request
						.getParameter("express_city_ids" + i));
				if (!city_ids.equals("") && trans_weight > 0) {
					float trans_fee = CommUtil.null2Float(request
							.getParameter("express_trans_fee" + i));
					int trans_add_weight = CommUtil.null2Int(request
							.getParameter("express_trans_add_weight" + i));
					float trans_add_fee = CommUtil.null2Float(request
							.getParameter("express_trans_add_fee" + i));
					String city_name = CommUtil.null2String(request
							.getParameter("express_city_names" + i));
					Map map1 = new HashMap();
					map1.put("city_id", city_ids);
					map1.put("city_name", city_name);
					map1.put("trans_weight", trans_weight);
					map1.put("trans_fee", trans_fee);
					map1.put("trans_add_weight", trans_add_weight);
					map1.put("trans_add_fee", trans_add_fee);
					trans_express_info.add(map1);
				}
			}
			transport.setTrans_express_info(Json.toJson(trans_express_info,
					JsonFormat.compact()));
		}
		if (CommUtil.null2Boolean(trans_ems)) {
			List<Map> trans_ems_info = new ArrayList<Map>();
			Map map = new HashMap();
			map.put("city_id", "-1");
			map.put("city_name", "全国");
			map.put("trans_weight",
					CommUtil.null2Int(request.getParameter("ems_trans_weight")));
			map.put("trans_fee",
					CommUtil.null2Float(request.getParameter("ems_trans_fee")));
			map.put("trans_add_weight", CommUtil.null2Int(request
					.getParameter("ems_trans_add_weight")));
			map.put("trans_add_fee", CommUtil.null2Float(request
					.getParameter("ems_trans_add_fee")));
			trans_ems_info.add(map);
			for (int i = 1; i <= CommUtil.null2Int(ems_city_count); i++) {
				int trans_weight = CommUtil.null2Int(request
						.getParameter("ems_trans_weight" + i));
				String city_ids = CommUtil.null2String(request
						.getParameter("ems_city_ids" + i));
				if (!city_ids.equals("") && trans_weight > 0) {
					float trans_fee = CommUtil.null2Float(request
							.getParameter("ems_trans_fee" + i));
					int trans_add_weight = CommUtil.null2Int(request
							.getParameter("ems_trans_add_weight" + i));
					float trans_add_fee = CommUtil.null2Float(request
							.getParameter("ems_trans_add_fee" + i));
					String city_name = CommUtil.null2String(request
							.getParameter("ems_city_names" + i));
					Map map1 = new HashMap();
					map1.put("city_id", city_ids);
					map1.put("city_name", city_name);
					map1.put("trans_weight", trans_weight);
					map1.put("trans_fee", trans_fee);
					map1.put("trans_add_weight", trans_add_weight);
					map1.put("trans_add_fee", trans_add_fee);
					trans_ems_info.add(map1);
				}
			}
			transport.setTrans_ems_info(Json.toJson(trans_ems_info,
					JsonFormat.compact()));
		}
		transport.setAddTime(new Date());
		if (id.equals("")) {
			this.transportService.save(transport);
		} else
			this.transportService.update(transport);
		return "redirect:transport_success.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "运费模板保存成功", value = "/admin/transport_success.htm*", rtype = "admin", rname = "自营物流", rcode = "transport_self", rgroup = "自营")
	@RequestMapping("/admin/transport_success.htm")
	public ModelAndView transport_success(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("op_title", "运费模板保存成功");
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/admin/transport_list.htm?currentPage=" + currentPage);
		return mv;
	}

	@SecurityMapping(title = "运费模板删除", value = "/admin/transport_del.htm*", rtype = "admin", rname = "自营物流", rcode = "transport_self", rgroup = "自营")
	@RequestMapping("/admin/transport_del.htm")
	public String transport_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				Transport transport = this.transportService.getObjById(Long
						.parseLong(id));
				this.transportService.delete(Long.parseLong(id));
			}
		}
		return "redirect:transport_list.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "运费模板详细信息", value = "/admin/transport_info.htm*", rtype = "admin", rname = "自营物流", rcode = "transport_self", rgroup = "自营")
	@RequestMapping("/admin/transport_info.htm")
	public ModelAndView transport_info(HttpServletRequest request,
			HttpServletResponse response, String type, String id) {
		if (type == null || type.equals("")) {
			type = CommUtil.null2String(request.getAttribute("type"));
		}
		if (id == null || id.equals("")) {
			id = CommUtil.null2String(request.getAttribute("id"));
		}
		if (CommUtil.null2String(type).equals("")) {
			type = "mail";
		}
		ModelAndView mv = new JModelAndView("admin/blue/transport_" + type
				+ ".html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			Transport transport = this.transportService.getObjById(Long
					.parseLong(id));
			mv.addObject("obj", transport);
			mv.addObject("transportTools", transportTools);
		}
		return mv;
	}

	@SecurityMapping(title = "运费模板区域编辑", value = "/admin/transport_area.htm*", rtype = "admin", rname = "自营物流", rcode = "transport_self", rgroup = "自营")
	@RequestMapping("/admin/transport_area.htm")
	public ModelAndView transport_area(HttpServletRequest request,
			HttpServletResponse response, String id, String trans_city_type,
			String trans_index) {
		ModelAndView mv = new JModelAndView("admin/blue/transport_area.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		List<TransArea> objs = this.transAreaService
				.query("select obj from TransArea obj where obj.parent.id is null order by obj.sequence asc",
						null, -1, -1);
		mv.addObject("objs", objs);
		mv.addObject("trans_city_type", trans_city_type);
		mv.addObject("trans_index", trans_index);
		return mv;
	}

}
