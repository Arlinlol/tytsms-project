package com.iskyshop.manage.admin.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.text.ParseException;
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

import com.easyjf.beans.BeanUtils;
import com.easyjf.beans.BeanWrapper;
import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.GroupArea;
import com.iskyshop.foundation.domain.GroupGoods;
import com.iskyshop.foundation.domain.GroupLifeGoods;
import com.iskyshop.foundation.domain.query.GroupAreaQueryObject;
import com.iskyshop.foundation.service.IGroupAreaService;
import com.iskyshop.foundation.service.IGroupGoodsService;
import com.iskyshop.foundation.service.IGroupLifeGoodsService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;

/**
 * 
 * <p>
 * Title: GroupAreaManageAction.java
 * </p>
 * 
 * <p>
 * Description: 团购区域管理类
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
public class GroupAreaManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGroupAreaService groupareaService;
	@Autowired
	private IGroupGoodsService groupGoodsService;
	@Autowired
	private IGroupLifeGoodsService groupLifeGoodsService;

	/**
	 * GroupArea列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "团购区域列表", value = "/admin/group_area_list.htm*", rtype = "admin", rname = "团购管理", rcode = "group_admin", rgroup = "运营")
	@RequestMapping("/admin/group_area_list.htm")
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView("admin/blue/group_area_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		GroupAreaQueryObject qo = new GroupAreaQueryObject(currentPage, mv,
				"ga_sequence", "asc");
		qo.addQuery("obj.parent.id is null", null);
		IPageList pList = this.groupareaService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url + "/admin/group_area_list.htm",
				"", params, pList, mv);
		return mv;
	}

	/**
	 * grouparea添加管理
	 * 
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "团购区域增加", value = "/admin/group_area_add.htm*", rtype = "admin", rname = "团购管理", rcode = "group_admin", rgroup = "运营")
	@RequestMapping("/admin/group_area_add.htm")
	public ModelAndView add(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String pid) {
		ModelAndView mv = new JModelAndView("admin/blue/group_area_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		GroupArea parent = this.groupareaService.getObjById(CommUtil
				.null2Long(pid));
		GroupArea obj = new GroupArea();
		obj.setParent(parent);
		List<GroupArea> gas = this.groupareaService.query(
				"select obj from GroupArea obj where obj.parent.id is null",
				null, -1, -1);
		mv.addObject("gas", gas);
		mv.addObject("obj", obj);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	/**
	 * grouparea编辑管理
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "团购区域编辑", value = "/admin/group_area_edit.htm*", rtype = "admin", rname = "团购管理", rcode = "group_admin", rgroup = "运营")
	@RequestMapping("/admin/group_area_edit.htm")
	public ModelAndView edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/group_area_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			GroupArea grouparea = this.groupareaService.getObjById(Long
					.parseLong(id));
			List<GroupArea> gas = this.groupareaService
					.query("select obj from GroupArea obj where obj.parent.id is null",
							null, -1, -1);
			mv.addObject("gas", gas);
			mv.addObject("obj", grouparea);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", true);
		}
		return mv;
	}

	/**
	 * grouparea保存管理
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "团购区域保存", value = "/admin/group_area_save.htm*", rtype = "admin", rname = "团购管理", rcode = "group_admin", rgroup = "运营")
	@RequestMapping("/admin/group_area_save.htm")
	public ModelAndView save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String cmd, String pid) {
		WebForm wf = new WebForm();
		GroupArea grouparea = null;
		if (id.equals("")) {
			grouparea = wf.toPo(request, GroupArea.class);
			grouparea.setAddTime(new Date());
		} else {
			GroupArea obj = this.groupareaService
					.getObjById(Long.parseLong(id));
			grouparea = (GroupArea) wf.toPo(request, obj);
		}
		GroupArea parent = this.groupareaService.getObjById(CommUtil
				.null2Long(pid));
		if (parent != null) {
			grouparea.setParent(parent);
			grouparea.setGa_level(parent.getGa_level() + 1);
		}
		if (id.equals("")) {
			this.groupareaService.save(grouparea);
		} else
			this.groupareaService.update(grouparea);
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/admin/group_area_list.htm");
		mv.addObject("op_title", "保存团购区域成功");
		mv.addObject("add_url", CommUtil.getURL(request)
				+ "/admin/group_area_add.htm" + "?currentPage=" + currentPage);
		return mv;
	}

	@SecurityMapping(title = "团购区域删除", value = "/admin/group_area_del.htm*", rtype = "admin", rname = "团购管理", rcode = "group_admin", rgroup = "运营")
	@RequestMapping("/admin/group_area_del.htm")
	public String delete(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				GroupArea grouparea = this.groupareaService.getObjById(Long
						.parseLong(id));
				Map params = new HashMap();
				params.put("area", grouparea.getId());
				List<GroupGoods> groupGoods = this.groupGoodsService.query(
						"select obj from GroupGoods obj where obj.gg_ga.id=:area",
						params, -1, -1);
				for(GroupGoods gg : groupGoods){
					gg.setGg_ga(null);
					this.groupGoodsService.update(gg);
				}
				List<GroupLifeGoods> groupLifeGoods = this.groupLifeGoodsService.query(
						"select obj from GroupLifeGoods obj where obj.gg_ga.id=:area",
						params, -1, -1);
				for(GroupLifeGoods gg : groupLifeGoods){
					gg.setGg_ga(null);
					this.groupLifeGoodsService.update(gg);
				}
				this.groupareaService.delete(Long.parseLong(id));
			}
		}
		return "redirect:group_area_list.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "团购区域Ajax更新", value = "/admin/group_area_ajax.htm*", rtype = "admin", rname = "团购管理", rcode = "group_admin", rgroup = "运营")
	@RequestMapping("/admin/group_area_ajax.htm")
	public void ajax(HttpServletRequest request, HttpServletResponse response,
			String id, String fieldName, String value)
			throws ClassNotFoundException {
		GroupArea obj = this.groupareaService.getObjById(Long.parseLong(id));
		Field[] fields = GroupArea.class.getDeclaredFields();
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
		this.groupareaService.update(obj);
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

	@SecurityMapping(title = "团购区域下级加载", value = "/admin/group_area_data.htm*", rtype = "admin", rname = "分类管理", rcode = "goods_class", rgroup = "商品")
	@RequestMapping("/admin/group_area_data.htm")
	public ModelAndView group_area_data(HttpServletRequest request,
			HttpServletResponse response, String pid, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/group_area_data.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map map = new HashMap();
		map.put("pid", CommUtil.null2Long(pid));
		List<GroupArea> gas = this.groupareaService.query(
				"select obj from GroupArea obj where obj.parent.id =:pid", map,
				-1, -1);
		mv.addObject("gas", gas);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	@RequestMapping("/admin/group_area_verify.htm")
	public void group_area_verify(HttpServletRequest request,
			HttpServletResponse response, String ga_name, String id, String pid) {
		boolean ret = true;
		Map params = new HashMap();
		params.put("ga_name", ga_name);
		params.put("id", CommUtil.null2Long(id));
		params.put("pid", CommUtil.null2Long(pid));
		List<GroupArea> gcs = this.groupareaService
				.query("select obj from GroupArea obj where obj.ga_name=:ga_name and obj.id!=:id and obj.parent.id =:pid",
						params, -1, -1);
		if (gcs != null && gcs.size() > 0) {
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
}