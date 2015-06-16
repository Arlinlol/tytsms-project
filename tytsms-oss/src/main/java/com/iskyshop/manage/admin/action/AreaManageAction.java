package com.iskyshop.manage.admin.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.ehcache.CacheManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
import com.iskyshop.core.tools.database.DatabaseTools;
import com.iskyshop.foundation.domain.Area;
import com.iskyshop.foundation.domain.query.AreaQueryObject;
import com.iskyshop.foundation.service.IAreaService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;

/**
 * 
 * <p>
 * Title: AreaManageAction.java
 * </p>
 * 
 * <p>
 * Description:
 * 常用区域管理控制器，用来管理控制系统常用区域信息，常用区域主要用在买家添加配送地址、买家住址信息等，默认为中国大陆三级行政区域信息
 * ，平台管理员可以任意管理该信息
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
 * @date 2014年5月27日
 * 
 * @version 1.0
 */
@Controller
public class AreaManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private DatabaseTools databaseTools;

	@SecurityMapping(title = "地区列表", value = "/admin/area_list.htm*", rtype = "admin", rname = "常用地区", rcode = "admin_area_set", rgroup = "设置")
	@RequestMapping("/admin/area_list.htm")
	public ModelAndView area_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String pid,
			String orderBy, String orderType) {
		ModelAndView mv = new JModelAndView("admin/blue/area_setting.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		AreaQueryObject qo = null;
		if (pid == null || pid.equals("")) {
			qo = new AreaQueryObject(currentPage, mv, orderBy, orderType);
			qo.addQuery("obj.parent.id is null", null);
		} else {
			qo = new AreaQueryObject(currentPage, mv, orderBy, orderType);
			qo.addQuery("obj.parent.id",
					new SysMap("pid", Long.parseLong(pid)), "=");
			params = "&pid=" + pid;
			Area parent = this.areaService.getObjById(Long.parseLong(pid));
			mv.addObject("parent", parent);
			//cty 修改时间2015-3-12 增加内容 
			if (parent.getLevel() == 0) { //国
				Map map = new HashMap();
				map.put("pid", parent.getId());
				List<Area> areas = this.areaService.query(
						"select obj from Area obj where obj.parent.id=:pid",
						map, -1, -1);
				mv.addObject("areas", areas);
				mv.addObject("firsts", parent);
			}
			if (parent.getLevel() == 1) {//省
				Map map = new HashMap();
				map.put("pid", parent.getId());
				List<Area> seconds = this.areaService.query(
						"select obj from Area obj where obj.parent.id=:pid",
						map, -1, -1);
				map.clear();
				map.put("pid", parent.getParent().getId());
				List<Area> areas = this.areaService.query(
						"select obj from Area obj where obj.parent.id=:pid",
						map, -1, -1);
				mv.addObject("areas", areas);
				mv.addObject("firsts", parent.getParent());
				mv.addObject("seconds", seconds);
				mv.addObject("first", parent);
			}
			if (parent.getLevel() == 2) {//市
				Map map = new HashMap();
				map.put("pid", parent.getParent().getParent().getId());
				List<Area> areas = this.areaService.query(
						"select obj from Area obj where obj.parent.id=:pid",
						map, -1, -1);
				map.clear();
				map.put("pid", parent.getParent().getId());
				List<Area> seconds = this.areaService.query(
						"select obj from Area obj where obj.parent.id=:pid",
						map, -1, -1);
				map.clear();
				map.put("pid", new Long(pid));
				List<Area> thirds = this.areaService.query(
						"select obj from Area obj where obj.parent.id=:pid",
						map, -1, -1);
				mv.addObject("firsts", parent.getParent().getParent());
				mv.addObject("first", parent.getParent());
				mv.addObject("second", parent);
				mv.addObject("areas", areas);//省
				mv.addObject("seconds", seconds); //市
				mv.addObject("thirds", thirds); //州县
			}
			if(parent.getLevel() == 3){//区
				Map map = new HashMap();
				map.put("pid", parent.getParent().getParent().getParent().getId());
				List<Area> areas = this.areaService.query(
						"select obj from Area obj where obj.parent.id=:pid",
						map, -1, -1);
				map.put("pid", parent.getParent().getParent().getId());
				List<Area> seconds = this.areaService.query(
						"select obj from Area obj where obj.parent.id=:pid",
						map, -1, -1);
				map.clear();
				map.put("pid", parent.getParent().getId());
				List<Area> thirds = this.areaService.query(
						"select obj from Area obj where obj.parent.id=:pid",
						map, -1, -1);
				map.clear();
				mv.addObject("firsts", parent.getParent().getParent().getParent());
				mv.addObject("first", parent.getParent().getParent());
				mv.addObject("second", parent.getParent());
				mv.addObject("areas", areas);
				mv.addObject("seconds", seconds);
				mv.addObject("thirds", thirds);
				mv.addObject("third",parent);
			}
		}
		WebForm wf = new WebForm();
		wf.toQueryPo(request, qo, Area.class, mv);
		IPageList pList = this.areaService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url + "/admin/area_list.htm", "",
				params, pList, mv);
		List<Area> country = this.areaService.query(
				"select obj from Area obj where obj.parent.id is null", null,
				-1, -1);
		mv.addObject("countrys", country);
		return mv;
	}

	/**
	 * area保存管理
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "地区保存", value = "/admin/area_save.htm*", rtype = "admin", rname = "常用地区", rcode = "admin_area_set", rgroup = "设置")
	@RequestMapping("/admin/area_save.htm")
	public ModelAndView area_save(HttpServletRequest request,
			HttpServletResponse response, String areaId, String pid,
			String count, String list_url, String currentPage) {
		// 批量更新
		if (areaId == null) {
		} else {
			String[] ids = areaId.split(",");
			int i = 1;
			for (String id : ids) {
				String areaName = request.getParameter("areaName_" + i);
				Area area = this.areaService.getObjById(Long.parseLong(request
						.getParameter("id_" + i)));
				area.setAreaName(areaName);
				area.setSequence(CommUtil.null2Int(request
						.getParameter("sequence_" + i)));
				this.areaService.update(area);
				i++;
			}
		}
		// 批量更新完毕
		// 批量保存
		Area parent = null;
		if (!pid.equals(""))
			parent = this.areaService.getObjById(Long.parseLong(pid));
		for (int i = 1; i <= CommUtil.null2Int(count); i++) {
			Area area = new Area();
			area.setAddTime(new Date());
			String areaName = request.getParameter("new_areaName_" + i);
			int sequence = CommUtil.null2Int(request
					.getParameter("new_sequence_" + i));
			if (parent != null) {
				area.setLevel(parent.getLevel() + 1);
				area.setParent(parent);
			}
			area.setAreaName(areaName);
			area.setSequence(sequence);
			this.areaService.save(area);
		}
		// 批量保存完毕
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("op_title", "更新区域成功");
		mv.addObject("list_url", list_url + "?currentPage=" + currentPage
				+ "&pid=" + pid);
		return mv;

	}

	private void genericIds(Area obj) {
		if(obj.getChilds().size()>0){
			for (Area area : obj.getChilds()) {
				genericIds(area);
			}
		}
		obj.setParent(null);
		obj.setChilds(null);
		this.areaService.update(obj);
		this.areaService.delete(obj.getId());
	}

	@SecurityMapping(title = "地区删除", value = "/admin/area_del.htm*", rtype = "admin", rname = "常用地区", rcode = "admin_area_set", rgroup = "设置")
	@RequestMapping("/admin/area_del.htm")
	public String area_del(HttpServletRequest request, String mulitId,
			String currentPage, String pid) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				genericIds(this.areaService
						.getObjById(Long.parseLong(id)));
			}
		}
		return "redirect:area_list.htm?pid=" + pid + "&currentPage="
				+ currentPage;
	}

	@RequestMapping("/admin/area_export.htm")
	public ModelAndView area_export(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mv = null;
		String path = request.getSession().getServletContext().getRealPath("/")
				+ "resources" + File.separator + "data" + File.separator
				+ "base.sql";
		String tables = "iskyshop_accessory,iskyshop_adv_pos,iskyshop_advert,iskyshop_articleclass,iskyshop_article,iskyshop_document,iskyshop_navigation,iskyshop_template,iskyshop_sysconfig";
		boolean ret = this.databaseTools.export(tables, path);
		if (ret) {
			mv = new JModelAndView("admin/blue/success.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			CacheManager manager = CacheManager.create();
			manager.clearAll();
		} else {
			mv = new JModelAndView("admin/blue/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
		}
		mv.addObject("op_title", "数据导出");
		mv.addObject("listUrl", CommUtil.getURL(request)
				+ "/admin/area_list.htm");
		return mv;
	}

	@SecurityMapping(title = "地区Ajax编辑", value = "/admin/area_ajax.htm*", rtype = "admin", rname = "常用地区", rcode = "admin_area_set", rgroup = "设置")
	@RequestMapping("/admin/area_ajax.htm")
	public void area_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String fieldName,
			String value) throws ClassNotFoundException {
		Area obj = this.areaService.getObjById(Long.parseLong(id));
		Field[] fields = Area.class.getDeclaredFields();
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
		this.areaService.update(obj);
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
}