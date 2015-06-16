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

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
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
import com.iskyshop.foundation.domain.Template;
import com.iskyshop.foundation.domain.query.TemplateQueryObject;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.ITemplateService;
import com.iskyshop.foundation.service.IUserConfigService;

/**
 * @info 通知模板管理控制器，用来管理系统各类通知模板，包括站内短信通知、邮件通知、手机短信通知
 * @author 沈阳网之商科技有限公司 www.iskyshop.com erikzhang
 * 
 */
@Controller
public class TemplateManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private ITemplateService templateService;

	/**
	 * Template列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "模板列表", value = "/admin/template_list.htm*", rtype = "admin", rname = "通知模板", rcode = "template_set", rgroup = "设置")
	@RequestMapping("/admin/template_list.htm")
	public ModelAndView template_list(HttpServletRequest request,
			HttpServletResponse response, String type, String currentPage,
			String orderBy, String orderType) {
		ModelAndView mv = new JModelAndView("admin/blue/template_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		TemplateQueryObject qo = new TemplateQueryObject(currentPage, mv,
				orderBy, orderType);
		if (type == null || type.equals(""))
			type = "msg";
		qo.addQuery("obj.type", new SysMap("type", type), "=");
		IPageList pList = this.templateService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url + "/admin/template_list.htm",
				"", params, pList, mv);
		mv.addObject("type", type);
		return mv;
	}

	/**
	 * template添加管理
	 * 
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "模板添加", value = "/admin/template_add.htm*", rtype = "admin", rname = "通知模板", rcode = "template_set", rgroup = "设置")
	@RequestMapping("/admin/template_add.htm")
	public ModelAndView template_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/template_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	/**
	 * template编辑管理
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "模板编辑", value = "/admin/template_edit.htm*", rtype = "admin", rname = "通知模板", rcode = "template_set", rgroup = "设置")
	@RequestMapping("/admin/template_edit.htm")
	public ModelAndView template_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/template_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			Template template = this.templateService.getObjById(Long
					.parseLong(id));
			mv.addObject("obj", template);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", true);
		}
		return mv;
	}

	/**
	 * template保存管理
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "模板保存", value = "/admin/template_save.htm*", rtype = "admin", rname = "通知模板", rcode = "template_set", rgroup = "设置")
	@RequestMapping("/admin/template_save.htm")
	public ModelAndView template_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String cmd, String list_url, String add_url) {
		WebForm wf = new WebForm();
		Template template = null;
		if (id.equals("")) {
			template = wf.toPo(request, Template.class);
			template.setAddTime(new Date());
		} else {
			Template obj = this.templateService.getObjById(Long.parseLong(id));
			template = (Template) wf.toPo(request, obj);
		}
		if (template.getType().equals("sms")) {// 手机短信过滤所有的html代码
			String content = Jsoup
					.clean(template.getContent(), Whitelist.none())
					.replace("&nbsp;", "").trim();
			;
			template.setContent(content);
		}
		if (id.equals("")) {
			this.templateService.save(template);
		} else
			this.templateService.update(template);
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", list_url + "?type=" + template.getType());
		mv.addObject("op_title", "保存模板成功");
		if (add_url != null) {
			mv.addObject("add_url", add_url + "?currentPage=" + currentPage);
		}
		return mv;
	}

	@SecurityMapping(title = "模板AJAX更新", value = "/admin/template_ajax.htm*", rtype = "admin", rname = "通知模板", rcode = "template_set", rgroup = "设置")
	@RequestMapping("/admin/template_ajax.htm")
	public void template_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String fieldName,
			String value) throws ClassNotFoundException {
		Template obj = this.templateService.getObjById(Long.parseLong(id));
		Field[] fields = Template.class.getDeclaredFields();
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
		this.templateService.update(obj);
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

	@SecurityMapping(title = "模板开启", value = "/admin/template_open.htm*", rtype = "admin", rname = "通知模板", rcode = "template_set", rgroup = "设置")
	@RequestMapping("/admin/template_open.htm")
	public String template_open(HttpServletRequest request, String mulitId,
			String currentPage, String type) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				Template obj = this.templateService.getObjById(Long
						.parseLong(id));
				obj.setOpen(true);
				this.templateService.update(obj);
			}
		}
		return "redirect:template_list.htm?currentPage=" + currentPage
				+ "&type=" + type;
	}

	@RequestMapping("/template/verify_mark.htm")
	public void verify_mark(HttpServletRequest request,
			HttpServletResponse response, String mark, String id) {
		boolean ret = true;
		Map params = new HashMap();
		params.put("mark", mark);
		params.put("id", CommUtil.null2Long(id));
		List<Template> list = this.templateService
				.query("select obj from Template obj where obj.mark=:mark and obj.id!=:id",
						params, -1, -1);
		if (list.size() > 0)
			ret = false;
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

	@SecurityMapping(title = "模板编辑", value = "/admin/template_copy.htm*", rtype = "admin", rname = "通知模板", rcode = "template_set", rgroup = "设置")
	@RequestMapping("/admin/template_copy.htm")
	public ModelAndView template_copy(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/template_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			Template template = this.templateService.getObjById(Long
					.parseLong(id));
			template.setId(null);
			mv.addObject("obj", template);
			mv.addObject("currentPage", currentPage);
		}
		return mv;
	}
}